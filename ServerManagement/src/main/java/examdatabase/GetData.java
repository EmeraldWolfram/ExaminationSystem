/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examdatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import querylist.CandidateInfo;
import querylist.PaperInfo;
import querylist.Programme;
import querylist.SessionAndDate;
import querylist.StaffInfo;
import querylist.Invigilator;
import querylist.Paper;
import querylist.Venue;


/**
 *
 * @author Krissy
 */
public class GetData {
    public final static String ERR_EMPTY_CONDITION = "Error: Invalid input search";
    
    public String type = "";
    public String data = "";
    
    //CandidateInfo
    private Integer candidate_id = 0; 
    private String ic = ""; 
    private String name = ""; 
    private String regNum = "";
    private String examId = "";
    
    //CandidateAttendance
    private String status = ""; 
    private String attendance = ""; 
    private String tableNum = "";
    
    //Programme
    private String progName = "";
    private String progGroup = "";
    private String faculty = "";
    
    //Paper
    private Integer paper_id = 0;
    private String date = "";
    private String session = "";
    private Integer numOfCand = 0;
    private Integer startingNum = 0;
    private String Collector = "";
    
    //PaperInfo
    private String paperCode = "";
    private String paperDesc = "";
    
    //Venue
    private Integer venue_id = 0;
    private String venueName = "";
    private String venueSize = "";
    private String block = "";
   
    //Invigilator And Assistant
    private String invStatus = "";
    private String invAttendnce = "";
    
    //StaffInfo
    private String staffID = "";
    private String staffName = "";
    private String staffFaculty = "";
    
    //StudentMark
    private Integer coursework;
    private Integer practical;
    
    private Integer examWeight = 0;
    private Integer courseworkWeight = 0;
    
    private String lecturer = "";
    private String tutor = "";
    
    private int day; private int month; private int year;
    
    private Integer session_id = 0;
    
    public GetData(){
        
    }
    
    public GetData(String data) {
        this.data = data;
    }
    
    public GetData( String ic, String name, String regNum,
                    String progName, String faculty,
                    String date,
                    String paperCode, String paperDesc,
                    Integer coursework, Integer practical
                    ){

        this.ic = ic;
        this.name = name;
        this.regNum = regNum;
        this.progName = progName;
        this.faculty = faculty;
        this.date = date;
        this.paperCode = paperCode;
        this.paperDesc = paperDesc;
        this.coursework = coursework;
        this. practical = practical;

    }
    
    public GetData( String ic, String name, String regNum,
                    String status, String attendance, String tableNum,
                    String progName, String faculty,
                    String session, String date,
                    String paperCode, String paperDesc,
                    String venueName, String venueSize
                    ){

        this.ic = ic;
        this.name = name;
        this.regNum = regNum;
        this.status = status;
        this.attendance = attendance;
        this.tableNum = tableNum;
        this.progName = progName;
        this.faculty = faculty;
        this.data = date;
        this.session = session;
        this.paperCode = paperCode;
        this.paperDesc = paperDesc;
        this.venueName = venueName;
        this.venueSize = venueSize;
    }
    
    public GetData( String ic, String name, String regNum,
                    String status, String attendance, String tableNum,
                    String progName, String faculty,
                    String session,
                    String paperCode, String paperDesc
                    ){
        this.ic = ic;
        this.name = name;
        this.regNum = regNum;
        this.status = status;
        this.attendance = attendance;
        this.tableNum = tableNum;
        this.progName = progName;
        this.faculty = faculty;
        this.session = session;
        this.paperCode = paperCode;
        this.paperDesc = paperDesc;
        this.venueName = venueName;
        this.venueSize = venueSize;
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
    public ArrayList<GetData> getDataFromTable() throws Exception{
        
        String sql =    "SELECT CandidateInfo.IC, CandidateInfo.Name, CandidateInfo.RegNum "
                + ", CandidateAttendance.Status, CandidateAttendance.Attendance, CandidateAttendance.TableNumber "
                + ", Programme.Name AS ProgName, Programme.Faculty "
                + ", Paper.Session_id"
                + ", PaperInfo.PaperCode, PaperInfo.PaperDescription "
                + ", Venue.Name AS VenueName, Venue.Size "
                + ", SessionAndDate.Session AS SessionType "
                + ", SessionAndDate.Date AS SessionDate "
                + " FROM CandidateAttendance "
                + " LEFT OUTER JOIN CandidateInfo ON CandidateAttendance.CI_id = CandidateInfo.CI_id "
                + " LEFT OUTER JOIN Programme ON CandidateInfo.Programme_id = Programme.Programme_id "
                + " LEFT OUTER JOIN Paper ON CandidateAttendance.Paper_id = Paper.Paper_id "
                + " LEFT OUTER JOIN PaperInfo ON Paper.PI_id = PaperInfo.PI_id "
                + " LEFT OUTER JOIN Venue ON Paper.Venue_id = Venue.Venue_id "
                + " LEFT OUTER JOIN SessionAndDate ON SessionAndDate.Session_id = Paper.Session_id "
                + " WHERE CandidateInfo.IC " + checkInput(this.ic)
                + " AND CandidateInfo.Name " + checkInput(this.name)
                + " AND CandidateInfo.RegNum "+ checkInput(this.regNum)
                + " AND CandidateAttendance.Status "+ checkInput(this.status)
                + " AND CandidateAttendance.Attendance "+ checkInput(this.attendance)
                + " AND CandidateAttendance.TableNumber "+ checkInput(this.tableNum)
                + " AND ProgName "+ checkInput(this.progName)
                + " AND Programme.Faculty "+ checkInput(this.faculty)
                + " AND SessionAndDate.Session "+ checkInput(this.session)
                + " AND SessionAndDate.Date "+ checkInput(this.date)
//                + " AND PaperInfo.PaperCode = * "//+ checkInput(this.paperCode)
//                + " AND VenueName = * "//+ checkInput(this.venueName)     
                ;

        GetData info;
        ArrayList<GetData> list = new ArrayList<>();
        
        try (Connection conn = new ConnectDB().connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                
                info = new GetData();
                info.setIc(rs.getString("IC"));
                info.setName(rs.getString("Name"));
                info.setRegNum(rs.getString("RegNum")); 
                info.setStatus(rs.getString("Status"));
                info.setAttendance(rs.getString("Attendance"));
                info.setTableNum(rs.getString("TableNumber"));
                info.setProgName(rs.getString("ProgName"));
                info.setFaculty(rs.getString("Faculty"));
                info.setSession(rs.getString("SessionType"));
                info.setPaperCode(rs.getString("PaperCode"));
                info.setPaperDesc(rs.getString("PaperDescription"));
                info.setVenueName(rs.getString("VenueName"));
                info.setDate(rs.getString("SessionDate"));
                
//                System.out.println(info.getName());
                
                list.add(info);
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

            return list;
    }
    
    /**
     * Get the info of a candidate from the connected database
     * @return list     The arraylist contain the info of the candidate
     */
    public ArrayList<GetData> getDataCheckMark() throws Exception{
        String sql =    "SELECT CandidateInfo.IC, CandidateInfo.Name, CandidateInfo.RegNum "
                + ", Programme.Name AS ProgName, Programme.Faculty "
                + ", StudentMark.Coursework, StudentMark.Practical"
                + ", PaperInfo.PaperCode, PaperInfo.PaperDescription "
                + " FROM StudentMark "
                + " LEFT OUTER JOIN CandidateInfo ON StudentMark.RegNum = CandidateInfo.RegNum "
                + " LEFT OUTER JOIN CandidateAttendance ON CandidateInfo.CI_id = CandidateAttendance.CI_id "
                + " LEFT OUTER JOIN Programme ON CandidateInfo.Programme_id = Programme.Programme_id "
                + " LEFT OUTER JOIN Paper ON CandidateAttendance.Paper_id = Paper.Paper_id "
                + " LEFT OUTER JOIN PaperInfo ON PaperInfo.PI_id = StudentMark.PI_id "
                + " LEFT OUTER JOIN Venue ON Paper.Venue_id = Venue.Venue_id "
                + " WHERE CandidateInfo.IC " + checkInput(this.getIc())
                + " AND CandidateInfo.Name " + checkInput(this.getName())
                + " AND CandidateInfo.RegNum "+ checkInput(this.getRegNum())
                + " AND ProgName "+ checkInput(this.getProgName())
                + " AND Programme.Faculty "+ checkInput(this.getFaculty())
                + " AND PaperInfo.PaperCode "+ checkInput(this.getPaperCode())  
                ;

        
        ArrayList<GetData> list = new ArrayList<>();
        
        try {
            Connection conn = new ConnectDB().connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {
                GetData info = new GetData();
                info.setIc(rs.getString("IC"));
                info.setName(rs.getString("Name"));
                info.setRegNum(rs.getString("RegNum"));
                info.setProgName(rs.getString("ProgName"));
                info.setFaculty(rs.getString("Faculty"));
                info.setPaperCode(rs.getString("PaperCode"));
                info.setPaperDesc(rs.getString("PaperDescription"));
                info.setCoursework(rs.getInt("Coursework"));
                info.setPractical(rs.getInt("Practical"));                        
                
                list.add(info);
            }
            
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if(list.isEmpty())
            throw new Exception("No data found.");
        else
            return list;
    }
    
    /**
     * Get the info of a course structure from the connected database
     * @return list     The arraylist contain the info of the candidate
     */
    public ArrayList<GetData> getCourseStructure() throws Exception{
        String sql =    "SELECT *, Programme.Name AS ProgrammeName, Programme.Programme_Group AS ProgrammeGroup "
                + " FROM CourseStructure "
                + " LEFT OUTER JOIN PaperInfo ON PaperInfo.PI_id = CourseStructure.Course "
                + " LEFT OUTER JOIN Programme ON Programme.Programme_id = CourseStructure.Programme_id "
                + " WHERE Lecturer " + checkInput(this.getLecturer())
                + " AND Tutor "+ checkInput(this.getTutor())
                + " AND PaperInfo.PaperCode "+ checkInput(this.getPaperCode())
                + " AND PaperInfo.PaperDescription "+ checkInput(this.getPaperDesc())  
                + " AND Programme.Name "+ checkInput(this.getProgName())  
//                + " AND Programme.Group "+ checkInput(this.getProgGroup()) 
                ;

        
        ArrayList<GetData> list = new ArrayList<>();
        
        try {
            Connection conn = new ConnectDB().connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {
                GetData info = new GetData();
                info.setLecturer(rs.getString("Lecturer"));
                info.setTutor(rs.getString("Tutor"));
                info.setPaperCode(rs.getString("PaperCode"));
                info.setPaperDesc(rs.getString("PaperDescription"));                
                info.setExamWeight(rs.getInt("ExamWeight"));                
                info.setCourseworkWeight(rs.getInt("CourseworkWeight"));                
                info.setProgName(rs.getString("ProgrammeName"));                
                info.setProgGroup(rs.getString("ProgrammeGroup"));                
                
                list.add(info);
            }
            
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if(list.isEmpty())
            throw new Exception("No data found.");
        else
            return list;
    }
    
    /**
     * Get the info of a course structure from the connected database
     * @return list     The arraylist contain the info of the candidate
     */
    public ArrayList<GetData> getPaperList() throws Exception{
        String sql =    "SELECT *, Programme.Name AS ProgrammeName, Programme.Programme_Group AS ProgrammeGroup,"
                + "Venue.Name AS VenueName "
                + " FROM Paper "
                + " LEFT OUTER JOIN PaperInfo ON PaperInfo.PI_id = Paper.PI_id "
                + " LEFT OUTER JOIN Programme ON Programme.Programme_id = Paper.Programme_id "
                + " LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + " LEFT OUTER JOIN SessionAndDate ON SessionAndDate.Session_id = Paper.Session_id "
                + " WHERE VenueName " + checkInput(this.getVenueName())
                + " AND Date "+ checkInput(this.getDate())
                + " AND PaperInfo.PaperCode "+ checkInput(this.getPaperCode())
                + " AND PaperInfo.PaperDescription "+ checkInput(this.getPaperDesc())  
                + " AND Programme.Name "+ checkInput(this.getProgName())  
//                + " AND Programme.Group "+ checkInput(this.getProgGroup()) 
                ;

        
        ArrayList<GetData> list = new ArrayList<>();
        
        try {
            Connection conn = new ConnectDB().connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {
                GetData info = new GetData();
                info.setPaperCode(rs.getString("PaperCode"));
                info.setPaperDesc(rs.getString("PaperDescription"));                      
                info.setProgName(rs.getString("ProgrammeName"));                
                info.setProgGroup(rs.getString("ProgrammeGroup"));                
                info.setVenueName(rs.getString("VenueName"));                
                info.setDate(rs.getString("Date"));                
                info.setSession(rs.getString("Session"));                
                info.setCollector(rs.getString("Collector"));         
                
                list.add(info);
            }
            
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if(list.isEmpty())
            throw new Exception("No data found.");
        else
            return list;
    }
    
     /**
     * Get the info of a Invigilators and Assistants info from the connected database
     * @return list     The arraylist contain the info of the candidate
     */
    public ArrayList<GetData> getInvigilatorInfo(String date, String session, String block,
                                                String venue, String staffID, String invStatus) throws Exception{
        
        String sql =  "SELECT * "
                + " FROM " + Invigilator.TABLE 
                + " LEFT OUTER JOIN " + Venue.TABLE + " ON " + Venue.TableCol.ID + " = " + Invigilator.TableCol.VENUE_ID 
                + " LEFT OUTER JOIN " + SessionAndDate.TABLE + " ON " + SessionAndDate.TableCol.ID + " = " + Invigilator.TableCol.SESSION_ID
                + " WHERE " + Invigilator.STAFFID + " " + checkInput(staffID)
                + " AND " + SessionAndDate.DATE + " " + checkInput(date)
                + " AND " + SessionAndDate.SESSION + " " +  checkInput(session)
                + " AND " + Venue.BLOCK + " " +  checkInput(block)  
                + " AND " + Venue.NAME + " " +  checkInput(venue)  
                + " AND " + Invigilator.STATUS + " " +  checkInput(invStatus)
                ;

        ArrayList<GetData> list = new ArrayList<>();
        
        try {
            Connection conn = new ConnectDB().connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {
                GetData info = new GetData();
                info.setStaffID(rs.getString(Invigilator.STAFFID));
                info.setDate(rs.getString(SessionAndDate.DATE));
                info.setSession(rs.getString(SessionAndDate.SESSION));
                info.setBlock(rs.getString(Venue.BLOCK));
                info.setVenueName(rs.getString(Venue.NAME));
                info.setInvStatus(rs.getString(Invigilator.STATUS));
                
                
                list.add(info);
            }
            
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if(list.isEmpty())
            throw new Exception("No data found.");
        else
            return list;
    }
    
    /**
     * 
     * @param table
     * @param data
     * @return 
     */
    public ArrayList<String> getList(String table, String data){
        ArrayList<String> list = new ArrayList<>();
        
        String sql = "SELECT DISTINCT " + data + " From " + table;
        
        try (Connection conn = new ConnectDB().connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                   list.add(rs.getString(data));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return list; 
    }
    
    /**
     * 
     * @param table
     * @param condColumn
     * @param cond
     * @param column
     * @param data
     * @return 
     */
    public ArrayList<String> getListWithOneCond(String table, String condColumn, String cond, String data) throws SQLException, Exception{
        ArrayList<String> list = new ArrayList<>();
        
        String sql = "SELECT DISTINCT " + data + 
                        " From " + table +
                        " WHERE " + condColumn + " = ?";
        
            Connection conn = new ConnectDB().connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            if(condColumn == Programme.ID){
                if(cond == null || cond.isEmpty())
                    throw new Exception(ERR_EMPTY_CONDITION);
                 else
                    pstmt.setInt(1,Integer.parseInt(cond));
            }
            if(cond == null || cond.isEmpty())
               throw new Exception(ERR_EMPTY_CONDITION);
            else
               pstmt.setString(1,cond);
            
            ResultSet rs    = pstmt.executeQuery();
            // loop through the result set
            while (rs.next()) {
                   list.add(rs.getString(data));
            }
            
            rs.close();
            pstmt.close();

        
        return list; 
    }
    
    
    
    public Integer getVenueIdFromDB(String venueName) throws SQLException{
        Integer id = 0;
        String sql = "Select " + Venue.ID +
                    " FROM " + Venue.TABLE +
                    " WHERE " + Venue.NAME + " = ? ";
        
        Connection conn = new ConnectDB().connect();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        pstmt.setString(1, venueName);
        
        ResultSet rs    = pstmt.executeQuery();
        
        if(rs.next())
            id = rs.getInt(Venue.ID);

        rs.close();
        pstmt.close();
        conn.close();
        
        return id;
    }
    
    public Integer getSessionIdFromDB(String session, String date) throws SQLException{
        Integer id = 0;
        String sql = "Select " + SessionAndDate.ID +
                    " FROM " + SessionAndDate.TABLE +
                    " WHERE " + SessionAndDate.SESSION + " = ? " +
                    " AND " + SessionAndDate.DATE + " = ? ";
        
        Connection conn = new ConnectDB().connect();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        pstmt.setString(1, session);
        pstmt.setString(2, date);
        
        ResultSet rs    = pstmt.executeQuery();
        
        if(rs.next())
            id = rs.getInt(SessionAndDate.ID);

        rs.close();
        pstmt.close();
        conn.close();
            
        return id;
    }
    
    
    /**
     *@brief    To obtain the size of specific venue
     */
    public String getVenueSize(String venue){
        String size = "";
        String sql = "SELECT " + Venue.SIZE + 
                    " FROM " + Venue.TABLE +
                    " WHERE " + Venue.NAME + " = ? ";
        
        try (Connection conn = new ConnectDB().connect();){
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, venue);
            
            ResultSet rs = pstmt.executeQuery();
            // loop through the result set
            while (rs.next()) {
                   size = rs.getString(Venue.SIZE);
            }
            
            rs.close();
                pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return size;
        
    }
    
    public ArrayList<GetData> getUnassignedVenuePaper(){
        ArrayList<GetData> list = new ArrayList<>();
        String sql = "SELECT * , " + Programme.TableCol.NAME + " AS ProgName "
                + " FROM " + Paper.TABLE 
                + " LEFT OUTER JOIN " + Venue.TABLE + " ON " + Venue.TableCol.ID + " = " + Paper.TableCol.VENUE_ID 
                + " LEFT OUTER JOIN " + Programme.TABLE + " ON " + Programme.TableCol.ID + " = " + Paper.TableCol.PROGRAMME_ID 
                + " LEFT OUTER JOIN " + PaperInfo.TABLE + " ON " + PaperInfo.TableCol.ID + " = " + Paper.TableCol.PI_ID 
                    ;
        
       try (Connection conn = new ConnectDB().connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                Paper paper = new Paper();
                paper.setPaperStartNo(rs.getInt(Paper.PAPER_START_NO));
                paper.setSession_id(rs.getInt(Paper.SESSION_ID));
                paper.setVenue_id(rs.getInt(Paper.VENUE_ID));

                
                
                if(paper.getVenue_id() != 0 && paper.getVenue_id() != null &&
                    paper.getPaperStartNo() != 0 && paper.getPaperStartNo() != null &&
                    paper.getSession_id() != 0 && paper.getSession_id() != null ){
                    
                }
                else{
                    GetData data = new GetData();
                    data.paper_id = rs.getInt(Paper.ID);
                    data.paperCode = rs.getString(PaperInfo.PAPER_CODE);
                    data.progName = rs.getString("ProgName");
                    data.progGroup = rs.getString(Programme.GROUP);
                    data.numOfCand = rs.getInt(Paper.TOTAL_CANDIDATE);
                    
                    list.add(data);
                }
                
            }
        } catch (SQLException ex) { 
            Logger.getLogger(GetData.class.getName()).log(Level.SEVERE, null, ex);
        }
       
       return list;
    }
    
    public ArrayList<GetData> getAssignedVenuePaper(String venue, String date, String session){
        ArrayList<GetData> list = new ArrayList<>();
        String sql = "SELECT * , " + Programme.TableCol.NAME + " AS ProgName, " + Venue.TableCol.NAME + " AS VenueName "
                + " FROM " + Paper.TABLE 
                + " LEFT OUTER JOIN " + Venue.TABLE + " ON " + Venue.TableCol.ID + " = " + Paper.TableCol.VENUE_ID 
                + " LEFT OUTER JOIN " + Programme.TABLE + " ON " + Programme.TableCol.ID + " = " + Paper.TableCol.PROGRAMME_ID 
                + " LEFT OUTER JOIN " + PaperInfo.TABLE + " ON " + PaperInfo.TableCol.ID + " = " + Paper.TableCol.PI_ID 
                + " LEFT OUTER JOIN " + SessionAndDate.TABLE + " ON " + SessionAndDate.TableCol.ID + " = " + Paper.TableCol.SESSION_ID 
                + " WHERE VenueName = ? AND " + SessionAndDate.DATE + " = ? AND " + SessionAndDate.SESSION + " = ? "  
                ;
        
       try {
           Connection conn = new ConnectDB().connect();
           PreparedStatement pstmt  = conn.prepareStatement(sql);
           
           pstmt.setString(1, venue);
           pstmt.setString(2, date);
           pstmt.setString(3, session);
           
           ResultSet rs    = pstmt.executeQuery();
                    
            // loop through the result set
            while (rs.next()) {
                Paper paper = new Paper();
                paper.setPaperStartNo(rs.getInt(Paper.PAPER_START_NO));
                paper.setSession_id(rs.getInt(Paper.SESSION_ID));
                paper.setVenue_id(rs.getInt(Paper.VENUE_ID));

                
                
                if(paper.getVenue_id() != 0 && paper.getVenue_id() != null &&
                    paper.getPaperStartNo() != 0 && paper.getPaperStartNo() != null &&
                    paper.getSession_id() != 0 && paper.getSession_id() != null ){
                    GetData data = new GetData();
                    data.paper_id = rs.getInt(Paper.ID);
                    data.paperCode = rs.getString(PaperInfo.PAPER_CODE);
                    data.progName = rs.getString("ProgName");
                    data.progGroup = rs.getString(Programme.GROUP);
                    data.numOfCand = rs.getInt(Paper.TOTAL_CANDIDATE);
                    data.startingNum = rs.getInt(Paper.PAPER_START_NO);
                    
                    list.add(data);
                }
            }
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch (SQLException ex) { 
            Logger.getLogger(GetData.class.getName()).log(Level.SEVERE, null, ex);
        }
       
       return list;
    }
    
    
    public ArrayList<GetData> getCandidateList(String candName, String ic, String regNum, String faculty, String programme, String programmeGroup) throws Exception{
        String sql =  "SELECT * , " + Programme.TableCol.NAME + " AS ProgName, " + CandidateInfo.TableCol.NAME + " AS CandName " 
                + " FROM " + CandidateInfo.TABLE 
                + " LEFT OUTER JOIN " + Programme.TABLE + " ON " + Programme.TableCol.ID + " = " + CandidateInfo.TableCol.PROGRAMME_ID
                + " WHERE " + "CandName" + " " + checkInput(candName)
                + " AND " + CandidateInfo.CANDIDATE_INFO_IC + " " + checkInput(ic)
                + " AND " + CandidateInfo.REGISTER_NUMBER + " " +  checkInput(regNum)
                + " AND " + "ProgName" + " " +  checkInput(programme)  
                + " AND " + Programme.GROUP + " " +  checkInput(programmeGroup)  
                + " AND " + Programme.FACULTY + " " +  checkInput(faculty)
                ;
        
        ArrayList<GetData> list = new ArrayList<>();
        
        try {
            Connection conn = new ConnectDB().connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {
                GetData info = new GetData();
                info.setCandidate_id(rs.getInt(CandidateInfo.ID));
                info.setName(rs.getString("CandName"));
                info.setIc(rs.getString(CandidateInfo.CANDIDATE_INFO_IC));
                info.setRegNum(rs.getString(CandidateInfo.REGISTER_NUMBER));
                info.setExamId(rs.getString(CandidateInfo.EXAM_ID));
                info.setFaculty(rs.getString(Programme.FACULTY));
                info.setProgName(rs.getString("ProgName"));
                info.setProgGroup(rs.getString(Programme.GROUP));
                
                list.add(info);
            }
            
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if(list.isEmpty())
            throw new Exception("No data found.");
        else
            return list;
    }
    
    public Boolean checkDataIsAvailable(String table, String condCol, String condition) throws SQLException{
        String sql = "SELECT *  " 
                + " FROM " + table 
                + " WHERE " + condCol + " = ? "  
                ;
        Boolean result = false;
        Connection conn = new ConnectDB().connect();
        PreparedStatement pstmt  = conn.prepareStatement(sql);
           
        pstmt.setString(1, condition);
           
        ResultSet rs    = pstmt.executeQuery();
        result = rs.next();
        
        rs.close();
        pstmt.close();
        conn.close();
        
        return result;
    }
    
    public int getPaperIdBaseProgrammeFromDB(String venue, String programmeGroup) throws SQLException{
        int paperId = 0;
        String sql = "SELECT *  " 
                + " FROM " + Programme.TABLE 
                + " WHERE " + Programme.NAME + " = ? "  
                + " AND " + Programme.GROUP + " = ? "  
                ;
        Connection conn = new ConnectDB().connect();
        PreparedStatement pstmt  = conn.prepareStatement(sql);
           
        pstmt.setString(1, venue);
        pstmt.setString(2, programmeGroup);
           
        ResultSet rs    = pstmt.executeQuery();
        
        if(rs.next())
            paperId = rs.getInt(Programme.ID);
        
        rs.close();
        pstmt.close();
        conn.close();
            
        return paperId;
    }

    /**
     * @return the ic
     */
    public String getIc() {
        return ic;
    }

    /**
     * @param ic the ic to set
     */
    public void setIc(String ic) {
        this.ic = ic;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the regNum
     */
    public String getRegNum() {
        return regNum;
    }

    /**
     * @param regNum the regNum to set
     */
    public void setRegNum(String regNum) {
        this.regNum = regNum;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the attendance
     */
    public String getAttendance() {
        return attendance;
    }

    /**
     * @param attendance the attendance to set
     */
    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    /**
     * @return the tableNum
     */
    public String getTableNum() {
        return tableNum;
    }

    /**
     * @param tableNum the tableNum to set
     */
    public void setTableNum(String tableNum) {
        this.tableNum = tableNum;
    }

    /**
     * @return the progName
     */
    public String getProgName() {
        return progName;
    }

    /**
     * @param progName the progName to set
     */
    public void setProgName(String progName) {
        this.progName = progName;
    }

    /**
     * @return the faculty
     */
    public String getFaculty() {
        return faculty;
    }

    /**
     * @param faculty the faculty to set
     */
    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the session
     */
    public String getSession() {
        return session;
    }

    /**
     * @param session the session to set
     */
    public void setSession(String session) {
        this.session = session;
    }

    /**
     * @return the paperCode
     */
    public String getPaperCode() {
        return paperCode;
    }

    /**
     * @param paperCode the paperCode to set
     */
    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode;
    }

    /**
     * @return the paperDesc
     */
    public String getPaperDesc() {
        return paperDesc;
    }

    /**
     * @param paperDesc the paperDesc to set
     */
    public void setPaperDesc(String paperDesc) {
        this.paperDesc = paperDesc;
    }

    /**
     * @return the venueName
     */
    public String getVenueName() {
        return venueName;
    }

    /**
     * @param venueName the venueName to set
     */
    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    /**
     * @return the venueSize
     */
    public String getVenueSize() {
        return venueSize;
    }

    /**
     * @param venueSize the venueSize to set
     */
    public void setVenueSize(String venueSize) {
        this.venueSize = venueSize;
    }

    /**
     * @return the invStatus
     */
    public String getInvStatus() {
        return invStatus;
    }

    /**
     * @param invStatus the invStatus to set
     */
    public void setInvStatus(String invStatus) {
        this.invStatus = invStatus;
    }

    /**
     * @return the invAttendnce
     */
    public String getInvAttendnce() {
        return invAttendnce;
    }

    /**
     * @param invAttendnce the invAttendnce to set
     */
    public void setInvAttendnce(String invAttendnce) {
        this.invAttendnce = invAttendnce;
    }

    /**
     * @return the staffID
     */
    public String getStaffID() {
        return staffID;
    }

    /**
     * @param staffID the staffID to set
     */
    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    /**
     * @return the staffName
     */
    public String getStaffName() {
        return staffName;
    }

    /**
     * @param staffName the staffName to set
     */
    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    /**
     * @return the staffFaculty
     */
    public String getStaffFaculty() {
        return staffFaculty;
    }

    /**
     * @param staffFaculty the staffFaculty to set
     */
    public void setStaffFaculty(String staffFaculty) {
        this.staffFaculty = staffFaculty;
    }

    /**
     * @return the coursework
     */
    public Integer getCoursework() {
        return coursework;
    }

    /**
     * @param coursework the coursework to set
     */
    public void setCoursework(Integer coursework) {
        this.coursework = coursework;
    }

    /**
     * @return the practical
     */
    public Integer getPractical() {
        return practical;
    }

    /**
     * @param practical the practical to set
     */
    public void setPractical(Integer practical) {
        this.practical = practical;
    }

    /**
     * @return the day
     */
    public int getDay() {
        return day;
    }

    /**
     * @param day the day to set
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * @return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * @param month the month to set
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * @param lecturer the lecturer to set
     */
    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    /**
     * @param tutor the tutor to set
     */
    public void setTutor(String tutor) {
        this.tutor = tutor;
    }

    /**
     * @param examWeight the examWeight to set
     */
    public void setExamWeight(Integer examWeight) {
        this.examWeight = examWeight;
    }

    /**
     * @param courseworkWeight the courseworkWeight to set
     */
    public void setCourseworkWeight(Integer courseworkWeight) {
        this.courseworkWeight = courseworkWeight;
    }

    /**
     * @return the examWeight
     */
    public Integer getExamWeight() {
        return examWeight;
    }

    /**
     * @return the courseworkWeight
     */
    public Integer getCourseworkWeight() {
        return courseworkWeight;
    }

    /**
     * @return the lecturer
     */
    public String getLecturer() {
        return lecturer;
    }

    /**
     * @return the tutor
     */
    public String getTutor() {
        return tutor;
    }

    /**
     * @return the progGroup
     */
    public String getProgGroup() {
        return progGroup;
    }

    /**
     * @param progGroup the progGroup to set
     */
    public void setProgGroup(String progGroup) {
        this.progGroup = progGroup;
    }

    /**
     * @return the block
     */
    public String getBlock() {
        return block;
    }

    /**
     * @param block the block to set
     */
    public void setBlock(String block) {
        this.block = block;
    }

    /**
     * @return the numOfCand
     */
    public Integer getNumOfCand() {
        return numOfCand;
    }

    /**
     * @return the startingNum
     */
    public Integer getStartingNum() {
        return startingNum;
    }

    /**
     * @return the paper_id
     */
    public Integer getPaper_id() {
        return paper_id;
    }

    /**
     * @return the venue_id
     */
    public Integer getVenue_id() {
        return venue_id;
    }

    /**
     * @return the session_id
     */
    public Integer getSession_id() {
        return session_id;
    }

    /**
     * @param venue_id the venue_id to set
     */
    public void setVenue_id(Integer venue_id) {
        this.venue_id = venue_id;
    }

    /**
     * @param session_id the session_id to set
     */
    public void setSession_id(Integer session_id) {
        this.session_id = session_id;
    }

    /**
     * @return the candidate_id
     */
    public Integer getCandidate_id() {
        return candidate_id;
    }

    /**
     * @param candidate_id the candidate_id to set
     */
    public void setCandidate_id(Integer candidate_id) {
        this.candidate_id = candidate_id;
    }

    /**
     * @return the examId
     */
    public String getExamId() {
        return examId;
    }

    /**
     * @param examId the examId to set
     */
    public void setExamId(String examId) {
        this.examId = examId;
    }

    /**
     * @return the Collector
     */
    public String getCollector() {
        return Collector;
    }

    /**
     * @param Collector the Collector to set
     */
    public void setCollector(String Collector) {
        this.Collector = Collector;
    }
 
}

