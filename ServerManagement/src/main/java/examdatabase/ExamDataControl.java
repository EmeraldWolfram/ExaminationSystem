/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examdatabase;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import querylist.Invigilator;
import querylist.Paper;
import querylist.Programme;
import querylist.Venue;

/**
 *
 * @author Krissy
 */
public class ExamDataControl implements Runnable{
    
    public final static Integer PAPER_SPACE = 4;
    
    ExamDataGUI examDataGUI;
    ArrayList<GetData> availablePaper5List = new ArrayList<>();
    ArrayList<GetData> selectedPaper5List = new ArrayList<>();
    
    String months[] = {
      "Jan", "Feb", "Mar", "Apr",
      "May", "Jun", "Jul", "Aug",
      "Sep", "Oct", "Nov", "Dec"};
    
    public ExamDataControl(){
        
    }
    
    public ExamDataControl(ExamDataGUI examDataGUI){
        this.examDataGUI = examDataGUI;
        availablePaper5List = new GetData().getUnassignedVenuePaper();
        
        
    }
    
    public void addGuiListener(ExamDataGUI examDataGUI){
        
        //add Search Button Action Listener in Tab 1
        examDataGUI.addSearchButtonListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                GetData getData = new GetData(examDataGUI.getIcFieldText(),examDataGUI.getNameFieldText(), examDataGUI.getIdFieldText(),
                                        examDataGUI.getStatusBoxText(),examDataGUI.getAttendanceBoxText(), examDataGUI.getTableNumberText(),
                                        examDataGUI.getProgrammeBoxText(),examDataGUI.getFacultyBoxText(),
                                        examDataGUI.getDateFieldText(), examDataGUI.getSessionBoxText(),
                                        examDataGUI.getPaperCodeBoxText(), "",
                                        examDataGUI.getVenueText(),""
                                        );
                examDataGUI.setWarningMessage("");
                ArrayList<GetData> list = null;

                examDataGUI.setExamTableRow(0);
                try {
                    list = getData.getDataFromTable();
                    int i = 0;
                    for(i = 0; i<list.size(); i++){
                        examDataGUI.addExamTableRow(new Object[]{list.get(i).getName(), list.get(i).getIc(), 
                            list.get(i).getRegNum(), list.get(i).getProgName(), list.get(i).getFaculty(),
                            list.get(i).getPaperCode(), list.get(i).getVenueName(), list.get(i).getDate(), 
                            list.get(i).getSession(), list.get(i).getStatus(), list.get(i).getAttendance(), 
                            list.get(i).getTableNum()});
                    }

                } catch (Exception ex) {
                    String message = ex.getMessage();
                    examDataGUI.setWarningMessage(message);
                }
            }

            });
        
        //add Search Button Action Listener in Tab 2
        examDataGUI.addSearchButtonTab2Listener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                GetData getData = new GetData(examDataGUI.getIcField1Text(), examDataGUI.getNameField1Text(), examDataGUI.getIdField1Text(),
                                        "","","",
                                        (String)examDataGUI.getProgrammeBox2Text(), (String)examDataGUI.getFacultyBox2Text(),
                                        "", "",
                                        (String)examDataGUI.getPaperCodeBox2Text(), "",
                                        "",""
                                        );
        
            examDataGUI.setStatusMessage("");
            ArrayList<GetData> list = null;

            examDataGUI.setMarkTableRowCount(0);

            try {
                list = getData.getDataCheckMark();
                int i = 0;
                for(i = 0; i<list.size(); i++){
                    examDataGUI.addMarkTable(new Object[]{list.get(i).getName(), list.get(i).getRegNum(), list.get(i).getIc(), list.get(i).getProgName(), list.get(i).getFaculty(), list.get(i).getPaperCode(), list.get(i).getPractical(), list.get(i).getCoursework()});
                }

            } catch (Exception ex) {
                String message = ex.getMessage();
                examDataGUI.setStatusMessage(message);
            }
            
        }});
        
        
        
        //add Save Button Action Listener in Tab 2
        examDataGUI.addSaveButtonListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                int confirm = JOptionPane.showConfirmDialog(examDataGUI
                        , "Confirm to save?"
                        ,"Save"
                        ,JOptionPane.OK_CANCEL_OPTION);
                GregorianCalendar calendar = new GregorianCalendar();

                if(confirm == JOptionPane.OK_OPTION){

                for(int i = 0; i < examDataGUI.getMarkTableRowCount(); i++){

                    new UpdateData( (String)examDataGUI.getMarkTableCell(i,1),
                                    (String)examDataGUI.getMarkTableCell(i,5),
                                    (Integer)examDataGUI.getMarkTableCell(i,6),
                                    (Integer)examDataGUI.getMarkTableCell(i,7)
                                    ).setMark();



                }
                examDataGUI.setStatusMessage("Recent updated on "
                                        + calendar.get(Calendar.HOUR)+":"
                                        + calendar.get(Calendar.MINUTE)+":"
                                        + calendar.get(Calendar.SECOND)+"  "
                                        + calendar.get(Calendar.DATE) + " "
                                        + months[calendar.get(Calendar.MONTH)]+ " "
                                        + calendar.get(Calendar.YEAR)
                                        + ""
                                        );
                }
            
            
            
         }});
        
        //add Restore Button Action Listener in Tab 2
        examDataGUI.addRestoreButtonListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                int confirm = JOptionPane.showConfirmDialog(examDataGUI
                    , "Confirm to restore to previous saved data?"
                    ,"Restore"
                    ,JOptionPane.OK_CANCEL_OPTION);

                if(confirm == JOptionPane.OK_OPTION){
                ArrayList<GetData> listTable = new ArrayList<>() ;

                    for(int i = 0; i < examDataGUI.getMarkTableRowCount(); i++){

                        listTable.add(new GetData((String)examDataGUI.getMarkTableCell(i,2),(String)examDataGUI.getMarkTableCell(i,0),
                                                    (String)examDataGUI.getMarkTableCell(i,1),
                                                    "","","",
                                                    (String)examDataGUI.getMarkTableCell(i,3), (String)examDataGUI.getMarkTableCell(i,4),
                                                    "", "",
                                                    (String)examDataGUI.getMarkTableCell(i,5), "",
                                                    "",""
                                                    ));
                    }

                    examDataGUI.setMarkTableRowCount(0);
                    ArrayList<GetData> listData = null; 

                    for(int i = 0; i<listTable.size(); i++){
                        try{
                            listData = listTable.get(i).getDataCheckMark();
                            examDataGUI.addMarkTable(new Object[]{listData.get(0).getName(), listData.get(0).getRegNum(), listData.get(0).getIc(), listData.get(0).getProgName(), listData.get(0).getFaculty(), listData.get(0).getPaperCode(), listData.get(0).getPractical(), listData.get(0).getCoursework()});
                        }catch (Exception ex) {
                            String message = ex.getMessage();
                            examDataGUI.setStatusMessage(message);
                        }
                    }

                }
        }});
        
        examDataGUI.addAddPaperTab3ButtonListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                    JPanel panel = new JPanel(new GridLayout(0, 1));
                    JTextField paperCodeField = new JTextField();
                    JTextField paperNameField = new JTextField();
                    JTextField lecturerField = new JTextField();
                    JTextField tutorField = new JTextField();
                    JTextField facultyField = new JTextField();

                    panel.add(new JLabel("Paper Code: "));
                    panel.add(paperCodeField);
                    panel.add(new JLabel("Paper Name: "));
                    panel.add(paperNameField);
                    panel.add(new JLabel("Lecturer: "));
                    panel.add(lecturerField);
                    panel.add(new JLabel("Tutor: "));
                    panel.add(tutorField);
                    panel.add(new JLabel("Faculty: "));
                    panel.add(facultyField);

                    int result = JOptionPane.showConfirmDialog(null, panel, "Add",
                        JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {
                    }
                
              }
            
        });
        
        //add Search Button Action Listener in Tab 3
        examDataGUI.addSearchButtonTab3Listener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                GetData getData = new GetData();
                
                getData.setVenueName((String) examDataGUI.getVenueBox3().getSelectedItem());
                getData.setDate((String) examDataGUI.getDateBox3().getSelectedItem());
                getData.setProgName(examDataGUI.getProgrammeBox3().getSelectedItem().toString());
                getData.setPaperCode(examDataGUI.getPaperCodeField3().getText());
                getData.setPaperDesc(examDataGUI.getPaperNameField3().getText());
//                getData.setPaperDesc(examDataGUI.getPaperNameField3().getText());
        
            examDataGUI.setStatusMessage("");
            ArrayList<GetData> list = null;

            examDataGUI.setPaperTable3RowCount(0);

            try {
                list = getData.getPaperList();
                int i = 0;
                for(i = 0; i<list.size(); i++){
                    examDataGUI.addPaperToPaperTable3(new Object[]{list.get(i).getPaperCode(), list.get(i).getPaperDesc(),
                                                                 list.get(i).getProgName(), list.get(i).getProgGroup(),
                                                                 list.get(i).getVenueName(),
                                                                 list.get(i).getDate(), list.get(i).getSession(),
                                                                 list.get(i).getCollector()});
                }

            } catch (Exception ex) {
                String message = ex.getMessage();
                examDataGUI.setStatusMessage(message);
            }
            
        }});
        
        examDataGUI.addSaveButtonTab3Listener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                int confirm = JOptionPane.showConfirmDialog(examDataGUI
                        , "Confirm to save?"
                        ,"Save"
                        ,JOptionPane.OK_CANCEL_OPTION);
                GregorianCalendar calendar = new GregorianCalendar();

                if(confirm == JOptionPane.OK_OPTION){

                for(int i = 0; i < examDataGUI.getMarkTableRowCount(); i++){

                    new UpdateData( (String)examDataGUI.getMarkTableCell(i,1),
                                    (String)examDataGUI.getMarkTableCell(i,5),
                                    (Integer)examDataGUI.getMarkTableCell(i,6),
                                    (Integer)examDataGUI.getMarkTableCell(i,7)
                                    ).setMark();



                }
                examDataGUI.setStatusMessage("Recent updated on "
                                        + calendar.get(Calendar.HOUR)+":"
                                        + calendar.get(Calendar.MINUTE)+":"
                                        + calendar.get(Calendar.SECOND)+"  "
                                        + calendar.get(Calendar.DATE) + " "
                                        + months[calendar.get(Calendar.MONTH)]+ " "
                                        + calendar.get(Calendar.YEAR)
                                        + ""
                                        );
                }
            }
        });
        
        //add Search Button Action Listener in Tab 4
        examDataGUI.addSearchButtonTab4Listener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                GetData getData = new GetData();
          
                examDataGUI.setStatusMessage("");
                ArrayList<GetData> list = null;
                availablePaper5List = new GetData().getUnassignedVenuePaper();
                examDataGUI.setInvTable4RowCount(0);

                try {
                    list = getData.getInvigilatorInfo(  (String)examDataGUI.getDateBox4().getSelectedItem(),
                                                        (String)examDataGUI.getSessionBox4().getSelectedItem(),
                                                        (String)examDataGUI.getBlockBox4().getSelectedItem(),
                                                        (String)examDataGUI.getVenueBox4().getSelectedItem(),
                                                        examDataGUI.getStaffIDField4().getText(),
                                                        (String)examDataGUI.getStatusBox4().getSelectedItem());
                    int i = 0;
                    for(i = 0; i<list.size(); i++){
                        examDataGUI.addInvigilatorTable4(new Object[]{list.get(i).getDate(), list.get(i).getSession(), list.get(i).getBlock(), list.get(i).getVenueName(), list.get(i).getStaffID(), list.get(i).getInvStatus()});
                    }

                } catch (Exception ex) {
                    Logger.getLogger(ExamDataControl.class.getName()).log(Level.SEVERE, null, ex);
                }
            
        }});
        
        examDataGUI.getVenueBox5().addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e) {
                
                setAvailablePaperTable5Content();
            }
            
        });
        
        ItemListener selectedPaperChange = new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e) {
//                System.out.println(examDataGUI.getAvailablePapers5().getSelectedRow());
                setSelectedPaperTable5Content();
                
            }
            
        };
        
        examDataGUI.getSessionBox5().addItemListener(selectedPaperChange);
        examDataGUI.getVenueBox5().addItemListener(selectedPaperChange);
        examDataGUI.getDateBox5().addItemListener(selectedPaperChange);
        
        examDataGUI.getSelectButton5().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String available = new GetData().getVenueSize((String)ExamDataControl.this.examDataGUI.getVenueBox5().getSelectedItem());
                Integer leftSize = Integer.parseInt(available) - calculateTotalOccupiedCandidate(selectedPaper5List);
                System.out.println("available: " + available);
                Integer paper_id = (Integer) examDataGUI.getAvailablePapers5().getModel().getValueAt(examDataGUI.getAvailablePapers5().getSelectedRow(), 4);
                    for (int i = 0; i < ExamDataControl.this.availablePaper5List.size(); i++ ){
                        if (Objects.equals(ExamDataControl.this.availablePaper5List.get(i).getPaper_id(), paper_id)){
                            try{

                                GetData data = ExamDataControl.this.availablePaper5List.get(i);
                                data.setVenue_id(new GetData().getVenueIdFromDB((String)ExamDataControl.this.examDataGUI.getVenueBox5().getSelectedItem()));
                                data.setSession_id(new GetData().getSessionIdFromDB((String)ExamDataControl.this.examDataGUI.getSessionBox5().getSelectedItem(),
                                                                                    (String)ExamDataControl.this.examDataGUI.getDateBox5().getSelectedItem()));
                                int requireSize = (int) examDataGUI.getAvailablePapers5().getModel().getValueAt(examDataGUI.getAvailablePapers5().getSelectedRow(), 3);
                                System.out.println("require: " + requireSize);
                                System.out.println("left: " + leftSize);
                                if(leftSize < requireSize)
                                    throw new Exception("Space is full.");
                                else
                                    new UpdateData().setVenueAndSessionForPaper(data.getPaper_id(), data.getVenue_id(), data.getSession_id(), getCurrentPaperSpace(ExamDataControl.this.selectedPaper5List));
                            } catch (SQLException ex) {
                                Logger.getLogger(ExamDataControl.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (Exception ex) {
                                ExamDataControl.this.popUpWarningMessage(ex.getMessage());
                            }

                            setSelectedPaperTable5Content();
                            setAvailablePaperTable5Content();
                        }
                    
                    examDataGUI.getOccupiedSizeLabel().setText(calculateTotalOccupiedCandidate(selectedPaper5List).toString());
                }
            }
            
        });
        
        examDataGUI.getRemoveButton5().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                
                Integer paper_id = (Integer) examDataGUI.getSelectedPapers5().getModel().getValueAt(examDataGUI.getSelectedPapers5().getSelectedRow(), 5);
                
                for (int i = 0; i < ExamDataControl.this.selectedPaper5List.size(); i++ ){
                        if (ExamDataControl.this.selectedPaper5List.get(i).getPaper_id() == paper_id){
                            try{

                                GetData data = ExamDataControl.this.selectedPaper5List.get(i);
                                data.setVenue_id(new GetData().getVenueIdFromDB((String)ExamDataControl.this.examDataGUI.getVenueBox5().getSelectedItem()));
                                data.setSession_id(new GetData().getSessionIdFromDB((String)ExamDataControl.this.examDataGUI.getSessionBox5().getSelectedItem(),
                                                                                    (String)ExamDataControl.this.examDataGUI.getDateBox5().getSelectedItem()));
//                                ExamDataControl.this.selectedPaper5List.add(data);
//                                ExamDataControl.this.availablePaper5List.remove(i);
                                new UpdateData().setVenueAndSessionForPaper(data.getPaper_id(), 0, 0, 0);
                            } catch (SQLException ex) {
                                Logger.getLogger(ExamDataControl.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            setSelectedPaperTable5Content();
                            setAvailablePaperTable5Content();
                        }
                    }
                examDataGUI.getOccupiedSizeLabel().setText(calculateTotalOccupiedCandidate(selectedPaper5List).toString());
            }
            
        });
        
        examDataGUI.addAddCandidateButton6Listener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                   
                JPanel panel = new JPanel(new GridLayout(0, 1));
                JTextField studentIDField = new JTextField();
                JTextField studentNameField = new JTextField();
                JTextField studentICField = new JTextField();
                JTextField examIDField = new JTextField();
                JComboBox facultyBox = new JComboBox();
                JComboBox programmeNameBox = new JComboBox();
                JComboBox programmeGroupBox = new JComboBox();

                    panel.add(new JLabel("Register Number: "));
                    panel.add(studentIDField);
                    panel.add(new JLabel("Name: "));
                    panel.add(studentNameField);
                    panel.add(new JLabel("IC number: "));
                    panel.add(studentICField);
                    panel.add(new JLabel("Exam ID: "));
                    panel.add(examIDField);
                    panel.add(new JLabel("Faculty: "));
                    panel.add(facultyBox);
                    panel.add(new JLabel("Programme Name: "));
                    panel.add(programmeNameBox);
                    panel.add(new JLabel("Programme Group: "));
                    panel.add(programmeGroupBox);

                    examDataGUI.createSuggestList(facultyBox, new GetData().getList(Programme.TABLE, Programme.FACULTY));
                    facultyBox.addItemListener(new ItemListener(){
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            try {
                                examDataGUI.createSuggestList(programmeNameBox, new GetData().getListWithOneCond(Programme.TABLE, Programme.FACULTY, (String)facultyBox.getSelectedItem(), Programme.NAME));
                            } catch (Exception ex) {
//                                popUpWarningMessage(ex.getMessage());
//                                Logger.getLogger(ExamDataControl.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                    
                    programmeNameBox.addItemListener(new ItemListener(){
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            try {
                                examDataGUI.createSuggestList(programmeGroupBox, new GetData().getListWithOneCond(Programme.TABLE, Programme.NAME, (String)programmeNameBox.getSelectedItem(), Programme.GROUP));
                            } catch (Exception ex) {
//                                popUpWarningMessage(ex.getMessage());
//                                Logger.getLogger(ExamDataControl.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                    
                    int result = JOptionPane.showConfirmDialog(null, panel, "Add",
                        JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            if(studentICField.getText()==null || studentICField.getText().isEmpty() ||
                                    studentNameField.getText()==null || studentNameField.getText().isEmpty() ||
                                    studentIDField.getText()==null || studentIDField.getText().isEmpty() ||
                                    examIDField.getText()==null || examIDField.getText().isEmpty() ||
                                    (String)programmeNameBox.getSelectedItem() == null || ((String)programmeNameBox.getSelectedItem()).isEmpty()||
                                    (String)programmeGroupBox.getSelectedItem() == null || ((String)programmeGroupBox.getSelectedItem()).isEmpty()
                                    ){
                                throw new Exception("Please fill in all the requirement.");
                            }
                            else{
                                new DataWriter().insertCandidate(studentICField.getText(), studentNameField.getText(),
                                        studentIDField.getText(), (String)programmeNameBox.getSelectedItem(),
                                        (String)programmeGroupBox.getSelectedItem(), examIDField.getText());
                                int programmeId = new GetData().getPaperIdBaseProgrammeFromDB((String)programmeNameBox.getSelectedItem(), (String)programmeGroupBox.getSelectedItem());
    //                                    popUpWarningMessage
                                new DataWriter().addCandidateAttendance(studentICField.getText(), new GetData().getListWithOneCond(Paper.TABLE, Paper.PROGRAMME_ID, String.valueOf(programmeId), Paper.ID));
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(ExamDataControl.class.getName()).log(Level.SEVERE, null, ex);
                            popUpWarningMessage(ex.getMessage());
                        }
                        refreshCandidateTable6();
                    }
                
              }
            
        });
        
        examDataGUI.getSearchButton6().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshCandidateTable6();
            
            }
            
        });
        
        examDataGUI.getDeleteCandidateButton6().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer candidate_id = (Integer) examDataGUI.getCandidateTable6().getModel().getValueAt(examDataGUI.getCandidateTable6().getSelectedRow(), 0);
                String candidate_ic = (String) examDataGUI.getCandidateTable6().getModel().getValueAt(examDataGUI.getCandidateTable6().getSelectedRow(), 2);
                int result = JOptionPane.showConfirmDialog(null, "Remove", "Confirm to remove?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                
                if (result == JOptionPane.YES_OPTION){
                    try {
                        new DataWriter().removeCandidate(candidate_id);
                        new DataWriter().removeCandidateAttendance(candidate_ic);
                        
                    } catch (SQLException ex) {
                        Logger.getLogger(ExamDataControl.class.getName()).log(Level.SEVERE, null, ex);
                        popUpWarningMessage("Please select a row.");
                    }
                }
                refreshCandidateTable6();
            }
        });
        
    }
    
    public Integer calculateTotalOccupiedCandidate(ArrayList<GetData> list){
        int total = 0;
        int i = 0;
        for(i = 0; i< list.size(); i++){
            total = total + list.get(i).getNumOfCand();
        }
        return total;
    }
    
    public void setSelectedPaperTable5Content(){
        examDataGUI.getAvailableSizeLabel().setText(new GetData().getVenueSize(examDataGUI.getVenueBox5().getSelectedItem().toString()));
                examDataGUI.getOccupiedSizeLabel().setText(calculateTotalOccupiedCandidate(selectedPaper5List).toString());
                examDataGUI.setSelectedPapers5RowCount(0);
                
                selectedPaper5List = new GetData().getAssignedVenuePaper((String)examDataGUI.getVenueBox5().getSelectedItem(), (String)examDataGUI.getDateBox5().getSelectedItem(), (String)examDataGUI.getSessionBox5().getSelectedItem());
                Collections.sort(selectedPaper5List, new Comparator<GetData>(){
                    @Override
                    public int compare(GetData o1, GetData o2) {
                        Integer num1 = o1.getStartingNum();
                        Integer num2 = o2.getStartingNum();
                        
                        if(num1>num2)
                            return 1;
                        else if (num1<num2)
                            return -1;
                        else
                            return 0;
                    }
                });
                try {
                    
                    int i = 0;
                    for(i = 0; i< selectedPaper5List.size(); i++){
                        examDataGUI.addSelectedPapers5(new Object[]{selectedPaper5List.get(i).getPaperCode(), selectedPaper5List.get(i).getProgName(), selectedPaper5List.get(i).getProgGroup(), selectedPaper5List.get(i).getNumOfCand(), selectedPaper5List.get(i).getStartingNum(), selectedPaper5List.get(i).getPaper_id()});
                        }
                    examDataGUI.getOccupiedSizeLabel().setText(calculateTotalOccupiedCandidate(selectedPaper5List).toString());

                } catch (Exception ex) {
                    Logger.getLogger(ExamDataControl.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
    public void setAvailablePaperTable5Content(){
        examDataGUI.getAvailableSizeLabel().setText(new GetData().getVenueSize(examDataGUI.getVenueBox5().getSelectedItem().toString()));
                examDataGUI.setAvailablePapers5RowCount(0);
                availablePaper5List = new GetData().getUnassignedVenuePaper();
                try {
                    
                    int i = 0;
                    for(i = 0; i<availablePaper5List.size(); i++){
                        examDataGUI.addAvailablePapers5(new Object[]{availablePaper5List.get(i).getPaperCode(), availablePaper5List.get(i).getProgName(), availablePaper5List.get(i).getProgGroup(), availablePaper5List.get(i).getNumOfCand(), availablePaper5List.get(i).getPaper_id()});
                        }

                } catch (Exception ex) {
                    Logger.getLogger(ExamDataControl.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
    public Integer getCurrentPaperSpace(ArrayList<GetData> selectedPaperList){
        
        GetData data = new GetData();
       
        if(!selectedPaperList.isEmpty()){
            data = selectedPaperList.get(selectedPaperList.size()-1); //return the last element of the arraylist
            return data.getStartingNum() + PAPER_SPACE + data.getNumOfCand();
        }
        else{
            return 1;
        }
        
    }
    
    public void refreshCandidateTable6(){
        GetData data = new GetData();
                data.setName(examDataGUI.getNameField6().getText());
                data.setIc(examDataGUI.getIcField6().getText());
                data.setRegNum(examDataGUI.getIdField6().getText());
                data.setFaculty((String)examDataGUI.getFacultyBox6().getSelectedItem());
                data.setProgName((String)examDataGUI.getProgrammeBox6().getSelectedItem());
                data.setProgGroup((String)examDataGUI.getProgrammeGroupBox6().getSelectedItem());
                
                examDataGUI.setWarningMessage("");
                ArrayList<GetData> list = null;

                examDataGUI.setCandidateTable6RowCount(0);
                try {
                    list = data.getCandidateList(examDataGUI.getNameField6().getText(), examDataGUI.getIcField6().getText(), examDataGUI.getIdField6().getText(),
                                                   (String)examDataGUI.getFacultyBox6().getSelectedItem(), (String)examDataGUI.getProgrammeBox6().getSelectedItem(),
                                                   (String)examDataGUI.getProgrammeGroupBox6().getSelectedItem());
                    int i = 0;
                    for(i = 0; i<list.size(); i++){
                        examDataGUI.addCandidateTable6(new Object[]{list.get(i).getCandidate_id(), list.get(i).getName(), list.get(i).getIc(), list.get(i).getRegNum(), list.get(i).getFaculty(), list.get(i).getProgName(), list.get(i).getProgGroup()});
                    }

                } catch (Exception ex) {
                    String message = ex.getMessage();
                    examDataGUI.setWarningMessage(message);
                }
    }
    
    public void popUpWarningMessage(String message){
        Object[] options = {"OK"};
        int n = JOptionPane.showOptionDialog(examDataGUI,
                   message,"Title",
                   JOptionPane.PLAIN_MESSAGE,
                   JOptionPane.QUESTION_MESSAGE,
                   null,
                   options,
                   options[0]);

    }

    @Override
    public void run() {
        addGuiListener(this.examDataGUI);
    }
}
