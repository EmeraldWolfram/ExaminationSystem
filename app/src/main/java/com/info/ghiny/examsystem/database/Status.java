package com.info.ghiny.examsystem.database;

/**
 * Created by GhinY on 29/07/2016.
 */
public enum Status {
    PRESENT,
    ABSENT,
    EXEMPTED,
    BARRED,
    QUARANTIZED;


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
            case "QUARANTIZED":
                return Status.QUARANTIZED;
            default:
                return Status.ABSENT;
        }
    }
}
