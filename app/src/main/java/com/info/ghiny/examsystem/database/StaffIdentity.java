package com.info.ghiny.examsystem.database;

import android.util.Base64;

import com.info.ghiny.examsystem.manager.IconManager;
import com.info.ghiny.examsystem.model.JavaHost;
import com.info.ghiny.examsystem.model.ProcessException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Copyright (C) 2016 - 2017 Steven Foong Ghin Yew <stevenfgy@yahoo.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

public class StaffIdentity {
    public static final String STAFF_DB_ID  = "_id";
    public static final String STAFF_NAME   = "Name";
    public static final String STAFF_PASS   = "Password";
    public static final String STAFF_HPASS  = "HashPass";
    public static final String STAFF_ID_NO  = "IdNo";
    public static final String STAFF_LEGIT  = "Eligible";
    public static final String STAFF_ROLE   = "Role";
    public static final String STAFF_VENUE  = "Venue";

    private String idNo;
    private String password;
    private String hashPass;
    private String name;
    private String venueHandling;
    private Role role;


    public StaffIdentity(){
        this.idNo           = null;
        this.name           = null;
        this.password       = null;
        this.venueHandling  = null;
        this.role           = Role.INVIGILATOR;
    }

    public StaffIdentity(String idNo, boolean elg, String name, String venue){
        this.idNo           = idNo;
        this.name           = name;
        this.password       = null;
        this.venueHandling  = venue;
        this.role           = Role.INVIGILATOR;
    }
    //Setter and getter, TO DO: Remove setIdentity as this is for testing purpose


    public String getExamVenue() {
        return venueHandling;
    }

    public void setExamVenue(String venueHandling) {
        this.venueHandling = venueHandling;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = Role.parseRole(role);
    }
    public void setRole(Role role) {
        this.role   = role;
    }

    public String getPassword(){ return this.password;}
    public void setPassword(String newPassword){
        this.password = newPassword;
    }

    public String getIdNo(){    return this.idNo;}
    public void setIdNo(String newRegNum){    this.idNo = newRegNum;}

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
        if(password != null && JavaHost.getConnector().getDuelMessage() != null)
             newEntry   = hmacSha(password, JavaHost.getConnector().getDuelMessage());

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
