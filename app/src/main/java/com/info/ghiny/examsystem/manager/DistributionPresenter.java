package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.ThreadManager;
import com.info.ghiny.examsystem.interfacer.DistributionMVP;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by FOONG on 6/12/2016.
 */

public class DistributionPresenter
        implements DistributionMVP.MvpVPresenter, DistributionMVP.MvpMPresenter{

    private DistributionMVP.MvpView taskView;
    private DistributionMVP.MvpModel taskModel;
    private boolean secureFlag;

    public DistributionPresenter(DistributionMVP.MvpView taskView){
        this.taskView   = taskView;
        this.secureFlag = false;
    }

    public void setTaskModel(DistributionMVP.MvpModel taskModel) {
        this.taskModel = taskModel;
    }

    @Override
    public void onCreate(Context context) {
        if(!ThreadManager.isRunning()){
            context.startService(new Intent(context, ThreadManager.class));
        }
        ThreadManager.startNewThread(taskView, taskModel);
        ExternalDbLoader.getJavaHost().setTaskView(taskView);
    }

    @Override
    public void onRestart() {
        if(!secureFlag){
            secureFlag = true;
            taskView.securityPrompt(false);
        }
    }

    @Override
    public void onDestroy() {
        ThreadManager.removeUnconnectedThread();
    }

    @Override
    public void onPasswordReceived(int requestCode, int resultCode, Intent data) {
        if(requestCode == PopUpLogin.PASSWORD_REQ_CODE && resultCode == Activity.RESULT_OK){
            this.secureFlag = false;
            String password = data.getStringExtra("Password");
            try{
                taskModel.matchPassword(password);
            } catch(ProcessException err){
                taskView.displayError(err);
                taskView.securityPrompt(false);
                this.secureFlag = true;
            }
        }
    }
}
