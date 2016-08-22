/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverquerylist;

/**
 *
 * @author Krissy
 */
public class Venue {
    private Integer venue_id;
    private String block;
    private String name;
    private Integer size;
    
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

    /**
     * @param venue_id the venue_id to set
     */
    public void setVenue_id(Integer venue_id) {
        this.venue_id = venue_id;
    }

    /**
     * @param block the block to set
     */
    public void setBlock(String block) {
        this.block = block;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param size the size to set
     */
    public void setSize(Integer size) {
        this.size = size;
    }
}
