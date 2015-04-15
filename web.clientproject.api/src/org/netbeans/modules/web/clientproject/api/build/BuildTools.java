/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.clientproject.api.build;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JComponent;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.project.Project;
import org.netbeans.modules.web.clientproject.api.build.ui.CustomizerPanel;
import org.netbeans.modules.web.clientproject.spi.build.BuildToolImplementation;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.Parameters;

/**
 * Support for build tools.
 * @since 1.81
 */
public final class BuildTools {

    private static final BuildTools INSTANCE = new BuildTools();


    private BuildTools() {
    }

    public static BuildTools getDefault() {
        return INSTANCE;
    }

    /**
     * Run "build" for the given project and command identifier.
     * <p>
     * If the are more build tools available, all the tools are run (in random order).
     * @param project project to be used for the command
     * @param commandId command identifier (build, rebuild, run etc.)
     * @param waitFinished wait till the command finishes?
     * @param warnUser warn user (show dialog, customizer) if any problem occurs (e.g. command is not known/set to this build tool)
     * @return {@code true} if command was run (at least by one build tool), {@code false} otherwise
     */
    public boolean run(@NonNull Project project, @NonNull String commandId, boolean waitFinished, boolean warnUser) {
        Parameters.notNull("project", project); // NOI18N
        Parameters.notNull("commandId", commandId); // NOI18N
        boolean run = false;
        for (BuildToolImplementation buildTool : getEnabledBuildTools(project)) {
            if (buildTool.run(commandId, waitFinished, warnUser)) {
                run = true;
            }
        }
        return run;
    }

    /**
     * Check whether any build tool supports (is enabled in) the given project.
     * @param project project to be checked
     * @return {@code true} if any build tool supports the given project, {@code false} otherwise
     * @since 1.82
     */
    public boolean hasBuildTools(@NonNull Project project) {
        Parameters.notNull("project", project); // NOI18N
        return !getEnabledBuildTools(project).isEmpty();
    }

    /**
     * Helper method for creating standard UI component for build tool customizer.
     * @param customizerSupport support for the UI component
     * @return standard UI component for build tool customizer
     * @since 1.87
     */
    public JComponent createCustomizerComponent(@NonNull CustomizerSupport customizerSupport) {
        Parameters.notNull("customizerSupport", customizerSupport); // NOI18N
        return new CustomizerPanel(customizerSupport);
    }

    private Collection<BuildToolImplementation> getEnabledBuildTools(Project project) {
        assert project != null;
        Collection<? extends BuildToolImplementation> allBuildTools = project.getLookup()
                .lookupAll(BuildToolImplementation.class);
        List<BuildToolImplementation> enabledBuildTools = new ArrayList<>(allBuildTools.size());
        for (BuildToolImplementation buildTool : allBuildTools) {
            if (buildTool.isEnabled()) {
                enabledBuildTools.add(buildTool);
            }
        }
        return enabledBuildTools;
    }

    //~ Inner classes

    /**
     * Support for standard UI component for build tool customizer.
     * @since 1.87
     */
    public interface CustomizerSupport {

        /**
         * Get customizer category.
         * @return customizer category
         */
        @NonNull
        ProjectCustomizer.Category getCategory();

        /**
         * Get header which will be shown in the top of UI component.
         * @return header which will be shown in the top of UI component
         */
        @NonNull
        String getHeader();

        /**
         * Get task for the given command identifier. Can return {@code null}
         * if none assigned.
         * <p>
         * Note: This method is called in the UI thread.
         * @param commandId command identifier
         * @return task for the given command identifier, can be {@code null} if none assigned
         */
        @CheckForNull
        String getTask(@NonNull String commandId);

        /**
         * Set the given task for the given command identifier. Task can
         * be {@code null} if none assigned.
         * <p>
         * Note: This method is called in a background thread.
         * @param commandId command identifier
         * @param task task for the given command identifier, can be {@code null} if none assigned
         */
        void setTask(@NonNull String commandId, @NullAllowed String task);

    }

}
