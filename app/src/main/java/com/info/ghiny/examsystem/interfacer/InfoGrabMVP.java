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

        boolean onSetting();
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
