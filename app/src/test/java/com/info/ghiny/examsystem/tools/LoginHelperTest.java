package com.info.ghiny.examsystem.tools;

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
    StaffIdentity staffId;
    @Before
    public void setUp() throws Exception{
        staffId = new StaffIdentity("12WW", "0123", true, "MR. TEST", "H3");
        PowerMockito.mockStatic(ExternalDbLoader.class);
    }

    //=CheckInvigilator=============================================================================
    //If NULL entered, MESSAGE_TOAST should be thrown
    @Test
    public void testCheckInvigilator_Null_Input_Should_Throw_MESSAGE_TOAST()throws Exception{
        try{
            LoginHelper.checkInvigilator(null);
            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Not an StaffIdentity", err.getErrorMsg());
        }
    }

    //If input StaffIdentity have false "eligible", MESSAGE_TOAST should be thrown
    @Test
    public void testCheckInvigilator_Illegal_ID_Should_Throw_MESSAGE_TOAST()throws Exception{
        try{
            StaffIdentity id = new StaffIdentity("12WW", "0123", false, "MR. TEST", "H3");
            LoginHelper.checkInvigilator(id);
            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Unauthorized Invigilator", err.getErrorMsg());
        }
    }

    //Nothing happen if the ID is legal
    @Test
    public void testCheckInvigilator_Nothing_Happen_if_Legal_ID_input()throws Exception{
        try{
            StaffIdentity id = new StaffIdentity("12WW", "0123", true, "MR. TEST", "H3");
            LoginHelper.checkInvigilator(id);
        } catch (ProcessException err){
            fail("Expect no error but obtained ErrMsg - " + err.getErrorMsg());
        }
    }

    //=CheckInputPassword===========================================================================
    //ERR_NULL_IDENTITY should be thrown if input ID is null
    @Test
    public void testCheckInputPassword_Null_ID_should_throw_MESSAGE_TOAST() throws Exception{
        try{
            LoginHelper.checkInputPassword(null, null);
            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("Input ID is null", err.getErrorMsg());
        }
    }

    //ERR_EMPTY_PASSWORD should be thrown if input pw is null
    @Test
    public void testCheckInputPassword_NULL_PW_should_throw_MESSAGE_TOAST() throws Exception{
        try{
            StaffIdentity id = new StaffIdentity("12WW", "0123", true, "MR. TEST", "H3");

            LoginHelper.checkInputPassword(id, null);

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Please enter a password to proceed", err.getErrorMsg());
        }
    }

    //ERR_EMPTY_PASSWORD should be thrown if input pw is nothing
    @Test
    public void testCheckInputPassword_EMPTY_PW_should_throw_MESSAGE_TOAST() throws Exception{
        try{
            StaffIdentity id = new StaffIdentity("12WW", "0123", true, "MR. TEST", "H3");

            LoginHelper.checkInputPassword(id, "");

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Please enter a password to proceed", err.getErrorMsg());
        }
    }

    //ERR_WRONG_PASSWORD should be thrown if input pw is wrong
    @Test
    public void testCheckInputPassword_Wrong_PW_should_throw_MESSAGE_TOAST() throws Exception{
        try{
            StaffIdentity id= new StaffIdentity("12WW", "0123", true, "MR. TEST", "H3");

            LoginHelper.checkInputPassword(id, "01234");

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Input password is incorrect", err.getErrorMsg());
        }
    }

    //= IdentifyStaff() ============================================================================
    /**
     *  identifyStaff()
     *
     *  when the server return a null
     *  means that the scanned QR was not a Staff ID
     *
     *  Error should be thrown
     */
    @Test
    public void testIdentifyStaff_Null_Input_Should_Throw_MESSAGE_TOAST()throws Exception{
        try{
            when(ExternalDbLoader.getStaffIdentity("ABCD")).thenReturn(null);
            LoginHelper.identifyStaff(null);
            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Not an StaffIdentity", err.getErrorMsg());
        }
    }

    /**
     *  identifyStaff()
     *
     *  when the staff is a valid staff
     *  assign to the static staff
     */
    @Test
    public void testIdentifyStaff()throws Exception{
        try{
            assertNull(LoginHelper.getStaff());

            when(ExternalDbLoader.getStaffIdentity("12WW"))
                    .thenReturn(staffId);
            LoginHelper.identifyStaff("12WW");

            assertEquals(staffId, LoginHelper.getStaff());
        } catch (ProcessException err){
            fail("Expect no error but obtained ErrMsg - " + err.getErrorMsg());
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
            LoginHelper.matchStaffPw(null);
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
            LoginHelper.matchStaffPw(null);

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Please enter a password to proceed", err.getErrorMsg());
        }
    }

    /**
     * matchStaffPw()
     *
     * When the inputPw was not the same as the Staff password
     * MESSAGE_TOAST will be thrown
     */
    //ERR_EMPTY_PASSWORD should be thrown if input pw is nothing
    @Test
    public void testMatchStaffPw_WRONG_PW_should_throw_MESSAGE_TOAST() throws Exception{
        try{
            LoginHelper.setStaff(staffId);
            LoginHelper.matchStaffPw("0124");

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Input password is incorrect", err.getErrorMsg());
        }
    }

}