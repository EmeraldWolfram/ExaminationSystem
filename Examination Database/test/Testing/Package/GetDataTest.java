/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing.Package;

import examdatabase.GetData;
import java.util.ArrayList;
import java.util.Iterator;
import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 * @author Krissy
 */
public class GetDataTest {
    
    @Test
    public void testGetData(){
        
        GetData getData = new GetData("IC","824456992345");
//       getData.selectAllFromCandidateInfo();
//        System.out.print("lol");
        
    }
    
    @Test
    public void testGetDataViaIC(){
        GetData getData = new GetData("829911092234","","","","");
        
        ArrayList<GetData> list = getData.getDataFromTable();

        assertEquals(list.get(0).ic, "829911092234");
        assertEquals(list.get(0).name, "Adlas");
        assertEquals(list.get(0).regNum, "16WAR25342");
        assertEquals(list.get(0).status, "Legal");
        assertEquals(list.get(0).attendance, "Absent");
    }
    
    @Test
    public void testGetDataViaName(){
        GetData getData = new GetData("","Liu","","","");
        
        ArrayList<GetData> list = getData.getDataFromTable();
        
        assertEquals(list.get(0).ic, "941014126347");
        assertEquals(list.get(0).name, "Liu");
        assertEquals(list.get(0).regNum, "15WAR09183");
        assertEquals(list.get(0).status, "Legal");
        assertEquals(list.get(0).attendance, "Absent");

    }
    
    @Test
    public void testGetDataViaStudentID(){
        GetData getData = new GetData("","","15WAD23345","","");
        
        ArrayList<GetData> list = getData.getDataFromTable();
        
        assertEquals(list.get(0).ic, "930529126289");
        assertEquals(list.get(0).name, "Oslo");
        assertEquals(list.get(0).regNum, "15WAD23345");
        assertEquals(list.get(0).status, "Barred");
        assertEquals(list.get(0).attendance, "Absent");

    }
    
    @Test
    public void testGetDataViaNameAndIC(){
        GetData getData = new GetData("930529126289","Oslo","","","");
        
        ArrayList<GetData> list = getData.getDataFromTable();
        
        assertEquals(list.get(0).ic, "930529126289");
        assertEquals(list.get(0).name, "Oslo");
        assertEquals(list.get(0).regNum, "15WAD23345");
        assertEquals(list.get(0).status, "Barred");
        assertEquals(list.get(0).attendance, "Absent");

    }
}
