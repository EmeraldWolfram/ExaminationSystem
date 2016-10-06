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
import com.info.ghiny.examsystem.interfacer.TaskScanViewOld;
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
    private TaskScanViewOld taskScanViewOld;
    private SetterView setterView;
    private AssignModel assignModel;
    private boolean navigationFlag;

    public AssignManager(TaskScanViewOld taskScanViewOld, SetterView setterView, CheckListLoader dBLoader){
        this.taskScanViewOld = taskScanViewOld;
        this.setterView     = setterView;
        this.navigationFlag = false;
        this.assignModel    = new AssignModel(this);
        try{
            this.assignModel.initLoader(dBLoader);
        } catch (ProcessException err) {
            taskScanViewOld.displayError(err);
        }
    }
    /**
    public void onCreate(){
        use dbLoader to query attdList
        if available, no need to do anything
        else download attendance list & paper list
     }
     */

    public void setAssignModel(AssignModel assignModel) {
        this.assignModel = assignModel;
    }

    @Override
    public void onPause(){
        taskScanViewOld.pauseScanning();
    }

    @Override
    public void onResume(){
        try{
            assignModel.updateAssignList();
            taskScanViewOld.resumeScanning();
        } catch (ProcessException err) {
            taskScanViewOld.displayError(err);
        }
    }

    @Override
    public void onRestart() {
        if(!navigationFlag){
            taskScanViewOld.securityPrompt(false);
        }
        navigationFlag  = false;
    }

    @Override
    public void onBackPressed(){
        taskScanViewOld.pauseScanning();
        ProcessException err    = new ProcessException("Confirm logout and exit?",
                ProcessException.YES_NO_MESSAGE, IconManager.MESSAGE);
        err.setBackPressListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                taskScanViewOld.resumeScanning();
                dialog.cancel();
            }
        });
        err.setListener(ProcessException.yesButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Save attendance list & paper list to database
                dialog.cancel();
                taskScanViewOld.finishActivity();
            }
        });
        err.setListener(ProcessException.noButton, buttonListener);
        taskScanViewOld.displayError(err);
    }

    @Override
    public void onPasswordReceived(int requestCode, int resultCode, Intent data){
        if(requestCode == PopUpLogin.PASSWORD_REQ_CODE && resultCode == Activity.RESULT_OK){
            String password = data.getStringExtra("Password");
            try{
                taskScanViewOld.pauseScanning();
                if(!LoginHelper.getStaff().matchPassword(password))
                    throw new ProcessException("Access denied. Incorrect Password",
                            ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);

                taskScanViewOld.resumeScanning();
            } catch(ProcessException err){
                taskScanViewOld.displayError(err);
                taskScanViewOld.securityPrompt(false);
            }
        }
    }

    @Override
    public void onScan(String scanStr){
        try{
            taskScanViewOld.pauseScanning();
            assignModel.tryAssignScanValue(scanStr);
            taskScanViewOld.resumeScanning();
        } catch (ProcessException err) {
            err.setListener(ProcessException.okayButton, buttonListener);
            err.setListener(ProcessException.noButton, buttonListener);
            err.setListener(ProcessException.yesButton, buttonListener);

            taskScanViewOld.displayError(err);
            if(err.getErrorType() == ProcessException.MESSAGE_TOAST)
                taskScanViewOld.resumeScanning();
        }
    }

    @Override
    public void onSwipeLeft(){
        navigationFlag  = true;
        taskScanViewOld.navigateActivity(FragmentListActivity.class);
    }

    @Override
    public void onSwipeBottom(){
        navigationFlag  = true;
        taskScanViewOld.navigateActivity(ObtainInfoActivity.class);
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
            taskScanViewOld.displayError(err);
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
            taskScanViewOld.resumeScanning();
            dialog.cancel();
        }
    };
}
