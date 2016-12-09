package com.info.ghiny.examsystem.interfacer;

import android.content.DialogInterface;

import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by FOONG on 9/12/2016.
 */

public interface HomeOptionMVP {
    interface MvpView extends GeneralView {}

    interface MvpVPresenter extends TaskSecurePresenter {
        void onBackPressed();
        void onAttendance();
        void onCollection();
        void onInfo();
        void onDistribution();
    }

    interface MvpMPresenter extends DialogInterface.OnCancelListener, DialogInterface.OnClickListener{}

    interface MvpModel extends TaskSecureModel{
        ProcessException prepareLogout();
    }
}
