package com.info.ghiny.examsystem;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 16/05/2016.
 */
public class IdentityTest {

    @Test
    public void testMatchPasswordGivenSameIdShouldReturnTrue() throws Exception {
        Identity actualId   = new Identity("15WAU09184", "0000", true, "FOONG");

        boolean returnItem = actualId.matchPassword("0000");
        assertTrue(returnItem);
    }

    @Test
    public void testMatchPasswordGivenDiffIdShouldReturnFalse() throws Exception {
        Identity actualId   = new Identity("15WAU09184", "0000", true, "FOONG");

        boolean returnItem = actualId.matchPassword("0001");
        assertFalse(returnItem);
    }

    @Test
    public void testMatchPasswordGivenDiffPassShouldReturnFalse() throws Exception {
        Identity actualId   = new Identity("15WAU09184", "0000", true, "FOONG");

        boolean returnItem = actualId.matchPassword(null);
        assertFalse(returnItem);
    }

}