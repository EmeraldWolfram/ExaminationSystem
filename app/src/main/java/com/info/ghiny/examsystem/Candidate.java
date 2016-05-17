package com.info.ghiny.examsystem;

/**
 * Created by GhinY on 14/05/2016.
 */
public class Candidate {
    private Integer tableNumber;
    private String studentName;
    private String paper;   //Just the code
    public enum Status {
        PRESENT,
        ABSENT,
        EXEMPTED,
        BARRED
        }
    private Status status;


    public Candidate(){
        tableNumber = 0;
        paper   = null;
        status  = Status.ABSENT;
        studentName = null;
    }

    public Candidate(int tableNumber, String paper,
                     Status status, String sName){
        this.tableNumber = tableNumber;
        this.paper  = paper;
        this.status = status;
        studentName = sName;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getTableNumber() {
        return tableNumber.toString();
    }

    public void setPaper(String paper) {
        this.paper = paper;
    }

    public String getPaper() {
        return paper;
    }

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
