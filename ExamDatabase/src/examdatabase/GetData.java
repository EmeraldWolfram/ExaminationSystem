/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examdatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;



/**
 *
 * @author Krissy
 */
public class GetData {
    
    public String type = "";
    public String data = "";
    
    //CandidateInfo
    public String ic = ""; 
    public String name = ""; 
    public String regNum = "";
    
    //CandidateAttendance
    public String status = ""; 
    public String attendance = ""; 
    public String tableNum = "";
    
    //Programme
    public String progName = "";
    public String faculty = "";
    
    //Paper
    public String date = "";
    public String session = "";
    
    //PaperInfo
    public String paperCode = "";
    public String paperDesc = "";
    
    //Venue
    public String venueName = "";
    public String venueSize = "";
   
    //Invigilator And Assistant
    public String invStatus = "";
    public String invAttendnce = "";
    
    //StaffInfo
    public String staffID = "";
    public String staffName = "";
    public String staffFaculty = "";
    
    //StudentMark
    public Integer coursework;
    public Integer pratical;
    
    int day; int month; int year;
    
    public GetData(){
        
    }
    
    public GetData(String type, String data) {
        this.type = type;
        this.data = data;
    }
    
    public GetData( String ic, String name, String regNum,
                    String progName, String faculty,
                    String date,
                    String paperCode, String paperDesc,
                    Integer coursework, Integer pratical
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
        this. pratical = pratical;
        

    }
    
    public GetData( String ic, String name, String regNum,
                    String status, String attendance, String tableNum,
                    String progName, String faculty,
                    String date, String session,
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
    public ArrayList<GetData> getDataFromTable() throws CustomException{
        String result = "";
        String sql =    "SELECT CandidateInfo.IC, CandidateInfo.Name, CandidateInfo.RegNum "
                + ", CandidateAttendance.Status, CandidateAttendance.Attendance, CandidateAttendance.TableNumber "
                + ", Programme.Name AS ProgName, Programme.Faculty "
                + ", Paper.Date, Paper.Session "
                + ", PaperInfo.PaperCode, PaperInfo.PaperDescription "
                + ", Venue.Name AS VenueName, Venue.Size "
                + " FROM CandidateInfo "
                + " LEFT OUTER JOIN CandidateAttendance ON CandidateInfo.IC = CandidateAttendance.CandidateInfoIC "
                + " LEFT OUTER JOIN Programme ON CandidateInfo.ProgrammeIndex = Programme.ProgrammeIndex "
                + " LEFT OUTER JOIN Paper ON CandidateAttendance.PaperIndex = Paper.PaperIndex "
                + " LEFT OUTER JOIN PaperInfo ON Paper.PIIndex = PaperInfo.PIIndex "
                + " LEFT OUTER JOIN Venue ON Paper.VenueIndex = Venue.VenueIndex "
                + " WHERE CandidateInfo.IC " + checkInput(this.ic)
                + " AND CandidateInfo.Name " + checkInput(this.name)
                + " AND CandidateInfo.RegNum "+ checkInput(this.regNum)
                + " AND CandidateAttendance.Status "+ checkInput(this.status)
                + " AND CandidateAttendance.Attendance "+ checkInput(this.attendance)
                + " AND CandidateAttendance.TableNumber "+ checkInput(this.tableNum)
                + " AND ProgName "+ checkInput(this.progName)
                + " AND Programme.Faculty "+ checkInput(this.faculty)
                + " AND Paper.Date "+ checkInput(this.date)
                + " AND Paper.Session "+ checkInput(this.session)
                + " AND PaperInfo.PaperCode "+ checkInput(this.paperCode)
                + " AND VenueName "+ checkInput(this.venueName)
                + " AND Venue.Size "+ checkInput(this.venueSize)              
                ;

        GetData info;
        ArrayList<GetData> list = new ArrayList<>();
        
        try (Connection conn = new ConnectDB().connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {

                info = new GetData(     rs.getString("IC"), 
                                        rs.getString("Name"),
                                        rs.getString("RegNum"),
                                        rs.getString("Status"),
                                        rs.getString("Attendance"),
                                        rs.getString("TableNumber"),
                                        rs.getString("ProgName"),
                                        rs.getString("Faculty"),
                                        rs.getString("Date"),
                                        rs.getString("Session"),
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
    public ArrayList<GetData> getDataCheckMark() throws CustomException{
        String sql =    "SELECT CandidateInfo.IC, CandidateInfo.Name, CandidateInfo.RegNum "
                + ", Programme.Name AS ProgName, Programme.Faculty "
                + ", Paper.Date, Paper.Session "
                + ", StudentMark.Coursework, StudentMark.Pratical"
                + ", PaperInfo.PaperCode, PaperInfo.PaperDescription "
                + " FROM StudentMark "
                + " LEFT OUTER JOIN CandidateInfo ON StudentMark.RegNum = CandidateInfo.RegNum "
                + " LEFT OUTER JOIN CandidateAttendance ON CandidateInfo.IC = CandidateAttendance.CandidateInfoIC "
                + " LEFT OUTER JOIN Programme ON CandidateInfo.ProgrammeIndex = Programme.ProgrammeIndex "
                + " LEFT OUTER JOIN Paper ON CandidateAttendance.PaperIndex = Paper.PaperIndex "
                + " LEFT OUTER JOIN PaperInfo ON PaperInfo.PIIndex = StudentMark.PIIndex "
                + " LEFT OUTER JOIN Venue ON Paper.VenueIndex = Venue.VenueIndex "
                + " WHERE CandidateInfo.IC " + checkInput(this.ic)
                + " AND CandidateInfo.Name " + checkInput(this.name)
                + " AND CandidateInfo.RegNum "+ checkInput(this.regNum)
                + " AND ProgName "+ checkInput(this.progName)
                + " AND Programme.Faculty "+ checkInput(this.faculty)
                + " AND Paper.Date "+ checkInput(this.date)
                + " AND PaperInfo.PaperCode "+ checkInput(this.paperCode)  
                ;

        GetData info;
        ArrayList<GetData> list = new ArrayList<>();
        
        try (Connection conn = new ConnectDB().connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {

                info = new GetData(     rs.getString("IC"), 
                                        rs.getString("Name"),
                                        rs.getString("RegNum"),
                                        rs.getString("ProgName"),
                                        rs.getString("Faculty"),
                                        rs.getString("Date"),
                                        rs.getString("PaperCode"),
                                        rs.getString("PaperDescription"),
                                        rs.getInt("Coursework"),
                                        rs.getInt("Pratical")
                );
                   list.add(info);
            }
            

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
 
}

