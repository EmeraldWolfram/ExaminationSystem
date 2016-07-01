package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.R;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 15/06/2016.
 */
public class ProcessExceptionTest {
    int test = 0;
    public void foo() throws ProcessException {
        throw new ProcessException("Error Message", ProcessException.MESSAGE_TOAST,
                IconManager.WARNING);
    }

    public void foo1() throws ProcessException {
        throw new ProcessException(null, ProcessException.MESSAGE_TOAST, IconManager.WARNING);
    }

    public void changeValue(int value){
        test = value;
    }

    /**
     *  This test experimented on how throw catch is worked in android java
     *  Checking if the error's message, type and icon was correct
     */
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

    /**
     *  Check if a input null message will not return a null upon
     *  calling getErrorMsg()
     */
    @Test
    public void testThrowExceptionWithoutMessage() throws Exception{
        try{
            foo1();
            fail("Expected ERR_NULL_IDENTITY but none thrown");
        }catch(ProcessException e){
            assertEquals(ProcessException.MESSAGE_TOAST, e.getErrorType());
            assertNotNull(e.getErrorMsg());
            assertEquals("", e.getErrorMsg());
            assertEquals(R.drawable.warn_icon, e.getErrorIcon());
        }
    }

    /**
     *  Check if the throwing an Exception with methods
     */
    @Test
    public void testThrowExceptionWithButton() throws Exception{
        try{

            throw new ProcessException("Something",
                    ProcessException.UPDATE_PROMPT, IconManager.MESSAGE){
                @Override
                public void onPositive() {
                    changeValue(20);
                }

                @Override
                public void onNegative() {
                    changeValue(-20);
                }
            };
        } catch (ProcessException err){
            assertEquals("Something", err.getMessage());
            assertEquals(ProcessException.UPDATE_PROMPT, err.getErrorType());
            assertEquals(R.drawable.msg_icon, err.getErrorIcon());

            err.onNeutral();
            assertEquals(0, test);

            err.onPositive();
            assertEquals(20, test);

            err.onNegative();
            assertEquals(-20, test);
        }
    }

    /*************************************************************************
     *  throwException
     *  Call methods without initialization
     *  should do nothing
     *  when the methods called
     *************************************************************************/
    @Test
    public void testThrowException_CallFunctionWithoutButton() throws Exception {
        try {
            throw new ProcessException("Something",
                    ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
        } catch (ProcessException err) {
            assertEquals("Something", err.getMessage());
            assertEquals(ProcessException.MESSAGE_DIALOG, err.getErrorType());
            assertEquals(R.drawable.msg_icon, err.getErrorIcon());

            err.onNeutral();
            assertEquals(0, test);

            err.onPositive();
            assertEquals(0, test);

            err.onNegative();
            assertEquals(0, test);
        }
    }

    /**
     *  throwException()
     *  With different override onPositive() and onNegative()
     *
     *  In the first throw, onPositive change value "test" to 10
     *  In the second throw, onPositive change value "test" to 20
     *
     */
    @Test
    public void testThrowException_with2DifferentMethodsSet() throws Exception{
        ProcessException err1 = new ProcessException("First Exception",
                    ProcessException.UPDATE_PROMPT, IconManager.MESSAGE){
            @Override
            public void onPositive() {
                changeValue(10);
            }

            @Override
            public void onNegative() {
                changeValue(-10);
            }
        };
        ProcessException err2 = new ProcessException("Second Exception",
                ProcessException.UPDATE_PROMPT, IconManager.MESSAGE){
            @Override
            public void onPositive() {
                changeValue(20);
            }

            @Override
            public void onNegative() {
                changeValue(-20);
            }
        };
        err1.onPositive();
        assertEquals(test, 10);

        err1.onNegative();
        assertEquals(test, -10);


        err2.onPositive();
        assertEquals(test, 20);

        err2.onNegative();
        assertEquals(test, -20);
    }
}