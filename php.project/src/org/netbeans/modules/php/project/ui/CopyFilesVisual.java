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

package org.netbeans.modules.php.project.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 * @author Tomas Mysik
 */
public class CopyFilesVisual extends JPanel {
    private static final long serialVersionUID = 16907251064819776L;

    final LocalServerController localServerController;
    final ChangeSupport changeSupport = new ChangeSupport(this);

    public CopyFilesVisual(SourcesFolderNameProvider sourcesFolderNameProvider, LocalServer... defaultLocalServers) {
        initComponents();

        localServerController = LocalServerController.create(copyFilesComboBox, copyFilesButton, sourcesFolderNameProvider,
                NbBundle.getMessage(CopyFilesVisual.class, "LBL_SelectFolderLocation"), defaultLocalServers);
        // set default, disabled state
        localServerController.setEnabled(false);

        copyFilesCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyFilesCheckBoxChanged();
                changeSupport.fireChange();
            }
        });
        localServerController.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                changeSupport.fireChange();
            }
        });
    }

    void copyFilesCheckBoxChanged() {
        boolean selected = copyFilesCheckBox.isSelected();
        localServerLabel.setEnabled(selected);
        localServerController.setEnabled(selected);
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    public boolean isCopyFiles() {
        return copyFilesCheckBox.isSelected();
    }

    public void setCopyFiles(boolean copyFiles) {
        copyFilesCheckBox.setSelected(copyFiles);
        copyFilesCheckBoxChanged();
    }

    public LocalServer getLocalServer() {
        return localServerController.getLocalServer();
    }

    public MutableComboBoxModel getLocalServerModel() {
        return localServerController.getLocalServerModel();
    }

    public void setLocalServerModel(MutableComboBoxModel localServers) {
        localServerController.setLocalServerModel(localServers);
    }

    public void selectLocalServer(LocalServer localServer) {
        localServerController.selectLocalServer(localServer);
    }

    // to enable/disable components
    public void setState(boolean enabled) {
        copyFilesCheckBox.setEnabled(enabled);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        copyFilesCheckBox = new javax.swing.JCheckBox();
        localServerLabel = new javax.swing.JLabel();
        copyFilesComboBox = new javax.swing.JComboBox();
        copyFilesButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(copyFilesCheckBox, org.openide.util.NbBundle.getMessage(CopyFilesVisual.class, "LBL_CopyFiles")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(localServerLabel, org.openide.util.NbBundle.getMessage(CopyFilesVisual.class, "LBL_CopyFileToFolder")); // NOI18N
        localServerLabel.setEnabled(false);

        copyFilesComboBox.setEditable(true);
        copyFilesComboBox.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(copyFilesButton, org.openide.util.NbBundle.getMessage(CopyFilesVisual.class, "LBL_Browse")); // NOI18N
        copyFilesButton.setEnabled(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(copyFilesCheckBox)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(localServerLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(copyFilesComboBox, 0, 168, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(copyFilesButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(copyFilesCheckBox)
                .add(9, 9, 9)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(copyFilesButton)
                    .add(copyFilesComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(localServerLabel)))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton copyFilesButton;
    private javax.swing.JCheckBox copyFilesCheckBox;
    private javax.swing.JComboBox copyFilesComboBox;
    private javax.swing.JLabel localServerLabel;
    // End of variables declaration//GEN-END:variables

}
