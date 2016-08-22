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
public class PaperInfo {
    private Integer pi_id;
    private String paperCode;
    private String paperDescrip;
    
    public PaperInfo(){}
    
    public PaperInfo(   Integer pi_id,
                        String paperCode,
                        String paperDescrip
                        ){
        this.pi_id = pi_id;
        this.paperCode = paperCode;
        this.paperDescrip = paperDescrip;
    }
    
    public Integer getPi_id(){
        return this.pi_id;
    }
    
    public String getPaperCode(){
        return this.paperCode;
    }
    
    public String getPaperDescrip(){
        return this.paperDescrip;
    }

    /**
     * @param pi_id the pi_id to set
     */
    public void setPi_id(Integer pi_id) {
        this.pi_id = pi_id;
    }

    /**
     * @param paperCode the paperCode to set
     */
    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode;
    }

    /**
     * @param paperDescrip the paperDescrip to set
     */
    public void setPaperDescrip(String paperDescrip) {
        this.paperDescrip = paperDescrip;
    }
}
