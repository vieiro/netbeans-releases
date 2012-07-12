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

/*
 * KenaiFeatureCellRenderer.java
 *
 * Created on Mar 3, 2009, 1:15:30 PM
 */

package org.netbeans.modules.team.ods.ui.dashboard;

import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.domain.project.ProjectService;
import com.tasktop.c2c.server.scm.domain.ScmRepository;
import org.netbeans.modules.team.ods.ui.dashboard.GetSourcesFromCloudPanel.ScmRepositoryListItem;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import org.netbeans.modules.team.ods.ui.api.CloudUiServer;
import org.netbeans.modules.team.ui.common.ColorManager;
import org.netbeans.modules.team.ui.spi.ProjectHandle;
import org.openide.util.NbBundle;

/**
 *
 * @author Milan Kubec
 */
public class CloudServiceCellRenderer extends JPanel implements ListCellRenderer {

    public CloudServiceCellRenderer() {
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
        GridBagConstraints gridBagConstraints;

        projectNameLabel = new JLabel();
        projectRepoLabel = new JLabel();
        repoTypeLabel = new JLabel();

        setName("Form"); // NOI18N
        setLayout(new GridBagLayout());

        projectNameLabel.setText(NbBundle.getMessage(CloudServiceCellRenderer.class, "CloudServiceCellRenderer.projectNameLabel.text")); // NOI18N
        projectNameLabel.setName("projectNameLabel"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 4, 0, 0);
        add(projectNameLabel, gridBagConstraints);

        projectRepoLabel.setText(NbBundle.getMessage(CloudServiceCellRenderer.class, "CloudServiceCellRenderer.projectRepoLabel.text")); // NOI18N
        projectRepoLabel.setName("projectRepoLabel"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 4, 0, 0);
        add(projectRepoLabel, gridBagConstraints);

        repoTypeLabel.setText(NbBundle.getMessage(CloudServiceCellRenderer.class, "CloudServiceCellRenderer.repoTypeLabel.text")); // NOI18N
        repoTypeLabel.setName("repoTypeLabel"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 4, 0, 0);
        add(repoTypeLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel projectNameLabel;
    private JLabel projectRepoLabel;
    private JLabel repoTypeLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        if (value == null) {
            return new JLabel();
        }
        ProjectHandle<Project> projectHandle = ((ScmRepositoryListItem) value).projectHandle;
        ScmRepository repository = ((ScmRepositoryListItem) value).repository;

        if (repository != null) {
            if (index == -1) {
                projectNameLabel.setText(null);
                projectRepoLabel.setText(repository.getUrl());
                projectRepoLabel.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
                projectRepoLabel.setForeground(list.getForeground());
                repoTypeLabel.setText(null);
            } else {
                projectNameLabel.setText(projectHandle.getDisplayName() + " (" + projectHandle.getId() + ")"); // NOI18N
                projectNameLabel.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
                projectNameLabel.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
                projectRepoLabel.setText(repository.getUrl());
                projectRepoLabel.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
                projectRepoLabel.setForeground(isSelected ? list.getSelectionForeground() : ColorManager.getDefault().getLinkColor());
                repoTypeLabel.setText("(" + repository.getType().name() + ")"); // NOI18N
                repoTypeLabel.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
                repoTypeLabel.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            }
        }

        setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
        setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());

        return this;

    }

}
