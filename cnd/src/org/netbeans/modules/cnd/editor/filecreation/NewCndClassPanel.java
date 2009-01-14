/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.editor.filecreation;

import java.awt.Component;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.cnd.settings.CppSettings;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author sg155630
 */
public class NewCndClassPanel extends CndPanel {
    
    NewCndClassPanel(Project project, SourceGroup[] folders, WizardDescriptor.Panel<WizardDescriptor> bottomPanel) {
        super(project, folders, bottomPanel);
    }

    public Component getComponent() {
        if (gui == null) {
            gui = new NewCndClassPanelGUI(project, folders, bottomPanel == null ? null : bottomPanel.getComponent());
            gui.addChangeListener(this);
        }
        return gui;
    }

    @Override
    protected void doStoreSettings(WizardDescriptor settings) {
        String table = (CppSettings.findObject(CppSettings.class, true)).getReplaceableStringsTable();
        table +="\nCLASSNAME=" + getGui().getClassName(); // NOI18N
        table +="\nDEFAULT_HEADER_EXT=" + getGui().getHeaderExt(); // NOI18N
        (CppSettings.findObject(CppSettings.class, true)).setReplaceableStringsTable(table);
        settings.putProperty("headerFileName", getGui().getHeaderFileName()); // NOI18N
    }
    
    NewCndClassPanelGUI getGui() {
        return (NewCndClassPanelGUI)gui;
    }

    @Override
    public boolean isValid() {
        boolean ok = super.isValid(); 
        
        if (!ok) {
            setErrorMessage (""); // NOI18N

            return false;
        }
        if (!org.openide.util.Utilities.isJavaIdentifier( getGui().getClassName() )) {
            setErrorMessage( NbBundle.getMessage(NewCndClassPanel.class, "MSG_not_valid_classname") );
            return false;
        }
        // check if the file name can be created
        String errorMessage = canUseFileName(getGui().getTargetGroup().getRootFolder(), getGui().getTargetFolder(), getGui().getSourceFileName(), false);
        if (errorMessage == null) {
            errorMessage = canUseFileName(getGui().getTargetGroup().getRootFolder(), getGui().getTargetFolder(), getGui().getHeaderFileName(), false);
        }
        setErrorMessage(errorMessage);

        return errorMessage == null;
    }
}
