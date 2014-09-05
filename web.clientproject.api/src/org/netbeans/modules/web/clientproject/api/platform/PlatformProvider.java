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
package org.netbeans.modules.web.clientproject.api.platform;

import java.net.URL;
import java.util.List;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.project.Project;
import org.netbeans.modules.web.clientproject.api.BadgeIcon;
import org.netbeans.modules.web.clientproject.platform.PlatformProviderAccessor;
import org.netbeans.modules.web.clientproject.spi.platform.PlatformProviderImplementation;
import org.netbeans.spi.project.ActionProvider;
import org.openide.util.Parameters;

/**
 * The API representation of a single provider for platform.
 * @since 1.68
 */
public final class PlatformProvider {

    /**
     * Property name for changes in enabled state.
     */
    public static final String PROP_ENABLED = PlatformProviderImplementation.PROP_ENABLED;


    private final PlatformProviderImplementation delegate;

    static {
        PlatformProviderAccessor.setDefault(new PlatformProviderAccessor() {

            @Override
            public PlatformProvider create(PlatformProviderImplementation platformProviderImplementation) {
                return new PlatformProvider(platformProviderImplementation);
            }
        });
    }

    private PlatformProvider(PlatformProviderImplementation delegate) {
        assert delegate != null;
        this.delegate = delegate;
    }

    /**
     * Returns the <b>non-localized (usually english)</b> identifier of this provider.
     * @return the <b>non-localized (usually english)</b> identifier; never {@code null}.
     */
    @NonNull
    public String getIdentifier() {
        return delegate.getIdentifier();
    }

    /**
     * Returns the display name of this provider. The display name is used
     * in the UI.
     * @return the display name; never {@code null}
     */
    @NonNull
    public String getDisplayName() {
        return delegate.getDisplayName();
    }

    /**
     * Returns badge icon of this provider.
     * @return badge icon of this provider, can be {@code null}
     */
    @CheckForNull
    public BadgeIcon getBadgeIcon() {
        return delegate.getBadgeIcon();
    }

    /**
     * Checks whether this provider is enabled in the given project.
     * @param project project to be checked
     * @return {@code true} if this provider is enabled in the given project, {@code false} otherwise
     */
    public boolean isEnabled(@NonNull Project project) {
        Parameters.notNull("project", project); // NOI18N
        return delegate.isEnabled(project);
    }

    /**
     * Gets list of source roots.
     * @param project project to be used
     * @return list of source roots, can be empty but never {@code null}
     */
    public List<URL> getSourceRoots(@NonNull Project project) {
        Parameters.notNull("project", project); // NOI18N
        return delegate.getSourceRoots(project);
    }

    /**
     * Get action provider of this provider.
     * @param project project to be source of the action
     * @return action provider of this provider, can be {@code null} if not supported
     */
    @CheckForNull
    public ActionProvider getActionProvider(@NonNull Project project) {
        Parameters.notNull("project", project); // NOI18N
        return delegate.getActionProvider(project);
    }

    /**
     * Notifies provider that the given project is being opened.
     * <p>
     * Provider is notified even if it is not {@link #isEnabled(Project) enabled} in the given project.
     * @param project project being opened
     */
    public void projectOpened(@NonNull Project project) {
        Parameters.notNull("project", project); // NOI18N
        delegate.projectOpened(project);
    }

    /**
     * Notifies provider that the given project is being closed.
     * <p>
     * Provider is notified even if it is not {@link #isEnabled(Project) enabled} in the given project.
     * @param project project being closed
     */
    public void projectClosed(@NonNull Project project) {
        Parameters.notNull("project", project); // NOI18N
        delegate.projectClosed(project);
    }

    PlatformProviderImplementation getDelegate() {
        return delegate;
    }

}

