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

package org.netbeans.modules.maven.execute.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.modules.maven.api.Constants;
import org.netbeans.modules.maven.api.execute.RunConfig;
import org.netbeans.modules.maven.api.execute.RunUtils;
import org.netbeans.modules.maven.debug.Utils;
import org.netbeans.modules.maven.execute.cmd.ExecMojo;
import org.netbeans.modules.maven.queries.MavenSourceJavadocAttacher;
import org.netbeans.modules.maven.queries.SourceJavadocByHash;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author mkleint
 */
public class DebugPluginSourceAction extends AbstractAction {
    private final ExecMojo mojo;
    private final RunConfig config;

    @NbBundle.Messages(value = "ACT_DEBUG_Plugin=Debug Plugin Mojo Source")
    public DebugPluginSourceAction(ExecMojo start, RunConfig conf) {
        putValue(NAME, Bundle.ACT_DEBUG_Plugin());
        this.mojo = start;
        this.config = conf;
    }

    @Override
    @NbBundle.Messages(value = "TIT_DEBUG_Plugin=Debugging Plugin Mojo")
    public void actionPerformed(ActionEvent e) {
        final AtomicBoolean cancel = new AtomicBoolean();
        org.netbeans.api.progress.ProgressUtils.runOffEventDispatchThread(new Runnable() {
            @Override
            public void run() {
                doLoad(cancel);
            }

        }, Bundle.TIT_DEBUG_Plugin(), cancel, false);
    }
    
    private void doLoad(final AtomicBoolean cancel) {
        URL[] urls = mojo.getClasspathURLs();
        String impl = mojo.getImplementationClass();
        if (urls != null) {
            //first download the source files for the binaries..
            MavenSourceJavadocAttacher attacher = new MavenSourceJavadocAttacher();
            for (URL url : urls) {
                try {
                    url = FileUtil.urlForArchiveOrDir(Utilities.toFile(url.toURI()));
                    List<? extends URL> ret = attacher.getSources(url, new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return cancel.get();
                        }
                    });
                    SourceForBinaryQuery.Result2 result = SourceForBinaryQuery.findSourceRoots2(url);
                    if (result.getRoots().length == 0 && !ret.isEmpty()) {
                        //binary not in repository, we need to hardwire the mapping here to have sfbq pick it up.
                        Set<File> fls = new HashSet<File>();
                        for (URL u : ret) {
                            File f = FileUtil.archiveOrDirForURL(u);
                            if (f != null) {
                                fls.add(f);
                            }
                        }
                        if (!fls.isEmpty()) {
                            SourceJavadocByHash.register(url, fls.toArray(new File[0]), false);
                        }
                    }
                    if (cancel.get()) {
                        return;
                    }
                } catch (URISyntaxException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            if (cancel.get()) {
                return;
            }
            ClassPath cp = Utils.convertToSourcePath(urls);
            RunConfig clone = RunUtils.cloneRunConfig(config);
            clone.setInternalProperty("jpda.additionalClasspath", cp);
            clone.setInternalProperty("jpda.stopclass", impl);
//stop method sometimes doesn't exist when inherited
            clone.setInternalProperty("jpda.stopmethod", "execute");
            clone.setProperty(Constants.ACTION_PROPERTY_JPDALISTEN, "maven");
            if (cancel.get()) {
                return;
            }
            RunUtils.run(clone);
        }
    }
}
