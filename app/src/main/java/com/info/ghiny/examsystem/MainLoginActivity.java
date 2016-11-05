package com.info.ghiny.examsystem;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.interfacer.LoginMVP;
import com.info.ghiny.examsystem.manager.LoginPresenter;
import com.info.ghiny.examsystem.manager.ConfigManager;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.model.LoginModel;
import com.info.ghiny.examsystem.model.ProcessException;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

import java.util.List;

public class MainLoginActivity extends AppCompatActivity implements LoginMVP.View {
    private static final String TAG = MainLoginActivity.class.getSimpleName();

    //private LoginPresenter loginManager;
    private LoginMVP.VPresenter taskPresenter;
    private CheckListLoader dbLoader;
    private ErrorManager errorManager;
    private BeepManager beepManager;
    private ProgressDialog progDialog;

    private int mode;
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
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        initView();
        initMVP();

        TextView idView = (TextView)findViewById(R.id.identityText);    assert idView  != null;
        idView.setTypeface(Typeface.createFromAsset(this.getAssets(), ConfigManager.DEFAULT_FONT));

        taskPresenter.loadSetting();
        barcodeView.decodeContinuous(callback);
    }

    private void initView(){
        barcodeView     = (BarcodeView) findViewById(R.id.loginScanner);
        scanInitiater   = (FloatingActionButton) findViewById(R.id.loginScanButton);
        crossHairView   = (ImageView) findViewById(R.id.loginCrossHair);
        errorManager    = new ErrorManager(this);
        beepManager     = new BeepManager(this);
    }

    private void initMVP(){
        dbLoader        = new CheckListLoader(this);
        SharedPreferences preferences   = PreferenceManager.getDefaultSharedPreferences(this);
        LoginPresenter presenter  = new LoginPresenter(this, preferences);
        LoginModel model       = new LoginModel(presenter, dbLoader);
        presenter.setHandler(new Handler());
        presenter.setTaskModel(model);
        taskPresenter           = presenter;
    }

    @Override
    protected void onResume() {
        taskPresenter.onResume(errorManager);
        super.onResume();
    }

    @Override
    protected void onPause() {
        taskPresenter.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        taskPresenter.onDestroy();
        beepManager.close();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event)
                || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onInitiateScan(View view) {
        barcodeView.resume();
    }

    //==============================================================================================
    public void onActivityResult(int reqCode, int resCode, Intent data){
        taskPresenter.onPasswordReceived(reqCode, resCode, data);
    }

    //Interface of View ============================================================================
    @Override
    public void displayError(ProcessException err) {
        errorManager.displayError(err);
    }

    @Override
    public void navigateActivity(Class<?> cls) {
        Intent nextAct  = new Intent(this, cls);
        startActivity(nextAct);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void securityPrompt(boolean cancellable) {
        Intent pwPrompt = new Intent(this, PopUpLogin.class);
        pwPrompt.putExtra("Cancellable", cancellable);
        startActivityForResult(pwPrompt, PopUpLogin.PASSWORD_REQ_CODE);
    }

    @Override
    public void beep() {
        beepManager.playBeepSoundAndVibrate();
    }

    @Override
    public void pauseScanning() {
        barcodeView.pause();
    }

    @Override
    public void resumeScanning() {
        switch (mode){
            case 2:
                barcodeView.postDelayed(this, 500);
                break;
            case 3:
                barcodeView.postDelayed(this, 1000);
                break;
            case 4:
                barcodeView.postDelayed(this, 2000);
                break;
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
    public void run() {
        barcodeView.resume();
    }
}