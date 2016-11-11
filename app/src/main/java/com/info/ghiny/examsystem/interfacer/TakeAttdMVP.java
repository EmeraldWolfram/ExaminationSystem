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
        void setDownloadComplete(boolean initComplete);
        boolean isDownloadComplete();
        void startTimer();
        void onTimesOut(ProcessException err);  //standard
        void displayTable(Integer tableNumber);
        void displayCandidate(Candidate cdd);
        void resetDisplay();
        void signalReassign(int whichReassigned);   //true means table, false mean cdd
    }

    interface Model extends Runnable, DialogInterface.OnClickListener, TaskSecureModel {
        boolean isInitialized();
        void initAttendance() throws ProcessException;  //prepare the Attd & papers (download or db)
        void checkDownloadResult(String chiefMessage) throws ProcessException;  //parse Attd and papers
        void saveAttendance();  //save before destroy
        void updateAssignList() throws ProcessException;    //update the assigned list
        //================================================================================
        void tryAssignScanValue(String scanStr) throws ProcessException;    //assign scan value
        void updateNewCandidate();  //when update pressed
        void cancelNewAssign();     //when cancel pressed
        void tagAsLate();
    }

}
