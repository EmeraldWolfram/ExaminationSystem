package com.info.ghiny.examsystem.database;

/**
 * Created by GhinY on 14/05/2016.
 */
public class Candidate {
    private Integer tableNumber;
    private String studentName;
    private String paperCode;
    private String paperDesc;
    public enum Status {
        PRESENT,
        ABSENT,
        EXEMPTED,
        BARRED
        }
    private Status status;


    public Candidate(){
        tableNumber = 0;
        paperCode   = null;
        paperDesc   = null;
        status      = Status.ABSENT;
        studentName = null;
    }

    public Candidate(int tableNumber, String paperCode, String paperDesc,
                     Status status, String sName){
        this.tableNumber = tableNumber;
        this.paperCode  = paperCode;
        this.paperDesc  = paperDesc;
        this.status     = status;
        studentName     = sName;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode;
    }
    public void setPaperDesc(String paperDesc) {
        this.paperDesc = paperDesc;
    }

    public String getPaperCode() {
        return paperCode;
    }
    public String getPaperDesc() {
        return paperDesc;
    }

    public String getPaper(){return paperCode + "  " + paperDesc;   }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getStatus() {
        return status.toString();
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentName() {
        return studentName;
    }
}
