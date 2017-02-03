package com.info.ghiny.examsystem.database;

import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.JavaHost;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class StaffIdentityTest {
    private StaffIdentity id;

    @Before
    public void setup() throws Exception{
        id  = new StaffIdentity();
        JavaHost.setConnector(new Connector("Address", 7032, "DUEL"));

    }

    //= HmacSha(...) ============================================================================

    /**
     * hmacSha(...)
     *
     * This method use take two input String and encrypt the 2nd String using the 1st String
     * and return a encrypted String
     *
     * Tests:
     * 1. When the two input string is totally same, should provide the same HashCode
     * 2. When null password detected during the process of encryption, throw FATAL_MESSAGE
     * 3. When null message detected during the process of encryption, throw FATAL_MESSAGE
     * 4. Encrypted data provided by Server program, to check if the function provide same hash code
     */

    @Test
    public void testHmacSha1_PositiveTest() throws Exception {
        String hash         = id.hmacSha("MyPassword", "7ABB8");
        String sameHash     = id.hmacSha("MyPassword", "7ABB8");
        String diffHash1    = id.hmacSha("MyPassword", "7aBB8"); //A -> a
        String diffHash2    = id.hmacSha("myPassword", "7ABB8"); //M -> m

        System.out.print(hash);

        assertTrue(hash.equals(sameHash));
        assertFalse(hash.equals(diffHash1));
        assertFalse(hash.equals(diffHash2));
    }

    @Test
    public void testHmacSha2_NegativeTest() throws Exception {
        try{
            String hash         = id.hmacSha(null, "7ABB8");

            fail("Expected FATAL_MESSAGE but none thrown!");
        } catch (ProcessException err) {
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("Encryption library not found\n" +
                    "Please contact developer!", err.getErrorMsg());
        }
    }

    @Test
    public void testHmacSha3_NegativeTest() throws Exception {
        try{
            String hash         = id.hmacSha("MyPassword", null);

            fail("Expected FATAL_MESSAGE but none thrown!");
        } catch (ProcessException err) {
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("Encryption library not found\n" +
                    "Please contact developer!", err.getErrorMsg());
        }
    }

    @Test
    public void testHmacSha4_BinderTest() throws Exception {
        String hash = id.hmacSha("exam", "123456");

        assertEquals("JHKKVtD1VGX+G1Is2MSVl3KyNtM06rbgzIE9lHNn0lU=\n", hash);
    }

    //= MatchPassword(...) =========================================================================

    @Test
    public void testMatchPasswordGivenSameIdShouldReturnTrue() throws Exception {
        StaffIdentity actualId   = new StaffIdentity("15WAU09184", true, "FOONG", "H1");
        actualId.setPassword("0000");
        actualId.setHashPass(id.hmacSha("0000", JavaHost.getConnector().getDuelMessage()));

        boolean returnItem = actualId.matchPassword("0000");
        assertTrue(returnItem);
    }

    @Test
    public void testMatchPasswordGivenDiffIdShouldReturnFalse() throws Exception {
        StaffIdentity actualId   = new StaffIdentity("15WAU09184", true, "FOONG", "H1");
        actualId.setPassword("0000");
        actualId.setHashPass(id.hmacSha("0000", JavaHost.getConnector().getDuelMessage()));

        boolean returnItem = actualId.matchPassword("0001");
        assertFalse(returnItem);
    }

    @Test
    public void testMatchPasswordGivenDiffPassShouldReturnFalse() throws Exception {
        StaffIdentity actualId   = new StaffIdentity("15WAU09184", true, "FOONG", "H1");
        actualId.setPassword("0000");
        actualId.setHashPass(id.hmacSha("0000", JavaHost.getConnector().getDuelMessage()));

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
            assertEquals("Password null exception", err.getErrorMsg());
        }
    }

}