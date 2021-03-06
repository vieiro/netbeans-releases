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
package org.netbeans.modules.php.phpunit.ui;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.netbeans.api.annotations.common.CheckForNull;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;
import org.openide.NotifyDescriptor;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;

/**
 * UI for selecting PhpUnit test groups.
 */
public final class PhpUnitTestGroupsPanel extends JPanel {

    private static final long serialVersionUID = 6576832132135L;

    // @GuardedBy("EDT")
    private final Set<String> selectedGroups;
    // @GuardedBy("EDT")
    private final GroupsTableModel tableModel;


    private PhpUnitTestGroupsPanel(List<String> allGroups, List<String> selectedGroups) {
        assert EventQueue.isDispatchThread();
        assert allGroups != null;
        assert selectedGroups != null;

        this.selectedGroups = new HashSet<>(selectedGroups);
        tableModel = new GroupsTableModel(allGroups, this.selectedGroups);

        initComponents();
        init();
    }

    @NbBundle.Messages("PhpUnitTestGroupsPanel.dialog.title=Test Groups")
    @CheckForNull
    public static List<String> showDialog(List<String> allGroups, List<String> selectedGroups) {
        final List<String> allGroupsCopy = new CopyOnWriteArrayList<>(allGroups);
        final List<String> selectedGroupsCopy = new CopyOnWriteArrayList<>(selectedGroups);
        return Mutex.EVENT.readAccess(new Mutex.Action<List<String>>() {
            @Override
            public List<String> run() {
                assert EventQueue.isDispatchThread();
                PhpUnitTestGroupsPanel panel = new PhpUnitTestGroupsPanel(allGroupsCopy, selectedGroupsCopy);
                NotifyDescriptor notifyDescriptor = new NotifyDescriptor(panel,
                        Bundle.PhpUnitTestGroupsPanel_dialog_title(),
                        NotifyDescriptor.OK_CANCEL_OPTION,
                        NotifyDescriptor.PLAIN_MESSAGE, null, NotifyDescriptor.OK_OPTION);
                NotificationLineSupport notificationLineSupport = notifyDescriptor.createNotificationLineSupport();
                panel.tableModel.addTableModelListener(new TestGroupsTableModelListener(notificationLineSupport));
                if (DialogDisplayer.getDefault().notify(notifyDescriptor) != NotifyDescriptor.OK_OPTION) {
                    return null;
                }
                return panel.getSelectedGroups();
            }
        });
    }

    @NbBundle.Messages("PhpUnitTestGroupsPanel.groups.select.all=Select all test groups")
    private void init() {
        assert EventQueue.isDispatchThread();
        selectAllTestGroupsCheckBox.setText(Bundle.PhpUnitTestGroupsPanel_groups_select_all());
        testGroupsTable.setTableHeader(null);
        testGroupsTable.setModel(tableModel);
        setFocusable(true);
    }

    private List<String> getSelectedGroups() {
        assert EventQueue.isDispatchThread();
        return new ArrayList<>(selectedGroups);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        testGroupsScrollPane = new javax.swing.JScrollPane();
        testGroupsTable = new javax.swing.JTable();
        selectTestGroupLabel = new javax.swing.JLabel();
        selectAllTestGroupsCheckBox = new javax.swing.JCheckBox();

        testGroupsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
        testGroupsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        testGroupsScrollPane.setViewportView(testGroupsTable);

        selectTestGroupLabel.setText(org.openide.util.NbBundle.getMessage(PhpUnitTestGroupsPanel.class, "PhpUnitTestGroupsPanel.selectTestGroupLabel.text")); // NOI18N

        selectAllTestGroupsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllTestGroupsCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(testGroupsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                    .addComponent(selectTestGroupLabel)
                    .addComponent(selectAllTestGroupsCheckBox, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(selectTestGroupLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(testGroupsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectAllTestGroupsCheckBox))
        );
    }// </editor-fold>//GEN-END:initComponents

    @NbBundle.Messages("PhpUnitTestGroupsPanel.groups.select.none=Select no test groups")
    private void selectAllTestGroupsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllTestGroupsCheckBoxActionPerformed
        assert EventQueue.isDispatchThread();
        boolean selectAllGroups;
        String label;
        if (selectAllTestGroupsCheckBox.isSelected()) {
            selectAllGroups = true;
            label = Bundle.PhpUnitTestGroupsPanel_groups_select_none();
        } else {
            selectAllGroups = false;
            label = Bundle.PhpUnitTestGroupsPanel_groups_select_all();
        }

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(selectAllGroups, i, 1);
        }
        selectAllTestGroupsCheckBox.setText(label);
    }//GEN-LAST:event_selectAllTestGroupsCheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox selectAllTestGroupsCheckBox;
    private javax.swing.JLabel selectTestGroupLabel;
    private javax.swing.JScrollPane testGroupsScrollPane;
    private javax.swing.JTable testGroupsTable;
    // End of variables declaration//GEN-END:variables

    //~ Inner classes

    private static final class GroupsTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 2346465465423157L;

        private final Class<?>[] types = new Class<?>[] {String.class, Boolean.class};

        // @GuardedBy("EDT")
        private final List<String> allGroups;
        // @GuardedBy("EDT")
        private final Set<String> selectedGroups;


        public GroupsTableModel(List<String> allGroups, Set<String> selectedGroups) {
            assert EventQueue.isDispatchThread();
            this.allGroups = allGroups;
            this.selectedGroups = selectedGroups;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return types[columnIndex];
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col == 0 ? false : true;
        }

        @Override
        public int getRowCount() {
            assert EventQueue.isDispatchThread();
            return allGroups.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            assert EventQueue.isDispatchThread();
            if (columnIndex == 0) {
                // name
                return allGroups.get(rowIndex);
            } else if (columnIndex == 1) {
                // selected or not?
                return selectedGroups.contains(allGroups.get(rowIndex));
            }
            throw new IllegalStateException("Unknown column index: " + columnIndex);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            assert EventQueue.isDispatchThread();
            if (columnIndex == 1) {
                Boolean selected = (Boolean) aValue;
                String group = allGroups.get(rowIndex);
                if (selected) {
                    selectedGroups.add(group);
                } else {
                    selectedGroups.remove(group);
                }
                fireTableCellUpdated(rowIndex, columnIndex);
                return;
            }
            throw new IllegalStateException("Unknown column index: " + columnIndex);
        }

    }

    private static final class TestGroupsTableModelListener implements TableModelListener {

        private final NotificationLineSupport notificationLineSupport;


        public TestGroupsTableModelListener(NotificationLineSupport notificationLineSupport) {
            this.notificationLineSupport = notificationLineSupport;
        }

        @Override
        @NbBundle.Messages("PhpUnitTestGroupsPanel.groups.selected.none=All tests will be executed.")
        public void tableChanged(TableModelEvent e) {
            TableModel tableModel = (TableModel) e.getSource();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Boolean isRowChecked = (Boolean) tableModel.getValueAt(i, 1);
                if (isRowChecked) {
                    notificationLineSupport.clearMessages();
                    return;
                }
            }
            notificationLineSupport.setInformationMessage(Bundle.PhpUnitTestGroupsPanel_groups_selected_none());
        }

    }

}
