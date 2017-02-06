package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;

import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.SettingActivity;
import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.AttdReportMVP;
import com.info.ghiny.examsystem.model.JavaHost;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TakeAttdModel;
import com.info.ghiny.examsystem.view_holder.ProgrammeDisplayHolder;
import com.info.ghiny.examsystem.view_holder.StatusDisplayHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class AttdReportPresenter implements AttdReportMVP.MvpVPresenter, AttdReportMVP.MvpMPresenter {

    private AttdReportMVP.MvpView taskView;
    private AttdReportMVP.MvpModel taskModel;

    private Handler timer;
    private boolean uploadFlag;
    private boolean secureFlag;
    private boolean navFlag;

    private List<ProgrammeDisplayHolder> reportParent;
    private HashMap<ProgrammeDisplayHolder, List<StatusDisplayHolder>> reportChild;


    public AttdReportPresenter(AttdReportMVP.MvpView taskView){
        this.taskView   = taskView;
        this.uploadFlag = false;
        this.secureFlag = false;
        this.navFlag    = false;
    }

    public void setTaskModel(AttdReportMVP.MvpModel taskModel) {
        this.taskModel      = taskModel;
        this.reportParent   = taskModel.getDisplayHeader();
        this.reportChild    = taskModel.getDisplayMap(reportParent);
    }

    public void setTimer(Handler timer) {
        this.timer = timer;
    }

    @Override
    public void onResume(final ErrorManager errManager) {
        if(ExternalDbLoader.getJavaHost() != null){
            ExternalDbLoader.getJavaHost().setTaskView(taskView);
            ExternalDbLoader.getJavaHost().setMessageListener(new JavaHost.OnMessageReceived() {
                @Override
                public void messageReceived(String message) {
                    onChiefRespond(errManager, message);
                }
            });
        }
    }

    @Override
    public void onRestart() {
        uploadFlag = false;
        if(!secureFlag && !navFlag){
            secureFlag = true;
            taskView.securityPrompt(false);
        }
        navFlag = false;
    }

    @Override
    public void onDestroy() {
        taskView.closeProgressWindow();
        if(timer != null){
            timer.removeCallbacks(taskModel);
        }
    }

    @Override
    public void onSubmit() {
        uploadFlag = true;
        secureFlag = true;
        taskView.securityPrompt(true);
    }

    @Override
    public void onChiefRespond(ErrorManager errManager, String messageRx) {
        try{
            String type = JsonHelper.parseType(messageRx);
            if(type.equals(JsonHelper.TYPE_SUBMISSION)){
                taskView.closeProgressWindow();
                taskModel.verifyChiefResponse(messageRx);
            } else if(type.equals(JsonHelper.TYPE_ATTENDANCE_UP)){
                ArrayList<Candidate> candidates = JsonHelper.parseUpdateList(messageRx);
                TakeAttdModel.rxAttendanceUpdate(candidates);
                ExternalDbLoader.acknowledgeUpdateReceive();
            }
        } catch (ProcessException err){
            ExternalDbLoader.getConnectionTask().publishError(errManager, err);
        }
    }

    @Override
    public void onPasswordReceived(int requestCode, int resultCode, Intent data) {
        if(requestCode == PopUpLogin.PASSWORD_REQ_CODE && resultCode == Activity.RESULT_OK){
            secureFlag  = false;
            String password = data.getStringExtra("Password");
            try{
                taskModel.matchPassword(password);

                if(uploadFlag){
                    taskModel.uploadAttdList();
                    taskView.openProgressWindow("Sending:", "Uploading Attendance List...");
                    timer.postDelayed(taskModel, 5000);
                }
            } catch(ProcessException err){
                taskView.displayError(err);
                secureFlag = true;
                taskView.securityPrompt(uploadFlag);
            }
        }
    }

    @Override
    public void onTimesOut(ProcessException err) {
        if(taskView != null){
            taskView.closeProgressWindow();
            taskView.displayError(err);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        dialog.cancel();
    }

    //= Adapter ====================================================================================

    @Override
    public int getChildrenCount(int groupPosition) {
        return reportChild.get(reportParent.get(groupPosition)).size();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return reportChild.get(reportParent.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getGroupCount() {
        return reportParent.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return reportParent.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
}
