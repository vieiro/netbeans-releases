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

package org.netbeans.modules.maven.j2ee.ui.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.j2ee.core.Profile;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule.Type;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.maven.j2ee.ui.Server;
import org.netbeans.modules.maven.j2ee.utils.MavenProjectSupport;

/**
 *
 * @author Martin Janicek
 */
public final class ServerUtils {

    private ServerUtils() {
    }


    public static Server findServer(Project project) {
        final Type moduleType = getModuleType(project);
        final String instanceID = MavenProjectSupport.readServerInstanceID(project);
        if (instanceID != null) {
            return findServerByInstance(moduleType, instanceID);
        }

        // Try to read serverID directly from pom.xml properties configration
        final String serverID = MavenProjectSupport.readServerID(project);
        if (serverID != null) {
            return findServerByType(moduleType, serverID);
        }

        return Server.NO_SERVER_SELECTED;
    }
    
    private static Type getModuleType(Project project) {
        J2eeModuleProvider moduleProvider = project.getLookup().lookup(J2eeModuleProvider.class);
        if (moduleProvider != null && moduleProvider.getJ2eeModule() != null) {
            return moduleProvider.getJ2eeModule().getType();
        }
        return null;
    }

    private static Server findServerByInstance(Type moduleType, String instanceId) {
        for (Server server : findServersFor(moduleType)) {
            if (instanceId.equals(server.getServerInstanceID())) {
                return server;
            }
        }
        return Server.NO_SERVER_SELECTED;
    }

    private static Server findServerByType(Type moduleType, String serverId) {
        for (Server server : findServersFor(moduleType)) {
            if (serverId.equals(server.getServerID())) {
                return server;
            }
        }
        return Server.NO_SERVER_SELECTED;
    }

    public static List<Server> findServersFor(Type moduleType) {
        return convertToList(Deployment.getDefault().getServerInstanceIDs(Collections.singleton(moduleType)));
    }

    private static List<Server> convertToList(String[] serverInstanceIDs) {
        final List<Server> servers = new ArrayList<Server>();
        for (String instanceID : serverInstanceIDs) {
            servers.add(new Server(instanceID));
        }

        // Sort the server list
        Collections.sort(servers);

        // We want to provide Maven project without server
        servers.add(Server.NO_SERVER_SELECTED);

        return servers;
    }
}
