package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.info.ghiny.examsystem.InfoDisplayActivity;
import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.SettingActivity;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.InfoGrabMVP;
import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.JavaHost;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TakeAttdModel;

import java.util.ArrayList;

/**
 * Created by GhinY on 08/08/2016.
 */


public class InfoGrabPresenter implements InfoGrabMVP.VPresenter, InfoGrabMVP.MPresenter{
    private String studentSubjects;
    private Handler handler;
    private InfoGrabMVP.ViewFace taskView;
    private InfoGrabMVP.Model taskModel;
    private boolean secureFlag;
    private boolean navFlag;

    private SharedPreferences preferences;
    private boolean crossHair;
    private boolean beep;
    private boolean vibrate;
    private int mode;

    public InfoGrabPresenter(InfoGrabMVP.ViewFace taskView, SharedPreferences pref){
        this.taskView       = taskView;
        this.preferences    = pref;
        this.secureFlag     = false;
        this.navFlag        = false;
    }

    public void setTaskModel(InfoGrabMVP.Model taskModel) {
        this.taskModel = taskModel;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public String getStudentSubjects() {
        return studentSubjects;
    }

    @Override
    public void onPause(){
        taskView.pauseScanning();
    }

    @Override
    public void onResume() {
        taskView.resumeScanning();
        loadSetting();
    }

    @Override
    public void onResume(final ErrorManager errManager){
        ExternalDbLoader.getJavaHost().setTaskView(taskView);
        ExternalDbLoader.getJavaHost().setMessageListener(new JavaHost.OnMessageReceived() {
            @Override
            public void messageReceived(String message) {
                onChiefRespond(errManager, message);
            }
        });
        onResume();
    }

    @Override
    public void onChiefRespond(ErrorManager errManager, String messageRx) {
        try{
            String type = JsonHelper.parseType(messageRx);
            if(type.equals(JsonHelper.TYPE_CANDIDATE_INFO)){
                taskView.closeProgressWindow();
                ConnectionTask.setCompleteFlag(true);
                boolean ack =   JsonHelper.parseBoolean(messageRx);
                if(ack){
                    studentSubjects = messageRx;
                    taskView.runItSeparate(new Runnable() {
                        @Override
                        public void run() {
                            taskView.navigateActivity(InfoDisplayActivity.class);
                        }
                    });
                } else {
                    throw new ProcessException("Server request denied!",
                            ProcessException.MESSAGE_DIALOG, IconManager.WARNING);
                }
            } else if(type.equals(JsonHelper.TYPE_ATTENDANCE_UP)){
                ArrayList<Candidate> candidates = JsonHelper.parseUpdateList(messageRx);
                TakeAttdModel.rxAttendanceUpdate(candidates);
                ExternalDbLoader.acknowledgeUpdateReceive();
            }
        } catch (ProcessException err) {
            ExternalDbLoader.getConnectionTask().publishError(errManager, err);
        }
    }

    @Override
    public void onDestroy(){
        taskView.closeProgressWindow();
        handler.removeCallbacks(taskModel);
    }

    @Override
    public void onScan(String scanStr){
        try{
            taskView.pauseScanning();
            taskModel.reqCandidatePapers(scanStr);
            taskView.openProgressWindow("Server Database Request", "Waiting for Respond...");
            handler.postDelayed(taskModel, 5000);
        } catch (ProcessException err){
            err.setListener(ProcessException.okayButton, this);

            taskView.displayError(err);
            if(err.getErrorType() == ProcessException.MESSAGE_TOAST)
                taskView.resumeScanning();
        }
    }

    @Override
    public void onRestart() {
        if(!secureFlag && !navFlag){
            secureFlag = true;
            taskView.securityPrompt(false);
        }
        navFlag = false;
    }

    @Override
    public void onPasswordReceived(int requestCode, int resultCode, Intent data) {
        if(requestCode == PopUpLogin.PASSWORD_REQ_CODE && resultCode == Activity.RESULT_OK){
            secureFlag  = false;
            String password = data.getStringExtra("Password");
            try{
                taskView.pauseScanning();
                taskModel.matchPassword(password);
                taskView.resumeScanning();
            } catch(ProcessException err){
                taskView.displayError(err);
                secureFlag  = true;
                taskView.securityPrompt(false);
            }
        }
    }

    @Override
    public void onSwipeTop() {
        taskView.finishActivity();
    }

    @Override
    public boolean onSetting() {
        navFlag = true;
        taskView.navigateActivity(SettingActivity.class);
        return true;
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
}
