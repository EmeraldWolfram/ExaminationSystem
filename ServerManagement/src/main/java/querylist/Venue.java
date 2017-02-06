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
    
    public final static String TABLE_SUB = "Venue.";
    public final static String TABLE = "Venue";
    public final static String ID = "Venue_id";
    public final static String BLOCK = "Block";
    public final static String NAME = "Name";
    public final static String SIZE = "Size";
    
    public class TableCol{
        
        public final static String ID = "Venue.Venue_id";
        public final static String BLOCK = "Venue.Block";
        public final static String NAME = "Venue.Name";
        public final static String SIZE = "Venue.Size";
    }
    
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
