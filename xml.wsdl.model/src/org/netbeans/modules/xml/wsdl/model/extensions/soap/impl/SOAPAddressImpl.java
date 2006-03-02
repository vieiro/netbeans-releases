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

package org.netbeans.modules.xml.wsdl.model.extensions.soap.impl;

import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.xml.wsdl.model.extensions.soap.SOAPAddress;
import org.netbeans.modules.xml.wsdl.model.extensions.soap.SOAPQName;
import org.netbeans.modules.xml.wsdl.model.impl.WSDLAttribute;
import org.netbeans.modules.xml.wsdl.model.visitor.WSDLVisitor;
import org.w3c.dom.Element;

/**
 *
 * @author Nam Nguyen
 */
public class SOAPAddressImpl extends SOAPComponentImpl implements SOAPAddress {
    
    /** Creates a new instance of SOAPAddressImpl */
    public SOAPAddressImpl(WSDLModel model, Element e) {
        super(model, e);
    }
    
    public SOAPAddressImpl(WSDLModel model){
        this(model, createPrefixedElement(SOAPQName.ADDRESS.getQName(), model));
    }
    
    public void accept(WSDLVisitor visitor) {
        visitor.visit(this);
    }

    public void setLocation(String locationURI) {
        setAttribute(LOCATION_PROPERTY, WSDLAttribute.LOCATION, locationURI);
    }

    public String getLocation() {
        return getAttribute(WSDLAttribute.LOCATION);
    }
}
