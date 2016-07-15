package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.LocalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GhinY on 06/06/2016.
 */
public class JsonHelper {
    public static final String KEY_TYPE_RETURN      = "Result";
    public static final String KEY_TYPE_CHECKIN     = "CheckIn";
    public static final String KEY_TYPE_TYPE        = "Type";
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

    public static void parseStaffIdentity(String inStr) throws ProcessException{
        HashMap<String, ExamSubject> map = new HashMap<>();
        AttendanceList attdList = new AttendanceList();

        try{
            LocalDbLoader jdbcLoader = new LocalDbLoader(LocalDbLoader.DRIVER, LocalDbLoader.ADDRESS);

            JSONObject staff = new JSONObject(inStr);
            if(staff.getBoolean(KEY_TYPE_RETURN)){
                //set staff
                String name     = staff.getString(StaffIdentity.STAFF_NAME);
                String venue    = staff.getString(StaffIdentity.STAFF_VENUE);

                LoginHelper.getStaff().setName(name);
                LoginHelper.getStaff().setVenueHandling(venue);

                JSONArray roles = staff.getJSONArray(StaffIdentity.STAFF_ROLE);

                for(int i = 0; i < roles.length(); i++){
                    LoginHelper.getStaff().addRole(roles.getString(i));
                }

                LoginHelper.getStaff().setIsSet(true);

                //Set papers
                JSONArray subjectArr  = staff.getJSONArray(PAPER_MAP);
                for(int i = 0; i < subjectArr.length(); i++){
                    JSONObject jSubject = subjectArr.getJSONObject(i);
                    ExamSubject subject = new ExamSubject();

                    subject.setPaperCode(jSubject.getString(ExamSubject.PAPER_CODE));
                    subject.setPaperDesc(jSubject.getString(ExamSubject.PAPER_DESC));
                    subject.setStartTableNum(jSubject.getInt(ExamSubject.PAPER_START_NO));
                    subject.setNumOfCandidate(jSubject.getInt(ExamSubject.PAPER_TOTAL_CDD));

                    map.put(subject.getPaperCode(), subject);
                }
                if(map.size() != 0){
                    jdbcLoader.savePaperList(map);
                    Candidate.setPaperList(map);
                }

                //Set attendance list
                JSONArray cddArr    = staff.getJSONArray(LIST_LIST);
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

                    attdList.addCandidate(cdd, cdd.getPaperCode(),
                            cdd.getStatus(), cdd.getProgramme());
                }
                if(attdList.getTotalNumberOfCandidates() != 0){
                    jdbcLoader.saveAttendanceList(attdList);
                    AssignHelper.setAttdList(attdList);
                }

                ChiefLink.setMsgValidFlag(true);
                ChiefLink.setCompleteFlag(true);
            } else {
                ChiefLink.setCompleteFlag(true);
                throw new ProcessException("Incorrect Login Id or Password",
                        ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
            }
        } catch (JSONException err){
            ChiefLink.setCompleteFlag(true);
            throw new ProcessException("Failed to query data from Chief\nPlease consult developer!",
                    ProcessException.MESSAGE_DIALOG, IconManager.WARNING);
        }
    }

    public static void parseBoolean(String inStr) throws ProcessException {
        try {
            JSONObject obj = new JSONObject(inStr);
            if(obj.getBoolean(KEY_TYPE_RETURN)){

                ChiefLink.setMsgValidFlag(true);
                ChiefLink.setCompleteFlag(true);
            } else {
                ChiefLink.setCompleteFlag(true);
                throw new ProcessException("Upload Failed", ProcessException.MESSAGE_DIALOG,
                        IconManager.WARNING);
            }
        } catch (Exception err) {
            ChiefLink.setCompleteFlag(true);
            throw new ProcessException("FATAL: " + err.getMessage() + "\nPlease consult developer!",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static void parseAttdList(String inStr) throws ProcessException{
        AttendanceList attdList = new AttendanceList();
        try{

            JSONObject obj          = new JSONObject(inStr);
            if(obj.getBoolean(KEY_TYPE_RETURN)){
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

                    attdList.addCandidate(cdd, cdd.getPaperCode(),
                            cdd.getStatus(), cdd.getProgramme());
                }
                AssignHelper.setAttdList(attdList);

                ChiefLink.setMsgValidFlag(true);
                ChiefLink.setCompleteFlag(true);
            } else {
                ChiefLink.setCompleteFlag(true);
                throw new ProcessException("FATAL: Unable to download Attendance List",
                        ProcessException.FATAL_MESSAGE, IconManager.WARNING);
            }
        } catch (JSONException err){
            ChiefLink.setCompleteFlag(true);
            throw new ProcessException("FATAL: " + err.getMessage() + "\nPlease Consult Developer!",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static void parsePaperMap(String inStr) throws ProcessException{
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
                Candidate.setPaperList(map);
                ChiefLink.setMsgValidFlag(true);
                ChiefLink.setCompleteFlag(true);
            } else {
                ChiefLink.setCompleteFlag(true);
                throw new ProcessException("FATAL: Unable to download Exam Paper from Chief",
                        ProcessException.FATAL_MESSAGE, IconManager.WARNING);
            }
        }catch (JSONException err){
            ChiefLink.setCompleteFlag(true);
            throw new ProcessException("FATAL: " + err.getMessage() + "\nPlease Consult Developer!",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static void parsePaperList(String inStr) throws ProcessException{
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
                    subject.setPaperSession(subject.parseSession(session));
                    subject.setExamVenue(jSubject.getString(ExamSubject.PAPER_VENUE));

                    subjects.add(subject);
                }
                ObtainInfoHelper.getAdapter().updatePapers(subjects);
                ChiefLink.setCompleteFlag(true);
                ChiefLink.setMsgValidFlag(true);
            } else {
                ChiefLink.setCompleteFlag(true);
                throw new ProcessException("Not a Candidate Identity",
                        ProcessException.MESSAGE_TOAST, IconManager.WARNING);
            }
        } catch (JSONException err) {
            ChiefLink.setCompleteFlag(true);
            throw new ProcessException("FATAL: " + err.getMessage() + "\nPlease Consult Developer!",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }
}
