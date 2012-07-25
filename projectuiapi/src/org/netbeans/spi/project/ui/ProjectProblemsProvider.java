/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.spi.project.ui;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.concurrent.Future;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.spi.project.ui.support.UILookupMergerSupport;
import org.openide.util.Parameters;

/**
 * Provider of project metadata problems.
 * The provider of various project metadata problems like broken reference to source root,
 * broken reference to a library, wrong compiler options, etc.
 * The provider implementation(s) are registered into the project lookup as well as
 * the {@link org.netbeans.spi.project.LookupMerger} for them
 * {@link UILookupMergerSupport#createProjectProblemsProviderMerger}.
 *
 * <div class="nonnormative">
 * <p>The presence of the {@link ProjectProblemsProvider} in the project lookup
 * automatically enable the broken project metadata badge on the project.
 * If the project wants to provide the "Resolve Broken Project" action it needs
 * to add a reference to the "org.netbeans.modules.project.ui.problems.BrokenProjectActionFactory"
 * action with required position, for example using the ActionRefrecne annotation:
 * <pre>
 * &#64;ActionReferences({
    &#64;ActionReference(
        id=&#64;ActionID(id="org.netbeans.modules.project.ui.problems.BrokenProjectActionFactory",category="Project"),
        position = 2600,
        path = "Projects/org-netbeans-modules-myproject/Actions")
})
 * </pre>
 * </p>
 * </div>
 *
 * @author Tomas Zezula
 * @since 1.60
 */
public interface ProjectProblemsProvider {

    /**
     * Name of the problems property.
     */
    String PROP_PROBLEMS = "problems";  //NOI18N


    /**
     * Adds a {@link PropertyChangeListener} listening on change of project
     * metadata problems.
     * @param listener the listener to be added.
     */
    void addPropertyChangeListener(@NonNull PropertyChangeListener listener);

    /**
     * Removes a {@link PropertyChangeListener} listening on change of project
     * metadata problems.
     * @param listener the listener to be removed.
     */
    void removePropertyChangeListener (@NonNull PropertyChangeListener listener);

    /**
     * Returns project metadata problems found by this {@link ProjectProblemsProvider}.
     * @return the problems
     */
    @NonNull
    Collection<? extends ProjectProblem> getProblems();


    /**
     * The {@link ProjectProblem} resolution status.
     */
    enum Status {
        RESOLVED,
        RESOLVED_WITH_WARNING,
        UNRESOLVED
    }


    /**
     * The {@link ProjectProblem} severity.
     */
    enum Severity {
        ERROR,
        WARNING
    }

    /**
     * Result of the project metadata problem resolution.
     */
    public final class Result {
        private final Status status;
        private final String message;

        private Result(
            @NonNull final Status status,
            @NullAllowed final String message) {
            this.status = status;
            this.message = message;
        }

        /**
         * Returns true if the problem was resolved.
         * @return true if the problem was successfully resolved.
         */
        public boolean isResolved() {
            return status != Status.UNRESOLVED;
        }

        /**
         * Returns status of the resolution.
         * @return the {@link ProjectProblemsProvider.Status}
         */
        @NonNull
        public Status getStatus() {
            return status;
        }

        /**
         * Returns possible error or warning message.
         * @return the message which should be presented to the user.
         */
        @CheckForNull
        public String getMessage() {
            return message;
        }

        /**
         * Creates a new instance of the {@link Result}.
         * @param status the status of the project problem resolution.
         * @return the new {@link Result} instance.
         */
        public static Result create(
            @NonNull final Status status) {
            Parameters.notNull("status", status);   //NOI18N
            return new Result(status, null);
        }

        /**
         * Creates a new instance of the {@link Result}.
         * @param status the status of the project problem resolution.
         * @param message the message which should be presented to the user.
         * @return the new {@link Result} instance.
         */
        public static Result create(
            @NonNull final Status status,
            @NonNull final String message) {
            Parameters.notNull("status", status);   //NOI18N
            Parameters.notNull("message", message);   //NOI18N
            return new Result(status, message);
        }

    }


    /**
     * Project metadata problem.
     * Represents a problem in the project metadata which should be presented
     * to the user and resolved.
     */
    public final class ProjectProblem {

        private final Severity severity;
        private final String displayName;
        private final String description;
        private final ProjectProblemResolver resolver;

        private ProjectProblem(
                @NonNull final Severity severity,
                @NonNull final String displayName,
                @NonNull final String description,
                @NonNull final ProjectProblemResolver resolver) {
            Parameters.notNull("severity", severity); //NOI18N
            Parameters.notNull("displayName", displayName); //NOI18N
            Parameters.notNull("description", description); //NOI18N
            Parameters.notNull("resolver", resolver);   //NOI18N
            this.severity = severity;
            this.displayName = displayName;
            this.description = description;
            this.resolver = resolver;
        }

        /**
         * Returns a {@link ProjectProblem} severity.
         * @return the {@link Severity}
         */
        @NonNull
        public Severity getSeverity() {
            return severity;
        }

        /**
         * Returns a display name of the problem.
         * The display name is presented to the user in the UI.
         * @return the display name.
         */
        @NonNull
        public String getDisplayName() {
            return displayName;
        }        

        /**
         * Returns the description of the problem.
         * The description is shown in the project problems details.
         * @return project problem description.
         */
        @NonNull
        public String getDescription() {
            return description;
        }

        /**
         * Resolves the problem.
         * Called by the Event Dispatch Thread.
         * When the resolution needs to be done by a background thread, eg. downloading
         * an archive from repository, the implementation directly returns
         * a {@link Future} which is completed by the background thread.
         * @return the {@link Future} holding the problem resolution status.
         */
        public Future<Result> resolve() {
            return resolver.resolve();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof ProjectProblem)) {
                return false;
            }
            final  ProjectProblem otherProblem = (ProjectProblem) other;
            return displayName.equals(otherProblem.displayName) &&
                description.equals(otherProblem.description) &&
                resolver.equals(otherProblem.resolver);
        }

        /**
         * {@inheritdoc}
         */
        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + displayName.hashCode();
            result = 31 * result + description.hashCode();
            result = 31 * result + resolver.hashCode();
            return result;
        }

        /**
         * {@inheritdoc}
         */
        @Override
        public String toString() {
            return String.format(
             "Project Problem: %s, resolvable by: %s",  //NOI18N
             displayName,
             resolver);
        }


        /**
         * Creates a new instance of the {@link ProjectProblem} with error {@link Severity}.
         * @param displayName the project problem display name.
         * @param description the project problem description.
         * @param resolver the {@link ProjectProblemResolver} to resolve the problem.
         * @return a new instance of {@link ProjectProblem}
         */
        @NonNull
        public static ProjectProblem createError(
                @NonNull final String displayName,
                @NonNull final String description,
                @NonNull final ProjectProblemResolver resolver) {            
            return new ProjectProblem(Severity.ERROR, displayName,description,resolver);
        }

        /**
         * Creates a new instance of the {@link ProjectProblem} with warning {@link Severity}.
         * @param displayName the project problem display name.
         * @param description the project problem description.
         * @param resolver the {@link ProjectProblemResolver} to resolve the problem.
         * @return a new instance of {@link ProjectProblem}
         */
        @NonNull
        public static ProjectProblem createWarning(
                @NonNull final String displayName,
                @NonNull final String description,
                @NonNull final ProjectProblemResolver resolver) {
            return new ProjectProblem(Severity.WARNING, displayName,description,resolver);
        }

    }

}
