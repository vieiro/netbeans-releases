/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.php.editor.indent.ui;

import java.io.IOException;
import static org.netbeans.modules.php.editor.indent.FmtOptions.*;
import static org.netbeans.modules.php.editor.indent.FmtOptions.CategorySupport.OPTION_ID;
import org.netbeans.modules.options.editor.spi.PreferencesCustomizer;


/**
 *
 * @author  phrebejk
 */
public class FmtBraces extends javax.swing.JPanel {

    /** Creates new form FmtAlignmentBraces */
    public FmtBraces() {
        initComponents();
        classDeclCombo.putClientProperty(OPTION_ID, classDeclBracePlacement);
        methodDeclCombo.putClientProperty(OPTION_ID, methodDeclBracePlacement);
	ifCombo.putClientProperty(OPTION_ID, ifBracePlacement);
	forCombo.putClientProperty(OPTION_ID, forBracePlacement);
	switchCombo.putClientProperty(OPTION_ID, switchBracePlacement);
	whileCombo.putClientProperty(OPTION_ID, whileBracePlacement);
	catchCombo.putClientProperty(OPTION_ID, catchBracePlacement);
	useTraitCombo.putClientProperty(OPTION_ID, useTraitBodyBracePlacement);
        otherCombo.putClientProperty(OPTION_ID, otherBracePlacement);
    }

    public static PreferencesCustomizer.Factory getController() {
	String preview = "";
        try {
            preview = Utils.loadPreviewText(FmtBlankLines.class.getClassLoader().getResourceAsStream("org/netbeans/modules/php/editor/indent/ui/Spaces.php"));
        } catch (IOException ex) {
            // TODO log it
        }
        return new CategorySupport.Factory("braces", FmtBraces.class, //NOI18N
                preview);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bracesPlacementLabel = new javax.swing.JLabel();
        classDeclLabel = new javax.swing.JLabel();
        classDeclCombo = new javax.swing.JComboBox();
        methodDeclLabel = new javax.swing.JLabel();
        methodDeclCombo = new javax.swing.JComboBox();
        otherLabel = new javax.swing.JLabel();
        otherCombo = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        ifCombo = new javax.swing.JComboBox();
        forCombo = new javax.swing.JComboBox();
        whileCombo = new javax.swing.JComboBox();
        switchCombo = new javax.swing.JComboBox();
        catchCombo = new javax.swing.JComboBox();
        ifLabel = new javax.swing.JLabel();
        forLabel = new javax.swing.JLabel();
        whileLabel = new javax.swing.JLabel();
        switchLabel = new javax.swing.JLabel();
        catchLabel = new javax.swing.JLabel();
        useTraitLabel = new javax.swing.JLabel();
        useTraitCombo = new javax.swing.JComboBox();

        setFocusTraversalPolicy(null);
        setName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "LBL_Braces")); // NOI18N
        setOpaque(false);

        bracesPlacementLabel.setLabelFor(bracesPlacementLabel);
        org.openide.awt.Mnemonics.setLocalizedText(bracesPlacementLabel, org.openide.util.NbBundle.getMessage(FmtBraces.class, "LBL_br_bracesPlacement")); // NOI18N

        classDeclLabel.setLabelFor(classDeclCombo);
        org.openide.awt.Mnemonics.setLocalizedText(classDeclLabel, org.openide.util.NbBundle.getMessage(FmtBraces.class, "LBL_bp_ClassDecl")); // NOI18N

        classDeclCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        classDeclCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                classDeclComboActionPerformed(evt);
            }
        });

        methodDeclLabel.setLabelFor(methodDeclCombo);
        org.openide.awt.Mnemonics.setLocalizedText(methodDeclLabel, org.openide.util.NbBundle.getMessage(FmtBraces.class, "LBL_bp_MethodDecl")); // NOI18N

        methodDeclCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        methodDeclCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                methodDeclComboActionPerformed(evt);
            }
        });

        otherLabel.setLabelFor(otherCombo);
        org.openide.awt.Mnemonics.setLocalizedText(otherLabel, org.openide.util.NbBundle.getMessage(FmtBraces.class, "LBL_bp_Other")); // NOI18N

        otherCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        otherCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                otherComboActionPerformed(evt);
            }
        });

        ifCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        forCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        whileCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        switchCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        catchCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        ifLabel.setLabelFor(ifCombo);
        org.openide.awt.Mnemonics.setLocalizedText(ifLabel, org.openide.util.NbBundle.getMessage(FmtBraces.class, "LBL_bp_If")); // NOI18N

        forLabel.setLabelFor(forCombo);
        org.openide.awt.Mnemonics.setLocalizedText(forLabel, org.openide.util.NbBundle.getMessage(FmtBraces.class, "LBL_bp_FOR")); // NOI18N

        whileLabel.setLabelFor(whileCombo);
        org.openide.awt.Mnemonics.setLocalizedText(whileLabel, org.openide.util.NbBundle.getMessage(FmtBraces.class, "LBL_bp_WHILE")); // NOI18N

        switchLabel.setLabelFor(switchCombo);
        org.openide.awt.Mnemonics.setLocalizedText(switchLabel, org.openide.util.NbBundle.getMessage(FmtBraces.class, "LBL_bp_SWITCH")); // NOI18N

        catchLabel.setLabelFor(catchCombo);
        org.openide.awt.Mnemonics.setLocalizedText(catchLabel, org.openide.util.NbBundle.getMessage(FmtBraces.class, "LBL_bp_catch")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(useTraitLabel, org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.useTraitLabel.text")); // NOI18N

        useTraitCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(methodDeclLabel)
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(classDeclCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(methodDeclCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bracesPlacementLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(classDeclLabel))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(forLabel)
                            .addComponent(ifLabel)
                            .addComponent(whileLabel)
                            .addComponent(switchLabel)
                            .addComponent(catchLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(catchCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(switchCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(whileCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(forCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ifCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(otherLabel)
                            .addComponent(useTraitLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(useTraitCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(otherCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {classDeclCombo, methodDeclCombo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(bracesPlacementLabel)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(classDeclLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(classDeclCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(methodDeclLabel)
                    .addComponent(methodDeclCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ifCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ifLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(forCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(forLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(whileCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(whileLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(switchCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(switchLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(catchCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(catchLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(useTraitLabel)
                    .addComponent(useTraitCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(otherCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(otherLabel))
                .addGap(23, 23, 23))
        );

        bracesPlacementLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.bracesPlacementLabel.AccessibleContext.accessibleName")); // NOI18N
        bracesPlacementLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.bracesPlacementLabel.AccessibleContext.accessibleDescription")); // NOI18N
        classDeclLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.classDeclLabel.AccessibleContext.accessibleName")); // NOI18N
        classDeclLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.classDeclLabel.AccessibleContext.accessibleDescription")); // NOI18N
        classDeclCombo.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.classDeclCombo.AccessibleContext.accessibleName")); // NOI18N
        classDeclCombo.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.classDeclCombo.AccessibleContext.accessibleDescription")); // NOI18N
        methodDeclLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.methodDeclLabel.AccessibleContext.accessibleName")); // NOI18N
        methodDeclLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.methodDeclLabel.AccessibleContext.accessibleDescription")); // NOI18N
        methodDeclCombo.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.methodDeclCombo.AccessibleContext.accessibleName")); // NOI18N
        methodDeclCombo.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.methodDeclCombo.AccessibleContext.accessibleDescription")); // NOI18N
        otherLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.otherLabel.AccessibleContext.accessibleName")); // NOI18N
        otherLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.otherLabel.AccessibleContext.accessibleDescription")); // NOI18N
        otherCombo.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.otherCombo.AccessibleContext.accessibleName")); // NOI18N
        otherCombo.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.otherCombo.AccessibleContext.accessibleDescription")); // NOI18N
        jSeparator1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.jSeparator1.AccessibleContext.accessibleName")); // NOI18N
        jSeparator1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.jSeparator1.AccessibleContext.accessibleDescription")); // NOI18N
        ifCombo.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.ifCombo.AccessibleContext.accessibleName")); // NOI18N
        ifCombo.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.ifCombo.AccessibleContext.accessibleDescription")); // NOI18N
        forCombo.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.forCombo.AccessibleContext.accessibleName")); // NOI18N
        forCombo.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.forCombo.AccessibleContext.accessibleDescription")); // NOI18N
        whileCombo.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.whileCombo.AccessibleContext.accessibleName")); // NOI18N
        whileCombo.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.whileCombo.AccessibleContext.accessibleDescription")); // NOI18N
        switchCombo.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.switchCombo.AccessibleContext.accessibleName")); // NOI18N
        switchCombo.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.switchCombo.AccessibleContext.accessibleDescription")); // NOI18N
        catchCombo.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.catchCombo.AccessibleContext.accessibleName")); // NOI18N
        catchCombo.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.catchCombo.AccessibleContext.accessibleDescription")); // NOI18N
        ifLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.ifLabel.AccessibleContext.accessibleName")); // NOI18N
        ifLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.ifLabel.AccessibleContext.accessibleDescription")); // NOI18N
        forLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.forLabel.AccessibleContext.accessibleName")); // NOI18N
        forLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.forLabel.AccessibleContext.accessibleDescription")); // NOI18N
        whileLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.whileLabel.AccessibleContext.accessibleName")); // NOI18N
        whileLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.whileLabel.AccessibleContext.accessibleDescription")); // NOI18N
        switchLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.switchLabel.AccessibleContext.accessibleName")); // NOI18N
        switchLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.switchLabel.AccessibleContext.accessibleDescription")); // NOI18N
        catchLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.catchLabel.AccessibleContext.accessibleName")); // NOI18N
        catchLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.catchLabel.AccessibleContext.accessibleDescription")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FmtBraces.class, "FmtBraces.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void methodDeclComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_methodDeclComboActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_methodDeclComboActionPerformed

    private void otherComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_otherComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_otherComboActionPerformed

    private void classDeclComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_classDeclComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_classDeclComboActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bracesPlacementLabel;
    private javax.swing.JComboBox catchCombo;
    private javax.swing.JLabel catchLabel;
    private javax.swing.JComboBox classDeclCombo;
    private javax.swing.JLabel classDeclLabel;
    private javax.swing.JComboBox forCombo;
    private javax.swing.JLabel forLabel;
    private javax.swing.JComboBox ifCombo;
    private javax.swing.JLabel ifLabel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JComboBox methodDeclCombo;
    private javax.swing.JLabel methodDeclLabel;
    private javax.swing.JComboBox otherCombo;
    private javax.swing.JLabel otherLabel;
    private javax.swing.JComboBox switchCombo;
    private javax.swing.JLabel switchLabel;
    private javax.swing.JComboBox useTraitCombo;
    private javax.swing.JLabel useTraitLabel;
    private javax.swing.JComboBox whileCombo;
    private javax.swing.JLabel whileLabel;
    // End of variables declaration//GEN-END:variables

}
