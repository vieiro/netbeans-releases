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
package org.netbeans.modules.javaee.wildfly;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import javax.enterprise.deploy.model.DeployableObject;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.DConfigBeanVersionType;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.DeploymentConfiguration;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.DConfigBeanVersionUnsupportedException;
import javax.enterprise.deploy.spi.exceptions.InvalidModuleException;
import javax.enterprise.deploy.spi.exceptions.TargetException;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.deployment.plugins.spi.DeploymentContext;
import org.netbeans.modules.j2ee.deployment.plugins.spi.DeploymentManager2;
import org.netbeans.modules.javaee.wildfly.deploy.WildflyDeploymentStatus;
import org.netbeans.modules.javaee.wildfly.deploy.WildflyProgressObject;
import org.netbeans.modules.javaee.wildfly.ide.commands.JBModule;
import org.netbeans.modules.javaee.wildfly.ide.commands.WildflyClient;
import org.netbeans.modules.javaee.wildfly.ide.ui.JBPluginProperties;
import org.netbeans.modules.javaee.wildfly.ide.ui.JBPluginUtils.Version;
import org.netbeans.modules.javaee.wildfly.nodes.JBEjbModuleNode;
import org.netbeans.modules.javaee.wildfly.util.WildFlyProperties;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Petr Hejl
 */
public class WildFlyDeploymentManager implements DeploymentManager2 {

    private static final Version JBOSS_8_0_0 = new Version("8.0.0"); // NOI18N

    private static final int DEBUGGING_PORT = 8787;
    private static final int CONTROLLER_PORT = 9999;

    private final WildflyClient client;

    /**
     * Stores information about running instances. instance is represented by
     * its InstanceProperties, running state by Boolean.TRUE, stopped state
     * Boolean.FALSE. WeakHashMap should guarantee erasing of an unregistered
     * server instance bcs instance properties are also removed along with
     * instance.
     */
    private static final Map<InstanceProperties, Boolean> PROPERTIES_TO_IS_RUNNING
            = Collections.synchronizedMap(new WeakHashMap());

    private final DeploymentFactory df;

    private final String realUri;

    private final InstanceProperties instanceProperties;

    /**
     * <i>GuardedBy("this")</i>
     */
    private boolean needsRestart;

    public WildFlyDeploymentManager(DeploymentFactory df, String realUri,
            String jbUri, String username, String password) {
        this.df = df;
        this.realUri = realUri;
        this.instanceProperties = InstanceProperties.getInstanceProperties(realUri);
        if (username != null && password != null) {
            this.client = new WildflyClient(instanceProperties, getHost(), CONTROLLER_PORT, username, password);
        } else {
            this.client = new WildflyClient(instanceProperties, getHost(), CONTROLLER_PORT);
        }
    }

    /**
     * Returns true if the given instance properties are present in the map and
     * value equals true. Otherwise return false.
     */
    public static boolean isRunningLastCheck(InstanceProperties ip) {
        return PROPERTIES_TO_IS_RUNNING.containsKey(ip) && PROPERTIES_TO_IS_RUNNING.get(ip).equals(Boolean.TRUE);
    }

    /**
     * Stores state of an instance represented by InstanceProperties.
     */
    public static void setRunningLastCheck(InstanceProperties ip, Boolean isRunning) {
        PROPERTIES_TO_IS_RUNNING.put(ip, isRunning);
    }

    @Override
    public ProgressObject redeploy(TargetModuleID[] tmids, DeploymentContext deployment) {
        if (df == null) {
            throw new IllegalStateException("Deployment manager is disconnected");
        }
        final WildflyProgressObject progress = new WildflyProgressObject(tmids);
        progress.fireProgressEvent(null, new WildflyDeploymentStatus(ActionType.EXECUTE, CommandType.REDEPLOY, StateType.RUNNING, ""));
        try {
            if (this.getClient().deploy(deployment)) {
                for(TargetModuleID tmid : tmids) {
                    ((JBTargetModuleID) tmid).setContextURL(this.getClient().getWebModuleURL(tmid.getModuleID()));
                    progress.fireProgressEvent(tmid, new WildflyDeploymentStatus(ActionType.EXECUTE, CommandType.DISTRIBUTE, StateType.COMPLETED, ""));
                }
            } else {
                progress.fireProgressEvent(null, new WildflyDeploymentStatus(ActionType.EXECUTE, CommandType.REDEPLOY, StateType.FAILED, ""));
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            progress.fireProgressEvent(null, new WildflyDeploymentStatus(ActionType.EXECUTE, CommandType.REDEPLOY, StateType.FAILED, ex.getMessage()));
        }
        return progress;
    }

    @Override
    public ProgressObject distribute(Target[] targets, DeploymentContext deployment) {
        if (df == null) {
            throw new IllegalStateException("Deployment manager is disconnected");
        }
        List<JBTargetModuleID> moduleIds = new ArrayList<JBTargetModuleID>(targets.length);
        for (Target target : targets) {
            moduleIds.add(new JBTargetModuleID(target, deployment.getModuleFile().getName()));
        }
        JBTargetModuleID[] tmids = moduleIds.toArray(new JBTargetModuleID[targets.length]);
        final WildflyProgressObject progress = new WildflyProgressObject(tmids);
        progress.fireProgressEvent(null, new WildflyDeploymentStatus(ActionType.EXECUTE, CommandType.DISTRIBUTE, StateType.RUNNING, ""));
        try {
            if (this.getClient().deploy(deployment)) {
                for(JBTargetModuleID tmid : tmids) {
                    tmid.setContextURL(this.getClient().getWebModuleURL(tmid.getModuleID()));
                    progress.fireProgressEvent(tmid, new WildflyDeploymentStatus(ActionType.EXECUTE, CommandType.DISTRIBUTE, StateType.COMPLETED, ""));
                }                
            } else {
                progress.fireProgressEvent(tmids[0], new WildflyDeploymentStatus(ActionType.EXECUTE, CommandType.DISTRIBUTE, StateType.FAILED, ""));
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            progress.fireProgressEvent(tmids[0], new WildflyDeploymentStatus(ActionType.EXECUTE, CommandType.DISTRIBUTE, StateType.FAILED, ex.getMessage()));
        }
        return progress;
    }

    @Override
    public Target[] getTargets() throws IllegalStateException {
        // XXX WILDFLY IMPLEMENT
        return new Target[]{new Target() {

            @Override
            public String getName() {
                return "WildFly Target";
            }

            @Override
            public String getDescription() {
                return "WildFly Target";
            }
        }};
    }

    @Override
    public TargetModuleID[] getRunningModules(ModuleType mt, Target[] targets) throws TargetException, IllegalStateException {
        if (df == null) {
            throw new IllegalStateException("Deployment manager is disconnected");
        }
        List<TargetModuleID> result = new ArrayList<TargetModuleID>();
        Collection<JBModule> modules;
        try {
            modules = getClient().listAvailableModules();

            if (ModuleType.EJB.equals(mt)) {
                for (JBModule module : modules) {
                    if (module.getArchiveName().endsWith("jar") && module.isRunning()) {
                        result.add(new JBTargetModuleID(targets[0], module.getArchiveName()));
                    }
                }
            } else if (ModuleType.WAR.equals(mt)) {
                for (JBModule module : modules) {
                    if (module.getArchiveName().endsWith("war") && module.isRunning()) {
                        JBTargetModuleID moduleId = new JBTargetModuleID(targets[0], module.getArchiveName());
                        moduleId.setContextURL(module.getUrl());
                        result.add(moduleId);
                    }
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return result.toArray(new TargetModuleID[result.size()]);
    }

    @Override
    public TargetModuleID[] getNonRunningModules(ModuleType mt, Target[] targets) throws TargetException, IllegalStateException {
        if (df == null) {
            throw new IllegalStateException("Deployment manager is disconnected");
        }
        // XXX WILDFLY IMPLEMENT
        return new TargetModuleID[]{};
    }

    @Override
    public TargetModuleID[] getAvailableModules(ModuleType mt, Target[] targets) throws TargetException, IllegalStateException {
        if (df == null) {
            throw new IllegalStateException("Deployment manager is disconnected");
        }
        List<TargetModuleID> result = new ArrayList<TargetModuleID>();
        Collection<JBModule> modules;
        try {
            modules = getClient().listAvailableModules();

            if (ModuleType.EJB.equals(mt)) {
                for (JBModule module : modules) {
                    if (module.getArchiveName().endsWith("jar") && module.isRunning()) {
                        result.add(new JBTargetModuleID(targets[0], module.getArchiveName()));
                    }
                }
            } else if (ModuleType.WAR.equals(mt)) {
                for (JBModule module : modules) {
                    if (module.getArchiveName().endsWith("war")) {
                        JBTargetModuleID moduleId = new JBTargetModuleID(targets[0], module.getArchiveName());
                        moduleId.setContextURL(module.getUrl());
                        result.add(moduleId);
                    }
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return result.toArray(new TargetModuleID[result.size()]);
    }

    @Override
    public ProgressObject distribute(Target[] targets, File file, File file1) throws IllegalStateException {
        if (df == null) {
            throw new IllegalStateException("Deployment manager is disconnected");
        }
        // XXX WILDFLY IMPLEMENT
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ProgressObject start(TargetModuleID[] tmids) throws IllegalStateException {
        final WildflyProgressObject progress = new WildflyProgressObject(tmids);
        progress.fireProgressEvent(null, new WildflyDeploymentStatus(
                ActionType.EXECUTE, CommandType.START, StateType.RUNNING, null));
        try {
            if (client.startModule(tmids[0].getModuleID())) {
                progress.fireProgressEvent(tmids[0], new WildflyDeploymentStatus(
                        ActionType.EXECUTE, CommandType.START, StateType.COMPLETED, null));
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            progress.fireProgressEvent(tmids[0], new WildflyDeploymentStatus(
                    ActionType.EXECUTE, CommandType.START, StateType.FAILED, null));
        }
        return progress;
    }

    @Override
    public ProgressObject stop(TargetModuleID[] tmids) throws IllegalStateException {
        if (df == null) {
            throw new IllegalStateException("Deployment manager is disconnected");
        }
        // XXX WILDFLY IMPLEMENT
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ProgressObject undeploy(TargetModuleID[] tmids) throws IllegalStateException {
        final WildflyProgressObject progress = new WildflyProgressObject(tmids);
        progress.fireProgressEvent(tmids[0], new WildflyDeploymentStatus(
                ActionType.EXECUTE, CommandType.UNDEPLOY, StateType.RUNNING, null));
        try {
            if (client.undeploy(tmids[0].getModuleID())) {
                progress.fireProgressEvent(null, new WildflyDeploymentStatus(
                        ActionType.EXECUTE, CommandType.UNDEPLOY, StateType.COMPLETED, null));
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            progress.fireProgressEvent(null, new WildflyDeploymentStatus(
                    ActionType.EXECUTE, CommandType.UNDEPLOY, StateType.FAILED, null));
        }
        return progress;
    }

    @Override
    public ProgressObject redeploy(TargetModuleID[] tmids, File file, File file1) throws UnsupportedOperationException, IllegalStateException {
        if (df == null) {
            throw new IllegalStateException("Deployment manager is disconnected");
        }
        // XXX WILDFLY IMPLEMENT
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ProgressObject redeploy(TargetModuleID[] tmids, InputStream in, InputStream in1) throws UnsupportedOperationException, IllegalStateException {
        if (df == null) {
            throw new IllegalStateException("Deployment manager is disconnected");
        }
        // XXX WILDFLY IMPLEMENT
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRedeploySupported() {
        return true;
    }

    @Override
    public void release() {
    }

    public String getHost() {
        String host = InstanceProperties.getInstanceProperties(realUri).
                getProperty(JBPluginProperties.PROPERTY_HOST);
        return host;
    }

    public int getPort() {
        String port = InstanceProperties.getInstanceProperties(realUri).
                getProperty(JBPluginProperties.PROPERTY_PORT);
        return new Integer(port).intValue();
    }

    public Version getServerVersion() {
        return JBOSS_8_0_0;
    }

    public int getDebuggingPort() {
        return DEBUGGING_PORT;
    }

    public InstanceProperties getInstanceProperties() {
        return instanceProperties;
    }

    public String getUrl() {
        return realUri;
    }

    public WildFlyProperties getProperties() {
        return new WildFlyProperties(this);
    }

    /**
     * Mark the server with a needs restart flag. This may be needed for
     * instance when JDBC driver is deployed to a running server.
     */
    public synchronized void setNeedsRestart(boolean needsRestart) {
        this.needsRestart = needsRestart;
    }

    /**
     * Returns true if the server needs to be restarted. This may occur for
     * instance when JDBC driver was deployed to a running server
     */
    public synchronized boolean getNeedsRestart() {
        return needsRestart;
    }

    public WildflyClient getClient() {
        return client;
    }

    @Override
    public ProgressObject distribute(Target[] targets, InputStream in, InputStream in1) throws IllegalStateException {
        throw new UnsupportedOperationException("This method should never be called!"); // NOI18N
    }

    @Override
    public ProgressObject distribute(Target[] targets, ModuleType mt, InputStream in, InputStream in1) throws IllegalStateException {
        throw new UnsupportedOperationException("This method should never be called!"); // NOI18N
    }

    @Override
    public DeploymentConfiguration createConfiguration(DeployableObject d) throws InvalidModuleException {
        throw new UnsupportedOperationException("This method should never be called!"); // NOI18N
    }

    @Override
    public Locale getDefaultLocale() {
        throw new UnsupportedOperationException("This method should never be called!"); // NOI18N
    }

    @Override
    public Locale getCurrentLocale() {
        throw new UnsupportedOperationException("This method should never be called!"); // NOI18N
    }

    @Override
    public void setLocale(Locale locale) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This method should never be called!"); // NOI18N
    }

    @Override
    public Locale[] getSupportedLocales() {
        throw new UnsupportedOperationException("This method should never be called!"); // NOI18N
    }

    @Override
    public boolean isLocaleSupported(Locale locale) {
        throw new UnsupportedOperationException("This method should never be called!"); // NOI18N
    }

    @Override
    public DConfigBeanVersionType getDConfigBeanVersion() {
        throw new UnsupportedOperationException("This method should never be called!"); // NOI18N
    }

    @Override
    public boolean isDConfigBeanVersionSupported(DConfigBeanVersionType dcbvt) {
        throw new UnsupportedOperationException("This method should never be called!"); // NOI18N
    }

    @Override
    public void setDConfigBeanVersion(DConfigBeanVersionType dcbvt) throws DConfigBeanVersionUnsupportedException {
        throw new UnsupportedOperationException("This method should never be called!"); // NOI18N
    }

}