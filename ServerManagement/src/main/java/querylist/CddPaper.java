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
public class CddPaper {
    
    
    private String paperCode;
    private String paperDesc;
    private String date;
    private String session;
    private String venue;
    
    public CddPaper(    String paperCode,
                        String paperDesc,
                        String date,
                        String session,
                        String venue){
        
        this.paperCode = paperCode;
        this.paperDesc = paperDesc;
        this.date = date;
        this.session = session;
        this.venue = venue;
        
    }

    /**
     * @return the paperCode
     */
    public String getPaperCode() {
        return paperCode;
    }

    /**
     * @param paperCode the paperCode to set
     */
    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode;
    }

    /**
     * @return the paperDesc
     */
    public String getPaperDesc() {
        return paperDesc;
    }

    /**
     * @param paperDesc the paperDesc to set
     */
    public void setPaperDesc(String paperDesc) {
        this.paperDesc = paperDesc;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the session
     */
    public String getSession() {
        return session;
    }

    /**
     * @param session the session to set
     */
    public void setSession(String session) {
        this.session = session;
    }

    /**
     * @return the venue
     */
    public String getVenue() {
        return venue;
    }

    /** nm,l m                                                                   mm                                
     * 
     * @param venue the venue to set
     */
    public void setVenue(String venue) {
        this.venue = venue;
    }
}
