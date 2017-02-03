package com.info.ghiny.examsystem.model;


import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.LocalDbLoader;
import com.info.ghiny.examsystem.database.Role;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.HomeOptionMVP;
import com.info.ghiny.examsystem.manager.IconManager;

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

public class HomeOptionModel implements HomeOptionMVP.MvpModel {

    private HomeOptionMVP.MvpMPresenter taskPresenter;
    private StaffIdentity user;
    private boolean initialized;
    private boolean isDownloadComplete;
    private LocalDbLoader dbLoader;

    public HomeOptionModel(HomeOptionMVP.MvpMPresenter taskPresenter, LocalDbLoader dbLoader){
        this.taskPresenter      = taskPresenter;
        this.user               = LoginModel.getStaff();
        this.initialized        = false;
        this.isDownloadComplete = false;
        this.dbLoader           =  dbLoader;
    }

    public void setDownloadComplete(boolean downloadComplete) {
        isDownloadComplete = downloadComplete;
    }

    void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void matchPassword(String password) throws ProcessException {
        if(!user.matchPassword(password))
            throw new ProcessException("Access denied. Incorrect Password",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
    }

    @Override
    public ProcessException prepareLogout() {
        ProcessException err    = new ProcessException("Confirm logout and exit?",
                ProcessException.YES_NO_MESSAGE, IconManager.MESSAGE);
        err.setBackPressListener(taskPresenter);
        err.setListener(ProcessException.yesButton, taskPresenter);
        err.setListener(ProcessException.noButton, taskPresenter);
        return err;
    }

    @Override
    public void initAttendance() throws ProcessException {
        if(JavaHost.getConnector().getMyHost() == Role.IN_CHARGE){
            downloadInfo();
        } else {
            if(dbLoader.emptyAttdInDB() || dbLoader.emptyPapersInDB()){
                downloadInfo();
            } else {
                taskPresenter.notifyDatabaseFound();
            }
        }
    }

    @Override
    public void downloadInfo() throws ProcessException{
        dbLoader.clearDatabase();
        isDownloadComplete = false;
        ExternalDbLoader.dlAttendanceList();
        taskPresenter.notifyDownloadInfo();
    }

    @Override
    public void restoreInfo() throws ProcessException {
        TakeAttdModel.setAttdList(dbLoader.queryAttendanceList());
        Candidate.setPaperList(dbLoader.queryPapers());
        initialized = true;
        throw new ProcessException("Restore Complete", ProcessException.MESSAGE_TOAST,
                IconManager.MESSAGE);
    }

    @Override
    public void checkDownloadResult(String chiefMessage) throws ProcessException {
        isDownloadComplete  = true;
        TakeAttdModel.setAttdList(JsonHelper.parseAttdList(chiefMessage));
        Candidate.setPaperList(JsonHelper.parsePaperMap(chiefMessage));
        setInitialized(true);
        throw new ProcessException("Download Complete", ProcessException.MESSAGE_TOAST,
                IconManager.MESSAGE);
    }

    @Override
    public void saveAttendance() {
        if(JavaHost.getConnector().getMyHost() == Role.CHIEF && TakeAttdModel.getAttdList() != null){
            dbLoader.saveAttendanceList(TakeAttdModel.getAttdList());
            dbLoader.savePaperList(Candidate.getPaperList());
        }
    }

    @Override
    public void run() {
        try{
            if(TakeAttdModel.getAttdList() == null) {
                ProcessException err = new ProcessException(
                        "Download failed. Response times out.",
                        ProcessException.FATAL_MESSAGE, IconManager.WARNING);
                err.setListener(ProcessException.okayButton, taskPresenter);
                err.setBackPressListener(taskPresenter);
                throw err;
            } else {
                ProcessException err = new ProcessException(
                        "Preparation complete.",
                        ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
                err.setBackPressListener(taskPresenter);
                throw err;
            }
        } catch (ProcessException err){
            taskPresenter.onTimesOut(err);
        }
    }
}
