package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;

import com.info.ghiny.examsystem.TakeAttendanceActivity;
import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.LoginPresenter;
import com.info.ghiny.examsystem.interfacer.TaskConnView;
import com.info.ghiny.examsystem.interfacer.TaskScanView;
import com.info.ghiny.examsystem.interfacer.TaskConnPresenter;
import com.info.ghiny.examsystem.interfacer.TaskScanPresenter;
import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.IconManager;
import com.info.ghiny.examsystem.model.LoginHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

/**
 * Created by GhinY on 08/08/2016.
 */
public class LoginManager implements LoginPresenter, TaskScanPresenter, TaskConnPresenter {
    private TaskScanView taskScanView;
    private TaskConnView taskConnView;
    private LoginHelper loginModel;
    private Handler handler;
    private boolean dlFlag = false;

    public LoginManager(TaskScanView taskScanView, TaskConnView taskConnView){
        this.taskScanView   = taskScanView;
        this.taskConnView   = taskConnView;
        this.loginModel     = new LoginHelper();
        this.handler        = new Handler();
    }

    public void setLoginModel(LoginHelper loginModel) {
        this.loginModel = loginModel;
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
        taskScanView.pauseScanning();
    }

    public void onResume() {
        taskScanView.resumeScanning();
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
        taskConnView.closeProgressWindow();
        handler.removeCallbacks(timer);
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
        taskScanView.pauseScanning();
        try{
            loginModel.checkQrId(scanStr);
            taskScanView.securityPrompt(true);
        } catch(ProcessException err){
            err.setListener(ProcessException.okayButton, buttonListener);
            err.setListener(ProcessException.yesButton, buttonListener);
            err.setListener(ProcessException.noButton, buttonListener);

            taskScanView.displayError(err);
            if(err.getErrorType() == ProcessException.MESSAGE_TOAST)
                taskScanView.resumeScanning();
        }
    }

    @Override
    public void onPasswordReceived(int reqCode, int resCode, Intent intent){
        if(reqCode == PopUpLogin.PASSWORD_REQ_CODE && resCode == Activity.RESULT_OK){
            taskScanView.pauseScanning();
            String password = intent.getStringExtra("Password");
            try{
                loginModel.matchStaffPw(password);
                taskConnView.openProgressWindow();
                handler.postDelayed(timer, 5000);
            } catch(ProcessException err){
                err.setListener(ProcessException.okayButton, buttonListener);
                err.setListener(ProcessException.yesButton, buttonListener);
                err.setListener(ProcessException.noButton, buttonListener);

                taskScanView.displayError(err);
                if(err.getErrorType() == ProcessException.MESSAGE_TOAST)
                    taskScanView.resumeScanning();
            }
        }
    }

    @Override
    public void onChiefRespond(ErrorManager errorManager, String message){
        try {
            //if(!dlFlag){
            taskConnView.closeProgressWindow();
            loginModel.checkLoginResult(message);
            //    dlFlag = true;
            //} else {
            //    loginModel.checkDetail(message);
            //    dlFlag = false;
            taskScanView.navigateActivity(TakeAttendanceActivity.class);
            //}
        } catch (ProcessException err) {
            err.setListener(ProcessException.okayButton, buttonListener);
            err.setListener(ProcessException.yesButton, buttonListener);
            err.setListener(ProcessException.noButton, buttonListener);

            ExternalDbLoader.getConnectionTask().publishError(errorManager, err);
        }
    }

    //==============================================================================================
    private DialogInterface.OnClickListener timesOutListener =
            new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    taskScanView.resumeScanning();
                    dialog.cancel();
                }
            };

    private Runnable timer = new Runnable() {
        @Override
        public void run() {
            if(!ConnectionTask.isComplete()){
                taskConnView.closeProgressWindow();

                ProcessException err = new ProcessException(
                        "Identity verification times out.",
                        ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
                err.setListener(ProcessException.okayButton, timesOutListener);
                err.setBackPressListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        taskScanView.resumeScanning();
                        dialog.cancel();
                    }
                });
                if(taskScanView != null){
                    taskScanView.pauseScanning();
                    taskScanView.displayError(err);
                }
            }
        }
    };

    private DialogInterface.OnClickListener buttonListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            taskScanView.resumeScanning();
            dialog.cancel();
        }
    };
}
