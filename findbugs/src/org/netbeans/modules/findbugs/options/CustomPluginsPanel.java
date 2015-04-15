/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.findbugs.options;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import org.netbeans.api.progress.BaseProgressUtils;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressRunnable;
import org.netbeans.modules.findbugs.DetectorCollectionProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author lahvac
 */
public class CustomPluginsPanel extends javax.swing.JPanel {

    public CustomPluginsPanel(List<String> plugins) {
        initComponents();
        
        DefaultListModel dlm = (DefaultListModel) pluginsList.getModel();
        
        for (String plugin : plugins) {
            dlm.addElement(plugin);
        }
        
        enableDisable();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        pluginsList = new javax.swing.JList();
        addPlugin = new javax.swing.JButton();
        removePlugin = new javax.swing.JButton();

        pluginsList.setModel(new DefaultListModel());
        pluginsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                pluginsListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(pluginsList);

        org.openide.awt.Mnemonics.setLocalizedText(addPlugin, org.openide.util.NbBundle.getMessage(CustomPluginsPanel.class, "CustomPluginsPanel.addPlugin.text")); // NOI18N
        addPlugin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPluginActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(removePlugin, org.openide.util.NbBundle.getMessage(CustomPluginsPanel.class, "CustomPluginsPanel.removePlugin.text")); // NOI18N
        removePlugin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePluginActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(addPlugin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(removePlugin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addPlugin)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(removePlugin)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    @Messages({"BTN_Select=Select",
        "MSG_Checking=Checking validity of the plugins",
        "# {0} - the name of the file",
        "MSG_Invalid_Plugin=The plugin \"{0}\" is not valid findbugs plugin.",
        "MSG_Multiple_Invalid_Plugins=There are multiple invalid plugins in the selection."})
    private void addPluginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPluginActionPerformed
        JFileChooser fc = new JFileChooser();
        
        fc.setApproveButtonText(Bundle.BTN_Select());
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(true);
        
        if (fc.showDialog(fc, null) == JFileChooser.APPROVE_OPTION) {
            final List<String> paths = new ArrayList<>();
            for (File f : fc.getSelectedFiles()) {
                paths.add(f.getAbsolutePath());
            }
            List<String> errors = BaseProgressUtils.showProgressDialogAndRun(new ProgressRunnable<List<String>>() {

                @Override
                public List<String> run(ProgressHandle handle) {
                    return DetectorCollectionProvider.checkTemporaryCollection(paths);
                }
            }, Bundle.MSG_Checking(), false);

            if (errors.isEmpty()) {
                for (String path : paths) {
                    ((DefaultListModel) pluginsList.getModel()).addElement(path);
                }
            } else {
                if (errors.size() == 1) {
                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                            Bundle.MSG_Invalid_Plugin(errors.get(0)), NotifyDescriptor.ERROR_MESSAGE));
                } else {
                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                            Bundle.MSG_Multiple_Invalid_Plugins(), NotifyDescriptor.ERROR_MESSAGE));
                }
            }
        }
        enableDisable();
    }//GEN-LAST:event_addPluginActionPerformed

    private void removePluginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removePluginActionPerformed
        DefaultListModel dlm = (DefaultListModel) pluginsList.getModel();
        int originalSelection = pluginsList.getSelectedIndex();
        
        for (Object toRemove : pluginsList.getSelectedValues()) {
            dlm.removeElement(toRemove);
        }
        
        int toSelect = Math.min(originalSelection, dlm.getSize() - 1);
        
        if (toSelect >= 0) pluginsList.setSelectedIndex(toSelect);
        enableDisable();
    }//GEN-LAST:event_removePluginActionPerformed

    private void pluginsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_pluginsListValueChanged
        enableDisable();
    }//GEN-LAST:event_pluginsListValueChanged

    private void enableDisable() {
        removePlugin.setEnabled(pluginsList.getSelectedIndex() >= 0);
    }
    
    public List<String> getPlugins() {
        DefaultListModel dlm = (DefaultListModel) pluginsList.getModel();
        List<String> result = new ArrayList<String>(dlm.getSize());
        
        for (int i = 0; i < dlm.getSize(); i++) {
            result.add((String) dlm.get(i));
        }
        
        return result;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPlugin;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList pluginsList;
    private javax.swing.JButton removePlugin;
    // End of variables declaration//GEN-END:variables
}
