/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examdatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import querylist.CandidateAttendance;
import querylist.CandidateInfo;

/**
 *
 * @author Krissy
 */
public class DataWriter {
    
    public static final String ERR_INVALID_PROG_GROUP = "Invalid Programme Group";
    public static final String EXIST_CANDIDATE = "Candidate already existed";
    
    public DataWriter(){
        
    }
    
    public void insertCandidate(String ic, String name, String regNum, 
                                String programmeName, String programmeGroup,
                                String examId) throws Exception {
        
        if (!new GetData().checkDataIsAvailable(CandidateInfo.TABLE, CandidateInfo.CANDIDATE_INFO_IC, ic)){
        String sql = "INSERT OR REPLACE INTO "
                    + "CandidateInfo(IC, Name, RegNum, Programme_id, ExamID) "
                    + "VALUES(?,?,?,(SELECT Programme_id FROM Programme WHERE Programme_Group =? AND Name =?), ?) ";
            
            Connection conn = new ConnectDB().connect();
            
            PreparedStatement ps = conn.prepareStatement(sql);
            
            if(isNumeric(programmeGroup)){
                ps.setString(1,ic);
                ps.setString(2,name);
                ps.setString(3,regNum);
                ps.setString(5,programmeName);
                ps.setInt(4,Integer.parseInt(programmeGroup));
                ps.setString(6,examId);
            }
            else
                throw new Exception(ERR_INVALID_PROG_GROUP);
            
            ps.executeUpdate();
            ps.close();
        
            conn.close();
        }
        else{
            throw new Exception(EXIST_CANDIDATE);
        }
            
    }
    
    public void removeCandidate(Integer candidate_id) throws SQLException{
        String sql = " DELETE FROM " + CandidateInfo.TABLE +
                    " WHERE " + CandidateInfo.ID + " = ? ";
        
        Connection conn = new ConnectDB().connect();
        PreparedStatement ps = conn.prepareStatement(sql);
        
        ps.setInt(1, candidate_id);
        ps.executeUpdate();
        conn.close();
    }
    
    public void addCandidateAttendance(String candidateIC, ArrayList<String> paperId) throws SQLException{
        
        Connection conn = new ConnectDB().connect();
        
        for(int i = 0; i < paperId.size(); i++){
            String sql = "INSERT INTO "
                    + CandidateAttendance.TABLE + "(" 
                    + CandidateAttendance.CI_ID
                    + "," + CandidateAttendance.PAPER_ID + ","
                    + CandidateAttendance.STATUS + "," + CandidateAttendance.ATTENDANCE + "," + CandidateAttendance.TABLE_NUMBER + ") "
                    + "VALUES("+" (SELECT " + CandidateInfo.ID + " FROM " + CandidateInfo.TABLE 
                    +" WHERE " +CandidateInfo.CANDIDATE_INFO_IC +" = ?) "
                    + ",?,?,?,?) ";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,candidateIC);
            ps.setInt(2,Integer.parseInt(paperId.get(i)));
            ps.setString(3,CandidateAttendance.ELIGIBLE);
            ps.setString(4,CandidateAttendance.ABSENT);
            ps.setInt(5,0);
            
            ps.executeUpdate();
            ps.close();
        }
        
        conn.close();
        
    }
    
    public void addStudentMark(String regNum, ArrayList<String> paperId) throws SQLException{
        
        Connection conn = new ConnectDB().connect();
        
        for(int i = 0; i < paperId.size(); i++){
            String sql = "INSERT INTO StudentMark"
                    + "(RegNum, PI_id, Pratical, Coursework, Exam)"
                    + "VALUES(?,?,?,?,?) ";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,regNum);
            ps.setInt(2,Integer.parseInt(paperId.get(i)));
            ps.setInt(3,0);
            ps.setInt(4,0);
            ps.setInt(5,0);
            
            ps.executeUpdate();
            ps.close();
        }
        
        conn.close();
        
    }
    
    
    
    public void removeCandidateAttendance(String candidateIC) throws SQLException{
        String sql = " DELETE FROM " + CandidateAttendance.TABLE +
                    " WHERE " + CandidateAttendance.CI_ID + " = " +
                    " (SELECT " + CandidateInfo.ID + " FROM " + CandidateInfo.TABLE + 
                    " WHERE " +CandidateInfo.CANDIDATE_INFO_IC +" = ?) ";
        
        Connection conn = new ConnectDB().connect();
        PreparedStatement ps = conn.prepareStatement(sql);
        
        ps.setString(1, candidateIC);
        ps.executeUpdate();
        conn.close();
    }
    
    public void insertPaper(String paperCodeField, String paperNameField, String lecturerField, 
                                String tutorField, String facultyField) throws SQLException {
        
        Connection conn = new ConnectDB().connect();
            String sql = "INSERT OR REPLACE INTO "
                    + "PaperInfo(PaperCode, PaperDecription) "
                    + "VALUES(?,?) ";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setString(1,paperCodeField);
            ps.setString(2,paperNameField);
//            ps.setInt(3,Integer.parseInt(programmeGroup));
//            ps.setInt(4,Integer.parseInt(programmeGroup));
            
            ps.executeUpdate();
            ps.close();
            conn.close();
            
    }
    
    public boolean isNumeric(String str)  {  
      try  
      {  
        double d = Double.parseDouble(str);  
      }  
      catch(NumberFormatException nfe)  
      {  
        return false;  
      }  
      return true;  
    }

}
