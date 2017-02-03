package com.info.ghiny.examsystem.database;

import org.junit.Test;

import java.util.Calendar;
import java.util.Locale;

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

public class ConnectorTest {

    @Test
    public void testGetDateInString() throws Exception {
        Connector connector = new Connector("127.0.0.1", 6666, "DUEL");

        int day     = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int month   = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int year    = Calendar.getInstance().get(Calendar.YEAR);

        assertEquals(String.format(Locale.ENGLISH, "%d:%d:%d", day, month, year),
                connector.getDateInString());
    }

    @Test
    public void testParseStringToDate() throws Exception {
        Connector connector = new Connector("127.0.0.1", 6666, "DUEL");
        Calendar date   = connector.parseStringToDate("5:9:2016");

        assertEquals(5, date.get(Calendar.DAY_OF_MONTH));
        assertEquals(8, date.get(Calendar.MONTH));
        assertEquals(2016, date.get(Calendar.YEAR));
    }

    @Test
    public void testParseStringToDate_NoColon() throws Exception {
        Connector connector = new Connector("127.0.0.1", 6666, "DUEL");
        Calendar date   = connector.parseStringToDate("592016");

        assertNull(date);
    }

    @Test
    public void testParseStringToDate_WithCharacter() throws Exception {
        Connector connector = new Connector("127.0.0.1", 6666, "DUEL");
        Calendar date   = connector.parseStringToDate("6:8A:2016");

        assertNull(date);
    }

    @Test
    public void testToString() throws Exception{
        Connector connector = new Connector("127.0.0.1", 6666, "DUEL");
        assertEquals("$IN_CHARGE:127.0.0.1:6666:DUEL:$", connector.toString());
    }
}