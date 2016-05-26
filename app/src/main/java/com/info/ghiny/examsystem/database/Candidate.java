package com.info.ghiny.examsystem.database;

import java.util.HashMap;

/**
 * Created by GhinY on 14/05/2016.
 */
public class Candidate {
    private Integer tableNumber;
    private String studentName;
    private String regNum;
    private String paperCode;
    private static HashMap<String, ExamSubject> paperList;
    private AttendanceList.Status status;

    //CONSTRUCTOR `````````````````````````````````````````````````````````````````````
    public Candidate(){
        tableNumber = 0;
        studentName = null;
        regNum      = null;
        paperCode   = null;
        status      = AttendanceList.Status.ABSENT;
    }

    public Candidate(int tableNumber, String sName, String regNum, String paperCode,
                     AttendanceList.Status status){
        this.tableNumber = tableNumber;
        studentName     = sName;
        this.regNum     = regNum;
        this.paperCode  = paperCode;
        this.status     = status;
    }

    //Instance Method ---------------------------------------------------------------------
    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }
    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode;
    }
    public String getPaperCode() {
        return paperCode;
    }
    public ExamSubject getPaper(){return getExamSubject(paperCode);   }

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
    public static HashMap<String, ExamSubject> getPaperList(){
        return paperList;
    }
    public static String getPaperDesc(String paperCode) {
        String paperDesc = null;
        ExamSubject examSubject = paperList.get(paperCode);
        if(examSubject != null)
            paperDesc = examSubject.getPaperDesc();
        return paperDesc;
    }
    private static ExamSubject getExamSubject(String paperCode){
        return paperList.get(paperCode);
    }
}
