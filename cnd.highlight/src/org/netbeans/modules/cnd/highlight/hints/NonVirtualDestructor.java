/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.highlight.hints;

import java.util.Collection;
import org.netbeans.modules.cnd.analysis.api.AnalyzerResponse;
import org.netbeans.modules.cnd.analysis.api.AuditPreferences;
import org.netbeans.modules.cnd.api.model.CsmClass;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmMember;
import org.netbeans.modules.cnd.api.model.CsmMethod;
import org.netbeans.modules.cnd.api.model.CsmNamespaceDefinition;
import org.netbeans.modules.cnd.api.model.CsmOffsetableDeclaration;
import org.netbeans.modules.cnd.api.model.services.CsmVirtualInfoQuery;
import org.netbeans.modules.cnd.api.model.syntaxerr.CsmErrorInfo;
import org.netbeans.modules.cnd.api.model.syntaxerr.CsmErrorProvider;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import org.netbeans.modules.cnd.api.model.xref.CsmTypeHierarchyResolver;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexander Simon
 */
class NonVirtualDestructor extends CodeAuditInfo {
    private NonVirtualDestructor(String id, String name, String description, String defaultSeverity, boolean defaultEnabled, AuditPreferences myPreferences) {
        super(id, name, description, defaultSeverity, defaultEnabled, myPreferences);
    }
    static NonVirtualDestructor create(AuditPreferences myPreferences) {
        String id = NbBundle.getMessage(NonVirtualDestructor.class, "NonVirtualDestructor.name"); // NOI18N
        String description = NbBundle.getMessage(NonVirtualDestructor.class, "NonVirtualDestructor.description"); // NOI18N
        return new NonVirtualDestructor(id, id, description, "warning", true, myPreferences); // NOI18N
    }
    
    @Override
    void doGetErrors(CsmErrorProvider.Request request, CsmErrorProvider.Response response) {
        CsmFile file = request.getFile();
        if (file != null) {
            if (request.isCancelled()) {
                return;
            }
            visit(file.getDeclarations(), request, response);
        }
    }

    private void visit(Collection<? extends CsmOffsetableDeclaration> decls, CsmErrorProvider.Request request, CsmErrorProvider.Response response) {
        for(CsmOffsetableDeclaration decl : decls) {
            if (request.isCancelled()) {
                return;
            }
            if (CsmKindUtilities.isClassMember(decl)) {
                visit((CsmMember) decl, request, response);
            } else if (CsmKindUtilities.isClass(decl)) {
                visit((CsmClass) decl, request, response);
            } else if (CsmKindUtilities.isNamespaceDefinition(decl)) {
                visit(((CsmNamespaceDefinition) decl).getDeclarations(), request, response);
            }
        }
    }
    
    private void visit(CsmMember csmMember, CsmErrorProvider.Request request, CsmErrorProvider.Response response) {
        int qq;
        if (CsmKindUtilities.isDestructor(csmMember)) {
            CsmMethod method = (CsmMethod)csmMember;
            if (!CsmVirtualInfoQuery.getDefault().isVirtual(method)) {
                if (!CsmTypeHierarchyResolver.getDefault().getSubTypes(method.getContainingClass(), true).isEmpty()) {
                    String message = NbBundle.getMessage(NonVirtualDestructor.class, "NonVirtualDestructor.message"); // NOI18N
                    CsmErrorInfo.Severity severity;
                    if ("error".equals(minimalSeverity())) { // NOI18N
                        severity = CsmErrorInfo.Severity.ERROR;
                    } else {
                        severity = CsmErrorInfo.Severity.WARNING;
                    }
                    if (response instanceof AnalyzerResponse) {
                        ((AnalyzerResponse) response).addError(AnalyzerResponse.AnalyzerSeverity.DetectedError, null, method.getContainingFile().getFileObject(),
                                new ErrorInfoImpl(getID()+"\n"+message, severity, method.getStartOffset(), method.getParameterList().getEndOffset())); // NOI18N
                    } else {
                        response.addError(new ErrorInfoImpl(message, severity, method.getStartOffset(), method.getParameterList().getEndOffset()));
                    }
                }
            }
        }
    }
    private void visit(CsmClass csmClass, CsmErrorProvider.Request request, CsmErrorProvider.Response response) {
        visit(csmClass.getMembers(), request, response);
    }
}
