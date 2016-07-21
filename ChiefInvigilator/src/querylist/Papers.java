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
public class Papers {
    String paperCode;
    String paperDesc;
    String paperStartNo;
    String totalCandidate;
    
    public Papers(){
        
    }
    
    public Papers(  String paperCode,
                    String paperDesc,
                    String paperStartNo,
                    String totalCandidate){
        this.paperCode = paperCode;
        this.paperDesc = paperDesc;
        this.paperStartNo = paperStartNo;
        this.totalCandidate = totalCandidate;
    }
    
    public String getPaperCode(){
        return paperCode;
    }
    
    public String getPaperDesc(){
        return paperDesc;
    }
    
    public String getPaperStartNo(){
        return paperStartNo;
    }
    
    
    public String getTotalCandidate(){
        return totalCandidate;
    }
    
    
}
