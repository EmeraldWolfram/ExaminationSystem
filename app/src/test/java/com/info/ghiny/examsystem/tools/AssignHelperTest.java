package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.Identity;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 15/06/2016.
 */
public class AssignHelperTest {

    AttendanceList attdList;
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

        Candidate.setPaperList(paperList);
    }

    //If checkCandidate() receive a null input, ERR_NULL_IDENTITY will be thrown
    @Test
    public void testCheckCandidate_Null_input_should_throw_ERR_NULL_IDENTITY() throws Exception {
        try{
            AssignHelper helper = new AssignHelper(attdList);
            testDummy =helper.checkCandidate(null);
            fail("Expected ERR_NULL_IDENTITY but none thrown");
        } catch(CustomException err){
            assertEquals(CustomException.ERR_NULL_IDENTITY, err.getErrorCode());
            assertEquals("Identity is null", err.getErrorMsg());
        }
    }

    //If checkCandidate() receive an ID that was not in the list, ERR_NULL_CANDIDATE will be thrown
    @Test
    public void testCheckCandidate_ID_Not_in_AttdList_should_throw_ERR_4() throws Exception {
        try{
            AssignHelper helper = new AssignHelper(attdList);
            testDummy = helper.checkCandidate(new Identity("15WAU22222", "0", false, "Mr. Test"));
            fail("Expected ERR_NULL_CANDIDATE but none thrown");
        } catch(CustomException err){
            assertEquals(CustomException.ERR_NULL_CANDIDATE, err.getErrorCode());
            assertEquals("Candidate not in list", err.getErrorMsg());
        }
    }

    //Check if checkCandidate() can detect a candidate with status Exempted
    @Test
    public void testCheckCandidate_EXEMPTED_Candidate_detected_should_throw_ERR_5() throws Exception {
        try{
            AssignHelper helper = new AssignHelper(attdList);
            testDummy = helper.checkCandidate(new Identity("15WAU00005", "0", false, "Ms. Exm"));
            fail("Expected ERR_STATUS_EXEMPTED but none thrown");
        } catch(CustomException err){
            assertEquals(CustomException.ERR_STATUS_EXEMPTED, err.getErrorCode());
            assertEquals("Candidate exempted", err.getErrorMsg());
        }
    }

    //Check if checkCandidate() can detect a candidate with status Barred
    @Test
    public void testCheckCandidate_BARRED_Candidate_detected_should_throw_ERR_6() throws Exception {
        try{
            AssignHelper helper = new AssignHelper(attdList);
            testDummy = helper.checkCandidate(new Identity("15WAU00004", "0", false, "Mr. Bar"));
            fail("Expected ERR_STATUS_BARRED but none thrown");
        } catch(CustomException err){
            assertEquals(CustomException.ERR_STATUS_BARRED, err.getErrorCode());
            assertEquals("Candidate barred", err.getErrorMsg());
        }
    }

    //Check if checkCandidate() can detect the input Identity that doesn't have a regNum
    @Test
    public void testCheckCandidate_ID_without_regNum_should_throw_ERR_7() throws Exception {
        try{
            AssignHelper helper = new AssignHelper(attdList);
            testDummy = helper.checkCandidate(new Identity());
            fail("Expected ERR_INCOMPLETE_ID but none thrown");
        } catch(CustomException err){
            assertEquals(CustomException.ERR_INCOMPLETE_ID, err.getErrorCode());
            assertEquals("Incomplete Identity", err.getErrorMsg());
        }
    }

    //Check if checkCandidate() can return a candidate if the candidate is valid
    @Test
    public void testCheckCandidate_Valid_ID_should_return_in_Candidate_Form() throws Exception{
        try{
            AssignHelper helper = new AssignHelper(attdList);
            testDummy = helper.checkCandidate(new Identity("15WAU00001", "0", false, "FGY"));
            assertNotNull(testDummy);
            assertEquals(testDummy, cdd1);
        } catch(CustomException err){
            fail("No error should be thrown but thrown ErrorMsg " + err.getErrorMsg());
        }
    }

    //=TryAssignCandidate===========================================================================
    //If both candidate and table weren't assign yet, tryAssignCandidate should return false
    @Test
    public void testTryAssignCandidate_Both_not_assign_should_return_false() throws Exception{
        try{
            AssignHelper helper = new AssignHelper(attdList);
            assertFalse(helper.tryAssignCandidate());
        }catch(CustomException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    //If table is not assign only, tryAssignCandidate should return false also
    @Test
    public void testTryAssignCandidate_Table_not_assign_should_return_false() throws Exception{
        try{
            AssignHelper helper = new AssignHelper(attdList);
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
            AssignHelper helper = new AssignHelper(attdList);
            helper.checkCandidate(new Identity("15WAU00001", "0", false, "FGY"));
            assertFalse(helper.tryAssignCandidate());
        }catch(CustomException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    //If both Candidate and Table is valid, tryAssignCandidate should return true
    @Test
    public void testTryAssignCandidate_When_successful_assigned_should_return_false() throws Exception{
        try{
            AssignHelper helper = new AssignHelper(attdList);
            helper.checkTable(12);
            helper.checkCandidate(new Identity("15WAU00001", "0", false, "FGY"));
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
            AssignHelper helper = new AssignHelper(attdList);
            helper.assgnList = assgnList;
            helper.checkTable(12);
            helper.checkCandidate(new Identity("15WAU00001", "0", false, "FGY"));
            boolean test = helper.tryAssignCandidate();
            fail("Expected ERR_TABLE_REASSIGN but none thrown");
        }catch(CustomException err){
            assertEquals(CustomException.ERR_TABLE_REASSIGN, err.getErrorCode());
            assertEquals("Table assigned before", err.getErrorMsg());
        }
    }

    //If Candidate have been assigned before, ERR_CANDIDATE_REASSIGN should be thrown
    @Test
    public void testTryAssign_Same_candidate_should_throw_ERR_CANDIDATE_REASSIGN() throws Exception{
        HashMap<Integer, String> assgnList = new HashMap<>();
        assgnList.put(14, "15WAU00001");

        try{
            AssignHelper helper = new AssignHelper(attdList);
            helper.assgnList = assgnList;
            helper.checkTable(12);
            helper.checkCandidate(new Identity("15WAU00001", "0", false, "FGY"));
            boolean test = helper.tryAssignCandidate();
            fail("Expected ERR_CANDIDATE_REASSIGN but none thrown");
        }catch(CustomException err){
            assertEquals(CustomException.ERR_CANDIDATE_REASSIGN, err.getErrorCode());
            assertEquals("Candidate assigned before", err.getErrorMsg());
        }
    }

    //If Candidate sit at a wrong table, ERR_PAPER_NOT_MATCH should be thrown
    //Candidate's paper does not match with the table's assigned paper
    @Test
    public void testTryAssign_Paper_not_Match_should_throw_ERR_PAPER_NOT_MATCH() throws Exception{
        HashMap<Integer, String> assgnList = new HashMap<>();
        assgnList.put(14, "15WAU00005");

        try{
            AssignHelper helper = new AssignHelper(attdList);
            helper.assgnList = assgnList;
            helper.checkTable(55);
            helper.checkCandidate(new Identity("15WAU00001", "0", false, "FGY"));
            boolean test = helper.tryAssignCandidate();
            fail("Expected ERR_PAPER_NOT_MATCH but none thrown");
        }catch(CustomException err){
            assertEquals(CustomException.ERR_PAPER_NOT_MATCH, err.getErrorCode());
            assertEquals("Paper for table and candidate does not match", err.getErrorMsg());
        }
    }

    

}