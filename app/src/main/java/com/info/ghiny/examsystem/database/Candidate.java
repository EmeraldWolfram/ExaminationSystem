package com.info.ghiny.examsystem.database;

import android.support.annotation.Nullable;

import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.IconManager;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by GhinY on 14/05/2016.
 */
public class Candidate {
    public static final String CDD_EXAM_INDEX   = "ExamIndex";
    public static final String CDD_REG_NUM      = "RegNum";
    public static final String CDD_STATUS       = "Status";
    public static final String CDD_ATTENDAND    = "Attendance";
    public static final String CDD_PROG         = "Programme";
    public static final String CDD_TABLE        = "TableNo";
    public static final String CDD_PAPER        = "Code";
    public static final String CDD_DB_ID        = "_id";

    private Integer tableNumber;
    private String examIndex;
    private String regNum;
    private String paperCode;
    private String programme;
    private static HashMap<String, ExamSubject> paperList;
    private Status status;

    //CONSTRUCTOR `````````````````````````````````````````````````````````````````````
    public Candidate(){
        tableNumber = 0;
        examIndex = null;
        regNum      = null;
        paperCode   = null;
        programme   = null;
        status      = Status.ABSENT;
    }

    public Candidate(int tableNumber, String programme, String examIndex, String regNum,
                     String paperCode, Status status){
        this.tableNumber    = tableNumber;
        this.programme      = programme;
        this.examIndex      = examIndex;
        this.regNum         = regNum;
        this.paperCode      = paperCode;
        this.status         = status;
    }

    //Instance Method ---------------------------------------------------------------------
    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }
    public Integer getTableNumber() {
        return tableNumber;
    }

    public String getProgramme() {
        return programme;
    }
    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode;
    }
    public String getPaperCode() {
        return paperCode;
    }

    public ExamSubject getPaper() throws ProcessException {
        ExamSubject subject = getExamSubject(paperCode);
        if(subject == null){
            //throw new ProcessException("There is no suitable paper for this candidate in this room",
            throw new ProcessException(String.format(Locale.ENGLISH, "Paper " + paperCode
                    + " is not in the initialize List that have %d subjects", paperList.size()),
                    ProcessException.MESSAGE_DIALOG, IconManager.WARNING);
        }
        return subject;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    public void setExamIndex(String examIndex) {
        this.examIndex = examIndex;
    }
    public String getExamIndex() {
        return examIndex;
    }

    public void setRegNum(String regNum) {this.regNum = regNum;}
    public String getRegNum() {
        return regNum;
    }

    //Static Method ----------------------------------------------------------------------------
    public static void setPaperList(HashMap<String, ExamSubject> papers){
        Candidate.paperList = papers;
    }

    @Nullable
    public static HashMap<String, ExamSubject> getPaperList(){
        return paperList;
    }

    @Nullable
    public static String getPaperDesc(String paperCode) {
        String paperDesc = null;
        ExamSubject examSubject = paperList.get(paperCode);
        if(examSubject != null)
            paperDesc = examSubject.getPaperDesc();
        return paperDesc;
    }

    @Nullable
    private static ExamSubject getExamSubject(String paperCode) throws ProcessException {
        if(paperCode == null){
            throw new ProcessException("FATAL: Candidate don't have paper",
                    ProcessException.MESSAGE_TOAST, IconManager.WARNING);
        }

        if(paperList == null){
            throw new ProcessException("Paper List haven initialize",
                    ProcessException.MESSAGE_DIALOG, IconManager.WARNING);
        }
        return paperList.get(paperCode);
    }
}
