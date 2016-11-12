package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.PaperBundle;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.database.Status;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 08/07/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class JsonHelperTest {


    //= FormatString() =============================================================================
    /**
     *  formatString(String type, String valueStr)
     *
     *  this method create a JSON Object and return the JSON Object in the format of String
     *
     */
    @Test
    public void testFormatString() throws Exception {
        String str = JsonHelper.formatString(JsonHelper.TYPE_SUBMISSION, "H4");

        JSONObject obj = new JSONObject(str);
        assertTrue(obj.has("Type"));
        assertEquals("Submission", obj.getString("Type"));
        assertEquals("H4", obj.getString("Value"));
    }

    //= FormatAttdList() ===========================================================================

    /**
     * formatAttendanceList(AttdList)
     *
     * Send out the whole AttendanceList
     */
    @Test
    public void testFormatAttdList() throws Exception {
        AttendanceList attdList = new AttendanceList();
        Candidate cdd1 = new Candidate(1, "RMB3", "FGY", "15WAU00001", "BAME 0001", Status.ABSENT);
        Candidate cdd2 = new Candidate(1, "RMB3", "NYN", "15WAU00002", "BAME 0001", Status.ABSENT);
        Candidate cdd3 = new Candidate(1, "RMB3", "LHN", "15WAU00003", "BAME 0001", Status.ABSENT);
        Candidate cdd4 = new Candidate(1, "RMB3", "Mr. Bar", "15WAU00004", "BAME 0002", Status.BARRED);
        Candidate cdd5 = new Candidate(1, "RMB3", "Ms. Exm", "15WAU00005", "BAME 0003", Status.EXEMPTED);
        Candidate cdd6 = new Candidate(1, "RMB3", "Ms. Qua", "15WAR00006", "BAME 0001", Status.QUARANTINED);

        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), cdd2.getStatus(), cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), cdd3.getStatus(), cdd3.getProgramme());
        attdList.addCandidate(cdd4, cdd4.getPaperCode(), cdd4.getStatus(), cdd4.getProgramme());
        attdList.addCandidate(cdd5, cdd5.getPaperCode(), cdd5.getStatus(), cdd5.getProgramme());
        attdList.addCandidate(cdd6, cdd6.getPaperCode(), cdd6.getStatus(), cdd6.getProgramme());

        String str = JsonHelper.formatAttendanceList(
                new StaffIdentity("246260", true, "Dr. Smart", "H1"), attdList);

        JSONObject obj = new JSONObject(str);
        assertEquals("Submission", obj.getString("Type"));
        assertEquals("H1", obj.getString("Venue"));
        assertEquals("246260", obj.getString("In-Charge"));
        assertEquals(4, obj.getInt("Size"));

        JSONArray jArr = obj.getJSONArray("AttendanceList");
    }

    //= FormatCollection() =========================================================================
    /**
     *  formatCollection(scanBundleStr)
     *
     *  return a JSON Object in string to acknowledge the collection of the bundle
     */
    @Test
    public void testFormatCollection() throws Exception {
        PaperBundle bundle  = new PaperBundle();
        bundle.parseBundle("14352/M4/BAME 0001/RMB3");

        String str = JsonHelper.formatCollection("246260", bundle);

        assertEquals("{\"Type\":\"Collection\",\"PaperBundle\":" +
                "{\"BundleId\":\"14352\",\"BundleProgramme\":\"RMB3\",\"BundleVenue\":\"M4\"," +
                "\"BundlePaperCode\":\"BAME 0001\"},\"Collector\":\"246260\"}", str);
    }

    //= FormatUndoCollection() =========================================================================
    /**
     *  formatUndoCollection(scanBundleStr)
     *
     *  return a JSON Object in string to acknowledge the collection of the bundle
     */
    @Test
    public void testFormatUndoCollection() throws Exception {
        PaperBundle bundle  = new PaperBundle();
        bundle.parseBundle("14352/M4/BAME 0001/RMB3");

        String str = JsonHelper.formatUndoCollection("246260", bundle);

        assertEquals("{\"Type\":\"UndoCollection\",\"PaperBundle\":" +
                "{\"BundleId\":\"14352\",\"BundleProgramme\":\"RMB3\",\"BundleVenue\":\"M4\"," +
                "\"BundlePaperCode\":\"BAME 0001\"},\"Collector\":\"246260\"}", str);
    }

    //= ParseStaffIdentity() =======================================================================

    /**
     *  parseStaffIdentity()
     *
     *  1. If JSON have wrong format, throw MESSAGE_DIALOG
     *  2. If Result is true, return a Staff Identity Object
     *  3. If Result is false, throw MESSAGE_DIALOG
     *
     */

    @Test
    public void testParseStaffIdentity_IncorrectFormat_Should_throw_DIALOG() throws Exception {
        try{
            StaffIdentity staff = JsonHelper.parseStaffIdentity(
                    "{\"Status\":[\"Collector\",\"Chief\"]," +
                            "\"Venue\":\"M5\",\"Result\":true," +
                            "\"NAME\":\"TESTER 1\",\"IdNo\":\"246810\"}", 3);

            fail("Expected FATAL_MESSAGE but none thrown");
        } catch (ProcessException err){
            assertEquals("Failed to read data from Chief\nPlease consult developer!", err.getErrorMsg());
            assertEquals(ProcessException.MESSAGE_DIALOG, err.getErrorType());
        }

    }

    @Test
    public void testParseStaffIdentity_True_Result_return_StaffIdentity() throws Exception {
        StaffIdentity staff = JsonHelper.parseStaffIdentity(
                "{\"Status\":[\"Collector\",\"Chief\"]," +
                        "\"Venue\":\"M5\",\"Result\":true," +
                        "\"Name\":\"TESTER 1\",\"IdNo\":\"246810\"}", 3);
        assertNotNull(staff);
        assertEquals("TESTER 1", staff.getName());
        assertEquals("M5", staff.getExamVenue());
        assertEquals("246810", staff.getIdNo());
    }

    @Test
    public void testParseStaffIdentity_False_Result_throw_MESSAGE_TOAST() throws Exception {
        try{
            StaffIdentity staff = JsonHelper.parseStaffIdentity("{\"Result\":false}", 3);

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err){
            assertEquals("Incorrect Login Id or Password\n" +
                    "3 attempt left", err.getErrorMsg());
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
        }

    }
    //= ParseAttdList() ============================================================================

    /**
     * parseAttdList(String inStr)
     *
     * 1. If result true, transform JSON into an AttendanceList and return it
     * 2. If result false, throw FATAL
     * 3. If JSON format wrong, throw FATAL
     */
    @Test
    public void testParseAttdList() throws Exception {
        JSONObject jObject  = new JSONObject();
        JSONArray jAttdList = new JSONArray();
        JSONObject jCdd1    = new JSONObject();
        JSONObject jCdd2    = new JSONObject();

        jCdd1.put(Candidate.CDD_EXAM_INDEX, "W010AUMB");
        jCdd1.put(Candidate.CDD_REG_NUM, "15WAU00001");
        jCdd1.put(Candidate.CDD_STATUS, "BARRED");
        jCdd1.put(Candidate.CDD_PAPER, "BAME 0001");
        jCdd1.put(Candidate.CDD_PROG, "RMB3");

        jCdd2.put(Candidate.CDD_EXAM_INDEX, "W020AUMB");
        jCdd2.put(Candidate.CDD_REG_NUM, "15WAR00002");
        jCdd2.put(Candidate.CDD_STATUS, "ABSENT");
        jCdd2.put(Candidate.CDD_PAPER, "BAME 0001");
        jCdd2.put(Candidate.CDD_PROG, "RMB3");

        jAttdList.put(jCdd1);
        jAttdList.put(jCdd2);

        jObject.put(JsonHelper.MAJOR_KEY_TYPE_RX, true);
        jObject.put(JsonHelper.MINOR_KEY_CANDIDATES, jAttdList);

        AttendanceList attdList = JsonHelper.parseAttdList(jObject.toString());

        assertNotNull(attdList);
        assertEquals(1, attdList.getNumberOfCandidates(Status.ABSENT));
        assertEquals(1, attdList.getNumberOfCandidates(Status.BARRED));
        assertEquals(0, attdList.getNumberOfCandidates(Status.PRESENT));
        assertEquals(0, attdList.getNumberOfCandidates(Status.EXEMPTED));
        assertEquals(0, attdList.getNumberOfCandidates(Status.QUARANTINED));
    }

    @Test
    public void testParseAttdList_Incorrect_Format_should_throw_FATAL_Exception() throws Exception {
        try{
            JSONObject jObject  = new JSONObject();

            JSONArray jAttdList = new JSONArray();
            JSONObject jCdd1    = new JSONObject();
            JSONObject jCdd2    = new JSONObject();

            jCdd1.put(Candidate.CDD_EXAM_INDEX, "W010AUMB");
            jCdd1.put(Candidate.CDD_REG_NUM, "15WAU00001");
            jCdd1.put(Candidate.CDD_STATUS, "BARRED");
            jCdd1.put(Candidate.CDD_PAPER, "BAME 0001");
            jCdd1.put("X", "RMB3");     //Set the format wrongly
            jCdd2.put(Candidate.CDD_EXAM_INDEX, "W020AUMB");
            jCdd2.put(Candidate.CDD_REG_NUM, "15WAR00002");
            jCdd2.put(Candidate.CDD_STATUS, "ABSENT");
            jCdd2.put(Candidate.CDD_PAPER, "BAME 0001");
            jCdd2.put(Candidate.CDD_PROG, "RMB3");

            jAttdList.put(jCdd1);
            jAttdList.put(jCdd2);

            jObject.put(JsonHelper.MAJOR_KEY_TYPE_RX, true);
            jObject.put(JsonHelper.MINOR_KEY_CANDIDATES, jAttdList);

            AttendanceList attd = JsonHelper.parseAttdList(jObject.toString());

            fail("Expected FATAL_MESSAGE but none thrown!");
        } catch (ProcessException err) {
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("Packet from Chief corrupted\nPlease Consult Developer!",
                    err.getErrorMsg());
        }
    }

    @Test
    public void testParseAttdList_Receiving_False_Result_throw_FATAL() throws Exception {
        try{
            JSONObject jObject  = new JSONObject();
            jObject.put("Result", false);
            JsonHelper.parseAttdList(jObject.toString());

            fail("Expected FATAL_MESSAGE but none were thrown");
        } catch (ProcessException err) {
            assertEquals("Unable to download Attendance List\nPlease retry login", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
        }


    }
    //= ParsePaperMap() ============================================================================

    /**
     * parsePaperMap()
     *
     * 1. If JSON format wrong, throw FATAL_MESSAGE
     * 2. If Result is true, return HashMap<String, ExamSubject> which will be examined
     * 3. If Result is false, throw MESSAGE_DIALOG
     */
    @Test
    public void testParsePaperMap_Wrong_Format_throw_FATAL_MESSAGE() throws Exception {
        try {
            JSONObject object   = new JSONObject();

            JSONArray array     = new JSONArray();
            JSONObject subject1 = new JSONObject();

            subject1.put(ExamSubject.PAPER_CODE, "BAME 0001");
            subject1.put(ExamSubject.PAPER_DESC, "SUBJECT 1");
            subject1.put(ExamSubject.PAPER_START_NO, 1);
            subject1.put(ExamSubject.PAPER_TOTAL_CDD, 10);

            array.put(subject1);

            object.put(JsonHelper.MINOR_KEY_PAPER_MAP, array);    //Missing Result line

            HashMap<String, ExamSubject> paperMap = JsonHelper.parsePaperMap(object.toString());
        } catch (ProcessException err) {
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("Data from Chief corrupted\nPlease Consult Developer",
                    err.getErrorMsg());
        }
    }

    @Test
    public void testParsePaperMap_TrueResult_return_HashMap() throws Exception {
        JSONObject object   = new JSONObject();
        JSONArray array     = new JSONArray();
        JSONObject subject1 = new JSONObject();
        JSONObject subject2 = new JSONObject();

        subject1.put(ExamSubject.PAPER_CODE, "BAME 0001");
        subject1.put(ExamSubject.PAPER_DESC, "SUBJECT 1");
        subject1.put(ExamSubject.PAPER_START_NO, 1);
        subject1.put(ExamSubject.PAPER_TOTAL_CDD, 10);

        subject2.put(ExamSubject.PAPER_CODE, "BAME 0002");
        subject2.put(ExamSubject.PAPER_DESC, "SUBJECT 2");
        subject2.put(ExamSubject.PAPER_START_NO, 11);
        subject2.put(ExamSubject.PAPER_TOTAL_CDD, 20);

        array.put(subject1);
        array.put(subject2);

        object.put(JsonHelper.MAJOR_KEY_TYPE_RX, true);
        object.put(JsonHelper.MINOR_KEY_PAPER_MAP, array);

        HashMap<String, ExamSubject> paperMap = JsonHelper.parsePaperMap(object.toString());

        assertNotNull(paperMap);
        assertEquals(2, paperMap.size());
        assertTrue(paperMap.containsKey("BAME 0001"));
        assertTrue(paperMap.containsKey("BAME 0002"));
    }

    @Test
    public void testParsePaperMap_False_Result_throw_FATAL_MESSAGE() throws Exception {
        try {
            JSONObject object   = new JSONObject();

            object.put("Result", false);
            object.put(JsonHelper.MINOR_KEY_PAPER_MAP, null);    //Missing Result line

            HashMap<String, ExamSubject> paperMap = JsonHelper.parsePaperMap(object.toString());
        } catch (ProcessException err) {
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("Unable to download Exam Paper from Chief\nPlease retry login",
                    err.getErrorMsg());
        }
    }
    //= ParsePaperList =============================================================================

    /**
     *  parsePaperList()
     *
     *  1. If JSON format wrong, throw MESSAGE_DIALOG
     *  2. If Result is true, return List<ExamSubject> which will be examined
     *  3. If Result is false, throw MESSAGE_TOAST
     *
     */
    @Test
    public void testParsePaperList_Wrong_Format_throw_MESSAGE_DIALOG() throws Exception {
        try {
            JSONObject object   = new JSONObject();

            JSONArray array     = new JSONArray();
            JSONObject subject1 = new JSONObject();

            subject1.put(ExamSubject.PAPER_CODE, "BAME 0001");
            subject1.put(ExamSubject.PAPER_DESC, "SUBJECT 1");
            subject1.put(ExamSubject.PAPER_START_NO, 1);
            subject1.put(ExamSubject.PAPER_TOTAL_CDD, 10);

            array.put(subject1);

            object.put("Result", true);
            object.put(JsonHelper.MINOR_KEY_PAPER_MAP, array);

            List<ExamSubject> paperList = JsonHelper.parsePaperList(object.toString());
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_DIALOG, err.getErrorType());
            assertEquals("Data from Chief corrupted\nPlease Consult Developer", err.getErrorMsg());
        }
    }

    @Test
    public void testParsePaperList_True_Result_return_List() throws Exception {
        JSONObject object   = new JSONObject();
        JSONArray array     = new JSONArray();
        JSONObject subject1 = new JSONObject();
        JSONObject subject2 = new JSONObject();

        subject1.put(ExamSubject.PAPER_CODE, "BAME 0001");
        subject1.put(ExamSubject.PAPER_DESC, "SUBJECT 1");
        subject1.put(ExamSubject.PAPER_SESSION, "AM");
        subject1.put(ExamSubject.PAPER_VENUE, "H3");
        subject1.put(ExamSubject.PAPER_DATE, "11/2/2016");

        subject2.put(ExamSubject.PAPER_CODE, "BAME 0002");
        subject2.put(ExamSubject.PAPER_DESC, "SUBJECT 2");
        subject2.put(ExamSubject.PAPER_SESSION, "AM");
        subject2.put(ExamSubject.PAPER_VENUE, "H4");
        subject2.put(ExamSubject.PAPER_DATE, "11/2/2016");

        array.put(subject1);
        array.put(subject2);

        object.put(JsonHelper.MAJOR_KEY_TYPE_RX, true);
        object.put(JsonHelper.MINOR_KEY_PAPER_LIST, array);
        List<ExamSubject> paperList = JsonHelper.parsePaperList(object.toString());

        assertEquals(2, paperList.size());
        assertEquals("BAME 0001", paperList.get(0).getPaperCode());
        assertEquals("BAME 0002", paperList.get(1).getPaperCode());
    }

    @Test
    public void testParsePaperList_False_Result_throw_MESSAGE_TOAST() throws Exception {
        try {
            JSONObject object   = new JSONObject();

            object.put("Result", false);
            object.put(JsonHelper.MINOR_KEY_PAPER_LIST, null);

            List<ExamSubject> paperList = JsonHelper.parsePaperList(object.toString());
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Not a Candidate Identity", err.getErrorMsg());
        }
    }

    //= FormatPassword() ===========================================================================
    /**
     *  formatStaff(String id, String password)
     *
     *  this method create a JSON Object of id and password
     *  then return the JSON Object in the format of String
     *
     */
    @Test
    public void testFormatPassword() throws Exception {
        String str = JsonHelper.formatStaff("246800", "0123");

        assertEquals("{\"Type\":\"Identification\",\"HashPass\":\"0123\",\"IdNo\":\"246800\"}", str);

        JSONObject obj = new JSONObject(str);
        assertNotNull(obj);
        assertEquals("Identification", obj.getString("Type"));
        assertEquals("246800", obj.getString("IdNo"));
        assertEquals("0123", obj.getString("HashPass"));
    }

    //= ParseType() ================================================================================
    /**
     * parseType()
     *
     * This method use to check the type of the message received
     *
     * Tests:
     * 1. There is a Type key, return its value
     * 2. The message does not have a Type key, throw FATAL
     *
     */
    @Test
    public void testParseType1_withCorrectType() throws Exception {
        assertEquals("Collection", JsonHelper.parseType("{\"Type\":\"Collection\"}"));
    }

    @Test
    public void testParseType2_withError() throws Exception {
        try{
            JsonHelper.parseType("{}");
        } catch (ProcessException err){
            assertEquals("FATAL: Data from Chief corrupted\n" +
                    "Please consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
        }
    }

    //= ParseBoolean() =============================================================================
    /**
     *  parseBoolean(String str)
     *
     *  parse str to get a boolean of true or false
     *  1. If the result is true, return true
     *  2. If the result is false, throw MESSAGE_DIALOG
     *  3. If the result have wrong format, throw FATAL
     *
     */
    @Test
    public void testParseBoolean_withTrueAcknowledgement() throws Exception {
        assertTrue(JsonHelper.parseBoolean("{\"Result\":true}"));
    }

    @Test
    public void testParseBoolean_FalseAcknowledgement_should_throw_FATAL() throws Exception {
        try{
            assertFalse(JsonHelper.parseBoolean("{\"Result\":false}"));
        } catch (ProcessException err){
            assertEquals("Request Failed", err.getErrorMsg());
            assertEquals(ProcessException.MESSAGE_DIALOG, err.getErrorType());
        }
    }

    @Test
    public void testParseBoolean_ReceivingWrongFormat_should_throw_FATAL() throws Exception {
        try{
            assertFalse(JsonHelper.parseBoolean("{}"));
        } catch (ProcessException err){
            assertEquals("FATAL: Data from Chief corrupted\n" +
                    "Please consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
        }
    }

    //= ParseChallengeMessage(...) =================================================================

    /**
     * parseChallengeMessage(...)
     *
     *  parse str to get a boolean of true or false
     *
     *  Tests:
     *  1. If the result is true, return true
     *  2. If the result is false, throw MESSAGE_DIALOG
     *  3. If the result have wrong format, throw FATAL
     *
     */

    @Test
    public void testParseChallengeMessage1_TrueResult() throws Exception {
        try{
            String test = JsonHelper.parseChallengeMessage(
                    "{\"Result\":true, \"DuelMsg\":\"xAsdfgF4560~l\"}");

            assertEquals("xAsdfgF4560~l", test);

        } catch (ProcessException err){
            fail("Exception --" + err.getErrorMsg() + "-- is not expected");
        }
    }

    @Test
    public void testParseChallengeMessage2_FalseResult() throws Exception {
        try{
            JsonHelper.parseChallengeMessage("{\"Result\":false}");
        } catch (ProcessException err){
            assertEquals("Chief denied reconnection.\n" +
                    "Please connect to chief with QR first", err.getErrorMsg());
            assertEquals(ProcessException.MESSAGE_DIALOG, err.getErrorType());
        }
    }

    @Test
    public void testParseChallengeMessage3_UnreadableResult() throws Exception {
        try{
            JsonHelper.parseChallengeMessage("{\"Result\":true}");
        } catch (ProcessException err){
            assertEquals("Failed to read data from Chief\n" +
                    "Please consult developer!", err.getErrorMsg());
            assertEquals(ProcessException.MESSAGE_DIALOG, err.getErrorType());
        }
    }



}