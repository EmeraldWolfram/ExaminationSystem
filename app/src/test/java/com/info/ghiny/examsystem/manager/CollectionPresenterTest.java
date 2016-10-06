package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;

import com.info.ghiny.examsystem.InfoGrabActivity;
import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.CollectionMVP;
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
public class CollectionPresenterTest {
    private CollectionMVP.Model taskModel;
    private CollectionMVP.View taskView;
    private CollectionPresenter manager;
    private Handler handler;
    private DialogInterface dialog;

    @Before
    public void setUp() throws Exception {
        taskModel   = Mockito.mock(CollectionMVP.Model.class);
        taskView    = Mockito.mock(CollectionMVP.View.class);
        handler     = Mockito.mock(Handler.class);
        dialog      = Mockito.mock(DialogInterface.class);

        manager = new CollectionPresenter(taskView);
        manager.setHandler(handler);
        manager.setTaskModel(taskModel);
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
        verify(taskView).pauseScanning();
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
        verify(taskView).resumeScanning();
    }

    @Test
    public void testOnResume_ConnectionOnResume() throws Exception {
        ErrorManager errManager = Mockito.mock(ErrorManager.class);
        TCPClient tcpClient     = Mockito.mock(TCPClient.class);
        ExternalDbLoader.setTcpClient(tcpClient);

        manager.onResume(errManager);
        verify(tcpClient).setMessageListener(any(TCPClient.OnMessageReceived.class));
        verify(taskView).resumeScanning();
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

        verify(taskView).securityPrompt(false);
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
        verify(taskView).closeProgressWindow();
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
        doNothing().when(taskModel).bundleCollection("PAPER ABCD");

        manager.onScan("PAPER ABCD");

        verify(taskView).pauseScanning();
        verify(taskView).openProgressWindow();
        verify(handler).postDelayed(any(Runnable.class), anyInt());

        verify(taskView, never()).displayError(any(ProcessException.class));
        verify(taskView, never()).resumeScanning();
    }

    @Test
    public void testOnScanForCollection_withNegativeToast() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, 1);

        doThrow(err).when(taskModel).bundleCollection("PAPER ABCD");

        manager.onScan("PAPER ABCD");

        verify(taskView).pauseScanning();
        verify(taskView, never()).openProgressWindow();
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());

        verify(taskView).displayError(err);
        verify(taskView).resumeScanning();
    }

    @Test
    public void testOnScanForCollection_withNegativeDialog() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_DIALOG, 1);
        doThrow(err).when(taskModel).bundleCollection("PAPER ABCD");
        assertNull(err.getListener(ProcessException.okayButton));

        manager.onScan("PAPER ABCD");

        verify(taskView).pauseScanning();
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(taskView, never()).openProgressWindow();

        verify(taskView).displayError(err);
        verify(taskView, never()).resumeScanning();
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

        verify(taskView).pauseScanning();
        verify(taskView).resumeScanning();
        verify(taskView, never()).displayError(any(ProcessException.class));
        verify(taskView, never()).securityPrompt(false);
    }

    @Test
    public void testOnPasswordReceived_2() throws Exception {
        StaffIdentity staffIdentity = new StaffIdentity();
        staffIdentity.setPassword("CORRECT");
        LoginHelper.setStaff(staffIdentity);

        Intent popUpLoginAct = Mockito.mock(Intent.class);
        when(popUpLoginAct.getStringExtra("Password")).thenReturn("INCORRECT");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, popUpLoginAct);

        verify(taskView).pauseScanning();
        verify(taskView, never()).resumeScanning();
        verify(taskView).displayError(any(ProcessException.class));
        verify(taskView).securityPrompt(false);
    }

    //= OnSwipeBottom() ============================================================================
    /**
     * onSwipeBottom()
     *
     * This method will be called when the CollectionActivity was swiped from top to bottom
     * this method should start the InfoGrabActivity
     */
    @Test
    public void testOnSwipeBottom() throws Exception {
        manager.onSwipeBottom();
        verify(taskView).navigateActivity(InfoGrabActivity.class);
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
        CollectionPresenter manager   = new CollectionPresenter(null);

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