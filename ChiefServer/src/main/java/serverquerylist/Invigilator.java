/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverquerylist;

/**
 *
 * @author Krissy
 */
public class Invigilator {
    private Integer ia_id;
    private String staffId;
    private String status;
    private String attendance;
    private String signInTime;
    private Integer venue_id;
    private Integer session_id;
    
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

    /**
     * @param ia_id the ia_id to set
     */
    public void setIa_id(Integer ia_id) {
        this.ia_id = ia_id;
    }

    /**
     * @param staffId the staffId to set
     */
    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @param attendance the attendance to set
     */
    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    /**
     * @param signInTime the signInTime to set
     */
    public void setSignInTime(String signInTime) {
        this.signInTime = signInTime;
    }

    /**
     * @param venue_id the venue_id to set
     */
    public void setVenue_id(Integer venue_id) {
        this.venue_id = venue_id;
    }

    /**
     * @param session_id the session_id to set
     */
    public void setSession_id(Integer session_id) {
        this.session_id = session_id;
    }
    
    
}
