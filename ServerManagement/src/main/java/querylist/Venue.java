/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package querylist;

/**
 *
 * @author Krissy
 */
public class Venue {
    Integer venue_id;
    String block;
    String name;
    Integer size;
    
    public Venue(){}
    
    public Venue(   Integer venue_id,
                    String block,
                    String name,
                    Integer size
                    ){
        this.venue_id = venue_id;
        this.block = block;
        this.name = name;
        this.size = size;
    }
    
    public Integer getVenue_id(){
        return this.venue_id;
    }
    
    public String getBlock(){
        return this.block;
    }
    
    public String getName(){
        return this.name;
    }
    
    public Integer getSize(){
        return this.size;
    }
}
