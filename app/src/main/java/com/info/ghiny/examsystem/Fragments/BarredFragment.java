package com.info.ghiny.examsystem.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.manager.FragListAdapter;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.model.FragmentHelper;

import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BarredFragment extends Fragment {


    public BarredFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view   = inflater.inflate(R.layout.fragment_barred, null);

        FragmentHelper helper = new FragmentHelper();

        List<String> header = helper.getTitleList(Status.BARRED);
        HashMap<String, List<Candidate>> child = helper.getChildList(Status.BARRED);

        ExpandableListView barredList = (ExpandableListView)view.findViewById(R.id.barredList);
        barredList.setAdapter(new FragListAdapter(getContext(), header, child));

        return view;
    }

}
