package com.info.ghiny.examsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.info.ghiny.examsystem.fragments.PresentFragment;
import com.info.ghiny.examsystem.interfacer.SubmissionMVP;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.manager.ReportAttdPresenter;
import com.info.ghiny.examsystem.manager.SubmissionPresenter;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.ReportAttdModel;
import com.info.ghiny.examsystem.model.SubmissionModel;

public class SubmissionActivity extends AppCompatActivity implements SubmissionMVP.MvpView {

    private SubmissionMVP.MvpVPresenter taskPresenter;
    private ErrorManager errorManager;
    private ProgressDialog progDialog;

    private FrameLayout pageFrame;
    private Toolbar toolbar;
    private FloatingActionButton submitButton;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggleButton;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);

        initView();
        initMVP();
    }

    private void initView(){
        pageFrame       = (FrameLayout)         findViewById(R.id.submitContainer);
        toolbar         = (Toolbar)             findViewById(R.id.toolbar);
        submitButton    = (FloatingActionButton)findViewById(R.id.fab);
        drawer          = (DrawerLayout)        findViewById(R.id.drawer_layout);
        navigationView  = (NavigationView)      findViewById(R.id.nav_view);

        drawerToggleButton = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(drawerToggleButton);
        drawerToggleButton.syncState();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.submitContainer, new PresentFragment());
        ft.commit();
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
    protected void onRestart() {
        taskPresenter.onRestart();
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.submission, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        taskPresenter.onPasswordReceived(requestCode, resultCode, data);
    }

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
        progDialog  = ProgressDialog.show(this, title, message);
    }

    @Override
    public void closeProgressWindow() {
        if(progDialog != null)
            progDialog.dismiss();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        return taskPresenter.onNavigationItemSelected(item, getSupportFragmentManager(), drawer);
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
