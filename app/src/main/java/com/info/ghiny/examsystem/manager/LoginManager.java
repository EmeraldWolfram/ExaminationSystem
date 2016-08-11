package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.info.ghiny.examsystem.AssignInfoActivity;
import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.ScannerView;
import com.info.ghiny.examsystem.model.AssignHelper;
import com.info.ghiny.examsystem.model.AssignModel;
import com.info.ghiny.examsystem.model.ChiefLink;
import com.info.ghiny.examsystem.model.IconManager;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.LoginHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

import java.util.HashMap;

/**
 * Created by GhinY on 08/08/2016.
 */
public class LoginManager {
    private ScannerView scannerView;
    private LoginHelper loginModel;

    public LoginManager(ScannerView scannerView){
        this.scannerView    = scannerView;
        this.loginModel     = new LoginHelper();

        ChiefLink connect   = new ChiefLink();
        connect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        ExternalDbLoader.setChiefLink(connect);
    }

    //==============================================================================================
    public void onScanForIdentity(String scanStr){
        scannerView.pauseScanning();
        try{
            loginModel.checkQrId(scanStr);
            scannerView.securityPrompt();
        } catch(ProcessException err){
            scannerView.displayError(err);
            scannerView.resumeScanning();
        }
    }

    public void onReceivePassword(int reqCode, int resCode, Intent intent){
        if(reqCode == PopUpLogin.PASSWORD_REQ_CODE && resCode == Activity.RESULT_OK){
            scannerView.pauseScanning();
            String password = intent.getStringExtra("Password");
            try{
                loginModel.matchStaffPw(password);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!ChiefLink.isComplete()){
                            ProcessException err = new ProcessException(
                                    "Identity verification times out.",
                                    ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
                            err.setListener(ProcessException.okayButton, timesOutListener);
                            if(scannerView != null){
                                scannerView.pauseScanning();
                                scannerView.displayError(err);
                                scannerView.resumeScanning();
                            }
                        }
                    }
                }, 5000);
            } catch(ProcessException err){
                scannerView.displayError(err);
                scannerView.resumeScanning();
            }
        }
    }

    public void onPause(){
        scannerView.pauseScanning();
    }

    public void onResume(final ErrorManager errorManager){
        while(ExternalDbLoader.getTcpClient() == null){}

        ExternalDbLoader.getTcpClient().setMessageListener(new TCPClient.OnMessageReceived() {
            //here the messageReceived method is implemented
            @Override
            public void messageReceived(String message) {
                try{
                    ChiefLink.setCompleteFlag(true);
                    loginModel.checkLoginResult(message);
                    scannerView.navigateActivity(AssignInfoActivity.class);
                } catch (ProcessException err) {
                    ExternalDbLoader.getChiefLink().publishError(errorManager, err);
                    scannerView.resumeScanning();
                }
            }
        });

        scannerView.resumeScanning();
    }

    public void onDestroy(){
        try {
            ExternalDbLoader.getTcpClient().stopClient();
            ExternalDbLoader.getChiefLink().cancel(true);
            ExternalDbLoader.setChiefLink(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //==============================================================================================
    private DialogInterface.OnClickListener timesOutListener =
            new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    scannerView.resumeScanning();
                    dialog.cancel();
                }
            };
}
