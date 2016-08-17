package com.info.ghiny.examsystem.manager;

import android.os.Handler;

import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.ScannerView;
import com.info.ghiny.examsystem.model.InfoCollectHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
 * Created by GhinY on 17/08/2016.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class CollectManagerTest {
    private Handler handler;
    private InfoCollectHelper infoModel;
    private ScannerView scannerView;
    private CollectManager manager;

    @Before
    public void setUp() throws Exception {
        infoModel = Mockito.mock(InfoCollectHelper.class);
        scannerView = Mockito.mock(ScannerView.class);
        handler = Mockito.mock(Handler.class);

        manager = new CollectManager(scannerView);
        manager.setHandler(handler);
        manager.setInfoModel(infoModel);
    }

    //= OnPause() ==================================================================================

    /**
     * onPause()
     *
     * verify if scanner.pause is called
     *
     * @throws Exception
     */
    @Test
    public void testOnPause() throws Exception {
        manager.onPause();
        verify(scannerView).pauseScanning();
    }

    //= OnResume() =================================================================================

    /**
     * onResume()
     *
     * verify if scanner.resume is called
     *
     * @throws Exception
     */
    @Test
    public void testOnResume() throws Exception {
        ErrorManager errManager = Mockito.mock(ErrorManager.class);
        TCPClient tcpClient     = Mockito.mock(TCPClient.class);
        ExternalDbLoader.setTcpClient(tcpClient);

        manager.onResume(errManager);
        verify(scannerView).resumeScanning();
    }

    //= OnDestroy() ================================================================================

    /**
     * onDestroy()
     *
     * verify if timer is stopped after destroy
     *
     * @throws Exception
     */
    @Test
    public void testOnDestroy() throws Exception {
        manager.onDestroy();
        verify(handler).removeCallbacks(any(Runnable.class));
    }

    //= OnScanForCollection() ======================================================================

    /**
     * onScanForCollection()
     *
     *
     *
     * @throws Exception
     */
    @Test
    public void testOnScanForCollection_withPositiveResult() throws Exception {
        doNothing().when(infoModel).bundleCollection("PAPER ABCD");

        manager.onScanForCollection("PAPER ABCD");

        verify(scannerView).pauseScanning();
        verify(handler).postDelayed(any(Runnable.class), anyInt());

        verify(scannerView, never()).displayError(any(ProcessException.class));
        verify(scannerView, never()).resumeScanning();
    }

    @Test
    public void testOnScanForCollection_withNegativeToast() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, 1);

        doThrow(err).when(infoModel).bundleCollection("PAPER ABCD");

        manager.onScanForCollection("PAPER ABCD");

        verify(scannerView).pauseScanning();
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());

        verify(scannerView).displayError(err);
        verify(scannerView).resumeScanning();
    }

    @Test
    public void testOnScanForCollection_withNegativeDialog() throws Exception {
        ProcessException err = new ProcessException("ERROR", ProcessException.MESSAGE_DIALOG, 1);
        doThrow(err).when(infoModel).bundleCollection("PAPER ABCD");

        manager.onScanForCollection("PAPER ABCD");

        verify(scannerView).pauseScanning();
        verify(handler, never()).postDelayed(any(Runnable.class), anyInt());

        verify(scannerView).displayError(err);
        verify(scannerView, never()).resumeScanning();
    }
}