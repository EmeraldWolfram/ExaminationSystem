package com.info.ghiny.examsystem;

import java.util.Date;

/**
 * Created by GhinY on 07/05/2016.
 */
public class ExamSubject {
    public String subjectCode;
    public String subjectDescrp;
    public Date date;
    public enum Session {AM, PM, VM}
    public enum ExamVenue {H1, H2, H3, H4, H5, H6, H7}
    public Session paperSession;
    public ExamVenue examVenue;

    public ExamSubject(){
        date = new Date();
        paperSession    = Session.AM;
        examVenue       = ExamVenue.H1;
        subjectCode     = null;
        subjectDescrp   = null;
    }

    public void setDate(Date date){
        this.date = date;
    }
    public Date getDate(){
        return date;
    }

    public void setPaperSession(Session session){
        paperSession = session;
    }

    public String getPaperSession(){
        return paperSession.toString();
    }

    public void setExamVenue(ExamVenue venue){
        this.examVenue = venue;
    }

    public String getExamVenue() {
        return examVenue.toString();
    }

    public void setSubjectCode(String subjectCode){
        this.subjectCode = subjectCode;
    }

    public String getSubjectCode(){
        return subjectCode;
    }

    public void setSubjectDescrp(String subjectDescrp) {
        this.subjectDescrp = subjectDescrp;
    }

    public String getSubjectDescrp() {
        return subjectDescrp;
    }
}
