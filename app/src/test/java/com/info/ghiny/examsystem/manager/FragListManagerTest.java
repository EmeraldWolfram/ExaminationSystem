package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.GeneralView;
import com.info.ghiny.examsystem.model.ChiefLink;
import com.info.ghiny.examsystem.model.FragmentHelper;
import com.info.ghiny.examsystem.model.LoginHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by GhinY on 12/08/2016.
 */
public class FragListManagerTest {
    private FragmentHelper fragModel;
    private FragListManager manager;
    private GeneralView generalView;
    private Handler handler;

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
        verify(generalView).navigateActivity(PopUpLogin.class);
    }

    //= OnReceivePassword() ========================================================================

    /**
     * onReceivePassword()
     *
     * 1. Model notify a Positive Result, tell Model to upload attendance and start timer
     * 2. Model notify a Negative Result, display the error thrown by Model
     *
     * @throws Exception
     */

    @Test
    public void testOnReceivePassword_CorrectPassword() throws Exception {
        Intent pw = Mockito.mock(Intent.class);

        when(pw.getStringExtra("Password")).thenReturn("123456");

        manager.onReceivePassword(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, pw);

        verify(fragModel).uploadAttdList();
        verify(handler).postDelayed(any(Runnable.class), anyInt());
        verify(generalView, never()).displayError(any(ProcessException.class));
    }

    @Test
    public void testOnReceivePassword_IncorrectPassword() throws Exception {
        Intent pw = Mockito.mock(Intent.class);

        when(pw.getStringExtra("Password")).thenReturn("abcdef");

        manager.onReceivePassword(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, pw);

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
}