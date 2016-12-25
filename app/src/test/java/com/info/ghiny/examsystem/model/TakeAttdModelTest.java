package com.info.ghiny.examsystem.model;


import android.content.DialogInterface;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.LocalDbLoader;
import com.info.ghiny.examsystem.database.Connector;
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

    private LocalDbLoader dBLoader;
    private TakeAttdModel model;
    private TakeAttdMVP.MPresenter taskPresenter;
    private StaffIdentity staff;

    private ConnectionTask connectionTask;
    private JavaHost javaHost;
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
        staff           = new StaffIdentity("id", true, "name", "M4");
        LoginModel.setStaff(staff);
        JavaHost.setConnector(new Connector("add", 7032, "DUEL"));
        connectionTask  = Mockito.mock(ConnectionTask.class);
        javaHost = Mockito.mock(JavaHost.class);
        dialog          = Mockito.mock(DialogInterface.class);

        ExternalDbLoader.setConnectionTask(connectionTask);
        ExternalDbLoader.setJavaHost(javaHost);

        attdList = new AttendanceList();
        cdd1 = new Candidate(0, "RMB3", "FGY", "15WAU00001", "BAME 0001", Status.ABSENT);
        cdd2 = new Candidate(0, "RMB3", "NYN", "15WAU00002", "BAME 0001", Status.ABSENT);
        cdd3 = new Candidate(0, "RMB3", "LHN", "15WAU00003", "BAME 0001", Status.ABSENT);
        cdd4 = new Candidate(0, "RMB3", "Mr. Bar", "15WAU00004", "BAME 0002", Status.BARRED);
        cdd5 = new Candidate(0, "RMB3", "Ms. Exm", "15WAU00005", "BAME 0003", Status.EXEMPTED);
        cdd6 = new Candidate(0, "RMB3", "Ms. Qua", "15WAR00006", "BAME 0001", Status.QUARANTINED);

        attdList.addCandidate(cdd1);
        attdList.addCandidate(cdd2);
        attdList.addCandidate(cdd3);
        attdList.addCandidate(cdd4);
        attdList.addCandidate(cdd5);
        attdList.addCandidate(cdd6);

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

        dBLoader = Mockito.mock(LocalDbLoader.class);

        taskPresenter = Mockito.mock(TakeAttdMVP.MPresenter.class);
        model   = new TakeAttdModel(taskPresenter);

        TakeAttdModel.setAttdList(attdList);

        model.setTempCdd(null);
        model.setTempTable(null);
        model.setAssgnList(new HashMap<Integer, String>());
        Candidate.setPaperList(paperList);
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
        staff.setPassword("CORRECT");
        String hashPass = staff.hmacSha("CORRECT", "DUEL");
        staff.setHashPass(hashPass);

        try{
            model.matchPassword("CORRECT");
        } catch (ProcessException err) {
            fail("Exception --" + err.getErrorMsg() + "-- not expected!");
        }
    }

    @Test
    public void testMatchPassword2_IncorrectPasswordReceived() throws Exception {
        staff.setPassword("CORRECT");
        String hashPass = staff.hmacSha("CORRECT", "DUEL");
        staff.setHashPass(hashPass);

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
     * 3. In-Charge respond with Attendance Update from another Client
     * 4. In-Charge respond with Attendance Update but format was wrong, throw an error
     *
     * @throws Exception
     */

    @Test
    public void testCheckDownloadResult3_RespondFromInCharge() throws Exception {
        String MESSAGE = "{\"Type\":\"AttendanceUpdate\",\"UpdateList\":[" +
                "{\"Attendance\":\"PRESENT\",\"AttdCollector\":\"145675\",\"Late\":false," +
                "\"TableNo\":1,\"RegNum\":\"15WAU00001\"},{\"Attendance\":\"ABSENT\"," +
                "\"Late\":false,\"TableNo\":1,\"RegNum\":\"15WAU00002\"},]}";
        try{
            assertNotNull(TakeAttdModel.getAttdList());
            assertEquals(0, TakeAttdModel.getAttdList().getNumberOfCandidates(Status.PRESENT));
            assertEquals(3, TakeAttdModel.getAttdList().getNumberOfCandidates(Status.ABSENT));

            model.checkDownloadResult(MESSAGE);

            assertEquals(1, TakeAttdModel.getAttdList().getNumberOfCandidates(Status.PRESENT));
            assertEquals(2, TakeAttdModel.getAttdList().getNumberOfCandidates(Status.ABSENT));
            assertEquals("145675", cdd1.getCollector());
            assertFalse(cdd1.isLate());
            assertEquals(Status.PRESENT, cdd1.getStatus());

        } catch (Exception err) {
            fail("Exception --" + err.getMessage() + "-- was not expected");
        }
    }

    @Test
    public void testCheckDownloadResult4_WrongRespondFromInCharge() throws Exception {
        String MESSAGE = "{\"Type\":\"AttendanceUpdate\",\"UpdateList\":[" +
                "{\"Attentor\":\"145675\",\"Late\":false}]}";
        try{
            assertNotNull(TakeAttdModel.getAttdList());
            assertEquals(0, TakeAttdModel.getAttdList().getNumberOfCandidates(Status.PRESENT));
            assertEquals(3, TakeAttdModel.getAttdList().getNumberOfCandidates(Status.ABSENT));

            model.checkDownloadResult(MESSAGE);
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_DIALOG, err.getErrorType());
            assertEquals("Update Data Corrupted\nPlease consult developer!", err.getErrorMsg());

            assertEquals(0, TakeAttdModel.getAttdList().getNumberOfCandidates(Status.PRESENT));
            assertEquals(3, TakeAttdModel.getAttdList().getNumberOfCandidates(Status.ABSENT));
        }
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
        cdd1.setTableNumber(12);
        cdd1.setStatus(Status.PRESENT);
        attdList.addCandidate(cdd1);
        attdList.addCandidate(cdd2);
        attdList.addCandidate(cdd3);
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

        assertEquals(0, model.getAssgnList().size());
        model.updateAssignList();
        assertEquals(0, model.getAssgnList().size());
    }

    //= TryAssignScanValue(...) ====================================================================
    /**
     * tryAssignScanValue(...)
     *
     * The core method of taking attendance
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
     * 5. Both Candidate & Table NOT null, receive input which is candidate, assign as candidate
     *    and clear previous displaying table and candidate
     * 6. Both Candidate & Table NOT null, receive input which is candidate, assign as candidate
     *    and clear previous displaying table and candidate
     * 7. Both are null, Input string was neither any of it, throw MESSAGE_TOAST Exception
     * 8. Candidate null only, Input string was neither any of it, throw MESSAGE_TOAST Exception
     * 9. Table null only, Input string was neither any of it, throw MESSAGE_TOAST Exception
     * 10. Both NOT null, Input string was neither any of it, throw MESSAGE_TOAST Exception
     */
    @Test
    public void testTryAssignScanValue1_AssignTableOnly() throws Exception {
        try{
            assertNull(model.getTempTable());
            assertNull(model.getTempCdd());

            model.tryAssignScanValue("13");

            verify(taskPresenter).notifyTableScanned(13);
            verify(taskPresenter, never()).notifyCandidateScanned(any(Candidate.class));
            verify(taskPresenter, never()).notifyDisplayReset();

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

            verify(taskPresenter, never()).notifyTableScanned(13);
            verify(taskPresenter).notifyCandidateScanned(any(Candidate.class));
            verify(taskPresenter, never()).notifyDisplayReset();

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

            verify(taskPresenter).notifyTableScanned(13);
            verify(taskPresenter, never()).notifyCandidateScanned(any(Candidate.class));
            verify(taskPresenter, never()).notifyDisplayReset();
            assertNotNull(model.getTempTable());
            assertNotNull(model.getTempCdd());
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

            verify(taskPresenter, never()).notifyTableScanned(anyInt());
            verify(taskPresenter).notifyCandidateScanned(cdd1);
            verify(taskPresenter, never()).notifyDisplayReset();
            assertNotNull(model.getTempTable());
            assertNotNull(model.getTempCdd());
        }
    }

    @Test
    public void testTryAssignScanValue5_BothAssigned() throws Exception {
        try{
            model.setTempCdd(cdd2);
            model.setTempTable(13);

            model.tryAssignScanValue("15WAU00001");

            verify(taskPresenter).notifyDisplayReset();
            verify(taskPresenter, never()).notifyTableScanned(anyInt());
            verify(taskPresenter).notifyCandidateScanned(cdd1);
            assertNull(model.getTempTable());
            assertNotNull(model.getTempCdd());
        } catch (ProcessException err) {
            fail("Exception --" + err.getErrorMsg() + "-- is not expected");
        }
    }

    @Test
    public void testTryAssignScanValue6_BothAssigned() throws Exception {
        try{
            model.setTempCdd(cdd2);
            model.setTempTable(13);

            model.tryAssignScanValue("45");

            verify(taskPresenter).notifyDisplayReset();
            verify(taskPresenter).notifyTableScanned(45);
            verify(taskPresenter, never()).notifyCandidateScanned(cdd1);
            assertNotNull(model.getTempTable());
            assertNull(model.getTempCdd());
        } catch (ProcessException err) {
            fail("Exception --" + err.getErrorMsg() + "-- is not expected");
        }
    }

    @Test
    public void testTryAssignScanValue7_InvalidStringBothNull() throws Exception {
        try{
            assertNull(model.getTempTable());
            assertNull(model.getTempCdd());

            model.tryAssignScanValue("AXXD");

            fail("Expected MESSAGE_TOAST exception but none thrown");
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Not a valid QR", err.getErrorMsg());

            verify(taskPresenter, never()).notifyTableScanned(anyInt());
            verify(taskPresenter, never()).notifyCandidateScanned(cdd1);
            verify(taskPresenter, never()).notifyDisplayReset();
            assertNull(model.getTempTable());
            assertNull(model.getTempCdd());
        }
    }

    @Test
    public void testTryAssignScanValue8_InvalidStringCandidateNull() throws Exception {
        try{
            model.setTempTable(13);
            assertNotNull(model.getTempTable());
            assertNull(model.getTempCdd());

            model.tryAssignScanValue("AXXD");

            fail("Expected MESSAGE_TOAST exception but none thrown");
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Not a valid QR", err.getErrorMsg());

            verify(taskPresenter, never()).notifyTableScanned(anyInt());
            verify(taskPresenter, never()).notifyCandidateScanned(cdd1);
            verify(taskPresenter, never()).notifyDisplayReset();
            assertNotNull(model.getTempTable());
            assertNull(model.getTempCdd());
        }
    }

    @Test
    public void testTryAssignScanValue9_InvalidStringTableNull() throws Exception {
        try{
            model.setTempCdd(cdd1);
            assertNull(model.getTempTable());
            assertNotNull(model.getTempCdd());

            model.tryAssignScanValue("AXXD");

            fail("Expected MESSAGE_TOAST exception but none thrown");
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Not a valid QR", err.getErrorMsg());

            verify(taskPresenter, never()).notifyTableScanned(anyInt());
            verify(taskPresenter, never()).notifyCandidateScanned(cdd1);
            verify(taskPresenter, never()).notifyDisplayReset();
            assertNull(model.getTempTable());
            assertNotNull(model.getTempCdd());
        }
    }

    @Test
    public void testTryAssignScanValue10_InvalidStringBothAssigned() throws Exception {
        try{
            model.setTempCdd(cdd1);
            model.setTempTable(13);
            assertNotNull(model.getTempTable());
            assertNotNull(model.getTempCdd());

            model.tryAssignScanValue("AXXD");

            fail("Expected MESSAGE_TOAST exception but none thrown");
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Not a valid QR", err.getErrorMsg());

            verify(taskPresenter, never()).notifyTableScanned(anyInt());
            verify(taskPresenter, never()).notifyCandidateScanned(cdd1);
            verify(taskPresenter, never()).notifyDisplayReset();
            assertNotNull(model.getTempTable());
            assertNotNull(model.getTempCdd());
        }
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
        attdList.addCandidate(cdd1);
        model.getAssgnList().put(13, cdd1.getRegNum());
        model.setTempTable(13);
        model.setTempCdd(cdd2);

        model.onClick(dialog, DialogInterface.BUTTON_NEUTRAL);

        assertTrue(model.getAssgnList().containsValue(cdd1.getRegNum()));
        assertFalse(model.getAssgnList().containsValue(cdd2.getRegNum()));
        assertNull(model.getTempCdd());
        assertNull(model.getTempTable());
        verify(dialog).cancel();
        verify(taskPresenter).notifyDisplayReset();
    }

    @Test
    public void testOnClickNegativeButton() throws Exception {
        attdList.removeCandidate(cdd1.getRegNum());
        cdd1.setTableNumber(13);
        cdd1.setStatus(Status.PRESENT);
        attdList.addCandidate(cdd1);
        model.getAssgnList().put(13, cdd1.getRegNum());
        model.setTempTable(13);
        model.setTempCdd(cdd2);

        model.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);

        assertTrue(model.getAssgnList().containsValue(cdd1.getRegNum()));
        assertFalse(model.getAssgnList().containsValue(cdd2.getRegNum()));
        assertNull(model.getTempCdd());
        assertNull(model.getTempTable());
        verify(dialog).cancel();
        verify(taskPresenter).notifyDisplayReset();
    }

    @Test
    public void testOnClickPositiveButton() throws Exception {
        attdList.removeCandidate(cdd1.getRegNum());
        cdd1.setTableNumber(13);
        cdd1.setStatus(Status.PRESENT);
        attdList.addCandidate(cdd1);
        model.getAssgnList().put(13, cdd1.getRegNum());
        model.setTempTable(13);
        model.setTempCdd(cdd2);

        model.onClick(dialog, DialogInterface.BUTTON_POSITIVE);

        assertTrue(model.getAssgnList().containsValue(cdd2.getRegNum()));
        assertFalse(model.getAssgnList().containsValue(cdd1.getRegNum()));
        assertNotNull(model.getTempCdd());
        assertNotNull(model.getTempTable());
        verify(dialog).cancel();
        verify(taskPresenter, never()).notifyDisplayReset();
    }

    //= TagAsLate() ================================================================================
    /**
     * tagAsLateNot()
     *
     * This method tag the candidate as late and tag the next candidate if candidate is null
     *
     * Tests:
     * 1. Candidate NOT null and not late, tag the candidate as late
     * 2. Candidate is null and tagNextLate is false, set tagNextLate as true
     * 3. Candidate NOT null and late, untag the candidate to make him/her NOT late
     * 4. Candidate is null and tagNextLate is true, set tagNextLate as false
     */
    @Test
    public void testTagAsLate1_CandidateNotNullAndNotLate() throws Exception{
        model.setTempCdd(cdd1);
        assertFalse(model.getTempCdd().isLate());
        assertFalse(model.isTagNextLate());

        model.tagAsLateNot();

        verify(taskPresenter).notifyTagUntag(true);
        assertTrue(model.getTempCdd().isLate());
        assertFalse(model.isTagNextLate());
    }

    @Test
    public void testTagAsLate2_CandidateNullAndTagIsFalse() throws Exception{
        model.setTempCdd(null);
        assertNull(model.getTempCdd());
        assertFalse(model.isTagNextLate());

        model.tagAsLateNot();

        verify(taskPresenter).notifyTagUntag(true);
        assertNull(model.getTempCdd());
        assertTrue(model.isTagNextLate());

        model.tryAssignScanValue("15WAU00002");

        assertFalse(model.isTagNextLate());
        assertTrue(cdd2.isLate());
    }

    @Test
    public void testTagAsLate3_CandidateNotNullButLate() throws Exception{
        model.setTempCdd(cdd1);
        cdd1.setLate(true);
        assertTrue(model.getTempCdd().isLate());
        assertFalse(model.isTagNextLate());

        model.tagAsLateNot();

        verify(taskPresenter).notifyTagUntag(false);
        assertFalse(model.getTempCdd().isLate());
        assertFalse(model.isTagNextLate());
    }

    @Test
    public void testTagAsLate4_CandidateNullAndTagIsTrue() throws Exception{
        model.setTempCdd(null);
        model.setTagNextLate(true);
        assertNull(model.getTempCdd());
        assertTrue(model.isTagNextLate());

        model.tagAsLateNot();

        verify(taskPresenter).notifyTagUntag(false);
        assertNull(model.getTempCdd());
        assertFalse(model.isTagNextLate());

        model.tryAssignScanValue("15WAU00002");

        assertFalse(model.isTagNextLate());
        assertFalse(cdd2.isLate());
    }

    //= VerifyTable(...) ===========================================================================
    /**
     * verifyTable(...)
     *
     * This method verify if the scan string is a table
     * if yes, return true
     * else return false
     *
     * Tests:
     * 1. Input string was a valid table
     * 2. Input string was not a full digit number (not possible to be table)
     *
     */
    @Test
    public void testVerifyTable1_ValidNumberString() throws Exception {
        try{
            assertTrue(model.verifyTable("12"));

            assertNotNull(model.getTempTable());
            assertEquals(12, model.getTempTable().intValue());
        } catch (ProcessException err) {
            fail("Exception --" + err.getErrorMsg() + "-- is not expected");
        }
    }

    @Test
    public void testVerifyTable2_NotFullDigitNumber() throws Exception {
        try{
            assertFalse(model.verifyTable("3A"));

            assertNull(model.getTempTable());
        } catch (ProcessException err) {
            fail("Exception --" + err.getErrorMsg() + "-- is not expected");
        }
    }

    //= VerifyCandidate() ==========================================================================
    /**
     * verifyCandidate()
     *
     * This method should NOT be called by outsider.
     * Partial method of tryAssignScanValue(...) 2nd piece of 6
     *
     * This method used to verify the scan String is a candidate register number
     * If verification passed, the candidate will be register into the candidate buffer
     * Exception will be thrown if verification failed
     * return true if candidate registered in buffer and return false if not
     *
     * Tests:
     * 1. Input String is a candidate, but not in attendance list. (Other venue maybe) Throw....
     * 2. Input String is a candidate, but listed as EXEMPTED. Throw MESSAGE_TOAST
     * 3. Input String is a candidate, but listed as BARRED. Throw MESSAGE_TOAST
     * 4. Input String is a candidate, but listed as QUARANTINED. Throw MESSAGE_TOAST
     * 5. Input String is a valid candidate, register into the buffer
     * 6. Attendance List is not initialized yet, throw FATAL_MESSAGE
     */
    @Test
    public void testVerifyCandidate1_NotListedCandidateThrowMessageToast() throws Exception{
        try{
            assertFalse(model.verifyCandidate("15WAU22222"));;

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException err){
            assertNull(model.getTempCdd());
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("15WAU22222 doest not belong to this venue", err.getErrorMsg());
        }
    }

    @Test
    public void testVerifyCandidate2_CandidateExemptedThrowMessageToast() throws Exception{
        try{
            assertFalse(model.verifyCandidate("15WAU00005"));

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException err){
            assertNull(model.getTempCdd());
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("The paper was exempted for Ms. Exm", err.getErrorMsg());
        }
    }

    @Test
    public void testVerifyCandidate3_CandidateBarredThrowMessageToast() throws Exception {
        try{
            model.verifyCandidate("15WAU00004");

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException err){
            assertNull(model.getTempCdd());
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Mr. Bar have been barred", err.getErrorMsg());
        }
    }

    @Test
    public void testVerifyCandidate4_CandidateQuarantinedThrowMessageToast() throws Exception{
        try{
            model.verifyCandidate("15WAR00006");

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException err){
            assertNull(model.getTempCdd());
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("The paper was quarantined for Ms. Qua", err.getErrorMsg());
        }
    }

    @Test
    public void testVerifyCandidate5_CandidateIsValid() throws Exception{
        try{
            assertTrue(model.verifyCandidate("15WAU00001"));

            assertNotNull(model.getTempCdd());
            assertEquals(model.getTempCdd(), cdd1);
        } catch(ProcessException err){
            fail("No error should be thrown but thrown ErrorMsg " + err.getErrorMsg());
        }
    }

    @Test
    public void testVerifyCandidate6_AttendanceListNotInitializeYet() throws Exception{
        try{
            TakeAttdModel.setAttdList(null);
            model.verifyCandidate("15WAU00001");

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
        attdList.addCandidate(cdd2);
        model.getAssgnList().put(12, cdd2.getRegNum());

        try{
            model.setTempTable(12);
            model.setTempCdd(cdd1);

            model.tryAssignCandidate();

            fail("Expected UPDATE_PROMPT but none thrown");
        }catch(ProcessException err){
            assertEquals(ProcessException.UPDATE_PROMPT, err.getErrorType());
            assertEquals("Previous: \nTable 12 assigned to NYN\nNew: \nTable 12 assign to FGY",
                    err.getErrorMsg());
        }
    }

    @Test
    public void testTryAssignCandidate6_ReassignCandidateThrowUpdatePrompt() throws Exception{
        attdList.removeCandidate(cdd1.getRegNum());
        cdd1.setStatus(Status.PRESENT);
        cdd1.setTableNumber(14);
        attdList.addCandidate(cdd1);
        model.getAssgnList().put(14, cdd1.getRegNum());

        try{
            model.setTempTable(12);
            model.setTempCdd(cdd1);

            model.tryAssignCandidate();

            fail("Expected UPDATE_PROMPT but none thrown");
        }catch(ProcessException err){
            assertEquals(ProcessException.UPDATE_PROMPT, err.getErrorType());
            assertEquals("Previous: \nFGY assigned to Table 14\nNew: \nFGY assign to 12",
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
     * updateNewAssignment():
     * + should reset Cdd1 to Table 0 and ABSENT
     * + should assign Cdd2 to Table 14 and PRESENT
    *************************************************/
    @Test
    public void testUpdateNewCandidate_ReassignTable() throws Exception{
        assertEquals(3, attdList.getNumberOfCandidates(Status.ABSENT));

        model.setTempTable(14);
        model.setTempCdd(attdList.getCandidate("15WAU00001"));
        model.tryAssignCandidate();

        model.setTempTable(14);
        model.setTempCdd(attdList.getCandidate("15WAU00002"));

        model.updateNewAssignment();

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
     * updateNewAssignment():
     * + should renew Cdd1 to Table 14
     *************************************************/
    @Test
    public void testUpdateNewCandidate_ReassignCandidate() throws Exception{
        assertEquals(3, attdList.getNumberOfCandidates(Status.ABSENT));

        model.setTempTable(12);
        model.setTempCdd(attdList.getCandidate("15WAU00001"));
        model.tryAssignCandidate();

        model.setTempTable(14);
        model.setTempCdd(attdList.getCandidate("15WAU00001"));

        model.updateNewAssignment();

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

        model.setTempTable(12);
        model.setTempCdd(attdList.getCandidate("15WAU00001"));
        model.tryAssignCandidate();

        model.setTempTable(14);
        model.setTempCdd(attdList.getCandidate("15WAU00001"));

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

        model.setTempTable(14);
        model.setTempCdd(attdList.getCandidate("15WAU00001"));
        model.tryAssignCandidate();

        model.setTempTable(14);
        model.setTempCdd(attdList.getCandidate("15WAU00002"));

        model.cancelNewAssign();

        assertEquals(1, model.getAssgnList().size());
        assertEquals("15WAU00001", model.getAssgnList().get(14));
        assertEquals(1, attdList.getNumberOfCandidates(Status.PRESENT));
        assertEquals(2, attdList.getNumberOfCandidates(Status.ABSENT));
        assertEquals(Status.PRESENT, cdd1.getStatus());
        assertEquals(Status.ABSENT, cdd2.getStatus());
    }

    //= ResetAttendanceAssignment() ================================================================
    /**
     * resetAttendanceAssignment()
     *
     * This method reset the assigned attendance on display
     * If the display is not filled or partially filled, only clear the buffer but no removal occur
     *
     * Tests:
     * 1. Table = null, Candidate != null
     *    - Clear candidate only
     * 2. Table != null, Candidate = null
     *    - Clear table only
     * 3. Table != null, Candidate != null
     *    - Undo assign of that candidate
     */
    @Test
    public void testResetAttendance_TableNull() throws Exception {
        model.setTempCdd(cdd1);
        assertNull(model.getTempTable());
        assertNotNull(model.getTempCdd());
        cdd1.setStatus(Status.PRESENT);
        attdList.addCandidate(cdd1);
        assertEquals(1, attdList.getNumberOfCandidates(Status.PRESENT));

        model.resetAttendanceAssignment();

        assertEquals(1, attdList.getNumberOfCandidates(Status.PRESENT));
        assertNull(model.getTempCdd());
        assertNull(model.getTempTable());
        verify(taskPresenter).notifyDisplayReset();
    }

    @Test
    public void testResetAttendance_CandidateNull() throws Exception {
        model.setTempTable(14);
        assertNotNull(model.getTempTable());
        assertNull(model.getTempCdd());

        model.resetAttendanceAssignment();

        assertNull(model.getTempCdd());
        assertNull(model.getTempTable());
        verify(taskPresenter).notifyDisplayReset();
    }

    @Test
    public void testResetAttendance_BothNotNull() throws Exception {
        model.setTempTable(14);
        model.setTempCdd(cdd1);
        assertNotNull(model.getTempTable());
        assertNotNull(model.getTempCdd());
        cdd1.setStatus(Status.PRESENT);
        attdList.removeCandidate(cdd1.getRegNum());
        attdList.addCandidate(cdd1);
        assertEquals(1, attdList.getNumberOfCandidates(Status.PRESENT));
        assertEquals(2, attdList.getNumberOfCandidates(Status.ABSENT));

        model.resetAttendanceAssignment();

        assertEquals(0, attdList.getNumberOfCandidates(Status.PRESENT));
        assertEquals(3, attdList.getNumberOfCandidates(Status.ABSENT));
        verify(taskPresenter).notifyDisplayReset();
    }

    //= AttemptReassign() ==========================================================================
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
            assertEquals("Previous: \nTable 12 assigned to NYN\nNew: \nTable 12 assign to FGY",
                    err.getErrorMsg());
        }
    }

    @Test
    public void testAttemptReassign2_SameCandidateShould_throw_UPDATE_PROMPT() throws Exception{
        model.getAssgnList().put(14, "15WAU00001");
        attdList.getCandidate("15WAU00001").setTableNumber(14);
        attdList.getCandidate("15WAU00001").setStatus(Status.PRESENT);
        attdList.removeCandidate(cdd1.getRegNum());
        attdList.addCandidate(cdd1);

        try{
            model.setTempTable(12);
            model.setTempCdd(cdd1);
            model.attemptReassign();
            fail("Expected UPDATE_PROMPT but none thrown");
        }catch(ProcessException err){
            assertEquals(ProcessException.UPDATE_PROMPT, err.getErrorType());
            assertEquals("Previous: \nFGY assigned to Table 14\nNew: \nFGY assign to 12",
                    err.getErrorMsg());
        }
    }

    @Test
    public void testAttemptReassign3_NoReassignDoNothing() throws Exception{
        model.getAssgnList().put(14, "15WAU00001");
        attdList.getCandidate("15WAU00001").setTableNumber(14);
        attdList.getCandidate("15WAU00001").setStatus(Status.PRESENT);
        attdList.removeCandidate(cdd1.getRegNum());
        attdList.addCandidate(cdd1);

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

        model.assignCandidate("24680", cdd1.getRegNum(), 15, false);

        assertEquals(1, attdList.getNumberOfCandidates(Status.PRESENT));
        assertEquals(2, attdList.getNumberOfCandidates(Status.ABSENT));
        assertTrue(model.getAssgnList().containsValue(cdd1.getRegNum()));
    }

    //=== RxAttendanceUpdate =======================================================================
    /**
     * rxAttendanceUpdate(...)
     *
     * This method process the update received from any Client
     * TODO: Not complete yet - store in preUpdateList
     *
     */
    @Test
    public void testRxAttendanceUpdate1_PositiveTest() throws Exception {

    }

    @Test
    public void testRxAttendanceUpdate2_NegativeTest() throws Exception {

    }

    //=== TxAttendanceUpdate =======================================================================
    /**
     * txAttendanceUpdate(...)
     *
     * This method send out the newly collected attendance out
     * TODO: Clear the preUpdateList and interval for updating
     *
     */
    @Test
    public void testTxAttendanceUpdate() throws Exception {

    }

}