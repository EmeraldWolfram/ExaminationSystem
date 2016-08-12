package com.info.ghiny.examsystem.model;


import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.LocalDbLoader;
import com.info.ghiny.examsystem.database.Session;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.manager.AssignManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Calendar;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by GhinY on 15/06/2016.
 */
public class AssignModelTest {

    AttendanceList attdList;
    CheckListLoader dBLoader;
    AssignModel assgnHelper;
    AssignManager manager;
    Candidate cdd1;
    Candidate cdd2;
    Candidate cdd3;
    Candidate cdd4;
    Candidate cdd5;
    Candidate cdd6;
    Candidate testDummy;

    HashMap<String, ExamSubject> paperList;
    ExamSubject subject1;
    ExamSubject subject2;
    ExamSubject subject3;

    @Before
    public void setUp() throws Exception{
        attdList = new AttendanceList();
        cdd1 = new Candidate(1, "RMB3", "FGY", "15WAU00001", "BAME 0001", Status.ABSENT);
        cdd2 = new Candidate(1, "RMB3", "NYN", "15WAU00002", "BAME 0001", Status.ABSENT);
        cdd3 = new Candidate(1, "RMB3", "LHN", "15WAU00003", "BAME 0001", Status.ABSENT);
        cdd4 = new Candidate(1, "RMB3", "Mr. Bar", "15WAU00004", "BAME 0002", Status.BARRED);
        cdd5 = new Candidate(1, "RMB3", "Ms. Exm", "15WAU00005", "BAME 0003", Status.EXEMPTED);
        cdd6 = new Candidate(1, "RMB3", "Ms. Qua", "15WAR00006", "BAME 0001", Status.QUARANTIZED);

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

        manager = Mockito.mock(AssignManager.class);
        dBLoader = Mockito.mock(CheckListLoader.class);
        AssignModel.setAttdList(attdList);

        assgnHelper = new AssignModel(manager);
        assgnHelper.initLoader(dBLoader);
        assgnHelper.setTempCdd(null);
        assgnHelper.setTempTable(null);
        AssignModel.setAssgnList(new HashMap<Integer, String>());
        Candidate.setPaperList(paperList);
    }

    //= CheckCandidate =============================================================================
    //If checkCandidate() receive a null input, MESSAGE_TOAST will be thrown
    @Test
    public void testCheckCandidate_Null_input_should_throw_MESSAGE_TOAST() throws Exception {
        try{
            assgnHelper.checkCandidate(null);
            testDummy = assgnHelper.getTempCdd();
            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Scanning a null value", err.getErrorMsg());
        }
    }

    //If checkCandidate() receive an ID that was not in the list, MESSAGE_TOAST will be thrown
    @Test
    public void testCheckCandidate_ID_Not_in_AttdList_should_throw_MESSAGE_TOAST() throws Exception{
        try{
            assgnHelper.checkCandidate("15WAU22222");
            testDummy = assgnHelper.getTempCdd();
            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException err){
            //assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("15WAU22222 doest not belong to this venue", err.getErrorMsg());
        }
    }

    //Check if checkCandidate() can detect a candidate with status Exempted
    @Test
    public void testCheckCandidate_detect_EXEMPTED_Candidate_throw_MESSAGE_TOAST() throws Exception{
        try{
            assgnHelper.checkCandidate("15WAU00005");
            testDummy = assgnHelper.getTempCdd();
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
            assgnHelper.checkCandidate("15WAU00004");
            testDummy = assgnHelper.getTempCdd();
            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Mr. Bar have been barred", err.getErrorMsg());
        }
    }

    //Check if checkCandidate() can detect a candidate with status Quarantized
    @Test
    public void testCheckCandidate_detect_QUARANTINZED_Candidate_throw_MESSAGE_TOAST() throws Exception{
        try{
            assgnHelper.checkCandidate("15WAR00006");
            testDummy = assgnHelper.getTempCdd();
            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("The paper was quarantized for Ms. Qua", err.getErrorMsg());
        }
    }

    //Check if checkCandidate() can return a candidate if the candidate is valid
    @Test
    public void testCheckCandidate_Valid_ID_should_return_in_Candidate_Form() throws Exception{
        try{
            //when(exLoader.getIdentity("15WAU00001"))
             //       .thenReturn(new StaffIdentity("15WAU00001", "0", false, "FGY"));
            assgnHelper.checkCandidate("15WAU00001");
            testDummy = assgnHelper.getTempCdd();
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
            AssignModel.setAttdList(null);
            assgnHelper.checkCandidate("15WAU00001");
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
            assertFalse(assgnHelper.tryAssignCandidate());
        }catch(ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    //If table is not assign only, tryAssignCandidate should return false also
    @Test
    public void testTryAssignCandidate_Table_not_assign_should_return_false() throws Exception{
        try{
            assgnHelper.checkTable("12");
            assertFalse(assgnHelper.tryAssignCandidate());
        }catch(ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    //If candidate haven assign only, tryAssignCandidate should return false also
    @Test
    public void testTryAssignCandidate_Candidate_not_assign_should_return_false() throws Exception{
        try{
            assgnHelper.checkCandidate("15WAU00001");
            assertFalse(assgnHelper.tryAssignCandidate());
        }catch(ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    //If both Candidate and Table is valid, tryAssignCandidate should return true
    @Test
    public void testTryAssignCandidate_When_successful_assigned_should_return_true() throws Exception{
        try{
            assgnHelper.checkCandidate("15WAU00001");
            assgnHelper.checkTable("12");
            assertTrue(assgnHelper.tryAssignCandidate());
        }catch(ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    //If Table have been assigned before, UPDATE_PROMPT should be thrown
    @Test
    public void testTryAssignCandidate_Same_table_should_throw_UPDATE_PROMPT()throws Exception{
        HashMap<Integer, String> assgnList = new HashMap<>();

        try{
            assgnHelper.checkTable("12");
            assgnHelper.checkCandidate("15WAU00002");
            boolean test = assgnHelper.tryAssignCandidate();
            //Second assign 12
            assgnHelper.checkTable("12");
            assgnHelper.checkCandidate("15WAU00001");
            test = assgnHelper.tryAssignCandidate();
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
            AssignModel.setAssgnList(assgnList);
            assgnHelper.checkTable("12");
            assgnHelper.checkCandidate("15WAU00001");
            boolean test = assgnHelper.tryAssignCandidate();
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
            AssignModel.setAssgnList(assgnList);
            assgnHelper.checkTable("55");
            assgnHelper.checkCandidate("15WAU00001");
            boolean test = assgnHelper.tryAssignCandidate();
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
        assertEquals(3, attdList.getNumberOfCandidates(Status.ABSENT));

        assgnHelper.checkTable("14");
        assgnHelper.checkCandidate("15WAU00001");
        assgnHelper.tryAssignCandidate();

        assgnHelper.checkTable("14");
        assgnHelper.checkCandidate("15WAU00002");

        assgnHelper.updateNewCandidate();

        assertEquals(1, AssignModel.getAssgnList().size());
        assertEquals("15WAU00002", AssignModel.getAssgnList().get(14));
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
    public void testUpdateNewCandidate_reassign_candidate() throws Exception{
        assertEquals(3, attdList.getNumberOfCandidates(Status.ABSENT));

        assgnHelper.checkTable("12");
        assgnHelper.checkCandidate("15WAU00001");
        assgnHelper.tryAssignCandidate();

        assgnHelper.checkTable("14");
        assgnHelper.checkCandidate("15WAU00001");

        assgnHelper.updateNewCandidate();

        assertEquals(1, AssignModel.getAssgnList().size());
        assertNull(AssignModel.getAssgnList().get(12));
        assertEquals("15WAU00001", AssignModel.getAssgnList().get(14));
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
    public void testCancelNewCandidate_reassign_candidate() throws Exception{
        assertEquals(3, attdList.getNumberOfCandidates(Status.ABSENT));

        assgnHelper.checkTable("12");
        assgnHelper.checkCandidate("15WAU00001");
        assgnHelper.tryAssignCandidate();

        assgnHelper.checkTable("14");
        assgnHelper.checkCandidate("15WAU00001");

        assgnHelper.cancelNewAssign();

        assertEquals(1, AssignModel.getAssgnList().size());
        assertNull(AssignModel.getAssgnList().get(14));
        assertEquals("15WAU00001", AssignModel.getAssgnList().get(12));
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
    public void testCancelNewAssign_reaasign_table() throws Exception{
        assertEquals(3, attdList.getNumberOfCandidates(Status.ABSENT));

        assgnHelper.checkTable("14");
        assgnHelper.checkCandidate("15WAU00001");
        assgnHelper.tryAssignCandidate();

        assgnHelper.checkTable("14");
        assgnHelper.checkCandidate("15WAU00002");

        assgnHelper.cancelNewAssign();

        assertEquals(1, AssignModel.getAssgnList().size());
        assertEquals("15WAU00001", AssignModel.getAssgnList().get(14));
        assertEquals(1, attdList.getNumberOfCandidates(Status.PRESENT));
        assertEquals(2, attdList.getNumberOfCandidates(Status.ABSENT));
        assertEquals(Status.PRESENT, cdd1.getStatus());
        assertEquals(Status.ABSENT, cdd2.getStatus());
    }

    //= attempReassign() ===========================================================================
    //If Table have been assigned before, UPDATE_PROMPT should be thrown
    @Test
    public void testAttempReassign_Same_table_should_throw_UPDATE_PROMPT()throws Exception{
        HashMap<Integer, String> assgnList = new HashMap<>();

        try{
            assgnHelper.checkTable("12");
            assgnHelper.checkCandidate("15WAU00002");
            boolean test = assgnHelper.tryAssignCandidate();
            //Second assign 12
            assgnHelper.checkTable("12");
            assgnHelper.checkCandidate("15WAU00001");
            assgnHelper.attempReassign();
            fail("Expected UPDATE_PROMPT but none thrown");
        }catch(ProcessException err){
            assertEquals(ProcessException.UPDATE_PROMPT, err.getErrorType());
            assertEquals("Previous: Table 12 assigned to NYN\nNew: Table 12 assign to FGY",
                    err.getErrorMsg());
        }
    }

    //If Candidate have been assigned before, UPDATE_PROMPT should be thrown
    @Test
    public void testAttempReassign_Same_candidate_should_throw_UPDATE_PROMPT() throws Exception{
        HashMap<Integer, String> assgnList = new HashMap<>();
        assgnList.put(14, "15WAU00001");

        try{
            AssignModel.setAssgnList(assgnList);
            assgnHelper.checkTable("12");
            assgnHelper.checkCandidate("15WAU00001");
            assgnHelper.attempReassign();
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
    public void testAttempInvalidTable_Paper_not_Match_should_throw_MESSAGE_TOAST() throws Exception{
        HashMap<Integer, String> assgnList = new HashMap<>();
        assgnList.put(14, "15WAU00005");

        try{
            AssignModel.setAssgnList(assgnList);
            assgnHelper.checkTable("55");
            assgnHelper.checkCandidate("15WAU00001");
            assgnHelper.attempInvalidSeat();
            fail("Expected MESSAGE_TOAST but none thrown");
        }catch(ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("FGY should not sit here\nSuggest to Table 10", err.getErrorMsg());
        }
    }


}