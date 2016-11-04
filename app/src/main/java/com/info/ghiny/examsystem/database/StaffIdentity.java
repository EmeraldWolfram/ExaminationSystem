package com.info.ghiny.examsystem.database;

import android.util.Base64;

import com.info.ghiny.examsystem.model.IconManager;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by GhinY on 06/05/2016.
 */
public class StaffIdentity {
    public static final String STAFF_DB_ID  = "_id";
    public static final String STAFF_NAME   = "Name";
    public static final String STAFF_PASS   = "Password";
    public static final String STAFF_HPASS  = "HashPass";
    public static final String STAFF_ID_NO  = "IdNo";
    public static final String STAFF_LEGIT  = "Eligible";
    public static final String STAFF_ROLE   = "Status";
    public static final String STAFF_VENUE  = "Venue";

    private String idNo;
    private String password;
    private String hashPass;
    private String name;
    private String venueHandling;
    private ArrayList<String> role;


    public StaffIdentity(){
        this.idNo           = null;
        this.name           = null;
        this.password       = null;
        this.venueHandling  = null;
        this.role           = new ArrayList<>();
    }

    public StaffIdentity(String idNo, boolean elg, String name, String venue){
        this.idNo           = idNo;
        this.name           = name;
        this.password       = null;
        this.venueHandling  = venue;
        this.role           = new ArrayList<>();
    }
    //Setter and getter, TO DO: Remove setIdentity as this is for testing purpose


    public String getExamVenue() {
        return venueHandling;
    }

    public void setExamVenue(String venueHandling) {
        this.venueHandling = venueHandling;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addRole(String role) {
        this.role.add(role);
    }
    public List<String> getRole() {
        return role;
    }

    public void setPassword(String newPassword){
        this.password = newPassword;
    }
    public String getPassword(){ return this.password;}

    public void setIdNo(String newRegNum){    this.idNo = newRegNum;}
    public String getIdNo(){    return this.idNo;}

    public String getHashPass() {
        return hashPass;
    }
    public void setHashPass(String hashPass) {
        this.hashPass = hashPass;
    }

    public boolean matchPassword(String password) throws ProcessException{
        if(this.hashPass == null)
            throw new ProcessException("Password null exception", ProcessException.FATAL_MESSAGE,
                    IconManager.WARNING);

        String newEntry = null;
        if(password != null && TCPClient.getConnector().getDuelMessage() != null)
             newEntry   = hmacSha(password, TCPClient.getConnector().getDuelMessage());

        //return this.password.equals(password);
        return this.hashPass.equals(newEntry);
    }

    public String hmacSha(String password, String duelMessage) throws ProcessException{
        String hash;
        Mac shaHMAC;

        try {
            shaHMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(password.getBytes(), "HmacSHA256");
            shaHMAC.init(secretKey);
            hash = Base64.encodeToString(shaHMAC.doFinal(duelMessage.getBytes()), Base64.DEFAULT);
        } catch (Exception err) {
            throw new ProcessException("Encryption library not found\nPlease contact developer!",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }

        return hash;
    }

}
