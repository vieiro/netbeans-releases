/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.cnd.modelimpl.csm.resolver;

import org.netbeans.modules.cnd.api.model.*;
import java.util.*;
import org.netbeans.modules.cnd.api.model.CsmDeclaration.Kind;
import org.netbeans.modules.cnd.api.model.deep.CsmDeclarationStatement;
import org.netbeans.modules.cnd.api.model.services.CsmClassifierResolver;
import org.netbeans.modules.cnd.api.model.services.CsmSelect;
import org.netbeans.modules.cnd.api.model.services.CsmSelect.CsmFilter;
import org.netbeans.modules.cnd.api.model.services.CsmUsingResolver;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import org.netbeans.modules.cnd.api.model.util.UIDs;
import org.netbeans.modules.cnd.modelimpl.csm.ForwardClass;
import org.netbeans.modules.cnd.modelimpl.csm.InheritanceImpl;
import org.netbeans.modules.cnd.modelimpl.csm.NamespaceImpl;
import org.netbeans.modules.cnd.modelimpl.csm.TemplateUtils;
import org.netbeans.modules.cnd.modelimpl.csm.core.FileImpl;
import org.netbeans.modules.cnd.modelimpl.csm.core.ProjectBase;
import org.netbeans.modules.cnd.modelimpl.csm.core.Unresolved;
import org.netbeans.modules.cnd.modelimpl.csm.core.Utils;
import org.netbeans.modules.cnd.modelimpl.debug.DiagnosticExceptoins;
import org.netbeans.modules.cnd.modelimpl.impl.services.BaseUtilitiesProviderImpl;
import org.netbeans.modules.cnd.modelutil.AntiLoop;
import org.openide.util.CharSequences;

/**
 * @author Vladimir Kvasihn
 */
public final class Resolver3 implements Resolver {

    private final ProjectBase project;
    private final CsmFile file;
    private final CsmFile startFile;
    private int offset;
    private final int origOffset;
    private Resolver parentResolver;

    private final List<CharSequence> usedNamespaces = new ArrayList<CharSequence>();
    private final Map<CharSequence, CsmNamespace> namespaceAliases = new HashMap<CharSequence, CsmNamespace>();
    private final Map<CharSequence, CsmDeclaration> usingDeclarations = new HashMap<CharSequence, CsmDeclaration>();

    private CsmTypedef currTypedef;
    private CsmClassifier currLocalClassifier;

    private CharSequence[] names;
    private int currNamIdx;
    private int interestedKind;
    private boolean resolveInBaseClass;
    private boolean inLocalContext = false;

    private CharSequence currName() {
        return (names != null && currNamIdx < names.length) ? names[currNamIdx] : CharSequences.empty();
    }

    private CsmNamespace containingNamespace;
    private CsmClass containingClass;
    private boolean contextFound = false;

    private CsmNamespace getContainingNamespace() {
        if( ! contextFound ) {
            findContext();
        }
        return containingNamespace;
    }

    private CsmClass getContainingClass() {
        if( ! contextFound ) {
            findContext();
        }
        return containingClass;
    }

    private void findContext() {
        contextFound = true;
        CsmFilter filter = CsmSelect.getFilterBuilder().createOffsetFilter(0, offset);
        findContext(CsmSelect.getDeclarations(file, filter), filter);
    }

    private Set<CsmFile> visitedFiles = new HashSet<CsmFile>();

    //private CsmNamespace currentNamespace;

    /**
     * should be created by ResolverFactory only
     * @param file file where object to be resolved is located
     * @param offset offset where object to be resolved is located 
     * @param parent parent resolver (can be null)
     * @param startFile start file where resolving started, it affects which objects considered as visible or not while resolving name at (file, offset)
     */
    /*package*/ Resolver3(CsmFile file, int offset, Resolver parent, CsmFile startFile) {
        this.file = file;
        this.offset = offset;
        this.origOffset = offset;
        parentResolver = parent;
        this.project = (ProjectBase) file.getProject();
        this.startFile = startFile;
    }

    private Resolver3(CsmFile file, int offset, Resolver parent) {
        this(file, offset, parent, (parent == null) ? file : parent.getStartFile());
    }

    private CsmClassifier findClassifier(CsmNamespace ns, CharSequence qualifiedNamePart) {
        CsmClassifier result = null;
        while ( ns != null  && result == null) {
            String fqn = ns.getQualifiedName() + "::" + qualifiedNamePart; // NOI18N
            result = findClassifierUsedInFile(fqn);
            ns = ns.getParent();
        }
        return result;
    }

    private CsmClassifier findClassifierUsedInFile(CharSequence qualifiedName) {
        // try to find visible classifier
        CsmClassifier result = null;
        // first of all - check local context
        currTypedef = null;
        currLocalClassifier = null;
        gatherMaps(file, false);
        if (currLocalClassifier != null && needClassifiers()) {
            result = currLocalClassifier;
        }
        if (currTypedef != null && needClassifiers()) {
            result = currTypedef;
        }
        if (result == null) {
            result = CsmClassifierResolver.getDefault().findClassifierUsedInFile(qualifiedName, getStartFile(), needClasses());
        }
        return result;
    }

    @Override
    public CsmFile getStartFile() {
        return startFile;
    }

    private CsmNamespace findNamespace(CsmNamespace ns, CharSequence qualifiedNamePart) {
        CsmNamespace result = null;
        if (ns == null) {
            result = findNamespace(qualifiedNamePart);
        } else {
            CsmNamespace containingNs = ns;
            while (containingNs != null && result == null) {
                String fqn = (containingNs.isGlobal() ? "" : (containingNs.getQualifiedName() + "::")) + qualifiedNamePart; // NOI18N
                result = findNamespace(fqn);
                containingNs = containingNs.getParent();
            }
        }
        return result;
    }

    private CsmNamespace findNamespace(CharSequence qualifiedName) {
        CsmNamespace result = project.findNamespace(qualifiedName);
        if( result == null ) {
            for (Iterator<CsmProject> iter = getLibraries().iterator(); iter.hasNext() && result == null;) {
                CsmProject lib = iter.next();
                result = lib.findNamespace(qualifiedName);
            }
        }
        return result;
    }

    @Override
    public Collection<CsmProject> getLibraries() {
        return getSearchLibraries(this.startFile.getProject());
    }

    public static Collection<CsmProject> getSearchLibraries(CsmProject prj) {
        if (prj.isArtificial() && prj instanceof ProjectBase) {
            List<ProjectBase> dependentProjects = ((ProjectBase)prj).getDependentProjects();
            Set<CsmProject> libs = new HashSet<CsmProject>();
            for (ProjectBase projectBase : dependentProjects) {
                if (!projectBase.isArtificial()) {
                    libs.addAll(projectBase.getLibraries());
                }
            }
            return libs;
        } else {
            return prj.getLibraries();
        }
    }

    @Override
    public CsmClassifier getOriginalClassifier(CsmClassifier orig) {
        if (isRecursionOnResolving(INFINITE_RECURSION)) {
            return null;
        }
        AntiLoop set = new AntiLoop(100);
        while (true) {
            set.add(orig);
            CsmClassifier resovedClassifier = null;
            if (CsmKindUtilities.isClassForwardDeclaration(orig)){
                CsmClassForwardDeclaration fd = (CsmClassForwardDeclaration) orig;
                resovedClassifier = fd.getCsmClass();
                if (resovedClassifier == null){
                    break;
                }
            } else if (CsmKindUtilities.isTypedef(orig)) {
                CsmType t = ((CsmTypedef)orig).getType();
                resovedClassifier = t.getClassifier();
                if (resovedClassifier == null) {
                    // have to stop with current 'orig' value
                    break;
                }
            } else if (ForwardClass.isForwardClass(orig)) {
                // try to find another class
                resovedClassifier = this.findClassifierUsedInFile(orig.getQualifiedName());
            } else {
                break;
            }
            if (set.contains(resovedClassifier)) {
                // try to recover from this error
                resovedClassifier = findOtherClassifier(orig);
                if (resovedClassifier == null || set.contains(resovedClassifier)) {
                    // have to stop with current 'orig' value
                    break;
                }
            }
            orig = resovedClassifier;
        }
        return orig;

    }

    private CsmClassifier findOtherClassifier(CsmClassifier out) {
        CsmNamespace ns = BaseUtilitiesProviderImpl.getImpl()._getClassNamespace(out);
        CsmClassifier cls = null;
        if (ns != null) {
            CsmUID<?> uid = UIDs.get(out);
            CharSequence fqn = out.getQualifiedName();
            Collection<CsmOffsetableDeclaration> col = null;
            if (ns instanceof NamespaceImpl) {
                col = ((NamespaceImpl)ns).getDeclarationsRange(fqn,
                        new Kind[]{Kind.CLASS, Kind.UNION, Kind.STRUCT, Kind.ENUM, Kind.TYPEDEF, Kind.TEMPLATE_DECLARATION, Kind.TEMPLATE_SPECIALIZATION, Kind.CLASS_FORWARD_DECLARATION});

            } else {
                col = ns.getDeclarations();
            }
            for (CsmDeclaration decl : col) {
                if (CsmKindUtilities.isClassifier(decl) && decl.getQualifiedName().equals(fqn)) {
                    if (!UIDs.get(decl).equals(uid)) {
                        cls = (CsmClassifier)decl;
                        if (!ForwardClass.isForwardClass(cls)) {
                            break;
                        }
                    }
                }
            }
        }
        return cls;
    }

    private void findContext(Iterator<?> it, CsmFilter filter) {
        while(it.hasNext()) {
            CsmDeclaration decl = (CsmDeclaration) it.next();
            if( decl.getKind() == CsmDeclaration.Kind.NAMESPACE_DEFINITION ) {
                CsmNamespaceDefinition nd = (CsmNamespaceDefinition) decl;
                if( nd.getStartOffset() < this.offset && this.offset < nd.getEndOffset()  ) {
                    containingNamespace = nd.getNamespace();
                    findContext(CsmSelect.getDeclarations(nd, filter), filter);
                }
            } else if(   decl.getKind() == CsmDeclaration.Kind.CLASS
                    || decl.getKind() == CsmDeclaration.Kind.STRUCT
                    || decl.getKind() == CsmDeclaration.Kind.UNION ) {

                CsmClass cls = (CsmClass) decl;
                if( cls.getStartOffset() < this.offset && this.offset < cls.getEndOffset()  ) {
                    containingClass = cls;
                    findContext(CsmSelect.getClassMembers(containingClass, filter), filter);
                }
            } else if( decl.getKind() == CsmDeclaration.Kind.FUNCTION_DEFINITION ||
                    decl.getKind() == CsmDeclaration.Kind.FUNCTION_FRIEND_DEFINITION) {
                CsmFunctionDefinition fd = (CsmFunctionDefinition) decl;
                if( fd.getStartOffset() < this.offset && this.offset < fd.getEndOffset()  ) {
                    CsmNamespace ns = BaseUtilitiesProviderImpl.getImpl()._getFunctionNamespace(fd);
                    if( ns != null && ! ns.isGlobal() ) {
                        containingNamespace = ns;
                    }
                    CsmFunction fun = getFunctionDeclaration(fd);
                    if( fun != null && CsmKindUtilities.isMethodDeclaration(fun) ) {
                        containingClass = getMethodContainingClass((CsmMethod) fun);
                    }
                }
            }
        }
    }

    private CsmFunction getFunctionDeclaration(CsmFunctionDefinition fd){
        if (isRecursionOnResolving(INFINITE_RECURSION)) {
            return null;
        }
        return fd.getDeclaration();
    }
    
    private CsmClass getMethodContainingClass(CsmMethod m){
        return m.getContainingClass();
    }
    

    @Override
    public boolean isRecursionOnResolving(int maxRecursion) {
        Resolver3 parent = (Resolver3)parentResolver;
        int count = 0;
        while(parent != null) {
            if (parent.origOffset == origOffset && parent.file.equals(file)) {
                if (TRACE_RECURSION) { traceRecursion(); }
                return true;
            }
            parent = (Resolver3) parent.parentResolver;
            count++;
            if (count > maxRecursion) {
                if (TRACE_RECURSION) { traceRecursion(); }
                return true;
            }
        }
        return false;
    }

    private CsmObject resolveInUsings(CsmNamespace containingNS, CharSequence nameToken) {
        if (isRecursionOnResolving(INFINITE_RECURSION)) {
            return null;
        }
        CsmObject result = null;
        for (CsmUsingDirective udir : CsmUsingResolver.getDefault().findUsingDirectives(containingNS)) {
            String fqn = udir.getName() + "::" + nameToken; // NOI18N
            result = findClassifierUsedInFile(fqn);
            if (result != null) {
                break;
            }
        }
        if (result == null) {
            CsmUsingResolver ur = CsmUsingResolver.getDefault();
             Collection<CsmDeclaration> decls = null;
            decls = ur.findUsedDeclarations(containingNS);
            for (CsmDeclaration decl : decls) {
                if (CharSequences.comparator().compare(nameToken, decl.getName()) == 0) {
                    if (CsmKindUtilities.isClassifier(decl) && needClassifiers()) {
                        result = decl;
                        break;
                    } else if (CsmKindUtilities.isClass(decl) && needClasses()) {
                        result = decl;
                        break;
                    }
                }
            }
        }
        return result;
    }

    void traceRecursion(){
        System.out.println("Detected recursion in resolver:"); // NOI18N
        System.out.println("\t"+this); // NOI18Nv
        Resolver3 parent = (Resolver3)parentResolver;
        while(parent != null) {
            System.out.println("\t"+parent); // NOI18N
            parent = (Resolver3) parent.parentResolver;
        }
        new Exception().printStackTrace();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(file.getAbsolutePath()).append(":").append(origOffset); // NOI18N
        buf.append(":Looking for "); // NOI18N
        if (needClassifiers()) {
            if (needClasses()) {
                buf.append("c"); // NOI18N
            } else {
                buf.append("C"); // NOI18N
            }
        }
        if (needNamespaces()) {
            buf.append("N"); // NOI18N
        }
        buf.append(":").append(currName()); // NOI18N
        for(int i = 0; i < names.length; i++){
            if (i == 0) {
                buf.append("?"); // NOI18N
            } else {
                buf.append("::"); // NOI18N
            }
            buf.append(names[i]); // NOI18N
        }

        if (containingClass != null) {
            buf.append(":Class=").append(containingClass.getName()); // NOI18N
        }
        if (containingNamespace != null) {
            buf.append(":NS=").append(containingNamespace.getName()); // NOI18N
        }
        return buf.toString();
    }

    private static final CsmFilter NO_FILTER = CsmSelect.getFilterBuilder().createOffsetFilter(0, Integer.MAX_VALUE);
    private static final CsmFilter NAMESPACE_FILTER = CsmSelect.getFilterBuilder().createKindFilter(
                         CsmDeclaration.Kind.NAMESPACE_DEFINITION
                       , CsmDeclaration.Kind.NAMESPACE_ALIAS
                       , CsmDeclaration.Kind.USING_DECLARATION
                       , CsmDeclaration.Kind.USING_DIRECTIVE
                       );
    private static final CsmFilter CLASS_FILTER = CsmSelect.getFilterBuilder().createKindFilter(
                         CsmDeclaration.Kind.NAMESPACE_DEFINITION
                       , CsmDeclaration.Kind.NAMESPACE_ALIAS
                       , CsmDeclaration.Kind.USING_DECLARATION
                       , CsmDeclaration.Kind.USING_DIRECTIVE
                       , CsmDeclaration.Kind.TYPEDEF
                       , CsmDeclaration.Kind.CLASS
                       , CsmDeclaration.Kind.ENUM
                       , CsmDeclaration.Kind.STRUCT
                       , CsmDeclaration.Kind.UNION
                       );

    private void gatherMaps(CsmFile file, boolean visitIncludedFiles) {
        if( file == null || visitedFiles.contains(file) ) {
            return;
        }
        visitedFiles.add(file);
        CsmFilter filter;
        if (offset == Integer.MAX_VALUE) {
            filter = NO_FILTER;
        } else {
            filter = CsmSelect.getFilterBuilder().createOffsetFilter(0, offset);
        }
        if (visitIncludedFiles) {
            Iterator<CsmInclude> iter = CsmSelect.getIncludes(file, filter);
            while (iter.hasNext()){
                CsmInclude inc = iter.next();
                CsmFile incFile = inc.getIncludeFile();
                if( incFile != null ) {
                    int oldOffset = offset;
                    offset = Integer.MAX_VALUE;
                    gatherMaps(incFile, true);
                    offset = oldOffset;
                }
            }
        }
        if (offset == Integer.MAX_VALUE) {
            if (needClassifiers()) {
                filter = CLASS_FILTER;
            } else {
                filter = NAMESPACE_FILTER;
            }
        }
        gatherMaps(CsmSelect.getDeclarations(file, filter));
        if (!visitIncludedFiles) {
            visitedFiles.remove(file);
        }
    }

    private void gatherMaps(Iterable<? extends CsmObject> declarations) {
        gatherMaps(declarations.iterator());
    }

    private void gatherMaps(Iterator<? extends CsmObject> it) {
        while(it.hasNext()) {
            CsmObject o = it.next();
            assert o instanceof CsmOffsetable;
            try {
                int start = ((CsmOffsetable) o).getStartOffset();
                int end = ((CsmOffsetable) o).getEndOffset();
                if( start >= this.offset ) {
                    break;
                }
                //assert o instanceof CsmScopeElement;
                if( o instanceof CsmScopeElement ) {

                    // not yet in local context, but jumping into it
                    boolean oldValue = inLocalContext;
                    if (!inLocalContext && CsmKindUtilities.isFunctionDefinition(o)) {
                        inLocalContext = true;
                    }
                    gatherMaps((CsmScopeElement) o, end);
                    inLocalContext = oldValue;
                } else {
                    if( FileImpl.reportErrors ) {
                        System.err.println("Expected CsmScopeElement, got " + o);
                    }
                }
            } catch (NullPointerException ex) {
                if( FileImpl.reportErrors ) {
                    // FIXUP: do not crush on NPE
                    System.err.println("Unexpected NULL element in declarations collection");
                    DiagnosticExceptoins.register(ex);
                }
            }
        }
    }

    private CsmClassifier findNestedClassifier(CsmClassifier clazz) {
        if (CsmKindUtilities.isClass(clazz)) {
            Iterator<CsmMember> it = CsmSelect.getClassMembers((CsmClass)clazz,
                    CsmSelect.getFilterBuilder().createNameFilter(currName(), true, true, false));
            while(it.hasNext()) {
                CsmMember member = it.next();
                if( CharSequences.comparator().compare(currName(),member.getName())==0 ) {
                    if(CsmKindUtilities.isClassifier(member)) {
                        return (CsmClassifier) member;
                    }
                }
            }
        }
        return null;
    }

    private void doProcessTypedefsInUpperNamespaces(CsmNamespaceDefinition nsd) {
        CsmFilter filter =  CsmSelect.getFilterBuilder().createKindFilter(
                                  CsmDeclaration.Kind.NAMESPACE_DEFINITION,
                                  CsmDeclaration.Kind.TYPEDEF);
        for (Iterator<CsmOffsetableDeclaration> iter = CsmSelect.getDeclarations(nsd, filter); iter.hasNext();) {
            CsmOffsetableDeclaration decl = iter.next();
            if( decl.getKind() == CsmDeclaration.Kind.NAMESPACE_DEFINITION ) {
                processTypedefsInUpperNamespaces((CsmNamespaceDefinition) decl);
            } else if( decl.getKind() == CsmDeclaration.Kind.TYPEDEF ) {
                CsmTypedef typedef = (CsmTypedef) decl;
                if( CharSequences.comparator().compare(currName(),typedef.getName())==0 ) {
                    currTypedef = typedef;
                }
            }
        }
    }

    private void processTypedefsInUpperNamespaces(CsmNamespaceDefinition nsd) {
        if( CharSequences.comparator().compare(nsd.getName(),currName())==0 )  {
            currNamIdx++;
            doProcessTypedefsInUpperNamespaces(nsd);
        } else {
            CsmNamespace cns = getContainingNamespace();
            if( cns != null ) {
                if( cns.equals(nsd.getNamespace())) {
                    doProcessTypedefsInUpperNamespaces(nsd);
                }
            }
        }
    }

    /**
     * It is guaranteed that element.getStartOffset < this.offset
     */
    private void gatherMaps(CsmScopeElement element, int end) {

        CsmDeclaration.Kind kind = (element instanceof CsmDeclaration) ? ((CsmDeclaration) element).getKind() : null;
        if( kind == CsmDeclaration.Kind.NAMESPACE_DEFINITION ) {
            CsmNamespaceDefinition nsd = (CsmNamespaceDefinition) element;
            if (nsd.getName().length() == 0) {
                // this is unnamed namespace and it should be considered as
                // it declares using itself
                usedNamespaces.add(nsd.getQualifiedName());
            }
            if (this.offset < end || isInContext(nsd)) {
                //currentNamespace = nsd.getNamespace();
                gatherMaps(nsd.getDeclarations());
            } else if (needClassifiers()){
                processTypedefsInUpperNamespaces(nsd);
            }
        } else if( kind == CsmDeclaration.Kind.NAMESPACE_ALIAS ) {
            CsmNamespaceAlias alias = (CsmNamespaceAlias) element;
            namespaceAliases.put(alias.getAlias(), alias.getReferencedNamespace());
        } else if( kind == CsmDeclaration.Kind.USING_DECLARATION ) {
            CsmDeclaration decl = resolveUsingDeclaration((CsmUsingDeclaration) element);
            if( decl != null ) {
                CharSequence id;
                if( decl.getKind() == CsmDeclaration.Kind.FUNCTION || decl.getKind() == CsmDeclaration.Kind.FUNCTION_DEFINITION ||
                        decl.getKind() == CsmDeclaration.Kind.FUNCTION_FRIEND || decl.getKind() == CsmDeclaration.Kind.FUNCTION_FRIEND) {
                    // TODO: decide how to resolve functions
                    id = ((CsmFunction) decl).getSignature();
                } else {
                    id = decl.getName();
                }
                usingDeclarations.put(id, decl);
            }
        } else if( kind == CsmDeclaration.Kind.USING_DIRECTIVE ) {
            CsmUsingDirective udir = (CsmUsingDirective) element;
            CharSequence name = udir.getName();
            if (!usedNamespaces.contains(name)) {
                usedNamespaces.add(name); // getReferencedNamespace()
            }
        } else if( element instanceof CsmDeclarationStatement ) {
            CsmDeclarationStatement ds = (CsmDeclarationStatement) element;
            if( ds.getStartOffset() < this.offset ) {
                gatherMaps( ((CsmDeclarationStatement) element).getDeclarators());
            }
        } else if (CsmKindUtilities.isScope(element)) {
            if (inLocalContext && needClassifiers() && CsmKindUtilities.isClassifier(element)) {
                // don't want forward to find itself
                if (!CsmKindUtilities.isClassForwardDeclaration(element) || (this.offset > end)) {
                    if (CharSequences.comparator().compare(currName(), ((CsmClassifier)element).getName()) == 0) {
                        currLocalClassifier = (CsmClassifier)element;
                    }
                }
            }
            if (this.offset < end || isInContext((CsmScope) element)) {
                gatherMaps( ((CsmScope) element).getScopeElements());
            }
        } else if( kind == CsmDeclaration.Kind.TYPEDEF && needClassifiers()){
            CsmTypedef typedef = (CsmTypedef) element;
            // don't want typedef to find itself
            if( this.offset > end && CharSequences.comparator().compare(currName(),typedef.getName())==0 ) {
                currTypedef = typedef;
            }
        }
    }

    private boolean isInContext(CsmScope scope) {
        if (!CsmKindUtilities.isClass(scope) && !CsmKindUtilities.isNamespace(scope)) {
            return false;
        }
        CsmQualifiedNamedElement el = (CsmQualifiedNamedElement)scope;
        CsmNamespace ns = getContainingNamespace();
        if (ns != null && startsWith(ns.getQualifiedName(), el.getQualifiedName())) {
            return true;
        }
        CsmClass cls = getContainingClass();
        if (cls != null && startsWith(cls.getQualifiedName(), el.getQualifiedName())) {
            return true;
        }
        return false;
    }

    private boolean startsWith(CharSequence qname, CharSequence prefix) {
        if (qname.length() < prefix.length()) {
            return false;
        }
        for (int i = 0; i < prefix.length(); ++i) {
            if (qname.charAt(i) != prefix.charAt(i)) {
                return false;
            }
        }
        return qname.length() == prefix.length()
                || qname.charAt(prefix.length()) == ':'; // NOI18N
    }

    private CsmDeclaration resolveUsingDeclaration(CsmUsingDeclaration udecl){
        if (isRecursionOnResolving(LIMITED_RECURSION)) {
            return null;
        }
        return  udecl.getReferencedDeclaration();
    }

    /**
     * Resolver class or namespace name.
     * Why class or namespace? Because in usage of kind org::vk::test
     * you don't know which is class and which is namespace name
     *
     * @param nameTokens tokenized name to resolve
     * (for example, for std::vector it is new CharSequence[] { "std", "vector" })
     *
     * @param context declaration within which the name found
     *
     * @return object of the following class:
     *  CsmClass
     *  CsmEnum
     *  CsmNamespace
     */
    @Override
    public CsmObject resolve(CharSequence[] nameTokens, int interestedKind) {
        CsmObject result = null;

        names = nameTokens;
        currNamIdx = 0;
        this.interestedKind = interestedKind;
        CsmNamespace containingNS = null;

        if( nameTokens.length == 1 ) {
            if( result == null && needClassifiers()) {
                CsmClass cls = getContainingClass();
                result = resolveInClass(cls, nameTokens[0]);
                if( result == null ) {
                    if (parentResolver == null || !((Resolver3)parentResolver).resolveInBaseClass) {
                        result = resolveInBaseClasses(cls, nameTokens[0]);
                    }
                }
            }
            if (result == null && needClassifiers()) {
                containingNS = getContainingNamespace();
                result = findClassifier(containingNS, nameTokens[0]);
                if (result == null && containingNS != null) {
                    result = resolveInUsings(containingNS, nameTokens[0]);
                }
            }
            if( result == null  && needNamespaces()) {
                containingNS = getContainingNamespace();
                result = findNamespace(containingNS, nameTokens[0]);
            }
            if (result == null  && needClassifiers()){
                result = findClassifierUsedInFile(nameTokens[0]);
            }
            if( result == null ) {
                gatherMaps(file, true);
                if( currLocalClassifier != null && needClassifiers()) {
                    result = currLocalClassifier;
                }
                if( currTypedef != null && needClassifiers()) {
                    result = currTypedef;
                }

                if( result == null ) {
                    CsmDeclaration decl = usingDeclarations.get(CharSequences.create(nameTokens[0]));
                    if( decl != null ) {
                        result = decl;
                    }
                }

                if( result == null && needClassifiers()) {
                    for (Iterator<CharSequence> iter = usedNamespaces.iterator(); iter.hasNext();) {
                        String nsp = iter.next().toString();
                        String fqn = nsp + "::" + nameTokens[0]; // NOI18N
                        result = findClassifierUsedInFile(fqn);
                        if (result == null) {
                            result = findClassifier(containingNS, fqn);
                        }
                        if (result == null) {
                            CsmNamespace ns = findNamespace(nsp);
                            if (ns != null) {
                                result = resolveInUsings(ns, nameTokens[0]);
                            }
                        }
                        if( result != null ) {
                            break;
                        }
                    }
                }

                if( result == null && needNamespaces()) {
                    Object o = namespaceAliases.get(CharSequences.create(nameTokens[0]));
                    if( o instanceof CsmNamespace ) {
                        result = (CsmNamespace) o;
                    }
                }

                if( result == null && needNamespaces()) {
                    for (Iterator<CharSequence> iter = usedNamespaces.iterator(); iter.hasNext();) {
                        String nsp = iter.next().toString();
                        String fqn = nsp + "::" + nameTokens[0]; // NOI18N
                        result = findNamespace(fqn);
                        if( result != null ) {
                            break;
                        }
                    }
                }
            }
            if( result == null ) {
                if(TemplateUtils.isTemplateQualifiedName(nameTokens[0].toString())) {
                    Resolver aResolver = ResolverFactory.createResolver(file, offset);
                    try {
                        result = aResolver.resolve(Utils.splitQualifiedName(TemplateUtils.getTemplateQualifiedNameWithoutSiffix(nameTokens[0].toString())), interestedKind);
                    } finally {
                        ResolverFactory.releaseResolver(aResolver);
                    }
                }
            }
        } else if( nameTokens.length > 1 ) {
            StringBuilder sb = new StringBuilder(nameTokens[0]);
            for (int i = 1; i < nameTokens.length; i++) {
                sb.append("::"); // NOI18N
                sb.append(nameTokens[i]);
            }
            if (needClassifiers()) {
                result = findClassifierUsedInFile(sb.toString());
            }
            if( result == null && needClassifiers()) {
                containingNS = getContainingNamespace();
                result = findClassifier(containingNS, sb.toString());
            }
            if( result == null && needNamespaces()) {
                containingNS = getContainingNamespace();
                result = findNamespace(containingNS, sb.toString());
            }
            if( result == null && needClassifiers()) {
                gatherMaps(file, true);
                if( currTypedef != null) {
                    CsmType type = currTypedef.getType();
                    if( type != null ) {
                        CsmClassifier currentClassifier = getTypeClassifier(type);
                        while (currNamIdx < names.length -1 && currentClassifier != null) {
                            currNamIdx++;
                            currentClassifier = findNestedClassifier(currentClassifier);
                            if (CsmKindUtilities.isTypedef(currentClassifier)) {
                                CsmType curType = ((CsmTypedef)currentClassifier).getType();
                                currentClassifier = curType == null ? null : getTypeClassifier(curType);
                            }
                        }
                        if (currNamIdx == names.length - 1) {
                            result = currentClassifier;
                        }
                    }
                }

                if( result == null ) {
                    for (Iterator<CharSequence> iter = usedNamespaces.iterator(); iter.hasNext();) {
                        String nsp = iter.next().toString();
                        String fqn = nsp + "::" + sb; // NOI18N
                        result = findClassifierUsedInFile(fqn);
                        if( result != null ) {
                            break;
                        }
                    }
                }

                if( result == null ) {
                    CsmObject first = null;
                    Resolver aResolver = ResolverFactory.createResolver(file, origOffset);
                    try {
                        first = aResolver.resolve(Utils.splitQualifiedName(nameTokens[0].toString()), NAMESPACE);
                    } finally {
                        ResolverFactory.releaseResolver(aResolver);
                    }
                    if( first != null ) {
                        if( first instanceof CsmNamespace ) {
                            NamespaceImpl ns = (NamespaceImpl) first;
                            sb = new StringBuilder(ns.getQualifiedName());
                            for (int i = 1; i < nameTokens.length; i++) {
                                sb.append("::"); // NOI18N
                                sb.append(nameTokens[i]);
                            }
                            result = findClassifierUsedInFile(sb.toString());
                            if (result == null) {
                                sb = new StringBuilder(nameTokens[1]);
                                for (int i = 2; i < nameTokens.length; i++) {
                                    sb.append("::"); // NOI18N
                                    sb.append(nameTokens[i]);
                                }
                                result = resolveInUsings(ns, sb.toString());
                            }
                        } else if( first instanceof CsmClass ) {

                        }
                    }
                }
            }
            if (result == null && needNamespaces()) {
                CsmObject obj = null;
                Resolver aResolver = ResolverFactory.createResolver(file, origOffset);
                try {
                    obj = aResolver.resolve(Utils.splitQualifiedName(nameTokens[0].toString()), NAMESPACE);
                } finally {
                    ResolverFactory.releaseResolver(aResolver);
                }
                if (obj instanceof CsmNamespace) {
                    CsmNamespace ns = (CsmNamespace) obj;
                    for (int i = 1; i < nameTokens.length; i++) {
                        CsmNamespace newNs = null;
                        CharSequence name = nameTokens[i];
                        Collection<CsmNamespaceAlias> aliases = CsmUsingResolver.getDefault().findNamespaceAliases(ns);
                        for (CsmNamespaceAlias alias : aliases) {
                            if (alias.getAlias().toString().equals(name.toString())) {
                                newNs = alias.getReferencedNamespace();
                                break;
                            }
                        }
                        if (newNs == null) {
                            Collection<CsmNamespace> namespaces = ns.getNestedNamespaces();
                            for (CsmNamespace namespace : namespaces) {
                                if (namespace.getName().toString().equals(name.toString())) {
                                    newNs = namespace;
                                    break;
                                }
                            }
                        }
                        ns = newNs;
                        if (ns == null) {
                            break;
                        }
                    }
                    result = ns;
                }
            }
            if( result == null ) {
                if( TemplateUtils.isTemplateQualifiedName(sb.toString())) {
                    StringBuilder sb2 = new StringBuilder(TemplateUtils.getTemplateQualifiedNameWithoutSiffix(nameTokens[0].toString()));
                    for (int i = 1; i < nameTokens.length; i++) {
                        sb2.append("::"); // NOI18N
                        sb2.append(TemplateUtils.getTemplateQualifiedNameWithoutSiffix(nameTokens[i].toString()));
                    }
                    Resolver aResolver = ResolverFactory.createResolver(file, offset);
                    try {
                        result = aResolver.resolve(Utils.splitQualifiedName(sb2.toString()), interestedKind);
                    } finally {
                        ResolverFactory.releaseResolver(aResolver);
                    }
                }
            }
        }
        return result;
    }

    private CsmClassifier getTypeClassifier(CsmType type){
        if (isRecursionOnResolving(INFINITE_RECURSION)) {
            return null;
        }
        return type.getClassifier();
    }

    private CsmObject resolveInBaseClasses(CsmClass cls, CharSequence name) {
        resolveInBaseClass = true;
        CsmObject res = _resolveInBaseClasses(cls, name, new HashSet<CharSequence>(), 0);
        resolveInBaseClass = false;
        return res;
    }

    private CsmObject _resolveInBaseClasses(CsmClass cls, CharSequence name, Set<CharSequence> antiLoop, int depth) {
        if (depth == 50) {
            new Exception("Too many loops in resolver!!!").printStackTrace(System.err); // NOI18N
            return null;
        }
        if(isNotNullNotUnresolved(cls)) {
            List<CsmClass> toAnalyze = getClassesContainers(cls);
            for (CsmClass csmClass : toAnalyze) {
                for (CsmInheritance inh : csmClass.getBaseClasses()) {
                    CsmClass base = getInheritanceClass(inh);
                    if (base != null && !antiLoop.contains(base.getQualifiedName())) {
                        antiLoop.add(base.getQualifiedName());
                        CsmObject result = resolveInClass(base, name);
                        if (result != null) {
                            return result;
                        }
                        result = _resolveInBaseClasses(base, name, antiLoop, depth + 1);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return null;
    }

    private CsmClass getInheritanceClass(CsmInheritance inh){
        if (inh instanceof InheritanceImpl) {
            if (isRecursionOnResolving(INFINITE_RECURSION)) {
                return null;
            }
            CsmClassifier out = inh.getClassifier();
            out = getOriginalClassifier(out);
            if (CsmKindUtilities.isClass(out)) {
                return (CsmClass) out;
            }
        }
        return getCsmClass(inh);
    }

    private CsmClass getCsmClass(CsmInheritance inh) {
        CsmClassifier classifier = inh.getClassifier();
        classifier = getOriginalClassifier(classifier);
        if (CsmKindUtilities.isClass(classifier)) {
            return (CsmClass)classifier;
        }
        return null;
    }

    private boolean isNotNullNotUnresolved(Object obj) {
        return obj != null && !Unresolved.isUnresolved(obj);
    }

    private CsmObject resolveInClass(CsmClass cls, CharSequence name) {
        if(isNotNullNotUnresolved(cls)){
            List<CsmClass> classesContainers = getClassesContainers(cls);
            for (CsmClass csmClass : classesContainers) {
                CsmClassifier classifier = null;
                CsmFilter filter = CsmSelect.getFilterBuilder().createNameFilter(name, true, true, false);
                Iterator<CsmMember> it = CsmSelect.getClassMembers(csmClass, filter);
                while (it.hasNext()) {
                    CsmMember member = it.next();
                    if (CsmKindUtilities.isClassifier(member)) {
                        classifier = (CsmClassifier) member;
                        if (!CsmKindUtilities.isClassForwardDeclaration(classifier)) {
                            return classifier;
                        }
                    }
                }
                if (classifier != null) {
                    return classifier;
                }
            }
        }
        return null;
    }

    private List<CsmClass> getClassesContainers(CsmClass cls) {
        List<CsmClass> out = new ArrayList<CsmClass>();
        CsmScope container = cls;
        while (CsmKindUtilities.isClass(container)) {
            out.add((CsmClass)container);
            container = ((CsmClass)container).getScope();
        }
        return out;
    }

    private boolean needClassifiers() {
        return ((interestedKind & CLASSIFIER) == CLASSIFIER) || needClasses();
    }

    private boolean needNamespaces() {
        return (interestedKind & NAMESPACE) == NAMESPACE;
    }

    private boolean needClasses() {
        return (interestedKind & CLASS) == CLASS;
    }
}
