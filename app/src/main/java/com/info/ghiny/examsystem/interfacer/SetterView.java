package com.info.ghiny.examsystem.interfacer;

import com.info.ghiny.examsystem.database.Candidate;

/**
 * Created by GhinY on 29/07/2016.
 */
public interface SetterView {
    void setTableView(String tableNum);
    void setCandidateView(String cddIndex, String cddRegNum, String cddPaper);
}
