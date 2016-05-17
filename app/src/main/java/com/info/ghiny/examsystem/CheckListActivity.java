package com.info.ghiny.examsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

/**
 * Created by GhinY on 14/05/2016.
 */
public class CheckListActivity extends AppCompatActivity {
    private CheckListAdapter adapter;
    public static final int ASSIGN_REQ_CODE = 127;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        ListView checkList = (ListView)findViewById(R.id.assignedList);
        adapter = new CheckListAdapter();
        checkList.setAdapter(adapter);
    }

    public void onHome(View view){  finish();}

    public void onAddOn(View view){
        Intent newIntent = new Intent(this, AssignInfoActivity.class);
        startActivityForResult(newIntent, ASSIGN_REQ_CODE);
    }
    public void onSubmit(View view){
        //INSERT TO TABLE
    }

     public void onActivityResult(int reqCode, int result, Intent data){
        if(reqCode == ASSIGN_REQ_CODE){
            if(result == RESULT_OK){
                String candidate = data.getStringExtra("Candidate");
                int tableNumber = data.getIntExtra("Table", 0);
                adapter.assignCandidate(tableNumber, "BAME 2002", Candidate.Status.PRESENT, candidate);
                adapter.notifyDataSetChanged();
            }

        }
     }
}
