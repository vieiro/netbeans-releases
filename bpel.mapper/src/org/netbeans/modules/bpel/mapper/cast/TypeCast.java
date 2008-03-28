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

package org.netbeans.modules.bpel.mapper.cast;

import org.netbeans.modules.bpel.mapper.predicates.editor.PathConverter;
import org.netbeans.modules.bpel.mapper.tree.spi.RestartableIterator;
import org.netbeans.modules.bpel.model.api.BpelEntity;
import org.netbeans.modules.bpel.model.api.events.VetoException;
import org.netbeans.modules.bpel.model.api.references.SchemaReference;
import org.netbeans.modules.bpel.model.api.support.BpelXPathModelFactory;
import org.netbeans.modules.bpel.model.ext.editor.api.Cast;
import org.netbeans.modules.xml.schema.model.GlobalType;
import org.netbeans.modules.xml.xpath.ext.XPathException;
import org.netbeans.modules.xml.xpath.ext.XPathExpression;
import org.netbeans.modules.xml.xpath.ext.XPathModel;
import org.netbeans.modules.xml.xpath.ext.XPathSchemaContext;
import org.netbeans.modules.xml.xpath.ext.XPathSchemaContextHolder;
import org.netbeans.modules.xml.xpath.ext.spi.XPathCast;
import org.openide.ErrorManager;

/**
 * The Type Cast objects based on the XPath expression.
 * It is usualy used when the mapper initiated from the sources. 
 * 
 * @author nk160297
 */
public class TypeCast extends AbstractTypeCast {

    private XPathExpression mXPathExpression;
    
    public static XPathExpression getExpression(Cast cast) {
        String pathText = cast.getPath();
        XPathModel xPathModel = BpelXPathModelFactory.create(cast);
        XPathExpression xPathExpr = null;
        try {
            xPathExpr = xPathModel.parseExpression(pathText);
        } catch (XPathException ex) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, 
                    "Unresolved XPath: " + pathText); //NOI18N
        }
        return xPathExpr;
    }
    
    public static TypeCast convert(Cast cast) {
        GlobalType castTo = null;
        //
        XPathExpression xPathExpr = getExpression(cast);
        //
        SchemaReference<GlobalType> gTypeRef = cast.getType();
        if (gTypeRef == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, 
                    "Cast To has to be specified");
        } else {
            castTo = gTypeRef.get();
            if (castTo == null) {
                ErrorManager.getDefault().log(ErrorManager.WARNING, 
                        "Unresolved global type: " + gTypeRef.getQName());
            }
        }
        //
        return new TypeCast(xPathExpr, castTo);
    }
    
    public TypeCast(XPathCast xPathCast) {
        this(xPathCast.getPath(), xPathCast.getCastTo());
    }
    
    public TypeCast(XPathExpression path, GlobalType castTo) {
        super(castTo);
        assert path != null;
        assert path instanceof XPathSchemaContextHolder;
        mXPathExpression = path;
    }
    
    public XPathSchemaContext getSchemaContext() {
        return ((XPathSchemaContextHolder)mXPathExpression).getSchemaContext();
    }

    public XPathExpression getXPathExpression() {
        return mXPathExpression;
    }
    
    @Override
    public boolean populateCast(Cast target, 
            BpelEntity destination, boolean inLeftMapperTree) {
        String pathText = mXPathExpression.getExpressionString();
        try {
            target.setPath(pathText);
        } catch (VetoException ex) {
            ErrorManager.getDefault().notify(ex);
            return false;
        }
        //
        return super.populateCast(target, destination, inLeftMapperTree);
    }
    
} 