package com.info.ghiny.examsystem.database;

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

    HashMap<String, HashMap<String, HashMap<String, Candidate>>> paperList1;
    HashMap<String, HashMap<String, HashMap<String, Candidate>>> paperList2;
    HashMap<String, HashMap<String, HashMap<String, Candidate>>> paperList3;

    HashMap<String, HashMap<String, Candidate>> prgList1;
    HashMap<String, HashMap<String, Candidate>> prgList2;
    HashMap<String, HashMap<String, Candidate>> prgList3;
    HashMap<String, HashMap<String, Candidate>> dummyPaperList;

    HashMap<String, Candidate> cddList1;
    HashMap<String, Candidate> cddList2;
    HashMap<String, Candidate> cddList3;
    HashMap<String, Candidate> cddList4;

    Candidate cdd1;
    Candidate cdd2;
    Candidate cdd3;
    Candidate cdd4;
    Candidate cdd5;


    @Before
    public void setUp() throws Exception{
        attdList = new AttendanceList();

        paperList1 = new HashMap<>();
        paperList1.put("BAME 0001", new HashMap<String, HashMap<String, Candidate>>());
        paperList1.put("BAME 0002", new HashMap<String, HashMap<String, Candidate>>());
        paperList1.put("BAME 0003", new HashMap<String, HashMap<String, Candidate>>());

        paperList2 = new HashMap<>();
        paperList2.put("BAME 1001", new HashMap<String, HashMap<String, Candidate>>());

        paperList3 = new HashMap<>();

        prgList1 = new HashMap<>();
        prgList1.put("RMB3", new HashMap<String, Candidate>());
        prgList1.put("RFN3", new HashMap<String, Candidate>());
        prgList1.put("RBS3", new HashMap<String, Candidate>());

        prgList2 = new HashMap<>();

        prgList3 = new HashMap<>();

        cdd1 = new Candidate(1, "RMB3", "FGY", "15WAU00001", "BAME 0001", AttendanceList.Status.ABSENT);
        cdd2 = new Candidate(1, "RMB3", "NYN", "15WAU00002", "BAME 0001", AttendanceList.Status.ABSENT);
        cdd3 = new Candidate(1, "RMB3", "LHN", "15WAU00003", "BAME 0001", AttendanceList.Status.ABSENT);
        cdd4 = new Candidate(1, "RMB3", "YZL", "15WAU00004", "BAME 0002", AttendanceList.Status.BARRED);
        cdd5 = new Candidate(1, "RMB3", "SYL", "15WAU00005", "BAME 0003", AttendanceList.Status.EXEMPTED);

        cddList1 = new HashMap<>();
        cddList1.put("15WAU00001", cdd1);
        cddList1.put("15WAU00002", cdd2);
        cddList1.put("15WAU00003", cdd3);
        cddList1.put("15WAU00004", cdd4);
        cddList1.put("15WAU00005", cdd5);
        cddList2 = new HashMap<>();
        cddList3 = new HashMap<>();
        cddList4 = new HashMap<>();
        cddList2.put("15WAU00001", cdd1);
        cddList2.put("15WAU00002", cdd2);
        cddList2.put("15WAU00003", cdd3);
        cddList3.put("15WAU00004", cdd4);
        cddList4.put("15WAU00005", cdd5);
    }

    //= GET PAPER LIST =============================================================================
    //Calling getPaperList on an empty attendanceList will not return null.
    //It will return an empty HashMap<>
    @Test
    public void testGetPaperList_EmptyAttdList() throws Exception {
        HashMap<String, HashMap<String, HashMap<String, Candidate>>> testList =
                attdList.getPaperList(AttendanceList.Status.PRESENT);

        assertNotNull(testList);
        assertTrue(testList.isEmpty());
    }

    @Test
    public void testGetPaperList_BasicFunctionality() throws Exception {
        attdList.getPaperList(AttendanceList.Status.PRESENT).putAll(paperList1);
        attdList.getPaperList(AttendanceList.Status.PRESENT).putAll(paperList2);
        attdList.getPaperList(AttendanceList.Status.PRESENT).putAll(paperList3);

        HashMap<String, HashMap<String, HashMap<String, Candidate>>>
                testMap = attdList.getPaperList(AttendanceList.Status.PRESENT);
        assertEquals(4, testMap.size());
    }

    //= GET PROGRAMME LIST =========================================================================
    //Calling getProgrammeList upon an empty AttendancList should
    //not return null but return an empty HashMap<>
    @Test
    public void testGetProgrammeList_EmptyAttendanceList() throws Exception{
        HashMap<String, HashMap<String, Candidate>> prgList =
                attdList.getProgrammeList(AttendanceList.Status.PRESENT, "BAME 0001");

        assertNotNull(prgList);
        assertTrue(prgList.isEmpty());
    }

    @Test
    public void testtGetProgrammeList_BasicFunctionality() throws Exception{
        paperList1.put("BAME 0001", prgList1);
        attdList.getPaperList(AttendanceList.Status.PRESENT).putAll(paperList1);

        HashMap<String, HashMap<String, Candidate>> prgList =
                attdList.getProgrammeList(AttendanceList.Status.PRESENT, "BAME 0001");

        assertNotNull(prgList);
        assertEquals(3, prgList.size());
        assertTrue(prgList.containsKey("RMB3"));
        assertTrue(prgList.containsKey("RFN3"));
        assertTrue(prgList.containsKey("RBS3"));
        assertEquals(3, attdList.getNumberOfProgramme(AttendanceList.Status.PRESENT, "BAME 0001"));
    }

    //= GET CANDIDATE LIST =========================================================================
    //Calling getCandidateList upon an empty AttendanceList should
    //not return null but return an empty HashMap<>
    @Test
    public void testGetCandidateList_EmptyAttendanceList() throws Exception {
        HashMap<String, Candidate> testList =
                attdList.getCandidateList(AttendanceList.Status.PRESENT, "BAME 2104", "RMB3");

        assertNotNull(testList);
        assertTrue(testList.isEmpty());
    }

    @Test
    public void testGetCandidateList_BasicFunctionality() throws Exception {
        prgList1.put("RMB3", cddList1);
        paperList1.put("BAME 0001", prgList1);
        attdList.getPaperList(AttendanceList.Status.PRESENT).putAll(paperList1);

        HashMap<String, Candidate> testList =
                attdList.getCandidateList(AttendanceList.Status.PRESENT, "BAME 0001", "RMB3");

        assertEquals(5, attdList.getTotalNumberOfCandidates());
        assertNotNull(testList);
        assertFalse(testList.isEmpty());
        assertEquals(5, testList.size());
        assertEquals(cddList1, testList);
    }

    @Test
    public void testGetCandidateList_GettingExistButEmptyCandidateList() throws Exception {
        prgList1.put("RMB3", cddList1);
        paperList1.put("BAME 0001", prgList1);
        attdList.getPaperList(AttendanceList.Status.PRESENT).putAll(paperList1);

        HashMap<String, Candidate> testList =
                attdList.getCandidateList(AttendanceList.Status.PRESENT, "BAME 0002", "RMB3");

        assertNotNull(testList);
        assertTrue(testList.isEmpty());
        assertEquals(new HashMap<String, Candidate>(), testList);
    }

    @Test
    public void testGetCandidateList_AttdListWithoutThePaper() throws Exception {
        prgList1.put("RMB3", cddList1);
        paperList1.put("BAME 0001", prgList1);
        attdList.getPaperList(AttendanceList.Status.PRESENT).putAll(paperList1);

        HashMap<String, Candidate> testList =
                    attdList.getCandidateList(AttendanceList.Status.PRESENT, "BAME 0008", "RMB3");

        assertTrue(testList.isEmpty());
    }
    //= ADD CANDIDATE ==============================================================================
    //Calling addCandidate to an empty Attendance List should create the List
    @Test
    public void testAddCandidate_EmptyCandidateList() throws Exception {
        assertEquals(0, attdList.getTotalNumberOfCandidates());
        assertEquals(0, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));
        assertEquals(4, attdList.getNumberOfStatus());

        attdList.addCandidate(cdd1, cdd1.getPaperCode(), AttendanceList.Status.PRESENT, "RMB3");

        assertEquals(1, attdList.getTotalNumberOfCandidates());
        assertEquals(1, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));
        assertEquals(4, attdList.getNumberOfStatus());
    }

    //If the candidate to be added was existed in the list, the new added
    //candidate will override the existed candidate
    @Test
    public void testAddCandidate_SameCandidateOccured() throws Exception {
        assertEquals(0, attdList.getTotalNumberOfCandidates());
        assertEquals(0, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));
        assertEquals(4, attdList.getNumberOfStatus());

        attdList.addCandidate(cdd1, cdd1.getPaperCode(), AttendanceList.Status.PRESENT, "RMB3");
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), AttendanceList.Status.PRESENT, "RMB3");

        assertEquals(1, attdList.getTotalNumberOfCandidates());
        assertEquals(1, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));
        assertEquals(4, attdList.getNumberOfStatus());
    }

    //Adding different candidate with the same paper should append the candidate List
    @Test
    public void testAddCandidate_AddOnToExistingList() throws Exception {
        attdList.getPaperList(AttendanceList.Status.PRESENT).putAll(paperList1);
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), AttendanceList.Status.PRESENT, "RMB3");

        assertEquals(1, attdList.getTotalNumberOfCandidates());
        assertEquals(3, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));

        attdList.addCandidate(cdd2, cdd2.getPaperCode(), AttendanceList.Status.PRESENT, "RMB3");
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), AttendanceList.Status.PRESENT, "RMB3");
        attdList.addCandidate(cdd4, cdd4.getPaperCode(), AttendanceList.Status.PRESENT, "RMB3");

        HashMap<String, Candidate> testList
                = attdList.getCandidateList(AttendanceList.Status.PRESENT,
                                            cdd1.getPaperCode(), cdd1.getProgramme());

        assertEquals(4, attdList.getTotalNumberOfCandidates());
        assertEquals(3, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));
        assertNotNull(testList);
        assertEquals(testList.get(cdd1.getRegNum()), cdd1);
        assertEquals(testList.get(cdd2.getRegNum()), cdd2);
        assertEquals(testList.get(cdd3.getRegNum()), cdd3);
        assertNull(testList.get(cdd4.getRegNum()));

        testList = attdList.getCandidateList(AttendanceList.Status.PRESENT,
                                             cdd4.getPaperCode(), cdd4.getProgramme());
        assertEquals(testList.get(cdd4.getRegNum()), cdd4);
    }

    //= REMOVE CANDIDATE ===========================================================================
    //If attendanceList have 3 candidates
    //After remove 1 should left 2
    @Test
    public void testRemoveCandidate_TestFunctionality() throws Exception {
        attdList.addCandidate(cdd1, cdd1.getPaperCode(),
                AttendanceList.Status.PRESENT, cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(),
                AttendanceList.Status.PRESENT, cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(),
                AttendanceList.Status.PRESENT, cdd3.getProgramme());

        assertEquals(3, attdList.getTotalNumberOfCandidates());
        assertEquals(1, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));
        attdList.removeCandidate("15WAU00001");
        assertEquals(2, attdList.getTotalNumberOfCandidates());
        assertEquals(1, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));
    }

    //If the attendanceList does not have the requested candidate
    //Nothing will happen upon calling removeCandidate()
    @Test
    public void testRemoveCandidate_TestNotInListCandidate() throws Exception {
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), AttendanceList.Status.PRESENT,
                cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), AttendanceList.Status.PRESENT,
                cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), AttendanceList.Status.PRESENT,
                cdd3.getProgramme());

        assertEquals(3, attdList.getTotalNumberOfCandidates());
        assertEquals(1, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));
        attdList.removeCandidate("15WAU99999");
        assertEquals(3, attdList.getTotalNumberOfCandidates());
        assertEquals(1, attdList.getNumberOfPaper(AttendanceList.Status.PRESENT));
    }

    //= GET CANDIDATE ==============================================================================
    //Obtain a candidate from the attendanceList with the given regNum(register number)
    @Test
    public void testGetCandidate() throws Exception {
        attdList.addCandidate(cdd1, cdd1.getPaperCode(),
                AttendanceList.Status.PRESENT, cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(),
                AttendanceList.Status.PRESENT, cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(),
                AttendanceList.Status.PRESENT, cdd3.getProgramme());

        Candidate testCdd = attdList.getCandidate("15WAU00001");

        assertEquals(testCdd, cdd1);
    }

    //Return a NULL if the candidate requested wasn't exist in the attendance list
    @Test
    public void testGetCandidate_ReturnNullIfDoesExist() throws Exception {
        attdList.addCandidate(cdd1, cdd1.getPaperCode(),
                AttendanceList.Status.PRESENT, cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(),
                AttendanceList.Status.PRESENT, cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(),
                AttendanceList.Status.PRESENT, cdd3.getProgramme());

        Candidate testCdd = attdList.getCandidate("15WAU99999");

        assertNull(testCdd);
    }

    //= GET ALL CANDIDATE REGISTER NUMBER LIST =====================================================
    //Calling getAllCandidateRegNumList() when the attendanceList is empty
    //should return an empty List<>
    @Test
    public void testGetAllCandidateRegNumList_TestEmptyAttdList() throws Exception {
        List<String> testList   = attdList.getAllCandidateRegNumList();

        assertNotNull(testList);
        assertTrue(testList.isEmpty());
    }

    //Calling getAllCandidateRegNumList() should return the candidate regNum existed in
    //the attendanceList
    @Test
    public void testGetAllCandidateRegNumList_TestExistedList() throws Exception {
        attdList.addCandidate(new Candidate(1, "RMB3", "FGY", "15WAU09184",
                "BAME 2134", AttendanceList.Status.ABSENT), "BAME 2134",
                AttendanceList.Status.PRESENT, "RMB3");
        attdList.addCandidate(cdd1, cdd1.getPaperCode(),
                AttendanceList.Status.PRESENT, cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(),
                AttendanceList.Status.PRESENT, cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(),
                AttendanceList.Status.PRESENT, cdd3.getProgramme());
        attdList.addCandidate(cdd4, cdd4.getPaperCode(),
                AttendanceList.Status.PRESENT, cdd4.getProgramme());
        attdList.addCandidate(cdd5, cdd5.getPaperCode(),
                AttendanceList.Status.PRESENT, cdd5.getProgramme());
        List<String> testList   = attdList.getAllCandidateRegNumList();

        assertNotNull(testList);
        assertEquals(6, testList.size());
        List<String> test = new ArrayList<>();
        test.add("15WAU09184");
        test.add("15WAU00002");
        test.add("15WAU00001");
        test.add("15WAU00003");
        test.add("15WAU00004");
        test.add("15WAU00005");
        assertEquals(test, testList);
    }

    //= GET NUMBER OF CANDIDATES ===================================================================
    /**
     * getTotalNumberOfCandidate() method should return the total number of
     * candidate to be examine WITHOUT considering the status
     *
     * getNumberOfCandidates(Status) method should return the nuber of Candidate
     * that have the input status
     *
     * In this test, there are 3 ABSENT, 1 BARRED and 1 EXEMPTED candidates
     * Therefore,
     * getTotal...() return 5
     * getNumbe...() return 3, 1, 1 for ABSENT, BARRED, EXEMPTED respectively
     */
    @Test
    public void testGetNumberOfCandidates() throws Exception{
        attdList.getCandidateList(AttendanceList.Status.ABSENT, "BAME 0001", "RMB3")
                .putAll(cddList2);
        attdList.getCandidateList(AttendanceList.Status.BARRED, "BAME 0002", "RMB3")
                .putAll(cddList3);
        attdList.getCandidateList(AttendanceList.Status.EXEMPTED, "BAME 0003", "RMB3")
                .putAll(cddList4);

        assertEquals(5, attdList.getTotalNumberOfCandidates());

        assertEquals(3, attdList.getNumberOfCandidates(AttendanceList.Status.ABSENT));
        assertEquals(3, attdList.getNumberOfCandidates(
                AttendanceList.Status.ABSENT, "BAME 0001", "RMB3"));
        assertEquals(0, attdList.getNumberOfCandidates(
                AttendanceList.Status.ABSENT, "BAME 0002", "RMB3"));
        assertEquals(0, attdList.getNumberOfCandidates(
                AttendanceList.Status.ABSENT, "BAME 0001", "RMB2"));

        assertEquals(1, attdList.getNumberOfCandidates(AttendanceList.Status.BARRED));
        assertEquals(1, attdList.getNumberOfCandidates(
                AttendanceList.Status.BARRED, "BAME 0002", "RMB3"));

        assertEquals(1, attdList.getNumberOfCandidates(AttendanceList.Status.EXEMPTED));
        assertEquals(1, attdList.getNumberOfCandidates(
                AttendanceList.Status.EXEMPTED, "BAME 0003", "RMB3"));
    }
}