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
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Krissy
 */
public class ServerComm {
    
    private CurrentTime time;
    
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
    
    public Staff staffSignIn(String id, String password) throws SQLException,Exception{
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
    public boolean staffVerify(String id, String password) throws SQLException{
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
    public Staff staffGetInfo(String id) throws SQLException, Exception{
        
        Staff staff = new Staff();
        Connection conn = new ConnectDB("FEB_MAR_2016.db").connect();
        String sql = "SELECT Venue.Name AS VenueName, InvigilatorAndAssistant.StaffID AS StaffIndex, "
                + "StaffInfo.Name AS StaffName "
                + ",* FROM InvigilatorAndAssistant "
                + "LEFT OUTER JOIN Paper ON Paper.PaperIndex = InvigilatorAndAssistant.PaperIndex "
                + "LEFT OUTER JOIN PaperInfo ON PaperInfo.PIIndex = Paper.PIIndex "
                + "LEFT OUTER JOIN Venue ON Venue.VenueIndex = Paper.VenueIndex "
                + "LEFT OUTER JOIN StaffInfo ON StaffInfo.StaffID = StaffIndex "
                + "WHERE StaffIndex = ? ";
//                + "AND Date = ? AND Session = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, id);
//        ps.setString(2, time.getDate());
//        ps.setString(3, getSessionType(Integer.parseInt(time.getTime())));
   
        ResultSet result = ps.executeQuery();
        if(result.next()){
         do{
            staff.setID(result.getString("StaffIndex"));
            staff.setName(result.getString("StaffName"));
            staff.setStatus(result.getString("Status"));
            staff.setAttendance(result.getString("Attendance"));
            staff.setVenue(result.getString("VenueName"));
            staff.setSession(result.getString("Session"));
            staff.setDate(result.getString("Date"));
        }while ( result.next() );
        }
        else
            throw new Exception("Invalid data in current session.");
        
        result.close();
        ps.close();
        conn.close();
        
        return staff;
    }
    
    /**
     * @brief   Get the session according to current time
     * @param   time  contain current time in integer type
     * @return  AM      From time 7:00AM to 11:00AM
     * @return  PM      From time 1:00PM to 4:00PM
     * @return  NULL    Other time
     */
    public String getSessionType(int time){
        if ((time >= 80000)&&(time <= 110000))
            return "AM";
        else if ((time >= 130000)&&(time <= 160000))
            return "PM";
        else 
            return "NULL";
            
    }
}
