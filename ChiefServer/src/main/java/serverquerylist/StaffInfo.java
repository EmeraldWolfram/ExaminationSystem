/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverquerylist;

/**
 *
 * @author Krissy
 */
public class StaffInfo {
    
    private Integer si_id;
    private String staffId;
    private String name;
    
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

    /**
     * @param si_id the si_id to set
     */
    public void setSi_id(Integer si_id) {
        this.si_id = si_id;
    }

    /**
     * @param staffId the staffId to set
     */
    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
