/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.xml.multiview.ui;

import org.netbeans.modules.xml.multiview.SectionNode;
import org.netbeans.modules.xml.multiview.Utils;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

import java.util.HashMap;

/**
 * @author pfiala
 */
public class SectionNodeView extends SectionView {

    private final DataObject dataObject;
    private SectionNode rootNode = null;
    private HashMap nodes = new HashMap();

    public SectionNodeView(DataObject dataObject) {
        super();
        this.dataObject = dataObject;
    }

    public void setRootNode(SectionNode rootNode) {
        this.rootNode = rootNode;
        Children root = new Children.Array();
        root.add(new Node[]{rootNode});
        AbstractNode mainNode = new AbstractNode(root);
        mainNode.setDisplayName(rootNode.getDisplayName());
        mainNode.setIconBase(rootNode.getIconBase());
        setRoot(mainNode);
        addSection(rootNode.getSectionNodePanel());
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    public void openSection(Node node) {
        openPanel(node);
    }

    public void openPanel(Object key) {
        SectionNode sectionNode = retrieveSectionNode((SectionNode) key);
        SectionNodePanel sectionNodePanel = sectionNode.getSectionNodePanel();
        sectionNodePanel.open();
        setActivePanel(sectionNodePanel);
        sectionNodePanel.setActive(true);
        selectNode(sectionNodePanel.getNode());
        Utils.scrollToVisible(sectionNodePanel);
    }

    public SectionNode getRootNode() {
        return rootNode;
    }

    public void registerNode(SectionNode node) {
        nodes.put(node, node);
    }

    public SectionNode retrieveSectionNode(SectionNode node) {
        SectionNode sectionNode = (SectionNode) nodes.get(node);
        return sectionNode == null ? rootNode : sectionNode;
    }
}