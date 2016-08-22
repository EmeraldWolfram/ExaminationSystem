package com.info.ghiny.examsystem.database;

import com.info.ghiny.examsystem.model.ChiefLink;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.LoginHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import java.util.Calendar;
import java.util.HashMap;

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
    /*@Test
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
    }*/
    //= TryLogin() =========================================================================

    /**
     * tryLogin(String id, String pw)
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
    public void testTryLogin() throws Exception {
        try{
            when(JsonHelper.formatPassword("246800", "0123")).thenReturn("Json Id & Password");
            doNothing().when(tcpClient).sendMessage("Json Id & Password");
            ExternalDbLoader.tryLogin("246800", "0123");
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testTryLogin_NullTCPShouldThrowFatalMessage() throws Exception {
        try{
            ExternalDbLoader.setTcpClient(null);
            when(JsonHelper.formatPassword("246800", "0123")).thenReturn("Json Id & Password");
            doNothing().when(tcpClient).sendMessage("Json Id & Password");
            ExternalDbLoader.tryLogin("246800", "0123");
        } catch (ProcessException err){
            assertEquals("FATAL: Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
        }
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
/*        LoginHelper.setStaff(staff);    //To set the venue to H3
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

        when(JsonHelper.formatString(JsonHelper.TYPE_ATTD_LIST, "H3")).thenReturn("Json H3");
        doNothing().when(tcpClient).sendMessage("Json H3");

        ExternalDbLoader.dlAttdList();
*/
    }
    //= DlPaperList() ===============================================================================

    /**
     * dlPaperList(String venue)
     *
     * format venue into JSON Object
     * Send out the JSON Object
     * Wait for JSON Object
     * Parse to HashMap<String, ExamSubject> format
     * return as HashMap<String, ExamSubject>
     *
     * @param venue     Venue of the staff handling in String. Eg. "H3"
     */
    @Test
    public void testDlPaperList() throws Exception {
        LoginHelper.setStaff(staff);    //Set the venue to H3

        HashMap<String, ExamSubject> paperList   = new HashMap<>();
        ExamSubject subject1    = new ExamSubject("BAME 0001", "SUBJECT 1", 10,
                Calendar.getInstance(), 20, "H1", Session.AM);
        ExamSubject subject2    = new ExamSubject("BAME 0002", "SUBJECT 2", 30,
                Calendar.getInstance(), 20, "H2", Session.PM);
        ExamSubject subject3    = new ExamSubject("BAME 0003", "SUBJECT 3", 50,
                Calendar.getInstance(), 20, "H3", Session.VM);
        paperList.put(subject1.getPaperCode(), subject1);
        paperList.put(subject2.getPaperCode(), subject2);
        paperList.put(subject3.getPaperCode(), subject3);

        when(JsonHelper.formatString(JsonHelper.TYPE_PAPERS_VENUE, "H3")).thenReturn("Json H3");
        doNothing().when(tcpClient).sendMessage("Json H3");

        ExternalDbLoader.dlPaperList();
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
        try{
            when(JsonHelper.formatString(JsonHelper.TYPE_PAPERS_CDD, "15WAU00001")).thenReturn("Json Cdd");
            doNothing().when(tcpClient).sendMessage("Json Cdd");

            ExternalDbLoader.getPapersExamineByCdd("15WAU00001");
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testGetPapersExamineByCdd_NullTCPShouldThrowFatalError() throws Exception {
        try{
            ExternalDbLoader.setTcpClient(null);

            when(JsonHelper.formatString(JsonHelper.TYPE_PAPERS_CDD, "15WAU00001")).thenReturn("Json Cdd");
            doNothing().when(tcpClient).sendMessage("Json Cdd");

            ExternalDbLoader.getPapersExamineByCdd("15WAU00001");
        } catch (ProcessException err){
            assertEquals("FATAL: Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
        }
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
        try{
            AttendanceList attdList = new AttendanceList();

            when(JsonHelper.formatAttdList(attdList)).thenReturn("Json AttdList");
            doNothing().when(tcpClient).sendMessage("Json AttdList");

            ExternalDbLoader.updateAttdList(attdList);
        } catch (ProcessException err) {
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testUpdateAttdList_Null_TCP_Should_throw_FATAL_Exception() throws Exception {
        try{
            ExternalDbLoader.setTcpClient(null);
            AttendanceList attdList = new AttendanceList();

            when(JsonHelper.formatAttdList(attdList)).thenReturn("Json AttdList");
            doNothing().when(tcpClient).sendMessage("Json AttdList");

            ExternalDbLoader.updateAttdList(attdList);
        } catch (ProcessException err) {
            assertEquals("FATAL: Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
        }
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
        try{
            String bundle = "BAME 0001 Subject 1 -----";

            when(JsonHelper.formatCollection(bundle)).thenReturn("Json Bundle");
            doNothing().when(tcpClient).sendMessage("Json Bundle");
            ExternalDbLoader.acknowledgeCollection(bundle);
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testAcknowledgeCollection_Null_TCP_Should_throw_FATAL_Exception() throws Exception {
        try{
            ExternalDbLoader.setTcpClient(null);
            String bundle = "BAME 0001 Subject 1 -----";

            when(JsonHelper.formatCollection(bundle)).thenReturn("Json Bundle");
            doNothing().when(tcpClient).sendMessage("Json Bundle");
            ExternalDbLoader.acknowledgeCollection(bundle);
        } catch (ProcessException err) {
            assertEquals("FATAL: Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
        }
    }
}