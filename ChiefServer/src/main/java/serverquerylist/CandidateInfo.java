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
public class CandidateInfo {
    private Integer ci_id;
    private String ic;
    private String name;
    private String regNum;
    private Integer programme_id;
    private String examId;
    
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

    /**
     * @param ci_id the ci_id to set
     */
    public void setCi_id(Integer ci_id) {
        this.ci_id = ci_id;
    }

    /**
     * @param ic the ic to set
     */
    public void setIc(String ic) {
        this.ic = ic;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param regNum the regNum to set
     */
    public void setRegNum(String regNum) {
        this.regNum = regNum;
    }

    /**
     * @param programme_id the programme_id to set
     */
    public void setProgramme_id(Integer programme_id) {
        this.programme_id = programme_id;
    }

    /**
     * @param examId the examId to set
     */
    public void setExamId(String examId) {
        this.examId = examId;
    }
}
