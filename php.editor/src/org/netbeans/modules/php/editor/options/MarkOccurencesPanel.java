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

package org.netbeans.modules.php.editor.options;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JCheckBox;
import org.openide.util.Exceptions;

/**
 *
 * @author  Petr Hrebejk
 */
public class MarkOccurencesPanel extends javax.swing.JPanel {

    private static final boolean DEFAULT_VALUE = true; // May need to be splited if the defaunts ar not all on
    private List<JCheckBox> boxes;
    private MarkOccurencesOptionsPanelController controller;

    /* Creates new form MarkOccurencesPanel */
    public MarkOccurencesPanel(MarkOccurencesOptionsPanelController controller) {
        initComponents();
        fillBoxes();
        addListeners();
        load(controller);
    }

    public void load(MarkOccurencesOptionsPanelController controller) {
        this.controller = controller;

        Preferences node = MarkOccurencesSettings.getCurrentNode();

        for (JCheckBox box : boxes) {
            box.setSelected(node.getBoolean(box.getActionCommand(), DEFAULT_VALUE));
        }

        componentsSetEnabled();

    }

    public void store() {
        Preferences node = MarkOccurencesSettings.getCurrentNode();
        for (javax.swing.JCheckBox box : boxes) {
            boolean value = box.isSelected();
            boolean original = node.getBoolean(box.getActionCommand(),
                                               DEFAULT_VALUE);

            if (value != original) {
                node.putBoolean(box.getActionCommand(), value);
            }
        }
        try {
            node.flush();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public boolean changed() {
        Preferences node = MarkOccurencesSettings.getCurrentNode();
        for (JCheckBox box : boxes) {
            boolean value = box.isSelected();
            boolean original = node.getBoolean(box.getActionCommand(), DEFAULT_VALUE);
            if (value != original) {
                return true;
            }
        }
        return false;
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        onOffCheckBox = new javax.swing.JCheckBox();
        keepMarksCheckBox = new javax.swing.JCheckBox();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        setFocusCycleRoot(true);
        setFocusTraversalPolicy(new java.awt.FocusTraversalPolicy() {
            public java.awt.Component getDefaultComponent(java.awt.Container focusCycleRoot){
                return onOffCheckBox;
            }//end getDefaultComponent

            public java.awt.Component getFirstComponent(java.awt.Container focusCycleRoot){
                return onOffCheckBox;
            }//end getFirstComponent

            public java.awt.Component getLastComponent(java.awt.Container focusCycleRoot){
                return onOffCheckBox;
            }//end getLastComponent

            public java.awt.Component getComponentAfter(java.awt.Container focusCycleRoot, java.awt.Component aComponent){
                return onOffCheckBox;//end getComponentAfter
            }
            public java.awt.Component getComponentBefore(java.awt.Container focusCycleRoot, java.awt.Component aComponent){
                return onOffCheckBox;//end getComponentBefore

            }}
        );

        org.openide.awt.Mnemonics.setLocalizedText(onOffCheckBox, org.openide.util.NbBundle.getMessage(MarkOccurencesPanel.class, "CTL_OnOff_CheckBox")); // NOI18N
        onOffCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        keepMarksCheckBox.setMnemonic('s');
        org.openide.awt.Mnemonics.setLocalizedText(keepMarksCheckBox, org.openide.util.NbBundle.getMessage(MarkOccurencesPanel.class, "MarkOccurencesPanel.keepMarksCheckBox.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(onOffCheckBox))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(keepMarksCheckBox)))
                .addContainerGap(226, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(onOffCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(keepMarksCheckBox)
                .addContainerGap(237, Short.MAX_VALUE))
        );

        onOffCheckBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(MarkOccurencesPanel.class, "MarkOccurrencesPanel.onOffCheckBox.AccessibleContext.accessibleName")); // NOI18N
        onOffCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MarkOccurencesPanel.class, "ACSD_OnOff_CB")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(MarkOccurencesPanel.class, "MarkOccurrencesPanel.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MarkOccurencesPanel.class, "MarkOccurrencesPanel.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox keepMarksCheckBox;
    private javax.swing.JCheckBox onOffCheckBox;
    // End of variables declaration//GEN-END:variables
    // End of variables declaration

    private void fillBoxes() {
        boxes = new ArrayList<>();
        boxes.add(onOffCheckBox);
        boxes.add(keepMarksCheckBox);
        onOffCheckBox.setActionCommand(MarkOccurencesSettings.ON_OFF);
        keepMarksCheckBox.setActionCommand(MarkOccurencesSettings.KEEP_MARKS);
    }

    private void addListeners() {
        ItemListener itemListener = new CheckItemListener();
        for (JCheckBox box : boxes) {
            box.addItemListener(itemListener);
        }
    }

    private void componentsSetEnabled() {
        for (int i = 1; i < boxes.size(); i++) {
            boxes.get(i).setEnabled(onOffCheckBox.isSelected()); // Switch off the other boxes
        }
    }

    private class CheckItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent evt) {
            if (evt.getSource() == onOffCheckBox) {
                componentsSetEnabled();
            }
            controller.changed();
        }

    }

}
