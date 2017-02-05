package com.info.ghiny.examsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.Role;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.interfacer.AttdReportMVP;
import com.info.ghiny.examsystem.manager.AttdReportPresenter;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.model.AttdReportModel;
import com.info.ghiny.examsystem.model.LoginModel;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.view_holder.ProgrammeDisplayHolder;
import com.info.ghiny.examsystem.view_holder.StatusDisplayHolder;

import java.util.HashMap;
import java.util.List;

import static java.security.AccessController.getContext;

public class AttdReportActivity extends AppCompatActivity implements AttdReportMVP.MvpView {

    private ExpandableListView report;
    private ReportListAdapter adapter;
    private ProgressDialog progDialog;
    private FloatingActionButton submitButton;

    private AttdReportMVP.MvpVPresenter taskPresenter;
    private ErrorManager errorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attd_report);
        initMVP();
        initView();
    }

    private void initMVP(){
        errorManager    = new ErrorManager(this);

        AttdReportPresenter presenter   = new AttdReportPresenter(this);
        AttdReportModel model           = new AttdReportModel(presenter);
        presenter.setTaskModel(model);
        presenter.setTimer(new Handler());
        taskPresenter                   = presenter;
    }

    private void initView(){
        report          = (ExpandableListView) findViewById(R.id.reportList);
        submitButton    = (FloatingActionButton)findViewById(R.id.submitButton);
        adapter         = new ReportListAdapter();

        report.setAdapter(adapter);
        submitButton.setVisibility(
                (LoginModel.getStaff().getRole() == Role.IN_CHARGE) ? View.VISIBLE : View.INVISIBLE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        taskPresenter.onPasswordReceived(requestCode, resultCode, data);
    }

    @Override
    public void onSubmit() {
        taskPresenter.onSubmit();
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

    @Override
    public void displayError(ProcessException err) {
        errorManager.displayError(err);
    }

    @Override
    public void finishActivity() {
        this.finish();
    }

    @Override
    public void runItSeparate(Runnable runner) {
        runOnUiThread(runner);
    }

    @Override
    public void navigateActivity(Class<?> cls) {
        Intent secure   = new Intent(this, cls);
        startActivityForResult(secure, PopUpLogin.PASSWORD_REQ_CODE);
    }

    @Override
    public void securityPrompt(boolean cancellable) {
        Intent secure   = new Intent(this, PopUpLogin.class);
        secure.putExtra("Cancellable", cancellable);
        startActivityForResult(secure, PopUpLogin.PASSWORD_REQ_CODE);
    }

    private class ReportListAdapter extends BaseExpandableListAdapter{

        //= Header Control =========================================================================
        @Override
        public int getGroupCount() {
            return taskPresenter.getGroupCount();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return taskPresenter.getGroupId(groupPosition);
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View v, ViewGroup parent) {
            ProgrammeDisplayHolder title = (ProgrammeDisplayHolder) taskPresenter.getGroup(groupPosition);

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.attendance_paper, null);
            }

            title.setView(v);
            title.setProgramme(title.getProgramme());
            title.setTotal(title.getTotal());

            return v;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return taskPresenter.getGroup(groupPosition);
        }

        //= Child Control ==========================================================================
        @Override
        public int getChildrenCount(int groupPosition) {
            return taskPresenter.getChildrenCount(groupPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return taskPresenter.getChildId(groupPosition, childPosition);
        }

        @Override
        public View getChildView(int groupP, int childP, boolean isLast, View v, ViewGroup parent) {
            StatusDisplayHolder status = (StatusDisplayHolder) taskPresenter.getChild(groupP, childP);

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.attendance_paper, null);
            }

            status.setView(v);
            status.setStatus(status.getStatus());
            status.setQuantity(status.getQuantity());

            return v;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return taskPresenter.getChild(groupPosition, childPosition);
        }

        //= Default Interface ======================================================================
        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
