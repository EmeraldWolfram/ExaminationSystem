/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examdatabase;

import java.lang.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;



/**
 *
 * @author Krissy
 */
public class GetData {
    
    public String type;
    public String data;
    public String ic; 
    public String name; 
    public String regNum;
    public String status; 
    public String attendance; 
    public int paperIndex;
    public String progName; int day; int month; int year;
    
    public GetData(){
        
    }
    
    public GetData(String type, String data) {
        this.type = type;
        this.data = data;
    }
    
    public GetData( String ic, String name, String regNum,
                    String status, String attendance){
//                    int day, int month, int year){
        
        this.ic = ic;
        this.name = name;
        this.regNum = regNum;
        this.status = status;
        this.attendance = attendance;
//        this.day = day;
//        this.month = month;
//        this.year = year;
        
    }
     /**
     * Connect to the test.db database
     *
     * @return the Connection object
     */
    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:ExamDatabase.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    
    public String checkInput(String input){
        if(input != "")
            return "= '" + input + "'";
        else
            return "IS NOT NULL";
        
    }
    
    /**
     * Get the info of a candidate 
     * @param 
     */
    public ArrayList<GetData> getDataFromTable(){
        String result = "";
        String sql =    "SELECT * FROM CandidateInfo JOIN CandidateAttendance"
                + " ON CandidateInfo.IC = CandidateAttendance.CandidateInfoIC "
                + "WHERE IC " + checkInput(this.ic)
                + " AND Name " + checkInput(this.name)
                + " AND RegNum "+ checkInput(this.regNum)
                + "\nUNION\n"
                + "SELECT * FROM CandidateInfo LEFT OUTER JOIN CandidateAttendance"
                + " ON CandidateInfo.IC = CandidateAttendance.CandidateInfoIC "
                + "WHERE IC " + checkInput(this.ic)
                + " AND Name " + checkInput(this.name)
                + " AND RegNum "+ checkInput(this.regNum);       
//        System.out.print(sql);
        GetData info;
        ArrayList<GetData> list = new ArrayList<GetData>();
        
        try (Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {

                info = new GetData(  rs.getString("IC"), 
                                        rs.getString("Name"),
                                        rs.getString("RegNum"),
                                        rs.getString("Status"),
                                        rs.getString("Attendance")
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
        
        return list;
    }
    
    /**
     * To select an entire row of data
     */
    public ArrayList<GetData> selectAllFromCandidateInfo(){
        String sql = "SELECT * FROM CandidateInfo ";
        String result = "";
        GetData info;
        ArrayList<GetData> list = new ArrayList<GetData>();  

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            
            while (rs.next()) {
//                System.out.println(rs.getInt("CIIndex") +  "\t\t" + 
//                                   rs.getString("IC") +  "\t\t" + 
//                                   rs.getString("Name") + "\t\t" +
//                                   rs.getString("RegNum") + "\t\t" +
//                                   rs.getInt("ProgrammeIndex"));
//                result = result + "\n" + rs.getString("CIIndex") +  "\t\t" + 
//                                   rs.getString("IC") + "\t\t" +
//                                   rs.getString("Name") + "\t\t" +
//                                   rs.getString("RegNum") + "\t\t" +
//                                   rs.getString("ProgrammeIndex");
                   info = new GetData(  rs.getString("IC"), 
                                        rs.getString("Name"),
                                        rs.getString("RegNum"),
                                        rs.getString("Status"),
                                        rs.getString("Attedance")
                   );
                   list.add(info);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        Iterator itr = list.iterator();  
        while(itr.hasNext()){  
            GetData st=(GetData)itr.next();  
            System.out.println(st.ic+" "+st.name+" "+st.regNum);  
        }  
        return list;
    }
}

