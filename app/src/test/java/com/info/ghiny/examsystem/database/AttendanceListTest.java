package com.info.ghiny.examsystem.database;

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


    @Before
    public void setUp() throws Exception{
        attdList = new AttendanceList();
    }


    @Test
    public void testGetStatusList() throws Exception {

    }

    @Test
    public void testAddCandidate() throws Exception {
        assertEquals(0, attdList.getNumberOfCandidates());
        assertEquals(0, attdList.getNumberOfPaper());
        assertEquals(4, attdList.getNumberOfStatus());

        attdList.addCandidate(new Candidate(1, "FGY", "15WAU09184",
                        "BAME 2134", AttendanceList.Status.ABSENT), "BAME 2134",
                AttendanceList.Status.PRESENT);

        assertEquals(1, attdList.getNumberOfCandidates());
        assertEquals(1, attdList.getNumberOfPaper());
        assertEquals(4, attdList.getNumberOfStatus());
    }

    @Test
    public void testRemoveCandidate() throws Exception {
        attdList.addCandidate(new Candidate(1, "FGY", "15WAU09184",
                        "BAME 2134", AttendanceList.Status.ABSENT), "BAME 2134",
                AttendanceList.Status.PRESENT);

        assertEquals(1, attdList.getNumberOfCandidates());
        assertEquals(1, attdList.getNumberOfPaper());
        attdList.removeCandidate("15WAU09184");
        assertEquals(0, attdList.getNumberOfCandidates());
        assertEquals(1, attdList.getNumberOfPaper());
    }

    @Test
    public void testGetCandidate() throws Exception {

    }

    @Test
    public void testGetAllCandidateRegNumList_TestEmptyAttdList() throws Exception {
        List<String> testList   = attdList.getAllCandidateRegNumList();

        assertNotNull(testList);
        assertEquals(new ArrayList<String>(), testList);
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
}