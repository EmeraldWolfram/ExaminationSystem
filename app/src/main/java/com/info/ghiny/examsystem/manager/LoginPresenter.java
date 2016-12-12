package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.info.ghiny.examsystem.HomeOptionActivity;
import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.Role;
import com.info.ghiny.examsystem.interfacer.LoginMVP;
import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

/**
 * Created by GhinY on 08/08/2016.
 */
public class LoginPresenter implements LoginMVP.MvpVPresenter, LoginMVP.MvpMPresenter {
    private LoginMVP.MvpView taskView;
    private LoginMVP.MvpModel taskModel;
    private Handler handler;
    private boolean dlFlag = false;

    private SharedPreferences preferences;
    private boolean crossHair;
    private boolean beep;
    private boolean vibrate;
    private int mode;

    public LoginPresenter(LoginMVP.MvpView taskView, SharedPreferences pref){
        this.preferences    = pref;
        this.taskView       = taskView;
    }

    public void setTaskModel(LoginMVP.MvpModel taskModel) {
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
        loadSetting();
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
        taskView.closeProgressWindow();
        handler.removeCallbacks(taskModel);
    }

    @Override
    public void onScan(String scanStr){
        taskView.pauseScanning();
        taskView.beep();
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
                taskView.openProgressWindow("Verifying:", "Waiting for Chief Respond...");
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
            taskView.closeProgressWindow();
            ConnectionTask.setCompleteFlag(true);
            Role role = taskModel.checkLoginResult(message);

            taskView.navToHome(true, true, true, (role == Role.IN_CHARGE));
        } catch (ProcessException err) {
            ExternalDbLoader.getConnectionTask().publishError(errorManager, err);
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
