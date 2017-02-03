package com.info.ghiny.examsystem.database;

import com.info.ghiny.examsystem.model.ProcessException;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Copyright (C) 2016 - 2017 Steven Foong Ghin Yew <stevenfgy@yahoo.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

public class ExamSubjectTest {

    //=toString()===================================================================================
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
        }catch (Exception e){
            assertEquals(NullPointerException.class, e.getClass());
            assertEquals(e.getMessage(), "Paper Description was not filled yet");
        }
    }

    //=isValidTable=================================================================================
    //Table range from 12 -> 31 (20 ppl) test Lower Limit
    //input 11 should return false to indicate not a valid table
    @Test
    public void testIsValidTable_Input_number_out_of_range_should_return_false() throws Exception{
        try{
            ExamSubject testSubject = new ExamSubject("BAME0001", "SUBJECT 1", 12,
                    Calendar.getInstance(), 20, "H1", Session.AM);
            assertFalse(testSubject.isValidTable(11));
            assertTrue(testSubject.isValidTable(12));
        }catch(Exception err){
            fail("Did not expect error to be thrown but thrown ErrorMsg " + err.getMessage() );
        }
    }

    //Table range from 12 -> 31 (20 ppl), test Upper Limit
    //input 11 should return false to indicate not a valid table
    @Test
    public void testIsValidTable_Input_number_12_should_return_true() throws Exception{
        try{
            ExamSubject testSubject = new ExamSubject("BAME0001", "SUBJECT 1", 12,
                    Calendar.getInstance(), 20, "H1", Session.AM);
            assertTrue(testSubject.isValidTable(31));
            assertFalse(testSubject.isValidTable(32));
        }catch(Exception err){
            fail("Did not expect error to be thrown but thrown ErrorMsg " + err.getMessage() );
        }
    }

    //Input to isValidTable() cannot be null
    //If null entry happen should throw MESSAGE_DIALOG
    @Test
    public void testIsValidTable_Input_null_should_throw_MESSAGE_DIALOG() throws Exception{
        try{
            ExamSubject testSubject = new ExamSubject("BAME0001", "SUBJECT 1", 12,
                    Calendar.getInstance(), 20, "H1", Session.AM);
            boolean testLogic = testSubject.isValidTable(null);
            fail("Expected ERR_NULL_TABLE but none thrown");
        }catch(ProcessException err){
            assertEquals(ProcessException.MESSAGE_DIALOG, err.getErrorType());
            assertEquals("Input tableNumber is null", err.getErrorMsg());
        }
    }

    //= ParseStringToDate(...) =====================================================================

    @Test
    public void testParseStringToDate_PositiveTest() throws Exception {
        Calendar date   = ExamSubject.parseStringToDate("5/9/2016");

        assertEquals(5, date.get(Calendar.DAY_OF_MONTH));
        assertEquals(8, date.get(Calendar.MONTH));
        assertEquals(2016, date.get(Calendar.YEAR));
    }

    @Test
    public void testParseStringToDate1_NoSlashNegativeTest() throws Exception {
        Calendar date   = ExamSubject.parseStringToDate("592016");

        assertNull(date);
    }

    @Test
    public void testParseStringToDate2_NonDigitNegativeTest() throws Exception {
        Calendar date   = ExamSubject.parseStringToDate("6/8A/2016");

        assertNull(date);
    }
}