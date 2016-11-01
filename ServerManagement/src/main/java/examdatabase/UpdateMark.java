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
    public Integer practical;
    
    public UpdateMark(  String regNum, String paperCode,
                        Integer practical, Integer coursework){
      
        this.regNum = regNum;
        this.paperCode = paperCode;
        this.coursework = coursework;
        this.practical = practical;
    }
    
    public void setMark(){
        String sql = "UPDATE StudentMark "
                + "SET Coursework = ?, Practical = ? "
                + "WHERE RegNum = ? AND PIIndex = (SELECT PIIndex FROM PaperInfo WHERE PaperCode = ? );";
        
        try (Connection conn = new ConnectDB().connect();
                ){
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            if(coursework != null)
            pstmt.setInt(1,coursework);
            
            if(practical != null)
            pstmt.setInt(2,practical);
            
            pstmt.setString(3,regNum);
            pstmt.setString(4,paperCode);
            
            pstmt.executeUpdate();
            
      
          pstmt.close();
          conn.close();
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          System.out.println(e.getMessage());
          System.exit(0);
        }
        
    }
}
