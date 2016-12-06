package com.info.ghiny.examsystem.manager;

import com.info.ghiny.examsystem.interfacer.DistributionMVP;
import com.info.ghiny.examsystem.model.DistributionModel;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by FOONG on 6/12/2016.
 */

public class DistributionPresenter
        implements DistributionMVP.MvpVPresenter, DistributionMVP.MvpMPresenter{

    private DistributionMVP.MvpView taskView;
    private DistributionMVP.MvpModel taskModel;

    public DistributionPresenter(DistributionMVP.MvpView taskView){
        this.taskView   = taskView;
    }

    public void setTaskModel(DistributionMVP.MvpModel taskModel) {
        this.taskModel = taskModel;
    }

    @Override
    public void onCreate() {
        try{
            taskView.setImageQr(taskModel.encodeQr());
        } catch (ProcessException err){
            taskView.displayError(err);
        }
    }
}
