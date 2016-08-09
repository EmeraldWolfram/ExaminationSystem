/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import querylist.CandidateAttendance;
import querylist.CandidateInfo;
import querylist.ChiefAndRelief;
import querylist.Collector;
import querylist.Invigilator;
import querylist.Paper;
import querylist.PaperInfo;
import querylist.Programme;
import querylist.SessionAndDate;
import querylist.StaffInfo;
import querylist.Venue;

/**
 *
 * @author Krissy
 */
public class ChiefData {
    String id;
    String password;
    String block;
    String date;
    String time;
    
    boolean valid = false;
    String status;
    StaffInfo chiefStaff;
    ChiefAndRelief chief;
    
    ArrayList<CandidateAttendance> cddAttdList;
    ArrayList<CandidateInfo> cddInfoList;
    ArrayList<ChiefAndRelief> chiefAndReliefList;
    ArrayList<Collector> collectorList;
    ArrayList<Invigilator> invigilatorList;
    ArrayList<Paper> paperList;
    ArrayList<PaperInfo> paperInfoList;
    ArrayList<Programme> programmeList;
    ArrayList<SessionAndDate> sessionList;
    ArrayList<StaffInfo> staffInfoList;
    ArrayList<Venue> venueList;
    
    public ChiefData(){}
    
    public ChiefData(   String id,
                        String block
                        ){
        this.id = id;
        this.block = block;
    }
    
    public ChiefData(   String id,
                        String password,
                        String block
                        ){
        this.id = id;
        this.password = password;
        this.block = block;
    }
    
    private Integer getSession_id(){
        Integer session_id = null;
        return session_id;
    }
    
    /**
     * @Brief   To get the chief info base on the id
     * @throws SQLException 
     */
    public void getChief_id() throws SQLException{
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM StaffInfo WHERE StaffID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, this.id);

        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            chiefStaff = new StaffInfo(  result.getInt("SI_id"),
                                        result.getString("StaffID"),
                                        result.getString("Name")
                                        );
        }
        
        
        result.close();
        ps.close();
        conn.close();
    }
    
    /**
     * @Brief   To get the status of there staff check whether is chief or not
     * @throws SQLException 
     */
    private void getChiefStatus() throws SQLException{
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM ChiefAndRelief WHERE SI_id = ? AND Block = ? AND Status = 'CHIEF' ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, chief.getSi_id());
        ps.setString(2, this.block);

        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            this.chief = new ChiefAndRelief(    result.getInt("CR_id"),
                                                result.getInt("SI_id"),
                                                result.getString("Block"),
                                                result.getInt("Session_id"),
                                                result.getString("Status"),
                                                result.getString("Attendance"),
                                                result.getString("SignInTime")
                                                );
        }
        
        result.close();
        ps.close();
        conn.close();
        
    } 

    public ArrayList<Venue> getVenueList() throws SQLException{
        ArrayList<Venue> venueList = new ArrayList<>();
        
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM Venue WHERE Block = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, this.block);

        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            venueList.add(new Venue(    result.getInt("Venue_id"),
                                        result.getString("Block"),
                                        result.getString("Name"),
                                        result.getInt("Size")
                                    ));
        }
        
        result.close();
        ps.close();
        conn.close();
        
        return venueList;
    }
    
    /**
     * @Brief   To get the paper database base on the session id and block
     * @throws SQLException 
     */
    public ArrayList<Paper> getPaperList() throws SQLException{
        ArrayList<Paper> paperList = new ArrayList<>();
        
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM Paper WHERE Venue_id = ? AND Session_id = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, this.block);
        ps.setInt(2, getSession_id());

        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            paperList.add(new Paper(    result.getInt("Paper_id"),
                                        result.getInt("PI_id"),
                                        result.getInt("Venue_id"),
                                        result.getInt("PaperStartNo"),
                                        result.getInt("TotalCandidate"),
                                        result.getInt("Session_id"),
                                        result.getInt("Programme_id")
                                    ));
        }
        
        result.close();
        ps.close();
        conn.close();
        
        return paperList;
    }
    
    
    public ArrayList<CandidateAttendance> getCddAttdList(Integer paper_id) throws SQLException{
        ArrayList<CandidateAttendance> cddAttdList = new ArrayList<>();

        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * "
                + "FROM CandidateAttendance "
                + "WHERE Paper_id = "
                + "(SELECT Paper_id FROM Paper WHERE Venue_id = ? AND Session_id = ?) ";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, this.block);
        ps.setInt(2, getSession_id());

        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            cddAttdList.add(new CandidateAttendance(    
                                        result.getInt("CA_id"),
                                        result.getString("CandidateInfoIC"),
                                        result.getInt("Paper_id"),
                                        result.getString("Status"),
                                        result.getString("Attendance"),
                                        result.getInt("TableNumber")
                                    ));
        }
        
        result.close();
        ps.close();
        conn.close();
        
        return cddAttdList;
    }
    
    public ArrayList<CandidateInfo> getCddInfoList() throws SQLException{
        ArrayList<CandidateInfo> cddInfoList = new ArrayList<>();

        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * "
                + "FROM CandidateInfo "
                + "WHERE IC = "
                + "(SELECT CandidateInfoIC FROM CandidateAttendance WHERE Paper_id = "
                + "(SELECT Paper_id FROM Paper WHERE Venue_id = ? AND Session_id = ?) "
                + ")";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, this.block);
        ps.setInt(2, getSession_id());

        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            cddInfoList.add(new CandidateInfo(    
                                        result.getInt("CI_id"),
                                        result.getString("IC"),
                                        result.getString("Name"),
                                        result.getString("RegNum"),
                                        result.getInt("Programme_id"),
                                        result.getString("ExamID")
                                    ));
        }
        
        result.close();
        ps.close();
        conn.close();
        
        return cddInfoList;
    }
    
    public ArrayList<ChiefAndRelief> getChAndReList() throws SQLException{
        ArrayList<ChiefAndRelief> chAndReList = new ArrayList<>();
        
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM ChiefAndRelief WHERE SI_id = ? AND Block = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, chief.getSi_id());
        ps.setString(2, this.block);

        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            chAndReList.add(new ChiefAndRelief(    result.getInt("CR_id"),
                                                result.getInt("SI_id"),
                                                result.getString("Block"),
                                                result.getInt("Session_id"),
                                                result.getString("Status"),
                                                result.getString("Attendance"),
                                                result.getString("SignInTime")
                                                ));
        }
        
        result.close();
        ps.close();
        conn.close();
        
        return chAndReList;
    }
}
