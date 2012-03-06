/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

/*
 * RepositoryPanel.java
 *
 * Created on Oct 14, 2008, 5:08:33 PM
 */

package org.netbeans.modules.bugzilla.repository;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Tomas Stupka, Jan Stola
 */
public class RepositoryPanel extends javax.swing.JPanel implements ActionListener {
    private BugzillaRepositoryController controller;

    /** Creates new form RepositoryPanel */
    public RepositoryPanel(BugzillaRepositoryController controller) {
        initComponents();
        this.controller = controller;
        validateLabel.setVisible(false);
        progressPanel.setVisible(false);
        httpCheckBox.addActionListener(this);
        enableHttpFields();
        connectionLabel.setForeground(new Color(0, 88, 00));
    }

    @Override
    public void addNotify() {
        super.addNotify();
        controller.populate();
        connectionLabel.setVisible(false);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        controller.cancel();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        urlLabel.setLabelFor(urlField);
        org.openide.awt.Mnemonics.setLocalizedText(urlLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.urlLabel.text_1")); // NOI18N

        urlField.setColumns(30);

        userLabel.setLabelFor(userField);
        org.openide.awt.Mnemonics.setLocalizedText(userLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.userLabel.text_1")); // NOI18N

        psswdLabel.setLabelFor(psswdField);
        org.openide.awt.Mnemonics.setLocalizedText(psswdLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.psswdLabel.text_1")); // NOI18N

        userField.setColumns(15);
        userField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userFieldActionPerformed(evt);
            }
        });

        psswdField.setColumns(15);

        nameLabel.setLabelFor(nameField);
        org.openide.awt.Mnemonics.setLocalizedText(nameLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.nameLabel.text_1")); // NOI18N

        nameField.setColumns(30);

        org.openide.awt.Mnemonics.setLocalizedText(validateButton, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.validateButton.text_1")); // NOI18N
        validateButton.setToolTipText(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.cbEnableLocalUsers.AccessibleContext.accessibleDescription")); // NOI18N
        validateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validateButtonActionPerformed(evt);
            }
        });

        progressPanel.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(validateLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.validateLabel.text_1")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(httpCheckBox, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.httpCheckBox.text")); // NOI18N
        httpCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                httpCheckBoxActionPerformed(evt);
            }
        });

        userLabel1.setLabelFor(httpUserField);
        org.openide.awt.Mnemonics.setLocalizedText(userLabel1, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.userLabel1.text")); // NOI18N

        httpUserField.setColumns(15);
        httpUserField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                httpUserFieldActionPerformed(evt);
            }
        });

        psswdLabel1.setLabelFor(httpPsswdField);
        org.openide.awt.Mnemonics.setLocalizedText(psswdLabel1, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.psswdLabel1.text")); // NOI18N

        httpPsswdField.setColumns(15);

        org.openide.awt.Mnemonics.setLocalizedText(cbEnableLocalUsers, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.cbEnableLocalUsers.text")); // NOI18N

        connectionLabel.setForeground(java.awt.Color.green);
        org.openide.awt.Mnemonics.setLocalizedText(connectionLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.connectionLabel.text_1")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cancelButton, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.cancelButton.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(httpCheckBox)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(psswdLabel1)
                    .addComponent(userLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(httpPsswdField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(httpUserField, javax.swing.GroupLayout.Alignment.LEADING)))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(urlLabel)
                    .addComponent(psswdLabel)
                    .addComponent(userLabel)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(psswdField, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(userField, javax.swing.GroupLayout.Alignment.LEADING))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(urlField, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(nameField, javax.swing.GroupLayout.Alignment.LEADING))))
            .addComponent(cbEnableLocalUsers)
            .addGroup(layout.createSequentialGroup()
                .addComponent(validateButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(validateLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(connectionLabel))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(urlField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(urlLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userLabel)
                    .addComponent(userField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(psswdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(psswdLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(httpCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userLabel1)
                    .addComponent(httpUserField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(httpPsswdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(psswdLabel1))
                .addGap(18, 18, 18)
                .addComponent(cbEnableLocalUsers)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(progressPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(validateLabel)
                    .addComponent(validateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelButton)
                    .addComponent(connectionLabel)))
        );

        urlField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.urlField.AccessibleContext.accessibleDescription")); // NOI18N
        userField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.userField.AccessibleContext.accessibleDescription")); // NOI18N
        psswdField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.psswdField.AccessibleContext.accessibleDescription")); // NOI18N
        nameField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.nameField.AccessibleContext.accessibleDescription")); // NOI18N
        validateButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.validateButton.AccessibleContext.accessibleDescription")); // NOI18N
        httpCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.httpCheckBox.AccessibleContext.accessibleDescription")); // NOI18N
        httpUserField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.httpUserField.AccessibleContext.accessibleDescription")); // NOI18N
        httpPsswdField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.httpPsswdField.AccessibleContext.accessibleDescription")); // NOI18N
        cbEnableLocalUsers.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.cbEnableLocalUsers.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void userFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userFieldActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_userFieldActionPerformed

    private void validateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validateButtonActionPerformed
    }//GEN-LAST:event_validateButtonActionPerformed

    private void httpUserFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_httpUserFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_httpUserFieldActionPerformed

    private void httpCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_httpCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_httpCheckBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    final javax.swing.JButton cancelButton = new javax.swing.JButton();
    final javax.swing.JCheckBox cbEnableLocalUsers = new javax.swing.JCheckBox();
    final javax.swing.JLabel connectionLabel = new javax.swing.JLabel();
    final javax.swing.JCheckBox httpCheckBox = new javax.swing.JCheckBox();
    final javax.swing.JPasswordField httpPsswdField = new javax.swing.JPasswordField();
    final javax.swing.JTextField httpUserField = new javax.swing.JTextField();
    final javax.swing.JTextField nameField = new javax.swing.JTextField();
    final javax.swing.JLabel nameLabel = new javax.swing.JLabel();
    final javax.swing.JPanel progressPanel = new javax.swing.JPanel();
    final javax.swing.JPasswordField psswdField = new javax.swing.JPasswordField();
    final javax.swing.JLabel psswdLabel = new javax.swing.JLabel();
    final javax.swing.JLabel psswdLabel1 = new javax.swing.JLabel();
    final javax.swing.JTextField urlField = new javax.swing.JTextField();
    final javax.swing.JLabel urlLabel = new javax.swing.JLabel();
    final javax.swing.JTextField userField = new javax.swing.JTextField();
    final javax.swing.JLabel userLabel = new javax.swing.JLabel();
    final javax.swing.JLabel userLabel1 = new javax.swing.JLabel();
    final javax.swing.JButton validateButton = new javax.swing.JButton();
    final javax.swing.JLabel validateLabel = new javax.swing.JLabel();
    // End of variables declaration//GEN-END:variables

    void enableFields(boolean bl) {
        psswdLabel.setEnabled(bl);
        psswdField.setEnabled(bl);
        userField.setEnabled(bl);
        userLabel.setEnabled(bl);
        nameField.setEnabled(bl);
        nameLabel.setEnabled(bl);
        urlField.setEnabled(bl);
        urlLabel.setEnabled(bl);
        httpCheckBox.setEnabled(bl);
        cbEnableLocalUsers.setEnabled(bl);
        enableHttpFields();
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == httpCheckBox) {
            enableHttpFields();
        }
    }

    private void enableHttpFields() {
        httpUserField.setEnabled(httpCheckBox.isSelected());
        httpPsswdField.setEnabled(httpCheckBox.isSelected());
    }
}
