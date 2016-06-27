package com.info.ghiny.examsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.ExamDatabaseLoader;
import com.info.ghiny.examsystem.tools.AssignHelper;
import com.info.ghiny.examsystem.tools.ErrorManager;
import com.info.ghiny.examsystem.tools.ProcessException;
import com.info.ghiny.examsystem.tools.CustomToast;
import com.info.ghiny.examsystem.tools.IconManager;
import com.info.ghiny.examsystem.tools.OnSwipeListener;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

/**
 * Created by GhinY on 13/05/2016.
 */
public class AssignInfoActivity extends AppCompatActivity {
    private static final String TAG = AssignInfoActivity.class.getSimpleName();

    //Required Tools
    private CustomToast message;                //Toast message tool
    private ErrorManager errManager;
    private Candidate cdd;

    private CompoundBarcodeView barcodeView;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                barcodeView.setStatusText(result.getText());
                lockScanValue(result.getText());
            }
        }
        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_info);

        errManager  = new ErrorManager(this);
        message     = new CustomToast(this);

        CheckListLoader clDBLoader      = new CheckListLoader(this);
        ExamDatabaseLoader exDBLoader   = new ExamDatabaseLoader(this);

        AssignHelper.setClDBLoader(clDBLoader);
        AssignHelper.setExternalLoader(exDBLoader);

        //Set swiping gesture
        RelativeLayout thisLayout = (RelativeLayout)findViewById(R.id.assignInfoActivityLayout);
        assert thisLayout != null;
        thisLayout.setOnTouchListener(new OnSwipeListener(this){
            @Override
            public void onSwipeBottom(){
                Intent obtainIntent = new Intent(AssignInfoActivity.this, ObtainInfoActivity.class);
                startActivity(obtainIntent);
            }
            @Override
            public void onSwipeLeft() {
                Intent listIntent = new Intent(AssignInfoActivity.this, FragmentListActivity.class);
                startActivity(listIntent);
            }
            @Override
            public void onSwipeRight(){
                finish();
            }

        });

        //Barcode Viewer
        barcodeView = (CompoundBarcodeView) findViewById(R.id.assignScanner);
        assert barcodeView != null;
        barcodeView.decodeContinuous(callback);
        barcodeView.setStatusText("Ready to take candidates attendance");
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //checkListDB.saveAttendanceList(attdList);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    public void lockScanValue(String scanString){
        TextView tableView  = (TextView)findViewById(R.id.tableNumberText);
        TextView cddView    = (TextView)findViewById(R.id.canddAssignText);
        TextView regNumView = (TextView)findViewById(R.id.regNumAssignText);
        TextView paperView  = (TextView)findViewById(R.id.paperAssignText);

        assert tableView    != null;    assert cddView     != null;
        assert regNumView   != null;    assert paperView   != null;

        try{
            int scanPossibly    =   AssignHelper.checkScan(scanString);

            if(scanPossibly == AssignHelper.MAYBE_TABLE){
                AssignHelper.checkTable(Integer.parseInt(scanString));
                tableView.setText(scanString);
            }

            if(scanPossibly == AssignHelper.MAYBE_CANDIDATE){
                cdd = AssignHelper.checkCandidate(scanString);
                //Candidate is legal, display all the candidate value
                cddView.setText(cdd.getStudentName());
                regNumView.setText(cdd.getRegNum());
                paperView.setText(cdd.getPaper().toString());
            }

            if(AssignHelper.tryAssignCandidate()){
                //Candidate successfully assigned, clear display and acknowledge with message
                tableView.setText("");  cddView.setText("");
                regNumView.setText(""); paperView.setText("");

                message.showCustomMessage(cdd.getStudentName()+ " Assigned to "
                        + cdd.getTableNumber().toString(),
                        new IconManager().getIcon(IconManager.ASSIGNED));
            }
        } catch(ProcessException err){
            barcodeView.pause();
            errManager.displayError(err);
            barcodeView.resume();
        }
    }

}
