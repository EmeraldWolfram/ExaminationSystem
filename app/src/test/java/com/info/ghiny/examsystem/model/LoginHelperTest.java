package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by GhinY on 15/06/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class LoginHelperTest {
    private LoginHelper helper;
    private StaffIdentity staffId;
    private CheckListLoader dbLoader;

    @Before
    public void setUp() throws Exception{
        TCPClient.setConnector(null);
        staffId = new StaffIdentity("12WW", true, "MR. TEST", "H3");
        dbLoader    = Mockito.mock(CheckListLoader.class);

        ExternalDbLoader.setConnectionTask(null);
        helper  = new LoginHelper();
    }

    //= MatchStaffPw() =============================================================================
    /**
     * matchStaffPw(String inputPw)
     *
     * thrown FATAL Message if the staff is not declare yet.
     * In fact, this will never happen.
     * Without Staff declaration, will not prompt for pw
     */
    @Test
    public void testMatchStaffPw_Staff_is_null_should_throw_FATAL_MESSAGE() throws Exception{
        try{
            LoginHelper.setStaff(null);
            helper.matchStaffPw(null);
            fail("Expected FATAL_MESSAGE but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("Input ID is null", err.getErrorMsg());
        }
    }

    /**
     * matchStaffPw(String inputPw)
     *
     * when the inputPw was empty
     * MESSAGE_TOAST will be thrown
     */
    @Test
    public void testMatchStaffPw_NULL_PW_should_throw_MESSAGE_TOAST() throws Exception{
        try{
            LoginHelper.setStaff(staffId);
            helper.matchStaffPw(null);

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Please enter a password to proceed", err.getErrorMsg());
        }
    }

    /**
     * matchStaffPw(String inputPw)
     *
     * when the inputPw was empty
     * MESSAGE_TOAST will be thrown
     */
    @Test
    public void testMatchStaffPw_EMPTY_PW_should_throw_MESSAGE_TOAST() throws Exception{
        try{
            LoginHelper.setStaff(staffId);
            helper.matchStaffPw("");

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Please enter a password to proceed", err.getErrorMsg());
        }
    }

    /**
     * matchStaffPw(String inputPw)
     *
     * when the inputPw was empty
     * MESSAGE_TOAST will be thrown
     */
    @Test
    public void testMatchStaffPw_Valid_PW_should_send_message() throws Exception{
        try{
            LoginHelper.setStaff(staffId);
            helper.matchStaffPw("ABCD");

            //assertEquals("Please enter a password to proceed", err.getErrorMsg());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    //= checkQrId() ================================================================================
    /**
     * checkQrId(scanStr)
     *
     * This method used to check an input string (scanned-in) whether it was possible to
     * be an ID
     *
     * 1. throw MESSAGE_TOAST if the QR scanned is not possible to be an ID
     * 2. do nothing if the scanned string could be an ID
     */
    @Test
    public void testCheckQrId_throw_MESSAGE_TOAST() throws Exception{
        try{
            helper.checkQrId("My name");
            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Invalid staff ID Number", err.getErrorMsg());
        }
    }

    @Test
    public void testCheckQrId_Nothing_Happen() throws Exception {
        try{
            helper.checkQrId("012345");
        } catch (ProcessException err) {
            fail("Expected nothing but thrown " + err.getErrorMsg());
        }
    }
}