package com.info.ghiny.examsystem.database;

/**
 * Created by GhinY on 06/05/2016.
 */
public class StaffIdentity {
    private String regNum;
    private String password;
    private boolean eligible;
    private String name;
    private String venueHandling;


    public StaffIdentity(){
        this.regNum   = null;
        this.password = null;
        this.eligible = false;
        this.name     = null;
        this.venueHandling  = null;
    }

    public StaffIdentity(String regNum, String password, boolean eligible, String name){
        this.regNum     = regNum;
        this.password   = password;
        this.eligible   = eligible;
        this.name       = name;
        this.venueHandling  = null;
    }

    public boolean matchPassword(String password){
        if(password == null)
            return false;
        return (this.password.equals(password));
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

    public void setPassword(String newPassword){    this.password = newPassword;}
    protected String getPassword(){ return this.password;}

    public void setRegNum(String newRegNum){    this.regNum = newRegNum;}
    public String getRegNum(){    return this.regNum;}
}
