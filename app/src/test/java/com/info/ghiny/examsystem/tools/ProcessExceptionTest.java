package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.R;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 15/06/2016.
 */
public class ProcessExceptionTest {

    public void foo() throws ProcessException {
        throw new ProcessException("Error Message", ProcessException.MESSAGE_TOAST,
                IconManager.WARNING);
    }

    public void foo1() throws ProcessException {
        throw new ProcessException(ProcessException.MESSAGE_TOAST);
    }

    @Test
    public void testThrowException() throws Exception {
        try{
            foo();
            fail("Expected MESSAGE_TOAST but none thrown");
        } catch(ProcessException e){
            assertEquals("Error Message",e.getErrorMsg());
            assertEquals(ProcessException.MESSAGE_TOAST, e.getErrorType());
            assertEquals(R.drawable.warn_icon, e.getErrorIcon());
        }
    }

    @Test
    public void testThrowExceptionWithoutMessage() throws Exception{
        try{
            foo1();
            fail("Expected ERR_NULL_IDENTITY but none thrown");
        }catch(ProcessException e){
            assertEquals(ProcessException.MESSAGE_TOAST, e.getErrorType());
            assertNull(e.getMessage());
            assertEquals(R.drawable.warn_icon, e.getErrorIcon());
        }
    }
}