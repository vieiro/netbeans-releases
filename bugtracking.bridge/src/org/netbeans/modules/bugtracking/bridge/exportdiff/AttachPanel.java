/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008-2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.bugtracking.bridge.exportdiff;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import org.netbeans.modules.bugtracking.ui.search.QuickSearchComboBar;
import org.netbeans.modules.bugtracking.api.Issue;
import org.netbeans.modules.bugtracking.api.Repository;
import org.netbeans.modules.bugtracking.api.RepositoryManager;
import org.netbeans.modules.bugtracking.util.RepositoryComboSupport;

/**
 *
 * @author Tomas Stupka, Jan Stola
 */
public class AttachPanel extends javax.swing.JPanel implements ItemListener, PropertyChangeListener {
    private QuickSearchComboBar qs;
    private PropertyChangeListener issueListener;
    
    public AttachPanel(PropertyChangeListener issueListener) {
        initComponents();
        this.issueListener = issueListener;
        qs = new QuickSearchComboBar(this);
        issuePanel.add(qs, BorderLayout.NORTH);
        issueLabel.setLabelFor(qs.getIssueComponent());
    }

    void init(File referenceFile) {
        if (referenceFile != null) {
            RepositoryComboSupport.setup(this, repositoryComboBox, referenceFile);
        } else {
            RepositoryComboSupport.setup(this, repositoryComboBox, false);
        }
        repositoryComboBox.addItemListener(this);
        setEnabled(false);
    }

    Issue getIssue() {
        return qs.getIssue();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        repositoryLabel = new javax.swing.JLabel();
        jButton2 = new org.netbeans.modules.versioning.util.WideButton();
        issueLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        issuePanel = new javax.swing.JPanel();
        descriptionLabel = new javax.swing.JLabel();

        repositoryLabel.setLabelFor(repositoryComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(repositoryLabel, org.openide.util.NbBundle.getMessage(AttachPanel.class, "AttachPanel.repositoryLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(AttachPanel.class, "AttachPanel.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(issueLabel, org.openide.util.NbBundle.getMessage(AttachPanel.class, "AttachPanel.issueLabel.text")); // NOI18N

        jLabel2.setForeground(javax.swing.UIManager.getDefaults().getColor("Button.disabledText"));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(AttachPanel.class, "AttachPanel.jLabel2.text")); // NOI18N

        issuePanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2)
            .addComponent(issuePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(issuePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2))
        );

        descriptionLabel.setLabelFor(descriptionTextField);
        org.openide.awt.Mnemonics.setLocalizedText(descriptionLabel, org.openide.util.NbBundle.getMessage(AttachPanel.class, "AttachPanel.descriptionLabel.text")); // NOI18N

        descriptionTextField.setColumns(30);
        descriptionTextField.setText(org.openide.util.NbBundle.getMessage(AttachPanel.class, "AttachPanel.descriptionTextField.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(repositoryLabel)
                    .addComponent(issueLabel)
                    .addComponent(descriptionLabel))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(repositoryComboBox, 0, 367, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(descriptionTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(repositoryLabel)
                    .addComponent(repositoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(issueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(descriptionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(descriptionLabel)))
        );

        jButton2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AttachPanel.class, "AttachPanel.jButton2.AccessibleContext.accessibleDescription")); // NOI18N
        repositoryComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AttachPanel.class, "AttachPanel.repositoryComboBox.AccessibleContext.accessibleDescription")); // NOI18N
        descriptionTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AttachPanel.class, "AttachPanel.descriptionTextField.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Repository repo = RepositoryManager.getInstance().createRepository();
        if(repo == null) {
            return;
        }
        repositoryComboBox.addItem(repo);
        repositoryComboBox.setSelectedItem(repo);
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel descriptionLabel;
    final javax.swing.JTextField descriptionTextField = new javax.swing.JTextField();
    private javax.swing.JLabel issueLabel;
    private javax.swing.JPanel issuePanel;
    private org.netbeans.modules.versioning.util.WideButton jButton2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    final javax.swing.JComboBox repositoryComboBox = new javax.swing.JComboBox();
    private javax.swing.JLabel repositoryLabel;
    // End of variables declaration//GEN-END:variables

    public void itemStateChanged(ItemEvent e) {
        enableFields();
        if(e.getStateChange() == ItemEvent.SELECTED) {
            Object item = e.getItem();
            if(item instanceof Repository) {
                Repository repo = (Repository) item;
                if(repo != null) {
                    qs.setRepository(repo);
                }
            }
        }
    }

    @Override
    public void addNotify() {
        qs.addPropertyChangeListener(this);
        qs.addPropertyChangeListener(issueListener);
        super.addNotify();
    }

    @Override
    public void removeNotify() {
        qs.removePropertyChangeListener(this);
        qs.removePropertyChangeListener(issueListener);
        super.removeNotify();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(QuickSearchComboBar.EVT_ISSUE_CHANGED)) {
            enableFields();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        enableFields(enabled);

        repositoryLabel.setEnabled(enabled);
        repositoryComboBox.setEnabled(enabled);
    }

    private void enableFields() {
        enableFields(true);
    }

    private void enableFields(boolean enable) {
        boolean repoSelected = repositoryComboBox.getSelectedItem() instanceof Repository;
        boolean enableFields = getIssue() != null && repoSelected;

        descriptionTextField.setEnabled(enableFields && enable);
        descriptionLabel.setEnabled(enableFields && enable);

        issueLabel.setEnabled(repoSelected && enable);
        qs.enableFields(repoSelected && enable);
    }

}
