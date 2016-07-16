package com.info.ghiny.examsystem.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.tools.ObtainInfoHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GhinY on 01/07/2016.
 */
public class ExamSubjectAdapter extends BaseAdapter {

    private static List<ExamSubject> papers;

    public ExamSubjectAdapter(){
        papers  = new ArrayList<>();
    }

    public void updatePapers(List<ExamSubject> papers){
        setPapers(papers);
        notifyDataSetChanged();
    }
    public static void setPapers(List<ExamSubject> papers) {
        ExamSubjectAdapter.papers = papers;
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
        TextView examDay    = (TextView)convertView.findViewById(R.id.paperDayText);
        TextView examVenue  = (TextView)convertView.findViewById(R.id.paperVenueText);
        TextView examSes    = (TextView)convertView.findViewById(R.id.paperSessionText);

        Integer days = ObtainInfoHelper.getDaysLeft(subject.getDate());
        String dayLeft;

        if(days == -1)
            dayLeft = "ENDED";
        else if(days == 0)
            dayLeft = "TODAY";
        else if(days == 1)
            dayLeft = "TOMORROW";
        else
            dayLeft = days.toString() + " days left";

        examPaper.setText(subject.toString());
        examDay.setText(dayLeft);
        examVenue.setText(subject.getExamVenue());
        examSes.setText(subject.getPaperSession());

        return convertView;
    }
}
