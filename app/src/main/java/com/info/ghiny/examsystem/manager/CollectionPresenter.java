package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;

import com.info.ghiny.examsystem.InfoGrabActivity;
import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.CollectionMVP;
import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

/**
 * Created by GhinY on 08/08/2016.
 */
public class CollectionPresenter implements CollectionMVP.PresenterForView, CollectionMVP.PresenterForModel {
    private CollectionMVP.View taskView;
    private CollectionMVP.Model taskModel;
    private Handler handler;

    public CollectionPresenter(CollectionMVP.View taskView){
        this.taskView       = taskView;
    }

    public void setTaskModel(CollectionMVP.Model taskModel) {
        this.taskModel = taskModel;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    //= For View ===================================================================================
    @Override
    public void onPause(){
        taskView.pauseScanning();
    }

    @Override
    public void onResume() {
        taskView.resumeScanning();
    }

    @Override
    public void onResume(final ErrorManager errorManager){
        ExternalDbLoader.getTcpClient().setMessageListener(new TCPClient.OnMessageReceived() {
            //here the messageReceived method is implemented
            @Override
            public void messageReceived(String message) {
                onChiefRespond(errorManager, message);
            }
        });
        this.onResume();
    }

    @Override
    public void onDestroy(){
        taskView.closeProgressWindow();
        handler.removeCallbacks(taskModel);
    }

    @Override
    public void onRestart() {
        taskView.securityPrompt(false);
    }

    @Override
    public void onChiefRespond(ErrorManager errManager, String messageRx) {
        try{
            taskView.closeProgressWindow();
            ConnectionTask.setCompleteFlag(true);
            boolean ack = JsonHelper.parseBoolean(messageRx);
        } catch (ProcessException err) {
            err.setListener(ProcessException.okayButton, this);
            ExternalDbLoader.getConnectionTask().publishError(errManager, err);
        }
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
    public void onScan(String scanStr){
        try{
            taskView.pauseScanning();
            taskModel.bundleCollection(scanStr);
            taskView.openProgressWindow("Notify Collection:", "Waiting for Acknowledgement...");
            handler.postDelayed(taskModel, 5000);
        } catch (ProcessException err) {
            err.setListener(ProcessException.okayButton, this);

            taskView.displayError(err);
            if(err.getErrorType() == ProcessException.MESSAGE_TOAST)
                taskView.resumeScanning();
        }
    }

    @Override
    public void onSwipeBottom(){
        taskView.navigateActivity(InfoGrabActivity.class);
    }

    //= For Model ==================================================================================
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
