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
public class CandidateAttendance {
    Integer ca_id;
    String ic;
    Integer paper_id;
    String status;
    String attendance;
    Integer tableNo;
    
    public CandidateAttendance( Integer ca_id,
                                String ic,
                                Integer paper_id,
                                String status,
                                String attendance,
                                Integer tableNo){
        this.ca_id = ca_id;
        this.ic = ic;
        this.paper_id = paper_id;
        this.status = status;
        this.attendance = attendance;
        this.tableNo = tableNo;
    }
    
    public Integer getCa_id(){
        return this.ca_id;
    }
    
    public String getIc(){
        return this.ic;
    }
    
    public Integer getPaper_id(){
        return this.paper_id;
    }
    
    public String getStatus(){
        return this.status;
    }
    
    public String getAttendance(){
        return this.attendance;
    }
    
    public Integer getTableNo(){
        return this.tableNo;
    }
    
}
