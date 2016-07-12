/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Krissy
 */
public class MainServer {
    public static void main(String [] args) throws Exception{
        
        MainServer mainServer = new MainServer();
        mainServer.boardCast(new ServerSocket());
    }
    
    public void boardCast(ServerSocket sSocket) throws Exception{
        (new Thread() {
    @Override
    public void run()
    {
        try {
            Socket mainSocket = sSocket.accept();
            System.out.println("\nJust connected to "+ mainSocket.getRemoteSocketAddress());
            InputStreamReader ir = new InputStreamReader(mainSocket.getInputStream());
            BufferedReader br = new BufferedReader(ir);
            
            String message  = br.readLine();
            System.out.print(message);
            
            if(message != null){
                
                PrintStream out = new PrintStream(mainSocket.getOutputStream());
                out.println("Hello Client");
                out.flush();
                
                System.out.println("end!!");
                System.out.println(sSocket.getLocalPort());
            }
        } catch (IOException ex) {
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        }
         }
        }).start();
        
    }
    
}
