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
package org.netbeans.modules.java.j2seproject.ui.wizards;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import org.netbeans.modules.java.api.common.classpath.ClassPathSupport;
import org.netbeans.modules.java.api.common.project.ui.customizer.ClassPathListCellRenderer;
import org.netbeans.modules.java.j2seproject.J2SEProject;
import org.openide.util.NbBundle;

/**
 *
 * @author Dusan Balek
 */
public class MoveToModulePathPanelGUI extends javax.swing.JPanel {

    private final List<ChangeListener> listeners = new ArrayList<>();

    /**
     * Creates new form MoveToModulePathPanelGUI
     */
    public MoveToModulePathPanelGUI(J2SEProject project) {

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
        table.setDefaultRenderer(Object.class, ClassPathListCellRenderer.createClassPathTableRenderer(project.evaluator(), project.getProjectDirectory()));        
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

    void setCPItems(Iterable<ClassPathSupport.Item> cpItems) {
        for (int i = table.getRowCount() -1; i >= 0 ; i--) {
            ((DefaultTableModel) table.getModel()).removeRow(i);            
        }
        for (ClassPathSupport.Item cpItem : cpItems) {
            ((DefaultTableModel) table.getModel()).addRow(new Object[]{cpItem, true});
        }
    }

    public Iterable<ClassPathSupport.Item> getCPItemsToMove() {
        List<ClassPathSupport.Item> itemsToMove = new ArrayList<>();
        for (int i = 0; i < table.getModel().getRowCount(); i++) {
            if ((boolean) table.getModel().getValueAt(i, 1)) {
                itemsToMove.add((ClassPathSupport.Item) table.getModel().getValueAt(i, 0));
            }
        }
        return itemsToMove;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables

}
