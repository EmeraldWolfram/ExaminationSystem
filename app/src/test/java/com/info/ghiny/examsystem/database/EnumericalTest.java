package com.info.ghiny.examsystem.database;


import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Created by user09 on 12/23/2016.
 */

public class EnumericalTest {

    //# Session ####################################################################################
    //= ParseSession ===============================================================================
    @Test
    public void testParseSession() throws Exception{
        Session am = Session.parseSession("AM");
        Session pm = Session.parseSession("PM");
        Session vm = Session.parseSession("VM");
        Session uk = Session.parseSession("XX");

        assertEquals(Session.AM, am);
        assertEquals(Session.PM, pm);
        assertEquals(Session.VM, vm);
        assertEquals(Session.AM, uk);
    }

    @Test
    public void testSessionToString() throws Exception {
        Session am  = Session.AM;
        Session pm  = Session.PM;
        Session vm  = Session.VM;

        assertEquals("AM", am.toString());
        assertEquals("PM", pm.toString());
        assertEquals("VM", vm.toString());
    }

    //# Status #####################################################################################
    //= Parse Status ===============================================================================

    /**
     * parseStatus()
     * Convert a String type of status into Status type
     */
    @Test
    public void testParseStatus() throws Exception{
        Status present      = Status.parseStatus("PRESENT");
        Status absent       = Status.parseStatus("ABSENT");
        Status barred       = Status.parseStatus("BARRED");
        Status exempted     = Status.parseStatus("EXEMPTED");
        Status quarantized  = Status.parseStatus("QUARANTINED");
        Status unknown      = Status.parseStatus("ELIGIBLE");

        assertEquals(Status.PRESENT, present);
        assertEquals(Status.ABSENT, absent);
        assertEquals(Status.BARRED, barred);
        assertEquals(Status.EXEMPTED, exempted);
        assertEquals(Status.QUARANTINED, quarantized);
        assertEquals(Status.ABSENT, unknown);
    }

    @Test
    public void testStatusToString() throws Exception {
        Status present      = Status.PRESENT;
        Status absent       = Status.ABSENT;
        Status barred       = Status.BARRED;
        Status exempted     = Status.EXEMPTED;
        Status quarantized  = Status.QUARANTINED;


        assertEquals("PRESENT", present.toString());
        assertEquals("ABSENT", absent.toString());
        assertEquals("BARRED", barred.toString());
        assertEquals("EXEMPTED", exempted.toString());
        assertEquals("QUARANTINED", quarantized.toString());
    }

    //# Role #######################################################################################
    //= Parse Role ===============================================================================

    /**
     * parseRole(...)
     * Convert a String into Role type
     */
    @Test
    public void testParseRole() throws Exception{
        Role chief      = Role.parseRole("CHIEF");
        Role inCharge   = Role.parseRole("IN_CHARGE");
        Role invigilator= Role.parseRole("INVIGILATOR");
        Role unknown    = Role.parseRole("Unknown");

        assertEquals(Role.CHIEF, chief);
        assertEquals(Role.IN_CHARGE, inCharge);
        assertEquals(Role.INVIGILATOR, invigilator);
        assertEquals(Role.INVIGILATOR, unknown);
    }

    @Test
    public void testRoleToString() throws Exception {
        Role chief      = Role.CHIEF;
        Role inCharge   = Role.IN_CHARGE;
        Role invigilator= Role.INVIGILATOR;

        assertEquals("CHIEF", chief.toString());
        assertEquals("IN_CHARGE", inCharge.toString());
        assertEquals("INVIGILATOR", invigilator.toString());
    }

}
