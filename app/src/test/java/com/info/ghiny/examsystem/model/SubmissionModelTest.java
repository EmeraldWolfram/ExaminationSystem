package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.LocalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.interfacer.HomeOptionMVP;
import com.info.ghiny.examsystem.interfacer.SubmissionMVP;
import com.info.ghiny.examsystem.manager.SortManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by FOONG on 25/12/2016.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class SubmissionModelTest {
    private SubmissionModel model;
    private SubmissionMVP.MvpMPresenter taskPresenter;
    private StaffIdentity staff;
    private Connector connector;

    private AttendanceList attendanceList;
    private ConnectionTask connectionTask;
    private JavaHost javaHost;

    private Candidate cdd1;
    private Candidate cdd2;
    private Candidate cdd3;
    private Candidate cdd4;
    private Candidate cdd5;
    private Candidate cdd6;

    @Before
    public void setUp() throws Exception {
        staff           = new StaffIdentity("id", true, "name", "M4");
        connectionTask  = Mockito.mock(ConnectionTask.class);
        javaHost        = Mockito.mock(JavaHost.class);

        connector = new Connector("add", 7032, "DUEL");
        JavaHost.setConnector(connector);
        LoginModel.setStaff(staff);

        ExternalDbLoader.setConnectionTask(connectionTask);
        ExternalDbLoader.setJavaHost(javaHost);

        attendanceList = new AttendanceList();
        cdd1 = new Candidate(11, "RMB3", "FGY", "15WAU00001", "BAME 0001", Status.PRESENT);
        cdd1.setCollector("123456");
        cdd2 = new Candidate(0, "RMB3", "NYN", "15WAU00002", "BAME 0001", Status.ABSENT);
        cdd3 = new Candidate(0, "RMB3", "LHN", "15WAU00003", "BAME 0001", Status.ABSENT);
        cdd4 = new Candidate(0, "RMB3", "Mr. Bar", "15WAU00004", "BAME 0002", Status.BARRED);
        cdd5 = new Candidate(0, "RMB3", "Ms. Exm", "15WAU00005", "BAME 0003", Status.EXEMPTED);
        cdd6 = new Candidate(0, "RMB3", "Ms. Qua", "15WAR00006", "BAME 0001", Status.QUARANTINED);

        attendanceList.addCandidate(cdd1);
        attendanceList.addCandidate(cdd2);
        attendanceList.addCandidate(cdd3);
        attendanceList.addCandidate(cdd4);
        attendanceList.addCandidate(cdd5);
        attendanceList.addCandidate(cdd6);
        TakeAttdModel.setAttdList(attendanceList);
        TakeAttdModel.getUpdatingList().clear();

        taskPresenter = Mockito.mock(SubmissionMVP.MvpMPresenter.class);
        model   = new SubmissionModel(taskPresenter);
    }

    @After
    public void tearDown() throws Exception {}

    //= UploadAttdList =============================================================================

    /**
     * uploadAttdList
     *
     * Reset Sent flag and send the attendance report to the chief
     *
     */

    @Test
    public void uploadAttdList() throws Exception {
        model.setSent(true);

        model.uploadAttdList();

        assertFalse(model.isSent());
        verify(javaHost).putMessageIntoSendQueue(anyString());
    }

    //= VerifyChiefResponse ========================================================================

    /**
     * verifyChiefResponse
     *
     * There is only 2 possibility for this method to happen
     * 1. The chief acknowledge that the attendance list he received is good without error
     * 2. The chief acknowledge that the attendance list he received have error
     *
     * Tests:
     * 1. When chief acknowledge successful, show Submission Complete message
     * 2. When chief acknowledge failure, show Error
     */
    @Test
    public void verifyChiefResponse_1_PositiveResponse() throws Exception {
        String MSG  = "{\"Result\":true,\"Type\":\"Submission\"}";

        try{
            model.setSent(false);

            model.verifyChiefResponse(MSG);
            fail("Expected MESSAGE_TOAST but nothing was thrown");
        } catch (ProcessException err) {
            assertTrue(model.isSent());
            assertEquals("Submission successful", err.getErrorMsg());
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
        }
    }

    @Test
    public void verifyChiefResponse_2_NegativeResponse() throws Exception {
        String MSG  = "{\"Result\":false,\"Type\":\"Submission\"}";

        try{
            model.setSent(false);

            model.verifyChiefResponse(MSG);
            fail("Expected MESSAGE_DIALOG but nothing was thrown");
        } catch (ProcessException err) {
            assertTrue(model.isSent());
            assertEquals("Request Failed", err.getErrorMsg());
            assertEquals(ProcessException.MESSAGE_DIALOG, err.getErrorType());
        }
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

    //= GetCandidatesWith ==========================================================================

    /**
     * getCandidatesWith(...)
     *
     * This method takes in the Sorting Method and the Attendance List
     * It then sort the display list following the sorting preference and return the list
     *
     * Tests:
     * 1. Obtaining the list sorted in ID number for PRESENT only
     * 2. Obtaining the list sorted in ID number for ABSENT only
     * 3. Obtaining the list sorted in ID number for BARRED only
     * 4. Obtaining the list sorted in ID number for EXEMPTED only
     * 5. Obtaining the list sorted in ID number for QUARANTINED only
     *
     */

    @Test
    public void testGetCandidatesWith_1() throws Exception {
        ArrayList<Candidate> testList   = model.getCandidatesWith(Status.PRESENT,
                SortManager.SortMethod.GROUP_PAPER_SORT_ID, true);

        assertEquals(1, testList.size());
        assertEquals(cdd1, testList.get(0));
    }

    @Test
    public void testGetCandidatesWith_2() throws Exception {
        ArrayList<Candidate> testList   = model.getCandidatesWith(Status.ABSENT,
                SortManager.SortMethod.GROUP_PAPER_SORT_ID, true);

        assertEquals(2, testList.size());
        assertEquals(cdd2, testList.get(0));
        assertEquals(cdd3, testList.get(1));
    }

    @Test
    public void testGetCandidatesWith_3() throws Exception {
        ArrayList<Candidate> testList   = model.getCandidatesWith(Status.BARRED,
                SortManager.SortMethod.GROUP_PAPER_SORT_ID, true);

        assertEquals(1, testList.size());
        assertEquals(cdd4, testList.get(0));
    }

    @Test
    public void testGetCandidatesWith_4() throws Exception {
        ArrayList<Candidate> testList   = model.getCandidatesWith(Status.EXEMPTED,
                SortManager.SortMethod.GROUP_PAPER_SORT_ID, true);

        assertEquals(1, testList.size());
        assertEquals(cdd5, testList.get(0));
    }

    @Test
    public void testGetCandidatesWith_5() throws Exception {
        ArrayList<Candidate> testList   = model.getCandidatesWith(Status.QUARANTINED,
                SortManager.SortMethod.GROUP_PAPER_SORT_ID, true);

        assertEquals(1, testList.size());
        assertEquals(cdd6, testList.get(0));
    }

    //= UnassignCandidate ==========================================================================

    /**
     * unassignCandidate(...)
     *
     * This method use to set a PRESENT Candidate to ABSENT reversible (use assignCandidate)
     * The method put the target Candidate into a HashMap to allow reverse back to PRESENT
     *
     * Tests:
     * 1. Unassign a PRESENT candidate successfully
     * 2. Unassign a null object, throw FATAL_ERROR
     * 3. Unassign a Candidate that is NOT PRESENT, throw FATAL_ERROR
     */
    @Test
    public void testUnassignCandidate_1_PositiveTest() throws Exception {
        try{
            HashMap<String, Integer> testMap = model.getUnassignedMap();
            assertEquals(1, attendanceList.getNumberOfCandidates(Status.PRESENT));
            assertEquals(0, TakeAttdModel.getUpdatingList().size());
            assertTrue(testMap.isEmpty());

            model.unassignCandidate(5, cdd1);

            assertFalse(testMap.isEmpty());
            assertEquals(1, testMap.size());
            assertTrue(testMap.containsKey(cdd1.getRegNum()));
            assertEquals(Status.ABSENT, cdd1.getStatus());
            assertEquals(0, attendanceList.getNumberOfCandidates(Status.PRESENT));
            assertEquals(1, TakeAttdModel.getUpdatingList().size());
        } catch (ProcessException err) {
            fail("Exception --" + err.getErrorMsg() + "-- was not Expected");
        }
    }

    @Test
    public void testUnassignCandidate_2_NullCandidate() throws Exception {
        HashMap<String, Integer> testMap = model.getUnassignedMap();
        try{
            assertEquals(1, attendanceList.getNumberOfCandidates(Status.PRESENT));
            assertEquals(0, TakeAttdModel.getUpdatingList().size());
            assertTrue(testMap.isEmpty());

            model.unassignCandidate(5, null);

            fail("Expected FATAL_ERROR but none was thrown");
        } catch (ProcessException err) {
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("Candidate Info Corrupted", err.getErrorMsg());
            assertEquals(1, attendanceList.getNumberOfCandidates(Status.PRESENT));
            assertEquals(0, TakeAttdModel.getUpdatingList().size());
            assertTrue(testMap.isEmpty());
        }
    }

    @Test
    public void testUnassignCandidate_3_CandidateThatIsNotPRESENT() throws Exception {
        HashMap<String, Integer> testMap = model.getUnassignedMap();
        try{
            assertEquals(1, attendanceList.getNumberOfCandidates(Status.PRESENT));
            assertEquals(0, TakeAttdModel.getUpdatingList().size());
            assertTrue(testMap.isEmpty());

            model.unassignCandidate(5, cdd3);

            fail("Expected FATAL_ERROR but none was thrown");
        } catch (ProcessException err) {
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("Candidate Info Corrupted", err.getErrorMsg());
            assertEquals(1, attendanceList.getNumberOfCandidates(Status.PRESENT));
            assertEquals(0, TakeAttdModel.getUpdatingList().size());
            assertTrue(testMap.isEmpty());
        }
    }

    //= AssignCandidate ============================================================================

    /**
     * assignCandidate(...)
     *
     * This method use to reverse ABSENT Candidate to PRESENT (unassignCandidate was called before)
     * The method check the HashMap see if the Candidate attendance was collected before
     *
     * Tests:
     * 1. Reverse and Assign an ABSENT candidate successfully
     * 2. Assign a null object, throw FATAL_ERROR
     * 3. Assign a Candidate that is never PRESENT before, throw MESSAGE_DIALOG
     */
    @Test
    public void testAssignCandidate_1_PositiveTest() throws Exception {
        HashMap<String, Integer> testMap = model.getUnassignedMap();
        TakeAttdModel.getUpdatingList().add(cdd1);
        try{
            model.unassignCandidate(5, cdd1);
            assertEquals(0, attendanceList.getNumberOfCandidates(Status.PRESENT));
            assertEquals(0, TakeAttdModel.getUpdatingList().size());
            assertTrue(testMap.containsKey(cdd1.getRegNum()));
            assertFalse(testMap.isEmpty());

            model.assignCandidate(cdd1);

            assertTrue(testMap.isEmpty());
            assertEquals(0, testMap.size());
            assertEquals(Status.PRESENT, cdd1.getStatus());
            assertEquals(1, attendanceList.getNumberOfCandidates(Status.PRESENT));
            assertEquals(1, TakeAttdModel.getUpdatingList().size());
        } catch (ProcessException err) {
            fail("Exception --" + err.getErrorMsg() + "-- was not Expected");
        }
    }

    @Test
    public void testAssignCandidate_2_NullCandidate() throws Exception {
        HashMap<String, Integer> testMap = model.getUnassignedMap();
        TakeAttdModel.getUpdatingList().add(cdd1);
        try{
            model.unassignCandidate(5, cdd1);
            assertEquals(0, attendanceList.getNumberOfCandidates(Status.PRESENT));
            assertEquals(0, TakeAttdModel.getUpdatingList().size());
            assertTrue(testMap.containsKey(cdd1.getRegNum()));
            assertFalse(testMap.isEmpty());

            model.assignCandidate(null);

            fail("Expected FATAL_ERROR but none was thrown");
        } catch (ProcessException err) {
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("Candidate Info Corrupted", err.getErrorMsg());
            assertEquals(0, attendanceList.getNumberOfCandidates(Status.PRESENT));
            assertEquals(0, TakeAttdModel.getUpdatingList().size());
            assertTrue(testMap.containsKey(cdd1.getRegNum()));
            assertFalse(testMap.isEmpty());
        }
    }

    @Test
    public void testAssignCandidate_3_CandidateThatIsNverPresentBefore() throws Exception {
        HashMap<String, Integer> testMap = model.getUnassignedMap();
        TakeAttdModel.getUpdatingList().add(cdd1);
        try{
            model.unassignCandidate(5, cdd1);
            assertEquals(0, attendanceList.getNumberOfCandidates(Status.PRESENT));
            assertEquals(0, TakeAttdModel.getUpdatingList().size());
            assertTrue(testMap.containsKey(cdd1.getRegNum()));
            assertFalse(testMap.isEmpty());

            model.assignCandidate(cdd3);

            fail("Expected FATAL_ERROR but none was thrown");
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Candidate is never assign before", err.getErrorMsg());
            assertEquals(0, attendanceList.getNumberOfCandidates(Status.PRESENT));
            assertEquals(0, TakeAttdModel.getUpdatingList().size());
            assertTrue(testMap.containsKey(cdd1.getRegNum()));
            assertFalse(testMap.isEmpty());
        }
    }
    //= Run() ======================================================================================
    /**
     * run()
     *
     * 1. When Submission is already complete before, do nothing
     * 3. When Submission was failed at this time, throw an error and the presenter shall handle
     */
    @Test
    public void testRun_ChiefDoRespond() throws Exception {
        model.setSent(true);
        model.run();
        verify(taskPresenter, never()).onTimesOut(any(ProcessException.class));
    }

    @Test
    public void testRun_ChiefNoRespond() throws Exception {
        model.setSent(false);
        model.run();
        verify(taskPresenter).onTimesOut(any(ProcessException.class));
    }

}