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

package org.netbeans.modules.vmd.properties;

import java.lang.ref.WeakReference;
import org.netbeans.modules.vmd.properties.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.WeakHashMap;
import org.netbeans.modules.vmd.api.io.ActiveViewSupport;
import org.netbeans.modules.vmd.api.io.DataEditorView;
import org.netbeans.modules.vmd.api.io.IOUtils;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.common.ActiveDocumentSupport;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.InstanceContent;

/**
 * @author Karol Harezlak
 */
final class PropertiesNodesManager implements ActiveViewSupport.Listener{
    
    private static PropertiesNodesManager INSTANCE;
    private WeakHashMap<DataEditorView, InstanceContent> icMap;
    private WeakHashMap<InstanceContent, Collection<AbstractNode>> nodesMap;
    private WeakReference<InstanceContent> currentIC;
    
    static PropertiesNodesManager getDefault() {
        synchronized (PropertiesNodesManager.class) {
            if (INSTANCE == null) {
                INSTANCE = new PropertiesNodesManager();
                ActiveViewSupport.getDefault().addActiveViewListener(INSTANCE);
            }
            return INSTANCE;
        }
    }
    
    private PropertiesNodesManager() {
        icMap = new WeakHashMap<DataEditorView, InstanceContent>();
        nodesMap = new WeakHashMap<InstanceContent, Collection<AbstractNode>>();
    }
    
    void add(DataEditorView view, InstanceContent content) {
        assert(view != null);
        assert(content != null);
        icMap.put(view, content);
    }
    
    AbstractNode[] getActiveNodes() {
        if (currentIC == null)
            return null;
        changeLookup(ActiveViewSupport.getDefault().getActiveView());
        Collection<AbstractNode> nodes = nodesMap.get(currentIC.get());
        return nodes.toArray(new AbstractNode[nodes.size()]);
    }
    
    void changeLookup(DataEditorView view) {
        assert(view != null);
        InstanceContent ic = icMap.get(view);
        if(ic == null)
            return;
        currentIC = new WeakReference<InstanceContent>(ic);
        Collection<AbstractNode> nodesToRemove = nodesMap.get(ic);
        if (nodesToRemove != null) {
            for (Node node : nodesToRemove) {
                ic.remove(node);
            }
            nodesToRemove.clear();
        } else {
            nodesToRemove = new HashSet<AbstractNode>();
            nodesMap.put(ic, nodesToRemove);
        }
        Collection<DesignComponent> components = ActiveDocumentSupport.getDefault().getActiveComponents();
        Node dataNode = view.getContext().getDataObject().getNodeDelegate();
        if (components != null && components.size() > 0) {
            for(DesignComponent c : components) {
                PropertiesNode node = new PropertiesNode(c, dataNode.getLookup());
                ic.add(node);
                nodesToRemove.add(node);
            }
        } else {
            AbstractNode genericNode = new AbstractNode(Children.LEAF, dataNode.getLookup());
            ic.add(genericNode);
            nodesToRemove.add(genericNode);
        }  
    }
    
    public void activeViewChanged(DataEditorView deactivatedView, final DataEditorView activatedView) {
        if (activatedView != null && activatedView.getKind() == DataEditorView.Kind.MODEL) {
            IOUtils.runInAWTNoBlocking(new Runnable() {
                public void run() {
                    changeLookup(activatedView);
                }
            });
        }
    }
}

