package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.StaffIdentity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GhinY on 06/06/2016.
 */
public class JsonHelper {
    private static final String KEY_TYPE     = "Type";
    private static final String KEY_VALUE    = "Value";

    public static final String TYPE_IDENTITY = "Identity";
    public static final String TYPE_VENUE    = "Venue";
    public static final String TYPE_STUDENT  = "Student";
    public static final String TYPE_LIST     = "AttdList";
    public static final String TYPE_COLLECT  = "Collection";

    public static final String LIST_SIZE    = "Size";
    public static final String LIST_VENUE   = "Venue";
    public static final String LIST_INVI    = "In-Charge";
    public static final String LIST_LIST    = "CddList";
    public static final String COLLECTOR    = "Collector";
    public static final String COLLECTED    = "BundleCode";
    public static final String CDD_INDEX    = "ExamIndex";
    public static final String CDD_TABLE    = "Table";
    public static final String CDD_STATUS   = "Status";

    public static String formatString(String type, String valueStr){
        JSONObject object = new JSONObject();
        try{
            object.put(KEY_TYPE, type);
            object.put(KEY_VALUE, valueStr);
            return object.toString();
        } catch(Exception err){
            err.printStackTrace();
        }
        return null;
    }

    public static String formatAttdList(AttendanceList attdList){
        JSONObject list = new JSONObject();
        JSONArray cddList = new JSONArray();
        JSONObject cddObj;
        List<String> regNumList = attdList.getAllCandidateRegNumList();

        try{
            list.put(KEY_TYPE, TYPE_LIST);
            list.put(LIST_SIZE, regNumList.size());
            list.put(LIST_INVI, LoginHelper.getStaff().getRegNum());
            list.put(LIST_VENUE, LoginHelper.getStaff().getVenueHandling());

            for(int i = 0; i < regNumList.size(); i++){
                Candidate cdd = attdList.getCandidate(regNumList.get(i));
                cddObj = new JSONObject();
                cddObj.put(CDD_INDEX, cdd.getExamIndex());
                cddObj.put(CDD_TABLE, cdd.getTableNumber().toString());
                cddObj.put(CDD_STATUS, cdd.getStatus().toString());
                cddList.put(cddObj);
            }

            list.put(LIST_LIST, cddList);
            return list.toString();
        } catch(Exception err){
            err.printStackTrace();
        }
        return null;
    }

    public static String formatCollection(String bundleStr){
        JSONObject object   = new JSONObject();
        try{
            object.put(KEY_TYPE, TYPE_COLLECT);
            object.put(COLLECTOR, LoginHelper.getStaff().getRegNum());
            object.put(COLLECTED, bundleStr);

            return object.toString();
        } catch(Exception err){
            err.printStackTrace();
        }
        return null;
    }

    public static StaffIdentity parseStaffIdentity(String inStr){
        StaffIdentity invglt = new StaffIdentity();
        try{
            JSONObject staff = new JSONObject(inStr);
            String name     = staff.getString("Name");
            String idNo     = staff.getString("RegNum");
            String venue    = staff.getString("Venue");
            String password = staff.getString("Password");
            Boolean legit   = staff.getBoolean("Eligible");

            invglt.setName(name);
            invglt.setRegNum(idNo);
            invglt.setVenueHandling(venue);
            invglt.setPassword(password);
            invglt.setEligible(legit);

        } catch(Exception err){
            //err.printStackTrace();
            return null;
        }
        return invglt;
    }

    public static AttendanceList parseAttdList(String inStr){
        AttendanceList attdList = new AttendanceList();

        return attdList;
    }

    public static HashMap<String, ExamSubject> parsePaperMap(String inStr){
        HashMap<String, ExamSubject> map = new HashMap<>();

        return map;
    }

    public static List<ExamSubject> parsePaperList(String inStr){
        List<ExamSubject> subjects = new ArrayList<>();

        //return null if not a candidate
        return subjects;
    }
}
