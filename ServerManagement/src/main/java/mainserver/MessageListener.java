/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import querylist.ExamDataList;

/**
 *
 * @author Krissy
 */
public class MessageListener extends Thread{
    ServerSocket server;
    int port;
    ChiefData chief;
    Socket client;
    
    public MessageListener(int port){
        try {
            server = new ServerSocket(port);
            this.start();
        } catch (IOException ex) {
            Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run(){
        
        try {
            client = server.accept();
            System.out.println("\n connected to "+ client.getRemoteSocketAddress());
            while(client.isClosed() != true){
                
                String message = receiveMessage();
                System.out.println("Received: " + message);
                response(message);
            }
        } catch (IOException ex) {
            Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String receiveMessage() throws IOException{
        
        String message = null;
        InputStream  ir = client.getInputStream();
        DataInputStream  br = new DataInputStream(ir);
        message = br.readUTF();
        return message;
    }
    
    private void sendMessage(String message) throws IOException{
        
        DataOutputStream out =
                 new DataOutputStream(client.getOutputStream());
            out.writeUTF(message);
        System.out.println("Message sent: " + message);
    }
    
    private void response(String message){
        
        try {
            JSONObject json = new JSONObject(message);
            System.out.println(json.getString("CheckIn"));
            switch(json.getString("CheckIn")){
                case "ChiefSignIn": chief = new ChiefData(json.getString("IdNo"),
                                                            json.getString("Password"),
                                                            json.getString("Block"));
                                    System.out.println(chief.staffVerify()+chief.getStatus());                 
                                    if((chief.staffVerify())&&(chief.getStatus().equals("CHIEF"))){
                                        sendMessage(booleanToJson(true,"ChiefSignIn").toString());
                                        sendMessage(dbToJson());
                                      }
                                    else
                                        sendMessage(booleanToJson(false,"ChiefSignIn").toString());
                                    
                case "Identity":    JSONObject jsonIdentity;
                                    if(new ChiefData().invVerify(json.getString("IdNo"), json.getString("Password")))
                                        jsonIdentity = booleanToJson(true,"Identity");
                                    else
                                        jsonIdentity = booleanToJson(false,"Identity");
//                                    System.out.println(jsonIdentity);
                                    jsonIdentity.put("ThreadId", json.getInt("ThreadId"));
                                    jsonIdentity.put("IdNo", json.getString("IdNo"));
                                    sendMessage(jsonIdentity.toString());
                                    break;
                                    
                case "CddPapers":   //sendMessage(prepareCddPapers(json.getString("Value")).toString());
                                    break;
            }
        } catch (Exception ex) {
            Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    /**
    * @brief    To convert a boolean into JSON object
    * @param    b   contains a boolean
    * @return   boolean in JSONObject format
    * @throws   JSONException 
    */
    private JSONObject booleanToJson(boolean b, String type) throws JSONException{
        JSONObject bool = new JSONObject();
        bool.put("Type", "ACK");
        bool.put("Result", b);
        bool.put("CheckIn", type);
        
        return bool;
    }
    
    private String dbToJson(){
        String message = null;
        ObjectMapper mapper = new ObjectMapper();
        JSONObject json = new JSONObject();
        try {
            ExamDataList examDataList = new ExamDataList(
                    chief.getCddAttdList(),
                    chief.getCddInfoList(),
                    chief.getChAndReList(),
                    chief.getInvigilatorList(),
                    chief.getPaperList(),
                    chief.getPaperInfoList(),
                    chief.getProgrammeList(),
                    chief.getStaffInfoList(),
                    chief.getVenueList()
            );
            System.out.println(chief.getCddAttdList().get(0).getCa_id());
            message = mapper.writeValueAsString(examDataList);
            JSONObject jsonData = new JSONObject(message);
            json.put("CheckIn", "ExamData");
            json.put("Values", jsonData);
        } catch (SQLException | IOException ex) {
            Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
//        System.out.println(json.toString());
        return json.toString();
    }
}