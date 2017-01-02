package com.info.ghiny.examsystem.manager;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;

import com.info.ghiny.examsystem.MainLoginActivity;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.LinkChiefMVP;
import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.JavaHost;
import com.info.ghiny.examsystem.model.ProcessException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
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
public class LinkChiefPresenterTest {

    private LinkChiefPresenter manager;
    private LinkChiefMVP.ViewFace genView;
    private LinkChiefMVP.ModelFace genModel;
    private ProcessException err;
    private Handler handler;
    private SharedPreferences preferences;
    private JavaHost javaHost;
    private ConnectionTask task;
    private DialogInterface dialog;

    @Before
    public void setUp() throws Exception {
        err = new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, IconManager.WARNING);
        genView     = Mockito.mock(LinkChiefMVP.ViewFace.class);
        genModel    = Mockito.mock(LinkChiefMVP.ModelFace.class);
        handler     = Mockito.mock(Handler.class);
        javaHost = Mockito.mock(JavaHost.class);
        task        = Mockito.mock(ConnectionTask.class);
        dialog      = Mockito.mock(DialogInterface.class);
        preferences = Mockito.mock(SharedPreferences.class);

        ExternalDbLoader.setJavaHost(javaHost);
        ExternalDbLoader.setConnectionTask(task);

        manager     = new LinkChiefPresenter(genView, preferences);
        manager.setTaskModel(genModel);
        manager.setHandler(handler);
    }

    //= OnScan() ==============================================================================
    /**
     * onScan(String scanStr)
     *
     * Pass the QR scanned to MvpModel
     * Control the MvpView to navigate to another MvpView when Chief address correct
     * Response according to the result from the MvpModel
     *
     * 1. First test, MvpModel show that result is positive
     * 2. Second test, MvpModel throw error due to incorrect format
     * @throws Exception
     */
    @Test
    public void testOnScan_withPositiveResult() throws Exception {
        doNothing().when(genModel).tryConnectWithQR("$CHIEF:...:$");

        manager.onScan("$CHIEF:...:$");

        verify(genView).pauseScanning();
        verify(handler).postDelayed(any(Runnable.class), anyInt());
        verify(genView, never()).displayError(err);
        verify(genView, never()).resumeScanning();
    }

    @Test
    public void testOnScan_withErrorThrown() throws Exception {
        doThrow(err).when(genModel).tryConnectWithQR("");

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
     *    and set the reconnect flag to true
     * 2. When database have no valid connector, do nothing and remain the flag as false
     *
     * @throws Exception
     */
    @Test
    public void testOnCreate_DbHaveConnectionEntry() throws Exception {
        when(genModel.tryConnectWithDatabase()).thenReturn(true);
        assertFalse(manager.isReconnect());

        manager.onCreate();

        assertTrue(manager.isReconnect());
    }

    @Test
    public void testOnCreate_DbNoConnectionEntry() throws Exception {
        when(genModel.tryConnectWithDatabase()).thenReturn(false);
        assertFalse(manager.isReconnect());

        manager.onCreate();

        assertFalse(manager.isReconnect());
    }

    //= OnDestroy() ==========================================================================
    /**
     * onDestroy()
     *
     * call close connection to terminate the connection
     * At the same time, remove the timer if started and close progress window if started
     */
    @Test
    public void testOnDestroy() throws Exception {
        doNothing().when(genModel).closeConnection();

        manager.onDestroy();

        verify(genModel).closeConnection();
        verify(handler).removeCallbacks(genModel);
        verify(genView).closeProgressWindow();
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

    //= OnResume(...) ==========================================================================
    /**
     * onResume(...)
     *
     * Used to setup the MessageListener for the ChiefRespond and request
     * the challenge message from the chief for reconnection purposes
     * and start timer
     * if and only if the connection is reconnect type.
     *
     * At any case, call resume to resume the QR scanner
     *
     * Tests:
     * 1. Reconnection is true, set listener, start timer, request message and resume the scanner
     * 2. Reconnection is false, resume the scanner only
     *
     */
    @Test
    public void testOnResume1_ReconnectUsingDatabase() throws Exception {
        ErrorManager errManager = Mockito.mock(ErrorManager.class);
        when(genModel.reconnect()).thenReturn(true);
        when(preferences.getString(anyString(), anyString())).thenReturn("4");

        manager.setReconnect(true);

        manager.onResume(errManager);

        verify(genView).openProgressWindow(anyString(), anyString());
        verify(handler).postDelayed(genModel, 5000);
        verify(genModel).reconnect();
        verify(genView).resumeScanning();
    }

    @Test
    public void testOnResume2_ConnectThroughQR() throws Exception {
        ErrorManager errManager = Mockito.mock(ErrorManager.class);
        manager.setReconnect(false);
        when(preferences.getString(anyString(), anyString())).thenReturn("4");

        manager.onResume(errManager);

        verify(genView, never()).openProgressWindow(anyString(), anyString());
        verify(handler, never()).postDelayed(genModel, 5000);
        verify(genModel, never()).reconnect();
        verify(genView).resumeScanning();
    }

    //= OnChiefRespond() ================================================================

    /**
     * onChiefRespond()
     *
     * 1. control MvpView to navigate to Login when the message is positive
     * 2. control MvpView to display error and resume the scanning when the message is negative
     * 3. control MvpView to display dialog and resume scanning after dialog ended when message
     *    is negative
     */
    @Test
    public void testOnChiefRespond1_withPositiveResult() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);
        manager.setReconnect(true);

        manager.onChiefRespond(errorManager, "{\"Type\":\"Reconnection\",\"Value\":\"Message\"}");

        verify(genView).closeProgressWindow();
        verify(genModel).onChallengeMessageReceived(anyString());
        verify(genView).navigateActivity(MainLoginActivity.class);
        verify(task, never()).publishError(any(ErrorManager.class), any(ProcessException.class));
        verify(genView, never()).resumeScanning();
    }

    @Test
    public void testOnChiefRespond2_withToastError() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);
        manager.setReconnect(true);
        err = new ProcessException(ProcessException.MESSAGE_TOAST);
        doThrow(err).when(genModel).onChallengeMessageReceived("{\"Type\":\"Reconnection\",\"Value\":\"Message\"}");

        manager.onChiefRespond(errorManager, "{\"Type\":\"Reconnection\",\"Value\":\"Message\"}");

        assertTrue(manager.isReconnect());
        verify(genView).closeProgressWindow();
        verify(genModel).onChallengeMessageReceived("{\"Type\":\"Reconnection\",\"Value\":\"Message\"}");
        verify(genView, never()).navigateActivity(MainLoginActivity.class);
        verify(task).publishError(errorManager, err);
    }

    @Test
    public void testOnChiefRespond3_withFatalError() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);
        manager.setReconnect(true);
        err = new ProcessException(ProcessException.FATAL_MESSAGE);
        doThrow(err).when(genModel).onChallengeMessageReceived("{\"Type\":\"Reconnection\",\"Value\":\"Message\"}");

        manager.onChiefRespond(errorManager, "{\"Type\":\"Reconnection\",\"Value\":\"Message\"}");

        assertTrue(manager.isReconnect());
        verify(genView).closeProgressWindow();
        verify(genModel).onChallengeMessageReceived("{\"Type\":\"Reconnection\",\"Value\":\"Message\"}");
        verify(genView, never()).navigateActivity(MainLoginActivity.class);
        verify(task).publishError(errorManager, err);
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
        verify(genView).resumeScanning();
    }

    @Test
    public void testOnClickNegativeButton() throws Exception {
        manager.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);

        verify(dialog).cancel();
        verify(genView).resumeScanning();
    }

    @Test
    public void testOnClickPositiveButton() throws Exception {
        manager.onClick(dialog, DialogInterface.BUTTON_POSITIVE);

        verify(dialog).cancel();
        verify(genView).resumeScanning();
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
        verify(genView).resumeScanning();
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
        LinkChiefPresenter manager   = new LinkChiefPresenter(null, null);

        manager.onTimesOut(err);

        verify(genView, never()).closeProgressWindow();
        verify(genView, never()).pauseScanning();
        verify(genView, never()).displayError(err);
    }

    @Test
    public void testOnTimesOutWithView() throws Exception {
        ProcessException err = new ProcessException(ProcessException.MESSAGE_TOAST);

        manager.onTimesOut(err);

        verify(genView).closeProgressWindow();
        verify(genView).pauseScanning();
        verify(genView).displayError(err);
    }

}