/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainserver;

import globalvariable.CandidatePaperList;
import globalvariable.CheckInType;
import globalvariable.InfoType;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import querylist.CddPaper;
import querylist.ExamDataList;
import querylist.CandidateAttendance;

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
            chief = new ChiefData();
            
        } catch (IOException ex) {
            Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run(){
        while(true){
        try {
            System.out.println("Ready to connect....");
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
    }
    
    private String receiveMessage() throws IOException{
        
        String message = null;
        InputStream  ir = client.getInputStream();
        DataInputStream  br = new DataInputStream(ir);
        message = br.readUTF();
        return message;
    }
    
    private void sendMessage(String message) throws IOException{
        
        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        out.writeUTF(message);
        System.out.println("Message sent: " + message);
    }
    
    private void response(String message){
        
        try {
            JSONObject json = new JSONObject(message);
            System.out.println(json.getString(InfoType.TYPE));
            
            String id = null;
            String ps = null;
            String block = null;
            String randomMsg = null;
            Integer deviceId = 0;
            Integer threadId = null;
                                    
            switch(json.getString(InfoType.TYPE)){
                
                case CheckInType.CHIEF_LOGIN: 
                                    id = json.getString(InfoType.ID_NO);
                                    ps = json.getString(InfoType.PASSWORD);
                                    block = json.getString(InfoType.BLOCK);
                                    randomMsg = json.getString(InfoType.RANDOM_MSG);
                                    
                                    // if id is valid staff and status is CHIEF
                                    if((chief.verifyStaff(id, ps, randomMsg))&&(chief.getStatus(id, block).equals("CHIEF"))){ 
                                        //send the desired semester database
                                        sendMessage(booleanToJson(true,CheckInType.CHIEF_LOGIN).toString());
                                        chief.setChiefSignInTime(id);
                                    }
                                    else{
                                        sendMessage(booleanToJson(false,CheckInType.CHIEF_LOGIN).toString());
                                    }
                                    break;
                                    
                case CheckInType.EXAM_DATA_DOWNLOAD:
                                    sendMessage(dbToJson(json.getString(InfoType.ID_NO), json.getString(InfoType.BLOCK)));
                                    break;
                        
                case CheckInType.STAFF_LOGIN:    
                                    
                                    try{
                                        id = json.getString(InfoType.ID_NO);
                                        ps = json.getString(InfoType.PASSWORD);
                                        randomMsg = json.getString(InfoType.RANDOM_MSG);
                                        threadId = json.getInt(InfoType.THREAD_ID);
                                        deviceId = json.getInt(InfoType.DEVICE_ID);

                                        if(new ChiefData().verifyStaff(id, ps, randomMsg)){
                                            sendMessage(staffSignInReply(id,threadId, CheckInType.STAFF_LOGIN, true, deviceId).toString());
                                            System.out.println("Staff "+json.getString(InfoType.ID_NO)+" was signed in");
                                            chief.setInvSignInTime(json.getString(InfoType.ID_NO));
                                        }
                                        else
                                            sendMessage(staffSignInReply(id, threadId, CheckInType.STAFF_LOGIN, false, deviceId).toString());
                                    }catch(SQLException ex){
                                        Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
                                        sendMessage(staffSignInReply(id, 0, CheckInType.STAFF_LOGIN, false, deviceId).toString());
                                    }
                                    break;
                                    
                case CheckInType.STAFF_LOGIN_FROM_CHIEF_SERVER:
                                    try{
                                        id = json.getString(InfoType.ID_NO);
                                        ps = json.getString(InfoType.PASSWORD);
                                        randomMsg = json.getString(InfoType.RANDOM_MSG);
                                    
                                        if(new ChiefData().verifyStaff(id, ps, randomMsg)){
                                            sendMessage(staffSignInReply(id, 0, CheckInType.STAFF_LOGIN_FROM_CHIEF_SERVER, true, 0).toString());
                                            System.out.println("Staff "+json.getString(InfoType.ID_NO)+" was signed in from ChiefServer");
                                            chief.setInvSignInTime(json.getString(InfoType.ID_NO));
                                        }
                                        else
                                            sendMessage(staffSignInReply(id, 0, CheckInType.STAFF_LOGIN_FROM_CHIEF_SERVER, false, 0).toString());
                                    }catch(SQLException ex){
                                        Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
                                        sendMessage(staffSignInReply(id, 0, CheckInType.STAFF_LOGIN_FROM_CHIEF_SERVER, false, 0).toString());
                                    }
                                    break;
                                    
                case CheckInType.CDDPAPERS:   
                                    deviceId = json.getInt(InfoType.DEVICE_ID);
                                    threadId = json.getInt(InfoType.THREAD_ID);
                                    sendMessage(cddPaperListToJson(json.getString(InfoType.VALUE), deviceId, threadId));
                                    break;
                                    
                case CheckInType.GEN_RANDOM_MSG:
                                    sendMessage(challengMsgToJson(json, generateRandomString()).toString());
                                    break;
                                    
                case CheckInType.EXAM_DATA_SUBMIT:
                                    try{
                                        updateDB(json.getJSONObject(InfoType.VALUE).toString());
                                        sendMessage(booleanToJson(true,CheckInType.EXAM_DATA_SUBMIT).toString());
                                    }catch(Exception ex){
                                        sendMessage(booleanToJson(false,CheckInType.EXAM_DATA_SUBMIT).toString());
                                    }
                                    break;
                                    
            }
        } catch (Exception ex) {
            Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
   public void updateDB(String data) throws IOException, SQLException{
        
        ObjectMapper mapper = new ObjectMapper();
        ChiefData chief = new ChiefData();
        ExamDataList examDataList = mapper.readValue(data, ExamDataList.class);
        chief.updateCddAttdList(examDataList.getCddAttd());
        chief.updatePaper(examDataList.getPaper());
    }
    
    /**
    * @brief    To convert a boolean into JSON object
    * @param    b   contains a boolean
    * @return   boolean in JSONObject format
    * @throws   JSONException 
    */
    private JSONObject booleanToJson(boolean b, String type) throws JSONException{
        JSONObject bool = new JSONObject();
        bool.put(InfoType.TYPE, type);
        bool.put(InfoType.RESULT, b);
        
        return bool;
    }
    
    private JSONObject staffSignInReply(String id, Integer threadId, String type, boolean bool, Integer deviceID){
        JSONObject json = new JSONObject();
        
        try {
            json.put(InfoType.RESULT, bool);
            json.put(InfoType.DEVICE_ID, deviceID);
            if(type.equals(CheckInType.STAFF_LOGIN))
                json.put(InfoType.THREAD_ID, threadId);

            json.put(InfoType.TYPE, type);
            json.put(InfoType.ID_NO, id);
            
        } catch (Exception ex) {
            Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }
    
    private String dbToJson(String id, String block){
        String jsonString = null;
        ObjectMapper mapper = new ObjectMapper();
        JSONObject json = new JSONObject();
        try {
            ExamDataList examDataList = new ExamDataList(
                    chief.getCddAttdList(block, chief.getSessionId()),
                    chief.getCddInfoList(block),
                    chief.getChAndReList(block),
                    chief.getCollectorList(),
                    chief.getInvigilatorList(block),
                    chief.getPaperList(block, chief.getSessionId()),
                    chief.getPaperInfoList(block),
                    chief.getProgrammeList(),
                    chief.getStaffInfoList(block,chief.getSessionId()),
                    chief.getVenueList(block)
            );
            //System.out.println(chief.getCddAttdList().get(0).getCa_id());
            jsonString = mapper.writeValueAsString(examDataList);
            JSONObject jsonData = new JSONObject(jsonString);
            json.put(InfoType.TYPE, CheckInType.EXAM_DATA_DOWNLOAD);
            json.put(InfoType.VALUE, jsonData);
        } catch (SQLException | IOException ex) {
            Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        
//        System.out.println(json.toString());
        return json.toString();
    }
    
    /**
     *  JSON: { "Result":true               or            "Result":false
     *          "Type":"CddPapers"                        "Type": "CddPapers"
     *          "PaperList":[{
     *              "PaperCode"
     *              "PaperDesc"
     *              "Date"
     *              "Session"
     *              "Venue"}...]
     *          }
     * 
     * @param regNum
     * @return
     * @throws IOException 
     */
    private String cddPaperListToJson(String regNum, Integer deviceId, Integer threadId) throws IOException{
        
        ObjectMapper mapper = new ObjectMapper();
        JSONObject json = new JSONObject();
        ArrayList<CddPaper> cddPaperList;
        
        try {
            String jsonString = null;
            cddPaperList = chief.getCddPaperList(regNum);
            
            JSONArray jsonPaperList = new JSONArray();
//            jsonString = mapper.writeValueAsString(cddPaperList);
            for(int i = 0; i < cddPaperList.size(); i++){
                JSONObject jsonPaper = new JSONObject();
                jsonPaper.put(CandidatePaperList.PAPER_CODE, cddPaperList.get(0).getPaperCode());
                jsonPaper.put(CandidatePaperList.PAPER_DATE, cddPaperList.get(0).getDate());
                jsonPaper.put(CandidatePaperList.PAPER_DESC, cddPaperList.get(0).getPaperDesc());
                jsonPaper.put(CandidatePaperList.PAPER_SESSION, cddPaperList.get(0).getSession());
                jsonPaper.put(CandidatePaperList.PAPER_VENUE, cddPaperList.get(0).getVenue());
                
                jsonPaperList.put(jsonPaper);
            }
//            JSONArray jsonData = new JSONArray(jsonString);
            json.put(InfoType.RESULT, true);
            json.put(InfoType.VALUE, jsonPaperList);
            json.put(InfoType.DEVICE_ID, deviceId);
            json.put(InfoType.THREAD_ID, threadId);
        } catch (SQLException ex) {
            json.put(InfoType.RESULT, false);
        }
        
        json.put(InfoType.TYPE, CheckInType.CDDPAPERS);
        return json.toString();
    }
    
    /**
     * 
     */
    protected JSONObject challengMsgToJson(JSONObject jsonMessage, String challengMsg){
        JSONObject json = new JSONObject(jsonMessage.toString());
        System.out.println(json.toString());
        json.put(InfoType.VALUE, challengMsg);
        System.out.println(jsonMessage.toString());
        return json;
    }
    
    /**
     * @brief   To generate a random message with certain character string
     * @return 
     */
    protected String generateRandomString() {
        String seed = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"+Long.toString(System.nanoTime());
        StringBuilder str = new StringBuilder();
        Random rnd = new Random();
        while (str.length() < 18) {
            int index = (int) (rnd.nextFloat() * seed.length());
            str.append(seed.charAt(index));
        }
        String saltStr = str.toString();
        return saltStr;
    }
    
}
