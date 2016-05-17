package com.info.ghiny.examsystem;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 16/05/2016.
 */
public class IdentityTest {

    @Test
    public void testMatchPasswordGivenSameIdShouldReturnTrue() throws Exception {
        Identity actualId   = new Identity("NG YEN AENG", "0000", true);
        Identity testId     = new Identity("NG YEN AENG", "0000", true);

        boolean returnItem = actualId.matchPassword(testId);
        assertTrue(returnItem);
    }

    @Test
    public void testMatchPasswordGivenDiffIdShouldReturnFalse() throws Exception {
        Identity actualId   = new Identity("NG YEN AENG", "0000", true);
        Identity testId     = new Identity("NG YON AENG", "0000", true);

        boolean returnItem = actualId.matchPassword(testId);
        assertFalse(returnItem);
    }

    @Test
    public void testMatchPasswordGivenDiffPassShouldReturnFalse() throws Exception {
        Identity actualId   = new Identity("NG YEN AENG", "QQ20,.,.", true);
        Identity testId     = new Identity("NG YEN AENG", "QQ21,.,.", true);

        boolean returnItem = actualId.matchPassword(testId);
        assertFalse(returnItem);
    }

}