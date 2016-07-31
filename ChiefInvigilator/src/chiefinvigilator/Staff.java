/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    public Staff(String id, String password){
        this.id = id;
        this.password = password;
        
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
    public JSONObject toJson(boolean valid) throws Exception{
        JSONObject json = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
       
            arr.put(getStatus());
            
            
            json.put("Result", valid);
            
            if(valid){
                json.put("Name", getName());
                json.put("Venue", getVenue());
                json.put("IdNo", getID());
                json.put("Status", getStatus());
                json.put("CddList", attdListToJson(ServerComm.getAttdList(getVenue())));
                json.put("PaperMap", attdListToJson(ServerComm.getAttdList(getVenue())));
            }
            
        } catch (JSONException ex) {
            throw new Exception("Error: Staff.toJson fail !!" + ex.getMessage());
        } catch (SQLException ex) {
            throw new Exception("Error: Database error !!" + ex.getMessage());
        }
        
        return json;
    }
    
    /**
     * @brief To get id and password from json string message
     * @param jsonString  contains json object in string format
     * @throws Exception 
     */
    public void setIdPsFromJsonString(String jsonString) throws Exception{
        try {
            JSONObject json = new JSONObject(jsonString);
            
            setID(json.getString("IdNo"));
            setPassword(json.getString("Password"));
        } catch (JSONException ex) {
            throw new Exception("Error: Staff.setIdPsFromJsonString fail!!  "+ex.getMessage());
        }
        
    }
    
    /**
     * @brief   To check the id and the password of the staff from database
     * @param   id            id of the staff
     * @param   password      password of the staff
     * @return match        The result of the checking ,true is correct while false is incorrect
     * @throws SQLException 
     */
    public boolean staffVerify() throws SQLException {
        boolean match = false;
        
            Connection conn = new ConnectDB("UserDatabase.db").connect();
            String sql = "SELECT Username, Password FROM User where Username=? and Password=?";


            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,id);
            ps.setString(2,password);
            ResultSet result = ps.executeQuery();

            match = result.next(); 

            result.close();
            ps.close();
            conn.close();
        
        return match;
    }
    
    /**
     * @brief   To get the staff info depends on the current time
     * @param   id            id of the staff
     * @return  staff       contains info of the staff
     * @throws SQLException 
     */
    public void getInvInfo() throws Exception{
        
        Connection conn = new ConnectDB("ChiefDataBase.db").connect();
        String sql = "SELECT Venue.Name AS VenueName, InvigilatorAndAssistant.StaffID AS Staff_id, "
                + "StaffInfo.Name AS StaffName "
                + ",* FROM InvigilatorAndAssistant "
                + "LEFT OUTER JOIN Paper ON Paper.Paper_id = InvigilatorAndAssistant.Paper_id "
                + "LEFT OUTER JOIN PaperInfo ON PaperInfo.PI_id = Paper.PI_id "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + "LEFT OUTER JOIN StaffInfo ON StaffInfo.StaffID = Staff_id "
                + "WHERE Staff_id = ? ";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
//            ps.setString(2, time.getDate());
//            ps.setString(3, time.getSession());

            ResultSet result = ps.executeQuery();
            
            if(result.isBeforeFirst())
                while(result.next()){
                    setName(result.getString("StaffName"));
                    setStatus(result.getString("Status"));
                    setVenue(result.getString("VenueName"));
                    setSession(result.getString("Session"));
                    setDate(result.getString("Date"));

                }
            else
                throw new Exception("No invigilator info found.");
            
            
        
        result.close();
        ps.close();
        conn.close();
        }catch(SQLException ex){
            throw new Exception(ex.getMessage());
        }
        
    }
}
