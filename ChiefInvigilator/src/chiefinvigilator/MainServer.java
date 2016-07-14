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
import jsonconvert.JsonConvert;

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
        JsonConvert jsonParser = new JsonConvert();
        Staff staff = new Staff();
        
        try {
            Socket mainSocket = sSocket.accept();
            System.out.println("\nJust connected to "+ mainSocket.getRemoteSocketAddress());
            InputStreamReader ir = new InputStreamReader(mainSocket.getInputStream());
            BufferedReader br = new BufferedReader(ir);
            
            String reteriveMsg  = br.readLine();
            
            //retrive json parser to get the id and password
            
            staff = jsonParser.jsonToSignIn(reteriveMsg);
            System.out.println(reteriveMsg);
            boolean verify = new ServerComm().staffVerify(staff.getID(),staff.getPassword());
            System.out.println(verify);
            if(verify){
                System.out.println("LOL");
                staff = new ServerComm().staffGetInfo(staff.id);
            }
            String jsonStaff = jsonParser.staffInfoToJson(verify, staff);
            
            
            
            if(reteriveMsg != null){
                System.out.println(jsonStaff);
                PrintStream out = new PrintStream(mainSocket.getOutputStream());
                out.println(jsonStaff);
                out.flush();
                DataOutputStream asd =
                 new DataOutputStream(mainSocket.getOutputStream());
            asd.writeUTF(jsonStaff);
            mainSocket.close();

                
                System.out.println("end!!");
            }
        } catch (IOException ex) {
            System.out.print(ex.getMessage());
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
         }
        }).start();
        
    }
    
}
