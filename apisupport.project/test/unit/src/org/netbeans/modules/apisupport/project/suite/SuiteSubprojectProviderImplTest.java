/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.apisupport.project.suite;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.apisupport.project.NbModuleProject;
import org.netbeans.modules.apisupport.project.TestBase;
import org.netbeans.modules.apisupport.project.ui.customizer.SuiteUtils;
import org.netbeans.spi.project.SubprojectProvider;

/**
 * @author Martin Krauskopf
 */
public class SuiteSubprojectProviderImplTest extends TestBase {

    public SuiteSubprojectProviderImplTest(String testName) {
        super(testName);
    }
    
    public void testGetSubprojects() throws Exception {
        SuiteProject s = generateSuite("suite");
        SubprojectProvider spp = s.getLookup().lookup(SubprojectProvider.class);
        assertEquals("suite doesn't have any submodules", 0, spp.getSubprojects().size());
        NbModuleProject module1 = generateSuiteComponent(s, "module1");
        assertEquals("suite has one submodule", 1, spp.getSubprojects().size());
        SuiteUtils.removeModuleFromSuite(module1);
        assertEquals("suite doesn't have any submodules", 0, spp.getSubprojects().size());
        generateSuiteComponent(s, "module2");
        generateSuiteComponent(s, "module3");
        assertEquals("suite has two submodules", 2, spp.getSubprojects().size());
    }
    
    public void testChangeListener() throws Exception {
        SuiteProject s = generateSuite("suite");
        SubprojectProvider spp = s.getLookup().lookup(SubprojectProvider.class);
        SPPChangeListener l = new SPPChangeListener();
        spp.addChangeListener(l);
        NbModuleProject module1 = generateSuiteComponent(s, "module1");
        assertTrue("change was noticed", l.changed);
        assertEquals("suite has one submodule", 1, spp.getSubprojects().size());
        l.changed = false;
        SuiteUtils.removeModuleFromSuite(module1);
        assertTrue("change was noticed", l.changed);
        l.changed = false;
        assertEquals("suite doesn't have any submodules", 0, spp.getSubprojects().size());
        spp.removeChangeListener(l);
        generateSuiteComponent(s, "module2");
        assertFalse("change was noticed", l.changed);
        assertEquals("suite has one submodule", 1, spp.getSubprojects().size());
    }
    
    private final class SPPChangeListener implements ChangeListener {
        
        boolean changed;
        
        public void stateChanged(ChangeEvent e) {
            changed = true;
        }
        
    }
    
}
