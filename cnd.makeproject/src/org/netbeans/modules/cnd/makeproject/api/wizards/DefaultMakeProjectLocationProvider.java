/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
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
 */
package org.netbeans.modules.cnd.makeproject.api.wizards;

import java.io.IOException;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.util.ConnectionManager;
import org.netbeans.modules.nativeexecution.api.util.HostInfoUtils;
import org.openide.util.Lookup;

/**
 *
 * @author Alexander
 */
public abstract class DefaultMakeProjectLocationProvider {

    private static final DefaultMakeProjectLocationProvider EMPTY = new Empty();

    protected DefaultMakeProjectLocationProvider() {
    }

    public static DefaultMakeProjectLocationProvider getDefault() {
        DefaultMakeProjectLocationProvider provider = Lookup.getDefault().lookup(DefaultMakeProjectLocationProvider.class);
        return provider == null ? EMPTY : provider;
    }

    public abstract String getDefaultProjectFolder();

    public abstract String getDefaultProjectFolder(ExecutionEnvironment env);

    private static final class Empty extends DefaultMakeProjectLocationProvider {

        Empty() {
        }

        @Override
        public String getDefaultProjectFolder() {
            return System.getProperty("user.home"); //NOI18N
        }

        @Override
        public String getDefaultProjectFolder(ExecutionEnvironment env) {
            try {
                if (env.isLocal()) {
                    return getDefaultProjectFolder();
                } else {
                    return HostInfoUtils.getHostInfo(env).getUserDir();
                }
            } catch (IOException ex) {
                ex.printStackTrace(System.err); // it doesn't make sense to disturb user
            } catch (ConnectionManager.CancellationException ex) {
                ex.printStackTrace(System.err); // it doesn't make sense to disturb user
            }
            return null;
        }

    }
}
