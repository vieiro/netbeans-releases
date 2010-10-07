/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.editor.indent;

/**
 *
 * @author Petr Pisl
 */

import java.util.prefs.Preferences;
import javax.swing.JEditorPane;
import javax.swing.text.Caret;
import javax.swing.text.DefaultEditorKit;
import org.netbeans.api.html.lexer.HTMLTokenId;
import org.netbeans.editor.BaseDocument;
import org.netbeans.lib.lexer.test.TestLanguageProvider;
import org.netbeans.modules.csl.api.Formatter;
import org.netbeans.modules.editor.indent.spi.CodeStylePreferences;
import org.netbeans.modules.php.editor.PHPTestBase;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.openide.filesystems.FileObject;

public class PHPBracketCompleterFileBasedTest extends PHPTestBase {

    public PHPBracketCompleterFileBasedTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
	//System.setProperty("org.netbeans.editor.linewrap.disable", "true");
        try {
            TestLanguageProvider.register(HTMLTokenId.language());
        } catch (IllegalStateException ise) {
            // Ignore -- we've already registered this either via layers or other means
        }
        try {
            TestLanguageProvider.register(PHPTokenId.language());
        } catch (IllegalStateException ise) {
            // Ignore -- we've already registered this either via layers or other means
        }
    }

    @Override
    protected boolean runInEQ() {
        return true;
    }

    protected void testIndentInFile(String file) throws Exception {
        testIndentInFile(file, null, 0);
    }

    protected void testIndentInFile(String file, IndentPrefs preferences, int initialIndent) throws Exception {
        FileObject fo = getTestFile(file);
        assertNotNull(fo);
        String source = readFile(fo);

        int sourcePos = source.indexOf('^');
        assertNotNull(sourcePos);
        String sourceWithoutMarker = source.substring(0, sourcePos) + source.substring(sourcePos+1);
        Formatter formatter = getFormatter(preferences);

        JEditorPane ta = getPane(sourceWithoutMarker);
        Caret caret = ta.getCaret();
        caret.setDot(sourcePos);
        BaseDocument doc = (BaseDocument) ta.getDocument();
        if (formatter != null) {
            configureIndenters(doc, formatter, true);
        }

        setupDocumentIndentation(doc, preferences);


        Preferences prefs = CodeStylePreferences.get(doc).getPreferences();
        prefs.putInt(FmtOptions.initialIndent, initialIndent);

        runKitAction(ta, DefaultEditorKit.insertBreakAction, "\n");

        doc.getText(0, doc.getLength());
        doc.insertString(caret.getDot(), "^", null);

        String target = doc.getText(0, doc.getLength());
        assertDescriptionMatches(file, target, false, ".indented");
    }

    public void testAlternativeSyntaxFor_01()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/alternativeSyntaxFor_01.php");
    }

    public void testAlternativeSyntaxFor_02()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/alternativeSyntaxFor_02.php");
    }

    public void testAlternativeSyntaxFor_03()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/alternativeSyntaxFor_03.php");
    }

    public void testAlternativeSyntaxFor_04()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/alternativeSyntaxFor_04.php");
    }

    public void testAlternativeSyntaxForEach_01()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/alternativeSyntaxForEach_01.php");
    }

    public void testAlternativeSyntaxForEach_02()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/alternativeSyntaxForEach_02.php");
    }

    public void testAlternativeSyntaxWhile_01()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/alternativeSyntaxWhile_01.php");
    }

    public void testAlternativeSyntaxWhile_02()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/alternativeSyntaxWhile_02.php");
    }

    public void testAlternativeSyntaxWhile_03()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/alternativeSyntaxWhile_03.php");
    }

    public void testAlternativeSyntaxSwitch_01()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/alternativeSyntaxSwitch_01.php");
    }

    public void testAlternativeSyntaxSwitch_02()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/alternativeSyntaxSwitch_02.php");
    }

    public void testAlternativeSyntaxSwitch_03()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/alternativeSyntaxSwitch_03.php");
    }

    public void testAlternativeSyntaxIf_01()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/alternativeSyntaxIf_01.php");
    }

    public void testAlternativeSyntaxIf_02()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/alternativeSyntaxIf_02.php");
    }

    public void testAlternativeSyntaxIf_03()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/alternativeSyntaxIf_03.php");
    }

    public void testAlternativeSyntaxIf_04()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/alternativeSyntaxIf_04.php");
    }

    public void testAlternativeSyntaxIf_05()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/alternativeSyntaxIf_05.php");
    }

    public void testAlternativeSyntaxIf_06()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/alternativeSyntaxIf_06.php");
    }

    public void testIssue167816_01()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/issue167816_01.php");
    }

    public void testIssue167816_02()throws Exception {
        testIndentInFile("testfiles/bracketCompleter/issue167816_02.php");
    }
}
