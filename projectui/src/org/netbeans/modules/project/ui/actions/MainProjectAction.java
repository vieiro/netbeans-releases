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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.project.ui.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.Icon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.project.ui.OpenProjectList;
import static org.netbeans.modules.project.ui.actions.Bundle.*;
import org.netbeans.spi.project.ui.support.ProjectActionPerformer;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.Actions;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.NbBundle.Messages;
import org.openide.util.WeakListeners;

/**
 * Similar to {@link ProjectAction} but has a different selection model.
 * First uses the main project, if set.
 * Else uses the selected projects, if any.
 * Finally, if just one project is open, uses that.
 */
public class MainProjectAction extends LookupSensitiveAction implements PropertyChangeListener {

    private String command;
    private ProjectActionPerformer performer;
    private String name;

    public MainProjectAction(ProjectActionPerformer performer, String name, Icon icon) {
        this( null, performer, name, icon );
    }

    public MainProjectAction(String command, String name, Icon icon) {
        this( command, null, name, icon );
    }

    @SuppressWarnings("LeakingThisInConstructor")
    private MainProjectAction(String command, ProjectActionPerformer performer, String name, Icon icon) {

        super(icon, null, new Class<?>[] {Project.class, DataObject.class});
        this.command = command;
        this.performer = performer;
        this.name = name;

        String presenterName = "";
        if (name != null) {
            presenterName = MessageFormat.format(name, -1);
        }
        setDisplayName(presenterName);
        if ( icon != null ) {
            setSmallIcon( icon );
        }

        // Start listening on open projects list to correctly enable the action
        OpenProjectList.getDefault().addPropertyChangeListener( WeakListeners.propertyChange( this, OpenProjectList.getDefault() ) );
        // XXX #47160: listen to changes in supported commands on current project, when that becomes possible
    }

    @Override
    protected boolean init() {
        boolean needsInit = super.init();
        if (needsInit) {
            refreshView(null, true);
        }
        return needsInit;
    }

    @Messages("MainProjectAction.no_main=Set a main project, or select one project or project file, or keep just one project open.")
    public @Override void actionPerformed(Lookup context) {
        Project mainProject = OpenProjectList.getDefault().getMainProject();
        Project[] projects = selection(mainProject, context);

        // if no main project or no selected or more than one project opened,
        // then show warning
        if (projects.length == 0) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(MainProjectAction_no_main(), NotifyDescriptor.WARNING_MESSAGE));
            return;
        }

        if (command != null && projects.length > 0) {
            ProjectAction.runSequentially(new LinkedList<Project>(Arrays.asList(projects)), this, command);
        } else if (performer != null && projects.length == 1) {
            performer.perform(projects[0]);
        }
    }

    public @Override void propertyChange( PropertyChangeEvent evt ) {
        if (OpenProjectList.PROPERTY_MAIN_PROJECT.equals(evt.getPropertyName()) ||
            OpenProjectList.PROPERTY_OPEN_PROJECTS.equals(evt.getPropertyName())) {
            refreshView(null, false);
        }
    }

    private Project[] selection(Project mainProject, Lookup context) {
        if (mainProject != null) {
            return new Project[] {mainProject};
        }
        Lookup theContext = context;
        if (theContext == null) {
            theContext = LastActivatedWindowLookup.INSTANCE;
        }
        if (theContext != null) {
            Project[] projects = ActionsUtil.getProjectsFromLookup(theContext, command);
            if (projects.length > 0) {
                return projects;
            }
        }
        Project[] projects = OpenProjects.getDefault().getOpenProjects();
        if (projects.length == 1) {
            return projects;
        }
        return new Project[0];
    }

    private void refreshView(final Lookup context, boolean immediate) {
        Runnable r= new Runnable() {
            public @Override void run() {

        Project mainProject = OpenProjectList.getDefault().getMainProject();
        Project[] projects = selection(mainProject, context);

        final String presenterName = getPresenterName(name, mainProject, projects);
        final boolean enabled;

        if ( command == null ) {
            enabled = projects.length == 1 && performer.enable(projects[0]);
        }
        else if (projects.length == 0) {
            enabled = false;
        } else {
            boolean e = true;
            for (Project p : projects) {
                if (!ActionsUtil.commandSupported(p, command, Lookup.EMPTY)) {
                    e = false;
                    break;
                }
            }
            enabled = e;
        }

        Mutex.EVENT.writeAccess(new Runnable() {
            public @Override void run() {
        putValue("menuText", presenterName);
        putValue(SHORT_DESCRIPTION, Actions.cutAmpersand(presenterName));
        setEnabled(enabled);
            }
        });
            }
        };
        if (immediate) {
            r.run();
        } else {
            RP.post(r);
        }
    }

    private String getPresenterName(String name, Project mPrj, Project[] cPrj) {
        if (name == null) {
            return "";
        } else if (mPrj == null) {
            return ActionsUtil.formatProjectSensitiveName(name, cPrj);
        } else {
            return MessageFormat.format(name, -1);
        }
    }

    @Override
    protected void refresh(Lookup context, boolean immediate) {
        refreshView(context, immediate);
    }

}
