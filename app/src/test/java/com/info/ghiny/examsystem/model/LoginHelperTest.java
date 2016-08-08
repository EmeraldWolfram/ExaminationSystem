package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by GhinY on 15/06/2016.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ExternalDbLoader.class)
public class LoginHelperTest {
    LoginHelper helper;
    StaffIdentity staffId;

    @Before
    public void setUp() throws Exception{
        TCPClient.setServerIp(null);
        TCPClient.setServerPort(0);
        staffId = new StaffIdentity("12WW", true, "MR. TEST", "H3");
        PowerMockito.mockStatic(ExternalDbLoader.class);

        helper  = new LoginHelper();
    }
    //= VerifyChief() ==============================================================================
    /**
     *  verifyChief()
     *
     *  when input String was correct Chief Address format
     *  TCP Client ServerIP and ServerPort will be set
     */
    @Test
    public void testVerifyChief_If_correct_String_format() throws Exception{
        try{
            assertNull(TCPClient.SERVERIP);
            assertEquals(0, TCPClient.SERVERPORT);

            String str = "$CHIEF:192.168.0.1:5000:$";
            helper.verifyChief(str);

            assertEquals("192.168.0.1", TCPClient.SERVERIP);
            assertEquals(5000, TCPClient.SERVERPORT);
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    /**
     *  verifyChief()
     *
     *  when input String was incorrect Chief Address format
     *  MESSAGE TOAST shall be thrown
     */
    @Test
    public void testVerifyChief_If_wrong_String_format() throws Exception{
        try{
            assertNull(TCPClient.SERVERIP);
            assertEquals(0, TCPClient.SERVERPORT);

            String str = "$CHIEF:192.168.0.1:5000:";
            helper.verifyChief(str);

            fail("Expected MESSAFE TOAST Exception but none were thrown");
        } catch (ProcessException err){
            assertNull(TCPClient.SERVERIP);
            assertEquals(0, TCPClient.SERVERPORT);
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Not a chief address", err.getErrorMsg());
        }
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