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


package org.netbeans.modules.maven.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.traversal.DependencyNodeVisitor;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.api.validation.adapters.DialogDescriptorAdapter;
import org.netbeans.api.validation.adapters.NotificationLineSupportAdapter;
import org.netbeans.modules.maven.dependencies.CheckNode;
import org.netbeans.modules.maven.dependencies.CheckNodeListener;
import org.netbeans.modules.maven.dependencies.CheckRenderer;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationListener;
import org.openide.DialogDescriptor;
import org.openide.NotificationLineSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author mkleint
 */
public class CreateLibraryPanel extends javax.swing.JPanel {
    private DependencyNode rootnode;
    private NotificationLineSupport line;
    private DialogDescriptor dd;
    private ValidationGroup vg;

    /** Creates new form CreateLibraryPanel */
    CreateLibraryPanel(DependencyNode root) {
        initComponents();
        DefaultComboBoxModel mdl = new DefaultComboBoxModel();
        txtName.putClientProperty(ValidationListener.CLIENT_PROP_NAME, NbBundle.getMessage(CreateLibraryPanel.class, "NAME_Library"));

        for (LibraryManager manager : LibraryManager.getOpenManagers()) {
            mdl.addElement(manager);
        }
        comManager.setModel(mdl);
        comManager.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (vg != null) vg.validateAll();
            }

        });
        comManager.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                LibraryManager man = (LibraryManager) value;
                return super.getListCellRendererComponent(list, man.getDisplayName(), index, isSelected, cellHasFocus);
            }

        });
        trDeps.setCellRenderer(new CheckRenderer(false));
        CheckNodeListener l = new CheckNodeListener(false);
        trDeps.addMouseListener(l);
        trDeps.addKeyListener(l);
        trDeps.setToggleClickCount(0);
        trDeps.setRootVisible(false);
        trDeps.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        rootnode = root;
        trDeps.setModel(new DefaultTreeModel(createDependenciesList()));
        setLibraryName();
    }

    @SuppressWarnings("unchecked")
    void createValidations(DialogDescriptor dd) {
        line = dd.createNotificationLineSupport();
        this.dd = dd;
        vg = ValidationGroup.create(new NotificationLineSupportAdapter(line), new DialogDescriptorAdapter(dd));
        vg.add(txtName,
                    Validators.merge(true,
                        Validators.REQUIRE_NON_EMPTY_STRING,
//                        Validators.REQUIRE_VALID_FILENAME,
                        new LibraryNameExists()
                    ));
    }

    private TreeNode createDependenciesList() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(null, true);
        Visitor vis = new Visitor(root);
        rootnode.accept(vis);
        return root;
    }

    private void setLibraryName() {
        LibraryManager manager = (LibraryManager) comManager.getSelectedItem();
        String currentName = getLibraryName();
        int index = 0;
        while (currentName.trim().length() == 0 || manager.getLibrary(currentName.trim()) != null) {
            currentName = rootnode.getArtifact().getArtifactId();
            if (index > 0) {
                currentName = currentName + index;
            }
            index++;
        }
        if (!currentName.equals(getLibraryName())) {
            txtName.setText(currentName);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblManager = new javax.swing.JLabel();
        comManager = new javax.swing.JComboBox();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        cbCopy = new javax.swing.JCheckBox();
        lblIncludes = new javax.swing.JLabel();
        cbJavadocSource = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        trDeps = new javax.swing.JTree();

        lblManager.setLabelFor(comManager);
        org.openide.awt.Mnemonics.setLocalizedText(lblManager, org.openide.util.NbBundle.getMessage(CreateLibraryPanel.class, "CreateLibraryPanel.lblManager.text")); // NOI18N

        comManager.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblName.setLabelFor(txtName);
        org.openide.awt.Mnemonics.setLocalizedText(lblName, org.openide.util.NbBundle.getMessage(CreateLibraryPanel.class, "CreateLibraryPanel.lblName.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cbCopy, org.openide.util.NbBundle.getMessage(CreateLibraryPanel.class, "CreateLibraryPanel.cbCopy.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblIncludes, org.openide.util.NbBundle.getMessage(CreateLibraryPanel.class, "CreateLibraryPanel.lblIncludes.text")); // NOI18N

        cbJavadocSource.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(cbJavadocSource, org.openide.util.NbBundle.getMessage(CreateLibraryPanel.class, "CreateLibraryPanel.cbJavadocSource.text")); // NOI18N

        jScrollPane1.setViewportView(trDeps);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cbCopy, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                    .addComponent(cbJavadocSource, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblManager)
                            .addComponent(lblName))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                            .addComponent(comManager, 0, 267, Short.MAX_VALUE)))
                    .addComponent(lblIncludes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblManager)
                    .addComponent(comManager, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbCopy)
                .addGap(25, 25, 25)
                .addComponent(lblIncludes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbJavadocSource)
                .addGap(16, 16, 16))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbCopy;
    private javax.swing.JCheckBox cbJavadocSource;
    private javax.swing.JComboBox comManager;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblIncludes;
    private javax.swing.JLabel lblManager;
    private javax.swing.JLabel lblName;
    private javax.swing.JTree trDeps;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables

    LibraryManager getLibraryManager() {
        return (LibraryManager) comManager.getSelectedItem();
    }

    String getLibraryName() {
        String name = txtName.getText().trim();
//        if (name.length() == 0) {
//            name = "library"; //NOI18N
//        }
        return name;
    }

    boolean isAllSourceAndJavadoc() {
        return cbJavadocSource.isSelected();
    }

    String getCopyDirectory() {
        if (!cbCopy.isSelected()) {
            return null;
        }
        String dir = getLibraryName();
        return dir;
    }

    List<Artifact> getIncludeArtifacts() {
        Object root = trDeps.getModel().getRoot();
        int count = trDeps.getModel().getChildCount(root);
        List<Artifact> toRet = new ArrayList<Artifact>();
        for (int i =0; i < count; i++) {
            CheckNode chn = (CheckNode) trDeps.getModel().getChild(root, i);
            if (chn.isSelected()) {
                Artifact art = (Artifact) chn.getUserObject();
                toRet.add(art);
            }
        }
        return toRet;
    }

    private static int getScopeOrder(String scope) {
        if (scope == null) {
            return 10;
        }
        if (scope.equals(Artifact.SCOPE_COMPILE)) {
            return 5;
        }
        if (scope.equals(Artifact.SCOPE_RUNTIME)) {
            return 4;
        }
        if (scope.equals(Artifact.SCOPE_TEST)) {
            return 3;
        }
        return 0;
    }


    private static class Comp implements Comparator<Artifact> {

        public int compare(Artifact a1, Artifact a2) {
            String scope1 = a1.getScope();
            String scope2 = a2.getScope();
            int order1 = getScopeOrder(scope1);
            int order2 = getScopeOrder(scope2);
            if (order1 > order2) {
                return -1;
            }
            if (order2 > order1) {
                return 1;
            }
            int trail1 = a1.getDependencyTrail().size();
            int trail2 = a2.getDependencyTrail().size();
            if (trail1 > trail2) {
                return -1;
            }
            if (trail2 > trail1) {
                return 1;
            }

            return a1.getId().compareTo(a2.getId());
        }
    }

    private class LibraryNameExists implements Validator<String> {
        public boolean validate(Problems problems, String compName, String model) {
            LibraryManager manager = (LibraryManager) comManager.getSelectedItem();
            String currentName = model.trim();
            if (manager.getLibrary(currentName) != null) {
                problems.add(NbBundle.getMessage(CreateLibraryPanel.class, "ERR_NameExists"));
                return false;
            }
            return true;
        }
    }


    private class Visitor implements DependencyNodeVisitor {

        private DefaultMutableTreeNode rootNode;
        private DependencyNode root;
        private Stack<DependencyNode> path;
        private Icon icn = ImageUtilities.image2Icon(ImageUtilities.loadImage("org/netbeans/modules/maven/TransitiveDependencyIcon.png", true)); //NOI18N
        private Icon icn2 = ImageUtilities.image2Icon(ImageUtilities.loadImage("org/netbeans/modules/maven/DependencyIcon.png", true)); //NOI18N

        Visitor(DefaultMutableTreeNode root) {
            this.rootNode = root;
        }

        public boolean visit(DependencyNode node) {
            if (root == null) {
                root = node;
                path = new Stack<DependencyNode>();
                Artifact rootA = node.getArtifact();
                String label = rootA.getGroupId() + ":" + rootA.getArtifactId();
                CheckNode nd = new CheckNode(rootA, label, icn2);
                nd.setSelected(true);
                rootNode.add(nd);
                return true;
            }
            if (node.getState() == DependencyNode.INCLUDED) {
                Artifact a = node.getArtifact();
                String label = a.getGroupId() + ":" + a.getArtifactId() + " [" + a.getScope() + "]";
                CheckNode nd = new CheckNode(a, label, path.size() > 0 ? icn : icn2);
                nd.setSelected(getScopeOrder(a.getScope()) > 3); //don't include tests and provided/system items
                rootNode.add(nd);

            }
            path.push(node);
            return true;
        }

        public boolean endVisit(DependencyNode node) {
            if (root == node) {
                root = null;
                path = null;
                return true;
            }
            path.pop();
            return true;
        }
    }
}
