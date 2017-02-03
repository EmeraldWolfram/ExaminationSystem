package com.info.ghiny.examsystem.interfacer;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.model.ProcessException;

import java.util.Calendar;

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

public interface InfoDisplayMVP {
    interface ViewFace extends GeneralView {
        void notifyDataSetChanged();
    }

    interface Presenter extends TaskSecurePresenter {
        void onCreate(Intent intent);
        int getCount();
        View getView(int position, android.view.View convertView, ViewGroup parent);
        long getItemId(int position);
        ExamSubject getItem(int position);
    }

    interface Model extends TaskSecureModel {
        void updateSubjects(String messageRx) throws ProcessException;
        int getNumberOfSubject();
        ExamSubject getSubjectAt(int position);
        int getDaysLeft(Calendar examTime);
    }

}
