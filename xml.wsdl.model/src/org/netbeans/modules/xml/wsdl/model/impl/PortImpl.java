/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.xml.wsdl.model.impl;

import org.netbeans.modules.xml.wsdl.model.Binding;
import org.netbeans.modules.xml.wsdl.model.Port;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.xml.xam.GlobalReference;
import org.w3c.dom.Element;

/**
 *
 * @author Nam Nguyen
 */
public class PortImpl extends NamedImpl implements Port {
    
    /** Creates a new instance of PortImpl */
    public PortImpl(WSDLModel model, Element e) {
        super(model, e);
    }
    
    public PortImpl(WSDLModel model){
        this(model, createNewElement(WSDLQNames.PORT.getQName(), model));
    }

    public void accept(org.netbeans.modules.xml.wsdl.model.visitor.WSDLVisitor visitor) {
        visitor.visit(this);
    }

    public void setBinding(GlobalReference<Binding> binding) {
        setGlobalReference(BINDING_PROPERTY, WSDLAttribute.BINDING, binding);
    }

    public GlobalReference<Binding> getBinding() {
        return resolveGlobalReference(Binding.class, WSDLAttribute.BINDING);
    }
}
