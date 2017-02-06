/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainserver;

import examdatabase.ExamDataControl;
import examdatabase.ExamDataGUI;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Krissy
 */
public class MainServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ExamDataGUI gui = new ExamDataGUI();
        gui.setVisible(true);
        (new Thread(new ExamDataControl(gui))).start();
        (new Thread(new MessageListener(5006))).start();
//        MessageListener ml = new MessageListener(5006);
        
    }
    
}
