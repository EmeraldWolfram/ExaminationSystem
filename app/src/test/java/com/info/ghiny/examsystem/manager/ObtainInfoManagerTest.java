package com.info.ghiny.examsystem.manager;

import android.os.Handler;

import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.TaskScanView;
import com.info.ghiny.examsystem.model.InfoCollectHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 12/08/2016.
 */
public class ObtainInfoManagerTest {
    private Handler handler;
    private InfoCollectHelper infoModel;
    private TaskScanView taskScanView;
    private ObtainInfoManager manager;

    @Before
    public void setUp() throws Exception {
        infoModel = Mockito.mock(InfoCollectHelper.class);
        taskScanView = Mockito.mock(TaskScanView.class);
        handler = Mockito.mock(Handler.class);

        manager = new ObtainInfoManager(taskScanView);
        manager.setHandler(handler);
        manager.setInfoModel(infoModel);
    }

    //= OnScanForCandidateDetail() =================================================================

    /**
     * onScanForCandidate()
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

        verify(taskScanView).pauseScanning();
        verify(infoModel).reqCandidatePapers("15WAU00001");
        verify(handler).postDelayed(any(Runnable.class), anyInt());
        verify(taskScanView, never()).displayError(any(ProcessException.class));
        verify(taskScanView, never()).resumeScanning();
    }

    @Test
    public void testOnScanForCandidateDetail_ModelComplainWithToast() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, 1);
        doThrow(err).when(infoModel).reqCandidatePapers("33");

        manager.onScan("33");

        verify(taskScanView).pauseScanning();
        verify(infoModel).reqCandidatePapers("33");
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(taskScanView).displayError(err);
        verify(taskScanView).resumeScanning();
    }

    @Test
    public void testOnScanForCandidateDetail_ModelComplainWithDialog() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_DIALOG, 1);
        doThrow(err).when(infoModel).reqCandidatePapers("33");
        assertNull(err.getListener(ProcessException.okayButton));

        manager.onScan("33");

        verify(taskScanView).pauseScanning();
        verify(infoModel).reqCandidatePapers("33");
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(taskScanView).displayError(err);
        verify(taskScanView, never()).resumeScanning();
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
        verify(taskScanView).pauseScanning();
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
    public void testOnResume() throws Exception {
        ErrorManager errManager = Mockito.mock(ErrorManager.class);
        TCPClient tcpClient     = Mockito.mock(TCPClient.class);
        ExternalDbLoader.setTcpClient(tcpClient);

        manager.onResume(errManager);

        verify(tcpClient).setMessageListener(any(TCPClient.OnMessageReceived.class));
        verify(taskScanView).resumeScanning();
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

        verify(handler).removeCallbacks(any(Runnable.class));
    }
}