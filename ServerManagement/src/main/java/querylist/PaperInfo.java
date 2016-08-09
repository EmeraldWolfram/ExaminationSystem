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
public class PaperInfo {
    Integer pi_id;
    String paperCode;
    String paperDescrip;
    
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
}
