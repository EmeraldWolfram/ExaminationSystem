package com.info.ghiny.examsystem.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.info.ghiny.examsystem.R;

import org.w3c.dom.Text;

/**
 * Created by GhinY on 13/05/2016.
 */
public class ExamSystemAdapter extends CursorAdapter {

    public ExamSystemAdapter(Context context, Cursor cursor){
        super(context, cursor);
    }

    public void bindView(View view, Context context, Cursor cursor){
        TextView examPaper  = (TextView)view.findViewById(R.id.paperCodeNameText);
        TextView examInfo   = (TextView)view.findViewById(R.id.paperDetailText);

        String paperCode    = cursor.getString(cursor.getColumnIndex("Code")) + "\t"
                + cursor.getString(cursor.getColumnIndex("Desc"));
        String paperInfo    = cursor.getString(cursor.getColumnIndex("Date")) + "\t"
                + cursor.getString(cursor.getColumnIndex("Venue")) + "\t"
                + cursor.getString(cursor.getColumnIndex("Session"));

        examPaper.setText(paperCode);
        examInfo.setText(paperInfo);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(R.layout.exam_subject, parent, false);
    }

}
