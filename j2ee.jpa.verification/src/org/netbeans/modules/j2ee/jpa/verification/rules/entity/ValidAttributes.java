/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.j2ee.jpa.verification.rules.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.lang.model.element.TypeElement;
import org.netbeans.modules.j2ee.jpa.model.AttributeWrapper;
import org.netbeans.modules.j2ee.jpa.model.ModelUtils;
import org.netbeans.modules.j2ee.jpa.verification.JPAClassRule;
import org.netbeans.modules.j2ee.jpa.verification.JPAEntityAttributeCheck;
import org.netbeans.modules.j2ee.jpa.verification.JPAProblemContext;
import org.netbeans.modules.j2ee.jpa.verification.common.ProblemContext;
import org.netbeans.modules.j2ee.jpa.verification.rules.attribute.ValidColumnName;
import org.netbeans.modules.j2ee.jpa.verification.rules.attribute.ValidModifiers;
import org.netbeans.modules.j2ee.persistence.api.metadata.orm.Basic;
import org.netbeans.modules.j2ee.persistence.api.metadata.orm.Entity;
import org.netbeans.modules.j2ee.persistence.api.metadata.orm.Id;
import org.netbeans.spi.editor.hints.ErrorDescription;

/**
 * Perform checks on the attributes
 *
 * @author Tomasz.Slota@Sun.COM
 */
public class ValidAttributes extends JPAClassRule {
    private static JPAEntityAttributeCheck[] attribChecks = new JPAEntityAttributeCheck[]{
        new ValidColumnName(),
        new ValidModifiers()
    };
    
    public ValidAttributes() {
        setClassContraints(Arrays.asList(ClassConstraints.ENTITY,
                ClassConstraints.EMBEDDABLE,
                ClassConstraints.MAPPED_SUPERCLASS));
    }
    
    @Override public ErrorDescription[] apply(TypeElement subject, ProblemContext ctx){
        List<ErrorDescription> problemsFound = new ArrayList<ErrorDescription>();
        List <AttributeWrapper> attrs = new ArrayList<AttributeWrapper>();
        Basic basicFields[] = null;
        Id idFields[] = null;
        
        if (ctx.getModelElement() instanceof Entity){
            Entity entity = (Entity)ctx.getModelElement();
            basicFields = entity.getAttributes().getBasic();
            
            idFields = entity.getAttributes().getId();
        }
        
        //TODO: handle MappedSuperClass etc.
        
        if (basicFields != null){
            for (Basic basic: basicFields){
                attrs.add(new AttributeWrapper(basic));
            }
        }
        
        if (idFields != null){
            for (Id id: idFields){
                attrs.add(new AttributeWrapper(id));
            }
        }
        
        JPAProblemContext jpaCtx = (JPAProblemContext)ctx;
        
        for (AttributeWrapper attr : attrs){
            ModelUtils.resolveJavaElementFromModel(jpaCtx, attr);
            
            for (JPAEntityAttributeCheck check : attribChecks){
                ErrorDescription[] attrProblems = check.check(jpaCtx, attr);
                
                if (attrProblems != null){
                    for (ErrorDescription err : attrProblems){
                        problemsFound.add(err);
                    }
                }
            }
        }
        
        return problemsFound.toArray(new ErrorDescription[problemsFound.size()]);
    }
}
