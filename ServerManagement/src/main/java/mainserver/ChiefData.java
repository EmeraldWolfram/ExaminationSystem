/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainserver;

import examdatabase.CurrentTime;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.Field;
import static org.jooq.impl.DSL.*;
import org.json.JSONObject;
import querylist.CddPaper;

/**
 *
 * @author Krissy
 */
public class ChiefData {
//    String id;
//    String password;
//    String block;
//    String date;
//    String time;
//    String status = "";
    
    //boolean valid = false;
    StaffInfo chiefStaff;
    
    public ChiefData(){}
    
//    public ChiefData(   String id,
//                        String block
//                        ){
//        this.id = id;
//        this.block = block;
//    }
//    
//    public ChiefData(   String id,
//                        String password,
//                        String block
//                        ) throws SQLException, Exception{
//        this.id = id;
//        this.password = password;
//        this.block = block;
//        
//    }
    
    public void setChiefSignInTime(String id) throws SQLException{
        Connection conn = new ConnectDB().connect();
        String sql = "UPDATE ChiefAndRelief "
                + "SET SignInTime = ? "
                + "WHERE SI_id = (SELECT SI_id FROM StaffInfo WHERE StaffID = ?);";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, new CurrentTime().getCurrentTime().toString());
        ps.setString(2, id);
        ps.executeUpdate();
        
        ps.close();
        conn.close();
        
    }
    
    public void setInvSignInTime(String id) throws SQLException{
        Connection conn = new ConnectDB().connect();
        String sql = "UPDATE InvigilatorAndAssistant "
                + "SET SignInTime = ? "
                + "WHERE StaffID = ? ;";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, new CurrentTime().getCurrentTime().toString());
        ps.setString(2, id);
        ps.executeUpdate();
        
        ps.close();
        conn.close();
        
    }
    
    public Integer getSession_id(){
        Integer session_id = null;
        return 1;
    }
    
    public String getStatus(String id, String block) throws SQLException{
        String status = null;
        Connection conn = new ConnectDB().connect();
        
        String sql = "SELECT * FROM ChiefAndRelief "
                + "LEFT OUTER JOIN StaffInfo ON StaffInfo.SI_id = ChiefAndRelief.SI_id "
                + "WHERE StaffID = ? AND Block = ?";
                
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, id);
        ps.setString(2, block);
        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            status = result.getString("Status");
        }
        
        return status;
    }
    
    /**
     * @Brief   To get the chief info base on the id
     * @throws SQLException 
     */
/*    public void getChief_id() throws SQLException{
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
    }*/
    
    /**
     * @brief   To check the id and the password of the staff from database
     * @param   id            id of the staff
     * @param   password      password of the staff
     * @return match        The result of the checking ,true is correct while false is incorrect
     * @throws SQLException 
     */
    public String getStaffPassword(String id) throws SQLException, Exception {
        boolean match = false;
        String password = null;
        Connection conn = new ConnectDB("StaffDatabase.db").connect();
        String sql = "SELECT Username, Password FROM User where Username=? ";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1,id);
//        ps.setString(2,password);
        ResultSet result = ps.executeQuery();

//        match = result.next(); 
        if(result.next()){
            password = result.getString("Password");
        }
        
        System.out.println(id+password+match);
        result.close();
        ps.close();
        conn.close();
            
        return password;
    }
    
    public boolean verifyStaff(String id, String hashPass, String randomMsg){
        
        try {
            String password = getStaffPassword(id);
            
            Hmac hmac = new Hmac();
            String encodedPassword = hmac.encode(password, randomMsg);
            System.out.println("hashPass: "+hashPass);
            System.out.println("encodedPassword: "+encodedPassword);
            System.out.println("randomMsg: "+randomMsg);
            return hashPass.equals(encodedPassword);
        } catch (Exception ex) {
            return false;
        }
        
    }
    
    
    /**
     * @param block
     * @Brief   To get the status of there staff check whether is chief or not
     * @throws SQLException 
     */
/*    public void setChiefInfo(String block) throws SQLException{
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM ChiefAndRelief "
                + "WHERE SI_id = 3 "//(SELECT SI_id FROM StaffInfo WHERE StaffID = ?) "
                + "AND Block = ? AND Status = 'CHIEF' ";
        PreparedStatement ps = conn.prepareStatement(sql);
//        ps.setString(1, this.id);
        ps.setString(1, block);

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
        
    } */

    /**
     * @Brief   To get the venue list from database
     * @return Arraylist of Venue object
     * @throws SQLException 
     */
    public ArrayList<Venue> getVenueList(String block) throws SQLException{
        ArrayList<Venue> venueList = new ArrayList<>();
        
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM Venue WHERE Block = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, block);

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
     * @Brief   To get the paper from database
     * @return Arraylist of Paper object
     * @throws SQLException 
     */
    public ArrayList<Paper> getPaperList(String block, Integer sessionId) throws SQLException{
        ArrayList<Paper> paperList = new ArrayList<>();
        
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM Paper"
                + " LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + " WHERE Block = ?"
                + " AND Session_id = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, block);
        ps.setInt(2, sessionId);

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
    
    /**
     * @Brief   To get the list of CandidateAttendance list from database
     * @return Arraylist of CandidateAttendance object
     * @throws SQLException 
     */
    public ArrayList<CandidateAttendance> getCddAttdList(String block, Integer sessionId) throws SQLException{
        ArrayList<CandidateAttendance> cddAttdList = new ArrayList<>();

        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * "
                + "FROM "+CandidateAttendance.TABLE+" "
                + "LEFT OUTER JOIN Paper ON Paper.Paper_id = CandidateAttendance.Paper_id "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + "WHERE Block = ? AND Session_id = ?";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, block);
        ps.setInt(2, sessionId);

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
        }
        
        result.close();
        ps.close();
        conn.close();
        
        return cddAttdList;
    }
    
    /**
     * @Brief   To get the list of CandidateInfo list from database
     * @return Arraylist of CandidateAttendance object
     * @throws SQLException 
     */
    public ArrayList<CandidateInfo> getCddInfoList(String block) throws SQLException{
        ArrayList<CandidateInfo> cddInfoList = new ArrayList<>();

        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * "
                + "FROM CandidateInfo "
                + "LEFT OUTER JOIN CandidateAttendance ON CandidateAttendance.CI_id = CandidateInfo.CI_id "
                + "LEFT OUTER JOIN Paper ON Paper.Paper_id = CandidateAttendance.Paper_id "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + "WHERE Block = ? AND Session_id = ?";
//                + "WHERE IC = "
//                + "(SELECT CandidateInfoIC FROM CandidateAttendance WHERE Paper_id = "
//                + "(SELECT Paper_id FROM Paper WHERE Venue_id = ? AND Session_id = ?) "
//                + ")";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, block);
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
    
    /**
     * @Brief   To get the list of Chief and Relief list from database
     * @return Arraylist of CandidateAttendance object
     * @throws SQLException 
     */
    public ArrayList<ChiefAndRelief> getChAndReList(String block) throws SQLException{
        ArrayList<ChiefAndRelief> chAndReList = new ArrayList<>();
        
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM ChiefAndRelief WHERE Session_id = ? AND Block = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, getSession_id());
        ps.setString(2, block);

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
    
    /**
     * @Brief   To get the list of Collector list from database
     * @return Arraylist of CandidateAttendance object
     * @throws SQLException 
     */
    public ArrayList<Collector> getCollectorList() throws SQLException{
        ArrayList<Collector> collectorList = new ArrayList<>();
        
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM Collector ";
        PreparedStatement ps = conn.prepareStatement(sql);

        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            collectorList.add(new Collector(    result.getInt("Collector_id"),
                                                result.getInt("Paper_id"),
                                                result.getString("StaffID")
                                                ));
        }
        
        result.close();
        ps.close();
        conn.close();
        
        return collectorList;
    }
    
    /**
     * @Brief   To get the list of Invigilator list from database
     * @return Arraylist of CandidateAttendance object
     * @throws SQLException 
     */
    public ArrayList<Invigilator> getInvigilatorList(String block) throws SQLException{
        ArrayList<Invigilator> invigilatorList = new ArrayList<>();

        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM InvigilatorAndAssistant "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = InvigilatorAndAssistant.Venue_id "
                + "WHERE Session_id = ? AND Block = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, getSession_id());
        ps.setString(2, block);

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
    
    /**
     * @Brief   To get the list of Paper Info list from database
     * @return Arraylist of CandidateAttendance object
     * @throws SQLException 
     */
    public ArrayList<PaperInfo> getPaperInfoList(String block) throws SQLException{
        ArrayList<PaperInfo> paperInfoList = new ArrayList<>();

        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM PaperInfo "
                + "LEFT OUTER JOIN Paper ON Paper.PI_id = PaperInfo.PI_id "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + "WHERE Block = ? AND Session_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, block);
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
    
    /**
     * @Brief   To get the list of Programme list from database
     * @return Arraylist of CandidateAttendance object
     * @throws SQLException 
     */
    public ArrayList<Programme> getProgrammeList() throws SQLException{
        ArrayList<Programme> programmeList = new ArrayList<>();

        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM Programme ";
//                + "LEFT OUTER JOIN CandidateInfo"
//                + "LEFT OUTER JOIN Paper ON Paper.Programme_id = Programme.Programme_id "
//                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
//                + "WHERE Block = ? AND Session_id = ?";
        
        PreparedStatement ps = conn.prepareStatement(sql);
//        ps.setString(1, this.block);
//        ps.setInt(2, getSession_id());

        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            programmeList.add(new Programme(    result.getInt("Programme_id"),
                                                result.getString("Name"),
                                                result.getString("Faculty"),
                                                result.getInt(Programme.GROUP)
                                                    ));
        }
        
        result.close();
        ps.close();
        conn.close();
        
        return programmeList;
    }
    
    /**
     * @Brief   To get the list of Staff Info list from database
     * @return Arraylist of CandidateAttendance object
     * @throws SQLException 
     */
    public ArrayList<StaffInfo> getStaffInfoList(String block, Integer sessionId) throws SQLException{
        ArrayList<StaffInfo> staffInfoList = new ArrayList<>();
        
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * FROM StaffInfo "
                + "LEFT OUTER JOIN InvigilatorAndAssistant ON InvigilatorAndAssistant.StaffID = StaffInfo.StaffID "
                + "LEFT OUTER JOIN ChiefAndRelief ON ChiefAndRelief.SI_id = StaffInfo.SI_id "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = InvigilatorAndAssistant.Venue_id "
                + "WHERE (Venue.Block = ? AND InvigilatorAndAssistant.Session_id = ?) "
                + "OR (ChiefAndRelief.Block = ? AND ChiefAndRelief.Session_id = ?) ";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, block);
        ps.setInt(2, sessionId);
        ps.setString(3, block);
        ps.setInt(4, sessionId);

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
    
    /**
     * @Brief   To get the list of Candidate Paper list from database
     * @return Arraylist of CandidateAttendance object
     * @throws SQLException 
     */
    public ArrayList<CddPaper> getCddPaperList(String regNum) throws SQLException{
        ArrayList<CddPaper> cddPaperList = new ArrayList<>();
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT Venue.Name AS VenueName "
                + ",* FROM CandidateInfo "
                + "LEFT OUTER JOIN CandidateAttendance ON CandidateAttendance.CI_id = CandidateInfo.CI_id "
                + "LEFT OUTER JOIN Paper ON Paper.Paper_id = CandidateAttendance.Paper_id "
                + "LEFT OUTER JOIN PaperInfo ON PaperInfo.PI_id = Paper.PI_id "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + "LEFT OUTER JOIN SessionAndDate ON SessionAndDate.Session_id = Paper.Session_id "
                + "WHERE RegNum = ? ";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1,regNum);
        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            cddPaperList.add(new CddPaper(    result.getString("PaperCode"),
                                              result.getString("PaperDescription"),
                                              result.getString("Date"),
                                              result.getString("Session"),
                                              result.getString("VenueName")
                                              ));
            System.out.println(result.getString("PaperCode"));
        }
        
        return cddPaperList;
    }
    
    public boolean invVerify(String id, String password){
        
        boolean match = false;
        try {
            
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
            
        } catch (SQLException ex) {
            Logger.getLogger(ChiefData.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return match;
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
        
    public Integer getSessionId(){
        return 1;
    }
    
    public String jooqtest(String block){
        Connection conn = new ConnectDB().connect();
        DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
        String result = create.select()
                .from("CandidateAttendance")
                .leftOuterJoin("Paper").on(field("Paper.Paper_id").equal(field("CandidateAttendance.Paper_id")))
                .leftOuterJoin("Venue").on(field("Venue.Venue_id").equal(field("Paper.Venue_id")))
                .where(field("Block").equal("M"))
                .and(field("Session_id").equal(getSession_id()))
                .fetch().formatJSON();
        System.out.println(block);
        return result;
    }
    
    public String jooqtest1(String block) throws Exception{

        Connection conn = new ConnectDB().connect();
        DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
//        String result = create.select()
//                .from("Paper")
//                .innerJoin("Venue").on(field("Venue.Venue_id").equal(field("Paper.Venue_id")))
//                .innerJoin("CandidateAttendance").on(field("CandidateAttendance.Paper_id").equal(field("Paper.Paper_id")))
//                .innerJoin("CandidateInfo").on(field("CandidateInfo.IC").equal(field("CandidateAttendance.CandidateInfoIC")))
//                .innerJoin("ChiefAndRelief").on(field("ChiefAndRelief.Block").equal(field("Venue.Block")))
//                .innerJoin("InvigilatorAndAssistant").on(field("InvigilatorAndAssistant.Venue_id").equal(field("Venue.Venue_id")))
//                .innerJoin("PaperInfo").on(field("PaperInfo.PI_id").equal(field("Paper.PI_id")))
//                .innerJoin("Programme").on(field("Programme.Programme_id").equal(field("Paper.Programme_id")))
//                .innerJoin("StaffInfo").on(field("StaffInfo.StaffID").equal(field("InvigilatorAndAssistant.StaffID")))
//                .where(field("Venue.Block").equal("M"))
//                .and(field("Session_id").equal(getSession_id()))
//                .fetch().formatJSON();
        
        String result = create.select()
                .from("Venue")
                .innerJoin("Paper").on(field("Paper.Venue_id").equal(field("Venue.Venue_id")))
                .innerJoin("CandidateAttendance").on(field("CandidateAttendance.Paper_id").equal(field("Paper.Paper_id")))
                .innerJoin("CandidateInfo").on(field("CandidateInfo.IC").equal(field("CandidateAttendance.CandidateInfoIC")))
                .innerJoin("ChiefAndRelief").on(field("ChiefAndRelief.Block").equal(field("Venue.Block")))
                .innerJoin("InvigilatorAndAssistant").on(field("InvigilatorAndAssistant.Venue_id").equal(field("Venue.Venue_id")))
                .innerJoin("PaperInfo").on(field("PaperInfo.PI_id").equal(field("Paper.PI_id")))
                .innerJoin("Programme").on(field("Programme.Programme_id").equal(field("Paper.Programme_id")))
                .innerJoin("StaffInfo").on(field("StaffInfo.StaffID").equal(field("InvigilatorAndAssistant.StaffID")))
                .where(field("Venue.Block").equal("M"))
//                .and(field("Session_id").equal(getSession_id()))
                .fetch().formatJSON();
        
        System.out.println(block);
        return result;
    }
    
    public void jooqtest2load(String data) throws IOException, SQLException{
//        System.out.println(data);
        Connection conn = new ConnectDB("testDatabase.db").connect();
        
        Statement statement = conn.createStatement();
//        statement.executeUpdate("DELETE FROM Venue");
//        statement.executeUpdate("DELETE FROM CandidateInfo");
//        statement.executeUpdate("DELETE FROM CandidateAttendance");
//        statement.executeUpdate("DELETE FROM ChiefAndRelief");
//        statement.executeUpdate("DELETE FROM Paper");
//        statement.executeUpdate("DELETE FROM InvigilatorAndAssistant");
//        statement.executeUpdate("DELETE FROM PaperInfo");
//        statement.executeUpdate("DELETE FROM Programme");
//        statement.executeUpdate("DELETE FROM StaffInfo");

        
        DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
        
        create.loadInto(table("Venue"))
            .loadJSON(data)
            .fields(field("Venue_id"), field("Block"), 
                    field("Name"), field("Size")
            )
            .execute();
        
        create.insertInto(table("Persons"), field("P_Id"),field("LastName")).values(2,"lol")
                .onDuplicateKeyUpdate().set(field("P_Id"),"lol").execute();
        
//        create.loadInto(table("Persons"))
//            .loadJSON(data)
//            .fields(null,null,null,null,
//                    field("LastName")
//            )
//            .execute();
        
        
        
        create.loadInto(table("CandidateAttendance"))
            .loadJSON(data)
            .fields(null,null,null,null,null,null,null,
                    null,null,null,null,field("CA_id"), 
                    field("CandidateInfoIC"), field("Paper_id"), field("Status"), 
                    field("Attendance"), field("TableNumber")
            )
            .execute();
        
        create.loadInto(table("CandidateInfo"))
            .loadJSON(data)
            .fields(null,null,null,null,null,null,null,
                    null,null,null,null,
                    null,null,null,null,null,null,
                    field("CI_id"), 
                    field("IC"), field("Name"), field("RegNum"), 
                    field("Programme_id"), field("ExamID")
            )
            .execute();
        
        create.loadInto(table("ChiefAndRelief"))
            .loadJSON(data)
            .fields(null,null,null,null,null,null,null,
                    null,null,null,null,
                    null,null,null,null,null,null,
                    null,null,null,null,null,null,
                    field("CR_id"), 
                    field("SI_id"), field("Block"), field("Session_id"), 
                    field("Status"), field("Attendance"), field("SignInTime")
            )
            .execute();
        
        create.loadInto(table("InvigilatorAndAssistant"))
            .loadJSON(data)
            .fields(null,null,null,null,null,null,null,
                    null,null,null,null,
                    null,null,null,null,null,null,
                    null,null,null,null,null,null,
                    null,null,null,null,null,null,null,
                    field("IA_id"), field("StaffID"), field("Status"), 
                    field("Attendance"), field("SignInTime"), field("Venue_id"), 
                    field("Session_id")
            )
            .execute();
        
        create.loadInto(table("PaperInfo"))
            .loadJSON(data)
            .fields(null,null,null,null,null,null,null,
                    null,null,null,null,
                    null,null,null,null,null,null,
                    null,null,null,null,null,null,
                    null,null,null,null,null,null,null,
                    null,null,null,null,null,null,null,
                    field("PI_id"), field("PaperCode"), 
                    field("PaperDescription")
            )
            .execute();
        
        create.loadInto(table("Programme"))
            .loadJSON(data)
            .fields(null,null,null,null,null,null,null,
                    null,null,null,null,
                    null,null,null,null,null,null,
                    null,null,null,null,null,null,
                    null,null,null,null,null,null,null,
                    null,null,null,null,null,null,null,
                    null,null,null,
                    field("Programme_id"), field("Name"), 
                    field("Faculty"), field("ProgrammeGroup")
            )
            .execute();
        
        create.loadInto(table("StaffInfo"))
            .loadJSON(data)
            .fields(null,null,null,null,null,null,null,
                    null,null,null,null,
                    null,null,null,null,null,null,
                    null,null,null,null,null,null,
                    null,null,null,null,null,null,null,
                    null,null,null,null,null,null,null,
                    null,null,null,
                    null,null,null,null,
                    field("SI_id"), 
                    field("StaffID"), field("Name")
            )
            .execute();
        
        
    }
}
