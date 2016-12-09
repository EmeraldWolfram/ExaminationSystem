package com.info.ghiny.examsystem.model;

import android.util.Log;

import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.HomeOptionMVP;
import com.info.ghiny.examsystem.manager.IconManager;

/**
 * Created by FOONG on 9/12/2016.
 */

public class HomeOptionModel implements HomeOptionMVP.MvpModel {

    private HomeOptionMVP.MvpMPresenter taskPresenter;
    private StaffIdentity user;

    public HomeOptionModel(HomeOptionMVP.MvpMPresenter taskPresenter){
        this.taskPresenter  = taskPresenter;
        this.user           = LoginModel.getStaff();
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
}
