/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.editor.verification;

import java.util.prefs.Preferences;

/**
 *
 * @author Ondrej Brejla <obrejla@netbeans.org>
 */
public class NestedHintsCustomizer extends javax.swing.JPanel {
    private final Preferences preferences;
    private final NestedBlocksHint nestedBlocksHint;

    public NestedHintsCustomizer(Preferences preferences, NestedBlocksHint nestedBlocksHint) {
        this.preferences = preferences;
        this.nestedBlocksHint = nestedBlocksHint;
        initComponents();
        numberOfAllowedNestedBlocksSpinner.getModel().setValue(nestedBlocksHint.getNumberOfAllowedNestedBlocks(preferences));
        allowConditionBlockCheckBox.setSelected(nestedBlocksHint.allowConditionBlock(preferences));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        numberOfAllowedNestedBlocksSpinner = new javax.swing.JSpinner();
        allowConditionBlockCheckBox = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(NestedHintsCustomizer.class, "NestedHintsCustomizer.jLabel1.text")); // NOI18N

        numberOfAllowedNestedBlocksSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 20, 1));
        numberOfAllowedNestedBlocksSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                numberOfAllowedNestedBlocksSpinnerStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(allowConditionBlockCheckBox, org.openide.util.NbBundle.getMessage(NestedHintsCustomizer.class, "NestedHintsCustomizer.allowConditionBlockCheckBox.text")); // NOI18N
        allowConditionBlockCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allowConditionBlockCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numberOfAllowedNestedBlocksSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(allowConditionBlockCheckBox))
                .addContainerGap(51, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(numberOfAllowedNestedBlocksSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(allowConditionBlockCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void numberOfAllowedNestedBlocksSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_numberOfAllowedNestedBlocksSpinnerStateChanged
        nestedBlocksHint.setNumberOfAllowedNestedBlocks(preferences, (Integer) numberOfAllowedNestedBlocksSpinner.getValue());
    }//GEN-LAST:event_numberOfAllowedNestedBlocksSpinnerStateChanged

    private void allowConditionBlockCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allowConditionBlockCheckBoxActionPerformed
        nestedBlocksHint.setAllowConditionBlock(preferences, allowConditionBlockCheckBox.isSelected());
    }//GEN-LAST:event_allowConditionBlockCheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox allowConditionBlockCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSpinner numberOfAllowedNestedBlocksSpinner;
    // End of variables declaration//GEN-END:variables
}
