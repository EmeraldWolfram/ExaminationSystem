package com.info.ghiny.examsystem.database;

import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.LoginModel;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


import java.util.Calendar;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by GhinY on 10/07/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class ExternalDbLoaderTest {
    private StaffIdentity staff;
    private TCPClient tcpClient;

    @Before
    public void setUp() throws Exception {
        tcpClient           = Mockito.mock(TCPClient.class);
        ConnectionTask task = Mockito.mock(ConnectionTask.class);

        ExternalDbLoader.setTcpClient(tcpClient);
        ExternalDbLoader.setConnectionTask(task);
        staff   = new StaffIdentity("246800", true, "Dr. TDD", "H3");
    }

    //= requestDuelMessage() =======================================================================
    /**
     * requestDuelMessage()
     *
     * This method is used when the connection of the android is dropped
     * In order to reconnect, a duel message is needed
     * Therefore, this method send out a request to the Chief for a Duel Message
     *
     * Tests:
     * 1. the tcpClient is not null, send out the input
     * 2. the tcpClient is null, throw error
     */
    @Test
    public void testRequestDuelMessage1_TcpNotNull() throws Exception {
        try{
            ExternalDbLoader.requestDuelMessage("Staff ID");

            verify(tcpClient).sendMessage(anyString());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testRequestDuelMessage2_TcpNull() throws Exception {
        try{
            ExternalDbLoader.setTcpClient(null);
            ExternalDbLoader.requestDuelMessage("Staff ID");

        } catch (ProcessException err){
            assertEquals("Fail to request duel message!" +
                    "\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(tcpClient, never()).sendMessage(anyString());
        }
    }


    //= TryLogin() =================================================================================
    /**
     * tryLogin(String id, String pw)
     *
     * send out staff Id Number and the input password through socket
     *
     * Tests:
     * 1. the tcpClient is not null, send out the input
     * 2. the tcpClient is null, throw error
     * 3. null input, throw error
     */
    @Test
    public void testTryLogin1_TCPNotNull() throws Exception {
        try{
            ExternalDbLoader.tryLogin("246800", "0123");

            verify(tcpClient).sendMessage(anyString());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testTryLogin2_NullTCPShouldThrowFatalMessage() throws Exception {
        try{
            ExternalDbLoader.setTcpClient(null);
            ExternalDbLoader.tryLogin("246800", "0123");

        } catch (ProcessException err){
            assertEquals("Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(tcpClient, never()).sendMessage(anyString());
        }
    }

    @Test
    public void testTryLogin3_NullInputParamThrowFatalMessage() throws Exception {
        try{
            ExternalDbLoader.tryLogin(null, "0123");

        } catch (ProcessException err){
            assertEquals("Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(tcpClient, never()).sendMessage(anyString());
        }
    }

    //= DlAttendanceList() =========================================================================
    /**
     * dlAttendanceList(String venue)
     *
     * format venue into JSON Object together as to request the Attendance List
     * Send out the JSON Object
     *
     * Tests:
     * 1. the tcpClient is not null, send out the input
     * 2. the tcpClient is null, throw error
     * 3. staff is not register (null), throw error - Actually Not possible to happen
     */
    @Test
    public void testDlAttendanceList1_TcpNotNull() throws Exception {
        try{

            LoginModel.setStaff(staff);    //To set the venue to H3
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

            ExternalDbLoader.dlAttendanceList();

            verify(tcpClient).sendMessage(anyString());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testDlAttendanceList2_TcpIsNull() throws Exception {
        try{
            ExternalDbLoader.setTcpClient(null);
            LoginModel.setStaff(staff);    //To set the venue to H3
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

            ExternalDbLoader.dlAttendanceList();

        } catch (ProcessException err){
            assertEquals("Fail to request attendance list!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(tcpClient, never()).sendMessage(anyString());
        }
    }

    @Test
    public void testDlAttendanceList3_IdIsNull() throws Exception {
        try{
            LoginModel.setStaff(null);
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

            ExternalDbLoader.dlAttendanceList();

        } catch (ProcessException err){
            assertEquals("Fail to request attendance list!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(tcpClient, never()).sendMessage(anyString());
        }
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
     */
    @Test
    public void testDlPaperList() throws Exception {
        LoginModel.setStaff(staff);    //Set the venue to H3

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

        ExternalDbLoader.dlPaperList();
    }

    //= GetPapersExamineByCdd() ====================================================================
    /**
     *  getPapersExamnineByCdd(String regNum)
     *
     *  By sending out the id of the student together with request
     *  message, the id is send out to request for paper
     *
     *  Tests:
     *  1. the tcpClient is not null, send out the input
     *  2. the tcpClient is null, throw error
     *  3. input param is null, throw error
     *
     */
    @Test
    public void testGetPapersExamineByCdd1_PositiveTest() throws Exception {
        try{
            ExternalDbLoader.getPapersExamineByCdd("15WAU00001");

            verify(tcpClient).sendMessage(anyString());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testGetPapersExamineByCdd2_NullTCPShouldThrowFatalError() throws Exception {
        try{
            ExternalDbLoader.setTcpClient(null);
            ExternalDbLoader.getPapersExamineByCdd("15WAU00001");
        } catch (ProcessException err){
            assertEquals("Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(tcpClient, never()).sendMessage(anyString());
        }
    }

    @Test
    public void testGetPapersExamineByCdd3_NullInputThrowFatalError() throws Exception {
        try{
            ExternalDbLoader.getPapersExamineByCdd(null);
        } catch (ProcessException err){
            assertEquals("Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(tcpClient, never()).sendMessage(anyString());
        }
    }

    //= UpdateAttdList() ===========================================================================
    /**
     * updateAttdList(AttendanceList attdList)
     *
     * format the updated attdList into a JSONObject containing a JSON Array of Candidates
     * send out the JSON Object in string format
     *
     *  Tests:
     *  1. the tcpClient is not null, send out the attendance list
     *  2. the tcpClient is null, throw error
     *  3. input param is null, throw error
     *
     */
    @Test
    public void testUpdateAttdList1_TcpNotNull() throws Exception {
        try{
            ExternalDbLoader.updateAttdList(new AttendanceList());

            verify(tcpClient).sendMessage(anyString());
        } catch (ProcessException err) {
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testUpdateAttdList2_NullTcpThrowFATAL() throws Exception {
        try{
            ExternalDbLoader.setTcpClient(null);
            ExternalDbLoader.updateAttdList(new AttendanceList());
        } catch (ProcessException err) {
            assertEquals("Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(tcpClient, never()).sendMessage(anyString());
        }
    }

    @Test
    public void testUpdateAttdList3_NullInputThrowFATAL() throws Exception {
        try{
            ExternalDbLoader.updateAttdList(null);
        } catch (ProcessException err) {
            assertEquals("Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(tcpClient, never()).sendMessage(anyString());
        }
    }

    //= AcknowledgeCollection() ====================================================================
    /**
     *  acknowledgeCollection(String bundle)
     *
     *  Send out the bundle and collector id in JSON Format
     *  this is to acknowledge the collection of bundle
     *
     *  Tests:
     *  1. the tcpClient is not null, send out the attendance list
     *  2. the tcpClient is null, throw error
     *  3. input param is null, throw error
     *
     */
    @Test
    public void testAcknowledgeCollection1_TcpNotNull() throws Exception {
        try{
            String bundle = "BAME 0001 Subject 1 -----";
            ExternalDbLoader.acknowledgeCollection(bundle);

            verify(tcpClient).sendMessage(anyString());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testAcknowledgeCollection2_NullTcpShouldThrowFATAL() throws Exception {
        try{
            ExternalDbLoader.setTcpClient(null);
            String bundle = "BAME 0001 Subject 1 -----";

            ExternalDbLoader.acknowledgeCollection(bundle);

        } catch (ProcessException err) {
            assertEquals("Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(tcpClient, never()).sendMessage(anyString());
        }
    }

    @Test
    public void testAcknowledgeCollection3_NullInputShouldThrowFATAL() throws Exception {
        try{
            ExternalDbLoader.acknowledgeCollection(null);

        } catch (ProcessException err) {
            assertEquals("Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(tcpClient, never()).sendMessage(anyString());
        }
    }
}