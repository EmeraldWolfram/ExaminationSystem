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
public class SessionAndDate {
    
    private Integer session_id;
    private String session;
    private String date;
    
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

    /**
     * @param session_id the session_id to set
     */
    public void setSession_id(Integer session_id) {
        this.session_id = session_id;
    }

    /**
     * @param session the session to set
     */
    public void setSession(String session) {
        this.session = session;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }
    
}
