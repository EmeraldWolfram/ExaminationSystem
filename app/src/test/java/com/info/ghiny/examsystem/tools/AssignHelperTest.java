package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.Identity;

import org.junit.Before;
import org.junit.Test;

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

    //=CheckTable=================================================================================

}