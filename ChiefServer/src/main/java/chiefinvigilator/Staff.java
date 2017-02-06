/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

import errormessage.ErrorMessage;
import globalvariable.CheckInType;
import globalvariable.InfoType;
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
import querylist.Paper;

/**
 *
 * @author Krissy
 */
public class Staff {
    
    String id = null;
    String name = null;
    String status = null;
    String attendance = null;
    String block = null;
    String venue = null;
    String session = null;
    String date = null;
    String password = null;
    
    public Staff(String id) throws Exception{
        this.id = id;
        setInvInfo(id);
    }
    
    public Staff(String id, String password){
        this.id = id;
        this.password = password;
        
    }

    public Staff() {}
    
    public void setID(String id){
        this.id = id;
    }
    
    public String getID(){
        return this.id;
    }
    
    public void setStatus(String status){
        this.status = status;
    }
    
    public String getStatus(){
        return this.status;
    }
    
    public void setAttendance(String attendance){
        this.attendance = attendance;
    }
    
    public String getAttendance(){
        return this.attendance;
    }
    
    public void setBlock(String block){
        this.block = block;
    }
    
    public String getBlock(){
        return this.block;
    }
    
    public void setVenue(String venue){
        this.venue = venue;
    }
    
    public String getVenue(){
        return this.venue;
    }
    
    public void setSession(String session){
        this.session = session;
    }
    
    public String getSession(){
        return this.session;
    }
    
    public void setDate(String date){
        this.date = date;
    }
    
    public String getDate(){
        return this.date;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    
    public String getPassword(){
        return password;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return name;
    }


    /**
     * @brief To convert staff info to Json object
     * @param valid
     * @param staff
     * @return
     * @throws JSONException 
     */
    public JSONObject toJsonMsg(boolean valid, Integer deviceId, String type) throws Exception{
        JSONObject json = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
       
            arr.put(getStatus());
            
            
            json.put("Result", valid);
            
            if(valid){
                json.put("Name", getName());
                json.put("Venue", getVenue());
                json.put("IdNo", getID());
                json.put("Role", getStatus());
                json.put(InfoType.DEVICE_ID, deviceId);
                json.put(InfoType.TYPE, type);
//                json.put("CddList", attdListToJson(ServerComm.getAttdList(getVenue())));
//                json.put("PaperMap", attdListToJson(ServerComm.getAttdList(getVenue())));
            }
            
        } catch (JSONException ex) {
            throw new Exception("Error: Staff.toJson fail !!" + ex.getMessage());
        } catch (Exception ex) {
            throw new Exception("Error: Database error !!" + ex.getMessage());
        }
        
        return json;
    }
    
    
    
    /**
     * @brief   To get the staff info depends on the current time
     * @param   id            id of the staff
     * @return  staff       contains info of the staff
     * @throws SQLException 
     */
    public void setInvInfo(String id) throws Exception{
        
        Connection conn = new ConnectDB().connect();
          String sql = "SELECT Venue.Name AS VenueName, InvigilatorAndAssistant.StaffID AS Staff_id, "
                + "StaffInfo.Name AS StaffName "
                + ",* FROM InvigilatorAndAssistant "
                + "LEFT OUTER JOIN Venue ON Venue.Venue_id = InvigilatorAndAssistant.Venue_id "
                + "LEFT OUTER JOIN StaffInfo ON StaffInfo.StaffID = InvigilatorAndAssistant.StaffID "
                + "WHERE Staff_id = ? ";
          
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);

            ResultSet result = ps.executeQuery();
            
            if(result.isBeforeFirst())
                while(result.next()){
                    setName(result.getString("StaffName"));
                    setStatus(result.getString("Status"));
                    setVenue(result.getString("VenueName"));
                    setBlock(result.getString("Block"));

                }
            else
                throw new Exception(ErrorMessage.INV_NOT_FOUND);
        
        result.close();
        ps.close();
        conn.close();
        
    }
    
    /**
     * 
     * @param venue     contains the name of venue
     * @return attendance list of the venue 
     */
    public ArrayList<Candidate> getCddList(String venue) throws SQLException, Exception{
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
                + "LEFT OUTER JOIN CandidateInfo ON CandidateInfo.CI_id = CandidateAttendance.CI_id "
                + "LEFT OUTER JOIN Programme ON Programme.Programme_id = CandidateInfo.Programme_id "
                + "WHERE VenueName = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        
        ps.setString(1, venue);
//        ps.setString(2, time.getDate());
//        ps.setString(3, time.getSession());
   
        ResultSet result = ps.executeQuery();
        
        if(result.isBeforeFirst()){
            while ( result.next() ){
                if(result.getString("RegNum") != null)
                cddList.add(new Candidate(result.getString("ExamID"), result.getString("RegNum"),
                                           result.getString("CandidateStatus"), result.getString("PaperCode"),
                                           result.getString("ProgrammeName"), result.getString("CandAttd")));
            }
        }
        else
            throw new Exception(ErrorMessage.CDDLIST_NOT_FOUND);
        
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
    public ArrayList<Paper> getPapers(String venue) throws SQLException, Exception{
        ArrayList<Paper> paperList = new ArrayList<>();
        AttdList attd;
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT Venue.Name AS VenueName "
                + ",* FROM Venue "
                + "LEFT OUTER JOIN Paper ON Paper.Venue_id = Venue.Venue_id "
                + "LEFT OUTER JOIN PaperInfo ON PaperInfo.PI_id = Paper.PI_id "
                + "WHERE VenueName = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, venue);
//        ps.setString(2, time.getDate());
//        ps.setString(3, time.getSession());
   
        ResultSet result = ps.executeQuery();
        
        if(result.isBeforeFirst()){
            while ( result.next() ){
                paperList.add(new Paper(result.getString("PaperCode"), result.getString("PaperDescription"),
                                    result.getString("PaperStartNo"), result.getString("TotalCandidate")));

            }
        }
        else
            throw new Exception(ErrorMessage.PAPERS_NOT_FOUND);
        
        result.close();
        ps.close();
        conn.close();

        return paperList;
    }
    
    public JSONObject prepareInvExamList(String venue, Integer deviceId){
        JSONObject json = new JSONObject();
        JSONArray papers;
        
        try {
            papers = papersToJson(getPapers(venue));
            JSONArray attdList = cddListToJson(getCddList(venue));
            
            json.put(InfoType.ATTENDANCE_LIST, attdList);
            json.put(InfoType.PAPER_LIST, papers);
            json.put(InfoType.RESULT, true);
            json.put(InfoType.TYPE, CheckInType.EXAM_INFO_LIST);
            json.put(InfoType.DEVICE_ID, deviceId);
            
        } catch (Exception ex) {
            json.put(InfoType.RESULT, false);
            json.put(InfoType.TYPE, CheckInType.EXAM_INFO_LIST);
            
        }
 
        return json;
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
            throw new Exception("Data not Found!!");
                    
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
            attd.put("PaperCode", attdList.get(i).getPaperCode());
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
   
   /**
    * @brief    To verify whether the given staffId hav a match with a bundleId in database
    * @param staffId
    * @param bundleId
    * @return
    * @throws SQLException 
    */
    public Boolean verifyForCollector(String staffId, String bundleId) throws SQLException{
        Boolean valid = false;
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT Collector_id FROM Collector "
                + " WHERE StaffID = ? AND Paper_id = (SELECT Paper_id FROM Paper Where BundleID = ?) ";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, staffId);
        ps.setString(2, bundleId);
        
        ResultSet result = ps.executeQuery();
        
        valid = result.next();
        
        result.close();
        ps.close();
        conn.close();
        
        return valid;
    }
    
    /**
     * @brief   To get the role of staff in current session and venue
     * @param staffId
     * @return
     * @throws SQLException 
     */
    public String getStaffRole(String staffId) throws SQLException{
        String role = null;
        Connection conn = new ConnectDB().connect();
        String sql = "SELECT Status FROM InvigilatorAndAssistant "
                + " WHERE StaffID = ?";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, staffId);
        
        ResultSet result = ps.executeQuery();
        
        if(result.next()){
            role = result.getString("Status");
        }
        
        result.close();
        ps.close();
        conn.close();
        
        return role;
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
    
    private void downloadCddDataFromJson(JSONArray jArr) throws JSONException, Exception{
        ArrayList<Candidate> cddList = new ArrayList<>();
        
        for(int i = 0; i < jArr.length(); i ++){
            Candidate candidate = new Candidate();
            candidate.fromJson(jArr.getJSONObject(i).toString());
            cddList.add(candidate);
        }
        updateCandidateAttendence(cddList);
        
    }
}
