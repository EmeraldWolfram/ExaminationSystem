/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing.Package;
import examdatabase.GetData;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Krissy
 */
public class CustomAssertion {
    
    public CustomAssertion(){
        
    }
    
    public static void assertDataEqual(GetData expected, GetData current){
        
        if(current != null){
            assertEquals(expected.getName(), current.getName());
            assertEquals(expected.getIc(), current.getIc());
            assertEquals(expected.getRegNum(), current.getRegNum());
            assertEquals(expected.getStatus(), current.getStatus());
            assertEquals(expected.getAttendance(), current.getAttendance());
            assertEquals(expected.getTableNum(), current.getTableNum());
            assertEquals(expected.getProgName(), current.getProgName());
            assertEquals(expected.getFaculty(), current.getFaculty());
            assertEquals(expected.getDate(), current.getDate());
            assertEquals(expected.getSession(), current.getSession());
            assertEquals(expected.getPaperCode(), current.getPaperCode());
            assertEquals(expected.getPaperDesc(), current.getPaperDesc());
            assertEquals(expected.getVenueName(), current.getVenueName());
            assertEquals(expected.getVenueSize(), current.getVenueSize());
        }
        else 
            System.out.print("No data in current value!!!");
    }


}
