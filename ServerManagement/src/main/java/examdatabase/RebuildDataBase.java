/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examdatabase;

import examdatabase.ConnectDB;
import java.sql.*;

/**
 *
 * @author Krissy
 */
public class RebuildDataBase {
    
    public RebuildDataBase(){
    String sql = "DELETE from StudentMark";
    Statement stmt = null;
    
    try{
        
      Class.forName("org.sqlite.JDBC");
      Connection conn = new ConnectDB().connect();
      conn.setAutoCommit(false);
      stmt = conn.createStatement();
      stmt.executeUpdate(sql);
      
      
      sql = "INSERT INTO StudentMark (MarkIndex,RegNum,PIIndex) " +
                   "VALUES (1, '15WAR09183',2 );"; 
      stmt.executeUpdate(sql);

      sql = "INSERT INTO StudentMark (MarkIndex,RegNum,PIIndex,Practical,Coursework) " +
                   "VALUES (2, '16WAR25342',2,45,12);"; 
      stmt.executeUpdate(sql);

      sql = "INSERT INTO StudentMark (MarkIndex,RegNum,PIIndex) " +
                   "VALUES (3, '16WAR25342',1 );";  
      stmt.executeUpdate(sql);

      sql = "INSERT INTO StudentMark (MarkIndex,RegNum,PIIndex) " +
                   "VALUES (4, '15WAR09183',3 );"; 
      stmt.executeUpdate(sql);
      
      sql = "INSERT INTO StudentMark (MarkIndex,RegNum,PIIndex) " +
                   "VALUES (5, '15WAD23345',3 );"; 
      stmt.executeUpdate(sql);
      
      conn.commit();
      ResultSet rs = stmt.executeQuery( "SELECT * FROM StudentMark;" );

      while ( rs.next() ) {
         
//         System.out.println( "ID = " + rs.getString("RegNum") );
      }
      rs.close();
      stmt.close();
      conn.close();
    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
  }
}
