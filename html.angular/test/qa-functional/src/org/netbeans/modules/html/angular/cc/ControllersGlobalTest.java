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
package org.netbeans.modules.html.angular.cc;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import junit.framework.Test;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.modules.html.angular.GeneralAngular;

/**
 *
 * @author Vladimir Riha
 */
public class ControllersGlobalTest extends GeneralAngular {

    static final String[] tests = new String[]{
        "openProject",
        "testNgClick",
        "testExpression2",
        "testDirective6",
        "testDirective7",
        "testDirective8",
        "testDirective9",
        "testExpression12",
        "testExpression14",
        "testExpression16",
        "testExpression18",
        "testDirective20",
        "testNgBind",
        "testNgModel",
        "testControllers",
        "testGoTo28",
        "testGoTo29",
        "testGoTo30",
        "testGoTo31",
        //                        "testGoTo32",
        "testGoTo33",
        //                        "testGoTo34",
        //                        "testGoTo35",
        //                        "testGoTo36",
        "testGoTo37",
        "testGoToPartial",
        "testGoToController"
    };

    public ControllersGlobalTest(String args) {
        super(args);
    }

    public static Test suite() {
        return createModuleTest(ControllersGlobalTest.class, tests);
    }

    public void openProject() throws Exception {
        startTest();
        JemmyProperties.setCurrentTimeout("ActionProducer.MaxActionTime", 180000);
        openDataProjects("ctrlglobal");
        evt.waitNoEvent(2000);
        openFile("partials|partial1.html", "ctrlglobal");
        waitScanFinished();
        ControllersGlobalTest.originalContent = new EditorOperator("partial1.html").getText();
        endTest();
    }

    public void testExpression2() throws Exception {
        startTest();
        testCompletionWithNegativeCheck(new EditorOperator("partial1.html"), 2);
        endTest();
    }

    public void testDirective6() throws Exception {
        startTest();
        testCompletionWithNegativeCheck(new EditorOperator("partial1.html"), 6);
        endTest();
    }

    public void testDirective7() throws Exception {
        startTest();
        testCompletionWithNegativeCheck(new EditorOperator("partial1.html"), 7);
        endTest();
    }

    public void testDirective8() throws Exception {
        startTest();
        testCompletionWithNegativeCheck(new EditorOperator("partial1.html"), 8);
        endTest();
    }

    public void testDirective9() throws Exception {
        startTest();
        testCompletionWithNegativeCheck(new EditorOperator("partial1.html"), 9);
        endTest();
    }

    public void testExpression12() throws Exception {
        startTest();
        testCompletionWithNegativeCheck(new EditorOperator("partial1.html"), 12);
        endTest();
    }

    public void testExpression14() throws Exception {
        startTest();
        testCompletionWithNegativeCheck(new EditorOperator("partial1.html"), 14);
        endTest();
    }

    public void testExpression16() throws Exception {
        startTest();
        testCompletionWithNegativeCheck(new EditorOperator("partial1.html"), 16);
        endTest();
    }

    public void testExpression18() throws Exception {
        startTest();
        testCompletionWithNegativeCheck(new EditorOperator("partial1.html"), 18);
        endTest();
    }

    public void testDirective20() throws Exception {
        startTest();
        testCompletionWithNegativeCheck(new EditorOperator("partial1.html"), 20);
        endTest();
    }

    public void testNgClick() throws Exception {
        startTest();
        testCompletionWithNegativeCheck(new EditorOperator("partial1.html"), 1);
        endTest();
    }

    public void testNgBind() throws Exception {
        startTest();
        testCompletionWithNegativeCheck(new EditorOperator("partial1.html"), 24);
        endTest();
    }

    public void testNgModel() throws Exception {
        startTest();
        testCompletionWithNegativeCheck(new EditorOperator("partial1.html"), 23);
        endTest();
    }

    public void testControllers() throws Exception {
        startTest();
        testCompletionWithNegativeCheck(new EditorOperator("partial1.html"), 26);
        endTest();
    }

    public void testGoTo28() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("partial1.html"), 28);
        endTest();
    }

    public void testGoTo29() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("partial1.html"), 29);
        endTest();
    }

    public void testGoTo30() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("partial1.html"), 30);
        endTest();
    }

    public void testGoTo31() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("partial1.html"), 31);
        endTest();
    }

    public void testGoTo32() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("partial1.html"), 32);
        endTest();
    }

    public void testGoTo33() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("partial1.html"), 33);
        endTest();
    }

    public void testGoTo34() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("partial1.html"), 34);
        endTest();
    }

    public void testGoTo35() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("partial1.html"), 35);
        endTest();
    }

    public void testGoTo36() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("partial1.html"), 36);
        endTest();
    }

    public void testGoTo37() throws Exception {
        startTest();
        testGoToDeclaration(new EditorOperator("partial1.html"), 37);
        endTest();
    }

    public void testGoToPartial() {
        openFile("js|app.js", "ctrlglobal");
        EditorOperator app = new EditorOperator("app.js");
        app.setCaretPosition("ial1", true);
        new org.netbeans.jellytools.actions.Action(null, null, KeyStroke.getKeyStroke(KeyEvent.VK_B, 2)).performShortcut(app);
        evt.waitNoEvent(500);
        EditorOperator ed = new EditorOperator("partial1.html");
        int position = ed.txtEditorPane().getCaretPosition();
        ed.setCaretPosition(1, 1);
        int expectedPosition = ed.txtEditorPane().getCaretPosition();
        assertTrue("Incorrect caret position. Expected position " + expectedPosition + " but was " + position, position == expectedPosition);
    }

    public void testGoToController() {
        EditorOperator app = new EditorOperator("app.js");
        app.setCaretPosition("rl1", true);
        new org.netbeans.jellytools.actions.Action(null, null, KeyStroke.getKeyStroke(KeyEvent.VK_B, 2)).performShortcut(app);
        evt.waitNoEvent(500);
        EditorOperator ed = new EditorOperator("controllers.js");
        int position = ed.txtEditorPane().getCaretPosition();
        ed.setCaretPosition(5, 10);
        int expectedPosition = ed.txtEditorPane().getCaretPosition();
        assertTrue("Incorrect caret position. Expected position " + expectedPosition + " but was " + position, position == expectedPosition);
    }

    @Override
    public void tearDown() throws Exception {
        EditorOperator eo = new EditorOperator("partial1.html");
        eo.typeKey('a', InputEvent.CTRL_MASK);
        eo.pressKey(KeyEvent.VK_DELETE);
        eo.insert(ControllersGlobalTest.originalContent);
//        eo.save();
        eo.pressKey(KeyEvent.VK_ESCAPE);
        evt.waitNoEvent(1500);
    }

}
