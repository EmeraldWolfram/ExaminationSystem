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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import querylist.AttdList;
/**
 *
 * @author Krissy
 */
@RunWith(MockitoJUnitRunner.class)
public class test {
  
    @Test
    public void testUserVerify1() {
        boolean match = false;
        try {
            match = new ServerComm().staffVerify("staff1","123456");
        } catch (SQLException ex) {
            System.out.print("Lost connection");
        }
        
        assertEquals(true,match);
    }
    
    @Test
    public void testUserVerify2() {
        boolean match = false;
        try {
            match = new ServerComm().staffVerify("staff2","1234");
        } catch (SQLException ex) {
            System.out.print("Lost connection");
        }
        
        assertEquals(true,match);
    }
    
    @Test
    public void testInvalidUserVerify1() {
        boolean match = false;
        try {
            match = new ServerComm().staffVerify("staff1","1234");
        } catch (SQLException ex) {
            System.out.print("Lost connection");
        }
        
        assertEquals(false,match);
    }
    
    @Test
    public void testInvalidUserVerify2() {
        boolean match = false;
        try {
            match = new ServerComm().staffVerify("wasqdf","boy");
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
        when(time.getSession()).thenReturn("AM");
        
        try {
            staff = servercomm.staffGetInfo("staff1");
        } catch (SQLException ex) {
            System.out.print("Lost connection");
        } catch (Exception ex) {
            assertEquals("Invalid data in current session.",ex.getMessage());
        }
        assertEquals("M4",staff.getVenue());
        assertEquals("chief",staff.getStatus());
    }
    
    @Test
    public void testGetStaffInfo2() {
        Staff staff = new Staff();
        when(time.getDate()).thenReturn("11/02/2015");
        when(time.getSession()).thenReturn("AM");
        
        try {
            staff = servercomm.staffGetInfo("staff2");
        } catch (SQLException ex) {
            System.out.print("Lost connection");
        } catch (Exception ex) {
            assertEquals("Invalid data in current session.",ex.getMessage());
        }
        
        assertEquals("M4",staff.getVenue());
        assertEquals("invigilator",staff.getStatus());
    }
    
    @Test
    public void testGetStaffInfo2overTime() {
        Staff staff = new Staff();
        when(time.getDate()).thenReturn("11/02/2015");
        when(time.getSession()).thenReturn("PM");
        
        try {
            staff = servercomm.staffGetInfo("staff2");
        } catch (SQLException ex) {
            System.out.print("Lost connection");
        } catch (Exception ex) {
            assertEquals("Invalid data in current session.",ex.getMessage());
        }

        assertEquals(null,staff.getVenue());
        assertEquals(null,staff.getStatus());
    }
    
    @Test
    public void testGetStaffInfo2overTime2() {
        Staff staff = new Staff();
        when(time.getDate()).thenReturn("11/03/2015");
        when(time.getSession()).thenReturn("AM");
        
        try {
            staff = servercomm.staffGetInfo("staff2");
        } catch (SQLException ex) {
            System.out.print("Lost connection");
        } catch (Exception ex) {
            assertEquals("Invalid data in current session.",ex.getMessage());
        }

        assertEquals(null,staff.getVenue());
        assertEquals(null,staff.getStatus());
    }
    
    @Test
    public void testGetAttdList(){
        ArrayList<AttdList> attdList= new ArrayList<>();
        when(time.getDate()).thenReturn("11/02/2015");
        when(time.getSession()).thenReturn("AM");
        
        try {
            attdList = servercomm.getAttdList("M4");
        } catch (SQLException ex) {
            System.out.print(ex.getMessage());
        } catch (Exception ex) {
            assertEquals("Invalid data in current session.",ex.getMessage());
        }
        
        assertEquals(1,attdList.size());
        assertEquals("W1004ADAC",attdList.get(0).getExamId());
        assertEquals("16WAR25342",attdList.get(0).getRegNum());
        assertEquals("Legal",attdList.get(0).getStatus());
        assertEquals("Present",attdList.get(0).getAttendance());
        assertEquals("MPU3123",attdList.get(0).getPaperCode());
        assertEquals("OGC2",attdList.get(0).getProgramme());
    }
    
    @Test
    public void testGetAttdList2(){
        ArrayList<AttdList> attdList= new ArrayList<>();
        when(time.getDate()).thenReturn("10/02/2016");
        when(time.getSession()).thenReturn("PM");
        
        try {
            attdList = servercomm.getAttdList("Q5");
        } catch (SQLException ex) {
            System.out.print(ex.getMessage());
        } catch (Exception ex) {
            assertEquals("Invalid data in current session.",ex.getMessage());
        }
        
        assertEquals(3,attdList.size());
        
    }
    
    @Test
    public void testGetAttdListIsEmpty(){
        ArrayList<AttdList> attdList= new ArrayList<>();
        when(time.getDate()).thenReturn("10/02/2015");
        when(time.getSession()).thenReturn("PM");
        
        try {
            attdList = servercomm.getAttdList("Q5");
        } catch (SQLException ex) {
            System.out.print(ex.getMessage());
        } catch (Exception ex) {
            assertEquals("Invalid data in current session.",ex.getMessage());
        }
        
        assertEquals(0,attdList.size());
        
    }
    
    
    
}
