/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing.Package;

import chiefinvigilator.Staff;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import jsonconvert.JsonConvert;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import querylist.AttdList;
import querylist.CddPapers;
/**
 *
 * @author Krissy
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonTest {
    
    @Test
    public void testStaffInfoToJson(){
        JSONObject result = null;
        Staff staff = new Staff();
        staff.setName("Liu");
        staff.setVenue("M8");
        staff.setID("staff5");
        staff.setStatus("Relief");
        
        try {
            result = new JsonConvert().staffInfoToJson(true,staff);
            
        } catch (JSONException ex) {
            System.out.print("Convert error");
        } catch (SQLException ex) {
            System.out.print("Convert error");
        }
        
        assertEquals("{\"Status\":\"Relief\",\"Venue\":\"M8\",\"CddList\":[],\"PaperMap\":[],\"IdNo\":\"staff5\",\"Result\":true,\"Name\":\"Liu\"}"
                , result.toString());
    }
    
    @Test
    public void testStaffInfoToJson2(){
        JSONObject result = null;
        Staff staff = new Staff();
        staff.setName("Dummy");
        staff.setVenue("PA2");
        staff.setID("staff3");
        staff.setStatus("Chief");
        try {
            result = new JsonConvert().staffInfoToJson(true,staff);
        } catch (JSONException ex) {
            System.out.print("Convert error");
        } catch (SQLException ex) {
            Logger.getLogger(JsonTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertEquals("{\"Status\":\"Chief\",\"Venue\":\"PA2\",\"CddList\":[{\"ExamIndex\":\"W1005ARMB\",\"Status\":\"Barred\",\"Code\":\"BABE2203\",\"RegNum\":\"15WAD23345\",\"Programme\":\"DOC1\"}],\"PaperMap\":[{\"ExamIndex\":\"W1005ARMB\",\"Status\":\"Barred\",\"Code\":\"BABE2203\",\"RegNum\":\"15WAD23345\",\"Programme\":\"DOC1\"}],\"IdNo\":\"staff3\",\"Result\":true,\"Name\":\"Dummy\"}"
                ,result.toString());
    }
    
    @Test
    public void testStaffInfoToJson3(){
        JSONObject result = null;
        Staff staff = new Staff();
        staff.setName("no");
        staff.setVenue("PA2");
        staff.setID("staff3");
        staff.setStatus("invalid");
        try {
            result = new JsonConvert().staffInfoToJson(false,staff);
        } catch (JSONException ex) {
            System.out.print("Convert error");
        } catch (SQLException ex) {
            Logger.getLogger(JsonTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals("{\"Status\":\"invalid\",\"Venue\":\"PA2\",\"CddList\":[{\"ExamIndex\":\"W1005ARMB\",\"Status\":\"Barred\",\"Code\":\"BABE2203\",\"RegNum\":\"15WAD23345\",\"Programme\":\"DOC1\"}],\"PaperMap\":[{\"ExamIndex\":\"W1005ARMB\",\"Status\":\"Barred\",\"Code\":\"BABE2203\",\"RegNum\":\"15WAD23345\",\"Programme\":\"DOC1\"}],\"IdNo\":\"staff3\",\"Result\":false,\"Name\":\"no\"}"
                , result.toString());
    }
    @Test
    public void testBooleanToJson(){
        JSONObject result = null;
        try {
            result = new JsonConvert().booleanToJson(true);
        } catch (JSONException ex) {
            System.out.print("Convert error");
        }
        assertEquals("{\"Result\":true}", result.toString());
    }
    
    @Test
    public void testBooleanToJson2(){
        JSONObject result = null;
        try {
            result = new JsonConvert().booleanToJson(false);
        } catch (JSONException ex) {
            System.out.print("Convert error");
        }
        assertEquals("{\"Result\":false}", result.toString());
    }
    
    @Test
    public void testJsonToSignIn(){
        Staff staff = new Staff();
        
        try {
            staff = new JsonConvert().jsonToSignIn("{\"CheckIn\":\"Identity\",\"IdNo\":\"staff1\",\"Password\":\"123456\"}");
        } catch (Exception ex) {
            Logger.getLogger(JsonTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertEquals("staff1", staff.getID());
        assertEquals("123456", staff.getPassword());
    }
    
    @Test
    public void testJsonToSignIn2(){
        Staff staff = new Staff();
        
        try {
            staff = new JsonConvert().jsonToSignIn("{\"CheckIn\":\"Identity\",\"INo\":\"staff1\",\"Password\":\"123456\"}");
        } catch (Exception ex) {
            assertEquals("JSONObject[\"IdNo\"] not found.", ex.getMessage());
        }
    }
    
    @Test
    public void testAttdListToJson(){
        JSONArray json = null;
        String jsonContent1 = null;
                
        ArrayList<AttdList> attdList = new ArrayList<>();
        
        attdList.add(new AttdList("W7718CDA", "10WAC9909", "Legal", "BAME2204",
                    "RMB2","Absent"));  
        
        try {
            json = new JsonConvert().attdListToJson(attdList);
            jsonContent1 = json.getJSONObject(0).toString();
        } catch (JSONException ex) {
            Logger.getLogger(JsonTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertEquals("{\"ExamIndex\":\"W7718CDA\",\"Status\":\"Legal\",\"Code\":\"BAME2204\",\"RegNum\":\"10WAC9909\",\"Programme\":\"RMB2\"}" 
                    ,jsonContent1);
        
    }
    
    @Test
    public void testAttdListToJson2(){
        JSONArray json = null;
        String jsonContent1 = null;
        String jsonContent2 = null;
                
        ArrayList<AttdList> attdList = new ArrayList<>();
        
        attdList.add(new AttdList("W123ABCD", "15WAR09183", "Legal", "BAME2004",
                    "RMB3","Absent"));  
        attdList.add(new AttdList("W123ZXCV", "15WAR01234", "Legal", "BAME2004",
                    "RMB3","Absent"));  
        
        try {
            json = new JsonConvert().attdListToJson(attdList);
            jsonContent1 = json.getJSONObject(0).toString();
            jsonContent2 = json.getJSONObject(1).toString();
        } catch (JSONException ex) {
            Logger.getLogger(JsonTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertEquals("{\"ExamIndex\":\"W123ABCD\",\"Status\":\"Legal\",\"Code\":\"BAME2004\",\"RegNum\":\"15WAR09183\",\"Programme\":\"RMB3\"}",
                jsonContent1);
        assertEquals("{\"ExamIndex\":\"W123ZXCV\",\"Status\":\"Legal\",\"Code\":\"BAME2004\",\"RegNum\":\"15WAR01234\",\"Programme\":\"RMB3\"}",
                jsonContent2);
        
    }
    
    @Test
    public void testAttdListToJsonWithEmptyContent(){
        JSONArray json = null;
        String jsonContent1 = null;
        String jsonContent2 = null;
                
        ArrayList<AttdList> attdList = new ArrayList<>();
        
        attdList.add(new AttdList("W123ABCD", "15WAR09183", null, "BAME2004",
                    "RMB3","Absent"));  

        
        try {
            json = new JsonConvert().attdListToJson(attdList);
            jsonContent1 = json.getJSONObject(0).toString();
        } catch (JSONException ex) {
            Logger.getLogger(JsonTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertEquals("{\"ExamIndex\":\"W123ABCD\",\"Code\":\"BAME2004\",\"RegNum\":\"15WAR09183\",\"Programme\":\"RMB3\"}",
                jsonContent1);
        
    }
    
    @Test
    public void testAttdListToJsonWithEmptyObject(){
        JSONArray json = null;
        
        String jsonContent1 = null;
                
        ArrayList<AttdList> attdList = new ArrayList<>();
        
        attdList.add(new AttdList());  

        try {
            json = new JsonConvert().attdListToJson(attdList);
            jsonContent1 = json.getJSONObject(0).toString();
        } catch (JSONException ex) {
            Logger.getLogger(JsonTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertEquals("{}",jsonContent1);
        
    }
    
    @Test
    public void testCddPapers(){
        JSONArray json = null;
        String jsonContent1 = null;
        String jsonContent2 = null;
        
        ArrayList<CddPapers> cddPapers = new ArrayList<>();
        
        cddPapers.add(new CddPapers("BAME1200", "Intro to Science",
                                    "19/8/1229", "AM", "M4"));
        cddPapers.add(new CddPapers("MPU1889", "Hubungan Etnik",
                                    "19/8/1999", "PM", "Q2"));
        
        try {
            json = new JsonConvert().cddPapersToJson(cddPapers);
            jsonContent1 = json.getJSONObject(0).toString();
            jsonContent2 = json.getJSONObject(1).toString();
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
        }
        assertEquals("{\"PaperDesc\":\"Intro to Science\",\"Venue\":\"M4\",\"PaperCode\":\"BAME1200\",\"Date\":\"19/8/1229\",\"Session\":\"AM\"}"
                    , jsonContent1);
        assertEquals("{\"PaperDesc\":\"Hubungan Etnik\",\"Venue\":\"Q2\",\"PaperCode\":\"MPU1889\",\"Date\":\"19/8/1999\",\"Session\":\"PM\"}"
                    , jsonContent2);
        
        
        
    }
    
}
