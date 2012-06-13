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
package org.netbeans.modules.php.editor.elements;

import org.netbeans.modules.php.editor.api.QualifiedName;
import org.netbeans.modules.php.editor.model.Model;
import org.netbeans.modules.php.editor.model.ModelUtils;
import org.netbeans.modules.php.editor.model.NamespaceScope;
import org.netbeans.modules.php.editor.model.impl.ModelTestBase;

/**
 *
 * @author Ondrej Brejla <obrejla@netbeans.org>
 */
public class TypeNameResolverImplTest extends ModelTestBase {

    private static final int TEST_TIMEOUT = Integer.getInteger("nb.php.test.timeout", 100000); //NOI18N

    public TypeNameResolverImplTest(String testName) {
        super(testName);
    }

    @Override
    protected int timeOut() {
        return TEST_TIMEOUT;
    }

    private int getResolvingOffset(final String preparedTestFile) {
        int indexOf = preparedTestFile.indexOf("/*^*/");
        assert indexOf != -1;
        return indexOf;
    }

    public void testNull_01() throws Exception {
        QualifiedName toResolve = QualifiedName.create("\\Test\\Omg");
        QualifiedName expected = QualifiedName.create("\\Test\\Omg");
        QualifiedName actual = TypeNameResolverImpl.forNull().resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testNull_02() throws Exception {
        QualifiedName toResolve = QualifiedName.create("Test\\Omg");
        QualifiedName expected = QualifiedName.create("Test\\Omg");
        QualifiedName actual = TypeNameResolverImpl.forNull().resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testNull_03() throws Exception {
        QualifiedName toResolve = QualifiedName.create("Omg");
        QualifiedName expected = QualifiedName.create("Omg");
        QualifiedName actual = TypeNameResolverImpl.forNull().resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testUnqualifiedName_01() throws Exception {
        QualifiedName toResolve = QualifiedName.create("Omg");
        QualifiedName expected = QualifiedName.create("Omg");
        QualifiedName actual = TypeNameResolverImpl.forUnqualifiedName().resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testUnqualifiedName_02() throws Exception {
        QualifiedName toResolve = QualifiedName.create("Foo\\Omg");
        QualifiedName expected = QualifiedName.create("Omg");
        QualifiedName actual = TypeNameResolverImpl.forUnqualifiedName().resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testUnqualifiedName_03() throws Exception {
        QualifiedName toResolve = QualifiedName.create("Foo\\Bar\\Omg");
        QualifiedName expected = QualifiedName.create("Omg");
        QualifiedName actual = TypeNameResolverImpl.forUnqualifiedName().resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testUnqualifiedName_04() throws Exception {
        QualifiedName toResolve = QualifiedName.create("\\Foo\\Bar\\Omg");
        QualifiedName expected = QualifiedName.create("Omg");
        QualifiedName actual = TypeNameResolverImpl.forUnqualifiedName().resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testFullyQualifiedName_01() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testFullyQualifiedName_01.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("Omg");
        QualifiedName expected = QualifiedName.create("\\Test\\Omg");
        QualifiedName actual = TypeNameResolverImpl.forFullyQualifiedName(namespaceScope, offset).resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testQualifiedName_01() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testQualifiedName_01.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("\\Test\\Foo");
        QualifiedName expected = QualifiedName.create("Foo");
        QualifiedName actual = TypeNameResolverImpl.forQualifiedName(namespaceScope, offset).resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testQualifiedName_02() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testQualifiedName_02.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("\\Foo\\Bar\\Baz\\Bat");
        QualifiedName expected = QualifiedName.create("Bar\\Baz\\Bat");
        QualifiedName actual = TypeNameResolverImpl.forQualifiedName(namespaceScope, offset).resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testQualifiedName_03() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testQualifiedName_03.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("\\Test2\\Omg");
        QualifiedName expected = QualifiedName.create("Omg");
        QualifiedName actual = TypeNameResolverImpl.forQualifiedName(namespaceScope, offset).resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testQualifiedName_04() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testQualifiedName_04.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("\\Foo\\Bar\\Baz\\Bat");
        QualifiedName expected = QualifiedName.create("Baz\\Bat");
        QualifiedName actual = TypeNameResolverImpl.forQualifiedName(namespaceScope, offset).resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testQualifiedName_05() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testQualifiedName_05.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("\\Foo\\Bar\\Baz\\Bat");
        QualifiedName expected = QualifiedName.create("Bar\\Baz\\Bat");
        QualifiedName actual = TypeNameResolverImpl.forQualifiedName(namespaceScope, offset).resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testQualifiedName_06() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testQualifiedName_06.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("\\Foo\\Bar\\Baz\\Bat");
        QualifiedName expected = QualifiedName.create("Foo\\Bar\\Baz\\Bat");
        QualifiedName actual = TypeNameResolverImpl.forQualifiedName(namespaceScope, offset).resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testQualifiedName_07() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testQualifiedName_07.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("\\Foo\\Bar\\Baz\\Bat");
        QualifiedName expected = QualifiedName.create("Foo\\Bar\\Baz\\Bat");
        QualifiedName actual = TypeNameResolverImpl.forQualifiedName(namespaceScope, offset).resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testQualifiedName_08() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testQualifiedName_08.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("Bar\\Baz\\Bat");
        QualifiedName expected = QualifiedName.create("Test\\Bar\\Baz\\Bat");
        QualifiedName actual = TypeNameResolverImpl.forQualifiedName(namespaceScope, offset).resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testQualifiedName_09() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testQualifiedName_09.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("Bat");
        QualifiedName expected = QualifiedName.create("Test\\Bat");
        QualifiedName actual = TypeNameResolverImpl.forQualifiedName(namespaceScope, offset).resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testQualifiedName_10() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testQualifiedName_10.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("Alias\\Bat");
        QualifiedName expected = QualifiedName.create("Alias\\Bat");
        QualifiedName actual = TypeNameResolverImpl.forQualifiedName(namespaceScope, offset).resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testQualifiedName_11() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testQualifiedName_11.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("Bar\\Baz\\Bat");
        QualifiedName expected = QualifiedName.create("Bar\\Baz\\Bat");
        QualifiedName actual = TypeNameResolverImpl.forQualifiedName(namespaceScope, offset).resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testQualifiedName_12() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testQualifiedName_12.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("\\Test\\Foo\\Bar\\Baz");
        QualifiedName expected = QualifiedName.create("Bar\\Baz");
        QualifiedName actual = TypeNameResolverImpl.forQualifiedName(namespaceScope, offset).resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testSmartName_fail_01() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testSmartName_fail.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("Bat");
        try {
            TypeNameResolverImpl.forSmartName(namespaceScope, offset).resolve(toResolve);
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testSmartName_fail_02() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testSmartName_fail.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("Baz\\Bat");
        try {
            TypeNameResolverImpl.forSmartName(namespaceScope, offset).resolve(toResolve);
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testSmartName_01() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testSmartName_01.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("\\Foo\\Bar\\Baz\\Bat");
        QualifiedName expected = QualifiedName.create("Bar\\Baz\\Bat");
        QualifiedName actual = TypeNameResolverImpl.forSmartName(namespaceScope, offset).resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testSmartName_02() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testSmartName_02.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("\\Omg\\Fq\\Name");
        QualifiedName expected = QualifiedName.create("\\Omg\\Fq\\Name");
        QualifiedName actual = TypeNameResolverImpl.forSmartName(namespaceScope, offset).resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testSmartName_03() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testSmartName_03.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("\\Foo\\Bar\\Baz\\Bat");
        QualifiedName expected = QualifiedName.create("Baz\\Bat");
        QualifiedName actual = TypeNameResolverImpl.forSmartName(namespaceScope, offset).resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testSmartName_04() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testSmartName_04.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("\\Foo\\Bar\\Baz\\Bat");
        QualifiedName expected = QualifiedName.create("Bat");
        QualifiedName actual = TypeNameResolverImpl.forSmartName(namespaceScope, offset).resolve(toResolve);
        assertEquals(expected, actual);
    }

    public void testSmartName_05() throws Exception {
        String preparedTestFile = prepareTestFile("testfiles/elements/typenameresolver/testSmartName_05.php");
        Model model = getModel(preparedTestFile);
        int offset = getResolvingOffset(preparedTestFile);
        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(model.getFileScope(), offset);

        QualifiedName toResolve = QualifiedName.create("\\Test\\Foo\\Bar\\Baz");
        QualifiedName expected = QualifiedName.create("Bar\\Baz");
        QualifiedName actual = TypeNameResolverImpl.forSmartName(namespaceScope, offset).resolve(toResolve);
        assertEquals(expected, actual);
    }

}
