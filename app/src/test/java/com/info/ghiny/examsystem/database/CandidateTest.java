package com.info.ghiny.examsystem.database;

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

    @Test
    public void testGetPaper_TestFunctionality() throws Exception {
        HashMap<String, ExamSubject> paperMap = new HashMap<>();
        paperMap.put(testPaper1.getPaperCode(), testPaper1);

        Candidate.setPaperList(paperMap);
        Candidate testCdd = new Candidate(1, "FGY", "15WAU09184",
                "BAME 2134", AttendanceList.Status.ABSENT);

        ExamSubject getSubject = testCdd.getPaper();

        assertEquals(getSubject, testPaper1);
    }

    @Test
    public void testGetPaper_TestDoestNotExist() throws Exception {
        HashMap<String, ExamSubject> paperMap = new HashMap<>();
        paperMap.put(testPaper1.getPaperCode(), testPaper1);

        Candidate.setPaperList(paperMap);
        Candidate testCdd = new Candidate(1, "FGY", "15WAU09184",
                "BAME 2004", AttendanceList.Status.ABSENT);

        ExamSubject getSubject = testCdd.getPaper();
        assertNull(getSubject);
    }

    @Test
    public void testGetPaper_TestNullPaperCode() throws Exception {
        HashMap<String, ExamSubject> paperMap = new HashMap<>();
        paperMap.put(testPaper1.getPaperCode(), testPaper1);

        Candidate.setPaperList(paperMap);
        Candidate testCdd = new Candidate();

        ExamSubject getSubject = testCdd.getPaper();
        assertNull(getSubject);
    }

    @Test
    public void testGetPaper_TestIteration() throws Exception {
        HashMap<String, ExamSubject> paperMap = new HashMap<>();
        paperMap.put(testPaper1.getPaperCode(), testPaper1);
        paperMap.put(testPaper2.getPaperCode(), testPaper2);

        Candidate.setPaperList(paperMap);
        Candidate testCdd = new Candidate(1, "FGY", "15WAU09184",
                "BAME 2004", AttendanceList.Status.ABSENT);

        ExamSubject getSubject = testCdd.getPaper();

        assertNotNull(getSubject);
        assertEquals(getSubject, testPaper2);
    }

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