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
            assertEquals(expected.name, current.name);
            assertEquals(expected.ic, current.ic);
            assertEquals(expected.regNum, current.regNum);
            assertEquals(expected.status, current.status);
            assertEquals(expected.attendance, current.attendance);
            assertEquals(expected.tableNum, current.tableNum);
            assertEquals(expected.progName, current.progName);
            assertEquals(expected.faculty, current.faculty);
            assertEquals(expected.date, current.date);
            assertEquals(expected.session, current.session);
            assertEquals(expected.paperCode, current.paperCode);
            assertEquals(expected.paperDesc, current.paperDesc);
            assertEquals(expected.venueName, current.venueName);
            assertEquals(expected.venueSize, current.venueSize);
        }
        else 
            System.out.print("No data in current value!!!");
    }


}
