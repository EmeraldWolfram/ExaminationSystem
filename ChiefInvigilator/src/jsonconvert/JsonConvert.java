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
import querylist.CddPapers;
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
    public static JSONObject staffInfoToJson(boolean valid, Staff staff) throws JSONException, SQLException{
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
    
    public static JSONObject booleanToJson(boolean b) throws JSONException{
        JSONObject bool = new JSONObject();
        
        bool.put("Result", b);
        
        return bool;
    }
    
    public static Staff jsonToSignIn(String jsonString) throws JSONException, Exception{
        JSONObject signID = new JSONObject(jsonString);
        Staff staff = new Staff();
        ServerComm comm = new ServerComm();
        
        staff.setID(signID.getString("IdNo"));
        staff.setPassword(signID.getString("Password"));
        
        return staff;
    }
    
    public static JSONArray attdListToJson(ArrayList<AttdList> attdList) throws JSONException{
        JSONArray jArr = new JSONArray();
        JSONObject attd;
        for(int i = 0; i < attdList.size(); i++){
            attd = new JSONObject();
            attd.put("ExamIndex", attdList.get(i).getExamId());
            attd.put("RegNum", attdList.get(i).getRegNum());
            attd.put("Status", attdList.get(i).getStatus());
            attd.put("Code", attdList.get(i).getPaperCode());
            attd.put("Programme", attdList.get(i).getProgramme());
            jArr.put(attd);
        }
        
        return jArr;
    }
    
    public static JSONArray papersToJson(ArrayList<Papers> papers) throws JSONException{
        JSONArray jArr = new JSONArray();
        JSONObject attd;
        
        for(int i = 0; i < papers.size(); i++){
            attd = new JSONObject();
            
            attd.put("PaperCode", papers.get(i).getPaperCode());
            attd.put("PaperDesc", papers.get(i).getPaperDesc());
            attd.put("PaperStartNo", Integer.parseInt(papers.get(i).getPaperStartNo()));
            attd.put("PaperTotalCdd", Integer.parseInt(papers.get(i).getTotalCandidate()));
            jArr.put(attd);
        }
        
        return jArr;
    }
    
    public static JSONArray cddPapersToJson(ArrayList<CddPapers> cddPapers) throws JSONException{
        JSONArray jArr = new JSONArray();
        JSONObject papers;
        for(int i = 0; i < cddPapers.size(); i++){
            papers = new JSONObject();
            
            papers.put("PaperCode", cddPapers.get(i).getPaperCode());
            papers.put("PaperDesc", cddPapers.get(i).getPaperDesc());
            papers.put("Date", cddPapers.get(i).getDate());
            papers.put("Session", cddPapers.get(i).getSession());
            papers.put("Venue", cddPapers.get(i).getVenue());
            jArr.put(papers);
        }
        
        return jArr;
    }
    
    public static JSONObject jsonStringConcatenate(JSONObject jsonStaff, JSONArray papers, JSONArray attdList) throws JSONException{
        
        jsonStaff.put("CddList", attdList);
        jsonStaff.put("PaperMap", papers);
        
        return jsonStaff;
    }
    
    public static String jsonToCddPapers(String candidateID) throws JSONException{
        JSONObject jsonObject = new JSONObject(candidateID);
        
        return jsonObject.getString("Value");
    }
    
    
}
