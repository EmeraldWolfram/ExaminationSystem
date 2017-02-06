/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing.Package;

import chiefinvigilator.Staff;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import querylist.AttdList;
import querylist.Candidate;
import querylist.CddPaperList;
import querylist.CddPapers;
import querylist.Paper;
/**
 *
 * @author Krissy
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonTest {
    /*
    @Test
    public void testStaffInfoToJson(){
        JSONObject json = new JSONObject();
        Staff staff = new Staff();
        staff.setName("Liu");
        staff.setVenue("M8");
        staff.setID("staff5");
        staff.setStatus("Relief");
        
        try {
            json = staff.toJson(true);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        assertEquals("{\"Status\":\"Relief\",\"Venue\":\"M8\",\"CddList\":[],\"PaperMap\":[],\"IdNo\":\"staff5\",\"Result\":true,\"Name\":\"Liu\"}"
                , json.toString());
    }
    
    @Test
    public void testStaffInfoToJson2(){
        JSONObject json = new JSONObject();
        Staff staff = new Staff();
        staff.setName("Dummy");
        staff.setVenue("PA2");
        staff.setID("staff3");
        staff.setStatus("Chief");
        
        try {
            json = staff.toJson(true);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        assertEquals("{\"Status\":\"Chief\",\"Venue\":\"PA2\",\"CddList\":[{\"ExamIndex\":\"W1005ARMB\",\"Status\":\"Barred\",\"Code\":\"BABE2203\",\"RegNum\":\"15WAD23345\",\"Programme\":\"DOC1\"}],\"PaperMap\":[{\"ExamIndex\":\"W1005ARMB\",\"Status\":\"Barred\",\"Code\":\"BABE2203\",\"RegNum\":\"15WAD23345\",\"Programme\":\"DOC1\"}],\"IdNo\":\"staff3\",\"Result\":true,\"Name\":\"Dummy\"}"
                ,json.toString());
    }
    
    @Test
    public void testStaffInfoToJson3(){
        JSONObject json = new JSONObject();
        Staff staff = new Staff();
        staff.setName("no");
        staff.setVenue("PA2");
        staff.setID("staff3");
        staff.setStatus("invalid");
        
        try {
            json = staff.toJson(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        assertEquals("{\"Result\":false}", json.toString());
    }
    
    @Test
    public void testBooleanToJson(){
        JSONObject result = null;
        try {
            result = new JsonConvert().booleanToJson(true);
        } catch (JSONException ex) {
            System.out.print("Convert error");
        }
        assertEquals("{\"Type\":\"Ack\",\"Result\":true}", result.toString());
    }
    
    @Test
    public void testBooleanToJson2(){
        JSONObject result = null;
        try {
            result = new JsonConvert().booleanToJson(false);
        } catch (JSONException ex) {
            System.out.print(ex.getMessage());
        }
        assertEquals("{\"Type\":\"Ack\",\"Result\":false}", result.toString());
    }
    
    @Test
    public void testSetIdPsFromJsonString(){
        Staff staff = new Staff();
        
        try {
            staff.setIdPsFromJsonString("{\"CheckIn\":\"Identity\",\"IdNo\":\"staff1\",\"Password\":\"123456\"}");
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        
        assertEquals("staff1", staff.getID());
        assertEquals("123456", staff.getPassword());
    }
    
    @Test
    public void testSetIdPsFromJsonString2(){
        Staff staff = new Staff();
        try {
            staff.setIdPsFromJsonString("{\"CheckIn\":\"Identity\",\"IdNo\":\"staff2\",\"Password\":\"abc123\"}");
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        assertEquals("staff2", staff.getID());
        assertEquals("abc123", staff.getPassword());
    }
    
    @Test
    public void testCandidateToJson(){
        Candidate cand = new Candidate("W00909","12WAR09183","legal","BAME9909","RND1","Absent");
        JSONObject json = new JSONObject();
        
        try {
            json = cand.toJson();
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        
        assertEquals("{\"ExamIndex\":\"W00909\",\"Status\":\"legal\",\"Code\":\"BAME9909\","
                        + "\"RegNum\":\"12WAR09183\",\"Programme\":\"RND1\"}"
                    , json.toString());
    }
    
    @Test
    public void testCandidateToJson2(){
        Candidate cand = new Candidate("A2002A","13WAD98811","illegal","BAME1122","RSD2","Present");
        JSONObject json = new JSONObject();
        
        try {
            json = cand.toJson();
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        assertEquals("{\"ExamIndex\":\"A2002A\",\"Status\":\"illegal\",\"Code\":\"BAME1122\","
                    + "\"RegNum\":\"13WAD98811\",\"Programme\":\"RSD2\"}"
                    , json.toString());
    }
    
    @Test
    public void testCandidateFromJson(){
        Candidate cand = new Candidate();
        
        try {
            cand.fromJson("{\"RegNum\":\"13WAD00999\",\"TableNo\":\"13\","
                    + "\"Code\":\"BAME2332\",\"Attendance\":\"Present\"}");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        assertEquals("13WAD00999", cand.getRegNum());
        assertEquals(13, cand.getTableNo());
        assertEquals("BAME2332", cand.getPaperCode());
        assertEquals("Present", cand.getAttendance());
        assertEquals(null, cand.getExamId());
        assertEquals(null, cand.getStatus());
        assertEquals(null, cand.getProgramme());

    }
    
    @Test
    public void testCandidateFromJson2(){
        Candidate cand = new Candidate();
        
        try {
            cand.fromJson("{\"RegNum\":\"12WED09882\",\"TableNo\":50,"
                    + "\"Code\":\"BAME8899\",\"Attendance\":\"Present\"}");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        assertEquals("12WED09882", cand.getRegNum());
        assertEquals(50, cand.getTableNo());
        assertEquals("BAME8899", cand.getPaperCode());
        assertEquals("Present", cand.getAttendance());
        assertEquals(null, cand.getExamId());
        assertEquals(null, cand.getStatus());
        assertEquals(null, cand.getProgramme());

    }
    
    @Test
    public void testPaperToJson(){
        Paper paper = new Paper("BAME1202", "some description", "30", "100");
        JSONObject json = new JSONObject();
        
        try{
            json = paper.toJson();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        assertEquals("{\"PaperStartNo\":30,\"PaperDesc\":\"some description\","
                + "\"PaperCode\":\"BAME1202\",\"PaperTotalCdd\":100}",
                json.toString());
        
    }
    
    
    
    @Test
    public void testPaperToJson2(){
        Paper paper = new Paper("BABE1002", "description", "11", "55");
        JSONObject json = new JSONObject();
        
        try{
            json = paper.toJson();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        assertEquals("{\"PaperStartNo\":11,\"PaperDesc\":\"description\","
                + "\"PaperCode\":\"BABE1002\",\"PaperTotalCdd\":55}",
                json.toString());
        
    }
            
    @Test
    public void testCddPaperList(){
        CddPaperList cddPaperList = new CddPaperList();
        
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
    
    @Test
    public void testArrayList(){
        String message = "[{\"RegNum\":\"13WAR19191\","
                + "\"Attendance\":\"Present\",\"TableNo\":\"12\",\"Code\":\"BAME1233\"}]";
        ArrayList<AttdList> attdList = new ArrayList();
        
        try {
            attdList = jsonToAttdList(message);
        } catch (JSONException ex) {
            System.out.println(ex);
        }
        
        assertEquals(1, attdList.size());
        assertEquals("13WAR19191", attdList.get(0).getRegNum());
        assertEquals("Present", attdList.get(0).getAttendance());
        assertEquals("BAME1233", attdList.get(0).getPaperCode());
        assertEquals(12, attdList.get(0).getTableNo());
                
    }
    
    @Test
    public void testArrayList2(){
        String message = "[{\"RegNum\":\"13WAR19191\","
                + "\"Attendance\":\"Present\",\"TableNo\":12,\"Code\":\"BAME1233\"},"
                + "{\"RegNum\":\"15WAR12121\",\"Attendance\":\"Present\",\"TableNo\":45,\"Code\":\"BAME1993\"}"
                + "]";
        ArrayList<AttdList> attdList = new ArrayList();
        
        try {
            attdList = jsonToAttdList(message);
        } catch (JSONException ex) {
            System.out.println(ex);
        }
        
        assertEquals(2, attdList.size());
        assertEquals("13WAR19191", attdList.get(0).getRegNum());
        assertEquals("Present", attdList.get(0).getAttendance());
        assertEquals("BAME1233", attdList.get(0).getPaperCode());
        assertEquals(12, attdList.get(0).getTableNo());
        assertEquals("15WAR12121", attdList.get(1).getRegNum());
        assertEquals("Present", attdList.get(1).getAttendance());
        assertEquals("BAME1993", attdList.get(1).getPaperCode());
        assertEquals(45, attdList.get(1).getTableNo());
                
    }
    */
}
