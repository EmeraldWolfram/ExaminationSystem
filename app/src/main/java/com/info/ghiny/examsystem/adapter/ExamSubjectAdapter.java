package com.info.ghiny.examsystem.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.database.ExamSubject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GhinY on 01/07/2016.
 */
public class ExamSubjectAdapter extends BaseAdapter {

    List<ExamSubject> papers;

    public ExamSubjectAdapter(){
        papers  = new ArrayList<>();
    }

    public void updatePapers(List<ExamSubject> papers){
        this.papers = papers;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return papers.size();
    }

    @Override
    public Object getItem(int position) {
        return papers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.exam_subject, parent, false);
        }

        ExamSubject subject = (ExamSubject)getItem(position);
        TextView examPaper  = (TextView)convertView.findViewById(R.id.paperCodeNameText);
        TextView examInfo   = (TextView)convertView.findViewById(R.id.paperDetailText);

        examPaper.setText(subject.toString());
        examInfo.setText(subject.getExamVenue());

        return convertView;
    }
}
