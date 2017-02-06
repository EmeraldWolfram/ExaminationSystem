/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing.Package;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import mainserver.ChiefData;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import querylist.*;
import org.junit.Test;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.when;

/**
 *
 * @author Krissy
 */
public class test {
    
    @InjectMocks
    ChiefData chief;
    
   
   
   
   @Test
   public void testVerifyStaff(){
       
       chief = new ChiefData();
       String status = null;
       
        try {
            chief.getStatus("staff1","123456");
//            json = new JSONObject(chief.jooqtest1());
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
      
        
        assertEquals("CHIEF", status);
       
   }
   
   @Test
   public void testGetInvigilatorList(){
//       when(chief.getSession_id()).thenReturn(2);
       chief = new ChiefData();
       ArrayList<Invigilator> invList = new ArrayList<>();
        try {
            invList = chief.getInvigilatorList("M");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
//        System.out.println(cddList.size());
        
        assertEquals(2, invList.size());
       
   }
   
   @Test
   public void testGetPaperInfoList(){
//       when(chief.getSession_id()).thenReturn(2);
       chief = new ChiefData();
       ArrayList<PaperInfo> paperInfoList = new ArrayList<>();
        try {
            paperInfoList = chief.getPaperInfoList("M");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
//        System.out.println(cddList.size());
        
        assertEquals(2, paperInfoList.size());
       
   }
   
   @Test
   public void testGetPaperList(){
       chief = new ChiefData();
       ArrayList<Paper> paperList = new ArrayList<>();
        try {
            paperList = chief.getPaperList("M", 1);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        assertEquals(2, paperList.size());
        assertEquals(32, (int)paperList.get(0).getPaperStartNo());
        assertEquals(68, (int)paperList.get(1).getPaperStartNo());
       
   }
   
   @Test
   public void testCddAttdList(){
       chief = new ChiefData();
       ArrayList<CandidateAttendance> paperList = new ArrayList<>();
        try {
            paperList = chief.getCddAttdList("M",1);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        assertEquals(8, paperList.size());
   }
   
   @Test
   public void testGetCddInfoList(){
       
       chief = new ChiefData();
       ArrayList<CandidateInfo> cddList = new ArrayList<>();
        try {
            cddList = chief.getCddInfoList("M");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        assertEquals(8, cddList.size());
   }
   
   @Test
   public void testGetInvList(){
       
       chief = new ChiefData();
       ArrayList<Invigilator> invList = new ArrayList<>();
        try {
            invList = chief.getInvigilatorList("M");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
      
        assertEquals(2, invList.size());
   }
   
   @Test
   public void testProgrammeList(){
       
       chief = new ChiefData();
       ArrayList<Programme> programmeList = new ArrayList<>();
        try {
            programmeList = chief.getProgrammeList();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
      
        assertEquals(3, programmeList.size());
   }
   
   
   @Test
   public void testGetStaffInfoList(){
       
       chief = new ChiefData();
       ArrayList<StaffInfo> staffInfoList = new ArrayList<>();
        try {
            staffInfoList = chief.getStaffInfoList("M", 1);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
      
        assertEquals(3, staffInfoList.size());
   }
   
   @Test
   public void testGetVenuesList(){
      
       chief = new ChiefData();
       ArrayList<Venue> venueList = new ArrayList<>();

        try {
            venueList = chief.getVenueList("M");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
      
        assertEquals(2, venueList.size());
   }
   
   @Test
   public void testChAndReList(){
      
       chief = new ChiefData();
       ArrayList<ChiefAndRelief> chAndReList = new ArrayList<>();

        try {
            chAndReList = chief.getChAndReList("M");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
      
        assertEquals(2, chAndReList.size());
   }
   
   @Test
   public void testExamDataList(){
        chief = new ChiefData();
        try {
            ExamDataList examDataList = new ExamDataList(
                    chief.getCddAttdList("M",1),
                    chief.getCddInfoList("M"),
                    chief.getChAndReList("M"),
                    chief.getCollectorList(),
                    chief.getInvigilatorList("M"),
                    chief.getPaperList("M",1),
                    chief.getPaperInfoList("M"),
                    chief.getProgrammeList(),
                    chief.getStaffInfoList("M",1),
                    chief.getVenueList("M")
            );
            
            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = mapper.writeValueAsString(examDataList);
            //System.out.println(jsonInString);
            
            ExamDataList test = mapper.readValue(jsonInString, ExamDataList.class);
        } catch (SQLException | IOException ex) {
            System.out.println(ex.getMessage());
        }
        
        

   }
   
   @Test
   public void testGetCddPaperList(){
       ChiefData chief = new ChiefData();
        try {
            ArrayList<CddPaper> cddPaperList = chief.getCddPaperList("16WAR25342");
//            System.out.
        } catch (SQLException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
   }
   
}
   

