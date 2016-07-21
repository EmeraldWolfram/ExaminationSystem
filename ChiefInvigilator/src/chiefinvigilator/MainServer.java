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
import org.json.JSONArray;
import org.json.JSONObject;


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
            
            JSONObject jsonMsg = new JSONObject();
            jsonParser.staffInfoToJson(verify, staff);
            if(verify){
                staff = new ServerComm().staffGetInfo(staff.id);
                JSONObject staffInfo = jsonParser.staffInfoToJson(verify, staff);
                JSONArray papers = jsonParser.papersToJson(new ServerComm().getPapers(staff.getVenue()));
                JSONArray attdList = jsonParser.attdListToJson(new ServerComm().getAttdList(staff.getVenue()));
                jsonMsg = jsonParser.jsonStringConcatenate(staffInfo, papers, attdList);
                
            }
            else
                jsonMsg = new JsonConvert().booleanToJson(false);
            
            
            
            
            if(reteriveMsg != null){
                System.out.println(jsonMsg.toString());
                PrintStream out = new PrintStream(mainSocket.getOutputStream());
                out.println(jsonMsg);
                out.flush();
                
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
