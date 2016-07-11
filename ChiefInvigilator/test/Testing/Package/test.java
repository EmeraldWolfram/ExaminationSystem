/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing.Package;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import chiefinvigilator.CurrentTime;
import chiefinvigilator.ServerComm;
import chiefinvigilator.Staff;
/**
 *
 * @author Krissy
 */
@RunWith(MockitoJUnitRunner.class)
public class test {
  
    @Test
    public void testUserLogin1() {
        boolean match = false;
        try {
            match = new ServerComm().staffLogin("staff1","123456");
        } catch (SQLException ex) {
            System.out.print("Lost connection");
        }
        
        assertEquals(true,match);
    }
    
    @Test
    public void testUserLogin2() {
        boolean match = false;
        try {
            match = new ServerComm().staffLogin("staff2","1234");
        } catch (SQLException ex) {
            System.out.print("Lost connection");
        }
        
        assertEquals(true,match);
    }
    
    @Test
    public void testInvalidUserLogin1() {
        boolean match = false;
        try {
            match = new ServerComm().staffLogin("staff1","1234");
        } catch (SQLException ex) {
            System.out.print("Lost connection");
        }
        
        assertEquals(false,match);
    }
    
    @Test
    public void testInvalidUserLogin2() {
        boolean match = false;
        try {
            match = new ServerComm().staffLogin("wasqdf","boy");
        } catch (SQLException ex) {
            System.out.print("Lost connection");
        }
        
        assertEquals(false,match);
    }
    
    @InjectMocks
    ServerComm servercomm = new ServerComm();
    
    @Mock
    CurrentTime time;
            
    @Test
    public void testGetStaffInfo() {
        Staff staff = new Staff();
        when(time.getDate()).thenReturn("11/02/2015");
        when(time.getTime()).thenReturn("080000");
        
        try {
            staff = servercomm.staffGetInfo("staff1");
        } catch (SQLException ex) {
            System.out.print("Lost connection");
        }
        assertEquals("M4",staff.getVenue());
        assertEquals("chief",staff.getStatus());
    }
    
    @Test
    public void testGetStaffInfo2() {
        Staff staff = new Staff();
        when(time.getDate()).thenReturn("11/02/2015");
        when(time.getTime()).thenReturn("091234");
        
        try {
            staff = servercomm.staffGetInfo("staff2");
        } catch (SQLException ex) {
            System.out.print("Lost connection");
        }
        
        assertEquals("M4",staff.getVenue());
        assertEquals("invigilator",staff.getStatus());
    }
    
    @Test
    public void testGetStaffInfo2overTime() {
        Staff staff = new Staff();
        when(time.getDate()).thenReturn("11/02/2015");
        when(time.getTime()).thenReturn("110957");
        
        try {
            staff = servercomm.staffGetInfo("staff2");
        } catch (SQLException ex) {
            System.out.print("Lost connection");
        }

        assertEquals(null,staff.getVenue());
        assertEquals(null,staff.getStatus());
    }
    
    @Test
    public void testGetStaffInfo2overTime2() {
        Staff staff = new Staff();
        when(time.getDate()).thenReturn("11/03/2015");
        when(time.getTime()).thenReturn("090000");
        
        try {
            staff = servercomm.staffGetInfo("staff2");
        } catch (SQLException ex) {
            System.out.print("Lost connection");
        }

        assertEquals(null,staff.getVenue());
        assertEquals(null,staff.getStatus());
    }
}
