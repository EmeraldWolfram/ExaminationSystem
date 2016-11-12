package com.info.ghiny.examsystem.interfacer;

import android.content.DialogInterface;
import android.view.View;

import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 07/10/2016.
 */

public interface TakeAttdMVP {
    byte TABLE_REASSIGN      = 0;
    byte CANDIDATE_REASSIGN  = 1;

    interface View extends TaskScanView, TaskConnView, GeneralView {
        /**
         * setTableView(...)
         *
         * This method is used to set and display table number scanned on the screen
         *
         * @param tableNum  The Table Number scanned in String
         */
        void setTableView(String tableNum);

        /**
         * setCandidateView(...)
         *
         * This method is used to set and display Candidate Info scanned on the screen
         *
         *
         * @param cddIndex      Candidate Info: the exam index of the candidate in String
         * @param cddRegNum     Candidate Info: the register number of the candidate in String
         * @param cddPaper      Candidate Info: the exam paper of the candidate in String
         */
        void setCandidateView(String cddIndex, String cddRegNum, String cddPaper);

        void setAssignBackgroundColor(int color);
    }

    interface VPresenter extends TaskScanPresenter, TaskSecurePresenter, TaskConnPresenter {
        void onBackPressed();   //Show dialog, prevent logout
        void onSwipeLeft();     //to display
        void onSwipeBottom();   //to info
        void onTag(android.view.View view);
    }

    interface MPresenter extends DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
        //void setDownloadComplete(boolean initComplete);
        //boolean isDownloadComplete();
        //void startTimer();
        void onTimesOut(ProcessException err);  //standard

        void notifyTableScanned(Integer tableNumber);
        void notifyCandidateScanned(Candidate cdd);
        void notifyDisplayReset();
        void notifyReassign(int whichReassigned);   //true means table, false mean cdd
    }

    interface Model extends Runnable, DialogInterface.OnClickListener, TaskSecureModel {
        boolean isInitialized();
        void initAttendance() throws ProcessException;  //prepare the Attd & papers (download or db)
        void checkDownloadResult(String chiefMessage) throws ProcessException;  //parse Attd and papers
        void saveAttendance();  //save before destroy
        void updateAssignList() throws ProcessException;    //update the assigned list
        //================================================================================

        /**
         * tryAssignScanValue()
         *
         * This method check the scanStr length and call one of the following
         * methods to assign the value if the length is possible table or candidate
         * 1. checkCandidate
         * 2. checkTable
         *
         * If the length is not possible to be any useful data for attendance collection process
         * this method throw MESSAGE_TOAST error
         *
         * After that, tryAssignCandidate was called to check if both table and candidate
         * is registered in the buffer and is a valid set of data and take the attendance
         *
         * @param scanStr               The value scan from the QR scanner
         */
        void tryAssignScanValue(String scanStr) throws ProcessException;    //assign scan value
        void updateNewCandidate();  //when update pressed
        void cancelNewAssign();     //when cancel pressed
        void tagAsLate();
    }

}
