package com.info.ghiny.examsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

import com.google.zxing.ResultPoint;
import com.info.ghiny.examsystem.interfacer.TaskConnView;
import com.info.ghiny.examsystem.interfacer.TaskScanViewOld;
import com.info.ghiny.examsystem.manager.ObtainInfoManager;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.OnSwipeListener;
import com.info.ghiny.examsystem.model.ProcessException;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

/**
 * Created by GhinY on 07/05/2016.
 */
public class ObtainInfoActivity extends AppCompatActivity implements TaskScanViewOld, TaskConnView {
    private static final String TAG = ObtainInfoActivity.class.getSimpleName();

    private ObtainInfoManager infoManager;
    private ErrorManager errManager;
    private ProgressDialog progDialog;
    private CompoundBarcodeView barcodeView;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                barcodeView.setStatusText(result.getText());
                infoManager.onScan(result.getText());
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

        infoManager = new ObtainInfoManager(this, this);
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
        barcodeView.decodeContinuous(callback);
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
    protected void onRestart() {
        infoManager.onRestart();
        super.onRestart();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        infoManager.onPasswordReceived(requestCode, resultCode, data);
    }

    //==============================================================================================
    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void displayError(ProcessException err) {
        errManager.displayError(err);
    }

    @Override
    public void securityPrompt(boolean cancellable) {
        Intent secure   = new Intent(this, PopUpLogin.class);
        secure.putExtra("Cancellable", cancellable);
        startActivityForResult(secure, PopUpLogin.PASSWORD_REQ_CODE);
    }

    @Override
    public void navigateActivity(Class<?> cls) {
        Intent displayList = new Intent(this, cls);
        displayList.putExtra(JsonHelper.LIST_LIST, infoManager.getStudentSubjects());
        startActivity(displayList);
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

    @Override
    public void openProgressWindow() {
        progDialog  = ProgressDialog.show(this, "Server Database Request", "Waiting for Respond...");
    }

    @Override
    public void closeProgressWindow() {
        if(progDialog != null)
            progDialog.dismiss();
    }
}
