package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.PaperBundle;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.CollectionMVP;
import com.info.ghiny.examsystem.manager.IconManager;

import java.util.ArrayList;

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

public class CollectionModel implements CollectionMVP.Model {

    private CollectionMVP.MvpMPresenter taskPresenter;
    private String staffIdentity;
    private PaperBundle bundle;
    private boolean acknowledgeCollection;
    private boolean acknowledgeUndoCollection;
    private StaffIdentity user;

    public CollectionModel(CollectionMVP.MvpMPresenter taskPresenter){
        this.taskPresenter  = taskPresenter;
        this.staffIdentity  = null;
        this.bundle         = null;
        this.user           = LoginModel.getStaff();
    }

    boolean isAcknowledgeCollection() {
        return acknowledgeCollection;
    }

    boolean isAcknowledgeUndoCollection() {
        return acknowledgeUndoCollection;
    }

    void setAcknowledgeCollection(boolean acknowledgeCollection) {
        this.acknowledgeCollection = acknowledgeCollection;
    }

    void setAcknowledgeUndoCollection(boolean acknowledgeUndoCollection) {
        this.acknowledgeUndoCollection = acknowledgeUndoCollection;
    }

    PaperBundle getBundle() {
        return bundle;
    }

    void setBundle(PaperBundle bundle) {
        this.bundle = bundle;
    }

    void setStaffIdentity(String staffIdentity) {
        this.staffIdentity = staffIdentity;
    }

    String getStaffIdentity() {
        return staffIdentity;
    }

    //==============================================================================================

    @Override
    public void bundleCollection(String scanValue) throws ProcessException {
        if(staffIdentity != null && bundle != null){
            if(verifyBundle(scanValue)){
                this.staffIdentity  = null;
                this.taskPresenter.notifyClearance();
                this.taskPresenter.notifyBundleScanned(bundle);
            } else if(verifyCollector(scanValue)){
                this.bundle = null;
                this.taskPresenter.notifyClearance();
                this.taskPresenter.notifyCollectorScanned(staffIdentity);
            } else {
                throw new ProcessException("The decrypted QR code is neither Staff ID or Bundle",
                        ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
            }
        } else {
            if(verifyCollector(scanValue)) {
                this.taskPresenter.notifyCollectorScanned(scanValue);
            } else if(verifyBundle(scanValue)) {
                this.taskPresenter.notifyBundleScanned(bundle);
            } else {
                throw new ProcessException("The decrypted QR code is neither Staff ID or Bundle",
                        ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
            }

            sendCollection();
        }
    }


    @Override
    public void run() {
        try{
            if(!acknowledgeCollection && !acknowledgeUndoCollection){
                ProcessException err = new ProcessException("Request times out.",
                        ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
                err.setListener(ProcessException.okayButton, taskPresenter);
                err.setBackPressListener(taskPresenter);
                throw err;
            }
        } catch (ProcessException err) {
            taskPresenter.onTimesOut(err);
        }
    }

    @Override
    public void matchPassword(String password) throws ProcessException {
        if(!user.matchPassword(password))
            throw new ProcessException("Access denied. Incorrect Password",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
    }

    @Override
    public void resetCollection() throws ProcessException {
        if(this.bundle != null && this.staffIdentity != null){
            acknowledgeUndoCollection = false;
            ExternalDbLoader.undoCollection(staffIdentity, bundle);
        }
        this.bundle = null;
        this.staffIdentity = null;
        this.taskPresenter.notifyClearance();
    }

    boolean verifyCollector(String scanStr){
        if(scanStr.length() == 6){
            this.staffIdentity  = scanStr;
            return true;
        }
        return false;
    }

    boolean verifyBundle(String scanStr){
        PaperBundle bundle  = new PaperBundle();

        if (bundle.parseBundle(scanStr)){
            this.bundle = bundle;
            return true;
        }

        return false;
    }

    private void sendCollection() throws ProcessException {
        if(staffIdentity != null && bundle != null){
            taskPresenter.notifyUpload();
            acknowledgeCollection   = false;
            ExternalDbLoader.acknowledgeCollection(staffIdentity, bundle);
        }
    }

    @Override
    public void acknowledgeChiefReply(String messageRx) throws ProcessException {
        switch (JsonHelper.parseType(messageRx)){
            case JsonHelper.TYPE_COLLECTION:
                acknowledgeCollection = true;
                if(JsonHelper.parseBoolean(messageRx)){
                    taskPresenter.notifyReceiveMessage("Collection successfully recorded!", IconManager.ASSIGNED);
                } else {
                    taskPresenter.notifyReceiveMessage("Chief denied collection", IconManager.WARNING);
                }
                break;
            case JsonHelper.TYPE_UNDO_COLLECTION:
                acknowledgeUndoCollection = true;
                if(JsonHelper.parseBoolean(messageRx)){
                    taskPresenter.notifyReceiveMessage("Undo collection successfully recorded!", IconManager.ASSIGNED);
                } else {
                    taskPresenter.notifyReceiveMessage("Chief denied request", IconManager.WARNING);
                }
                break;
            case JsonHelper.TYPE_ATTENDANCE_UP:
                ArrayList<Candidate> candidates = JsonHelper.parseUpdateList(messageRx);
                TakeAttdModel.rxAttendanceUpdate(candidates);
                ExternalDbLoader.acknowledgeUpdateReceive();
                break;
        }
    }
}
