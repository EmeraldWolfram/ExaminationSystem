package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.InfoGrabMVP;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


/**
 * Created by GhinY on 01/07/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class InfoGrabModelTest {
    private InfoGrabMVP.MPresenter taskPresenter;
    private InfoGrabModel model;
    private JavaHost javaHost;
    private StaffIdentity staff;

    @Before
    public void setUp() throws Exception {
        JavaHost.setConnector(new Connector("add", 7032, "DUEL"));
        staff           = new StaffIdentity("id", true, "name", "M4");
        LoginModel.setStaff(staff);

        javaHost = Mockito.mock(JavaHost.class);
        ConnectionTask connectionTask   = Mockito.mock(ConnectionTask.class);
        ExternalDbLoader.setConnectionTask(connectionTask);
        ExternalDbLoader.setJavaHost(javaHost);

        taskPresenter   = Mockito.mock(InfoGrabMVP.MPresenter.class);

        model = new InfoGrabModel(taskPresenter);
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
            model.reqCandidatePapers("15");
            fail("Expected MESSAGE_TOAST but nothing was thrown");
        } catch (ProcessException err){
            assertEquals("Not a candidate ID", err.getErrorMsg());
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());

            verify(javaHost, never()).putMessageIntoSendQueue(anyString());
        }
    }

    /**
     *  reqCandidatePapers()
     *
     *  When the candidate was found, request for paper will be sent to Chief
     */
    @Test
    public void testReqCandidatePapers_ValidCandidate() throws Exception {
        try{
            model.reqCandidatePapers("15WAU00001");

            verify(javaHost).putMessageIntoSendQueue(anyString());
        } catch (ProcessException err){
            fail("No Exception expected but " +  err.getErrorMsg() + " was thrown");
        }
    }


    //= Run() ======================================================================================
    /**
     * run()
     *
     * 1. When ConnectionTask is complete, do nothing
     * 2. When ConnectionTask is not complete, throw an error and the presenter shall handle
     */
    @Test
    public void testRun_ChiefDoRespond() throws Exception {
        ConnectionTask.setCompleteFlag(true);
        model.run();
        verify(taskPresenter, never()).onTimesOut(any(ProcessException.class));
    }

    @Test
    public void testRun_ChiefNoRespond() throws Exception {
        ConnectionTask.setCompleteFlag(false);
        model.run();
        verify(taskPresenter).onTimesOut(any(ProcessException.class));
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