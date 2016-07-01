/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examdatabase;

import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.JMenuBar;

/**
 *
 * @author Krissy
 */
public class Menu extends JMenuBar{
    public Menu(){
        
        //create File menu catagory
        JMenu menuFile = new JMenu("File");
        add(menuFile);
        
        //create menu File items
        JMenuItem itemSave = new JMenuItem("Save");
        menuFile.add(itemSave);
        JMenuItem itemOption = new JMenuItem("Option");
        menuFile.add(itemOption);
        JMenuItem itemExit = new JMenuItem("Exit");
        menuFile.add(itemExit);
        
        //create Help menu catagory
        JMenu menuView = new JMenu("View");
        add(menuView);
        
        //create menu File items
        JCheckBoxMenuItem itemTool = new JCheckBoxMenuItem("Toolbar");
        menuView.add(itemTool);
        
        //create Help menu catagory
        JMenu menuHelp = new JMenu("Help");
        add(menuHelp);
        
        //create menu File items
        JMenuItem itemAbout = new JMenuItem("About");
        menuHelp.add(itemAbout);
    }
}
