package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.PaperBundle;
import com.info.ghiny.examsystem.database.Session;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.manager.IconManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Created by GhinY on 06/06/2016.
 */
public class JsonHelper {
    public static final String MAJOR_KEY_TYPE_RX    = "Result";
    public static final String MAJOR_KEY_TYPE_TX    = "Type";
    public static final String MAJOR_KEY_TYPE_ID    = "DeviceId";
    public static final String MAJOR_KEY_ERROR      = "Error";

    //=== BTW ANDROID & PC ========================================================================
    public static final String TYPE_RECONNECTION    = "Reconnection";
    public static final String TYPE_IDENTIFICATION  = "Identification";
    public static final String TYPE_COLLECTION      = "Collection";
    public static final String TYPE_UNDO_COLLECTION = "UndoCollection";
    public static final String TYPE_VENUE_INFO      = "VenueInfo";
    public static final String TYPE_CANDIDATE_INFO  = "CandidateInfo";
    public static final String TYPE_SUBMISSION      = "Submission";
    //============================================
    public static final String TYPE_PAPERS_VENUE    = "Papers";

    //Client to Host
    public static final String MINOR_KEY_VALUE      = "Value";
    public static final String MINOR_KEY_ID_NO      = "IdNo";
    public static final String MINOR_KEY_HASH_CODE  = "HashPass";
    public static final String MINOR_KEY_COLLECTOR  = "Collector";
    public static final String MINOR_KEY_BUNDLE     = "PaperBundle";

    //Host to Client
    public static final String MINOR_KEY_CANDIDATES = "AttendanceList";
    public static final String MINOR_KEY_PAPER_MAP  = "PaperMap";
    public static final String MINOR_KEY_PAPER_LIST = "PaperList";

    //To be determine
    public static final String LIST_SIZE    = "Size";
    public static final String LIST_VENUE   = "Venue";
    public static final String LIST_INVI    = "In-Charge";

    //=== BTW ANDROID & ANDROID ===================================================================
    public static final String TYPE_ATTENDANCE_UP   = "AttendanceUpdate";
    public static final String MINOR_KEY_UPDATE     = "UpdateList";

    public static String formatString(String type, String valueStr){
        JSONObject object = new JSONObject();
        try{
            object.put(MAJOR_KEY_TYPE_TX, type);
            object.put(MAJOR_KEY_TYPE_ID, 0);
            object.put(MINOR_KEY_VALUE, valueStr);
            return object.toString();
        } catch(Exception err){
            return null;
        }
    }

    public static String formatStaff(String idNo, String hashCode){
        JSONObject object = new JSONObject();
        try{
            object.put(MAJOR_KEY_TYPE_TX, TYPE_IDENTIFICATION);
            object.put(MAJOR_KEY_TYPE_ID, 0);
            object.put(MINOR_KEY_ID_NO, idNo);
            object.put(MINOR_KEY_HASH_CODE, hashCode);

            return object.toString();
        } catch (Exception err){
            return null;
        }
    }

    public static String formatAttendanceList(StaffIdentity inCharge, AttendanceList attdList){
        JSONObject list     = new JSONObject();
        JSONArray cddList   = new JSONArray();
        JSONObject cddObj;
        List<String> regNumList = attdList.getAllCandidateRegNumList();

        try{
            list.put(MAJOR_KEY_TYPE_TX, TYPE_SUBMISSION);
            list.put(LIST_INVI, inCharge.getIdNo());
            list.put(LIST_VENUE, inCharge.getExamVenue());

            for(int i = 0; i < regNumList.size(); i++){
                Candidate cdd = attdList.getCandidate(regNumList.get(i));
                if(cdd.getStatus() != Status.EXEMPTED
                        && cdd.getStatus() != Status.BARRED
                        && cdd.getStatus() != Status.QUARANTINED
                        && cdd.getStatus() != Status.ABSENT){
                    cddObj = new JSONObject();
                    cddObj.put(Candidate.CDD_EXAM_INDEX,    cdd.getExamIndex());
                    cddObj.put(Candidate.CDD_PAPER,         cdd.getPaperCode());
                    cddObj.put(Candidate.CDD_TABLE,         cdd.getTableNumber());
                    cddObj.put(Candidate.CDD_ATTENDANCE,    cdd.getStatus().toString());
                    cddObj.put(Candidate.CDD_LATE,          cdd.isLate());
                    cddList.put(cddObj);
                }
            }
            list.put(MINOR_KEY_CANDIDATES, cddList);
            list.put(LIST_SIZE, cddList.length());
            list.put(MAJOR_KEY_TYPE_ID, 0);
            return list.toString();
        } catch(Exception err){
            return null;
        }
    }

    public static String formatCollection(String staffId, PaperBundle bundle){
        JSONObject object   = new JSONObject();
        JSONObject jBundle   = new JSONObject();
        try{
            jBundle.put(PaperBundle.BUNDLE_ID,      bundle.getColId());
            jBundle.put(PaperBundle.BUNDLE_VENUE,   bundle.getColVenue());
            jBundle.put(PaperBundle.BUNDLE_PROG,    bundle.getColProgramme());
            jBundle.put(PaperBundle.BUNDLE_PAPER,   bundle.getColPaperCode());

            object.put(MAJOR_KEY_TYPE_TX, TYPE_COLLECTION);
            object.put(MAJOR_KEY_TYPE_ID, 0);
            object.put(MINOR_KEY_COLLECTOR, staffId);
            object.put(MINOR_KEY_BUNDLE, jBundle);

            return object.toString();
        } catch(Exception err){
            return null;
        }
    }

    public static String formatUndoCollection(String staffId, PaperBundle bundle){
        JSONObject object   = new JSONObject();
        JSONObject jBundle   = new JSONObject();
        try{
            jBundle.put(PaperBundle.BUNDLE_ID,      bundle.getColId());
            jBundle.put(PaperBundle.BUNDLE_VENUE,   bundle.getColVenue());
            jBundle.put(PaperBundle.BUNDLE_PROG,    bundle.getColProgramme());
            jBundle.put(PaperBundle.BUNDLE_PAPER,   bundle.getColPaperCode());

            object.put(MAJOR_KEY_TYPE_TX, TYPE_UNDO_COLLECTION);
            object.put(MAJOR_KEY_TYPE_ID, 0);
            object.put(MINOR_KEY_COLLECTOR, staffId);
            object.put(MINOR_KEY_BUNDLE, jBundle);

            return object.toString();
        } catch(Exception err){
            return null;
        }
    }

    public static String formatAttendanceUpdate(ArrayList<Candidate> candidates){
        JSONObject jsonObject   = new JSONObject();
        JSONArray jArrayList    = new JSONArray();

        try{
            for(int i = 0; i < candidates.size(); i++){
                JSONObject candidate = new JSONObject();
                Candidate cdd   = candidates.get(i);
                candidate.put(Candidate.CDD_REG_NUM,    cdd.getRegNum());
                candidate.put(Candidate.CDD_TABLE,      cdd.getTableNumber());
                candidate.put(Candidate.CDD_ATTENDANCE, cdd.getStatus().toString());
                candidate.put(Candidate.CDD_COLLECTOR,  cdd.getCollector());
                candidate.put(Candidate.CDD_LATE,       cdd.isLate());

                jArrayList.put(candidate);
            }

            jsonObject.put(MAJOR_KEY_TYPE_TX, TYPE_ATTENDANCE_UP);
            jsonObject.put(MAJOR_KEY_TYPE_ID, 0);
            jsonObject.put(MINOR_KEY_UPDATE, jArrayList);

            return jsonObject.toString();
        } catch(Exception err){
            return null;
        }
    }

    public static String formatVenueInfo(AttendanceList attdList,
                                         HashMap<String, ExamSubject> paperMap){
        JSONObject jInfo    = new JSONObject();
        JSONArray jAttdList = new JSONArray();
        JSONArray jPaperMap = new JSONArray();

        try{

            List<String> candidateRegNumList = attdList.getAllCandidateRegNumList();
            for(int i = 0; i < candidateRegNumList.size(); i++){
                Candidate cdd = attdList.getCandidate(candidateRegNumList.get(i));
                JSONObject jCandidate = new JSONObject();

                jCandidate.put(Candidate.CDD_EXAM_INDEX, cdd.getExamIndex());
                jCandidate.put(Candidate.CDD_STATUS, cdd.getStatus().toString());
                jCandidate.put(Candidate.CDD_REG_NUM, cdd.getRegNum());
                jCandidate.put(Candidate.CDD_TABLE, cdd.getTableNumber().intValue());
                jCandidate.put(Candidate.CDD_PAPER, cdd.getPaperCode());
                jCandidate.put(Candidate.CDD_PROG, cdd.getProgramme());

                jAttdList.put(jCandidate);
            }

            for(ExamSubject subject : paperMap.values()){
                JSONObject jSubject = new JSONObject();

                jSubject.put(ExamSubject.PAPER_CODE, subject.getPaperCode());
                jSubject.put(ExamSubject.PAPER_DESC, subject.getPaperDesc());
                jSubject.put(ExamSubject.PAPER_START_NO, subject.getStartTableNum().intValue());
                jSubject.put(ExamSubject.PAPER_TOTAL_CDD, subject.getNumOfCandidate().intValue());

                jPaperMap.put(jSubject);
            }

            jInfo.put(MAJOR_KEY_TYPE_TX, TYPE_VENUE_INFO);
            jInfo.put(MAJOR_KEY_TYPE_RX, true);
            jInfo.put(MAJOR_KEY_TYPE_ID, 0);
            jInfo.put(MINOR_KEY_CANDIDATES, jAttdList);
            jInfo.put(MINOR_KEY_PAPER_MAP, jPaperMap);

            return jInfo.toString();
        } catch (Exception err){
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
                staffId.setExamVenue(venue);
                staffId.setRole(role);

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

    public static String parseType(String inStr) throws ProcessException {
        try {
            JSONObject obj = new JSONObject(inStr);
            return obj.getString(MAJOR_KEY_TYPE_TX);
        } catch (JSONException err) {
            throw new ProcessException("FATAL: Data from Chief corrupted\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static int parseClientId(String inStr) throws ProcessException {
        try {
            JSONObject jMsg = new JSONObject(inStr);
            return jMsg.getInt(MAJOR_KEY_TYPE_ID);
        } catch (JSONException err){
            throw new ProcessException("FATAL: Data from Chief corrupted\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
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
                    cdd.setTableNumber((jsonCdd.has(Candidate.CDD_TABLE)) ?
                            jsonCdd.getInt(Candidate.CDD_TABLE) : 0);
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

    public static ArrayList<Candidate> parseUpdateList(String inStr) throws ProcessException{
        ArrayList<Candidate> candidates = new ArrayList<>();

        try{
            JSONObject cddsObj  = new JSONObject(inStr);
            JSONArray cddsArr   = cddsObj.getJSONArray(MINOR_KEY_UPDATE);

            for(int i=0; i < cddsArr.length(); i++){
                JSONObject jCdd = cddsArr.getJSONObject(i);
                Candidate candidate = new Candidate();
                candidate.setStatus(Status.parseStatus(jCdd.getString(Candidate.CDD_ATTENDANCE)));
                if(candidate.getStatus() == Status.PRESENT){
                    candidate.setCollector(jCdd.getString(Candidate.CDD_COLLECTOR));
                }
                candidate.setLate(jCdd.getBoolean(Candidate.CDD_LATE));
                candidate.setTableNumber(jCdd.getInt(Candidate.CDD_TABLE));
                candidate.setRegNum(jCdd.getString(Candidate.CDD_REG_NUM));

                candidates.add(candidate);
            }

            return candidates;
        } catch (JSONException err) {
            throw new ProcessException("Update Data Corrupted\nPlease consult developer!",
                    ProcessException.MESSAGE_DIALOG, IconManager.WARNING);
        }

    }
}
