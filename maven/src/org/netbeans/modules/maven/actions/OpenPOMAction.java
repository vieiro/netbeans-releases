/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.netbeans.api.actions.Openable;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.ContextAwareAction;
import org.openide.util.NbBundle.Messages;

@ActionID(category="Project", id="org.netbeans.modules.maven.actions.openpom")
@ActionRegistration(displayName="#BTN_open_pom")
@ActionReference(path="Projects/org-netbeans-modules-maven/Actions", position=1650, separatorAfter=1655)
@Messages("BTN_open_pom=Open POM")
public class OpenPOMAction implements ActionListener {

    public static ContextAwareAction instance() {
        ContextAwareAction a = FileUtil.getConfigObject("Actions/Project/org-netbeans-modules-maven-actions-openpom.instance", ContextAwareAction.class); // XXX #205798
        assert a != null;
        return a;
    }

    private final List<NbMavenProjectImpl> projects;

    public OpenPOMAction(List<NbMavenProjectImpl> projects) {
        this.projects = projects;
    }

    @Override public void actionPerformed(ActionEvent e) {
        for (NbMavenProjectImpl project : projects) {
            FileObject pom = FileUtil.toFileObject(project.getPOMFile());
            if (pom != null) {
                DataObject d;
                try {
                    d = DataObject.find(pom);
                } catch (DataObjectNotFoundException x) {
                    continue;
                }
                Openable o = d.getLookup().lookup(Openable.class);
                if (o != null) {
                    o.open();
                }
            }
        }
    }

}
