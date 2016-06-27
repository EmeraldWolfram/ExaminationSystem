package com.info.ghiny.examsystem.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.database.Candidate;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by GhinY on 10/06/2016.
 */
public class CustomAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> dataHeader;    //List of Programme
    private HashMap<String, List<Candidate>> dataChild;
    //Map with  Key:    Programme
    //          Value:  Candidate List

    public CustomAdapter(Context context, List<String> header,
                             HashMap<String, List<Candidate>> child){
        this.context    = context;
        this.dataHeader = header;
        this.dataChild  = child;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.dataChild.get(this.dataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView,
                             ViewGroup parent){

        Candidate childCdd = (Candidate)getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.attendance_body, null);
        }

        TextView tableNumView   = (TextView) convertView.findViewById(R.id.assignedTableText);
        TextView candidateView  = (TextView) convertView.findViewById(R.id.assignedCddText);
        TextView cddPrgView     = (TextView) convertView.findViewById(R.id.assignedPrgText);

        tableNumView.setText(childCdd.getTableNumber().toString());
        candidateView.setText(childCdd.getStudentName());
        cddPrgView.setText(childCdd.getProgramme());

        if(childCdd.getTableNumber() != 0)
            tableNumView.setBackgroundResource(R.drawable.rounded_table);
        else{
            tableNumView.setText("");
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.dataChild.get(this.dataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.dataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.dataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        String size        = String.format(Locale.ENGLISH, "%d", getChildrenCount(groupPosition));

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.attendance_paper, null);
        }

        TextView statusHeader   = (TextView) convertView.findViewById(R.id.groupHeaderPaper);
        TextView sizeOfList     = (TextView) convertView.findViewById(R.id.sizeOfCandidate);

        statusHeader.setTypeface(null, Typeface.BOLD);
        statusHeader.setText(headerTitle);
        sizeOfList.setTypeface(null, Typeface.ITALIC);
        sizeOfList.setText(size);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
