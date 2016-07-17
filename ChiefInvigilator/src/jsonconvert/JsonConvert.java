/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsonconvert;
import chiefinvigilator.ServerComm;
import chiefinvigilator.Staff;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import querylist.AttdList;

/**
 *
 * @author Krissy
 */
public class JsonConvert {
    
    public JsonConvert(){
        
    }
    
    public void state(){
        
    }
    
    public String staffInfoToJson(boolean valid, Staff staff) throws JSONException{
        JSONObject jsonStaff = new JSONObject();
        JSONArray arr = new JSONArray();
        arr.put(staff.getStatus());
        
        jsonStaff.put("Type", "Identity");
        jsonStaff.put("Result", valid);
        jsonStaff.put("Name", staff.getName());
        jsonStaff.put("Venue", staff.getVenue());
        jsonStaff.put("IdNo", staff.getID());
        jsonStaff.put("Status", arr);
        
        return jsonStaff.toString();
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
    
    public String attdListToJson(ArrayList<AttdList> attdList) throws JSONException{
        JSONArray jArr = new JSONArray();
        JSONObject attd;
        for(int i = 0; i < attdList.size(); i++){
            attd = new JSONObject();
            attd.put("Index", attdList.get(i).getExamId());
            attd.put("RegNum", attdList.get(i).getRegNum());
            attd.put("Status", attdList.get(i).getStatus());
            attd.put("Code", attdList.get(i).getPaperCode());
            attd.put("Programme", attdList.get(i).getProgramme());
            jArr.put(attd);
        }
        
        return jArr.toString();
    }
}
