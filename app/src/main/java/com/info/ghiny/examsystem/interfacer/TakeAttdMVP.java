package com.info.ghiny.examsystem.interfacer;

import android.content.DialogInterface;

import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 07/10/2016.
 */

public interface TakeAttdMVP {
    interface View extends TaskScanPresenter, GeneralView {
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
    }

    interface VPresenter extends TaskScanPresenter, TaskSecurePresenter {
        void onBackPressed();
        void onSwipeLeft();
        void onSwipeBottom();
    }

    interface MPresenter extends DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
        void displayTable(Integer tableNumber);
        void displayCandidate(Candidate cdd);
        void resetDisplay();
    }

    interface Model {
        void tryAssignScanValue(String scanStr) throws ProcessException;
        void checkTable(String scanString);
        void checkCandidate(String scanString) throws ProcessException;
        boolean tryAssignCandidate() throws ProcessException;
        void attemptReassign() throws ProcessException;
        void attemptNotMatch() throws ProcessException;
        void assignCandidate();
        void updateNewCandidate();
        void cancelNewAssign();
    }

}
