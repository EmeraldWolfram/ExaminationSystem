/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing.Package;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mainserver.ChiefData;
import org.junit.Test;

/**
 *
 * @author Krissy
 */
public class ChiefData_Test {
    
    @Test
    public void testUpdateTime(){
        ChiefData chiefData = new ChiefData();
        
        try {
            chiefData.setChiefSignInTime("staff3");
        } catch (SQLException ex) {
            Logger.getLogger(ChiefData_Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
