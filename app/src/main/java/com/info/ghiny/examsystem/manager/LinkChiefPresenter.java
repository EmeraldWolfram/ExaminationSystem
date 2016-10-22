package com.info.ghiny.examsystem.manager;

import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;

import com.info.ghiny.examsystem.LinkChiefActivity;
import com.info.ghiny.examsystem.MainLoginActivity;
import com.info.ghiny.examsystem.TakeAttdActivity;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.LinkChiefMVP;
import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

/**
 * Created by GhinY on 08/08/2016.
 */
public class LinkChiefPresenter implements LinkChiefMVP.PresenterFace, LinkChiefMVP.MPresenter {
    private LinkChiefMVP.ViewFace taskView;
    private LinkChiefMVP.ModelFace taskModel;
    private Handler handler;
    private boolean reconnect = false;

    public LinkChiefPresenter(LinkChiefMVP.ViewFace taskView){
        this.taskView       = taskView;
    }

    public void setTaskModel(LinkChiefMVP.ModelFace taskModel) {
        this.taskModel = taskModel;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setReconnect(boolean reconnect) {
        this.reconnect = reconnect;
    }

    public boolean isReconnect() {
        return reconnect;
    }

    @Override
    public void onScan(String scanStr){
        try{
            taskView.pauseScanning();
            taskView.beep();
            taskModel.tryConnectWithQR(scanStr);
            taskView.navigateActivity(MainLoginActivity.class);
        } catch (ProcessException err) {
            taskView.displayError(err);
            taskView.resumeScanning();
        }
    }

    @Override
    public void onCreate(){
        reconnect = taskModel.tryConnectWithDatabase();
    }

    @Override
    public void onResume() {
        taskView.resumeScanning();
    }

    @Override
    public void onResume(final ErrorManager errManager) {
        if(reconnect){
            while(ExternalDbLoader.getTcpClient() == null){}

            try{
                taskModel.reconnect();
                taskView.openProgressWindow("RECONNECTION", "Authenticating...");
                handler.postDelayed(taskModel, 5000);
            } catch (ProcessException err) {
                taskView.displayError(err);
            }

            ExternalDbLoader.getTcpClient().setMessageListener(new TCPClient.OnMessageReceived() {
                //here the messageReceived method is implemented
                @Override
                public void messageReceived(String message) {
                    onChiefRespond(errManager, message);
                }
            });
        }
        this.onResume();
    }

    @Override
    public void onPause() {
        taskView.pauseScanning();
    }

    @Override
    public void onDestroy(){
        try {
            taskModel.closeConnection();
            taskView.closeProgressWindow();
            handler.removeCallbacks(taskModel);
        } catch (Exception e) {
            Log.d(LinkChiefActivity.TAG, e.getMessage());
        }
    }

    @Override
    public void onChiefRespond(ErrorManager errManager, String messageRx) {
        try {
            taskView.closeProgressWindow();
            ConnectionTask.setCompleteFlag(true);

            taskModel.onChallengeMessageReceived(messageRx);

            reconnect   = false;
            taskView.navigateActivity(MainLoginActivity.class);
        } catch (ProcessException err) {
            ExternalDbLoader.getConnectionTask().publishError(errManager, err);
        }
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
    public void onClick(DialogInterface dialog, int which) {
        taskView.resumeScanning();
        dialog.cancel();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        taskView.resumeScanning();
        dialog.cancel();
    }
}
