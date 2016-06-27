package com.info.ghiny.examsystem.database;

import android.support.test.runner.AndroidJUnit4;
import static android.support.test.InstrumentationRegistry.getTargetContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 23/06/2016.
 */
@RunWith(AndroidJUnit4.class)
public class CheckListLoaderTest {
    private CheckListLoader clDbLoader;

    @Before
    public void setUp() throws Exception {
        getTargetContext().deleteDatabase(CheckListLoader.DATABASE_NAME);
        clDbLoader = new CheckListLoader(getTargetContext());
    }

    //= SaveAttendanceList() =======================================================================
    /****************************************************************
     * saveAttendanceList()
     * should save the 5 Candidate into the database
     *
     ****************************************************************/
    @Test
    public void testSaveAttendanceList() throws Exception {
        Candidate cdd1 = new Candidate(1, "RMB3", "FGY", "15WAU00001", "BAME 0001",
                AttendanceList.Status.ABSENT);
        Candidate cdd2 = new Candidate(1, "RMB3", "NYN", "15WAU00002", "BAME 0001",
                AttendanceList.Status.ABSENT);
        Candidate cdd3 = new Candidate(1, "RMB3", "LHN", "15WAU00003", "BAME 0001",
                AttendanceList.Status.ABSENT);
        Candidate cdd4 = new Candidate(1, "RMB3", "YZL", "15WAU00004", "BAME 0002",
                AttendanceList.Status.BARRED);
        Candidate cdd5 = new Candidate(1, "RMB3", "SYL", "15WAU00005", "BAME 0003",
                AttendanceList.Status.EXEMPTED);
        AttendanceList attdList = new AttendanceList();
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), cdd2.getStatus(), cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), cdd3.getStatus(), cdd3.getProgramme());
        attdList.addCandidate(cdd4, cdd4.getPaperCode(), cdd4.getStatus(), cdd4.getProgramme());
        attdList.addCandidate(cdd5, cdd5.getPaperCode(), cdd5.getStatus(), cdd5.getProgramme());

        clDbLoader.saveAttendanceList(attdList);
        HashMap<AttendanceList.Status, HashMap<String, HashMap<String, HashMap<String, Candidate>>>>
                testMap = clDbLoader.getLastSavedAttendanceList();

        assertEquals(0, testMap.get(AttendanceList.Status.PRESENT).size());

        //3 Candidate in the ABSENT List
        assertEquals(3, testMap.get(AttendanceList.Status.ABSENT).get("BAME 0001").get("RMB3").size());
        assertEquals(0, testMap.get(AttendanceList.Status.ABSENT).get("BAME 0002").size());
        assertEquals(0, testMap.get(AttendanceList.Status.ABSENT).get("BAME 0003").size());

        //1 Candidate in BARRED List
        assertEquals(1, testMap.get(AttendanceList.Status.BARRED).get("BAME 0002").get("RMB3").size());
        assertEquals(0, testMap.get(AttendanceList.Status.BARRED).get("BAME 0001").size());
        assertEquals(0, testMap.get(AttendanceList.Status.BARRED).get("BAME 0003").size());

        //1 Candidate in EXEMPTED List
        assertEquals(1, testMap.get(AttendanceList.Status.EXEMPTED).get("BAME 0003").get("RMB3").size());
        assertEquals(0, testMap.get(AttendanceList.Status.EXEMPTED).get("BAME 0001").size());
        assertEquals(0, testMap.get(AttendanceList.Status.EXEMPTED).get("BAME 0002").size());
    }

    //= ClearDatabase() ============================================================================

    /************************************************************
     * clearDatabase()
     * should delete all the data in the database
     *
     * Initially, the database is not empty
     * after clearDatabase(), the database become empty
     ************************************************************/
    @Test
    public void testClearDatabase() throws Exception {
        Candidate cdd1 = new Candidate(1, "RMB3", "FGY", "15WAU00001", "BAME 0001",
                AttendanceList.Status.ABSENT);
        AttendanceList attdList = new AttendanceList();
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        clDbLoader.saveAttendanceList(attdList);
        assertFalse(clDbLoader.isEmpty());

        CheckListLoader.clearDatabase();
        assertTrue(clDbLoader.isEmpty());

    }


    //= getLastSavedAttendanceList() ===============================================================

    /*********************************************************************
     * getLastSavedAttd()
     *
     * should return an empty HashMap of AttendanceList
     * instead of null when the database is empty.
     **********************************************************************/
    @Test
    public void testGetLastSavedAttendanceList_withEmptyDatabase() throws Exception {
        HashMap<AttendanceList.Status, HashMap<String, HashMap<String, HashMap<String, Candidate>>>>
                map1 = clDbLoader.getLastSavedAttendanceList();

        assertNotNull(map1);
        assertEquals(4, map1.size());
        assertEquals(0, map1.get(AttendanceList.Status.PRESENT).size());
        assertEquals(0, map1.get(AttendanceList.Status.ABSENT).size());
        assertEquals(0, map1.get(AttendanceList.Status.BARRED).size());
        assertEquals(0, map1.get(AttendanceList.Status.EXEMPTED).size());
    }

    /*********************************************************************
     * getLastSavedAttd()
     *
     * should return a HashMap of AttendanceList as in the database
     **********************************************************************/
    @Test
    public void testGetLastSavedAttendanceList() throws Exception {
        Candidate cdd1 = new Candidate(1, "RMB3", "FGY", "15WAU00001", "BAME 0001",
                AttendanceList.Status.ABSENT);
        AttendanceList attdList = new AttendanceList();
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        clDbLoader.saveAttendanceList(attdList);

        HashMap<AttendanceList.Status, HashMap<String, HashMap<String, HashMap<String, Candidate>>>>
                map1 = clDbLoader.getLastSavedAttendanceList();

        assertNotNull(map1);
        assertEquals(4, map1.size());
        assertEquals(0, map1.get(AttendanceList.Status.PRESENT).size());
        assertEquals(1, map1.get(AttendanceList.Status.ABSENT).size());
        assertEquals(0, map1.get(AttendanceList.Status.BARRED).size());
        assertEquals(0, map1.get(AttendanceList.Status.EXEMPTED).size());
    }

    //= isEmpty() ==================================================================================
    /**********************************************************************
     * isEmpty()
     * should return true when the database is empty without table
     * should return false when the database is not empty
     *
     * At first, the database was empty and isEmpty() return true
     * When the attdList was saved, isEmpty() return false
     **********************************************************************/
    @Test
    public void testIsEmpty() throws Exception {
        boolean isEmpty = clDbLoader.isEmpty();
        assertTrue(isEmpty);
        //----------------------- ADD Thing into Database ------------------------------------
        Candidate cdd1 = new Candidate(1, "RMB3", "FGY", "15WAU00001", "BAME 0001",
                AttendanceList.Status.ABSENT);
        AttendanceList attdList = new AttendanceList();
        attdList.addCandidate(cdd1, cdd1.getPaperCode(), cdd1.getStatus(), cdd1.getProgramme());
        clDbLoader.saveAttendanceList(attdList);
        //----------------------- Database no longer empty ------------------------------------
        isEmpty = clDbLoader.isEmpty();
        assertFalse(isEmpty);
    }
}