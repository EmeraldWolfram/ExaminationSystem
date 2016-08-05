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
import jsonconvert.JsonConvert;
import static jsonconvert.JsonConvert.jsonToAttdList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import querylist.AttdList;
import querylist.Candidate;
import querylist.CddPapers;
import querylist.Paper;
import querylist.Papers;

/**
 *
 * @author Krissy
 */
public class ClientComm {
    boolean signIn = false;
    static ArrayList<Staff> staffList = new ArrayList<>();
    static ArrayList<Candidate> candidateList = new ArrayList<>();
    ServerSocket sSocket;
    Socket socket;
    
    public ClientComm(ServerSocket sSocket){
        this.sSocket= sSocket; 
    }
    
    /***
     * @brief broadcast the signal to receive incoming message and response
     * @throws Exception 
     */
    public void boardCast() throws Exception{
        (new Thread() {
            @Override
            public void run()
            {
                try {
                    socket = sSocket.accept();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
                Staff staff = new Staff();
                JSONObject jsonMsg = new JSONObject();
                String retreiveMsg = null;
                
                try {
                    ChiefGui.regenerateQRInterface();
                    System.out.println("\nJust connected to "+ socket.getRemoteSocketAddress());
                    
                    do{

                    retreiveMsg  = receiveMessage();
                    staff.setIdPsFromJsonString(retreiveMsg);
                    System.out.println(retreiveMsg);
                    
                    signIn = staff.staffVerify();

                    if(signIn){
                        staff.getInvInfo();
                        jsonMsg = staffRequestSend(staff);
                        ChiefGui.addStaffInfoToRow(staff);
                    }
                    else
                        jsonMsg = booleanToJson(false);

                    if(retreiveMsg != null){
//                        System.out.println("Message sent: " + jsonMsg.toString());
//                        PrintStream out = new PrintStream(mainSocket.getOutputStream());
//                        out.println(jsonMsg.toString());
//                        out.flush();
                        sendMessage(jsonMsg.toString());
                        
                    }
                    }while(signIn != true);
                    
                    
                    while(signIn != false){
                        System.out.println("Ready for incoming message");
                        retreiveMsg  = receiveMessage();
                        System.out.println("Message received: "+ retreiveMsg);
                        checkIn(new JSONObject(retreiveMsg));
                    }
                    System.out.println("Signed Out");
                        socket.close();

                        System.out.println("end!!");
                    
                } catch (SocketTimeoutException  ex) {
                    System.out.println("\nSocket timed out!");
                } catch (IOException ex) {
                    System.out.print(ex.getMessage());
                } catch (Exception ex) {
                    System.out.print(ex.getMessage());
                }
              }
        }).start();
        
    }
    
    private JSONObject staffRequestSend(Staff staff) throws Exception{
//        staff = ServerComm.staffGetInfo(staff.id);
        
//        JSONObject json = JsonConvert.staffInfoToJson(signIn, staff);
        JSONObject json = staff.toJson(true);
//        JSONArray papers = JsonConvert.papersToJson(ServerComm.getPapers(staff.getVenue()));
        JSONArray papers = papersToJson(getPapers(staff.getVenue()));
//        JSONArray attdList = JsonConvert.attdListToJson(ServerComm.getAttdList(staff.getVenue()));
        JSONArray attdList = cddListToJson(getCddList(staff.getVenue()));
        
        json.put("CddList", attdList);
        json.put("PaperMap", papers);
        System.out.println(json.toString());
//        return JsonConvert.jsonStringConcatenate(staffInfo, papers, attdList);
        return json;
    }
    
    private String receiveMessage() throws IOException{
        System.out.println(socket.getLocalPort());
        InputStreamReader ir = new InputStreamReader(socket.getInputStream());
        BufferedReader br = new BufferedReader(ir);

        return br.readLine();
    }
    
    private void sendMessage(String message) throws IOException{
        System.out.println("Message sent: " + message);
        PrintStream out = new PrintStream(socket.getOutputStream());
        out.println(message);
        out.flush();
    }
    
    public void checkIn(JSONObject json) throws IOException, JSONException{ 
        JSONObject jsonObject = new JSONObject();
            System.out.println(json.toString());
            String checkIn;
            try {
                checkIn = json.getString("CheckIn");
            
            
            switch(checkIn){
                case "Collection" : break;
                    
                case "CddPapers" : 
                    sendMessage(prepareCddPapers(json.getString("Value")).toString());
                    break;
                    
                case "AttdList" : 
                        downloadCddData(json.getJSONArray("CddList"));
                        sendMessage(booleanToJson(true).toString());
                    break;
                    
                default: break;
            }
            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            sendMessage(booleanToJson(false).toString());
        }
        
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
    
    public ArrayList<Paper> getCddPapers(String candidateID) throws SQLException{
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
    
    public JSONObject prepareCddPapers(String candidateID) throws SQLException, JSONException{
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
}
