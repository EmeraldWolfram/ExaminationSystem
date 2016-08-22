/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package querylist;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Krissy
 */
public class CddPaperList {
    
    boolean validRegNum;
    String regNum;
    ArrayList<Paper> papers = new ArrayList<>();
    
    public CddPaperList(){
        
    }
    
    public CddPaperList(String regNum){
        this.regNum = regNum;
    }
    
    public ArrayList<Paper> getCddPaperList(){
        return this.papers;
    }
    
    public boolean checkRegNum(){
        return this.validRegNum;
    }
    
    public JSONObject toJson() throws JSONException{
        JSONObject json = new JSONObject();
        JSONArray jsonArr = new JSONArray(this.papers);
        
        if(checkRegNum()){
            json.put("Result", true);
            json.put("PaperList", jsonArr);
        }else
            json.put("Result", false);

        return json;
    }
    
}
