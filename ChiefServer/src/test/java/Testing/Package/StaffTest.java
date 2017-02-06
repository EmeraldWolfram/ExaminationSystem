/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing.Package;

import chiefinvigilator.ClientComm;
import chiefinvigilator.Hmac;
import chiefinvigilator.Staff;
import errormessage.ErrorMessage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import querylist.Candidate;
import querylist.Paper;
import querylist.Papers;

/**
 *
 * @author Krissy
 */
public class StaffTest {
//    
//    /**
//     * Get invigilator info with ID staff1
//     */
//    @Test
//    public void testStaff(){
//        Staff staff = new Staff();
//        try {
//            staff = new Staff("staff1");
//        } catch (Exception ex) {
//        }
//        
//        assertEquals("AMStaff1", staff.getName());
//        assertEquals("M", staff.getBlock());
//        assertEquals("invigilator", staff.getStatus());
//        assertEquals("M4", staff.getVenue());
//
//    }
//    
//    
//    /**
//     * Get invigilator info with ID staff2
//     */
//    @Test
//    public void testStaff2(){
//        Staff staff = new Staff();
//        try {
//            staff = new Staff("staff2");
//        } catch (Exception ex) {
//        }
//
//        assertEquals("AMStaff2", staff.getName());
//        assertEquals("M", staff.getBlock());
//        assertEquals("invigilator", staff.getStatus());
//        assertEquals("M1", staff.getVenue());
//    }
//    
//    /**
//     * Get invigilator info with ID staff23 and throw error
//     */
//    public void testStaffError(){
//        String message = null;
//        Staff staff;
//        try {
//            staff = new Staff("staff23");
//        } catch (Exception ex) {
//            message = ex.getMessage();
//        }
//        
//        assertEquals(ErrorMessage.INV_NOT_FOUND, message);
//    }
//    
//    /**
//     * To get the candidate list who are going to take exam at venue M1
//     */
//    @Test
//    public void testGetCddList(){
//        Staff staff = new Staff();
//        ArrayList<Candidate> cddList = new ArrayList<>();
//        
//        try {
//            cddList = staff.getCddList("M1");
//        } catch (SQLException ex) {
//            Logger.getLogger(StaffTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            Logger.getLogger(StaffTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
////        System.out.print(cddList.get(0).getProgramme());
//        assertEquals(6,cddList.size());
//    }
//    
//    /**
//     * To get the candidate list who are going to take exam at venue M4
//     */
//    @Test
//    public void testGetCddList2(){
//        Staff staff = new Staff();
//        ArrayList<Candidate> cddList = new ArrayList<>();
//        
//        try {
//            cddList = staff.getCddList("M4");
//        } catch (SQLException ex) {
//            Logger.getLogger(StaffTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            Logger.getLogger(StaffTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        assertEquals(7,cddList.size());
//        
//        for(int i = 0; i<7; i++){
//            System.out.println(cddList.get(i).getProgramme() + cddList.get(i).getExamId());
//        }
//    }
//    
//    /**
//     * To get the candidate list who are going to take exam at venue D10 but no found
//     */
//    @Test
//    public void testGetCddList_Not_Found(){
//        Staff staff = new Staff();
//        ArrayList<Candidate> cddList = new ArrayList<>();
//        String message = null;
//        try {
//            cddList = staff.getCddList("D10");
//        } catch (SQLException ex) {
//            Logger.getLogger(StaffTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            message = ex.getMessage();
//        }
//        
//        assertEquals(ErrorMessage.CDDLIST_NOT_FOUND,message);
//    }
//    
//    /**
//     * To get the exam paper list who are going to be taken at venue M1
//     */
//    @Test
//    public void testGetPapers(){
//        Staff staff = new Staff();
//        ArrayList<Paper> paperList = new ArrayList<>();
//        String message = null;
//        try {
//            paperList = staff.getPapers("M1");
//        } catch (SQLException ex) {
//            Logger.getLogger(StaffTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            message = ex.getMessage();
//        }
//        
//        assertEquals(2,paperList.size());
//    }
//    
//    /**
//     * To get the exam paper list who are going to be taken at venue M4
//     */
//    @Test
//    public void testGetPapers2(){
//        Staff staff = new Staff();
//        ArrayList<Paper> paperList = new ArrayList<>();
//        String message = null;
//        try {
//            paperList = staff.getPapers("M4");
//        } catch (SQLException ex) {
//            Logger.getLogger(StaffTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            message = ex.getMessage();
//        }
//        
//        assertEquals(1,paperList.size());
//    }
//    
//    /**
//     * To get the exam paper list who are going to be taken at venue PA10 but not found
//     */
//    @Test
//    public void testGetPapers_Not_Found(){
//        Staff staff = new Staff();
//        ArrayList<Paper> paperList = new ArrayList<>();
//        String message = null;
//        try {
//            paperList = staff.getPapers("PA10");
//        } catch (SQLException ex) {
//            Logger.getLogger(StaffTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            message = ex.getMessage();
//        }
//        
//        assertEquals(ErrorMessage.PAPERS_NOT_FOUND,message);
//    }
    
    @Test
    public void testHmac(){
        Hmac hmac = new Hmac();
        String encodedStr = null;
        
        try {
            encodedStr = hmac.encode("exam", "abc");
            System.out.println(encodedStr);
        } catch (Exception ex) {
            Logger.getLogger(StaffTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertEquals("CXTfyy84BfM0iMC/Pr3kNiuqyJz85ZilYr8m/I11/tw=\n" , encodedStr);
    }
    
    @Test
    public void testVerifyForCollector(){
        Staff staff = new Staff();
        Boolean verifyResult = false;
        
        try {
            verifyResult = staff.verifyForCollector("staff1", "bundle1");
        } catch (SQLException ex) {
            Logger.getLogger(StaffTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertEquals(verifyResult, true);
    }
    
    @Test
    public void testVerifyForCollector2(){
        Staff staff = new Staff();
        Boolean verifyResult = false;
        
        try {
            verifyResult = staff.verifyForCollector("staff3", "bundle4");
        } catch (SQLException ex) {
            Logger.getLogger(StaffTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertEquals(verifyResult, true);
    }
    
    @Test
    public void testVerifyForCollector3(){
        Staff staff = new Staff();
        Boolean verifyResult = false;
        
        try {
            verifyResult = staff.verifyForCollector("staff2", "bundle4");
        } catch (SQLException ex) {
            Logger.getLogger(StaffTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertEquals(verifyResult, false);
    }
    
}
