package com.info.ghiny.examsystem.interfacer;

import android.app.ActivityManager;
import android.content.DialogInterface;

import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 05/10/2016.
 */

public interface CollectionMVP {
    interface View extends TaskConnView, TaskScanView, GeneralView {}

    interface PresenterForView extends TaskConnPresenter, TaskScanPresenter, TaskSecurePresenter {
        void onSwipeBottom();
    }

    interface PresenterForModel extends DialogInterface.OnClickListener,
            DialogInterface.OnCancelListener {
        void onTimesOut(ProcessException err);
    }

    interface Model extends Runnable{
        /**
         * Runnable interface
         * run() was used by Handler to handle what to happen when the
         * Chief does not respond in 5 second
         */

        /**
         * bundleCollection(...)
         *
         * When collector try to collect a bundle of exam papers for marking
         * They need to scan the bundle of paper to obtain the data about the bundle collected
         * and acknowledge the Chief
         *
         * This method is responsible to
         * - verify the format of the data of the bundle obtained from the QR code
         * - verify the user is a rightful person to collect the paper
         * If the above two verification PASSED, send acknowledgement to the Chief
         * If the above two verification FAILED, throw an error to notify the user
         *
         * @param scanValue         The data of the bundle obtained directly from the QR code
         * @throws ProcessException Any exception thrown due to the verification
         */
        void bundleCollection(String scanValue) throws ProcessException;
    }

}
