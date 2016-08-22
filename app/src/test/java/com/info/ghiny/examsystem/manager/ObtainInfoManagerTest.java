package com.info.ghiny.examsystem.manager;

import android.content.DialogInterface;
import android.os.Handler;

import com.google.zxing.client.android.Intents;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.ScannerView;
import com.info.ghiny.examsystem.model.InfoCollectHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.Assert.*;
import static org.mockito.Mockito.withSettings;

/**
 * Created by GhinY on 12/08/2016.
 */
public class ObtainInfoManagerTest {
    private Handler handler;
    private InfoCollectHelper infoModel;
    private ScannerView scannerView;
    private ObtainInfoManager manager;

    @Before
    public void setUp() throws Exception {
        infoModel = Mockito.mock(InfoCollectHelper.class);
        scannerView = Mockito.mock(ScannerView.class);
        handler = Mockito.mock(Handler.class);

        manager = new ObtainInfoManager(scannerView);
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

        manager.onScanForCandidateDetail("15WAU00001");

        verify(scannerView).pauseScanning();
        verify(infoModel).reqCandidatePapers("15WAU00001");
        verify(handler).postDelayed(any(Runnable.class), anyInt());
        verify(scannerView, never()).displayError(any(ProcessException.class));
        verify(scannerView, never()).resumeScanning();
    }

    @Test
    public void testOnScanForCandidateDetail_ModelComplainWithToast() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, 1);
        doThrow(err).when(infoModel).reqCandidatePapers("33");

        manager.onScanForCandidateDetail("33");

        verify(scannerView).pauseScanning();
        verify(infoModel).reqCandidatePapers("33");
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(scannerView).displayError(err);
        verify(scannerView).resumeScanning();
    }

    @Test
    public void testOnScanForCandidateDetail_ModelComplainWithDialog() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_DIALOG, 1);
        doThrow(err).when(infoModel).reqCandidatePapers("33");
        assertNull(err.getListener(ProcessException.okayButton));

        manager.onScanForCandidateDetail("33");

        verify(scannerView).pauseScanning();
        verify(infoModel).reqCandidatePapers("33");
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());
        verify(scannerView).displayError(err);
        verify(scannerView, never()).resumeScanning();
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
        verify(scannerView).pauseScanning();
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
        verify(scannerView).resumeScanning();
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