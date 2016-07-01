/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examdatabase;

import java.sql.*;

/**
 *
 * @author Krissy
 */
public class ConnSqlite {
    
    public ConnSqlite(){
    };
    
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
    
    public static void createNewTable(String filename) {
        // SQLite connection string
        String url = "jdbc:sqlite:"+filename;
        
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            
        String sql;
        
        
         //SQL statement for creating a new table
        sql = "CREATE TABLE IF NOT EXISTS CandidateInfo (\n"
                + "	CIIndex         INT    PRIMARY KEY,\n"
                + "	IC              TEXT   UNIQUE ,\n"
                + "	Name            TEXT    ,\n"
                + "	RegNum          TEXT    ,\n"
                + "	ProgrammeIndex  INT     \n"
                + ");";
        stmt.execute(sql);
        
        sql = "\nCREATE TABLE IF NOT EXISTS CandidateAttendance (\n"
                + "	CAIndex         INT    PRIMARY KEY,\n"
                + "	CandidateInfoIC TEXT    ,\n"
                + "	PaperIndex      INT     ,\n"
                + "	Status          TEXT    ,\n"
                + "	Attendance      TEXT    ,\n"
                + "	TableNumber     INT     \n"
                + ");";
        stmt.execute(sql);
        
        sql = "\nCREATE TABLE IF NOT EXISTS Programme (\n"
                + "	ProgrammeIndex  INT    PRIMARY KEY,\n"
                + "	Name            TEXT    ,\n"
                + "	Faculty         TEXT    \n"
                + ");";
        
        
            // create a new table
        stmt.execute(sql);
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
        String url = "jdbc:sqlite:ExamDatabase.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    
    /**Insert new data
     * 
     * @param   ciIndex
     *          ic
     *          name
     *          regnum
     *          programmeIndex
     * 
     */
    public void insertCandidateInfo(int ciIndex, String ic, String name, String regNum, int programmeIndex) {
 
        String sql = "INSERT INTO CandidateInfo (CIIndex,IC,Name,RegNum,ProgrammeIndex) " +
                "VALUES (?,?,?,?,?)"; 

         try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ciIndex);
            pstmt.setString(2, ic);
            pstmt.setString(3, name);
            pstmt.setString(4, regNum);
            pstmt.setInt(5, programmeIndex);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println(".............");
            System.out.println(e.getMessage());
        }
    }
}
