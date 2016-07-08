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
public class ExamDatabase {
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
//        new GUIapp().setVisible(true);
        new ConnectDB().setConnection("FEB_MAR", "2016_for_test");
        
        new RebuildDataBase();
       
    }
    
    
}


