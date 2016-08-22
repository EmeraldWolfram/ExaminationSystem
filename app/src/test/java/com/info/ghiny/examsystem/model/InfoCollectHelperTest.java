package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.Session;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Calendar;

import static org.junit.Assert.*;


/**
 * Created by GhinY on 01/07/2016.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ExternalDbLoader.class)
public class InfoCollectHelperTest {
    InfoCollectHelper helper;
    ExamSubject subject1;
    ExamSubject subject2;
    ExamSubject subject3;
    ExamSubject subject4;

    @Before
    public void setUp() throws Exception {
        subject1 = new ExamSubject("BAME 0001", "SUBJECT 1", 25, Calendar.getInstance(),
                10, "H2", Session.AM);
        subject2 = new ExamSubject("BAME 0002", "SUBJECT 2", 55, Calendar.getInstance(),
                10, "H2", Session.AM);
        subject3 = new ExamSubject("BAME 0003", "SUBJECT 3", 10, Calendar.getInstance(),
                10, "H2", Session.AM);
        subject4 = new ExamSubject("BAME 0004", "SUBJECT 4", 70, Calendar.getInstance(),
                10, "H2", Session.AM);

        helper = new InfoCollectHelper();
        PowerMockito.mockStatic(ExternalDbLoader.class);
    }
    //= ReqCandidatePapers =========================================================================
    /**
     *  reqCandidatePapers()
     *
     *  When the candidate was not found
     *  MESSAGE_TOAST will be thrown
     */
    @Test
    public void testReqCandidatePapers_Throw_Error_input_string_size_not_10() throws Exception {
        try{
            helper.reqCandidatePapers("15");
            fail("Expected MESSAGE_TOAST but nothing was thrown");
        } catch (ProcessException err){
            assertEquals("Not a candidate ID", err.getErrorMsg());
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
        }
    }

    /**
     *  reqCandidatePapers()
     *
     *  When the candidate does not have any paper
     *  should return empty List<> but not null
     */
    @Test
    public void testReqCandidatePapers_CandidateWithoutPapers() throws Exception {
        try{
            helper.reqCandidatePapers("15WAU00001");
        } catch (ProcessException err){
            fail("No Exception expected but " +  err.getErrorMsg() + " was thrown");
        }
    }

    //= GetDaysLeft ================================================================================
    /**
     * getDaysLeft()
     *
     * return -1 when the Date of the paper to be examine is already past
     *
     * 1st of July --> today
     */
    @Test
    public void testGetDaysLeft_PastExam() throws Exception {
        Calendar paperDate = Calendar.getInstance();
        paperDate.set(2016, 6, 1);

        Integer dayLeft = helper.getDaysLeft(paperDate);

        assertEquals(-1, dayLeft.intValue());
    }

    /**
     * getDaysLeft()
     *
     * return -1 when the Date of the paper to be examine is already past
     * even if the DAY_OF_MONTH is larger for the paperDate
     *
     * Note: 5 = June, not May
     * As it start from 0, January is 0
     */
    @Test
    public void testGetDaysLeft_PastExam_With_Different_in_MONTH() throws Exception {
        Calendar paperDate = Calendar.getInstance();
        paperDate.set(2016, 5, 29);

        Integer dayLeft = helper.getDaysLeft(paperDate);

        assertEquals(-1, dayLeft.intValue());
    }

    /**
     * getDaysLeft()
     *
     * return 0 when the Date of the paper to be examine is the same day
     */
    @Test
    public void testGetDaysLeft_PresentExam() throws Exception {
        Calendar paperDate = Calendar.getInstance();

        Integer dayLeft = helper.getDaysLeft(paperDate);

        assertEquals(0, dayLeft.intValue());
    }

    /**
     * getDaysLeft()
     *
     * return number of day left when the Date of the paper to be examine is not yet
     */
    @Test
    public void testGetDaysLeft_FutureExam() throws Exception {
        Calendar paperDate = Calendar.getInstance();
        paperDate.add(Calendar.DAY_OF_MONTH, 4);

        Integer dayLeft = helper.getDaysLeft(paperDate);

        assertEquals(4, dayLeft.intValue());
    }

    /**
     * getDaysLeft()
     *
     * return number of day left when the Date of the paper to be examine was having year different
     * Although not realistic but it is a feature of the method to be correct
     */
    @Test
    public void testGetDaysLeft_FutureExamWithYearOfDifferent() throws Exception {
        Calendar paperDate = Calendar.getInstance();
        paperDate.add(Calendar.DAY_OF_MONTH, 800);

        Integer dayLeft = helper.getDaysLeft(paperDate);

        assertEquals(799, dayLeft.intValue());
    }
}