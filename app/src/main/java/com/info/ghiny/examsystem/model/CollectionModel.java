package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.CollectionMVP;

/**
 * Created by GhinY on 05/10/2016.
 */

public class CollectionModel implements CollectionMVP.Model {

    private CollectionMVP.PresenterForModel taskPresenter;

    public CollectionModel(CollectionMVP.PresenterForModel taskPresenter){
        this.taskPresenter  = taskPresenter;
    }

    //Not Yet Complete, refer interface
    //Verify Rightful Collector
    //Verify input Bundle format
    @Override
    public void bundleCollection(String scanValue) throws ProcessException {
        ConnectionTask.setCompleteFlag(false);
        ExternalDbLoader.acknowledgeCollection(scanValue);
    }

    @Override
    public void run() {
        try{
            if(!ConnectionTask.isComplete()){
                ProcessException err = new ProcessException("Bundle collection times out.",
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
        if(!LoginModel.getStaff().matchPassword(password))
            throw new ProcessException("Access denied. Incorrect Password",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
    }
}
