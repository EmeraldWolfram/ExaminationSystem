/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examdatabase;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Krissy
 */
public class GUIapplication extends JFrame implements ActionListener{
    
    JTextField nameField, icField,idField;
    JTextField venueField,dateField,sessionField;
    JTextField paparCodeField;
    JTextArea textArea;
    DefaultComboBoxModel searchBy;
    
    public GUIapplication(String name){
        super(name);
    };
    
    
    static void createGUI() {
        GUIapplication frame = new GUIapplication("Protol");
        frame.setLayout(new CardLayout(10,10));
        frame.setSize(100,100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.addPanel(frame.getContentPane());
         //Add the ubiquitous "Hello World" label.
        JLabel label = new JLabel("Hello World");

        
        
//         Create the menu bar.  Make it have a BLUE background.
        JMenuBar blueMenuBar = new Menu();
        blueMenuBar.setOpaque(true);
        blueMenuBar.setBackground(new Color(165, 157, 239));
        blueMenuBar.setPreferredSize(new Dimension(200, 20));
        frame.setJMenuBar(blueMenuBar);

        frame.pack();
        frame.setVisible(true);
    }
    
    private void addPanel(Container pane){
        pane.setLayout(new BorderLayout());
        pane.setPreferredSize(new Dimension(1000,500));
        
        //first column spring layout
        SpringLayout springLayout = new SpringLayout();
        JPanel firstColumn = new JPanel();
        firstColumn.setBounds(60,60,60,60);
        firstColumn.setLayout(new BoxLayout(firstColumn, BoxLayout.Y_AXIS));
//        firstColumn.setPreferredSize(new Dimension(500,100));
        // Create text field 

        JLabel nameLabel = new JLabel("Name:",JLabel.TRAILING);
        nameLabel.setLabelFor(nameField);
        firstColumn.add(nameLabel);
        nameField = new JTextField(15);
        firstColumn.add(nameField);
        
//        JLabel icLabel = new JLabel("IC:",JLabel.TRAILING);
//        icLabel.setLabelFor(nameField);
//        icField = new JTextField(15);
//        firstColumn.add(icField);
        
        pane.add(firstColumn, BorderLayout.CENTER);
        springLayout.putConstraint(SpringLayout.EAST, nameLabel, 10, SpringLayout.WEST, nameField);
        
        // Create Button
        Button searchButton = new Button("SEARCH");
        searchButton.addActionListener(this);
        

    }
    
    public void addOutputText(){
        textArea = new JTextArea("System Initiated.\n",10,100);
        JPanel panel = new JPanel(new FlowLayout());
        
       // panel.setPreferredSize(new Dimension(100,100));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        panel.add(scrollPane);
        this.getContentPane().add(panel, "South");
        
        
    }
    
    
    
     public void actionPerformed(ActionEvent e){
        String text = nameField.getText();
        String searchFor = (String) searchBy.getSelectedItem();
        textArea.append("\nSearching: "+ text + "\n" + "Result:\n");
        nameField.selectAll();
        
        GetData getData = new GetData();
        try {
            ArrayList<GetData> list = getData.getDataFromTable();
        } catch (CustomException ex) {
            Logger.getLogger(GUIapplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        //textArea.append();
        textArea.append("\nEnd of searching.\n");
        //Make sure the new text is visible, even if there
        //was a selection in the text area.
        textArea.setCaretPosition(textArea.getDocument().getLength());
            
        }

  
    
    
    
}
