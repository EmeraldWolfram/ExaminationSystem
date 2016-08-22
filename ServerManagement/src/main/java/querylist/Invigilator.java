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
