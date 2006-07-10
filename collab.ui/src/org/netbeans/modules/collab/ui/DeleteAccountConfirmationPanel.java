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
package org.netbeans.modules.collab.ui;

import org.openide.util.*;

import com.sun.collablet.Account;

public class DeleteAccountConfirmationPanel extends javax.swing.JPanel {
    // </editor-fold>
    // Variables declaration - do not modify
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;

    /** Creates new form DeleteAccountConfirmationPanel */
    public DeleteAccountConfirmationPanel(Account account) {
        initComponents();
        jLabel1.setText(
            NbBundle.getMessage(
                DeleteAccountConfirmationPanel.class, "LBL_DeleteAccountConfirmationPanel_Confirm",
                account.getDisplayName()
            )
        );
        jCheckBox1.setText(
            NbBundle.getMessage(
                DeleteAccountConfirmationPanel.class, "BTN_DeleteAccountConfirmationPanel_DeleteServerAccount",
                account.getServer()
            )
        );
        jCheckBox1.setMnemonic(
            NbBundle.getMessage(
                DeleteAccountConfirmationPanel.class, "BTN_DeleteAccountConfirmationPanel_DeleteServerAccount_Mnemonic",
                account.getServer()
            ).charAt(0)
        );
    }

    /**
     *
     *
     */
    public boolean deleteServerAccount() {
        return jCheckBox1.isSelected();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(5, 10, 0, 10)));
        setMinimumSize(new java.awt.Dimension(488, 50));
        setPreferredSize(null);
        jLabel1.setText(
            java.util.ResourceBundle.getBundle("org/netbeans/modules/collab/ui/Bundle").getString(
                "LBL_DeleteAccountConfirmationPanel_Confirm"
            )
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
        add(jLabel1, gridBagConstraints);

        jCheckBox1.setText(
            java.util.ResourceBundle.getBundle("org/netbeans/modules/collab/ui/Bundle").getString(
                "BTN_DeleteAccountConfirmationPanel_DeleteServerAccount"
            )
        );
        jCheckBox1.setBorder(null);
        jCheckBox1.setFocusPainted(true);
        jCheckBox1.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        add(jCheckBox1, gridBagConstraints);
    }

    // End of variables declaration                   
}
