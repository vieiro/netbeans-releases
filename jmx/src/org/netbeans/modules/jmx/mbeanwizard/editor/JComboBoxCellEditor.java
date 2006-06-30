/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 2004-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.jmx.mbeanwizard.editor;
import org.netbeans.modules.jmx.mbeanwizard.listener.TableRemoveListener;
import org.netbeans.modules.jmx.mbeanwizard.tablemodel.AbstractJMXTableModel;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;
import org.netbeans.modules.jmx.mbeanwizard.table.AttributeTable;
import org.netbeans.modules.jmx.mbeanwizard.table.OperationTable;
import org.netbeans.modules.jmx.mbeanwizard.table.WrapperAttributeTable;

/**
 * Class implementing the behaviour for the editor of a ComboBox
 * 
 */
public class JComboBoxCellEditor extends DefaultCellEditor 
        implements FocusListener, TableRemoveListener {
    
    /*******************************************************************/
    // here, the model is not typed because more than one table uses it
    // i.e we have to call explicitely the model's internal structure
    // via getValueAt and setValueAt
    /********************************************************************/
    
        private JComboBox tf;
        private JTable table;
        private TableModel model;
        
        private int editedRow = 0;
        private int editedColumn = 0;
        
        private Object lastSelectedItem = null;
        
        /**
         * Constructor
         * @param tf the combobox to add an editor to
         * @param table the JTable which contains the combobox
         */
        public JComboBoxCellEditor(JComboBox tf, JTable table) {
            super(tf);
            this.tf = tf;
            this.table = table;
            this.model = table.getModel();
            tf.addFocusListener(this);
            ((AbstractJMXTableModel)this.model).addTableRemoveListener(this); 
        }

        /**
         * Overriden method; called eached time the component gets in the 
         * editor mode
         * @param table the JTable in which the combobox is in
         * @param value the object with the current value
         * @param isSelected boolean indicating whether the component is 
         * selected or not
         * @param row the selected row in the table
         * @param column the selected column in the table
         * @return Component the modified component
         */
        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int column) {
            lastSelectedItem = tf.getSelectedItem();
            tf.setSelectedItem(model.getValueAt(row,column).toString());
            return tf;
        }
        
        /**
         * Method defining the behaviour of the component when he gets the focus
         * @param e a focusEvent
         */
        public void focusGained(FocusEvent e) {           
            editedRow = table.getEditingRow();
            editedColumn = table.getEditingColumn();
            
        }
        
        /**
         * Method defining the behaviour of the component when he looses 
         * the focus
         * @param e a focusEvent
         */
        public void focusLost(FocusEvent e) {
            
            if (editedColumn != table.getEditingColumn()) {
                lastSelectedItem = tf.getSelectedItem();
            } else if (editedRow == table.getEditingRow()) {
                lastSelectedItem = tf.getSelectedItem();
            }
            if (editedRow < ((AbstractJMXTableModel)table.getModel()).size()) 
                model.setValueAt(lastSelectedItem, editedRow, editedColumn);
           
           
        }
        
        /**
         * Method which sets the model right after a Table Model Event has 
         * been thrown
         * @param e the table model event
         */
        public void tableStateChanged(TableModelEvent e) {
            if (e.getFirstRow() < ((AbstractJMXTableModel)
                        table.getModel()).size()) {
                if (e.getColumn() != -1)
                    tf.setSelectedItem(model.getValueAt(e.getFirstRow(),
                            e.getColumn()).toString());
            }
        }
}
