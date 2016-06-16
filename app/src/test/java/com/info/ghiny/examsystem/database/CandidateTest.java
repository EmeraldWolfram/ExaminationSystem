package com.info.ghiny.examsystem.database;

import com.info.ghiny.examsystem.tools.CustomException;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 25/05/2016.
 */
public class CandidateTest {

    ExamSubject testPaper1 = new ExamSubject();
    ExamSubject testPaper2 = new ExamSubject();

    @Before
    public void setUp() throws Exception {
        testPaper1.setPaperCode("BAME 2134");
        testPaper1.setPaperDesc("TEST DRIVEN DEVELOPMENT");

        testPaper2.setPaperCode("BAME 2004");
        testPaper2.setPaperDesc("PROGRAMMING IN C");
    }

    @Test
    public void testSetPaperList_TestFunctionality() throws Exception {
        HashMap<String, ExamSubject> paperMap = new HashMap<>();
        paperMap.put(testPaper1.getPaperCode(), testPaper1);

        Candidate.setPaperList(paperMap);

        HashMap<String, ExamSubject> getMap = Candidate.getPaperList();

        assertNotNull(getMap);
        assertEquals(getMap.size(), 1);
        assertEquals(getMap.get("BAME 2134"), testPaper1);
    }
    //=GetPaper=====================================================================================
    //Paper available in the list, the paper should be returned
    @Test
    public void testGetPaper_TestFunctionality() throws Exception {
        HashMap<String, ExamSubject> paperMap = new HashMap<>();
        paperMap.put(testPaper1.getPaperCode(), testPaper1);
        try{
            Candidate.setPaperList(paperMap);
            Candidate testCdd = new Candidate(1, "RMB3", "FGY", "15WAU09184",
                    "BAME 2134", AttendanceList.Status.ABSENT);
            ExamSubject getSubject = testCdd.getPaper();
            assertEquals(getSubject, testPaper1);
        }catch (CustomException err){
            fail("No exception expected but thrown " + err.getErrorMsg());
        }

    }

    //If paper not in the list, a null should be returned
    @Test
    public void testGetPaper_TestDoestNotExist() throws Exception {
        HashMap<String, ExamSubject> paperMap = new HashMap<>();
        paperMap.put(testPaper1.getPaperCode(), testPaper1);
        try{
            Candidate.setPaperList(paperMap);
            Candidate testCdd = new Candidate(1, "RMB3", "FGY", "15WAU09184",
                    "BAME 2004", AttendanceList.Status.ABSENT);

            ExamSubject getSubject = testCdd.getPaper();
            assertNull(getSubject);
        }catch (CustomException err){
            fail("No exception expected but thrown " + err.getErrorMsg());
        }
    }

    //If Candidate does not have paperCode, getPaper() should return null
    @Test
    public void testGetPaper_TestNullPaperCode() throws Exception {
        HashMap<String, ExamSubject> paperMap = new HashMap<>();
        paperMap.put(testPaper1.getPaperCode(), testPaper1);
        try{
            Candidate.setPaperList(paperMap);
            Candidate testCdd = new Candidate();
            ExamSubject getSubject = testCdd.getPaper();
            assertNull(getSubject);
        }catch (CustomException err){
            fail("No exception expected but thrown " + err.getErrorMsg());
        }
    }

    //If PaperList is not initialized, getPaper() should throw ERR_EMPTY_PAPER_LIST
    @Test
    public void testGetPaper_TestEmptyPaperList() throws Exception {
        try{
            Candidate testCdd = new Candidate();
            ExamSubject getSubject = testCdd.getPaper();
            fail("Expected ERR_EMPTY_PAPER_LIST but none thrown");
        }catch (CustomException err){
            assertEquals(CustomException.ERR_EMPTY_PAPER_LIST, err.getErrorCode());
            assertEquals("Paper List haven initialize", err.getErrorMsg());
        }
    }

    //Test if getPaper can handle paperList with more than a subject
    @Test
    public void testGetPaper_TestIteration() throws Exception {
        HashMap<String, ExamSubject> paperMap = new HashMap<>();
        paperMap.put(testPaper1.getPaperCode(), testPaper1);
        paperMap.put(testPaper2.getPaperCode(), testPaper2);

        Candidate.setPaperList(paperMap);
        Candidate testCdd = new Candidate(1, "RMB3", "FGY", "15WAU09184",
                "BAME 2004", AttendanceList.Status.ABSENT);

        ExamSubject getSubject = testCdd.getPaper();

        assertNotNull(getSubject);
        assertEquals(getSubject, testPaper2);
    }

    //=GetPaperDesc=================================================================================
    @Test
    public void testGetPaperDesc() throws Exception {
        HashMap<String, ExamSubject> paperMap = new HashMap<>();
        paperMap.put(testPaper1.getPaperCode(), testPaper1);
        paperMap.put(testPaper2.getPaperCode(), testPaper2);

        Candidate.setPaperList(paperMap);
        String testStr = Candidate.getPaperDesc(testPaper1.getPaperCode());

        assertNotNull(testStr);
        assertEquals(testStr, "TEST DRIVEN DEVELOPMENT");
    }

    @Test
    public void testGetPaperDesc_TestNull() throws Exception {
        HashMap<String, ExamSubject> paperMap = new HashMap<>();
        paperMap.put(testPaper1.getPaperCode(), testPaper1);
        paperMap.put(testPaper2.getPaperCode(), testPaper2);

        Candidate.setPaperList(paperMap);
        String testStr = Candidate.getPaperDesc("BAMM 1001");

        assertNull(testStr);
    }
}