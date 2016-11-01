/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import org.json.JSONException;
import org.json.JSONObject;
import queue.ClientToServer;
import queue.ThreadMessage;
import serverquerylist.ExamDataList;

/**
 *
 * @author Krissy
 */
public class ServerComm extends Thread implements Runnable{

    Socket socket;
    boolean signIn = false;
    ChiefData chief;
    Queue serverQueue;
    HashMap clientQueueList;
    
    Thread t = new Thread(new Runnable() {
         public void run()
         {
            while(true){
            System.out.println("Check");
            invSignIn();// Insert some method call here.
            }
         }
        });
    
    public ServerComm() throws IOException, SQLException{
        connectToServer();
        chief = new ChiefData();
        serverQueue = new LinkedList();
        clientQueueList = new HashMap();
        
        this.start();

    }
    
    public boolean getSignIn(){
        return this.signIn;
    }
    
    public void connectToServer() throws IOException{

//        System.out.println("Connecting to " + serverName +
//		 " on port " + port);
//         socket = new Socket("localhost", 5006);
//         System.out.println("Just connected to " 
//		 + socket.getRemoteSocketAddress());
        
    }
    
    @Override
    public void run(){
        t.start();
        try {
            
            while(socket.isClosed() != true){
            
            System.out.println("\n connected to "+ socket.getRemoteSocketAddress());
            String message = receiveMessage();
            System.out.println("ServerComm Message Received: " + message);
            response(message);
            
            }
            System.out.println("End?");
        } catch (IOException ex) {
            Logger.getLogger(ServerComm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void invSignIn(){
        try {
            while(this.serverQueue.isEmpty()){
                sleep(1000);
                System.out.print(this.serverQueue.size());
            };
            System.out.println("check123");
            ThreadMessage tm = (ThreadMessage) this.serverQueue.poll();
            System.out.println("Staff sign in : " + tm.toJsonString());
            sendMessage(tm.toJsonString());
            
        } catch (IOException ex) {
            Logger.getLogger(ServerComm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerComm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loginToServer(String id, String password, String block) throws Exception{
        String message;
        
        sendMessage(identityInToJson(id,password,block));
        
//        socket.close();
    }
    
    public String identityInToJson(String id, String password, String block) throws JSONException{
        JSONObject json = new JSONObject();
        
        json.put("CheckIn","ChiefSignIn");
        json.put("IdNo",id);
        json.put("Password",password);
        json.put("Block",block);
        
        return json.toString();
    }
    
    private String receiveMessage() throws IOException{
//        System.out.println(socket.getLocalPort());
        InputStream  ir = socket.getInputStream();
        DataInputStream  br = new DataInputStream(ir);

        return br.readUTF();
    }
    
    private void sendMessage(String message) throws IOException{
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF(message);
            
        System.out.println("ServerComm Message sent: " + message);
    }
    
    public void messageVerify(String message){
        boolean bool = false;
        JSONObject json = new JSONObject(message);
        
        this.signIn = (boolean) json.get("Result");
        
        
    }
    
    public void response(String message){
        try {
            JSONObject json = new JSONObject(message);
            System.out.println("ServerComm: " +json.getString("CheckIn"));
            
            switch(json.getString("CheckIn")){
                case "ChiefSignIn": if(json.getBoolean("Result"))
                                        this.signIn = true;
                                    else
                                        ChiefGui.showSignInErrorMsg();
                                    break;
                                    
                case "ExamData":    System.out.println(json.getJSONObject("Values").toString());
                                    updateDB(json.getJSONObject("Values").toString());
                                    break;
                                    
                case "Identity":    putQueue(json.getLong("ThreadId"), message);
                                    break;
                
                case "CddPapers":   putQueue(json.getLong("ThreadId"), message);
                                    break;
                                    
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void getSendQueue(ThreadMessage tm){
        this.serverQueue.add(tm);
        System.out.println("ServerComm Size = "+this.serverQueue.size());
    }
    
    public void createReceiveQueue(long threadId){
        this.clientQueueList.put(threadId, new LinkedList());
    }
    
    public void putQueue(long threadId, String message){
        
        LinkedList list = (LinkedList)(this.clientQueueList.get(threadId));
        list.add(new ThreadMessage(threadId, message));
        this.clientQueueList.replace(threadId, list);
    }
    
    public ThreadMessage getReceiveQueue(long threadId){
        LinkedList list = (LinkedList)(this.clientQueueList.get(threadId));
        while(list.isEmpty()){
            list = (LinkedList)(this.clientQueueList.get(threadId));
        }
        return (ThreadMessage)list.poll();
    }
    
    public void updateDB(String data) throws IOException, SQLException{
        ObjectMapper mapper = new ObjectMapper();
        ChiefData chief = new ChiefData();
        ExamDataList examDataList = mapper.readValue(data, ExamDataList.class);
        chief.updateCddAttdList(examDataList.getCddAttd());
        chief.updateCddInfoList(examDataList.getCddInfo());
        chief.updateChAndRe(examDataList.getChAndRe());
        chief.updateInvigilator(examDataList.getInv());
        chief.updatePaper(examDataList.getPaper());
        chief.updatePaperInfo(examDataList.getPaperInfo());
        chief.updateProgramme(examDataList.getProgramme());
        chief.updateStaffInfo(examDataList.getStaffInfo());
        chief.updateVenue(examDataList.getVenue());
    }
    
    public static void createQueue(String threadId){
        
    }
    
}
