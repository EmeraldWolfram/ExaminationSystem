package com.info.ghiny.examsystem.interfacer;

import android.content.DialogInterface;
import android.content.Intent;

import com.info.ghiny.examsystem.database.Role;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 06/10/2016.
 */

public interface LoginMVP {
    interface MvpView extends TaskScanView, TaskConnView, GeneralView {
        /**
         * navToHome(...)
         *
         * This method navigate to the home screen to allow the user to start doing their task
         * However, different user have different role and different responsibility
         * Therefore the four parameter is the four feature that can be used by user
         * depending on their role
         *
         * @param attendance    Enable attendance collection feature
         * @param bundle        Enable bundle collection feature
         * @param info          Enable info grabbing feature
         * @param distribution  Enable multi-attendance collection
         */

        void navToHome(Boolean attendance, Boolean bundle, Boolean info, Boolean distribution);
    }

    interface MvpVPresenter extends TaskScanPresenter, TaskConnPresenter {
        /**
         * onPasswordReceived(...)
         *
         * This method was used to handle the String (password) key in by the user
         * When the password was received
         *
         * This method take the string and use it to hash the challenge key send by the
         * Chief then send back to the Chief. The chief will then check if the password is correct
         *
         * @param reqCode   The request code of the password (PASSWORD_REQ_CODE)
         * @param resCode   The result code (RESULT_OK or RESULT_CANCEL)
         * @param intent    The password intent that the user key in password
         */
        void onPasswordReceived(int reqCode, int resCode, Intent intent);
    }

    interface MvpMPresenter extends DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
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

    interface MvpModel extends Runnable {
        void checkQrId(String scanStr) throws ProcessException;
        void matchStaffPw(String inputPw) throws ProcessException;
        Role checkLoginResult(String msgFromChief) throws ProcessException;
    }

}
