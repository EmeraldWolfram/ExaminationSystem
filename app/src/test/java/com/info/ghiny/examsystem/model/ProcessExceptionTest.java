package com.info.ghiny.examsystem.model;

import android.content.DialogInterface;

import com.info.ghiny.examsystem.R;

import org.junit.Before;
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
        throw new ProcessException(null, ProcessException.MESSAGE_TOAST, IconManager.WARNING);
    }

    DialogInterface.OnClickListener testListener1;
    DialogInterface.OnClickListener testListener2;
    DialogInterface.OnClickListener testListener3;
    DialogInterface.OnClickListener testListener4;

    @Before
    public void setUp() throws Exception{
        testListener1 = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        };

        testListener2 = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        };

        testListener3 = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        };

        testListener4 = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        };
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
            ProcessException err = new ProcessException("Something",
                    ProcessException.UPDATE_PROMPT, IconManager.MESSAGE);

            err.setListener(ProcessException.okayButton, testListener1);
            err.setListener(ProcessException.cancelButton, testListener2);
            throw err;
        } catch (ProcessException err){
            assertEquals("Something", err.getMessage());
            assertEquals(ProcessException.UPDATE_PROMPT, err.getErrorType());
            assertEquals(R.drawable.msg_icon, err.getErrorIcon());
            assertEquals(testListener1, err.getListener(ProcessException.okayButton));
            assertEquals(testListener2, err.getListener(ProcessException.cancelButton));
        }
    }

    /*************************************************************************
     *  throwException
     *  Call methods without initialization
     *  should do nothing
     *  should not be null
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
            assertNull(err.getListener("Whatever"));
    }
}

    /**
     *  throwException()
     *  With different methods
     *
     *  The first throw and second throw should not overlap
     */
    @Test
    public void testThrowException_with2DifferentMethodsSet() throws Exception{
        ProcessException err1 = new ProcessException("First Exception",
                    ProcessException.UPDATE_PROMPT, IconManager.MESSAGE);
        err1.setListener(ProcessException.updateButton, testListener1);
        err1.setListener(ProcessException.cancelButton, testListener2);
        ProcessException err2 = new ProcessException("Second Exception",
                ProcessException.UPDATE_PROMPT, IconManager.MESSAGE);
        err2.setListener(ProcessException.updateButton, testListener3);
        err2.setListener(ProcessException.cancelButton, testListener4);

        assertEquals(testListener1, err1.getListener(ProcessException.updateButton));
        assertEquals(testListener2, err1.getListener(ProcessException.cancelButton));
        assertEquals(testListener3, err2.getListener(ProcessException.updateButton));
        assertEquals(testListener4, err2.getListener(ProcessException.cancelButton));

    }
}