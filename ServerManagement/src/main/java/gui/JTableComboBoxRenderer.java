/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.Component;
 
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
 

public class JTableComboBoxRenderer extends DefaultTableCellRenderer {
     
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
//        if (value instanceof Country) {
//            Country country = (Country) value;
//            setText(country.getName());
//        }
         
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getSelectionForeground());
        }
         
        return this;
    }
     
}