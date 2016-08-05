/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

import querylist.AttdList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import querylist.Papers;
import querylist.CddPapers;


/**
 *
 * @author Krissy
 */
public class ServerComm {
    
    static private CurrentTime time;
    
    public ServerComm(){
        
    }
    
    /**
     * @brief to set the CurrentTime value
     * @param time  CurrentTime object
     */
    public void setCurrentTime(CurrentTime time){
        this.time = time;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
    public static Staff staffSignIn(String id, String password) throws SQLException,Exception{
        Staff staff = new Staff();
       
        
        if(staffVerify(id,password)){
            staff = staffGetInfo(id);
        }
        else
            throw new Exception("Wrong id or password.");

        return staff;
    }
    
    /**
     * @brief   To check the id and the password of the staff from database
     * @param   id            id of the staff
     * @param   password      password of the staff
     * @return match        The result of the checking ,true is correct while false is incorrect
     * @throws SQLException 
     */
    public static boolean staffVerify(String id, String password) throws SQLException{
        boolean match;
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
    public static Staff staffGetInfo(String id) throws SQLException, Exception{
        
        Staff staff = new Staff();
        Connection conn = new ConnectDB("ChiefDataBase.db").connect();
        String sql = "SELECT Venue.Name AS VenueName, InvigilatorAndAssistant.StaffID AS Staff_id, "
                + "StaffInfo.Name AS StaffName "
                + ",* FROM InvigilatorAndAssistant "
                + "LEFT OUTER JOIN Paper ON Paper.Paper_id = InvigilatorAndAssistant.Paper_id "
                + "LEFT OUTER JOIN PaperInfo ON PaperInfo.PI_id = Paper.PI_id "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + "LEFT OUTER JOIN StaffInfo ON StaffInfo.StaffID = Staff_id "
                + "WHERE Staff_id = ? ";
//                + "AND Date = ? AND Session = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, id);
//        ps.setString(2, time.getDate());
//        ps.setString(3, time.getSession());
   
        ResultSet result = ps.executeQuery();
        while(result.next()){
            staff.setID(result.getString("Staff_id"));
            staff.setName(result.getString("StaffName"));
            staff.setStatus(result.getString("Status"));
            staff.setAttendance(result.getString("Attendance"));
            staff.setVenue(result.getString("VenueName"));
            staff.setSession(result.getString("Session"));
            staff.setDate(result.getString("Date"));
 
        }
        
        result.close();
        ps.close();
        conn.close();
        
        return staff;
    }
    

    
    /**
     * 
     * @param venue     contains the name of venue
     * @return attendance list of the venue 
     */
    public static ArrayList<AttdList> getAttdList(String venue) throws SQLException{
        ArrayList<AttdList> attdList = new ArrayList<>();
        AttdList attd;
        Connection conn = new ConnectDB("ChiefDataBase.db").connect();
        String sql = "SELECT CandidateInfo.Name AS CandidateName, CandidateAttendance.Status AS CandidateStatus, "
                + "Venue.Name AS VenueName, CandidateAttendance.Attendance AS CandAttd ,"
                + "Programme.Name AS ProgrammeName"
                + ",* FROM Venue "
                + "LEFT OUTER JOIN Paper ON Paper.Venue_id = Venue.Venue_id "
                + "LEFT OUTER JOIN PaperInfo ON PaperInfo.PI_id = Paper.PI_id "
                + "LEFT OUTER JOIN CandidateAttendance ON CandidateAttendance.Paper_id = Paper.Paper_id "
                + "LEFT OUTER JOIN CandidateInfo ON CandidateInfo.IC = CandidateAttendance.CandidateInfoIC "
                + "LEFT OUTER JOIN Programme ON Programme.Programme_id = CandidateInfo.Programme_id "
                + "WHERE VenueName = ? ";
//                + "AND Date = ? AND Session = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        
        ps.setString(1, venue);
//        ps.setString(2, time.getDate());
//        ps.setString(3, time.getSession());
   
        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            attdList.add(new AttdList(result.getString("ExamID"), result.getString("RegNum"),
                                       result.getString("CandidateStatus"), result.getString("PaperCode"),
                                       result.getString("ProgrammeName"), result.getString("CandAttd")));
        }
        
        result.close();
        ps.close();
        conn.close();
        
        return attdList;
    }
    
    /**
     * 
     * @param venue     contains the name of venue
     * @return paper list that are being examined at the venue 
     */
    public static ArrayList<Papers> getPapers(String venue) throws SQLException{
        ArrayList<Papers> papers = new ArrayList<>();
        AttdList attd;
        Connection conn = new ConnectDB("ChiefDataBase.db").connect();
        String sql = "SELECT Venue.Name AS VenueName "
                + ",* FROM Venue "
                + "LEFT OUTER JOIN Paper ON Paper.Venue_id = Venue.Venue_id "
                + "LEFT OUTER JOIN PaperInfo ON PaperInfo.PI_id = Paper.PI_id "
                + "WHERE VenueName = ? ";
//                + "AND Date = ? AND Session = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, venue);
//        ps.setString(2, time.getDate());
//        ps.setString(3, time.getSession());
   
        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            papers.add(new Papers(result.getString("PaperCode"), result.getString("PaperDescription"),
                                result.getString("PaperStartNo"), result.getString("TotalCandidate")));
            
        }

        result.close();
        ps.close();
        conn.close();

        return papers;
    }
    
    public static void checkCollector(){}
    
    public static ArrayList<CddPapers> getCddPapers(String candidateID) throws SQLException{
        ArrayList<CddPapers> cddPapers = new ArrayList<>();
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT Venue.Name AS VenueName "
                + ",* FROM CandidateInfo "
                + "LEFT OUTER JOIN CandidateAttendance ON CandidateAttendance.CandidateInfoIC = CandidateInfo.IC "
                + "LEFT OUTER JOIN Paper ON Paper.Paper_id = CandidateAttendance.Paper_id "
                + "LEFT OUTER JOIN PaperInfo ON PaperInfo.PI_id = Paper.PI_id "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + "WHERE RegNum = ? "
                + "AND Date = ? AND Session = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, candidateID);
        ps.setString(2, time.getDate());
        ps.setString(3, time.getSession());

        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            cddPapers.add(new CddPapers(result.getString("PaperCode"),
                                        result.getString("PaperDescription"),
                                        result.getString("Date"),
                                        result.getString("Session"),
                                        result.getString("VenueName")
                                        ));
        }
        
        result.close();
        ps.close();
        conn.close();
        
        return cddPapers;
    }
    
    public static void updateCandidateAttendence(ArrayList<AttdList> attdList) throws SQLException{
        String sql = "UPDATE CandidateAttendence "
                + "SET Attendance = ?, TableNumber = ? "
                + "WHERE CandidateInfoIC = (SELECT IC FROM CandidateInfo WHERE RegNum = ? ) ";
    
        Connection conn = new ConnectDB("ChiefDataBase.db").connect();
        
         PreparedStatement pstmt = conn.prepareStatement(sql);
         
         for(int i = 0; i < attdList.size(); i++)  {
             
            pstmt.setString(1,attdList.get(i).getAttendance());
            pstmt.setInt(2,attdList.get(i).getTableNo());
            pstmt.setString(3,attdList.get(i).getRegNum());
            
            pstmt.executeUpdate();
            
         }
          pstmt.close();
          conn.close();
    }
}

