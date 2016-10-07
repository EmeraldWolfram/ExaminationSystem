package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;

import com.info.ghiny.examsystem.TakeAttendanceActivity;
import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.LoginMVP;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

/**
 * Created by GhinY on 08/08/2016.
 */
public class LoginPresenter implements LoginMVP.VPresenter, LoginMVP.MPresenter {
    private LoginMVP.View taskView;
    private LoginMVP.Model taskModel;
    private Handler handler;
    private boolean dlFlag = false;

    public LoginPresenter(LoginMVP.View taskView){
        this.taskView       = taskView;
    }

    public void setTaskModel(LoginMVP.Model taskModel) {
        this.taskModel = taskModel;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public boolean isDlFlag() {
        return dlFlag;
    }

    //==============================================================================================

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
        while(ExternalDbLoader.getTcpClient() == null){}

        ExternalDbLoader.getTcpClient().setMessageListener(new TCPClient.OnMessageReceived() {
            //here the messageReceived method is implemented
            @Override
            public void messageReceived(String message) {
                onChiefRespond(errorManager, message);
            }
        });

        onResume();
    }

    @Override
    public void onDestroy(){
        //try {
        taskView.closeProgressWindow();
        handler.removeCallbacks(taskModel);
            //ExternalDbLoader.getTcpClient().sendMessage("Termination");
            //ExternalDbLoader.getTcpClient().stopClient();
            //ExternalDbLoader.getConnectionTask().cancel(true);
            //ExternalDbLoader.setConnectionTask(null);
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
    }

    @Override
    public void onScan(String scanStr){
        taskView.pauseScanning();
        try{
            taskModel.checkQrId(scanStr);
            taskView.securityPrompt(true);
        } catch(ProcessException err){
            taskView.displayError(err);
            if(err.getErrorType() == ProcessException.MESSAGE_TOAST)
                taskView.resumeScanning();
        }
    }

    @Override
    public void onPasswordReceived(int reqCode, int resCode, Intent intent){
        if(reqCode == PopUpLogin.PASSWORD_REQ_CODE && resCode == Activity.RESULT_OK){
            taskView.pauseScanning();
            String password = intent.getStringExtra("Password");
            try{
                taskModel.matchStaffPw(password);
                taskView.openProgressWindow();
                handler.postDelayed(taskModel, 5000);
            } catch(ProcessException err){
                taskView.displayError(err);
                if(err.getErrorType() == ProcessException.MESSAGE_TOAST)
                    taskView.resumeScanning();
            }
        }
    }

    @Override
    public void onChiefRespond(ErrorManager errorManager, String message){
        try {
            //if(!dlFlag){
            taskView.closeProgressWindow();
            taskModel.checkLoginResult(message);
            //    dlFlag = true;
            //} else {
            //    loginModel.checkDetail(message);
            //    dlFlag = false;
            taskView.navigateActivity(TakeAttendanceActivity.class);
            //}
        } catch (ProcessException err) {
            ExternalDbLoader.getConnectionTask().publishError(errorManager, err);
        }
    }

    //==============================================================================================


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
