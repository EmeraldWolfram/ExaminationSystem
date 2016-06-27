package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.ExamDatabaseLoader;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.Identity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by GhinY on 15/06/2016.
 */
public class AssignHelperTest {

    AttendanceList attdList;
    CheckListLoader dbLoader;
    ExamDatabaseLoader exLoader;
    Candidate cdd1;
    Candidate cdd2;
    Candidate cdd3;
    Candidate cdd4;
    Candidate cdd5;
    Candidate testDummy;

    HashMap<String, ExamSubject> paperList;
    ExamSubject subject1;
    ExamSubject subject2;
    ExamSubject subject3;

    @Before
    public void setUp() throws Exception{
        attdList = new AttendanceList();
        cdd1 = new Candidate(1, "RMB3", "FGY", "15WAU00001", "BAME 0001", AttendanceList.Status.ABSENT);
        cdd2 = new Candidate(1, "RMB3", "NYN", "15WAU00002", "BAME 0001", AttendanceList.Status.ABSENT);
        cdd3 = new Candidate(1, "RMB3", "LHN", "15WAU00003", "BAME 0001", AttendanceList.Status.ABSENT);
        cdd4 = new Candidate(1, "RMB3", "Mr. Bar", "15WAU00004", "BAME 0002", AttendanceList.Status.BARRED);
        cdd5 = new Candidate(1, "RMB3", "Ms. Exm", "15WAU00005", "BAME 0003", AttendanceList.Status.EXEMPTED);

        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), cdd2.getStatus(), cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), cdd3.getStatus(), cdd3.getProgramme());
        attdList.addCandidate(cdd4, cdd4.getPaperCode(), cdd4.getStatus(), cdd4.getProgramme());
        attdList.addCandidate(cdd5, cdd5.getPaperCode(), cdd5.getStatus(), cdd5.getProgramme());

        paperList   = new HashMap<>();
        subject1    = new ExamSubject("BAME 0001", "SUBJECT 1", 10, new Date(), 20,
                ExamSubject.ExamVenue.H1, ExamSubject.Session.AM);
        subject2    = new ExamSubject("BAME 0002", "SUBJECT 2", 30, new Date(), 20,
                ExamSubject.ExamVenue.H2, ExamSubject.Session.PM);
        subject3    = new ExamSubject("BAME 0003", "SUBJECT 3", 50, new Date(), 20,
                ExamSubject.ExamVenue.H3, ExamSubject.Session.VM);
        paperList.put(subject1.getPaperCode(), subject1);
        paperList.put(subject2.getPaperCode(), subject2);
        paperList.put(subject3.getPaperCode(), subject3);

        dbLoader = Mockito.mock(CheckListLoader.class);
        when(dbLoader.isEmpty()).thenReturn(false);
        when(dbLoader.getLastSavedAttendanceList())
                .thenReturn(attdList.getAttendanceList())
                .thenReturn(null);

        exLoader = Mockito.mock(ExamDatabaseLoader.class);

        AssignHelper.setClDBLoader(dbLoader);
        AssignHelper.setExternalLoader(exLoader);
        AssignHelper.tempCdd = null;
        AssignHelper.tempTable = null;
        AssignHelper.assgnList = new HashMap<>();
        Candidate.setPaperList(paperList);
    }

    //= CheckCandidate =============================================================================
    //If checkCandidate() receive a null input, MESSAGE_TOAST will be thrown
    @Test
    public void testCheckCandidate_Null_input_should_throw_MESSAGE_TOAST() throws Exception {
        try{
            when(exLoader.getIdentity(null)).thenReturn(null);
            testDummy = AssignHelper.checkCandidate(null);
            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Not an Identity", err.getErrorMsg());
        }
    }

    //If checkCandidate() receive an ID that was not in the list, MESSAGE_TOAST will be thrown
    @Test
    public void testCheckCandidate_ID_Not_in_AttdList_should_throw_MESSAGE_TOAST() throws Exception{
        try{
            when(exLoader.getIdentity("15WAU22222"))
                    .thenReturn(new Identity("15WAU22222", "0", false, "Mr. Test"));
            testDummy = AssignHelper.checkCandidate("15WAU22222");
            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Mr. Test doest not belong to this venue", err.getErrorMsg());
        }
    }

    //Check if checkCandidate() can detect a candidate with status Exempted
    @Test
    public void testCheckCandidate_detect_EXEMPTED_Candidate_throw_MESSAGE_TOAST() throws Exception{
        try{
            when(exLoader.getIdentity("15WAU00005"))
                    .thenReturn(new Identity("15WAU00005", "0", false, "Ms. Exm"));

            testDummy = AssignHelper.checkCandidate("15WAU00005");
            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("The paper was exempted for Ms. Exm", err.getErrorMsg());
        }
    }

    //Check if checkCandidate() can detect a candidate with status Barred
    @Test
    public void testCheckCandidate_detect_BARRED_Candidate_throw_MESSAGE_TOAST() throws Exception {
        try{
            when(exLoader.getIdentity("15WAU00004"))
                    .thenReturn(new Identity("15WAU00004", "0", false, "Mr. Bar"));
            testDummy = AssignHelper.checkCandidate("15WAU00004");
            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Mr. Bar have been barred", err.getErrorMsg());
        }
    }

    //Check if checkCandidate() can detect the input Identity that doesn't have a regNum
    @Test
    public void testCheckCandidate_ID_without_regNum_should_throw_MESSAGE_DIALOG() throws Exception{
        try{
            when(exLoader.getIdentity("15WAU00004"))
                    .thenReturn(new Identity());
            testDummy = AssignHelper.checkCandidate("15WAU00004");
            fail("Expected MESSAGE_DIALOG but none thrown");
        } catch(ProcessException err){
            assertEquals(ProcessException.MESSAGE_DIALOG, err.getErrorType());
            assertEquals("FATAL: Unable to process ID", err.getErrorMsg());
        }
    }

    //Check if checkCandidate() can return a candidate if the candidate is valid
    @Test
    public void testCheckCandidate_Valid_ID_should_return_in_Candidate_Form() throws Exception{
        try{
            when(exLoader.getIdentity("15WAU00001"))
                    .thenReturn(new Identity("15WAU00001", "0", false, "FGY"));
            testDummy = AssignHelper.checkCandidate("15WAU00001");
            assertNotNull(testDummy);
            assertEquals(testDummy, cdd1);
        } catch(ProcessException err){
            fail("No error should be thrown but thrown ErrorMsg " + err.getErrorMsg());
        }
    }

    //Check if checkCandidate() can detect an empty attd list, should throw MESSAGE_DIALOG
    @Test
    public void testCheckCandidate_should_throw_MESSAGE_DIALOG() throws Exception{
        try{
            AssignHelper.setClDBLoader(dbLoader);
            when(exLoader.getIdentity("15WAU00001"))
                    .thenReturn(new Identity("15WAU00001", "0", false, "FGY"));
            testDummy = AssignHelper.checkCandidate("15WAU00001");
            fail("Expected MESSAGE_DIALOG but none thrown");
        } catch(ProcessException err){
            assertEquals(ProcessException.MESSAGE_DIALOG, err.getErrorType());
            assertEquals("No Attendance List", err.getErrorMsg());
        }
    }

    //=TryAssignCandidate===========================================================================
    //If both candidate and table weren't assign yet, tryAssignCandidate should return false
    @Test
    public void testTryAssignCandidate_Both_not_assign_should_return_false() throws Exception{
        try{
            assertFalse(AssignHelper.tryAssignCandidate());
        }catch(ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    //If table is not assign only, tryAssignCandidate should return false also
    @Test
    public void testTryAssignCandidate_Table_not_assign_should_return_false() throws Exception{
        try{
            AssignHelper.checkTable(12);
            assertFalse(AssignHelper.tryAssignCandidate());
        }catch(ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    //If candidate haven assign only, tryAssignCandidate should return false also
    @Test
    public void testTryAssignCandidate_Candidate_not_assign_should_return_false() throws Exception{
        try{
            when(exLoader.getIdentity("15WAU00001"))
                    .thenReturn(new Identity("15WAU00001", "0", false, "FGY"));
            AssignHelper.checkCandidate("15WAU00001");
            assertFalse(AssignHelper.tryAssignCandidate());
        }catch(ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    //If both Candidate and Table is valid, tryAssignCandidate should return true
    @Test
    public void testTryAssignCandidate_When_successful_assigned_should_return_true() throws Exception{
        try{
            when(exLoader.getIdentity("15WAU00001"))
                    .thenReturn(new Identity("15WAU00001", "0", false, "FGY"));
            AssignHelper.checkCandidate("15WAU00001");
            AssignHelper.checkTable(12);
            assertTrue(AssignHelper.tryAssignCandidate());
        }catch(ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    //If Table have been assigned before, UPDATE_PROMPT should be thrown
    @Test
    public void testTryAssignCandidate_Same_table_should_throw_UPDATE_PROMPT()throws Exception{
        HashMap<Integer, String> assgnList = new HashMap<>();

        try{
            when(exLoader.getIdentity(anyString()))
                    .thenReturn(new Identity("15WAU00002", "0", false, "NYN"))
                    .thenReturn(new Identity("15WAU00001", "0", false, "FGY"));
            AssignHelper.checkTable(12);
            AssignHelper.checkCandidate("15WAU00002");
            boolean test = AssignHelper.tryAssignCandidate();
            //Second assign 12
            AssignHelper.checkTable(12);
            AssignHelper.checkCandidate("15WAU00001");
            test = AssignHelper.tryAssignCandidate();
            fail("Expected UPDATE_PROMPT but none thrown");
        }catch(ProcessException err){
            assertEquals(ProcessException.UPDATE_PROMPT, err.getErrorType());
            assertEquals("Previous: Table 12 assigned to NYN\nNew: Table 12 assign to FGY",
                    err.getErrorMsg());
        }
    }

    //If Candidate have been assigned before, UPDATE_PROMPT should be thrown
    @Test
    public void testTryAssign_Same_candidate_should_throw_UPDATE_PROMPT() throws Exception{
        HashMap<Integer, String> assgnList = new HashMap<>();
        assgnList.put(14, "15WAU00001");

        try{
            AssignHelper.assgnList = assgnList;
            when(exLoader.getIdentity("15WAU00001"))
                    .thenReturn(new Identity("15WAU00001", "0", false, "FGY"));
            AssignHelper.checkTable(12);
            AssignHelper.checkCandidate("15WAU00001");
            boolean test = AssignHelper.tryAssignCandidate();
            fail("Expected UPDATE_PROMPT but none thrown");
        }catch(ProcessException err){
            assertEquals(ProcessException.UPDATE_PROMPT, err.getErrorType());
            assertEquals("Previous: FGY assigned to Table 1\nNew: FGY assign to 12",
                    err.getErrorMsg());
        }
    }

    //If Candidate sit at a wrong table, ERR_PAPER_NOT_MATCH should be thrown
    //Candidate's paper does not match with the table's assigned paper
    @Test
    public void testTryAssign_Paper_not_Match_should_throw_MESSAGE_TOAST() throws Exception{
        HashMap<Integer, String> assgnList = new HashMap<>();
        assgnList.put(14, "15WAU00005");

        try{
            AssignHelper.assgnList = assgnList;
            when(exLoader.getIdentity("15WAU00001"))
                    .thenReturn(new Identity("15WAU00001", "0", false, "FGY"));
            AssignHelper.checkTable(55);
            AssignHelper.checkCandidate("15WAU00001");
            boolean test = AssignHelper.tryAssignCandidate();
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
    public void testUpdateNewCandidate_reaasign_table() throws Exception{
        assertEquals(3, attdList.getNumberOfCandidates(AttendanceList.Status.ABSENT));
        when(exLoader.getIdentity(anyString()))
                .thenReturn(new Identity("15WAU00001", "0", false, "FGY"))
                .thenReturn(new Identity("15WAU00002", "0", false, "NYN"));

        AssignHelper.checkTable(14);
        AssignHelper.checkCandidate("15WAU00001");
        AssignHelper.tryAssignCandidate();

        AssignHelper.checkTable(14);
        AssignHelper.checkCandidate("15WAU00002");

        AssignHelper.updateNewCandidate();

        assertEquals(1, AssignHelper.assgnList.size());
        assertEquals("15WAU00002", AssignHelper.assgnList.get(14));
        assertEquals(1, attdList.getNumberOfCandidates(AttendanceList.Status.PRESENT));
        assertEquals(2, attdList.getNumberOfCandidates(AttendanceList.Status.ABSENT));
        assertEquals(AttendanceList.Status.ABSENT, cdd1.getStatus());
        assertEquals(AttendanceList.Status.PRESENT, cdd2.getStatus());
    }
    /*************************************************
     * Dialog -> update
     * Table 12 previously assigned to Cdd1
     * Table 14 then assign to Cdd1
     * updateNewCandidate():
     * + should renew Cdd1 to Table 14
     *************************************************/
    @Test
    public void testUpdateNewCandidate_reassign_candidate() throws Exception{
        assertEquals(3, attdList.getNumberOfCandidates(AttendanceList.Status.ABSENT));
        when(exLoader.getIdentity(anyString()))
                .thenReturn(new Identity("15WAU00001", "0", false, "FGY"))
                .thenReturn(new Identity("15WAU00001", "0", false, "FGY"));

        AssignHelper.checkTable(12);
        AssignHelper.checkCandidate("15WAU00001");
        AssignHelper.tryAssignCandidate();

        AssignHelper.checkTable(14);
        AssignHelper.checkCandidate("15WAU00001");

        AssignHelper.updateNewCandidate();

        assertEquals(1, AssignHelper.assgnList.size());
        assertNull(AssignHelper.assgnList.get(12));
        assertEquals("15WAU00001", AssignHelper.assgnList.get(14));
        assertEquals(14, (int)attdList.getCandidate("15WAU00001").getTableNumber());
        assertEquals(1, attdList.getNumberOfCandidates(AttendanceList.Status.PRESENT));
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
    public void testCancelNewCandidate_reassign_candidate() throws Exception{
        assertEquals(3, attdList.getNumberOfCandidates(AttendanceList.Status.ABSENT));
        when(exLoader.getIdentity(anyString()))
                .thenReturn(new Identity("15WAU00001", "0", false, "FGY"))
                .thenReturn(new Identity("15WAU00001", "0", false, "FGY"));

        AssignHelper.checkTable(12);
        AssignHelper.checkCandidate("15WAU00001");
        AssignHelper.tryAssignCandidate();

        AssignHelper.checkTable(14);
        AssignHelper.checkCandidate("15WAU00001");

        AssignHelper.cancelNewAssign();

        assertEquals(1, AssignHelper.assgnList.size());
        assertNull(AssignHelper.assgnList.get(14));
        assertEquals("15WAU00001", AssignHelper.assgnList.get(12));
        assertEquals(12, (int)attdList.getCandidate("15WAU00001").getTableNumber());
        assertEquals(1, attdList.getNumberOfCandidates(AttendanceList.Status.PRESENT));
    }

    /*************************************************************
     * Dialog -> cancel
     * Table 14 previously assigned to Cdd1
     * Table 14 then assign to Cdd2
     * cancelNewAssign():
     * + should remain the previous assigned table and candidate
     *************************************************************/
    @Test
    public void testCancelNewAssign_reaasign_table() throws Exception{
        assertEquals(3, attdList.getNumberOfCandidates(AttendanceList.Status.ABSENT));
        when(exLoader.getIdentity(anyString()))
                .thenReturn(new Identity("15WAU00001", "0", false, "FGY"))
                .thenReturn(new Identity("15WAU00002", "0", false, "NYN"));

        AssignHelper.checkTable(14);
        AssignHelper.checkCandidate("15WAU00001");
        AssignHelper.tryAssignCandidate();

        AssignHelper.checkTable(14);
        AssignHelper.checkCandidate("15WAU00002");

        AssignHelper.cancelNewAssign();

        assertEquals(1, AssignHelper.assgnList.size());
        assertEquals("15WAU00001", AssignHelper.assgnList.get(14));
        assertEquals(1, attdList.getNumberOfCandidates(AttendanceList.Status.PRESENT));
        assertEquals(2, attdList.getNumberOfCandidates(AttendanceList.Status.ABSENT));
        assertEquals(AttendanceList.Status.PRESENT, cdd1.getStatus());
        assertEquals(AttendanceList.Status.ABSENT, cdd2.getStatus());
    }

    //= checkScan() ================================================================================
    /**
     *  checkScan()
     *
     *  return MAYBE_TABLE when the input string length is less than 4
     */
    @Test
    public void testCheckScan_MAYBE_TABLE() throws Exception{
        try{
            int flag = AssignHelper.checkScan("012");
            assertEquals(AssignHelper.MAYBE_TABLE, flag);
        }catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getMessage());
        }
    }

    /**
     *  checkScan()
     *
     *  return MAYBE_CANDIDATE when the input string length is less than 4
     */
    @Test
    public void testCheckScan_MAYBE_CANDIDATE() throws Exception{
        try{
            int flag = AssignHelper.checkScan("666666666666");
            assertEquals(AssignHelper.MAYBE_CANDIDATE, flag);
        }catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getMessage());
        }
    }

    /**
     *  checkScan()
     *
     *  return MAYBE_CANDIDATE when the input string length is less than 4
     */
    @Test
    public void testCheckScan_Throw_Error_When_the_string_length_is_zero() throws Exception{
        try{
            int flag = AssignHelper.checkScan("");
            fail("Expected MESSAGE_TOAST but none thrown ");
        }catch (ProcessException err){
            assertEquals(err.getErrorType(), ProcessException.MESSAGE_TOAST);
            assertEquals(err.getErrorMsg(), "Not a valid QR");
        }
    }

}