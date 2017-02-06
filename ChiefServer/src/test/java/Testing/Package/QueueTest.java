/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing.Package;

import chiefinvigilator.ServerComm;
import org.junit.Test;
import queue.ThreadMessage;

/**
 *
 * @author Krissy
 */
public class QueueTest {
    
    @Test
    public void testCreateReceiveQueue(){
        ServerComm serverComm = new ServerComm();
        
        serverComm.createReceiveQueue(12);
    }
    
    @Test
    public void testSendQueue(){
        ServerComm serverComm = new ServerComm();
        
        serverComm.getSendQueue(new ThreadMessage());
    }
    
}
