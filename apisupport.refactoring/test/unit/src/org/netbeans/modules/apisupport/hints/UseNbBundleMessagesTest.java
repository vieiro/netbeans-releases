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

package org.netbeans.modules.apisupport.hints;

import java.net.URL;
import org.junit.Test;
import static org.netbeans.modules.apisupport.hints.Bundle.*;
import org.netbeans.modules.java.hints.test.api.HintTest;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

public class UseNbBundleMessagesTest {

    @Test public void regularWarning() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "class Test {\n" +
                       "    String m() {\n" +
                       "        return org.openide.util.NbBundle.getMessage(Test.class, \"somekey\");\n" +
                       "    }\n" +
                       "}\n").
                input("test/Bundle.properties", "somekey=text\n", false).
                run(UseNbBundleMessages.class).
                assertWarnings("3:41-3:51:warning:" + UseNbBundleMessages_error_text());
    }

    @Test public void nonConstClass() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "class Test {\n" +
                       "    String m() {\n" +
                       "        return org.openide.util.NbBundle.getMessage(getClass(), \"somekey\");\n" +
                       "    }\n" +
                       "}\n").
                input("test/Bundle.properties", "somekey=text\n", false).
                run(UseNbBundleMessages.class).
                findWarning("3:41-3:51:warning:" + UseNbBundleMessages_only_class_const()).
                assertFixes();
    }

    @Test public void wrongClass() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "class Test {\n" +
                       "    class Nested {\n" +
                       "        String m() {\n" +
                       "            return org.openide.util.NbBundle.getMessage(Nested.class, \"somekey\");\n" +
                       "        }\n" +
                       "    }\n" +
                       "}\n").
                input("test/Bundle.properties", "somekey=text\n", false).
                run(UseNbBundleMessages.class).
                findWarning("4:45-4:55:warning:" + UseNbBundleMessages_wrong_class_name("Test")).
                assertFixes();
    }

    @Test public void nonConstKey() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "class Test {\n" +
                       "    String m(String which) {\n" +
                       "        return org.openide.util.NbBundle.getMessage(Test.class, which + \"key\");\n" +
                       "    }\n" +
                       "}\n").
                input("test/Bundle.properties", "somekey=text\n", false).
                run(UseNbBundleMessages.class).
                findWarning("3:41-3:51:warning:" + UseNbBundleMessages_only_string_const()).
                assertFixes();
    }

    @Test public void noSuchBundle() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "class Test {\n" +
                       "    String m() {\n" +
                       "        return org.openide.util.NbBundle.getMessage(Test.class, \"somekey\");\n" +
                       "    }\n" +
                       "}\n").
                run(UseNbBundleMessages.class).
                findWarning("3:41-3:51:warning:" + UseNbBundleMessages_no_such_bundle("test/Bundle.properties")).
                assertFixes();
    }

    @Test public void noSuchKey() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "class Test {\n" +
                       "    String m() {\n" +
                       "        return org.openide.util.NbBundle.getMessage(Test.class, \"otherkey\");\n" +
                       "    }\n" +
                       "}\n").
                input("test/Bundle.properties", "somekey=text\n", false).
                run(UseNbBundleMessages.class).
                findWarning("3:41-3:51:warning:" + UseNbBundleMessages_no_such_key("otherkey")).
                assertFixes();
    }

    @Test public void simpleFix() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "class Test {\n" +
                       "    String m() {\n" +
                       "        return org.openide.util.NbBundle.getMessage(Test.class, \"somekey\");\n" +
                       "    }\n" +
                       "}\n").
                input("test/Bundle.properties", "somekey=text\n", false).
                run(UseNbBundleMessages.class).
                findWarning("3:41-3:51:warning:" + UseNbBundleMessages_error_text()).
                applyFix().
                /* XXX does not work because Bundle not yet generated & parsed:
                assertCompilable().
                */
                assertVerbatimOutput("test/Bundle.properties", "").
                assertOutput("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "import static test.Bundle.*;\n" +
                       "class Test {\n" +
                       "    @Messages(\"somekey=text\")\n" +
                       "    String m() {\n" +
                       "        return somekey();\n" +
                       "    }\n" +
                       "}\n");
    }

    @Test public void onConstructor() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "class Test {\n" +
                       "    Test() {\n" +
                       "        String k = org.openide.util.NbBundle.getMessage(Test.class, \"k\");\n" +
                       "    }\n" +
                       "}\n").
                input("test/Bundle.properties", "k=v\n", false).
                run(UseNbBundleMessages.class).
                findWarning("3:45-3:55:warning:" + UseNbBundleMessages_error_text()).
                applyFix().
                assertVerbatimOutput("test/Bundle.properties", "").
                assertOutput("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "import static test.Bundle.*;\n" +
                       "class Test {\n" +
                       "    @Messages(\"k=v\")\n" +
                       "    Test() {\n" +
                       "        String k = k();\n" +
                       "    }\n" +
                       "}\n");
    }

    @Test public void onField() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "class Test {\n" +
                       "    private static final String s = org.openide.util.NbBundle.getMessage(Test.class, \"k\");\n" +
                       "}\n").
                input("test/Bundle.properties", "k=v\n", false).
                run(UseNbBundleMessages.class).
                findWarning("2:62-2:72:warning:" + UseNbBundleMessages_error_text()).
                applyFix().
                assertVerbatimOutput("test/Bundle.properties", "").
                assertOutput("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "import static test.Bundle.*;\n" +
                       "class Test {\n" +
                       "    @Messages(\"k=v\")\n" +
                       "    private static final String s = k();\n" +
                       "}\n");
    }

    @Test public void onClass() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "class Test {\n" +
                       "    static {\n" +
                       "        String k = org.openide.util.NbBundle.getMessage(Test.class, \"k\");\n" +
                       "    }\n" +
                       "}\n").
                input("test/Bundle.properties", "k=v\n", false).
                run(UseNbBundleMessages.class).
                findWarning("3:45-3:55:warning:" + UseNbBundleMessages_error_text()).
                applyFix().
                assertVerbatimOutput("test/Bundle.properties", "").
                assertOutput("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "import static test.Bundle.*;\n" +
                       "@Messages(\"k=v\")\n" +
                       "class Test {\n" +
                       "    static {\n" +
                       "        String k = k();\n" +
                       "    }\n" +
                       "}\n");
    }

    @Test public void insideAnonymous() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "class Test {\n" +
                       "    Object o = new Object() {\n" +
                       "        public String toString() {\n" +
                       "            return org.openide.util.NbBundle.getMessage(Test.class, \"k\");\n" +
                       "        }\n" +
                       "    };\n" +
                       "}\n").
                input("test/Bundle.properties", "k=v\n", false).
                run(UseNbBundleMessages.class).
                findWarning("4:45-4:55:warning:" + UseNbBundleMessages_error_text()).
                applyFix().
                assertVerbatimOutput("test/Bundle.properties", "").
                assertOutput("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "import static test.Bundle.*;\n" +
                       "class Test {\n" +
                       "    @Messages(\"k=v\")\n" +
                       "    Object o = new Object() {\n" +
                       "        public String toString() {\n" +
                       "            return k();\n" +
                       "        }\n" +
                       "    };\n" +
                       "}\n");
    }

    @Test public void insideLocal() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "class Test {\n" +
                       "    void m() {\n" +
                       "        class Local {\n" +
                       "            class MoreLocal {\n" +
                       "                String s = org.openide.util.NbBundle.getMessage(Test.class, \"k\");\n" +
                       "            }\n" +
                       "        }\n" +
                       "    }\n" +
                       "}\n").
                input("test/Bundle.properties", "k=v\n", false).
                run(UseNbBundleMessages.class).
                findWarning("5:53-5:63:warning:" + UseNbBundleMessages_error_text()).
                applyFix().
                assertVerbatimOutput("test/Bundle.properties", "").
                assertOutput("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "import static test.Bundle.*;\n" +
                       "class Test {\n" +
                       "    @Messages(\"k=v\")\n" +
                       "    void m() {\n" +
                       "        class Local {\n" +
                       "            class MoreLocal {\n" +
                       "                String s = k();\n" +
                       "            }\n" +
                       "        }\n" +
                       "    }\n" +
                       "}\n");
    }

    @Test public void annotationOnClass() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "@javax.annotation.Resource(description=\"#k\")\n" +
                       "class Test {\n" +
                       "}\n").
                input("test/Bundle.properties", "k=v\n", false).
                run(UseNbBundleMessages.class).
                findWarning("1:27-1:43:warning:" + UseNbBundleMessages_error_text()).
                applyFix().
                assertVerbatimOutput("test/Bundle.properties", "").
                assertOutput("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "@javax.annotation.Resource(description=\"#k\")\n" +
                       "@Messages(\"k=v\")\n" +
                       "class Test {\n" +
                       "}\n");
    }

    @Test public void annotationOnMethod() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "class Test {\n" +
                       "    @javax.annotation.Resource(description=\"#k\")\n" +
                       "    void m() {}\n" +
                       "}\n").
                input("test/Bundle.properties", "k=v\n", false).
                run(UseNbBundleMessages.class).
                findWarning("2:31-2:47:warning:" + UseNbBundleMessages_error_text()).
                applyFix().
                assertVerbatimOutput("test/Bundle.properties", "").
                assertOutput("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "class Test {\n" +
                       "    @javax.annotation.Resource(description=\"#k\")\n" +
                       "    @Messages(\"k=v\")\n" +
                       "    void m() {}\n" +
                       "}\n");
    }

    @Test public void annotationOnField() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "class Test {\n" +
                       "    @javax.annotation.Resource(description=\"#k\")\n" +
                       "    static final Void f = null;\n" +
                       "}\n").
                input("test/Bundle.properties", "k=v\n", false).
                run(UseNbBundleMessages.class).
                findWarning("2:31-2:47:warning:" + UseNbBundleMessages_error_text()).
                applyFix().
                assertVerbatimOutput("test/Bundle.properties", "").
                assertOutput("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "class Test {\n" +
                       "    @javax.annotation.Resource(description=\"#k\")\n" +
                       "    @Messages(\"k=v\")\n" +
                       "    static final Void f = null;\n" +
                       "}\n");
    }

    @Test public void annotationOnPackage() throws Exception {
        HintTest.create().classpath(cp()).
                input("test/package-info.java",
                       "@javax.annotation.Generated(value={}, comments=\"#k\")\n" +
                       "package test;\n").
                input("test/Bundle.properties", "k=v\n", false).
                run(UseNbBundleMessages.class).
                findWarning("0:38-0:51:warning:" + UseNbBundleMessages_error_text()).
                applyFix().
                assertVerbatimOutput("test/Bundle.properties", "").
                assertOutput("test/package-info.java",
                       "@javax.annotation.Generated(value={}, comments=\"#k\")\n" +
                       "@Messages(\"k=v\")\n" +
                       "package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n");
    }

    @Test public void addToExistingSingle() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "import static test.Bundle.*;\n" +
                       "class Test {\n" +
                       "    @Messages(\"one=first\")\n" +
                       "    String m(boolean flag) {\n" +
                       "        return flag ? one() : org.openide.util.NbBundle.getMessage(Test.class, \"two\");\n" +
                       "    }\n" +
                       "}\n", /* Bundle not generated */false).
                input("test/Bundle.properties", "two=second\nthree=third\n", false).
                run(UseNbBundleMessages.class).
                findWarning("6:56-6:66:warning:" + UseNbBundleMessages_error_text()).
                applyFix().
                assertVerbatimOutput("test/Bundle.properties", "three=third\n").
                assertOutput("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "import static test.Bundle.*;\n" +
                       "class Test {\n" +
                       "    @Messages({\"one=first\", \"two=second\"})\n" +
                       "    String m(boolean flag) {\n" +
                       "        return flag ? one() : two();\n" +
                       "    }\n" +
                       "}\n");
    }

    @Test public void addToExistingMultiple() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "import static test.Bundle.*;\n" +
                       "class Test {\n" +
                       "    @Messages({\"one=first\", \"two=second\"})\n" +
                       "    String m(boolean flag1, boolean flag2) {\n" +
                       "        return flag1 ? one() : flag2 ? two() : org.openide.util.NbBundle.getMessage(Test.class, \"three\");\n" +
                       "    }\n" +
                       "}\n", false).
                input("test/Bundle.properties", "three=third\n", false).
                run(UseNbBundleMessages.class).
                findWarning("6:73-6:83:warning:" + UseNbBundleMessages_error_text()).
                applyFix().
                assertVerbatimOutput("test/Bundle.properties", "").
                assertOutput("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "import static test.Bundle.*;\n" +
                       "class Test {\n" +
                       "    @Messages({\"one=first\", \"two=second\", \"three=third\"})\n" +
                       "    String m(boolean flag1, boolean flag2) {\n" +
                       "        return flag1 ? one() : flag2 ? two() : three();\n" +
                       "    }\n" +
                       "}\n");
    }

    @Test public void useAlreadyDefinedOnEnclosing() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "@javax.annotation.Resource(description=\"#k\")\n" +
                       "@Messages(\"k=v\")\n" +
                       "class Test {\n" +
                       "    String m() {\n" +
                       "        return org.openide.util.NbBundle.getMessage(Test.class, \"k\");\n" +
                       "    }\n" +
                       "}\n").
                run(UseNbBundleMessages.class).
                findWarning("6:41-6:51:warning:" + UseNbBundleMessages_error_text()).
                applyFix().
                assertOutput("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "import static test.Bundle.*;\n" +
                       "@javax.annotation.Resource(description=\"#k\")\n" +
                       "@Messages(\"k=v\")\n" +
                       "class Test {\n" +
                       "    String m() {\n" +
                       "        return k();\n" +
                       "    }\n" +
                       "}\n");
    }

    @Test public void alreadyDefinedOnAnnotation() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "@javax.annotation.Resource(description=\"#k\")\n" +
                       "@Messages(\"k=v\")\n" +
                       "class Test {\n" +
                       "}\n").
                run(UseNbBundleMessages.class).
                assertWarnings();
    }

    @Test public void messageFormatParametersAndComments() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "class Test {\n" +
                       "    String m(int x1, int x2) {\n" +
                       "        return org.openide.util.NbBundle.getMessage(Test.class, \"k\", x1, x2);\n" +
                       "    }\n" +
                       "}\n").
                input("test/Bundle.properties", "# leading comment\n# {0} - first\n# {1} - second\nk={0} out of {1}\n# unrelated comment\n", false).
                run(UseNbBundleMessages.class).
                findWarning("3:41-3:51:warning:" + UseNbBundleMessages_error_text()).
                applyFix().
                assertVerbatimOutput("test/Bundle.properties", "# unrelated comment\n").
                assertOutput("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "import static test.Bundle.*;\n" +
                       "class Test {\n" +
                       "    @Messages({\"# leading comment\", \"# {0} - first\", \"# {1} - second\", \"k={0} out of {1}\"})\n" +
                       "    String m(int x1, int x2) {\n" +
                       "        return k(x1, x2);\n" +
                       "    }\n" +
                       "}\n");
    }

    @Test public void unusualCharacters() throws Exception {
        HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "class Test {\n" +
                       "    String m() {\n" +
                       "        return org.openide.util.NbBundle.getMessage(Test.class, \"1/0\");\n" +
                       "    }\n" +
                       "}\n").
                input("test/Bundle.properties", "1/0=first \"line\"\\n\\\nand second\n", false).
                run(UseNbBundleMessages.class).
                findWarning("3:41-3:51:warning:" + UseNbBundleMessages_error_text()).
                applyFix().
                assertVerbatimOutput("test/Bundle.properties", "").
                assertOutput("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "import static test.Bundle.*;\n" +
                       "class Test {\n" +
                       "    @Messages(\"1/0=first \\\"line\\\"\\nand second\")\n" +
                       "    String m() {\n" +
                       "        return _1_0();\n" +
                       "    }\n" +
                       "}\n");
    }

    // XXX test interaction with @Override (#211037)

    @Test public void sequentialFixes() throws Exception {
        HintTest.HintOutput output = HintTest.create().classpath(cp()).
                input("package test;\n" +
                       "class Test {\n" +
                       "    void m() {\n" +
                       "        String v1 = org.openide.util.NbBundle.getMessage(Test.class, \"k1\");\n" +
                       "        String v2 = org.openide.util.NbBundle.getMessage(Test.class, \"k2\");\n" +
                       "    }\n" +
                       "}\n").
                input("test/Bundle.properties", "k1=v1\nk2=v2\n", false).
                run(UseNbBundleMessages.class);
        output.findWarning("3:46-3:56:warning:" + UseNbBundleMessages_error_text()).
                applyFix().
                assertVerbatimOutput("test/Bundle.properties", "k2=v2\n").assertOutput("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "import static test.Bundle.*;\n" +
                       "class Test {\n" +
                       "    @Messages(\"k1=v1\")\n" +
                       "    void m() {\n" +
                       "        String v1 = k1();\n" +
                       "        String v2 = org.openide.util.NbBundle.getMessage(Test.class, \"k2\");\n" +
                       "    }\n" +
                       "}\n");
        // XXX #211087: may not be enough
        output.findWarning("9:46-9:56:warning:" + UseNbBundleMessages_error_text()).
                applyFix().
                assertVerbatimOutput("test/Bundle.properties", "").
                assertOutput("package test;\n" +
                       "import org.openide.util.NbBundle.Messages;\n" +
                       "import static test.Bundle.*;\n" +
                       "class Test {\n" +
                       "    @Messages({\"k1=v1\", \"k2=v2\"})\n" +
                       "    void m() {\n" +
                       "        String v1 = k1();\n" +
                       "        String v2 = k2();\n" +
                       "    }\n" +
                       "}\n");
    }

    @Test public void skipBundleJava() throws Exception {
        HintTest.create().classpath(cp()).
                input("test/Bundle.java", "package test;\n" +
                       "class Bundle {\n" +
                       "    static String somekey() {\n" +
                       "        return org.openide.util.NbBundle.getMessage(Bundle.class, \"somekey\");\n" +
                       "    }\n" +
                       "}\n").
                run(UseNbBundleMessages.class).
                assertWarnings();
    }

    private URL cp() {
        URL cp = NbBundle.class.getProtectionDomain().getCodeSource().getLocation();
        return cp.toString().endsWith("/") ? cp : FileUtil.getArchiveRoot(cp);
    }

}
