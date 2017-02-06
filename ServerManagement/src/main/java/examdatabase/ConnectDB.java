/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examdatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Krissy
 */
public class ConnectDB {
    static String url = "";
    static String month = "";
    static String year = "";
    
    public ConnectDB(){
    }
    
    public void setConnection(String month, String year){
        this.month = month;
        this.year = year;
        
    }
    
    /**
     * Connect to the test.db database
     *
     * @return the Connection object
     */
    public static Connection connect() {
        // SQLite connection string
        
        if(month.equals("")||year.equals(""))
            url = "jdbc:sqlite:FEB_MAR_2016.db";
        else
            url = "jdbc:sqlite:"+month+"_"+year+".db";
        
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
