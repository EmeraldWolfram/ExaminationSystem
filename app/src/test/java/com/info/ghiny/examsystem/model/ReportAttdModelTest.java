package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.interfacer.ReportAttdMVP;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by GhinY on 24/06/2016.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class ReportAttdModelTest {

    private AttendanceList attdList;

    private ReportAttdModel helper;
    private ReportAttdMVP.MPresenter taskPresenter;


    private HashMap<String, HashMap<String, HashMap<String, Candidate>>> paperList2;
    private HashMap<String, HashMap<String, HashMap<String, Candidate>>> paperList3;

    private HashMap<String, HashMap<String, Candidate>> prgList2;

    private Candidate cdd1;
    private Candidate cdd2;
    private Candidate cdd3;
    private Candidate cdd4;
    private Candidate cdd5;


    @Before
    public void setUp() throws Exception{
        attdList = new AttendanceList();

        paperList2 = new HashMap<>();
        paperList3 = new HashMap<>();
        paperList2.put("BAME 1001", new HashMap<String, HashMap<String, Candidate>>());

        prgList2 = new HashMap<>();

        cdd1 = new Candidate(1, "RMB3", "FGY", "15WAU00001", "BAME 0001", Status.ABSENT);
        cdd2 = new Candidate(1, "RMB3", "NYN", "15WAU00002", "BAME 0001", Status.ABSENT);
        cdd3 = new Candidate(1, "RMB3", "LHN", "15WAU00003", "BAME 0001", Status.ABSENT);
        cdd4 = new Candidate(1, "RMB3", "YZL", "15WAU00004", "BAME 0002", Status.BARRED);
        cdd5 = new Candidate(1, "RMB3", "SYL", "15WAU00005", "BAME 0003", Status.EXEMPTED);

        taskPresenter = Mockito.mock(ReportAttdMVP.MPresenter.class);
        helper = new ReportAttdModel(taskPresenter);

    }

    //= GetTitleList(...) =============================================================================
    /*****************************************************************************
     * getTitleList(Status)
     * return a List<String> of paperCode that is not empty in the Status List
     * Eg.
     * AttdList -> ABSENT ->    1. BAME 0001 (3 Candidates)
     *                          2. BAME 0002 (1 Candidates)
     *
     * The method should return List with BAME 0001 and BAME 0002.
     *****************************************************************************/
    @Test
    public void testGetTitleList_2subjectWithAbsentCandidate() throws Exception{
        cdd4 = new Candidate(0, "RMB3", "TS 1", "15WAU10004", "BAME 0002", Status.ABSENT);
        cdd5 = new Candidate(0, "RSF3", "TS 2", "15WAU10005", "BAME 0002", Status.ABSENT);

        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), cdd2.getStatus(), cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), cdd3.getStatus(), cdd3.getProgramme());
        attdList.addCandidate(cdd4, cdd4.getPaperCode(), cdd4.getStatus(), cdd4.getProgramme());
        attdList.addCandidate(cdd5, cdd5.getPaperCode(), cdd5.getStatus(), cdd5.getProgramme());

        TakeAttdModel.setAttdList(attdList);

        List<String> list = helper.getTitleList(Status.ABSENT);

        assertEquals(2, list.size());
        assertEquals("BAME 0001", list.get(0));
        assertEquals("BAME 0002", list.get(1));
    }

    /*************************************************************
     * getTitleList(Status)
     * If the AttendanceList object is empty
     * The method should return empty List instead of null.
     *************************************************************/
    @Test
    public void testGetTitleList_EmptyAttdListShouldNotReturnNull() throws Exception{
        TakeAttdModel.setAttdList(attdList);
        List<String> list = helper.getTitleList(Status.ABSENT);

        assertNotNull(list);
        assertEquals(0, list.size());
    }

    /*****************************************************************************
     * getTitleList(Status)
     * a paperCode with zero Candidates should be included also
     * as a delete in the PRESENT record may add a Candidate into the ABSENT
     * Therefore, the empty Candidate List should not be ignored
     *
     * Eg.
     * AttdList -> ABSENT ->    1. BAME 0001 (3 Candidates)
     *                          2. BAME 0002 (1 Candidates)
     *                          3. BAME 0003 (0 Candidates)
     *
     * The method should return List with BAME 0001, BAME 0002, BAME 0003
     *****************************************************************************/
    @Test
    public void testGetTitle_EmptyCandidateListShouldBeIgnored() throws Exception{
        cdd4 = new Candidate(0, "RMB3", "TS 1", "15WAU10004", "BAME 0002", Status.ABSENT);
        cdd5 = new Candidate(0, "RSF3", "TS 2", "15WAU10005", "BAME 0002", Status.ABSENT);

        //Create Empty PaperList (paperList3) and place into AttendanceList-------------------------
        prgList2.put("RXX2", new HashMap<String, Candidate>());
        paperList3.put("BAME 0003", prgList2);
        attdList.getAttendanceList().put(Status.ABSENT, paperList3);
        //------------------------------------------------------------------------------------------
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), cdd2.getStatus(), cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), cdd3.getStatus(), cdd3.getProgramme());
        attdList.addCandidate(cdd4, cdd4.getPaperCode(), cdd4.getStatus(), cdd4.getProgramme());
        attdList.addCandidate(cdd5, cdd5.getPaperCode(), cdd5.getStatus(), cdd5.getProgramme());

        TakeAttdModel.setAttdList(attdList);
        List<String> list = helper.getTitleList(Status.ABSENT);

        assertEquals(3, list.size());
        assertEquals("BAME 0001", list.get(0));
        assertEquals("BAME 0002", list.get(1));
        assertEquals("BAME 0003", list.get(2));
    }

    //= GetChildList() =============================================================================
    /**
     * getChildList(...)
     * should return a HashMap<PaperCode, List<Candidate>>
     * @PaperCode       the paper examined (the title reference)
     * @List<Candidate> Candidates that sit for the particular paper (the expandable body List)
     */

    @Test
    public void testGetChildList() throws Exception {
        cdd4 = new Candidate(0, "RMB3", "TS 1", "15WAU10004", "BAME 0002", Status.ABSENT);
        cdd5 = new Candidate(0, "RSF3", "TS 2", "15WAU10005", "BAME 0002", Status.ABSENT);

        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), cdd2.getStatus(), cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), cdd3.getStatus(), cdd3.getProgramme());
        attdList.addCandidate(cdd4, cdd4.getPaperCode(), cdd4.getStatus(), cdd4.getProgramme());
        attdList.addCandidate(cdd5, cdd5.getPaperCode(), cdd5.getStatus(), cdd5.getProgramme());

        TakeAttdModel.setAttdList(attdList);

        HashMap<String, List<Candidate>> testMap = helper.getChildList(Status.ABSENT);

        List<Candidate> cddList;
        assertEquals(2, testMap.size());

        //Check the first List of Candidate
        cddList = testMap.get("BAME 0001");
        assertEquals(3, cddList.size());
        assertTrue(cddList.contains(cdd1));
        assertTrue(cddList.contains(cdd2));
        assertTrue(cddList.contains(cdd3));

        //Check the second List of Candidate
        cddList = testMap.get("BAME 0002");
        assertEquals(2, cddList.size());
        assertTrue(cddList.contains(cdd4));
        assertTrue(cddList.contains(cdd5));
    }

    /*************************************************************
     * getChildList(Status)
     * If the AttendanceList object is empty
     * The method should return empty HashMap<> instead of null.
     *************************************************************/
    @Test
    public void testGetChildList_EmptyAttdListShouldNotReturnNull() throws Exception{
        TakeAttdModel.setAttdList(attdList);
        HashMap<String, List<Candidate>> map = helper.getChildList(Status.ABSENT);

        assertNotNull(map);
        assertEquals(0, map.size());
    }

    /*****************************************************************************
     * getChildList(Status)
     * a paperCode with zero Candidates should be included also
     * as a delete in the PRESENT record may add a Candidate into the ABSENT
     * Therefore, the empty Candidate List should not be ignored
     *
     * Eg.
     * AttdList -> ABSENT ->    1. BAME 0001 (3 Candidates)
     *                          2. BAME 0002 (1 Candidates)
     *                          3. BAME 0003 (0 Candidates)
     *
     * The method should return List with BAME 0001, BAME 0002, BAME 0003
     *****************************************************************************/
    @Test
    public void testGetChildList_EmptyCandidateListShouldBeIgnored() throws Exception{
        cdd4 = new Candidate(0, "RMB3", "TS 1", "15WAU10004", "BAME 0002", Status.ABSENT);
        cdd5 = new Candidate(0, "RSF3", "TS 2", "15WAU10005", "BAME 0002", Status.ABSENT);

        //Create Empty PaperList (paperList3) and place into AttendanceList-------------------------
        prgList2.put("RXX2", new HashMap<String, Candidate>());
        paperList3.put("BAME 0003", prgList2);
        attdList.getAttendanceList().put(Status.ABSENT, paperList3);
        //------------------------------------------------------------------------------------------
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), cdd2.getStatus(), cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), cdd3.getStatus(), cdd3.getProgramme());
        attdList.addCandidate(cdd4, cdd4.getPaperCode(), cdd4.getStatus(), cdd4.getProgramme());
        attdList.addCandidate(cdd5, cdd5.getPaperCode(), cdd5.getStatus(), cdd5.getProgramme());

        TakeAttdModel.setAttdList(attdList);
        HashMap<String, List<Candidate>> map = helper.getChildList(Status.ABSENT);

        assertEquals(3, map.size());

        List<Candidate> testList;
        testList = map.get("BAME 0001");
        assertEquals(3, testList.size());
        assertTrue(testList.contains(cdd1));
        assertTrue(testList.contains(cdd2));
        assertTrue(testList.contains(cdd3));

        testList = map.get("BAME 0002");
        assertEquals(2, testList.size());
        assertTrue(testList.contains(cdd4));
        assertTrue(testList.contains(cdd5));

        testList = map.get("BAME 0003");
        assertEquals(0, testList.size());
    }

    //= UnassignCandidate() ==========================================================================

    /**
     * unassignCandidate()
     *
     * remove the candidate from PRESENT list and add to ABSENT list
     *
     * 1. Successfully remove a candidate with PRESENT to ABSENT
     * 2. Throw FATAL ERROR (Corrupt) when the candidate is ABSENT when called
     * 3. Throw FATAL ERROR (Corrupt) when the candidate is BARRED when called
     * 4. Throw FATAL ERROR (Corrupt) when the candidate is EXEMPTED when called
     * 5. Throw FATAL ERRPR (Corrupt) when the candidate is not in the attendance list
     * 6. Throw FATAL ERROR (NULL POINTER) when the attendance list is null
     */

    @Test
    public void testUnassignCandidate_1_PositiveTest() throws Exception {
        cdd1.setTableNumber(12);
        cdd1.setStatus(Status.PRESENT);
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        TakeAttdModel.setAttdList(attdList);

        assertEquals(1, attdList.getNumberOfCandidates(Status.PRESENT));
        assertEquals(0, attdList.getNumberOfCandidates(Status.ABSENT));

        helper.unassignCandidate(cdd1.getTableNumber().toString(), cdd1.getExamIndex());

        assertEquals(1, attdList.getNumberOfCandidates(Status.ABSENT));
        assertEquals(0, attdList.getNumberOfCandidates(Status.PRESENT));
    }

    @Test
    public void testUnassignCandidate_2_NegativeTest() throws Exception {
        try{
            cdd1.setTableNumber(12);
            cdd1.setStatus(Status.ABSENT);
            attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
            TakeAttdModel.setAttdList(attdList);
            assertEquals(0, attdList.getNumberOfCandidates(Status.PRESENT));
            assertEquals(1, attdList.getNumberOfCandidates(Status.ABSENT));
            helper.unassignCandidate(cdd1.getTableNumber().toString(), cdd1.getExamIndex());

            fail("Expected FATAL_MESSAGE but no error was thrown");
        } catch (ProcessException err) {
            assertEquals("FATAL ERROR: Candidate Info Corrupted", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
        }
    }

    @Test
    public void testUnassignCandidate_3_NegativeTest() throws Exception {
        try{
            cdd1.setTableNumber(12);
            cdd1.setStatus(Status.BARRED);
            attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
            TakeAttdModel.setAttdList(attdList);

            helper.unassignCandidate(cdd1.getTableNumber().toString(), cdd1.getExamIndex());

            fail("Expected FATAL_MESSAGE but no error was thrown");
        } catch (ProcessException err) {
            assertEquals("FATAL ERROR: Candidate Info Corrupted", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
        }
    }

    @Test
    public void testUnassignCandidate_4_NegativeTest() throws Exception {
        try{
            cdd1.setTableNumber(12);
            cdd1.setStatus(Status.EXEMPTED);
            attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
            TakeAttdModel.setAttdList(attdList);

            helper.unassignCandidate(cdd1.getTableNumber().toString(), cdd1.getExamIndex());

            fail("Expected FATAL_MESSAGE but no error was thrown");
        } catch (ProcessException err) {
            assertEquals("FATAL ERROR: Candidate Info Corrupted", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
        }
    }

    @Test
    public void testUnassignCandidate_5_NegativeTest() throws Exception {
        try{
            TakeAttdModel.setAttdList(attdList);

            helper.unassignCandidate(cdd1.getTableNumber().toString(), cdd1.getExamIndex());

            fail("Expected FATAL_MESSAGE but no error was thrown");
        } catch (ProcessException err) {
            assertEquals("FATAL ERROR: Candidate Info Corrupted", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
        }
    }

    @Test
    public void testUnassignCandidate_6_NegativeTest() throws Exception {
        try{
            TakeAttdModel.setAttdList(null);
            helper.unassignCandidate(cdd1.getTableNumber().toString(), cdd1.getExamIndex());

            fail("Expected FATAL_MESSAGE but no error was thrown");
        } catch (ProcessException err) {
            assertEquals("FATAL ERROR: Attendance List not initialized", err.getErrorMsg());
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
        }
    }

    //= AssignCandidate() ==========================================================================

    /**
     * assignCandidate()
     *
     * place ABSENT candidate back to previous assigned PRESENT position
     *
     * 1. Successfully undo the unassign action
     * 2. Throw FATAL_MESSAGE (Corrupt) when the candidate is not assigned previously
     * 3. Throw FATAL_MESSAGE (Corrupt) when candidate not in attendance list
     * 4. Throw FATAL_MESSAGE (Null Pointer) when the attendance list is null
     */

    @Test
    public void testAssignCandidate_1_PositiveTest() throws Exception {
        cdd1.setTableNumber(12);
        cdd1.setStatus(Status.PRESENT);
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        TakeAttdModel.setAttdList(attdList);
        helper.unassignCandidate(cdd1.getTableNumber().toString(), cdd1.getExamIndex());

        assertEquals(1, attdList.getNumberOfCandidates(Status.ABSENT));
        assertEquals(0, attdList.getNumberOfCandidates(Status.PRESENT));

        helper.assignCandidate(cdd1.getExamIndex());

        assertEquals(1, attdList.getNumberOfCandidates(Status.PRESENT));
        assertEquals(0, attdList.getNumberOfCandidates(Status.ABSENT));
    }

    @Test
    public void testAssignCandidate_2_NegativeTest_UnassignNeverCallBefore() throws Exception {
        try{
            cdd1.setTableNumber(0);
            attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
            TakeAttdModel.setAttdList(attdList);
            helper.assignCandidate(cdd1.getExamIndex());

            fail("Expected FATAL_MESSAGE but none were thrown");
        } catch (ProcessException err) {
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("FATAL ERROR: Candidate is never assign before", err.getErrorMsg());
        }
    }

    @Test
    public void testAssignCandidate_3_NegativeTest_CandidateNotInAttdList() throws Exception {
        try{
            TakeAttdModel.setAttdList(attdList);
            helper.assignCandidate(cdd1.getExamIndex());

            fail("Expected FATAL_MESSAGE but none were thrown");
        } catch (ProcessException err) {
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("FATAL ERROR: Candidate Info Corrupted", err.getErrorMsg());
        }
    }

    @Test
    public void testAssignCandidate_4_NegativeTest_AttdListIsNull() throws Exception {
        try{
            helper.assignCandidate(cdd1.getExamIndex());

            fail("Expected FATAL_MESSAGE but none were thrown");
        } catch (ProcessException err) {
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("FATAL ERROR: Attendance List not initialized", err.getErrorMsg());
        }
    }

    //= UploadAttendance() =========================================================================
    /**
     * uploadAttendance()
     *
     * Check if helper send out attendance list
     *
     */
    @Test
    public void testUploadAttendance() throws Exception {
        ConnectionTask task = Mockito.mock(ConnectionTask.class);
        TCPClient tcpClient = Mockito.mock(TCPClient.class);

        ExternalDbLoader.setTcpClient(tcpClient);
        ExternalDbLoader.setConnectionTask(task);
        TakeAttdModel.setAttdList(attdList);

        helper.uploadAttdList();
        verify(tcpClient).sendMessage(anyString());
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
        helper.run();
        verify(taskPresenter, never()).onTimesOut(any(ProcessException.class));
    }

    @Test
    public void testRun_ChiefNoRespond() throws Exception {
        ConnectionTask.setCompleteFlag(false);
        helper.run();
        verify(taskPresenter).onTimesOut(any(ProcessException.class));
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
        LoginModel.setStaff(new StaffIdentity());
        LoginModel.getStaff().setPassword("CORRECT");
        try{
            helper.matchPassword("CORRECT");
        } catch (ProcessException err) {
            fail("Exception --" + err.getErrorMsg() + "-- not expected!");
        }
    }

    @Test
    public void testMatchPassword2_IncorrectPasswordReceived() throws Exception {
        LoginModel.setStaff(new StaffIdentity());
        LoginModel.getStaff().setPassword("CORRECT");
        try{
            helper.matchPassword("INCORRECT");
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Access denied. Incorrect Password", err.getErrorMsg());
        }
    }

}