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
package org.netbeans.modules.maven.j2ee;

import java.io.IOException;
import org.netbeans.api.j2ee.core.Profile;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.common.dd.DDHelper;
import org.netbeans.modules.maven.j2ee.web.WebModuleImpl;
import org.netbeans.modules.maven.j2ee.web.WebModuleProviderImpl;
import org.netbeans.spi.project.AuxiliaryProperties;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 * Provides a various methods to help with typical Maven Projects requirements
 * 
 * @author Martin Janicek
 */
public class MavenProjectSupport {

    private MavenProjectSupport() {
    }
    
    
    public static void createDDIfRequired(Project project) {
        createDDIfRequired(project, null);
    }
    
    /**
     * Creates web.xml deployment descriptor if it's required for given project (this method was created as a
     * workaround for issue #204572 and probably won't be needed when WebLogic issue will be fixed)
     * 
     * @param project project for which should DD be generated
     * @param serverID server ID of given project
     */
    public static void createDDIfRequired(Project project, String serverID) {
        if (serverID == null) {
            serverID = readServerID(project);
        }
        // TODO change condition to use ConfigSupportImpl.isDescriptorRequired
        if (serverID != null && serverID.contains("WebLogic")) {
            createDD(project);
        }
    }
    
    private static String readServerID(Project project) {
        AuxiliaryProperties props = project.getLookup().lookup(AuxiliaryProperties.class);
        return props.get(MavenJavaEEConstants.HINT_DEPLOY_J2EE_SERVER, false);
    }
    
    private static void createDD(Project project) {
        WebModuleProviderImpl webModule = project.getLookup().lookup(WebModuleProviderImpl.class);
        
        if (webModule != null) {
            WebModuleImpl webModuleImpl = webModule.getWebModuleImplementation();
            try {
                FileObject webInf = webModuleImpl.getWebInf();
                if (webInf == null) {
                    webInf = webModuleImpl.createWebInf();
                }
                FileObject webXml = webModuleImpl.getDeploymentDescriptor();
                if (webXml == null) {
                    AuxiliaryProperties props = project.getLookup().lookup(AuxiliaryProperties.class);
                    String j2eeVersion = props.get(MavenJavaEEConstants.HINT_J2EE_VERSION, false);
                    webXml = DDHelper.createWebXml(Profile.fromPropertiesString(j2eeVersion), webInf);
                }

                assert webXml != null; // this should never happend if there a valid j2eeVersion was parsed
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
