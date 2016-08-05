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
import querylist.CddPapers;
import querylist.Papers;
/**
 *
 * @author Krissy
 */
@RunWith(MockitoJUnitRunner.class)
public class test {
  
    @Test
    public void testUserVerify1() {
        Staff staff = new Staff("staff1","123456");
        boolean match = false;
        try {
            match = staff.staffVerify();
        } catch (SQLException ex) {
            System.out.print(ex.getMessage());
        }
        
        assertEquals(true,match);
    }
    
    @Test
    public void testUserVerify2() {
        Staff staff = new Staff("staff2","1234");
        boolean match = false;
        try {
            match = staff.staffVerify();
        } catch (SQLException ex) {
            System.out.print(ex.getMessage());
        }
        
        assertEquals(true,match);
    }
    
    @Test
    public void testInvalidUserVerify1() {
        Staff staff = new Staff("staff2","123456");
        boolean match = false;
        try {
            match = staff.staffVerify();
        } catch (SQLException ex) {
            System.out.print(ex.getMessage());
        }
        
        assertEquals(false,match);
    }
    
    @Test
    public void testInvalidUserVerify2() {
        Staff staff = new Staff("wasqdf","boy");
        boolean match = false;
        try {
            match = staff.staffVerify();
        } catch (SQLException ex) {
            System.out.print(ex.getMessage());
        }
        
        assertEquals(false,match);
    }
    
    @InjectMocks
    ServerComm servercomm = new ServerComm();
    
    @Mock
    CurrentTime time;
    
    @Test
    public void testGetInvInfo() {
        Staff staff = new Staff();
        staff.setID("staff1");
        
        try {
            staff.getInvInfo();
        } catch (SQLException ex) {
            System.out.print(ex.getMessage());
        } catch (Exception ex) { 
            System.out.print(ex.getMessage());
        }
        assertEquals("M4",staff.getVenue());
        assertEquals("chief",staff.getStatus());
        assertEquals("AMStaff1",staff.getName());
        assertEquals("AM",staff.getSession());
        assertEquals("11/02/2015",staff.getDate());
    }
    
    @Test
    public void testGetInvInfo2() {
        Staff staff = new Staff();
        staff.setID("staff2");
        
        try {
            staff.getInvInfo();
        } catch (SQLException ex) {
            System.out.print(ex.getMessage());
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        
        assertEquals("M4",staff.getVenue());
        assertEquals("invigilator",staff.getStatus());
        assertEquals("AMStaff2",staff.getName());
        assertEquals("AM",staff.getSession());
        assertEquals("11/02/2015",staff.getDate());
    }
    
    @Test
    public void testGetInvInfo_Invalid_Staff_id() {
        String message = null;
        Staff staff = new Staff();
        staff.setID("asdf");
        
        try {
            staff.getInvInfo();
        } catch (SQLException ex) {
            System.out.print(ex.getMessage());
        } catch (Exception ex) {
            message = ex.getMessage();
        }
        
        assertEquals("No invigilator info found.",message);
    }
    
    /*
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
            System.out.print(ex.getMessage());
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
            System.out.print(ex.getMessage());
        }

        assertEquals(null,staff.getVenue());
        assertEquals(null,staff.getStatus());
    }
    */
    
    
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
            System.out.print(ex.getMessage());
        }
        
        assertEquals(1,attdList.size());
        assertEquals("W1004ADAC",attdList.get(0).getExamId());
        assertEquals("16WAR25342",attdList.get(0).getRegNum());
        assertEquals("Legal",attdList.get(0).getStatus());
        assertEquals("Present",attdList.get(0).getAttendance());
        assertEquals("MPU3123",attdList.get(0).getPaperCode());
        assertEquals("OGC2",attdList.get(0).getProgramme());
    }
    /*
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
    
    @Test
    public void testGetPapers(){
        ArrayList<Papers> papers = new ArrayList<>();
        when(time.getDate()).thenReturn("12/02/2016");
        when(time.getSession()).thenReturn("AM");
        
        try {
            papers = servercomm.getPapers("M1");
        } catch (SQLException ex) {
            System.out.print(ex.getMessage());
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        
        assertEquals(1,papers.size());
        assertEquals("BAME1212", papers.get(0).getPaperCode());
        assertEquals("no descrip", papers.get(0).getPaperDesc());
        assertEquals("0", papers.get(0).getPaperStartNo());
        assertEquals("42", papers.get(0).getTotalCandidate());
    }
    
    @Test
    public void testGetPapers2(){
        ArrayList<Papers> papers = new ArrayList<>();
        when(time.getDate()).thenReturn("11/02/2015");
        when(time.getSession()).thenReturn("AM");
        
        try {
            papers = servercomm.getPapers("M4");
        } catch (SQLException ex) {
            System.out.print(ex.getMessage());
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        
        assertEquals(1,papers.size());
        assertEquals("MPU3123", papers.get(0).getPaperCode());
        assertEquals("no description", papers.get(0).getPaperDesc());
        assertEquals("32", papers.get(0).getPaperStartNo());
        assertEquals("9", papers.get(0).getTotalCandidate());
    }
    
    @Test
    public void testGetPapersWrongTime(){
        ArrayList<Papers> papers = new ArrayList<>();
        when(time.getDate()).thenReturn("11/06/2015");
        when(time.getSession()).thenReturn("AM");
        
        try {
            papers = servercomm.getPapers("M4");
        } catch (SQLException ex) {
            System.out.print(ex.getMessage());
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        
        assertEquals(0,papers.size());
    }
    
    @Test
    public void testGetCddPapers(){
        ArrayList<CddPapers> cddPapers = new ArrayList<>();
        when(time.getDate()).thenReturn("11/02/2015");
        when(time.getSession()).thenReturn("AM");
        
        try {
            cddPapers = servercomm.getCddPapers("16WAR25342");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        assertEquals(1, cddPapers.size());
        
    }
    
    @Test
    public void testGetCddPapers2(){
        ArrayList<CddPapers> cddPapers = new ArrayList<>();
        when(time.getDate()).thenReturn("11/02/2015");
        when(time.getSession()).thenReturn("AM");
        
        try {
            cddPapers = servercomm.getCddPapers("15WAR04184");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        assertEquals(1, cddPapers.size());
        
    }
    
*/
}
