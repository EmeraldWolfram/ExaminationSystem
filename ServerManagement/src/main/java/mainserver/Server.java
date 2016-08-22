/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainserver;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import querylist.CandidateAttendance;

/**
 *
 * @author Krissy
 */
public class Server extends Thread{
    
    private ServerSocket serverSocket;
    private Socket socket;
    ChiefData chief;
            
    public Server(int port) throws IOException{
        serverSocket = new ServerSocket(port);
//      serverSocket.setSoTimeout(10000);
    broadCast();

    }
    
    @Override
    public void run(){
        
    }
    
      
   public void broadCast(){
       (new Thread() {
            @Override
            public void run()
            {
                try {
                    socket = serverSocket.accept();
              
                
                JSONObject jsonMsg = new JSONObject();
                String retrieveMsg = null;
                

                System.out.println("\nJust connected to "+ socket.getRemoteSocketAddress());
                    
                do{
                    
                    try{
                    retrieveMsg  = receiveMessage();
                    
                    System.out.println("message received " + retrieveMsg);
                    jsonMsg = new JSONObject(retrieveMsg);
                    
                    chief = new ChiefData(jsonMsg.getString("IdNo"),
                                                        jsonMsg.getString("Password"),
                                                        jsonMsg.getString("Block"));


                    if((chief.staffVerify())&&(chief.getStatus().equals("CHIEF"))){
                        jsonMsg = booleanToJson(true);
                    }
                    else
                        jsonMsg = booleanToJson(false);

                    if(retrieveMsg != null){
                        sendMessage(jsonMsg.toString());
                        
                    }
                    
             
                    }catch(IOException ex) {
//                        System.out.println(retrieveMsg);
                        throw new Exception(ex);
                    }catch(Exception ex) {
//                        System.out.println(retrieveMsg);
                        System.out.println("ErrorMsg: "+ex.getMessage());
                        ex.printStackTrace();
                    }   
                    System.out.println(chief.getStatus()+"test");
                }while((chief.staffVerify() != true)||(!(chief.getStatus().equals("CHIEF"))));
                    
                sendMessage(chief.jooqtest1());
                    
                while(chief.staffVerify() != false){
                    System.out.println("Ready for incoming message");
                    retrieveMsg  = receiveMessage();
                    System.out.println("Message received: "+ retrieveMsg);
                       
                }
                    System.out.println("Signed Out");
                        socket.close();

                        System.out.println("end!!");
                } catch (Exception ex) {
                    System.out.println("2ErrorMsg: "+ex.getMessage());
                    ex.printStackTrace();
                }
              }
        }).start();
        
   }
    

   private String receiveMessage() throws IOException{
//        System.out.println(socket.getLocalPort());
//        InputStreamReader ir = new InputStreamReader(socket.getInputStream());
//        BufferedReader br = new BufferedReader(ir);
//
//        return br.readLine();
        
        String message = null;
        InputStream  ir = socket.getInputStream();
        DataInputStream  br = new DataInputStream(ir);
        message = br.readUTF();
        return message;
    }
    
    private void sendMessage(String message) throws IOException{
        
        DataOutputStream out =
                 new DataOutputStream(socket.getOutputStream());
            out.writeUTF(message);
        System.out.println("Message sent: " + message);
    }
    
    /**
    * @brief    To convert a boolean into JSON object
    * @param    b   contains a boolean
    * @return   boolean in JSONObject format
    * @throws   JSONException 
    */
    private JSONObject booleanToJson(boolean b) throws JSONException{
        JSONObject bool = new JSONObject();
        
        bool.put("Result", b);
        bool.put("Type", "Ack");
        
        return bool;
    }
    
    
}
