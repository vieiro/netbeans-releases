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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.gizmo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.cnd.api.remote.HostInfoProvider;
import org.netbeans.modules.cnd.api.remote.SetupProvider;
import org.netbeans.modules.cnd.dwarfdump.Offset2LineService;
import org.netbeans.modules.cnd.utils.cache.CndFileUtils;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.NativeProcess;
import org.netbeans.modules.nativeexecution.api.NativeProcessBuilder;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;

/**
 *
 * @author Alexander Simon
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.modules.cnd.api.remote.SetupProvider.class)
public class RemoteJarServiceProvider implements SetupProvider {
    private static final Class<?> service = Offset2LineService.class;
    private static final String prefix = "/modules/"; // NOI18N
    private static final String relativePath;
    private static final String localAbsPath;
    static {
        String[] paths = findPaths();
        relativePath = paths[0];
        localAbsPath = paths[1];
    }

    private static String[] findPaths() {
        try {
            String path = service.getProtectionDomain().getCodeSource().getLocation().getPath();
            path = path.replace('\\', '/'); // NOI18N
            path = path.substring(path.lastIndexOf(prefix)+1); // NOI18N
            if (path.indexOf('!') > 0) {
                path = path.substring(0, path.indexOf('!')); // NOI18N
            }
            String relPath = path;
            String absPath = null;
            File file = InstalledFileLocator.getDefault().locate(relPath, "org.netbeans.modules.cnd.dwarfdump", false); //NOI18N
            if (file != null) {
                absPath = file.getAbsolutePath();
            }
            return new String[] { relPath, absPath };
        } catch (Throwable thr) {
            Exceptions.printStackTrace(thr);
            return new String[] { null, null };
        }
    }

    @Override
    public Map<String, File> getBinaryFiles(ExecutionEnvironment env) {
        Map<String, File> result = new HashMap<String, File>();
        if (relativePath != null && localAbsPath != null) {
            result.put(relativePath, CndFileUtils.createLocalFile(localAbsPath)); // NOI18N
        }
        return result;
    }

    public static NativeProcess getJavaProcess(Class<?> clazz, ExecutionEnvironment env, String[] arguments) throws IOException{
        NativeProcessBuilder npb = NativeProcessBuilder.newProcessBuilder(env);
        npb.setCharset(Charset.forName("UTF-8")); // NOI18N
        npb.setExecutable("java"); //NOI18N
        List<String> args = new ArrayList<String>();
        args.add("-cp"); //NOI18N
        if (env.isLocal()) {
            args.add(localAbsPath == null ? "." : localAbsPath); //NOI18N
        } else {
            String libDir = HostInfoProvider.getLibDir(env); //NB: should contain trailing '/'
            if (!libDir.endsWith("/")) { // NOI18N
                libDir += "/"; // NOI18N
            }
            String resource = libDir+relativePath;
            args.add(resource);
        }
        args.add(clazz.getName());
        args.addAll(Arrays.asList(arguments));
        npb.setArguments(args.toArray(new String[args.size()]));
        return npb.call();
    }
}
