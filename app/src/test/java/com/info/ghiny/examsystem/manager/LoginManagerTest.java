package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

import com.info.ghiny.examsystem.AssignInfoActivity;
import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.ExternalDbLoaderTest;
import com.info.ghiny.examsystem.interfacer.ScannerView;
import com.info.ghiny.examsystem.model.ChiefLink;
import com.info.ghiny.examsystem.model.LoginHelper;
import com.info.ghiny.examsystem.model.ProcessException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
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
    private ScannerView scannerView;
    private Handler handler;
    private Intent password;

    @Before
    public void setUp() throws Exception {
        scannerView = Mockito.mock(ScannerView.class);
        password    = Mockito.mock(Intent.class);
        loginModel  = Mockito.mock(LoginHelper.class);
        handler     = Mockito.mock(Handler.class);
        manager     = new LoginManager(scannerView);
        manager.setLoginModel(loginModel);
        manager.setHandler(handler);
    }

    //= OnScanForIdentity() ========================================================================
    /**
     * onScanForIdentity()
     *
     * 1. prompt user to key-in password if Model didn't throw any error
     * 2. display the error and resume scanning if Model throw any error
     *
     * @throws Exception
     */
    @Test
    public void testOnScanForIdentity_validId() throws Exception {
        doNothing().when(loginModel).checkQrId("0123456");

        manager.onScanForIdentity("012345");

        verify(scannerView).securityPrompt();
        verify(scannerView, never()).displayError(any(ProcessException.class));
        verify(scannerView, never()).resumeScanning();
    }

    @Test
    public void testOnScanForIdentity_InvalidId() throws Exception {
        doThrow(new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, 1))
                .when(loginModel).checkQrId("xyz");

        manager.onScanForIdentity("xyz");

        verify(scannerView, never()).securityPrompt();
        verify(scannerView).displayError(any(ProcessException.class));
        verify(scannerView).resumeScanning();
    }

    //= OnReceivePassword() ========================================================================

    /**
     * onReceivePassword()
     *
     * 1. View notify user input password, pause the Scanning and request Model to verify
     * 2. View notify user input password, model complain with Exception, pause the scanning
     *    display the error and resume the scanning
     *
     * @throws Exception
     */
    @Test
    public void testOnReceivePassword_ModelDidNotComplain() throws Exception {
        when(password.getStringExtra("Password")).thenReturn("123456");
        doNothing().when(loginModel).matchStaffPw("123456");

        manager.onReceivePassword(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, password);
        verify(handler).postDelayed(any(Runnable.class), anyInt());
        verify(scannerView).pauseScanning();
        verify(scannerView, never()).displayError(any(ProcessException.class));
        verify(scannerView, never()).resumeScanning();
    }

    @Test
    public void testOnReceivePassword_ModelComplain() throws Exception {
        when(password.getStringExtra("Password")).thenReturn("");
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, 1);
        doThrow(err).when(loginModel).matchStaffPw("");

        manager.onReceivePassword(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, password);
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(scannerView).pauseScanning();
        verify(scannerView).displayError(err);
        verify(scannerView).resumeScanning();
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
        verify(scannerView).pauseScanning();
    }

    //= OnResume() =================================================================================

    /**
     * onResume()
     *
     * setListener to ExternalDbLoader.ChiefLink
     * control View to resume scanning
     *
     * @throws Exception
     */
    @Test
    public void testOnResume() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);
        ExternalDbLoader.setChiefLink(new ChiefLink());

        manager.onResume(errorManager);

        verify(scannerView).resumeScanning();
    }

    //= OnMessageReceiveFromChief() ================================================================

    /**
     * onMessageReceiveFromChief()
     *
     * 1. control View to navigate to another View when the message is positive
     * 2. control View to display error and resume the scanning when the message is negative
     *
     * @throws Exception
     */
    @Test
    public void testOnMessageReceiveFromChief_withPositiveResult() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);
        ExternalDbLoader.setChiefLink(new ChiefLink());
        doNothing().when(loginModel).checkLoginResult("Message");

        manager.onMessageReceiveFromChief(errorManager, "Message");

        verify(scannerView).navigateActivity(AssignInfoActivity.class);
        verify(scannerView, never()).displayError(any(ProcessException.class));
        verify(scannerView, never()).resumeScanning();
    }

    @Test
    public void testOnMessageReceiveFromChief_withError() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);
        ChiefLink chiefLink       = Mockito.mock(ChiefLink.class);
        ExternalDbLoader.setChiefLink(chiefLink);
        doThrow(new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, 1))
                .when(loginModel).checkLoginResult("Message");

        manager.onMessageReceiveFromChief(errorManager, "Message");

        verify(scannerView, never()).navigateActivity(AssignInfoActivity.class);
        verify(chiefLink).publishError(any(ErrorManager.class), any(ProcessException.class));
        verify(scannerView).resumeScanning();
    }
}