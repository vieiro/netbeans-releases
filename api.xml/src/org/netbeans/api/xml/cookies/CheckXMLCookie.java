/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.xml.cookies;

import org.openide.nodes.Node;

/**
 * Cookie used for XML entity syntax checking. It must not have any UI.
 *
 * @author  Petr Kuzel
 * @deprecated XML tools API candidate
 */
public interface CheckXMLCookie extends Node.Cookie {
    
    /**
     * Check XML entity for syntax wellformedness.
     * @param report optional listener (<code>null</code> allowed)
     *               giving judgement details
     * @return true if syntax check passes
     */
    boolean checkXML(ProcessorListener l);
    
}
