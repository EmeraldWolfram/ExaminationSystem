package com.info.ghiny.examsystem.database;

import com.info.ghiny.examsystem.tools.ProcessException;
import com.info.ghiny.examsystem.tools.IconManager;

import java.util.Date;

/**
 * Created by GhinY on 07/05/2016.
 */
public class ExamSubject {
    private String paperCode;
    private String paperDesc;
    private Date date;
    private Integer startTableNum;
    private Integer numOfCandidate;
    public enum Session {AM, PM, VM}
    public enum ExamVenue {H1, H2, H3, H4, H5, H6, H7}
    private Session paperSession;
    private ExamVenue examVenue;

    public ExamSubject(){
        date = new Date();
        paperSession    = Session.AM;
        examVenue       = ExamVenue.H1;
        startTableNum   = 0;
        numOfCandidate  = 0;
        paperCode       = null;
        paperDesc       = null;
    }

    public ExamSubject(String paperCode, String paperDesc, int startTableNum, Date date,
                       int numOfCandidate, ExamVenue examVenue, Session paperSession){
        this.date           = date;
        this.paperSession   = paperSession;
        this.examVenue      = examVenue;
        this.startTableNum  = startTableNum;
        this.numOfCandidate = numOfCandidate;
        this.paperCode      = paperCode;
        this.paperDesc      = paperDesc;
    }

    public Integer getNumOfCandidate() {
        return numOfCandidate;
    }
    public Integer getStartTableNum() {
        return startTableNum;
    }

    public void setNumOfCandidate(Integer numOfCandidate) {
        this.numOfCandidate = numOfCandidate;
    }
    public void setStartTableNum(Integer startTableNum) {
        this.startTableNum = startTableNum;
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

    public void setPaperCode(String paperCode){
        this.paperCode = paperCode;
    }

    public String getPaperCode(){
        return paperCode;
    }

    public void setPaperDesc(String paperDesc) {
        this.paperDesc = paperDesc;
    }

    public String getPaperDesc() {
        return paperDesc;
    }

    public boolean isValidTable(Integer tableNumber) throws ProcessException {
        boolean valid   = false;
        int startNumber = startTableNum - 1;
        int endNumber   = startTableNum + numOfCandidate;

        if(tableNumber == null)
            throw new ProcessException("Input tableNumber is null", ProcessException.MESSAGE_DIALOG,
                    IconManager.WARNING);

        if(tableNumber < endNumber && tableNumber > startNumber)
            valid = true;

        return valid;
    }

    @Override
    public String toString() throws NullPointerException{
        String str;
        if(paperCode != null && paperDesc != null)
            str = paperCode + "  " + paperDesc;
        else if(paperCode == null)
            throw new NullPointerException("Paper Code was not filled yet");
        else
            throw new NullPointerException("Paper Description was not filled yet");

        return str;
    }
}
