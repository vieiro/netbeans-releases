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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ddloaders.multiview.ui;

import org.netbeans.modules.xml.multiview.Refreshable;
import org.netbeans.modules.xml.multiview.ui.SectionNodeInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionNodeView;

import javax.swing.*;

/**
 * @author pfiala
 */
public class SessionOverviewForm extends SectionNodeInnerPanel {

    static final String SESSION_TYPE_STATELESS="Stateless"; //NOI18N
    static final String SESSION_TYPE_STATEFUL="Stateful"; //NOI18N
    static final String TRANSACTION_TYPE_BEAN="Bean"; //NOI18N
    static final String TRANSACTION_TYPE_CONTAINER="Container"; //NOI18N

    /**
     * Creates new form SessionOverviewForm
     */
    public SessionOverviewForm(SectionNodeView sectionNodeView) {
        super(sectionNodeView);
        initComponents();
        statelessRadioButton.putClientProperty(Refreshable.PROPERTY_FIXED_VALUE, SESSION_TYPE_STATELESS);
        statefulRadioButton.putClientProperty(Refreshable.PROPERTY_FIXED_VALUE, SESSION_TYPE_STATEFUL);
        beanRadioButton.putClientProperty(Refreshable.PROPERTY_FIXED_VALUE, TRANSACTION_TYPE_BEAN);
        containerRadioButton.putClientProperty(Refreshable.PROPERTY_FIXED_VALUE, TRANSACTION_TYPE_CONTAINER);
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

        sessionTypeButtonGroup = new javax.swing.ButtonGroup();
        transactionTypeButtonGroup = new javax.swing.ButtonGroup();
        sessionUnmatchedRadioButton = new javax.swing.JRadioButton();
        transactionUnmatchedRadioButton = new javax.swing.JRadioButton();
        nameLabel = new javax.swing.JLabel();
        ejbNameTextField = new javax.swing.JTextField();
        sessionTypeLabel = new javax.swing.JLabel();
        statelessRadioButton = new javax.swing.JRadioButton();
        statefulRadioButton = new javax.swing.JRadioButton();
        beanRadioButton = new javax.swing.JRadioButton();
        containerRadioButton = new javax.swing.JRadioButton();
        transactionTypeLabel = new javax.swing.JLabel();
        layoutHelperLabel = new javax.swing.JLabel();

        sessionTypeButtonGroup.add(sessionUnmatchedRadioButton);
        sessionUnmatchedRadioButton.setText("null");
        transactionTypeButtonGroup.add(transactionUnmatchedRadioButton);
        transactionUnmatchedRadioButton.setSelected(true);
        transactionUnmatchedRadioButton.setText("null");

        setLayout(new java.awt.GridBagLayout());

        nameLabel.setText(org.openide.util.NbBundle.getMessage(SessionOverviewForm.class, "LBL_EjbName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(nameLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(ejbNameTextField, gridBagConstraints);

        sessionTypeLabel.setText(org.openide.util.NbBundle.getMessage(SessionOverviewForm.class, "LBL_SessionType"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(sessionTypeLabel, gridBagConstraints);

        sessionTypeButtonGroup.add(statelessRadioButton);
        statelessRadioButton.setText(org.openide.util.NbBundle.getMessage(SessionOverviewForm.class, "LBL_Stateless"));
        statelessRadioButton.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(statelessRadioButton, gridBagConstraints);

        sessionTypeButtonGroup.add(statefulRadioButton);
        statefulRadioButton.setText(org.openide.util.NbBundle.getMessage(SessionOverviewForm.class, "LBL_Stateful"));
        statefulRadioButton.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(statefulRadioButton, gridBagConstraints);

        transactionTypeButtonGroup.add(beanRadioButton);
        beanRadioButton.setText(org.openide.util.NbBundle.getMessage(SessionOverviewForm.class, "LBL_Bean"));
        beanRadioButton.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(beanRadioButton, gridBagConstraints);

        transactionTypeButtonGroup.add(containerRadioButton);
        containerRadioButton.setText(org.openide.util.NbBundle.getMessage(SessionOverviewForm.class, "LBL_Container"));
        containerRadioButton.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(containerRadioButton, gridBagConstraints);

        transactionTypeLabel.setText(org.openide.util.NbBundle.getMessage(SessionOverviewForm.class, "LBL_Transaction_Type"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(transactionTypeLabel, gridBagConstraints);

        layoutHelperLabel.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(layoutHelperLabel, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton beanRadioButton;
    private javax.swing.JRadioButton containerRadioButton;
    private javax.swing.JTextField ejbNameTextField;
    private javax.swing.JLabel layoutHelperLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.ButtonGroup sessionTypeButtonGroup;
    private javax.swing.JLabel sessionTypeLabel;
    private javax.swing.JRadioButton sessionUnmatchedRadioButton;
    private javax.swing.JRadioButton statefulRadioButton;
    private javax.swing.JRadioButton statelessRadioButton;
    private javax.swing.ButtonGroup transactionTypeButtonGroup;
    private javax.swing.JLabel transactionTypeLabel;
    private javax.swing.JRadioButton transactionUnmatchedRadioButton;
    // End of variables declaration//GEN-END:variables

    public JTextField getEjbNameTextField() {
        return ejbNameTextField;
    }

    public ButtonGroup getSessionTypeButtonGroup() {
        return sessionTypeButtonGroup;
    }

    public ButtonGroup getTransactionTypeButtonGroup() {
        return transactionTypeButtonGroup;
    }

    public JComponent getErrorComponent(String errorId) {
        return null;
    }

    public void setValue(JComponent source, Object value) {

    }

    public void linkButtonPressed(Object ddBean, String ddProperty) {

    }
}
