package com.info.ghiny.examsystem.database;

/**
 * Created by GhinY on 29/07/2016.
 */
public enum Status {
    PRESENT,
    ABSENT,
    EXEMPTED,
    BARRED,
    QUARANTINED;


    @Override
    public String toString() {
        return super.toString();
    }

    public static Status parseStatus(String statusString){
        switch (statusString){
            case "BARRED":
                return Status.BARRED;
            case "EXEMPTED":
                return Status.EXEMPTED;
            case "QUARANTINED":
                return Status.QUARANTINED;
            default:
                return Status.ABSENT;
        }
    }
}
