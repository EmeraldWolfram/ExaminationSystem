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
    
    public String getSession(){
        int time = Integer.parseInt(getTime());
        if ((time >= 80000)&&(time <= 110000))
            return "AM";
        else if ((time >= 130000)&&(time <= 160000))
            return "PM";
        else 
            return "NULL";
    }
    
}
