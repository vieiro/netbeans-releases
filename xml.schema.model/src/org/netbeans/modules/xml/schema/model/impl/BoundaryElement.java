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

/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.xml.schema.model.impl;
import org.netbeans.modules.xml.schema.model.BoundaryFacet;
import org.w3c.dom.Element;

/**
 * Common class for element class representing bounding value.
 *
 * @author nn136682
 */
public abstract class BoundaryElement extends SchemaComponentImpl implements BoundaryFacet {
    
    /** Creates a new instance of BoundaryElement */
    public BoundaryElement(SchemaModelImpl model, Element e) {
        super(model, e);
    }
    
    public abstract String getComponentName(); 
    
    public void setValue(String v) {
        setAttribute(VALUE_PROPERTY, SchemaAttributes.VALUE, v);
    }
    
    public String getValue() {
        String v = super.getAttribute(SchemaAttributes.VALUE);
        if (v == null) {
            throw new IllegalArgumentException("Element '" + getComponentName() + "' got null value.");
        }
        return v;
    }
    
    public Boolean isFixed() {
        String s = getAttribute(SchemaAttributes.FIXED);
        return s == null ? null : Boolean.valueOf(s);
    }
    
    public void setFixed(Boolean isFixed) {
        setAttribute(FIXED_PROPERTY, SchemaAttributes.FIXED, isFixed);
    }

    public boolean getFixedDefault() {
        return false;
    }
	
    public boolean getFixedEffective() {
        Boolean v = isFixed();
        return v == null ? getFixedDefault() : v;
    }
}
    