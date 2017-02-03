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
