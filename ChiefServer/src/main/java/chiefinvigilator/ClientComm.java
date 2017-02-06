/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

import globalvariable.CheckInType; 
import globalvariable.HashCode;
import globalvariable.InfoType;
import globalvariable.JSONKey;
import globalvariable.PaperBundle;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.Timer;
import javax.xml.bind.DatatypeConverter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import qrgen.QRgen;
import querylist.AttdList;
import querylist.Candidate;
import querylist.CddPapers;
import querylist.Paper;
import querylist.Papers;
import queue.ClientToServer;
import queue.ThreadMessage;

/**
 *
 * @author Krissy
 */
public class ClientComm extends Thread {
    private boolean signIn = false;
    private ServerSocket serverSocket;
    private Socket client;
    private ServerComm serverComm;
    private long threadId;
    private Staff staff;
    private ChiefServer chiefServer;
    private QRgen qrGen;
    private String challengeMsg;
    private ChiefControl chiefControl;
    
    //Send The message inside the queue
    private Thread queueThread = new Thread(new Runnable() {
         public void run()
         {
             while(true){
                 getQueuePacket();
             }
         }
        });
    
    private Thread connectionThread = new Thread(new Runnable(){
       public void run()
       {
           while(true){
           getConnectionFromStaff();
           }
       }
    });
    
    public ClientComm() {
    }
    
    public ClientComm(ServerSocket serverSocket, ServerComm serverComm,
                        ChiefServer chiefServer, QRgen qrGen,
                        ChiefControl chiefControl) throws IOException{
        this.serverSocket= serverSocket; 
        this.serverComm = serverComm;
        this.threadId = Thread.currentThread().getId();
        this.chiefServer = chiefServer;
        this.qrGen = qrGen;
        this.chiefControl = chiefControl;
//        serverComm.createReceiveQueue(this.threadId);
//        timer.start();
    }

    
    
    /***
     * @brief broadcast the signal to receive incoming message and response
     * @throws Exception 
     */
            @Override
            public void run()
            {
                try {
                    
                    this.serverSocket.setSoTimeout(100000);
                   queueThread.start(); // create a queue with the serverComm
                  //  getConnectionFromStaff();
                    
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
                
                
              }
            
    public void getConnectionFromStaff(){
        
            try{
                System.out.println("Socket "+this.serverSocket.getLocalPort()+" is accepting....");
                System.out.println("Address "+this.serverSocket.getInetAddress().getHostAddress()+" is accepting....");
                setClient(this.serverSocket.accept());
//                this.serverSocket.accept();

                System.out.println("connected to "+ getClient().getRemoteSocketAddress());
                chiefControl.createNewClientComm();
                while(getClient().isClosed() != true){

                                    System.out.println(getClient().isClosed());
                                        System.out.println("ClientComm Ready to receive message");
                                        String message = receiveMessage();
                                        System.out.println("ClientComm Received: " + message);

                                        if((message.equals("-1"))||(message.equals(null)))
                                            break;
                                        else{
                                            response(message);
                                        }
                                }
                            } catch (Exception ex) {
                                System.out.println("ClientComm run() error : " + ex.getMessage());
                            }
                        
    }

    
    
    private String receiveMessage() throws IOException{
//        System.out.println(socket.getLocalPort());
        InputStreamReader ir = new InputStreamReader(getClient().getInputStream());
        BufferedReader br = new BufferedReader(ir);

        return br.readLine();
    }
    
    private void sendMessage(String message) throws IOException{
        
        PrintStream out = new PrintStream(getClient().getOutputStream());
        out.println(message);
        out.flush();
        System.out.println("ClientComm Message sent: " + message);
    }
    
    public void response(String message) throws IOException { 
        if(message != null){
        JSONObject json = new JSONObject(message);
            System.out.println(json.toString());
            String checkIn;
            Integer deviceId = json.getInt(InfoType.DEVICE_ID);
            try {
                 
                checkIn = json.getString(InfoType.TYPE);
            
            
            switch(checkIn){
                case CheckInType.STAFF_LOGIN : 
                                    String id = json.getString(InfoType.ID_NO);
                                    this.setStaff(new Staff(id));
                                    this.getStaff().setInvInfo(id);
                                    this.getServerComm().getSendQueue(new ThreadMessage(this.getThreadId(),message,this.challengeMsg)); // send to main server
                                    break;
                                    
                case CheckInType.EXAM_INFO_LIST :  
                                    sendMessage(this.staff.prepareInvExamList(json.getString(InfoType.VALUE), deviceId).toString());
                                    break;
                                    
                case CheckInType.STAFF_RECONNECT:   
                                    sendMessage(replyRconnect().toString());
                                    break;
                                    
                case CheckInType.COLLECTION : 
                                    JSONObject paperBundle = json.getJSONObject(InfoType.PAPERBUNDLE_JSON);
                                    String bundleId = paperBundle.getString(InfoType.BUNDLE_ID);
                                    String staffId = json.getString(InfoType.COLLECTOR);
                                    
                                    boolean verifyResult = new Staff().verifyForCollector(staffId, bundleId);
                                    if(verifyResult)
                                        updatePaperCollector(bundleId, staffId);
//                                    System.out.println("Collection " +bundleId+" "+"staffId"+" "+deviceId+" "+verifyResult);
                                    sendMessage(booleanToJson(verifyResult, CheckInType.COLLECTION, deviceId).toString());
                                    break;
                    
                case CheckInType.CDDPAPERS : 
                                    this.getServerComm().getSendQueue(new ThreadMessage(this.getThreadId(),message)); //
                                    break;
                    
                case CheckInType.ATTDLIST : 
                                    downloadCddData(json.getJSONArray(InfoType.ATTENDANCE_LIST));
                                    sendMessage(booleanToJson(true, CheckInType.ATTDLIST, deviceId).toString());
                                    break;
                                  
                case CheckInType.PAPERS:      
                                    if(isSignedIn()){
                                        JSONObject jsonMsg = this.getStaff().prepareInvExamList(json.getString(JSONKey.VENUE), deviceId);
                                    }
                                    break;
                default: break;
            }
            
        } catch (Exception ex) {
            Logger.getLogger(ClientComm.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }
    
    /**
     * @Brief   To check whether the queue pack with ServerComm is empty or not
     * @return 
     */
    public void getQueuePacket(){
        String message = null;
        try {
            JSONObject json;
            
            ThreadMessage tm = this.getServerComm().getReceiveQueue(this.getThreadId());

//            System.out.println("ClientComm queue packet received : " + tm.getMessage());
            if(tm != null){
            json = new JSONObject(tm.getMessage());
            
            String type = json.getString(InfoType.TYPE);
            
            switch(type){
                case CheckInType.STAFF_LOGIN:        
                                    
                                    String id = json.getString(InfoType.ID_NO);
                                    Boolean result = json.getBoolean(InfoType.RESULT);
                                    String role = staff.getStaffRole(json.getString(InfoType.ID_NO));
                    
                                    if((result) && (role != null)){
                                        this.setStaff(new Staff(id));
                                        this.setSignIn(tm.getResultKey());
                                        message = this.getStaff().toJsonMsg(true, json.getInt(InfoType.DEVICE_ID), json.getString(InfoType.TYPE)).toString();
//                                        json.put(InfoType.ROLE, role);
                                        // add staff info to the chief GUI staffInfoTable
                                        this.chiefControl.addStaffInfoToGuiTable(this.staff);
                                    }
                                    else
                                        message = json.toString();
                                    
                                    sendMessage(message);
                                    break;
                
                case CheckInType.CDDPAPERS:   
                                    
                                    message = json.toString();
                                    sendMessage(message);
                                    break;
                                    
                case CheckInType.GEN_RANDOM_MSG:
                                    regenerateQRInterface(json.getString(InfoType.VALUE));
                                    if(!connectionThread.isAlive())
                                        connectionThread.start();
                                    break;                   
             
            }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerComm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ClientComm.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    /**
     * 
     * @param venue     contains the name of venue
     * @return attendance list of the venue 
     */
    public ArrayList<Candidate> getCddList(String venue) throws SQLException{
        ArrayList<Candidate> cddList = new ArrayList<>();
        
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT CandidateInfo.Name AS CandidateName, CandidateAttendance.Status AS CandidateStatus, "
                + "Venue.Name AS VenueName, CandidateAttendance.Attendance AS CandAttd ,"
                + "Programme.Name AS ProgrammeName"
                + ",* FROM Venue "
                + "LEFT OUTER JOIN Paper ON Paper.Venue_id = Venue.Venue_id "
                + "LEFT OUTER JOIN PaperInfo ON PaperInfo.PI_id = Paper.PI_id "
                + "LEFT OUTER JOIN CandidateAttendance ON CandidateAttendance.Paper_id = Paper.Paper_id "
                + "LEFT OUTER JOIN CandidateInfo ON CandidateInfo.CI_id = CandidateAttendance.CI_id "
                + "LEFT OUTER JOIN Programme ON Programme.Programme_id = CandidateInfo.Programme_id "
                + "WHERE VenueName = ? ";
//                + "AND Date = ? AND Session = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        
        ps.setString(1, venue);
//        ps.setString(2, time.getDate());
//        ps.setString(3, time.getSession());
   
        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            cddList.add(new Candidate(result.getString("ExamID"), result.getString("RegNum"),
                                       result.getString("CandidateStatus"), result.getString("PaperCode"),
                                       result.getString("ProgrammeName"), result.getString("CandAttd")));
        }
        
        result.close();
        ps.close();
        conn.close();
        
        return cddList;
    }
    
    /**
     * @brief   To get the paper list according to the venue
     * @param   venue     contains the name of venue
     * @return  paper list that are being examined at the venue 
     */
    public ArrayList<Paper> getPapers(String venue) throws SQLException{
        ArrayList<Paper> paperList = new ArrayList<>();
        AttdList attd;
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT Venue.Name AS VenueName "
                + ",* FROM Venue "
                + "LEFT OUTER JOIN Paper ON Paper.Venue_id = Venue.Venue_id "
                + "LEFT OUTER JOIN PaperInfo ON PaperInfo.PI_id = Paper.PI_id "
                + "WHERE VenueName = ? ";
//                + "AND Date = ? AND Session = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, venue);
//        ps.setString(2, time.getDate());
//        ps.setString(3, time.getSession());
   
        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            paperList.add(new Paper(result.getString("PaperCode"), result.getString("PaperDescription"),
                                result.getString("PaperStartNo"), result.getString("TotalCandidate")));
            
        }
        
        result.close();
        ps.close();
        conn.close();

        return paperList;
    }
    
    public ArrayList<Paper> getCddPapers(String candidateID) throws SQLException, Exception{
        ArrayList<Paper> cddPapers = new ArrayList<>();
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT Venue.Name AS VenueName "
                + ",* FROM CandidateInfo "
                + "LEFT OUTER JOIN CandidateAttendance ON CandidateAttendance.CI_id = CandidateInfo.CI_id "
                + "LEFT OUTER JOIN Paper ON Paper.Paper_id = CandidateAttendance.Paper_id "
                + "LEFT OUTER JOIN PaperInfo ON PaperInfo.PI_id = Paper.PI_id "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = Paper.Venue_id "
                + "WHERE RegNum = ? ";
//                + "AND Date = ? AND Session = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, candidateID);
//        ps.setString(2, time.getDate());
//        ps.setString(3, time.getSession());

        ResultSet result = ps.executeQuery();
        
        while ( result.next() ){
            cddPapers.add(new Paper(result.getString("PaperCode"),
                                        result.getString("PaperDescription"),
                                        result.getString("Date"),
                                        result.getString("Session"),
                                        result.getString("VenueName")
                                        ));
        }
        
        
        result.close();
        ps.close();
        conn.close();
        
        if(cddPapers.isEmpty())
            throw new Exception("\nData not Found!!");
                    
        return cddPapers;
    }
    
    
    public JSONArray cddListToJson(ArrayList<Candidate> attdList) throws JSONException{
        JSONArray jArr = new JSONArray();
        JSONObject attd;
        for(int i = 0; i < attdList.size(); i++){
            attd = new JSONObject();
            attd.put("ExamIndex", attdList.get(i).getExamId());
            attd.put("RegNum", attdList.get(i).getRegNum());
            attd.put("Status", attdList.get(i).getStatus());
            attd.put("Code", attdList.get(i).getPaperCode());
            attd.put("Programme", attdList.get(i).getProgramme());
            jArr.put(attd);
        }
        
        return jArr;
    }
    
    private JSONArray papersToJson(ArrayList<Paper> papers) throws JSONException{
        JSONArray jArr = new JSONArray();
        JSONObject attd;
        
        for(int i = 0; i < papers.size(); i++){
            attd = new JSONObject();
            
            attd.put("PaperCode", papers.get(i).getPaperCode());
            attd.put("PaperDesc", papers.get(i).getPaperDesc());
            attd.put("PaperStartNo", Integer.parseInt(papers.get(i).getPaperStartNo()));
            attd.put("PaperTotalCdd", Integer.parseInt(papers.get(i).getTotalCandidate()));
            jArr.put(attd);
        }
        
        return jArr;
    }
    
    private JSONArray cddPapersToJson(ArrayList<Paper> cddPapers) throws JSONException{
        JSONArray jArr = new JSONArray();
        JSONObject papers;
        for(int i = 0; i < cddPapers.size(); i++){
            papers = new JSONObject();
            
            papers.put("PaperCode", cddPapers.get(i).getPaperCode());
            papers.put("PaperDesc", cddPapers.get(i).getPaperDesc());
            papers.put("Date", cddPapers.get(i).getDate());
            papers.put("Session", cddPapers.get(i).getSession());
            papers.put("Venue", cddPapers.get(i).getVenue());
            jArr.put(papers);
        }
        
        return jArr;
    }
    
    public JSONObject prepareCddPapers(String candidateID) throws SQLException, JSONException, Exception{
        JSONObject json = new JSONObject();
        ArrayList<Paper> cddPapers = new ArrayList<>();
        
        cddPapers = getCddPapers(candidateID);
        json.put("PaperList", cddPapersToJson(cddPapers));
        json.put("Type", "CddPapers");
        json.put("Result", true);
        
        return json;
    }
    
    /**
     * @breif   to update the candidate list in the chief database
     * @param   cddList   contain the list of candidate object
     * @throws  SQLException 
     */
   public void updateCandidateAttendence(ArrayList<Candidate> cddList) throws SQLException{
        String sql = "UPDATE CandidateAttendance "
                + "SET Attendance = ?, TableNumber = ? "
                + "WHERE CI_id = (SELECT CI_id FROM CandidateInfo WHERE ExamID = ? ) ";
    
        Connection conn = new ConnectDB().connect();
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
         
        for(int i = 0; i < cddList.size(); i++)  {
            
            pstmt.setString(1,cddList.get(i).getAttendance());
            pstmt.setInt(2,cddList.get(i).getTableNo());
            pstmt.setString(3,cddList.get(i).getExamId());
            pstmt.executeUpdate();
            
        }
        
        pstmt.close();
        conn.close();
    }
   
   public void updatePaperCollector(String bundleId, String staffId) throws SQLException{
       String sql = "UPDATE Paper "
                + "SET Collector = ? "
                + "WHERE bundleId = ? ";
    
        Connection conn = new ConnectDB().connect();
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
  
            
            pstmt.setString(1,staffId);
            pstmt.setString(2,bundleId);
            pstmt.executeUpdate();

        
        pstmt.close();
        conn.close();
   }

   /**
    * @brief    To convert a boolean into JSON object
    * @param    b   contains a boolean
    * @return   boolean in JSONObject format
    * @throws   JSONException 
    */
    private JSONObject booleanToJson(boolean b, String type, Integer deviceId) throws JSONException{
        JSONObject bool = new JSONObject();
        
        bool.put(InfoType.RESULT, b);
        bool.put(InfoType.TYPE, type);
        bool.put(InfoType.DEVICE_ID, deviceId);
        
        return bool;
    }
    
    private JSONObject replyRconnect() throws JSONException{
        JSONObject bool = new JSONObject();
        
        bool.put(InfoType.RESULT, true);
        bool.put(InfoType.DUEL_MSG, this.challengeMsg);
        
        return bool;
    }
    
    
    private void downloadCddData(JSONArray jArr) throws JSONException, Exception{
        ArrayList<Candidate> cddList = new ArrayList<>();
        
        for(int i = 0; i < jArr.length(); i ++){
            Candidate candidate = new Candidate();
            candidate.fromJson(jArr.getJSONObject(i).toString());
            cddList.add(candidate);
        }
        updateCandidateAttendence(cddList);
        
    }

    /**
     * @return the signIn
     */
    public boolean isSignedIn() {
        return signIn;
    }

    /**
     * @param signIn the signIn to set
     */
    public void setSignIn(boolean signIn) {
        this.signIn = signIn;
    }

    /**
     * @return the server
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * @param server the server to set
     */
    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * @return the client
     */
    public Socket getClient() {
        return client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(Socket client) {
        this.client = client;
    }

    /**
     * @return the serverComm
     */
    public ServerComm getServerComm() {
        return serverComm;
    }

    /**
     * @param serverComm the serverComm to set
     */
    public void setServerComm(ServerComm serverComm) {
        this.serverComm = serverComm;
    }

    /**
     * @return the threadId
     */
    public long getThreadId() {
        return threadId;
    }

    /**
     * @param threadId the threadId to set
     */
    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    /**
     * @return the staff
     */
    public Staff getStaff() {
        return staff;
    }

    /**
     * @param staff the staff to set
     */
    public void setStaff(Staff staff) {
        this.staff = staff;
    }
    
    public void setChiefControl(ChiefControl chiefControl){
        this.chiefControl = chiefControl;
    }
    
    public void setChallengeMsg(String challengeMsg){
        System.out.println(challengeMsg);
        this.challengeMsg = challengeMsg;
    }
    
    public void regenerateQRInterface(String randomString) throws Exception{
        setChallengeMsg(randomString);
//        System.out.println("*******"+this.challengeMsg);
        this.qrGen.regenerateQR(this.chiefServer.getServerSocket(), this.serverComm, randomString);
    }
    
    /**
     * @brief   To ask serverComm to send a request to main server for a random message
     */
    public void requestForRandomMessage(){
        JSONObject json = new JSONObject();
        
        json.put(InfoType.TYPE, CheckInType.GEN_RANDOM_MSG);
        this.getServerComm().getSendQueue(new ThreadMessage(this.getThreadId(),json.toString()));
    }
    
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
   
    class TimerActionListener implements ActionListener{
      public void actionPerformed(ActionEvent e) {
          try {
              requestForRandomMessage();
          } catch (Exception ex) {
              System.out.println("Error TimerActionListener->actionPerformed");
          }
            
      }
    }
}
