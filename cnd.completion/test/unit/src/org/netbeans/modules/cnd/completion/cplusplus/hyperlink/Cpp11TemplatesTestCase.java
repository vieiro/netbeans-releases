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

package org.netbeans.modules.cnd.completion.cplusplus.hyperlink;

/**
 *
 * @author petrk
 */
public class Cpp11TemplatesTestCase extends HyperlinkBaseTestCase {

    public Cpp11TemplatesTestCase(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        System.setProperty("cnd.modelimpl.tracemodel.project.name", "DummyProject"); // NOI18N
        System.setProperty("parser.report.errors", "true");
        System.setProperty("antlr.exceptions.hideExpectedTokens", "true");
        System.setProperty("cnd.language.flavor.cpp11", "true");         
        super.setUp();
    }
    
    public void testBug238847_1() throws Exception {
        // Bug 238913 - Unable to deduce type through uniqe_ptr and decltype
        performTest("bug238847_1.cpp", 72, 16, "bug238847_1.cpp", 66, 9);
        performTest("bug238847_1.cpp", 75, 16, "bug238847_1.cpp", 66, 9);
    }
    
    public void testBug238847_3() throws Exception {
        // Bug 238913 - Unable to deduce type through uniqe_ptr and decltype
        performTest("bug238847_3.cpp", 119, 15, "bug238847_3.cpp", 113, 9);
        performTest("bug238847_3.cpp", 122, 22, "bug238847_3.cpp", 113, 9);
    }    
    
    public void testBug238889() throws Exception {
        // Bug 238889 - C++11: Unresolved identifier if templates are closed by RSHIFT token
        performTest("bug238889.cpp", 17, 65, "bug238889.cpp", 13, 9);
        performTest("bug238889.cpp", 18, 87, "bug238889.cpp", 13, 9);
    }        
    
    public void testBug239901() throws Exception {
        // Bug 239901 - Unresolved identifier in type alias template with default type 
        performTest("bug239901.cpp", 19, 13, "bug239901.cpp", 3, 9);
        performTest("bug239901.cpp", 22, 13, "bug239901.cpp", 7, 9);
        performTest("bug239901.cpp", 25, 18, "bug239901.cpp", 3, 9);
    }            
}