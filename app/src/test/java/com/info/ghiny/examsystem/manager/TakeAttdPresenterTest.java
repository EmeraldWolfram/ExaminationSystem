package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.InfoGrabActivity;
import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.SubmissionActivity;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.Session;
import com.info.ghiny.examsystem.interfacer.TakeAttdMVP;
import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.JavaHost;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Calendar;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

/**
 * Created by GhinY on 12/08/2016.
 */
public class TakeAttdPresenterTest {
    private TakeAttdPresenter manager;
    private TakeAttdMVP.View taskView;
    private TakeAttdMVP.Model taskModel;
    private ConnectionTask task;
    private JavaHost javaHost;
    private Handler handler;
    private DialogInterface dialog;
    private SharedPreferences preferences;

    @Before
    public void setUp() throws Exception {
        taskView    = Mockito.mock(TakeAttdMVP.View.class);
        taskModel   = Mockito.mock(TakeAttdMVP.Model.class);
        handler     = Mockito.mock(Handler.class);
        dialog      = Mockito.mock(DialogInterface.class);
        javaHost = Mockito.mock(JavaHost.class);
        task        = Mockito.mock(ConnectionTask.class);
        preferences = Mockito.mock(SharedPreferences.class);

        ExternalDbLoader.setConnectionTask(task);
        ExternalDbLoader.setJavaHost(javaHost);
        ConnectionTask.setCompleteFlag(false);

        manager = new TakeAttdPresenter(taskView, preferences);
        manager.setTaskModel(taskModel);
    }


    //= OnScan() ===================================================================================
    /**
     * onScan()
     *
     * Always pause the scanner and resume the scanner
     * 1. MvpModel did not throw any error
     * 2. MvpModel throw a MESSAGE_TOAST error
     * 3. MvpModel throw a MESSAGE_DIALOG error
     *
     * @throws Exception
     */
    @Test
    public void testOnScan1_withoutAnyNegativeResult() throws Exception {
        doNothing().when(taskModel).tryAssignScanValue("30");

        manager.onScan("30");

        verify(taskView).pauseScanning();
        verify(taskView).resumeScanning();
        verify(taskView, never()).displayError(any(ProcessException.class));
    }

    @Test
    public void testOnScan2_withNegativeToastResult() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, 12);
        doThrow(err).when(taskModel).tryAssignScanValue("XXX");

        manager.onScan("XXX");

        verify(taskView).pauseScanning();
        verify(taskView).displayError(err);
        verify(taskView).resumeScanning();
    }

    @Test
    public void testOnScan3_withNegativeDialogResult() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_DIALOG, 12);
        doThrow(err).when(taskModel).tryAssignScanValue("XXX");

        manager.onScan("XXX");

        verify(taskView).pauseScanning();
        verify(taskView).displayError(err);
        verify(taskView, never()).resumeScanning();
    }

    //= OnPasswordReceived() ========================================================================
    /**
     * onPasswordReceived()
     *
     * 1. when the password is correct, pause the scanning and resume the scanning
     * 2. when the password is incorrect, pause the scanning, display the error,
     *    control MvpView to prompt the user password again and never resume scanning
     *
     * @throws Exception
     */
    @Test
    public void testOnPasswordReceived_CorrectPassword() throws Exception {
        Intent pw    = Mockito.mock(Intent.class);
        when(pw.getStringExtra("Password")).thenReturn("123456");
        doNothing().when(taskModel).matchPassword("123456");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, pw);

        verify(taskView).pauseScanning();
        verify(taskView).resumeScanning();
        verify(taskModel).matchPassword("123456");
        verify(taskView, never()).displayError(any(ProcessException.class));
        verify(taskView, never()).securityPrompt(false);
    }

    @Test
    public void testOnPasswordReceived_IncorrectPassword() throws Exception {
        Intent pw    = Mockito.mock(Intent.class);
        when(pw.getStringExtra("Password")).thenReturn("wrong_Password");
        ProcessException err = new ProcessException(ProcessException.MESSAGE_TOAST);
        doThrow(err).when(taskModel).matchPassword("wrong_Password");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, pw);

        verify(taskView).pauseScanning();
        verify(taskView, never()).resumeScanning();
        verify(taskModel).matchPassword("wrong_Password");
        verify(taskView).displayError(err);
        verify(taskView).securityPrompt(false);
    }

    //= OnPause() ==================================================================================
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

    //= OnResume() =================================================================================
    /**
     * onResume()
     *
     * 1. resume the scanner and update assign list when called
     * 2. throw error due to failure updating assign list
     *
     * @throws Exception
     */
    @Test
    public void testOnResume1_AbleToUpdate() throws Exception {
        doNothing().when(taskModel).updateAssignList();
        when(preferences.getString(anyString(), anyString())).thenReturn("4");
        manager.onResume();

        verify(taskModel).updateAssignList();
        verify(taskView).resumeScanning();
        verify(taskView, never()).displayError(any(ProcessException.class));
    }

    @Test
    public void testOnResume2_ModelThrowError() throws Exception {
        doThrow(new ProcessException(ProcessException.MESSAGE_TOAST))
                .when(taskModel).updateAssignList();
        when(preferences.getString(anyString(), anyString())).thenReturn("4");
        manager.onResume();

        verify(taskModel).updateAssignList();
        verify(taskView, never()).resumeScanning();
        verify(taskView).displayError(any(ProcessException.class));
    }

    //= OnResume(...) ==============================================================================
    /**
     * onResume(ErrorManager errManager)
     *
     * Tests:
     * Set message listener to JavaHost and call onResume
     *
     */
    @Test
    public void testOnResume1() throws Exception {
        ErrorManager errorManager = Mockito.mock(ErrorManager.class);
        when(preferences.getString(anyString(), anyString())).thenReturn("4");
        manager.onResume(errorManager);

        verify(javaHost).setMessageListener(any(JavaHost.OnMessageReceived.class));
        verify(taskView).resumeScanning();
    }

    //= OnRestart() ================================================================================
    /**
     * onRestart()
     *
     * ask user to key in password when the activity restarted
     *
     * @throws Exception
     */
    @Test
    public void testOnRestart1_NotNavigationTrigger() throws Exception {
        manager.setNavigationFlag(false);

        manager.onRestart();

        verify(taskView).securityPrompt(false);
    }

    @Test
    public void testOnRestart_TriggerByNavigation() throws Exception {
        manager.setNavigationFlag(true);

        manager.onRestart();

        verify(taskView, never()).securityPrompt(false);
    }

    //= onDestroy() ================================================================================
    /**
     * onDestroy()
     *
     * When this screen is about to destroy
     * save the data into database, close any window and stop any timer
     */
    @Test
    public void testOnDestroy() throws Exception {
        manager.setSynTimer(handler);

        manager.onDestroy();

        verify(taskModel).txAttendanceUpdate();
        verify(handler).removeCallbacks(any(Runnable.class));

    }

    //= onSwipeLeft() ==============================================================================
    /**
     * onSwipeLeft()
     *
     * This method will be called when TakeAttdActivity was swiped from right to left
     * this method should start the ReportActivity
     */
    @Test
    public void testOnSwipeLeft() throws Exception {
        manager.onSwipeLeft();
        verify(taskView).navigateActivity(SubmissionActivity.class);
    }

    //= onSwipeBottom() ============================================================================
    /**
     * onSwipeBottom()
     *
     * This method will be called when TakeAttdActivity was swiped from top to bottom
     * this method should start the InfoGrabActivity
     */
    @Test
    public void testOnSwipeBottom() throws Exception {
        manager.onSwipeBottom();
        verify(taskView).navigateActivity(InfoGrabActivity.class);
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
        doNothing().when(taskModel).checkDownloadResult("ATTENDANCE & PAPERS");

        manager.onChiefRespond(errorManager, "ATTENDANCE & PAPERS");

        verify(taskModel).checkDownloadResult("ATTENDANCE & PAPERS");
        verify(task, never()).publishError(any(ErrorManager.class), any(ProcessException.class));
    }

    @Test
    public void testOnChiefRespond2_NegativeResult() throws Exception {
        ErrorManager errorManager   = Mockito.mock(ErrorManager.class);
        ProcessException err    = new ProcessException(ProcessException.FATAL_MESSAGE);
        doThrow(err).when(taskModel).checkDownloadResult("NO DATA");

        manager.onChiefRespond(errorManager, "NO DATA");

        verify(taskModel).checkDownloadResult("NO DATA");
        verify(task).publishError(errorManager, err);
    }

    //= NotifyTableScanned() =============================================================================

    /**
     * notifyTableScanned()
     *
     * Set TextView (tableNum) in the MvpView
     *
     * 1. When the table is 0, set the MvpView to empty string
     * 2. When the table is not 0, set the MvpView to the respective string
     *
     * @throws Exception
     */

    @Test
    public void testDisplayTable_withNumberZeroOrSmaller() throws Exception {
        manager.notifyTableScanned(0);
        verify(taskView).setTableView("");
    }

    @Test
    public void testDisplayTable_withNumberLargerThanZero() throws Exception {
        manager.notifyTableScanned(12);
        verify(taskView).setTableView("12");
    }

    //= NotifyCandidateScanned() =============================================================================

    /**
     * notifyCandidateScanned(Candidate)
     *
     * Set all the TextView related to Candidate (Index, RegNum, Paper ...) in the MvpView
     *
     * 1. If the received candidate have all the field ready, set it successfully
     * 2. If the received candidate is null or having any null field, display the error
     *
     * @throws Exception
     */
    @Test
    public void testDisplayCandidate_withAllDataReady() throws Exception {
        ExamSubject paper = new ExamSubject("BAME 0001", "SUBJECT 1", 0,
                Calendar.getInstance(), 10, "H1", Session.AM);
        Candidate cdd = Mockito.mock(Candidate.class);
        when(cdd.getExamIndex()).thenReturn("W0000AUMB");
        when(cdd.getRegNum()).thenReturn("15WAU00001");
        when(cdd.getPaper()).thenReturn(paper);

        manager.notifyCandidateScanned(cdd);

        verify(taskView).setCandidateView("W0000AUMB", "15WAU00001", "BAME 0001  SUBJECT 1");
        verify(taskView, never()).displayError(any(ProcessException.class));
    }

    @Test
    public void testDisplayCandidate_withNullField() throws Exception {
        Candidate cdd = Mockito.mock(Candidate.class);
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_DIALOG, 0);
        when(cdd.getPaper()).thenThrow(err);

        manager.notifyCandidateScanned(cdd);

        verify(taskView, never()).setCandidateView(anyString(), anyString(), anyString());
        verify(taskView).displayError(any(ProcessException.class));
    }

    //= NotifyDisplayReset() ===================================================================

    /**
     * notifyDisplayReset()
     *
     * clear the Table and Candidate related TextView in the layout xml
     *
     * @throws Exception
     */

    @Test
    public void testResetDisplay() throws Exception {
        when(preferences.getString(anyString(), anyString())).thenReturn("4");

        manager.notifyDisplayReset();

        verify(taskView).setTableView("");
        verify(taskView).setCandidateView("", "", "");
        verify(taskView).setAssignBackgroundColor(R.color.colorDarkGreen);
        verify(taskModel).updateAssignList();
        verify(taskView).resumeScanning();
    }

    //= OnClick(...) ===============================================================================
    /**
     * onClick(...)
     *
     * Whenever a message window pop out, the camera scanner at the back will be paused
     * Test if the scanner is resumed, when button is clicked or the activity will be finished
     * 1. Neutral Button Pressed    (resume scanner)
     * 2. Negative Button Pressed   (resume scanner)
     * 3. Positive Button Pressed   (finish activity)
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
     * Test if the scanner is resumed when the back button was pressed in the above case
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

    //= NotifyReassign(...) ========================================================================
    /**
     * notifyReassign(...)
     *
     * Tests:
     * 1. Input byte is not listed, do nothing
     * 2. Input byte is TABLE_REASSIGN, setAssignBackgroundColor(...)
     * 3. Input byte is CANDIDATE_REASSIGN, setAssignBackgroundColor(...)
     *
     */
    @Test
    public void testSignalReassign1_NotListedInput() throws Exception{
        manager.notifyReassign(10);

        verify(taskView, never()).setAssignBackgroundColor(R.color.colorDarkRed);
    }

    @Test
    public void testSignalReassign2_TableReassign() throws Exception{
        manager.notifyReassign(TakeAttdMVP.TABLE_REASSIGN);

        verify(taskView).setAssignBackgroundColor(R.color.colorDarkRed);
    }

    @Test
    public void testSignalReassign3_CandidateReassign() throws Exception{
        manager.notifyReassign(TakeAttdMVP.CANDIDATE_REASSIGN);

        verify(taskView).setAssignBackgroundColor(R.color.colorDarkRed);
    }
}