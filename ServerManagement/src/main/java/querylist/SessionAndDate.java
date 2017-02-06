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
    
    public final static String TABLE = "SessionAndDate";
    public final static String ID = "Session_id";
    public final static String SESSION = "Session";
    public final static String DATE = "Date";
    
    public class TableCol{
        
        public final static String ID = "SessionAndDate.Session_id";
        public final static String SESSION = "SessionAndDate.Session";
        public final static String DATE = "SessionAndDate.Date";
        
    }
    
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
