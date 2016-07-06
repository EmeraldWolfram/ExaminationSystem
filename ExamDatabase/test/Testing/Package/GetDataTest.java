/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing.Package;

import examdatabase.ConnectDB;
import examdatabase.CustomException;
import examdatabase.GetData;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Krissy
 */
public class GetDataTest {
    
    private static GetData adlas;
    private static GetData oslo;
    private static GetData liu;
    
    @Before
    public void oneTimeSetUp(){
        new ConnectDB().setConnection("FEB_MAR", "2016");
        
        adlas = new GetData(    "829911092234", "Adlas", "16WAR25342", 
                                "Legal", "Present", "56",
                                "OGC2", "FASC",
                                "11/2/2015", "AM",
                                "MPU3123", "no description",
                                "M4", "45"
                                );
        
        oslo = new GetData(     "930529126289", "Oslo", "15WAD23345",
                                "Barred", "Absent", "", 
                                "DOC1", "FEBE",
                                "13/1/2016", "AM",
                                "BABE2203", "none",
                                "PA2", "100"
                                );
        
        liu = new GetData(      "941014126347", "Liu", "15WAR09183",
                                "Legal", "Absent", "",
                                "RMB3", "FASC",
                                "10/2/2016", "PM",
                                "BABE2203", "none",
                                "Q5", "50"
                                );
    }
    /***
     * 
     * Input: GetData(  IC, Name, RegNum, Status, Attendance, 
     *                  TableNumber, ProgammeName, Faculty)
     */
    @Test
    public void testGetDataAll() {
        
        GetData getData = new GetData("","","","","","","","","","","","","","");
        
        ArrayList<GetData> list = null;
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
            Logger.getLogger(GetDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        CustomAssertion.assertDataEqual(oslo,list.get(0));
        CustomAssertion.assertDataEqual(adlas,list.get(1));
        CustomAssertion.assertDataEqual(liu,list.get(2));
        
    }

    @Test
    public void testGetDataViaIC(){
        GetData getData = new GetData("829911092234","","","","","","","","","","","","","");
        
        
        ArrayList<GetData> list = null;
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
            Logger.getLogger(GetDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        CustomAssertion.assertDataEqual(adlas,list.get(0));
        
    }
    
    @Test
    public void testGetDataViaName(){
        GetData getData = new GetData("","Liu","","","","","","","","","","","","");
        
        ArrayList<GetData> list = null;
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
            Logger.getLogger(GetDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        CustomAssertion.assertDataEqual(liu,list.get(0));
    }
    
    @Test
    public void testGetDataViaStudentID(){
        GetData getData = new GetData("","","15WAD23345","","","","","","","","","","","");
        
        ArrayList<GetData> list = null;
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
            Logger.getLogger(GetDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        CustomAssertion.assertDataEqual(oslo,list.get(0));

    }
    
    @Test
    public void testGetDataViaNameAndIC(){
        GetData getData = new GetData("930529126289","Oslo","","","","","","","","","","","","");
        
        ArrayList<GetData> list = null;
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
            Logger.getLogger(GetDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        CustomAssertion.assertDataEqual(oslo,list.get(0));

    }
    
    @Test
    public void testGetDataViaStatusLegal(){
        GetData getData = new GetData("","","","Legal","","","","","","","","","","");

        ArrayList<GetData> list = null;
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
            Logger.getLogger(GetDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        CustomAssertion.assertDataEqual(adlas,list.get(0));
        CustomAssertion.assertDataEqual(liu,list.get(1));
    }
    
    @Test
    public void testGetDataViaStatusBarred(){
        GetData getData = new GetData("","","","Barred","","","","","","","","","","");

        ArrayList<GetData> list = null;
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
            Logger.getLogger(GetDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        CustomAssertion.assertDataEqual(oslo,list.get(0));
    }
    
    @Test
    public void testGetDataViaAttendanceAbsent(){
        GetData getData = new GetData("","","","","Absent","","","","","","","","","");

        ArrayList<GetData> list = null;
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
            Logger.getLogger(GetDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        CustomAssertion.assertDataEqual(oslo,list.get(0));
        CustomAssertion.assertDataEqual(liu,list.get(1));
    }
    
    @Test
    public void testGetDataViaAttendance(){
        GetData getData = new GetData("","","","","Absent","","","","","","","","","");

        ArrayList<GetData> list = null;
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
            Logger.getLogger(GetDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        CustomAssertion.assertDataEqual(oslo,list.get(0));
        CustomAssertion.assertDataEqual(liu,list.get(1));
    }
    
    @Test
    public void testGetDataViaTableNumber(){
        GetData getData = new GetData("","","","","","56","","","","","","","","");

        ArrayList<GetData> list = null;
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
            Logger.getLogger(GetDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        CustomAssertion.assertDataEqual(adlas,list.get(0));
    }
    
    @Test
    public void testGetDataViaProgNameOGC2(){
        GetData getData = new GetData("","","","","","","OGC2","","","","","","","");

        ArrayList<GetData> list = null;
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
            System.out.printf(ex.getMessage());
        }

        CustomAssertion.assertDataEqual(adlas,list.get(0));
    }
    
    @Test
    public void testGetDataViaFacultyFASC(){
        GetData getData = new GetData("","","","","","","","FASC","","","","","","");

        ArrayList<GetData> list = null;
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
            System.out.printf(ex.getMessage());
        }
        
        CustomAssertion.assertDataEqual(adlas,list.get(0));
        CustomAssertion.assertDataEqual(liu,list.get(1));
    }
    
    @Test
    public void testGetDataViaSessionPM(){
        GetData getData = new GetData("","","","","","","","","","PM","","","","");

        ArrayList<GetData> list = null;
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
            System.out.printf(ex.getMessage());
        }

        CustomAssertion.assertDataEqual(liu,list.get(0));
    }
    
    @Test
    public void testGetDataViaPaperCodeMPU3123(){
        GetData getData = new GetData("","","","","","","","","","","BABE2203","","","");

        ArrayList<GetData> list = null;
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
            System.out.printf(ex.getMessage());
        }

        CustomAssertion.assertDataEqual(oslo,list.get(0));
        CustomAssertion.assertDataEqual(liu,list.get(1));
    }
    
    @Test
    public void testGetDataViaVenueNameM4(){
        GetData getData = new GetData("","","","","","","","","","","","","M4","");

        ArrayList<GetData> list = null;
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
            System.out.printf(ex.getMessage());
        }

        CustomAssertion.assertDataEqual(adlas,list.get(0));
    }
    
    @Test
    public void testGetDataViaVenueSize100(){
        GetData getData = new GetData("","","","","","","","","","","","","","50");

        ArrayList<GetData> list = null;
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
            System.out.printf(ex.getMessage());
        }

        CustomAssertion.assertDataEqual(liu,list.get(0));
    }
    
    @Test
    public void testGetDataFromMultiCondition(){
        GetData getData = new GetData("","","","Legal","","","RMB3","","","","","","","50");

        ArrayList<GetData> list = null;
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
            System.out.printf(ex.getMessage());
        }

        CustomAssertion.assertDataEqual(liu,list.get(0));
    }
    
    @Test
    public void testGetDataException(){
        GetData getData = new GetData("","","","","sdsd","","","","","","","","","");
        
        ArrayList<GetData> list = null;
        
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
            String message = ex.getMessage();
            assertEquals("No data found.", message);
        }

    }
    
    @Test
    public void testGetDataLIKEname_begining_with_a_char(){
        GetData getData = new GetData("","A%","","","","","","","","","","","","");
        
        ArrayList<GetData> list = null;
        
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
        }
        
        CustomAssertion.assertDataEqual(adlas,list.get(0));

    }
    
    @Test
    public void testGetDataLIKEname_begining_with_L_char_and_other_2_char(){
        GetData getData = new GetData("","L__","","","","","","","","","","","","");
        
        ArrayList<GetData> list = null;
        
        try {
            list = getData.getDataFromTable();
        } catch (CustomException ex) {
        }
        
        CustomAssertion.assertDataEqual(liu,list.get(0));

    }
}
