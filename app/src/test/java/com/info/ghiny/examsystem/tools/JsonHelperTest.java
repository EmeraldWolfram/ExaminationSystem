package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.adapter.ExamSubjectAdapter;
import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.LocalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
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

    @Before
    public void setUp() throws Exception {

    }

    //= FormatString() =============================================================================
    /**
     *  formatString(String type, String valueStr)
     *
     *  this method create a JSON Object and return the JSON Object in the format of String
     *
     *  @param type         The type of query message to be send. Eg. TYPE_IDENTITY
     *  @param valueStr     The value to be send across
     */
    @Test
    public void testFormatString() throws Exception {
        String str = JsonHelper.formatString(JsonHelper.TYPE_ATTD_LIST, "H4");

        JSONObject obj = new JSONObject(str);
        assertTrue(obj.has("CheckIn"));
        assertEquals("AttdList", obj.getString("CheckIn"));
        assertEquals("H4", obj.getString("Value"));
    }

    //= FormatAttdList() ===========================================================================

    /**
     * formatAttdList(AttdList)
     *
     * Send out the whole AttendanceList
     * @param attdList  The updated attendance list that have the candidates attendance taken
     */
    @Test
    public void testFormatAttdList() throws Exception {
        AttendanceList attdList = new AttendanceList();
        Candidate cdd1 = new Candidate(1, "RMB3", "FGY", "15WAU00001", "BAME 0001", AttendanceList.Status.ABSENT);
        Candidate cdd2 = new Candidate(1, "RMB3", "NYN", "15WAU00002", "BAME 0001", AttendanceList.Status.ABSENT);
        Candidate cdd3 = new Candidate(1, "RMB3", "LHN", "15WAU00003", "BAME 0001", AttendanceList.Status.ABSENT);
        Candidate cdd4 = new Candidate(1, "RMB3", "Mr. Bar", "15WAU00004", "BAME 0002", AttendanceList.Status.BARRED);
        Candidate cdd5 = new Candidate(1, "RMB3", "Ms. Exm", "15WAU00005", "BAME 0003", AttendanceList.Status.EXEMPTED);
        Candidate cdd6 = new Candidate(1, "RMB3", "Ms. Qua", "15WAR00006", "BAME 0001", AttendanceList.Status.QUARANTIZED);

        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), cdd2.getStatus(), cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), cdd3.getStatus(), cdd3.getProgramme());
        attdList.addCandidate(cdd4, cdd4.getPaperCode(), cdd4.getStatus(), cdd4.getProgramme());
        attdList.addCandidate(cdd5, cdd5.getPaperCode(), cdd5.getStatus(), cdd5.getProgramme());
        attdList.addCandidate(cdd6, cdd6.getPaperCode(), cdd6.getStatus(), cdd6.getProgramme());

        LoginHelper.setStaff(new StaffIdentity("246260", true, "Dr. Smart", "H1"));

        String str = JsonHelper.formatAttdList(attdList);

        JSONObject obj = new JSONObject(str);
        assertEquals("AttdList", obj.getString("CheckIn"));
        assertEquals("H1", obj.getString("Venue"));
        assertEquals("246260", obj.getString("In-Charge"));
        assertEquals(6, obj.getInt("Size"));

        JSONArray jArr = obj.getJSONArray("CddList");
    }

    //= FormatCollection() =========================================================================
    /**
     *  formatCollection(scanBundleStr)
     *
     *  return a JSON Object in string to acknowledge the collection of the bundle
     *  @param scanBundleStr    The QR code on top of the bundle that represent the bundle
     */
    @Test
    public void testFormatCollection() throws Exception {
        LoginHelper.setStaff(new StaffIdentity("246260", true, "Dr. Smart", null));
        String str = JsonHelper.formatCollection("BAME 0001 SUBJECT 1");

        assertEquals("{\"Bundle\":\"BAME 0001 SUBJECT 1\",\"Collector\":\"246260\"," +
                "\"CheckIn\":\"Collection\"}", str);
    }

    //= ParseStaffIdentity() =======================================================================
    /**
     *  parseStaffIdentity()
     *
     *  parse str to get the object of StaffIdentity
     *  @param str  The input message received from sockets
     */
    @Test
    public void testParseStaffIdentity() throws Exception {
        LoginHelper.setStaff(new StaffIdentity());

        JsonHelper.parseStaffIdentity(
                 "{\"Status\":[\"Collector\",\"Chief\"]," +
                         "\"Venue\":\"M5\",\"Result\":true," +
                         "\"Name\":\"TESTER 1\"," +
                         "\"CddList\":[],\"PaperMap\":[]}");


        StaffIdentity staff = LoginHelper.getStaff();
        assertNotNull(staff);
        assertEquals("TESTER 1", staff.getName());
        assertEquals("M5", staff.getVenueHandling());
    }

    /**
     *  parseStaffIdentity()
     *
     *  parse str to get the object of StaffIdentity
     *  @param str  The input message received from sockets
     */
    @Test
    public void testParseStaffIdentity_IncorrectFormat_Should_throw_FATAL() throws Exception {
        try{
            LoginHelper.setStaff(new StaffIdentity());
            JsonHelper.parseStaffIdentity(
                    "{\"Venue\":\"M5\"," +
                            "\"Eligible\":true," +
                            "\"RegNum\":\"246800\"," +
                            "\"NAME\":\"TESTER 1\"," +  //Name not NAME
                            "\"Password\":\"0123\"}");

            fail("Expected FATAL_MESSAGE but none thrown");
        } catch (ProcessException err){
            assertEquals("Failed to query data from Chief\nPlease consult developer!", err.getErrorMsg());
            assertEquals(ProcessException.MESSAGE_DIALOG, err.getErrorType());
        }

    }

    //= ParseAttdList() ============================================================================

    /**
     * parseAttdList(String inStr)
     *
     * Test on transforming a the String back into an AttendanceList;
     * @param inStr     JSON formatted AttendanceList in a String
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

        jObject.put(JsonHelper.KEY_TYPE_RETURN, true);
        jObject.put(JsonHelper.LIST_LIST, jAttdList);

        JsonHelper.parseAttdList(jObject.toString());
        AttendanceList attdList = AssignHelper.getAttdList();

        assertNotNull(attdList);
        assertEquals(1, attdList.getNumberOfCandidates(AttendanceList.Status.ABSENT));
        assertEquals(1, attdList.getNumberOfCandidates(AttendanceList.Status.BARRED));
        assertEquals(0, attdList.getNumberOfCandidates(AttendanceList.Status.PRESENT));
        assertEquals(0, attdList.getNumberOfCandidates(AttendanceList.Status.EXEMPTED));
        assertEquals(0, attdList.getNumberOfCandidates(AttendanceList.Status.QUARANTIZED));
    }

    /**
     * parseAttdList(String inStr)
     *
     * Return null if any of the parameter was wrong
     * @param inStr     JSON formatted AttendanceList in a String
     */
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

            jObject.put(JsonHelper.KEY_TYPE_RETURN, true);
            jObject.put(JsonHelper.LIST_LIST, jAttdList);

            JsonHelper.parseAttdList(jObject.toString());

            fail("Expected FATAL_MESSAGE but none thrown!");
        } catch (ProcessException err) {
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("FATAL: JSONObject[\"Programme\"] not found.\nPlease Consult Developer!",
                    err.getErrorMsg());
        }
    }

    /**
     * parseAttdList(String inStr)
     *
     * Return null if str pass in is empty
     * @param inStr     JSON formatted AttendanceList in a String
     */
    @Test
    public void testParseAttdList_Receiving_Empty_JSONObject_thrown_FATAL() throws Exception {
        try{
            JSONObject jObject  = new JSONObject();
            JsonHelper.parseAttdList(jObject.toString());

            fail("Expected FATAL_MESSAGE but none were thrown");
        } catch (ProcessException err) {
            assertEquals("FATAL: JSONObject[\"Result\"] not found.\nPlease Consult Developer!",
                    err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
        }


    }
    //= ParsePaperMap() ============================================================================

    /**
     * parsePaperMap()
     *
     * This method return a HashMap<String, ExamSubject> which will be examined
     * in that particular venue for that particular session
     */
    @Test
    public void testParsePaperMap() throws Exception {
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

        object.put(JsonHelper.KEY_TYPE_RETURN, true);
        object.put(JsonHelper.PAPER_MAP, array);

        JsonHelper.parsePaperMap(object.toString());
        HashMap<String, ExamSubject> paperMap = Candidate.getPaperList();

        assertNotNull(paperMap);
        assertEquals(2, paperMap.size());
        assertTrue(paperMap.containsKey("BAME 0001"));
        assertTrue(paperMap.containsKey("BAME 0002"));
    }

    //= ParsePaperList =============================================================================

    /**
     *  parsePaperList()
     *
     *  This method return the papers examine by the candidate for that session of examination
     */
    @Test
    public void testParsePaperList() throws Exception {
        ObtainInfoHelper.setAdapter(new ExamSubjectAdapter());
        JSONObject object   = new JSONObject();
        JSONArray array     = new JSONArray();
        JSONObject subject1 = new JSONObject();
        JSONObject subject2 = new JSONObject();

        subject1.put(ExamSubject.PAPER_CODE, "BAME 0001");
        subject1.put(ExamSubject.PAPER_DESC, "SUBJECT 1");
        subject1.put(ExamSubject.PAPER_SESSION, "AM");
        subject1.put(ExamSubject.PAPER_VENUE, "H3");

        subject2.put(ExamSubject.PAPER_CODE, "BAME 0002");
        subject2.put(ExamSubject.PAPER_DESC, "SUBJECT 2");
        subject2.put(ExamSubject.PAPER_SESSION, "AM");
        subject2.put(ExamSubject.PAPER_VENUE, "H4");

        array.put(subject1);
        array.put(subject2);

        object.put(JsonHelper.KEY_TYPE_RETURN, true);
        object.put(JsonHelper.PAPER_LIST, array);
        JsonHelper.parsePaperList(object.toString());

        assertFalse(ExamSubjectAdapter.papersIsEmpty());
    }

    //= FormatPassword() ===========================================================================
    /**
     *  formatPassword(String id, String password)
     *
     *  this method create a JSON Object of id and password
     *  then return the JSON Object in the format of String
     *
     *  @param id         The idNo of the staff scanned
     *  @param password   The password entered
     */
    @Test
    public void testFormatPassword() throws Exception {
        String str = JsonHelper.formatPassword("246800", "0123");

        assertEquals("{\"CheckIn\":\"Identity\",\"IdNo\":\"246800\",\"Password\":\"0123\"}", str);

        JSONObject obj = new JSONObject(str);
        assertNotNull(obj);
        assertEquals("Identity", obj.getString("CheckIn"));
        assertEquals("246800", obj.getString("IdNo"));
        assertEquals("0123", obj.getString("Password"));
    }

    //= ParseBoolean() =============================================================================
    /**
     *  parseBoolean(String str)
     *
     *  parse str to get a boolean of true or false
     *  default to false
     *
     *  @param str  The input message received from sockets
     */
    @Test
    public void testParseBoolean() throws Exception {
        JsonHelper.parseBoolean("{\"Result\":true}");

        assertNotNull(ChiefLink.isComplete());
        assertTrue(ChiefLink.isMsgValid());
    }
}