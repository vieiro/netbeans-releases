/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.project.ui;

import java.awt.Component;
import java.beans.BeanInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.libraries.Library;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Dusan Balek
 */
public class MoveToModulePathPanelGUI extends javax.swing.JPanel {

    private static String RESOURCE_ICON_JAR = "org/netbeans/modules/java/api/common/project/ui/resources/jar.gif"; //NOI18N
    private static String RESOURCE_ICON_LIBRARY = "org/netbeans/modules/java/api/common/project/ui/resources/libraries.gif"; //NOI18N
    private static String RESOURCE_ICON_ARTIFACT = "org/netbeans/modules/java/api/common/project/ui/resources/projectDependencies.gif"; //NOI18N                

    public static ImageIcon ICON_JAR = ImageUtilities.loadImageIcon(RESOURCE_ICON_JAR, false);
    public static ImageIcon ICON_LIBRARY = ImageUtilities.loadImageIcon(RESOURCE_ICON_LIBRARY, false);
    public static ImageIcon ICON_ARTIFACT = ImageUtilities.loadImageIcon(RESOURCE_ICON_ARTIFACT, false);
    public static ImageIcon ICON_FOLDER = null;

    private final List<ChangeListener> listeners = new ArrayList<>();

    /**
     * Creates new form MoveToModulePathPanelGUI
     */
    public MoveToModulePathPanelGUI() {

        initComponents();

        table.getDefaultEditor(Boolean.class).addCellEditorListener(new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                fireChange();
            }
            @Override
            public void editingCanceled(ChangeEvent e) {
            }
        });
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
                if (value != null) {
                    assert value instanceof NewJavaFileWizardIterator.ClassPathItem : value.getClass().toString();
                    NewJavaFileWizardIterator.ClassPathItem item = (NewJavaFileWizardIterator.ClassPathItem)value;
                    setIcon(getIcon(item));
                    setToolTipText(item.getURL().toExternalForm());
                    return super.getTableCellRendererComponent(table, getDisplayName(item), isSelected, false, row, column);
                } else {
                    setIcon(null);
                    return super.getTableCellRendererComponent(table, null, isSelected, false, row, column);
                }
            }
        
            private String getDisplayName(NewJavaFileWizardIterator.ClassPathItem item) {
                AntArtifact aa = item.asAntArtifact();
                if (aa != null) {
                    String projectName = ProjectUtils.getInformation(aa.getProject()).getDisplayName();
                    return NbBundle.getMessage(MoveToModulePathPanelGUI.class, "MSG_ProjectArtifactFormat", new Object[] {
                        projectName,
                        aa.getArtifactLocation().toString()
                    });
                }
                Project p = item.asProject();
                if (p != null) {
                    return ProjectUtils.getInformation(p).getDisplayName();
                }
                Library l = item.asLibrary();
                if (l != null) {
                    return l.getDisplayName();
                }
                File f = item.asFile();
                if (f != null) {
                    return f.getPath();
                }
                return item.getURL().toExternalForm();
            }

            private Icon getIcon(NewJavaFileWizardIterator.ClassPathItem item) {
                AntArtifact aa = item.asAntArtifact();
                if (aa != null) {
                    Project p = aa.getProject();
                    if (p != null) {
                        return ProjectUtils.getInformation(p).getIcon();
                    }
                    return ICON_ARTIFACT;
                }
                Project p = item.asProject();
                if (p != null) {
                    return ProjectUtils.getInformation(p).getIcon();
                }
                Library l = item.asLibrary();
                if (l != null) {
                    return ICON_LIBRARY;
                }
                File f = item.asFile();
                if (f != null) {
                    return f.isDirectory() ? getFolderIcon() : ICON_JAR;
                }
                return null;
            }

        });
        table.setRowHeight(table.getRowHeight() + 3);
        setName(NbBundle.getBundle(MoveToModulePathPanelGUI.class).getString("LBL_MoveToModulePathPanelGUI_Name")); //NOI18N
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
        table = new javax.swing.JTable();

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Classpath entries", "Move to modulepath"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(MoveToModulePathPanelGUI.class, "MoveToModulePathPanelGUI.table.columnModel.title0")); // NOI18N
            table.getColumnModel().getColumn(1).setResizable(false);
            table.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(MoveToModulePathPanelGUI.class, "MoveToModulePathPanelGUI.table.columnModel.title1")); // NOI18N
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    public void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }

    public void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }

    private void fireChange() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener l : listeners) {
            l.stateChanged(e);
        }
    }

    void setCPItems(Iterable<NewJavaFileWizardIterator.ClassPathItem> cpItems) {
        for (int i = table.getRowCount() -1; i >= 0 ; i--) {
            ((DefaultTableModel) table.getModel()).removeRow(i);            
        }
        for (NewJavaFileWizardIterator.ClassPathItem cpItem : cpItems) {
            ((DefaultTableModel) table.getModel()).addRow(new Object[]{cpItem, true});
        }
    }

    public Iterable<NewJavaFileWizardIterator.ClassPathItem> getCPItemsToMove() {
        List<NewJavaFileWizardIterator.ClassPathItem> itemsToMove = new ArrayList<>();
        for (int i = 0; i < table.getModel().getRowCount(); i++) {
            if ((boolean) table.getModel().getValueAt(i, 1)) {
                itemsToMove.add((NewJavaFileWizardIterator.ClassPathItem) table.getModel().getValueAt(i, 0));
            }
        }
        return itemsToMove;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables

    private static ImageIcon getFolderIcon() {
        if (ICON_FOLDER == null) {
            DataFolder dataFolder = DataFolder.findFolder(FileUtil.getConfigRoot());
            ICON_FOLDER = new ImageIcon(dataFolder.getNodeDelegate().getIcon(BeanInfo.ICON_COLOR_16x16));
        }
        return ICON_FOLDER;
    }
}
