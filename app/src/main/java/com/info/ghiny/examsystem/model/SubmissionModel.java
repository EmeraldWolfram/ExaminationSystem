package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.SubmissionMVP;

import java.util.HashMap;

/**
 * Created by user09 on 11/17/2016.
 */

public class SubmissionModel implements SubmissionMVP.MvpModel {
    private HashMap<String, Integer> unassignedMap;
    private SubmissionMVP.MvpMPresenter taskPresenter;
    private boolean sent;

    public SubmissionModel(SubmissionMVP.MvpMPresenter taskPresenter){
        this.taskPresenter  = taskPresenter;
        this.sent           = false;
        this.unassignedMap  = new HashMap<>();
    }

    @Override
    public void uploadAttdList() throws ProcessException {
        this.sent   = false;
        ExternalDbLoader.updateAttendanceList(TakeAttdModel.getAttdList());
    }

    @Override
    public void verifyChiefResponse(String messageRx) throws ProcessException {
        sent    = true;
        boolean uploaded = JsonHelper.parseBoolean(messageRx);
        throw new ProcessException("Submission successful",
                ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
    }

    @Override
    public void matchPassword(String password) throws ProcessException {
        if(!LoginModel.getStaff().matchPassword(password))
            throw new ProcessException("Access denied. Incorrect Password",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
    }

    @Override
    public void run() {
        try{
            if(!sent){
                ProcessException err = new ProcessException(
                        "Server busy. Upload times out.\nPlease try again later.",
                        ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
                err.setListener(ProcessException.okayButton, taskPresenter);
                err.setBackPressListener(taskPresenter);
                throw err;
            }
        } catch (ProcessException err) {
            taskPresenter.onTimesOut(err);
        }
    }
}
