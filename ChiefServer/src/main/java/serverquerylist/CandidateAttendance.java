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
     public final static String TABLE = "CandidateAttendance";
        public final static String ID = "CA_id";
        public final static String CI_ID = "CI_id";
        public final static String PAPER_ID = "Paper_id";
        public final static String STATUS = "Status";
        public final static String ATTENDANCE = "Attendance";
        public final static String TABLE_NUMBER = "TableNumber";
        
        public final static String ELIGIBLE = "ELIGIBLE";
        public final static String BARRED = "BARRED";
        public final static String EXEMPTED = "EXEMPTED";
        
        public final static String ABSENT = "ABSENT";
        public final static String PRESENT = "PRESENT";
        
    private Integer ca_id;
    private Integer ci_id;
    private Integer paper_id;
    private String status;
    private String attendance;
    private Integer tableNo;
    
    public CandidateAttendance(){}
    
    public CandidateAttendance( Integer ca_id,
                                Integer ci_id,
                                Integer paper_id,
                                String status,
                                String attendance,
                                Integer tableNo){
        this.ca_id = ca_id;
        this.ci_id = ci_id;
        this.paper_id = paper_id;
        this.status = status;
        this.attendance = attendance;
        this.tableNo = tableNo;
    }
    
    public Integer getCa_id(){
        return this.ca_id;
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

    /**
     * @return the ci_id
     */
    public Integer getCi_id() {
        return ci_id;
    }

    /**
     * @param ci_id the ci_id to set
     */
    public void setCi_id(Integer ci_id) {
        this.ci_id = ci_id;
    }
    
}
