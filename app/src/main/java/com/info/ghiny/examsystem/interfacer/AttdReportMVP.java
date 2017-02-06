package com.info.ghiny.examsystem.interfacer;

import android.content.DialogInterface;
import android.view.View;

import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.view_holder.ProgrammeDisplayHolder;
import com.info.ghiny.examsystem.view_holder.StatusDisplayHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by FOONG on 3/2/2017.
 */

public interface AttdReportMVP {

    interface MvpView extends GeneralView, TaskConnView {
        void onSubmit(View view);
    }

    interface MvpVPresenter extends TaskConnPresenter, TaskSecurePresenter {
        void onSubmit();

        //For View Adapter
        int getGroupCount();
        long getGroupId(int groupPosition);
        Object getGroup(int groupPosition);

        int getChildrenCount(int groupPosition);
        long getChildId(int groupPosition, int childPosition);
        Object getChild(int groupPosition, int childPosition);
    }

    interface MvpMPresenter extends DialogInterface.OnCancelListener, DialogInterface.OnClickListener {
        void onTimesOut(ProcessException err);
    }

    interface MvpModel extends TaskSecureModel, Runnable {
        void verifyChiefResponse(String messageRx) throws ProcessException;
        void uploadAttdList() throws ProcessException;
        List<ProgrammeDisplayHolder> getDisplayHeader();
        HashMap<ProgrammeDisplayHolder, List<StatusDisplayHolder>>
        getDisplayMap(List<ProgrammeDisplayHolder> header);
    }

}
