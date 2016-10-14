package com.info.ghiny.examsystem.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.interfacer.ReportAttdMVP;

import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PresentFragment extends Fragment {

    private ReportAttdMVP.Model taskModel;

    public PresentFragment() {}

    public void setTaskModel(ReportAttdMVP.Model taskModel) {
        this.taskModel = taskModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_present, null);

        List<String> header    = taskModel.getTitleList(Status.PRESENT);
        HashMap<String, List<Candidate>> cddChild = taskModel.getChildList(Status.PRESENT);

        ExpandableListView presentList  = (ExpandableListView) view.findViewById(R.id.presentList);
        presentList.setAdapter(new FragListAdapter(getContext(), header, cddChild));

        return view;
    }
}
