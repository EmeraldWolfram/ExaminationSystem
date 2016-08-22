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
public class SessionAndDate {
    
    Integer session_id;
    String session;
    String date;
    
    public SessionAndDate(){}
    
    public SessionAndDate(  Integer session_id,
                            String session,
                            String date
                            ){
        this.session_id = session_id;
        this.session = session;
        this.date = date;
    }
    
    public Integer getSession_id(){
        return this.session_id;
    }
    
    public String getSession(){
        return this.session;
    }
    
    public String getDate(){
        return this.date;
    }
    
}
