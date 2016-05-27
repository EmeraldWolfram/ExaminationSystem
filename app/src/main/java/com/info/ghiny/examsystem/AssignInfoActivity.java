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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GhinY on 13/05/2016.
 */
public class AssignInfoActivity extends AppCompatActivity {
    private CustomToast message;
    private ExamDatabaseHelper databaseHelper;
    private CheckListDatabaseHelper checkListDB;
    private String scanString;
    private Identity candidateID;
    private Candidate candidate;
    private AttendanceList attdList;
    private ArrayList<String> cddNames;
    private ArrayList<String> cddPapers;
    private ArrayList<Integer> cddTables;
    private ArrayList<String> cddStatus;

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
        attdList        = new AttendanceList();
        cddPapers       = new ArrayList<>();
        cddNames        = new ArrayList<>();
        cddTables       = new ArrayList<>();
        cddStatus       = new ArrayList<>();

        if(checkListDB.isEmpty())
            checkListDB.saveAttendanceList(prepareList());
        attdList.setAttendanceList(checkListDB.getLastSavedAttendanceList());

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
                packageAttdListToSend(listIntent);
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
    protected void onStop() {
        super.onStop();
        checkListDB.saveAttendanceList(attdList);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    public void lockScanValue(){
        TextView tableView  = (TextView)findViewById(R.id.tableNumberText);
        TextView cddView    = (TextView)findViewById(R.id.canddAssignText);
        TextView regNumView = (TextView)findViewById(R.id.regNumAssignText);
        TextView paperView  = (TextView)findViewById(R.id.paperAssignText);
        assert tableView    != null;    assert cddView     != null;
        assert regNumView   != null;    assert paperView   != null;

        if(scanString.length() < 4 && tableView.getText().toString().isEmpty())
                tableView.setText(scanString);

        if(scanString.length() == 12 && cddView.getText().toString().isEmpty()){
                candidateID = databaseHelper.getIdentity(scanString);
                candidate = attdList.getCandidate(candidateID.getRegNum());


                cddView.setText(candidateID.getName());
                regNumView.setText(candidateID.getRegNum());
                //paperView.setText(candidate.getPaper().toString());
        }

        assignToList(tableView, cddView, regNumView, paperView);
    }

    private void assignToList(TextView tableView,   TextView cddView,
                              TextView regNumView,  TextView paperView){
        CharSequence tableText  = tableView.getText();
        CharSequence cddText    = cddView.getText();

        if(!tableText.toString().isEmpty() && !cddText.toString().isEmpty()){
            tableView.setText("");
            cddView.setText("");
            regNumView.setText("");
            paperView.setText("");
            //If candidate == null means the candidate should not be in this room
            //If candidate.getStatus() == PRESENT means it is assigned
            //If candidate.getStatus() != ABSENT means it is not legit to take paper
            //If ExamSubject range does not meet, DO something
            attdList.addCandidate(candidate, candidate.getPaperCode(), AttendanceList.Status.PRESENT);

            message.showMessage(cddText.toString()+ " Assigned to " + tableText.toString());
        }
    }

    private AttendanceList prepareList(){
        AttendanceList attdList = new AttendanceList();

        Candidate cdd1 = new Candidate(1, "FGY", "15WAU00001", "BAME 00001", AttendanceList.Status.ABSENT);
        Candidate cdd2 = new Candidate(1, "NYN", "15WAU00002", "BAME 00001", AttendanceList.Status.ABSENT);
        Candidate cdd3 = new Candidate(1, "LHN", "15WAU00003", "BAME 00001", AttendanceList.Status.ABSENT);
        Candidate cdd4 = new Candidate(1, "YZL", "15WAU00004", "BAME 00001", AttendanceList.Status.BARRED);
        Candidate cdd5 = new Candidate(1, "SYL", "15WAU00005", "BAME 00001", AttendanceList.Status.EXEMPTED);

        attdList.addCandidate(cdd1, cdd1.getPaperCode(), AttendanceList.Status.ABSENT);
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), AttendanceList.Status.ABSENT);
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), AttendanceList.Status.ABSENT);
        attdList.addCandidate(cdd4, cdd4.getPaperCode(), AttendanceList.Status.BARRED);
        attdList.addCandidate(cdd5, cdd5.getPaperCode(), AttendanceList.Status.EXEMPTED);

        packageCandidateToSend(cdd1);
        packageCandidateToSend(cdd2);
        packageCandidateToSend(cdd3);
        packageCandidateToSend(cdd4);
        packageCandidateToSend(cdd5);

        return attdList;
    }

    private void packageAttdListToSend(Intent listIntent){
        List<String> regNumList = attdList.getAllCandidateRegNumList();
        for(int i = 0; i < regNumList.size(); i++){
            Candidate listCdd = attdList.getCandidate(regNumList.get(i));
            packageCandidateToSend(listCdd);
        }
        listIntent.putStringArrayListExtra("Name", cddNames);
        listIntent.putStringArrayListExtra("Paper", cddPapers);
        listIntent.putIntegerArrayListExtra("Table", cddTables);
        listIntent.putStringArrayListExtra("Status", cddStatus);

    }

    private void packageCandidateToSend(Candidate cdd){
        cddNames.add(cdd.getStudentName());
        cddPapers.add(cdd.getPaperCode());
        cddTables.add(cdd.getTableNumber());
        cddStatus.add(cdd.getStatus().toString());
    }
}
