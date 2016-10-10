package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.Session;
import com.info.ghiny.examsystem.interfacer.InfoGrabMVP;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


/**
 * Created by GhinY on 01/07/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class InfoGrabModelTest {
    private InfoGrabMVP.MPresenter taskPresenter;
    private InfoGrabModel model;
    private TCPClient tcpClient;

    @Before
    public void setUp() throws Exception {
        tcpClient = Mockito.mock(TCPClient.class);
        ConnectionTask connectionTask   = Mockito.mock(ConnectionTask.class);
        ExternalDbLoader.setConnectionTask(connectionTask);
        ExternalDbLoader.setTcpClient(tcpClient);

        taskPresenter   = Mockito.mock(InfoGrabMVP.MPresenter.class);

        model = new InfoGrabModel(taskPresenter);
    }
    //= ReqCandidatePapers =========================================================================
    /**
     *  reqCandidatePapers()
     *
     *  When the candidate was not found
     *  MESSAGE_TOAST will be thrown
     */
    @Test
    public void testReqCandidatePapers_Throw_Error_input_string_size_not_10() throws Exception {
        try{
            model.reqCandidatePapers("15");
            fail("Expected MESSAGE_TOAST but nothing was thrown");
        } catch (ProcessException err){
            assertEquals("Not a candidate ID", err.getErrorMsg());
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());

            verify(tcpClient, never()).sendMessage(anyString());
        }
    }

    /**
     *  reqCandidatePapers()
     *
     *  When the candidate was found, request for paper will be sent to Chief
     */
    @Test
    public void testReqCandidatePapers_ValidCandidate() throws Exception {
        try{
            model.reqCandidatePapers("15WAU00001");

            verify(tcpClient).sendMessage(anyString());
        } catch (ProcessException err){
            fail("No Exception expected but " +  err.getErrorMsg() + " was thrown");
        }
    }

    //= GetDaysLeft ================================================================================
    /**
     * getDaysLeft()
     *
     * return -1 when the Date of the paper to be examine is already past
     *
     * 1st of July --> today
     */
    @Test
    public void testGetDaysLeft_PastExam() throws Exception {
        Calendar paperDate = Calendar.getInstance();
        paperDate.set(2016, 6, 1);

        Integer dayLeft = InfoGrabModel.getDaysLeft(paperDate);

        assertEquals(-1, dayLeft.intValue());
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
        verify(taskPresenter, never()).onTimesOut(any(ProcessException.class));
    }

    @Test
    public void testRun_ChiefNoRespond() throws Exception {
        ConnectionTask.setCompleteFlag(false);
        model.run();
        verify(taskPresenter).onTimesOut(any(ProcessException.class));
    }
}