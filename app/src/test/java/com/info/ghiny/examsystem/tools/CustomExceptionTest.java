package com.info.ghiny.examsystem.tools;

import android.graphics.drawable.Icon;

import com.info.ghiny.examsystem.R;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 15/06/2016.
 */
public class CustomExceptionTest {

    public void foo() throws CustomException{
        throw new CustomException("Error Message", CustomException.ERR_NULL_IDENTITY,
                IconManager.WARNING);
    }

    public void foo1() throws CustomException{
        throw new CustomException(CustomException.ERR_NULL_IDENTITY);
    }

    @Test
    public void testThrowException() throws Exception {
        try{
            foo();
            fail("Expected ERR_NULL_IDENTITY but none thrown");
        } catch(CustomException e){
            assertEquals("Error Message",e.getErrorMsg());
            assertEquals(CustomException.ERR_NULL_IDENTITY, e.getErrorCode());
            assertEquals(R.drawable.warn_icon, e.getErrorIcon());
        }
    }

    @Test
    public void testThrowExceptionWithoutMessage() throws Exception{
        try{
            foo1();
            fail("Expected ERR_NULL_IDENTITY but none thrown");
        }catch(CustomException e){
            assertEquals(CustomException.ERR_NULL_IDENTITY, e.getErrorCode());
            assertNull(e.getMessage());
            assertEquals(R.drawable.warn_icon, e.getErrorIcon());
        }
    }
}