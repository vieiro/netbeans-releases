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

package org.netbeans.modules.xml.wsdl.model;

import org.netbeans.modules.xml.xam.Model;
import org.netbeans.modules.xml.xam.xdm.ComponentUpdater;

/**
 *
 * @author rico
 * @author Nam Nguyen
 * Interface for wsdl extensibility elements
 */
public interface ExtensibilityElement extends WSDLComponent {
    
    interface UpdaterProvider extends ExtensibilityElement {
        /**
         * @return component updater to be used in merge operations when source sync happens.
         */
        <T extends ExtensibilityElement> ComponentUpdater<T> getComponentUpdater();
    }
    
    interface Embedder extends ExtensibilityElement {
        Model getEmbeddedModel();
    }
}
