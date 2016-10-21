package com.info.ghiny.examsystem.model;


import android.content.DialogInterface;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.Session;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.interfacer.TakeAttdMVP;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by GhinY on 15/06/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class TakeAttdModelTest {

    private CheckListLoader dBLoader;
    private TakeAttdModel model;
    private TakeAttdMVP.MPresenter taskPresenter;

    private ConnectionTask connectionTask;
    private TCPClient tcpClient;
    private String MESSAGE_FROM_CHIEF;
    private DialogInterface dialog;

    private AttendanceList attdList;
    private Candidate cdd1;
    private Candidate cdd2;
    private Candidate cdd3;
    private Candidate cdd4;
    private Candidate cdd5;
    private Candidate cdd6;

    private HashMap<String, ExamSubject> paperList;
    private ExamSubject subject1;
    private ExamSubject subject2;
    private ExamSubject subject3;

    @Before
    public void setUp() throws Exception{
        LoginModel.setStaff(new StaffIdentity("id", true, "name", "M4"));
        connectionTask  = Mockito.mock(ConnectionTask.class);
        tcpClient       = Mockito.mock(TCPClient.class);
        dialog          = Mockito.mock(DialogInterface.class);

        ExternalDbLoader.setConnectionTask(connectionTask);
        ExternalDbLoader.setTcpClient(tcpClient);

        attdList = new AttendanceList();
        cdd1 = new Candidate(0, "RMB3", "FGY", "15WAU00001", "BAME 0001", Status.ABSENT);
        cdd2 = new Candidate(0, "RMB3", "NYN", "15WAU00002", "BAME 0001", Status.ABSENT);
        cdd3 = new Candidate(0, "RMB3", "LHN", "15WAU00003", "BAME 0001", Status.ABSENT);
        cdd4 = new Candidate(0, "RMB3", "Mr. Bar", "15WAU00004", "BAME 0002", Status.BARRED);
        cdd5 = new Candidate(0, "RMB3", "Ms. Exm", "15WAU00005", "BAME 0003", Status.EXEMPTED);
        cdd6 = new Candidate(0, "RMB3", "Ms. Qua", "15WAR00006", "BAME 0001", Status.QUARANTINED);

        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), cdd2.getStatus(), cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), cdd3.getStatus(), cdd3.getProgramme());
        attdList.addCandidate(cdd4, cdd4.getPaperCode(), cdd4.getStatus(), cdd4.getProgramme());
        attdList.addCandidate(cdd5, cdd5.getPaperCode(), cdd5.getStatus(), cdd5.getProgramme());
        attdList.addCandidate(cdd6, cdd6.getPaperCode(), cdd6.getStatus(), cdd6.getProgramme());

        paperList   = new HashMap<>();
        subject1    = new ExamSubject("BAME 0001", "SUBJECT 1", 10, Calendar.getInstance(), 20,
                "H1", Session.AM);
        subject2    = new ExamSubject("BAME 0002", "SUBJECT 2", 30, Calendar.getInstance(), 20,
                "H2", Session.PM);
        subject3    = new ExamSubject("BAME 0003", "SUBJECT 3", 50, Calendar.getInstance(), 20,
                "H3", Session.VM);
        paperList.put(subject1.getPaperCode(), subject1);
        paperList.put(subject2.getPaperCode(), subject2);
        paperList.put(subject3.getPaperCode(), subject3);

        dBLoader = Mockito.mock(CheckListLoader.class);

        taskPresenter = Mockito.mock(TakeAttdMVP.MPresenter.class);
        model   = new TakeAttdModel(taskPresenter, dBLoader);

        TakeAttdModel.setAttdList(attdList);

        model.setTempCdd(null);
        model.setTempTable(null);
        model.setAssgnList(new HashMap<Integer, String>());
        Candidate.setPaperList(paperList);
    }

    //= InitAttendance() ===========================================================================
    /**
     * initAttendance()
     *
     * This method prepare the attendance list and exam subjects info for the
     * attendance taking process.
     *
     * It prepare by loading database or query from chief
     *
     * Tests:
     * 1. When database have no attendance list, send request to chief and start timer
     * 2. When database have no exam papers info, send request to chief and start timer
     * 3. When database have attendance list and exam papers info, load from the database
     */
    @Test
    public void testInitAttendance1_NoAttendanceListInDB() throws Exception {
        when(dBLoader.emptyAttdInDB()).thenReturn(true);
        when(dBLoader.emptyPapersInDB()).thenReturn(false);

        model.initAttendance();

        verify(tcpClient).sendMessage(anyString());
        verify(dBLoader, never()).queryAttendanceList();
        verify(dBLoader, never()).queryPapers();
        assertFalse(model.isInitialized());
    }

    @Test
    public void testInitAttendance2_NoExamPapersInfoInDB() throws Exception {
        when(dBLoader.emptyAttdInDB()).thenReturn(false);
        when(dBLoader.emptyPapersInDB()).thenReturn(true);

        model.initAttendance();

        verify(tcpClient).sendMessage(anyString());
        verify(dBLoader, never()).queryAttendanceList();
        verify(dBLoader, never()).queryPapers();
        assertFalse(model.isInitialized());
    }

    @Test
    public void testInitAttendance3_RequiredInfoAvailableDB() throws Exception {
        when(dBLoader.emptyAttdInDB()).thenReturn(false);
        when(dBLoader.emptyPapersInDB()).thenReturn(false);

        model.initAttendance();

        verify(tcpClient, never()).sendMessage(anyString());
        verify(dBLoader).queryAttendanceList();
        verify(dBLoader).queryPapers();
        assertTrue(model.isInitialized());
    }

    //= MatchPassword(...) =========================================================================
    /**
     * matchPassword(...)
     *
     * This method is used after the user had logged in but inactive for sometime
     * Prompt for password and match it when the user try to activate the phone again
     *
     * Tests:
     * 1. When input password is CORRECT, do nothing
     * 2. When input password is INCORRECT, throw MESSAGE_TOAST Exception
     *
     */
    @Test
    public void testMatchPassword1_CorrectPasswordReceived() throws Exception {
        LoginModel.getStaff().setPassword("CORRECT");
        try{
            model.matchPassword("CORRECT");
        } catch (ProcessException err) {
            fail("Exception --" + err.getErrorMsg() + "-- not expected!");
        }
    }

    @Test
    public void testMatchPassword2_IncorrectPasswordReceived() throws Exception {
        LoginModel.getStaff().setPassword("CORRECT");
        try{
            model.matchPassword("INCORRECT");
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Access denied. Incorrect Password", err.getErrorMsg());
        }
    }

    //= CheckDownloadResult() ======================================================================

    /**
     * checkDownloadResult()
     *
     * During initAttendance(), request for attendance list and papers was sent to chief
     * This method was used to read the chief respond and take action accordingly
     *
     * 1. Chief respond with the attendance list and exam papers, parse the message and assign
     * 2. Chief respond with negative message due to some reason, throw an error
     *
     * @throws Exception
     */
    @Test
    public void testCheckDownloadResult1_PositiveRespondFromChief() throws Exception {
        MESSAGE_FROM_CHIEF = "{\"CddList\":[" +
                "{\"ExamIndex\":\"W0001AAAA\",\"Status\":\"LEGAL\",\"Code\":\"MPU3123\",\"RegNum\":\"15WAR00001\",\"Programme\":\"RMB3\"}," +
                "{\"ExamIndex\":\"W0002AAAA\",\"Status\":\"LEGAL\",\"Code\":\"MPU3123\",\"RegNum\":\"15WAR00002\",\"Programme\":\"RMB3\"}," +
                "{\"ExamIndex\":\"W0003AAAA\",\"Status\":\"LEGAL\",\"Code\":\"MPU3123\",\"RegNum\":\"15WAR00003\",\"Programme\":\"RMB3\"}," +
                "{\"ExamIndex\":\"W0004AAAA\",\"Status\":\"BARRED\",\"Code\":\"MPU3123\",\"RegNum\":\"15WAR00004\",\"Programme\":\"RMB3\"}," +
                "{\"ExamIndex\":\"W0005AAAA\",\"Status\":\"EXEMPTED\",\"Code\":\"MPU3123\",\"RegNum\":\"15WAR00005\",\"Programme\":\"RMB3\"}]," +
                "\"PaperMap\":[" +
                "{\"PaperStartNo\":45,\"PaperDesc\":\"Mathematic\",\"PaperCode\":\"BABE2203\",\"PaperTotalCdd\":4}," +
                "{\"PaperStartNo\":32,\"PaperDesc\":\"Hubungan Etnik\",\"PaperCode\":\"MPU3123\",\"PaperTotalCdd\":9}]," +
                "\"Result\":true}";

        try{
            assertFalse(model.isInitialized());
            assertEquals(paperList, Candidate.getPaperList());
            assertEquals(attdList, TakeAttdModel.getAttdList());

            model.checkDownloadResult(MESSAGE_FROM_CHIEF);

            assertTrue(model.isInitialized());
            assertNotEquals(paperList, Candidate.getPaperList());
            assertNotEquals(attdList, TakeAttdModel.getAttdList());
        } catch (ProcessException err) {
            fail("Exception --" + err.getErrorMsg() + "-- is not expected!");
        }
    }

    @Test
    public void testCheckDownloadResult2_NegativeRespondFromChief() throws Exception {
        MESSAGE_FROM_CHIEF = "{\"Result\":false}";
        try{
            assertFalse(model.isInitialized());
            assertEquals(paperList, Candidate.getPaperList());
            assertEquals(attdList, TakeAttdModel.getAttdList());

            model.checkDownloadResult(MESSAGE_FROM_CHIEF);
        } catch (ProcessException err) {
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("Unable to download Attendance List\nPlease retry login",err.getErrorMsg());
            //Make no changes to PaperList and AttdList
            assertEquals(paperList, Candidate.getPaperList());
            assertEquals(attdList, TakeAttdModel.getAttdList());
        }

    }

    //= SaveAttendance() ===========================================================================

    /**
     * saveAttendance()
     *
     * This method is used to save the attendance list into the database before
     * the activity was destroyed.
     *
     * Test
     * Check if method called to save data into database
     */
    @Test
    public void testSaveAttendance() throws Exception {
        model.saveAttendance();

        verify(dBLoader).saveAttendanceList(attdList);
        verify(dBLoader).savePaperList(paperList);
    }

    //= UpdateAssignList() =========================================================================
    /**
     * updateAssignList()
     *
     * update assign list according to the present candidates in attendance list
     *
     * 1. Successfully extract present candidates into assign list
     * 2. When the attendance list is null, throw FATAL_MESSAGE (NULL Pointer)
     * 3. When the attendance list have no present candidate, do nothing
     */

    @Test
    public void testUpdateAssignList1_PositiveTest() throws Exception {
        attdList = new AttendanceList();
        model.setInitialized(true);
        cdd1.setTableNumber(12);
        cdd1.setStatus(Status.PRESENT);
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), cdd2.getStatus(), cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), cdd3.getStatus(), cdd3.getProgramme());
        TakeAttdModel.setAttdList(attdList);

        HashMap<Integer, String> map = model.getAssgnList();
        assertEquals(0, map.size());

        model.updateAssignList();

        assertEquals(1, map.size());
        assertEquals(cdd1.getRegNum(), map.get(12));
    }

    @Test
    public void testUpdateAssignList2_NegativeTest() throws Exception {
        try{
            TakeAttdModel.setAttdList(null);
            model.setInitialized(true);

            model.updateAssignList();

            fail("Expected FATAL_MESSAGE but non were thrown");
        } catch (ProcessException err) {
            assertEquals("Attendance List is not initialize", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());

        }
    }

    @Test
    public void testUpdateAssignList3_NegativeTest() throws Exception {
        TakeAttdModel.setAttdList(attdList);
        model.setInitialized(true);

        assertEquals(0, model.getAssgnList().size());
        model.updateAssignList();
        assertEquals(0, model.getAssgnList().size());
    }

    //= TryAssignScanValue(...) ====================================================================
    /**
     * tryAssignScanValue(...)
     *
     * The most core method of taking attendance
     * It takes the qr code in String as input,
     * filter, verify and take action according to the input String
     *
     * Tests:
     * 1. Input string was table and candidate was null, assign as table
     * 2. Input string was candidate and table was null, assign as candidate
     * 3. Input string was table and candidate NOT null, assign as table and match the pair
     *    and throw MESSAGE_TOAST to indicate assign
     * 4. Input string was candidate and table NOT null, assign as candidate and match the pair
     *    and throw MESSAGE_TOAST to indicate assign
     * 5. Input string was neither any of it, throw MESSAGE_TOAST Exception
     */
    @Test
    public void testTryAssignScanValue1_AssignTableOnly() throws Exception {
        try{
            assertNull(model.getTempTable());
            assertNull(model.getTempCdd());

            model.tryAssignScanValue("13");

            verify(taskPresenter).displayTable(13);
            verify(taskPresenter, never()).displayCandidate(any(Candidate.class));
            verify(taskPresenter, never()).resetDisplay();

            assertNull(model.getTempCdd());
            assertEquals(13, model.getTempTable().intValue());
        } catch (ProcessException err) {
            fail("Exception --" + err.getErrorMsg() + "-- is not expected!");
        }
    }

    @Test
    public void testTryAssignScanValue2_AssignCandidateOnly() throws Exception {
        try{
            assertNull(model.getTempTable());
            assertNull(model.getTempCdd());

            model.tryAssignScanValue("15WAU00001");

            verify(taskPresenter, never()).displayTable(13);
            verify(taskPresenter).displayCandidate(any(Candidate.class));
            verify(taskPresenter, never()).resetDisplay();

            assertEquals(cdd1, model.getTempCdd());
            assertNull(model.getTempTable());
        } catch (ProcessException err) {
            fail("Exception --" + err.getErrorMsg() + "-- is not expected!");
        }
    }

    @Test
    public void testTryAssignScanValue3_AssignTableWithCandidateReady() throws Exception {
        try{
            assertNull(model.getTempTable());
            model.setTempCdd(cdd3);

            model.tryAssignScanValue("13");

            fail("Expected MESSAGE_TOAST exception but none thrown");
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("LHN Assigned to 13", err.getErrorMsg());

            verify(taskPresenter).displayTable(13);
            verify(taskPresenter, never()).displayCandidate(any(Candidate.class));
            verify(taskPresenter).resetDisplay();
            assertNull(model.getTempTable());
            assertNull(model.getTempCdd());
        }
    }

    @Test
    public void testTryAssignScanValue4_AssignCandidateWithTableReady() throws Exception {
        try{
            assertNull(model.getTempCdd());
            model.setTempTable(13);

            model.tryAssignScanValue("15WAU00001");

            fail("Expected MESSAGE_TOAST exception but none thrown");
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("FGY Assigned to 13", err.getErrorMsg());

            verify(taskPresenter, never()).displayTable(anyInt());
            verify(taskPresenter).displayCandidate(cdd1);
            verify(taskPresenter).resetDisplay();
            assertNull(model.getTempTable());
            assertNull(model.getTempCdd());
        }
    }

    @Test
    public void testTryAssignScanValue5_InvalidString() throws Exception {
        try{
            assertNull(model.getTempTable());
            assertNull(model.getTempCdd());

            model.tryAssignScanValue("AXXD");

            fail("Expected MESSAGE_TOAST exception but none thrown");
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Not a valid QR", err.getErrorMsg());

            verify(taskPresenter, never()).displayTable(anyInt());
            verify(taskPresenter, never()).displayCandidate(cdd1);
            verify(taskPresenter, never()).resetDisplay();
            assertNull(model.getTempTable());
            assertNull(model.getTempCdd());
        }
    }

    //= Run() ======================================================================================
    /**
     * run()
     *
     * 1. When ConnectionTask is complete, do nothing
     * 2. When ConnectionTask is not complete, throw an error and the presenter shall handle
     */
    @Test
    public void testRun_ChiefDoRespond() throws Exception {
        ConnectionTask.setCompleteFlag(true);
        model.run();
        verify(taskPresenter, never()).onTimesOut(any(ProcessException.class));
    }

    @Test
    public void testRun_ChiefNoRespond() throws Exception {
        ConnectionTask.setCompleteFlag(false);
        model.run();
        verify(taskPresenter).onTimesOut(any(ProcessException.class));
    }

    //= OnClick(...) ===============================================================================
    /**
     * onClick(...)
     *
     * Unlike most onClick listener implemented in the presenter layer
     * This onClick listener is used for update and cancel button when
     * a window pop out due to reassign candidate or reassign table.
     *
     * Tests:
     * 1. Neutral button pressed. Will not happen.
     * 2. Negative button pressed (cancel). Clear the buffer and reset the display
     * 3. Positive button pressed (update). Clear the buffer and reset the display
     *
     */
    @Test
    public void testOnClickNeutralButton() throws Exception {
        attdList.removeCandidate(cdd1.getRegNum());
        cdd1.setTableNumber(13);
        cdd1.setStatus(Status.PRESENT);
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        model.getAssgnList().put(13, cdd1.getRegNum());
        model.setTempTable(13);
        model.setTempCdd(cdd2);

        model.onClick(dialog, DialogInterface.BUTTON_NEUTRAL);

        assertTrue(model.getAssgnList().containsValue(cdd1.getRegNum()));
        assertFalse(model.getAssgnList().containsValue(cdd2.getRegNum()));
        assertNull(model.getTempCdd());
        assertNull(model.getTempTable());
        verify(dialog).cancel();
        verify(taskPresenter).resetDisplay();
    }

    @Test
    public void testOnClickNegativeButton() throws Exception {
        attdList.removeCandidate(cdd1.getRegNum());
        cdd1.setTableNumber(13);
        cdd1.setStatus(Status.PRESENT);
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        model.getAssgnList().put(13, cdd1.getRegNum());
        model.setTempTable(13);
        model.setTempCdd(cdd2);

        model.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);

        assertTrue(model.getAssgnList().containsValue(cdd1.getRegNum()));
        assertFalse(model.getAssgnList().containsValue(cdd2.getRegNum()));
        assertNull(model.getTempCdd());
        assertNull(model.getTempTable());
        verify(dialog).cancel();
        verify(taskPresenter).resetDisplay();
    }

    @Test
    public void testOnClickPositiveButton() throws Exception {
        attdList.removeCandidate(cdd1.getRegNum());
        cdd1.setTableNumber(13);
        cdd1.setStatus(Status.PRESENT);
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        model.getAssgnList().put(13, cdd1.getRegNum());
        model.setTempTable(13);
        model.setTempCdd(cdd2);

        model.onClick(dialog, DialogInterface.BUTTON_POSITIVE);

        assertTrue(model.getAssgnList().containsValue(cdd2.getRegNum()));
        assertFalse(model.getAssgnList().containsValue(cdd1.getRegNum()));
        assertNull(model.getTempCdd());
        assertNull(model.getTempTable());
        verify(dialog).cancel();
        verify(taskPresenter).resetDisplay();
    }

    //= CheckTable =================================================================================
    /**
     * checkTable()
     *
     * This method should not be called by outsider.
     * Partial method of tryAssignScanValue(...) 1st piece of 6
     *
     * This method should verify the table is a valid table number in the venue
     * and check if the string is all digits
     *
     * Tests:
     * 1. Input string was a valid table
     * 2. Input string was not a full digit number (not possible to be table)
     *
     */
    @Test
    public void testCheckTable1_ValidNumberString() throws Exception {
        try{
            model.checkTable("12");

            assertNotNull(model.getTempTable());
            assertEquals(12, model.getTempTable().intValue());
        } catch (ProcessException err) {
            fail("Exception --" + err.getErrorMsg() + "-- is not expected");
        }
    }

    @Test
    public void testCheckTable2_NotFullDigitNumber() throws Exception {
        try{
            model.checkTable("3A");
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Not a valid QR code", err.getErrorMsg());
        }
    }

    //= CheckCandidate =============================================================================
    /**
     * checkCandidate()
     *
     * This method should not be called by outsider.
     * Partial method of tryAssignScanValue(...) 2nd piece of 6
     *
     * This method used to verify the scan String is a candidate register number
     * If verification passed, the candidate will be register into the candidate buffer
     * Exception will be thrown if verification failed
     *
     * Tests:
     * 1. Input String is null, throw error. (Not possible, tryAssignScanValue will pre-handle)
     * 2. Input String is a candidate, but not in attendance list. (Other venue maybe) Throw....
     * 3. Input String is a candidate, but listed as EXEMPTED. Throw MESSAGE_TOAST
     * 4. Input String is a candidate, but listed as BARRED. Throw MESSAGE_TOAST
     * 5. Input String is a candidate, but listed as QUARANTINED. Throw MESSAGE_TOAST
     * 6. Input String is a valid candidate, register into the buffer
     * 7. Attendance List is not initialized yet, throw FATAL_MESSAGE
     */
    @Test
    public void testCheckCandidate1_NullInputStringThrowMessageToast() throws Exception {
        try{
            model.checkCandidate(null);

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException err){
            assertNull(model.getTempCdd());
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Scanning a null value", err.getErrorMsg());
        }
    }

    @Test
    public void testCheckCandidate2_NotAListedCandidateThrowMessageToast() throws Exception{
        try{
            model.checkCandidate("15WAU22222");

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException err){
            assertNull(model.getTempCdd());
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("15WAU22222 doest not belong to this venue", err.getErrorMsg());
        }
    }

    @Test
    public void testCheckCandidate3_CandidateExemptedThrowMessageToast() throws Exception{
        try{
            model.checkCandidate("15WAU00005");

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException err){
            assertNull(model.getTempCdd());
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("The paper was exempted for Ms. Exm", err.getErrorMsg());
        }
    }

    @Test
    public void testCheckCandidate4_CandidateBarredThrowMessageToast() throws Exception {
        try{
            model.checkCandidate("15WAU00004");

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException err){
            assertNull(model.getTempCdd());
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Mr. Bar have been barred", err.getErrorMsg());
        }
    }

    @Test
    public void testCheckCandidate5_CandidateQuarantinedThrowMessageToast() throws Exception{
        try{
            model.checkCandidate("15WAR00006");

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException err){
            assertNull(model.getTempCdd());
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("The paper was quarantized for Ms. Qua", err.getErrorMsg());
        }
    }

    @Test
    public void testCheckCandidate6_CandidateIsValid() throws Exception{
        try{
            model.checkCandidate("15WAU00001");

            assertNotNull(model.getTempCdd());
            assertEquals(model.getTempCdd(), cdd1);
        } catch(ProcessException err){
            fail("No error should be thrown but thrown ErrorMsg " + err.getErrorMsg());
        }
    }

    @Test
    public void testCheckCandidate7_AttendanceListNotInitializeYet() throws Exception{
        try{
            TakeAttdModel.setAttdList(null);
            model.checkCandidate("15WAU00001");

            fail("Expected MESSAGE_DIALOG but none thrown");
        } catch(ProcessException err){
            assertEquals(ProcessException.MESSAGE_DIALOG, err.getErrorType());
            assertEquals("No Attendance List", err.getErrorMsg());
        }
    }

    //=TryAssignCandidate===========================================================================
    /**
     * tryAssignCandidate()
     *
     * This method should not be called by outsider.
     * Partial method of tryAssignScanValue(...) 3rd piece of 6
     * 4th, 5th and 6th piece shall be called by this method.
     *
     * This method is used after verification was done by checkCandidate() and checkTable()
     *
     * When the buffers for candidate and table are both filled up
     * This method will perform matching and take attendance of the candidate
     *
     * Tests:
     * 1. Both buffer are empty, do nothing and return FALSE
     * 2. Candidate buffer is not filled, do nothing and return FALSE
     * 3. Table buffer is not filled, do nothing and return FALSE
     * 4. Both buffer are filled, pair is match and return TRUE
     * 5. Both buffer are filled, table is reassigned and throw UPDATE_PROMPT
     * 6. Both buffer are filled, candidate is reassigned and throw UPDATE_PROMPT
     * 7. Both buffer are filled, pair not match and throw MESSAGE_TOAST
     */

    @Test
    public void testTryAssignCandidate1_Both_not_assign_should_return_false() throws Exception{
        try{
            model.setTempTable(null);
            model.setTempCdd(null);

            assertFalse(model.tryAssignCandidate());
        }catch(ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testTryAssignCandidate2_EmptyCandidateBufferReturnFalse() throws Exception{
        try{
            model.setTempTable(13);
            model.setTempCdd(null);

            assertFalse(model.tryAssignCandidate());
        }catch(ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testTryAssignCandidate3_EmptyTableBufferReturnFalse() throws Exception{
        try{
            model.setTempTable(null);
            model.setTempCdd(cdd1);

            assertFalse(model.tryAssignCandidate());
        }catch(ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testTryAssignCandidate4_BothBufferAreFilled() throws Exception{
        try{
            model.setTempCdd(cdd1);
            model.setTempTable(12);

            assertTrue(model.tryAssignCandidate());
        }catch(ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testTryAssignCandidate5_ReassignTableThrowUpdatePrompt()throws Exception{
        attdList.removeCandidate(cdd2.getRegNum());
        cdd2.setStatus(Status.PRESENT);
        cdd2.setTableNumber(12);
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), cdd2.getStatus(), cdd2.getProgramme());
        model.getAssgnList().put(12, cdd2.getRegNum());

        try{
            model.setTempTable(12);
            model.setTempCdd(cdd1);

            model.tryAssignCandidate();

            fail("Expected UPDATE_PROMPT but none thrown");
        }catch(ProcessException err){
            assertEquals(ProcessException.UPDATE_PROMPT, err.getErrorType());
            assertEquals("Previous: Table 12 assigned to NYN\nNew: Table 12 assign to FGY",
                    err.getErrorMsg());
        }
    }

    @Test
    public void testTryAssignCandidate6_ReassignCandidateThrowUpdatePrompt() throws Exception{
        attdList.removeCandidate(cdd1.getRegNum());
        cdd1.setStatus(Status.PRESENT);
        cdd1.setTableNumber(14);
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        model.getAssgnList().put(14, cdd1.getRegNum());

        try{
            model.setTempTable(12);
            model.setTempCdd(cdd1);

            model.tryAssignCandidate();

            fail("Expected UPDATE_PROMPT but none thrown");
        }catch(ProcessException err){
            assertEquals(ProcessException.UPDATE_PROMPT, err.getErrorType());
            assertEquals("Previous: FGY assigned to Table 14\nNew: FGY assign to 12",
                    err.getErrorMsg());
        }
    }

    @Test
    public void testTryAssignCandidate7_PaperNotMatchThrowMessageToast() throws Exception{
        try{
            model.setTempTable(55);
            model.setTempCdd(cdd1);

            model.tryAssignCandidate();

            fail("Expected MESSAGE_TOAST but none thrown");
        }catch(ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("FGY should not sit here\nSuggest to Table 10", err.getErrorMsg());
        }
    }

    //= UpdateNewCandidate() =======================================================================
    /*************************************************
     * Dialog -> update
     * Table 14 previously assigned to Cdd1
     * Table 14 then assign to Cdd2
     * updateNewCandidate():
     * + should reset Cdd1 to Table 0 and ABSENT
     * + should assign Cdd2 to Table 14 and PRESENT
    *************************************************/
    @Test
    public void testUpdateNewCandidate_ReassignTable() throws Exception{
        assertEquals(3, attdList.getNumberOfCandidates(Status.ABSENT));

        model.checkTable("14");
        model.checkCandidate("15WAU00001");
        model.tryAssignCandidate();

        model.checkTable("14");
        model.checkCandidate("15WAU00002");

        model.updateNewCandidate();

        assertEquals(1, model.getAssgnList().size());
        assertEquals("15WAU00002", model.getAssgnList().get(14));
        assertEquals(1, attdList.getNumberOfCandidates(Status.PRESENT));
        assertEquals(2, attdList.getNumberOfCandidates(Status.ABSENT));
        assertEquals(Status.ABSENT, cdd1.getStatus());
        assertEquals(Status.PRESENT, cdd2.getStatus());
    }
    /*************************************************
     * Dialog -> update
     * Table 12 previously assigned to Cdd1
     * Table 14 then assign to Cdd1
     * updateNewCandidate():
     * + should renew Cdd1 to Table 14
     *************************************************/
    @Test
    public void testUpdateNewCandidate_ReassignCandidate() throws Exception{
        assertEquals(3, attdList.getNumberOfCandidates(Status.ABSENT));

        model.checkTable("12");
        model.checkCandidate("15WAU00001");
        model.tryAssignCandidate();

        model.checkTable("14");
        model.checkCandidate("15WAU00001");

        model.updateNewCandidate();

        assertEquals(1, model.getAssgnList().size());
        assertNull(model.getAssgnList().get(12));
        assertEquals("15WAU00001", model.getAssgnList().get(14));
        assertEquals(14, (int)attdList.getCandidate("15WAU00001").getTableNumber());
        assertEquals(1, attdList.getNumberOfCandidates(Status.PRESENT));
    }

    //= CancelNewAssign ============================================================================
    /*************************************************
     * Dialog -> cancel
     * Table 12 previously assigned to Cdd1
     * Table 14 then assign to Cdd1
     * cancelNewAssign():
     * + should remain Cdd1 to Table 12
     *************************************************/
    @Test
    public void testCancelNewCandidate_ReassignCandidate() throws Exception{
        assertEquals(3, attdList.getNumberOfCandidates(Status.ABSENT));

        model.checkTable("12");
        model.checkCandidate("15WAU00001");
        model.tryAssignCandidate();

        model.checkTable("14");
        model.checkCandidate("15WAU00001");

        model.cancelNewAssign();

        assertEquals(1, model.getAssgnList().size());
        assertNull(model.getAssgnList().get(14));
        assertEquals("15WAU00001", model.getAssgnList().get(12));
        assertEquals(12, (int)attdList.getCandidate("15WAU00001").getTableNumber());
        assertEquals(1, attdList.getNumberOfCandidates(Status.PRESENT));
    }

    /*************************************************************
     * Dialog -> cancel
     * Table 14 previously assigned to Cdd1
     * Table 14 then assign to Cdd2
     * cancelNewAssign():
     * + should remain the previous assigned table and candidate
     *************************************************************/
    @Test
    public void testCancelNewAssign_ReassugnTable() throws Exception{
        assertEquals(3, attdList.getNumberOfCandidates(Status.ABSENT));

        model.checkTable("14");
        model.checkCandidate("15WAU00001");
        model.tryAssignCandidate();

        model.checkTable("14");
        model.checkCandidate("15WAU00002");

        model.cancelNewAssign();

        assertEquals(1, model.getAssgnList().size());
        assertEquals("15WAU00001", model.getAssgnList().get(14));
        assertEquals(1, attdList.getNumberOfCandidates(Status.PRESENT));
        assertEquals(2, attdList.getNumberOfCandidates(Status.ABSENT));
        assertEquals(Status.PRESENT, cdd1.getStatus());
        assertEquals(Status.ABSENT, cdd2.getStatus());
    }

    //= AttemptReassign() ===========================================================================
    /**
     * attemptReassign()
     *
     * This method should not be called by outsider.
     * Partial method of tryAssignScanValue(...) 4th piece of 6
     *
     * when a candidate or table is reassigned during the scanning process
     * this method will caught the occurrence and throw an update/cancel exception
     *
     * Tests:
     * 1. Table reassigned, throw Update/Cancel Exception
     * 2. Candidate reassigned, throw Update/Cancel Exception
     * 3. No reassign, do nothing
     */

    @Test
    public void testAttemptReassign1_SameTableShould_throw_UPDATE_PROMPT()throws Exception{
        model.setTempTable(12);
        model.setTempCdd(cdd2);
        boolean test = model.tryAssignCandidate();

        try{
            model.setTempTable(12);
            model.setTempCdd(cdd1);
            model.attemptReassign();

            fail("Expected UPDATE_PROMPT but none thrown");
        }catch(ProcessException err){
            assertEquals(ProcessException.UPDATE_PROMPT, err.getErrorType());
            assertEquals("Previous: Table 12 assigned to NYN\nNew: Table 12 assign to FGY",
                    err.getErrorMsg());
        }
    }

    @Test
    public void testAttemptReassign2_SameCandidateShould_throw_UPDATE_PROMPT() throws Exception{
        model.getAssgnList().put(14, "15WAU00001");
        attdList.getCandidate("15WAU00001").setTableNumber(14);
        attdList.getCandidate("15WAU00001").setStatus(Status.PRESENT);
        attdList.removeCandidate(cdd1.getRegNum());
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());

        try{
            model.setTempTable(12);
            model.setTempCdd(cdd1);
            model.attemptReassign();
            fail("Expected UPDATE_PROMPT but none thrown");
        }catch(ProcessException err){
            assertEquals(ProcessException.UPDATE_PROMPT, err.getErrorType());
            assertEquals("Previous: FGY assigned to Table 14\nNew: FGY assign to 12",
                    err.getErrorMsg());
        }
    }

    @Test
    public void testAttemptReassign3_NoReassignDoNothing() throws Exception{
        model.getAssgnList().put(14, "15WAU00001");
        attdList.getCandidate("15WAU00001").setTableNumber(14);
        attdList.getCandidate("15WAU00001").setStatus(Status.PRESENT);
        attdList.removeCandidate(cdd1.getRegNum());
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());

        try{
            model.setTempTable(12);
            model.setTempCdd(cdd3);
            model.attemptReassign();
        }catch(ProcessException err){
            fail("Exception --" + err.getErrorMsg() + "-- is not expected");
        }
    }

    //= AttemptNotMatch() ==========================================================================
    /**
     * attemptNotMatch()
     *
     * This method should not be called by outsider.
     * Partial method of tryAssignScanValue(...) 5th piece of 6
     *
     * This method is used to check if the candidate and table is a possible match
     * If Candidate sit at a wrong table, ERR_PAPER_NOT_MATCH should be thrown
     * Candidate's paper does not match with the table's assigned paper
     *
     * Tests:
     * 1. NOT Match Pair found, throw MESSAGE_TOAST Exception
     * 2. NOT Match Pair not found, do nothing
     */

    @Test
    public void testAttemptNotMatch1_AttendancePairNotMatch() throws Exception{
        try{
            model.setTempTable(55);
            model.setTempCdd(cdd1);
            model.attemptNotMatch();
            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException err) {
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("FGY should not sit here\nSuggest to Table 10", err.getErrorMsg());
        }
    }

    @Test
    public void testAttemptNotMatch2_AttendancePairMatch() throws Exception{
        try{
            model.setTempTable(13);
            model.setTempCdd(cdd1);
            model.attemptNotMatch();
        } catch(ProcessException err) {
            fail("Exception --" + err.getErrorMsg() + "-- is not expected");
        }
    }

    //= AssignCandidate() ==========================================================================
    /**
     * assignCandidate()
     *
     * This method should not be called by outsider.
     * Partial method of tryAssignScanValue(...) 6th piece of 6
     *
     * This method was called when all the verification and matching was done.
     * The last step of the process, it take the attendance of the candidate,
     * everything else is already correct.
     *
     * Test:
     * Set cdd1 from ABSENT to PRESENT with the given table in the attendance list
     * and assigned list. reset the display and clear the buffer.
     *
     */
    @Test
    public void testAssignCandidate() throws Exception {
        assertEquals(0, attdList.getNumberOfCandidates(Status.PRESENT));
        assertEquals(3, attdList.getNumberOfCandidates(Status.ABSENT));
        assertFalse(model.getAssgnList().containsValue(cdd1.getRegNum()));
        model.setTempCdd(cdd1);
        model.setTempTable(15);

        model.assignCandidate();

        verify(taskPresenter).resetDisplay();
        assertEquals(1, attdList.getNumberOfCandidates(Status.PRESENT));
        assertEquals(2, attdList.getNumberOfCandidates(Status.ABSENT));
        assertTrue(model.getAssgnList().containsValue(cdd1.getRegNum()));
    }

}