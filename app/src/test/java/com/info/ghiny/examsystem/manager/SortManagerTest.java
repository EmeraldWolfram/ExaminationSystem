package com.info.ghiny.examsystem.manager;

import com.google.zxing.pdf417.encoder.Compaction;
import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.Status;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.*;

/**
 * Created by FOONG on 26/12/2016.
 */
public class SortManagerTest {

    private AttendanceList attdList;
    private Candidate cdd1;
    private Candidate cdd2;
    private Candidate cdd3;
    private Candidate cdd4;
    private Candidate cdd5;
    private Candidate cdd6;


    @Before
    public void setUp() throws Exception {
        attdList = new AttendanceList();
        cdd1 = new Candidate(6, "RMB3", "NAME A1", "15WAU00001", "BAME 0003", Status.PRESENT);
        cdd2 = new Candidate(3, "RMB3", "NAME A3", "15WAU00002", "BAME 0002", Status.PRESENT);
        cdd3 = new Candidate(1, "RMB3", "NAME A5", "15WAU00003", "BAME 0001", Status.PRESENT);
        cdd4 = new Candidate(2, "RMB3", "NAME A7", "15WAU00004", "BAME 0001", Status.PRESENT);
        cdd5 = new Candidate(4, "RMB3", "NAME A4", "15WAU00005", "BAME 0001", Status.PRESENT);
        cdd6 = new Candidate(8, "RMB3", "NAME A2", "15WAU00006", "BAME 0001", Status.PRESENT);

        attdList.addCandidate(cdd1);
        attdList.addCandidate(cdd2);
        attdList.addCandidate(cdd3);
        attdList.addCandidate(cdd4);
        attdList.addCandidate(cdd5);
        attdList.addCandidate(cdd6);
    }

    //= GetComparator ==============================================================================

    /**
     * getComparator(...)
     *
     * This method return a comparator for TreeSet to sort the attendance list for display
     *
     * Tests:
     * 1. Group Programme and Sort ID
     * 2. Group Programme and Sort Name
     * 3. Group Programme and Sort Table
     * 4. Group Nothing and Sort ID
     * 5. Group Nothing and Sort Name
     * 6. Group Programme and Sort Table but Table is null (ABSENT case)
     *    (The sort should then sort according to ID number
     * 7. Group Programme and Sort Name but Name is the same
     *    (The sort should then sort according to ID number)
     * 8. Group Nothing and Sort Name but Name is the same
     *    (Sort according ID)
     * 9. Group ID but same ID occur (Actually won't happen)
     *    (According to First Come First Serve Basis)
     */


    @Test
    public void testGetComparator_1_GroupedSortID() throws Exception {
        SortManager sorter  = new SortManager();
        Comparator<Candidate> sort  =
                sorter.getComparator(SortManager.SortMethod.GROUP_PAPER_GROUP_PROGRAM_SORT_ID);

        TreeSet<Candidate> treeSet  = new TreeSet<>(sort);
        List<String> regNumList    = attdList.getAllCandidateRegNumList();
        for(int i = 0; i < attdList.getTotalNumberOfCandidates(); i++) {
            treeSet.add(attdList.getCandidate(regNumList.get(i)));
        }

        ArrayList<Candidate> testList   = new ArrayList<>(treeSet);

        assertEquals(cdd3, testList.get(0)); //Subject BAME 0001 with Lowest ID
        assertEquals(cdd4, testList.get(1));
        assertEquals(cdd5, testList.get(2));
        assertEquals(cdd6, testList.get(3));
        assertEquals(cdd2, testList.get(4)); //Subject BAME 0002
        assertEquals(cdd1, testList.get(5)); //Subject BAME 0003
    }

    @Test
    public void testGetComparator_2_GroupedSortName() throws Exception {
        SortManager sorter  = new SortManager();
        Comparator<Candidate> sort  =
                sorter.getComparator(SortManager.SortMethod.GROUP_PAPER_GROUP_PROGRAM_SORT_NAME);

        TreeSet<Candidate> treeSet  = new TreeSet<>(sort);
        List<String> regNumList    = attdList.getAllCandidateRegNumList();
        for(int i = 0; i < attdList.getTotalNumberOfCandidates(); i++) {
            treeSet.add(attdList.getCandidate(regNumList.get(i)));
        }

        ArrayList<Candidate> testList   = new ArrayList<>(treeSet);

        assertEquals(cdd6, testList.get(0));
        assertEquals(cdd5, testList.get(1));
        assertEquals(cdd3, testList.get(2));
        assertEquals(cdd4, testList.get(3));
        assertEquals(cdd2, testList.get(4));
        assertEquals(cdd1, testList.get(5));
    }

    @Test
    public void testGetComparator_3_GroupedSortTable() throws Exception {
        SortManager sorter  = new SortManager();
        Comparator<Candidate> sort  =
                sorter.getComparator(SortManager.SortMethod.GROUP_PAPER_GROUP_PROGRAM_SORT_TABLE);

        TreeSet<Candidate> treeSet  = new TreeSet<>(sort);
        List<String> regNumList    = attdList.getAllCandidateRegNumList();
        for(int i = 0; i < attdList.getTotalNumberOfCandidates(); i++) {
            treeSet.add(attdList.getCandidate(regNumList.get(i)));
        }

        ArrayList<Candidate> testList   = new ArrayList<>(treeSet);

        assertEquals(cdd3, testList.get(0));
        assertEquals(cdd4, testList.get(1));
        assertEquals(cdd2, testList.get(2));
        assertEquals(cdd5, testList.get(3));
        assertEquals(cdd1, testList.get(4));
        assertEquals(cdd6, testList.get(5));
    }

    @Test
    public void testGetComparator_4_SortID() throws Exception {
        SortManager sorter  = new SortManager();
        Comparator<Candidate> sort  =
                sorter.getComparator(SortManager.SortMethod.GROUP_PAPER_SORT_ID);

        TreeSet<Candidate> treeSet  = new TreeSet<>(sort);
        List<String> regNumList    = attdList.getAllCandidateRegNumList();
        for(int i = 0; i < attdList.getTotalNumberOfCandidates(); i++) {
            treeSet.add(attdList.getCandidate(regNumList.get(i)));
        }

        ArrayList<Candidate> testList   = new ArrayList<>(treeSet);

        assertEquals(cdd1, testList.get(0));
        assertEquals(cdd2, testList.get(1));
        assertEquals(cdd3, testList.get(2));
        assertEquals(cdd4, testList.get(3));
        assertEquals(cdd5, testList.get(4));
        assertEquals(cdd6, testList.get(5));
    }

    @Test
    public void testGetComparator_5_SortName() throws Exception {
        SortManager sorter  = new SortManager();
        Comparator<Candidate> sort  =
                sorter.getComparator(SortManager.SortMethod.GROUP_PAPER_SORT_NAME);

        TreeSet<Candidate> treeSet  = new TreeSet<>(sort);
        List<String> regNumList    = attdList.getAllCandidateRegNumList();
        for(int i = 0; i < attdList.getTotalNumberOfCandidates(); i++) {
            treeSet.add(attdList.getCandidate(regNumList.get(i)));
        }

        ArrayList<Candidate> testList   = new ArrayList<>(treeSet);

        assertEquals(cdd1, testList.get(0));
        assertEquals(cdd6, testList.get(1));
        assertEquals(cdd2, testList.get(2));
        assertEquals(cdd5, testList.get(3));
        assertEquals(cdd3, testList.get(4));
        assertEquals(cdd4, testList.get(5));
    }

    @Test
    public void testGetComparator_6_SortTableButNullTable() throws Exception {
        SortManager sorter  = new SortManager();
        Comparator<Candidate> sort  =
                sorter.getComparator(SortManager.SortMethod.GROUP_PAPER_GROUP_PROGRAM_SORT_TABLE);

        TreeSet<Candidate> treeSet  = new TreeSet<>(sort);
        List<String> regNumList    = attdList.getAllCandidateRegNumList();
        for(int i = 0; i < attdList.getTotalNumberOfCandidates(); i++) {
            attdList.getCandidate(regNumList.get(i)).setTableNumber(0);
            treeSet.add(attdList.getCandidate(regNumList.get(i)));
        }

        ArrayList<Candidate> testList   = new ArrayList<>(treeSet);

        assertEquals(cdd1, testList.get(0));
        assertEquals(cdd2, testList.get(1));
        assertEquals(cdd3, testList.get(2));
        assertEquals(cdd4, testList.get(3));
        assertEquals(cdd5, testList.get(4));
        assertEquals(cdd6, testList.get(5));
    }

    @Test
    public void testGetComparator_7_GroupedSortNameButNameIsSame() throws Exception {
        SortManager sorter  = new SortManager();
        Comparator<Candidate> sort  =
                sorter.getComparator(SortManager.SortMethod.GROUP_PAPER_GROUP_PROGRAM_SORT_NAME);

        TreeSet<Candidate> treeSet  = new TreeSet<>(sort);
        List<String> regNumList    = attdList.getAllCandidateRegNumList();
        for(int i = 0; i < attdList.getTotalNumberOfCandidates(); i++) {
            attdList.getCandidate(regNumList.get(i)).setExamIndex("SAME NAME");
            treeSet.add(attdList.getCandidate(regNumList.get(i)));
        }

        ArrayList<Candidate> testList   = new ArrayList<>(treeSet);

        assertEquals(cdd3, testList.get(0));
        assertEquals(cdd4, testList.get(1));
        assertEquals(cdd5, testList.get(2));
        assertEquals(cdd6, testList.get(3));
        assertEquals(cdd2, testList.get(4));
        assertEquals(cdd1, testList.get(5));
    }

    @Test
    public void testGetComparator_8_SortNameButNameIsSame() throws Exception {
        SortManager sorter = new SortManager();
        Comparator<Candidate> sort =
                sorter.getComparator(SortManager.SortMethod.GROUP_PAPER_SORT_NAME);

        TreeSet<Candidate> treeSet = new TreeSet<>(sort);
        List<String> regNumList = attdList.getAllCandidateRegNumList();
        for (int i = 0; i < attdList.getTotalNumberOfCandidates(); i++) {
            attdList.getCandidate(regNumList.get(i)).setExamIndex("SAME NAME");
            treeSet.add(attdList.getCandidate(regNumList.get(i)));
        }

        ArrayList<Candidate> testList = new ArrayList<>(treeSet);

        assertEquals(cdd1, testList.get(0));
        assertEquals(cdd2, testList.get(1));
        assertEquals(cdd3, testList.get(2));
        assertEquals(cdd4, testList.get(3));
        assertEquals(cdd5, testList.get(4));
        assertEquals(cdd6, testList.get(5));
    }

    @Test
    public void testGetComparator_9_SortIdButIdSame_SortAccordingFIFO() throws Exception {
        SortManager sorter = new SortManager();
        Comparator<Candidate> sort =
                sorter.getComparator(SortManager.SortMethod.GROUP_PAPER_SORT_ID);

        TreeSet<Candidate> treeSet = new TreeSet<>(sort);
        List<String> regNumList = attdList.getAllCandidateRegNumList();
        for (int i = 0; i < attdList.getTotalNumberOfCandidates(); i++) {
            attdList.getCandidate(regNumList.get(i)).setRegNum("SAME ID");
            treeSet.add(attdList.getCandidate(regNumList.get(i)));
        }

        ArrayList<Candidate> testList = new ArrayList<>(treeSet);

        assertEquals(cdd1, testList.get(0));
        assertEquals(cdd2, testList.get(1));
        assertEquals(cdd3, testList.get(2));
        assertEquals(cdd4, testList.get(3));
        assertEquals(cdd5, testList.get(4));
        assertEquals(cdd6, testList.get(5));
    }

}