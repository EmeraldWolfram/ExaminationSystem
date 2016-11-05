package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.ReportAttdActivity;
import com.info.ghiny.examsystem.InfoGrabActivity;
import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.TakeAttdMVP;
import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.IconManager;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

/**
 * Created by GhinY on 08/08/2016.
 */
public class TakeAttdPresenter implements TakeAttdMVP.VPresenter, TakeAttdMVP.MPresenter{
    private TakeAttdMVP.View taskView;
    private TakeAttdMVP.Model taskModel;
    private boolean navigationFlag;
    private boolean initComplete;
    private Handler handler;

    private SharedPreferences preferences;
    private boolean crossHair;
    private boolean beep;
    private boolean vibrate;
    private int mode;

    public TakeAttdPresenter(TakeAttdMVP.View taskView, SharedPreferences pref){
        this.preferences    = pref;
        this.taskView       = taskView;
        this.navigationFlag = false;
    }

    public void setTaskModel(TakeAttdMVP.Model taskModel) {
        this.taskModel = taskModel;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setNavigationFlag(boolean navigationFlag) {
        this.navigationFlag = navigationFlag;
    }

    @Override
    public void setInitComplete(boolean initComplete) {
        this.initComplete = initComplete;
    }

    @Override
    public boolean isInitComplete() {
        return initComplete;
    }

    @Override
    public void onResume(){
        loadSetting();
        try{
            //if(taskModel.isInitialized())
            taskModel.updateAssignList();
            taskView.resumeScanning();
        } catch (ProcessException err) {
            taskView.displayError(err);
        }
    }

    @Override
    public void onResume(final ErrorManager errManager) {
        ExternalDbLoader.getTcpClient().setMessageListener(new TCPClient.OnMessageReceived() {
            //here the messageReceived method is implemented
            @Override
            public void messageReceived(String message) {
                onChiefRespond(errManager, message);
            }
        });
        if(!taskModel.isInitialized()){
            try{
                taskModel.initAttendance();
                taskView.openProgressWindow("Initializing:", "Preparing Attendance List...");
                handler.postDelayed(taskModel, 5000);
            } catch (ProcessException err) {
                taskView.displayError(err);
            }
        }
        onResume();
    }

    @Override
    public void onPause(){
        taskView.pauseScanning();
    }

    @Override
    public void onDestroy() {
        taskModel.saveAttendance();
        taskView.closeProgressWindow();
        handler.removeCallbacks(taskModel);
    }

    @Override
    public void onRestart() {
        if(!navigationFlag){
            taskView.securityPrompt(false);
        }
        navigationFlag  = false;
    }

    @Override
    public void onBackPressed(){
        taskView.pauseScanning();
        ProcessException err    = new ProcessException("Confirm logout and exit?",
                ProcessException.YES_NO_MESSAGE, IconManager.MESSAGE);
        err.setBackPressListener(this);
        err.setListener(ProcessException.yesButton, this);
        err.setListener(ProcessException.noButton, this);
        taskView.displayError(err);
    }

    @Override
    public void onPasswordReceived(int requestCode, int resultCode, Intent data){
        if(requestCode == PopUpLogin.PASSWORD_REQ_CODE && resultCode == Activity.RESULT_OK){
            String password = data.getStringExtra("Password");
            try{
                taskView.pauseScanning();
                taskModel.matchPassword(password);
                taskView.resumeScanning();
            } catch(ProcessException err){
                taskView.displayError(err);
                taskView.securityPrompt(false);
            }
        }
    }

    @Override
    public void onScan(String scanStr){
        try{
            taskView.pauseScanning();
            taskModel.tryAssignScanValue(scanStr);
            taskView.resumeScanning();
        } catch (ProcessException err) {
            taskView.displayError(err);
            if(err.getErrorType() == ProcessException.MESSAGE_TOAST)
                taskView.resumeScanning();
        }
    }

    @Override
    public void onSwipeLeft(){
        navigationFlag  = true;
        taskView.navigateActivity(ReportAttdActivity.class);
    }

    @Override
    public void onSwipeBottom(){
        navigationFlag  = true;
        taskView.navigateActivity(InfoGrabActivity.class);
    }

    @Override
    public void onChiefRespond(ErrorManager errManager, String messageRx) {
        try {
            taskView.closeProgressWindow();//Might Change
            this.initComplete = true;
            taskModel.checkDownloadResult(messageRx);
        } catch (ProcessException err) {
            ExternalDbLoader.getConnectionTask().publishError(errManager, err);
        }
    }

    @Override
    public void loadSetting() {
        crossHair   = preferences.getBoolean("CrossHair", true);
        beep        = preferences.getBoolean("Beep", false);
        vibrate     = preferences.getBoolean("Vibrate", false);
        mode        = Integer.parseInt(preferences.getString("ScannerMode", "4"));

        taskView.changeScannerSetting(crossHair, beep, vibrate, mode);
    }

    @Override
    public void displayTable(Integer tableNum){
        if(tableNum > 0){
            taskView.setTableView(tableNum.toString());
        } else{
            taskView.setTableView("");
        }
    }

    @Override
    public void displayCandidate(Candidate cdd){
        try{
            ExamSubject paper = cdd.getPaper();
            taskView.setCandidateView(cdd.getExamIndex(), cdd.getRegNum(), paper.toString());
        } catch (ProcessException err) {
            err.setListener(ProcessException.okayButton, this);
            taskView.displayError(err);
        }
    }

    @Override
    public void resetDisplay(){
        taskView.pauseScanning();
        taskView.setTableView("");
        taskView.setCandidateView("","","");
        taskView.setAssignBackgroundColor(R.color.colorDarkGreen);
        onResume();
    }

    @Override
    public void signalReassign(int whichReassigned) {
        switch(whichReassigned){
            case TakeAttdMVP.TABLE_REASSIGN:
                taskView.setAssignBackgroundColor(R.color.colorDarkRed);
                break;
            case TakeAttdMVP.CANDIDATE_REASSIGN:
                taskView.setAssignBackgroundColor(R.color.colorDarkRed);
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case DialogInterface.BUTTON_POSITIVE:
                taskView.finishActivity();
                break;
            default:
                taskView.resumeScanning();
                break;
        }
        dialog.cancel();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        taskView.resumeScanning();
        dialog.cancel();
    }

    @Override
    public void startTimer() {
        handler.postDelayed(taskModel, 5000);
    }

    @Override
    public void onTimesOut(ProcessException err) {
        if(taskView != null){
            taskView.closeProgressWindow();
            taskView.pauseScanning();
            taskView.displayError(err);
            if(err.getErrorType() == ProcessException.MESSAGE_TOAST){
                taskView.resumeScanning();
            }
        }
    }
}
