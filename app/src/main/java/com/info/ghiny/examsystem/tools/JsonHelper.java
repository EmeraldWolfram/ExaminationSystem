package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.LocalDbLoader;
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
    public static final String PAPER_MAP    = "PaperMap";
    public static final String PAPER_LIST   = "PaperList";
    public static final String COLLECTOR    = "Collector";
    public static final String COLLECTED    = "BundleCode";

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
            list.put(LIST_INVI, LoginHelper.getStaff().getIdNo());
            list.put(LIST_VENUE, LoginHelper.getStaff().getVenueHandling());

            for(int i = 0; i < regNumList.size(); i++){
                Candidate cdd = attdList.getCandidate(regNumList.get(i));
                cddObj = new JSONObject();
                cddObj.put(LocalDbLoader.TABLE_INFO_COLUMN_INDEX, cdd.getExamIndex());
                cddObj.put(LocalDbLoader.TABLE_INFO_COLUMN_TABLE, cdd.getTableNumber().toString());
                cddObj.put(LocalDbLoader.TABLE_INFO_COLUMN_STATUS, cdd.getStatus().toString());
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
            object.put(COLLECTOR, LoginHelper.getStaff().getIdNo());
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
            String name     = staff.getString(StaffIdentity.STAFF_NAME);
            String idNo     = staff.getString(StaffIdentity.STAFF_ID_NO);
            String venue    = staff.getString(StaffIdentity.STAFF_VENUE);
            String password = staff.getString(StaffIdentity.STAFF_PASS);
            Boolean legit   = staff.getBoolean(StaffIdentity.STAFF_LEGIT);

            invglt.setName(name);
            invglt.setIdNo(idNo);
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
        try{
            JSONObject obj      = new JSONObject(inStr);
            JSONArray cddArr    = obj.getJSONArray(LIST_LIST);

            for(int i = 0; i < cddArr.length(); i++){
                JSONObject jsonCdd  = cddArr.getJSONObject(i);
                Candidate cdd       = new Candidate();

                cdd.setExamIndex(jsonCdd.getString(LocalDbLoader.TABLE_INFO_COLUMN_INDEX));
                cdd.setRegNum(jsonCdd.getString(LocalDbLoader.TABLE_INFO_COLUMN_REGNUM));

                String status   = jsonCdd.getString(LocalDbLoader.TABLE_INFO_COLUMN_STATUS);
                cdd.setStatus(attdList.parseStatus(status));
                cdd.setTableNumber(0);
                cdd.setPaperCode(jsonCdd.getString(LocalDbLoader.TABLE_INFO_COLUMN_CODE));
                cdd.setProgramme(jsonCdd.getString(LocalDbLoader.TABLE_INFO_COLUMN_PRG));

                attdList.addCandidate(cdd, cdd.getPaperCode(), cdd.getStatus(), cdd.getProgramme());
            }
        } catch (Exception err){
            return null;
        }

        return attdList;
    }

    public static HashMap<String, ExamSubject> parsePaperMap(String inStr){
        HashMap<String, ExamSubject> map = new HashMap<>();
        try{
            JSONObject object   = new JSONObject(inStr);
            JSONArray subjectArr  = object.getJSONArray(PAPER_MAP);

            for(int i = 0; i < subjectArr.length(); i++){
                JSONObject jSubject = subjectArr.getJSONObject(i);
                ExamSubject subject = new ExamSubject();

                subject.setPaperCode(jSubject.getString(LocalDbLoader.PAPER_CODE));
                subject.setPaperDesc(jSubject.getString(LocalDbLoader.PAPER_DESC));
                subject.setStartTableNum(jSubject.getInt(LocalDbLoader.PAPER_START_NO));
                subject.setNumOfCandidate(jSubject.getInt(LocalDbLoader.PAPER_TOTAL_CDD));

                map.put(subject.getPaperCode(), subject);
            }
        }catch (Exception err){
            return null;
        }

        return map;
    }

    public static List<ExamSubject> parsePaperList(String inStr){
        List<ExamSubject> subjects  = new ArrayList<>();
        try{
            JSONObject jObj         = new JSONObject(inStr);
            JSONArray subjectArr    = jObj.getJSONArray(PAPER_LIST);

            for(int i = 0; i < subjectArr.length(); i++){
                JSONObject jSubject = subjectArr.getJSONObject(i);
                ExamSubject subject = new ExamSubject();

                subject.setPaperCode(jSubject.getString(LocalDbLoader.PAPER_CODE));
                subject.setPaperDesc(jSubject.getString(LocalDbLoader.PAPER_DESC));

                String session  = jSubject.getString(ExamSubject.PAPER_SESSION);
                subject.setPaperSession(subject.parseSession(session));
                subject.setExamVenue(jSubject.getString(ExamSubject.PAPER_VENUE));

                subjects.add(subject);
            }
        } catch (Exception err) {
            return null;
        }

        return subjects;
    }
}
