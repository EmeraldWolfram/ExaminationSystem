/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
    private ServerSocket server;
    private Socket client;
    private ServerComm serverComm;
    private long threadId;
    private Staff staff;
    
    private Thread t = new Thread(new Runnable() {
         public void run()
         {
             while(true){
                 try {
                     sendMessage(sendInvInfo());// Insert some method call here.
                 } catch (IOException ex) {
                     Logger.getLogger(ClientComm.class.getName()).log(Level.SEVERE, null, ex);
                 }
             }
         }
        });
    
    public ClientComm() {
    }
    
    public ClientComm(ServerSocket server, ServerComm serverComm){
        this.server= server; 
        this.serverComm = serverComm;
        this.threadId = Thread.currentThread().getId();
        serverComm.createReceiveQueue(this.threadId);
        
//        this.run();
    }

    
    
    /***
     * @brief broadcast the signal to receive incoming message and response
     * @throws Exception 
     */
            @Override
            public void run()
            {
                
                
                try {
                    setClient(getServer().accept());
                    t.start();
                    System.out.println("connected to "+ getClient().getRemoteSocketAddress());
                    while(getClient().isClosed() != true){
                        System.out.println("ClientComm Ready to receive message");
                        String message = receiveMessage();
                        System.out.println("ClientComm Received: " + message);
                        response(message);
                    }
                    
                } catch (SocketTimeoutException  ex) {
                    System.out.println("\nSocket timed out!");
                } catch (IOException ex) {
                    System.out.print(ex.getMessage());
                } catch (Exception ex) {
                    System.out.print(ex.getMessage());
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
    
    public void response(String message) throws IOException, JSONException{ 
        if(message != null){
        JSONObject json = new JSONObject(message);
            System.out.println(json.toString());
            String checkIn;
            try {
                checkIn = json.getString("CheckIn");
            
            
            switch(checkIn){
                case "Identity":    this.setStaff(new Staff(json.getString("IdNo")));
                                    this.getStaff().getInvInfo();
                                    this.getServerComm().getSendQueue(new ThreadMessage(this.getThreadId(),message));
                                    break;
                                 
                case "Collection" : break;
                    
                case "CddPapers" : 
                                    this.getServerComm().getSendQueue(new ThreadMessage(this.getThreadId(),message));
                                    break;
                    
                case "AttdList" : 
                                    downloadCddData(json.getJSONArray("CddList"));
                                    sendMessage(booleanToJson(true).toString());
                                    break;
                                  
                case "Papers":      if(isSignIn()){
                                        JSONObject jsonMsg = this.getStaff().prepareInvExamList();
                                    }
                
                                    break;
                default: break;
            }
            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            sendMessage(booleanToJson(false).toString());
        }
        }
    }
    
    
    public String sendInvInfo(){
        String message = null;
        try {
            JSONObject json;
            
            ThreadMessage tm = this.getServerComm().getReceiveQueue(this.getThreadId());
            System.out.println("Staff sign in : " + tm.getMessage());
            json = new JSONObject(tm.getMessage());
            
            if(json.getBoolean("Result")){
                this.setStaff(new Staff(json.getString("IdNo")));
                this.setSignIn(tm.getResultKey());
                message = this.getStaff().prepareInvExamList().toString();
            }
            else
                message = json.toString();
            
        } catch (IOException ex) {
            Logger.getLogger(ServerComm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ClientComm.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return message;
    }
    
    /**
     * 
     * @param venue     contains the name of venue
     * @return attendance list of the venue 
     */
    public ArrayList<Candidate> getCddList(String venue) throws SQLException{
        ArrayList<Candidate> cddList = new ArrayList<>();
        AttdList attd;
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT CandidateInfo.Name AS CandidateName, CandidateAttendance.Status AS CandidateStatus, "
                + "Venue.Name AS VenueName, CandidateAttendance.Attendance AS CandAttd ,"
                + "Programme.Name AS ProgrammeName"
                + ",* FROM Venue "
                + "LEFT OUTER JOIN Paper ON Paper.Venue_id = Venue.Venue_id "
                + "LEFT OUTER JOIN PaperInfo ON PaperInfo.PI_id = Paper.PI_id "
                + "LEFT OUTER JOIN CandidateAttendance ON CandidateAttendance.Paper_id = Paper.Paper_id "
                + "LEFT OUTER JOIN CandidateInfo ON CandidateInfo.IC = CandidateAttendance.CandidateInfoIC "
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
                + "LEFT OUTER JOIN CandidateAttendance ON CandidateAttendance.CandidateInfoIC = CandidateInfo.IC "
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
                + "WHERE CandidateInfoIC = (SELECT IC FROM CandidateInfo WHERE ExamID = ? ) ";
    
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
    public boolean isSignIn() {
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
    public ServerSocket getServer() {
        return server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(ServerSocket server) {
        this.server = server;
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

   
}
