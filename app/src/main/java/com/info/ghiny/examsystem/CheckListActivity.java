package com.info.ghiny.examsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.CheckListDatabaseHelper;
import com.info.ghiny.examsystem.tools.ExpandListAdapter;
import com.info.ghiny.examsystem.tools.OnSwipeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GhinY on 14/05/2016.
 */
public class CheckListActivity extends AppCompatActivity {
    private ExpandListAdapter adapter;
    private CheckListDatabaseHelper databaseHelper;
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

        ExpandableListView checkList = (ExpandableListView) findViewById(R.id.assignedList);
        prepareList();

        assert checkList != null;
        checkList.setOnTouchListener(new OnSwipeListener(this){
            @Override
            public void onSwipeRight(){
                CheckListActivity.this.finish();
            }

            @Override
            public void onSwipeBottom() {
                Intent obtainIntent = new Intent(CheckListActivity.this, ObtainInfoActivity.class);
                startActivity(obtainIntent);
            }
        });

        adapter = new ExpandListAdapter(this, statusHead, dataChild);
        checkList.setAdapter(adapter);
    }

    public void onSubmit(View view){
        databaseHelper.clearDatabase();
        //finish();
        //INSERT TO TABLE
    }

    private void prepareList(){
        statusHead = new ArrayList<String>();
        dataChild = new HashMap<String, List<Candidate>>();

        // Adding child data
        statusHead.add("PRESENT");
        statusHead.add("ABSENT");
        statusHead.add("BARRED");
        statusHead.add("EXEMPTED");

        present = databaseHelper.getCandidatesList(AttendanceList.Status.PRESENT);
        absent  = databaseHelper.getCandidatesList(AttendanceList.Status.ABSENT);
        barred  = databaseHelper.getCandidatesList(AttendanceList.Status.BARRED);
        exempted = databaseHelper.getCandidatesList(AttendanceList.Status.EXEMPTED);


        dataChild.put(statusHead.get(0), present); // Header, Child data
        dataChild.put(statusHead.get(1), absent);
        dataChild.put(statusHead.get(2), barred);
        dataChild.put(statusHead.get(3), exempted);

    }
}
