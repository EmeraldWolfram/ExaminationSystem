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
public class QuarantinedFragment extends Fragment {

    private ReportAttdMVP.Model taskModel;

    public QuarantinedFragment() {}

    public void setTaskModel(ReportAttdMVP.Model taskModel) {
        this.taskModel = taskModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quarantined, container, false);

        List<String> header = taskModel.getTitleList(Status.QUARANTINED);
        HashMap<String, List<Candidate>> child = taskModel.getChildList(Status.QUARANTINED);

        ExpandableListView barredList = (ExpandableListView)view.findViewById(R.id.quarantizedList);
        barredList.setAdapter(new FragListAdapter(getContext(), header, child));

        return view;
    }

}
