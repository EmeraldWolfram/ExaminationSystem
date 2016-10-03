package com.info.ghiny.examsystem.manager;

import android.util.Log;

import com.info.ghiny.examsystem.LinkChiefActivity;
import com.info.ghiny.examsystem.MainLoginActivity;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.ConnectPresenter;
import com.info.ghiny.examsystem.interfacer.TaskScanView;
import com.info.ghiny.examsystem.interfacer.TaskScanPresenter;
import com.info.ghiny.examsystem.model.LoginHelper;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 08/08/2016.
 */
public class ConnectionManager implements ConnectPresenter, TaskScanPresenter {
    private TaskScanView taskScanView;
    private LoginHelper loginModel;
    private CheckListLoader dbLoader;

    public ConnectionManager(TaskScanView taskScanView, CheckListLoader dbLoader){
        this.taskScanView = taskScanView;
        this.loginModel     = new LoginHelper();
        this.dbLoader       = dbLoader;
    }

    void setLoginModel(LoginHelper loginModel) {
        this.loginModel = loginModel;
    }

    @Override
    public void onScan(String scanStr){
        try{
            taskScanView.pauseScanning();
            taskScanView.beep();
            loginModel.tryConnectWithQR(scanStr, dbLoader);
            taskScanView.navigateActivity(MainLoginActivity.class);
        } catch (ProcessException err) {
            taskScanView.displayError(err);
            taskScanView.resumeScanning();
        }
    }

    @Override
    public void onCreate(){
        if(loginModel.tryConnectWithDatabase(dbLoader)){
            taskScanView.navigateActivity(MainLoginActivity.class);
        }
    }

    @Override
    public void onResume() {
        taskScanView.resumeScanning();
    }

    @Override
    public void onPause() {
        taskScanView.pauseScanning();
    }

    @Override
    public void onDestroy(){
        try {
            loginModel.closeConnection();
        } catch (Exception e) {
            Log.d(LinkChiefActivity.TAG, e.getMessage());
        }
    }
}
