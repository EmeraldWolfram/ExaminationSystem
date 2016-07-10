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

    public static final String TYPE_A_PASSWORD      = "ackPassword";
    public static final String TYPE_Q_IDENTITY      = "qIdentity";
    public static final String TYPE_Q_ATTD_VENUE    = "qAttdList";
    public static final String TYPE_Q_PAPERS_VENUE  = "qPapers";
    public static final String TYPE_Q_PAPERS_CDD    = "qCddPapers";
    public static final String TYPE_A_ATTD_LIST     = "ackAttdList";
    public static final String TYPE_A_COLLECT       = "ackCollection";

    public static final String KEY_RETURN    = "Result";

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

    public static String formatPassword(String id, String password){
        JSONObject object = new JSONObject();
        try{
            object.put(KEY_TYPE, TYPE_A_PASSWORD);
            object.put(StaffIdentity.STAFF_ID_NO, id);
            object.put(StaffIdentity.STAFF_PASS, password);

            return object.toString();
        } catch (Exception err){
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
            list.put(KEY_TYPE, TYPE_A_ATTD_LIST);
            list.put(LIST_SIZE, regNumList.size());
            list.put(LIST_INVI, LoginHelper.getStaff().getIdNo());
            list.put(LIST_VENUE, LoginHelper.getStaff().getVenueHandling());

            for(int i = 0; i < regNumList.size(); i++){
                Candidate cdd = attdList.getCandidate(regNumList.get(i));
                cddObj = new JSONObject();
                cddObj.put(Candidate.CDD_EXAM_INDEX, cdd.getExamIndex());
                cddObj.put(Candidate.CDD_TABLE, cdd.getTableNumber().toString());
                cddObj.put(Candidate.CDD_STATUS, cdd.getStatus().toString());
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
            object.put(KEY_TYPE, TYPE_A_COLLECT);
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
            Boolean legit   = staff.getBoolean(StaffIdentity.STAFF_LEGIT);

            invglt.setName(name);
            invglt.setIdNo(idNo);
            invglt.setVenueHandling(venue);
            invglt.setEligible(legit);

        } catch(Exception err){
            //err.printStackTrace();
            return null;
        }
        return invglt;
    }

    public static boolean parseBoolean(String inStr) {
        boolean isTrue;
        try {
            JSONObject obj = new JSONObject(inStr);
            isTrue = obj.getBoolean(KEY_RETURN);

        } catch (Exception err) {
            return false;
        }
        return isTrue;
    }

    public static AttendanceList parseAttdList(String inStr){
        AttendanceList attdList = new AttendanceList();
        try{
            JSONObject obj      = new JSONObject(inStr);
            JSONArray cddArr    = obj.getJSONArray(LIST_LIST);

            for(int i = 0; i < cddArr.length(); i++){
                JSONObject jsonCdd  = cddArr.getJSONObject(i);
                Candidate cdd       = new Candidate();

                cdd.setExamIndex(jsonCdd.getString(Candidate.CDD_EXAM_INDEX));
                cdd.setRegNum(jsonCdd.getString(Candidate.CDD_REG_NUM));

                String status   = jsonCdd.getString(Candidate.CDD_STATUS);
                cdd.setStatus(attdList.parseStatus(status));
                cdd.setTableNumber(0);
                cdd.setPaperCode(jsonCdd.getString(Candidate.CDD_PAPER));
                cdd.setProgramme(jsonCdd.getString(Candidate.CDD_PROG));

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

                subject.setPaperCode(jSubject.getString(ExamSubject.PAPER_CODE));
                subject.setPaperDesc(jSubject.getString(ExamSubject.PAPER_DESC));
                subject.setStartTableNum(jSubject.getInt(ExamSubject.PAPER_START_NO));
                subject.setNumOfCandidate(jSubject.getInt(ExamSubject.PAPER_TOTAL_CDD));

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

                subject.setPaperCode(jSubject.getString(ExamSubject.PAPER_CODE));
                subject.setPaperDesc(jSubject.getString(ExamSubject.PAPER_DESC));

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
