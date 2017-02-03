package com.info.ghiny.examsystem.interfacer;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;

import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.model.ProcessException;

import java.util.HashMap;
import java.util.List;

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

public interface ReportAttdMVP {

    interface View extends GeneralView, TaskConnView {
        void displayReportWindow(String inCharge, String venue, String[] statusNo, String total);
    }

    interface VPresenter extends TaskConnPresenter, TaskSecurePresenter, DialogInterface.OnClickListener {
        Fragment getItem(int index);
        int getCount();
        CharSequence getPageTitle(int position);
        void toggleUnassign(android.view.View view);
        void signToUpload();
        void onUpload();
    }

    interface MPresenter extends DialogInterface.OnClickListener, DialogInterface.OnCancelListener{
        void setSent(boolean sent);
        boolean isSent();
        void onTimesOut(ProcessException err);
    }

    interface Model extends Runnable, TaskSecureModel {
        void uploadAttdList() throws ProcessException;
        void unassignCandidate(String tableNumber, String cddIndex) throws ProcessException;
        void assignCandidate(String cddIndex) throws ProcessException;
        List<String> getTitleList(Status status);
        HashMap<String, List<Candidate>> getChildList(Status status);
    }

}
