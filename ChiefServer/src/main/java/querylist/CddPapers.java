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
public class CddPapers {
    
    String paperCode;
    String paperDesc;
    String date;
    String session;
    String venue;
    
    public CddPapers(){
    
    }
    
    public CddPapers(String paperCode, String paperDesc,
                        String date, String session,
                        String venue){
        
        this.paperCode = paperCode;
        this.paperDesc = paperDesc;
        this.date = date;
        this.session = session;
        this.venue = venue;
    }
    
    public String getPaperCode(){
        return paperCode;
    }
    
    public String getPaperDesc(){
        return paperDesc;
    }
    
    public String getDate(){
        return date;
    }
    
    public String getSession(){
        return session;
    }
    
    public String getVenue(){
        return venue;
    }

    public JSONObject cddPapersToJson() throws JSONException{
        JSONObject json = new JSONObject();
            
            json.put("PaperCode", getPaperCode());
            json.put("PaperDesc", getPaperDesc());
            json.put("Date", getDate());
            json.put("Session", getSession());
            json.put("Venue", getVenue());
        
        return json;
    }
}
