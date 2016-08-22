/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package querylist;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    
    public JSONObject papersToJson() throws JSONException{
        JSONObject json = new JSONObject();
        
 
            json = new JSONObject();
            
            json.put("PaperCode", getPaperCode());
            json.put("PaperDesc", getPaperDesc());
            json.put("PaperStartNo", Integer.parseInt(getPaperStartNo()));
            json.put("PaperTotalCdd", Integer.parseInt(getTotalCandidate()));
 
        return json;
    }
}
