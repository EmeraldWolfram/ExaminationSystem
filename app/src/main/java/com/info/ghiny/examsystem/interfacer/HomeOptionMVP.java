package com.info.ghiny.examsystem.interfacer;

import android.content.DialogInterface;

import com.info.ghiny.examsystem.model.ProcessException;

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

public interface HomeOptionMVP {
    interface MvpView extends GeneralView, TaskConnView {}

    interface MvpVPresenter extends TaskSecurePresenter, TaskConnPresenter {
        void onBackPressed();
        void onAttendance();
        void onCollection();
        void onInfo();
        void onDistribution();
        void onReport();
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
