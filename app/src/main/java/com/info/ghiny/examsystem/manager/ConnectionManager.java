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
    private ScannerView scannerView;
    private LoginHelper loginModel;
    private CheckListLoader dbLoader;

    public ConnectionManager(ScannerView scannerView, CheckListLoader dbLoader){
        this.scannerView    = scannerView;
        this.loginModel     = new LoginHelper();
        this.dbLoader       = dbLoader;
    }

    public void setLoginModel(LoginHelper loginModel) {
        this.loginModel = loginModel;
    }

    @Override
    public void onScan(String scanStr){
        scannerView.pauseScanning();
        scannerView.beep();
        try{
            loginModel.verifyChief(scanStr);

            ChiefLink connect   = new ChiefLink();
            connect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            ExternalDbLoader.setChiefLink(connect);

            scannerView.navigateActivity(MainLoginActivity.class);
        } catch (ProcessException err) {
            scannerView.displayError(err);
            scannerView.resumeScanning();
        }
    }

    @Override
    public void onPause() {
        scannerView.pauseScanning();
    }

    @Override
    public void onResume() {
        scannerView.resumeScanning();
    }

    @Override
    public void setupConnection(){
        if(loginModel.tryConnection(dbLoader)){
            //Connect here if wanted
            ChiefLink connect   = new ChiefLink();
            connect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            ExternalDbLoader.setChiefLink(connect);

            scannerView.navigateActivity(MainLoginActivity.class);
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
