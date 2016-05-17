package com.info.ghiny.examsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by GhinY on 06/05/2016.
 */
public class HomeOptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }

    public void onObtainInfo(View view){
        Intent newIntent = new Intent(this, ObtainInfoActivity.class);
        startActivity(newIntent);
    }

    public void onSubmission(View view){
        Intent newIntent = new Intent(this, CheckListActivity.class);
        startActivity(newIntent);
    }
}
