/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;



/**
 *
 * @author Krissy
 */
public class InfoData {
    
    private String type = "";
    private String data = "";
    
    //CandidateInfo
    private String ic = ""; 
    private String name = ""; 
    private String regNum = "";
    
    //CandidateAttendance
    private String status = ""; 
    private String attendance = ""; 
    private String tableNum = "";
    
    //Programme
    private String progName = "";
    private String faculty = "";
    
    //Paper
    private String date = "";
    private String session = "";
    private String bundleId = "";
    private String collector = "";
    
    //PaperInfo
    private String paperCode = "";
    private String paperDesc = "";
    
    //Venue
    private String venueName = "";
    private String venueSize = "";
   
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
    
    private int day; private int month; private int year;
    
    public InfoData(){
        
    }
    
    public InfoData(String type, String data) {
        this.type = type;
        this.data = data;
    }
    
    public InfoData( String ic, String name, String regNum,
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
    
    public InfoData( String ic, String name, String regNum,
                    String status, String attendance, String tableNum,
                    String progName, String faculty,
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
        this.date = date;
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
    public ArrayList<InfoData> getDataFromTable() throws CustomException{
        String result = "";
        String sql =    "SELECT CandidateInfo.IC, CandidateInfo.Name, CandidateInfo.RegNum "
                + ", CandidateAttendance.Status, CandidateAttendance.Attendance, CandidateAttendance.TableNumber "
                + ", Programme.Name AS ProgName, Programme.Faculty "
                + ", PaperInfo.PaperCode, PaperInfo.PaperDescription "
                + ", Venue.Name AS VenueName, Venue.Size, * "
                + " FROM CandidateInfo "
                + " LEFT OUTER JOIN CandidateAttendance ON CandidateInfo.CI_id = CandidateAttendance.CI_id "
                + " LEFT OUTER JOIN Programme ON CandidateInfo.Programme_id = Programme.Programme_id "
                + " LEFT OUTER JOIN Paper ON CandidateAttendance.Paper_id = Paper.Paper_id "
                + " LEFT OUTER JOIN PaperInfo ON Paper.PI_id = PaperInfo.PI_id "
                + " LEFT OUTER JOIN Venue ON Paper.Venue_id = Venue.Venue_id "
                + " WHERE CandidateAttendance.Status " + checkInput(this.status)
                + " AND CandidateAttendance.Attendance " + checkInput(this.attendance)
                + " AND CandidateInfo.RegNum "+ checkInput(this.regNum)
                + " AND Venue.Name "+ checkInput(this.venueName)
                + " AND CandidateAttendance.TableNumber "+ checkInput(this.tableNum)
                ;

        InfoData info;
        ArrayList<InfoData> list = new ArrayList<>();
        
        try (Connection conn = new ConnectDB().connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                
                info = new InfoData(     rs.getString("IC"), 
                                        rs.getString("Name"),
                                        rs.getString("RegNum"),
                                        rs.getString("Status"),
                                        rs.getString("Attendance"),
                                        rs.getString("TableNumber"),
                                        rs.getString("ProgName"),
                                        rs.getString("Faculty"),
                                        rs.getString("PaperCode"),
                                        rs.getString("PaperDescription"),
                                        rs.getString("VenueName"),
                                        rs.getString("Size")
                );
                   list.add(info);
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

//        Iterator itr = list.iterator();  
//        while(itr.hasNext()){  
//            GetData st=(GetData)itr.next();  
//            System.out.println(st.ic+" "+st.name+" "+st.regNum+
//                                " "+st.status+" "+st.attendance);     
//        }  

        if(list.isEmpty())
            throw new CustomException("No data found.");
        else
            return list;
    }
    
    /**
     * Get the info of a candidate from the connected database
     * @return list     The arraylist contain the info of the candidate
     */
    public ArrayList<InfoData> getCollectorList() throws CustomException{
        String result = "";
        String sql =    "SELECT * "
                + ", Programme.Name AS ProgName "
                + ", Venue.Name AS VenueName "
                + " FROM Collector "
                + " LEFT OUTER JOIN Paper ON Collector.Paper_id = Paper.Paper_id "
                + " LEFT OUTER JOIN Programme ON Programme.Programme_id = Paper.Programme_id "
                + " LEFT OUTER JOIN PaperInfo ON Paper.PI_id = PaperInfo.PI_id "
                + " LEFT OUTER JOIN Venue ON Paper.Venue_id = Venue.Venue_id "
                
//                + " WHERE BundleID " + checkInput(this.bundleId)
//                + " AND ProgName " + checkInput(this.progName)
//                + " AND PaperCode "+ checkInput(this.paperCode)
//                + " AND VenueName "+ checkInput(this.venueName)
//                + " AND StaffID "+ checkInput(this.collector)
                ;
//        System.out.print("Check: "+ checkInput(this.bundleId)+checkInput(this.collector) );
        InfoData data;
        ArrayList<InfoData> list = new ArrayList<>();
        
        try (Connection conn = new ConnectDB().connect();
                
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
//                System.out.print("Check: "+ rs.getString("StaffID"));
                data = new InfoData();
                
                data.setBundleId(rs.getString("BundleID"));
                data.setProgName(rs.getString("ProgName"));
                data.setPaperCode(rs.getString("PaperCode"));
                data.setVenueName(rs.getString("VenueName"));
                data.setCollector(rs.getString("StaffID"));
                
                if(rs.getString("BundleID") != null)
                   list.add(data);
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


        if(list.isEmpty())
            throw new CustomException("No data found.");
        else
            return list;
    }
    
    /**
     * Get the info of a candidate from the connected database
     * @return list     The arraylist contain the info of the candidate
     */
    public ArrayList<InfoData> getDataCheckMark() throws CustomException{
        String sql =    "SELECT CandidateInfo.IC, CandidateInfo.Name, CandidateInfo.RegNum "
                + ", Programme.Name AS ProgName, Programme.Faculty "
                + ", Paper.Date, Paper.Session "
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
                + " AND Paper.Date "+ checkInput(this.getDate())
                + " AND PaperInfo.PaperCode "+ checkInput(this.getPaperCode())  
                ;

        InfoData info;
        ArrayList<InfoData> list = new ArrayList<>();
        
        try {
            Connection conn = new ConnectDB().connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {

                info = new InfoData(     rs.getString("IC"), 
                                        rs.getString("Name"),
                                        rs.getString("RegNum"),
                                        rs.getString("ProgName"),
                                        rs.getString("Faculty"),
                                        rs.getString("Date"),
                                        rs.getString("PaperCode"),
                                        rs.getString("PaperDescription"),
                                        rs.getInt("Coursework"),
                                        rs.getInt("Practical")
                );
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
    
    public ArrayList<String> getList(String table, String data){
        ArrayList<String> list = new ArrayList<>();
        
        String sql = "SELECT DISTINCT " + data + " From " + table;
        
        
        try (Connection conn = new ConnectDB().connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                   list.add(rs.getString(data));
//                   System.out.println(rs.getString("Name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return list; 
    }
    
    public int getCountTotalCdd(String venue) throws SQLException{
        int count = 0;
        
        String sql = "SELECT *, Venue.Name as VenueName FROM CandidateAttendance "
                + " LEFT OUTER JOIN Paper ON Paper.Paper_id = CandidateAttendance.paper_id "
                + " LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + " WHERE Venue.Name " + checkInput(venue)
                + " ";
        Connection conn = new ConnectDB().connect();
        Statement stmt  = conn.createStatement();
        ResultSet rs    = stmt.executeQuery(sql);
        
        while (rs.next()) {
                count = count +1;
            }
            
            rs.close();
            stmt.close();
            conn.close();
        return count;
    }
    
    public int getCountAttdCdd(String attendance, String venue) throws SQLException{
        int count = 0;
        
        String sql = "SELECT COUNT(Attendance) AS totalCdd FROM CandidateAttendance"
                 + " LEFT OUTER JOIN Paper ON Paper.Paper_id = CandidateAttendance.paper_id "
                + " LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + " WHERE Attendance = ? "
                + " AND Venue.Name " + checkInput(venue) ;
        
        Connection conn = new ConnectDB().connect();
        PreparedStatement ps = conn.prepareStatement(sql);
        
        ps.setString(1, attendance);
        ResultSet rs    = ps.executeQuery();
        
        while (rs.next()) {

               count = rs.getInt("totalCdd");
            }
            
            rs.close();
            ps.close();
            conn.close();
        return count;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(String data) {
        this.data = data;
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
     * @return the bundleId
     */
    public String getBundleId() {
        return bundleId;
    }

    /**
     * @param bundleId the bundleId to set
     */
    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    /**
     * @return the collector
     */
    public String getCollector() {
        return collector;
    }

    /**
     * @param collector the collector to set
     */
    public void setCollector(String collector) {
        this.collector = collector;
    }
 
}

