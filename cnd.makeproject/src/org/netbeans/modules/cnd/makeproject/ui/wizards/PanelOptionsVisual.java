/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.cnd.makeproject.ui.wizards;

import org.netbeans.modules.cnd.utils.MIMEExtensions;
import org.openide.WizardDescriptor;

/**
 *
 */
public class PanelOptionsVisual extends SettingsPanel {

    private PanelConfigureProject panel;
    private boolean valid;
    private int type;

    /** Creates new form PanelOptionsVisual */
    public PanelOptionsVisual(PanelConfigureProject panel, int type) {
        initComponents();
        this.panel = panel;
        this.type = type;
        setAsMainCheckBox.setVisible(true);

        createMainTextField.setText("main"); // NOI18N
        createMainComboBox.addItem("C"); // NOI18N
        createMainComboBox.addItem("C++"); // NOI18N
        createMainComboBox.setSelectedIndex(0);

        if (type == NewMakeProjectWizardIterator.TYPE_APPLICATION) {
            createMainCheckBox.setVisible(true);
            createMainTextField.setVisible(true);
            createMainComboBox.setVisible(true);
        } else if (type == NewMakeProjectWizardIterator.TYPE_QT_APPLICATION) {
            createMainCheckBox.setVisible(true);
            createMainTextField.setVisible(true);
            createMainComboBox.setVisible(false);
        } else {
            createMainCheckBox.setSelected(false);
            createMainCheckBox.setVisible(false);
            createMainTextField.setVisible(false);
            createMainComboBox.setVisible(false);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        createMainCheckBox = new javax.swing.JCheckBox();
        createMainTextField = new javax.swing.JTextField();
        createMainComboBox = new javax.swing.JComboBox();
        setAsMainCheckBox = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        createMainCheckBox.setSelected(true);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/makeproject/ui/wizards/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(createMainCheckBox, bundle.getString("LBL_createMainfile")); // NOI18N
        createMainCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        createMainCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createMainCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 0, 0);
        add(createMainCheckBox, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(createMainTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(createMainComboBox, gridBagConstraints);

        setAsMainCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(setAsMainCheckBox, bundle.getString("LBL_setAsMainCheckBox")); // NOI18N
        setAsMainCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(setAsMainCheckBox, gridBagConstraints);
        setAsMainCheckBox.getAccessibleContext().setAccessibleName(bundle.getString("ACSN_setAsMainCheckBox")); // NOI18N
        setAsMainCheckBox.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_setAsMainCheckBox")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);
        jPanel1.getAccessibleContext().setAccessibleName(bundle.getString("ACSN_jPanel1")); // NOI18N
        jPanel1.getAccessibleContext().setAccessibleDescription(bundle.getString("ASCD_jPanel1")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACSN_PanelOptionsVisual")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACSD_PanelOptionsVisual")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void createMainCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createMainCheckBoxActionPerformed
        // TODO add your handling code here:
        createMainTextField.setEnabled(createMainCheckBox.isSelected());
        createMainComboBox.setEnabled(createMainCheckBox.isSelected());
    }//GEN-LAST:event_createMainCheckBoxActionPerformed

    boolean valid(WizardDescriptor settings) {
        return true;
    }

    void read(WizardDescriptor d) {
        //TODO:
    }

    void store(WizardDescriptor d) {
        d.putProperty( /*XXX Define somewhere */"setAsMain", setAsMainCheckBox.isSelected() && setAsMainCheckBox.isVisible() ? Boolean.TRUE : Boolean.FALSE); // NOI18N
        d.putProperty( /*XXX Define somewhere */"mainClass", null); // NOI18N


        MIMEExtensions cExtensions = MIMEExtensions.get("text/x-c"); // NOI18N
        MIMEExtensions ccExtensions = MIMEExtensions.get("text/x-c++"); // NOI18N

        d.putProperty("createMainFile", createMainCheckBox.isSelected() ? Boolean.TRUE : Boolean.FALSE); // NOI18N
        if (createMainCheckBox.isSelected() && createMainTextField.getText().length() > 0) {
            if (type == NewMakeProjectWizardIterator.TYPE_APPLICATION) {
                if (((String) createMainComboBox.getSelectedItem()).equals("C")) { // NOI18N
                    d.putProperty("mainFileName", createMainTextField.getText() + "." + cExtensions.getDefaultExtension()); // NOI18N
                    d.putProperty("mainFileTemplate", "Templates/cFiles/main.c"); // NOI18N
                } else {
                    d.putProperty("mainFileName", createMainTextField.getText() + "." + ccExtensions.getDefaultExtension()); // NOI18N
                    d.putProperty("mainFileTemplate", "Templates/cppFiles/main.cc"); // NOI18N
                }
            } else if (type == NewMakeProjectWizardIterator.TYPE_QT_APPLICATION) {
                d.putProperty("mainFileName", createMainTextField.getText() + "." + ccExtensions.getDefaultExtension()); // NOI18N
                d.putProperty("mainFileTemplate", "Templates/qtFiles/main.cc"); // NOI18N
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox createMainCheckBox;
    private javax.swing.JComboBox createMainComboBox;
    private javax.swing.JTextField createMainTextField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JCheckBox setAsMainCheckBox;
    // End of variables declaration//GEN-END:variables
}

