/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER. Copyright 1997-2007
 * Sun Microsystems, Inc. All rights reserved. The contents of this file are
 * subject to the terms of either the GNU General Public License Version 2 only
 * ("GPL") or the Common Development and Distribution License("CDDL")
 * (collectively, the "License"). You may not use this file except in compliance
 * with the License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP.
 * See the License for the specific language governing permissions and
 * limitations under the License. When distributing the software, include this
 * License Header Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this particular file as
 * subject to the "Classpath" exception as provided by Sun in the GPL Version 2
 * section of the License file that accompanied this code. If applicable, add
 * the following below the License Header, with the fields enclosed by brackets
 * [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]" Contributor(s): The
 * Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc.
 * All Rights Reserved. If you wish your version of this file to be governed by
 * only the CDDL or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution under the
 * [CDDL or GPL Version 2] license." If you do not indicate a single choice of
 * license, a recipient has the option to distribute your version of this file
 * under either the CDDL, the GPL Version 2 or to extend the choice of license
 * to its licensees as provided above. However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright holder.
 */
package org.netbeans.modules.web.beans.impl.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.netbeans.api.java.source.ClassIndex.SearchKind;
import org.netbeans.api.java.source.ClassIndex.SearchScope;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.modules.j2ee.metadata.model.api.support.annotation.AnnotationHandler;
import org.netbeans.modules.j2ee.metadata.model.api.support.annotation.AnnotationModelHelper;
import org.netbeans.modules.j2ee.metadata.model.api.support.annotation.PersistentObjectManager;
import org.netbeans.modules.j2ee.metadata.model.api.support.annotation.parser.AnnotationParser;
import org.netbeans.modules.j2ee.metadata.model.api.support.annotation.parser.ParseResult;
import org.netbeans.modules.web.beans.api.model.Result;
import org.netbeans.modules.web.beans.impl.model.results.DefinitionErrorResult;
import org.netbeans.modules.web.beans.impl.model.results.ResultImpl;
import org.netbeans.modules.web.beans.model.spi.WebBeansModelProvider;
import org.openide.util.NbBundle;

/**
 * @author ads
 */
abstract class FieldInjectionPointLogic {

    static final String PRODUCER_ANNOTATION = 
                    "javax.enterprise.inject.Produces";             // NOI18N

    static final String ANY_QUALIFIER_ANNOTATION = 
                     "javax.enterprise.inject.Any";                 // NOI18N

    static final String DEFAULT_QUALIFIER_ANNOTATION = 
                     "javax.enterprise.inject.Default";             // NOI18N

    static final String NEW_QUALIFIER_ANNOTATION = 
                      "javax.enterprise.inject.New";                // NOI18N
    
    static final String NAMED_QUALIFIER_ANNOTATION = 
                       "javax.inject.Named";                        // NOI18N

    static final String INJECT_ANNOTATION = 
                        "javax.inject.Inject";                      // NOI18N
    
    static final String INSTANCE_INTERFACE = 
                        "javax.enterprise.inject.Instance";         // NOI18N
    
    static final String TYPED_RESTRICTION = 
                        "javax.enterprise.inject.Typed";            // NOI18N

    static final Logger LOGGER = Logger.getLogger(WebBeansModelProvider.class
            .getName());
    
    
    FieldInjectionPointLogic( WebBeansModelImplementation model) {
        myModel = model;
    }
    
    public abstract TypeMirror resolveType( String fqn ) ;
    
    protected WebBeansModelImplementation getModel(){
        return myModel;
    }
    
    protected Result findVariableInjectable( VariableElement element, 
            DeclaredType parentType , ResultLookupStrategy strategy )
    {
        DeclaredType parent = parentType;
        try {
            parent = getParent(element, parentType);
        }
        catch ( DefinitionError e ){
            TypeElement type = e.getElement();
            return new DefinitionErrorResult(element,  parentType, 
                    NbBundle.getMessage(WebBeansModelProviderImpl.class, 
                            "ERR_BadParent", element.getSimpleName(),
                             type!= null? type.toString(): null));
        }
        
        TypeMirror elementType = strategy.getType(getModel(), parent , element );
        Result result  = doFindVariableInjectable(element, elementType, true);
        return strategy.getResult( getModel() , result );
    }
    
    protected DeclaredType getParent( Element element , DeclaredType parentType) 
        throws DefinitionError
    {
        DeclaredType parent = parentType;
        if ( parent == null ){
            TypeElement type = getModel().getHelper().getCompilationController().
                getElementUtilities().enclosingTypeElement(element);
            
            boolean isDeclaredType = ( type.asType() instanceof DeclaredType );
            if ( isDeclaredType ){
                parent = (DeclaredType)type.asType();
            }
            if ( !isDeclaredType) {
                throw new DefinitionError( type );
            }
        }
        return parent;
    }
    
    protected Result doFindVariableInjectable( VariableElement element,
            TypeMirror elementType, boolean injectRequired)
    {
        List<AnnotationMirror> quilifierAnnotations = new LinkedList<AnnotationMirror>();
        boolean anyQualifier = false;
        try {
            anyQualifier = hasAnyQualifier(element,injectRequired, false, 
                    quilifierAnnotations);
        }
        catch(InjectionPointDefinitionError e ){
            return new DefinitionErrorResult(element, elementType, e.getMessage());
        }
        
        /*
         * Single @Default annotation means increasing types that 
         * is eligible for injection. Each bean without any qualifiers
         * type has @Default qualifier by default. So it should
         * be also considered as injectable.  
         */
        boolean defaultQualifier = !anyQualifier && quilifierAnnotations.size() == 0;
        /*
         * The @New target is 
         * @Target(value={FIELD,PARAMETER})
         * and injectable couldn't have any other qualifiers.
         * So @New should be the only qualifier for injection point 
         * and it could be assigned by user to bean type.
         */
        boolean newQualifier = false; 
        String annotationName = null; 
        Set<TypeElement> types = new HashSet<TypeElement>();
        if ( quilifierAnnotations.size() == 1 ){
            AnnotationMirror annotationMirror = quilifierAnnotations.get( 0 );
            DeclaredType type = annotationMirror.getAnnotationType();
            TypeElement annotationElement = (TypeElement)type.asElement();
            annotationName = annotationElement.getQualifiedName().toString();
            defaultQualifier = annotationElement.getQualifiedName().contentEquals( 
                    DEFAULT_QUALIFIER_ANNOTATION);
            newQualifier = annotationElement.getQualifiedName().contentEquals( 
                    NEW_QUALIFIER_ANNOTATION );
        }
        if ( (quilifierAnnotations.size() == 0 && anyQualifier) ||
                defaultQualifier )
        {
            LOGGER.fine("Found built-in binding "+annotationName); // NOI18N
            Set<TypeElement> assignableTypes= getAssignableTypes( element , 
                    elementType );
            if ( defaultQualifier ){
                LOGGER.fine("@Default annotation requires test for implementors" +
                        " of varaible type");                      // NOI18N
                /*
                 *  Filter all appropriate types for presence qualifier.
                 *  It should be either absent at all or qualifiers 
                 *  should contain @Default.  
                 */
                filterBindingsByDefault( assignableTypes );
            }
            types.addAll( assignableTypes );
        }
        else if (newQualifier){
            return handleNewQualifier(element, elementType, quilifierAnnotations);
        }
        else {
            /*
             * This is list with types that have all required qualifiers. This
             * list will be used for further typesafe resolution.
             */
            Set<TypeElement> typesWithQualifiers = getBindingTypes(
                    quilifierAnnotations);
            
            filterBindingsByMembers(quilifierAnnotations, typesWithQualifiers, 
                    TypeElement.class );
            
            /*
             * Now <code>typesWithQualifiers</code> contains appropriate types
             * which has required qualifier with required parameters ( if any ).
             * Next step is filter types via typesafe resolution.
             */
            filterBindingsByType( element , elementType, typesWithQualifiers );
            types.addAll( typesWithQualifiers );
        }
        
        /*
         * This is list with production fields or methods ( they have @Produces annotation )
         * that  have all required bindings.
         * This list will be also used for further typesafe resolution. 
         */
        Set<Element> productionElements;
        if ( (quilifierAnnotations.size() == 0 && anyQualifier) || 
                defaultQualifier )
        {
            productionElements = getAllProductions( );
            if ( defaultQualifier ){
                filterDefaultProductions( productionElements );
            }
        }
        else {
            productionElements = getProductions( quilifierAnnotations); 
            filterBindingsByMembers( quilifierAnnotations , productionElements , 
                     Element.class );
        }
        /*
         * Elements which are keys in the following map are production fields
         * or production methods. But ONLY element instances could be not sufficient.
         * If method is not parameterized ( as member of some generic class )
         * then one don't need values in this hash. But method could be a
         * member generic class which is extended by other class with real type
         * as parameter. In this case method is inherited by child class
         * and its type mirror should be taken into account. Accessing to 
         * TypeMirror of element ( which is key ) should be done via Types.asMemberOf()   
         */
        Map<Element, List<DeclaredType>> productions = filterProductionByType( 
                element, elementType, productionElements );
        
        return createResult( element, elementType, types , productions );
    }

    protected boolean isQualifier( TypeElement element, 
            AnnotationModelHelper helper, boolean event )
    {
        QualifierChecker checker = QualifierChecker.get( event );
        checker.init( element , helper );
        return checker.check();
    }
    
    protected Set<Element> getChildSpecializes( Element productionElement,
            WebBeansModelImplementation model )
    {
        TypeElement typeElement = model.getHelper().getCompilationController()
                .getElementUtilities().enclosingTypeElement(productionElement);
        Set<TypeElement> implementors = getImplementors(model, typeElement);
        implementors.remove( productionElement.getEnclosingElement());
        Set<Element> specializeElements = new HashSet<Element>();
        specializeElements.add(productionElement);
        for (TypeElement implementor : implementors) {
            inspectHierarchy(productionElement, implementor,
                    specializeElements, model);
        }
        specializeElements.remove(productionElement);
        return specializeElements;
    }
    
    protected boolean hasAnyQualifier( VariableElement element,boolean injectRequired,
            boolean eventQualifiers, List<AnnotationMirror> quilifierAnnotations ) 
            throws InjectionPointDefinitionError
    {
        List<? extends AnnotationMirror> annotations = 
            getModel().getHelper().getCompilationController().getElements().
            getAllAnnotationMirrors(element);
        boolean isProducer = false;
        
        /* Single @Any annotation means skip searching in qualifiers .
         * One need to check any bean that has required type .
         * @Any qualifier type along with other qualifiers 
         * equivalent to the same list of qualifiers without @Any.
         */
        boolean anyQualifier = false;
        
        boolean hasInject = false;
        
        for (AnnotationMirror annotationMirror : annotations) {
            DeclaredType type = annotationMirror.getAnnotationType();
            TypeElement annotationElement = (TypeElement)type.asElement();
            if ( ANY_QUALIFIER_ANNOTATION.equals( 
                    annotationElement.getQualifiedName().toString()))
            {
                anyQualifier = true;
            }
            else if ( isQualifier( annotationElement , getModel().getHelper(),
                    eventQualifiers) )
            {
                quilifierAnnotations.add( annotationMirror );
            }
            if ( PRODUCER_ANNOTATION.contentEquals( 
                    annotationElement.getQualifiedName()))
            {
                isProducer = true;
            }
            else if ( INJECT_ANNOTATION.contentEquals( 
                    annotationElement.getQualifiedName()))
            {
                hasInject = true;
            }
            /* TODO : one needs somehow to check absence of initialization
             * for field... 
             */
        }
        if ( isProducer ){
            throw new InjectionPointDefinitionError(
                    NbBundle.getMessage( WebBeansModelProviderImpl.class, 
                            "ERR_ProducerInjectPoint" , element.getSimpleName() ));
        }
        
        if ( injectRequired && !hasInject ){
            throw new InjectionPointDefinitionError(
                    NbBundle.getMessage( WebBeansModelProviderImpl.class, 
                            "ERR_NoInjectPoint" , element.getSimpleName() ));
        }
        return anyQualifier;
    }
    
    protected <T extends Element> void filterBindingsByMembers(
            List<AnnotationMirror> bindingAnnotations,
            Set<T> elementsWithBindings,  Class<T> clazz)
    {
        MemberBindingFilter<T> filter = MemberBindingFilter.get( clazz );
        filter.init( bindingAnnotations, getModel() );
        filter.filter( elementsWithBindings );
    }
    
    static Set<TypeElement> getImplementors( WebBeansModelImplementation modelImpl,
            Element typeElement )
    {
        if (! (typeElement instanceof TypeElement )){
            return Collections.emptySet();
        }
        Set<TypeElement> result = new HashSet<TypeElement>();
        result.add( (TypeElement) typeElement );
        
        Set<TypeElement> toProcess = new HashSet<TypeElement>();
        toProcess.add((TypeElement) typeElement );
        while ( toProcess.size() >0 ){
            TypeElement element = toProcess.iterator().next();
            toProcess.remove( element );
            Set<TypeElement> set = doGetImplementors(modelImpl, element );
            if ( set.size() == 0 ){
                continue;
            }
            result.addAll( set );
            for (TypeElement impl : set) {
                toProcess.add(impl);
            }
        }
        return result;
    }
    
    private Result createResult( VariableElement element, TypeMirror elementType, 
            Set<TypeElement> types,Map<Element, List<DeclaredType>> productions )
    {
        return new ResultImpl(element, elementType, types, productions, 
                getModel().getHelper() );
    }
    
    private Result handleNewQualifier( VariableElement element,
            TypeMirror elementType,List<AnnotationMirror> quilifierAnnotations)
    {
        AnnotationMirror annotationMirror = quilifierAnnotations.get( 0 );
        AnnotationParser parser = AnnotationParser.create( getModel().getHelper());
        parser.expectClass( "value", null);                         // NOI18N 
        ParseResult parseResult = parser.parse(annotationMirror);
        String clazz = parseResult.get( "value" , String.class );   // NOI18N
        
        TypeMirror typeMirror;
        if ( clazz == null ){
            typeMirror = elementType;
        }
        else {
            typeMirror = resolveType( clazz );
        }
        Element typeElement = null;
        if ( typeMirror != null ) {
            typeElement = getModel().getHelper().getCompilationController().
                getTypes().asElement(typeMirror);
        }
        if ( typeElement!= null ){
            /*
             *  No need to look at implementors .
             *  Because they have qualifier @New(X.class) where X their class.
             *  X is binding parameter which should equals to binding 
             *  parameter of @New qualifier for injection point. This
             *  parameter is <code>typeMirror</code> class . So X should
             *  be ONLY typeMirror class which is typeElement.  
             *  types.addAll(getImplementors(modelImpl, typeElement ));
             */
            if( getModel().getHelper().getCompilationController().getTypes().
                    isAssignable(typeMirror, elementType))
            {
                return new ResultImpl(element, elementType , (TypeElement)typeElement , 
                        getModel().getHelper());
            }
        }
        return new ResultImpl(element, elementType, getModel().getHelper());
    }

    private void inspectHierarchy( Element productionElement,
            TypeElement implementor, Set<Element> specializeElements ,
            WebBeansModelImplementation model )
    {
        List<? extends Element> enclosedElements = implementor.getEnclosedElements();
        for (Element enclosedElement : enclosedElements) {
            if ( enclosedElement.getKind() != ElementKind.METHOD) {
                continue;
            }
            if ( !productionElement.getSimpleName().contentEquals(
                    enclosedElement.getSimpleName()))
            {
                continue;
            }
            Set<Element> probableSpecializes = new HashSet<Element>();
            if ( collectSpecializes( productionElement ,
                    (ExecutableElement)enclosedElement , model ,
                    probableSpecializes , specializeElements))
            {
                // for one method there could be just one override method in considered class
                specializeElements.addAll( probableSpecializes );
                return;
            }
        }
    }
    
    private boolean collectSpecializes( Element productionElement,
            ExecutableElement element, WebBeansModelImplementation model,
            Set<Element> probableSpecializes, Set<Element> specializeElements )
    {
        ElementUtilities elementUtilities =
            model.getHelper().getCompilationController().getElementUtilities();
        if ( !elementUtilities.overridesMethod(element)){
            return false;
        }
        ExecutableElement overriddenMethod = elementUtilities.
            getOverriddenMethod( element);
        if ( overriddenMethod == null ){
            return false;
        }
        if (!AnnotationObjectProvider.hasSpecializes(element,  model.getHelper())){
            return false;
        }
        probableSpecializes.add( element);
        if( overriddenMethod.equals( productionElement ) ||
                specializeElements.contains( productionElement))
        {
            return true;
        }
        else {
            return collectSpecializes(productionElement, overriddenMethod, model,
                    probableSpecializes, specializeElements);
        }
    }

    private static Set<TypeElement> doGetImplementors( 
            WebBeansModelImplementation modelImpl, TypeElement typeElement )
    {
        Set<TypeElement> result = new HashSet<TypeElement>();
        ElementHandle<TypeElement> handle = ElementHandle
                .create((TypeElement) typeElement);
        final Set<ElementHandle<TypeElement>> handles = modelImpl
                .getHelper().getClasspathInfo().getClassIndex()
                .getElements(
                        handle,
                        EnumSet.of(SearchKind.IMPLEMENTORS),
                        EnumSet.of(SearchScope.SOURCE,
                                SearchScope.DEPENDENCIES));
        if (handles == null) {
            LOGGER.log(Level.WARNING,
                    "ClassIndex.getElements() was interrupted"); // NOI18N
            return Collections.emptySet();
        }
        for (ElementHandle<TypeElement> elementHandle : handles) {
            LOGGER.log(Level.FINE, "found derived element {0}",
                    elementHandle.getQualifiedName()); // NOI18N
            TypeElement derivedElement = elementHandle.resolve(modelImpl
                    .getHelper().getCompilationController());
            if (derivedElement == null) {
                continue;
            }
            result.add(derivedElement);
        }
        return result;
    }
    
    private void filterDefaultProductions( Set<Element> productionElements ) 
    {
        DefaultBindingTypeFilter<Element> filter = DefaultBindingTypeFilter.get( 
                Element.class);
        filter.init( getModel() );
        filter.filter( productionElements );
    }

    private Set<Element> getAllProductions( ){
        final Set<Element> result = new HashSet<Element>();
        try {
            getModel().getHelper().getAnnotationScanner().findAnnotations( 
                    PRODUCER_ANNOTATION, 
                    EnumSet.of( ElementKind.FIELD, ElementKind.METHOD), 
                    new AnnotationHandler() {
                        @Override
                        public void handleAnnotation( TypeElement type, 
                                Element element,AnnotationMirror annotation )
                        {
                                result.add( element );
                        }
                    });
        }
        catch (InterruptedException e) {
            LOGGER.warning("Finding annotation "+PRODUCER_ANNOTATION+
                    " was interrupted"); // NOI18N
        }
        return result;
    }

    private Map<Element, List<DeclaredType>> filterProductionByType( 
            VariableElement element, TypeMirror elementType, 
            Set<Element> productionElements )
    {
        TypeProductionFilter filter = TypeProductionFilter.get( );
        filter.init( elementType, element.getSimpleName().toString(), getModel());
        filter.filter( productionElements );
        return filter.getResult();
    }
    
    private void filterBindingsByDefault( Set<TypeElement> assignableTypes ){
        DefaultBindingTypeFilter<TypeElement> filter = DefaultBindingTypeFilter.get( 
                TypeElement.class);
        filter.init( getModel() );
        filter.filter( assignableTypes );
    }

    private Set<TypeElement> getAssignableTypes( VariableElement element,
            TypeMirror elementType )
    {
        if (elementType.getKind() != TypeKind.DECLARED) {
            return Collections.emptySet();
        }
        Element typeElement = ((DeclaredType) elementType).asElement();
        if (!(typeElement instanceof TypeElement)) {
            return Collections.emptySet();
        }
        if (((TypeElement) typeElement).getTypeParameters().size() != 0) {
            return getAssignables(  elementType, (TypeElement)typeElement, 
                    element );
        }
        else {
            Set<TypeElement> implementors = getImplementors(getModel(), typeElement);
            restrictedTypeFilter( implementors , (TypeElement)typeElement );
            return implementors;
        }
    }
    
    private void restrictedTypeFilter( Set<TypeElement> allImplementors , 
            TypeElement originalElement  ) {
        RestrictedTypedFilter filter = new RestrictedTypedFilter();
        filter.init( originalElement , getModel());
        filter.filter( allImplementors );
    }

    private Set<TypeElement> getAssignables(  TypeMirror elementType, 
            TypeElement typeElement  , VariableElement element) 
    {
        Set<TypeElement> result = getImplementors(getModel(), typeElement);
        
        // Now filter all found child classes according to real element type ( type mirror )  
        TypeBindingFilter filter = TypeBindingFilter.get();
        filter.init( elementType, element.getSimpleName().toString(), getModel() );
        filter.filter( result );
        return result;
    }

    private void filterBindingsByType( VariableElement element, 
            TypeMirror elementType,Set<TypeElement> typesWithBindings)
    {
        TypeBindingFilter filter = TypeBindingFilter.get();
        filter.init( elementType, element.getSimpleName().toString(), getModel() );
        filter.filter( typesWithBindings );
    }
    /*
     * Method finds production elements which have appropriate binding types.
     */
    private Set<Element> getProductions( 
            List<AnnotationMirror> qualifierAnnotations ) 
    {
        List<Set<Element>> bindingCollections = 
            new ArrayList<Set<Element>>( qualifierAnnotations.size());
        /*
         * One need to handle special case with @Default annotation 
         * in case of specialization. There can be a case 
         * when production method doesn't explicitly declare @Default but 
         * specialize other method with several appropriate qualifiers.
         * In this case original method will have @Default along with 
         * qualifiers "inherited" from specialized methods.  
         */
        boolean hasDefault = getModel().getHelper().getAnnotationsByType( 
                qualifierAnnotations ).get(DEFAULT_QUALIFIER_ANNOTATION) != null ;
        Set<Element> currentBindings = new HashSet<Element>();
        for (AnnotationMirror annotationMirror : qualifierAnnotations) {
            DeclaredType type = annotationMirror.getAnnotationType();
            TypeElement annotationElement = (TypeElement)type.asElement();
            String annotationFQN = annotationElement.getQualifiedName().toString();
            findAnnotation( bindingCollections, annotationFQN , hasDefault,
                    currentBindings );
        }

        if ( hasDefault ){
            bindingCollections.add( currentBindings );
        }
        
        Set<Element> result= null;
        for ( int i=0; i<bindingCollections.size() ; i++ ){
            Set<Element> list = bindingCollections.get(i);
            if ( i==0 ){
                result = list;
            }
            else {
                result.retainAll( list );
            }
        }
        if ( result == null ){
            return Collections.emptySet();
        }
        return result;
    }

    private void findAnnotation( final List<Set<Element>> bindingCollections, 
            final String annotationFQN ,final boolean hasCurrent , 
            final Set<Element> currentBindings )
    {
        try {
            final Set<Element> bindings = new HashSet<Element>();
            getModel().getHelper().getAnnotationScanner().findAnnotations( 
                    annotationFQN, 
                    EnumSet.of( ElementKind.FIELD, ElementKind.METHOD), 
                    new AnnotationHandler() {
                        @Override
                        public void handleAnnotation( TypeElement type, 
                                Element element,AnnotationMirror annotation )
                                {
                                    if (AnnotationObjectProvider.hasAnnotation(
                                            element, PRODUCER_ANNOTATION,
                                            getModel().getHelper()))
                                    {
                                        bindings.add(element);
                                        bindings.addAll(getChildSpecializes(
                                                element, getModel()));
                                        if (annotationFQN
                                                .contentEquals(DEFAULT_QUALIFIER_ANNOTATION))
                                        {
                                            currentBindings.addAll(bindings);
                                        }
                                        else {
                                            bindingCollections.add(bindings);
                                        }
                                    }
                                }
                    });
            if ( hasCurrent ){
                for (Element element : bindings) {
                    if ( AnnotationObjectProvider.checkDefault(
                            element, getModel().getHelper()))
                    {
                        currentBindings.add( element );
                    }
                }
            }
        }
        catch (InterruptedException e) {
            LOGGER.warning("Finding annotation "+annotationFQN+
                    " was interrupted"); // NOI18N
        }
    }

    /*
     * Method finds type elements which have appropriate binding types.
     */
    private Set<TypeElement> getBindingTypes( List<AnnotationMirror> qualifierAnnotations ){
        List<Set<BindingQualifier>> bindingCollections = 
            new ArrayList<Set<BindingQualifier>>( qualifierAnnotations.size());

        /*
         * One need to handle special case with @Default annotation 
         * in case of specialization. There can be a case 
         * when bean doesn't explicitly declare @Default but 
         * specializes other beans with several appropriate qualifiers.
         * In this case original bean will have @Default along with 
         * qualifiers "inherited" from specialized beans.  
         */
        boolean hasDefault = getModel().getHelper().getAnnotationsByType( 
                qualifierAnnotations ).get(DEFAULT_QUALIFIER_ANNOTATION) != null ;
        Set<BindingQualifier> defaultQualifiers = new HashSet<BindingQualifier>();
        for (AnnotationMirror annotationMirror : qualifierAnnotations) {
            DeclaredType type = annotationMirror.getAnnotationType();
            TypeElement annotationElement = (TypeElement) type.asElement();
            String annotationFQN = annotationElement.getQualifiedName()
                    .toString();
            PersistentObjectManager<BindingQualifier> manager = getModel()
                    .getManager(annotationFQN);
            Collection<BindingQualifier> bindings = manager.getObjects();
            if (annotationFQN.contentEquals(DEFAULT_QUALIFIER_ANNOTATION)) {
                defaultQualifiers.addAll(bindings);
            }
            else {
                bindingCollections.add(new HashSet<BindingQualifier>(bindings));
                if (hasDefault) {
                    for (BindingQualifier binding : bindings) {
                        if (AnnotationObjectProvider
                                .checkDefault(binding.getTypeElement(),
                                        getModel().getHelper()))
                        {
                            defaultQualifiers.add(new BindingQualifier(
                                    getModel().getHelper(), binding
                                            .getTypeElement(),
                                    DEFAULT_QUALIFIER_ANNOTATION));
                        }
                    }
                }
            }
        }
        
        if ( hasDefault ){
            bindingCollections.add( defaultQualifiers );
        }
        
        Set<BindingQualifier> result= null;
        for ( int i=0; i<bindingCollections.size() ; i++ ){
            Set<BindingQualifier> set = bindingCollections.get(i);
            if ( i==0 ){
                result = set;
            }
            else {
                result.retainAll( set );
            }
        }
        if ( result == null ){
            return Collections.emptySet();
        }
        else {
            Set<TypeElement> set = new HashSet<TypeElement>();
            for (BindingQualifier binding : result) {
                set.add( binding.getTypeElement() );
            }
            return set;
        }
    }

    protected static class InjectionPointDefinitionError extends Exception{
        private static final long serialVersionUID = -1568276063434281036L;

        private InjectionPointDefinitionError(String msg){
            super( msg );
        }
    }
    
    protected static class DefinitionError extends Exception {
        
        private static final long serialVersionUID = 8538541504206293629L;

        protected DefinitionError( TypeElement element ){
            myElement = element;
        }
        
        public TypeElement getElement(){
            return myElement;
        }
        
        private TypeElement myElement; 
    }
    private WebBeansModelImplementation myModel;
}
