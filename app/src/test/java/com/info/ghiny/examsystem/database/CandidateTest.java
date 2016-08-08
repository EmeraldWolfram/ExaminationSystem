package com.info.ghiny.examsystem.database;

import com.info.ghiny.examsystem.model.ProcessException;

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

        Candidate.setPaperList(null);
    }

    //=SetPaperList=================================================================================
    //Simple test on static method setPaperList whether a static variable can be assigned this way
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
    //Paper available in the list, the requested paper should be returned without exception
    @Test
    public void testGetPaper_TestFunctionality() throws Exception {
        HashMap<String, ExamSubject> paperMap = new HashMap<>();
        paperMap.put(testPaper1.getPaperCode(), testPaper1);
        try{
            Candidate.setPaperList(paperMap);
            Candidate testCdd = new Candidate(1, "RMB3", "FGY", "15WAU09184",
                    "BAME 2134", Status.ABSENT);
            ExamSubject getSubject = testCdd.getPaper();
            assertEquals(getSubject, testPaper1);
        }catch (ProcessException err){
            fail("No exception expected but thrown " + err.getErrorMsg());
        }
    }

    //If requested paper not in the list, MESSAGE_DIALOG shall be thrown
    @Test
    public void testGetPaper_TestPaperDoestNotExist() throws Exception {
        HashMap<String, ExamSubject> paperMap = new HashMap<>();
        paperMap.put(testPaper1.getPaperCode(), testPaper1);
        try{
            Candidate.setPaperList(paperMap);
            Candidate testCdd = new Candidate(1, "RMB3", "FGY", "15WAU09184",
                    "BAME 2004", Status.ABSENT);

            ExamSubject getSubject = testCdd.getPaper();
            fail("Exception MESSAGE_DIALOG expected but none thrown");
        }catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_DIALOG, err.getErrorType());
            assertEquals("There is no suitable paper for this candidate in this room",
                    err.getErrorMsg());
        }
    }

    //If Candidate does not have paperCode, getPaper() should throw MESSAGE_TOAST
    @Test
    public void testGetPaper_TestNullPaperCode_should_throw_MESSAGE_TOAST() throws Exception {
        HashMap<String, ExamSubject> paperMap = new HashMap<>();
        paperMap.put(testPaper1.getPaperCode(), testPaper1);
        try{
            Candidate.setPaperList(paperMap);
            Candidate testCdd = new Candidate();
            ExamSubject getSubject = testCdd.getPaper();
            fail("Expected MESSAGE_TOAST but none thrown");
        }catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("FATAL: Candidate don't have paper", err.getErrorMsg());
        }
    }

    //If PaperList is not initialized, getPaper() should throw MESSAGE_DIALOG
    @Test
    public void testGetPaper_TestEmptyPaperList() throws Exception {
        try{
            Candidate testCdd = new Candidate(1, "RMB3", "FGY", "15WAU09184",
                    "BAME 2134", Status.ABSENT);;
            ExamSubject getSubject = testCdd.getPaper();
            assertEquals(testPaper2, getSubject);
            fail("Expected MESSAGE_DIALOG but none thrown");
        }catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_DIALOG, err.getErrorType());
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
                "BAME 2004", Status.ABSENT);

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