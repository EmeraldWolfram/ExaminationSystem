package com.info.ghiny.examsystem.interfacer;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.model.ProcessException;

import java.util.Calendar;

/**
 * Created by GhinY on 10/10/2016.
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
