package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.InfoGrabMVP;

import java.util.Calendar;

/**
 * Created by GhinY on 01/07/2016.
 */
public class InfoGrabModel implements InfoGrabMVP.Model{

    private InfoGrabMVP.MPresenter taskPresenter;

    public InfoGrabModel(InfoGrabMVP.MPresenter taskPresenter){
        this.taskPresenter  = taskPresenter;
    }

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

    public static Integer getDaysLeft(Calendar paperDate) {
        Calendar today = Calendar.getInstance();
        Integer numberOfDay;

        numberOfDay = paperDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR);

        if(numberOfDay == 0 && paperDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)){
            numberOfDay = 0;
        } else if(today.after(paperDate)){
            numberOfDay = -1;
        } else {
            if(today.get(Calendar.YEAR) < paperDate.get(Calendar.YEAR)){
                int yearDiff = paperDate.get(Calendar.YEAR) - today.get(Calendar.YEAR);
                numberOfDay = paperDate.get(Calendar.DAY_OF_YEAR)
                        + (int)(yearDiff * 365.25) - today.get(Calendar.DAY_OF_YEAR);
            } else {
                numberOfDay = paperDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR);
            }
        }
        return numberOfDay;
    }

}
