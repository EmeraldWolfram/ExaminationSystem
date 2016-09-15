package com.info.ghiny.examsystem.manager;

import android.os.AsyncTask;

import com.info.ghiny.examsystem.MainLoginActivity;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.ConnectPresenter;
import com.info.ghiny.examsystem.interfacer.TaskScanView;
import com.info.ghiny.examsystem.interfacer.TaskScanPresenter;
import com.info.ghiny.examsystem.model.ConnectionTask;
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

    public void setLoginModel(LoginHelper loginModel) {
        this.loginModel = loginModel;
    }

    @Override
    public void onScan(String scanStr){
        taskScanView.pauseScanning();
        taskScanView.beep();
        try{
            loginModel.verifyChief(scanStr);

            ConnectionTask connect   = new ConnectionTask();
            connect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            ExternalDbLoader.setConnectionTask(connect);

            taskScanView.navigateActivity(MainLoginActivity.class);
        } catch (ProcessException err) {
            taskScanView.displayError(err);
            taskScanView.resumeScanning();
        }
    }

    @Override
    public void onPause() {
        taskScanView.pauseScanning();
    }

    @Override
    public void onResume() {
        taskScanView.resumeScanning();
    }

    @Override
    public void setupConnection(){
        if(loginModel.tryConnection(dbLoader)){
            //Connect here if wanted
            ConnectionTask connect   = new ConnectionTask();
            connect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            ExternalDbLoader.setConnectionTask(connect);

            taskScanView.navigateActivity(MainLoginActivity.class);
        }
        //Setup ConnectionTask and TCP Client
    }

    @Override
    public void closeConnection(){
        try {
            ExternalDbLoader.getTcpClient().sendMessage("Termination");
            ExternalDbLoader.getTcpClient().stopClient();
            ExternalDbLoader.getConnectionTask().cancel(true);
            ExternalDbLoader.setConnectionTask(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Close ConnectionTask and TCP Client
    }
}
