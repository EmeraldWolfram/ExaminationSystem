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
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.manager.SortManager;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.view_holder.CandidateDisplayHolder;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.interfacer.SubmissionMVP;

import java.util.ArrayList;

/**
 * Copyright (C) 2016 - 2017 Steven Foong Ghin Yew <stevenfgy@yahoo.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

public class FragmentExempted extends RootFragment {
    private SubmissionMVP.MvpModel taskModel;
    private RecyclerView recyclerView;
    private ExemptedListAdapter adapter;
    private ErrorManager errorManager;

    public FragmentExempted(){}

    @Override
    public void setTaskModel(SubmissionMVP.MvpModel taskModel) {
        this.taskModel = taskModel;
    }

    @Override
    public void setErrorManager(ErrorManager errorManager) {
        this.errorManager = errorManager;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view   =  inflater.inflate(R.layout.fragment_status_exempted, null);

        recyclerView    = (RecyclerView) view.findViewById(R.id.recyclerExemptedList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        try {
            adapter = new ExemptedListAdapter(taskModel.getCandidatesWith(Status.EXEMPTED,
                    SortManager.SortMethod.GROUP_PAPER_GROUP_PROGRAM_SORT_NAME, true));
        } catch (ProcessException e) {
            errorManager.displayError(e);
        }
        recyclerView.setAdapter(adapter);

        return view;
    }


    public class ExemptedListAdapter extends RecyclerView.Adapter<CandidateDisplayHolder> {

        private ArrayList<Candidate> exemptedList;


        ExemptedListAdapter(ArrayList<Candidate> exemptedList) {
            this.exemptedList    = exemptedList;
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
            Candidate cdd   = exemptedList.get(position);

            holder.setCddName(cdd.getExamIndex());
            holder.setCddRegNum(cdd.getRegNum());
            holder.setCddPaperCode(cdd.getPaperCode());
            holder.setCddProgramme(cdd.getProgramme());
            holder.setCddTable(cdd.getTableNumber());
        }


        @Override
        public int getItemCount() {
            return exemptedList.size();
        }
    }
}
