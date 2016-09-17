package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.TaskConnView;
import com.info.ghiny.examsystem.interfacer.TaskScanView;
import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.InfoCollectHelper;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.LoginHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by GhinY on 17/08/2016.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class CollectManagerTest {
    private Handler handler;
    private InfoCollectHelper infoModel;
    private TaskScanView taskScanView;
    private TaskConnView taskConnView;
    private CollectManager manager;

    @Before
    public void setUp() throws Exception {
        infoModel = Mockito.mock(InfoCollectHelper.class);
        taskScanView = Mockito.mock(TaskScanView.class);
        taskConnView = Mockito.mock(TaskConnView.class);
        handler = Mockito.mock(Handler.class);

        manager = new CollectManager(taskScanView, taskConnView);
        manager.setHandler(handler);
        manager.setInfoModel(infoModel);
    }

    //= OnPause() ==================================================================================

    /**
     * onPause()
     *
     * verify if scanner.pause is called
     *
     * @throws Exception
     */
    @Test
    public void testOnPause() throws Exception {
        manager.onPause();
        verify(taskScanView).pauseScanning();
    }

    //= OnResume() =================================================================================

    /**
     * onResume()
     *
     * verify if scanner.resume is called
     *
     * @throws Exception
     */
    @Test
    public void testOnResume_ScannerOnResume() throws Exception {
        TCPClient tcpClient     = Mockito.mock(TCPClient.class);
        ExternalDbLoader.setTcpClient(tcpClient);

        manager.onResume();
        verify(tcpClient, never()).setMessageListener(any(TCPClient.OnMessageReceived.class));
        verify(taskScanView).resumeScanning();
    }

    @Test
    public void testOnResume_ConnectionOnResume() throws Exception {
        ErrorManager errManager = Mockito.mock(ErrorManager.class);
        TCPClient tcpClient     = Mockito.mock(TCPClient.class);
        ExternalDbLoader.setTcpClient(tcpClient);

        manager.onResume(errManager);
        verify(tcpClient).setMessageListener(any(TCPClient.OnMessageReceived.class));
        verify(taskScanView).resumeScanning();
    }

    //= OnRestart() ================================================================================
    /**
     * onRestart()
     *
     * verify if security prompt was called when the app go through restart
     *
     */
    @Test
    public void testOnRestart() throws Exception {
        manager.onRestart();

        verify(taskScanView).securityPrompt(false);
    }

    //= OnDestroy() ================================================================================

    /**
     * onDestroy()
     *
     * verify if timer is stopped after destroy
     *
     * @throws Exception
     */
    @Test
    public void testOnDestroy() throws Exception {
        manager.onDestroy();
        verify(taskConnView).closeProgressWindow();
        verify(handler).removeCallbacks(any(Runnable.class));
    }

    //= OnScanForCollection() ======================================================================

    /**
     * onScan()
     *
     * 1. The scanner was paused and not resumed if nothing wrong with the scan string
     * 2. The scanner was paused and resumed after display toast error thrown by Model
     * 3. The scanner was paused and resumed after display dialog error thrown by Model
     *
     * @throws Exception
     */
    @Test
    public void testOnScanForCollection_withPositiveResult() throws Exception {
        doNothing().when(infoModel).bundleCollection("PAPER ABCD");

        manager.onScan("PAPER ABCD");

        verify(taskScanView).pauseScanning();
        verify(taskConnView).openProgressWindow();
        verify(handler).postDelayed(any(Runnable.class), anyInt());

        verify(taskScanView, never()).displayError(any(ProcessException.class));
        verify(taskScanView, never()).resumeScanning();
    }

    @Test
    public void testOnScanForCollection_withNegativeToast() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, 1);

        doThrow(err).when(infoModel).bundleCollection("PAPER ABCD");

        manager.onScan("PAPER ABCD");

        verify(taskScanView).pauseScanning();
        verify(taskConnView, never()).openProgressWindow();
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());

        verify(taskScanView).displayError(err);
        verify(taskScanView).resumeScanning();
    }

    @Test
    public void testOnScanForCollection_withNegativeDialog() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_DIALOG, 1);
        doThrow(err).when(infoModel).bundleCollection("PAPER ABCD");
        assertNull(err.getListener(ProcessException.okayButton));

        manager.onScan("PAPER ABCD");

        verify(taskScanView).pauseScanning();
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(taskConnView, never()).openProgressWindow();

        verify(taskScanView).displayError(err);
        verify(taskScanView, never()).resumeScanning();
        assertNotNull(err.getListener(ProcessException.okayButton));
    }

    //= OnChiefRespond() ===========================================================================
    /**
     * onChiefRespond()
     *
     * 1. When message is positive, nothing happen
     * 2. When message is negative, display the error
     */
    @Test
    public void testOnChiefRespond_1() throws Exception {
        ErrorManager errManager = Mockito.mock(ErrorManager.class);
        ConnectionTask.setCompleteFlag(false);
        ConnectionTask conTask = Mockito.mock(ConnectionTask.class);
        ExternalDbLoader.setConnectionTask(conTask);
        String message = "{\"Result\":true}";

        assertFalse(ConnectionTask.isComplete());
        manager.onChiefRespond(errManager, message);

        verify(taskConnView).closeProgressWindow();
        assertTrue(ConnectionTask.isComplete());
        verify(conTask, never()).publishError(any(ErrorManager.class), any(ProcessException.class));
    }

    @Test
    public void testOnChiefRespond_2() throws Exception {
        ErrorManager errManager = Mockito.mock(ErrorManager.class);
        ConnectionTask.setCompleteFlag(false);
        ConnectionTask conTask = Mockito.mock(ConnectionTask.class);
        ExternalDbLoader.setConnectionTask(conTask);
        String message = "{\"Result\":false}";

        assertFalse(ConnectionTask.isComplete());
        manager.onChiefRespond(errManager, message);

        verify(taskConnView).closeProgressWindow();
        assertTrue(ConnectionTask.isComplete());
        verify(conTask).publishError(any(ErrorManager.class), any(ProcessException.class));
    }

    //= OnPasswordReceived() ========================================================================
    /**
     * onPasswordReceived()
     *
     * 1. Password receive is correct, resume the scanner
     * 2. Password receive is incorrect, display the error and call security prompt again
     *
     */
    @Test
    public void testOnPasswordReceived_1() throws Exception {
        StaffIdentity staffIdentity = new StaffIdentity();
        staffIdentity.setPassword("CORRECT");
        LoginHelper.setStaff(staffIdentity);

        Intent popUpLoginAct = Mockito.mock(Intent.class);
        when(popUpLoginAct.getStringExtra("Password")).thenReturn("CORRECT");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, popUpLoginAct);

        verify(taskScanView).pauseScanning();
        verify(taskScanView).resumeScanning();
        verify(taskScanView, never()).displayError(any(ProcessException.class));
        verify(taskScanView, never()).securityPrompt(false);
    }

    @Test
    public void testOnPasswordReceived_2() throws Exception {
        StaffIdentity staffIdentity = new StaffIdentity();
        staffIdentity.setPassword("CORRECT");
        LoginHelper.setStaff(staffIdentity);

        Intent popUpLoginAct = Mockito.mock(Intent.class);
        when(popUpLoginAct.getStringExtra("Password")).thenReturn("INCORRECT");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, popUpLoginAct);

        verify(taskScanView).pauseScanning();
        verify(taskScanView, never()).resumeScanning();
        verify(taskScanView).displayError(any(ProcessException.class));
        verify(taskScanView).securityPrompt(false);
    }
}