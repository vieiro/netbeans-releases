/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.project.ui.customizer;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.netbeans.modules.php.project.ui.customizer.PhpProjectProperties.RunAsType;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.NbBundle;

/**
 * @author Tomas Mysik
 */
public class RunAsRemoteWeb extends RunAsPanel.InsidePanel {
    private static final long serialVersionUID = -559348988746891271L;
    private final JLabel[] labels;
    private final JComponent[] components;
    private final String[] propertyNames;
    private String displayName;

    public RunAsRemoteWeb(ConfigManager manager, Category category) {
        this(manager, category, NbBundle.getMessage(RunAsRemoteWeb.class, "LBL_ConfigRemoteWeb"));
    }

    public RunAsRemoteWeb(ConfigManager manager, Category category, String displayName) {
        super(manager, category);
        initComponents();
        this.displayName = displayName;
        labels = new JLabel[] {
            urlLabel,
            indexFileLabel,
            argsLabel
        };
        components = new JComponent[] {
            urlTextField,
            indexFileTextField,
            argsTextField
        };
        propertyNames = new String[] {
            PhpProjectProperties.URL,
            PhpProjectProperties.INDEX_FILE,
            PhpProjectProperties.ARGS



        };
        assert labels.length == components.length && labels.length == propertyNames.length;
        // listeners
        for (int i = 0; i < components.length; i++) {
            /*DocumentListener dl = new FieldUpdater(propertyNames[i], labels[i], textFields[i]);
            textFields[i].getDocument().addDocumentListener(dl);*/
        }
    }

    @Override
    protected RunAsType getRunAsType() {
        return RunAsType.REMOTE;
    }

    @Override
    protected String getDisplayName() {
        return displayName;
    }

    @Override
    protected JComboBox getRunAsCombo() {
        return runAsComboBox;
    }

    @Override
    protected JLabel getRunAsLabel() {
        return runAsLabel;
    }

    @Override
    protected void loadFields() {
        // XXX
    }

    @Override
    protected void validateFields() {
        // XXX
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        runAsLabel = new javax.swing.JLabel();
        runAsComboBox = new javax.swing.JComboBox();
        urlLabel = new javax.swing.JLabel();
        urlTextField = new javax.swing.JTextField();
        indexFileLabel = new javax.swing.JLabel();
        indexFileTextField = new javax.swing.JTextField();
        argsLabel = new javax.swing.JLabel();
        argsTextField = new javax.swing.JTextField();
        urlHintLabel = new javax.swing.JTextArea();
        ftpConnectionLabel = new javax.swing.JLabel();
        ftpConnectionComboBox = new javax.swing.JComboBox();
        manageFtpConnectionButton = new javax.swing.JButton();
        uploadDirectoryLabel = new javax.swing.JLabel();
        uploadDirectoryTextField = new javax.swing.JTextField();
        uploadFilesLabel = new javax.swing.JLabel();
        uploadFilesComboBox = new javax.swing.JComboBox();
        uploadFilesHintLabel = new javax.swing.JLabel();

        runAsLabel.setLabelFor(runAsComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(runAsLabel, org.openide.util.NbBundle.getMessage(RunAsRemoteWeb.class, "LBL_RunAs")); // NOI18N

        urlLabel.setLabelFor(urlTextField);
        org.openide.awt.Mnemonics.setLocalizedText(urlLabel, org.openide.util.NbBundle.getMessage(RunAsRemoteWeb.class, "LBL_ProjectUrl")); // NOI18N

        indexFileLabel.setLabelFor(indexFileTextField);
        org.openide.awt.Mnemonics.setLocalizedText(indexFileLabel, org.openide.util.NbBundle.getMessage(RunAsRemoteWeb.class, "LBL_IndexFile")); // NOI18N

        argsLabel.setLabelFor(argsTextField);
        org.openide.awt.Mnemonics.setLocalizedText(argsLabel, org.openide.util.NbBundle.getMessage(RunAsRemoteWeb.class, "LBL_Arguments")); // NOI18N

        urlHintLabel.setEditable(false);
        urlHintLabel.setLineWrap(true);
        urlHintLabel.setRows(2);
        urlHintLabel.setWrapStyleWord(true);
        urlHintLabel.setBorder(null);
        urlHintLabel.setEnabled(false);
        urlHintLabel.setOpaque(false);

        ftpConnectionLabel.setLabelFor(ftpConnectionComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(ftpConnectionLabel, org.openide.util.NbBundle.getMessage(RunAsRemoteWeb.class, "LBL_FtpConnection")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(manageFtpConnectionButton, org.openide.util.NbBundle.getMessage(RunAsRemoteWeb.class, "LBL_Manage")); // NOI18N

        uploadDirectoryLabel.setLabelFor(uploadDirectoryTextField);
        org.openide.awt.Mnemonics.setLocalizedText(uploadDirectoryLabel, org.openide.util.NbBundle.getMessage(RunAsRemoteWeb.class, "LBL_UploadDirectory")); // NOI18N

        uploadFilesLabel.setLabelFor(uploadFilesComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(uploadFilesLabel, org.openide.util.NbBundle.getMessage(RunAsRemoteWeb.class, "LBL_UploadFiles")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(uploadFilesHintLabel, "dummy"); // NOI18N
        uploadFilesHintLabel.setEnabled(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(ftpConnectionLabel)
                    .add(uploadDirectoryLabel)
                    .add(uploadFilesLabel)
                    .add(urlLabel)
                    .add(runAsLabel)
                    .add(indexFileLabel)
                    .add(argsLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(urlTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                    .add(indexFileTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                    .add(argsTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                    .add(urlHintLabel)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, uploadFilesHintLabel)
                    .add(layout.createSequentialGroup()
                        .add(ftpConnectionComboBox, 0, 121, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(manageFtpConnectionButton))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, uploadDirectoryTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, uploadFilesComboBox, 0, 222, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, runAsComboBox, 0, 222, Short.MAX_VALUE))
                .add(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(runAsLabel)
                    .add(runAsComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(urlLabel)
                    .add(urlTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(indexFileLabel)
                    .add(indexFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(argsLabel)
                    .add(argsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(urlHintLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ftpConnectionLabel)
                    .add(manageFtpConnectionButton)
                    .add(ftpConnectionComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(uploadDirectoryLabel)
                    .add(uploadDirectoryTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(uploadFilesLabel)
                    .add(uploadFilesComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(uploadFilesHintLabel)
                .addContainerGap(27, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel argsLabel;
    private javax.swing.JTextField argsTextField;
    private javax.swing.JComboBox ftpConnectionComboBox;
    private javax.swing.JLabel ftpConnectionLabel;
    private javax.swing.JLabel indexFileLabel;
    private javax.swing.JTextField indexFileTextField;
    private javax.swing.JButton manageFtpConnectionButton;
    private javax.swing.JComboBox runAsComboBox;
    private javax.swing.JLabel runAsLabel;
    private javax.swing.JLabel uploadDirectoryLabel;
    private javax.swing.JTextField uploadDirectoryTextField;
    private javax.swing.JComboBox uploadFilesComboBox;
    private javax.swing.JLabel uploadFilesHintLabel;
    private javax.swing.JLabel uploadFilesLabel;
    private javax.swing.JTextArea urlHintLabel;
    private javax.swing.JLabel urlLabel;
    private javax.swing.JTextField urlTextField;
    // End of variables declaration//GEN-END:variables

}
