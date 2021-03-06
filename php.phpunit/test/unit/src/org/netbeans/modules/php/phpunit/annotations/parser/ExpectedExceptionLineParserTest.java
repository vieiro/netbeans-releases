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
package org.netbeans.modules.php.phpunit.annotations.parser;

import org.netbeans.modules.php.phpunit.annotations.parser.ExpectedExceptionLineParser;
import java.util.Collections;
import java.util.Map;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.php.spi.annotation.AnnotationParsedLine;

/**
 *
 * @author Ondrej Brejla <obrejla@netbeans.org>
 */
public class ExpectedExceptionLineParserTest extends NbTestCase {
    private ExpectedExceptionLineParser parser;

    public ExpectedExceptionLineParserTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        parser = new ExpectedExceptionLineParser();
    }

    public void testValidUseCase_01() throws Exception {
        AnnotationParsedLine parsedLine = parser.parse("expectedException Exception");
        assertEquals("expectedException", parsedLine.getName());
        assertEquals("Exception", parsedLine.getDescription());
        Map<OffsetRange, String> types = parsedLine.getTypes();
        assertNotNull(types);
        assertEquals(1, types.size());
        for (Map.Entry<OffsetRange, String> entry : types.entrySet()) {
            OffsetRange offsetRange = entry.getKey();
            String typeName = entry.getValue();
            assertEquals(18, offsetRange.getStart());
            assertEquals(27, offsetRange.getEnd());
            assertEquals("Exception", typeName);
        }
    }

    public void testValidUseCase_02() throws Exception {
        AnnotationParsedLine parsedLine = parser.parse("expectedException ");
        assertEquals("expectedException", parsedLine.getName());
        assertEquals("", parsedLine.getDescription());
        assertEquals(Collections.EMPTY_MAP, parsedLine.getTypes());
    }

    public void testValidUseCase_03() throws Exception {
        AnnotationParsedLine parsedLine = parser.parse("expectedException\t");
        assertEquals("expectedException", parsedLine.getName());
        assertEquals("", parsedLine.getDescription());
        assertEquals(Collections.EMPTY_MAP, parsedLine.getTypes());
    }

    public void testValidUseCase_04() throws Exception {
        AnnotationParsedLine parsedLine = parser.parse("expectedException\tException");
        assertEquals("expectedException", parsedLine.getName());
        assertEquals("Exception", parsedLine.getDescription());
        Map<OffsetRange, String> types = parsedLine.getTypes();
        assertNotNull(types);
        assertEquals(1, types.size());
        for (Map.Entry<OffsetRange, String> entry : types.entrySet()) {
            OffsetRange offsetRange = entry.getKey();
            String typeName = entry.getValue();
            assertEquals(18, offsetRange.getStart());
            assertEquals(27, offsetRange.getEnd());
            assertEquals("Exception", typeName);
        }
    }

    public void testValidUseCase_05() throws Exception {
        AnnotationParsedLine parsedLine = parser.parse("expectedException           Exception     ");
        assertEquals("expectedException", parsedLine.getName());
        assertEquals("Exception", parsedLine.getDescription());
        Map<OffsetRange, String> types = parsedLine.getTypes();
        assertNotNull(types);
        assertEquals(1, types.size());
        for (Map.Entry<OffsetRange, String> entry : types.entrySet()) {
            OffsetRange offsetRange = entry.getKey();
            String typeName = entry.getValue();
            assertEquals(28, offsetRange.getStart());
            assertEquals(37, offsetRange.getEnd());
            assertEquals("Exception", typeName);
        }
    }

    public void testValidUseCase_06() throws Exception {
        AnnotationParsedLine parsedLine = parser.parse("expectedException\t    \tException   \t   ");
        assertEquals("expectedException", parsedLine.getName());
        assertEquals("Exception", parsedLine.getDescription());
        Map<OffsetRange, String> types = parsedLine.getTypes();
        assertNotNull(types);
        assertEquals(1, types.size());
        for (Map.Entry<OffsetRange, String> entry : types.entrySet()) {
            OffsetRange offsetRange = entry.getKey();
            String typeName = entry.getValue();
            assertEquals(23, offsetRange.getStart());
            assertEquals(32, offsetRange.getEnd());
            assertEquals("Exception", typeName);
        }
    }

    public void testValidUseCase_07() throws Exception {
        AnnotationParsedLine parsedLine = parser.parse("expectedException\t\\My\\Cool\\Exception");
        assertEquals("expectedException", parsedLine.getName());
        assertEquals("\\My\\Cool\\Exception", parsedLine.getDescription());
        Map<OffsetRange, String> types = parsedLine.getTypes();
        assertNotNull(types);
        assertEquals(1, types.size());
        for (Map.Entry<OffsetRange, String> entry : types.entrySet()) {
            OffsetRange offsetRange = entry.getKey();
            String typeName = entry.getValue();
            assertEquals(18, offsetRange.getStart());
            assertEquals(36, offsetRange.getEnd());
            assertEquals("\\My\\Cool\\Exception", typeName);
        }
    }

    public void testValidUseCase_08() throws Exception {
        AnnotationParsedLine parsedLine = parser.parse("expectedException\tMy\\Cool\\Exception");
        assertEquals("expectedException", parsedLine.getName());
        assertEquals("My\\Cool\\Exception", parsedLine.getDescription());
        Map<OffsetRange, String> types = parsedLine.getTypes();
        assertNotNull(types);
        assertEquals(1, types.size());
        for (Map.Entry<OffsetRange, String> entry : types.entrySet()) {
            OffsetRange offsetRange = entry.getKey();
            String typeName = entry.getValue();
            assertEquals(18, offsetRange.getStart());
            assertEquals(35, offsetRange.getEnd());
            assertEquals("My\\Cool\\Exception", typeName);
        }
    }

    public void testValidUseCase_09() throws Exception {
        AnnotationParsedLine parsedLine = parser.parse("expectedException\tMy\\Cool\\Exception\tMy description");
        assertEquals("expectedException", parsedLine.getName());
        assertEquals("My\\Cool\\Exception\tMy description", parsedLine.getDescription());
        Map<OffsetRange, String> types = parsedLine.getTypes();
        assertNotNull(types);
        assertEquals(1, types.size());
        for (Map.Entry<OffsetRange, String> entry : types.entrySet()) {
            OffsetRange offsetRange = entry.getKey();
            String typeName = entry.getValue();
            assertEquals(18, offsetRange.getStart());
            assertEquals(35, offsetRange.getEnd());
            assertEquals("My\\Cool\\Exception", typeName);
        }
    }

    public void testValidUseCase_10() throws Exception {
        AnnotationParsedLine parsedLine = parser.parse("expectedException\tMy\\Cool\\Exception   My description");
        assertEquals("expectedException", parsedLine.getName());
        assertEquals("My\\Cool\\Exception   My description", parsedLine.getDescription());
        Map<OffsetRange, String> types = parsedLine.getTypes();
        assertNotNull(types);
        assertEquals(1, types.size());
        for (Map.Entry<OffsetRange, String> entry : types.entrySet()) {
            OffsetRange offsetRange = entry.getKey();
            String typeName = entry.getValue();
            assertEquals(18, offsetRange.getStart());
            assertEquals(35, offsetRange.getEnd());
            assertEquals("My\\Cool\\Exception", typeName);
        }
    }

    public void testInvalidUseCase_01() throws Exception {
        AnnotationParsedLine parsedLine = parser.parse("@throws Exception");
        assertNull(parsedLine);
    }

    public void testInvalidUseCase_02() throws Exception {
        AnnotationParsedLine parsedLine = parser.parse("foo Exception");
        assertNull(parsedLine);
    }

    public void testInvalidUseCase_03() throws Exception {
        AnnotationParsedLine parsedLine = parser.parse("throw Exception");
        assertNull(parsedLine);
    }

}
