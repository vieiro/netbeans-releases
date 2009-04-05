/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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

package org.netbeans.modules.db.explorer.action;

import java.util.logging.Logger;
import org.netbeans.lib.ddl.impl.Specification;
import org.netbeans.modules.db.explorer.DatabaseConnection;
import org.netbeans.modules.db.explorer.DatabaseConnector;
import org.netbeans.modules.db.explorer.node.SchemaNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Rob Englander
 */
public class MakeDefaultSchemaAction extends BaseAction {
    private static final Logger LOGGER = Logger.getLogger(MakeDefaultSchemaAction.class.getName());

    @Override
    public String getName() {
        return NbBundle.getMessage (MakeDefaultSchemaAction.class, "MakeDefaultSchema"); // NOI18N
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        boolean result = false;
        
        if (activatedNodes.length == 1) {
            SchemaNode node = activatedNodes[0].getLookup().lookup(SchemaNode.class);
            if (node != null) {
                DatabaseConnector connector = node.getLookup().lookup(DatabaseConnection.class).getConnector();
                result = connector.supportsCommand(Specification.DEFAULT_SCHEMA);
            }
        }

        return result;
    }

    @Override
    protected void performAction(final Node[] activatedNodes) {
        RequestProcessor.getDefault().post(
            new Runnable() {
                public void run() {
                    DatabaseConnection connection = activatedNodes[0].getLookup().lookup(DatabaseConnection.class);
                    String name = activatedNodes[0].getLookup().lookup(SchemaNode.class).getName();

                    try {
                        connection.setDefaultSchema(name);
                    } catch (Exception e) {
                        Exceptions.printStackTrace(e);
                    }
                }
            }
        );
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx(MakeDefaultSchemaAction.class);
    }
}
