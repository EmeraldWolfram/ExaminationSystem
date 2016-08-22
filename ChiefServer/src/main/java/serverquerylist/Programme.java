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
public class Programme {
    private Integer programme_id;
    private String name;
    private String faculty;
    private Integer group;
    
    public Programme(){}
    
    public Programme(   Integer programme_id,
                        String name,
                        String faculty,
                        Integer group
                        ){
        this.programme_id = programme_id;
        this.name = name;
        this.faculty = faculty;
        this.group = group;
    }
    
    public Integer getProgramme_id(){
        return this.programme_id;
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getFaculty(){
        return this.faculty;
    }
    
    public Integer getGroup(){
        return this.group;
    }

    /**
     * @param programme_id the programme_id to set
     */
    public void setProgramme_id(Integer programme_id) {
        this.programme_id = programme_id;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param faculty the faculty to set
     */
    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(Integer group) {
        this.group = group;
    }
}
