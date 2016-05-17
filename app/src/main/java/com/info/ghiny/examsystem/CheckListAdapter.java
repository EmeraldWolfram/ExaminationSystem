package com.info.ghiny.examsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by GhinY on 15/05/2016.
 */
public class CheckListAdapter extends BaseAdapter {

    private ArrayList<Candidate> candiddates = new ArrayList<Candidate>();

    public CheckListAdapter(){
        candiddates.add(new Candidate(1,"BAME 2134",
                Candidate.Status.PRESENT, "STEVEN WILSON"));
        candiddates.add(new Candidate(2,"BAME 2134",
                Candidate.Status.ABSENT, "FOONG GHIN YEW"));
        candiddates.add(new Candidate(69,"BAME 2004",
                Candidate.Status.PRESENT, "NG YEN AENG"));
        candiddates.add(new Candidate(70,"BAME 2004",
                Candidate.Status.BARRED, "LOUISE SIAH YI LOI"));
        candiddates.add(new Candidate(71,"BAME 2004",
                Candidate.Status.EXEMPTED, "CHU JAAN HORNG"));
    }

    public void assignCandidate(int tableNo, String paper,
                                Candidate.Status status, String candidate){
        candiddates.add(new Candidate(tableNo, paper, status, candidate));
    }

    public long getItemId(int index){
        return index;
    }

    public int getCount(){
        return candiddates.size();
    }

    public Object getItem(int index){
        return candiddates.get(index);
    }

    public View getView(int index, View view, ViewGroup parent){

        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.candidate_attendance, parent, false);
        }

        Candidate candidate = candiddates.get(index);

        TextView table  = (TextView)view.findViewById(R.id.assignedTableText);
        TextView cdName = (TextView)view.findViewById(R.id.assignedCddText);
        TextView paper  = (TextView)view.findViewById(R.id.assignedPaperText);
        TextView status = (TextView)view.findViewById(R.id.assignedStatusText);


        table.setText(candidate.getTableNumber());
        cdName.setText(candidate.getStudentName());
        paper.setText(candidate.getPaper());
        status.setText(candidate.getStatus());

        return view;
    }
}
