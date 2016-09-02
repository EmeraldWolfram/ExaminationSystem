package com.info.ghiny.examsystem.manager;

import android.os.AsyncTask;

import com.info.ghiny.examsystem.MainLoginActivity;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.ConnectPresenter;
import com.info.ghiny.examsystem.interfacer.GeneralView;
import com.info.ghiny.examsystem.interfacer.ScannerView;
import com.info.ghiny.examsystem.interfacer.TaskScanPresenter;
import com.info.ghiny.examsystem.model.ChiefLink;
import com.info.ghiny.examsystem.model.LoginHelper;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 08/08/2016.
 */
public class ConnectionManager implements ConnectPresenter, TaskScanPresenter {
    private ScannerView generalView;
    private LoginHelper loginModel;
    private CheckListLoader dbLoader;

    public ConnectionManager(ScannerView generalView, CheckListLoader dbLoader){
        this.generalView    = generalView;
        this.loginModel     = new LoginHelper();
        this.dbLoader       = dbLoader;
    }

    public void setLoginModel(LoginHelper loginModel) {
        this.loginModel = loginModel;
    }

    @Override
    public void onScan(String scanStr){
        try{
            generalView.pauseScanning();
            loginModel.verifyChief(scanStr);

            ChiefLink connect   = new ChiefLink();
            connect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            ExternalDbLoader.setChiefLink(connect);

            generalView.navigateActivity(MainLoginActivity.class);
        } catch (ProcessException err) {
            generalView.displayError(err);
            generalView.resumeScanning();
        }
    }

    @Override
    public void onPause() {
        generalView.pauseScanning();
    }

    @Override
    public void onResume() {
        generalView.resumeScanning();
    }

    @Override
    public void setupConnection(){
        if(loginModel.tryConnection(dbLoader)){
            //Connect here if wanted
            ChiefLink connect   = new ChiefLink();
            connect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            ExternalDbLoader.setChiefLink(connect);

            generalView.navigateActivity(MainLoginActivity.class);
        }
        //Setup ChiefLink and TCP Client
    }

    @Override
    public void closeConnection(){
        try {
            ExternalDbLoader.getTcpClient().sendMessage("Termination");
            ExternalDbLoader.getTcpClient().stopClient();
            ExternalDbLoader.getChiefLink().cancel(true);
            ExternalDbLoader.setChiefLink(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Close ChiefLink and TCP Client
    }
}
