package com.info.ghiny.examsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.manager.IconManager;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 01/07/2016.
 */
public class HomeOptionActivity extends AppCompatActivity
        implements DialogInterface.OnCancelListener, DialogInterface.OnClickListener{

    public static final String FEATURE_INFO_GRAB    = "InfoGrab";
    public static final String FEATURE_COLLECTION   = "Collection";
    public static final String FEATURE_CONNECTION   = "Connection";
    public static final String FEATURE_ATTENDANCE   = "Attendance";

    private ErrorManager errorManager;

    private ImageView infoGrabButton;
    private ImageView collectionButton;
    private ImageView connectionButton;
    private ImageView attendanceButton;

    private boolean infoEnable;
    private boolean collectionEnable;
    private boolean connectionEnable;
    private boolean attendanceEnable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_option);

        initView();
        initMVP();
    }

    private void initView(){
        infoGrabButton      = (ImageView) findViewById(R.id.optionInfoGrab);
        collectionButton    = (ImageView) findViewById(R.id.optionCollection);
        connectionButton    = (ImageView) findViewById(R.id.optionConnection);
        attendanceButton    = (ImageView) findViewById(R.id.optionAttendance);

        Intent loginActivity    = getIntent();
        infoEnable          = loginActivity.getBooleanExtra(FEATURE_INFO_GRAB, true);
        collectionEnable    = loginActivity.getBooleanExtra(FEATURE_COLLECTION, false);
        connectionEnable    = loginActivity.getBooleanExtra(FEATURE_CONNECTION, false);
        attendanceEnable    = loginActivity.getBooleanExtra(FEATURE_ATTENDANCE, true);

        infoGrabButton.setClickable(infoEnable);

        collectionButton.setClickable(collectionEnable);
        connectionButton.setClickable(connectionEnable);
        connectionButton.setBackgroundColor(Color.GRAY);

        attendanceButton.setClickable(attendanceEnable);

    }

    private void initMVP(){
        errorManager    = new ErrorManager(this);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater   = this.getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_setting:
                Intent setting  = new Intent(this, SettingActivity.class);
                startActivity(setting);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        ProcessException err    = new ProcessException("Confirm logout and exit?",
                ProcessException.YES_NO_MESSAGE, IconManager.MESSAGE);
        err.setBackPressListener(this);
        err.setListener(ProcessException.yesButton, this);
        err.setListener(ProcessException.noButton, this);
        errorManager.displayError(err);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        dialog.cancel();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case DialogInterface.BUTTON_POSITIVE:
                finish();
                break;
            default:
                dialog.cancel();
                break;
        }
    }

    public void onAttendance(View view){
        Intent assignIntent = new Intent(this, TakeAttdActivity.class);
        startActivity(assignIntent);
    }

    public void onInfo(View view){
        Intent infoIntent   = new Intent(this, InfoGrabActivity.class);
        startActivity(infoIntent);
    }

    public void onCollection(View view){
        Intent collectionIntent = new Intent(this, CollectionActivity.class);
        startActivity(collectionIntent);
    }

    public void onConnection(View view){
        Toast.makeText(this, "CONNECTION Clicked", Toast.LENGTH_LONG).show();
    }
}
