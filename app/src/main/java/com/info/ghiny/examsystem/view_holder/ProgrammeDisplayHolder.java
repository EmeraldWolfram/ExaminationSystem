package com.info.ghiny.examsystem.view_holder;

import android.view.View;
import android.widget.TextView;

import com.info.ghiny.examsystem.R;

import org.w3c.dom.Text;

/**
 * Created by FOONG on 5/2/2017.
 */

public class ProgrammeDisplayHolder {
    private TextView programmeView;
    private TextView totalView;
    private String programme;
    private Integer total;

    public ProgrammeDisplayHolder(String programme, Integer total){
        this.programme  = programme;
        this.total      = total;
    }

    public void setView(View parentView){
        this.programmeView  = (TextView) parentView.findViewById(R.id.groupHeaderPaper);
        this.totalView      = (TextView) parentView.findViewById(R.id.sizeOfCandidate);
    }

    public void setTotal(Integer total) {
        this.total = total;
        this.totalView.setText(total.toString());
    }

    public void setProgramme(String programme) {
        this.programme = programme;
        this.programmeView.setText(programme);
    }

    public Integer getTotal() {
        return total;
    }

    public String getProgramme() {
        return programme;
    }
}
