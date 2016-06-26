package com.info.ghiny.examsystem;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.info.ghiny.examsystem.adapter.CustomAdapter;
import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.tools.FragmentHelper;

import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AbsentFragment extends Fragment {


    public AbsentFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view   = inflater.inflate(R.layout.fragment_absent, null);

        List<String> header = FragmentHelper.getTitleList(AttendanceList.Status.ABSENT);
        HashMap<String, List<Candidate>> child =
                FragmentHelper.getChildList(AttendanceList.Status.ABSENT);

        ExpandableListView absentList = (ExpandableListView)view.findViewById(R.id.absentList);
        absentList.setAdapter(new CustomAdapter(getContext(), header, child));

        return view;
    }

}
