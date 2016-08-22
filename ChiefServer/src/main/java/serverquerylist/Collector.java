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
public class Collector {
    Integer paper_id;
    String staffId;
    
    public Collector(){}
    
    public Collector(   Integer paper_id,
                        String staffId
                        ){
        this.paper_id = paper_id;
        this.staffId = staffId;
        
    }
    
    public Integer getPaper(){
        return this.paper_id;
    }
    
    public String getStaffId(){
        return this.staffId;
    }
}
