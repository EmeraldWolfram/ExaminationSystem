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
        public final static String TABLE = "PaperInfo";
        public final static String ID = "PI_id";
        public final static String PAPER_CODE = "PaperCode";
        public final static String PAPER_DESCRIPTION = "PaperDescription";
        public final static String EXAM_WEIGHT = "ExamWeight";
        public final static String COURSEWORK_WEIGHT = "CourseworkWeight";
        
        
        public class TableCol{
            public final static String ID = "PaperInfo.PI_id";
            public final static String PAPER_CODE = "PaperInfo.PaperCode";
            public final static String PAPER_DESCRIPTION = "PaperInfo.PaperDescription";
            public final static String EXAM_WEIGHT = "PaperInfo.ExamWeight";
            public final static String COURSEWORK_WEIGHT = "PaperInfo.CourseworkWeight";
        }
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
