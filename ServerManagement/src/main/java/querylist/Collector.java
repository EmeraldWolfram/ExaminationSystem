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
public class Collector {
    
    public final static String TABLE = "Collector";
    public final static String ID = "Collector_id";
    public final static String PAPER_ID = "Paper_id";
    public final static String STAFF_ID = "StaffID";
    
    Integer collector_id;
    Integer paper_id;
    String staffId;
    
    public Collector(){}
    
    public Collector(   Integer collector_id,
                        Integer paper_id,
                        String staffId
                        ){
        this.collector_id = collector_id;
        this.paper_id = paper_id;
        this.staffId = staffId;
    }
    
    public Integer getCollector_id(){
        return this.collector_id;
    }
    
    public Integer getPaper_id(){
        return this.paper_id;
    }
    
    public String getStaffId(){
        return this.staffId;
    }
    
    
}
