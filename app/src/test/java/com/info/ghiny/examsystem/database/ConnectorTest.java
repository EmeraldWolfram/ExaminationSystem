package com.info.ghiny.examsystem.database;

import org.junit.Test;

import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 19/08/2016.
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
        assertEquals("$IN-CHARGE:127.0.0.1:6666:DUEL:$", connector.toString());
    }
}