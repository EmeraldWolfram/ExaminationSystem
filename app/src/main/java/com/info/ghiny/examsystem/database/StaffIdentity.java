package com.info.ghiny.examsystem.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GhinY on 06/05/2016.
 */
public class StaffIdentity {
    public static final String STAFF_NAME   = "Name";
    public static final String STAFF_PASS   = "Password";
    public static final String STAFF_ID_NO  = "IdNo";
    public static final String STAFF_LEGIT  = "Eligible";
    public static final String STAFF_ROLE   = "Status";
    public static final String STAFF_VENUE  = "Venue";

    private String idNo;
    private String password;
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


    public String getVenueHandling() {
        return venueHandling;
    }

    public void setVenueHandling(String venueHandling) {
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

    public void setPassword(String newPassword){    this.password = newPassword;}
    public String getPassword(){ return this.password;}

    public void setIdNo(String newRegNum){    this.idNo = newRegNum;}
    public String getIdNo(){    return this.idNo;}
}
