package com.info.ghiny.examsystem.interfacer;

import android.content.DialogInterface;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;

import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.model.ProcessException;

import java.util.ArrayList;

/**
 * Created by user09 on 11/17/2016.
 */

public interface SubmissionMVP {
    interface MvpView extends NavigationView.OnNavigationItemSelectedListener,
            GeneralView, TaskConnView{
        void onUpload(View view);

        void displayReportWindow(String inCharge, String venue, String[] statusNo, String total);
    }

    interface MvpVPresenter extends DialogInterface.OnClickListener,
            TaskSecurePresenter, TaskConnPresenter {
        void onUpload();
        boolean onNavigationItemSelected(MenuItem item, FragmentManager manager, DrawerLayout drawer);
    }

    interface MvpMPresenter extends DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
        void onTimesOut(ProcessException err);
    }

    interface MvpModel extends Runnable, TaskSecureModel {
        void verifyChiefResponse(String messageRx) throws ProcessException;
        void uploadAttdList() throws ProcessException;
        ArrayList<Candidate> getCandidatesWith(Status status);
        void unassignCandidate(int lastPosition, Candidate candidate) throws ProcessException;
        int assignCandidate(Candidate candidate) throws ProcessException;
    }

}
