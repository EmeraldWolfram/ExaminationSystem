package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.InfoGrabActivity;
import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.SubmissionActivity;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.TakeAttdMVP;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;
import com.info.ghiny.examsystem.model.TakeAttdModel;

/**
 * Created by GhinY on 08/08/2016.
 */
public class TakeAttdPresenter implements TakeAttdMVP.VPresenter, TakeAttdMVP.MPresenter{
    private TakeAttdMVP.View taskView;
    private TakeAttdMVP.Model taskModel;
    private boolean navigationFlag;
    private Handler handler;
    private Snackbar snackbar;
    private View refView;
    private boolean secureFlag;

    private SharedPreferences preferences;
    private boolean crossHair;
    private boolean beep;
    private boolean vibrate;
    private int mode;

    public TakeAttdPresenter(TakeAttdMVP.View taskView, SharedPreferences pref){
        this.preferences    = pref;
        this.taskView       = taskView;
        this.navigationFlag = false;
        this.secureFlag     = false;
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

    //==============================================================================================
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
        if(TakeAttdModel.getAttdList() == null){
            try{
                taskModel.initAttendance();
                if(!taskModel.isInitialized()){
                    taskView.openProgressWindow("Preparing Attendance List:", "Retrieving data...");
                    handler.postDelayed(taskModel, 5000);
                }
            } catch (ProcessException err) {
                taskView.displayError(err);
            }
        }
        onResume();
    }

    @Override
    public void onPause(){
        if(snackbar != null){
            snackbar.dismiss();
        }
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
        if(!navigationFlag && !secureFlag){
            secureFlag = true;
            taskView.securityPrompt(false);
        }
        navigationFlag  = false;
    }

    @Override
    public void onPasswordReceived(int requestCode, int resultCode, Intent data){
        if(requestCode == PopUpLogin.PASSWORD_REQ_CODE && resultCode == Activity.RESULT_OK){
            secureFlag  = false;
            String password = data.getStringExtra("Password");
            try{
                taskView.pauseScanning();
                taskModel.matchPassword(password);
                taskView.resumeScanning();
            } catch(ProcessException err){
                taskView.displayError(err);
                taskView.securityPrompt(false);
                this.secureFlag = true;
            }
        }
    }

    @Override
    public void onScan(String scanStr){
        try{
            taskView.pauseScanning();
            if(snackbar != null){
                snackbar.dismiss();
            }
            taskModel.tryAssignScanValue(scanStr);
            taskView.resumeScanning();
        } catch (ProcessException err) {
            taskView.displayError(err);
            if(err.getErrorType() == ProcessException.MESSAGE_TOAST)
                taskView.resumeScanning();
        }
    }

    @Override
    public void onSwiped(View refView) {
        this.refView    = refView;
        taskModel.resetAttendanceAssignment();
    }

    @Override
    public void notifyUndone(String message){
        snackbar    = Snackbar.make(refView, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("UNDO", this);
        snackbar.show();
    }

    @Override
    public void onSwipeLeft(){
        navigationFlag  = true;
        taskView.navigateActivity(SubmissionActivity.class);
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
    public void notifyTableScanned(Integer tableNum){
        if(tableNum > 0){
            taskView.setTableView(tableNum.toString());
        } else{
            taskView.setTableView("");
        }
    }

    @Override
    public void notifyCandidateScanned(Candidate cdd){
        try{
            ExamSubject paper = cdd.getPaper();
            taskView.setCandidateView(cdd.getExamIndex(), cdd.getRegNum(), paper.toString());
        } catch (ProcessException err) {
            err.setListener(ProcessException.okayButton, this);
            taskView.displayError(err);
        }
    }

    @Override
    public void notifyDisplayReset(){
        taskView.pauseScanning();
        taskView.setTableView("");
        taskView.setCandidateView("","","");
        taskView.setAssignBackgroundColor(R.color.colorDarkGreen);
        taskView.setTagButton(false);
        onResume();
    }

    @Override
    public void notifyReassign(int whichReassigned) {
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
    public void notifyTagUntag(boolean showAntiTag) {
        taskView.setTagButton(showAntiTag);
    }

    @Override
    public void onClick(View v) {
        taskModel.undoResetAttendanceAssignment();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        taskView.resumeScanning();
        dialog.cancel();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        taskView.resumeScanning();
        dialog.cancel();
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

    @Override
    public void onTag(View view) {
        taskModel.tagAsLateNot();
    }
}
