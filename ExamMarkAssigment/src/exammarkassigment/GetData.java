/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exammarkassigment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Krissy
 */
public class GetData {
    
    //CandidateInfo
    public String examId = ""; 
    
    //Programme
    public String progName = "";
    
    //PaperInfo
    public String paperCode = "";
    public String paperDesc = "";
    public Integer examMark ;
    
    public GetData(){
        
    }
    
    public void setPaperCode(String paperCode){
        this.paperCode = paperCode;
    }
    
    public String getPaperCode(){
        return paperCode;
    }
    
    public void setProgName(String progName){
        this.progName = progName;
    }
    
    public String getProgName(){
        return progName;
    }
    
    public void setExamId(String examId){
        this.examId = examId;
    }
    
    public String getExamId(){
        return examId;
    }
    
    public void setExamMark(Integer examMark){
        this.examMark = examMark;
    }
    
    public Integer getExamMark(){
        return examMark;
    }
    
    /**
     * Check whether the input is null or not
     * @param input     string of a data
     * @return          reconstruct input to SQLite code or return "IS NOT NULL" when the input is null
     * 
     */
    public String checkInput(String input){
        if((input == "")||(input == " ")||(input == null)||(input.isEmpty()))
            return "IS NOT NULL";  
        else
            return "LIKE '" + input + "'"; 
    }
    
    
    /**
     * Get the info of a candidate from the connected database
     * @return list     The arraylist contain the info of the candidate
     */
    public ArrayList<GetData> getDataCheckExamMark() throws CustomException{
        String sql =    "SELECT CandidateInfo.ExamIndex, "
                + " Programme.Name AS ProgName, "
                + " PaperInfo.PaperCode, "
                + " StudentMark.Exam"
                + " FROM StudentMark "
                + " LEFT OUTER JOIN CandidateInfo ON StudentMark.RegNum = CandidateInfo.RegNum "
                + " LEFT OUTER JOIN CandidateAttendance ON CandidateInfo.IC = CandidateAttendance.CandidateInfoIC "
                + " LEFT OUTER JOIN Programme ON CandidateInfo.ProgrammeIndex = Programme.ProgrammeIndex "
                + " LEFT OUTER JOIN Paper ON CandidateAttendance.PaperIndex = Paper.PaperIndex "
                + " LEFT OUTER JOIN PaperInfo ON PaperInfo.PIIndex = StudentMark.PIIndex "
                + " LEFT OUTER JOIN Venue ON Paper.VenueIndex = Venue.VenueIndex "
                + " WHERE CandidateInfo.ExamIndex " + checkInput(this.examId)
                + " AND ProgName "+ checkInput(this.progName)
                + " AND PaperInfo.PaperCode "+ checkInput(this.paperCode)  
                ;

        ArrayList<GetData> list = new ArrayList<>();
        
        try {
            Connection conn = new ConnectDB().connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {

                GetData info = new GetData();
                info.setExamId(rs.getString("ExamIndex"));
                info.setPaperCode(rs.getString("PaperCode"));
                info.setProgName(rs.getString("ProgName"));
                info.setExamMark(rs.getInt("Exam"));
                list.add(info);
            }
            
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if(list.isEmpty())
            throw new CustomException("No data found.");
        else
            return list;
    }
}
