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
public class CandidateAttendance {
    private Integer ca_id;
    private String ic;
    private Integer paper_id;
    private String status;
    private String attendance;
    private Integer tableNo;
    
    public CandidateAttendance(){}
    
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

    /**
     * @param ca_id the ca_id to set
     */
    public void setCa_id(Integer ca_id) {
        this.ca_id = ca_id;
    }

    /**
     * @param ic the ic to set
     */
    public void setIc(String ic) {
        this.ic = ic;
    }

    /**
     * @param paper_id the paper_id to set
     */
    public void setPaper_id(Integer paper_id) {
        this.paper_id = paper_id;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @param attendance the attendance to set
     */
    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    /**
     * @param tableNo the tableNo to set
     */
    public void setTableNo(Integer tableNo) {
        this.tableNo = tableNo;
    }
    
}
