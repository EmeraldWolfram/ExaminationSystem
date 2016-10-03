package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by GhinY on 15/06/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class LoginHelperTest {
    private LoginHelper helper;
    private StaffIdentity staffId;
    private CheckListLoader dbLoader;

    @Before
    public void setUp() throws Exception{
        TCPClient.setConnector(null);
        staffId = new StaffIdentity("12WW", true, "MR. TEST", "H3");
        dbLoader    = Mockito.mock(CheckListLoader.class);

        ExternalDbLoader.setConnectionTask(null);
        helper  = new LoginHelper();
    }
    //= TryConnectWithQR() ==============================================================================
    /**
     *  tryConnectWithQR()
     *
     *  when input String was correct Chief Address format
     *  TCP Client ServerIP and ServerPort will be set
     *  and new Thread will be started
     */
    @Test
    public void testTryConnectWithQR_If_correct_String_format() throws Exception{
        try{
            assertNull(TCPClient.connector);
            assertNull(ExternalDbLoader.getConnectionTask());

            String str = "$CHIEF:192.168.0.1:5000:$";
            helper.tryConnectWithQR(str, dbLoader);

            verify(dbLoader).saveConnector(any(Connector.class));
            assertEquals("192.168.0.1", TCPClient.connector.getIpAddress());
            assertEquals(Integer.valueOf(5000), TCPClient.connector.getPortNumber());
            assertNotNull(ExternalDbLoader.getConnectionTask());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    /**
     *  tryConnectWithQR()
     *
     *  when input String was incorrect Chief Address format
     *  MESSAGE TOAST shall be thrown
     */
    @Test
    public void testTryConnectWithQR_If_wrong_String_format() throws Exception{
        try{
            assertNull(TCPClient.connector);
            assertNull(ExternalDbLoader.getConnectionTask());
            String str = "$CHIEF:192.168.0.1:5000:";

            helper.tryConnectWithQR(str, dbLoader);

            fail("Expected MESSAFE TOAST Exception but none were thrown");
        } catch (ProcessException err){
            assertNull(TCPClient.connector);
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Not a chief address", err.getErrorMsg());
            verify(dbLoader, never()).saveConnector(any(Connector.class));
            assertNull(ExternalDbLoader.getConnectionTask());
        }
    }

    //= MatchStaffPw() =============================================================================
    /**
     * matchStaffPw(String inputPw)
     *
     * thrown FATAL Message if the staff is not declare yet.
     * In fact, this will never happen.
     * Without Staff declaration, will not prompt for pw
     */
    @Test
    public void testMatchStaffPw_Staff_is_null_should_throw_FATAL_MESSAGE() throws Exception{
        try{
            LoginHelper.setStaff(null);
            helper.matchStaffPw(null);
            fail("Expected FATAL_MESSAGE but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("Input ID is null", err.getErrorMsg());
        }
    }

    /**
     * matchStaffPw(String inputPw)
     *
     * when the inputPw was empty
     * MESSAGE_TOAST will be thrown
     */
    @Test
    public void testMatchStaffPw_NULL_PW_should_throw_MESSAGE_TOAST() throws Exception{
        try{
            LoginHelper.setStaff(staffId);
            helper.matchStaffPw(null);

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Please enter a password to proceed", err.getErrorMsg());
        }
    }

    /**
     * matchStaffPw(String inputPw)
     *
     * when the inputPw was empty
     * MESSAGE_TOAST will be thrown
     */
    @Test
    public void testMatchStaffPw_EMPTY_PW_should_throw_MESSAGE_TOAST() throws Exception{
        try{
            LoginHelper.setStaff(staffId);
            helper.matchStaffPw("");

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Please enter a password to proceed", err.getErrorMsg());
        }
    }

    /**
     * matchStaffPw(String inputPw)
     *
     * when the inputPw was empty
     * MESSAGE_TOAST will be thrown
     */
    @Test
    public void testMatchStaffPw_Valid_PW_should_send_message() throws Exception{
        try{
            LoginHelper.setStaff(staffId);
            helper.matchStaffPw("ABCD");

            //assertEquals("Please enter a password to proceed", err.getErrorMsg());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    //= checkQrId() ================================================================================
    /**
     * checkQrId(scanStr)
     *
     * This method used to check an input string (scanned-in) whether it was possible to
     * be an ID
     *
     * 1. throw MESSAGE_TOAST if the QR scanned is not possible to be an ID
     * 2. do nothing if the scanned string could be an ID
     */
    @Test
    public void testCheckQrId_throw_MESSAGE_TOAST() throws Exception{
        try{
            helper.checkQrId("My name");
            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Invalid staff ID Number", err.getErrorMsg());
        }
    }

    @Test
    public void testCheckQrId_Nothing_Happen() throws Exception {
        try{
            helper.checkQrId("012345");
        } catch (ProcessException err) {
            fail("Expected nothing but thrown " + err.getErrorMsg());
        }
    }

    //= TryConnectWithDatabase() ============================================================================

    /**
     * tryConnectWithDatabase(...)
     *
     * 1. Database is empty, return false and did not start any thread
     * 2. Database stored invalid connector, return false and did not start any thread
     * 3. Database contain valid connector, start a new thread and return true
     */
    @Test
    public void testTryConnectWithDatabase_withEmptyDb_return_false() throws Exception {
        CheckListLoader dbLoader    = Mockito.mock(CheckListLoader.class);
        when(dbLoader.queryConnector()).thenReturn(null);
        assertNull(ExternalDbLoader.getConnectionTask());

        assertFalse(helper.tryConnectWithDatabase(dbLoader));
        assertNull(ExternalDbLoader.getConnectionTask());
    }

    @Test
    public void testTryConnectWithDatabase_withInvalidDb_return_false() throws Exception {
        CheckListLoader dbLoader    = Mockito.mock(CheckListLoader.class);
        Connector connector         = new Connector("127.0.0.1", 6666);
        Calendar date               = Calendar.getInstance();
        date.set(2016, 7, 18);
        connector.setDate(date);
        assertNull(ExternalDbLoader.getConnectionTask());

        when(dbLoader.queryConnector()).thenReturn(connector);
        assertFalse(helper.tryConnectWithDatabase(dbLoader));
        assertNull(ExternalDbLoader.getConnectionTask());
    }

    @Test
    public void testTryConnectWithDatabase_withValidDb_return_true() throws Exception {
        CheckListLoader dbLoader    = Mockito.mock(CheckListLoader.class);
        Connector connector         = new Connector("127.0.0.1", 6666);
        when(dbLoader.queryConnector()).thenReturn(connector);
        assertNull(ExternalDbLoader.getConnectionTask());

        assertTrue(helper.tryConnectWithDatabase(dbLoader));
        assertNotNull(ExternalDbLoader.getConnectionTask());
    }

    //= CloseConnection() ==========================================================================
    /**
     * closeConnection()
     *
     * 1. When ConnectTask = null, TCPClient = null, do nothing
     * 2. When ConnectTask != null, TCPClient = null, cancel ConnectTask
     * 3. When ConnectTask = null, TCPClient != null, stop TCPClient
     * 4. When ConnectTask != null, TCPClient != null, stop TCP and cancel ConnectTask
     */
    @Test
    public void testCloseConnection_BothNull() throws Exception {
        ConnectionTask task = Mockito.mock(ConnectionTask.class);
        TCPClient client    = Mockito.mock(TCPClient.class);

        helper.closeConnection();

        verify(client, never()).stopClient();
        verify(client, never()).sendMessage("Termination");
        verify(task, never()).cancel(true);
        assertNull(ExternalDbLoader.getConnectionTask());
    }

    @Test
    public void testCloseConnection_TaskNotNull() throws Exception {
        ConnectionTask task = Mockito.mock(ConnectionTask.class);
        TCPClient client    = Mockito.mock(TCPClient.class);
        ExternalDbLoader.setConnectionTask(task);

        helper.closeConnection();

        verify(client, never()).stopClient();
        verify(client, never()).sendMessage("Termination");
        verify(task).cancel(true);
        assertNull(ExternalDbLoader.getConnectionTask());
    }

    @Test
    public void testCloseConnection_TCPClientNotNull() throws Exception {
        ConnectionTask task = Mockito.mock(ConnectionTask.class);
        TCPClient client    = Mockito.mock(TCPClient.class);
        ExternalDbLoader.setTcpClient(client);

        helper.closeConnection();

        verify(client).stopClient();
        verify(client).sendMessage("Termination");
        verify(task, never()).cancel(true);
        assertNull(ExternalDbLoader.getConnectionTask());
    }

    @Test
    public void testCloseConnection_BothNotNull() throws Exception {
        ConnectionTask task = Mockito.mock(ConnectionTask.class);
        TCPClient client    = Mockito.mock(TCPClient.class);
        ExternalDbLoader.setTcpClient(client);
        ExternalDbLoader.setConnectionTask(task);

        helper.closeConnection();

        verify(client).stopClient();
        verify(client).sendMessage("Termination");
        verify(task).cancel(true);
        assertNull(ExternalDbLoader.getConnectionTask());
    }

}