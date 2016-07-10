package com.info.ghiny.examsystem.database;

import com.info.ghiny.examsystem.tools.ChiefLink;
import com.info.ghiny.examsystem.tools.JsonHelper;
import com.info.ghiny.examsystem.tools.LoginHelper;
import com.info.ghiny.examsystem.tools.TCPClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by GhinY on 10/07/2016.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JsonHelper.class, ChiefLink.class, TCPClient.class})
public class ExternalDbLoaderTest {
    StaffIdentity staff;
    TCPClient tcpClient;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(JsonHelper.class);
        PowerMockito.mockStatic(ChiefLink.class);
        tcpClient = PowerMockito.mock(TCPClient.class);
        ExternalDbLoader.setTcpClient(tcpClient);
        staff   = new StaffIdentity("246800", true, "Dr. TDD", "H3");
    }

    //= GetStaffIdentity() =========================================================================

    /**
     * getStaffIdentity(String scanId)
     *
     * send out Id Number scanned through socket
     * wait for a JSON file
     * parse the JSON file to a StaffIdentity object
     *
     * return as a StaffIdentity
     * return null if anything goes wrong
     *
     * @param scanId    the Id Number scanned
     */
    @Test
    public void testGetStaffIdentity() throws Exception {
        when(JsonHelper.formatString(JsonHelper.TYPE_Q_IDENTITY, "246800")).thenReturn("Json Id");
        doNothing().when(tcpClient).sendMessage("Json Id");
        when(ChiefLink.isMsgReadyFlag()).thenReturn(false).thenReturn(true);
        when(ChiefLink.getMsgReceived()).thenReturn("Staff in Json");
        when(JsonHelper.parseStaffIdentity("Staff in Json")).thenReturn(staff);

        StaffIdentity id = ExternalDbLoader.getStaffIdentity("246800");

        assertEquals(staff, id);
    }

    @Test
    public void testGetStaffIdentity_returnNullWhenParserReturnNull() throws Exception {
        when(JsonHelper.formatString(JsonHelper.TYPE_Q_IDENTITY, "246800")).thenReturn("Json Id");
        doNothing().when(tcpClient).sendMessage("Json Id");
        when(ChiefLink.isMsgReadyFlag()).thenReturn(false).thenReturn(true);
        when(ChiefLink.getMsgReceived()).thenReturn("Something Wrong");
        when(JsonHelper.parseStaffIdentity("Something Wrong")).thenReturn(null);

        StaffIdentity id = ExternalDbLoader.getStaffIdentity("246800");

        assertNull(id);
    }
    //= MatchPassword() =========================================================================

    /**
     * matchPassword(String id, String pw)
     *
     * send out staff Id Number and the input password through socket
     * wait for a JSON file
     * parse the JSON file to true or false
     *
     * return the parse result
     *
     * @param id    The id number of the staff
     * @param pw    The password entered by the staff
     */
    @Test
    public void testMatchPassword() throws Exception {
        when(JsonHelper.formatPassword("246800", "0123")).thenReturn("Json Id & Password");
        doNothing().when(tcpClient).sendMessage("Json Id & Password");
        when(ChiefLink.isMsgReadyFlag()).thenReturn(false).thenReturn(true).thenReturn(true);
        when(ChiefLink.getMsgReceived()).thenReturn("true").thenReturn("false");

        when(JsonHelper.parseBoolean("true")).thenReturn(true);
        boolean isCorrect = ExternalDbLoader.matchPassword("246800", "0123");
        assertTrue(isCorrect);

        when(JsonHelper.parseBoolean("false")).thenReturn(false);
        isCorrect   = ExternalDbLoader.matchPassword("246800", "0123");
        assertFalse(isCorrect);
    }

    //= DlAttdList() ===============================================================================

    /**
     * dlAttdList(String venue)
     *
     * format venue into JSON Object
     * Send out the JSON Object
     * Wait for JSON Object
     * Parse to AttendanceList format
     * return as AttendanceList
     *
     * @param venue     Venue of the staff handling in String. Eg. "H3"
     */
    @Test
    public void testDlAttdList() throws Exception {
        LoginHelper.setStaff(staff);    //To set the venue to H3

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

        when(JsonHelper.formatString(JsonHelper.TYPE_Q_ATTD_VENUE, "H3")).thenReturn("Json H3");
        doNothing().when(tcpClient).sendMessage("Json H3");
        when(ChiefLink.isMsgReadyFlag()).thenReturn(false).thenReturn(true);
        when(ChiefLink.getMsgReceived()).thenReturn("Json AttdList");
        when(JsonHelper.parseAttdList("Json AttdList")).thenReturn(attdList);

        AttendanceList returnList = ExternalDbLoader.dlAttdList();

        assertEquals(attdList, returnList);
    }
    //= DlAttdList() ===============================================================================

    /**
     * dlAttdList(String venue)
     *
     * format venue into JSON Object
     * Send out the JSON Object
     * Wait for JSON Object
     * Parse to AttendanceList format
     * return as AttendanceList
     *
     * @param venue     Venue of the staff handling in String. Eg. "H3"
     */
    @Test
    public void testDlPaperList() throws Exception {
        LoginHelper.setStaff(staff);    //Set the venue to H3

        HashMap<String, ExamSubject> paperList   = new HashMap<>();
        ExamSubject subject1    = new ExamSubject("BAME 0001", "SUBJECT 1", 10,
                Calendar.getInstance(), 20, "H1", ExamSubject.Session.AM);
        ExamSubject subject2    = new ExamSubject("BAME 0002", "SUBJECT 2", 30,
                Calendar.getInstance(), 20, "H2", ExamSubject.Session.PM);
        ExamSubject subject3    = new ExamSubject("BAME 0003", "SUBJECT 3", 50,
                Calendar.getInstance(), 20, "H3", ExamSubject.Session.VM);
        paperList.put(subject1.getPaperCode(), subject1);
        paperList.put(subject2.getPaperCode(), subject2);
        paperList.put(subject3.getPaperCode(), subject3);

        when(JsonHelper.formatString(JsonHelper.TYPE_Q_PAPERS_VENUE, "H3")).thenReturn("Json H3");
        doNothing().when(tcpClient).sendMessage("Json H3");
        when(ChiefLink.isMsgReadyFlag()).thenReturn(false).thenReturn(true);
        when(ChiefLink.getMsgReceived()).thenReturn("Json PaperMap");
        when(JsonHelper.parsePaperMap("Json PaperMap")).thenReturn(paperList);

        HashMap<String, ExamSubject> paperMap = ExternalDbLoader.dlPaperList();

        assertEquals(paperList, paperMap);
    }

    //= GetPapersExamineByCdd() ====================================================================

    /**
     *  getPapersExamnineByCdd(String regNum)
     *
     *  format venue
     *
     *  @param regNum   Register Number of the Candidate
     */
    @Test
    public void testGetPapersExamineByCdd() throws Exception {
        List<ExamSubject> paperList   = new ArrayList<>();
        ExamSubject subject1    = new ExamSubject("BAME 0001", "SUBJECT 1", 10,
                Calendar.getInstance(), 20, "H1", ExamSubject.Session.AM);
        ExamSubject subject2    = new ExamSubject("BAME 0002", "SUBJECT 2", 30,
                Calendar.getInstance(), 20, "H2", ExamSubject.Session.PM);
        ExamSubject subject3    = new ExamSubject("BAME 0003", "SUBJECT 3", 50,
                Calendar.getInstance(), 20, "H3", ExamSubject.Session.VM);
        paperList.add(subject1);
        paperList.add(subject2);
        paperList.add(subject3);

        when(JsonHelper.formatString(JsonHelper.TYPE_Q_PAPERS_CDD, "15WAU00001")).thenReturn("Json Cdd");
        doNothing().when(tcpClient).sendMessage("Json Cdd");
        when(ChiefLink.isMsgReadyFlag()).thenReturn(false).thenReturn(true);
        when(ChiefLink.getMsgReceived()).thenReturn("Json PaperExamine");
        when(JsonHelper.parsePaperList("Json PaperExamine")).thenReturn(paperList);

        List<ExamSubject> subjects = ExternalDbLoader.getPapersExamineByCdd("15WAU00001");

        assertEquals(paperList, subjects);
    }

    //= UpdateAttdList() ===========================================================================

    /**
     * updateAttdList(AttendanceList attdList)
     *
     * format the updated attdList into a JSONObject containing a JSON Array of Candidates
     * send out the JSON Object in string format
     *
     * return true when the chief acknowledge the receive
     * return false when the chief found error or times out
     *
     * @param attdList  the updated AttendanceList to be send out
     */
    @Test
    public void testUpdateAttdList() throws Exception {
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

        when(JsonHelper.formatAttdList(attdList)).thenReturn("Json AttdList");
        doNothing().when(tcpClient).sendMessage("Json AttdList");
        when(ChiefLink.isMsgReadyFlag()).thenReturn(true);

        when(ChiefLink.getMsgReceived()).thenReturn("Json true");
        when(JsonHelper.parseBoolean("Json true")).thenReturn(true);
        boolean received = ExternalDbLoader.updateAttdList(attdList);
        assertTrue(received);

        when(ChiefLink.getMsgReceived()).thenReturn("Json false");
        when(JsonHelper.parseBoolean("Json false")).thenReturn(false);
        received    = ExternalDbLoader.updateAttdList(attdList);
        assertFalse(received);
    }

    //= AcknowledgeCollection() ====================================================================

    /**
     *  acknowledgeCollection(String bundle)
     *
     *  Send out the bundle and collector id in JSON Format
     *  wait for Acknowledgement from Chief
     *
     *  return true when the Chief acknowledge updated
     *  return false when the Chief found error or times out
     *
     *  @param bundle   The QR code on the bundle
     */
    @Test
    public void testAcknowledgeCollection() throws Exception {
        String bundle = "BAME 0001 Subject 1 -----";

        when(JsonHelper.formatCollection(bundle)).thenReturn("Json Bundle");
        doNothing().when(tcpClient).sendMessage("Json Bundle");
        when(ChiefLink.isMsgReadyFlag()).thenReturn(true);

        when(ChiefLink.getMsgReceived()).thenReturn("Json true");
        when(JsonHelper.parseBoolean("Json true")).thenReturn(true);
        boolean updated = ExternalDbLoader.acknowledgeCollection(bundle);
        assertTrue(updated);

        when(ChiefLink.getMsgReceived()).thenReturn("Json false");
        when(JsonHelper.parseBoolean("Json false")).thenReturn(false);
        updated    = ExternalDbLoader.acknowledgeCollection(bundle);
        assertFalse(updated);
    }
}