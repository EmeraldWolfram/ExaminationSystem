/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package querylist;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Krissy
 */
public class Candidate {
    
    String examId;
    String regNum;
    String status;
    String paperCode;
    String programme;
    String attendance;
    int tableNo;
    
    public Candidate(){
        
    }
    
    public Candidate(String regNum,
                    int tableNo,
                    String paperCode,
                    String attendance){
        
        this.regNum = regNum;
        this.tableNo = tableNo;
        this.paperCode = paperCode;
        this.attendance = attendance;
        
        
    }
    
    public Candidate(String examId,
                    String regNum,
                    String status,
                    String paperCode,
                    String programme,
                    String attendance){
        
        this.examId = examId;
        this.regNum = regNum;
        this.status = status;
        this.paperCode = paperCode;
        this.programme = programme;
        this.attendance = attendance;
    }
    
    
    public String getExamId(){
        return examId;
    }
    
    public String getAttendance(){
        return attendance;
    }
    
    public String getRegNum(){
        return regNum;
    }
    
    public String getStatus(){
        return status;
    }
    
    public String getPaperCode(){
        return paperCode;
    }
    
    public String getProgramme(){
        return programme;
    }
    
    public int getTableNo(){
        return tableNo;
    }
    
    public void setExamId(String examId){
        this.examId = examId;
    }
    
    public void setStudentStatus(String attendance){
        this.attendance = attendance;
    }
    
    /**
     * @brief   To convert the Candidate object to JSON object
     * @return  contain the candidate info in JSON object
     * @throws JSONException 
     */
    public JSONObject toJson() throws Exception{
        JSONObject json = new JSONObject();
        
        try{
            json = new JSONObject();
            json.put("ExamIndex", getExamId());
            json.put("RegNum", getRegNum());
            json.put("Status", getStatus());
            json.put("PaperCode", getPaperCode());
            json.put("Programme", getProgramme());
        }catch (JSONException ex){
            throw new Exception("Error: " + ex.getMessage());
        }
        
        return json;
    }
    
    /**
     * @brief   To convert JSON object to Candidate Object 
     *          to update the attendance and table number
     * @param   jsonString    contains the JSONObject in String format
     * @throws  Exception 
     */
    public void fromJson(String jsonString) throws Exception{
        
        try{
        JSONObject json = new JSONObject(jsonString);
       
        this.examId = json.getString("ExamIndex");
        this.tableNo = json.getInt("TableNo");
        this.paperCode = json.getString("PaperCode");
        this.attendance = json.getString("Attendance");
        }catch(JSONException ex){
            throw new Exception("Error: " + ex.getMessage());
        }

    }
    
}
