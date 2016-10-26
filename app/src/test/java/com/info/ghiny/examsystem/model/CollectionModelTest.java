package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.CollectionMVP;

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
 * Created by GhinY on 05/10/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class CollectionModelTest {

    private CollectionModel model;
    private CollectionMVP.PresenterForModel presenterFace;
    private TCPClient tcpClient;
    private StaffIdentity staff;

    @Before
    public void setUp() throws Exception {
        TCPClient.setConnector(new Connector("add", 7032, "DUEL"));
        staff           = new StaffIdentity("id", true, "name", "M4");
        LoginModel.setStaff(staff);

        tcpClient = Mockito.mock(TCPClient.class);
        ConnectionTask connectionTask   = Mockito.mock(ConnectionTask.class);
        ExternalDbLoader.setConnectionTask(connectionTask);
        ExternalDbLoader.setTcpClient(tcpClient);
        presenterFace   = Mockito.mock(CollectionMVP.PresenterForModel.class);

        model   = new CollectionModel(presenterFace);
    }

    //= BundleCollection(...) ======================================================================

    /**
     * Temporary check
     * @throws Exception
     */
    @Test
    public void bundleCollection() throws Exception {
        try{
            model.bundleCollection("CORRECT FORMAT");
            verify(tcpClient).sendMessage(anyString());
        } catch (ProcessException err){
            fail("No exception expected but thrown " + err.getErrorMsg());
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
        verify(presenterFace, never()).onTimesOut(any(ProcessException.class));
    }

    @Test
    public void testRun_ChiefNoRespond() throws Exception {
        ConnectionTask.setCompleteFlag(false);
        model.run();
        verify(presenterFace).onTimesOut(any(ProcessException.class));
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