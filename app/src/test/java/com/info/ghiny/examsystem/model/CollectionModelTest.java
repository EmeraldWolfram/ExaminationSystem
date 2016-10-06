package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.CollectionMVP;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by GhinY on 05/10/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class CollectionModelTest {

    private CollectionModel model;
    private CollectionMVP.PresenterForModel presenterFace;

    @Before
    public void setUp() throws Exception {
        TCPClient tcpClient = Mockito.mock(TCPClient.class);
        ConnectionTask connectionTask   = Mockito.mock(ConnectionTask.class);
        ExternalDbLoader.setConnectionTask(connectionTask);
        ExternalDbLoader.setTcpClient(tcpClient);
        presenterFace   = Mockito.mock(CollectionMVP.PresenterForModel.class);

        model   = new CollectionModel(presenterFace);
    }

    //= BundleCollection(...) ======================================================================

    /**
     * Temporary check
     * @throws Exception
     */
    @Test
    public void bundleCollection() throws Exception {
        try{
            model.bundleCollection("CORRECT FORMAT");
        } catch (ProcessException err){
            fail("No exception expected but thrown " + err.getErrorMsg());
        }
    }

    //= Run() ======================================================================================

    /**
     * run()
     *
     * 1. When ConnectionTask is complete, do nothing
     * 2. When ConnectionTask is not complete, throw an error and the presenter shall handle
     */
    @Test
    public void testRun_ChiefDoRespond() throws Exception {
        ConnectionTask.setCompleteFlag(true);
        model.run();
        verify(presenterFace, never()).onTimesOut(any(ProcessException.class));
    }

    @Test
    public void testRun_ChiefNoRespond() throws Exception {
        ConnectionTask.setCompleteFlag(false);
        model.run();
        verify(presenterFace).onTimesOut(any(ProcessException.class));
    }
}