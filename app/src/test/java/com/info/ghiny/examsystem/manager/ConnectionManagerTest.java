package com.info.ghiny.examsystem.manager;

import com.info.ghiny.examsystem.MainLoginActivity;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.interfacer.ScannerView;
import com.info.ghiny.examsystem.model.IconManager;
import com.info.ghiny.examsystem.model.LoginHelper;
import com.info.ghiny.examsystem.model.ProcessException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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
    private ScannerView genView;
    private LoginHelper loginModel;
    private ProcessException err;
    private CheckListLoader dbLoader;

    @Before
    public void setUp() throws Exception {
        err = new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, IconManager.WARNING);
        genView     = Mockito.mock(ScannerView.class);
        loginModel  = Mockito.mock(LoginHelper.class);
        dbLoader    = Mockito.mock(CheckListLoader.class);
        manager     = new ConnectionManager(genView, dbLoader);
        manager.setLoginModel(loginModel);
    }

    //= OnScanChief() ==============================================================================
    /**
     * onScanChief(String scanStr)
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
    public void testOnScanForChief_withPositiveResult() throws Exception {
        doNothing().when(loginModel).verifyChief("$CHIEF:...:$");

        manager.onScanForChief("$CHIEF:...:$");
        verify(genView).pauseScanning();
        verify(genView).navigateActivity(MainLoginActivity.class);
        verify(genView, never()).displayError(err);
        verify(genView, never()).resumeScanning();
    }

    @Test
    public void testOnScanForChief_withErrorThrown() throws Exception {

        doThrow(err).when(loginModel).verifyChief("");

        manager.onScanForChief("");
        verify(genView).pauseScanning();
        verify(genView, never()).navigateActivity(MainLoginActivity.class);
        verify(genView).displayError(err);
        verify(genView).resumeScanning();
    }

    @Test
    public void testSetUpConnection_DbHaveConnectionEntry() throws Exception {
        when(loginModel.tryConnection(dbLoader)).thenReturn(true);
        manager.setupConnection();
        verify(genView).navigateActivity(MainLoginActivity.class);
    }

    @Test
    public void testSetUpConnection_DbNoConnectionEntry() throws Exception {
        when(loginModel.tryConnection(dbLoader)).thenReturn(false);
        manager.setupConnection();
        verify(genView, never()).navigateActivity(MainLoginActivity.class);
    }
}