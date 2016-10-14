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
        /**
         * onSwipeTop()
         *
         * This method was called when the Layout view go through a swipe from bottom to top
         * In this part, it was used to terminate the activity
         */
        void onSwipeTop();
    }

    interface MPresenter extends DialogInterface.OnClickListener, DialogInterface.OnCancelListener{
        /**
         * onClick and onCancel
         *
         * are both used for the onTimesOut display.
         * In this system, onTimesOut always show a pop out dialog with a single button
         * When the button was click, onClick will be called
         * When the dialog was cancelled (by back button press), onCancel will be called
         */

        /**
         * onTimesOut(...)
         *
         * This method is called whenever the user try to communicate with another device
         * When the message was send out, a timer is started to wait for the respond from
         * the other device.
         *
         * When the timer times out, this method shall be called
         *
         * Message should be created in an exception form and put into the method.
         * This method then display the message on the screen.
         *
         * @param err   The message in form of exception to be display to the user
         */
        void onTimesOut(ProcessException err);
    }

    interface Model extends Runnable, TaskSecureModel {
        /**
         * Runnable interface
         * run() was used by Handler to handle what to happen when the
         * Chief does not respond in 5 second, onTimesOut from Presenter shall be called
         */

        /**
         * reqCandidatePapers(...)
         *
         * This method is used to:
         * 1. verify the format of the string captured from the QR code
         * if the format is CORRECT, a request of info shall be send to the Chief
         * If the format is INCORRECT, throw an error
         *
         * @param scanValue             Registration Number (RegNum) of the candidate
         * @throws ProcessException     Error of wrong format or not RegNum
         *
         * Eg. of correct Registration Number (15WAU88888)
         */
        void reqCandidatePapers(String scanValue) throws ProcessException;
    }
}
