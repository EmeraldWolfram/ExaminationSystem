/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import serverquerylist.CandidateAttendance;
import serverquerylist.CandidateInfo;
import serverquerylist.ChiefAndRelief;
import serverquerylist.Collector;
import serverquerylist.Invigilator;
import serverquerylist.Paper;
import serverquerylist.PaperInfo;
import serverquerylist.Programme;
import serverquerylist.SessionAndDate;
import serverquerylist.StaffInfo;
import serverquerylist.Venue;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.Field;
import static org.jooq.impl.DSL.*;
import org.json.JSONObject;

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
    String status = "";
    
    boolean valid = false;
    StaffInfo chiefStaff;
//    ChiefAndRelief chief;
    
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
                        ) throws SQLException, Exception{
        this.id = id;
        this.password = password;
        this.block = block;
        this.valid = staffVerify();
        
    }
    
    public Integer getSession_id(){
        Integer session_id = null;
        return 1;
    }
    
    public String getStatus(){
        return this.status;
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
     * @brief   To check the id and the password of the staff from database
     * @param   id            id of the staff
     * @param   password      password of the staff
     * @return match        The result of the checking ,true is correct while false is incorrect
     * @throws SQLException 
     */
    public boolean staffVerify() throws SQLException, Exception {
        boolean match = false;
        
        
        Connection conn = new ConnectDB("StaffDatabase.db").connect();
        String sql = "SELECT Username, Password FROM User where Username=? and Password=?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1,id);
        ps.setString(2,password);
        ResultSet result = ps.executeQuery();

        match = result.next(); 
        
        System.out.println(id+password+match);
        result.close();
        ps.close();
        conn.close();
            
        getChiefInfo();
        return match;
    }
    
    
    /**
     * @Brief   To get the status of there staff check whether is chief or not
     * @throws SQLException 
     */
    public void getChiefInfo() throws SQLException{
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM ChiefAndRelief "
                + "WHERE SI_id = 3 "//(SELECT SI_id FROM StaffInfo WHERE StaffID = ?) "
                + "AND Block = ? AND Status = 'CHIEF' ";
        PreparedStatement ps = conn.prepareStatement(sql);
//        ps.setString(1, this.id);
        ps.setString(1, this.block);

        ResultSet result = ps.executeQuery();
        
        if (result.next()) {
            do{
//                this.chief = new ChiefAndRelief(    result.getInt("CR_id"),
//                                                    result.getInt("SI_id"),
//                                                    result.getString("Block"),
//                                                    result.getInt("Session_id"),
//                                                    result.getString("Status"),
//                                                    result.getString("Attendance"),
//                                                    result.getString("SignInTime")
//                                                    );
                this.status = result.getString("Status");
            }while ( result.next() );
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
        String sql = "SELECT * FROM Paper";
//                + " LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id ";
//                + " WHERE Block = ?";
//                + " AND Session_id = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
//        ps.setString(1, this.block);
//        ps.setInt(2, getSession_id());

        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            paperList.add(new Paper(    result.getInt("Paper_id"),
                                        result.getInt("PI_id"),
                                        result.getInt("Venue_id"),
                                        result.getInt("PaperStartNo"),
                                        result.getInt("TotalCandidate"),
                                        result.getInt("Session_id"),
                                        result.getInt("Programme_id"),
                                        result.getString(Paper.BUNDLE_ID),
                                        result.getString(Paper.COLLECTOR)
                                    ));
        }
        
        result.close();
        ps.close();
        conn.close();
        
        return paperList;
    }
    
    
    public ArrayList<CandidateAttendance> getCddAttdList(String block) throws SQLException{
        ArrayList<CandidateAttendance> cddAttdList = new ArrayList<>();

        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * "
                + "FROM CandidateAttendance "
                + "LEFT OUTER JOIN Paper ON Paper.Paper_id = CandidateAttendance.Paper_id "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + "WHERE Block = ? ";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, block);

        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            cddAttdList.add(new CandidateAttendance(    
                                        result.getInt(CandidateAttendance.ID),
                                        result.getInt(CandidateAttendance.CI_ID),
                                        result.getInt(CandidateAttendance.PAPER_ID),
                                        result.getString(CandidateAttendance.STATUS),
                                        result.getString(CandidateAttendance.ATTENDANCE),
                                        result.getInt(CandidateAttendance.TABLE_NUMBER)
                                    ));
//            System.out.println("CandidateInfoIC: " + result.getString("CandidateInfoIC"));
        }
        
        result.close();
        ps.close();
        conn.close();
        
        return cddAttdList;
    }
    
    public ArrayList<CandidateInfo> getCddInfoList(String block) throws SQLException{
        ArrayList<CandidateInfo> cddInfoList = new ArrayList<>();

        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * "
                + "FROM CandidateInfo "
                + "LEFT OUTER JOIN CandidateAttendance ON CandidateAttendance.CI_id = CandidateInfo.CI_id "
                + "LEFT OUTER JOIN Paper ON Paper.Paper_id = CandidateAttendance.Paper_id "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + "WHERE Block = ? ";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, block);

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
        String sql = "SELECT * FROM ChiefAndRelief WHERE Block = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1,block);

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
    
    public ArrayList<Collector> getCollectorList() throws SQLException{
        ArrayList<Collector> collectorList = new ArrayList<>();

        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM " + Collector.TABLE ;
//                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = InvigilatorAndAssistant.Venue_id "
//                + " WHERE Session_id = ? AND Block = ?";
        Statement stmt  = conn.createStatement();
        ResultSet result    = stmt.executeQuery(sql);
        
        while ( result.next() ){
            collectorList.add(new Collector(result.getInt(Collector.ID),
                                            result.getInt(Collector.PAPER_ID),
                                            result.getString(Collector.STAFF_ID)
                                ));
        }
        
        result.close();
        stmt.close();
        conn.close();
        
        return collectorList;
    }
    
    public ArrayList<Invigilator> getInvigilatorList() throws SQLException{
        ArrayList<Invigilator> invigilatorList = new ArrayList<>();

        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM InvigilatorAndAssistant "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = InvigilatorAndAssistant.Venue_id "
                + "WHERE Session_id = ? AND Block = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, getSession_id());
        ps.setString(2, this.block);

        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            invigilatorList.add(new Invigilator(    result.getInt("IA_id"),
                                                    result.getString("StaffID"),
                                                    result.getString("Status"),
                                                    result.getString("Attendance"),
                                                    result.getString("SignInTime"),
                                                    result.getInt("Venue_id"),
                                                    result.getInt("Session_id")
                                                    ));
        }
        
        result.close();
        ps.close();
        conn.close();
        
        return invigilatorList;
    }
    
    public ArrayList<PaperInfo> getPaperInfoList() throws SQLException{
        ArrayList<PaperInfo> paperInfoList = new ArrayList<>();

        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM PaperInfo "
                + "LEFT OUTER JOIN Paper ON Paper.PI_id = PaperInfo.PI_id "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + "WHERE Block = ? AND Session_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, this.block);
        ps.setInt(2, getSession_id());

        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            paperInfoList.add(new PaperInfo(    result.getInt("PI_id"),
                                                    result.getString("PaperCode"),
                                                    result.getString("PaperDescription")
                                                    ));
        }
        
        result.close();
        ps.close();
        conn.close();
        
        return paperInfoList;
    }
    
    public ArrayList<Programme> getProgrammeList() throws SQLException{
        ArrayList<Programme> programmeList = new ArrayList<>();

        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM Programme "
                + "LEFT OUTER JOIN Paper ON Paper.Programme_id = Programme.Programme_id "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + "WHERE Block = ? AND Session_id = ?";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, this.block);
        ps.setInt(2, getSession_id());

        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            programmeList.add(new Programme(    result.getInt("Programme_id"),
                                                result.getString("Name"),
                                                result.getString("Faculty"),
                                                result.getInt("Group")
                                                    ));
        }
        
        result.close();
        ps.close();
        conn.close();
        
        return programmeList;
    }
    
    public ArrayList<StaffInfo> getStaffInfoList() throws SQLException{
        ArrayList<StaffInfo> staffInfoList = new ArrayList<>();
        
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM StaffInfo "
                + "LEFT OUTER JOIN InvigilatorAndAssistant ON InvigilatorAndAssistant.StaffID = StaffInfo.StaffID "
                + "LEFT OUTER JOIN ChiefAndRelief ON ChiefAndRelief.SI_id = StaffInfo.SI_id "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = InvigilatorAndAssistant.Venue_id "
                + "WHERE (Venue.Block = ? AND InvigilatorAndAssistant.Session_id = ?) "
                + "OR (ChiefAndRelief.Block = ? AND ChiefAndRelief.Session_id = ?) ";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, this.block);
        ps.setInt(2, getSession_id());
        ps.setString(3, this.block);
        ps.setInt(4, getSession_id());

        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            staffInfoList.add(new StaffInfo(    result.getInt("SI_id"),
                                                result.getString("StaffID"),
                                                result.getString("Name")
                                                    ));
        }
        
        result.close();
        ps.close();
        conn.close();
        return staffInfoList;
    }
    
    
    public void updateCddAttdList(ArrayList<CandidateAttendance> cddAttdList) throws SQLException{
        Connection conn = new ConnectDB().connect();
        for(int i=0; i<cddAttdList.size(); i++){
            String sql = "INSERT OR REPLACE INTO CandidateAttendance VALUES"
                   + "(?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, cddAttdList.get(i).getCa_id());
            ps.setInt(2, cddAttdList.get(i).getCi_id());
            ps.setInt(3, cddAttdList.get(i).getPaper_id());
            ps.setString(4, cddAttdList.get(i).getStatus());
            ps.setString(5, cddAttdList.get(i).getAttendance());
            ps.setInt(6, cddAttdList.get(i).getTableNo());

            ps.executeUpdate();
            ps.close();
           
       }
        conn.close();
   }
    
    public void updateCddInfoList(ArrayList<CandidateInfo> cddInfoList) throws SQLException{
        Connection conn = new ConnectDB().connect();
        for(int i=0; i<cddInfoList.size(); i++){
            String sql = "INSERT OR REPLACE INTO CandidateInfo VALUES"
                   + "(?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, cddInfoList.get(i).getCi_id());
            ps.setString(2, cddInfoList.get(i).getIc());
            ps.setString(3, cddInfoList.get(i).getName());
            ps.setString(4, cddInfoList.get(i).getRegNum());
            ps.setInt(5, cddInfoList.get(i).getProgramme_id());
            ps.setString(6, cddInfoList.get(i).getExamId());

            ps.executeUpdate();
            ps.close();
           
       }
        conn.close();
   }
    
    public void updateChAndRe(ArrayList<ChiefAndRelief> chAndReList) throws SQLException{
        Connection conn = new ConnectDB().connect();
        for(int i=0; i<chAndReList.size(); i++){
            String sql = "INSERT OR REPLACE INTO ChiefAndRelief VALUES"
                   + "(?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, chAndReList.get(i).getCr_id());
            ps.setInt(2, chAndReList.get(i).getSi_id());
            ps.setString(3, chAndReList.get(i).getBlock());
            ps.setInt(4, chAndReList.get(i).getSession_id());
            ps.setString(5, chAndReList.get(i).getStatus());
            ps.setString(6, chAndReList.get(i).getAttendance());
            ps.setString(7, chAndReList.get(i).getSignInTime());

            ps.executeUpdate();
            ps.close();
           
       }
        conn.close();
   }
    
    public void updateCollector(ArrayList<Collector> collector) throws SQLException{
        Connection conn = new ConnectDB().connect();
        for(int i=0; i<collector.size(); i++){
            String sql = "INSERT OR REPLACE INTO Collector VALUES"
                   + "(?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, collector.get(i).getCollector_id());
            ps.setInt(2, collector.get(i).getPaper_id());
            ps.setString(3, collector.get(i).getStaffId());
            ps.executeUpdate();
            ps.close();
           
       }
        conn.close();
   }
    
    public void updateInvigilator(ArrayList<Invigilator> invList) throws SQLException{
        Connection conn = new ConnectDB().connect();
        for(int i=0; i<invList.size(); i++){
            String sql = "INSERT OR REPLACE INTO InvigilatorAndAssistant VALUES"
                   + "(?,?,?,?,?,?,?)";
            
            System.out.println(invList.get(i).getStatus()+ "  " +invList.get(i).getstaffId());
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, invList.get(i).getIa_id());
            ps.setString(2, invList.get(i).getstaffId());
            ps.setString(3, invList.get(i).getStatus());
            ps.setString(4, invList.get(i).getAttendance());
            ps.setString(5, invList.get(i).getSignInTime());
            ps.setInt(6, invList.get(i).getVenue_id());
            ps.setInt(7, invList.get(i).getSession_id());

            ps.executeUpdate();
            ps.close();
           
       }
        conn.close();
   }
    
    public void updatePaper(ArrayList<Paper> paperList) throws SQLException{
        Connection conn = new ConnectDB().connect();
        for(int i=0; i<paperList.size(); i++){
            String sql = "INSERT OR REPLACE INTO Paper VALUES"
                   + "(?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, paperList.get(i).getPaper_id());
            ps.setInt(2, paperList.get(i).getPi_id());
            ps.setInt(3, paperList.get(i).getVenue_id());
            ps.setInt(4, paperList.get(i).getPaperStartNo());
            ps.setInt(5, paperList.get(i).getTotalCandidate());
            ps.setInt(6, paperList.get(i).getSession_id());
            ps.setInt(7, paperList.get(i).getProgramme_id());
            ps.setString(8, paperList.get(i).getBundleId());
            ps.setString(9, paperList.get(i).getCollector());

            ps.executeUpdate();
            ps.close();
           
       }
        conn.close();
   }
    
    public void updatePaperInfo(ArrayList<PaperInfo> paperInfoList) throws SQLException{
        Connection conn = new ConnectDB().connect();
        for(int i=0; i<paperInfoList.size(); i++){
            String sql = "INSERT OR REPLACE INTO PaperInfo VALUES"
                   + "(?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, paperInfoList.get(i).getPi_id());
            ps.setString(2, paperInfoList.get(i).getPaperCode());
            ps.setString(3, paperInfoList.get(i).getPaperDescrip());

            ps.executeUpdate();
            ps.close();
           
       }
        conn.close();
   }
    
    public void updateProgramme(ArrayList<Programme> programmeList) throws SQLException{
        Connection conn = new ConnectDB().connect();
        for(int i=0; i<programmeList.size(); i++){
            String sql = "INSERT OR REPLACE INTO Programme VALUES"
                   + "(?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, programmeList.get(i).getProgramme_id());
            ps.setString(2, programmeList.get(i).getName());
            ps.setString(3, programmeList.get(i).getFaculty());
            ps.setInt(4, programmeList.get(i).getGroup());

            ps.executeUpdate();
            ps.close();
           
       }
        conn.close();
   }
    
    public void updateStaffInfo(ArrayList<StaffInfo> staffInfoList) throws SQLException{
        Connection conn = new ConnectDB().connect();
        for(int i=0; i<staffInfoList.size(); i++){
            String sql = "INSERT OR REPLACE INTO StaffInfo VALUES"
                   + "(?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, staffInfoList.get(i).getSi_id());
            ps.setString(2, staffInfoList.get(i).getStaffId());
            ps.setString(3, staffInfoList.get(i).getName());

            ps.executeUpdate();
            ps.close();
           
       }
        conn.close();
   }
    
    public void updateVenue(ArrayList<Venue> venueList) throws SQLException{
        Connection conn = new ConnectDB().connect();
        System.out.println("Size: "+venueList.size());
        for(int i=0; i<venueList.size(); i++){
            String sql = "INSERT OR REPLACE INTO Venue VALUES"
                   + "(?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, venueList.get(i).getVenue_id());
            ps.setString(2, venueList.get(i).getBlock());
            ps.setString(3, venueList.get(i).getName());
            ps.setInt(4, venueList.get(i).getSize());

            ps.executeUpdate();
            ps.close();
           
       }
        conn.close();
   }
    
    

}
