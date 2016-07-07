package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.database.StaffIdentity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 15/06/2016.
 */
public class LoginHelperTest {

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
            StaffIdentity id = new StaffIdentity("12WW", "0123", false, "MR. TEST");
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
            StaffIdentity id = new StaffIdentity("12WW", "0123", true, "MR. TEST");
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
            StaffIdentity id = new StaffIdentity("12WW", "0123", true, "MR. TEST");

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
            StaffIdentity id = new StaffIdentity("12WW", "0123", true, "MR. TEST");

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
            StaffIdentity id = new StaffIdentity("12WW", "0123", true, "MR. TEST");

            LoginHelper.checkInputPassword(id, "01234");

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Input password is incorrect", err.getErrorMsg());
        }
    }
}