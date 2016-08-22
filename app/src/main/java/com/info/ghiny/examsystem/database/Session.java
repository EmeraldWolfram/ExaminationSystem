package com.info.ghiny.examsystem.database;

/**
 * Created by GhinY on 29/07/2016.
 */
public enum Session {
    AM,
    PM,
    VM;

    @Override
    public String toString() {
        return super.toString();
    }

    public static Session parseSession(String session){
        switch (session){
            case "PM":
                return Session.PM;
            case "VM":
                return Session.VM;
            default:
                return Session.AM;
        }
    }
}
