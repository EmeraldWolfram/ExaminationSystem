/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

/**
 *
 * @author Krissy
 */
public class Staff {
    
    String id;
    String status;
    String attendance;
    String block;
    String venue;
    String session;
    String date;
    
    public Staff(){
    }
    
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
}
