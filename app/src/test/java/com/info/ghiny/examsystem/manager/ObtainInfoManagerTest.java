package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.TaskConnView;
import com.info.ghiny.examsystem.interfacer.TaskScanViewOld;
import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.InfoCollectHelper;
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
 * Created by GhinY on 12/08/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class ObtainInfoManagerTest {
    private Handler handler;
    private InfoCollectHelper infoModel;
    private TaskScanViewOld taskScanViewOld;
    private TaskConnView taskConnView;
    private ObtainInfoManager manager;

    @Before
    public void setUp() throws Exception {
        infoModel = Mockito.mock(InfoCollectHelper.class);
        taskScanViewOld = Mockito.mock(TaskScanViewOld.class);
        taskConnView = Mockito.mock(TaskConnView.class);
        handler = Mockito.mock(Handler.class);

        manager = new ObtainInfoManager(taskScanViewOld, taskConnView);
        manager.setHandler(handler);
        manager.setInfoModel(infoModel);
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
        doNothing().when(infoModel).reqCandidatePapers("15WAU00001");

        manager.onScan("15WAU00001");

        verify(taskScanViewOld).pauseScanning();
        verify(taskConnView).openProgressWindow();
        verify(infoModel).reqCandidatePapers("15WAU00001");
        verify(handler).postDelayed(any(Runnable.class), anyInt());
        verify(taskScanViewOld, never()).displayError(any(ProcessException.class));
        verify(taskScanViewOld, never()).resumeScanning();
    }

    @Test
    public void testOnScanForCandidateDetail_ModelComplainWithToast() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, 1);
        doThrow(err).when(infoModel).reqCandidatePapers("33");

        manager.onScan("33");

        verify(taskScanViewOld).pauseScanning();
        verify(taskConnView, never()).openProgressWindow();
        verify(infoModel).reqCandidatePapers("33");
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(taskScanViewOld).displayError(err);
        verify(taskScanViewOld).resumeScanning();
    }

    @Test
    public void testOnScanForCandidateDetail_ModelComplainWithDialog() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_DIALOG, 1);
        doThrow(err).when(infoModel).reqCandidatePapers("33");
        assertNull(err.getListener(ProcessException.okayButton));

        manager.onScan("33");

        verify(taskScanViewOld).pauseScanning();
        verify(taskConnView, never()).openProgressWindow();
        verify(infoModel).reqCandidatePapers("33");
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(taskScanViewOld).displayError(err);
        verify(taskScanViewOld, never()).resumeScanning();
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
        verify(taskScanViewOld).pauseScanning();
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

        manager.onResume();

        verify(tcpClient, never()).setMessageListener(any(TCPClient.OnMessageReceived.class));
        verify(taskScanViewOld).resumeScanning();
    }

    @Test
    public void testOnResume_ConnectionOnResume() throws Exception {
        ErrorManager errManager = Mockito.mock(ErrorManager.class);
        TCPClient tcpClient     = Mockito.mock(TCPClient.class);
        ExternalDbLoader.setTcpClient(tcpClient);

        manager.onResume(errManager);

        verify(tcpClient).setMessageListener(any(TCPClient.OnMessageReceived.class));
        verify(taskScanViewOld).resumeScanning();
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
        verify(taskScanViewOld).securityPrompt(false);
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

        verify(taskConnView).closeProgressWindow();
        verify(handler).removeCallbacks(any(Runnable.class));
    }

    //= OnChiefRespond() ===========================================================================
    /**
     * onChiefRespond()
     *
     * 1. When respond is positive, navigate to ExamListActivity
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

        verify(taskConnView).closeProgressWindow();
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

        verify(taskConnView).closeProgressWindow();
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
        StaffIdentity staffIdentity = new StaffIdentity();
        staffIdentity.setPassword("CORRECT");
        LoginHelper.setStaff(staffIdentity);

        Intent popUpLoginAct = Mockito.mock(Intent.class);
        when(popUpLoginAct.getStringExtra("Password")).thenReturn("CORRECT");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, popUpLoginAct);

        verify(taskScanViewOld).pauseScanning();
        verify(taskScanViewOld).resumeScanning();
        verify(taskScanViewOld, never()).displayError(any(ProcessException.class));
        verify(taskScanViewOld, never()).securityPrompt(false);
    }

    @Test
    public void testOnPasswordReceived_2() throws Exception {
        StaffIdentity staffIdentity = new StaffIdentity();
        staffIdentity.setPassword("CORRECT");
        LoginHelper.setStaff(staffIdentity);

        Intent popUpLoginAct = Mockito.mock(Intent.class);
        when(popUpLoginAct.getStringExtra("Password")).thenReturn("INCORRECT");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, popUpLoginAct);

        verify(taskScanViewOld).pauseScanning();
        verify(taskScanViewOld, never()).resumeScanning();
        verify(taskScanViewOld).displayError(any(ProcessException.class));
        verify(taskScanViewOld).securityPrompt(false);
    }
}