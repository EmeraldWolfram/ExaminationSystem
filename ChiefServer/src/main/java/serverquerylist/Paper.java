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
public class Paper {
    private Integer paper_id;
    private Integer pi_id;
    private Integer venue_id;
    private Integer paperStartNo;
    private Integer totalCandidate;
    private Integer session_id;
    private Integer programme_id;
    
    public Paper(){}
    
    public Paper(  Integer paper_id,
            Integer pi_id,
            Integer venue_id,
            Integer paperStartNo,
            Integer totalCandidate,
            Integer session_id,
            Integer programme_id
            ){
        this.paper_id = paper_id;
        this.pi_id = pi_id;
        this.venue_id = venue_id;
        this.paperStartNo = paperStartNo;
        this.totalCandidate = totalCandidate;
        this.session_id = session_id;
        this.programme_id = programme_id;
        
    }
    
    public Integer getPaper_id(){
        return this.paper_id;
    }
    
    public Integer getPi_id(){
        return this.pi_id;
    }
    
    public Integer getVenue_id(){
        return this.venue_id;
    }
    
    public Integer getPaperStartNo(){
        return this.paperStartNo;
    }
    
    public Integer getTotalCandidate(){
        return this.totalCandidate;
    }
    
    public Integer getSession_id(){
        return this.session_id;
    }
    
    public Integer getProgramme_id(){
        return this.programme_id;
    }

    /**
     * @param paper_id the paper_id to set
     */
    public void setPaper_id(Integer paper_id) {
        this.paper_id = paper_id;
    }

    /**
     * @param pi_id the pi_id to set
     */
    public void setPi_id(Integer pi_id) {
        this.pi_id = pi_id;
    }

    /**
     * @param venue_id the venue_id to set
     */
    public void setVenue_id(Integer venue_id) {
        this.venue_id = venue_id;
    }

    /**
     * @param paperStartNo the paperStartNo to set
     */
    public void setPaperStartNo(Integer paperStartNo) {
        this.paperStartNo = paperStartNo;
    }

    /**
     * @param totalCandidate the totalCandidate to set
     */
    public void setTotalCandidate(Integer totalCandidate) {
        this.totalCandidate = totalCandidate;
    }

    /**
     * @param session_id the session_id to set
     */
    public void setSession_id(Integer session_id) {
        this.session_id = session_id;
    }

    /**
     * @param programme_id the programme_id to set
     */
    public void setProgramme_id(Integer programme_id) {
        this.programme_id = programme_id;
    }
    
}
