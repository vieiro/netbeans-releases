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

package org.netbeans.modules.j2ee.ejbcore.ejb.wizard.session;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;

/**
 *
 * @author  cwebster
 * @author Martin Adamek
 */
public class SessionEJBWizardPanel extends javax.swing.JPanel {

    private ChangeListener listener;

    /** Creates new form SingleEJBWizardPanel */
    public SessionEJBWizardPanel(ChangeListener changeListener) {
        this.listener = changeListener;
        initComponents();

        localCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listener.stateChanged(null);
            }
        });

        remoteCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listener.stateChanged(null);
            }
        });

    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        sessionStateButtons = new javax.swing.ButtonGroup();
        sessionTypeLabel = new javax.swing.JLabel();
        statelessButton = new javax.swing.JRadioButton();
        statefulButton = new javax.swing.JRadioButton();
        interfaceLabel = new javax.swing.JLabel();
        remoteCheckBox = new javax.swing.JCheckBox();
        localCheckBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(sessionTypeLabel, org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_SessionType"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(sessionTypeLabel, gridBagConstraints);

        sessionStateButtons.add(statelessButton);
        statelessButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle").getString("MN_Stateless").charAt(0));
        statelessButton.setSelected(true);
        statelessButton.setText(org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_Stateless"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        add(statelessButton, gridBagConstraints);
        statelessButton.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle").getString("LBL_Stateless"));
        statelessButton.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle").getString("LBL_Stateless"));

        sessionStateButtons.add(statefulButton);
        statefulButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle").getString("MN_Stateful").charAt(0));
        statefulButton.setText(org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_Stateful"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        add(statefulButton, gridBagConstraints);
        statefulButton.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle").getString("LBL_Stateful"));
        statefulButton.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle").getString("LBL_Stateful"));

        org.openide.awt.Mnemonics.setLocalizedText(interfaceLabel, org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_Interface"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 0, 0, 0);
        add(interfaceLabel, gridBagConstraints);

        remoteCheckBox.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle").getString("MN_Remote").charAt(0));
        remoteCheckBox.setText(org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_Remote"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        add(remoteCheckBox, gridBagConstraints);
        remoteCheckBox.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle").getString("LBL_Remote"));
        remoteCheckBox.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle").getString("LBL_Remote"));

        localCheckBox.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle").getString("MN_Local").charAt(0));
        localCheckBox.setSelected(true);
        localCheckBox.setText(org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_Local"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        add(localCheckBox, gridBagConstraints);
        localCheckBox.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle").getString("LBL_Local"));
        localCheckBox.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle").getString("LBL_Local"));

    }// </editor-fold>//GEN-END:initComponents
                        
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel interfaceLabel;
    private javax.swing.JCheckBox localCheckBox;
    private javax.swing.JCheckBox remoteCheckBox;
    private javax.swing.ButtonGroup sessionStateButtons;
    private javax.swing.JLabel sessionTypeLabel;
    private javax.swing.JRadioButton statefulButton;
    private javax.swing.JRadioButton statelessButton;
    // End of variables declaration//GEN-END:variables

    public boolean isStateless() {
        return statelessButton.isSelected();
    }
    
    public boolean isRemote() {
        return remoteCheckBox.isSelected();
    }
    
    public boolean isLocal() {
        return localCheckBox.isSelected();
    }
}
