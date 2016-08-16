package com.info.ghiny.examsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

import com.google.zxing.ResultPoint;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.ScannerView;
import com.info.ghiny.examsystem.manager.ObtainInfoManager;
import com.info.ghiny.examsystem.model.ChiefLink;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.model.IconManager;
import com.info.ghiny.examsystem.model.InfoCollectHelper;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.OnSwipeListener;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

/**
 * Created by GhinY on 07/05/2016.
 */
public class ObtainInfoActivity extends AppCompatActivity implements ScannerView {
    private static final String TAG = ObtainInfoActivity.class.getSimpleName();

    private ObtainInfoManager infoManager;
    private ErrorManager errManager;
    private CompoundBarcodeView barcodeView;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                barcodeView.setStatusText(result.getText());
                onScan(result.getText());
                //get The info of the student here
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    //==============================================================================================
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obtain_info);

        infoManager = new ObtainInfoManager(this);
        errManager  = new ErrorManager(this);

        RelativeLayout thisLayout = (RelativeLayout) findViewById(R.id.obtainInfoLayout);
        assert thisLayout != null;
        thisLayout.setOnTouchListener(new OnSwipeListener(this){
            @Override
            public void onSwipeTop() {
                finish();
            }
        });

        barcodeView = (CompoundBarcodeView) findViewById(R.id.obtainScanner);
        barcodeView.setStatusText("Scan candidate ID to get his/her exam details");
    }

    @Override
    protected void onResume() {
        infoManager.onResume(errManager);
        super.onResume();
    }

    @Override
    protected void onPause() {
        infoManager.onPause();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event)
                || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        infoManager.onDestroy();
        super.onDestroy();
    }

    //==============================================================================================
    private void onScan(String scanStr){
        infoManager.onScanForCandidateDetail(scanStr);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void pauseScanning() {
        barcodeView.pause();
    }

    @Override
    public void securityPrompt() {
        //Intent secure   = new Intent(this, PopUpLogin.class);
        //startActivityForResult(secure, PopUpLogin.PASSWORD_REQ_CODE);
    }

    @Override
    public void displayError(ProcessException err) {
        errManager.displayError(err);
    }

    @Override
    public void resumeScanning() {
        barcodeView.decodeContinuous(callback);
        barcodeView.resume();
    }

    @Override
    public void navigateActivity(Class<?> cls) {
        Intent displayList = new Intent(this, cls);
        displayList.putExtra(JsonHelper.LIST_LIST, infoManager.getStudentSubjects());
        startActivity(displayList);
    }
}
