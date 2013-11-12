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
package org.netbeans.modules.web.jsf.editor.completion;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementScanner6;
import javax.swing.ImageIcon;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.html.editor.api.completion.HtmlCompletionItem;
import org.netbeans.modules.html.editor.api.gsf.HtmlExtension;
import org.netbeans.modules.html.editor.api.gsf.HtmlParserResult;
import org.netbeans.modules.html.editor.lib.api.elements.Element;
import org.netbeans.modules.html.editor.lib.api.elements.ElementType;
import org.netbeans.modules.html.editor.lib.api.elements.ElementUtils;
import org.netbeans.modules.html.editor.lib.api.elements.ElementVisitor;
import org.netbeans.modules.html.editor.lib.api.elements.Node;
import org.netbeans.modules.html.editor.lib.api.elements.OpenTag;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.common.api.FileReferenceCompletion;
import org.netbeans.modules.web.common.api.LexerUtils;
import org.netbeans.modules.web.common.taginfo.AttrValueType;
import org.netbeans.modules.web.common.taginfo.LibraryMetadata;
import org.netbeans.modules.web.common.taginfo.TagAttrMetadata;
import org.netbeans.modules.web.common.taginfo.TagMetadata;
import org.netbeans.modules.web.jsf.editor.JsfSupportImpl;
import org.netbeans.modules.web.jsf.editor.JsfUtils;
import org.netbeans.modules.web.jsf.editor.facelets.CompositeComponentLibrary;
import org.netbeans.modules.web.jsf.editor.facelets.FaceletsLibraryMetadata;
import org.netbeans.modules.web.jsf.editor.index.CompositeComponentModel;
import org.netbeans.modules.web.jsf.editor.index.JsfPageModelFactory;
import org.netbeans.modules.web.jsfapi.api.Attribute;
import org.netbeans.modules.web.jsfapi.api.DefaultLibraryInfo;
import org.netbeans.modules.web.jsfapi.api.Library;
import org.netbeans.modules.web.jsfapi.api.LibraryComponent;
import org.netbeans.modules.web.jsfapi.api.NamespaceUtils;
import org.netbeans.modules.web.jsfapi.spi.LibraryUtils;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 * Contains helper method for completion within Facelet.
 *
 * @author Martin Fousek <marfous@netbeans.org>
 */
public class JsfAttributesCompletionHelper {

    private static final FilenameSupport FILENAME_SUPPORT = new FilenameSupport();

    private JsfAttributesCompletionHelper() {
    }

    public static void completeJavaClasses(final HtmlExtension.CompletionContext context, final List<CompletionItem> items, String ns, OpenTag openTag) {
        // <cc:attribute type="com.example.|
        String tagName = openTag.unqualifiedName().toString();
        String attrName = context.getAttributeName();
        if (NamespaceUtils.containsNsOf(Collections.singleton(ns), DefaultLibraryInfo.COMPOSITE)
                && "attribute".equalsIgnoreCase(tagName) && "type".equalsIgnoreCase(attrName)) { //NOI18N

            FileObject fileObject = context.getResult().getSnapshot().getSource().getFileObject();
            JavaSource js = JavaSource.create(ClasspathInfo.create(fileObject));
            if (js == null) {
                return;
            }

            try {
                js.runUserActionTask(new org.netbeans.api.java.source.Task<CompilationController>() {
                    @Override
                    public void run(CompilationController cc) throws Exception {
                        String prefix = context.getPrefix();
                        String packageName = context.getPrefix();

                        int dotIndex = prefix.lastIndexOf('.'); // NOI18N
                        if (dotIndex != -1) {
                            packageName = prefix.substring(0, dotIndex);
                        }

                        // adds packages to the CC
                        addPackages(context, cc, items, prefix);

                        // adds types to the CC
                        addTypesFromPackages(context, cc, items, prefix, packageName);
                    }
                }, true);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private static void addPackages(HtmlExtension.CompletionContext context, CompilationController controler, List<CompletionItem> items, String prefix) {
        int dotOffset = prefix.lastIndexOf('.');
        for (String pkgName : controler.getClasspathInfo().getClassIndex().getPackageNames(prefix, true, EnumSet.of(ClassIndex.SearchScope.SOURCE))) {
            items.add(HtmlCompletionItem.createAttributeValue(
                    pkgName.substring(dotOffset == -1 ? 0 : dotOffset + 1),
                    context.getCCItemStartOffset() + (dotOffset == -1 ? 0 : dotOffset + 1)));
        }
    }

    private static void addTypesFromPackages(HtmlExtension.CompletionContext context, CompilationController cc, List<CompletionItem> items, String prefix, String packageName) {
        int dotOffset = prefix.lastIndexOf('.');
        PackageElement pkgElem = cc.getElements().getPackageElement(packageName);
        if (pkgElem == null) {
            return;
        }

        List<TypeElement> tes = new TypeScanner().scan(pkgElem);
        for (TypeElement te : tes) {
            if (te.getQualifiedName().toString().startsWith(prefix)) {
                items.add(HtmlCompletionItem.createAttributeValue(
                        te.getSimpleName().toString(),
                        context.getCCItemStartOffset() + (dotOffset == -1 ? 0 : dotOffset + 1)));
            }
        }
    }

    public static void completeSectionsOfTemplate(final HtmlExtension.CompletionContext context, final List<CompletionItem> items, String ns, OpenTag openTag) {
        // <ui:define name="|" ...
        String tagName = openTag.unqualifiedName().toString();
        String attrName = context.getAttributeName();
        if (NamespaceUtils.containsNsOf(Collections.singleton(ns), DefaultLibraryInfo.FACELETS)
                && "define".equalsIgnoreCase(tagName) && "name".equalsIgnoreCase(attrName)) { //NOI18N

            // get the template path
            Node root = JsfUtils.getRoot(context.getResult(), DefaultLibraryInfo.FACELETS);
            final String[] template = new String[1];
            ElementUtils.visitChildren(root, new ElementVisitor() {
                @Override
                public void visit(Element node) {
                    OpenTag openTag = (OpenTag) node;
                    if ("composition".equalsIgnoreCase(openTag.unqualifiedName().toString())) { //NOI18N
                        for (org.netbeans.modules.html.editor.lib.api.elements.Attribute attribute : openTag.attributes()) {
                            if ("template".equalsIgnoreCase(attribute.name().toString())) { //NOI18N
                                template[0] = attribute.unquotedValue().toString();
                            }
                        }
                    }
                }
            }, ElementType.OPEN_TAG);

            if (template[0] == null) {
                return;
            }

            // find the template inside the web root or resource library contract
            List<Source> candidates = getTemplateCandidates(context.getResult().getSnapshot().getSource().getFileObject(), template[0]);
            try {
                ParserManager.parse(candidates, new UserTask() {
                    @Override
                    public void run(ResultIterator resultIterator) throws Exception {
                        Parser.Result result = resultIterator.getParserResult(0);
                        if (result.getSnapshot().getMimeType().equals("text/html")) {
                            HtmlParserResult htmlResult = (HtmlParserResult) result;
                            Node root = JsfUtils.getRoot(htmlResult, DefaultLibraryInfo.FACELETS);
                            if (root != null) {
                                List<OpenTag> foundNodes = findValue(root.children(OpenTag.class), "ui:insert", new ArrayList<OpenTag>()); //NOI18N
                                for (OpenTag node : foundNodes) {
                                    org.netbeans.modules.html.editor.lib.api.elements.Attribute attr = node.getAttribute("name"); //NOI18N
                                    if (attr != null) {
                                        String value = attr.unquotedValue().toString();
                                        if (value != null && !"".equals(value)) { //NOI18N
                                            items.add(HtmlCompletionItem.createAttributeValue(value, context.getCCItemStartOffset(), !context.isValueQuoted())); //NOI18N
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            } catch (ParseException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private static List<OpenTag> findValue(Collection<OpenTag> nodes, String tagName, List<OpenTag> foundNodes) {
        if (nodes == null) {
            return foundNodes;
        }
        for (OpenTag ot : nodes) {
            if (LexerUtils.equals(tagName, ot.name(), true, false)) {
                foundNodes.add(ot);
            } else {
                foundNodes = findValue(ot.children(OpenTag.class), tagName, foundNodes);
            }

        }
        return foundNodes;
    }

    private static List<Source> getTemplateCandidates(FileObject client, String path) {
        List<Source> result = new ArrayList<>();
        FileObject template = client.getParent().getFileObject(path);
        if (template != null) {
            result.add(Source.create(template));
        }

        Project project = FileOwnerQuery.getOwner(client);
        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());
        if (wm != null && wm.getDocumentBase() != null) {
            handleContracts(wm.getDocumentBase(), path, result);
        } else {
            Sources sources = ProjectUtils.getSources(project);
            SourceGroup[] sourceGroups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            for (SourceGroup sourceGroup : sourceGroups) {
                FileObject metaInf = sourceGroup.getRootFolder().getFileObject("META-INF"); //NOI18N
                if (metaInf != null) {
                    handleContracts(metaInf, path, result);
                }
            }
        }

        return result;
    }

    private static void handleContracts(FileObject parent, String path, List<Source> result) {
        FileObject contractsFolder = parent.getFileObject("contracts"); //NOI18N
        if (contractsFolder != null) {
            for (FileObject child : contractsFolder.getChildren()) {
                FileObject contract = child.getFileObject(path);
                if (contract != null) {
                    result.add(Source.create(contract));
                }
            }
        }
    }

    //1.
    //<cc:implementation>
    //<cc:render/insertFacet name="|" />
    //</cc:implementation>
    //offsers facet declarations only from within this document
    public static void completeFacetsInCCImpl(HtmlExtension.CompletionContext context, List<CompletionItem> items, String ns, OpenTag openTag, JsfSupportImpl jsfs) {
        if ("http://java.sun.com/jsf/composite".equalsIgnoreCase(ns) || "http://xmlns.jcp.org/jsf/composite".equalsIgnoreCase(ns)) {
            String tagName = openTag.unqualifiedName().toString();
            if ("renderFacet".equalsIgnoreCase(tagName) || "insertFacet".equalsIgnoreCase(tagName)) { //NOI18N
                if ("name".equalsIgnoreCase(context.getAttributeName())) { //NOI18N
                    CompositeComponentModel ccModel = (CompositeComponentModel) JsfPageModelFactory.getFactory(CompositeComponentModel.Factory.class).getModel(context.getResult());
                    if (ccModel != null) {
                        Collection<String> facets = ccModel.getDeclaredFacets();
                        for (String facet : facets) {
                            items.add(HtmlCompletionItem.createAttributeValue(facet, context.getCCItemStartOffset(), !context.isValueQuoted())); //NOI18N
                        }
                    }
                }
            }
        }
    }

    //2.<f:facet name="|">
    //offsers all facetes
    public static void completeFacets(HtmlExtension.CompletionContext context, List<CompletionItem> items, String ns, OpenTag openTag, JsfSupportImpl jsfs) {
        if ("http://java.sun.com/jsf/core".equalsIgnoreCase(ns) || "http://xmlns.jcp.org/jsf/core".equalsIgnoreCase(ns)) {
            String tagName = openTag.unqualifiedName().toString();
            if ("facet".equalsIgnoreCase(tagName)) { //NOI18N
                if ("name".equalsIgnoreCase(context.getAttributeName())) { //NOI18N
                    //try to get composite library model for all declared libraries and extract facets from there
                    for (String libraryNs : context.getResult().getNamespaces().keySet()) {
                        Library library = jsfs.getLibrary(libraryNs);
                        if (library != null) {
                            if (library instanceof CompositeComponentLibrary) {
                                Collection<? extends LibraryComponent> lcs = library.getComponents();
                                for (LibraryComponent lc : lcs) {
                                    CompositeComponentLibrary.CompositeComponent ccomp = (CompositeComponentLibrary.CompositeComponent) lc;
                                    CompositeComponentModel model = ccomp.getComponentModel();
                                    for (String facetName : model.getDeclaredFacets()) {
                                        items.add(HtmlCompletionItem.createAttributeValue(facetName, context.getCCItemStartOffset(), !context.isValueQuoted())); //NOI18N
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void completeValueAccordingToType(HtmlExtension.CompletionContext context, List<CompletionItem> items, String ns, OpenTag openTag, JsfSupportImpl jsfs) {
        Library lib = jsfs.getLibrary(ns);
        if (lib == null) {
            return;
        }

        String tagName = openTag.unqualifiedName().toString();

        LibraryComponent comp = lib.getComponent(tagName);
        if (comp == null) {
            return;
        }

        String attrName = context.getAttributeName();
        Attribute attr = comp.getTag().getAttribute(attrName);
        if (attr == null) {
            return;
        }

        //TODO: Add more types and generalize the code then
        String aType = attr.getType();
        if ("boolean".equals(aType) || "java.lang.Boolean".equals(aType)) { //NOI18N
            //boolean type
            items.add(HtmlCompletionItem.createAttributeValue("true", context.getCCItemStartOffset(), !context.isValueQuoted())); //NOI18N
            items.add(HtmlCompletionItem.createAttributeValue("false", context.getCCItemStartOffset(), !context.isValueQuoted())); //NOI18N
        }

    }

    public static void completeFaceletsFromProject(HtmlExtension.CompletionContext context, List<CompletionItem> items, String ns, OpenTag openTag) {
        // <ui:include src="|" ...
        String tagName = openTag.unqualifiedName().toString();
        String attrName = context.getAttributeName();
        if (NamespaceUtils.containsNsOf(Collections.singleton(ns), DefaultLibraryInfo.FACELETS)
                && "include".equalsIgnoreCase(tagName) && "src".equalsIgnoreCase(attrName)) { //NOI18N
            items.addAll(FILENAME_SUPPORT.getItems(
                    context.getResult().getSnapshot().getSource().getFileObject(),
                    context.getCCItemStartOffset(),
                    context.getPrefix()));
        }
    }

    public static void completeXMLNSAttribute(HtmlExtension.CompletionContext context, List<CompletionItem> items, JsfSupportImpl jsfs) {
        if (context.getAttributeName().toLowerCase(Locale.ENGLISH).startsWith("xmlns")) { //NOI18N
            //xml namespace completion for facelets namespaces
            Set<String> nss = NamespaceUtils.getAvailableNss(jsfs.getLibraries(), jsfs.isJsf22Plus());

            //add also xhtml ns to the completion
            nss.add(LibraryUtils.XHTML_NS);
            for (String namespace : nss) {
                if (namespace.startsWith(context.getPrefix())) {
                    items.add(HtmlCompletionItem.createAttributeValue(namespace, context.getCCItemStartOffset(), !context.isValueQuoted()));
                }
            }
        }
    }

    public static void completeTagLibraryMetadata(HtmlExtension.CompletionContext context, List<CompletionItem> items, String ns, OpenTag openTag) {
        String attrName = context.getAttributeName();
        String tagName = openTag.unqualifiedName().toString();
        LibraryMetadata lib = FaceletsLibraryMetadata.get(ns);

        if (lib != null) {
            TagMetadata tag = lib.getTag(tagName);

            if (tag != null) {
                TagAttrMetadata attr = tag.getAttribute(attrName);

                if (attr != null) {
                    Collection<AttrValueType> valueTypes = attr.getValueTypes();

                    if (valueTypes != null) {
                        for (AttrValueType valueType : valueTypes) {
                            String[] possibleVals = valueType.getPossibleValues();

                            if (possibleVals != null) {
                                for (String val : possibleVals) {
                                    if (val.startsWith(context.getPrefix())) {
                                        CompletionItem itm = HtmlCompletionItem.createAttributeValue(val,
                                                context.getCCItemStartOffset(),
                                                !context.isValueQuoted());

                                        items.add(itm);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static class FilenameSupport extends FileReferenceCompletion<HtmlCompletionItem> {

        @Override
        public HtmlCompletionItem createFileItem(FileObject file, int anchor) {
            return HtmlCompletionItem.createFileCompletionItem(file, anchor);
        }

        @Override
        public HtmlCompletionItem createGoUpItem(int anchor, Color color, ImageIcon icon) {
            return HtmlCompletionItem.createGoUpFileCompletionItem(anchor, color, icon); // NOI18N
        }
    }

    private static final class TypeScanner extends ElementScanner6<List<TypeElement>, Void> {

        public TypeScanner() {
            super(new ArrayList<TypeElement>());
        }

        private static boolean isAccessibleClass(TypeElement te) {
            NestingKind nestingKind = te.getNestingKind();
            return (nestingKind == NestingKind.TOP_LEVEL);
        }

        @Override
        public List<TypeElement> visitType(TypeElement typeElement, Void arg) {
            if (typeElement.getKind() == javax.lang.model.element.ElementKind.CLASS && isAccessibleClass(typeElement)) {
                DEFAULT_VALUE.add(typeElement);
            }
            return super.visitType(typeElement, arg);
        }
    }
}
