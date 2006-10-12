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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.websvc.core.jaxws.bindings.model.impl;

import org.netbeans.modules.websvc.core.jaxws.bindings.model.BindingsHandlerClass;
import org.w3c.dom.Element;

/**
 *
 * @author Roderico Cruz
 */
public class BindingsHandlerClassImpl extends BindingsComponentImpl implements BindingsHandlerClass{
    
    /**
     * Creates a new instance of BindingsHandlerClassImpl
     */
    public BindingsHandlerClassImpl(BindingsModelImpl model, Element element) {
        super(model, element);
    }
    
    public BindingsHandlerClassImpl(BindingsModelImpl model){
        this(model, createPrefixedElement(BindingsQName.HANDLER_CLASS.getQName(), model));
    }

    public void setClassName(String name) {
        setText(HANDLER_CLASS_NAME_PROPERTY, name);
    }


    public String getClassName() {
        return getText();
    }

    protected String getNamespaceURI() {
        return BindingsQName.JAVAEE_NS_URI;
    }
}
