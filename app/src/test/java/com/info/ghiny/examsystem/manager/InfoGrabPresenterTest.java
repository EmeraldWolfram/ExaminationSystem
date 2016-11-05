package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.InfoGrabMVP;
import com.info.ghiny.examsystem.model.ConnectionTask;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by GhinY on 12/08/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class InfoGrabPresenterTest {
    private Handler handler;
    private InfoGrabMVP.ViewFace taskView;
    private InfoGrabMVP.Model taskModel;
    private InfoGrabPresenter manager;
    private DialogInterface dialog;
    private SharedPreferences preferences;

    @Before
    public void setUp() throws Exception {
        taskModel   = Mockito.mock(InfoGrabMVP.Model.class);
        taskView    = Mockito.mock(InfoGrabMVP.ViewFace.class);
        handler     = Mockito.mock(Handler.class);
        dialog      = Mockito.mock(DialogInterface.class);
        preferences = Mockito.mock(SharedPreferences.class);

        manager = new InfoGrabPresenter(taskView, preferences);
        manager.setHandler(handler);
        manager.setTaskModel(taskModel);
    }

    //= OnScan() =================================================================

    /**
     * onScan()
     *
     * 1. No complain from Model, pause the scanner, and start the delay
     * 2. Complain from Model, pause the scanner, toast error and resume the scanner
     * 3. Complain from Model, pause the scanner, show error dialog and did not resume
     *
     * @throws Exception
     */
    @Test
    public void testOnScanForCandidateDetail_ModelNotComplaining() throws Exception {
        doNothing().when(taskModel).reqCandidatePapers("15WAU00001");

        manager.onScan("15WAU00001");

        verify(taskView).pauseScanning();
        verify(taskView).openProgressWindow("Server Database Request", "Waiting for Respond...");
        verify(taskModel).reqCandidatePapers("15WAU00001");
        verify(handler).postDelayed(any(Runnable.class), anyInt());
        verify(taskView, never()).displayError(any(ProcessException.class));
        verify(taskView, never()).resumeScanning();
    }

    @Test
    public void testOnScanForCandidateDetail_ModelComplainWithToast() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, 1);
        doThrow(err).when(taskModel).reqCandidatePapers("33");

        manager.onScan("33");

        verify(taskView).pauseScanning();
        verify(taskView, never()).openProgressWindow(anyString(), anyString());
        verify(taskModel).reqCandidatePapers("33");
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(taskView).displayError(err);
        verify(taskView).resumeScanning();
    }

    @Test
    public void testOnScanForCandidateDetail_ModelComplainWithDialog() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_DIALOG, 1);
        doThrow(err).when(taskModel).reqCandidatePapers("33");
        assertNull(err.getListener(ProcessException.okayButton));

        manager.onScan("33");

        verify(taskView).pauseScanning();
        verify(taskView, never()).openProgressWindow(anyString(), anyString());
        verify(taskModel).reqCandidatePapers("33");
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(taskView).displayError(err);
        verify(taskView, never()).resumeScanning();
        assertNotNull(err.getListener(ProcessException.okayButton));
    }

    //= onPause() ==================================================================================

    /**
     * onPause()
     *
     * pause the scanner when called
     *
     * @throws Exception
     */

    @Test
    public void testOnPause() throws Exception {
        manager.onPause();
        verify(taskView).pauseScanning();
    }

    //= onResume() =================================================================================

    /**
     * onResume()
     *
     * set the Listener to the running TCPClient object and resume the scanner
     *
     * @throws Exception
     */
    @Test
    public void testOnResume_ScannerOnResume() throws Exception {
        TCPClient tcpClient     = Mockito.mock(TCPClient.class);
        ExternalDbLoader.setTcpClient(tcpClient);
        when(preferences.getString(anyString(), anyString())).thenReturn("4");

        manager.onResume();

        verify(tcpClient, never()).setMessageListener(any(TCPClient.OnMessageReceived.class));
        verify(taskView).resumeScanning();
    }

    @Test
    public void testOnResume_ConnectionOnResume() throws Exception {
        ErrorManager errManager = Mockito.mock(ErrorManager.class);
        TCPClient tcpClient     = Mockito.mock(TCPClient.class);
        ExternalDbLoader.setTcpClient(tcpClient);
        when(preferences.getString(anyString(), anyString())).thenReturn("4");

        manager.onResume(errManager);

        verify(tcpClient).setMessageListener(any(TCPClient.OnMessageReceived.class));
        verify(taskView).resumeScanning();
    }

    //= onRestart() ================================================================================
    /**
     * onRestart()
     *
     * call security prompt whenever the screen go through onRestart method = (Inactivity)
     *
     */
    @Test
    public void testOnRestart() throws Exception {
        manager.onRestart();
        verify(taskView).securityPrompt(false);
    }

    //= onDestroy() ================================================================================

    /**
     * onDestroy()
     *
     * notify by the View when the View is going to be destroy
     * remove the callback of the timer started after sending the data to chief
     *
     * @throws Exception
     */
    @Test
    public void testOnDestroy() throws Exception {
        manager.onDestroy();

        verify(taskView).closeProgressWindow();
        verify(handler).removeCallbacks(any(Runnable.class));
    }

    //= OnChiefRespond() ===========================================================================
    /**
     * onChiefRespond()
     *
     * 1. When respond is positive, navigate to InfoDisplayActivity
     * 2. When respond is negative, publish the error
     *
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

        verify(taskView).closeProgressWindow();
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

        verify(taskView).closeProgressWindow();
        assertTrue(ConnectionTask.isComplete());
        verify(conTask).publishError(any(ErrorManager.class), any(ProcessException.class));
    }

    //= OnPasswordReceived() =======================================================================
    /**
     * onPasswordReceived()
     *
     * 1. When the password is correct, resume the scanning
     * 2. When the password is incorrect, display the error and call security prompt again
     */
    @Test
    public void testOnPasswordReceived_1() throws Exception {
        Intent popUpLoginAct = Mockito.mock(Intent.class);
        when(popUpLoginAct.getStringExtra("Password")).thenReturn("CORRECT");
        doNothing().when(taskModel).matchPassword("CORRECT");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, popUpLoginAct);

        verify(taskModel).matchPassword("CORRECT");
        verify(taskView).pauseScanning();
        verify(taskView).resumeScanning();
        verify(taskView, never()).displayError(any(ProcessException.class));
        verify(taskView, never()).securityPrompt(false);
    }

    @Test
    public void testOnPasswordReceived_2() throws Exception {
        ProcessException err = new ProcessException(ProcessException.MESSAGE_TOAST);
        Intent popUpLoginAct = Mockito.mock(Intent.class);
        when(popUpLoginAct.getStringExtra("Password")).thenReturn("INCORRECT");
        doThrow(err).when(taskModel).matchPassword("INCORRECT");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, popUpLoginAct);

        verify(taskModel).matchPassword("INCORRECT");
        verify(taskView).pauseScanning();
        verify(taskView, never()).resumeScanning();
        verify(taskView).displayError(err);
        verify(taskView).securityPrompt(false);
    }

    //= OnSwipeTop() ===============================================================================
    /**
     * onSwipeTop()
     *
     * This method will be called when the InfoGrabActivity was swiped too top
     * this method should finish the activity when swiped top
     */
    @Test
    public void testOnSwipeTop() throws Exception {
        manager.onSwipeTop();
        verify(taskView).finishActivity();
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
        InfoGrabPresenter manager   = new InfoGrabPresenter(null, null);

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