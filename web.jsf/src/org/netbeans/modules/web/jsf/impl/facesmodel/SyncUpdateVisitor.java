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

package org.netbeans.modules.web.jsf.impl.facesmodel;

import org.netbeans.modules.web.jsf.api.facesmodel.JSFConfigComponent;
import org.netbeans.modules.web.jsf.api.facesmodel.JSFConfigVisitor;
import org.netbeans.modules.xml.xam.ComponentUpdater;
import org.netbeans.modules.xml.xam.ComponentUpdater.Operation;

/**
 *
 * @author Petr Pisl
 */
public class SyncUpdateVisitor extends JSFConfigVisitor.Default implements ComponentUpdater<JSFConfigComponent>{
    
    private JSFConfigComponent target;
    private Operation operation;
    private int index;
    
    /** Creates a new instance of SyncUpdateVisitor */
    public SyncUpdateVisitor() {
    }
    

    public void update(JSFConfigComponent target, JSFConfigComponent child, Operation operation) {
        update(target, child, -1 , operation);
    }

    public void update(JSFConfigComponent target, JSFConfigComponent child, int index, Operation operation) {
        assert target != null;
        assert child != null;
        this.target = target;
        this.index = index;
        this.operation = operation;
        child.accept(this);
    }

}
