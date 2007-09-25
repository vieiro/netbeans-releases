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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.i18n;


import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import org.netbeans.beaninfo.editors.StringEditor;
import org.openide.awt.Mnemonics;
import org.openide.util.HelpCtx;
import org.openide.ErrorManager;


/**
 * Custom editor for editing string type formats with help pattern descritions.
 *
 * @author  Peter Zavadsky
 */
public class HelpStringCustomEditor extends JPanel {

    /** Creates new form CodeCustomEditor.
     * @param value value to be customized 
     * @param items for sleecteing in combo box
     * @param helpItems patterns described in list
     * @param comboText label for the combo-box, optionally with an ampersand marking the mnemonic character
     */
    public HelpStringCustomEditor(String value, List items, List helpItems, String comboText, String helpID) {
        initComponents();
        
        combo.setModel(new DefaultComboBoxModel(items.toArray()));
        combo.setSelectedItem(value);

        list.setListData(helpItems.toArray());
        list.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField..disabledBackground")); // NOI18N
//        list.setBackground(new Color(SystemColor.window.getRGB()));
        
        Mnemonics.setLocalizedText(comboLabel, comboText);
        Mnemonics.setLocalizedText(listLabel, I18nUtil.getBundle().getString("LBL_Arguments"));
        
        initAccessibility ();     
        
        HelpCtx.setHelpIDString(this, helpID);
    }

    /**
    * @return property value that is result of <code>CodeCustomEditor</code>.
    * @exception <code>InvalidStateException</code> when the custom property editor does not represent valid property value
    */
    public Object getPropertyValue() {
        return (String)combo.getSelectedItem();
    }
    
    
    private void initAccessibility() {
        this.getAccessibleContext().setAccessibleDescription(I18nUtil.getBundle().getString("ACS_HelpStringCustomEditor"));
        combo.getAccessibleContext().setAccessibleDescription(I18nUtil.getBundle().getString("ACS_HelpStringCombo"));
        list.getAccessibleContext().setAccessibleDescription(I18nUtil.getBundle().getString("ACS_HelpStringList"));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        combo = new javax.swing.JComboBox();
        scrollPane = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        comboLabel = new javax.swing.JLabel();
        listLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        combo.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 11);
        add(combo, gridBagConstraints);

        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKeyPressed(evt);
            }
        });
        list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listMouseClicked(evt);
            }
        });

        scrollPane.setViewportView(list);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 11, 11);
        add(scrollPane, gridBagConstraints);

        comboLabel.setLabelFor(combo);
        comboLabel.setText("comboLabel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        add(comboLabel, gridBagConstraints);

        listLabel.setLabelFor(list);
        listLabel.setText("listLabel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 11);
        add(listLabel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void listKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKeyPressed
        String selected = (String)list.getSelectedValue();
        if(evt.getKeyCode() == KeyEvent.VK_ENTER && selected != null) {
            evt.consume();
            insertInFormat(selected);
        }
    }//GEN-LAST:event_listKeyPressed

    private void listMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listMouseClicked
        String selected = (String)list.getSelectedValue();
        if(evt.getClickCount() == 2 && selected != null) {
            insertInFormat(selected);
        }
    }//GEN-LAST:event_listMouseClicked

    /** Helper method. */
    private void insertInFormat(String selected) {
        int index = selected.indexOf(' ');
        
        if(index < 0 || index > selected.length())
            return;
        
        String replace = selected.substring(0, index);
        
        JTextField textField = (JTextField)combo.getEditor().getEditorComponent();
        try {
            textField.getDocument().insertString(textField.getCaretPosition(), replace, null); // NOI18N
        } catch(BadLocationException ble) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ble);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox combo;
    private javax.swing.JLabel comboLabel;
    private javax.swing.JList list;
    private javax.swing.JLabel listLabel;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

    /** Nested class. <code>PropertyEditor</code>. 
     * @see I18nOptions#PROP_INIT_JAVA_CODE */
    public static class InitCodeEditor extends StringEditor {
        /** Overrides superclass method. */
        public Component getCustomEditor() {
            return new HelpStringCustomEditor(
                (String)getValue(), 
                I18nUtil.getInitFormatItems(), 
                I18nUtil.getInitHelpItems(),
                I18nUtil.getBundle().getString("LBL_InitCodeFormat"),
                I18nUtil.PE_BUNDLE_CODE_HELP_ID
            );
        }
    } // End of nested class InitCodeEditor.
    
    /** Nested class. <code>PropertyEditor</code>.
     * @see I18nOptions#PROP_INIT_REPLACE_CODE */
    public static class ReplaceCodeEditor extends StringEditor {
        /** Overrides superclass method. */
        public Component getCustomEditor() {
            return new HelpStringCustomEditor(
                (String)getValue(),
                I18nUtil.getReplaceFormatItems(),
                I18nUtil.getReplaceHelpItems(),
                I18nUtil.getBundle().getString("LBL_ReplaceCodeFormat"),
                I18nUtil.PE_REPLACE_CODE_HELP_ID
            );
        }
    } // End of nested class ReplaceCodeEditor.
    
    /** Nested class. <code>PropertyEditor</code>.
     * @see I18nOptions#PROP_REGULAR_EXPRESSION */
    public static class RegExpEditor extends StringEditor {
        /** Overrides superclass method. */
        public Component getCustomEditor() {
            return new HelpStringCustomEditor(
                (String)getValue(),
                I18nUtil.getRegExpItems(),
                I18nUtil.getRegExpHelpItems(),
                I18nUtil.getBundle().getString("LBL_NonI18nRegExpFormat"),
                I18nUtil.PE_I18N_REGEXP_HELP_ID
            );
        }
    } // End of nested class RegExpEditor.
    
    /** Nested class. <code>PropertyEditor</code>.
     * @see I18nOptions#PROP_I18N_REGULAR_EXPRESSION */
    public static class I18nRegExpEditor extends StringEditor {
        /** Overrides superclass method. */
        public Component getCustomEditor() {
            return new HelpStringCustomEditor(
                (String)getValue(), 
                I18nUtil.getI18nRegExpItems(), 
                I18nUtil.getRegExpHelpItems(),
                I18nUtil.getBundle().getString("LBL_I18nRegExpFormat"),
                I18nUtil.PE_TEST_REGEXP_HELP_ID
            );
        }
    } // End of nested class I18nRegExpEditor.
    
}
