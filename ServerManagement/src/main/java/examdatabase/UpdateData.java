/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examdatabase;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import querylist.Paper;
import querylist.SessionAndDate;
import querylist.Venue;

/**
 *
 * @author Krissy
 */
public class UpdateData {
    
     //CandidateInfo
    public String ic = ""; 
    public String name = ""; 
    public String regNum = "";
    
    //Programme
    public String progName = "";
    public String faculty = "";
    
    //Paper Info
    public String paperCode = "";
    
    //Paper
    public String date = "";
    
    //StudentMark
    public Integer coursework ;
    public Integer practical;
    
    public UpdateData(){
        
    }
    
    public UpdateData(  String regNum, String paperCode,
                        Integer practical, Integer coursework){
      
        this.regNum = regNum;
        this.paperCode = paperCode;
        this.coursework = coursework;
        this.practical = practical;
    }
    
    public void setMark(){
        String sql = "UPDATE StudentMark "
                + "SET Coursework = ?, Practical = ? "
                + "WHERE RegNum = ? AND PI_id = (SELECT PI_id FROM PaperInfo WHERE PaperCode = ? );";
        
        try (Connection conn = new ConnectDB().connect();
                ){
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            if(coursework != null)
            pstmt.setInt(1,coursework);
            
            if(practical != null)
            pstmt.setInt(2,practical);
            
            pstmt.setString(3,regNum);
            pstmt.setString(4,paperCode);
            
            pstmt.executeUpdate();
            
      
          pstmt.close();
          conn.close();
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          System.out.println(e.getMessage());
          System.exit(0);
        }
        
    }
    
    public void setPaperToStaff(String paperCode, String lecturer, String tutor,
            String programme, String programmeGroup, String examWeightage, 
            String programmeWeightage, String practicalWeightage){
//        
//        String sql = "SELECT CandidateInfo.IC, CandidateInfo.Name, CandidateInfo.RegNum "
//                + ", Programme.Name AS ProgName, Programme.Faculty "
//                + ", StudentMark.Coursework, StudentMark.Practical"
//                + ", PaperInfo.PaperCode, PaperInfo.PaperDescription "
//                + " FROM StudentMark "
//                + " LEFT OUTER JOIN CandidateInfo ON StudentMark.RegNum = CandidateInfo.RegNum "
//                + " LEFT OUTER JOIN CandidateAttendance ON CandidateInfo.IC = CandidateAttendance.CandidateInfoIC "
//                + " LEFT OUTER JOIN Programme ON CandidateInfo.Programme_id = Programme.Programme_id "
//                + " LEFT OUTER JOIN Paper ON CandidateAttendance.Paper_id = Paper.Paper_id "
//                + " LEFT OUTER JOIN PaperInfo ON PaperInfo.PI_id = StudentMark.PI_id "
//                + " LEFT OUTER JOIN Venue ON Paper.Venue_id = Venue.Venue_id "
//                + " WHERE CandidateInfo.IC " + checkInput(this.getIc())
//                + " AND CandidateInfo.Name " + checkInput(this.getName())
//                + " AND CandidateInfo.RegNum "+ checkInput(this.getRegNum())
//                + " AND ProgName "+ checkInput(this.getProgName())
//                + " AND Programme.Faculty "+ checkInput(this.getFaculty())
//                + " AND PaperInfo.PaperCode "+ checkInput(this.getPaperCode())  ;
        
    }
    
    public void setVenueAndSessionForPaper(Integer paper_id, Integer venue_id, Integer session_id, Integer candStartNo){
        String sql = "UPDATE Paper "
//                + " SET "+ Paper.VENUE_ID + " = "
//                + "(SELECT "+ Venue.ID + " FROM " + Venue.TABLE + " WHERE " + Venue.NAME + " = ? ) , "
//                + Paper.SESSION_ID + " = "
//                + "(SELECT "+ SessionAndDate.ID + " FROM " + SessionAndDate.TABLE + " WHERE " + SessionAndDate.SESSION + " = ? AND "+SessionAndDate.DATE+" = ? ) , "
//                + Paper.PAPER_START_NO + " = ? " 
                + " SET "+ Paper.VENUE_ID + " = ? ,"
                + Paper.SESSION_ID + " = ? , "
                + Paper.PAPER_START_NO + " = ? "
                + "WHERE " + Paper.ID + " = ? ";
        
        try (Connection conn = new ConnectDB().connect();){
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            if(venue_id != null && venue_id != 0 &&
                    session_id != null && session_id != 0 &&
                    candStartNo != null && candStartNo != 0){
                pstmt.setInt(1,venue_id);
                pstmt.setInt(2,session_id);
                pstmt.setInt(3,candStartNo);
                pstmt.setInt(4,paper_id);
            }
            else{
                pstmt.setInt(1,0);
                pstmt.setInt(2,0);
                pstmt.setInt(3,0);
                pstmt.setInt(4,paper_id);
            }
            
            pstmt.executeUpdate();
            
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(UpdateData.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
}
