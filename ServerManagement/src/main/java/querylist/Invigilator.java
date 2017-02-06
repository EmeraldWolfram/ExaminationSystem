/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package querylist;

/**
 *
 * @author Krissy
 */
public class Invigilator {
    
    public final static String TABLE = "InvigilatorAndAssistant";
    public final static String ID = "IA_id";
    public final static String STAFFID = "StaffID";
    public final static String STATUS = "Status";
    public final static String ATTENDANCE = "Attendance";
    public final static String SIGNINTIME = "SignInTime";
    public final static String VENUE_ID = "Venue_id";
    public final static String SESSION_ID = "Session_id";
    
    public class TableCol{
    
        public final static String ID = "InvigilatorAndAssistant.IA_id";
        public final static String STAFFID = "InvigilatorAndAssistant.StaffID";
        public final static String STATUS = "InvigilatorAndAssistant.Status";
        public final static String ATTENDANCE = "InvigilatorAndAssistant.Attendance";
        public final static String SIGNINTIME = "InvigilatorAndAssistant.SignInTime";
        public final static String VENUE_ID = "InvigilatorAndAssistant.Venue_id";
        public final static String SESSION_ID = "InvigilatorAndAssistant.Session_id";
    }
    
    Integer ia_id;
    String staffId;
    String status;
    String attendance;
    String signInTime;
    Integer venue_id;
    Integer session_id;
    
    public Invigilator(){}
    
    public Invigilator( Integer ia_id,
                        String staffId,
                        String status,
                        String attendance,
                        String signInTime,
                        Integer venue_id,
                        Integer session_id
                        ){
        this.ia_id = ia_id;
        this.staffId = staffId;
        this.status = status;
        this.attendance = attendance;
        this.signInTime = signInTime;
        this.venue_id = venue_id;
        this.session_id = session_id;
        
    }
    
    public Integer getIa_id(){
        return this.ia_id;
    }
    
    public String getstaffId(){
        return this.staffId;
    }
    
    public String getStatus(){
        return this.status;
    }
    
    public String getAttendance(){
        return this.attendance;
    }
    
    public String getSignInTime(){
        return this.signInTime;
    }
    
    public Integer getVenue_id(){
        return this.venue_id;
    }
    
    public Integer getSession_id(){
        return this.session_id;
    }
    
    
}
