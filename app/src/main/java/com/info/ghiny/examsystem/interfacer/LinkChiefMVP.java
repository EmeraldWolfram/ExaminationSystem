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

public interface LinkChiefMVP {
    interface ViewFace extends TaskScanView, GeneralView, TaskConnView {

    }

    interface PresenterFace extends TaskScanPresenter, TaskConnPresenter {
        /**
         * The objective of this method is to
         * 1. Check if there is a valid connector in database
         * 2. a. If No, do nothing
         * 2. b. If Yes, try to reconnect with the Chief IP in the database
         */
        void onCreate();

        /**
         * onDestroy()
         * This method is a cleaning method that use to close the
         * connection between the user (this Android) and the Chief (PC)
         */
    }

    interface MPresenter extends DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
        void setRequestComplete(boolean requestComplete);
        boolean isRequestComplete();
        void onTimesOut(ProcessException err);
    }

    interface ModelFace extends Runnable{
        /**
         * tryConnectWithQR(...)
         *
         * When trying to connect to a chief, QR generated with these encrypted data
         * 1. Chief IP Address
         * 2. Port
         * 3. Challenge Message
         *
         * This method take the QR data in a String and verify the format.
         * When the format is CORRECT,
         *  - connect to the chief with the IP address and port number
         *  - create and save the connector object into database
         *
         * When the format is INCORRECT,
         *  - throw an error
         *
         * @param scanStr       the String scanned from the QR
         * @throws ProcessException     any exception thrown when the QR format was wrong
         */
        void tryConnectWithQR(String scanStr) throws ProcessException;

        /**
         * tryConnectWithDatabase(...)
         *
         * This method was called to check if there is any valid connector in the database
         * If yes, connect with the connector and return TRUE
         * If no, return FALSE
         *
         * @return          whether connection was establish with the connector saved in database
         */
        boolean tryConnectWithDatabase();

        /**
         * onChallengeMessageReceived(...)
         *
         */
        void onChallengeMessageReceived(String messageRx) throws ProcessException;

        /**
         * closeConnection()
         *
         * This method used to terminate the connection with the Chief
         *
         * @throws Exception
         */
        void closeConnection() throws Exception;

        /**
         * reconnect()
         *
         * This method is used to reconnect to the Chief
         * by sending a request to the Chief
         * the Chief will reply with the Challenge Message
         *
         */
        boolean reconnect() throws ProcessException;
    }
}
