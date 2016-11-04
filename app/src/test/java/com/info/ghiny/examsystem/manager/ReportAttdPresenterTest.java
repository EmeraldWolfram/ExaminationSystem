package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.ReportAttdMVP;
import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.LoginModel;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

/**
 * Created by GhinY on 12/08/2016.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class ReportAttdPresenterTest {
    private ReportAttdMVP.View taskView;
    private ReportAttdMVP.Model taskModel;
    private Handler handler;
    private ReportAttdPresenter manager;
    private View view;
    private TextView dummyView;
    private CheckBox dummyBox;

    @Before
    public void setUp() throws Exception {
        StaffIdentity id = new StaffIdentity("staff1", true, "AM_Staff", "H3");
        id.setPassword("123456");
        LoginModel.setStaff(id);

        taskView    = Mockito.mock(ReportAttdMVP.View.class);
        taskModel   = Mockito.mock(ReportAttdMVP.Model.class);
        handler     = Mockito.mock(Handler.class);

        manager     = new ReportAttdPresenter(taskView);
        manager.setTaskModel(taskModel);
        manager.setHandler(handler);

        view        = Mockito.mock(View.class);
        dummyView   = Mockito.mock(TextView.class);
        dummyBox    = Mockito.mock(CheckBox.class);
    }

    //= signToUpload() =================================================================================

    /**
     * signToUpload()
     *
     * navigate the View to prompt user for password when called
     *
     */
    @Test
    public void testSignToUpload() throws Exception {
        manager.signToUpload();
        assertTrue(manager.isUploadFlag());
        verify(taskView).securityPrompt(true);
    }

    //= OnPasswordReceived() ========================================================================
    /**
     * onPasswordReceived()
     *
     * 1. Model notify a Positive Result, tell Model to upload attendance and start timer
     * 2. Model notify a Negative Result, display the error thrown by Model
     * 3. Extended: Receive Correct Password, prompt because of inactivity
     * 4. Extended: Receive Wrong Password, prompt because of inactivity
     */
    @Test
    public void testOnReceivePassword_CorrectPassword() throws Exception {
        Intent pw = Mockito.mock(Intent.class);

        when(pw.getStringExtra("Password")).thenReturn("123456");
        doNothing().when(taskView).securityPrompt(true);
        doNothing().when(taskModel).matchPassword("123456");
        manager.signToUpload();

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, pw);

        verify(taskModel).matchPassword("123456");
        verify(taskModel).uploadAttdList();
        verify(taskView).openProgressWindow("Sending:", "Uploading Attendance List...");
        verify(handler).postDelayed(any(Runnable.class), anyInt());
        verify(taskView, never()).displayError(any(ProcessException.class));
    }

    @Test
    public void testOnReceivePassword_IncorrectPassword() throws Exception {
        Intent pw = Mockito.mock(Intent.class);
        ProcessException err = new ProcessException(ProcessException.MESSAGE_TOAST);

        doNothing().when(taskView).securityPrompt(true);
        doThrow(err).when(taskModel).matchPassword("abcdef");
        when(pw.getStringExtra("Password")).thenReturn("abcdef");

        manager.signToUpload();
        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, pw);

        verify(taskModel).matchPassword("abcdef");
        verify(taskModel, never()).uploadAttdList();
        verify(taskView, never()).openProgressWindow(anyString(), anyString());
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(taskView).displayError(err);
    }

    @Test
    public void testOnReceivePassword_CorrectPassword_PromptOnInactiviy() throws Exception {
        Intent pw = Mockito.mock(Intent.class);

        when(pw.getStringExtra("Password")).thenReturn("123456");
        doNothing().when(taskView).securityPrompt(true);
        doNothing().when(taskModel).matchPassword("123456");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, pw);

        verify(taskModel).matchPassword("123456");
        verify(taskModel, never()).uploadAttdList();
        verify(taskView, never()).openProgressWindow(anyString(), anyString());
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(taskView, never()).displayError(any(ProcessException.class));
    }

    @Test
    public void testOnReceivePassword_IncorrectPassword_PromptOnInactivity() throws Exception {
        Intent pw = Mockito.mock(Intent.class);
        ProcessException err = new ProcessException(ProcessException.MESSAGE_TOAST);

        doNothing().when(taskView).securityPrompt(true);
        doThrow(err).when(taskModel).matchPassword("abcdef");
        when(pw.getStringExtra("Password")).thenReturn("abcdef");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, pw);

        verify(taskModel).matchPassword("abcdef");
        verify(taskModel, never()).uploadAttdList();
        verify(taskView, never()).openProgressWindow(anyString(), anyString());
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(taskView).displayError(err);
    }

    //= OnResume() =================================================================================
    /**
     * onResume()
     *
     * Set the listener of the running TCPClient object when called
     */
    @Test
    public void testOnResume() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);
        TCPClient tcpClient = Mockito.mock(TCPClient.class);
        ExternalDbLoader.setTcpClient(tcpClient);

        manager.onResume(errorManager);

        verify(tcpClient).setMessageListener(any(TCPClient.OnMessageReceived.class));
    }

    //= OnRestart() ================================================================================
    /**
     * onRestart()
     *
     * verify if security prompt is called whenever onRestart was called
     */
    @Test
    public void testOnRestart() throws Exception {
        manager.onRestart();

        assertFalse(manager.isUploadFlag());
        verify(taskView).securityPrompt(false);
    }


    //= OnDestroy() ================================================================================
    /**
     * onDestroy()
     *
     * Stop the timer that was started after message sent to Chief if the Activity was destroy
     *
     */
    @Test
    public void testOnDestroy() throws Exception {
        manager.onDestroy();
        verify(taskView).closeProgressWindow();
        verify(handler).removeCallbacks(taskModel);
    }

    //= ToggleUnassign() ===========================================================================
    /**
     * toggleUnassign()
     *
     * Undo assign or redo assign depend on the current state
     *
     * When current state is ASSIGNED
     * 1. set status to unassign, set text to chalky and call unassign function in Model
     * 2. display error when unassign function in model throw an error
     */
    @Test
    public void testToggleUnassign_Assigned_1_PositiveTest() throws Exception {
        ViewGroup parent  = Mockito.mock(ViewGroup.class);
        when(view.getParent()).thenReturn(parent);
        when(parent.findViewById(anyInt())).thenReturn(dummyView)
                .thenReturn(dummyView).thenReturn(dummyView)
                .thenReturn(dummyBox).thenReturn(dummyView);
        when(dummyView.getText()).thenReturn("Testing");
        doNothing().when(taskModel).unassignCandidate(anyString(), anyString());

        manager.toggleUnassign(view);

        verify(dummyView, times(3)).setAlpha(0.1f);
        verify(taskModel).unassignCandidate(anyString(), anyString());
        verify(taskModel, never()).assignCandidate(anyString());
        verify(taskView, never()).displayError(any(ProcessException.class));
    }

    @Test
    public void testToggleUnassign_Assigned_2_NegativeTest() throws Exception {
        ViewGroup parent  = Mockito.mock(ViewGroup.class);
        when(view.getParent()).thenReturn(parent);
        when(parent.findViewById(anyInt())).thenReturn(dummyView)
                .thenReturn(dummyView).thenReturn(dummyView)
                .thenReturn(dummyBox).thenReturn(dummyView);
        when(dummyView.getText()).thenReturn("Testing");

        ProcessException err = new ProcessException(ProcessException.FATAL_MESSAGE);
        doThrow(err).when(taskModel).unassignCandidate(anyString(), anyString());

        manager.toggleUnassign(view);

        verify(dummyView, never()).setAlpha(anyFloat());
        verify(taskModel).unassignCandidate(anyString(), anyString());
        verify(taskModel, never()).assignCandidate(anyString());
        verify(taskView).displayError(err);
    }

    /**
     * toggleUnassign()
     *
     * When current state is UNASSIGNED
     * 1. set status to assign, set text to clear and call assign function in Model
     * 2. display error when assign function in model throw an error
     *
     */

    @Test
    public void testToggleUnassign_Unassigned_1_PositiveTest() throws ProcessException {
        ViewGroup parent  = Mockito.mock(ViewGroup.class);
        when(view.getParent()).thenReturn(parent);
        when(parent.findViewById(anyInt())).thenReturn(dummyView)
                .thenReturn(dummyView).thenReturn(dummyView)
                .thenReturn(dummyBox).thenReturn(dummyView);
        when(dummyView.getText()).thenReturn("Testing");
        when(dummyBox.isChecked()).thenReturn(true);
        doNothing().when(taskModel).assignCandidate(anyString());

        manager.toggleUnassign(view);

        verify(dummyView, times(3)).setAlpha(1.0f);
        verify(taskModel, never()).unassignCandidate(anyString(), anyString());
        verify(taskModel).assignCandidate(anyString());
        verify(taskView, never()).displayError(any(ProcessException.class));
    }

    @Test
    public void testToggleUnassign_Unassigned_2_NegativeTest() throws ProcessException {
        ViewGroup parent  = Mockito.mock(ViewGroup.class);
        when(view.getParent()).thenReturn(parent);
        when(parent.findViewById(anyInt())).thenReturn(dummyView)
                .thenReturn(dummyView).thenReturn(dummyView)
                .thenReturn(dummyBox).thenReturn(dummyView);
        when(dummyView.getText()).thenReturn("Testing");
        when(dummyBox.isChecked()).thenReturn(true);
        ProcessException err = new ProcessException(ProcessException.FATAL_MESSAGE);
        doThrow(err).when(taskModel).assignCandidate(anyString());

        manager.toggleUnassign(view);

        verify(dummyView, never()).setAlpha(1.0f);
        verify(taskModel, never()).unassignCandidate(anyString(), anyString());
        verify(taskModel).assignCandidate(anyString());
        verify(taskView).displayError(err);
    }

    //= OnChiefRespond() ===========================================================================
    /**
     * onChiefRespond()
     *
     * Whatever the result, when receive a respond, call closeProgressDialog
     * 1. When respond is positive, do nothing
     * 2. When respond is negative, display the error
     *
     */
    @Test
    public void testOnChiefRespond_1() throws Exception {
        ErrorManager errManager = Mockito.mock(ErrorManager.class);
        ConnectionTask.setCompleteFlag(false);
        ConnectionTask conTask = Mockito.mock(ConnectionTask.class);
        ExternalDbLoader.setConnectionTask(conTask);
        String message = "{\"Result\":true}";

        assertFalse(manager.isSent());
        manager.onChiefRespond(errManager, message);

        verify(taskView).closeProgressWindow();
        assertTrue(manager.isSent());
        verify(conTask, never()).publishError(any(ErrorManager.class), any(ProcessException.class));
    }

    @Test
    public void testOnChiefRespond_2() throws Exception {
        ErrorManager errManager = Mockito.mock(ErrorManager.class);
        ConnectionTask.setCompleteFlag(false);
        ConnectionTask conTask = Mockito.mock(ConnectionTask.class);
        ExternalDbLoader.setConnectionTask(conTask);
        String message = "{\"Result\":false}";

        assertFalse(manager.isSent());
        manager.onChiefRespond(errManager, message);

        verify(taskView).closeProgressWindow();
        assertTrue(manager.isSent());
        verify(conTask).publishError(any(ErrorManager.class), any(ProcessException.class));
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
        verify(taskView, never()).displayError(err);
    }

    @Test
    public void testOnTimesOutWithView() throws Exception {
        ProcessException err = new ProcessException(ProcessException.MESSAGE_TOAST);

        manager.onTimesOut(err);

        verify(taskView).closeProgressWindow();
        verify(taskView).displayError(err);
    }

}