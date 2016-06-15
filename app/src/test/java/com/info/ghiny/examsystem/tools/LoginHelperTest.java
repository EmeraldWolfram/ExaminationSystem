package com.info.ghiny.examsystem.tools;

import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;

import com.info.ghiny.examsystem.database.Identity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 15/06/2016.
 */
public class LoginHelperTest {

    //=CheckInvigilator=============================================================================
    //If NULL entered, ERR_NULL_IDENTITY should be thrown
    @Test
    public void testCheckInvigilator_Null_Input_Should_Throw_ERR_NULL_IDENTITY()throws Exception{
        try{
            LoginHelper.checkInvigilator(null);
            fail("Expected ERR_NULL_IDENTITY but none thrown");
        } catch (CustomException err){
            assertEquals(CustomException.ERR_NULL_IDENTITY, err.getErrorCode());
            assertEquals("ID is null", err.getErrorMsg());
        }
    }

    //If input Identity have false "eligible", ERR_ILLEGAL_IDENTITY should be thrown
    @Test
    public void testCheckInvigilator_Illegal_ID_Should_Throw_ERR_ILLEGAL_IDENTITY()throws Exception{
        try{
            Identity id = new Identity("12WW", "0123", false, "MR. TEST");
            LoginHelper.checkInvigilator(id);
            fail("Expected ERR_ILLEGAL_IDENTITY but none thrown");
        } catch (CustomException err){
            assertEquals(CustomException.ERR_ILLEGAL_IDENTITY, err.getErrorCode());
            assertEquals("ID not eligible", err.getErrorMsg());
        }
    }

    //Nothing happen if the ID is legal
    @Test
    public void testCheckInvigilator_Nothing_Happen_if_Legal_ID_input()throws Exception{
        try{
            Identity id = new Identity("12WW", "0123", true, "MR. TEST");
            LoginHelper.checkInvigilator(id);
        } catch (CustomException err){
            fail("Expect no error but obtained ErrMsg - " + err.getErrorMsg());
        }
    }

    //=CheckInputPassword===========================================================================
    //ERR_NULL_IDENTITY should be thrown if input ID is null
    @Test
    public void testCheckInputPassword_Null_ID_should_throw_ERR_NULL_IDENTITY() throws Exception{
        try{
            LoginHelper.checkInputPassword(null, null);
            fail("Expected ERR_NULL_IDENTITY but none thrown");
        } catch (CustomException err){
            assertEquals(CustomException.ERR_NULL_IDENTITY, err.getErrorCode());
            assertEquals("Input ID is null", err.getErrorMsg());
        }
    }

    //ERR_EMPTY_PASSWORD should be thrown if input pw is null
    @Test
    public void testCheckInputPassword_NULL_PW_should_throw_ERR_EMPTY_PASSWORD() throws Exception{
        try{
            Identity id = new Identity("12WW", "0123", true, "MR. TEST");

            LoginHelper.checkInputPassword(id, null);

            fail("Expected ERR_EMPTY_PASSWORD but none thrown");
        } catch (CustomException err){
            assertEquals(CustomException.ERR_EMPTY_PASSWORD, err.getErrorCode());
            assertEquals("Input pw empty", err.getErrorMsg());
        }
    }

    //ERR_EMPTY_PASSWORD should be thrown if input pw is nothing
    @Test
    public void testCheckInputPassword_EMPTY_PW_should_throw_ERR_EMPTY_PASSWORD() throws Exception{
        try{
            Identity id = new Identity("12WW", "0123", true, "MR. TEST");

            LoginHelper.checkInputPassword(id, "");

            fail("Expected ERR_EMPTY_PASSWORD but none thrown");
        } catch (CustomException err){
            assertEquals(CustomException.ERR_EMPTY_PASSWORD, err.getErrorCode());
            assertEquals("Input pw empty", err.getErrorMsg());
        }
    }

    //ERR_WRONG_PASSWORD should be thrown if input pw is wrong
    @Test
    public void testCheckInputPassword_Wrong_PW_should_throw_ERR_WRONG_PASSWORD() throws Exception{
        try{
            Identity id = new Identity("12WW", "0123", true, "MR. TEST");

            LoginHelper.checkInputPassword(id, "01234");

            fail("Expected ERR_WRONG_PASSWORD but none thrown");
        } catch (CustomException err){
            assertEquals(CustomException.ERR_WRONG_PASSWORD, err.getErrorCode());
            assertEquals("Input pw was wrong", err.getErrorMsg());
        }
    }
}