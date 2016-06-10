/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samplejdbc;
import java.sql.*;
/**
 *
 * @author Krissy
 */
public class SampleJDBC {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
       SampleJDBC sample = new SampleJDBC();
       
       sample.getFromIC("931120129989");
       
    }
    
    /**
     * To select an entire row of data
     */
    public void selectAll(){
        String sql = "SELECT IC,Name,RegNum,ProgrammeID FROM CandidateInfo";
        
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            
            while (rs.next()) {
                System.out.println(rs.getString("IC") +  "\t\t" + 
                                   rs.getString("Name") + "\t\t" +
                                   rs.getString("RegNum") + "\t\t" +
                                   rs.getString("ProgrammeID"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Get the info of a candidate from a specific ic
     * @param ic
     */
    public void getFromIC(String ic){
               String sql = "SELECT IC,Name,RegNum,ProgrammeID "
                          + "FROM CandidateInfo WHERE IC = ?";
        
        try (Connection conn = this.connect();
            PreparedStatement pstmt  = conn.prepareStatement(sql)){
            
            // set the value
            pstmt.setString(1,ic);
         
            ResultSet rs  = pstmt.executeQuery();
            
            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("IC") +  "\t\t" + 
                                   rs.getString("Name") + "\t\t" +
                                   rs.getString("RegNum") + "\t\t" +
                                   rs.getString("ProgrammeID"));
            }
            System.out.println("End of searching.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
        
    /**Insert new data
     * 
     * @param   id
     *          name
     *          regNum
     *          programmeID
     * 
     */
    public void insert(String ic, String name, String regNum, String programmeID) {
 
        String sql = "INSERT INTO CandidateInfo (IC,Name,RegNum,ProgrammeID) " +
                   "VALUES (?,?,?,?)"; 

         try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ic);
            pstmt.setString(2, name);
            pstmt.setString(3, regNum);
            pstmt.setString(4, programmeID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Connect to the test.db database
     *
     * @return the Connection object
     */
    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:Student.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    
     /**
     * Connect to a database, if database file not found, a new database file will be created
     *
     * @param fileName the database file name
     */
    public static void createNewDatabase(String fileName) {
 
        String url = "jdbc:sqlite:" + fileName;
 
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
 
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
