/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 */
package org.netbeans.modules.db.explorer;

import java.awt.Dimension;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.lib.ddl.CommandNotSupportedException;
import org.netbeans.lib.ddl.DDLException;
import org.netbeans.lib.ddl.impl.AbstractCommand;
import org.netbeans.lib.ddl.impl.Specification;
import org.netbeans.modules.db.explorer.node.TableNode;
import org.netbeans.modules.db.metadata.model.api.Action;
import org.netbeans.modules.db.metadata.model.api.ForeignKey;
import org.netbeans.modules.db.metadata.model.api.ForeignKeyColumn;
import org.netbeans.modules.db.metadata.model.api.Metadata;
import org.netbeans.modules.db.metadata.model.api.MetadataElementHandle;
import org.netbeans.modules.db.metadata.model.api.MetadataModelException;
import org.netbeans.modules.db.metadata.model.api.Table;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 * Extendend Delete for Tabels.
 * 
 * Basic idea:
 * 
 * 1. Group databases to be deleted by connection
 * 2. Per connection establish a topolical order (if possible)
 * 3. Delete nodes in order, so that dependent tables are removed before their
 *    dependecies
 */
public class TableExtendedDelete {

    private static class TopolocationSortResult {
        public final List<TableNode> list;
        public final boolean isCyclic;
        
        public TopolocationSortResult(List<TableNode> list, boolean isCyclic) {
            this.list = list;
            this.isCyclic = isCyclic;
        }
    }
    
    private static final Logger LOG = Logger.getLogger(TableExtendedDelete.class.getName());

    private static void retainOnlyLocalDependencies(Map<MetadataElementHandle, Set<MetadataElementHandle>> dependencies) {
        Set<MetadataElementHandle> keys = dependencies.keySet();
        for (Entry<MetadataElementHandle, Set<MetadataElementHandle>> e : dependencies.entrySet()) {
            e.getValue().retainAll(keys);
        }
    }
    
    private static TopolocationSortResult getTopologicalSort(final DatabaseConnection dbconn, final List<TableNode> tableNodes) {
        boolean isCyclic = false;
        final Map<MetadataElementHandle, Set<MetadataElementHandle>> dependencies = new HashMap<>();
        try {
            dbconn.getMetadataModel().runReadAction(
                    new Action<Metadata>() {
                        @Override
                        public void run(Metadata metaData) {
                            for (TableNode tn : tableNodes) {
                                Table t = tn.getTableHandle().resolve(metaData);
                                Set<MetadataElementHandle> deps = new HashSet<>();
                                for (ForeignKey k : t.getForeignKeys()) {
                                    for (ForeignKeyColumn fkc : k.getColumns()) {
                                        deps.add(MetadataElementHandle.create(
                                                        fkc.getReferredColumn().getParent()
                                                ));
                                    }
                                }
                                dependencies.put(tn.getTableHandle(), deps);
                            }
                        }
                    });
        } catch (MetadataModelException ex) {
            LOG.warning(ex.getMessage());
        }

        retainOnlyLocalDependencies(dependencies);

        List<MetadataElementHandle> deleteOrder = new LinkedList<>();

        // Iterate over all dependencies - at each round the entry with
        // the smallest number of dependencies is removed, ideally all removed
        // elements should have zero dependencies - if not a cyclic dependency is
        // present - the design ensures that in this case no infinite loop is created
        while (dependencies.size() > 0) {
            int candidateSize = Integer.MAX_VALUE;
            MetadataElementHandle candidate = null;
            for (Entry<MetadataElementHandle, Set<MetadataElementHandle>> e2 : dependencies.entrySet()) {
                if (candidate == null || e2.getValue().size() < candidateSize) {
                    candidate = e2.getKey();
                    candidateSize = e2.getValue().size();
                }
            }
            deleteOrder.add(candidate);
            dependencies.remove(candidate);
            retainOnlyLocalDependencies(dependencies);
            if (candidateSize > 0) {
                isCyclic = true;
            }
        }

        Collections.reverse(deleteOrder);
        List<TableNode> deleteOrder2 = new LinkedList<>();

        for (MetadataElementHandle meh : deleteOrder) {
            for (TableNode tn : tableNodes) {
                if (tn.getTableHandle().equals(meh)) {
                    deleteOrder2.add(tn);
                    break;
                }
            }
        }
        return new TopolocationSortResult(deleteOrder2, isCyclic);
    }

    public static void delete(Node[] inputNodes) {
        List<String> errors = new LinkedList<>();
        Map<DatabaseConnection, List<TableNode>> nodes = new HashMap<>();
        
        // Assign Table Nodes to be deleted to the database connection
        for (Node n : inputNodes) {
            TableNode tn = n.getLookup().lookup(TableNode.class);
            if (tn != null) {
                DatabaseConnection connection = tn.getLookup().lookup(DatabaseConnection.class);
                if (nodes.get(connection) == null) {
                    nodes.put(connection, new LinkedList<TableNode>());
                }
                nodes.get(connection).add(tn);
            }
        }
        
        boolean isCyclic = false;

        // Foreach database connection, order table nodes in dependency order and delete
        for (final Entry<DatabaseConnection, List<TableNode>> e : nodes.entrySet()) {
            TopolocationSortResult deleteHelper = getTopologicalSort(e.getKey(), e.getValue());
            isCyclic |= deleteHelper.isCyclic;
            for (TableNode tn : deleteHelper.list) {
                DatabaseConnector connector = e.getKey().getConnector();
                Specification spec = connector.getDatabaseSpecification();

                try {
                    // Duplicate from TableNode#destroy, to customize error handling
                    AbstractCommand command = spec.createCommandDropTable(tn.getName());
                    String schemaName = tn.getSchemaName();
                    String catalogName = tn.getCatalogName();
                    if (schemaName == null) {
                        schemaName = catalogName;
                    }

                    command.setObjectOwner(schemaName);
                    command.execute();
                } catch (DDLException | CommandNotSupportedException ex) {
                    LOG.log(Level.INFO, "Error while deleting table " + tn.getName(), ex);
                    errors.add(NbBundle.getMessage(TableExtendedDelete.class,
                            "TableExtendedDelete_TableDeleteError", 
                            tn.getName(),
                            ex.getMessage()));
                }
            }
            ConnectionManager.getDefault().refreshConnectionInExplorer(e.getKey().getDatabaseConnection());
        }

        // If errors happend => report them and if cycle is detected, inform user
        if (errors.size() > 0) {
            StringBuilder sb = new StringBuilder();

            if (isCyclic) {
                sb.append(NbBundle.getMessage(
                        TableExtendedDelete.class, 
                        "TableExtendedDelete_CyclicDependency"));
            }

            for (int i = 0; i < errors.size(); i++) {
                if (i > 0) {
                    sb.append("\n\n");
                }
                sb.append(errors.get(i));
            }

            JTextArea output = new JTextArea(sb.toString());
            output.setLineWrap(true);
            output.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(output);
            scrollPane.setMinimumSize(new Dimension(600, 200));
            scrollPane.setPreferredSize(new Dimension(600, 200));
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            DialogDisplayer.getDefault().notifyLater(
                    new NotifyDescriptor.Message(
                            scrollPane,
                            NotifyDescriptor.ERROR_MESSAGE));
        }
    }
}
