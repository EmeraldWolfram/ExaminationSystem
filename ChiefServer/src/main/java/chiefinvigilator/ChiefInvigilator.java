/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Krissy
 */
public class ChiefInvigilator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ChiefGui gui = new ChiefGui();
        try {
            ChiefControl control = new ChiefControl(gui);
        } catch (Exception ex) {
            Logger.getLogger(ChiefInvigilator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
