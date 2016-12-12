package com.info.ghiny.examsystem.interfacer;

import android.content.DialogInterface;

import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.view_holder.OnSwipeAnimator;
import com.info.ghiny.examsystem.model.ProcessException;

import java.util.ArrayList;

/**
 * Created by GhinY on 07/10/2016.
 */

public interface TakeAttdMVP {
    byte TABLE_REASSIGN      = 0;
    byte CANDIDATE_REASSIGN  = 1;

    interface View extends TaskScanView, TaskConnView, GeneralView,OnSwipeAnimator.OnSwipeListener {
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

        void setTagButton(boolean showAntiTag);

        void onTag(android.view.View view);
    }

    interface VPresenter extends TaskScanPresenter, TaskSecurePresenter, TaskConnPresenter {
        void onSwiped(android.view.View refView);
        void onSwipeLeft();     //to display
        void onSwipeBottom();   //to info
        void onTag(android.view.View view);
        boolean onSetting();
    }

    interface MPresenter extends DialogInterface.OnClickListener, DialogInterface.OnCancelListener,
            android.view.View.OnClickListener {
        void onTimesOut(ProcessException err);  //standard

        void notifyUndone(String message);
        void notifyTableScanned(Integer tableNumber);
        void notifyCandidateScanned(Candidate cdd);
        void notifyDisplayReset();
        void notifyReassign(int whichReassigned);   //true means table, false mean cdd
        void notifyTagUntag(boolean showAntiTag);
    }

    interface Model extends Runnable, DialogInterface.OnClickListener, TaskSecureModel {
        boolean isInitialized();
        void initAttendance() throws ProcessException;  //prepare the Attd & papers (download or db)
        void checkDownloadResult(String chiefMessage) throws ProcessException;  //parse Attd and papers
        void saveAttendance();  //save before destroy
        void updateAssignList() throws ProcessException;    //update the assigned list
        //================================================================================

        void rxAttendanceUpdate(ArrayList<Candidate> modifyList);
        void txAttendanceUpdate(ArrayList<Candidate> modifyList) throws ProcessException;

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
        void tryAssignScanValue(String scanStr) throws ProcessException;

        /**
         * updateNewAssignment()
         *
         * This method will be called when the update button was pressed
         * in a Reassign Window
         * Replace previously assigned Table Candidate set with New Table Candidate set
         *
         */
        void updateNewAssignment();

        /**
         * cancelNewAssign()
         *
         * This method will be called when the cancel button was pressed
         * in a Reassign Window
         * Remain previously assigned Table Candidate set and discard New Table Candidate set
         */
        void cancelNewAssign();     //when cancel pressed

        /**
         * resetAttendanceAssignment()
         *
         * This method use to undo assigned Table Candidate set that was displaying
         * If the set is partially filled or not filled, this method simply clear the view
         */
        void resetAttendanceAssignment();

        /**
         * undoResetAttendanceAssignment()
         *
         * The reverse of the previous method which is used to allow candidate to undo
         * the reset
         */
        void undoResetAttendanceAssignment();

        /**
         * tagAsLateNot()
         *
         * This method use to tag the candidate in the display as late
         *
         * If no candidate is in display, the next candidate that was scanned will be tagged
         *
         */
        void tagAsLateNot();
    }

}
