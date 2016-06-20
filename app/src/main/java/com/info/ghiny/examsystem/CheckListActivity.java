package com.info.ghiny.examsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.adapter.ExpandListAdapter;
import com.info.ghiny.examsystem.tools.AssignHelper;
import com.info.ghiny.examsystem.tools.OnSwipeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GhinY on 14/05/2016.
 */
public class CheckListActivity extends AppCompatActivity {
    private ExpandListAdapter adapter;
    private AssignHelper helper;
    private List<String> statusHead;
    private HashMap<String, List<Candidate>> dataChild;

    private List<Candidate> present;
    private List<Candidate> absent;
    private List<Candidate> barred;
    private List<Candidate> exempted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        helper = new AssignHelper();

        present = new ArrayList<>();
        absent  = new ArrayList<>();
        barred  = new ArrayList<>();
        exempted = new ArrayList<>();

        prepareList();

        ExpandableListView checkList = (ExpandableListView) findViewById(R.id.assignedList);
        assert checkList != null;
        checkList.setOnTouchListener(new OnSwipeListener(this){
            @Override
            public void onSwipeRight(){
                finish();
            }
        });

        adapter = new ExpandListAdapter(this, statusHead, dataChild);
        checkList.setAdapter(adapter);
    }

    public void onSubmit(View view){
        //CheckListLoader.clearDatabase();
        Intent intent = new Intent(this, FragmentListActivity.class);
        startActivity(intent);
        //finish();
        //INSERT TO TABLE
    }

    private void prepareList(){
        statusHead = new ArrayList<>();
        dataChild = new HashMap<>();

        // Adding child data
        statusHead.add("PRESENT");
        statusHead.add("ABSENT");
        statusHead.add("BARRED");
        statusHead.add("EXEMPTED");

        AttendanceList attdList = AssignHelper.getAttdList();
        List<String> candidates = attdList.getAllCandidateRegNumList();

        for(int i = 0; i < candidates.size(); i++){
            Candidate cdd = attdList.getCandidate(candidates.get(i));
            switch(cdd.getStatus()){
                case PRESENT:
                    cdd.setStatus(AttendanceList.Status.PRESENT);
                    present.add(cdd);
                    break;
                case BARRED:
                    cdd.setStatus(AttendanceList.Status.BARRED);
                    barred.add(cdd);
                    break;
                case EXEMPTED:
                    cdd.setStatus(AttendanceList.Status.EXEMPTED);
                    exempted.add(cdd);
                    break;
                default:
                    cdd.setStatus(AttendanceList.Status.ABSENT);
                    absent.add(cdd);
                    break;
            }
        }

        dataChild.put(statusHead.get(0), present); // Header, Child data
        dataChild.put(statusHead.get(1), absent);
        dataChild.put(statusHead.get(2), barred);
        dataChild.put(statusHead.get(3), exempted);

    }
}
