package com.info.ghiny.examsystem.tools;

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
        String str = JsonHelper.formatString(JsonHelper.TYPE_IDENTITY, "246800");

        assertEquals("{\"Type\":\"Identity\",\"Value\":\"246800\"}", str);

        JSONObject obj = new JSONObject(str);
        assertEquals("Identity", obj.getString("Type"));
        assertEquals("246800", obj.getString("Value"));
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

        LoginHelper.setStaff(new StaffIdentity("246260", "0123", true, "Dr. Smart", "H1"));

        String str = JsonHelper.formatAttdList(attdList);

        JSONObject obj = new JSONObject(str);
        assertEquals("AttdList", obj.getString("Type"));
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
        LoginHelper.setStaff(new StaffIdentity("246260", "0123", true, "Dr. Smart", null));
        String str = JsonHelper.formatCollection("BAME 0001 SUBJECT 1");

        assertEquals("{\"Type\":\"Collection\",\"Collector\":\"246260\"," +
                "\"BundleCode\":\"BAME 0001 SUBJECT 1\"}", str);
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
        StaffIdentity staff = JsonHelper.parseStaffIdentity(
                "{\"Venue\":\"M5\"," +
                "\"Eligible\":true," +
                "\"IdNo\":\"246800\"," +
                "\"Name\":\"TESTER 1\"," +
                "\"Password\":\"0123\"}");

        assertNotNull(staff);
        assertEquals("TESTER 1", staff.getName());
        assertEquals("M5", staff.getVenueHandling());
        assertEquals("246800", staff.getIdNo());
        assertTrue(staff.matchPassword("0123"));
    }

    /**
     *  parseStaffIdentity()
     *
     *  parse str to get the object of StaffIdentity
     *  @param str  The input message received from sockets
     */
    @Test
    public void testParseStaffIdentity_return_null_if_format_incorrect() throws Exception {
        StaffIdentity staff = JsonHelper.parseStaffIdentity(
                "{\"Venue\":\"M5\"," +
                        "\"Eligible\":true," +
                        "\"RegNum\":\"246800\"," +
                        "\"NAME\":\"TESTER 1\"," +  //Name not NAME
                        "\"Password\":\"0123\"}");

        assertNull(staff);
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

        jCdd1.put(LocalDbLoader.TABLE_INFO_COLUMN_INDEX, "W010AUMB");
        jCdd1.put(LocalDbLoader.TABLE_INFO_COLUMN_REGNUM, "15WAU00001");
        jCdd1.put(LocalDbLoader.TABLE_INFO_COLUMN_STATUS, "BARRED");
        jCdd1.put(LocalDbLoader.TABLE_INFO_COLUMN_CODE, "BAME 0001");
        jCdd1.put(LocalDbLoader.TABLE_INFO_COLUMN_PRG, "RMB3");

        jCdd2.put(LocalDbLoader.TABLE_INFO_COLUMN_INDEX, "W020AUMB");
        jCdd2.put(LocalDbLoader.TABLE_INFO_COLUMN_REGNUM, "15WAR00002");
        jCdd2.put(LocalDbLoader.TABLE_INFO_COLUMN_STATUS, "ABSENT");
        jCdd2.put(LocalDbLoader.TABLE_INFO_COLUMN_CODE, "BAME 0001");
        jCdd2.put(LocalDbLoader.TABLE_INFO_COLUMN_PRG, "RMB3");

        jAttdList.put(jCdd1);
        jAttdList.put(jCdd2);

        jObject.put(JsonHelper.LIST_LIST, jAttdList);

        AttendanceList attdList = JsonHelper.parseAttdList(jObject.toString());

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
    public void testParseAttdList_returnNullUponWrongFormat() throws Exception {
        JSONObject jObject  = new JSONObject();
        JSONArray jAttdList = new JSONArray();
        JSONObject jCdd1    = new JSONObject();
        JSONObject jCdd2    = new JSONObject();

        jCdd1.put(LocalDbLoader.TABLE_INFO_COLUMN_INDEX, "W010AUMB");
        jCdd1.put(LocalDbLoader.TABLE_INFO_COLUMN_REGNUM, "15WAU00001");
        jCdd1.put(LocalDbLoader.TABLE_INFO_COLUMN_STATUS, "BARRED");
        jCdd1.put(LocalDbLoader.TABLE_INFO_COLUMN_CODE, "BAME 0001");
        jCdd1.put("X", "RMB3");

        jCdd2.put(LocalDbLoader.TABLE_INFO_COLUMN_INDEX, "W020AUMB");
        jCdd2.put(LocalDbLoader.TABLE_INFO_COLUMN_REGNUM, "15WAR00002");
        jCdd2.put(LocalDbLoader.TABLE_INFO_COLUMN_STATUS, "ABSENT");
        jCdd2.put(LocalDbLoader.TABLE_INFO_COLUMN_CODE, "BAME 0001");
        jCdd2.put(LocalDbLoader.TABLE_INFO_COLUMN_PRG, "RMB3");

        jAttdList.put(jCdd1);
        jAttdList.put(jCdd2);

        jObject.put(JsonHelper.LIST_LIST, jAttdList);

        AttendanceList attdList = JsonHelper.parseAttdList(jObject.toString());

        assertNull(attdList);
    }

    /**
     * parseAttdList(String inStr)
     *
     * Return null if str pass in is empty
     * @param inStr     JSON formatted AttendanceList in a String
     */
    @Test
    public void testParseAttdList_returnNullEmptyJSONObject() throws Exception {
        JSONObject jObject  = new JSONObject();

        AttendanceList attdList = JsonHelper.parseAttdList(jObject.toString());

        assertNull(attdList);
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

        subject1.put(LocalDbLoader.PAPER_CODE, "BAME 0001");
        subject1.put(LocalDbLoader.PAPER_DESC, "SUBJECT 1");
        subject1.put(LocalDbLoader.PAPER_START_NO, 1);
        subject1.put(LocalDbLoader.PAPER_TOTAL_CDD, 10);

        subject2.put(LocalDbLoader.PAPER_CODE, "BAME 0002");
        subject2.put(LocalDbLoader.PAPER_DESC, "SUBJECT 2");
        subject2.put(LocalDbLoader.PAPER_START_NO, 11);
        subject2.put(LocalDbLoader.PAPER_TOTAL_CDD, 20);

        array.put(subject1);
        array.put(subject2);

        object.put(JsonHelper.PAPER_MAP, array);

        HashMap<String, ExamSubject> paperMap = JsonHelper.parsePaperMap(object.toString());

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
        JSONObject object   = new JSONObject();
        JSONArray array     = new JSONArray();
        JSONObject subject1 = new JSONObject();
        JSONObject subject2 = new JSONObject();

        subject1.put(LocalDbLoader.PAPER_CODE, "BAME 0001");
        subject1.put(LocalDbLoader.PAPER_DESC, "SUBJECT 1");
        subject1.put(ExamSubject.PAPER_SESSION, "AM");
        subject1.put(ExamSubject.PAPER_VENUE, "H3");

        subject2.put(LocalDbLoader.PAPER_CODE, "BAME 0002");
        subject2.put(LocalDbLoader.PAPER_DESC, "SUBJECT 2");
        subject2.put(ExamSubject.PAPER_SESSION, "AM");
        subject2.put(ExamSubject.PAPER_VENUE, "H4");

        array.put(subject1);
        array.put(subject2);

        object.put(JsonHelper.PAPER_LIST, array);

        List<ExamSubject> subjects = JsonHelper.parsePaperList(object.toString());

        assertNotNull(subjects);
        assertEquals(2, subjects.size());
        assertEquals("BAME 0001", subjects.get(0).getPaperCode());
        assertEquals("BAME 0002", subjects.get(1).getPaperCode());
    }
}