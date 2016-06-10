package com.info.ghiny.examsystem.tools;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.database.Candidate;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by GhinY on 10/06/2016.
 */
public class CustomAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> statusHeader;
    private HashMap<String, List<String>> paperHeader;
    private HashMap<String, HashMap<String, List<Candidate>>> dataChild;

    public CustomAdapter(Context context, List<String> statusHeader,
                         HashMap<String, List<String>> paperHeader,
                         HashMap<String, HashMap<String, List<Candidate>>> child){
        this.context        = context;
        this.statusHeader   = statusHeader;
        this.paperHeader    = paperHeader;
        this.dataChild      = child;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.paperHeader.get(this.statusHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView,
                             ViewGroup parent) {

        final String paperData = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.attendance_paper, null);
        }

        TextView paperNameView    = (TextView)convertView.findViewById(R.id.groupHeaderPaper);
        TextView sizeOfCandidate  = (TextView)convertView.findViewById(R.id.sizeOfCandidate);

        paperNameView.setText(paperData);
        sizeOfCandidate.setText("X");

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.paperHeader.get(this.statusHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.statusHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.statusHeader.size();
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
            convertView = inflater.inflate(R.layout.attendance_status, null);
        }

        TextView statusHeader = (TextView) convertView.findViewById(R.id.groupHeaderStatus);
        TextView sizeOfList = (TextView) convertView.findViewById(R.id.sizeOfList);

        statusHeader.setTypeface(null, Typeface.BOLD);
        statusHeader.setText(headerTitle);
        sizeOfList.setTypeface(null, Typeface.NORMAL);
        sizeOfList.setText(size);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }



}
