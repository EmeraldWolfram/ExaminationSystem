package com.info.ghiny.examsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.info.ghiny.examsystem.tools.ErrorManager;
import com.info.ghiny.examsystem.tools.LoginHelper;
import com.info.ghiny.examsystem.tools.ProcessException;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

import java.util.List;

public class LinkChiefActivity extends AppCompatActivity {
    private static final String TAG = LinkChiefActivity.class.getSimpleName();
    private ErrorManager errorManager;

    private BeepManager beepManager;
    private BarcodeView barcodeView;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                beepManager.playBeepSoundAndVibrate();
                checkForChief(result.getText());
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
        beepManager.close();
        barcodeView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event)
                || super.onKeyDown(keyCode, event);
    }

    //==============================================================================================
    public void checkForChief(String scanStr){
        try{
            barcodeView.pause();
            LoginHelper helper = new LoginHelper();
            helper.verifyChief(scanStr);

            Intent login    = new Intent(this, MainLoginActivity.class);
            startActivity(login);
        } catch (ProcessException err){
            errorManager.displayError(err);
            barcodeView.resume();
        }
    }
}
