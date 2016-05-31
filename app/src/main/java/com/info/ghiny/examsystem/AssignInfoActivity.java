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
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.ExamDatabaseLoader;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.Identity;
import com.info.ghiny.examsystem.tools.CustomToast;
import com.info.ghiny.examsystem.tools.OnSwipeListener;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GhinY on 13/05/2016.
 */
public class AssignInfoActivity extends AppCompatActivity {
    private CustomToast message;
    private ExamDatabaseLoader databaseHelper;
    private CheckListLoader checkListDB;
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

        databaseHelper  = new ExamDatabaseLoader(this);
        checkListDB     = new CheckListLoader(this);
        message         = new CustomToast(this);
        attdList        = new AttendanceList();

        Candidate.setPaperList(fakeTheExamPaper());

        if(checkListDB.isEmpty())
            checkListDB.saveAttendanceList(prepareList());
        attdList.setAttendanceList(checkListDB.getLastSavedAttendanceList());

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

        if(scanString.length() < 4 && tableView.getText().toString().isEmpty())
                tableView.setText(scanString);

        if(scanString.length() == 12 && cddView.getText().toString().isEmpty()){
                candidateID = databaseHelper.getIdentity(scanString);
                candidate = attdList.getCandidate(candidateID.getRegNum());

                cddView.setText(candidateID.getName());
                regNumView.setText(candidateID.getRegNum());

                paperView.setText(candidate.getPaper().toString());
        }

        assignToList(candidate, tableView, cddView, regNumView, paperView);
    }

    private void assignToList(Candidate cdd, TextView tableView,   TextView cddView,
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
            cdd.setTableNumber(Integer.parseInt(tableText.toString()));
            cdd.setStatus(AttendanceList.Status.PRESENT);
            attdList.removeCandidate(cdd.getRegNum());
            attdList.addCandidate(cdd, cdd.getPaperCode(), AttendanceList.Status.PRESENT, cdd.getProgramme());

            message.showMessage(cddText.toString()+ " Assigned to " + tableText.toString());
        }
    }

    private AttendanceList prepareList(){
        AttendanceList attdList = new AttendanceList();

        Candidate cdd1 = new Candidate(0, "RMB3", "FGY", "15WAU00001", "BAME 0001", AttendanceList.Status.ABSENT);
        Candidate cdd2 = new Candidate(0, "RMB3", "NYN", "15WAU00002", "BAME 0001", AttendanceList.Status.ABSENT);
        Candidate cdd3 = new Candidate(0, "RMB3", "LHN", "15WAU00003", "BAME 0001", AttendanceList.Status.ABSENT);
        Candidate cdd4 = new Candidate(0, "RMB3", "YZL", "15WAU00004", "BAME 0001", AttendanceList.Status.BARRED);
        Candidate cdd5 = new Candidate(0, "RMB3", "SYL", "15WAU00005", "BAME 0001", AttendanceList.Status.EXEMPTED);
        Candidate cdd6 = new Candidate(0, "RMB3", "WJS", "15WAU00006", "BAME 0001", AttendanceList.Status.BARRED);
        Candidate cddF = new Candidate(7, "RMB3", "FOONG GHIN YEW", "15WAU09184", "BAME 0001", AttendanceList.Status.ABSENT);
        Candidate cddN = new Candidate(8, "RMB3", "NG YEN AENG", "15WAD88888", "BAME 0001", AttendanceList.Status.ABSENT);

        attdList.addCandidate(cdd1, cdd1.getPaperCode(), AttendanceList.Status.ABSENT, cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), AttendanceList.Status.ABSENT, cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), AttendanceList.Status.ABSENT, cdd3.getProgramme());
        attdList.addCandidate(cdd4, cdd4.getPaperCode(), AttendanceList.Status.BARRED, cdd4.getProgramme());
        attdList.addCandidate(cdd5, cdd5.getPaperCode(), AttendanceList.Status.EXEMPTED, cdd5.getProgramme());
        attdList.addCandidate(cdd6, cdd6.getPaperCode(), AttendanceList.Status.BARRED, cdd6.getProgramme());
        attdList.addCandidate(cddF, cddF.getPaperCode(), AttendanceList.Status.ABSENT, cddF.getProgramme());
        attdList.addCandidate(cddN, cddN.getPaperCode(), AttendanceList.Status.ABSENT, cddN.getProgramme());

        return attdList;
    }

    private HashMap<String, ExamSubject> fakeTheExamPaper(){
        HashMap<String, ExamSubject> paperMap = new HashMap<>();

        ExamSubject subject1 = new ExamSubject("BAME 0001", "SUBJECT 1", 1, new Date(), 10,
                ExamSubject.ExamVenue.H2, ExamSubject.Session.AM);
        ExamSubject subject2 = new ExamSubject("BAME 0002", "SUBJECT 2", 2, new Date(), 10,
                ExamSubject.ExamVenue.H2, ExamSubject.Session.AM);
        ExamSubject subject3 = new ExamSubject("BAME 0003", "SUBJECT 3", 3, new Date(), 10,
                ExamSubject.ExamVenue.H2, ExamSubject.Session.AM);
        ExamSubject subject4 = new ExamSubject("BAME 0004", "SUBJECT 4", 4, new Date(), 10,
                ExamSubject.ExamVenue.H2, ExamSubject.Session.AM);

        paperMap.put(subject1.getPaperCode(), subject1);
        paperMap.put(subject2.getPaperCode(), subject2);
        paperMap.put(subject3.getPaperCode(), subject3);
        paperMap.put(subject4.getPaperCode(), subject4);

        return paperMap;
    }

    private void packageAttdListToSend(Intent listIntent){
        cddPapers       = new ArrayList<>();
        cddNames        = new ArrayList<>();
        cddTables       = new ArrayList<>();
        cddStatus       = new ArrayList<>();
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
