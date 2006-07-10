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

import java.awt.Dialog;
import java.util.*;
import javax.swing.event.*;

import org.openide.*;
import org.openide.util.NbBundle;

import com.sun.collablet.CollabPrincipal;
import org.netbeans.modules.collab.core.Debug;

/**
 *
 * @author  sherylsu
 */
public class SelectUserForm extends javax.swing.JPanel implements ListSelectionListener {
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel matchesFoundLabel;

    // End of variables declaration//GEN-END:variables
    private CollabPrincipal[] users;
    private DialogDescriptor dialogDescriptor;

    /**
     *
     *
     */
    public SelectUserForm(CollabPrincipal[] users) {
        super();
        Arrays.sort(users);
        this.users = users;

        Debug.out.println(" from select user form:" + users); // NOI18N
        initComponents();

        // Update the label
        matchesFoundLabel.setText(
            NbBundle.getMessage(
                SelectUserForm.class, "LBL_SelectUserFORM_NUM_OF_MATCHES", // NOI18N
                new Integer(users.length)
            )
        );

        jList1.setListData(users);

        Vector v = new Vector();
        v.addAll(Arrays.asList(users));

        ListModel model = new ListModel(jList1, v, false, true, false);

        ListRenderer renderer = new ListRenderer(model);
        jList1.setCellRenderer(renderer);
        jList1.setModel(model);
        jList1.addListSelectionListener(this);
    }

    public CollabPrincipal[] getSelectedUsers() {
        dialogDescriptor = new DialogDescriptor(
                this, NbBundle.getMessage(SelectUserForm.class, "TITLE_SelectUserForm")
            ); // NOI18N
        dialogDescriptor.setValid(false);

        Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);

        try {
            dialog.show();

            if (dialogDescriptor.getValue() == DialogDescriptor.OK_OPTION) {
                int[] indices = jList1.getSelectedIndices();
                CollabPrincipal[] result = new CollabPrincipal[indices.length];

                for (int i = 0; i < indices.length; i++) {
                    int j = indices[i];
                    result[i] = users[j];
                }

                Debug.out.println(" return from select user:" + result); // NOI18N

                return result;
            } else {
                return null;
            }
        } finally {
            dialog.dispose();
        }
    }

    /**
     *
     *
     */
    public void valueChanged(ListSelectionEvent event) {
        if (event.getSource() == jList1) {
            dialogDescriptor.setValid(!jList1.isSelectionEmpty());
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

        java.awt.GridBagConstraints gridBagConstraints;

        matchesFoundLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(5, 5, 5, 5)));
        matchesFoundLabel.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        matchesFoundLabel.setText(
            java.util.ResourceBundle.getBundle("org/netbeans/modules/collab/ui/Bundle").getString(
                "LBL_SelectUserFORM_NUM_OF_MATCHES"
            )
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        add(matchesFoundLabel, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        jLabel2.setText(
            java.util.ResourceBundle.getBundle("org/netbeans/modules/collab/ui/Bundle").getString(
                "LBL_SelectUser_PleaseSelect"
            )
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(jLabel2, gridBagConstraints);

        jList1.setMaximumSize(null);
        jList1.setMinimumSize(null);
        jList1.setPreferredSize(null);
        jList1.setVisibleRowCount(10);
        jScrollPane1.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        add(jScrollPane1, gridBagConstraints);
    }//GEN-END:initComponents
}
