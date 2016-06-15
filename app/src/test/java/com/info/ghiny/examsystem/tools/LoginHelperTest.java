package com.info.ghiny.examsystem.tools;

import android.support.v4.widget.SimpleCursorAdapter;

import com.info.ghiny.examsystem.database.Identity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 15/06/2016.
 */
public class LoginHelperTest {

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

    @Test
    public void testCheckInvigilator_Nothing_Happen_if_Legal_ID_input()throws Exception{
        try{
            Identity id = new Identity("12WW", "0123", true, "MR. TEST");
            LoginHelper.checkInvigilator(id);
        } catch (CustomException err){
            fail("Expect no error but obtained ErrMsg - " + err.getErrorMsg());
        }
    }



}