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
public class Paper {
    Integer paper_id;
    Integer pi_id;
    Integer venue_id;
    Integer paperStartNo;
    Integer totalCandidate;
    Integer session_id;
    Integer programme_id;
    
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
    
}
