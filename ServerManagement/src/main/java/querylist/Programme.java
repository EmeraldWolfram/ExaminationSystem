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
public class Programme {
    
    public final static String TABLE = "Programme";
    public final static String ID = "Programme_id";
    public final static String NAME = "Name";
    public final static String FACULTY = "Faculty";
    public final static String GROUP = "Programme_Group";
    
    public class TableCol{
        
        public final static String ID = "Programme.Programme_id";
        public final static String NAME = "Programme.Name";
        public final static String FACULTY = "Programme.Faculty";
        public final static String GROUP = "Programme.Programme_Group";
    }
    
    Integer programme_id;
    String name;
    String faculty;
    Integer group;
    
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
}
