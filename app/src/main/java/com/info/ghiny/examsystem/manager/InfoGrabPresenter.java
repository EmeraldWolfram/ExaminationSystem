package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;

import com.info.ghiny.examsystem.InfoDisplayActivity;
import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.InfoGrabMVP;
import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

/**
 * Created by GhinY on 08/08/2016.
 */


public class InfoGrabPresenter implements InfoGrabMVP.VPresenter, InfoGrabMVP.MPresenter{
    private String studentSubjects;
    private Handler handler;
    private InfoGrabMVP.ViewFace taskView;
    private InfoGrabMVP.Model taskModel;

    public InfoGrabPresenter(InfoGrabMVP.ViewFace taskView){
        this.taskView   = taskView;
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
    }

    @Override
    public void onResume(final ErrorManager errManager){
        ExternalDbLoader.getTcpClient().setMessageListener(new TCPClient.OnMessageReceived() {
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
            taskView.closeProgressWindow();
            ConnectionTask.setCompleteFlag(true);
            boolean ack =   JsonHelper.parseBoolean(messageRx);
            studentSubjects = messageRx;
            taskView.navigateActivity(InfoDisplayActivity.class);
        } catch (ProcessException err) {
            err.setListener(ProcessException.okayButton, this);
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
        taskView.securityPrompt(false);
    }

    @Override
    public void onPasswordReceived(int requestCode, int resultCode, Intent data) {
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
    public void onSwipeTop() {
        taskView.finishActivity();
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
}
