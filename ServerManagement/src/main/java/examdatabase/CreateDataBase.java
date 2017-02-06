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
public class CreateDataBase {
    String filename;
    
    CreateDataBase(String filename){
      this.filename = filename;
      createNewDatabase();
      createNewTable();
      
    }
    private void createNewTable() {
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
                + "	ProgrammeIndex  INT    \n"
                + ");";
        stmt.execute(sql);
        
        sql = "\nCREATE TABLE IF NOT EXISTS CandidateAttendance (\n"
                + "	CAIndex         INT    PRIMARY KEY,\n"
                + "	CI_ID TEXT    ,\n"
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
        stmt.execute(sql);
        
        sql = "\nCREATE TABLE IF NOT EXISTS PaperInfo (\n"
                + "	PIIndex  INT        PRIMARY KEY,\n"
                + "	PaperCode           TEXT    ,\n"
                + "	PaperDescription    TEXT    \n"
                + ");";
        stmt.execute(sql);
        
        sql = "\nCREATE TABLE IF NOT EXISTS Paper (\n"
                + "	PaperIndex      INT        PRIMARY KEY,\n"
                + "	PIIndex         INT    ,\n"
                + "	Date            TEXT   ,\n"
                + "     Session         TEXT   ,\n"
                + "     VenueIndex      INT    \n"
                + ");";
        stmt.execute(sql);
        
        sql = "\nCREATE TABLE IF NOT EXISTS Venue (\n"
                + "	VenueIndex  INT        PRIMARY KEY,\n"
                + "	Name        TEXT    ,\n"
                + "	Size        INT    \n"
                + ");";
        stmt.execute(sql);
        
        sql = "\nCREATE TABLE IF NOT EXISTS StaffInfo (\n"
                + "	SIIndex     INT        PRIMARY KEY,\n"
                + "     StaffID     TEXT   ,\n"
                + "     Name        TEXT    \n"
                + "     Faculty     TEXT    \n"
                + ");";
        stmt.execute(sql);
        
        sql = "\nCREATE TABLE IF NOT EXISTS InvigilatorAndAssistant (\n"
                + "	IAIndex        INT        PRIMARY KEY,\n"
                + "	SIIndex        INT    ,\n"
                + "	PaperIndex     INT   ,\n"
                + "     Status         TEXT   ,\n"
                + "     Attendance     TEXT    \n"
                + ");";
        stmt.execute(sql);
        
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void createNewDatabase() {
 
        String url = "jdbc:sqlite:" + filename;
 
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
