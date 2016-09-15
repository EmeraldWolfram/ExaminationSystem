package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;

import com.info.ghiny.examsystem.ExamListActivity;
import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.TaskScanView;
import com.info.ghiny.examsystem.interfacer.TaskConnPresenter;
import com.info.ghiny.examsystem.interfacer.TaskScanPresenter;
import com.info.ghiny.examsystem.interfacer.TaskSecurePresenter;
import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.IconManager;
import com.info.ghiny.examsystem.model.InfoCollectHelper;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.LoginHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

/**
 * Created by GhinY on 08/08/2016.
 */
public class ObtainInfoManager implements TaskScanPresenter, TaskConnPresenter, TaskSecurePresenter{
    private InfoCollectHelper infoModel;
    private TaskScanView taskScanView;
    private String studentSubjects;
    private Handler handler;

    public ObtainInfoManager(TaskScanView taskScanView){
        this.taskScanView = taskScanView;
        this.infoModel      = new InfoCollectHelper();
        this.handler        = new Handler();
    }

    public void setInfoModel(InfoCollectHelper infoModel) {
        this.infoModel = infoModel;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public String getStudentSubjects() {
        return studentSubjects;
    }

    @Override
    public void onPause(){
        taskScanView.pauseScanning();
    }

    @Override
    public void onResume() {
        taskScanView.resumeScanning();
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
            ConnectionTask.setCompleteFlag(true);
            boolean ack =   JsonHelper.parseBoolean(messageRx);
            studentSubjects = messageRx;
            taskScanView.navigateActivity(ExamListActivity.class);
        } catch (ProcessException err) {
            err.setListener(ProcessException.okayButton, buttonListener);
            err.setListener(ProcessException.yesButton, buttonListener);
            err.setListener(ProcessException.noButton, buttonListener);

            ExternalDbLoader.getConnectionTask().publishError(errManager, err);
        }
    }

    @Override
    public void onDestroy(){
        handler.removeCallbacks(timer);
    }

    @Override
    public void onScan(String scanStr){
        try{
            taskScanView.pauseScanning();
            infoModel.reqCandidatePapers(scanStr);
            handler.postDelayed(timer, 5000);
        } catch (ProcessException err){
            err.setListener(ProcessException.okayButton, buttonListener);

            taskScanView.displayError(err);
            if(err.getErrorType() == ProcessException.MESSAGE_TOAST)
                taskScanView.resumeScanning();
        }
    }

    @Override
    public void onRestart() {
        taskScanView.securityPrompt(false);
    }

    @Override
    public void onPasswordReceived(int requestCode, int resultCode, Intent data) {
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
                ProcessException err = new ProcessException(
                        "Server busy. Request times out. \n Please try again later.",
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
