/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package querylist;

import java.util.ArrayList;

/**
 *
 * @author Krissy
 */
public class ExamDataList {
    private ArrayList<CandidateAttendance> cddAttd = new ArrayList<>();
    private ArrayList<CandidateInfo> cddInfo = new ArrayList<>();
    private ArrayList<ChiefAndRelief> chAndRe = new ArrayList<>();
    private ArrayList<Collector> collector = new ArrayList<>();
    private ArrayList<Invigilator> inv = new ArrayList<>();
    private ArrayList<Paper> paper = new ArrayList<>();
    private ArrayList<PaperInfo> paperInfo = new ArrayList<>();
    private ArrayList<Programme> programme = new ArrayList<>();
    private ArrayList<StaffInfo> staffInfo = new ArrayList<>();
    private ArrayList<Venue> venue = new ArrayList<>();
    
    public ExamDataList(){}
    
    public ExamDataList(ArrayList<CandidateAttendance> cddAttd,
                        ArrayList<CandidateInfo> cddInfo,
                        ArrayList<ChiefAndRelief> chAndRe,
                        ArrayList<Collector> collector,
                        ArrayList<Invigilator> inv,
                        ArrayList<Paper> paper,
                        ArrayList<PaperInfo> paperInfo,
                        ArrayList<Programme> programme,
                        ArrayList<StaffInfo> staffInfo,
                        ArrayList<Venue> venue
                        ){
        
        this.cddAttd = cddAttd;
        this.cddInfo = cddInfo;
        this.chAndRe = chAndRe;
        this.collector = collector;
        this.inv = inv;
        this.paper = paper;
        this.paperInfo = paperInfo;
        this.programme = programme;
        this.staffInfo = staffInfo;
        this.venue = venue;
        
    };

    /**
     * @return the cddAttd
     */
    public ArrayList<CandidateAttendance> getCddAttd() {
        return cddAttd;
    }

    /**
     * @return the cddInfo
     */
    public ArrayList<CandidateInfo> getCddInfo() {
        return cddInfo;
    }

    /**
     * @return the chAndRe
     */
    public ArrayList<ChiefAndRelief> getChAndRe() {
        return chAndRe;
    }
    
    /**
     * @return the chAndRe
     */
    public ArrayList<Collector> getCollector() {
        return collector;
    }

    /**
     * @return the inv
     */
    public ArrayList<Invigilator> getInv() {
        return inv;
    }

    /**
     * @return the paper
     */
    public ArrayList<Paper> getPaper() {
        return paper;
    }

    /**
     * @return the paperInfo
     */
    public ArrayList<PaperInfo> getPaperInfo() {
        return paperInfo;
    }

    /**
     * @return the programme
     */
    public ArrayList<Programme> getProgramme() {
        return programme;
    }

    /**
     * @return the staffInfo
     */
    public ArrayList<StaffInfo> getStaffInfo() {
        return staffInfo;
    }

    /**
     * @return the venue
     */
    public ArrayList<Venue> getVenue() {
        return venue;
    }

    /**
     * @param cddAttd the cddAttd to set
     */
    public void setCddAttd(ArrayList<CandidateAttendance> cddAttd) {
        this.cddAttd = cddAttd;
    }

    /**
     * @param cddInfo the cddInfo to set
     */
    public void setCddInfo(ArrayList<CandidateInfo> cddInfo) {
        this.cddInfo = cddInfo;
    }

    /**
     * @param chAndRe the chAndRe to set
     */
    public void setChAndRe(ArrayList<ChiefAndRelief> chAndRe) {
        this.chAndRe = chAndRe;
    }

    /**
     * @param inv the inv to set
     */
    public void setInv(ArrayList<Invigilator> inv) {
        this.inv = inv;
    }

    /**
     * @param paper the paper to set
     */
    public void setPaper(ArrayList<Paper> paper) {
        this.paper = paper;
    }

    /**
     * @param paperInfo the paperInfo to set
     */
    public void setPaperInfo(ArrayList<PaperInfo> paperInfo) {
        this.paperInfo = paperInfo;
    }

    /**
     * @param programme the programme to set
     */
    public void setProgramme(ArrayList<Programme> programme) {
        this.programme = programme;
    }

    /**
     * @param staffInfo the staffInfo to set
     */
    public void setStaffInfo(ArrayList<StaffInfo> staffInfo) {
        this.staffInfo = staffInfo;
    }

    /**
     * @param venue the venue to set
     */
    public void setVenue(ArrayList<Venue> venue) {
        this.venue = venue;
    }
}
