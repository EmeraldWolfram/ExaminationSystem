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
    
    public final static String TABLE = "Paper";
    public final static String ID = "Paper_id";
    public final static String PI_ID = "PI_id";
    public final static String VENUE_ID = "Venue_id";
    public final static String PAPER_START_NO = "PaperStartNo";
    public final static String TOTAL_CANDIDATE = "TotalCandidate";
    public final static String SESSION_ID = "Session_id";
    public final static String PROGRAMME_ID = "Programme_id";
    public final static String BUNDLE_ID = "BundleID";
    public final static String COLLECTOR = "Collector";

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
    
    public class TableCol{
        
        public final static String PAPER_ID = "Paper.Paper_id";
        public final static String PI_ID = "Paper.PI_id";
        public final static String VENUE_ID = "Paper.Venue_id";
        public final static String PAPER_START_NO = "Paper.PaperStartNo";
        public final static String TOTAL_CANDIDATE = "Paper.TotalCandidate";
        public final static String SESSION_ID = "Paper.Session_id";
        public final static String PROGRAMME_ID = "Paper.Programme_id";
    }
    
    private Integer paper_id;
    private Integer pi_id;
    private Integer venue_id;
    private Integer paperStartNo;
    private Integer totalCandidate;
    private Integer session_id;
    private Integer programme_id;
    private String bundleId;
    private String collector;
    
    public Paper(){}
    
    public Paper(  Integer paper_id,
            Integer pi_id,
            Integer venue_id,
            Integer paperStartNo,
            Integer totalCandidate,
            Integer session_id,
            Integer programme_id,
            String bundleId,
            String collector
            ){
        this.paper_id = paper_id;
        this.pi_id = pi_id;
        this.venue_id = venue_id;
        this.paperStartNo = paperStartNo;
        this.totalCandidate = totalCandidate;
        this.session_id = session_id;
        this.programme_id = programme_id;
        this.bundleId = bundleId;
        this.collector = collector;
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
    
    public String getBundleId(){
        return this.bundleId;
    }
    
    public String getCollector(){
        return this.collector;
    }
    
}
