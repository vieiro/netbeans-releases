/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.php.editor.codegen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.editor.api.ElementQuery.Index;
import org.netbeans.modules.php.editor.api.ElementQueryFactory;
import org.netbeans.modules.php.editor.api.NameKind;
import org.netbeans.modules.php.editor.api.QuerySupportFactory;
import org.netbeans.modules.php.editor.api.elements.ClassElement;
import org.netbeans.modules.php.editor.api.elements.ElementFilter;
import org.netbeans.modules.php.editor.api.elements.ElementTransformation;
import org.netbeans.modules.php.editor.api.elements.MethodElement;
import org.netbeans.modules.php.editor.api.elements.TypeElement;
import org.netbeans.modules.php.editor.api.elements.TreeElement;
import org.netbeans.modules.php.editor.codegen.CGSGenerator.GenWay;
import org.netbeans.modules.php.editor.nav.NavUtils;
import org.netbeans.modules.php.editor.parser.astnodes.*;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Petr Pisl
 */
public class CGSInfo {

    private String className;
    // cotain the class consructor?
    private boolean hasConstructor;
    final private List<Property> properties;
    final private List<Property> possibleGetters;
    final private List<Property> possibleSetters;
    final private List<Property> possibleGettersSetters;
    final private List<MethodProperty> possibleMethods;
    final private JTextComponent textComp;
    /**
     * how to generate  getters and setters method name
     */
    private CGSGenerator.GenWay howToGenerate;
    private boolean generateDoc;

    private CGSInfo(JTextComponent textComp) {
        properties = new ArrayList<Property>();
        possibleGetters = new ArrayList<Property>();
        possibleSetters = new ArrayList<Property>();
        possibleGettersSetters = new ArrayList<Property>();
        possibleMethods = new ArrayList<MethodProperty>();
        className = null;
        this.textComp = textComp;
        hasConstructor = false;
        this.generateDoc = true;
        this.howToGenerate = CGSGenerator.GenWay.AS_JAVA;
    }

    public static CGSInfo getCGSInfo(JTextComponent textComp) {
        CGSInfo info = new CGSInfo(textComp);
        info.findPropertyInScope();
        return info;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public List<MethodProperty> getPossibleMethods() {
        return possibleMethods;
    }

    public List<Property> getPossibleGetters() {
        return possibleGetters;
    }

    public List<Property> getPossibleGettersSetters() {
        return possibleGettersSetters;
    }

    public List<Property> getPossibleSetters() {
        return possibleSetters;
    }

    public String getClassName() {
        return className;
    }

    public boolean hasConstructor() {
        return hasConstructor;
    }

    public GenWay getHowToGenerate() {
        return howToGenerate;
    }

    public void setHowToGenerate(GenWay howGenerate) {
        this.howToGenerate = howGenerate;
    }

    public boolean isGenerateDoc() {
        return generateDoc;
    }

    public void setGenerateDoc(boolean generateDoc) {
        this.generateDoc = generateDoc;
    }


    /**
     * Extract attributes and methods from caret enclosing class and initialize list of properties.
     */
    private void findPropertyInScope() {
        FileObject file = NavUtils.getFile(textComp.getDocument());
        if (file == null) {
            return;
        }
        try {
            ParserManager.parse(Collections.singleton(Source.create(textComp.getDocument())), new UserTask() {

                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    ParserResult info = (ParserResult) resultIterator.getParserResult();
                    int caretOffset = textComp.getCaretPosition();
                    ClassDeclaration classDecl = findEnclosingClass(info, caretOffset);
                    if (classDecl != null) {
                        className = classDecl.getName().getName();
                        if (info != null && className != null) {
                            FileObject fileObject = info.getSnapshot().getSource().getFileObject();
                            Index index = ElementQueryFactory.getIndexQuery(info);
                            final ElementFilter forFilesFilter = ElementFilter.forFiles(fileObject);
                            Set<ClassElement> classes = forFilesFilter.filter(index.getClasses(NameKind.exact(className)));
                            for (ClassElement classElement : classes) {
                                ElementFilter forNotDeclared = ElementFilter.forExcludedElements(index.getDeclaredMethods(classElement));
                                final Set<MethodElement> accessibleMethods = new HashSet<MethodElement>();
                                accessibleMethods.addAll(forNotDeclared.filter(index.getAccessibleMethods(classElement, classElement)));
                                accessibleMethods.addAll(ElementFilter.forExcludedElements(accessibleMethods).filter(forNotDeclared.filter(index.getConstructors(classElement))));
                                accessibleMethods.addAll(ElementFilter.forExcludedElements(accessibleMethods).filter(forNotDeclared.filter(index.getAccessibleMagicMethods(classElement))));
                                final Set<TypeElement> preferedTypes = forFilesFilter.prefer(ElementTransformation.toMemberTypes().transform(accessibleMethods));
                                final TreeElement<TypeElement> enclosingType = index.getInheritedTypesAsTree(classElement, preferedTypes);
                                final List<MethodProperty> properties = new ArrayList<MethodProperty>();
                                final Set<MethodElement> methods = ElementFilter.forMembersOfTypes(preferedTypes).filter(accessibleMethods);
                                for (final MethodElement methodElement : methods) {
                                    if (!methodElement.isFinal()) {
                                        properties.add(new MethodProperty(methodElement, enclosingType));
                                    }
                                }
                                Collections.<MethodProperty>sort(properties, MethodProperty.getComparator());
                                getPossibleMethods().addAll(properties);
                            }
                        }

                        List<String> existingGetters = new ArrayList<String>();
                        List<String> existingSetters = new ArrayList<String>();

                        PropertiesVisitor visitor = new PropertiesVisitor(getProperties(), existingGetters, existingSetters);
                        visitor.scan(classDecl);
                        String propertyName;
                        boolean existGetter, existSetter;
                        for (Property property : getProperties()) {
                            propertyName = property.getName().toLowerCase();
                            existGetter = existingGetters.contains(propertyName);
                            existSetter = existingSetters.contains(propertyName);
                            if (!existGetter && !existSetter) {
                                getPossibleGettersSetters().add(property);
                                getPossibleGetters().add(property);
                                getPossibleSetters().add(property);
                            } else if (!existGetter) {
                                getPossibleGetters().add(property);
                            } else if (!existSetter) {
                                getPossibleSetters().add(property);
                            }
                        }
                    }
                }
            });
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Find out class enclosing caret
     * @param info
     * @param offset caret offset
     * @return class declaration or null
     */
    private ClassDeclaration findEnclosingClass(ParserResult info, int offset) {
        List<ASTNode> nodes = NavUtils.underCaret(info, offset);
        int count = nodes.size();
        if (count > 2) {  // the cursor has to be in class block see issue #142417
            ASTNode declaration = nodes.get(count - 2);
            ASTNode block = nodes.get(count - 1);
            if (block instanceof Block &&  declaration instanceof ClassDeclaration) {
                return (ClassDeclaration) declaration;
            }
        }
        return null;
    }

    private class PropertiesVisitor extends DefaultVisitor {

        private final List<String> existingGetters;
        private final List<String> existingSetters;
        private final List<Property> properties;

        public PropertiesVisitor(List<Property> properties, List<String> existingGetters, List<String> existingSetters) {
            this.existingGetters = existingGetters;
            this.existingSetters = existingSetters;
            this.properties = properties;
        }

        @Override
        public void visit(FieldsDeclaration node) {
            List<SingleFieldDeclaration> fields = node.getFields();
            if (!BodyDeclaration.Modifier.isStatic(node.getModifier())) {
                for (SingleFieldDeclaration singleFieldDeclaration : fields) {
                    Variable variable = singleFieldDeclaration.getName();
                    if (variable != null && variable.getName() instanceof Identifier) {
                        String name = ((Identifier) variable.getName()).getName();
                        getProperties().add(new Property(name, node.getModifier()));
                    }
                }
            }
        }

        @Override
        public void visit(MethodDeclaration node) {
            String name = node.getFunction().getFunctionName().getName();
            String possibleProperty;
            if (name != null) {
                if (name.startsWith(CGSGenerator.START_OF_GETTER)) {
                    possibleProperty = name.substring(CGSGenerator.START_OF_GETTER.length());
                    existingGetters.addAll(getAllPossibleProperties(possibleProperty));
                } else if (name.startsWith(CGSGenerator.START_OF_SETTER)) {
                    possibleProperty = name.substring(CGSGenerator.START_OF_GETTER.length());
                    existingSetters.addAll(getAllPossibleProperties(possibleProperty));
                }
                else if (className!= null && (className.equals(name) || "__construct".equals(name))) { //NOI18N
                    hasConstructor = true;
                }
            }
        }

        /**
         * Returns all possible properties which are based on the passed property derived from method name.
         *
         * @param possibleProperty Name of the property which was derived from method name (setField() -> field).
         * @return field => (field, _field) OR _field => (_field, field)
         */
        private List<String> getAllPossibleProperties(String possibleProperty) {
            List<String> allPossibleProperties = new LinkedList<String>();
            possibleProperty = possibleProperty.toLowerCase();
            allPossibleProperties.add(possibleProperty);
            if (possibleProperty.startsWith("_")) { // NOI18N
                allPossibleProperties.add(possibleProperty.substring(1));
            } else {
                allPossibleProperties.add("_" + possibleProperty); // NOI18N
            }
            return allPossibleProperties;
        }
    }
}
