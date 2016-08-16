package com.info.ghiny.examsystem;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.info.ghiny.examsystem.interfacer.ScannerView;
import com.info.ghiny.examsystem.manager.CollectManager;
import com.info.ghiny.examsystem.manager.ConfigManager;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.model.ProcessException;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

import java.util.List;

public class CollectionActivity extends AppCompatActivity implements ScannerView {
    private static final String TAG = CollectionActivity.class.getSimpleName();

    private ErrorManager errorManager;
    private CollectManager collectManager;
    private static BarcodeView barcodeView;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                onScanBundle(result.getText());
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
        setContentView(R.layout.activity_collection);

        TextView bundleView = (TextView)findViewById(R.id.bundleText);
        assert bundleView  != null;
        bundleView.setTypeface(Typeface.createFromAsset(this.getAssets(), ConfigManager.DEFAULT_FONT));

        errorManager    = new ErrorManager(this);
        collectManager  = new CollectManager(this);

        barcodeView = (BarcodeView) findViewById(R.id.bundleScanner);
        assert barcodeView != null;
    }

    @Override
    protected void onResume() {
        collectManager.onResume(errorManager);
        super.onResume();
    }

    @Override
    protected void onPause() {
        collectManager.onPause();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event)
                || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        collectManager.onDestroy();
        super.onDestroy();
    }

    //==============================================================================================
    private void onScanBundle(String scanStr){
        collectManager.onScanForCollection(scanStr);
    }

    @Override
    public void navigateActivity(Class<?> cls) {}

    @Override
    public void displayError(ProcessException err) {
        errorManager.displayError(err);
    }

    @Override
    public void pauseScanning() {
        barcodeView.pause();
    }

    @Override
    public void resumeScanning() {
        barcodeView.decodeContinuous(callback);
        barcodeView.resume();
    }

    @Override
    public void securityPrompt() {
        Intent secure   = new Intent(this, PopUpLogin.class);
        startActivityForResult(secure, PopUpLogin.PASSWORD_REQ_CODE);
    }

    @Override
    public void finishActivity() {
        finish();
    }
}
