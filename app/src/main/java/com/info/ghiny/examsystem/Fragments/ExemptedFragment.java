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
public class ExemptedFragment extends Fragment {

    private ReportAttdMVP.Model taskModel;

    public ExemptedFragment() {}

    public void setTaskModel(ReportAttdMVP.Model taskModel) {
        this.taskModel = taskModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view   = inflater.inflate(R.layout.fragment_exempted, null);

        List<String> header = taskModel.getTitleList(Status.EXEMPTED);
        HashMap<String, List<Candidate>> child = taskModel.getChildList(Status.EXEMPTED);

        ExpandableListView exemList = (ExpandableListView)view.findViewById(R.id.exemptedList);
        FragListAdapter adapter = new FragListAdapter(getContext(), header, child);
        exemList.setAdapter(adapter);
        for(int i = 0; i < adapter.getGroupCount(); i++){
            exemList.expandGroup(i);
        }

        return view;
    }

}
