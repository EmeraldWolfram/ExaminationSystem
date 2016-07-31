package com.info.ghiny.examsystem;

import com.info.ghiny.examsystem.database.Candidate;

/**
 * Created by GhinY on 29/07/2016.
 */
public interface ViewSetter {
    void clearView();
    void setTableView(Integer tableNum);
    void setCandidateView(Candidate cdd);
}
