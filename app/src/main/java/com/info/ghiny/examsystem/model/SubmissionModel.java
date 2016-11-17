package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.interfacer.SubmissionMVP;

/**
 * Created by user09 on 11/17/2016.
 */

public class SubmissionModel implements SubmissionMVP.MvpModel {

    private SubmissionMVP.MvpMPresenter taskPresenter;

    public SubmissionModel(SubmissionMVP.MvpMPresenter taskPresenter){
        this.taskPresenter  = taskPresenter;
    }


}
