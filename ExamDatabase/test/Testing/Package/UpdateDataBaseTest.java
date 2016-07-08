/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing.Package;

import examdatabase.ConnectDB;
import examdatabase.RebuildDataBase;
import examdatabase.CustomException;
import examdatabase.GetData;
import examdatabase.UpdateMark;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.*;
import static org.junit.Assert.*;
/**
 *
 * @author Krissy
 */
public class UpdateDataBaseTest {
    
    private static GetData data1;
    private static GetData data2;
    private static GetData data3;
    
    @Before
    public void oneTimeSetUp(){
        new ConnectDB().setConnection("FEB_MAR", "2016_for_test");
        new RebuildDataBase();
        
        GetData getData = new GetData("","","","","","","","","","","","","","");
        ArrayList<GetData> list = null;
        
        try {
            list = getData.getDataCheckMark();
        } catch (CustomException ex) {
            System.out.printf("No data");
        }
        
        data1 = list.get(0);
        data2 = list.get(1);
        data3 = list.get(2);
//        System.out.print(list.get(0).ic);
        
    }
    
    @Test
    public void testdata1MarkUpdate(){
        int practicalMark = data1.practical;
        assertEquals(0,practicalMark);
        int courseworkMark = data1.coursework;
        assertEquals(0,courseworkMark);
        
        new UpdateMark(data1.regNum,data1.paperCode,70,20).setMark();
        
        ArrayList<GetData> updatedlist = null;
        
        try {
            updatedlist = new GetData("","",data1.regNum,"","","","","","","",data1.paperCode,"","","").getDataCheckMark();
        } catch (CustomException ex) {
            System.out.print("No data");
        }
        courseworkMark = updatedlist.get(0).coursework;
        assertEquals(20,courseworkMark);
        practicalMark = updatedlist.get(0).practical;
        assertEquals(70,practicalMark);

    }
    
    @Test
    public void testdata2MarkUpdate(){
        int practicalMark = data2.practical;
        assertEquals(45,practicalMark);
        int courseworkMark = data2.coursework;
        assertEquals(12,courseworkMark);
        
        new UpdateMark(data2.regNum,data2.paperCode,100,10).setMark();
        
        ArrayList<GetData> updatedlist = null;
        
        try {
            updatedlist = new GetData("","",data2.regNum,"","","","","","","",data2.paperCode,"","","").getDataCheckMark();
        } catch (CustomException ex) {
            System.out.print("No data");
        }
        courseworkMark = updatedlist.get(0).coursework;
        assertEquals(10,courseworkMark);
        practicalMark = updatedlist.get(0).practical;
        assertEquals(100,practicalMark);

    }
    
    @Test
    public void testdata3MarkUpdate(){
        int practicalMark = data3.practical;
        assertEquals(0,practicalMark);
        int courseworkMark = data3.coursework;
        assertEquals(0,courseworkMark);
        
        System.out.print(data3.regNum);
        new UpdateMark(data3.regNum,data3.paperCode,100,10).setMark();
        
        ArrayList<GetData> updatedlist = null;
        
        try {
            updatedlist = new GetData("","",data3.regNum,"","","","","","","",data3.paperCode,"","","").getDataCheckMark();
        } catch (CustomException ex) {
            System.out.print("No data");
        }
        courseworkMark = updatedlist.get(0).coursework;
        assertEquals(10,courseworkMark);
        practicalMark = updatedlist.get(0).practical;
        assertEquals(100,practicalMark);

    }
}
