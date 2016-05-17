package com.info.ghiny.examsystem;

/**
 * Created by GhinY on 06/05/2016.
 */
public class Identity {
    private String identity;
    private String password;
    private boolean eligible;


    public Identity(){
        this.identity = null;
        this.password = null;
        this.eligible = false;
    }

    public Identity(String identity, String password, boolean eligible){
        this.identity   = identity;
        this.password   = password;
        this.eligible   = eligible;
    }

    public boolean matchPassword(Identity identity){
        return (identity.identity.equals(this.identity)
                && identity.password.equals(this.password));
    }

    //Setter and getter, TO DO: Remove setIdentity as this is for testing purpose
    public void setEligible(boolean eligible){      this.eligible = eligible;}
    public boolean getEligible(){   return this.eligible;}

    public void setPassword(String newPassword){    this.password = newPassword;}
    protected String getPassword(){ return this.password;}

    public void setIdentity(String newIdentity){    this.identity = newIdentity;}
    public String getIdentity(){    return this.identity;}
}
