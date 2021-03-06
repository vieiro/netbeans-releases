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
package org.netbeans.modules.bugtracking.tasks;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JLabel;
import org.openide.util.NbBundle;

/**
 *
 * @author jpeska
 */
public class SortPanel extends javax.swing.JPanel {

    private final TaskSorter sorter = TaskSorter.getInstance();
    private final Set<SortAttributePanel> panels = new HashSet<SortAttributePanel>(NUMBER_OF_COMBOS);
    private static final int NUMBER_OF_COMBOS = 4;
    private List<TaskAttribute> attributes;
    private final SortingChangeListener listener;

    /**
     * Creates new form SortPanel
     */
    public SortPanel() {
        listener = new SortingChangeListener();
        initComponents();
        initCombos();
        pnlAttributes.add(new JLabel(), new GridBagConstraints(0, NUMBER_OF_COMBOS, 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0, 0, 3, 0), 0, 0));
    }

    public void saveAttributes() {
        sorter.setAttributes(attributes);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlAttributes = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        pnlAttributes.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, NbBundle.getMessage(SortPanel.class, "SortPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 10);
        pnlAttributes.add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, NbBundle.getMessage(SortPanel.class, "SortPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 10);
        pnlAttributes.add(jLabel2, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlAttributes, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlAttributes, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel pnlAttributes;
    // End of variables declaration//GEN-END:variables

    private void initCombos() {
        attributes = new ArrayList<TaskAttribute>(sorter.getAttributes());
        boolean previousHasRank = true;
        for (int i = 0; i < NUMBER_OF_COMBOS; i++) {
            TaskAttribute att = attributes.get(i);
            boolean hasRank = att.getRank() != TaskAttribute.NO_RANK;
            SortAttributePanel panel = new SortAttributePanel(attributes, hasRank ? att : null, i + 1);
            if (!previousHasRank) {
                panel.setEnabled(false);
            }
            previousHasRank = hasRank;
            panel.addSortingChangeListener(listener);
            pnlAttributes.add(panel, new GridBagConstraints(1, i, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 3, 0), 0, 0));
            panels.add(panel);
        }
    }

    private class SortingChangeListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Collections.sort(attributes);
            boolean previousHasRank = true;
            for (SortAttributePanel panel : panels) {
                panel.removeSortingChangeListener(listener);

                TaskAttribute selectCandidate = attributes.get(panel.getIndex() - 1);
                previousHasRank = selectCandidate.getRank() != TaskAttribute.NO_RANK;
                panel.updateModel(previousHasRank ? selectCandidate : null);

                if (panel.getIndex() > 1) {
                    TaskAttribute previousAttr = attributes.get(panel.getIndex() - 2);
                    panel.setComponentsEnabled(previousAttr.getRank() != TaskAttribute.NO_RANK);
                }

                panel.addSortingChangeListener(listener);
            }
        }

    }
}
