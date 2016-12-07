package com.info.ghiny.examsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.fragments.FragmentPresent;
import com.info.ghiny.examsystem.interfacer.SubmissionMVP;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.manager.SubmissionPresenter;
import com.info.ghiny.examsystem.model.LoginModel;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.SubmissionModel;

public class SubmissionActivity extends AppCompatActivity implements SubmissionMVP.MvpView {

    private SubmissionMVP.MvpVPresenter taskPresenter;
    private ErrorManager errorManager;
    private ProgressDialog progDialog;

    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggleButton;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private TextView venue;
    private TextView user;
    private TextView session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);
        initMVP();
        initView();
    }

    private void initView(){
        toolbar         = (Toolbar)             findViewById(R.id.toolbar);
        drawer          = (DrawerLayout)        findViewById(R.id.drawer_layout);
        navigationView  = (NavigationView)      findViewById(R.id.nav_view);

        View header     = navigationView.getHeaderView(0);

        user    = (TextView) header.findViewById(R.id.submitInChargeName);
        venue   = (TextView) header.findViewById(R.id.submitInChargeVenue);
        session = (TextView) header.findViewById(R.id.submitInChargeSession);

        drawerToggleButton = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerToggleButton.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        drawer.addDrawerListener(drawerToggleButton);
        drawerToggleButton.syncState();
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar.setSubtitle("Present Candidates");

        if(user == null){
            Toast.makeText(this, "SOME", Toast.LENGTH_LONG).show();
        }
        StaffIdentity staff = LoginModel.getStaff();
        user.setText(staff.getName());
        venue.setText(staff.getExamVenue());
        session.setText("AM");

        taskPresenter.onNavigationItemSelected(toolbar, R.id.nav_present, errorManager,
                getSupportFragmentManager(), drawer);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggleButton.syncState();
    }

    private void initMVP(){
        errorManager    = new ErrorManager(this);
        SubmissionPresenter presenter   = new SubmissionPresenter(this);
        SubmissionModel model        = new SubmissionModel(presenter);
        presenter.setTaskModel(model);
        presenter.setHandler(new Handler());
        taskPresenter   = presenter;
    }

    @Override
    protected void onResume() {
        taskPresenter.onResume(errorManager);
        super.onResume();
    }

    @Override
    protected void onRestart() {
        taskPresenter.onRestart();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        taskPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_setting:
                Intent setting  = new Intent(this, SettingActivity.class);
                startActivity(setting);
                return true;
            default:
                return drawerToggleButton.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        taskPresenter.onPasswordReceived(requestCode, resultCode, data);
    }

    //==============================================================================================

    @Override
    public void onUpload(View view) {
        taskPresenter.onUpload();
    }

    @Override
    public void securityPrompt(boolean cancellable) {
        Intent secure   = new Intent(this, PopUpLogin.class);
        secure.putExtra("Cancellable", cancellable);
        startActivityForResult(secure, PopUpLogin.PASSWORD_REQ_CODE);
    }

    @Override
    public void navigateActivity(Class<?> cls) {
        Intent secure   = new Intent(this, cls);
        startActivityForResult(secure, PopUpLogin.PASSWORD_REQ_CODE);
    }

    @Override
    public void finishActivity() {
        this.finish();
    }

    @Override
    public void displayError(ProcessException err) {
        errorManager.displayError(err);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        return taskPresenter.onNavigationItemSelected(toolbar, item.getItemId(), errorManager,
                                                        getSupportFragmentManager(), drawer);
    }

    @Override
    public void displayReportWindow(String inCharge, String venue, String[] statusNo, String total) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("ATTENDANCE SUBMISSION");
        View view = this.getLayoutInflater().inflate(R.layout.pop_up_report_submission, null);
        dialog.setView(view);

        TextView inChargeV  = (TextView) view.findViewById(R.id.reportRowName);
        TextView venueV     = (TextView) view.findViewById(R.id.reportRowVenue);
        TextView presentV   = (TextView) view.findViewById(R.id.reportRowPresent);
        TextView absentV    = (TextView) view.findViewById(R.id.reportRowAbsent);
        TextView barredV    = (TextView) view.findViewById(R.id.reportRowBarred);
        TextView exemptedV  = (TextView) view.findViewById(R.id.reportRowExempted);
        TextView totalV     = (TextView) view.findViewById(R.id.reportRowTotal);

        inChargeV.setText(inCharge);
        venueV.setText(venue);
        presentV.setText(statusNo[0]);
        absentV.setText(statusNo[1]);
        barredV.setText(statusNo[2]);
        exemptedV.setText(statusNo[3]);
        totalV.setText(total);

        dialog.setCancelable(true);
        dialog.setPositiveButton(ProcessException.submitButton, taskPresenter);
        dialog.setNegativeButton(ProcessException.cancelButton, taskPresenter);

        AlertDialog alert = dialog.create();
        alert.show();
    }
}
