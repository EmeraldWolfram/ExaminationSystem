/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Krissy
 */
public class CurrentTime {
    
    public CurrentTime(){
        
    }
    
    public String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }
    
    public String getTime(){
        DateFormat timeFormat = new SimpleDateFormat("HHmmss");
        Date time = new Date();
        return timeFormat.format(time);
    }
    
//    public String getDate();
//    public String getTime();
}
