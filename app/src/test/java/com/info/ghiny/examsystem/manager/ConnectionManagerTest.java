package com.info.ghiny.examsystem.manager;

import com.info.ghiny.examsystem.MainLoginActivity;
import com.info.ghiny.examsystem.interfacer.GeneralView;
import com.info.ghiny.examsystem.model.IconManager;
import com.info.ghiny.examsystem.model.LoginHelper;
import com.info.ghiny.examsystem.model.ProcessException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by GhinY on 11/08/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class ConnectionManagerTest {

    private ConnectionManager manager;
    private GeneralView genView;
    private LoginHelper loginModel;
    private ProcessException err;

    @Before
    public void setUp() throws Exception {
        err = new ProcessException("ERROR", ProcessException.MESSAGE_TOAST, IconManager.WARNING);
        genView     = Mockito.mock(GeneralView.class);
        loginModel  = Mockito.mock(LoginHelper.class);
        manager     = new ConnectionManager(genView);
        manager.setLoginModel(loginModel);
    }

    //= OnScanChief() ==============================================================================
    /**
     * onScanChief(String scanStr)
     *
     * Pass the QR scanned to Model
     * Control the View to navigate to another View when Chief address correct
     * Response according to the result from the Model
     *
     * 1. First test, Model show that result is positive
     * 2. Second test, Model throw error due to incorrect format
     * @throws Exception
     */
    @Test
    public void testOnScanForChief_withPositiveResult() throws Exception {
        doNothing().when(loginModel).verifyChief("$CHIEF:...:$");

        manager.onScanForChief("$CHIEF:...:$");
        verify(genView).navigateActivity(MainLoginActivity.class);
        verify(genView, never()).displayError(err);
    }

    @Test
    public void testOnScanForChief_withErrorThrown() throws Exception {

        doThrow(err).when(loginModel).verifyChief("");

        manager.onScanForChief("");
        verify(genView, never()).navigateActivity(MainLoginActivity.class);
        verify(genView).displayError(err);
    }


}