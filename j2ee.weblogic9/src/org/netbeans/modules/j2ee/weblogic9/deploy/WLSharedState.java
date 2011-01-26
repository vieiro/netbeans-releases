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

package org.netbeans.modules.j2ee.weblogic9.deploy;

import java.io.File;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.weblogic9.WLPluginProperties;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;

/**
 * <i>ThreadSafe</i>
 *
 * @author Petr Hejl
 * TODO perhaps this could be merged into WLDeploymentManager
 */
public class WLSharedState {

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    private final InstanceProperties ip;

    /** <i>GuardedBy("this")</i> */
    private DomainChangeListener domainListener;

    /* <i>GuardedBy("this")</i> */
    private boolean restartNeeded;

    /* <i>GuardedBy("this")</i> */
    private Process serverProcess;

    public WLSharedState(InstanceProperties ip) {
        this.ip = ip;
    }

    public synchronized void configure() {
        if (domainListener != null) {
            return;
        }

        File domainConfig = WLPluginProperties.getDomainConfigFile(ip);
        if (domainConfig != null) {
            domainListener = new DomainChangeListener();
            // weak reference
            FileUtil.addFileChangeListener(domainListener, domainConfig);
        }
    }

    public void addDomainChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeDomainChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    public synchronized boolean isRestartNeeded() {
        return restartNeeded;
    }

    public synchronized void setRestartNeeded(boolean restartNeeded) {
        this.restartNeeded = restartNeeded;
    }

    public synchronized Process getServerProcess() {
        return serverProcess;
    }

    public synchronized void setServerProcess(Process serverProcess) {
        this.serverProcess = serverProcess;
    }

    private class DomainChangeListener implements FileChangeListener {

        @Override
        public void fileAttributeChanged(FileAttributeEvent fe) {
            // noop
        }

        @Override
        public void fileChanged(FileEvent fe) {
            changeSupport.fireChange();
        }

        @Override
        public void fileDataCreated(FileEvent fe) {
            // realistically this would not happen
            changeSupport.fireChange();
        }

        @Override
        public void fileDeleted(FileEvent fe) {
            // realistically this would not happen
            changeSupport.fireChange();
        }

        @Override
        public void fileFolderCreated(FileEvent fe) {
            // noop
        }

        @Override
        public void fileRenamed(FileRenameEvent fe) {
            // realistically this would not happen
            changeSupport.fireChange();
        }
    }
}
