package com.info.ghiny.examsystem;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.info.ghiny.examsystem.database.LocalDbLoader;
import com.info.ghiny.examsystem.interfacer.HomeOptionMVP;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.manager.HomeOptionPresenter;
import com.info.ghiny.examsystem.manager.IconManager;
import com.info.ghiny.examsystem.model.HomeOptionModel;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 01/07/2016.
 */
public class HomeOptionActivity extends AppCompatActivity implements HomeOptionMVP.MvpView{

    public static final String FEATURE_INFO_GRAB    = "InfoGrab";
    public static final String FEATURE_COLLECTION   = "Collection";
    public static final String FEATURE_CONNECTION   = "Connection";
    public static final String FEATURE_ATTENDANCE   = "Attendance";

    private ErrorManager errorManager;
    private HomeOptionMVP.MvpVPresenter taskPresenter;

    private ImageView infoGrabButton;
    private ImageView collectionButton;
    private ImageView connectionButton;
    private ImageView attendanceButton;
    private ProgressDialog progDialog;

    private RelativeLayout help;
    private boolean helpDisplay;

    private boolean infoEnable;
    private boolean collectionEnable;
    private boolean connectionEnable;
    private boolean attendanceEnable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_option);

        initMVP();
        initView();
    }

    @Override
    protected void onResume() {
        taskPresenter.onResume(errorManager);
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        taskPresenter.onRestart();
    }

    @Override
    protected void onDestroy() {
        taskPresenter.onDestroy();
        super.onDestroy();
    }

    private void initView(){
        infoGrabButton      = (ImageView) findViewById(R.id.optionInfoGrab);
        collectionButton    = (ImageView) findViewById(R.id.optionCollection);
        connectionButton    = (ImageView) findViewById(R.id.optionConnection);
        attendanceButton    = (ImageView) findViewById(R.id.optionAttendance);
        help                = (RelativeLayout) findViewById(R.id.homeHelpContext);

        Intent loginActivity    = getIntent();
        infoEnable          = loginActivity.getBooleanExtra(FEATURE_INFO_GRAB, true);
        collectionEnable    = loginActivity.getBooleanExtra(FEATURE_COLLECTION, false);
        connectionEnable    = loginActivity.getBooleanExtra(FEATURE_CONNECTION, false);
        attendanceEnable    = loginActivity.getBooleanExtra(FEATURE_ATTENDANCE, true);

        setupButton(infoEnable, infoGrabButton);
        setupButton(collectionEnable, collectionButton);
        setupButton(connectionEnable, connectionButton);
        setupButton(attendanceEnable, attendanceButton);

        helpDisplay = false;
        help.setVisibility(View.INVISIBLE);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpDisplay = false;
                help.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void initMVP(){
        errorManager    = new ErrorManager(this);
        HomeOptionPresenter presenter = new HomeOptionPresenter(this);
        presenter.setHandler(new Handler());
        HomeOptionModel model         = new HomeOptionModel(presenter, new LocalDbLoader(this));
        presenter.setTaskModel(model);
        taskPresenter   = presenter;
    }

    private void setupButton(boolean enable, ImageView button){
        button.setClickable(enable);
        if(enable){
            button.setBackgroundResource(R.drawable.custom_border_enabled);
        } else {
            button.setBackgroundResource(R.drawable.custom_border_disabled);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_help:
                helpDisplay = true;
                help.setVisibility(View.VISIBLE);
                return true;
            case R.id.action_setting:
                return taskPresenter.onSetting();
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        taskPresenter.onPasswordReceived(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if(helpDisplay){
            helpDisplay = false;
            help.setVisibility(View.INVISIBLE);
        } else {
            taskPresenter.onBackPressed();
        }
    }

    @Override
    public void finishActivity() {
        this.finish();
    }

    @Override
    public void displayError(ProcessException err) {
        this.errorManager.displayError(err);
    }

    @Override
    public void navigateActivity(Class<?> cls) {
        Intent next = new Intent(this, cls);
        startActivity(next);
    }

    @Override
    public void securityPrompt(boolean cancellable) {
        Intent secure   = new Intent(this, PopUpLogin.class);
        secure.putExtra("Cancellable", cancellable);
        startActivityForResult(secure, PopUpLogin.PASSWORD_REQ_CODE);
    }

    public void onAttendance(View view){
        taskPresenter.onAttendance();
    }

    public void onInfo(View view){
        taskPresenter.onInfo();
    }

    public void onCollection(View view){
        taskPresenter.onCollection();
    }

    public void onDistribution(View view){
        taskPresenter.onDistribution();
    }

    @Override
    public void openProgressWindow(String title, String message) {
        progDialog  = new ProgressDialog(this, R.style.ProgressDialogTheme);
        progDialog.setMessage(message);
        progDialog.setTitle(title);
        progDialog.show();
    }

    @Override
    public void closeProgressWindow() {
        if(progDialog != null)
            progDialog.dismiss();
    }
}
