/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

/*
 * LocalInstanceComponent.java
 *
 * Created on Jul 1, 2011, 3:30:21 PM
 */
package org.netbeans.modules.cloud.oracle.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.j2ee.weblogic9.WLPluginProperties;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 *
 * @author Petr Hejl
 */
public class LocalInstanceComponent extends javax.swing.JPanel {

    private final ChangeSupport support = new ChangeSupport(this);

    /** Creates new form LocalInstanceComponent */
    public LocalInstanceComponent() {
        initComponents();
        setName(NbBundle.getBundle(LocalInstanceComponent.class).getString("LBL_Name_Local"));
        
        localServerComboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                support.fireChange();
            }
        });
        localServerComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                support.fireChange();
            }
        });
        
        String loc = WLPluginProperties.getLastServerRoot();
        if (loc != null) { // NOI18N
            if(LocalInstancePanel.isSupportedVersion(
                    WLPluginProperties.getServerVersion(new File(loc)))) {
                localServerComboBox.getEditor().setItem(loc);
            }
        }
    }

    public void addChangeListener(ChangeListener l) {
        support.addChangeListener(l);
    }

    public void removeChangeListener(ChangeListener l) {
        support.removeChangeListener(l);
    }
    
    String getLocalServerDirectory() {
        return (String) localServerComboBox.getEditor().getItem();
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        localServerLabel = new javax.swing.JLabel();
        localServerComboBox = new javax.swing.JComboBox();
        localServerBrowseButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        localServerLabel.setLabelFor(localServerComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(localServerLabel, org.openide.util.NbBundle.getMessage(LocalInstanceComponent.class, "LocalInstanceComponent.localServerLabel.text")); // NOI18N

        localServerComboBox.setEditable(true);

        org.openide.awt.Mnemonics.setLocalizedText(localServerBrowseButton, org.openide.util.NbBundle.getMessage(LocalInstanceComponent.class, "LocalInstanceComponent.localServerBrowseButton.text")); // NOI18N
        localServerBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                localServerBrowseButtonActionPerformed(evt);
            }
        });

        jLabel1.setText(org.openide.util.NbBundle.getMessage(LocalInstanceComponent.class, "LocalInstanceComponent.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(localServerLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                    .addComponent(localServerComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 294, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(localServerBrowseButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(localServerLabel)
                    .addComponent(localServerComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(localServerBrowseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void localServerBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localServerBrowseButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        Object item = localServerComboBox.getEditor().getItem();
        if (item != null && item.toString().trim().length() > 0) {
            chooser.setSelectedFile(new File(item.toString()));
        }
        if (chooser.showOpenDialog(SwingUtilities.getWindowAncestor(this)) == JFileChooser.APPROVE_OPTION) {
            localServerComboBox.getEditor().setItem(chooser.getSelectedFile().getAbsolutePath());
            support.fireChange();
        }
    }//GEN-LAST:event_localServerBrowseButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton localServerBrowseButton;
    private javax.swing.JComboBox localServerComboBox;
    private javax.swing.JLabel localServerLabel;
    // End of variables declaration//GEN-END:variables
}
