package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.info.ghiny.examsystem.TakeAttdActivity;
import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.Role;
import com.info.ghiny.examsystem.interfacer.LoginMVP;
import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.JavaHost;
import com.info.ghiny.examsystem.model.ProcessException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
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
public class LoginPresenterTest {
    private LoginPresenter manager;
    private LoginMVP.MvpModel taskModel;
    private LoginMVP.MvpView taskView;
    private Handler handler;
    private Intent password;
    private DialogInterface dialog;
    private SharedPreferences preferences;

    @Before
    public void setUp() throws Exception {
        taskView    = Mockito.mock(LoginMVP.MvpView.class);
        taskModel   = Mockito.mock(LoginMVP.MvpModel.class);
        handler     = Mockito.mock(Handler.class);
        password    = Mockito.mock(Intent.class);
        dialog      = Mockito.mock(DialogInterface.class);
        preferences = Mockito.mock(SharedPreferences.class);

        manager     = new LoginPresenter(taskView, preferences);
        manager.setTaskModel(taskModel);
        manager.setHandler(handler);
    }

    //= OnScan() ========================================================================
    /**
     * onScan()
     *
     * 1. prompt user to key-in password if MvpModel didn't throw any error
     * 2. display the error and resume scanning if MvpModel throw Toast error
     * 3. display dialog error but no resume scanning if MvpModel throw Dialog error
     *
     * @throws Exception
     */
    @Test
    public void testOnScanForIdentity_validId() throws Exception {
        doNothing().when(taskModel).checkQrId("012345");

        manager.onScan("012345");

        verify(taskView).securityPrompt(true);
        verify(taskView, never()).displayError(any(ProcessException.class));
        verify(taskView, never()).resumeScanning();
    }

    @Test
    public void testOnScanForIdentity_InvalidId() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, 1);
        doThrow(err).when(taskModel).checkQrId("xyz");

        manager.onScan("xyz");

        verify(taskView, never()).securityPrompt(true);
        verify(taskView).displayError(err);
        verify(taskView).resumeScanning();
    }

    @Test
    public void testOnScanForIdentity_SomeDialogError() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_DIALOG, 1);
        doThrow(err).when(taskModel).checkQrId("xyz");

        manager.onScan("xyz");

        verify(taskView, never()).securityPrompt(true);
        verify(taskView).displayError(err);
        verify(taskView, never()).resumeScanning();
    }

    //= OnPasswordReceived() ========================================================================

    /**
     * onPasswordReceived()
     *
     * 1. MvpView notify user input password, pause the Scanning and request MvpModel to verify
     * 2. MvpView notify user input password, model complain with Toast, pause the scanning
     *    display the error and resume the scanning
     * 3. MvpView notify user input password, model complain with Fatal, pause the scanning
     *    display the error, did not resume
     * 4. Do nothing when the result is RESULT_CANCELLED
     * @throws Exception
     */
    @Test
    public void testOnPasswordReceived_ModelDidNotComplain() throws Exception {
        when(password.getStringExtra("Password")).thenReturn("123456");
        doNothing().when(taskModel).matchStaffPw("123456");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, password);
        verify(handler).postDelayed(any(Runnable.class), anyInt());
        verify(taskView).pauseScanning();
        verify(taskView).openProgressWindow("Verifying:", "Waiting for Chief Respond...");
        verify(taskView, never()).displayError(any(ProcessException.class));
        verify(taskView, never()).resumeScanning();
    }

    @Test
    public void testOnPasswordReceived_ModelComplainWithToast() throws Exception {
        when(password.getStringExtra("Password")).thenReturn("");
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, 1);
        doThrow(err).when(taskModel).matchStaffPw("");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, password);

        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(taskView, never()).openProgressWindow(anyString(), anyString());
        verify(taskView).pauseScanning();
        verify(taskView).displayError(err);
        verify(taskView).resumeScanning();
    }

    @Test
    public void testOnPasswordReceived_ModelComplainWithFatal() throws Exception {
        when(password.getStringExtra("Password")).thenReturn("");
        ProcessException err = new ProcessException("ERROR", ProcessException.FATAL_MESSAGE, 1);
        doThrow(err).when(taskModel).matchStaffPw("");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, password);

        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(taskView, never()).openProgressWindow(anyString(), anyString());
        verify(taskView).pauseScanning();
        verify(taskView).displayError(err);
        verify(taskView, never()).resumeScanning();
    }

    @Test
    public void testOnPasswordReceived_ResultCancelled() throws Exception {
        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_CANCELED, password);

        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(taskView, never()).openProgressWindow(anyString(), anyString());
        verify(taskView, never()).pauseScanning();
        verify(taskView, never()).displayError(any(ProcessException.class));
        verify(taskView, never()).resumeScanning();
    }
    //= OnPause() ==================================================================================

    /**
     * onPause()
     *
     * control the MvpView to stop scanning when called
     *
     * @throws Exception
     */
    @Test
    public void testOnPause() throws Exception {
        manager.onPause();
        verify(taskView).pauseScanning();
    }

    //= OnResume() =================================================================================

    /**
     * onResume()
     *
     * setListener to ExternalDbLoader.ConnectionTask
     * control MvpView to resume scanning
     *
     * @throws Exception
     */
    @Test
    public void testOnResume() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);
        JavaHost javaHost = Mockito.mock(JavaHost.class);

        ExternalDbLoader.setJavaHost(javaHost);
        ExternalDbLoader.setConnectionTask(new ConnectionTask());
        when(preferences.getString(anyString(), anyString())).thenReturn("4");

        manager.onResume(errorManager);

        verify(javaHost).setMessageListener(any(JavaHost.OnMessageReceived.class));
        verify(taskView).resumeScanning();
    }

    //= OnChiefRespond() ================================================================

    /**
     * onChiefRespond()
     *
     * 1. control MvpView to navigate to another MvpView when the message is positive (Invigilator)
     * 2. control MvpView to display error and resume the scanning when the message is negative
     * 3. control MvpView to display dialog and resume scanning after dialog ended when message
     *    is negative
     * 4. control MvpView to navigate to another MvpView when the message is positive (In-Charge)
     *
     * @throws Exception
     */
    @Test
    public void testOnChiefRespond_withPositiveResult() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);
        ExternalDbLoader.setConnectionTask(new ConnectionTask());

        when(taskModel.checkLoginResult("Message")).thenReturn(Role.INVIGILATOR);

        manager.onChiefRespond(errorManager, "Message");

        verify(taskView).navToHome(true, true, true, false);
    }

    @Test
    public void testOnChiefRespond_withToastError() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);
        ConnectionTask connectionTask = Mockito.mock(ConnectionTask.class);
        ExternalDbLoader.setConnectionTask(connectionTask);
        doThrow(new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, 1))
                .when(taskModel).checkLoginResult("Message");

        assertFalse(manager.isDlFlag());
        manager.onChiefRespond(errorManager, "Message");
        assertFalse(manager.isDlFlag());


        verify(taskView, never()).navigateActivity(TakeAttdActivity.class);
        verify(connectionTask).publishError(any(ErrorManager.class), any(ProcessException.class));
        verify(taskView, never()).resumeScanning();
    }

    @Test
    public void testOnChiefRespond_withFatalError() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);
        ConnectionTask connectionTask = Mockito.mock(ConnectionTask.class);
        ExternalDbLoader.setConnectionTask(connectionTask);

        ProcessException err = new ProcessException("ERROR", ProcessException.FATAL_MESSAGE, 1);
        doThrow(err).when(taskModel).checkLoginResult("Message");

        assertFalse(manager.isDlFlag());
        manager.onChiefRespond(errorManager, "Message");
        assertFalse(manager.isDlFlag());

        verify(taskView, never()).navigateActivity(TakeAttdActivity.class);
        verify(connectionTask).publishError(any(ErrorManager.class), any(ProcessException.class));
        verify(taskView, never()).resumeScanning();
    }

    @Test
    public void testOnChiefRespond4_withInCharge() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);
        ExternalDbLoader.setConnectionTask(new ConnectionTask());

        when(taskModel.checkLoginResult("Message")).thenReturn(Role.IN_CHARGE);

        manager.onChiefRespond(errorManager, "Message");

        verify(taskView).navToHome(true, true, true, true);
    }

    //= OnClick(...) ===============================================================================
    /**
     * onClick(...)
     *
     * Whenever a message window pop out, the camera scanner at the back will be paused
     * Test if the scanner is resumed, when button is clicked
     */
    @Test
    public void testOnClickNeutralButton() throws Exception {
        manager.onClick(dialog, DialogInterface.BUTTON_NEUTRAL);

        verify(dialog).cancel();
        verify(taskView).resumeScanning();
    }

    @Test
    public void testOnClickNegativeButton() throws Exception {
        manager.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);

        verify(dialog).cancel();
        verify(taskView).resumeScanning();
    }

    @Test
    public void testOnClickPositiveButton() throws Exception {
        manager.onClick(dialog, DialogInterface.BUTTON_POSITIVE);

        verify(dialog).cancel();
        verify(taskView).resumeScanning();
    }

    //= OnCancel(...) ==============================================================================
    /**
     * onCancel(...)
     *
     * Sometimes, a pop out window could be cancelled by pressing the back button
     * of the phone
     *
     * Test if the scanner is resumed when the back button was pressed
     */
    @Test
    public void testOnCancel() throws Exception {
        manager.onCancel(dialog);

        verify(dialog).cancel();
        verify(taskView).resumeScanning();
    }


    //= OnTimesOut(...) ============================================================================
    /**
     * onTimesOut(...)
     *
     * When a message was sent to the chief to query something,
     * a progress window will pop out and a timer will be started.
     * If there is no respond from the chief for 5 second, onTimesOut(...)
     * will be called.
     *
     * 1. When taskView is null, do nothing
     * 2. When taskView is not null, close the progress window and display the error
     *
     */
    @Test
    public void testOnTimesOutWithNullView() throws Exception {
        ProcessException err = new ProcessException(ProcessException.MESSAGE_TOAST);
        LoginPresenter manager   = new LoginPresenter(null, null);

        manager.onTimesOut(err);

        verify(taskView, never()).closeProgressWindow();
        verify(taskView, never()).pauseScanning();
        verify(taskView, never()).displayError(err);
    }

    @Test
    public void testOnTimesOutWithView() throws Exception {
        ProcessException err = new ProcessException(ProcessException.MESSAGE_TOAST);

        manager.onTimesOut(err);

        verify(taskView).closeProgressWindow();
        verify(taskView).pauseScanning();
        verify(taskView).displayError(err);
    }
}