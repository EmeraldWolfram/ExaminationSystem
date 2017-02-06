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
public class StaffInfo {
    
    public final static String TABLE = "StaffInfo";
    public final static String SI_ID = "Si_id";
    public final static String STAFFID = "StaffID";
    public final static String NAME = "Name";
    
    Integer si_id;
    String staffId;
    String name;
    
    public StaffInfo(){}
    
    public StaffInfo(   Integer si_id,
                        String staffId,
                        String name
                        ){
        this.si_id = si_id;
        this.staffId = staffId;
        this.name = name;
    }
    
    public Integer getSi_id(){
        return this.si_id;
    }
    
    public String getStaffId(){
        return this.staffId;
    }
    
    public String getName(){
        return this.name;
    }
}
