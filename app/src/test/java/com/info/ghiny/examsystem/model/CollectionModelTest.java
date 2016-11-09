package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.PaperBundle;
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
import static org.mockito.Mockito.when;

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

    //= VerifyCollector(...) =======================================================================
    /**
     * verifyCollector(...);
     *
     * This method check if the string is a collector using the length
     *
     * Tests:
     * 1. The length is 6, assign as a collector
     * 2. The length is > 6, did not assign
     * 3. The length is < 6, did not assign
     */
    @Test
    public void testVerifyCollector1_PositiveTest() throws Exception {
        assertNull(model.getStaffIdentity());

        assertTrue(model.verifyCollector("012345"));

        assertNotNull(model.getStaffIdentity());
    }

    @Test
    public void testVerifyCollector2_NegativeTest() throws Exception {
        assertNull(model.getStaffIdentity());

        assertFalse(model.verifyCollector("01234567"));

        assertNull(model.getStaffIdentity());
    }

    @Test
    public void testVerifyCollector3_NegativeTest() throws Exception {
        assertNull(model.getStaffIdentity());

        assertFalse(model.verifyCollector("01234"));

        assertNull(model.getStaffIdentity());
    }

    //= VerifyBundle(...) ==========================================================================
    /**
     * verifyBundle(...)
     *
     * This method check if the string is a bundle using the PaperBundle instance parse method
     *
     * Tests:
     * 1. A valid bundle code with three "/", return true
     * 2. Invalid bundle code, return false
     */
    @Test
    public void testVerifyBundle1_PositiveTest() throws Exception {
        assertNull(model.getBundle());

        assertTrue(model.verifyBundle("M4/BAME 0001/RMB3"));

        assertNotNull(model.getBundle());
    }

    @Test
    public void testVerifyBundle2_NegativeTest() throws Exception {
        assertNull(model.getBundle());

        assertFalse(model.verifyBundle("M4/BAME 0001RMB3"));

        assertNull(model.getBundle());
    }


    //= BundleCollection(...) ======================================================================

    /**
     * bundleCollection(...)
     *
     * This method take in any string decoded from QR code perform 2 verification
     * 1. Verify if the string is staff ID
     * 2. Verify if the string is bundle information
     * If it was any of it, it will save and hold it, else throw error
     *
     * When both string was received, it send out the staff id and bundle to acknowledge collection
     * of bundle
     *
     * Tests:
     * 1. Staff ID & Bundle are both null,
     *      - input is staff ID, assign Staff ID but did not sent
     *
     * 2. Staff ID & Bundle are both null,
     *      - input is bundle info, assign bundle but did not sent
     *
     * 3. Staff ID (null) & Bundle not null,
     *      - input is staff ID, assign Staff ID, send out data and clear the two holder to null
     *
     * 4. Staff ID not null & Bundle (null),
     *      - input is bundle, assign bundle, send out data and clear the two holder to null
     *
     * 5. Input is neither bundle info or staff ID, throw MESSAGE_TOAST
     */
    @Test
    public void bundleCollection1_BothNotAssign() throws Exception {
        try{
            assertNull(model.getBundle());
            assertNull(model.getStaffIdentity());

            model.bundleCollection("246810");

            assertNull(model.getBundle());
            assertEquals("246810", model.getStaffIdentity());
            verify(tcpClient, never()).sendMessage(anyString());
        } catch (ProcessException err){
            fail("No exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void bundleCollection2_BothNotAssign() throws Exception {
        try{
            assertNull(model.getBundle());
            assertNull(model.getStaffIdentity());

            model.bundleCollection("M4/BAME 3233/RMB3");

            assertNotNull(model.getBundle());
            assertNull(model.getStaffIdentity());
            verify(tcpClient, never()).sendMessage(anyString());
        } catch (ProcessException err){
            fail("No exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void bundleCollection3_BundleAssigned() throws Exception {
        try{
            model.verifyBundle("M4/BAME 3233/RMB3");
            assertNotNull(model.getBundle());
            assertNull(model.getStaffIdentity());

            model.bundleCollection("246810");

            assertNull(model.getBundle());
            assertNull(model.getStaffIdentity());
            verify(tcpClient).sendMessage(anyString());
        } catch (ProcessException err){
            fail("No exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void bundleCollection4_StaffAssigned() throws Exception {
        try{
            model.setStaffIdentity("246810");
            assertNull(model.getBundle());
            assertNotNull(model.getStaffIdentity());

            model.bundleCollection("M4/BAME 3233/RMB3");

            assertNull(model.getBundle());
            assertNull(model.getStaffIdentity());
            verify(tcpClient).sendMessage(anyString());
        } catch (ProcessException err){
            fail("No exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void bundleCollection5_ThrowMessageToastWhenQRNotRecognize() throws Exception {
        try{
            model.bundleCollection("INCORRECT KIND OF QR STRING");
            fail("Expected MESSAGE_TOAST but none was thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("The decrypted QR code is neither Staff ID or Bundle", err.getErrorMsg());
            verify(tcpClient, never()).sendMessage(anyString());
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
        when(presenterFace.isAcknowledgementComplete()).thenReturn(true);
        model.run();
        verify(presenterFace, never()).onTimesOut(any(ProcessException.class));
    }

    @Test
    public void testRun_ChiefNoRespond() throws Exception {
        when(presenterFace.isAcknowledgementComplete()).thenReturn(false);
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