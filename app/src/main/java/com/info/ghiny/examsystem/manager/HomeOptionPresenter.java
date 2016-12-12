package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import com.info.ghiny.examsystem.CollectionActivity;
import com.info.ghiny.examsystem.DistributionActivity;
import com.info.ghiny.examsystem.InfoGrabActivity;
import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.SettingActivity;
import com.info.ghiny.examsystem.TakeAttdActivity;
import com.info.ghiny.examsystem.interfacer.HomeOptionMVP;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by FOONG on 9/12/2016.
 */

public class HomeOptionPresenter implements HomeOptionMVP.MvpVPresenter, HomeOptionMVP.MvpMPresenter {

    private boolean secureFlag;
    private boolean navFlag;
    private HomeOptionMVP.MvpView taskView;
    private HomeOptionMVP.MvpModel taskModel;

    public HomeOptionPresenter(HomeOptionMVP.MvpView taskView){
        this.taskView   = taskView;
        this.secureFlag = false;
        this.navFlag    = false;
    }

    public void setTaskModel(HomeOptionMVP.MvpModel taskModel) {
        this.taskModel = taskModel;
    }

    @Override
    public void onRestart() {
        if(!secureFlag && !navFlag){
            secureFlag = true;
            taskView.securityPrompt(false);
        }
        navFlag = false;
    }

    @Override
    public void onPasswordReceived(int requestCode, int resultCode, Intent data) {
        if(requestCode == PopUpLogin.PASSWORD_REQ_CODE && resultCode == Activity.RESULT_OK){
            secureFlag      = false;
            String password = data.getStringExtra("Password");
            try{
                taskModel.matchPassword(password);
            } catch(ProcessException err){
                taskView.displayError(err);
                secureFlag = true;
                taskView.securityPrompt(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        taskView.displayError(taskModel.prepareLogout());
    }

    @Override
    public boolean onSetting() {
        navFlag = true;
        taskView.navigateActivity(SettingActivity.class);
        return true;
    }

    @Override
    public void onAttendance() {
        navFlag = true;
        taskView.navigateActivity(TakeAttdActivity.class);
    }

    @Override
    public void onCollection() {
        navFlag = true;
        taskView.navigateActivity(CollectionActivity.class);
    }

    @Override
    public void onDistribution() {
        navFlag = true;
        taskView.navigateActivity(DistributionActivity.class);
    }

    @Override
    public void onInfo() {
        navFlag = true;
        taskView.navigateActivity(InfoGrabActivity.class);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case DialogInterface.BUTTON_POSITIVE:
                taskView.finishActivity();
                break;
            default:
                dialog.cancel();
                break;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        dialog.cancel();
    }
}