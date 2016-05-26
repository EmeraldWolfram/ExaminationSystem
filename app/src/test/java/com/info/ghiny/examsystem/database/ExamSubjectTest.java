package com.info.ghiny.examsystem.database;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.matchers.Null;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 26/05/2016.
 */
public class ExamSubjectTest {

    @Test
    public void testToString_TestFunctionality() throws Exception {
        try{
            ExamSubject testSubject = new ExamSubject();
            testSubject.setPaperCode("BAME 2134");
            testSubject.setPaperDesc("TEST DRIVEN DEVELOPMENT");

            String testStr = testSubject.toString();
            assertNotNull(testStr);
            assertEquals(testStr, "BAME 2134  TEST DRIVEN DEVELOPMENT");
        }catch(NullPointerException e){
            fail("Should not have error thrown");
        }
    }

    @Test
    public void testToString_TestNullExamSubject() throws Exception {
        try{
            ExamSubject testSubject = new ExamSubject();
            String testStr = testSubject.toString();
            fail("NullPointerException should be thrown but no error thrown!");
        }catch (NullPointerException e){
            assertEquals(e.getMessage(), "Paper Code was not filled yet");
        }
    }

    @Test
    public void testToString_TestNullPaperDesc() throws Exception {
        try{
            ExamSubject testSubject = new ExamSubject();
            testSubject.setPaperCode("BAMM 1011");
            String testStr = testSubject.toString();

            fail("NullPointerException should be thrown but no error thrown!");
        }catch (NullPointerException e){
            assertEquals(e.getMessage(), "Paper Description was not filled yet");
        }
    }
}