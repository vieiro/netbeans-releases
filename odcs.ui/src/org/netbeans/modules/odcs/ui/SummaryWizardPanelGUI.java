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

package org.netbeans.modules.odcs.ui;

import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import org.openide.WizardDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;
import static org.netbeans.modules.odcs.ui.Bundle.*;
import org.netbeans.modules.odcs.ui.NewProjectWizardIterator.SharedItem;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Maros Sandor
 */
public class SummaryWizardPanelGUI extends javax.swing.JPanel {

    private SummaryWizardPanel panel;

    public SummaryWizardPanelGUI(SummaryWizardPanel pnl) {
        panel = pnl;
        initComponents();
    }

    @Override
    @Messages("SummaryWizardPanelGUI.panelName=Summary")
    public String getName() {
        return SummaryWizardPanelGUI_panelName();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new JLabel();
        projectCreatedLabel = new JLabel();
        jScrollPane1 = new JScrollPane();
        commitedItems = new JList();
        repoLabel = new JLabel();
        commitPrepareLabel = new JLabel();
        tobeSharedLabel = new JLabel();
        projectsMoveLabel = new JLabel();
        localRoot = new JLabel();

        Mnemonics.setLocalizedText(jLabel1, NbBundle.getMessage(SummaryWizardPanelGUI.class, "SummaryWizardPanelGUI.jLabel1.text")); // NOI18N

        Mnemonics.setLocalizedText(projectCreatedLabel, NbBundle.getMessage(SummaryWizardPanelGUI.class, "SummaryWizardPanelGUI.projectCreatedLabel.text")); // NOI18N

        jScrollPane1.setViewportView(commitedItems);

        Mnemonics.setLocalizedText(repoLabel, NbBundle.getMessage(SummaryWizardPanelGUI.class, "SummaryWizardPanelGUI.repoLabel.text")); // NOI18N

        Mnemonics.setLocalizedText(commitPrepareLabel, NbBundle.getMessage(SummaryWizardPanelGUI.class, "SummaryWizardPanelGUI.commitPrepareLabel.text")); // NOI18N

        tobeSharedLabel.setLabelFor(commitedItems);
        Mnemonics.setLocalizedText(tobeSharedLabel, NbBundle.getMessage(SummaryWizardPanelGUI.class, "SummaryWizardPanelGUI.tobeSharedLabel.text")); // NOI18N

        Mnemonics.setLocalizedText(projectsMoveLabel, NbBundle.getMessage(SummaryWizardPanelGUI.class, "SummaryWizardPanelGUI.projectsMoveLabel.text")); // NOI18N

        Mnemonics.setLocalizedText(localRoot, NbBundle.getMessage(SummaryWizardPanelGUI.class, "SummaryWizardPanelGUI.localRoot.text")); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(localRoot, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(commitPrepareLabel, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(projectsMoveLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jScrollPane1)
                .addGap(12, 12, 12))
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tobeSharedLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(projectCreatedLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(repoLabel, GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(projectCreatedLabel)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(tobeSharedLabel)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(projectsMoveLabel)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(localRoot)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(repoLabel)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(commitPrepareLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleName(NbBundle.getMessage(SummaryWizardPanelGUI.class, "SourceAndIssuesWizardPanelGUI.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SummaryWizardPanelGUI.class, "SourceAndIssuesWizardPanelGUI.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel commitPrepareLabel;
    private JList commitedItems;
    private JLabel jLabel1;
    private JScrollPane jScrollPane1;
    private JLabel localRoot;
    private JLabel projectCreatedLabel;
    private JLabel projectsMoveLabel;
    private JLabel repoLabel;
    private JLabel tobeSharedLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void addNotify() {
        super.addNotify();
        panel.fireChangeEvent();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        this.putClientProperty(NewProjectWizardIterator.PROP_EXC_ERR_MSG, null);
    }

    public void read(WizardDescriptor settings) {
        projectCreatedLabel.setText(NbBundle.getMessage(SummaryWizardPanelGUI.class, 
                "SummaryWizardPanelGUI.projectCreatedLabel.text",
                settings.getProperty(NewProjectWizardIterator.PROP_PRJ_NAME),
                panel.getServer().getDisplayName())
        );

        repoLabel.setText(NbBundle.getMessage(SummaryWizardPanelGUI.class, "SummaryWizardPanelGUI.repoLabel.text",
                settings.getProperty(NewProjectWizardIterator.PROP_SCM_TYPE)));
        commitPrepareLabel.setText(NbBundle.getMessage(SummaryWizardPanelGUI.class, "SummaryWizardPanelGUI.commitPrepareLabel.text"));

        List<SharedItem> sharedItems = (List<SharedItem>) settings.getProperty(NewProjectWizardIterator.PROP_FOLDERS_TO_SHARE);
        commitedItems.setListData(sharedItems.toArray(new SharedItem[sharedItems.size()]));

        commitPrepareLabel.setVisible(sharedItems.size() > 0);
//        repoLabel.setVisible(sharedItems.isEmpty());
        jScrollPane1.setVisible(sharedItems.size() > 0);
        tobeSharedLabel.setVisible(sharedItems.size() > 0);

        String newPrjScmLocal = (String) settings.getProperty(NewProjectWizardIterator.PROP_SCM_LOCAL);
//        boolean inPlaceRepository = NewProjectWizardIterator.isCommonParent(sharedItems, newPrjScmLocal);
//        projectsMoveLabel.setVisible(!inPlaceRepository);
        if (NewProjectWizardIterator.getSharedItemsToMove(settings).isEmpty()) {
            Mnemonics.setLocalizedText(projectsMoveLabel, NbBundle.getMessage(SummaryWizardPanelGUI.class, 
                    "SummaryWizardPanelGUI.projectsMoveLabelNoSelection.text"));
        }
//        localRoot.setVisible(!inPlaceRepository);
        localRoot.setText("<html><b>"+newPrjScmLocal+"</b></html>"); // NOI18N

        validate();
    }

    public void store(WizardDescriptor settings) {
    }
}
