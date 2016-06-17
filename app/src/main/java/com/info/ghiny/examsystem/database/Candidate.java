package com.info.ghiny.examsystem.database;

import android.support.annotation.Nullable;

import com.info.ghiny.examsystem.tools.CustomException;

import java.util.HashMap;

/**
 * Created by GhinY on 14/05/2016.
 */
public class Candidate {
    private Integer tableNumber;
    private String studentName;
    private String regNum;
    private String paperCode;
    private String programme;
    private static HashMap<String, ExamSubject> paperList;
    private AttendanceList.Status status;

    //CONSTRUCTOR `````````````````````````````````````````````````````````````````````
    public Candidate(){
        tableNumber = 0;
        studentName = null;
        regNum      = null;
        paperCode   = null;
        programme   = null;
        status      = AttendanceList.Status.ABSENT;
    }

    public Candidate(int tableNumber, String programme, String sName, String regNum, String paperCode,
                     AttendanceList.Status status){
        this.tableNumber    = tableNumber;
        this.programme      = programme;
        this.studentName    = sName;
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

    public ExamSubject getPaper() throws CustomException{
        ExamSubject subject = getExamSubject(paperCode);
        if(subject == null){
            throw new CustomException("Paper is not in the list", CustomException.ERR_NULL_PAPER);
        }

        return subject;
    }

    public AttendanceList.Status getStatus() {
        return status;
    }
    public void setStatus(AttendanceList.Status status) {
        this.status = status;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    public String getStudentName() {
        return studentName;
    }

    public void setRegNum(String regNum) {this.regNum = regNum;}
    public String getRegNum() {
        return regNum;
    }

    //Static Method ----------------------------------------------------------------------------
    public static void setPaperList(HashMap<String, ExamSubject> papers){
        paperList = papers;
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
    private static ExamSubject getExamSubject(String paperCode) throws CustomException{
        if(paperCode == null){
            throw new CustomException("Paper Code is null", CustomException.ERR_NULL_PAPER);
        }

        if(paperList == null){
            throw new CustomException("Paper List haven initialize",
                    CustomException.ERR_EMPTY_PAPER_LIST);
        }
        return paperList.get(paperCode);
    }
}
