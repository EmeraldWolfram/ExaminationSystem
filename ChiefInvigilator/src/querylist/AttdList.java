/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package querylist;

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
    int tableSeat;
    
    public AttdList(){
        
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
    
    public void setExamId(String examId){
        this.examId = examId;
    }
    
    public void setStudentStatus(String attendance){
        this.attendance = attendance;
    }
}
