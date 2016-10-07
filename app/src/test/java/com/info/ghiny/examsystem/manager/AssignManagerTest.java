package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.Intent;

import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.Session;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.TaskScanViewOld;
import com.info.ghiny.examsystem.interfacer.SetterView;
import com.info.ghiny.examsystem.model.AssignModel;
import com.info.ghiny.examsystem.model.LoginModel;
import com.info.ghiny.examsystem.model.ProcessException;

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

import static org.junit.Assert.*;

/**
 * Created by GhinY on 12/08/2016.
 */
public class AssignManagerTest {
    private AssignModel assignModel;
    private TaskScanViewOld taskScanViewOld;
    private SetterView setterView;
    private CheckListLoader dBLoader;
    private AssignManager manager;

    @Before
    public void setUp() throws Exception {
        StaffIdentity id = new StaffIdentity("staff1", true, "AM_Staff", "H3");
        id.setPassword("123456");
        LoginModel.setStaff(id);

        assignModel = Mockito.mock(AssignModel.class);
        taskScanViewOld = Mockito.mock(TaskScanViewOld.class);
        setterView  = Mockito.mock(SetterView.class);
        dBLoader    = Mockito.mock(CheckListLoader.class);

        manager = new AssignManager(taskScanViewOld, setterView, dBLoader);
        manager.setAssignModel(assignModel);
    }

    //= OnScanForTableOrCandidate() ================================================================

    /**
     * onScan()
     *
     * Always pause the scanner and resume the scanner
     * 1. Model did not throw any error
     * 2. Model throw a MESSAGE_TOAST error
     * 3. Model throw a MESSAGE_DIALOG error
     *
     * @throws Exception
     */
    @Test
    public void testOnScanForTableOrCandidate_withoutAnyNegativeResult() throws Exception {
        doNothing().when(assignModel).tryAssignScanValue("30");

        manager.onScan("30");

        verify(taskScanViewOld).pauseScanning();
        verify(taskScanViewOld).resumeScanning();
        verify(taskScanViewOld, never()).displayError(any(ProcessException.class));
    }

    @Test
    public void testOnScanForTableOrCandidate_withNegativeToastResult() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, 12);
        doThrow(err).when(assignModel).tryAssignScanValue("XXX");

        assertNull(err.getListener(ProcessException.okayButton));

        manager.onScan("XXX");

        verify(taskScanViewOld).pauseScanning();
        verify(taskScanViewOld).displayError(err);
        verify(taskScanViewOld).resumeScanning();
        assertNotNull(err.getListener(ProcessException.okayButton));
    }

    @Test
    public void testOnScanForTableOrCandidate_withNegativeDialogResult() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_DIALOG, 12);
        doThrow(err).when(assignModel).tryAssignScanValue("XXX");

        assertNull(err.getListener(ProcessException.okayButton));
        manager.onScan("XXX");

        verify(taskScanViewOld).pauseScanning();
        verify(taskScanViewOld).displayError(err);
        verify(taskScanViewOld, never()).resumeScanning();

        assertNotNull(err.getListener(ProcessException.okayButton));
    }

    //= OnPasswordReceived() ========================================================================

    /**
     * onPasswordReceived()
     *
     * 1. when the password is correct, pause the scanning and resume the scanning
     * 2. when the password is incorrect, pause the scanning, display the error,
     *    control View to prompt the user password again and never resume scanning
     *
     * @throws Exception
     */
    @Test
    public void testOnPasswordReceived() throws Exception {
        Intent pw    = Mockito.mock(Intent.class);
        when(pw.getStringExtra("Password")).thenReturn("123456");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, pw);

        verify(taskScanViewOld).pauseScanning();
        verify(taskScanViewOld).resumeScanning();
        verify(taskScanViewOld, never()).displayError(any(ProcessException.class));
        verify(taskScanViewOld, never()).securityPrompt(false);
    }

    @Test
    public void testOnPasswordReceived_withErrorThrown() throws Exception {
        Intent pw    = Mockito.mock(Intent.class);
        when(pw.getStringExtra("Password")).thenReturn("wrong_Password");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, pw);

        verify(taskScanViewOld).pauseScanning();
        verify(taskScanViewOld, never()).resumeScanning();
        verify(taskScanViewOld).displayError(any(ProcessException.class));
        verify(taskScanViewOld).securityPrompt(false);
    }

    //= OnBackPressed() ============================================================================

    /**
     * onBackPressed()
     *
     * ask the user for confirmation on logging out when called
     *
     * @throws Exception
     */
    @Test
    public void testOnBackPressed() throws Exception {
        manager.onBackPressed();
        verify(taskScanViewOld).displayError(any(ProcessException.class));
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
        verify(taskScanViewOld).pauseScanning();
    }

    //= OnResume() =================================================================================

    /**
     * onResume()
     *
     * resume the scanner when called
     *
     * @throws Exception
     */
    @Test
    public void testOnResume() throws Exception {
        manager.onResume();
        verify(taskScanViewOld).resumeScanning();
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
    public void testOnRestart() throws Exception {
        manager.onRestart();
        verify(taskScanViewOld).securityPrompt(false);
    }

    //= SetTable() =================================================================================

    /**
     * displayTable()
     *
     * Set TextView (tableNum) in the View
     *
     * 1. When the table is 0, set the View to empty string
     * 2. When the table is not 0, set the View to the respective string
     *
     * @throws Exception
     */

    @Test
    public void testSetTable_withNumberZeroOrSmaller() throws Exception {
        manager.displayTable(0);
        verify(setterView).setTableView("");
    }

    @Test
    public void testSetTable_withNumberLargerThanZero() throws Exception {
        manager.displayTable(12);
        verify(setterView).setTableView("12");
    }

    //= SetCandidate() =============================================================================

    /**
     * displayCandidate(Candidate)
     *
     * Set all the TextView related to Candidate (Index, RegNum, Paper ...) in the View
     *
     * 1. If the received candidate have all the field ready, set it successfully
     * 2. If the received candidate is null or having any null field, display the error
     *
     * @throws Exception
     */
    @Test
    public void testSetCandidate_withAllDataReady() throws Exception {
        ExamSubject paper = new ExamSubject("BAME 0001", "SUBJECT 1", 0,
                Calendar.getInstance(), 10, "H1", Session.AM);
        Candidate cdd = Mockito.mock(Candidate.class);
        when(cdd.getExamIndex()).thenReturn("W0000AUMB");
        when(cdd.getRegNum()).thenReturn("15WAU00001");
        when(cdd.getPaper()).thenReturn(paper);

        manager.displayCandidate(cdd);

        verify(setterView).setCandidateView("W0000AUMB", "15WAU00001", "BAME 0001  SUBJECT 1");
        verify(taskScanViewOld, never()).displayError(any(ProcessException.class));
    }

    @Test
    public void testSetCandidate_withNullField() throws Exception {
        Candidate cdd = Mockito.mock(Candidate.class);
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_DIALOG, 0);
        when(cdd.getPaper()).thenThrow(err);

        manager.displayCandidate(cdd);

        verify(setterView, never()).setCandidateView(anyString(), anyString(), anyString());
        verify(taskScanViewOld).displayError(any(ProcessException.class));
    }

    //= ClearTableAndCandidate() ===================================================================

    /**
     * resetDisplay()
     *
     * clear the Table and Candidate related TextView in the layout xml
     *
     * @throws Exception
     */

    @Test
    public void testClearTableAndCandidate() throws Exception {
        manager.resetDisplay();
        verify(setterView).setTableView("");
        verify(setterView).setCandidateView("", "", "");
    }
}