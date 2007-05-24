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
import java.util.ArrayList;
import org.netbeans.modules.vmd.api.model.DesignDocument;
import org.netbeans.modules.vmd.properties.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import org.netbeans.modules.vmd.api.io.DataEditorView;
import org.netbeans.modules.vmd.api.io.DataObjectContext;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.common.ActiveDocumentSupport;
import org.netbeans.modules.vmd.api.properties.DesignPropertyDescriptor;
import org.netbeans.modules.vmd.api.properties.DesignPropertyEditor;
import org.netbeans.modules.vmd.api.properties.GroupPropertyEditor;
import org.netbeans.modules.vmd.api.properties.PropertiesPresenter;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.WeakSet;
import org.openide.util.lookup.InstanceContent;

/**
 * @author Karol Harezlak
 */
public final class PropertiesNodesManager implements ActiveDocumentSupport.Listener {
    
    
    private static final WeakHashMap<DataObjectContext, PropertiesNodesManager> INSTANCES = new WeakHashMap<DataObjectContext, PropertiesNodesManager>();
    
    private WeakHashMap<DataEditorView, InstanceContent> icMap;
    private Collection<InstanceContent> ics;
    private WeakHashMap<InstanceContent, WeakSet<Node>> nodesToRemoveMap;
    private WeakHashMap<DesignComponent, Sheet> sheetMap;
    private WeakReference<DataObjectContext> context;
    private WeakHashMap<DesignComponent, WeakSet<DefaultPropertySupport>> propertySupportMap;

    public synchronized static PropertiesNodesManager getDefault(DataObjectContext context) {
        if (INSTANCES.get(context) == null) {
            PropertiesNodesManager manager = new PropertiesNodesManager(context);
            ActiveDocumentSupport.getDefault().addActiveDocumentListener(manager);
            INSTANCES.put(context, manager);
        }
        return INSTANCES.get(context);
        
    }
    
    private PropertiesNodesManager(DataObjectContext context) {
        nodesToRemoveMap = new WeakHashMap<InstanceContent, WeakSet<Node>>();
        sheetMap = new WeakHashMap<DesignComponent, Sheet>();
        this.context = new WeakReference<DataObjectContext>(context);
        icMap = new WeakHashMap<DataEditorView, InstanceContent>();
        ics = new HashSet();
        propertySupportMap = new WeakHashMap<DesignComponent, WeakSet<DefaultPropertySupport>>();
    }
    
    public void add(InstanceContent ic) {
        assert(ic != null);
        this.ics.add(ic);
    }
    
    public void add(DataEditorView view, InstanceContent ic) {
        assert(ic != null);
        icMap.put(view, ic);
    }
    
    synchronized void changeLookup(DataEditorView view, Collection<DesignComponent> components) {
        if (components == null)
            return;
        Collection<InstanceContent> tempIcs = new HashSet<InstanceContent>();
        tempIcs.addAll(ics);
        if (icMap.get(view) != null)
            tempIcs.add(icMap.get(view));
        for (InstanceContent ic : tempIcs) {
            WeakSet<Node> nodesToRemove = nodesToRemoveMap.get(ic);
            if (nodesToRemove == null) {
                nodesToRemove = new WeakSet<Node>();
                nodesToRemoveMap.put(ic, nodesToRemove);
            }
            for (Node node : nodesToRemove) {
                ic.remove(node);
            }
            nodesToRemove.clear();
        }
        
        for (InstanceContent ic : tempIcs) {
            for(DesignComponent component : components) {
                Set<Node> nodesToRemove = nodesToRemoveMap.get(ic);
                PropertiesNode node = new PropertiesNode(context.get(), component, context.get().getDataObject().getLookup());
                ic.add(node);
                nodesToRemove.add(node);
            }
        }
    }
    
    public synchronized Sheet getSheet(DesignComponent component) {
        assert (component != null);
        Sheet sheet = sheetMap.get(component);
        if (sheet == null) {
            sheet = createSheet(component);
            sheetMap.put(component, sheet);
        }
        return sheet;
    }
    
    private synchronized static void createCategoriesSet(Sheet sheet, List<String> categories) {
        for (String propertyCategory : categories) {
            sheet.put(createPropertiesSet(propertyCategory));
        }
    }
    
    private synchronized static Sheet.Set createPropertiesSet(String categoryName) {
        Sheet.Set setSheet = new Sheet.Set();
        setSheet.setName(categoryName);
        setSheet.setDisplayName(categoryName);
        return setSheet;
    }
    
    private static Comparator<DesignPropertyDescriptor> compareByDisplayName = new Comparator<DesignPropertyDescriptor>() {
        public int compare(DesignPropertyDescriptor descriptor1, DesignPropertyDescriptor descriptor2) {
            return descriptor1.getPropertyDisplayName().compareTo(descriptor2.getPropertyDisplayName());
        }
    };
    
   
    public Sheet createSheet(final DesignComponent component) {
        final Sheet sheet = new Sheet();
        component.getDocument().getTransactionManager().readAccess(new Runnable() {
            public void run() {
                List<DesignPropertyDescriptor> designerPropertyDescriptors;
                List<String> categories;
                designerPropertyDescriptors = new ArrayList<DesignPropertyDescriptor>();
                categories = new ArrayList<String>();
                for (PropertiesPresenter propertiesPresenter : component.getPresenters(PropertiesPresenter.class)) {
                    designerPropertyDescriptors.addAll(propertiesPresenter.getDesignPropertyDescriptors());
                    categories.addAll(propertiesPresenter.getPropertiesCategories());
                }
                if (designerPropertyDescriptors != null)
                    Collections.sort(designerPropertyDescriptors, compareByDisplayName);
                createCategoriesSet(sheet, categories);
                for (DesignPropertyDescriptor designerPropertyDescriptor : designerPropertyDescriptors) {
                    DefaultPropertySupport property;
                    DesignPropertyEditor propertyEditor = designerPropertyDescriptor.getPropertyEditor();
                    
                    if (propertyEditor instanceof GroupPropertyEditor && designerPropertyDescriptor.getPropertyNames().size() == 0)
                        throw new IllegalStateException("To use AdvancedPropertyEditorSupport you need to specific at least one propertyName"); //NOI18N
                    
                    if (propertyEditor instanceof GroupPropertyEditor) {
                        property = new AdvancedPropertySupport(designerPropertyDescriptor, designerPropertyDescriptor.getPropertyEditorType());
                        addPropertySupport(component, property);
                    } else if (designerPropertyDescriptor.getPropertyNames().size() <= 1) {
                        property = new PrimitivePropertySupport(designerPropertyDescriptor, designerPropertyDescriptor.getPropertyEditorType());
                        addPropertySupport(component, property);
                    } else {
                        throw new IllegalArgumentException();
                    }
                    if (propertyEditor != null &&  propertyEditor.canEditAsText() != null)
                        property.setValue("canEditAsText", propertyEditor.canEditAsText()); //NOI18N
                    property.setValue("changeImmediate", false); // NOI18
                    sheet.get(designerPropertyDescriptor.getPropertyCategory()).put(property);
                }
            }
        });
        
        return sheet;
    }
    
    private void addPropertySupport(DesignComponent component, DefaultPropertySupport propertySupport) {
        WeakSet<DefaultPropertySupport> propertySupports = propertySupportMap.get(component);
        if (propertySupports == null) {
            propertySupports = new WeakSet<DefaultPropertySupport>();
            propertySupportMap.put(component, propertySupports);
        }
        propertySupports.add(propertySupport);
    }
    
    public void updatePropertyEditorsValues(DataEditorView view, Collection<DesignComponent> components) {
         if (components == null)
            return;
        for (DesignComponent component : components) {
            Set<DefaultPropertySupport> propertySupports = propertySupportMap.get(component);
            if (propertySupports == null)
                return;
            for (DefaultPropertySupport ps : propertySupports) {
                ps.update();
            }
        }
        Collection<InstanceContent> tempIcs = new HashSet<InstanceContent>();
        tempIcs.addAll(ics);
        if (icMap.get(view) != null)
            tempIcs.add(icMap.get(view));
        for (InstanceContent ic : tempIcs) {
            WeakSet<Node> nodesToRemove = nodesToRemoveMap.get(ic);
            for (Node node : nodesToRemove) {
                ((PropertiesNode)node).updateNode();
            }
        }
    }
    
    public void activeDocumentChanged(DesignDocument deactivatedDocument, DesignDocument activatedDocument) {
        changeLookup(null, Collections.<DesignComponent>emptySet());
    }
    
    public void activeComponentsChanged(Collection<DesignComponent> activeComponents) {
    }
    
}

