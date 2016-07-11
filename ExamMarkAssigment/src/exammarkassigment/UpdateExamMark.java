/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exammarkassigment;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 *
 * @author Krissy
 */
public class UpdateExamMark {
    
    //CandidateInfo
    public String examId = "";
    
    //Programme
    public String progName = "";
    
    //Paper Info
    public String paperCode = "";
    
    
    //StudentMark
    public Integer examMark ;
    
    public UpdateExamMark(  String examId, String paperCode,
                        String progName, Integer examMark){
      
        this.examId = examId;
        this.paperCode = paperCode;
        this.progName = progName;
        this.examMark = examMark;
    }
    
    public void setMark(){
        String sql = "UPDATE StudentMark "
                + "SET Exam = ?"
                + "WHERE RegNum = (SELECT RegNum FROM CandidateInfo WHERE ExamIndex = ?) "
                + "AND PIIndex = (SELECT PIIndex FROM PaperInfo WHERE PaperCode = ? ); ";
        
        try (Connection conn = new ConnectDB().connect();
                ){
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            
            pstmt.setInt(1,examMark);
            pstmt.setString(2,examId);
            pstmt.setString(3,paperCode);
            
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
