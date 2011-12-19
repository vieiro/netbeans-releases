/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of the
 * License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include the
 * License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by Oracle
 * in the GPL Version 2 section of the License file that accompanied this code.
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL or only
 * the GPL Version 2, indicate your decision by adding "[Contributor] elects to
 * include this software in this distribution under the [CDDL or GPL Version 2]
 * license." If you do not indicate a single choice of license, a recipient has
 * the option to distribute your version of this file under either the CDDL, the
 * GPL Version 2 or to extend the choice of license to its licensees as provided
 * above. However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is made
 * subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.xml.schema.model.visitor;

import java.util.Set;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.matchers.JUnitMatchers.hasItem;
import org.junit.rules.TestRule;
import org.netbeans.modules.xml.schema.model.GlobalElement;
import org.netbeans.modules.xml.schema.model.TestCatalogModel;
import org.netbeans.modules.xml.schema.model.Util;
import org.netbeans.modules.xml.schema.model.impl.SchemaModelImpl;

/**
 * Tests for {@link FindSubstitutionsVisitor}
 * @author Daniel Bell (dbell@netbeans.org)
 */
public class FindSubstitutionsVisitorTest {
    public static final String PARENT_NS_URI = "urn:parent";
    public static final String SUBSTITUTION_GROUP_HEAD = "child";
    private static final String PARENT_SCHEMA = "resources/SubstitutionGroupParent.xsd";
    private static final String CHILD_SCHEMA_ONE = "resources/SubstitutionGroupChildOne.xsd";
    private static final String CHILD_SCHEMA_TWO = "resources/SubstitutionGroupChildTwo.xsd";
    private static final String SUBSTITUTION_ELEMENT_ONE = "child-one";
    private static final String SUBSTITUTION_ELEMENT_TWO = "child-two";
    
    @Rule
    public final TestRule catalogMaintainer = TestCatalogModel.maintainer();
    
    private SchemaModelImpl parentModel;
    private SchemaModelImpl childModelOne;
    private SchemaModelImpl childModelTwo;
    
    @Before
    public void setUp() throws Exception {
        childModelOne = load(CHILD_SCHEMA_ONE);
        childModelTwo = load(CHILD_SCHEMA_TWO);
        parentModel = load(PARENT_SCHEMA);
    }
    
    /**
     * Each schema imports a substitution group base, and defines a global 
     * element that is part of this substitution group.
     * FindSubstitutionsVisitor should resolve this substitution.
     */
    @Test
    public void shouldResolveSubstitutionsFromLinkedSchemas() {
        GlobalElement substitutionGroupHead = getCachedElement(parentModel, SUBSTITUTION_GROUP_HEAD);
        GlobalElement expectedSubstitutionOne = getCachedElement(childModelOne, SUBSTITUTION_ELEMENT_ONE);
        GlobalElement expectedSubstitutionTwo = getCachedElement(childModelTwo, SUBSTITUTION_ELEMENT_TWO);
        
        Set<GlobalElement> possibleSubstitutionsOne = FindSubstitutions.resolveSubstitutions(childModelOne, substitutionGroupHead);
        Set<GlobalElement> possibleSubstitutionsTwo = FindSubstitutions.resolveSubstitutions(childModelTwo, substitutionGroupHead);

        assertThat(possibleSubstitutionsOne.size(), is(1));
        assertThat(possibleSubstitutionsOne, hasItem(expectedSubstitutionOne));
        
        assertThat(possibleSubstitutionsTwo.size(), is(1));
        assertThat(possibleSubstitutionsTwo, hasItem(expectedSubstitutionTwo)); //Should used cached substitution group head
    }
        
    private static GlobalElement getCachedElement(SchemaModelImpl model, String localName) {
        return model.getGlobalComponentsIndexSupport().findByNameAndType(localName, GlobalElement.class);
    }

    private static SchemaModelImpl load(String schemaPath) throws Exception {
        return Util.toSchemaModelImpl(Util.loadSchemaModel2(schemaPath));
    }
}
