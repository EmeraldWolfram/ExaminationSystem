package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.CheckListActivity;
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
        Candidate.setPaperList(paperList);
    }

    //= CheckCandidate =============================================================================
    //If checkCandidate() receive a null input, ERR_NULL_IDENTITY will be thrown
    @Test
    public void testCheckCandidate_Null_input_should_throw_ERR_NULL_IDENTITY() throws Exception {
        try{
            when(exLoader.getIdentity(null)).thenReturn(null);
            AssignHelper helper = new AssignHelper();
            testDummy =helper.checkCandidate(null);
            fail("Expected ERR_NULL_IDENTITY but none thrown");
        } catch(CustomException err){
            assertEquals(CustomException.ERR_NULL_IDENTITY, err.getErrorCode());
            assertEquals("Not an Identity", err.getErrorMsg());
        }
    }

    //If checkCandidate() receive an ID that was not in the list, ERR_NULL_CANDIDATE will be thrown
    @Test
    public void testCheckCandidate_ID_Not_in_AttdList_should_throw_ERR_4() throws Exception {
        try{
            AssignHelper helper = new AssignHelper();
            when(exLoader.getIdentity("15WAU22222"))
                    .thenReturn(new Identity("15WAU22222", "0", false, "Mr. Test"));
            testDummy = helper.checkCandidate("15WAU22222");
            fail("Expected ERR_NULL_CANDIDATE but none thrown");
        } catch(CustomException err){
            assertEquals(CustomException.ERR_NULL_CANDIDATE, err.getErrorCode());
            assertEquals("Mr. Test doest not belong to this venue", err.getErrorMsg());
        }
    }

    //Check if checkCandidate() can detect a candidate with status Exempted
    @Test
    public void testCheckCandidate_EXEMPTED_Candidate_detected_should_throw_ERR_5() throws Exception {
        try{
            AssignHelper helper = new AssignHelper();
            when(exLoader.getIdentity("15WAU00005"))
                    .thenReturn(new Identity("15WAU00005", "0", false, "Ms. Exm"));

            testDummy = helper.checkCandidate("15WAU00005");
            fail("Expected ERR_STATUS_EXEMPTED but none thrown");
        } catch(CustomException err){
            assertEquals(CustomException.ERR_STATUS_EXEMPTED, err.getErrorCode());
            assertEquals("The paper was exempted for Ms. Exm", err.getErrorMsg());
        }
    }

    //Check if checkCandidate() can detect a candidate with status Barred
    @Test
    public void testCheckCandidate_BARRED_Candidate_detected_should_throw_ERR_6() throws Exception {
        try{
            AssignHelper helper = new AssignHelper();
            when(exLoader.getIdentity("15WAU00004"))
                    .thenReturn(new Identity("15WAU00004", "0", false, "Mr. Bar"));
            testDummy = helper.checkCandidate("15WAU00004");
            fail("Expected ERR_STATUS_BARRED but none thrown");
        } catch(CustomException err){
            assertEquals(CustomException.ERR_STATUS_BARRED, err.getErrorCode());
            assertEquals("Mr. Bar have been barred", err.getErrorMsg());
        }
    }

    //Check if checkCandidate() can detect the input Identity that doesn't have a regNum
    @Test
    public void testCheckCandidate_ID_without_regNum_should_throw_ERR_7() throws Exception {
        try{
            AssignHelper helper = new AssignHelper();
            when(exLoader.getIdentity("15WAU00004"))
                    .thenReturn(new Identity());
            testDummy = helper.checkCandidate("15WAU00004");
            fail("Expected ERR_INCOMPLETE_ID but none thrown");
        } catch(CustomException err){
            assertEquals(CustomException.ERR_INCOMPLETE_ID, err.getErrorCode());
            assertEquals("FATAL: Unable to process ID", err.getErrorMsg());
        }
    }

    //Check if checkCandidate() can return a candidate if the candidate is valid
    @Test
    public void testCheckCandidate_Valid_ID_should_return_in_Candidate_Form() throws Exception{
        try{
            AssignHelper helper = new AssignHelper();
            when(exLoader.getIdentity("15WAU00001"))
                    .thenReturn(new Identity("15WAU00001", "0", false, "FGY"));
            testDummy = helper.checkCandidate("15WAU00001");
            assertNotNull(testDummy);
            assertEquals(testDummy, cdd1);
        } catch(CustomException err){
            fail("No error should be thrown but thrown ErrorMsg " + err.getErrorMsg());
        }
    }

    //Check if checkCandidate() can detect an empty attd list, should throw ERR_EMPTY_ATTD_LIST
    @Test
    public void testCheckCandidate_should_throw_ERR_EMPTY_ATTD_LIST() throws Exception{
        try{
            AssignHelper helper = new AssignHelper();
            AssignHelper.setClDBLoader(dbLoader);
            when(exLoader.getIdentity("15WAU00001"))
                    .thenReturn(new Identity("15WAU00001", "0", false, "FGY"));
            testDummy = helper.checkCandidate("15WAU00001");
            fail("Expected ERR_EMPTY_ATTD_LIST but none thrown");
        } catch(CustomException err){
            assertEquals(CustomException.ERR_EMPTY_ATTD_LIST, err.getErrorCode());
            assertEquals("No Attendance List", err.getErrorMsg());
        }
    }

    //=TryAssignCandidate===========================================================================
    //If both candidate and table weren't assign yet, tryAssignCandidate should return false
    @Test
    public void testTryAssignCandidate_Both_not_assign_should_return_false() throws Exception{
        try{
            AssignHelper helper = new AssignHelper();
            assertFalse(helper.tryAssignCandidate());
        }catch(CustomException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    //If table is not assign only, tryAssignCandidate should return false also
    @Test
    public void testTryAssignCandidate_Table_not_assign_should_return_false() throws Exception{
        try{
            AssignHelper helper = new AssignHelper();
            helper.checkTable(12);
            assertFalse(helper.tryAssignCandidate());
        }catch(CustomException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    //If candidate haven assign only, tryAssignCandidate should return false also
    @Test
    public void testTryAssignCandidate_Candidate_not_assign_should_return_false() throws Exception{
        try{
            AssignHelper helper = new AssignHelper();
            when(exLoader.getIdentity("15WAU00001"))
                    .thenReturn(new Identity("15WAU00001", "0", false, "FGY"));
            helper.checkCandidate("15WAU00001");
            assertFalse(helper.tryAssignCandidate());
        }catch(CustomException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    //If both Candidate and Table is valid, tryAssignCandidate should return true
    @Test
    public void testTryAssignCandidate_When_successful_assigned_should_return_false() throws Exception{
        try{
            AssignHelper helper = new AssignHelper();
            when(exLoader.getIdentity("15WAU00001"))
                    .thenReturn(new Identity("15WAU00001", "0", false, "FGY"));
            helper.checkCandidate("15WAU00001");
            helper.checkTable(12);
            assertTrue(helper.tryAssignCandidate());
        }catch(CustomException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    //If Table have been assigned before, ERR_TABLE_REASSIGN should be thrown
    @Test
    public void testTryAssignCandidate_Same_table_should_throw_ERR_TABLE_REASSIGN()throws Exception{
        HashMap<Integer, String> assgnList = new HashMap<>();
        assgnList.put(12, "15WAU11111");

        try{
            AssignHelper helper = new AssignHelper();
            helper.assgnList = assgnList;
            helper.checkTable(12);
            when(exLoader.getIdentity("15WAU00001"))
                    .thenReturn(new Identity("15WAU00001", "0", false, "FGY"));
            helper.checkCandidate("15WAU00001");
            boolean test = helper.tryAssignCandidate();
            fail("Expected ERR_TABLE_REASSIGN but none thrown");
        }catch(CustomException err){
            assertEquals(CustomException.ERR_TABLE_REASSIGN, err.getErrorCode());
            assertEquals("Previous: Table 12 assigned to null\nNew: Table 12 assign to FGY",
                    err.getErrorMsg());
        }
    }

    //If Candidate have been assigned before, ERR_CANDIDATE_REASSIGN should be thrown
    @Test
    public void testTryAssign_Same_candidate_should_throw_ERR_CANDIDATE_REASSIGN() throws Exception{
        HashMap<Integer, String> assgnList = new HashMap<>();
        assgnList.put(14, "15WAU00001");

        try{
            AssignHelper helper = new AssignHelper();
            helper.assgnList = assgnList;
            when(exLoader.getIdentity("15WAU00001"))
                    .thenReturn(new Identity("15WAU00001", "0", false, "FGY"));
            helper.checkTable(12);
            helper.checkCandidate("15WAU00001");
            boolean test = helper.tryAssignCandidate();
            fail("Expected ERR_CANDIDATE_REASSIGN but none thrown");
        }catch(CustomException err){
            assertEquals(CustomException.ERR_CANDIDATE_REASSIGN, err.getErrorCode());
            assertEquals("Previous: FGY assigned to Table 1\nNew: FGY assign to 12",
                    err.getErrorMsg());
        }
    }

    //If Candidate sit at a wrong table, ERR_PAPER_NOT_MATCH should be thrown
    //Candidate's paper does not match with the table's assigned paper
    @Test
    public void testTryAssign_Paper_not_Match_should_throw_ERR_PAPER_NOT_MATCH() throws Exception{
        HashMap<Integer, String> assgnList = new HashMap<>();
        assgnList.put(14, "15WAU00005");

        try{
            AssignHelper helper = new AssignHelper();
            helper.assgnList = assgnList;
            when(exLoader.getIdentity("15WAU00001"))
                    .thenReturn(new Identity("15WAU00001", "0", false, "FGY"));
            helper.checkTable(55);
            helper.checkCandidate("15WAU00001");
            boolean test = helper.tryAssignCandidate();
            fail("Expected ERR_PAPER_NOT_MATCH but none thrown");
        }catch(CustomException err){
            assertEquals(CustomException.ERR_PAPER_NOT_MATCH, err.getErrorCode());
            assertEquals("FGY should not sit here\nSuggest to Table 10", err.getErrorMsg());
        }
    }

    //= UpdateNewCandidate() =======================================================================
    /*************************************************
     * Table 14 previously assigned to Cdd1
     * Table 14 then assign to Cdd2
     * updateNewCandidate():
     * + should reset Cdd1 to Table 0 and ABSENT
     * + should assign Cdd2 to Table 14 and PRESENT
    *************************************************/
    @Test
    public void testUpdateNewCandidate_reaasign_table() throws Exception{
        AssignHelper helper = new AssignHelper();
        assertEquals(3, attdList.getNumberOfCandidates(AttendanceList.Status.ABSENT));
        when(exLoader.getIdentity(anyString()))
                .thenReturn(new Identity("15WAU00001", "0", false, "FGY"))
                .thenReturn(new Identity("15WAU00002", "0", false, "NYN"));

        helper.checkTable(14);
        helper.checkCandidate("15WAU00001");
        helper.tryAssignCandidate();

        helper.checkTable(14);
        helper.checkCandidate("15WAU00002");

        helper.updateNewCandidate(CustomException.ERR_TABLE_REASSIGN);

        assertEquals(1, helper.assgnList.size());
        assertEquals("15WAU00002", helper.assgnList.get(14));
        assertEquals(1, attdList.getNumberOfCandidates(AttendanceList.Status.PRESENT));
        assertEquals(2, attdList.getNumberOfCandidates(AttendanceList.Status.ABSENT));
        assertEquals(AttendanceList.Status.ABSENT, cdd1.getStatus());
        assertEquals(AttendanceList.Status.PRESENT, cdd2.getStatus());
    }
    /*************************************************
     * Table 12 previously assigned to Cdd1
     * Table 14 then assign to Cdd1
     * updateNewCandidate():
     * + should renew Cdd1 to Table 14
     *************************************************/
    @Test
    public void testUpdateNewCandidate_reassign_candidate() throws Exception{
        AssignHelper helper = new AssignHelper();
        assertEquals(3, attdList.getNumberOfCandidates(AttendanceList.Status.ABSENT));
        when(exLoader.getIdentity(anyString()))
                .thenReturn(new Identity("15WAU00001", "0", false, "FGY"))
                .thenReturn(new Identity("15WAU00001", "0", false, "FGY"));

        helper.checkTable(12);
        helper.checkCandidate("15WAU00001");
        helper.tryAssignCandidate();

        helper.checkTable(14);
        helper.checkCandidate("15WAU00001");

        helper.updateNewCandidate(CustomException.ERR_CANDIDATE_REASSIGN);

        assertEquals(1, helper.assgnList.size());
        assertNull(helper.assgnList.get(12));
        assertEquals("15WAU00001", helper.assgnList.get(14));
        assertEquals(14, (int)attdList.getCandidate("15WAU00001").getTableNumber());
        assertEquals(1, attdList.getNumberOfCandidates(AttendanceList.Status.PRESENT));
    }

    //= CancelNewAssign ============================================================================


}