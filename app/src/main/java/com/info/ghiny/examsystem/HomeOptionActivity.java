package com.info.ghiny.examsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by GhinY on 01/07/2016.
 */
public class HomeOptionActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_option);
    }

    public void onAssign(View view){
        Intent assignIntent = new Intent(this, AssignInfoActivity.class);
        startActivity(assignIntent);
    }

    public void onInfo(View view){
        Intent infoIntent   = new Intent(this, ObtainInfoActivity.class);
        startActivity(infoIntent);
    }

    public void onSubmission(View view){
        Intent submitIntent = new Intent(this, FragmentListActivity.class);
        startActivity(submitIntent);
    }

    public void onSetting(View view){

    }
}
