package com.info.ghiny.examsystem;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.LinkChiefMVP;
import com.info.ghiny.examsystem.manager.LinkChiefPresenter;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.model.LinkChiefModel;
import com.info.ghiny.examsystem.model.ProcessException;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

import java.util.List;

public class LinkChiefActivity extends AppCompatActivity implements LinkChiefMVP.ViewFace {
    public static final String TAG = LinkChiefActivity.class.getSimpleName();
    //Presenter and Manager
    private ErrorManager errorManager;
    private BeepManager beepManager;
    private LinkChiefMVP.PresenterFace taskPresenter;
    private int mode;

    //View Objects
    private ProgressDialog progDialog;
    private ImageView crossHairView;
    private FloatingActionButton scanInitiater;
    private BarcodeView barcodeView;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                taskPresenter.onScan(result.getText());
            }
        }
        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {}
    };

    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        setContentView(R.layout.activity_link_chief);

        initView();
        initMVP();

        taskPresenter.onCreate();
        taskPresenter.loadSetting();
        barcodeView.decodeContinuous(callback);
    }

    private void initView(){
        barcodeView                 = (BarcodeView) findViewById(R.id.ipScanner);
        scanInitiater               = (FloatingActionButton) findViewById(R.id.linkScanButton);
        crossHairView               = (ImageView) findViewById(R.id.linkerCrossHair);
        errorManager                = new ErrorManager(this);
        beepManager                 = new BeepManager(this);
    }

    private void initMVP(){
        SharedPreferences preferences   = PreferenceManager.getDefaultSharedPreferences(this);
        LinkChiefPresenter presenter= new LinkChiefPresenter(this, preferences);
        CheckListLoader dbLoader    = new CheckListLoader(this);
        LinkChiefModel model        = new LinkChiefModel(dbLoader, presenter);
        presenter.setTaskModel(model);
        presenter.setHandler(new Handler());
        taskPresenter               = presenter;
    }

    @Override
    protected void onPause() {
        taskPresenter.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskPresenter.onResume(errorManager);
    }

    @Override
    protected void onDestroy() {
        taskPresenter.onDestroy();
        super.onDestroy();
        beepManager.close();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater   = this.getMenuInflater();
        inflater.inflate(R.menu.home_option_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                Intent setting  = new Intent(this, SettingActivity.class);
                startActivity(setting);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onInitiateScan(View view){
        barcodeView.resume();
    }

    //==============================================================================================
    @Override
    public void displayError(ProcessException err) {
        errorManager.displayError(err);
    }

    @Override
    public void navigateActivity(Class<?> cls) {
        Intent login    = new Intent(this, cls);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setReenterTransition(new Slide(Gravity.START));
            startActivity(login, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(login);
        }
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void beep() {
        if(beepManager.isBeepEnabled()){
            beepManager.playBeepSoundAndVibrate();
        }
    }

    @Override
    public void pauseScanning() {
        barcodeView.pause();
    }

    @Override
    public void resumeScanning() {
        switch (mode){
            case 2:
                barcodeView.postDelayed(this, 1000);
                break;
            case 3:
                barcodeView.postDelayed(this, 2000);
                break;
            case 4:
                barcodeView.postDelayed(this, 3000);
                break;
        }
    }

    @Override
    public void securityPrompt(boolean cancellable) {}

    @Override
    public void changeScannerSetting(boolean crossHair, boolean beep, boolean vibrate, int mode) {
        if(crossHair){
            this.crossHairView.setVisibility(View.VISIBLE);
        } else {
            this.crossHairView.setVisibility(View.INVISIBLE);
        }
        this.beepManager.setBeepEnabled(beep);
        this.beepManager.setVibrateEnabled(vibrate);
        this.mode   = mode;
        if(mode == 1){
            scanInitiater.setVisibility(View.VISIBLE);
        } else {
            scanInitiater.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void openProgressWindow(String title, String message) {
        progDialog  = ProgressDialog.show(this, title, message);
    }

    @Override
    public void closeProgressWindow() {
        if(progDialog != null)
            progDialog.dismiss();
    }

    @Override
    public void run() {
        barcodeView.resume();
    }
}
