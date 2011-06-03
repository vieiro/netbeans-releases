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

package org.netbeans.modules.profiler.j2ee.impl;

import org.netbeans.api.project.Project;
import org.netbeans.lib.profiler.client.ClientUtils;
import org.netbeans.lib.profiler.client.ClientUtils.SourceCodeSelection;
import org.netbeans.modules.profiler.j2ee.WebProjectUtils;
import org.netbeans.modules.profiler.projectsupport.utilities.ProjectUtilities;
import org.netbeans.modules.profiler.spi.project.ProjectProfilingSupport;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jaroslav Bachorik
 */
@ProjectServiceProvider(projectType={"org-netbeans-modules-web-project", "org-netbeans-modules-maven/war"}, service=ProjectProfilingSupport.class)
public class WebProfilingSupport implements ProjectProfilingSupport {
    private Project project;
    
    public WebProfilingSupport(Project project) {
        this.project = project;
    }

    @Override
    public String getFilter(boolean useSubprojects) {
        ClientUtils.SourceCodeSelection[] jspMethods = WebProjectUtils.getJSPRootMethods(project, useSubprojects); // TODO: needs to be computed also for subprojects!

        StringBuffer buffer = new StringBuffer(jspMethods.length * 30);

        if (jspMethods != null) {
            for (int i = 0; i < jspMethods.length; i++) {
                buffer.append(jspMethods[i].getClassName()).append(' '); // NOI18N
            }
        }
        return buffer.toString().trim();
    }

    @Override
    public SourceCodeSelection[] getRootMethods(FileObject profiledClassFile) {
        if (profiledClassFile == null) {
            // Profile Project, extract root methods from the project
            return WebProjectUtils.getJSPRootMethods(project, true); // TODO: needs to be computed also for subprojects!
        } else {
            // Profile Single, provide correct root methods
            if (WebProjectUtils.isJSP(profiledClassFile)) {
                // TODO: create list of jsp-specific methods (execute & all used Beans)
                return ProjectUtilities.getProjectDefaultRoots(project, new String[2][]);
            }
        }
        return new SourceCodeSelection[0];
    }

    @Override
    public boolean canProfileFile(FileObject fileObject) {
        return ((WebProjectUtils.isJSP(fileObject) && WebProjectUtils.isWebDocumentSource(fileObject, project)) // jsp from /web directory               
                  || (WebProjectUtils.isHttpServlet(fileObject) && WebProjectUtils.isWebJavaSource(fileObject, project)
                  && WebProjectUtils.isMappedServlet(fileObject, project, true))); // mapped servlet from /src directory
    }
}
