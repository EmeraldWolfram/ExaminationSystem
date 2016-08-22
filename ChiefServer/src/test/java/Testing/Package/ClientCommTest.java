/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing.Package;

import chiefinvigilator.ClientComm;
import chiefinvigilator.Staff;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author Krissy
 */
public class ClientCommTest {
    
    @Test
    public void testStaffRequest(){
        JSONObject json = new JSONObject();
        try {
            ClientComm client = new ClientComm();
            Staff staff = new Staff("staff1");
                    
                    
                    } catch (Exception ex) {
            Logger.getLogger(ClientCommTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }
    
    @Test
    public void testSendInfo(){
        ClientComm client = Mockito.mock(ClientComm.class);
        
        
    }
}
