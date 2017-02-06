/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing.Package;

import examdatabase.UpdateData;
import org.junit.Test;

/**
 *
 * @author Krissy
 */
public class UpdateDataTest {
    
    @Test
    public void testSetVenueAndSessionForPaper(){
        new UpdateData().setVenueAndSessionForPaper(14, 2, 1, 4);
        //Do not open sqlite browser while update the database
    }
}
