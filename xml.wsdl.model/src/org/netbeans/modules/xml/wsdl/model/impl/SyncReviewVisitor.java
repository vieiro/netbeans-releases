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

import org.netbeans.modules.xml.wsdl.model.NotificationOperation;
import org.netbeans.modules.xml.wsdl.model.OneWayOperation;
import org.netbeans.modules.xml.wsdl.model.Operation;
import org.netbeans.modules.xml.wsdl.model.PortType;
import org.netbeans.modules.xml.wsdl.model.RequestResponseOperation;
import org.netbeans.modules.xml.wsdl.model.SolicitResponseOperation;
import org.netbeans.modules.xml.wsdl.model.WSDLComponent;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.xml.wsdl.model.spi.ElementFactory;
import org.netbeans.modules.xml.wsdl.model.impl.ElementFactoryRegistry;
import org.netbeans.modules.xml.wsdl.model.visitor.DefaultVisitor;
import org.netbeans.modules.xml.xam.AbstractComponent;
import org.netbeans.modules.xml.xam.xdm.ChangedNodes;
import org.netbeans.modules.xml.xam.xdm.SyncUnit;
import org.w3c.dom.Element;

/**
 *
 * @author Nam Nguyen
 */
public class SyncReviewVisitor extends DefaultVisitor {
    
    private SyncUnit unit;
    
    /** Creates a new instance of SyncUnitReviewVisistor */
    public SyncReviewVisitor() {
    }
 
    SyncUnit review(SyncUnit toReview) {
        this.unit = toReview;
        if (unit.getTarget() instanceof WSDLComponent) {
            ((WSDLComponent)unit.getTarget()).accept(this);
        }
        return unit;
    }

    private void reviewOperation(Operation target) {
        if (unit.getToAddList().size() > 0 || unit.getToRemoveList().size() > 0) {
            SyncUnit reviewed = new SyncUnit(target.getParent());
            ChangedNodes change = unit.getLastChange();
            Element peer = change.getParent();
            change.markParentAsChanged();
            reviewed.addChange(change);
            reviewed.addToRemoveList(target);
            reviewed.addToAddList(createOperation(target.getParent(), peer));
            unit = reviewed;
        }
    }
    
    private Operation createOperation(WSDLComponent parent, Element e) {
        WSDLModel model = parent.getWSDLModel();
        ElementFactory factory = ElementFactoryRegistry.getDefault().get(WSDLQNames.OPERATION.getQName());
        WSDLComponent component = factory.create(parent, e);
        assert component != null;
        return (Operation) component;
    }
    
    public void visit(OneWayOperation target) {
        reviewOperation(target);
    }

    public void visit(SolicitResponseOperation target) {
        reviewOperation(target);
    }

    public void visit(RequestResponseOperation target) {
        reviewOperation(target);
    }

    public void visit(NotificationOperation target) {
        reviewOperation(target);
    }
}
