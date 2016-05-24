package com.info.ghiny.examsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;

import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.CheckListDatabaseHelper;
import com.info.ghiny.examsystem.tools.ExpandListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GhinY on 14/05/2016.
 */
public class CheckListActivity extends AppCompatActivity {
    private ExpandListAdapter adapter;
    private ExpandableListView checkList;
    private Intent grabIntent;
    private CheckListDatabaseHelper databaseHelper;
    private List<Candidate> candidates;
    private List<String> statusHead;
    private HashMap<String, List<Candidate>> dataChild;

    List<Candidate> present;
    List<Candidate> absent;
    List<Candidate> barred;
    List<Candidate> exempted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        databaseHelper = new CheckListDatabaseHelper(this);

        grabIntent = getIntent();
        ArrayList<String> nameList  = grabIntent.getStringArrayListExtra("Candidate");
        ArrayList<Integer> tableList= grabIntent.getIntegerArrayListExtra("Table");

        candidates = new ArrayList<Candidate>();

        for(int i=0; i < nameList.size(); i++){
            candidates.add(new Candidate(tableList.get(i), "BAME 2002", "PROGRAMMING IN C",
                    Candidate.Status.PRESENT, nameList.get(i)));
        }

        checkList = (ExpandableListView) findViewById(R.id.assignedList);

        addCandidate(candidates);
        prepareList();

        adapter = new ExpandListAdapter(this, statusHead, dataChild);
        checkList.setAdapter(adapter);
    }

    public void onHome(View view){  finish();}

    public void onAddOn(View view){
        //Intent newIntent = new Intent(this, AssignInfoActivity.class);
        //startActivityForResult(newIntent, ASSIGN_REQ_CODE);
        finish();
    }
    public void onSubmit(View view){
        finish();
        //INSERT TO TABLE
    }

    public void addCandidate(List<Candidate> candidates){
        databaseHelper.insertCandidateList(candidates);
    }

    private void prepareList(){
        statusHead = new ArrayList<String>();
        dataChild = new HashMap<String, List<Candidate>>();

        // Adding child data
        statusHead.add("PRESENT");
        statusHead.add("ABSENT");
        statusHead.add("BARRED");
        statusHead.add("EXEMPTED");

        // Adding child data
        //List<Candidate> pres = new ArrayList<Candidate>();
        //pres.add(new Candidate(1, "BAME 2134", "TEST DRIVEN DEVELOPEMENT",
        //      Candidate.Status.PRESENT, "WONG JANG SING"));
        //pres.add(new Candidate(2, "BAME 2134", "TEST DRIVEN DEVELOPEMENT",
        //        Candidate.Status.PRESENT, "LOUISE SIAH YI LOI"));
        //pres.add(new Candidate(3, "BAME 2134", "TEST DRIVEN DEVELOPEMENT",
        //        Candidate.Status.PRESENT, "LIU YIK CHENG"));
        //pres.add(new Candidate(4, "BAME 2134", "TEST DRIVEN DEVELOPEMENT",
        //        Candidate.Status.PRESENT, "LIEW EE MEI"));
        //pres.add(new Candidate(5, "BAME 2134", "TEST DRIVEN DEVELOPEMENT",
        //        Candidate.Status.PRESENT, "CHU JAAN HORNG"));

        //List<Candidate> abs = new ArrayList<Candidate>();
        //abs.add(new Candidate(0, "BAME 2134", "TEST DRIVEN DEVELOPEMENT",
        //        Candidate.Status.ABSENT, "LAI HWA NENG"));

        //barred = new ArrayList<Candidate>();
        //barred.add(new Candidate(0, "BAME 2134", "TEST DRIVEN DEVELOPEMENT",
        //       Candidate.Status.BARRED, "NICKSON YAP ZHEN LYNN"));

        //exempted = new ArrayList<Candidate>();
        //exempted.add(new Candidate(0, "BAME 2134", "TEST DRIVEN DEVELOPEMENT",
        //        Candidate.Status.EXEMPTED, "POH TZE VEN"));

        present = databaseHelper.getCandidatesList(Candidate.Status.PRESENT);
        absent  = databaseHelper.getCandidatesList(Candidate.Status.ABSENT);
        barred  = databaseHelper.getCandidatesList(Candidate.Status.BARRED);
        exempted = databaseHelper.getCandidatesList(Candidate.Status.EXEMPTED);


        dataChild.put(statusHead.get(0), present); // Header, Child data
        dataChild.put(statusHead.get(1), absent);
        dataChild.put(statusHead.get(2), barred);
        dataChild.put(statusHead.get(3), exempted);

    }
}
