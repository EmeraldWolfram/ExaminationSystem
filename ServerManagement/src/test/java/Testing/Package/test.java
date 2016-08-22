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
   public void testGetChiefInfoList(){
       
       chief = new ChiefData("Staff3","M");
        try {
            chief.getChiefInfo();
//            json = new JSONObject(chief.jooqtest1());
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
      
        
        assertEquals("CHIEF", chief.getStatus());
       
   }
   
   @Test
   public void testGetInvigilatorList(){
//       when(chief.getSession_id()).thenReturn(2);
       chief = new ChiefData("Staff3","M");
       ArrayList<Invigilator> invList = new ArrayList<>();
        try {
            invList = chief.getInvigilatorList();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
//        System.out.println(cddList.size());
        
        assertEquals(2, invList.size());
       
   }
   
   @Test
   public void testGetPaperInfoList(){
//       when(chief.getSession_id()).thenReturn(2);
       chief = new ChiefData("Staff3","M");
       ArrayList<PaperInfo> paperInfoList = new ArrayList<>();
        try {
            paperInfoList = chief.getPaperInfoList();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
//        System.out.println(cddList.size());
        
        assertEquals(2, paperInfoList.size());
       
   }
   
   @Test
   public void testGetPaperList(){
       chief = new ChiefData("Staff3","M");
       ArrayList<Paper> paperList = new ArrayList<>();
        try {
            paperList = chief.getPaperList();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        assertEquals(2, paperList.size());
        assertEquals(32, (int)paperList.get(0).getPaperStartNo());
        assertEquals(68, (int)paperList.get(1).getPaperStartNo());
       
   }
   
   @Test
   public void testCddAttdList(){
       chief = new ChiefData("Staff3","M");
       ArrayList<CandidateAttendance> paperList = new ArrayList<>();
        try {
            paperList = chief.getCddAttdList();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        assertEquals(8, paperList.size());
   }
   
   @Test
   public void testGetCddInfoList(){
       
       chief = new ChiefData("Staff3","M");
       ArrayList<CandidateInfo> cddList = new ArrayList<>();
        try {
            cddList = chief.getCddInfoList();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        assertEquals(8, cddList.size());
   }
   
   @Test
   public void testGetInvList(){
       
       chief = new ChiefData("Staff3","M");
       ArrayList<Invigilator> invList = new ArrayList<>();
        try {
            invList = chief.getInvigilatorList();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
      
        assertEquals(2, invList.size());
   }
   
   @Test
   public void testProgrammeList(){
       
       chief = new ChiefData("Staff3","M");
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
       
       chief = new ChiefData("Staff3","M");
       ArrayList<StaffInfo> staffInfoList = new ArrayList<>();
        try {
            staffInfoList = chief.getStaffInfoList();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
      
        assertEquals(3, staffInfoList.size());
   }
   
   @Test
   public void testGetVenuesList(){
      
       chief = new ChiefData("Staff3","M");
       ArrayList<Venue> venueList = new ArrayList<>();

        try {
            venueList = chief.getVenueList();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
      
        assertEquals(2, venueList.size());
   }
   
   @Test
   public void testChAndReList(){
      
       chief = new ChiefData("Staff3","M");
       ArrayList<ChiefAndRelief> chAndReList = new ArrayList<>();

        try {
            chAndReList = chief.getChAndReList();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
      
        assertEquals(2, chAndReList.size());
   }
   
   @Test
   public void testExamDataList(){
        chief = new ChiefData("Staff3","M");
        try {
            ExamDataList examDataList = new ExamDataList(
                    chief.getCddAttdList(),
                    chief.getCddInfoList(),
                    chief.getChAndReList(),
                    chief.getInvigilatorList(),
                    chief.getPaperList(),
                    chief.getPaperInfoList(),
                    chief.getProgrammeList(),
                    chief.getStaffInfoList(),
                    chief.getVenueList()
            );
            
            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = mapper.writeValueAsString(examDataList);
            System.out.println(jsonInString);
            
            ExamDataList test = mapper.readValue(jsonInString, ExamDataList.class);
        } catch (SQLException | IOException ex) {
            System.out.println(ex.getMessage());
        }
        
        

   }
   
}
   

