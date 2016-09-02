package com.info.ghiny.examsystem;


import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.info.ghiny.examsystem.interfacer.ScannerView;
import com.info.ghiny.examsystem.manager.LoginManager;
import com.info.ghiny.examsystem.manager.ConfigManager;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.model.ProcessException;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

import java.util.List;

public class MainLoginActivity extends AppCompatActivity implements ScannerView{
    private static final String TAG = MainLoginActivity.class.getSimpleName();

    private LoginManager loginManager;
    private ErrorManager errorManager;

    private BarcodeView barcodeView;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                loginManager.onScan(result.getText());
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

        TextView idView = (TextView)findViewById(R.id.identityText);    assert idView  != null;
        idView.setTypeface(Typeface.createFromAsset(this.getAssets(), ConfigManager.DEFAULT_FONT));

        loginManager    = new LoginManager(this);
        errorManager    = new ErrorManager(this);
        barcodeView     = (BarcodeView) findViewById(R.id.loginScanner);
        barcodeView.decodeContinuous(callback);
    }

    @Override
    protected void onResume() {
        loginManager.onResume(errorManager);
        super.onResume();
    }

    @Override
    protected void onPause() {
        loginManager.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        loginManager.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event)
                || super.onKeyDown(keyCode, event);
    }

    //==============================================================================================
    public void onActivityResult(int reqCode, int resCode, Intent data){
        loginManager.onPasswordReceived(reqCode, resCode, data);
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
    public void securityPrompt() {
        Intent pwPrompt = new Intent(this, PopUpLogin.class);
        startActivityForResult(pwPrompt, PopUpLogin.PASSWORD_REQ_CODE);
    }

    @Override
    public void beep() {}

    @Override
    public void pauseScanning() {
        barcodeView.pause();
    }

    @Override
    public void resumeScanning() {
        barcodeView.resume();
    }
}