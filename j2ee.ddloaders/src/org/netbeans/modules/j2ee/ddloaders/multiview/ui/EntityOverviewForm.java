/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ddloaders.multiview.ui;

import org.netbeans.modules.xml.multiview.ui.SectionNodeInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionNodeView;

import javax.swing.*;

/**
 * @author pfiala
 */
public class EntityOverviewForm extends SectionNodeInnerPanel {

    /**
     * Creates new form EntityOverviewForm
     */
    public EntityOverviewForm(SectionNodeView sectionNodeView) {
        super(sectionNodeView);
        initComponents();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        primaryKeyFieldLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        ejbNameTextField = new javax.swing.JTextField();
        persistenceTypeTextField = new javax.swing.JTextField();
        abstractSchemaNameTextField = new javax.swing.JTextField();
        primaryKeyFieldComboBox = new javax.swing.JComboBox();
        primaryKeyClassComboBox = new javax.swing.JComboBox();
        primaryKeyClassTextField = new javax.swing.JTextField();
        reentrantCheckBox = new javax.swing.JCheckBox();
        spacerLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(EntityOverviewForm.class, "LBL_EjbName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 6);
        add(jLabel1, gridBagConstraints);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(EntityOverviewForm.class, "LBL_PersistenceType"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 6);
        add(jLabel2, gridBagConstraints);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(EntityOverviewForm.class, "LBL_AbstractSchemaName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 6);
        add(jLabel3, gridBagConstraints);

        primaryKeyFieldLabel.setText(org.openide.util.NbBundle.getMessage(EntityOverviewForm.class, "LBL_PrimaryKeyField"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 6);
        add(primaryKeyFieldLabel, gridBagConstraints);

        jLabel5.setText("Primary key class:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 6);
        add(jLabel5, gridBagConstraints);

        jLabel6.setText("Reentrant:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 6);
        add(jLabel6, gridBagConstraints);

        ejbNameTextField.setColumns(25);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(ejbNameTextField, gridBagConstraints);

        persistenceTypeTextField.setColumns(25);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(persistenceTypeTextField, gridBagConstraints);

        abstractSchemaNameTextField.setColumns(25);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(abstractSchemaNameTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(primaryKeyFieldComboBox, gridBagConstraints);

        primaryKeyClassComboBox.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(primaryKeyClassComboBox, gridBagConstraints);

        primaryKeyClassTextField.setColumns(25);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(primaryKeyClassTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        add(reentrantCheckBox, gridBagConstraints);

        spacerLabel.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(spacerLabel, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField abstractSchemaNameTextField;
    private javax.swing.JTextField ejbNameTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField persistenceTypeTextField;
    private javax.swing.JComboBox primaryKeyClassComboBox;
    private javax.swing.JTextField primaryKeyClassTextField;
    private javax.swing.JComboBox primaryKeyFieldComboBox;
    private javax.swing.JLabel primaryKeyFieldLabel;
    private javax.swing.JCheckBox reentrantCheckBox;
    private javax.swing.JLabel spacerLabel;
    // End of variables declaration//GEN-END:variables

    public JTextField getAbstractSchemaNameTextField() {
        return abstractSchemaNameTextField;
    }

    public JTextField getEjbNameTextField() {
        return ejbNameTextField;
    }

    public JTextField getPersistenceTypeTextField() {
        return persistenceTypeTextField;
    }

    public JComboBox getPrimaryKeyClassComboBox() {
        return primaryKeyClassComboBox;
    }

    public JLabel getPrimaryKeyFieldLabel() {
        return primaryKeyFieldLabel;
    }

    public JComboBox getPrimaryKeyFieldComboBox() {
        return primaryKeyFieldComboBox;
    }

    public JCheckBox getReentrantCheckBox() {
        return reentrantCheckBox;
    }

    public JLabel getSpacerLabel() {
        return spacerLabel;
    }

    public JComponent getErrorComponent(String errorId) {
        return null;
    }

    public void setValue(JComponent source, Object value) {
    }

    public void linkButtonPressed(Object ddBean, String ddProperty) {
    }

    public JTextField getPrimaryKeyClassTextField() {
        return primaryKeyClassTextField;
    }
}
