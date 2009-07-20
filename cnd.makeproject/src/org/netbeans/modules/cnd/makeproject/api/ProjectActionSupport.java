/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.cnd.makeproject.api;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.cnd.api.execution.ExecutionListener;
import org.netbeans.modules.cnd.api.remote.CommandProvider;
import org.netbeans.modules.cnd.api.remote.HostInfoProvider;
import org.netbeans.modules.cnd.api.remote.PathMap;
import org.netbeans.modules.cnd.api.remote.RemoteSyncWorker;
import org.netbeans.modules.cnd.api.remote.ServerList;
import org.netbeans.modules.cnd.api.utils.IpeUtils;
import org.netbeans.modules.cnd.makeproject.MakeOptions;
import org.netbeans.modules.cnd.makeproject.api.BuildActionsProvider.BuildAction;
import org.netbeans.modules.cnd.makeproject.api.configurations.Configuration;
import org.netbeans.modules.cnd.makeproject.api.configurations.ConfigurationDescriptorProvider;
import org.netbeans.modules.cnd.makeproject.api.configurations.DebuggerChooserConfiguration;
import org.netbeans.modules.cnd.makeproject.api.configurations.MakeConfiguration;
import org.netbeans.modules.cnd.makeproject.api.configurations.ui.CustomizerNode;
import org.netbeans.modules.cnd.makeproject.api.remote.FilePathAdaptor;
import org.netbeans.modules.cnd.makeproject.ui.MakeLogicalViewProvider;
import org.netbeans.modules.cnd.makeproject.ui.SelectExecutablePanel;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Cancellable;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 * Most of the code here came from DefaultProjectActionHandler
 * as result of refactoring.
 */
public class ProjectActionSupport {

    private static ProjectActionSupport instance;
    private final List<ProjectActionHandlerFactory> handlerFactories;

    private ProjectActionSupport() {
        handlerFactories = new ArrayList<ProjectActionHandlerFactory>(
                Lookup.getDefault().lookupAll(ProjectActionHandlerFactory.class));
    }

    /**
     * Singleton pattern: instance getter.
     *
     * @return singleton instance
     */
    public static synchronized ProjectActionSupport getInstance() {
        if (instance == null) {
            instance = new ProjectActionSupport();
        }
        return instance;
    }

    /**
     * Checks if given action type can be handled. All registered
     * handler factories are asked.
     *
     * @param conf
     * @param type
     * @return
     */
    public boolean canHandle(MakeConfiguration conf, ProjectActionEvent.Type type) {
        if (conf != null) {
            DebuggerChooserConfiguration chooser = conf.getDebuggerChooserConfiguration();
            CustomizerNode node = chooser.getNode();
            if (node instanceof ProjectActionHandlerFactory) {
                if (((ProjectActionHandlerFactory) node).canHandle(type, conf)) {
                    return true;
                }
            }
        }
        for (ProjectActionHandlerFactory factory : handlerFactories) {
            if (factory.canHandle(type, conf)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Executes an array of project actions asynchronously.
     *
     * @param paes  project actions
     */
    public void fireActionPerformed(ProjectActionEvent[] paes) {
        new HandleEvents(paes, null).go();
    }

    public void fireActionPerformed(ProjectActionEvent[] paes, ProjectActionHandler preferredHandler) {
        new HandleEvents(paes, preferredHandler).go();
    }

////////////////////////////////////////////////////////////////////////////////

    private InputOutput mainTab = null;
    private HandleEvents mainTabHandler = null;
    private ArrayList<String> tabNames = new ArrayList<String>();

    private class HandleEvents implements ExecutionListener {

        private InputOutput ioTab = null;
        private ProjectActionEvent[] paes;
        private String tabName;
        private String tabNameSeq;
        int currentAction = 0;
        private StopAction sa = null;
        private RerunAction ra = null;
        List<BuildAction> additional;
        private ProgressHandle progressHandle = null;
        private final Object lock = new Object();
        private ProjectActionHandler customHandler = null;
        private ProjectActionHandler currentHandler = null;

        public HandleEvents(ProjectActionEvent[] paes, ProjectActionHandler customHandler) {
            this.paes = paes;
            this.customHandler = customHandler;
            currentAction = 0;

            if (MakeOptions.getInstance().getReuse()) {
                synchronized (lock) {
                    if (mainTabHandler == null && mainTab != null /*&& !mainTab.isClosed()*/) {
                        mainTab.closeInputOutput();
                        mainTab = null;
                    }
                    tabName = getTabName(paes);
                    tabNameSeq = tabName;
                    if (tabNames.contains(tabName)) {
                        int seq = 2;
                        while (true) {
                            tabNameSeq = tabName + " #" + seq; // NOI18N
                            if (!tabNames.contains(tabNameSeq)) {
                                break;
                            }
                            seq++;
                        }
                    }
                    tabNames.add(tabNameSeq);
                    ioTab = getIOTab(tabNameSeq, true);
                    if (mainTabHandler == null) {
                        mainTab = ioTab;
                        mainTabHandler = this;
                    }
                }
            } else {
                tabName = getTabName(paes);
                tabNameSeq = tabName;
                ioTab = getIOTab(tabName, false);
            }
        }

        private String getTabName(ProjectActionEvent[] paes) {
            String projectName = ProjectUtils.getInformation(paes[0].getProject()).getDisplayName();
            StringBuilder name = new StringBuilder(projectName);
            name.append(" ("); // NOI18N
            for (int i = 0; i < paes.length; i++) {
                if (i >= 2) {
                    name.append("..."); // NOI18N
                    break;
                }
                name.append(paes[i].getActionName());
                if (i < paes.length - 1) {
                    name.append(", "); // NOI18N
                }
            }
            name.append(")"); // NOI18N
            if (paes.length > 0) {
                MakeConfiguration conf = paes[0].getConfiguration();
                if (!conf.getDevelopmentHost().isLocalhost()) {
                    String hkey = conf.getDevelopmentHost().getHostKey();
                    name.append(" - ").append(hkey); //NOI18N
                }
            }
            return name.toString();
        }

        private InputOutput getTab() {
            return ioTab;
        }

        private ProgressHandle createProgressHandle() {
            ProgressHandle handle = ProgressHandleFactory.createHandle(tabNameSeq, new Cancellable() {
                public boolean cancel() {
                    sa.actionPerformed(null);
                    return true;
                }
            }, new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    getTab().select();
                }
            });
            handle.setInitialDelay(0);
            return handle;
        }

        private ProgressHandle createProgressHandleNoCancel() {
            ProgressHandle handle = ProgressHandleFactory.createHandle(tabNameSeq,
                    new AbstractAction() {
                        public void actionPerformed(ActionEvent e) {
                            getTab().select();
                        }
                    });
            handle.setInitialDelay(0);
            return handle;
        }

        private InputOutput getIOTab(String name, boolean reuse) {
            sa = new StopAction(this);
            ra = new RerunAction(this);
            List<Action> list = new ArrayList<Action>();
            list.add(sa);
            list.add(ra);
            additional = BuildActionsProvider.getDefault().getActions(name, paes);
            list.addAll(additional);
            InputOutput tab;
            if (reuse) {
                tab = IOProvider.getDefault().getIO(name, false); // This will (sometimes!) find an existing one.
                tab.closeInputOutput(); // Close it...
            }
            tab = IOProvider.getDefault().getIO(name, list.toArray(new Action[list.size()])); // Create a new ...
            try {
                tab.getOut().reset();
            } catch (IOException ioe) {
            }

            progressHandle = createProgressHandle();
            progressHandle.start();

            return tab;
        }

        private void reRun() {
            currentAction = 0;
            getTab().closeInputOutput();
            synchronized (lock) {
                tabNames.add(tabNameSeq);
            }
            try {
                getTab().getOut().reset();
            } catch (IOException ioe) {
            }
            progressHandle = createProgressHandle();
            progressHandle.start();
            go();
        }

        private void go() {
            currentHandler = null;
            sa.setEnabled(false);
            ra.setEnabled(false);
            if (currentAction >= paes.length) {
                return;
            }

            final ProjectActionEvent pae = paes[currentAction];

            // Validate executable
            switch (pae.getType()) {
                case RUN:
                case DEBUG:
                case DEBUG_LOAD_ONLY:
                case DEBUG_STEPINTO:
                case CHECK_EXECUTABLE:
                case CUSTOM_ACTION:
                if (!checkExecutable(pae) || pae.getType() == ProjectActionEvent.Type.CHECK_EXECUTABLE) {
                    progressHandle.finish();
                    return;
                }
            }

            if (pae.getType() == ProjectActionEvent.Type.CUSTOM_ACTION && customHandler != null) {
                initHandler(customHandler, pae, paes);
                customHandler.execute(ioTab);
            } else {
                if (currentAction == 0 && !checkRemotePath(pae)) {
                    progressHandle.finish();
                    return;
                }
                for (ProjectActionHandlerFactory factory : handlerFactories) {
                    if (factory.canHandle(pae.getType(), pae.getConfiguration())) {
                        ProjectActionHandler handler = currentHandler = factory.createHandler();
                        initHandler(handler, pae, paes);
                        handler.execute(ioTab);
                        break;
                    }
                }

            }

        }

        // moved from DefaultProjectActionHandler.execute
        private boolean checkRemotePath(ProjectActionEvent pae) {
            boolean success = true;
            MakeConfiguration conf = pae.getConfiguration();
            if (!conf.getDevelopmentHost().isLocalhost()) {
                // Make sure the project and 1st level subprojects are visible remotely
                String baseDir = pae.getProfile().getBaseDir();
                ExecutionEnvironment env = conf.getDevelopmentHost().getExecutionEnvironment();
                for (String subprojectDir : conf.getSubProjectLocations()) {
                    subprojectDir = IpeUtils.toAbsolutePath(baseDir, subprojectDir);
                    success &= sync(subprojectDir, env);
                    if (!success) {
                        break;
                    }
                }
                if (success) {
                    success &= sync(baseDir, env);
                }
            }
            return success;
        }

        private boolean sync(String projectDir, ExecutionEnvironment env) {
            PrintWriter err = null;
            PrintWriter out = null;
            InputOutput tab = getTab();
            if (tab != null) {
                out = this.ioTab.getOut();
                err = this.ioTab.getErr();
            }
            File dir2sync = new File(projectDir);
            File privProjectStorage = new File(new File(dir2sync, "nbproject"), "private"); //NOI18N
            RemoteSyncWorker syncWorker = ServerList.get(env).getSyncFactory().createNew(
                    dir2sync, env, out, err, privProjectStorage);
            return syncWorker.synchronize();
        }

        private void initHandler(ProjectActionHandler handler, ProjectActionEvent pae, ProjectActionEvent[] paes) {
            handler.init(pae, paes);
            progressHandle.finish();
            progressHandle = handler.canCancel()? createProgressHandle() : createProgressHandleNoCancel();
            progressHandle.start();
            sa.setEnabled(handler.canCancel());
            handler.addExecutionListener(this);
        }

        public ProjectActionHandler getCurrentHandler() {
            return currentHandler;
        }

        public void executionStarted(int pid) {
            if (additional != null) {
                for (BuildAction action : additional) {
                    action.setStep(currentAction);
                    action.executionStarted(pid);
                }
            }
        }

        public void executionFinished(int rc) {
            if (additional != null) {
                for (Action action : additional) {
                    ((ExecutionListener) action).executionFinished(rc);
                }
            }
            if (paes[currentAction].getType() == ProjectActionEvent.Type.BUILD || paes[currentAction].getType() == ProjectActionEvent.Type.CLEAN) {
                // Refresh all files
                try {
                    FileObject projectFileObject = paes[currentAction].getProject().getProjectDirectory();
                    projectFileObject.getFileSystem().refresh(false);
                    MakeLogicalViewProvider.refreshBrokenItems(paes[currentAction].getProject());
                } catch (Exception e) {
                }
            }
            if (currentAction >= paes.length - 1 || rc != 0) {
                synchronized (lock) {
                    if (mainTabHandler == this) {
                        mainTabHandler = null;
                    }
                    tabNames.remove(tabNameSeq);
                }
                sa.setEnabled(false);
                ra.setEnabled(true);
                progressHandle.finish();
                return;
            }

            // This code is executed in finishing ProjectActionHandler's thread.
            // Starting next handler in this thread may lead to problems, such as
            // new threads being created in old handler's thread group, and NetBeans
            // thinking that old handler has not completed.
            // So the call to go() is posted to RequestProcessor.
            if (rc == 0) {
                currentAction++;
                RequestProcessor.getDefault().post(new Runnable() {
                    public void run() {
                        go();
                    }
                });
            }
        }

        private boolean checkExecutable(ProjectActionEvent pae) {
            // Check if something is specified
            String executable = pae.getExecutable();
            if (executable.length() == 0) {
                SelectExecutablePanel panel = new SelectExecutablePanel(pae.getConfiguration());
                DialogDescriptor descriptor = new DialogDescriptor(panel, getString("SELECT_EXECUTABLE"));
                panel.setDialogDescriptor(descriptor);
                DialogDisplayer.getDefault().notify(descriptor);
                if (descriptor.getValue() == DialogDescriptor.OK_OPTION) {
                    // Set executable in configuration
                    MakeConfiguration makeConfiguration = pae.getConfiguration();
                    executable = panel.getExecutable();
                    executable = FilePathAdaptor.naturalize(executable);
                    executable = IpeUtils.toRelativePath(makeConfiguration.getBaseDir(), executable);
                    executable = FilePathAdaptor.normalize(executable);
                    makeConfiguration.getMakefileConfiguration().getOutput().setValue(executable);
                    // Mark the project 'modified'
                    ConfigurationDescriptorProvider pdp = pae.getProject().getLookup().lookup(ConfigurationDescriptorProvider.class);
                    if (pdp != null) {
                        pdp.getConfigurationDescriptor().setModified();
                    }
                    // Set executable in pae
                    if (pae.getType() == ProjectActionEvent.Type.RUN) {
                        // Next block is commented out due to IZ120794
                        /*CompilerSet compilerSet = CompilerSetManager.getDefault(makeConfiguration.getDevelopmentHost().getName()).getCompilerSet(makeConfiguration.getCompilerSet().getValue());
                        if (compilerSet != null && compilerSet.getCompilerFlavor() != CompilerFlavor.MinGW) {
                        // IZ 120352
                        executable = FilePathAdaptor.naturalize(executable);
                        }*/
                        pae.setExecutable(executable);
                    } else {
                        pae.setExecutable(makeConfiguration.getMakefileConfiguration().getAbsOutput());
                    }
                } else {
                    return false;
                }
            }
            // Check existence of executable
            if (!IpeUtils.isPathAbsolute(executable) && (executable.startsWith(".") || executable.indexOf(File.separatorChar) > 0)) { // NOI18N
                //executable is relative to run directory - convert to absolute and check. Should be safe (?).
                String runDir = pae.getProfile().getRunDir();
                if (runDir == null || runDir.length() == 0) {
                    executable = IpeUtils.toAbsolutePath(pae.getConfiguration().getBaseDir(), executable);
                } else {
                    runDir = IpeUtils.toAbsolutePath(pae.getConfiguration().getBaseDir(), runDir);
                    executable = IpeUtils.toAbsolutePath(runDir, executable);
                }
                executable = FilePathAdaptor.normalize(executable);
            }
            if (IpeUtils.isPathAbsolute(executable)) {
                Configuration conf = pae.getConfiguration();
                boolean ok = true;

                if (conf instanceof MakeConfiguration && !((MakeConfiguration) conf).getDevelopmentHost().isLocalhost()) {
                    final ExecutionEnvironment execEnv = ((MakeConfiguration) conf).getDevelopmentHost().getExecutionEnvironment();
                    PathMap mapper = HostInfoProvider.getMapper(execEnv);
                    executable = mapper.getRemotePath(executable,true);
                    CommandProvider cmd = Lookup.getDefault().lookup(CommandProvider.class);
                    if (cmd != null) {
                        ok = cmd.run(execEnv, "test", null, "-x", executable, "-a", "-f", executable) == 0; // NOI18N
                    }
                } else {
                    // FIXUP: getExecutable should really return fully qualified name to executable including .exe
                    // but it is too late to change now. For now try both with and without.
                    File file = new File(executable);
                    if (!file.exists()) {
                        file = new File(executable + ".exe"); // NOI18N
                    }
                    if (!file.exists() || file.isDirectory()) {
                        ok = false;
                    }
                }
                if (!ok) {
                    String errormsg = getString("EXECUTABLE_DOESNT_EXISTS", executable); // NOI18N
                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(errormsg, NotifyDescriptor.ERROR_MESSAGE));
                    return false;
                }
            }

            // Finally set pae.executable to a real, verified file with an absolute
            // path that reflects file location on a target host (local or remote)

            pae.setExecutable(executable);
            
            return true;
        }

    }

// VK: inlined since it's used once; and caller should know not only return status,
// but mapped name as well => it's easier to inline
//    /**
//     * Verify a remote executable exists, is executable, and is not a directory.
//     *
//     * @param execEnv The remote host
//     * @param executable The file to remotely check
//     * @return true if executable exists and is an executable, otherwise false
//     */
//    private static boolean verifyRemoteExecutable(ExecutionEnvironment execEnv, String executable) {
//        PathMap mapper = HostInfoProvider.getMapper(execEnv);
//        String remoteExecutable = mapper.getRemotePath(executable);
//        CommandProvider cmd = Lookup.getDefault().lookup(CommandProvider.class);
//        if (cmd != null) {
//            return cmd.run(execEnv, "test -x " + remoteExecutable + " -a -f " + remoteExecutable, null) == 0; // NOI18N
//        }
//        return false;
//    }

    private static final class StopAction extends AbstractAction {

        HandleEvents handleEvents;

        public StopAction(HandleEvents handleEvents) {
            this.handleEvents = handleEvents;
            putValue(Action.SMALL_ICON, ImageUtilities.loadImageIcon("org/netbeans/modules/cnd/makeproject/ui/resources/stop.png", false)); // NOI18N
            putValue(Action.SHORT_DESCRIPTION, getString("TargetExecutor.StopAction.stop")); // NOI18N
        //System.out.println("handleEvents 1 " + handleEvents);
        //setEnabled(false); // initially, until ready
        }

        public void actionPerformed(ActionEvent e) {
            if (!isEnabled()) {
                return;
            }
            setEnabled(false);
            if (handleEvents.getCurrentHandler() != null) {
                handleEvents.getCurrentHandler().cancel();
            }
        }
    }

    private static final class RerunAction extends AbstractAction {

        HandleEvents handleEvents;

        public RerunAction(HandleEvents handleEvents) {
            this.handleEvents = handleEvents;
            putValue(Action.SMALL_ICON, ImageUtilities.loadImageIcon("org/netbeans/modules/cnd/makeproject/ui/resources/rerun.png", false)); // NOI18N
            putValue(Action.SHORT_DESCRIPTION, getString("TargetExecutor.RerunAction.rerun")); // NOI18N
        }

        public void actionPerformed(ActionEvent e) {
            setEnabled(false);
            handleEvents.reRun();
        }
    }

    /** Look up i18n strings here */
    private static String getString(String s) {
        return NbBundle.getBundle(ProjectActionSupport.class).getString(s);
    }

    private static String getString(String s, String arg) {
        return NbBundle.getMessage(ProjectActionSupport.class, s, arg);
    }
}
