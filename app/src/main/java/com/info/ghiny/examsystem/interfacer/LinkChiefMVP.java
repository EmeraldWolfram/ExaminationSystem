package com.info.ghiny.examsystem.interfacer;

import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 05/10/2016.
 */

public interface LinkChiefMVP {
    interface ViewFace extends TaskScanView, GeneralView {}

    interface PresenterFace extends TaskScanPresenter {
        /**
         * The objective of this method is to
         * 1. Check if there is a valid connector in database
         * 2. a. If No, do nothing
         * 2. b. If Yes, try to reconnect with the Chief IP in the database
         */
        void onCreate();

        /**
         * This method is a cleaning method that use to close the
         * connection between the user (this Android) and the Chief (PC)
         */
        void onDestroy();
    }

    interface ModelFace {
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
         * closeConnection()
         *
         * This method used to terminate the connection with the Chief
         *
         * @throws Exception
         */
        void closeConnection() throws Exception;
    }
}
