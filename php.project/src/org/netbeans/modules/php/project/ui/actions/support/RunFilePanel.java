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

package org.netbeans.modules.php.project.ui.actions.support;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.php.project.ui.customizer.RunAsValidator;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

// inspired from ruby project
final class RunFilePanel extends JPanel {
    private static final long serialVersionUID = 92648723648723L;

    private DialogDescriptor dialogDescriptor;
    private NotificationLineSupport notificationLineSupport;

    private RunFilePanel(RunFileActionProvider.RunFileArgs args) {
        initComponents();

        runArgsField.setText(args.getRunArgs());
        workDirField.setText(args.getWorkDir());
        phpOptionsField.setText(args.getPhpOpts());

        workDirField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                processChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                processChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                processChange();
            }

            private void processChange() {
                validateWorkDir();
            }
        });
    }

    public static RunFileActionProvider.RunFileArgs open(RunFileActionProvider.RunFileArgs args, File file, boolean debug) {
        final RunFilePanel panel = new RunFilePanel(args);
        panel.dialogDescriptor = new DialogDescriptor(
                panel,
                NbBundle.getMessage(RunFilePanel.class, debug ? "LBL_DebugFile" : "LBL_RunFile", file.getName()),
                true,
                DialogDescriptor.OK_CANCEL_OPTION,
                DialogDescriptor.OK_OPTION,
                null);
        panel.notificationLineSupport = panel.dialogDescriptor.createNotificationLineSupport();
        panel.validateWorkDir();
        if (DialogDisplayer.getDefault().notify(panel.dialogDescriptor) == DialogDescriptor.OK_OPTION) {
            return panel.getArgs();
        }
        return null;
    }

    private RunFileActionProvider.RunFileArgs getArgs() {
        return new RunFileActionProvider.RunFileArgs(getRunArgs(), getWorkDir(), getPhpOpts(), !displayDialog.isSelected());
    }

    private String getRunArgs() {
        return runArgsField.getText();
    }

    private String getWorkDir() {
        return workDirField.getText();
    }

    private String getPhpOpts() {
        return phpOptionsField.getText();
    }

    void validateWorkDir() {
        assert notificationLineSupport != null;

        String error = RunAsValidator.validateWorkDir(getWorkDir(), false);
        if (error != null) {
            notificationLineSupport.setErrorMessage(error);
            dialogDescriptor.setValid(false);
            return;
        }
        notificationLineSupport.clearMessages();
        dialogDescriptor.setValid(true);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        runArgsLabel = new javax.swing.JLabel();
        runArgsField = new javax.swing.JTextField();
        workDirLabel = new javax.swing.JLabel();
        workDirField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        phpOptionsLabel = new javax.swing.JLabel();
        phpOptionsField = new javax.swing.JTextField();
        displayDialog = new javax.swing.JCheckBox();

        runArgsLabel.setLabelFor(runArgsField);
        org.openide.awt.Mnemonics.setLocalizedText(runArgsLabel, org.openide.util.NbBundle.getMessage(RunFilePanel.class, "RunFilePanel.runArgsLabel.text")); // NOI18N

        workDirLabel.setLabelFor(workDirField);
        org.openide.awt.Mnemonics.setLocalizedText(workDirLabel, org.openide.util.NbBundle.getMessage(RunFilePanel.class, "RunFilePanel.workDirLabel.text")); // NOI18N

        workDirField.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(RunFilePanel.class, "RunFilePanel.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        phpOptionsLabel.setLabelFor(phpOptionsField);
        org.openide.awt.Mnemonics.setLocalizedText(phpOptionsLabel, org.openide.util.NbBundle.getMessage(RunFilePanel.class, "RunFilePanel.phpOptionsLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(displayDialog, org.openide.util.NbBundle.getMessage(RunFilePanel.class, "RunFilePanel.displayDialog.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(phpOptionsLabel)
                            .addComponent(workDirLabel)
                            .addComponent(runArgsLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(runArgsField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(workDirField, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(browseButton))
                            .addComponent(phpOptionsField, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)))
                    .addComponent(displayDialog))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(runArgsLabel)
                    .addComponent(runArgsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(workDirLabel)
                    .addComponent(workDirField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(phpOptionsLabel)
                    .addComponent(phpOptionsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(displayDialog))
        );

        displayDialog.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RunFilePanel.class, "RunFilePanel.displayDialog.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(NbBundle.getMessage(RunFilePanel.class, "LBL_SelectWorkingDirectory"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setCurrentDirectory(new File(getWorkDir()));
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            File workDir = FileUtil.normalizeFile(chooser.getSelectedFile());
            workDirField.setText(workDir.getAbsolutePath());
        }
    }//GEN-LAST:event_browseButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JCheckBox displayDialog;
    private javax.swing.JTextField phpOptionsField;
    private javax.swing.JLabel phpOptionsLabel;
    private javax.swing.JTextField runArgsField;
    private javax.swing.JLabel runArgsLabel;
    private javax.swing.JTextField workDirField;
    private javax.swing.JLabel workDirLabel;
    // End of variables declaration//GEN-END:variables

}
