package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.ExternalDbLoader;

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
 * Created by GhinY on 05/10/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class LinkChiefModelTest {
    private CheckListLoader dbLoader;
    private LinkChiefModel model;

    @Before
    public void setUp() throws Exception {
        dbLoader    = Mockito.mock(CheckListLoader.class);
        model       = new LinkChiefModel(dbLoader);

        TCPClient.setConnector(null);
        ExternalDbLoader.setConnectionTask(null);
    }

//= TryConnectWithQR() ==============================================================================
    /**
     *  tryConnectWithQR()
     *
     *  1.  when input String was correct Chief Address format
     *      TCP Client ServerIP and ServerPort will be set
     *      and new Thread will be started to establish connection
     *
     *  2.  when input String was incorrect Chief Address format
     *      MESSAGE TOAST shall be thrown
     */
    @Test
    public void testTryConnectWithQR_If_correct_String_format() throws Exception{
        try{
            assertNull(TCPClient.connector);
            assertNull(ExternalDbLoader.getConnectionTask());

            model.tryConnectWithQR("$CHIEF:192.168.0.1:5000:DUEL:$");

            verify(dbLoader).saveConnector(any(Connector.class));
            assertEquals("192.168.0.1", TCPClient.connector.getIpAddress());
            assertEquals(Integer.valueOf(5000), TCPClient.connector.getPortNumber());
            assertNotNull(ExternalDbLoader.getConnectionTask());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testTryConnectWithQR_If_wrong_String_format() throws Exception{
        try{
            assertNull(TCPClient.connector);
            assertNull(ExternalDbLoader.getConnectionTask());

            model.tryConnectWithQR("$CHIEF:192.168.0.1:5000:DUEL:");

            fail("Expected MESSAFE TOAST Exception but none were thrown");
        } catch (ProcessException err){
            assertNull(TCPClient.connector);
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Not a chief address", err.getErrorMsg());
            verify(dbLoader, never()).saveConnector(any(Connector.class));
            assertNull(ExternalDbLoader.getConnectionTask());
        }
    }

    //= TryConnectWithDatabase() ===================================================================

    /**
     * tryConnectWithDatabase(...)
     *
     * 1. Database is empty, return false and did not start any thread
     * 2. Database stored invalid connector, return false and did not start any thread
     * 3. Database contain valid connector, start a new thread and return true
     */
    @Test
    public void testTryConnectWithDatabase_withEmptyDb_return_false() throws Exception {
        when(dbLoader.queryConnector()).thenReturn(null);
        assertNull(ExternalDbLoader.getConnectionTask());

        assertFalse(model.tryConnectWithDatabase());
        assertNull(ExternalDbLoader.getConnectionTask());
        verify(dbLoader, never()).clearDatabase();
        verify(dbLoader, never()).clearUserDatabase();
    }

    @Test
    public void testTryConnectWithDatabase_withInvalidDb_return_false() throws Exception {
        Connector connector         = new Connector("127.0.0.1", 6666, "DUEL");
        Calendar date               = Calendar.getInstance();
        date.set(2016, 7, 18);
        connector.setDate(date);
        assertNull(ExternalDbLoader.getConnectionTask());
        when(dbLoader.queryConnector()).thenReturn(connector);

        assertFalse(model.tryConnectWithDatabase());

        assertNull(ExternalDbLoader.getConnectionTask());
        verify(dbLoader).clearDatabase();
        verify(dbLoader).clearUserDatabase();
    }

    @Test
    public void testTryConnectWithDatabase_withValidDb_return_true() throws Exception {
        Connector connector         = new Connector("127.0.0.1", 6666, "DUEL");
        when(dbLoader.queryConnector()).thenReturn(connector);
        assertNull(ExternalDbLoader.getConnectionTask());

        assertTrue(model.tryConnectWithDatabase());
        assertNotNull(ExternalDbLoader.getConnectionTask());
        verify(dbLoader, never()).clearDatabase();
        verify(dbLoader, never()).clearUserDatabase();
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

        model.closeConnection();

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

        model.closeConnection();

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

        model.closeConnection();

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

        model.closeConnection();

        verify(client).stopClient();
        verify(client).sendMessage("Termination");
        verify(task).cancel(true);
        assertNull(ExternalDbLoader.getConnectionTask());
    }

}