package com.info.ghiny.examsystem;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.info.ghiny.examsystem.tools.AssignHelper;
import com.info.ghiny.examsystem.tools.CustomException;
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
    private static final String TAG = AssignInfoActivity.class.getSimpleName();

    //Required Tools
    private ExamDatabaseLoader databaseHelper;  //to obtain Identity RegNum from IC
    private CheckListLoader checkListDB;        //temporary database in the mobile
    private AssignHelper helper;

    private CustomToast message;                //Toast message tool
    private Candidate candidate;                //store value of scanned Candidate

    private AttendanceList attdList;            //The attdList grabbed
    private ArrayList<String> cddNames;         //parameter to be send to CheckList
    private ArrayList<String> cddStatus;        //parameter to be send to CheckList
    private ArrayList<String> cddPapers;        //parameter to be send to CheckList
    private ArrayList<String> cddProgram;        //parameter to be send to CheckList
    private ArrayList<Integer> cddTables;       //parameter to be send to CheckList

    private String prevTableStr = "";           //To store previous scanned value
    private String prevCddStr = "";             //To store previous scanned value

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

        //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
        Candidate.setPaperList(fakeTheExamPaper()); //Suppose to query external database

        if(checkListDB.isEmpty())
            checkListDB.saveAttendanceList(prepareList());  //Suppose to query external database
        attdList.setAttendanceList(checkListDB.getLastSavedAttendanceList());
        helper          = new AssignHelper(attdList);
        //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

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
            @Override
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

        Candidate cdd   = null;
        Identity Id     = null;
        try{
            if(scanString.length() < 4){
                helper.checkTable(Integer.parseInt(scanString));
                tableView.setText(scanString);
            }

            if(scanString.length() == 12){
                Id  = databaseHelper.getIdentity(scanString);
                cdd = helper.checkCandidate(Id);
                //Candidate is legal, display all the candidate value
                cddView.setText(cdd.getStudentName());
                regNumView.setText(cdd.getRegNum());
                paperView.setText(cdd.getPaper().toString());
            }

            if(helper.tryAssignCandidate()){
                //Candidate successfully assigned, clear display and acknowledge with message
                tableView.setText("");  cddView.setText("");
                regNumView.setText(""); paperView.setText("");
                assert cdd != null;
                message.showCustomMessage(cdd.getStudentName()+ " Assigned to "
                        + cdd.getTableNumber().toString(), R.drawable.entry_icon);
            }
        } catch(CustomException err){
            //If Id is null, it will be caught as the first Exception thrown, ERR_NULL_IDENTITY
            //Therefore, Id can never be null for the rest of the Exceptions thrown
            //Display message according to the error caught
            switch (err.getErrorCode()){
                case CustomException.ERR_NULL_IDENTITY:
                    message.showCustomMessage("Not an Identity",  R.drawable.warn_icon);
                    break;
                case CustomException.ERR_INCOMPLETE_ID:
                    throw new NullPointerException("ID register Number is null");
                case CustomException.ERR_NULL_CANDIDATE:
                    message.showCustomMessage(Id.getName() +
                            " doest not belong to this venue", R.drawable.msg_icon);
                    break;
                case CustomException.ERR_STATUS_BARRED:
                    message.showCustomMessage(Id.getName() +
                            " have been barred", R.drawable.warn_icon);
                    break;
                case CustomException.ERR_STATUS_EXEMPTED:
                    message.showCustomMessage("The paper was exempted for " +
                            Id.getName(), R.drawable.msg_icon);
                    break;
                case CustomException.ERR_PAPER_NOT_MATCH:
                    //TO DO
                    break;
                case CustomException.ERR_TABLE_REASSIGN:
                    //TO DO
                    break;
                case CustomException.ERR_CANDIDATE_REASSIGN:
                    //TO DO
                    break;
                case CustomException.ERR_NULL_TABLE:
                    //TO DP
                    break;
            }
        }
    }



    private AttendanceList prepareList(){
        AttendanceList attdList = new AttendanceList();

        Candidate cdd1 = new Candidate(0, "RMB3", "FGY", "15WAD00001", "BAME 0001", AttendanceList.Status.ABSENT);
        Candidate cdd2 = new Candidate(0, "RMB3", "NYN", "15WAD00002", "BAME 0001", AttendanceList.Status.ABSENT);
        Candidate cdd3 = new Candidate(0, "RMB3", "LHN", "15WAD00003", "BAME 0001", AttendanceList.Status.ABSENT);
        Candidate cdd4 = new Candidate(0, "RMB3", "YZL", "15WAD00004", "BAME 0001", AttendanceList.Status.BARRED);
        Candidate cdd5 = new Candidate(0, "RMB3", "SYL", "15WAD00005", "BAME 0001", AttendanceList.Status.EXEMPTED);
        Candidate cdd6 = new Candidate(0, "RMB3", "WJS", "15WAD00006", "BAME 0001", AttendanceList.Status.BARRED);
        Candidate cddF = new Candidate(0, "RMB3", "FOONG GHIN YEW", "15WAU09184", "BAME 0001", AttendanceList.Status.ABSENT);
        Candidate cddN = new Candidate(0, "RMB3", "NG YEN AENG", "15WAD88888", "BAME 0001", AttendanceList.Status.ABSENT);

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
        cddProgram      = new ArrayList<>();
        List<String> regNumList = attdList.getAllCandidateRegNumList();
        for(int i = 0; i < regNumList.size(); i++){
            Candidate listCdd = attdList.getCandidate(regNumList.get(i));
            packageCandidateToSend(listCdd);
        }
        listIntent.putStringArrayListExtra("Name", cddNames);
        listIntent.putStringArrayListExtra("Status", cddStatus);
        listIntent.putStringArrayListExtra("Paper", cddPapers);
        listIntent.putStringArrayListExtra("Programme", cddProgram);
        listIntent.putIntegerArrayListExtra("Table", cddTables);

    }

    private void packageCandidateToSend(Candidate cdd){
        cddNames.add(cdd.getStudentName());
        cddStatus.add(cdd.getStatus().toString());
        cddPapers.add(cdd.getPaperCode());
        cddProgram.add(cdd.getProgramme());
        cddTables.add(cdd.getTableNumber());
    }
}
