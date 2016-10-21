package com.info.ghiny.examsystem.database;

import com.info.ghiny.examsystem.model.ProcessException;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 16/05/2016.
 */
public class StaffIdentityTest {
    @Test
    public void testMatchPasswordGivenSameIdShouldReturnTrue() throws Exception {
        StaffIdentity actualId   = new StaffIdentity("15WAU09184", true, "FOONG", "H1");
        actualId.setPassword("0000");

        boolean returnItem = actualId.matchPassword("0000");
        assertTrue(returnItem);
    }

    @Test
    public void testMatchPasswordGivenDiffIdShouldReturnFalse() throws Exception {
        StaffIdentity actualId   = new StaffIdentity("15WAU09184", true, "FOONG", "H1");
        actualId.setPassword("0000");

        boolean returnItem = actualId.matchPassword("0001");
        assertFalse(returnItem);
    }

    @Test
    public void testMatchPasswordGivenDiffPassShouldReturnFalse() throws Exception {
        StaffIdentity actualId   = new StaffIdentity("15WAU09184", true, "FOONG", "H1");
        actualId.setPassword("0000");

        boolean returnItem = actualId.matchPassword(null);
        assertFalse(returnItem);
    }

    @Test
    public void testMatchPasswordGivenNullStorageThrowFatalError() throws Exception {
        try{
            StaffIdentity actualId   = new StaffIdentity("15WAU09184", true, "FOONG", "H1");
            assertFalse(actualId.matchPassword(null));

        } catch (ProcessException err) {
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("Password Null Exception", err.getErrorMsg());
        }
    }
}