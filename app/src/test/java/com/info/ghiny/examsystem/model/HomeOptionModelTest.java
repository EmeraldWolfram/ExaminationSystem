package com.info.ghiny.examsystem.model;


import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.LocalDbLoader;
import com.info.ghiny.examsystem.database.Role;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.HomeOptionMVP;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by user09 on 12/25/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class HomeOptionModelTest {

    private HomeOptionModel model;
    private HomeOptionMVP.MvpMPresenter taskPresenter;
    private LocalDbLoader dBLoader;
    private StaffIdentity staff;
    private Connector connector;

    private AttendanceList attendanceList;
    private ConnectionTask connectionTask;
    private JavaHost javaHost;

    @Before
    public void setUp() throws Exception {
        staff           = new StaffIdentity("id", true, "name", "M4");
        connectionTask  = Mockito.mock(ConnectionTask.class);
        javaHost        = Mockito.mock(JavaHost.class);
        dBLoader        = Mockito.mock(LocalDbLoader.class);

        connector = new Connector("add", 7032, "DUEL");
        JavaHost.setConnector(connector);
        LoginModel.setStaff(staff);

        ExternalDbLoader.setConnectionTask(connectionTask);
        ExternalDbLoader.setJavaHost(javaHost);


        taskPresenter = Mockito.mock(HomeOptionMVP.MvpMPresenter.class);
        model   = new HomeOptionModel(taskPresenter, dBLoader);
    }

    @After
    public void tearDown() throws Exception {

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

    //= PrepareLogout ==============================================================================

    /**
     * prepareLogout()
     *
     * This method prepare an Exception with the
     * -Yes Button as confirm logout
     * -No Button for cancel
     *
     */
    @Test
    public void prepareLogout() throws Exception {
        ProcessException test   = model.prepareLogout();

        assertEquals("Confirm logout and exit?", test.getErrorMsg());
        assertEquals(ProcessException.YES_NO_MESSAGE, test.getErrorType());
        assertEquals(taskPresenter, test.getBackPressListener());
        assertEquals(taskPresenter, test.getListener(ProcessException.yesButton));
        assertEquals(taskPresenter, test.getListener(ProcessException.noButton));
    }

    //= InitAttendance() ===========================================================================
    /**
     * initAttendance()
     *
     * This method prepare the attendance list and exam subjects info for the
     * attendance taking process.
     *
     * It prepare by loading database or query from chief
     *
     * Tests:
     * 1. Connected to chief, database got NO attendance list, send request to chief & start timer
     * 2. Connected to chief, database got NO exam papers info, send request to chief & start timer
     * 3. Connected to chief, database got attendance list & exam papers info, load from the database
     * 4. Connected to in-charge, send request to in-charge & start timer
     */
    @Test
    public void testInitAttendance1_NoAttendanceListInDB() throws Exception {
        connector.setMyHost(Role.CHIEF);
        when(dBLoader.emptyAttdInDB()).thenReturn(true);
        when(dBLoader.emptyPapersInDB()).thenReturn(false);

        model.initAttendance();

        verify(javaHost).putMessageIntoSendQueue(anyString());
        verify(dBLoader, never()).queryAttendanceList();
        verify(dBLoader, never()).queryPapers();
        assertFalse(model.isInitialized());
    }

    @Test
    public void testInitAttendance2_NoExamPapersInfoInDB() throws Exception {
        connector.setMyHost(Role.CHIEF);
        when(dBLoader.emptyAttdInDB()).thenReturn(false);
        when(dBLoader.emptyPapersInDB()).thenReturn(true);

        model.initAttendance();

        verify(javaHost).putMessageIntoSendQueue(anyString());
        verify(dBLoader, never()).queryAttendanceList();
        verify(dBLoader, never()).queryPapers();
        assertFalse(model.isInitialized());
    }

    @Test
    public void testInitAttendance3_RequiredInfoAvailableDB() throws Exception {
        connector.setMyHost(Role.CHIEF);
        attendanceList  = new AttendanceList();
        when(dBLoader.emptyAttdInDB()).thenReturn(false);
        when(dBLoader.emptyPapersInDB()).thenReturn(false);
        when(dBLoader.queryAttendanceList()).thenReturn(attendanceList);

        model.initAttendance();

        verify(javaHost, never()).putMessageIntoSendQueue(anyString());
        verify(dBLoader).queryAttendanceList();
        verify(dBLoader).queryPapers();
        assertTrue(model.isInitialized());
    }

    @Test
    public void testInitAttendance4_RequiredInfoFromInCharge() throws Exception {
        connector.setMyHost(Role.IN_CHARGE);
        when(dBLoader.emptyAttdInDB()).thenReturn(true);
        when(dBLoader.emptyPapersInDB()).thenReturn(false);

        model.initAttendance();

        verify(javaHost).putMessageIntoSendQueue(anyString());
        verify(dBLoader, never()).queryAttendanceList();
        verify(dBLoader, never()).queryPapers();
        assertFalse(model.isInitialized());
    }

    //= CheckDownloadResult() ======================================================================
    /**
     * checkDownloadResult()
     *
     * During initAttendance(), request for attendance list and papers was sent to chief
     * This method was used to read the chief respond and take action accordingly
     *
     * 1. Chief respond with the attendance list and exam papers, parse the message and assign
     * 2. Chief respond with negative message due to some reason, throw an error
     *
     */
    @Test
    public void testCheckDownloadResult1_PositiveRespondFromChief() throws Exception {
        String MESSAGE_FROM_CHIEF = "{\"Type\":\"VenueInfo\",\"AttendanceList\":[" +
                "{\"ExamIndex\":\"W0001AAAA\",\"Status\":\"LEGAL\",\"PaperCode\":\"MPU3123\"," +
                "\"RegNum\":\"15WAR00001\",\"Programme\":\"RMB3\"}," +
                "{\"ExamIndex\":\"W0002AAAA\",\"Status\":\"LEGAL\",\"PaperCode\":\"MPU3123\"," +
                "\"RegNum\":\"15WAR00002\",\"Programme\":\"RMB3\"}," +
                "{\"ExamIndex\":\"W0003AAAA\",\"Status\":\"LEGAL\",\"PaperCode\":\"MPU3123\"," +
                "\"RegNum\":\"15WAR00003\",\"Programme\":\"RMB3\"}," +
                "{\"ExamIndex\":\"W0004AAAA\",\"Status\":\"BARRED\",\"PaperCode\":\"MPU3123\"," +
                "\"RegNum\":\"15WAR00004\",\"Programme\":\"RMB3\"}," +
                "{\"ExamIndex\":\"W0005AAAA\",\"Status\":\"EXEMPTED\",\"PaperCode\":\"MPU3123\"," +
                "\"RegNum\":\"15WAR00005\",\"Programme\":\"RMB3\"}]," +
                "\"PaperMap\":[" +
                "{\"PaperStartNo\":45,\"PaperDesc\":\"Mathematic\",\"PaperCode\":\"BABE2203\"," +
                "\"PaperTotalCdd\":4}," +
                "{\"PaperStartNo\":32,\"PaperDesc\":\"Hubungan Etnik\",\"PaperCode\":\"MPU3123\"," +
                "\"PaperTotalCdd\":9}]," +
                "\"Result\":true}";

        try{
            assertFalse(model.isInitialized());
            Candidate.setPaperList(null);
            TakeAttdModel.setAttdList(null);

            model.checkDownloadResult(MESSAGE_FROM_CHIEF);

            fail("Expected MESSAGE_TOAST but none was thrown!");
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Download Complete", err.getErrorMsg());

            assertTrue(model.isInitialized());
            assertNotNull(Candidate.getPaperList());
            assertNotNull(TakeAttdModel.getAttdList());
        }
    }

    @Test
    public void testCheckDownloadResult2_NegativeRespondFromChief() throws Exception {
        String MESSAGE_FROM_CHIEF = "{\"Result\":false,\"Type\":\"VenueInfo\"}";
        try{
            assertFalse(model.isInitialized());
            Candidate.setPaperList(null);
            TakeAttdModel.setAttdList(null);

            model.checkDownloadResult(MESSAGE_FROM_CHIEF);
        } catch (ProcessException err) {
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("Unable to download Attendance List\nPlease retry login",err.getErrorMsg());
            //Make no changes to PaperList and AttdList
            assertNull(Candidate.getPaperList());
            assertNull(TakeAttdModel.getAttdList());
        }
    }

    //= SaveAttendance() ===========================================================================

    /**
     * saveAttendance()
     *
     * This method is used to save the attendance list into the database before
     * the activity was destroyed.
     *
     * Test
     * 1. Attendance List is ready and host is Chief, save the attendance list in database
     * 2. Attendance List is null and host is Chief, do not save anything
     * 3. Attendance List is ready and host is In-Charge, do not save anything
     */
    @Test
    public void testSaveAttendance1_PositiveTest() throws Exception {
        connector.setMyHost(Role.CHIEF);
        TakeAttdModel.setAttdList(new AttendanceList());

        model.saveAttendance();

        verify(dBLoader).saveAttendanceList(any(AttendanceList.class));
        verify(dBLoader).savePaperList(any(HashMap.class));
    }

    @Test
    public void testSaveAttendance2_AttendanceListIsNull() throws Exception {
        connector.setMyHost(Role.CHIEF);
        TakeAttdModel.setAttdList(null);

        model.saveAttendance();

        verify(dBLoader, never()).saveAttendanceList(any(AttendanceList.class));
        verify(dBLoader, never()).savePaperList(any(HashMap.class));
    }

    @Test
    public void testSaveAttendance3_HostIsNotChief() throws Exception {
        connector.setMyHost(Role.IN_CHARGE);
        TakeAttdModel.setAttdList(new AttendanceList());

        model.saveAttendance();

        verify(dBLoader, never()).saveAttendanceList(any(AttendanceList.class));
        verify(dBLoader, never()).savePaperList(any(HashMap.class));
    }

    //= Run() ======================================================================================
    /**
     * run()
     *
     * 1. When ConnectionTask is complete, do nothing
     * 2. Initialize using database, no times out should be thrown
     * 3. When ConnectionTask is not complete, throw an error and the presenter shall handle
     */
    @Test
    public void testRun_ChiefDoRespond() throws Exception {
        model.setDownloadComplete(true);
        model.setInitialized(true);
        model.run();
        verify(taskPresenter).onTimesOut(any(ProcessException.class));
    }

    @Test
    public void testRun_ChiefNoRespond() throws Exception {
        model.setDownloadComplete(false);
        model.setInitialized(true);
        model.run();
        verify(taskPresenter).onTimesOut(any(ProcessException.class));
    }

    @Test
    public void testRun_UsedDatabaseRespond() throws Exception {
        model.setInitialized(false);
        model.setDownloadComplete(false);
        model.run();
        verify(taskPresenter).onTimesOut(any(ProcessException.class));
    }

}