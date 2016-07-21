/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsonconvert;
import chiefinvigilator.ServerComm;
import chiefinvigilator.Staff;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import querylist.AttdList;
import querylist.Papers;

/**
 *
 * @author Krissy
 */
public class JsonConvert {
    
    public JsonConvert(){
        
    }
    
    public void state(){
        
    }
    
    /**
     * @brief To convert staff info to Json object
     * @param valid
     * @param staff
     * @return
     * @throws JSONException 
     */
    public JSONObject staffInfoToJson(boolean valid, Staff staff) throws JSONException, SQLException{
        JSONObject jsonStaff = new JSONObject();
        JSONArray arr = new JSONArray();
        arr.put(staff.getStatus());
        
        jsonStaff.put("Result", valid);
        jsonStaff.put("Name", staff.getName());
        jsonStaff.put("Venue", staff.getVenue());
        jsonStaff.put("IdNo", staff.getID());
        jsonStaff.put("Status", staff.getStatus());
        jsonStaff.put("CddList", attdListToJson(new ServerComm().getAttdList(staff.getVenue())));
        jsonStaff.put("PaperMap", attdListToJson(new ServerComm().getAttdList(staff.getVenue())));
        return jsonStaff;
    }
    
    public JSONObject booleanToJson(boolean b) throws JSONException{
        JSONObject bool = new JSONObject();
        
        bool.put("Result", b);
        
        return bool;
    }
    
    public Staff jsonToSignIn(String jsonString) throws JSONException, Exception{
        JSONObject signID = new JSONObject(jsonString);
        Staff staff = new Staff();
        ServerComm comm = new ServerComm();
        
        staff.setID(signID.getString("IdNo"));
        staff.setPassword(signID.getString("Password"));
        
        return staff;
    }
    
    public JSONArray attdListToJson(ArrayList<AttdList> attdList) throws JSONException{
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
        
        return jArr;
    }
    
    public JSONArray papersToJson(ArrayList<Papers> papers) throws JSONException{
        JSONArray jArr = new JSONArray();
        JSONObject attd;
        System.out.println(papers.size());
        for(int i = 0; i < papers.size(); i++){
            attd = new JSONObject();
            
            attd.put("PaperCode", papers.get(i).getPaperCode());
            attd.put("PaperDesc", papers.get(i).getPaperDesc());
            attd.put("PaperStartNo", papers.get(i).getPaperStartNo());
            attd.put("PaperTotalCdd", papers.get(i).getTotalCandidate());
            jArr.put(attd);
        }
        
        return jArr;
    }
    
    public JSONObject jsonStringConcatenate(JSONObject jsonStaff, JSONArray papers, JSONArray attdList) throws JSONException{
        
        jsonStaff.put("CddList", attdList);
        jsonStaff.put("PaperMap", papers);
        
        return jsonStaff;
    }
    
    
    
}
