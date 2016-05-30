package com.info.ghiny.examsystem.database;

import android.media.CamcorderProfile;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 26/05/2016.
 */
public class AttendanceListTest {
    AttendanceList attdList;

    HashMap<String, HashMap<String, Candidate>> paperList1;
    HashMap<String, HashMap<String, Candidate>> paperList2;
    HashMap<String, HashMap<String, Candidate>> paperList3;
    HashMap<String, HashMap<String, Candidate>> dummyPaperList;

    HashMap<String, Candidate> cddList1;
    HashMap<String, Candidate> cddList2;
    HashMap<String, Candidate> cddList3;

    Candidate cdd1;
    Candidate cdd2;
    Candidate cdd3;
    Candidate cdd4;
    Candidate cdd5;


    @Before
    public void setUp() throws Exception{
        attdList = new AttendanceList();

        paperList1 = new HashMap<>();
        paperList1.put("BAME 0001", new HashMap<String, Candidate>());
        paperList1.put("BAME 0002", new HashMap<String, Candidate>());
        paperList1.put("BAME 0003", new HashMap<String, Candidate>());

        paperList2 = new HashMap<>();
        paperList2.put("BAME 2000", new HashMap<String, Candidate>());

        paperList3 = new HashMap<>();
        paperList3.put("BAME 2000", new HashMap<String, Candidate>());
        paperList3.put("BAME 2001", new HashMap<String, Candidate>());

        cdd1 = new Candidate(1, "FGY", "15WAU00001", "BAME 0001", AttendanceList.Status.ABSENT);
        cdd2 = new Candidate(1, "NYN", "15WAU00002", "BAME 0001", AttendanceList.Status.ABSENT);
        cdd3 = new Candidate(1, "LHN", "15WAU00003", "BAME 0001", AttendanceList.Status.ABSENT);
        cdd4 = new Candidate(1, "YZL", "15WAU00004", "BAME 0002", AttendanceList.Status.BARRED);
        cdd5 = new Candidate(1, "SYL", "15WAU00005", "BAME 0003", AttendanceList.Status.EXEMPTED);

        cddList1 = new HashMap<>();
        cddList1.put("15WAU00001", cdd1);
        cddList1.put("15WAU00002", cdd2);
        cddList1.put("15WAU00003", cdd3);
        cddList1.put("15WAU00004", cdd4);
        cddList1.put("15WAU00005", cdd5);
        cddList2 = new HashMap<>();
        cddList3 = new HashMap<>();
    }

    //GET PAPER LIST =============================================================================
    //Calling getPaperList on an empty attendanceList will not return null.
    //It will return an empty HashMap<>
    @Test
    public void testGetPaperList_EmptyAttdList() throws Exception {
        HashMap<String, HashMap<String, Candidate>> testList =
                attdList.getPaperList(AttendanceList.Status.PRESENT);

        assertNotNull(testList);
        assertTrue(testList.isEmpty());
    }

    @Test
    public void testGetPaperList_BasicFunctionality() throws Exception {
        attdList.getPaperList(AttendanceList.Status.PRESENT).putAll(paperList1);
        attdList.getPaperList(AttendanceList.Status.PRESENT).putAll(paperList2);
        attdList.getPaperList(AttendanceList.Status.PRESENT).putAll(paperList3);

        HashMap<String, HashMap<String, Candidate>>
                testMap = attdList.getPaperList(AttendanceList.Status.PRESENT);
        assertEquals(5, testMap.size());
    }
    //==============================================================================================
    //GET CANDIDATE LIST
    //Calling getCandidateList upon an empty AttendanceList should
    //not return null but return an empty HashMap<>
    @Test
    public void testGetCandidateList_EmptyAttendanceList() throws Exception {
        HashMap<String, Candidate> testList = attdList.getCandidateList(AttendanceList.Status.PRESENT, "BAME 2104");

        assertNotNull(testList);
        assertTrue(testList.isEmpty());
    }

    @Test
    public void testGetCandidateList_BasicFunctionality() throws Exception {
        paperList1.put("BAME 0001", cddList1);
        attdList.getPaperList(AttendanceList.Status.PRESENT).putAll(paperList1);

        HashMap<String, Candidate> testList =
                attdList.getCandidateList(AttendanceList.Status.PRESENT, "BAME 0001");

        assertNotNull(testList);
        assertFalse(testList.isEmpty());
        assertEquals(5, testList.size());
        assertEquals(cddList1, testList);
    }

    @Test
    public void testGetCandidateList_GettingExistButEmptyCandidateList() throws Exception {
        paperList1.put("BAME 0001", cddList1);
        attdList.getPaperList(AttendanceList.Status.PRESENT).putAll(paperList1);

        HashMap<String, Candidate> testList =
                attdList.getCandidateList(AttendanceList.Status.PRESENT, "BAME 0002");

        assertNotNull(testList);
        assertTrue(testList.isEmpty());
        assertEquals(new HashMap<String, Candidate>(), testList);
    }

    @Test
    public void testGetCandidateList_AttdListWithoutThePaper() throws Exception {
        paperList1.put("BAME 0001", cddList1);
        attdList.getPaperList(AttendanceList.Status.PRESENT).putAll(paperList1);

        HashMap<String, Candidate> testList =
                    attdList.getCandidateList(AttendanceList.Status.PRESENT, "BAME 0008");

        assertTrue(testList.isEmpty());
    }
    //==========================================================================================
    //ADD CANDIDATE
    //Calling addCandidate to an empty Attendance List should create the List
    @Test
    public void testAddCandidate_EmptyCandidateList() throws Exception {
        assertEquals(0, attdList.getNumberOfCandidates());
        assertEquals(0, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));
        assertEquals(4, attdList.getNumberOfStatus());

        attdList.addCandidate(cdd1, cdd1.getPaperCode(), AttendanceList.Status.PRESENT);

        //assertEquals(1, attdList.getNumberOfCandidates());
        //assertEquals(1, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));
        assertEquals(4, attdList.getNumberOfStatus());
    }

    //If the candidate to be added was existed in the list, the new added
    //candidate will override the existed candidate
    @Test
    public void testAddCandidate_SameCandidateOccured() throws Exception {
        assertEquals(0, attdList.getNumberOfCandidates());
        assertEquals(0, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));
        assertEquals(4, attdList.getNumberOfStatus());

        attdList.addCandidate(cdd1, cdd1.getPaperCode(), AttendanceList.Status.PRESENT);
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), AttendanceList.Status.PRESENT);

        assertEquals(1, attdList.getNumberOfCandidates());
        assertEquals(1, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));
        assertEquals(4, attdList.getNumberOfStatus());
    }

    //Adding different candidate with the same paper should append the candidate List
    @Test
    public void testAddCandidate_AddOnToExistingList() throws Exception {
        attdList.getPaperList(AttendanceList.Status.PRESENT).putAll(paperList1);
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), AttendanceList.Status.PRESENT);

        assertEquals(1, attdList.getNumberOfCandidates());
        assertEquals(3, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));

        attdList.addCandidate(cdd2, cdd2.getPaperCode(), AttendanceList.Status.PRESENT);
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), AttendanceList.Status.PRESENT);
        attdList.addCandidate(cdd4, cdd4.getPaperCode(), AttendanceList.Status.PRESENT);

        HashMap<String, Candidate> testList
                = attdList.getCandidateList(AttendanceList.Status.PRESENT, cdd1.getPaperCode());

        assertEquals(4, attdList.getNumberOfCandidates());
        assertEquals(3, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));
        assertNotNull(testList);
        assertEquals(testList.get(cdd1.getRegNum()), cdd1);
        assertEquals(testList.get(cdd2.getRegNum()), cdd2);
        assertEquals(testList.get(cdd3.getRegNum()), cdd3);
        assertNull(testList.get(cdd4.getRegNum()));

        testList = attdList.getCandidateList(AttendanceList.Status.PRESENT, cdd4.getPaperCode());
        assertEquals(testList.get(cdd4.getRegNum()), cdd4);
    }
    //=============================================================================================
    @Test
    public void testRemoveCandidate_TestFunctionality() throws Exception {
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), AttendanceList.Status.PRESENT);
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), AttendanceList.Status.PRESENT);
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), AttendanceList.Status.PRESENT);

        assertEquals(3, attdList.getNumberOfCandidates());
        assertEquals(1, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));
        attdList.removeCandidate("15WAU00001");
        assertEquals(2, attdList.getNumberOfCandidates());
        assertEquals(1, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));
    }

    @Test
    public void testRemoveCandidate_TestNotInListCandidate() throws Exception {
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), AttendanceList.Status.PRESENT);
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), AttendanceList.Status.PRESENT);
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), AttendanceList.Status.PRESENT);

        assertEquals(3, attdList.getNumberOfCandidates());
        assertEquals(1, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));
        attdList.removeCandidate("15WAU99999");
        assertEquals(3, attdList.getNumberOfCandidates());
        assertEquals(1, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));
    }

    @Test
    public void testGetCandidate() throws Exception {
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), AttendanceList.Status.PRESENT);
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), AttendanceList.Status.PRESENT);
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), AttendanceList.Status.PRESENT);

        Candidate testCdd = attdList.getCandidate("15WAU00001");

        assertEquals(testCdd, cdd1);
    }

    @Test
    public void testGetCandidate_ReturnNullIfDoesExist() throws Exception {
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), AttendanceList.Status.PRESENT);
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), AttendanceList.Status.PRESENT);
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), AttendanceList.Status.PRESENT);

        Candidate testCdd = attdList.getCandidate("15WAU99999");

        assertNull(testCdd);
    }

    @Test
    public void testGetAllCandidateRegNumList_TestEmptyAttdList() throws Exception {
        List<String> testList   = attdList.getAllCandidateRegNumList();

        assertNotNull(testList);
        assertTrue(testList.isEmpty());
    }

    @Test
    public void testGetAllCandidateRegNumList_TestExistedList() throws Exception {
        attdList.addCandidate(new Candidate(1, "FGY", "15WAU09184",
                "BAME 2134", AttendanceList.Status.ABSENT), "BAME 2134",
                AttendanceList.Status.PRESENT);
        List<String> testList   = attdList.getAllCandidateRegNumList();

        assertNotNull(testList);
        List<String> test = new ArrayList<>();
        test.add("15WAU09184");
        assertEquals(testList, test);
    }

    @Test
    public void testAddCandidate() throws Exception {

    }

    @Test
    public void testRemoveCandidate() throws Exception {

    }

    @Test
    public void testGetCandidate1() throws Exception {

    }

    @Test
    public void testGetAllCandidateRegNumList() throws Exception {

    }
}