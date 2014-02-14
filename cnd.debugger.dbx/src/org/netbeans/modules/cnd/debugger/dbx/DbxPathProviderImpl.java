/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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

package org.netbeans.modules.cnd.debugger.dbx;

import org.netbeans.modules.cnd.debugger.dbx.spi.DbxPathProvider;
import org.netbeans.modules.cnd.debugger.common2.debugger.remote.Host;
import org.netbeans.modules.cnd.debugger.common2.debugger.remote.Platform;
import com.sun.tools.swdev.toolscommon.base.InstallDir;
import java.io.File;
import org.netbeans.modules.cnd.debugger.common2.debugger.NativeDebuggerManager;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author Egor Ushakov
 */
public final class DbxPathProviderImpl implements DbxPathProvider {
    public DbxPathProviderImpl() {
    }

    public String getDbxPath(Host host) {
	String dbx = System.getProperty("SPRO_DBX_PATH");	// NOI18N
        if (dbx == null)
	    dbx = System.getenv("SPRO_DBX_PATH");		// NOI18N

	if (dbx != null) {
	    Platform platform;
	    if (host != null)
		platform = host.getPlatform();
	    else
		platform = Platform.local();
	    String variant;
	    if (host.isLinux64())
		variant = platform.variant64();
	    else
		variant = platform.variant();
	    dbx = dbx.replaceAll("/PLATFORM/", "/" + variant + "/"); // NOI18N
	}

        // use spro.home for Tool only, see CR 7014085
        if (dbx == null && NativeDebuggerManager.isStandalone()) {
	    String overrideInstallDir = null;
	    if (host.isRemote())
		overrideInstallDir = host.getRemoteStudioLocation();

	    if (overrideInstallDir != null) {
		dbx = overrideInstallDir + "/bin/dbx"; // NOI18N
	    } else {

		String spro_home = InstallDir.get();
		if (spro_home == null) {
		    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
			    NbBundle.getMessage(DbxPathProviderImpl.class,
			    "MSG_MISSING_SPRO_HOME"))); // NOI18N
		} else {
                    String dbxPath = spro_home + "/bin/dbx"; // NOI18N
                    File dbxFile = new File(dbxPath);

                    if (!dbxFile.exists()) {
                        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                                NbBundle.getMessage(DbxPathProviderImpl.class,
                                "MSG_CantFindDbx", // NOI18N
                                dbxFile))); // NOI18N
                    } else {
                        dbx = dbxFile.getAbsolutePath();
                    }
                }
	    }
        }
        return dbx;
    }
}