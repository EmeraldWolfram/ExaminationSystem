package com.info.ghiny.examsystem.tools;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 15/06/2016.
 */
public class CustomExceptionTest {

    public void foo() throws CustomException{
        throw new CustomException("Error Message", CustomException.ERR_NOT_IDENTITY);
    }

    @Test
    public void testThrowException() throws Exception {
        try{
            foo();
        } catch(CustomException e){
            assertEquals("Error Message",e.getErrorMsg());
            assertEquals(CustomException.ERR_NOT_IDENTITY, e.getErrorCode());
        }
    }
}