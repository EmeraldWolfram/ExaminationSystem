package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.Session;
import com.info.ghiny.examsystem.database.StaffIdentity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Copyright (C) 2016 - 2017 Steven Foong Ghin Yew <stevenfgy@yahoo.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class InfoDisplayModelTest {
    private InfoDisplayModel model;
    private String MESSAGE_FROM_CHIEF;
    private StaffIdentity staff;

    private List<ExamSubject> subjects;
    private ExamSubject subject1;
    private ExamSubject subject2;
    private ExamSubject subject3;

    @Before
    public void setUp() throws Exception {
        JavaHost.setConnector(new Connector("add", 7032, "DUEL"));
        staff           = new StaffIdentity("id", true, "name", "M4");
        LoginModel.setStaff(staff);

        model   = new InfoDisplayModel();
        subjects    = new ArrayList<>();
        model.setPapers(subjects);

        subject1    = new ExamSubject("BAME 0001", "SUBJECT 1", 10, Calendar.getInstance(), 20,
                "H1", Session.AM);
        subject2    = new ExamSubject("BAME 0002", "SUBJECT 2", 30, Calendar.getInstance(), 20,
                "H2", Session.PM);
        subject3    = new ExamSubject("BAME 0003", "SUBJECT 3", 50, Calendar.getInstance(), 20,
                "H3", Session.VM);

    }

    //= UpdateSubjects() ===========================================================================

    /**
     * updateSubject()
     *
     * parse the subject into list of exam subject and place into the subjects list
     *
     * @throws Exception
     */

    @Test
    public void updateSubjects() throws Exception {
        assertEquals(0, subjects.size());
        MESSAGE_FROM_CHIEF = "{\"Result\":true,\"Type\":\"PaperList\"," +
                "\"Value\":[" +
                "{\"PaperDesc\":\"SUBJECT 1\",\"PaperVenue\":\"M4\",\"PaperCode\":\"BAME 0001\"," +
                                                "\"PaperDate\":\"10:10:2016\",\"PaperSession\":\"AM\"}," +
                "{\"PaperDesc\":\"SUBJECT 2\",\"PaperVenue\":\"M4\",\"PaperCode\":\"BAME 0002\"," +
                                                "\"PaperDate\":\"9:10:2016\",\"PaperSession\":\"AM\"}]}";

        model.updateSubjects(MESSAGE_FROM_CHIEF);

        assertEquals(2, subjects.size());
    }

    //= GetSubjectAt(...) ==========================================================================
    @Test
    public void testGetSubjectAt() throws Exception {
        subjects.add(subject1);
        subjects.add(subject2);
        subjects.add(subject3);

        assertEquals(subject1, model.getSubjectAt(0));
        assertEquals(subject2, model.getSubjectAt(1));
        assertEquals(subject3, model.getSubjectAt(2));

    }

    //= GetDaysLeft() ==============================================================================
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

        Integer dayLeft = model.getDaysLeft(paperDate);

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

        Integer dayLeft = model.getDaysLeft(paperDate);

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

        Integer dayLeft = model.getDaysLeft(paperDate);

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

        Integer dayLeft = model.getDaysLeft(paperDate);

        assertEquals(800, dayLeft.intValue());
    }

    //= GetNumberOfSubject() =======================================================================
    @Test
    public void getNumberOfSubject() throws Exception {
        assertEquals(0, model.getNumberOfSubject());
        subjects.add(subject1);
        assertEquals(1, model.getNumberOfSubject());
        subjects.add(subject2);
        assertEquals(2, model.getNumberOfSubject());
        subjects.add(subject3);
        assertEquals(3, model.getNumberOfSubject());
    }

    //= MatchPassword(...) =========================================================================
    /**
     * matchPassword(...)
     *
     * This method is used after the user had logged in but inactive for sometime
     * Prompt for password and match it when the user try to activate the phone again
     *
     * Tests:
     * 1. When input password is CORRECT, do nothing
     * 2. When input password is INCORRECT, throw MESSAGE_TOAST Exception
     *
     */
    @Test
    public void testMatchPassword1_CorrectPasswordReceived() throws Exception {
        staff.setPassword("CORRECT");
        String hashPass = staff.hmacSha("CORRECT", "DUEL");
        staff.setHashPass(hashPass);
        try{
            model.matchPassword("CORRECT");
        } catch (ProcessException err) {
            fail("Exception --" + err.getErrorMsg() + "-- not expected!");
        }
    }

    @Test
    public void testMatchPassword2_IncorrectPasswordReceived() throws Exception {
        staff.setPassword("CORRECT");
        String hashPass = staff.hmacSha("CORRECT", "DUEL");
        staff.setHashPass(hashPass);
        try{
            model.matchPassword("INCORRECT");
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Access denied. Incorrect Password", err.getErrorMsg());
        }
    }
}