package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.SettingActivity;
import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.fragments.FragmentPresent;
import com.info.ghiny.examsystem.interfacer.HomeOptionMVP;
import com.info.ghiny.examsystem.interfacer.SubmissionMVP;
import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.JavaHost;
import com.info.ghiny.examsystem.model.LoginModel;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TakeAttdModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by FOONG on 25/12/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class SubmissionPresenterTest {

    private SubmissionPresenter manager;
    private SubmissionMVP.MvpModel taskModel;
    private SubmissionMVP.MvpView taskView;
    private ConnectionTask task;
    private JavaHost javaHost;
    private Handler handler;
    private DialogInterface dialog;

    @Before
    public void setUp() throws Exception {
        taskView    = Mockito.mock(SubmissionMVP.MvpView.class);
        taskModel   = Mockito.mock(SubmissionMVP.MvpModel.class);
        handler     = Mockito.mock(Handler.class);
        dialog      = Mockito.mock(DialogInterface.class);
        javaHost    = Mockito.mock(JavaHost.class);
        task        = Mockito.mock(ConnectionTask.class);

        ExternalDbLoader.setConnectionTask(task);
        ExternalDbLoader.setJavaHost(javaHost);
        ConnectionTask.setCompleteFlag(false);

        manager = new SubmissionPresenter(taskView);
        manager.setTaskModel(taskModel);
        manager.setHandler(handler);
        TakeAttdModel.setAttdList(null);
    }

    @After
    public void tearDown() throws Exception {}

    //= OnResume(...) ==============================================================================
    /**
     * onResume(ErrorManager errManager)
     *
     * Tests:
     * 1. Set message listener to JavaHost
     *
     */
    @Test
    public void testOnResume1_AttdListIsNull_Download() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);

        manager.onResume(errorManager);

        verify(javaHost).setMessageListener(any(JavaHost.OnMessageReceived.class));
    }

    //= OnRestart() ================================================================================
    /**
     * onRestart()
     *
     * ask user to key in password when the activity restarted
     *
     * Tests:
     * 1. !NavFlag & !SecureFlag, prompt user for password
     * 2. !NavFlag & SecureFlag, do not prompt user for password
     * 3. NavFlag & !SecureFlag, do not prompt user for password
     * 4. NavFlag & SecureFlag, do not prompt user for password
     *
     */
    @Test
    public void testOnRestart1_NotNavigation_Trigger() throws Exception {
        manager.setNavFlag(false);
        manager.setSecureFlag(false);

        manager.onRestart();

        verify(taskView).securityPrompt(false);
    }

    @Test
    public void testOnRestart_NotNavigation_But_AlreadyPrompt() throws Exception {
        manager.setNavFlag(false);
        manager.setSecureFlag(true);

        manager.onRestart();

        verify(taskView, never()).securityPrompt(false);
    }

    @Test
    public void testOnRestart3_IsNavigation_DoNotPrompt() throws Exception {
        manager.setNavFlag(true);
        manager.setSecureFlag(false);

        manager.onRestart();

        verify(taskView, never()).securityPrompt(false);
    }

    @Test
    public void testOnRestart4_IsNavigationSomemoreAlreadyPrompt() throws Exception {
        manager.setNavFlag(true);
        manager.setSecureFlag(true);

        manager.onRestart();

        verify(taskView, never()).securityPrompt(false);
    }

    //= onDestroy() ================================================================================
    /**
     * onDestroy()
     *
     * When this screen is about to destroy
     * close any window and stop any timer
     */
    @Test
    public void testOnDestroy() throws Exception {
        manager.onDestroy();

        verify(taskView).closeProgressWindow();
        verify(handler).removeCallbacks(taskModel);
    }

    //= OnUpload ===================================================================================

    /**
     * onUpload()
     *
     * This method feed data to the view layer to display a window with attendance report to submit
     * It allow the user to perform submit or cancel
     */
    @Test
    public void onUpload() throws Exception {
        StaffIdentity staff = new StaffIdentity("TEST ID", true, "Dr. POH T.V.", "M4");
        LoginModel.setStaff(staff);
        TakeAttdModel.setAttdList(new AttendanceList());

        manager.onUpload();

        verify(taskView).displayReportWindow(staff.getName(), staff.getExamVenue(),
                new String[]{"0","0","0","0"}, "0");

    }

    //= OnPasswordReceived() ========================================================================
    /**
     * onPasswordReceived()
     *
     * 1. when password is correct in Upload Mode, upload the attendance list
     * 2. when password is incorrect in Upload Mode, display error & prompt user password again
     * 3. when password is correct in Normal Mode, do nothing
     * 4. when password is incorrect in Normal Mode, display error & prompt user password again
     *
     * @throws Exception
     */
    @Test
    public void testOnPasswordReceived_1_CorrectPassword_Upload() throws Exception {
        Intent pw    = Mockito.mock(Intent.class);
        when(pw.getStringExtra("Password")).thenReturn("123456");
        doNothing().when(taskModel).matchPassword("123456");
        manager.setUploadFlag(true);

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, pw);

        verify(taskModel).matchPassword("123456");
        verify(taskModel).uploadAttdList();
        verify(taskView, never()).displayError(any(ProcessException.class));
        verify(taskView, never()).securityPrompt(true);
    }

    @Test
    public void testOnPasswordReceived_2_IncorrectPassword_Upload() throws Exception {
        Intent pw    = Mockito.mock(Intent.class);
        when(pw.getStringExtra("Password")).thenReturn("wrong_Password");
        ProcessException err = new ProcessException(ProcessException.MESSAGE_TOAST);
        doThrow(err).when(taskModel).matchPassword("wrong_Password");
        manager.setUploadFlag(true);

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, pw);

        verify(taskModel).matchPassword("wrong_Password");
        verify(taskModel, never()).uploadAttdList();
        verify(taskView).displayError(err);
        verify(taskView).securityPrompt(true);
    }

    @Test
    public void testOnPasswordReceived_3_CorrectPassword_Normal() throws Exception {
        Intent pw    = Mockito.mock(Intent.class);
        when(pw.getStringExtra("Password")).thenReturn("123456");
        doNothing().when(taskModel).matchPassword("123456");
        manager.setUploadFlag(false);

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, pw);

        verify(taskModel).matchPassword("123456");
        verify(taskModel, never()).uploadAttdList();
        verify(taskView, never()).displayError(any(ProcessException.class));
        verify(taskView, never()).securityPrompt(false);
    }

    @Test
    public void testOnPasswordReceived_4_IncorrectPassword_Normal() throws Exception {
        Intent pw    = Mockito.mock(Intent.class);
        when(pw.getStringExtra("Password")).thenReturn("wrong_Password");
        ProcessException err = new ProcessException(ProcessException.MESSAGE_TOAST);
        doThrow(err).when(taskModel).matchPassword("wrong_Password");
        manager.setUploadFlag(false);

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, pw);

        verify(taskModel).matchPassword("wrong_Password");
        verify(taskModel, never()).uploadAttdList();
        verify(taskView).displayError(err);
        verify(taskView).securityPrompt(false);
    }

    //= onChiefRespond() ===========================================================================
    /**
     * onChiefRespond()
     *
     * When model start request for attendance list, chief will respond
     * This method will be called when the respond received
     *
     * Tests:
     * 1. Received message, send to model, model did not complain
     * 2. Received message, send to model, model complain with Exception
     *
     */
    @Test
    public void testOnChiefRespond1_PositiveResult() throws Exception {
        ErrorManager errorManager   = Mockito.mock(ErrorManager.class);
        doNothing().when(taskModel).verifyChiefResponse("{\"Type\":\"Submission\",\"Value\":\"NO DATA\"}");

        manager.onChiefRespond(errorManager, "{\"Type\":\"Submission\",\"Value\":\"NO DATA\"}");

        verify(taskView).closeProgressWindow();
        verify(taskModel).verifyChiefResponse("{\"Type\":\"Submission\",\"Value\":\"NO DATA\"}");
        verify(task, never()).publishError(any(ErrorManager.class), any(ProcessException.class));
    }

    @Test
    public void testOnChiefRespond2_NegativeResult() throws Exception {
        ErrorManager errorManager   = Mockito.mock(ErrorManager.class);
        ProcessException err    = new ProcessException(ProcessException.FATAL_MESSAGE);
        doThrow(err).when(taskModel).verifyChiefResponse("{\"Type\":\"Submission\",\"Value\":\"NO DATA\"}");


        manager.onChiefRespond(errorManager, "{\"Type\":\"Submission\",\"Value\":\"NO DATA\"}");

        verify(taskView).closeProgressWindow();
        verify(taskModel).verifyChiefResponse("{\"Type\":\"Submission\",\"Value\":\"NO DATA\"}");
        verify(task).publishError(errorManager, err);
    }


    //= OnSetting ==================================================================================

    /**
     * onSetting()
     *
     * Simple navigating method
     * make sure nav flag was flagged
     */
    @Test
    public void onSetting() throws Exception {
        manager.setNavFlag(false);

        manager.onSetting();

        assertTrue(manager.isNavFlag());
        verify(taskView).navigateActivity(SettingActivity.class);
    }

    //= OnClick(...) ===============================================================================
    /**
     * onClick(...)
     *
     * When message pop out
     *
     * Tests
     * 1. Neutral Button Pressed    do nothing
     * 2. Negative Button Pressed   do nothing
     * 3. Positive Button Pressed   (upload button pressed, prompt cancellable user password)
     */
    @Test
    public void testOnClickNeutralButton() throws Exception {
        manager.setSecureFlag(false);
        manager.setNavFlag(false);
        manager.setUploadFlag(false);

        manager.onClick(dialog, DialogInterface.BUTTON_NEUTRAL);

        verify(dialog).cancel();
        verify(taskView, never()).securityPrompt(true);
        assertFalse(manager.isNavFlag());
        assertFalse(manager.isSecureFlag());
        assertFalse(manager.isUploadFlag());
    }

    @Test
    public void testOnClickNegativeButton() throws Exception {
        manager.setSecureFlag(false);
        manager.setNavFlag(false);
        manager.setUploadFlag(false);

        manager.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);

        verify(dialog).cancel();
        verify(taskView, never()).securityPrompt(true);
        assertFalse(manager.isNavFlag());
        assertFalse(manager.isSecureFlag());
        assertFalse(manager.isUploadFlag());
    }

    @Test
    public void testOnClickPositiveButton() throws Exception {
        manager.setSecureFlag(false);
        manager.setNavFlag(false);
        manager.setUploadFlag(false);

        manager.onClick(dialog, DialogInterface.BUTTON_POSITIVE);

        verify(dialog).cancel();
        verify(taskView).securityPrompt(true);
        assertFalse(manager.isNavFlag());
        assertTrue(manager.isSecureFlag());
        assertTrue(manager.isUploadFlag());
    }

    //= OnCancel(...) ==============================================================================
    /**
     * onCancel(...)
     *
     * Sometimes, a pop out window could be cancelled by pressing the back button
     * of the phone
     *
     */
    @Test
    public void testOnCancel() throws Exception {
        manager.onCancel(dialog);

        verify(dialog).cancel();
    }

    //= OnTimesOut(...) ============================================================================
    /**
     * onTimesOut(...)
     *
     * When a message was sent to the chief to query attendance list,
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
        TakeAttdPresenter manager   = new TakeAttdPresenter(null, null);

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