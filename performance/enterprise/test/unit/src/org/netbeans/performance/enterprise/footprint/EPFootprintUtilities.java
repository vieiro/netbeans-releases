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

package org.netbeans.performance.enterprise.footprint;

import org.netbeans.jellytools.NewWebProjectNameLocationStepOperator;
import org.netbeans.jellytools.NewProjectWizardOperator;
import org.netbeans.jellytools.ProjectsTabOperator;
import org.netbeans.jellytools.RuntimeTabOperator;
import org.netbeans.jellytools.nodes.Node;

import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.operators.JCheckBoxOperator;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.modules.performance.utilities.CommonUtilities;
//import org.netbeans.performance.enterprise.EPUtilities;


/**
 * Utilities for Memory footprint tests
 *
 * @author  anebuzelsky@netbeans.org, mmirilovic@netbeans.org
 */
public class EPFootprintUtilities extends CommonUtilities {
    
    static String creatJ2EEeproject(String category, String project, boolean wait) {
        return createProjectGeneral(category, project, wait, true);
    }
    
    private static String createProjectGeneral(String category, String project, boolean wait, boolean j2eeProject) {
        // select Projects tab
        ProjectsTabOperator.invoke();
        
        // create a project
        NewProjectWizardOperator wizard = NewProjectWizardOperator.invoke();
        wizard.selectCategory(category);
        wizard.selectProject(project);
        wizard.next();

        NewWebProjectNameLocationStepOperator wizard_location = new NewWebProjectNameLocationStepOperator();
        wizard_location.txtProjectLocation().clearText();
        wizard_location.txtProjectLocation().typeText(CommonUtilities.getTempDir());
        String pname = wizard_location.txtProjectName().getText();
        
        pname = pname + "_" + System.currentTimeMillis();
        wizard_location.txtProjectName().clearText();
        wizard_location.txtProjectName().typeText(pname);
        
        wizard.next();
        
        if(j2eeProject) {
            new JComboBoxOperator(wizard,1).selectItem(1);
            new JCheckBoxOperator(wizard,"Create Application Client module:").setSelected(true);
        }
        
        new EventTool().waitNoEvent(1000);
        wizard.finish();

        // wait 30 seconds
        waitForProjectCreation(30000, wait);
        
        return pname;
    }
    
    public static void killRunOnProject(String project) {
        killProcessOnProject(project, "run");
    }
    
    public static void killDebugOnProject(String project) {
        killProcessOnProject(project, "debug");
    }
    
    private static void killProcessOnProject(String project, String process) {
        // prepare Runtime tab
        RuntimeTabOperator runtime = RuntimeTabOperator.invoke();
        
        // kill the execution
        Node node = new Node(runtime.getRootNode(), "Processes|"+project+ " (" + process + ")");
        node.select();
        node.performPopupAction("Terminate Process");
    }
}
