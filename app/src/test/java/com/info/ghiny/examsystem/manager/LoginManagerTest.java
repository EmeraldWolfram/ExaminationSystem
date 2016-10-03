package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;

import com.info.ghiny.examsystem.TakeAttendanceActivity;
import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.TaskConnView;
import com.info.ghiny.examsystem.interfacer.TaskScanView;
import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.LoginHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by GhinY on 12/08/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class LoginManagerTest {
    private LoginManager manager;
    private LoginHelper loginModel;
    private TaskScanView taskScanView;
    private TaskConnView taskConnView;
    private Handler handler;
    private Intent password;

    @Before
    public void setUp() throws Exception {
        taskScanView = Mockito.mock(TaskScanView.class);
        taskConnView = Mockito.mock(TaskConnView.class);
        password    = Mockito.mock(Intent.class);
        loginModel  = Mockito.mock(LoginHelper.class);
        handler     = Mockito.mock(Handler.class);
        manager     = new LoginManager(taskScanView, taskConnView);
        manager.setLoginModel(loginModel);
        manager.setHandler(handler);
    }

    //= OnScanForIdentity() ========================================================================
    /**
     * onScanForIdentity()
     *
     * 1. prompt user to key-in password if Model didn't throw any error
     * 2. display the error and resume scanning if Model throw Toast error
     * 3. display dialog error but no resume scanning if Model throw Dialog error
     *
     * @throws Exception
     */
    @Test
    public void testOnScanForIdentity_validId() throws Exception {
        doNothing().when(loginModel).checkQrId("0123456");

        manager.onScan("012345");

        verify(taskScanView).securityPrompt(true);
        verify(taskScanView, never()).displayError(any(ProcessException.class));
        verify(taskScanView, never()).resumeScanning();
    }

    @Test
    public void testOnScanForIdentity_InvalidId() throws Exception {
        doThrow(new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, 1))
                .when(loginModel).checkQrId("xyz");

        manager.onScan("xyz");

        verify(taskScanView, never()).securityPrompt(true);
        verify(taskScanView).displayError(any(ProcessException.class));
        verify(taskScanView).resumeScanning();
    }

    @Test
    public void testOnScanForIdentity_SomeDialogError() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_DIALOG, 1);
        doThrow(err).when(loginModel).checkQrId("xyz");

        assertNull(err.getListener(ProcessException.okayButton));
        manager.onScan("xyz");

        verify(taskScanView, never()).securityPrompt(true);
        verify(taskScanView).displayError(any(ProcessException.class));
        verify(taskScanView, never()).resumeScanning();
        assertNotNull(err.getListener(ProcessException.okayButton));
    }

    //= OnPasswordReceived() ========================================================================

    /**
     * onPasswordReceived()
     *
     * 1. View notify user input password, pause the Scanning and request Model to verify
     * 2. View notify user input password, model complain with Toast, pause the scanning
     *    display the error and resume the scanning
     * 3. View notify user input password, model complain with Dialog, pause the scanning
     *    display the error, did not resume but set listener that will resume onClick
     * @throws Exception
     */
    @Test
    public void testOnPasswordReceived_ModelDidNotComplain() throws Exception {
        when(password.getStringExtra("Password")).thenReturn("123456");
        doNothing().when(loginModel).matchStaffPw("123456");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, password);
        verify(handler).postDelayed(any(Runnable.class), anyInt());
        verify(taskScanView).pauseScanning();
        verify(taskScanView, never()).displayError(any(ProcessException.class));
        verify(taskScanView, never()).resumeScanning();
    }

    @Test
    public void testOnPasswordReceived_ModelComplainWithToast() throws Exception {
        when(password.getStringExtra("Password")).thenReturn("");
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, 1);
        doThrow(err).when(loginModel).matchStaffPw("");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, password);

        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(taskScanView).pauseScanning();
        verify(taskScanView).displayError(err);
        verify(taskScanView).resumeScanning();
    }

    @Test
    public void testOnPasswordReceived_ModelComplainWithDialog() throws Exception {
        when(password.getStringExtra("Password")).thenReturn("");
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_DIALOG, 1);
        doThrow(err).when(loginModel).matchStaffPw("");

        assertNull(err.getListener(ProcessException.okayButton));

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, password);

        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(taskScanView).pauseScanning();
        verify(taskScanView).displayError(err);
        verify(taskScanView, never()).resumeScanning();
        assertNotNull(err.getListener(ProcessException.okayButton));
    }

    //= OnPause() ==================================================================================

    /**
     * onPause()
     *
     * control the View to stop scanning when called
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
     * setListener to ExternalDbLoader.ConnectionTask
     * control View to resume scanning
     *
     * @throws Exception
     */
    @Test
    public void testOnResume() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);
        TCPClient tcpClient = Mockito.mock(TCPClient.class);

        ExternalDbLoader.setTcpClient(tcpClient);
        ExternalDbLoader.setConnectionTask(new ConnectionTask());

        manager.onResume(errorManager);

        verify(tcpClient).setMessageListener(any(TCPClient.OnMessageReceived.class));
        verify(taskScanView).resumeScanning();
    }

    //= OnChiefRespond() ================================================================

    /**
     * onChiefRespond()
     *
     * 1. control View to navigate to another View when the message is positive
     * 2. control View to display error and resume the scanning when the message is negative
     * 3. control View to display dialog and resume scanning after dialog ended when message
     *    is negative
     *
     * @throws Exception
     */
    @Test
    public void testOnChiefRespond_withPositiveResult() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);
        ExternalDbLoader.setConnectionTask(new ConnectionTask());

        doNothing().when(loginModel).checkLoginResult("Message");
        doNothing().when(loginModel).checkDetail(anyString());

        //assertFalse(manager.isDlFlag());

        manager.onChiefRespond(errorManager, "Message");
        //assertTrue(manager.isDlFlag());
        //verify(taskScanView, never()).navigateActivity(TakeAttendanceActivity.class);

        //manager.onChiefRespond(errorManager, "Message");
        //assertFalse(manager.isDlFlag());


        verify(taskScanView).navigateActivity(TakeAttendanceActivity.class);
        verify(taskScanView, never()).displayError(any(ProcessException.class));
        verify(taskScanView, never()).resumeScanning();
    }

    @Test
    public void testOnChiefRespond_withToastError() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);
        ConnectionTask connectionTask = Mockito.mock(ConnectionTask.class);
        ExternalDbLoader.setConnectionTask(connectionTask);
        doThrow(new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, 1))
                .when(loginModel).checkLoginResult("Message");

        assertFalse(manager.isDlFlag());
        manager.onChiefRespond(errorManager, "Message");
        assertFalse(manager.isDlFlag());


        verify(taskScanView, never()).navigateActivity(TakeAttendanceActivity.class);
        verify(connectionTask).publishError(any(ErrorManager.class), any(ProcessException.class));
        verify(taskScanView, never()).resumeScanning();
    }

    @Test
    public void testOnChiefRespond_withDialogError() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);
        ConnectionTask connectionTask = Mockito.mock(ConnectionTask.class);
        ExternalDbLoader.setConnectionTask(connectionTask);

        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_DIALOG, 1);
        doThrow(err).when(loginModel).checkLoginResult("Message");

        assertNull(err.getListener(ProcessException.okayButton));

        assertFalse(manager.isDlFlag());
        manager.onChiefRespond(errorManager, "Message");
        assertFalse(manager.isDlFlag());

        verify(taskScanView, never()).navigateActivity(TakeAttendanceActivity.class);
        verify(connectionTask).publishError(any(ErrorManager.class), any(ProcessException.class));
        verify(taskScanView, never()).resumeScanning();
        assertNotNull(err.getListener(ProcessException.okayButton));
    }
}