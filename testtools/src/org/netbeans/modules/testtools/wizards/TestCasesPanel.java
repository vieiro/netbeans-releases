/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.testtools.wizards;

/*
 * TestCasesPanel.java
 *
 * Created on April 10, 2002, 1:46 PM
 */

import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import java.util.Vector;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JDialog;
import javax.swing.DefaultComboBoxModel;
import org.netbeans.modules.java.JavaDataObject;
import org.openide.loaders.TemplateWizard;
import org.openide.src.MethodElement;
import javax.swing.DefaultListCellRenderer;
import org.openide.util.Utilities;

/**
 *
 * @author  <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 */
public class TestCasesPanel extends javax.swing.JPanel implements WizardDescriptor.FinishPanel {
    
    private Vector listData;

    
    /** Creates new form TestSuitePanel2 */
    public TestCasesPanel() {
        initComponents();
        caseName.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {refreshAdd();}
            public void removeUpdate(DocumentEvent e) {refreshAdd();}
            public void changedUpdate(DocumentEvent e) {refreshAdd();}
        });
        testCaseTypes.setRenderer(new WizardIterator.MyCellRenderer());
        testCases.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {refreshButtons();}
        });
        listData=new Vector();
        testCases.setListData(listData);
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        testCaseTypes = new javax.swing.JComboBox();
        caseName = new javax.swing.JTextField();
        add = new javax.swing.JButton();
        remove = new javax.swing.JButton();
        up = new javax.swing.JButton();
        down = new javax.swing.JButton();
        scroll = new javax.swing.JScrollPane();
        testCases = new javax.swing.JList();
        nameLabel = new javax.swing.JLabel();
        templateLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(testCaseTypes, gridBagConstraints);

        caseName.setText("testMyTestCase");
        caseName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                caseNameActionPerformed(evt);
            }
        });

        caseName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                caseNameFocusGained(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(caseName, gridBagConstraints);

        add.setMnemonic('a');
        add.setText("Add");
        add.setPreferredSize(new java.awt.Dimension(80, 27));
        add.setMinimumSize(new java.awt.Dimension(80, 27));
        add.setEnabled(false);
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(add, gridBagConstraints);

        remove.setMnemonic('r');
        remove.setText("Remove");
        remove.setPreferredSize(new java.awt.Dimension(80, 27));
        remove.setMinimumSize(new java.awt.Dimension(80, 27));
        remove.setEnabled(false);
        remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(remove, gridBagConstraints);

        up.setMnemonic('u');
        up.setText("Up");
        up.setPreferredSize(new java.awt.Dimension(80, 27));
        up.setMinimumSize(new java.awt.Dimension(80, 27));
        up.setEnabled(false);
        up.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(up, gridBagConstraints);

        down.setMnemonic('d');
        down.setText("Down");
        down.setPreferredSize(new java.awt.Dimension(80, 27));
        down.setMinimumSize(new java.awt.Dimension(80, 27));
        down.setEnabled(false);
        down.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(down, gridBagConstraints);

        testCases.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scroll.setViewportView(testCases);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 4.0;
        gridBagConstraints.weighty = 100.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(scroll, gridBagConstraints);

        nameLabel.setText("Test Case Name:");
        nameLabel.setDisplayedMnemonic(110);
        nameLabel.setLabelFor(caseName);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        add(nameLabel, gridBagConstraints);

        templateLabel.setText("Template:");
        templateLabel.setDisplayedMnemonic(116);
        templateLabel.setLabelFor(testCaseTypes);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        add(templateLabel, gridBagConstraints);

    }//GEN-END:initComponents

    private void caseNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_caseNameActionPerformed
        if (add.isEnabled())
            addActionPerformed(evt);
    }//GEN-LAST:event_caseNameActionPerformed

    private void caseNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_caseNameFocusGained
        caseName.selectAll();
    }//GEN-LAST:event_caseNameFocusGained

    private void downActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downActionPerformed
        int index=testCases.getSelectedIndex();
        listData.add(index+1,listData.remove(index));
        testCases.setListData(listData);
        testCases.setSelectedIndex(index+1);
    }//GEN-LAST:event_downActionPerformed

    private void upActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upActionPerformed
        int index=testCases.getSelectedIndex();
        listData.add(index-1,listData.remove(index));
        testCases.setListData(listData);
        testCases.setSelectedIndex(index-1);
    }//GEN-LAST:event_upActionPerformed

    private void removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActionPerformed
        int index=testCases.getSelectedIndex();
        listData.remove(index);
        testCases.setListData(listData);
        if (index>=listData.size())
            index--;
        if (index>=0)
            testCases.setSelectedIndex(index);
        refreshAdd();
    }//GEN-LAST:event_removeActionPerformed

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
        listData.add(new WizardIterator.CaseElement(caseName.getText(),(MethodElement)testCaseTypes.getSelectedItem()));
        testCases.setListData(listData);
        testCases.setSelectedIndex(listData.size()-1);
        refreshAdd();
    }//GEN-LAST:event_addActionPerformed
    
    public void refreshAdd() {
        String name=caseName.getText();
        boolean b=true;
        for (int i=0; b&&i<listData.size(); i++)
            b=!name.equals(((WizardIterator.CaseElement)listData.get(i)).getName());
        add.setEnabled(Utilities.isJavaIdentifier(name)&&b);
    }
    
    public void refreshButtons() {
        int index=testCases.getSelectedIndex();
        remove.setEnabled(index>-1);
        up.setEnabled(index>0);
        down.setEnabled((index>-1)&&(index<listData.size()-1));
    }
    
    public void addChangeListener(javax.swing.event.ChangeListener changeListener) {
    }    
    
    public java.awt.Component getComponent() {
        return this;
    }    
    
    public org.openide.util.HelpCtx getHelp() {
        return new HelpCtx(TestCasesPanel.class);
    }
    
    public void readSettings(Object obj) {
        testCaseTypes.setModel(new DefaultComboBoxModel(WizardSettings.get(obj).templateMethods));
        refreshAdd();
        refreshButtons();
    }

    public void removeChangeListener(javax.swing.event.ChangeListener changeListener) {
    }
    
    public void storeSettings(Object obj) {
        WizardSettings.get(obj).methods=(WizardIterator.CaseElement[])listData.toArray(new WizardIterator.CaseElement[listData.size()]);
    }

    public boolean isValid() {
        return true;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel nameLabel;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTextField caseName;
    private javax.swing.JLabel templateLabel;
    private javax.swing.JComboBox testCaseTypes;
    private javax.swing.JButton up;
    private javax.swing.JButton remove;
    private javax.swing.JButton down;
    private javax.swing.JList testCases;
    private javax.swing.JButton add;
    // End of variables declaration//GEN-END:variables

}

