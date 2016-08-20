package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.Session;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.manager.AssignManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 24/06/2016.
 */
public class FragmentHelperTest {

    AttendanceList attdList;

    FragmentHelper helper;
    AssignManager manager;

    HashMap<String, HashMap<String, HashMap<String, Candidate>>> paperList1;
    HashMap<String, HashMap<String, HashMap<String, Candidate>>> paperList2;
    HashMap<String, HashMap<String, HashMap<String, Candidate>>> paperList3;

    HashMap<String, HashMap<String, Candidate>> prgList2;
    HashMap<String, HashMap<String, Candidate>> prgList3;

    Candidate cdd1;
    Candidate cdd2;
    Candidate cdd3;
    Candidate cdd4;
    Candidate cdd5;


    @Before
    public void setUp() throws Exception{
        attdList = new AttendanceList();

        paperList2 = new HashMap<>();
        paperList3 = new HashMap<>();
        paperList2.put("BAME 1001", new HashMap<String, HashMap<String, Candidate>>());

        prgList2 = new HashMap<>();
        prgList3 = new HashMap<>();

        cdd1 = new Candidate(1, "RMB3", "FGY", "15WAU00001", "BAME 0001", Status.ABSENT);
        cdd2 = new Candidate(1, "RMB3", "NYN", "15WAU00002", "BAME 0001", Status.ABSENT);
        cdd3 = new Candidate(1, "RMB3", "LHN", "15WAU00003", "BAME 0001", Status.ABSENT);
        cdd4 = new Candidate(1, "RMB3", "YZL", "15WAU00004", "BAME 0002", Status.BARRED);
        cdd5 = new Candidate(1, "RMB3", "SYL", "15WAU00005", "BAME 0003", Status.EXEMPTED);

        helper = new FragmentHelper();

        manager = Mockito.mock(AssignManager.class);
    }

    //= GET TITLE LIST =============================================================================
    /*****************************************************************************
     * getTitleList(Status)
     * return a List<String> of paperCode that is not empty in the Status List
     * Eg.
     * AttdList -> ABSENT ->    1. BAME 0001 (3 Candidates)
     *                          2. BAME 0002 (1 Candidates)
     *
     * The method should return List with BAME 0001 and BAME 0002.
     *****************************************************************************/
    @Test
    public void testGetTitleList() throws Exception{
        cdd4 = new Candidate(0, "RMB3", "TS 1", "15WAU10004", "BAME 0002", Status.ABSENT);
        cdd5 = new Candidate(0, "RSF3", "TS 2", "15WAU10005", "BAME 0002", Status.ABSENT);

        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), cdd2.getStatus(), cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), cdd3.getStatus(), cdd3.getProgramme());
        attdList.addCandidate(cdd4, cdd4.getPaperCode(), cdd4.getStatus(), cdd4.getProgramme());
        attdList.addCandidate(cdd5, cdd5.getPaperCode(), cdd5.getStatus(), cdd5.getProgramme());

        AssignModel.setAttdList(attdList);

        List<String> list = helper.getTitleList(Status.ABSENT);

        assertEquals(2, list.size());
        assertEquals("BAME 0001", list.get(0));
        assertEquals("BAME 0002", list.get(1));
    }

    /*************************************************************
     * getTitleList(Status)
     * If the AttendanceList object is empty
     * The method should return empty List instead of null.
     *************************************************************/
    @Test
    public void testGetTitleList_EmptyAttdListShouldNotReturnNull() throws Exception{
        AssignModel.setAttdList(attdList);
        List<String> list = helper.getTitleList(Status.ABSENT);

        assertNotNull(list);
        assertEquals(0, list.size());
    }

    /*****************************************************************************
     * getTitleList(Status)
     * a paperCode with zero Candidates should be included also
     * as a delete in the PRESENT record may add a Candidate into the ABSENT
     * Therefore, the empty Candidate List should not be ignored
     *
     * Eg.
     * AttdList -> ABSENT ->    1. BAME 0001 (3 Candidates)
     *                          2. BAME 0002 (1 Candidates)
     *                          3. BAME 0003 (0 Candidates)
     *
     * The method should return List with BAME 0001, BAME 0002, BAME 0003
     *****************************************************************************/
    @Test
    public void testGetTitle_EmptyCandidateListShouldBeIgnored() throws Exception{
        cdd4 = new Candidate(0, "RMB3", "TS 1", "15WAU10004", "BAME 0002", Status.ABSENT);
        cdd5 = new Candidate(0, "RSF3", "TS 2", "15WAU10005", "BAME 0002", Status.ABSENT);

        //Create Empty PaperList (paperList3) and place into AttendanceList-------------------------
        prgList2.put("RXX2", new HashMap<String, Candidate>());
        paperList3.put("BAME 0003", prgList2);
        attdList.getAttendanceList().put(Status.ABSENT, paperList3);
        //------------------------------------------------------------------------------------------
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), cdd2.getStatus(), cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), cdd3.getStatus(), cdd3.getProgramme());
        attdList.addCandidate(cdd4, cdd4.getPaperCode(), cdd4.getStatus(), cdd4.getProgramme());
        attdList.addCandidate(cdd5, cdd5.getPaperCode(), cdd5.getStatus(), cdd5.getProgramme());

        AssignModel.setAttdList(attdList);
        List<String> list = helper.getTitleList(Status.ABSENT);

        assertEquals(3, list.size());
        assertEquals("BAME 0001", list.get(0));
        assertEquals("BAME 0002", list.get(1));
        assertEquals("BAME 0003", list.get(2));
    }

    //= GET CHILD LIST =============================================================================
    /**
     * getChildList()
     * should return a HashMap<PaperCode, List<Candidate>>
     * @PaperCode       the paper examined (the title reference)
     * @List<Candidate> Candidates that sit for the particular paper (the expandable body List)
     */

    @Test
    public void testGetChildList() throws Exception {
        cdd4 = new Candidate(0, "RMB3", "TS 1", "15WAU10004", "BAME 0002", Status.ABSENT);
        cdd5 = new Candidate(0, "RSF3", "TS 2", "15WAU10005", "BAME 0002", Status.ABSENT);

        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), cdd2.getStatus(), cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), cdd3.getStatus(), cdd3.getProgramme());
        attdList.addCandidate(cdd4, cdd4.getPaperCode(), cdd4.getStatus(), cdd4.getProgramme());
        attdList.addCandidate(cdd5, cdd5.getPaperCode(), cdd5.getStatus(), cdd5.getProgramme());

        AssignModel.setAttdList(attdList);

        HashMap<String, List<Candidate>> testMap = helper.getChildList(Status.ABSENT);

        List<Candidate> cddList;
        assertEquals(2, testMap.size());

        //Check the first List of Candidate
        cddList = testMap.get("BAME 0001");
        assertEquals(3, cddList.size());
        assertTrue(cddList.contains(cdd1));
        assertTrue(cddList.contains(cdd2));
        assertTrue(cddList.contains(cdd3));

        //Check the second List of Candidate
        cddList = testMap.get("BAME 0002");
        assertEquals(2, cddList.size());
        assertTrue(cddList.contains(cdd4));
        assertTrue(cddList.contains(cdd5));
    }

    /*************************************************************
     * getChildList(Status)
     * If the AttendanceList object is empty
     * The method should return empty HashMap<> instead of null.
     *************************************************************/
    @Test
    public void testGetChildList_EmptyAttdListShouldNotReturnNull() throws Exception{
        AssignModel.setAttdList(attdList);
        HashMap<String, List<Candidate>> map = helper.getChildList(Status.ABSENT);

        assertNotNull(map);
        assertEquals(0, map.size());
    }

    /*****************************************************************************
     * getChildList(Status)
     * a paperCode with zero Candidates should be included also
     * as a delete in the PRESENT record may add a Candidate into the ABSENT
     * Therefore, the empty Candidate List should not be ignored
     *
     * Eg.
     * AttdList -> ABSENT ->    1. BAME 0001 (3 Candidates)
     *                          2. BAME 0002 (1 Candidates)
     *                          3. BAME 0003 (0 Candidates)
     *
     * The method should return List with BAME 0001, BAME 0002, BAME 0003
     *****************************************************************************/
    @Test
    public void testGetChildList_EmptyCandidateListShouldBeIgnored() throws Exception{
        cdd4 = new Candidate(0, "RMB3", "TS 1", "15WAU10004", "BAME 0002", Status.ABSENT);
        cdd5 = new Candidate(0, "RSF3", "TS 2", "15WAU10005", "BAME 0002", Status.ABSENT);

        //Create Empty PaperList (paperList3) and place into AttendanceList-------------------------
        prgList2.put("RXX2", new HashMap<String, Candidate>());
        paperList3.put("BAME 0003", prgList2);
        attdList.getAttendanceList().put(Status.ABSENT, paperList3);
        //------------------------------------------------------------------------------------------
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), cdd2.getStatus(), cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), cdd3.getStatus(), cdd3.getProgramme());
        attdList.addCandidate(cdd4, cdd4.getPaperCode(), cdd4.getStatus(), cdd4.getProgramme());
        attdList.addCandidate(cdd5, cdd5.getPaperCode(), cdd5.getStatus(), cdd5.getProgramme());

        AssignModel.setAttdList(attdList);
        HashMap<String, List<Candidate>> map = helper.getChildList(Status.ABSENT);

        assertEquals(3, map.size());

        List<Candidate> testList;
        testList = map.get("BAME 0001");
        assertEquals(3, testList.size());
        assertTrue(testList.contains(cdd1));
        assertTrue(testList.contains(cdd2));
        assertTrue(testList.contains(cdd3));

        testList = map.get("BAME 0002");
        assertEquals(2, testList.size());
        assertTrue(testList.contains(cdd4));
        assertTrue(testList.contains(cdd5));

        testList = map.get("BAME 0003");
        assertEquals(0, testList.size());
    }

    //= resetNewAssign() ===========================================================================
    /**
     * resetNewAssign()
     *
     * 1. do nothing when no table assign before
     * 2. remove the last assigned table if exist
     */
    @Test
    public void testResetNewAssign_doNothingWhenNoAssignBefore() throws Exception {
        //FragmentHelper.resetCandidate(null);
        //AssignModel.setAttdList(attdList);

        //assertEquals(0, AssignModel.getAssgnList().size());
        //assertEquals(attdList, AssignModel.getAttdList());
    }

    @Test
    public void testResetNewAssign_removePreviouslyAssignedValue() throws Exception {
        //AssignModel assgnHelper = new AssignModel(manager);
        //attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        //HashMap<String, ExamSubject> paperList   = new HashMap<>();
        //ExamSubject subject1    = new ExamSubject("BAME 0001", "SUBJECT 1", 10,
        //        Calendar.getInstance(), 20, "H1", Session.AM);
        //ExamSubject subject2    = new ExamSubject("BAME 0002", "SUBJECT 2", 30,
        /*        Calendar.getInstance(), 20, "H2", Session.PM);
        ExamSubject subject3    = new ExamSubject("BAME 0003", "SUBJECT 3", 50,
                Calendar.getInstance(), 20, "H3", Session.VM);
        paperList.put(subject1.getPaperCode(), subject1);
        paperList.put(subject2.getPaperCode(), subject2);
        paperList.put(subject3.getPaperCode(), subject3);

        AssignModel.setAttdList(attdList);
        Candidate.setPaperList(paperList);

        assgnHelper.checkCandidate("15WAU00001");
        assgnHelper.checkTable("12");
        assertTrue(assgnHelper.tryAssignCandidate());
        assertEquals(1, AssignModel.getAssgnList().size());
        assertEquals(Status.PRESENT, attdList.getCandidate("15WAU00001").getStatus());

        FragmentHelper.resetCandidate(12);

        assertEquals(0, AssignModel.getAssgnList().size());
        assertEquals(Status.ABSENT, attdList.getCandidate("15WAU00001").getStatus());*/
    }
}