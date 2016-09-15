package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import com.info.ghiny.examsystem.FragmentListActivity;
import com.info.ghiny.examsystem.ObtainInfoActivity;
import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.interfacer.AssignPresenter;
import com.info.ghiny.examsystem.interfacer.TaskScanView;
import com.info.ghiny.examsystem.interfacer.SetterView;
import com.info.ghiny.examsystem.interfacer.TaskScanPresenter;
import com.info.ghiny.examsystem.interfacer.TaskSecurePresenter;
import com.info.ghiny.examsystem.model.AssignModel;
import com.info.ghiny.examsystem.model.IconManager;
import com.info.ghiny.examsystem.model.LoginHelper;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 08/08/2016.
 */
public class AssignManager implements AssignPresenter, TaskScanPresenter, TaskSecurePresenter{
    private TaskScanView taskScanView;
    private SetterView setterView;
    private AssignModel assignModel;
    private boolean navigationFlag;

    public AssignManager(TaskScanView taskScanView, SetterView setterView, CheckListLoader dBLoader){
        this.taskScanView = taskScanView;
        this.setterView     = setterView;
        this.navigationFlag = false;
        this.assignModel    = new AssignModel(this);
        try{
            this.assignModel.initLoader(dBLoader);
        } catch (ProcessException err) {
            taskScanView.displayError(err);
        }
    }

    public void setAssignModel(AssignModel assignModel) {
        this.assignModel = assignModel;
    }

    @Override
    public void onPause(){
        taskScanView.pauseScanning();
    }

    @Override
    public void onResume(){
        try{
            assignModel.updateAssignList();
            taskScanView.resumeScanning();
        } catch (ProcessException err) {
            taskScanView.displayError(err);
        }
    }

    @Override
    public void onRestart() {
        if(!navigationFlag){
            taskScanView.securityPrompt(false);
        }
        navigationFlag  = false;
    }

    @Override
    public void onBackPressed(){
        taskScanView.pauseScanning();
        ProcessException err    = new ProcessException("Confirm logout and exit?",
                ProcessException.YES_NO_MESSAGE, IconManager.MESSAGE);
        err.setBackPressListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                taskScanView.resumeScanning();
                dialog.cancel();
            }
        });
        err.setListener(ProcessException.yesButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                taskScanView.finishActivity();
            }
        });
        err.setListener(ProcessException.noButton, buttonListener);
        taskScanView.displayError(err);
    }

    @Override
    public void onPasswordReceived(int requestCode, int resultCode, Intent data){
        if(requestCode == PopUpLogin.PASSWORD_REQ_CODE && resultCode == Activity.RESULT_OK){
            String password = data.getStringExtra("Password");
            try{
                taskScanView.pauseScanning();
                if(!LoginHelper.getStaff().matchPassword(password))
                    throw new ProcessException("Access denied. Incorrect Password",
                            ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);

                taskScanView.resumeScanning();
            } catch(ProcessException err){
                taskScanView.displayError(err);
                taskScanView.securityPrompt(false);
            }
        }
    }

    @Override
    public void onScan(String scanStr){
        try{
            taskScanView.pauseScanning();
            assignModel.tryAssignScanValue(scanStr);
            taskScanView.resumeScanning();
        } catch (ProcessException err) {
            err.setListener(ProcessException.okayButton, buttonListener);
            err.setListener(ProcessException.noButton, buttonListener);
            err.setListener(ProcessException.yesButton, buttonListener);

            taskScanView.displayError(err);
            if(err.getErrorType() == ProcessException.MESSAGE_TOAST)
                taskScanView.resumeScanning();
        }
    }

    @Override
    public void navigateToDisplay(){
        navigationFlag  = true;
        taskScanView.navigateActivity(FragmentListActivity.class);
    }

    @Override
    public void navigateToDetail(){
        navigationFlag  = true;
        taskScanView.navigateActivity(ObtainInfoActivity.class);
    }

    @Override
    public void displayTable(Integer tableNum){
        if(tableNum > 0){
            setterView.setTableView(tableNum.toString());
        } else{
            setterView.setTableView("");
        }
    }

    @Override
    public void displayCandidate(Candidate cdd){
        try{
            ExamSubject paper = cdd.getPaper();
            setterView.setCandidateView(cdd.getExamIndex(), cdd.getRegNum(), paper.toString());
        } catch (ProcessException err) {
            taskScanView.displayError(err);
        }
    }

    @Override
    public void resetDisplay(){
        setterView.setTableView("");
        setterView.setCandidateView("","","");
    }

    private DialogInterface.OnClickListener buttonListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            taskScanView.resumeScanning();
            dialog.cancel();
        }
    };
}
