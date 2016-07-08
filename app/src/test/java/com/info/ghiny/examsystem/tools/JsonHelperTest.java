package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.StaffIdentity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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
        assertEquals("{\"Type\":\"AttdList\","
                + "\"Venue\":\"H1\",\"In-Charge\":\"246260\",\"Size\":6,\"CddList\":["
                + "{\"ExamIndex\":\"NYN\",\"Status\":\"ABSENT\",\"Table\":\"1\"},"
                + "{\"ExamIndex\":\"FGY\",\"Status\":\"ABSENT\",\"Table\":\"1\"},"
                + "{\"ExamIndex\":\"LHN\",\"Status\":\"ABSENT\",\"Table\":\"1\"},"
                + "{\"ExamIndex\":\"Ms. Qua\",\"Status\":\"QUARANTIZED\",\"Table\":\"1\"},"
                + "{\"ExamIndex\":\"Mr. Bar\",\"Status\":\"BARRED\",\"Table\":\"1\"},"
                + "{\"ExamIndex\":\"Ms. Exm\",\"Status\":\"EXEMPTED\",\"Table\":\"1\"}]}", str);

        JSONObject obj = new JSONObject(str);
        assertEquals("AttdList", obj.getString("Type"));
        assertEquals("H1", obj.getString("Venue"));
        assertEquals("246260", obj.getString("In-Charge"));
        assertEquals(6, obj.getInt("Size"));

        JSONArray jArr = obj.getJSONArray("CddList");
        assertEquals("NYN",     jArr.getJSONObject(0).getString("ExamIndex"));
        assertEquals("FGY",     jArr.getJSONObject(1).getString("ExamIndex"));
        assertEquals("LHN",     jArr.getJSONObject(2).getString("ExamIndex"));
        assertEquals("Ms. Qua", jArr.getJSONObject(3).getString("ExamIndex"));
        assertEquals("Mr. Bar", jArr.getJSONObject(4).getString("ExamIndex"));
        assertEquals("Ms. Exm", jArr.getJSONObject(5).getString("ExamIndex"));
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
                "\"RegNum\":\"246800\"," +
                "\"Name\":\"TESTER 1\"," +
                "\"Password\":\"0123\"}");

        assertNotNull(staff);
        assertEquals("TESTER 1", staff.getName());
        assertEquals("M5", staff.getVenueHandling());
        assertEquals("246800", staff.getRegNum());
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

    //==============================================================================================
    @Test
    public void testParseAttdList() throws Exception {

    }

    @Test
    public void testParsePaperMap() throws Exception {

    }

    @Test
    public void testParsePaperList() throws Exception {

    }
}