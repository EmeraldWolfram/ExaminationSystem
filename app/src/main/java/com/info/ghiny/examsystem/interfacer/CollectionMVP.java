package com.info.ghiny.examsystem.interfacer;

import android.content.DialogInterface;

import com.info.ghiny.examsystem.database.PaperBundle;
import com.info.ghiny.examsystem.view_holder.OnSwipeAnimator;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 05/10/2016.
 */

public interface CollectionMVP {
    interface View extends TaskConnView, TaskScanView, GeneralView {
        void setBundle(String venue, String paper, String programme);
        void setCollector(String id);
    }

    interface MvpVPresenter extends TaskConnPresenter, TaskScanPresenter, TaskSecurePresenter,
            OnSwipeAnimator.OnSwipeListener {}

    interface MvpMPresenter extends DialogInterface.OnClickListener,
            DialogInterface.OnCancelListener {
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

        void notifyBundleScanned(PaperBundle bundle);

        void notifyCollectorScanned(String id);

        void notifyClearance();
    }

    interface Model extends Runnable, TaskSecureModel{
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

        void resetCollection() throws ProcessException;

        void acknowledgeChiefReply(String messageRx) throws ProcessException;
    }

}
