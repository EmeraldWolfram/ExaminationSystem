/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainserver;

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
        String sql = "SELECT * FROM Paper"
                + " LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + " WHERE Block = ?"
                + " AND Session_id = ? ";
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
    
    
    public ArrayList<CandidateAttendance> getCddAttdList() throws SQLException{
        ArrayList<CandidateAttendance> cddAttdList = new ArrayList<>();

        Connection conn = new ConnectDB().connect();
        String sql = "SELECT * "
                + "FROM CandidateAttendance "
                + "LEFT OUTER JOIN Paper ON Paper.Paper_id = CandidateAttendance.Paper_id "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + "WHERE Block = ? AND Session_id = ?";
        
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
                + "LEFT OUTER JOIN CandidateAttendance ON CandidateAttendance.CandidateInfoIC = CandidateInfo.IC "
                + "LEFT OUTER JOIN Paper ON Paper.Paper_id = CandidateAttendance.Paper_id "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + "WHERE Block = ? AND Session_id = ?";
//                + "WHERE IC = "
//                + "(SELECT CandidateInfoIC FROM CandidateAttendance WHERE Paper_id = "
//                + "(SELECT Paper_id FROM Paper WHERE Venue_id = ? AND Session_id = ?) "
//                + ")";
        
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
        String sql = "SELECT * FROM ChiefAndRelief WHERE Session_id = ? AND Block = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, getSession_id());
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
    
    public ArrayList<Collector> getCollectorList() throws SQLException{
        ArrayList<Collector> collectorList = new ArrayList<>();
        
        
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
            
            getChiefInfo();
            
        } catch (SQLException ex) {
            Logger.getLogger(ChiefData.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return match;
    }
    
    public String jooqtest(){
        Field lol;
//        lol.
        Connection conn = new ConnectDB().connect();
        DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
        String result = create.select()
                .from("CandidateAttendance")
                .leftOuterJoin("Paper").on(field("Paper.Paper_id").equal(field("CandidateAttendance.Paper_id")))
                .leftOuterJoin("Venue").on(field("Venue.Venue_id").equal(field("Paper.Venue_id")))
                .where(field("Block").equal("M"))
                .and(field("Session_id").equal(getSession_id()))
                .fetch().formatJSON();
        System.out.println(this.block);
        return result;
    }
    
    public String jooqtest1() throws Exception{

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
        
        System.out.println(this.block);
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
