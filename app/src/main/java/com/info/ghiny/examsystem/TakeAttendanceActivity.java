package com.info.ghiny.examsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.interfacer.TakeAttdMVP;
import com.info.ghiny.examsystem.manager.TakeAttdPresenter;
import com.info.ghiny.examsystem.manager.ConfigManager;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.model.TakeAttdModel;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.OnSwipeListener;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

/**
 * Created by GhinY on 13/05/2016.
 */
public class TakeAttendanceActivity extends AppCompatActivity implements TakeAttdMVP.View{
    private static final String TAG = TakeAttendanceActivity.class.getSimpleName();

    //Required Tools
    private TakeAttdMVP.VPresenter taskPresenter;
    private ProgressDialog progDialog;

    //private TakeAttdPresenter assignManager;
    private ErrorManager errManager;
    private CompoundBarcodeView barcodeView;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                barcodeView.setStatusText(result.getText());
                taskPresenter.onScan(result.getText());
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
        setContentView(R.layout.activity_assign_info);

        initMVP();
        //Set swiping gesture
        RelativeLayout thisLayout = (RelativeLayout)findViewById(R.id.assignInfoBarcodeLayout);
        assert thisLayout != null;
        thisLayout.setOnTouchListener(new OnSwipeListener(this){
            @Override
            public void onSwipeBottom(){
                taskPresenter.onSwipeBottom();
            }
            @Override
            public void onSwipeLeft() {
                taskPresenter.onSwipeLeft();
            }
        });

        taskPresenter.onCreate();

        //Barcode Viewer
        barcodeView.decodeContinuous(callback);
        assert barcodeView != null;
        barcodeView.setStatusText("Ready to take candidates attendance");
    }

    private void initMVP(){
        barcodeView = (CompoundBarcodeView) findViewById(R.id.assignScanner);
        errManager      = new ErrorManager(this);

        CheckListLoader dbLoader    = new CheckListLoader(this);
        TakeAttdPresenter presenter     = new TakeAttdPresenter(this);
        TakeAttdModel model           = new TakeAttdModel(presenter, dbLoader);
        presenter.setTaskModel(model);
        presenter.setHandler(new Handler());

        taskPresenter   = presenter;
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskPresenter.onResume(errManager);
    }

    @Override
    protected void onPause() {
        super.onPause();
        taskPresenter.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        taskPresenter.onRestart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        taskPresenter.onPasswordReceived(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        taskPresenter.onBackPressed();
    }

    //= Interface Method ==========================================================================
    @Override
    public void setTableView(String tableNum) {
        TextView tableView  = (TextView)findViewById(R.id.tableNumberText);
        assert tableView != null;
        tableView.setTypeface(Typeface.createFromAsset(getAssets(), ConfigManager.THICK_FONT));
        tableView.setText(tableNum);
    }

    @Override
    public void setCandidateView(String cddIndex, String cddRegNum, String cddPaper) {
        TextView cddView    = (TextView)findViewById(R.id.canddAssignText);
        TextView regNumView = (TextView)findViewById(R.id.regNumAssignText);
        TextView paperView  = (TextView)findViewById(R.id.paperAssignText);
        assert cddView     != null; assert regNumView   != null;    assert paperView   != null;

        cddView.setTypeface(Typeface.createFromAsset(getAssets(), ConfigManager.BOLD_FONT));
        regNumView.setTypeface(Typeface.createFromAsset(getAssets(), ConfigManager.DEFAULT_FONT));
        paperView.setTypeface(Typeface.createFromAsset(getAssets(), ConfigManager.DEFAULT_FONT));

        cddView.setText(cddIndex);
        regNumView.setText(cddRegNum);
        paperView.setText(cddPaper);
    }

    @Override
    public void navigateActivity(Class<?> cls) {
        Intent nextAct  = new Intent(this, cls);
        startActivity(nextAct);
    }

    @Override
    public void displayError(ProcessException err) {
        errManager.displayError(err);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void securityPrompt(boolean cancellable) {
        Intent secure   = new Intent(this, PopUpLogin.class);
        secure.putExtra("Cancellable", cancellable);
        startActivityForResult(secure, PopUpLogin.PASSWORD_REQ_CODE);
    }

    @Override
    public void beep() {}

    @Override
    public void resumeScanning() {
        barcodeView.resume();
    }

    @Override
    public void pauseScanning() {
        barcodeView.pause();
    }

    @Override
    public void openProgressWindow() {
        progDialog  = ProgressDialog.show(this, "Initializing:", "Preparing Attendance List...");
    }

    @Override
    public void closeProgressWindow() {
        if(progDialog != null)
            progDialog.dismiss();
    }
}