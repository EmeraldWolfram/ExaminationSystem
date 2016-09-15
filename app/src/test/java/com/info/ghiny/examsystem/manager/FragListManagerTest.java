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
import com.info.ghiny.examsystem.interfacer.GeneralView;
import com.info.ghiny.examsystem.model.FragmentHelper;
import com.info.ghiny.examsystem.model.LoginHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.fail;
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
public class FragListManagerTest {
    private FragmentHelper fragModel;
    private FragListManager manager;
    private GeneralView generalView;
    private Handler handler;
    private View view;
    private TextView dummyView;
    private CheckBox dummyBox;

    @Before
    public void setUp() throws Exception {
        StaffIdentity id = new StaffIdentity("staff1", true, "AM_Staff", "H3");
        id.setPassword("123456");
        LoginHelper.setStaff(id);

        generalView = Mockito.mock(GeneralView.class);
        fragModel   = Mockito.mock(FragmentHelper.class);
        handler     = Mockito.mock(Handler.class);

        manager     = new FragListManager(generalView);
        manager.setFragmentModel(fragModel);
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
     * @throws Exception
     */
    @Test
    public void testSignToUpload() throws Exception {
        manager.signToUpload();
        verify(generalView).securityPrompt(true);
    }

    //= OnReceivePassword() ========================================================================

    /**
     * onPasswordReceived()
     *
     * 1. Model notify a Positive Result, tell Model to upload attendance and start timer
     * 2. Model notify a Negative Result, display the error thrown by Model
     * 3. Extended: Receive Correct Password, prompt because of inactivity
     * 4. Extended: Receive Wrong Password, prompt because of inactivity
     *
     * @throws Exception
     */

    @Test
    public void testOnReceivePassword_CorrectPassword() throws Exception {
        Intent pw = Mockito.mock(Intent.class);

        when(pw.getStringExtra("Password")).thenReturn("123456");
        doNothing().when(generalView).securityPrompt(true);
        manager.signToUpload();

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, pw);

        verify(fragModel).uploadAttdList();
        verify(handler).postDelayed(any(Runnable.class), anyInt());
        verify(generalView, never()).displayError(any(ProcessException.class));
    }

    @Test
    public void testOnReceivePassword_IncorrectPassword() throws Exception {
        Intent pw = Mockito.mock(Intent.class);

        doNothing().when(generalView).securityPrompt(true);
        when(pw.getStringExtra("Password")).thenReturn("abcdef");

        manager.signToUpload();
        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, pw);

        verify(fragModel, never()).uploadAttdList();
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(generalView).displayError(any(ProcessException.class));
    }

    @Test
    public void testOnReceivePassword_CorrectPassword_PromptOnInactiviy() throws Exception {
        Intent pw = Mockito.mock(Intent.class);

        when(pw.getStringExtra("Password")).thenReturn("123456");
        doNothing().when(generalView).securityPrompt(true);

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, pw);

        verify(fragModel, never()).uploadAttdList();
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(generalView, never()).displayError(any(ProcessException.class));
    }

    @Test
    public void testOnReceivePassword_IncorrectPassword_PromptOnInactivity() throws Exception {
        Intent pw = Mockito.mock(Intent.class);

        doNothing().when(generalView).securityPrompt(true);
        when(pw.getStringExtra("Password")).thenReturn("abcdef");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, pw);

        verify(fragModel, never()).uploadAttdList();
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(generalView).displayError(any(ProcessException.class));
    }

    //= OnResume() =================================================================================

    /**
     * onResume()
     *
     * Set the listener of the running TCPClient object when called
     *
     * @throws Exception
     */
    @Test
    public void testOnResume() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);
        TCPClient tcpClient = Mockito.mock(TCPClient.class);
        ExternalDbLoader.setTcpClient(tcpClient);

        manager.onResume(errorManager);

        verify(tcpClient).setMessageListener(any(TCPClient.OnMessageReceived.class));
    }

    //= OnDestroy() ================================================================================
    /**
     * onDestroy()
     *
     * Stop the timer that was started after message sent to Chief if the Activity was destroy
     *
     * @throws Exception
     */
    @Test
    public void testOnDestroy() throws Exception {
        manager.onDestroy();
        verify(handler).removeCallbacks(any(Runnable.class));
    }

    //= ToggleUnassign() ===========================================================================
    /**
     * toggleUnassign()
     *
     * Undo assign or redo assign depend on the current state
     *
     * When current state is assigned
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
        doNothing().when(fragModel).unassignCandidate(anyString(), anyString());

        manager.toggleUnassign(view);

        verify(dummyView, times(3)).setAlpha(0.1f);
        verify(fragModel).unassignCandidate(anyString(), anyString());
        verify(fragModel, never()).assignCandidate(anyString());
        verify(generalView, never()).displayError(any(ProcessException.class));
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
        doThrow(err).when(fragModel).unassignCandidate(anyString(), anyString());

        manager.toggleUnassign(view);

        verify(dummyView, never()).setAlpha(anyFloat());
        verify(fragModel).unassignCandidate(anyString(), anyString());
        verify(fragModel, never()).assignCandidate(anyString());
        verify(generalView).displayError(err);
    }

    /**
     * toggleUnassign()
     *
     * When current state is unassigned
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
        doNothing().when(fragModel).assignCandidate(anyString());

        manager.toggleUnassign(view);

        verify(dummyView, times(3)).setAlpha(1.0f);
        verify(fragModel, never()).unassignCandidate(anyString(), anyString());
        verify(fragModel).assignCandidate(anyString());
        verify(generalView, never()).displayError(any(ProcessException.class));
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
        doThrow(err).when(fragModel).assignCandidate(anyString());

        manager.toggleUnassign(view);

        verify(dummyView, never()).setAlpha(1.0f);
        verify(fragModel, never()).unassignCandidate(anyString(), anyString());
        verify(fragModel).assignCandidate(anyString());
        verify(generalView).displayError(err);
    }
}