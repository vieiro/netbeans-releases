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
package org.netbeans.modules.web.common.api;

import javax.swing.text.BadLocationException;
import org.netbeans.junit.NbTestCase;

/**
 *
 * @author marekfukala
 */
public class LinesTest extends NbTestCase {

    public LinesTest(String name) {
        super(name);
    }

    public void testBasic() throws BadLocationException {
        String source = "jedna\ndve\ntri";
        //               012345 6789 012

        Lines l = new Lines(source);

        assertEquals(3, l.getLinesCount());

        assertEquals(0, l.getLineIndex(0));
        assertEquals(0, l.getLineIndex(1));
        assertEquals(0, l.getLineIndex(2));
        assertEquals(0, l.getLineIndex(3));
        assertEquals(0, l.getLineIndex(4));
        assertEquals(0, l.getLineIndex(5));

        assertEquals(1, l.getLineIndex(6));
        assertEquals(1, l.getLineIndex(7));
        assertEquals(1, l.getLineIndex(8));
        assertEquals(1, l.getLineIndex(9));

        assertEquals(2, l.getLineIndex(10));
        assertEquals(2, l.getLineIndex(11));
        assertEquals(2, l.getLineIndex(12));

        assertEquals(0, l.getLineOffset(0));
        assertEquals(6, l.getLineOffset(1));
        assertEquals(10, l.getLineOffset(2));

    }

    public void testEmpty() throws BadLocationException {
        String source = "";
        Lines l = new Lines(source);
        assertEquals(1, l.getLinesCount());
        assertEquals(0, l.getLineIndex(0));
        assertEquals(0, l.getLineOffset(0));
    }

    public void testJustNL() throws BadLocationException {
        String source = "\n";
        Lines l = new Lines(source);
        assertEquals(1, l.getLinesCount());
        assertEquals(0, l.getLineIndex(0));
        assertEquals(0, l.getLineOffset(0));
    }

    public void testJustNLs() throws BadLocationException {
        String source = "\n\n\n";
        //               0 1 2

        Lines l = new Lines(source);
        assertEquals(3, l.getLinesCount());

        assertEquals(0, l.getLineIndex(0));
        assertEquals(1, l.getLineOffset(1));
        assertEquals(2, l.getLineOffset(2));

        assertEquals(0, l.getLineIndex(0));
        assertEquals(1, l.getLineIndex(1));
        assertEquals(2, l.getLineIndex(2));

    }

    public void testCharAfterNL() throws BadLocationException {
        String source = "\nA";
        //               0 1

        Lines l = new Lines(source);
        assertEquals(2, l.getLinesCount());

        assertEquals(0, l.getLineIndex(0));
        assertEquals(1, l.getLineOffset(1));

        assertEquals(0, l.getLineIndex(0));
        assertEquals(1, l.getLineIndex(1));

    }

    public void testCharBeforeNL() throws BadLocationException {
        String source = "A\n";
        //               01

        Lines l = new Lines(source);
        assertEquals(1, l.getLinesCount());

        assertEquals(0, l.getLineIndex(0));
        assertEquals(0, l.getLineIndex(0));

    }

    public void testPerformance() throws BadLocationException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            sb.append("fkdhfgjdhsgjkfdhgjkfdhkjghpru2iordhsflkdshgjkdshfgkjdshgjkdshfkjlasdhfjkdshfkjdshfjkdsfhj\n");
        }
        
        System.out.println("text size = " + sb.length());
        Lines l = new Lines(sb);
        
        long a = System.currentTimeMillis();
        int lindex = -1;
        for(int i = 0; i < sb.length(); i+=100) {
            int lineIndex = l.getLineIndex(i);
            assertTrue(lindex != lineIndex); //cannot hit same line two times
            lindex = lineIndex;
        }
        long b = System.currentTimeMillis();
        
        System.out.println("took " + (b-a) + "ms.");
        
    }
}
