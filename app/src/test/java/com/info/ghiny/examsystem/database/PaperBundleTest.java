package com.info.ghiny.examsystem.database;

import org.junit.Before;
import org.junit.Test;

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

public class PaperBundleTest {
    @Before
    public void setUp() throws Exception {

    }

    //= ParseBundle(...) ===========================================================================
    /**
     * parseBundle(...)
     *
     * This method take in the string encrypted in the QR code on each bundle to be collected
     * With the String, it check if the QR code is a valid bundle data
     *
     * Tests:
     * 1. When the String is valid, fill up the bundle info and return true
     * 2. String have only 3 field, does not fill up the bundle and return false
     * 2. String have 5 field, does not fill up the bundle and return false
     *
     * @throws Exception
     */

    @Test
    public void testParseBundle1_PositiveTest() throws Exception {
        String inputScanStr = "13452/M4/BAME 3323/RMB3";

        PaperBundle bundle  = new PaperBundle();

        assertTrue(bundle.parseBundle(inputScanStr));

        assertEquals("13452", bundle.getColId());
        assertEquals("M4", bundle.getColVenue());
        assertEquals("BAME 3323", bundle.getColPaperCode());
        assertEquals("RMB3", bundle.getColProgramme());
    }

    @Test
    public void testParseBundle2_NegativeTest() throws Exception {
        String inputScanStr = "SSdd2/M4/BAME 3323";

        PaperBundle bundle  = new PaperBundle();

        assertFalse(bundle.parseBundle(inputScanStr));

        assertNull(bundle.getColId());
        assertNull(bundle.getColVenue());
        assertNull(bundle.getColProgramme());
        assertNull(bundle.getColPaperCode());
    }

    @Test
    public void testParseBundle3_NegativeTest() throws Exception {
        String inputScanStr = "M4/BAME 3323/RMB3/S/D";

        PaperBundle bundle  = new PaperBundle();

        assertFalse(bundle.parseBundle(inputScanStr));

        assertNull(bundle.getColId());
        assertNull(bundle.getColVenue());
        assertNull(bundle.getColProgramme());
        assertNull(bundle.getColPaperCode());
    }

    //= ToString() =================================================================================
    /**
     * toString()
     *
     * This method is redundant at this point but it might be useful in extension
     * It format the bundle into a valid bundle string that was scanned previously
     *
     * Tests:
     * 1. All the field of the bundle was filled, return valid bundle string
     * 2. All the field of the bundle was empty, return empty String "null/null/null"
     */
    @Test
    public void testToString1_PositiveTest() throws Exception {
        PaperBundle bundle  = new PaperBundle();
        assertTrue(bundle.parseBundle("13452/M4/BAME 3333/RMB3"));
        assertEquals("13452/M4/BAME 3333/RMB3", bundle.toString());
    }

    @Test
    public void testToString2_NegativeTest() throws Exception {
        PaperBundle bundle  = new PaperBundle();
        assertEquals("null/null/null/null", bundle.toString());
    }
}