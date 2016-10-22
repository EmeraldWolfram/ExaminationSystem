package com.info.ghiny.examsystem;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.Window;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.info.ghiny.examsystem.database.CheckListLoader;
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
    private ErrorManager errorManager;
    private BeepManager beepManager;
    private LinkChiefMVP.PresenterFace taskPresenter;
    private ProgressDialog progDialog;

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

        initMVP();

        taskPresenter.onCreate();
        barcodeView.decodeContinuous(callback);
    }

    private void initMVP(){
        barcodeView  = (BarcodeView) findViewById(R.id.ipScanner);

        LinkChiefPresenter presenter= new LinkChiefPresenter(this);
        CheckListLoader dbLoader    = new CheckListLoader(this);
        LinkChiefModel model        = new LinkChiefModel(dbLoader, presenter);
        presenter.setTaskModel(model);
        presenter.setHandler(new Handler());
        taskPresenter               = presenter;

        errorManager                = new ErrorManager(this);
        beepManager                 = new BeepManager(this);
        beepManager.setBeepEnabled(true);
        beepManager.setVibrateEnabled(true);
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
        //Experiment on Menu Bar
        MenuInflater inflater   = getMenuInflater();
        inflater.inflate(R.menu.home_option_menu, menu);

        return true;
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
        beepManager.playBeepSoundAndVibrate();
    }

    @Override
    public void resumeScanning() {
        barcodeView.resume();
    }

    @Override
    public void pauseScanning() {
        barcodeView.pause();
    }

    @Override
    public void securityPrompt(boolean cancellable) {}

    @Override
    public void openProgressWindow(String title, String message) {
        progDialog  = ProgressDialog.show(this, title, message);
    }

    @Override
    public void closeProgressWindow() {
        if(progDialog != null)
            progDialog.dismiss();
    }
}
