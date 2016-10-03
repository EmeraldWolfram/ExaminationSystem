package com.info.ghiny.examsystem.manager;

import com.info.ghiny.examsystem.MainLoginActivity;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.interfacer.TaskScanView;
import com.info.ghiny.examsystem.model.IconManager;
import com.info.ghiny.examsystem.model.LoginHelper;
import com.info.ghiny.examsystem.model.ProcessException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by GhinY on 11/08/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class ConnectionManagerTest {

    private ConnectionManager manager;
    private TaskScanView genView;
    private LoginHelper loginModel;
    private ProcessException err;
    private CheckListLoader dbLoader;

    @Before
    public void setUp() throws Exception {
        err = new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, IconManager.WARNING);
        genView     = Mockito.mock(TaskScanView.class);
        loginModel  = Mockito.mock(LoginHelper.class);
        dbLoader    = Mockito.mock(CheckListLoader.class);
        manager     = new ConnectionManager(genView, dbLoader);
        manager.setLoginModel(loginModel);
    }

    //= OnScan() ==============================================================================
    /**
     * onScan(String scanStr)
     *
     * Pass the QR scanned to Model
     * Control the View to navigate to another View when Chief address correct
     * Response according to the result from the Model
     *
     * 1. First test, Model show that result is positive
     * 2. Second test, Model throw error due to incorrect format
     * @throws Exception
     */
    @Test
    public void testOnScan_withPositiveResult() throws Exception {
        doNothing().when(loginModel).tryConnectWithQR("$CHIEF:...:$", dbLoader);

        manager.onScan("$CHIEF:...:$");

        verify(genView).pauseScanning();
        verify(genView).navigateActivity(MainLoginActivity.class);
        verify(genView, never()).displayError(err);
        verify(genView, never()).resumeScanning();
    }

    @Test
    public void testOnScan_withErrorThrown() throws Exception {
        doThrow(err).when(loginModel).tryConnectWithQR("", dbLoader);

        manager.onScan("");

        verify(genView).pauseScanning();
        verify(genView, never()).navigateActivity(MainLoginActivity.class);
        verify(genView).displayError(err);
        verify(genView).resumeScanning();
    }

    //= OnCreate() ==========================================================================

    /**
     * onCreate()
     *
     * try to setup a connection using the previous connected ip and port
     * 1. When database have valid connector, establish the connection using the connector in db
     * 2. When database have no valid connector, do nothing
     *
     * @throws Exception
     */
    @Test
    public void testOnCreate_DbHaveConnectionEntry() throws Exception {
        when(loginModel.tryConnectWithDatabase(dbLoader)).thenReturn(true);

        manager.onCreate();

        verify(genView).navigateActivity(MainLoginActivity.class);
    }

    @Test
    public void testOnCreate_DbNoConnectionEntry() throws Exception {
        when(loginModel.tryConnectWithDatabase(dbLoader)).thenReturn(false);

        manager.onCreate();

        verify(genView, never()).navigateActivity(MainLoginActivity.class);
    }

    //= OnDestroy() ==========================================================================
    /**
     * onDestroy()
     *
     * call close connection to terminate the connection
     */
    @Test
    public void testOnDestroy() throws Exception {
        doNothing().when(loginModel).closeConnection();

        manager.onDestroy();

        verify(loginModel).closeConnection();
    }

    //= OnPause() ==========================================================================
    /**
     * onPause()
     *
     * call pause to pause the QR scanner
     */
    @Test
    public void testOnPause() throws Exception {
        doNothing().when(genView).pauseScanning();

        manager.onPause();

        verify(genView).pauseScanning();
    }

    //= OnResume() ==========================================================================
    /**
     * onResume()
     *
     * call resume to resume the QR scanner
     */
    @Test
    public void testOnResume() throws Exception {
        doNothing().when(genView).resumeScanning();

        manager.onResume();

        verify(genView).resumeScanning();
    }

}