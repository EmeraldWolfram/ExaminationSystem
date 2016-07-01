/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examdatabase;
import java.sql.*;

/**
 *
 * @author Krissy
 */
public class UpdateMark {
    
     //CandidateInfo
    public String ic = ""; 
    public String name = ""; 
    public String regNum = "";
    
    //Programme
    public String progName = "";
    public String faculty = "";
    
    //Paper Info
    public String paperCode = "";
    
    //Paper
    public String date = "";
    
    //StudentMark
    public Integer coursework ;
    public Integer pratical;
    
    public UpdateMark(  String regNum, String paperCode,
                        Integer pratical, Integer coursework){
      
        this.regNum = regNum;
        this.paperCode = paperCode;
        this.coursework = coursework;
        this.pratical = pratical;
    }
    
    public void setMark(){
        String sql = "UPDATE StudentMark "
                + "SET Coursework = ?, Pratical = ? "
//                + "FROM StudentMark JOIN PaperInfo ON StudentMark.PIIndex = PaperInfo.PIIndex"
                + "WHERE RegNum = ? AND PIIndex = (SELECT PIIndex FROM PaperInfo WHERE PaperCode = ? );";
        
        try (Connection conn = new ConnectDB().connect();
                ){
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            if(coursework != null)
            pstmt.setInt(1,coursework);
            
            if(pratical != null)
            pstmt.setInt(2,pratical);
            
            pstmt.setString(3,regNum);
            pstmt.setString(4,paperCode);
            
            pstmt.executeUpdate();
            
            sql = "SELECT id, first, last, age FROM Registration";
//            ResultSet rs = stmt.executeQuery(sql);
      
//          pstmt.close();
//          conn.close();
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          System.exit(0);
        }
        
    }
}
