package com.info.ghiny.examsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.info.ghiny.examsystem.interfacer.GeneralView;
import com.info.ghiny.examsystem.manager.ConnectionManager;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.manager.LoginManager;
import com.info.ghiny.examsystem.model.ProcessException;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

import java.util.List;

public class LinkChiefActivity extends AppCompatActivity implements GeneralView {
    private static final String TAG = LinkChiefActivity.class.getSimpleName();
    private ErrorManager errorManager;
    private BeepManager beepManager;
    private ConnectionManager conManager;

    private BarcodeView barcodeView;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                barcodeView.pause();
                beepManager.playBeepSoundAndVibrate();
                onScanForChief(result.getText());
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
        setContentView(R.layout.activity_link_chief);

        conManager   = new ConnectionManager(this);
        errorManager = new ErrorManager(this);
        beepManager  = new BeepManager(this);
        beepManager.setBeepEnabled(true);
        beepManager.setVibrateEnabled(true);

        barcodeView  = (BarcodeView) findViewById(R.id.ipScanner);
        assert barcodeView != null;
        barcodeView.decodeContinuous(callback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beepManager.close();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event)
                || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater   = getMenuInflater();
        inflater.inflate(R.menu.home_option_menu, menu);

        return true;
    }

    //==============================================================================================
    public void onScanForChief(String scanStr){
        conManager.onScanForChief(scanStr);
        barcodeView.resume();
    }

    @Override
    public void displayError(ProcessException err) {
        errorManager.displayError(err);
    }

    @Override
    public void navigateActivity(Class<?> cls) {
        Intent login    = new Intent(this, cls);
        startActivity(login);
    }

    @Override
    public void finishActivity() {
        finish();
    }
}
