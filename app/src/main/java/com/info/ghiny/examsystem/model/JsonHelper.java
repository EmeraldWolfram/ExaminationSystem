package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.Connector;
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
    public static final String MAJOR_KEY_TYPE_RX    = "Result";
    public static final String MAJOR_KEY_TYPE_TX    = "Type";
    public static final String MAJOR_KEY_ERROR      = "Error";

    public static final String TYPE_RECONNECTION    = "Reconnection";
    public static final String TYPE_IDENTIFICATION  = "Identification";
    public static final String TYPE_COLLECTION      = "Collection";
    public static final String TYPE_VENUE_INFO      = "VenueInfo";
    public static final String TYPE_CANDIDATE_INFO  = "CandidateInfo";
    public static final String TYPE_SUBMISSION      = "Submission";
    //============================================
    public static final String TYPE_PAPERS_VENUE    = "Papers";
    //public static final

    //Client to Host
    public static final String MINOR_KEY_VALUE      = "Value";
    public static final String MINOR_KEY_ID_NO      = "IdNo";
    public static final String MINOR_KEY_HASH_CODE  = "HashPass";
    public static final String MINOR_KEY_COLLECTOR  = "Collector";
    public static final String MINOR_KEY_BUNDLE     = "Bundle";

    //Host to Client
    public static final String MINOR_KEY_CANDIDATES = "AttendanceList";
    public static final String MINOR_KEY_PAPER_MAP  = "PaperMap";
    public static final String MINOR_KEY_PAPER_LIST = "PaperList";

    //To be determine
    public static final String LIST_SIZE    = "Size";
    public static final String LIST_VENUE   = "Venue";
    public static final String LIST_INVI    = "In-Charge";

    public static String formatString(String type, String valueStr){
        JSONObject object = new JSONObject();
        try{
            object.put(MAJOR_KEY_TYPE_TX, type);
            object.put(MINOR_KEY_VALUE, valueStr);
            return object.toString();
        } catch(Exception err){
            return null;
        }
    }

    public static String formatStaff(String idNo, String hashCode){
        JSONObject object = new JSONObject();
        try{
            object.put(MAJOR_KEY_TYPE_TX, JsonHelper.TYPE_IDENTIFICATION);
            object.put(MINOR_KEY_ID_NO, idNo);
            object.put(MINOR_KEY_HASH_CODE, hashCode);

            return object.toString();
        } catch (Exception err){
            return null;
        }
    }

    public static String formatAttendanceList(AttendanceList attdList){
        JSONObject list     = new JSONObject();
        JSONArray cddList   = new JSONArray();
        JSONObject cddObj;
        List<String> regNumList = attdList.getAllCandidateRegNumList();

        try{
            list.put(MAJOR_KEY_TYPE_TX, TYPE_SUBMISSION);
            list.put(LIST_INVI, LoginModel.getStaff().getIdNo());
            list.put(LIST_VENUE, LoginModel.getStaff().getVenueHandling());

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
            list.put(MINOR_KEY_CANDIDATES, cddList);
            list.put(LIST_SIZE, cddList.length());
            return list.toString();
        } catch(Exception err){
            return null;
        }
    }

    public static String formatCollection(String bundleStr){
        JSONObject object   = new JSONObject();
        try{
            object.put(MAJOR_KEY_TYPE_TX, TYPE_COLLECTION);
            object.put(MINOR_KEY_COLLECTOR, LoginModel.getStaff().getIdNo());
            object.put(MINOR_KEY_BUNDLE, bundleStr);

            return object.toString();
        } catch(Exception err){
            return null;
        }
    }

    //==============================================================================================

    public static StaffIdentity parseStaffIdentity(String inStr, int attp) throws ProcessException{
        StaffIdentity staffId   = new StaffIdentity();

        try{
            JSONObject staff = new JSONObject(inStr);
            if(staff.getBoolean(MAJOR_KEY_TYPE_RX)){
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
            if(obj.getBoolean(MAJOR_KEY_TYPE_RX)){
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
            if(obj.getBoolean(MAJOR_KEY_TYPE_RX)){
                JSONArray cddArr    = obj.getJSONArray(MINOR_KEY_CANDIDATES);
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
                throw new ProcessException("Unable to download Attendance List\nPlease retry login",
                        ProcessException.FATAL_MESSAGE, IconManager.WARNING);
            }
        } catch (JSONException err){
            throw new ProcessException("Packet from Chief corrupted\nPlease Consult Developer!",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static HashMap<String, ExamSubject> parsePaperMap(String inStr) throws ProcessException{
        HashMap<String, ExamSubject> map = new HashMap<>();
        try{
            JSONObject object   = new JSONObject(inStr);
            if(object.getBoolean(MAJOR_KEY_TYPE_RX)){
                JSONArray subjectArr  = object.getJSONArray(MINOR_KEY_PAPER_MAP);
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
                throw new ProcessException("Unable to download Exam Paper from Chief\n" +
                        "Please retry login", ProcessException.FATAL_MESSAGE, IconManager.WARNING);
            }
        }catch (JSONException err){
            throw new ProcessException("Data from Chief corrupted\nPlease Consult Developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static List<ExamSubject> parsePaperList(String inStr) throws ProcessException{
        List<ExamSubject> subjects  = new ArrayList<>();
        try{

            JSONObject jObj         = new JSONObject(inStr);
            if(jObj.getBoolean(MAJOR_KEY_TYPE_RX)){
                JSONArray subjectArr    = jObj.getJSONArray(MINOR_KEY_PAPER_LIST);
                for(int i = 0; i < subjectArr.length(); i++){
                    JSONObject jSubject = subjectArr.getJSONObject(i);
                    ExamSubject subject = new ExamSubject();

                    subject.setPaperCode(jSubject.getString(ExamSubject.PAPER_CODE));
                    subject.setPaperDesc(jSubject.getString(ExamSubject.PAPER_DESC));
                    subject.setExamVenue(jSubject.getString(ExamSubject.PAPER_VENUE));

                    String session  = jSubject.getString(ExamSubject.PAPER_SESSION);
                    subject.setPaperSession(Session.parseSession(session));

                    String date     = jSubject.getString(ExamSubject.PAPER_DATE);
                    subject.setDate(ExamSubject.parseStringToDate(date));

                    subjects.add(subject);
                }

                return subjects;
            } else {
                throw new ProcessException("Not a Candidate Identity",
                        ProcessException.MESSAGE_TOAST, IconManager.WARNING);
            }
        } catch (JSONException err) {
            throw new ProcessException("Data from Chief corrupted\nPlease Consult Developer",
                    ProcessException.MESSAGE_DIALOG, IconManager.WARNING);
        }
    }

    public static String parseChallengeMessage(String inStr) throws ProcessException {
        String challengeMsg;

        try{
            JSONObject challengeObj = new JSONObject(inStr);
            if(challengeObj.getBoolean(MAJOR_KEY_TYPE_RX)){
                challengeMsg    = challengeObj.getString(Connector.CONNECT_MESSAGE);
                return challengeMsg;
            } else {
                throw new ProcessException("Chief denied reconnection." +
                        "\nPlease connect to chief with QR first",
                        ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
            }
        } catch (JSONException err) {
            throw new ProcessException("Failed to read data from Chief\nPlease consult developer!",
                    ProcessException.MESSAGE_DIALOG, IconManager.WARNING);
        }
    }
}
