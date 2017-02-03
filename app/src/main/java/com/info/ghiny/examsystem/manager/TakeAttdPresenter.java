package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.InfoGrabActivity;
import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.SettingActivity;
import com.info.ghiny.examsystem.SubmissionActivity;
import com.info.ghiny.examsystem.TakeAttdActivity;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.TakeAttdMVP;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.JavaHost;
import com.info.ghiny.examsystem.model.TakeAttdModel;

/**
 * Copyright (C) 2016 - 2017 Steven Foong Ghin Yew <stevenfgy@yahoo.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
public class TakeAttdPresenter implements TakeAttdMVP.VPresenter, TakeAttdMVP.MPresenter{
    private TakeAttdMVP.View taskView;
    private TakeAttdMVP.Model taskModel;
    private boolean navigationFlag;
    //private Handler handler;
    private Handler synTimer;
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

    //public void setHandler(Handler handler) {
    //    this.handler = handler;
    //}

    public void setSynTimer(Handler synTimer) {
        this.synTimer = synTimer;
        this.synTimer.postDelayed(taskSyn, 6000);
    }

    void setNavigationFlag(boolean navigationFlag) {
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
        ExternalDbLoader.getJavaHost().setTaskView(taskView);
        ExternalDbLoader.getJavaHost().setMessageListener(new JavaHost.OnMessageReceived() {
            //here the messageReceived method is implemented
            @Override
            public void messageReceived(String message) {
                onChiefRespond(errManager, message);
            }
        });
        //=========================================================================
        try{
            taskModel.updateAssignList();
        } catch (ProcessException err) {
            taskView.displayError(err);
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
        try{
            taskModel.txAttendanceUpdate();
            synTimer.removeCallbacks(taskSyn);
        } catch (ProcessException err){
            Log.d(TakeAttdActivity.TAG, err.getErrorMsg());
        }
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
    public boolean onSetting() {
        navigationFlag = true;
        taskView.navigateActivity(SettingActivity.class);
        return true;
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
    public void notifyNotMatch(){
        taskView.setTableView("");
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
            case TakeAttdMVP.NO_REASSIGN:
                taskView.setAssignBackgroundColor(R.color.colorDarkGreen);
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

    private Runnable taskSyn    = new Runnable() {
        @Override
        public void run() {
            try{
                taskModel.txAttendanceUpdate();
                synTimer.postDelayed(this, 6000);
            } catch (ProcessException err){
                taskView.displayError(err);
            }
        }
    };
}
