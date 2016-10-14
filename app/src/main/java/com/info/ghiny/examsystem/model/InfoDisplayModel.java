package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.interfacer.InfoDisplayMVP;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by GhinY on 10/10/2016.
 */

public class InfoDisplayModel implements InfoDisplayMVP.Model {

    private List<ExamSubject> papers;

    public InfoDisplayModel(){
        papers  = new ArrayList<>();
    }

    public void setPapers(List<ExamSubject> papers) {
        this.papers = papers;
    }

    @Override
    public void updateSubjects(String messageRx) throws ProcessException {
        papers.clear();
        List<ExamSubject> subjects  = JsonHelper.parsePaperList(messageRx);
        papers.addAll(subjects);
    }

    @Override
    public ExamSubject getSubjectAt(int position) {
        return papers.get(position);
    }

    @Override
    public int getDaysLeft(Calendar examTime) {
        Calendar today = Calendar.getInstance();
        Integer numberOfDay;

        numberOfDay = examTime.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR);

        if(numberOfDay == 0 && examTime.get(Calendar.YEAR) == today.get(Calendar.YEAR)){
            numberOfDay = 0;
        } else if(today.after(examTime)){
            numberOfDay = -1;
        } else {
            if(today.get(Calendar.YEAR) < examTime.get(Calendar.YEAR)){
                int yearDiff = examTime.get(Calendar.YEAR) - today.get(Calendar.YEAR);
                numberOfDay = examTime.get(Calendar.DAY_OF_YEAR)
                        + (int)(yearDiff * 365.25) - today.get(Calendar.DAY_OF_YEAR);
            } else {
                numberOfDay = examTime.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR);
            }
        }
        return numberOfDay;
    }

    @Override
    public int getNumberOfSubject() {
        return papers.size();
    }

    @Override
    public void matchPassword(String password) throws ProcessException {
        if(!LoginModel.getStaff().matchPassword(password))
            throw new ProcessException("Access denied. Incorrect Password",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
    }
}
