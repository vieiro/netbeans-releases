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
package org.netbeans.performance.j2se.actions;

import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import junit.framework.Test;
import org.netbeans.modules.performance.utilities.PerformanceTestCase;
import org.netbeans.performance.j2se.setup.J2SESetup;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.EditorWindowOperator;
import static org.netbeans.jellytools.JellyTestCase.emptyConfiguration;
import org.netbeans.jellytools.actions.Action;
import org.netbeans.jellytools.actions.OpenAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.nodes.SourcePackagesNode;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.modules.performance.guitracker.LoggingRepaintManager;

/**
 * Test of Page Up and Page Down in opened source editor.
 *
 * @author anebuzelsky@netbeans.org
 */
public class PageUpPageDownInEditorTest extends PerformanceTestCase {

    private boolean pgup;
    private EditorOperator editorOperator;

    /**
     * Creates a new instance of PageUpPageDownInEditor
     *
     * @param testName test name
     */
    public PageUpPageDownInEditorTest(String testName) {
        super(testName);
        expectedTime = 100;
        WAIT_AFTER_OPEN = 200;
        pgup = true;
    }

    /**
     * Creates a new instance of PageUpPageDownInEditor
     *
     * @param testName test name
     * @param performanceDataName data name
     */
    public PageUpPageDownInEditorTest(String testName, String performanceDataName) {
        super(testName, performanceDataName);
        expectedTime = 100;
        WAIT_AFTER_OPEN = 200;
    }

    public static Test suite() {
        return emptyConfiguration()
                .addTest(J2SESetup.class, "testCloseMemoryToolbar", "testOpenDataProject")
                .addTest(PageUpPageDownInEditorTest.class)
                .suite();
    }

    public void testPageUp() {
        pgup = true;
        doMeasurement();
    }

    public void testPageDown() {
        pgup = false;
        doMeasurement();
    }

    @Override
    public void initialize() {
        EditorOperator.closeDiscardAll();
        repaintManager().addRegionFilter(LoggingRepaintManager.EDITOR_FILTER);
        new OpenAction().performAPI(new Node(new SourcePackagesNode("PerformanceTestData"), "org.netbeans.test.performance|Main20kB.java"));
        editorOperator = EditorWindowOperator.getEditor("Main20kB.java");
    }

    @Override
    public void prepare() {
        if (pgup) {
            new Action(null, null, KeyStroke.getKeyStroke(KeyEvent.VK_END, KeyEvent.CTRL_MASK)).perform(editorOperator);
        } else {
            editorOperator.setCaretPositionToLine(1);
        }
    }

    @Override
    public ComponentOperator open() {
        if (pgup) {
            new Action(null, null, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0)).perform(editorOperator);
        } else {
            new Action(null, null, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0)).perform(editorOperator);
        }
        return null;
    }

    @Override
    protected void shutdown() {
        super.shutdown();
        repaintManager().resetRegionFilters();
    }
}
