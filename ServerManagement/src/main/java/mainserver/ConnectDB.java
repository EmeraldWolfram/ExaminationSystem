/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Krissy
 */
public class ConnectDB {
    
    static String month;
    static String year;
    static String name;
    
    public ConnectDB(){
        this.name = "FEB_MAR_2016.db";
    }
    
    public ConnectDB(String name){
        this.name = name;
    }
    
    
    /**
     * Connect to the test.db database
     *
     * @return the Connection object
     */
    public Connection connect() {
        // SQLite connection string
        
        String url = "jdbc:sqlite:"+name;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
