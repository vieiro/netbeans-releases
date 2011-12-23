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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.editor.model.impl;

import java.util.Set;
import org.netbeans.modules.php.editor.api.QualifiedName;
import org.netbeans.modules.php.editor.api.PhpModifiers;
import java.util.Collection;
import org.netbeans.modules.php.editor.api.PhpElementKind;
import org.netbeans.modules.php.editor.api.elements.TypeResolver;
import org.netbeans.modules.php.editor.model.*;
import org.netbeans.modules.php.editor.model.nodes.ASTNodeInfo;
import org.netbeans.modules.php.editor.model.nodes.ConstantDeclarationInfo;
import org.netbeans.modules.php.editor.model.nodes.FunctionDeclarationInfo;
import org.netbeans.modules.php.editor.model.nodes.NamespaceDeclarationInfo;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.Program;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.UseStatementPart;
import org.netbeans.modules.php.editor.parser.astnodes.Variable;
import org.netbeans.modules.php.editor.api.elements.VariableElement;

/**
 *
 * @author Radek Matous
 */
final class NamespaceScopeImpl extends ScopeImpl implements NamespaceScope, VariableNameFactory {

    private final boolean isDefault;

    public VariableNameImpl createElement( Variable variable) {
        VariableNameImpl retval = new VariableNameImpl(this, variable, true);
        return retval;
    }

    public VariableNameImpl createElement( VariableElement variable) {
        VariableNameImpl retval = new VariableNameImpl(this, variable);
        Set<TypeResolver> instanceTypes = variable.getInstanceTypes();
        return retval;
    }

    ScalarConstantElementImpl createConstantElement(final ASTNodeInfo<Scalar> node, final String value) {
        return new ScalarConstantElementImpl(this, node, value);
    }
    ConstantElementImpl createElement(ConstantDeclarationInfo node) {
        ConstantElementImpl retval = new ConstantElementImpl(this, node);
        return retval;
    }
    UseElementImpl createUseStatementPart(ASTNodeInfo<UseStatementPart> node) {
        UseElementImpl retval = new UseElementImpl(this, node);
        return retval;
    }

    FunctionScopeImpl createElement(Program program, FunctionDeclaration node) {
        FunctionScopeImpl retval = new FunctionScopeImpl(this, FunctionDeclarationInfo.create(program, node),
                VariousUtils.getReturnTypeFromPHPDoc(program, node));
        return retval;
    }

    NamespaceScopeImpl(FileScopeImpl inScope, NamespaceDeclarationInfo info) {
        super(inScope, info, PhpModifiers.fromBitMask(PhpModifiers.PUBLIC), info.getOriginalNode().getBody());
        isDefault = false;
    }

    NamespaceScopeImpl(FileScopeImpl inScope) {
        super(inScope, NamespaceDeclarationInfo.DEFAULT_NAMESPACE_NAME,inScope.getFile(), inScope.getNameRange(), PhpElementKind.NAMESPACE_DECLARATION);
        isDefault = true;
    }

    @Override
    void addElement(ModelElementImpl element) {
        super.addElement(element);
    }

    public Collection<? extends ClassScopeImpl> getDeclaredClasses() {
        return filter(getElements(), new ElementFilter<ClassScopeImpl>() {
            public boolean isAccepted(ModelElement element) {
                return element.getPhpElementKind().equals(PhpElementKind.CLASS);
            }
        });
    }

    public Collection<? extends InterfaceScope> getDeclaredInterfaces() {
        return filter(getElements(), new ElementFilter() {
            public boolean isAccepted(ModelElement element) {
                return element.getPhpElementKind().equals(PhpElementKind.IFACE);
            }
        });
    }

    @Override
    public Collection<? extends TraitScope> getDeclaredTraits() {
        return filter(getElements(), new ElementFilter() {
            @Override
            public boolean isAccepted(ModelElement element) {
                return element.getPhpElementKind().equals(PhpElementKind.TRAIT);
            }
        });
    }

    public Collection<? extends ConstantElement> getDeclaredConstants() {
        return filter(getElements(), new ElementFilter() {
            public boolean isAccepted(ModelElement element) {
                return element.getPhpElementKind().equals(PhpElementKind.CONSTANT);
            }
        });
    }


    public Collection<? extends FunctionScope> getDeclaredFunctions() {
        return filter(getElements(), new ElementFilter() {
            public boolean isAccepted(ModelElement element) {
                return element.getPhpElementKind().equals(PhpElementKind.FUNCTION);
            }
        });
    }

    public Collection<? extends UseElement> getDeclaredUses() {
        return filter(getElements(), new ElementFilter() {
            public boolean isAccepted(ModelElement element) {
                return element.getPhpElementKind().equals(PhpElementKind.USE_STATEMENT);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public Collection<? extends TypeScope> getDeclaredTypes() {
        Collection<? extends ClassScope> classes = getDeclaredClasses();
        Collection<? extends InterfaceScope> interfaces = getDeclaredInterfaces();
        Collection<? extends TraitScope> traits = getDeclaredTraits();
        return ModelUtils.merge(classes, interfaces, traits);
    }


    public Collection<? extends VariableName> getDeclaredVariables() {
        return filter(getElements(), new ElementFilter() {
            public boolean isAccepted(ModelElement element) {
                return element.getPhpElementKind().equals(PhpElementKind.VARIABLE);
            }
        });
    }

    public boolean isDefaultNamespace() {
        return this.isDefault;
    }

    public FileScopeImpl getFileScope() {
        return (FileScopeImpl) getInScope();
    }

    public QualifiedName getQualifiedName() {
        QualifiedName qualifiedName = QualifiedName.create(this);
        return qualifiedName;
    }

    @Override
    public String getIndexSignature() {
        StringBuilder sb = new StringBuilder();
        QualifiedName qualifiedName = getQualifiedName();
        String name = qualifiedName.toName().toString();
        String namespaceName = qualifiedName.toNamespaceName().toString();
        sb.append(name.toLowerCase()).append(";");//NOI18N
        sb.append(name).append(";");//NOI18N
        sb.append(namespaceName).append(";");//NOI18N
        return sb.toString();
    }

}
