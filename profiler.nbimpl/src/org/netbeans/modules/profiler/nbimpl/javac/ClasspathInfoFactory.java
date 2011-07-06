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
package org.netbeans.modules.profiler.nbimpl.javac;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.project.Project;
import org.netbeans.modules.profiler.projectsupport.utilities.ProjectUtilities;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jaroslav Bachorik
 */
public class ClasspathInfoFactory {
    public static ClasspathInfo infoFor(Project prj) {
        return infoFor(prj, true);
    }
    
    public static ClasspathInfo infoFor(Project prj, final boolean includeSubprojects) {
        return infoFor(prj, includeSubprojects, true, true);
    }

    public static ClasspathInfo infoFor(Project prj, final boolean includeSubprojects,
                                        final boolean includeSources, final boolean includeLibraries) {
        FileObject[] sourceRoots = ProjectUtilities.getSourceRoots(prj, includeSubprojects);
        Set<FileObject> srcRootSet = new HashSet<FileObject>(sourceRoots.length);
        java.util.List<URL> urlList = new ArrayList<URL>();

        srcRootSet.addAll(Arrays.asList(sourceRoots));

        if (((sourceRoots == null) || (sourceRoots.length == 0)) && !includeSubprojects) {
            sourceRoots = ProjectUtilities.getSourceRoots(prj, true);
        }

        final ClassPath cpEmpty = ClassPathSupport.createClassPath(new FileObject[0]);

        if (sourceRoots.length == 0) {
            return null; // fail early
        }

        ClassPath cpSource = ClassPathSupport.createClassPath(sourceRoots);

        // cleaning up compile classpatth; we need to get rid off all project's class file references in the classpath
        ClassPath cpCompile = ClassPath.getClassPath(sourceRoots[0], ClassPath.COMPILE);

        for (ClassPath.Entry entry : cpCompile.entries()) {
            SourceForBinaryQuery.Result rslt = SourceForBinaryQuery.findSourceRoots(entry.getURL());
            FileObject[] roots = rslt.getRoots();

            if ((roots == null) || (roots.length == 0)) {
                urlList.add(entry.getURL());
            }
        }

        cpCompile = ClassPathSupport.createClassPath(urlList.toArray(new URL[urlList.size()]));

        return ClasspathInfo.create(includeLibraries ? ClassPath.getClassPath(sourceRoots[0], ClassPath.BOOT) : cpEmpty,
                includeLibraries ? cpCompile : cpEmpty, includeSources ? cpSource : cpEmpty);
    }
}
