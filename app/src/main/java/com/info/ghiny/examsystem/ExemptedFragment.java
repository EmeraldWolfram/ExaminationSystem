package com.info.ghiny.examsystem;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.info.ghiny.examsystem.adapter.FragListAdapter;
import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.tools.FragmentHelper;

import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExemptedFragment extends Fragment {


    public ExemptedFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view   = inflater.inflate(R.layout.fragment_exempted, null);

        FragmentHelper helper   = new FragmentHelper();

        List<String> header = helper.getTitleList(Status.EXEMPTED);
        HashMap<String, List<Candidate>> child = helper.getChildList(Status.EXEMPTED);

        ExpandableListView exemList = (ExpandableListView)view.findViewById(R.id.exemptedList);
        exemList.setAdapter(new FragListAdapter(getContext(), header, child));

        return view;
    }

}
