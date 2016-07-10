package com.info.ghiny.examsystem.database;

/**
 * Created by GhinY on 06/05/2016.
 */
public class StaffIdentity {
    public static final String STAFF_NAME   = "Name";
    public static final String STAFF_PASS   = "Password";
    public static final String STAFF_ID_NO  = "IdNo";
    public static final String STAFF_LEGIT  = "Eligible";
    public static final String STAFF_VENUE  = "Venue";

    private String idNo;
    //private String password;
    private boolean eligible;
    private String name;
    private String venueHandling;


    public StaffIdentity(){
        this.idNo = null;
        //this.password = null;
        this.eligible = false;
        this.name     = null;
        this.venueHandling  = null;
    }

    public StaffIdentity(String idNo, boolean elg, String name, String venue){
        this.idNo           = idNo;
        this.eligible       = elg;
        this.name           = name;
        this.venueHandling  = venue;
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

    public void setEligible(boolean eligible){      this.eligible = eligible;}
    public boolean getEligible(){   return this.eligible;}

    //public void setPassword(String newPassword){    this.password = newPassword;}
    //protected String getPassword(){ return this.password;}

    public void setIdNo(String newRegNum){    this.idNo = newRegNum;}
    public String getIdNo(){    return this.idNo;}
}
