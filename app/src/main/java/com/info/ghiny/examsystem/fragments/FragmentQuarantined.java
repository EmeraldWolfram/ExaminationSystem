package com.info.ghiny.examsystem.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.view_holder.CandidateDisplayHolder;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.interfacer.SubmissionMVP;

import java.util.ArrayList;

/**
 * Created by user09 on 11/23/2016.
 */

public class FragmentQuarantined extends RootFragment{
    private SubmissionMVP.MvpModel taskModel;
    private RecyclerView recyclerView;
    private QuarantinedListAdapter adapter;

    public FragmentQuarantined(){}

    @Override
    public void setTaskModel(SubmissionMVP.MvpModel taskModel) {
        this.taskModel = taskModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view   =  inflater.inflate(R.layout.fragment_status_quarantined, null);

        recyclerView    = (RecyclerView) view.findViewById(R.id.recyclerQuarantinedList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new QuarantinedListAdapter(taskModel.getCandidatesWith(Status.QUARANTINED));
        recyclerView.setAdapter(adapter);

        return view;
    }


    public class QuarantinedListAdapter extends RecyclerView.Adapter<CandidateDisplayHolder> {

        private ArrayList<Candidate> quarantinedList;


        QuarantinedListAdapter(ArrayList<Candidate> quarantinedList) {
            this.quarantinedList    = quarantinedList;
        }

        @Override
        public CandidateDisplayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View v = LayoutInflater.from(context).inflate(R.layout.attendance_body, parent, false);
            v.setBackgroundResource(R.drawable.custom_attendance_grey_out);
            return new CandidateDisplayHolder(context, v);
        }

        @Override
        public void onBindViewHolder(CandidateDisplayHolder holder, int position) {
            Candidate cdd   = quarantinedList.get(position);

            holder.setCddName(cdd.getExamIndex());
            holder.setCddRegNum(cdd.getRegNum());
            holder.setCddPaperCode(cdd.getPaperCode());
            holder.setCddProgramme(cdd.getProgramme());
            holder.setCddTable(cdd.getTableNumber());
        }


        @Override
        public int getItemCount() {
            return quarantinedList.size();
        }
    }
}
