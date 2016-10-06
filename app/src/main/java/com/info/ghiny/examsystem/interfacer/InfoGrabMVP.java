package com.info.ghiny.examsystem.interfacer;

import android.content.DialogInterface;

import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 06/10/2016.
 */

public interface InfoGrabMVP {
    interface ViewFace extends TaskScanView, TaskConnView, GeneralView {}
    interface VPresenter extends TaskScanPresenter, TaskConnPresenter, TaskSecurePresenter {
        String getStudentSubjects();
        void onSwipeTop();
    }
    interface MPresenter extends DialogInterface.OnClickListener, DialogInterface.OnCancelListener{
        void onTimesOut(ProcessException err);
    }
    interface Model extends Runnable {
        void reqCandidatePapers(String scanValue) throws ProcessException;
    }
}
