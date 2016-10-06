package com.info.ghiny.examsystem.manager;

import android.util.Log;

import com.info.ghiny.examsystem.LinkChiefActivity;
import com.info.ghiny.examsystem.MainLoginActivity;
import com.info.ghiny.examsystem.interfacer.LinkChiefMVP;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 08/08/2016.
 */
public class LinkChiefPresenter implements LinkChiefMVP.PresenterFace {
    private LinkChiefMVP.ViewFace taskView;
    private LinkChiefMVP.ModelFace taskModel;

    public LinkChiefPresenter(LinkChiefMVP.ViewFace taskView){
        this.taskView       = taskView;
    }

    public void setTaskModel(LinkChiefMVP.ModelFace taskModel) {
        this.taskModel = taskModel;
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
        if(taskModel.tryConnectWithDatabase()){
            taskView.navigateActivity(MainLoginActivity.class);
        }
    }

    @Override
    public void onResume() {
        taskView.resumeScanning();
    }

    @Override
    public void onPause() {
        taskView.pauseScanning();
    }

    @Override
    public void onDestroy(){
        try {
            taskModel.closeConnection();
        } catch (Exception e) {
            Log.d(LinkChiefActivity.TAG, e.getMessage());
        }
    }
}
