/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queue;

import org.json.JSONObject;

/**
 *
 * @author Krissy
 */
public class ThreadMessage {
    private long threadId;
    private String message;
    
    public ThreadMessage(){}
    
    public ThreadMessage(long threadId, String message){
        this.threadId = threadId;
        this.message = message;
    }

    /**
     * @return the threadId
     */
    public long getThreadId() {
        return threadId;
    }

    /**
     * @param threadId the threadId to set
     */
    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String toJsonString(){
        JSONObject json = new JSONObject(this.message);
        json.put("ThreadId", threadId);
//        json.put("ThreadId", threadId);
//        json.put("Message", new JSONObject(this.message));
        
        return json.toString();
        
    }
    
    public boolean getResultKey(){
        JSONObject json = new JSONObject(this.message);
        return json.getBoolean("Result");
    }
        
}
