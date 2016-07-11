/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsonconvert;
import chiefinvigilator.ServerComm;
import chiefinvigilator.Staff;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Krissy
 */
public class JsonConvert {
    
    public JsonConvert(){
        
    }
    
    public void state(){
        
    }
    
    public String staffInfoToJson(String name, String venue, String staffId) throws JSONException{
        JSONObject staff = new JSONObject();
        
        staff.put("Name", name);
        staff.put("Venue", venue);
        staff.put("IdNo", staffId);
        
        return staff.toString();
    }
    
    public String booleanToJson(boolean b) throws JSONException{
        JSONObject bool = new JSONObject();
        
        bool.put("Result", b);
        
        return bool.toString();
    }
    
    public Staff jsonToSignIn(String jsonString) throws JSONException, Exception{
        JSONObject signID = new JSONObject(jsonString);
        Staff staff = new Staff();
        ServerComm comm = new ServerComm();
        
        staff.setID(signID.getString("IdNo"));
        staff.setPassword(signID.getString("Password"));
        
        return staff;
    }
}
