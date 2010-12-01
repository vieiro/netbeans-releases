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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

/*
 * TemplatesPanel.java
 *
 * Created on Jan 27, 2010, 2:00:23 PM
 */

package org.netbeans.modules.versioning.util;

/**
 *
 * @author Tomas Stupka
 */
class TemplatesPanel extends javax.swing.JPanel {

    /** Creates new form TemplatesPanel */
    public TemplatesPanel() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();

        jLabel1.setLabelFor(templateTextArea);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(TemplatesPanel.class, "TemplatesPanel.jLabel1.text")); // NOI18N
        jLabel1.setToolTipText(org.openide.util.NbBundle.getMessage(TemplatesPanel.class, "TemplatesPanel.jLabel1.TTtext")); // NOI18N

        templateTextArea.setColumns(20);
        templateTextArea.setRows(5);
        jScrollPane1.setViewportView(templateTextArea);

        org.openide.awt.Mnemonics.setLocalizedText(autoFillInCheckBox, org.openide.util.NbBundle.getMessage(TemplatesPanel.class, "TemplatesPanel.autoFillInCheckBox.text")); // NOI18N
        autoFillInCheckBox.setToolTipText(org.openide.util.NbBundle.getMessage(TemplatesPanel.class, "TemplatesPanel.autoFillInCheckBox.TTtext")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(openButton, org.openide.util.NbBundle.getMessage(TemplatesPanel.class, "TemplatesPanel.openButton.text")); // NOI18N
        openButton.setToolTipText(org.openide.util.NbBundle.getMessage(TemplatesPanel.class, "TemplatesPanel.openButton.TTtext")); // NOI18N
        openButton.setActionCommand(org.openide.util.NbBundle.getMessage(TemplatesPanel.class, "TemplatesPanel.openButton.actionCommand")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(saveButton, org.openide.util.NbBundle.getMessage(TemplatesPanel.class, "TemplatesPanel.saveButton.text")); // NOI18N
        saveButton.setToolTipText(org.openide.util.NbBundle.getMessage(TemplatesPanel.class, "TemplatesPanel.saveButton.TTtext")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .add(autoFillInCheckBox)
                    .add(layout.createSequentialGroup()
                        .add(openButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(saveButton))
                    .add(jLabel1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(autoFillInCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(openButton)
                    .add(saveButton)))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    final javax.swing.JCheckBox autoFillInCheckBox = new javax.swing.JCheckBox();
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    final javax.swing.JButton openButton = new javax.swing.JButton();
    final javax.swing.JButton saveButton = new javax.swing.JButton();
    final javax.swing.JTextArea templateTextArea = new javax.swing.JTextArea();
    // End of variables declaration//GEN-END:variables

    
    
}
