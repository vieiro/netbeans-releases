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
package org.netbeans.modules.wsdlextensions.jms;

/**
 * JMSUnNamedPart
 */
public interface JMSUnNamedPart extends JMSComponent {

    public static final String ATTR_TYPE = "type";
    public static final String ATTR_PART = "part";
    
    /**
     * Get value of type attribute
     * @return The String value of name attribute
     */
    public String getType();

    /**
     * Sets value of type attribute
     * @param val The String value of type attribute
     */
    public void setType(String val);

    /**
     * Get value of part attribute
     * @return The String value of part attribute
     */
    public String getPart();

    /**
     * Sets value of part attribute
     * @param val The String value of part attribute
     */
    public void setPart(String val);
}
