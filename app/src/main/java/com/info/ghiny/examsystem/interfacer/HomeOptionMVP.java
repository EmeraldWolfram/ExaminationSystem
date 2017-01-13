package com.info.ghiny.examsystem.interfacer;

import android.content.DialogInterface;

import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by FOONG on 9/12/2016.
 */

public interface HomeOptionMVP {
    interface MvpView extends GeneralView, TaskConnView {}

    interface MvpVPresenter extends TaskSecurePresenter, TaskConnPresenter {
        void onBackPressed();
        void onAttendance();
        void onCollection();
        void onInfo();
        void onDistribution();
        boolean onSetting();
    }

    interface MvpMPresenter extends DialogInterface.OnCancelListener, DialogInterface.OnClickListener{
        void onTimesOut(ProcessException err);
        void notifyDownloadInfo();
        void notifyDatabaseFound();
    }

    interface MvpModel extends TaskSecureModel, Runnable{
        boolean isInitialized();
        void initAttendance() throws ProcessException;  //prepare the Attd & papers (download or db)
        void checkDownloadResult(String chiefMessage) throws ProcessException;  //parse Attd and papers
        void saveAttendance();  //save before destroy
        ProcessException prepareLogout();
        void restoreInfo() throws ProcessException;
        void downloadInfo() throws ProcessException;
    }
}
