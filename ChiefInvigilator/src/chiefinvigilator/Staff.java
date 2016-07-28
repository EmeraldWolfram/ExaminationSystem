/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

import java.sql.SQLException;
import static jsonconvert.JsonConvert.attdListToJson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Krissy
 */
public class Staff {
    
    String id;
    String name;
    String status;
    String attendance;
    String block;
    String venue;
    String session;
    String date;
    String password;
    
    public Staff(){
    }
    
    public void setID(String id){
        this.id = id;
    }
    
    public String getID(){
        return this.id;
    }
    
    public void setStatus(String status){
        this.status = status;
    }
    
    public String getStatus(){
        return this.status;
    }
    
    public void setAttendance(String attendance){
        this.attendance = attendance;
    }
    
    public String getAttendance(){
        return this.attendance;
    }
    
    public void setBlock(String block){
        this.block = block;
    }
    
    public String getBlock(){
        return this.block;
    }
    
    public void setVenue(String venue){
        this.venue = venue;
    }
    
    public String getVenue(){
        return this.venue;
    }
    
    public void setSession(String session){
        this.session = session;
    }
    
    public String getSession(){
        return this.session;
    }
    
    public void setDate(String date){
        this.date = date;
    }
    
    public String getDate(){
        return this.date;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    
    public String getPassword(){
        return password;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return name;
    }


    /**
     * @brief To convert staff info to Json object
     * @param valid
     * @param staff
     * @return
     * @throws JSONException 
     */
    public JSONObject staffInfoToJson(boolean valid) throws JSONException, SQLException{
        JSONObject json = new JSONObject();
        JSONArray arr = new JSONArray();
        arr.put(getStatus());
        
        json.put("Result", valid);
        json.put("Name", getName());
        json.put("Venue", getVenue());
        json.put("IdNo", getID());
        json.put("Status", getStatus());
        json.put("CddList", attdListToJson(new ServerComm().getAttdList(getVenue())));
        json.put("PaperMap", attdListToJson(new ServerComm().getAttdList(getVenue())));
        return json;
    }
    
    public void jsonToSignIn(String jsonString) throws JSONException, Exception{
        JSONObject signID = new JSONObject(jsonString);
        Staff staff = new Staff();
        ServerComm comm = new ServerComm();
        
        setID(signID.getString("IdNo"));
        setPassword(signID.getString("Password"));
        
    }
}
