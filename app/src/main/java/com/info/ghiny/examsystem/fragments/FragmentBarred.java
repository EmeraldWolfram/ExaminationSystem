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
import com.info.ghiny.examsystem.database.CandidateDisplayHolder;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.interfacer.SubmissionMVP;
import com.info.ghiny.examsystem.manager.ErrorManager;

import java.util.ArrayList;

/**
 * Created by user09 on 11/23/2016.
 */

public class FragmentBarred extends RootFragment {
    private SubmissionMVP.MvpModel taskModel;
    private RecyclerView recyclerView;
    private BarredListAdapter adapter;

    public FragmentBarred(){}

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
        View view       =  inflater.inflate(R.layout.fragment_status_barred, null);
        recyclerView    = (RecyclerView) view.findViewById(R.id.recyclerBarredList);
        adapter         = new BarredListAdapter(taskModel.getCandidatesWith(Status.BARRED));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        return view;
    }


    public class BarredListAdapter extends RecyclerView.Adapter<CandidateDisplayHolder> {

        private ArrayList<Candidate> barredList;


        BarredListAdapter(ArrayList<Candidate> barredList) {
            this.barredList    = barredList;
        }

        @Override
        public CandidateDisplayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View v = LayoutInflater.from(context).inflate(R.layout.attendance_body, parent, false);
            return new CandidateDisplayHolder(context, v);
        }

        @Override
        public void onBindViewHolder(CandidateDisplayHolder holder, int position) {
            Candidate cdd   = barredList.get(position);

            holder.setCddName(cdd.getExamIndex());
            holder.setCddRegNum(cdd.getRegNum());
            holder.setCddPaperCode(cdd.getPaperCode());
            holder.setCddProgramme(cdd.getProgramme());
            holder.setCddTable(cdd.getTableNumber());
        }


        @Override
        public int getItemCount() {
            return barredList.size();
        }
    }
}
