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
public class CandidateInfo {
    Integer ci_id;
    String ic;
    String name;
    String regNum;
    Integer programme_id;
    String examId;
    
    public CandidateInfo(){}
    
    public CandidateInfo(   Integer ci_id,
                            String ic,
                            String name,
                            String regNum,
                            Integer programme_id,
                            String examId
                            ){
        this.ci_id = ci_id;
        this.ic = ic;
        this.name = name;
        this.regNum = regNum;
        this.programme_id = programme_id;
        this.examId = examId;
        
    }
    
    public Integer getCi_id(){
        return this.ci_id;
    }
    
    public String getIc(){
        return this.ic;
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getRegNum(){
        return this.regNum;
    }
    
    public Integer getProgramme_id(){
        return this.programme_id;
    }
    
    public String getExamId(){
        return this.examId;
    }
}
