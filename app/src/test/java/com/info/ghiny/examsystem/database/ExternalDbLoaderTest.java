package com.info.ghiny.examsystem.database;

import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.LoginModel;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.JavaHost;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


import java.util.ArrayList;

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
    private JavaHost javaHost;

    @Before
    public void setUp() throws Exception {
        javaHost = Mockito.mock(JavaHost.class);
        ConnectionTask task = Mockito.mock(ConnectionTask.class);

        ExternalDbLoader.setJavaHost(javaHost);
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
     * 1. the javaHost is not null, send out the input
     * 2. the javaHost is null, throw error
     */
    @Test
    public void testRequestDuelMessage1_TcpNotNull() throws Exception {
        try{
            ExternalDbLoader.requestDuelMessage("Staff ID");

            verify(javaHost).putMessageIntoSendQueue(anyString());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testRequestDuelMessage2_TcpNull() throws Exception {
        try{
            ExternalDbLoader.setJavaHost(null);
            ExternalDbLoader.requestDuelMessage("Staff ID");

        } catch (ProcessException err){
            assertEquals("Fail to request duel message!" +
                    "\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(javaHost, never()).putMessageIntoSendQueue(anyString());
        }
    }


    //= TryLogin() =================================================================================
    /**
     * tryLogin(String id, String pw)
     *
     * send out staff Id Number and the input password through socket
     *
     * Tests:
     * 1. the javaHost is not null, send out the input
     * 2. the javaHost is null, throw error
     * 3. null input, throw error
     */
    @Test
    public void testTryLogin1_TCPNotNull() throws Exception {
        try{
            ExternalDbLoader.tryLogin("246800", "0123");

            verify(javaHost).putMessageIntoSendQueue(anyString());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testTryLogin2_NullTCPShouldThrowFatalMessage() throws Exception {
        try{
            ExternalDbLoader.setJavaHost(null);
            ExternalDbLoader.tryLogin("246800", "0123");

        } catch (ProcessException err){
            assertEquals("Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(javaHost, never()).putMessageIntoSendQueue(anyString());
        }
    }

    @Test
    public void testTryLogin3_NullInputParamThrowFatalMessage() throws Exception {
        try{
            ExternalDbLoader.tryLogin(null, "0123");

        } catch (ProcessException err){
            assertEquals("Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(javaHost, never()).putMessageIntoSendQueue(anyString());
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
     * 1. the javaHost is not null, send out the input
     * 2. the javaHost is null, throw error
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

            attdList.addCandidate(cdd1);
            attdList.addCandidate(cdd2);
            attdList.addCandidate(cdd3);
            attdList.addCandidate(cdd4);
            attdList.addCandidate(cdd5);
            attdList.addCandidate(cdd6);

            ExternalDbLoader.dlAttendanceList();

            verify(javaHost).putMessageIntoSendQueue(anyString());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testDlAttendanceList2_TcpIsNull() throws Exception {
        try{
            ExternalDbLoader.setJavaHost(null);
            LoginModel.setStaff(staff);    //To set the venue to H3
            AttendanceList attdList = new AttendanceList();

            Candidate cdd1 = new Candidate(1, "RMB3", "FGY", "15WAU00001", "BAME 0001", Status.ABSENT);
            Candidate cdd2 = new Candidate(1, "RMB3", "NYN", "15WAU00002", "BAME 0001", Status.ABSENT);
            Candidate cdd3 = new Candidate(1, "RMB3", "LHN", "15WAU00003", "BAME 0001", Status.ABSENT);
            Candidate cdd4 = new Candidate(1, "RMB3", "Mr. Bar", "15WAU00004", "BAME 0002", Status.BARRED);
            Candidate cdd5 = new Candidate(1, "RMB3", "Ms. Exm", "15WAU00005", "BAME 0003", Status.EXEMPTED);
            Candidate cdd6 = new Candidate(1, "RMB3", "Ms. Qua", "15WAR00006", "BAME 0001", Status.QUARANTINED);

            attdList.addCandidate(cdd1);
            attdList.addCandidate(cdd2);
            attdList.addCandidate(cdd3);
            attdList.addCandidate(cdd4);
            attdList.addCandidate(cdd5);
            attdList.addCandidate(cdd6);

            ExternalDbLoader.dlAttendanceList();

        } catch (ProcessException err){
            assertEquals("Fail to request attendance list!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(javaHost, never()).putMessageIntoSendQueue(anyString());
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

            attdList.addCandidate(cdd1);
            attdList.addCandidate(cdd2);
            attdList.addCandidate(cdd3);
            attdList.addCandidate(cdd4);
            attdList.addCandidate(cdd5);
            attdList.addCandidate(cdd6);

            ExternalDbLoader.dlAttendanceList();

        } catch (ProcessException err){
            assertEquals("Fail to request attendance list!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(javaHost, never()).putMessageIntoSendQueue(anyString());
        }
    }


    //= GetPapersExamineByCdd() ====================================================================
    /**
     *  getPapersExamnineByCdd(String regNum)
     *
     *  By sending out the id of the student together with request
     *  message, the id is send out to request for paper
     *
     *  Tests:
     *  1. the javaHost is not null, send out the input
     *  2. the javaHost is null, throw error
     *  3. input param is null, throw error
     *
     */
    @Test
    public void testGetPapersExamineByCdd1_PositiveTest() throws Exception {
        try{
            ExternalDbLoader.getPapersExamineByCdd("15WAU00001");

            verify(javaHost).putMessageIntoSendQueue(anyString());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testGetPapersExamineByCdd2_NullTCPShouldThrowFatalError() throws Exception {
        try{
            ExternalDbLoader.setJavaHost(null);
            ExternalDbLoader.getPapersExamineByCdd("15WAU00001");
        } catch (ProcessException err){
            assertEquals("Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(javaHost, never()).putMessageIntoSendQueue(anyString());
        }
    }

    @Test
    public void testGetPapersExamineByCdd3_NullInputThrowFatalError() throws Exception {
        try{
            ExternalDbLoader.getPapersExamineByCdd(null);
        } catch (ProcessException err){
            assertEquals("Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(javaHost, never()).putMessageIntoSendQueue(anyString());
        }
    }

    //= UpdateAttdList() ===========================================================================
    /**
     * updateAttendanceList(AttendanceList attdList)
     *
     * format the updated attdList into a JSONObject containing a JSON Array of Candidates
     * send out the JSON Object in string format
     *
     *  Tests:
     *  1. the javaHost is not null, send out the attendance list
     *  2. the javaHost is null, throw error
     *  3. input param is null, throw error
     *
     */
    @Test
    public void testUpdateAttdList1_TcpNotNull() throws Exception {
        try{
            ExternalDbLoader.updateAttendanceList(new AttendanceList());

            verify(javaHost).putMessageIntoSendQueue(anyString());
        } catch (ProcessException err) {
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testUpdateAttdList2_NullTcpThrowFATAL() throws Exception {
        try{
            ExternalDbLoader.setJavaHost(null);
            ExternalDbLoader.updateAttendanceList(new AttendanceList());
        } catch (ProcessException err) {
            assertEquals("Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(javaHost, never()).putMessageIntoSendQueue(anyString());
        }
    }

    @Test
    public void testUpdateAttdList3_NullInputThrowFATAL() throws Exception {
        try{
            ExternalDbLoader.updateAttendanceList(null);
        } catch (ProcessException err) {
            assertEquals("Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(javaHost, never()).putMessageIntoSendQueue(anyString());
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
     *  1. the javaHost is not null, send out the collector and bundle
     *  2. the javaHost is null, throw error
     *  3. input param is null, throw error
     *
     */
    @Test
    public void testAcknowledgeCollection1_TcpNotNull() throws Exception {
        try{
            PaperBundle bundle  = new PaperBundle();
            bundle.parseBundle("M4/BAME 0001/RMB3");
            ExternalDbLoader.acknowledgeCollection("246810", bundle);

            verify(javaHost).putMessageIntoSendQueue(anyString());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testAcknowledgeCollection2_NullTcpShouldThrowFATAL() throws Exception {
        try{
            ExternalDbLoader.setJavaHost(null);
            PaperBundle bundle  = new PaperBundle();
            bundle.parseBundle("M4/BAME 0001/RMB3");
            ExternalDbLoader.acknowledgeCollection("246810", bundle);

        } catch (ProcessException err) {
            assertEquals("Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(javaHost, never()).putMessageIntoSendQueue(anyString());
        }
    }

    @Test
    public void testAcknowledgeCollection3_NullInputShouldThrowFATAL() throws Exception {
        try{
            ExternalDbLoader.acknowledgeCollection("246810", null);

        } catch (ProcessException err) {
            assertEquals("Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(javaHost, never()).putMessageIntoSendQueue(anyString());
        }
    }

    //= AcknowledgeUndoCollection() ====================================================================
    /**
     *  acknowledgeUndoCollection(String bundle)
     *
     *  Send out the bundle and collector id in JSON Format
     *  this is to acknowledge the collection of bundle
     *
     *  Tests:
     *  1. the javaHost is not null, send out the removing collector and bundle
     *  2. the javaHost is null, throw error
     *  3. input param is null, throw error
     *
     */
    @Test
    public void testAcknowledgeUndoCollection1_TcpNotNull() throws Exception {
        try{
            PaperBundle bundle  = new PaperBundle();
            bundle.parseBundle("M4/BAME 0001/RMB3");
            ExternalDbLoader.undoCollection("246810", bundle);

            verify(javaHost).putMessageIntoSendQueue(anyString());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testAcknowledgeUndoCollection2_NullTcpShouldThrowFATAL() throws Exception {
        try{
            ExternalDbLoader.setJavaHost(null);
            PaperBundle bundle  = new PaperBundle();
            bundle.parseBundle("M4/BAME 0001/RMB3");
            ExternalDbLoader.undoCollection("246810", bundle);

        } catch (ProcessException err) {
            assertEquals("Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(javaHost, never()).putMessageIntoSendQueue(anyString());
        }
    }

    @Test
    public void testAcknowledgeUndoCollection3_NullInputShouldThrowFATAL() throws Exception {
        try{
            ExternalDbLoader.undoCollection("246810", null);

        } catch (ProcessException err) {
            assertEquals("Fail to send out request!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(javaHost, never()).putMessageIntoSendQueue(anyString());
        }
    }

    //= UpdateAttendance ===========================================================================
    /**
     * updateAttendance(...)
     *
     * Send out the newly collected attendance in JSON format
     * This is to synchronize with others attendance collector
     *
     * Tests:
     *  1. the javaHost is not null, send out the removing collector and bundle
     *  2. the javaHost is null, throw error
     *  3. input param is null, throw error
     *
     */
    @Test
    public void testUpdateAttendance1_TcpNotNull() throws Exception {
        try{
            ArrayList<Candidate> candidates = new ArrayList<>();
            ExternalDbLoader.updateAttendance(candidates);

            verify(javaHost).putMessageIntoSendQueue(anyString());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testUpdateAttendance2_NullTcpShouldThrowFATAL() throws Exception {
        try{
            ExternalDbLoader.setJavaHost(null);
            ArrayList<Candidate> candidates = new ArrayList<>();
            ExternalDbLoader.updateAttendance(candidates);

        } catch (ProcessException err) {
            assertEquals("Fail to send out update!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(javaHost, never()).putMessageIntoSendQueue(anyString());
        }
    }

    @Test
    public void testUpdateAttendance3_NullInputShouldThrowFATAL() throws Exception {
        try{
            ExternalDbLoader.updateAttendance(null);

        } catch (ProcessException err) {
            assertEquals("Fail to send out update!\nPlease consult developer", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            verify(javaHost, never()).putMessageIntoSendQueue(anyString());
        }
    }

}