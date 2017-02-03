package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.InfoGrabMVP;
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
public class InfoGrabModel implements InfoGrabMVP.Model{

    private InfoGrabMVP.MPresenter taskPresenter;
    private StaffIdentity user;

    public InfoGrabModel(InfoGrabMVP.MPresenter taskPresenter){
        this.taskPresenter  = taskPresenter;
        this.user           = LoginModel.getStaff();
    }

    @Override
    public void reqCandidatePapers(String scanValue) throws ProcessException{

        if(scanValue.length() != 10)
            throw new ProcessException("Not a candidate ID", ProcessException.MESSAGE_TOAST,
                    IconManager.MESSAGE);

        ConnectionTask.setCompleteFlag(false);
        ExternalDbLoader.getPapersExamineByCdd(scanValue);  //Send a request only
    }

    @Override
    public void run() {
        try{
            if(!ConnectionTask.isComplete()) {
                ProcessException err = new ProcessException(
                        "Server busy. Request times out. \n Please try again later.",
                        ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
                err.setListener(ProcessException.okayButton, taskPresenter);
                err.setBackPressListener(taskPresenter);
                throw err;
            }
        } catch (ProcessException err){
            taskPresenter.onTimesOut(err);
        }
    }

    @Override
    public void matchPassword(String password) throws ProcessException {
        if(!user.matchPassword(password))
            throw new ProcessException("Access denied. Incorrect Password",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
    }
}
