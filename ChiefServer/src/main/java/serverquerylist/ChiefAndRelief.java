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
public class ChiefAndRelief {
    Integer cr_id;
    Integer si_id;
    String block;
    Integer session_id;
    String status = "";
    String attendance = "";
    String signInTime;
    
    public ChiefAndRelief(){}
    
    public ChiefAndRelief(  Integer cr_id,
                            Integer si_id,
                            String block,
                            Integer session_id,
                            String status,
                            String attendance,
                            String signInTime
                            ){
        this.cr_id = cr_id;
        this.si_id = si_id;
        this.block = block;
        this.session_id = session_id;
        this.status = status;
        this.attendance = attendance;
        this.signInTime = signInTime;
        
    }
    
    public Integer getCr_id(){
        return this.cr_id;
    }
    
    public Integer getSi_id(){
        return this.si_id;
    }
    
    public Integer getSession_id(){
        return this.session_id;
    }
    
    public String getBlock(){
        return this.block;
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
    
}
