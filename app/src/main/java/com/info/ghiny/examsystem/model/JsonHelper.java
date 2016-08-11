package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.Session;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.database.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by GhinY on 06/06/2016.
 */
public class JsonHelper {
    public static final String KEY_TYPE_RETURN      = "Result";
    public static final String KEY_TYPE_CHECKIN     = "CheckIn";
    public static final String KEY_VALUE            = "Value";

    public static final String TYPE_LOGIN           = "Identity";
    public static final String TYPE_PAPERS_VENUE    = "Papers";
    public static final String TYPE_PAPERS_CDD      = "CddPapers";
    public static final String TYPE_ATTD_LIST       = "AttdList";
    public static final String TYPE_COLLECT         = "Collection";

    public static final String LIST_SIZE    = "Size";
    public static final String LIST_VENUE   = "Venue";
    public static final String LIST_INVI    = "In-Charge";
    public static final String LIST_LIST    = "CddList";
    public static final String PAPER_MAP    = "PaperMap";
    public static final String PAPER_LIST   = "PaperList";
    public static final String COLLECTOR    = "Collector";
    public static final String COLLECTED    = "Bundle";

    public static String formatString(String type, String valueStr){
        JSONObject object = new JSONObject();
        try{
            object.put(KEY_TYPE_CHECKIN, type);
            object.put(KEY_VALUE, valueStr);
            return object.toString();
        } catch(Exception err){
            return null;
        }
    }

    public static String formatPassword(String id, String password){
        JSONObject object = new JSONObject();
        try{
            object.put(KEY_TYPE_CHECKIN, TYPE_LOGIN);
            object.put(StaffIdentity.STAFF_ID_NO, id);
            object.put(StaffIdentity.STAFF_PASS, password);

            return object.toString();
        } catch (Exception err){
            return null;
        }
    }

    public static String formatAttdList(AttendanceList attdList){
        JSONObject list     = new JSONObject();
        JSONArray cddList   = new JSONArray();
        JSONObject cddObj;
        List<String> regNumList = attdList.getAllCandidateRegNumList();

        try{
            list.put(KEY_TYPE_CHECKIN, TYPE_ATTD_LIST);
            list.put(LIST_INVI, LoginHelper.getStaff().getIdNo());
            list.put(LIST_VENUE, LoginHelper.getStaff().getVenueHandling());

            for(int i = 0; i < regNumList.size(); i++){
                Candidate cdd = attdList.getCandidate(regNumList.get(i));
                if(cdd.getStatus() != Status.EXEMPTED && cdd.getStatus() != Status.BARRED){
                    cddObj = new JSONObject();
                    cddObj.put(Candidate.CDD_EXAM_INDEX, cdd.getExamIndex());
                    cddObj.put(Candidate.CDD_PAPER, cdd.getPaperCode());
                    cddObj.put(Candidate.CDD_TABLE, cdd.getTableNumber().toString());
                    cddObj.put(Candidate.CDD_ATTENDAND, cdd.getStatus().toString());
                    cddList.put(cddObj);
                }
            }
            list.put(LIST_LIST, cddList);
            list.put(LIST_SIZE, cddList.length());
            return list.toString();
        } catch(Exception err){
            return null;
        }
    }

    public static String formatCollection(String bundleStr){
        JSONObject object   = new JSONObject();
        try{
            object.put(KEY_TYPE_CHECKIN, TYPE_COLLECT);
            object.put(COLLECTOR, LoginHelper.getStaff().getIdNo());
            object.put(COLLECTED, bundleStr);

            return object.toString();
        } catch(Exception err){
            return null;
        }
    }

    public static StaffIdentity parseStaffIdentity(String inStr, int attp) throws ProcessException{
        StaffIdentity staffId   = new StaffIdentity();

        try{
            JSONObject staff = new JSONObject(inStr);
            if(staff.getBoolean(KEY_TYPE_RETURN)){
                //set staff
                String name     = staff.getString(StaffIdentity.STAFF_NAME);
                String venue    = staff.getString(StaffIdentity.STAFF_VENUE);
                String idNo     = staff.getString(StaffIdentity.STAFF_ID_NO);
                String role     = staff.getString(StaffIdentity.STAFF_ROLE);


                staffId.setName(name);
                staffId.setIdNo(idNo);
                staffId.setVenueHandling(venue);
                staffId.addRole(role);
                //JSONArray roles = staff.getJSONArray(StaffIdentity.STAFF_ROLE);

                //for(int i = 0; i < roles.length(); i++){
                //    staffId.addRole(roles.getString(i));
                //}

                return staffId;
            } else {
                throw new ProcessException(
                        String.format(Locale.ENGLISH, "Incorrect Login Id or Password\n" +
                                "%d attempt left", attp),
                        ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
            }
        } catch (JSONException err) {
            throw new ProcessException("Failed to read data from Chief\nPlease consult developer!",
                    ProcessException.MESSAGE_DIALOG, IconManager.WARNING);
        }
    }

    public static boolean parseBoolean(String inStr) throws ProcessException {
        try {
            JSONObject obj = new JSONObject(inStr);
            if(obj.getBoolean(KEY_TYPE_RETURN)){
                return true;
            } else {
                throw new ProcessException("Request Failed", ProcessException.MESSAGE_DIALOG,
                        IconManager.WARNING);
            }
        } catch (JSONException err) {
            throw new ProcessException("FATAL: Data from Chief corrupted\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static AttendanceList parseAttdList(String inStr) throws ProcessException{
        AttendanceList attdList = new AttendanceList();
        try{
            JSONObject obj  = new JSONObject(inStr);
            if(obj.getBoolean(KEY_TYPE_RETURN)){
                JSONArray cddArr    = obj.getJSONArray(LIST_LIST);
                for(int i = 0; i < cddArr.length(); i++){
                    JSONObject jsonCdd  = cddArr.getJSONObject(i);
                    Candidate cdd       = new Candidate();

                    cdd.setExamIndex(jsonCdd.getString(Candidate.CDD_EXAM_INDEX));
                    cdd.setRegNum(jsonCdd.getString(Candidate.CDD_REG_NUM));

                    String status   = jsonCdd.getString(Candidate.CDD_STATUS);
                    cdd.setStatus(Status.parseStatus(status));
                    cdd.setTableNumber(0);
                    cdd.setPaperCode(jsonCdd.getString(Candidate.CDD_PAPER));
                    cdd.setProgramme(jsonCdd.getString(Candidate.CDD_PROG));

                    attdList.addCandidate(cdd, cdd.getPaperCode(),
                            cdd.getStatus(), cdd.getProgramme());
                }
                return attdList;
            } else {
                throw new ProcessException("Unable to download Attendance List",
                        ProcessException.MESSAGE_DIALOG, IconManager.WARNING);
            }
        } catch (JSONException err){
            throw new ProcessException("FATAL: Packet from Chief corrupted\n" +
                    "Please Consult Developer!",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static HashMap<String, ExamSubject> parsePaperMap(String inStr) throws ProcessException{
        HashMap<String, ExamSubject> map = new HashMap<>();
        try{
            JSONObject object   = new JSONObject(inStr);
            if(object.getBoolean(KEY_TYPE_RETURN)){
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
                return map;
            } else {
                throw new ProcessException("FATAL: Unable to download Exam Paper from Chief",
                        ProcessException.FATAL_MESSAGE, IconManager.WARNING);
            }
        }catch (JSONException err){
            throw new ProcessException("FATAL: Data from Chief corrupted\nPlease Consult Developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static List<ExamSubject> parsePaperList(String inStr) throws ProcessException{
        List<ExamSubject> subjects  = new ArrayList<>();
        try{

            JSONObject jObj         = new JSONObject(inStr);
            if(jObj.getBoolean(KEY_TYPE_RETURN)){
                JSONArray subjectArr    = jObj.getJSONArray(PAPER_LIST);
                for(int i = 0; i < subjectArr.length(); i++){
                    JSONObject jSubject = subjectArr.getJSONObject(i);
                    ExamSubject subject = new ExamSubject();

                    subject.setPaperCode(jSubject.getString(ExamSubject.PAPER_CODE));
                    subject.setPaperDesc(jSubject.getString(ExamSubject.PAPER_DESC));

                    String session  = jSubject.getString(ExamSubject.PAPER_SESSION);
                    subject.setPaperSession(Session.parseSession(session));
                    subject.setExamVenue(jSubject.getString(ExamSubject.PAPER_VENUE));

                    subjects.add(subject);
                }

                return subjects;
            } else {
                throw new ProcessException("Not a Candidate Identity",
                        ProcessException.MESSAGE_TOAST, IconManager.WARNING);
            }
        } catch (JSONException err) {
            throw new ProcessException("FATAL: Data from Chief corrupted\nPlease Consult Developer",
                    ProcessException.MESSAGE_DIALOG, IconManager.WARNING);
        }
    }
}
