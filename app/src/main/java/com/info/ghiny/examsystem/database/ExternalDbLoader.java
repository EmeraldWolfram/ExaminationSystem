package com.info.ghiny.examsystem.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GhinY on 07/07/2016.
 */
public class ExternalDbLoader {

    public ExternalDbLoader(){}

    public static StaffIdentity getStaffIdentity(String scanIdNumber){
        StaffIdentity id = null;
        //send the idNumberScanned across
        //wait for JSON file
        //Parse JSON to StaffIdentity class and return

        return id;
    }

    public static AttendanceList dlAttdList(){
        AttendanceList attdList = new AttendanceList();
        /*staff.getVenueHandling();*/

        //send venue across
        //wait for JSON file
        //Parse JSON to AttendanceList object and return
        return attdList;
    }

    public static HashMap<String, ExamSubject> dlPaperList(){
        HashMap<String, ExamSubject> map = new HashMap<>();

        /*staff.getVenueHandling();*/

        return map;
    }

    public static List<ExamSubject> getPapersExamineByCdd(String scanRegNum){
        List<ExamSubject> subjects = null;

        //return null if wasn't a candidate

        return subjects;
    }

    public static void updateAttdList(AttendanceList attdList){
        //send the whole attdList across in JSON
    }

    public static void acknowledgeCollection(String scanBundleCode){
        //send the bundle code across
    }


}
