/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing.Package;

import chiefinvigilator.Staff;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import jsonconvert.JsonConvert;
import org.json.JSONException;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
/**
 *
 * @author Krissy
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonTest {
    
    @Test
    public void testStaffInfoToJson(){
        String result = null;
        Staff staff = new Staff();
        staff.setName("Liu");
        staff.setVenue("M8");
        staff.setID("staff5");
        staff.setStatus("Collector");
        
        try {
            result = new JsonConvert().staffInfoToJson(true,staff);
        } catch (JSONException ex) {
            System.out.print("Convert error");
        }
        
        assertEquals("{\"Status\":\"Collector\",\"Venue\":\"M8\",\"IdNo\":\"staff5\",\"Result\":true,\"Name\":\"Liu\"}", result);
    }
    
    @Test
    public void testStaffInfoToJson2(){
        String result = null;
        Staff staff = new Staff();
        staff.setName("Dummy");
        staff.setVenue("PA2");
        staff.setID("staff3");
        staff.setStatus("Chief");
        try {
            result = new JsonConvert().staffInfoToJson(true,staff);
        } catch (JSONException ex) {
            System.out.print("Convert error");
        }
        
        assertEquals("{\"Status\":\"Chief\",\"Venue\":\"PA2\",\"IdNo\":\"staff3\",\"Result\":true,\"Name\":\"Dummy\"}", result);
    }
    
    @Test
    public void testStaffInfoToJson3(){
        String result = null;
        Staff staff = new Staff();
        staff.setName("no");
        staff.setVenue("PA2");
        staff.setID("staff3");
        staff.setStatus("invalid");
        try {
            result = new JsonConvert().staffInfoToJson(false,staff);
        } catch (JSONException ex) {
            System.out.print("Convert error");
        }
        
        assertEquals("{\"Status\":\"invalid\",\"Venue\":\"PA2\",\"IdNo\":\"staff3\",\"Result\":false,\"Name\":\"no\"}", result);
    }
    @Test
    public void testBooleanToJson(){
        String result = null;
        try {
            result = new JsonConvert().booleanToJson(true);
        } catch (JSONException ex) {
            System.out.print("Convert error");
        }
        assertEquals("{\"Result\":true}", result);
    }
    
    @Test
    public void testBooleanToJson2(){
        String result = null;
        try {
            result = new JsonConvert().booleanToJson(false);
        } catch (JSONException ex) {
            System.out.print("Convert error");
        }
        assertEquals("{\"Result\":false}", result);
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
    
}
