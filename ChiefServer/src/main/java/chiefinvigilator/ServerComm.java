/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

import globalvariable.CheckInType;
import globalvariable.InfoType;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
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

    ReentrantLock sendMutex = new ReentrantLock();

    Socket socket;
    boolean signIn = false;
    ChiefData chiefData;
    ChiefControl chiefControl;
    Queue serverQueue = new LinkedList();
    HashMap clientQueueList = new HashMap();
    
    //static Semaphore mutex = new Semaphore(1);
    Thread queueThread = new Thread(new Runnable() {
         public void run()
         {
            while(true){
            System.out.println("Check");
            invSignIn();
            }
         }
        });
    
    public ServerComm(){
        chiefData = new ChiefData();
        serverQueue = new LinkedList();
        clientQueueList = new HashMap();
    }
    
    public ServerComm(ChiefControl chiefControl){
        chiefData = new ChiefData();
        serverQueue = new LinkedList();
        clientQueueList = new HashMap();
        this.chiefControl = chiefControl;
    }
    
    public boolean getSignIn(){
        return this.signIn;
    }
    
    public void connectToServer(String hostName, int port) throws IOException{
        socket = new Socket(hostName, port);
//            this.start();
       
    }
    
    /**
     * 
     * @param hostName
     * @param port
     * @return 
     */
    public boolean isSocketAliveUitlitybyCrunchify(String hostName, int port) {
		boolean isAlive = false;
 
		// Creates a socket address from a hostname and a port number
		SocketAddress socketAddress = new InetSocketAddress(hostName, port);
		Socket socket = new Socket();
 
		// Timeout required - it's in milliseconds
		int timeout = 2000;
 
		try {
			socket.connect(socketAddress, timeout);
			socket.close();
			isAlive = true;
 
		} catch (SocketTimeoutException exception) {
			System.out.println("SocketTimeoutException " + hostName + ":" + port + ". " + exception.getMessage());
		} catch (IOException exception) {
			System.out.println(
					"IOException - Unable to connect to " + hostName + ":" + port + ". " + exception.getMessage());
		}
		return isAlive;
	}
    
    @Override
    public void run(){
        this.queueThread.start();
        try {
            System.out.println("\nconnected to "+ socket.getRemoteSocketAddress());
            
            while(this.socket.isClosed() != true){
            
            String message = receiveMessage();
            System.out.println("ServerComm Message Received: " + message);
            response(message);
            
            }
            
        } catch (IOException ex) {
            Logger.getLogger(ServerComm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void invSignIn(){
        try {
            while(this.serverQueue.isEmpty()){
                sleep(1000);
//                System.out.print(this.serverQueue.size());
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
    
    public void signInToServer(String id, String password, String value, String type) throws Exception{
        sendMessage(identityInToJson(id,password,value,type));
    }
    
    public String identityInToJson(String id, String password, String value, String type) throws JSONException, Exception{
        JSONObject json = new JSONObject();
        Hmac hmac = new Hmac();
        String randomMessage = hmac.generateRandomString();
        
        
        json.put(InfoType.TYPE,type);
        json.put(InfoType.ID_NO,id);
        json.put(InfoType.RANDOM_MSG,randomMessage);
        json.put(InfoType.HASHCODE,hmac.encode(password, randomMessage));
        
        if(type.equals(CheckInType.CHIEF_LOGIN))
            json.put(InfoType.BLOCK,value);
//        json.put(InfoType.THREAD_ID,"fff");
        
        return json.toString();
    }
    
    private String receiveMessage() throws IOException{
//        System.out.println(socket.getLocalPort());
        InputStream  ir = socket.getInputStream();
        DataInputStream  br = new DataInputStream(ir);

        return br.readUTF();
    }
    
    private void sendMessage(String message) throws IOException{
        System.out.println("SeverComm Sending: " + message);
        sendMutex.lock();
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF(message);
        sendMutex.unlock();
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
            System.out.println("ServerComm: " +json.getString(InfoType.TYPE));
            
            switch(json.getString(InfoType.TYPE)){
                case CheckInType.CHIEF_LOGIN: 
                                    if(json.getBoolean(InfoType.RESULT)){
                                        this.signIn = true;
                                        this.chiefControl.updateGuiLoggedChief();
                                        this.chiefControl.setGuiPanelEnable(true);
                                        downloadDB(this.chiefControl.getChiefId(), this.chiefControl.getChiefBlock());
                    
                                    }
                                    else
                                        this.chiefControl.chiefGui.popUpErrorPane("Chief Sign in : Wrong ID/PASSWORD");
                                    break;
                                    
                case CheckInType.STAFF_LOGIN_FROM_CHIEF_SERVER:
                                    if(json.getBoolean(InfoType.RESULT)){
                                        String id = json.getString(InfoType.ID_NO);
                                        Staff staff = new Staff(id);
                                        staff.setInvInfo(id);
                                        this.chiefControl.addStaffInfoToGuiTable(staff);
                                    }
                                    else{
                                        this.chiefControl.chiefGui.popUpErrorPane("Staff Siaaaaaaaaaaaaaaaaaagn in : Wrong ID/PASSWORD");
                                        
                                    }
                                    break;
                                    
                case CheckInType.EXAM_DATA_DOWNLOAD:    
                                    System.out.println(json.getJSONObject(InfoType.VALUE).toString());
                                    updateDB(json.getJSONObject(InfoType.VALUE).toString());
                                    break;
                                    
                case CheckInType.STAFF_LOGIN:    
                                    putQueue(json.getLong(InfoType.THREAD_ID), message);
                                    break;
                
                case CheckInType.CDDPAPERS:   
                                    putQueue(json.getLong(InfoType.THREAD_ID), message);
                                    break;
                                    
                case CheckInType.GEN_RANDOM_MSG:
                                    putQueue(json.getLong(InfoType.THREAD_ID), message);
                                    break;
                
                case CheckInType.EXAM_DATA_SUBMIT:
                                    if(json.getBoolean(InfoType.RESULT))
                                        this.chiefControl.chiefGui.popUpInfoPane("Atttendance list submitted.");
                                    else
                                        this.chiefControl.chiefGui.popUpErrorPane("Failed to submitted.");
                                    break;
                                    
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerComm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ServerComm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ServerComm.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    private void setInviSignInTime(String id) throws SQLException{
        
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        Connection conn = new ConnectDB().connect();
        
        String sql = "UPDATE InvigilatorAndAssistant "
                + "SET SignInTime = ? , Attendance = ? "
                + "WHERE StaffID = ?";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        
        ps.setString(1, dateFormat.format(cal.getTime()));
        ps.setString(2, "PRESENT");
        ps.setString(3, id);
        
        ps.executeUpdate();
        ps.close();
    }
    
    public boolean invIsAssigned(String id) throws SQLException{
        boolean assign = false;
        Connection conn = new ConnectDB().connect();
        
        String sql = "SELECT Status FROM InvigilatorAndAssistant "
                + "WHERE StaffID = ? ";
//                + "AND Session_id = (SELECT Session_id FROM SessionAndDate WHERE Session = ? AND Date =?) ";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        
        ps.setString(1, id);
//        ps.setString(2, session);
//        ps.setString(3, date);
        
        ResultSet result = ps.executeQuery();
        
        assign = result.next();
                
        result.close();
        ps.close();
        conn.close();
        
        return assign;
    }
    
    
    public void getSendQueue(ThreadMessage tm){
        this.serverQueue.add(tm);
//        System.out.println("ServerComm Size = "+this.serverQueue.size());
    }
    
//    public void createReceiveQueue(long threadId){
//        this.clientQueueList.put(threadId, new LinkedList());
//    }
    
    public void putQueue(long threadId, String message){
        this.clientQueueList.put(threadId, new ThreadMessage(threadId, message));
//        LinkedList list = (LinkedList)(this.clientQueueList.get(threadId));
//        list.add(new ThreadMessage(threadId, message));
//        this.clientQueueList.replace(threadId, list);
        
    }
    
    /**
     * @brief   Continuously
     * @param threadId
     * @return 
     */
    public ThreadMessage getReceiveQueue(long threadId) throws InterruptedException{
        Boolean set = false;
//        LinkedList list = new LinkedList();
        ThreadMessage tm = new ThreadMessage();
//            list = (LinkedList)(this.clientQueueList.get(threadId));
//            readSemaphore.release();

            if(this.clientQueueList.containsKey(threadId)){
//                list = (LinkedList) this.clientQueueList.remove(threadId);
                tm = (ThreadMessage) this.clientQueueList.remove(threadId);
                return tm;
            }
            else 
                return null;
//            if(list.isEmpty()==false){
//                
//                System.out.println("List is empty= "+list.isEmpty());
//                tm = (ThreadMessage) list.get(0);
//                
//                if(tm != null && tm.getThreadId() == threadId){
////                    writeMutex.lock();
//                    while(readSemaphore.availablePermits() != MAX_AVAILABLE);
//                    list = (LinkedList)(this.clientQueueList.get(threadId));
//                    tm = (ThreadMessage)list.poll();
//                    set = true;
////                    writeMutex.unlock();
//                    return tm;
//                }
                
//            }
//        return null;
    }
    
    public void updateDB(String data) throws IOException, SQLException{
        
        ObjectMapper mapper = new ObjectMapper();
        ChiefData chief = new ChiefData();
        
        ExamDataList examDataList = mapper.readValue(data, ExamDataList.class);
        chief.updateCddAttdList(examDataList.getCddAttd());
        chief.updateCddInfoList(examDataList.getCddInfo());
        chief.updateChAndRe(examDataList.getChAndRe());
        chief.updateCollector(examDataList.getCollector());
        chief.updateInvigilator(examDataList.getInv());
        chief.updatePaper(examDataList.getPaper());
        chief.updatePaperInfo(examDataList.getPaperInfo());
        chief.updateProgramme(examDataList.getProgramme());
        chief.updateStaffInfo(examDataList.getStaffInfo());
        chief.updateVenue(examDataList.getVenue());
    }
    
    public void downloadDB(String id, String block) throws IOException{
        JSONObject json = new JSONObject();
        json.put(InfoType.TYPE, CheckInType.EXAM_DATA_DOWNLOAD);
        json.put(InfoType.ID_NO, id);
        json.put(InfoType.BLOCK, block);
        
        this.sendMessage(json.toString());
        
    }
    
    public void submitDB(String id, String block) throws IOException{
        this.sendMessage(dbToJson(id, block));
    }
    
    private String dbToJson(String id, String block){
        String jsonString = null;
        ObjectMapper mapper = new ObjectMapper();
        JSONObject json = new JSONObject();
        
        try {
            ExamDataList examDataList = new ExamDataList(
                    chiefData.getCddAttdList(block),
                    chiefData.getPaperList()
            );
            jsonString = mapper.writeValueAsString(examDataList);
            JSONObject jsonData = new JSONObject(jsonString);
            json.put(InfoType.TYPE, CheckInType.EXAM_DATA_SUBMIT);
            json.put(InfoType.VALUE, jsonData);
        } catch (SQLException ex) {
            Logger.getLogger(ChiefControl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ChiefControl.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return json.toString();
    }
    

}
