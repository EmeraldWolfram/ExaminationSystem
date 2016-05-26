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

        paperList1 = new HashMap<>();
        paperList2 = new HashMap<>();
        paperList3 = new HashMap<>();
        paperList1.put("BAME 1234", new HashMap<String, Candidate>());
        paperList3.put("BAME 1234", new HashMap<String, Candidate>());
        paperList3.put("BAME 2134", new HashMap<String, Candidate>());
        paperList3.put("BAME 3134", new HashMap<String, Candidate>());

        cddList1 = new HashMap<>();
        cddList2 = new HashMap<>();
        cddList3 = new HashMap<>();
        cddList1.put("12WAD03444", new Candidate());
        cddList2.put("13WAD05666", new Candidate());
        cddList3.put("14WAD07888", new Candidate());
    }

    @Test
    public void testPutSubjectList_WhenAttdListWasEmpty() throws Exception {
        //AttdList initially was empty
        assertEquals(0, attdList.getNumberOfPaper());

        //Added one paper list to AttdList should now have size 1
        attdList.putSubjectList(paperList1);
        dummyPaperList = attdList.getPaperList(AttendanceList.Status.PRESENT);
        assertEquals(dummyPaperList, paperList1);
        assertEquals(1, attdList.getNumberOfPaper());

        //Added empty paper list to AttdList should not change anything
        attdList.putSubjectList(paperList2);
        assertEquals(1, attdList.getNumberOfPaper());

        //Added same Key paperCode will not create new PaperMap
        attdList.putSubjectList(paperList3);
        assertEquals(3, attdList.getNumberOfPaper());
    }

    @Test
    public void testPutCandidateList() throws Exception {
        int i = attdList.getNumberOfCandidates();
        assertEquals(i, 0);

        HashMap<String, Candidate> cddMap = new HashMap<>();
        attdList.putCandidateList("BAME 2134", cddMap, AttendanceList.Status.PRESENT);
    }

    @Test
    public void testGetStatusList() throws Exception {

    }

    @Test
    public void testAddCandidate() throws Exception {

    }

    @Test
    public void testRemoveCandidate() throws Exception {

    }

    @Test
    public void testGetCandidate() throws Exception {

    }

    @Test
    public void testGetAllCandidateRegNumList_TestEmptyAttdList() throws Exception {
        List<String> testList   = attdList.getAllCandidateRegNumList();

        assertNotNull(testList);
        assertEquals(testList, new ArrayList<String>());
    }
}