package com.info.ghiny.examsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.CheckListDatabaseHelper;
import com.info.ghiny.examsystem.database.ExamDatabaseHelper;
import com.info.ghiny.examsystem.database.Identity;
import com.info.ghiny.examsystem.tools.CustomToast;
import com.info.ghiny.examsystem.tools.OnSwipeListener;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

/**
 * Created by GhinY on 13/05/2016.
 */
public class AssignInfoActivity extends AppCompatActivity {
    private CustomToast message;
    private ExamDatabaseHelper databaseHelper;
    private CheckListDatabaseHelper checkListDB;
    private String scanString;
    private Identity candidate;

    private static final String TAG = AssignInfoActivity.class.getSimpleName();
    private CompoundBarcodeView barcodeView;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                barcodeView.setStatusText(result.getText());
                scanString = result.getText();
                lockScanValue();
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

        databaseHelper  = new ExamDatabaseHelper(this);
        checkListDB     = new CheckListDatabaseHelper(this);
        message         = new CustomToast(this);

        RelativeLayout thisLayout = (RelativeLayout)findViewById(R.id.assignInfoActivityLayout);
        assert thisLayout != null;
        thisLayout.setOnTouchListener(new OnSwipeListener(this){
            @Override
            public void onSwipeTop(){
                Intent obtainIntent = new Intent(AssignInfoActivity.this, ObtainInfoActivity.class);
                startActivity(obtainIntent);
            }

            @Override
            public void onSwipeLeft() {
                Intent listIntent = new Intent(AssignInfoActivity.this, CheckListActivity.class);
                startActivity(listIntent);
            }
            public void onSwipeRight(){
                finish();
            }
        });

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    public void lockScanValue(){
        int i = 0;

        TextView tableView  = (TextView)findViewById(R.id.tableNumberText);
        TextView cddView    = (TextView)findViewById(R.id.canddAssignText);
        assert tableView    != null;
        assert cddView      != null;

        if(scanString.length() < 4 && tableView.getText().toString().isEmpty())
                tableView.setText(scanString);

        if(scanString.length() == 12 && cddView.getText().toString().isEmpty()){
                candidate = databaseHelper.getIdentity(scanString);
                cddView.setText(candidate.getName());
        }

        assignToDatabase(tableView, cddView);
    }

    private void assignToDatabase(TextView tableView, TextView cddView){
        CharSequence tableText  = tableView.getText();
        CharSequence cddText    = cddView.getText();

        if(!tableText.toString().isEmpty() && !cddText.toString().isEmpty()){
            tableView.setText("");
            cddView.setText("");
            //checkListDB.insertCandidateList(new Candidate());
            message.showMessage(cddText.toString()+ " Assigned to " + tableText.toString());
        }
    }
}
