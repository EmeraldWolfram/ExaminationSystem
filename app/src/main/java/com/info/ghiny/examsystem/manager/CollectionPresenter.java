package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.SettingActivity;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.PaperBundle;
import com.info.ghiny.examsystem.interfacer.CollectionMVP;
import com.info.ghiny.examsystem.model.JavaHost;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Copyright (C) 2016 - 2017 Steven Foong Ghin Yew <stevenfgy@yahoo.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

public class CollectionPresenter implements CollectionMVP.MvpVPresenter, CollectionMVP.MvpMPresenter {
    private CollectionMVP.View taskView;
    private CollectionMVP.Model taskModel;
    private Handler handler;
    private boolean secureFlag;
    private boolean navFlag;
    //private boolean acknowledgementComplete;

    private SharedPreferences preferences;
    private boolean crossHair;
    private boolean beep;
    private boolean vibrate;
    private int mode;
    private int waitTime;

    public CollectionPresenter(CollectionMVP.View taskView, SharedPreferences pref){
        this.taskView       = taskView;
        this.preferences    = pref;
        this.secureFlag     = false;
        this.navFlag        = false;
    }

    public void setTaskModel(CollectionMVP.Model taskModel) {
        this.taskModel = taskModel;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    /*@Override
    public boolean isAcknowledgementComplete() {
        return acknowledgementComplete;
    }

    @Override
    public void setAcknowledgementComplete(boolean acknowledgementComplete) {
        this.acknowledgementComplete = acknowledgementComplete;
    }*/

    //= For MvpView ===================================================================================
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
        ExternalDbLoader.getJavaHost().setTaskView(taskView);
        ExternalDbLoader.getJavaHost().setMessageListener(new JavaHost.OnMessageReceived() {
            //here the messageReceived method is implemented
            @Override
            public void messageReceived(String message) {
                onChiefRespond(errorManager, message);
            }
        });
        this.onResume();
    }

    @Override
    public void onDestroy(){
        taskView.closeProgressWindow();
        handler.removeCallbacks(taskModel);
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
    public void onChiefRespond(ErrorManager errManager, String messageRx) {
        try{
            String type = JsonHelper.parseType(messageRx);
            if(type.equals(JsonHelper.TYPE_COLLECTION)
                    || type.equals(JsonHelper.TYPE_UNDO_COLLECTION)) {
                taskView.closeProgressWindow();
            }
            taskModel.acknowledgeChiefReply(messageRx);

        } catch (ProcessException err) {
            ExternalDbLoader.getConnectionTask().publishError(errManager, err);
        }
    }

    @Override
    public void onPasswordReceived(int requestCode, int resultCode, Intent data) {
        if(requestCode == PopUpLogin.PASSWORD_REQ_CODE && resultCode == Activity.RESULT_OK){
            this.secureFlag = false;
            String password = data.getStringExtra("Password");
            try{
                taskView.pauseScanning();
                taskModel.matchPassword(password);
                taskView.resumeScanning();
            } catch(ProcessException err){
                taskView.displayError(err);
                taskView.securityPrompt(false);
                this.secureFlag = true;
            }
        }
    }

    @Override
    public void onScan(String scanStr){
        try{
            taskView.pauseScanning();
            taskView.beep();
            taskModel.bundleCollection(scanStr);

        } catch (ProcessException err) {
            err.setListener(ProcessException.okayButton, this);
            taskView.displayError(err);
            if(err.getErrorType() == ProcessException.MESSAGE_TOAST)
                taskView.resumeScanning();
        }
    }

    @Override
    public void loadSetting() {
        crossHair   = preferences.getBoolean("CrossHair", true);
        beep        = preferences.getBoolean("Beep", false);
        vibrate     = preferences.getBoolean("Vibrate", false);
        mode        = Integer.parseInt(preferences.getString("ScannerMode", "1"));
        waitTime    = Integer.parseInt(preferences.getString("PacketWaitTime", "5000"));

        taskView.changeScannerSetting(crossHair, beep, vibrate, mode);
    }

    @Override
    public void onSwiped() {
        try{
            taskModel.resetCollection();
        } catch (ProcessException err) {
            taskView.displayError(err);
        }
    }

    @Override
    public boolean onSetting() {
        navFlag = true;
        taskView.navigateActivity(SettingActivity.class);
        return true;
    }

    //= For MvpModel ==================================================================================


    @Override
    public void notifyUpload() {
        taskView.openProgressWindow("Notify Collection:", "Waiting for Acknowledgement...");
        handler.postDelayed(taskModel, waitTime);
    }

    @Override
    public void notifyReceiveMessage(final String message, final int icon) {
        taskView.runItSeparate(new Runnable() {
            @Override
            public void run() {
                ProcessException err = new ProcessException(message,
                        ProcessException.MESSAGE_DIALOG, icon);
                err.setListener(ProcessException.okayButton, CollectionPresenter.this);
                taskView.displayError(err);
            }
        });

    }

    @Override
    public void notifyBundleScanned(PaperBundle bundle) {
        String venue = "";
        String paper = "";
        String program = "";

        if(bundle != null){
            venue    = bundle.getColVenue();
            paper    = bundle.getColPaperCode();
            program  = bundle.getColProgramme();
        }

        taskView.setBundle(venue, paper, program);
    }

    @Override
    public void notifyCollectorScanned(String id) {
        taskView.setCollector(id);
    }

    @Override
    public void notifyClearance() {
        taskView.setBundle("", "", "");
        taskView.setCollector("");
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

    @Override
    public void onTimesOut(ProcessException err) {
        if(taskView != null){
            taskView.closeProgressWindow();
            taskView.pauseScanning();
            taskView.displayError(err);
        }
    }
}
