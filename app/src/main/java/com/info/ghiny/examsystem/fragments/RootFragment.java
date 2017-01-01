package com.info.ghiny.examsystem.fragments;



import android.support.v4.app.Fragment;

import com.info.ghiny.examsystem.interfacer.SubmissionMVP;
import com.info.ghiny.examsystem.manager.ErrorManager;

/**
 * Created by user09 on 11/22/2016.
 */

public class RootFragment extends Fragment {

    public RootFragment() {}

    public void setTaskModel(SubmissionMVP.MvpModel taskModel) {}

    public void setErrorManager(ErrorManager errorManager){}

    public void refresh(){}
}
