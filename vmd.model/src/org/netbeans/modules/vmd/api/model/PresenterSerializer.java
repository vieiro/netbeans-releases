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
 *
 */

package org.netbeans.modules.vmd.api.model;

import org.w3c.dom.Node;
import org.w3c.dom.Document;

import java.util.List;

/**
 * This interface is used as an input parameter for <code>ComponentSerializationSupport.serialize</code> method
 * and allows to specify serialization of custom presenters.
 *
 * @author David Kaspar
 */
public interface PresenterSerializer {

    /**
     * Called to perform serialization of custom presenters.
     * The method should not modify the document, instead just return a list of newly created nodes.
     * @param document the xml document
     * @return the list of newly created nodes that contains serialized data of custom presenters.
     */
    List<Node> serialize (Document document);

}
