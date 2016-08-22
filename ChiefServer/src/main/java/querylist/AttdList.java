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
public class AttdList {
    
    String examId;
    String regNum;
    String status;
    String paperCode;
    String programme;
    String attendance;
    int tableNo;
    
    public AttdList(){
        
    }
    
    public AttdList(String regNum,
                    int tableNo,
                    String paperCode,
                    String attendance){
        
        this.regNum = regNum;
        this.tableNo = tableNo;
        this.paperCode = paperCode;
        this.attendance = attendance;
        
        
    }
    
    public AttdList(String examId,
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
    
    public JSONObject attdListToJson() throws JSONException{
        JSONObject json = new JSONObject();
        
            json = new JSONObject();
            json.put("ExamIndex", getExamId());
            json.put("RegNum", getRegNum());
            json.put("Status", getStatus());
            json.put("Code", getPaperCode());
            json.put("Programme", getProgramme());
        
        return json;
    }
    
    public void jsonToAttdList(String jsonString) throws JSONException{
        JSONObject json = new JSONObject(jsonString);
        
        this.regNum = json.getString("RegNum");
        this.tableNo = json.getInt("TableNo");
        this.paperCode = json.getString("Code");
        this.attendance = json.getString("Attendance");

    }
    
}
