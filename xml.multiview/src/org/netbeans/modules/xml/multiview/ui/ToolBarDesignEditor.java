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

import org.netbeans.modules.xml.multiview.Error;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.ChoiceView;
import org.openide.explorer.view.NodeListModel;
import org.openide.util.Lookup;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;

/**
 * The ComponentPanel three pane editor. This is basically a container that implements the ExplorerManager
 * interface. It coordinates the selection of a node in the structure pane and the display of a panel by the a PanelView
 * in the content pane and the nodes properties in the properties pane. It will populate the tree view in the structure pane
 * from the root node of the supplied PanelView.
 *
 **/

public class ToolBarDesignEditor extends AbstractDesignEditor {
    
    protected JComponent designPanel;
    private ErrorPanel errorPanel;
    private Object lastActive;
    public static final String PROPERTY_FLUSH_DATA = "Flush Data"; // NOI18N

    /**
     * Creates a new instance of ToolBarDesignEditor
     * @param panel The PanelView which will provide the node tree for the structure view
     *              and the set of panels the nodes map to.
     */
    public ToolBarDesignEditor(){
        super();
        createDesignPanel();
        add(java.awt.BorderLayout.CENTER,designPanel);
        add(java.awt.BorderLayout.SOUTH,createErrorPanel());
    }
    
    /**
     * Creates a new instance of ToolBarDesignEditor
     * @param panel The PanelView which will provide the node tree for the structure view
     *              and the set of panels the nodes map to.
     */
    public ToolBarDesignEditor(PanelView panel){
        super(panel);
        createDesignPanel();
        designPanel.add(panel,BorderLayout.CENTER);
        add(java.awt.BorderLayout.CENTER,designPanel);
        add(java.awt.BorderLayout.SOUTH,createErrorPanel());
        panel.attachErrorPanel(errorPanel);
    }
    
    public JComponent createDesignPanel(){
        designPanel = new JPanel(new BorderLayout());
        return designPanel;
    }
    
    public void setContentView(PanelView panel) {
        if (getContentView()!=null) {
            designPanel.remove(getContentView());
        }
        designPanel.add(panel,BorderLayout.CENTER);
        panel.attachErrorPanel(errorPanel);
        super.setContentView(panel);
    }
    
    public ErrorPanel getErrorPanel() {
        return errorPanel;
    }
    
    public Error getError() {
        return(errorPanel==null?null:errorPanel.getError());
    }
    
    private ErrorPanel createErrorPanel() {
        errorPanel = new ErrorPanel(this);
        return errorPanel;
    }
    public void componentClosed() {
        super.componentClosed();
    }
    public void componentOpened() {
        super.componentOpened();
    }
    public void componentShowing() {
        super.componentShowing();
    }
    public void componentHidden() {
        super.componentShowing();
    }
    
    /**
     * Used to create an instance of the JComponent used for the structure component. Usually a subclass of BeanTreeView.
     * @return the JComponent
     */
    
    public JComponent createStructureComponent() {
        JToolBar toolbar = new ToolBarView(getExplorerManager(),getContentView().getRoot(), helpAction);
        return toolbar;
    }
 
    private static class ToolBarView extends JToolBar implements ExplorerManager.Provider, Lookup.Provider {
        private ExplorerManager manager;
        private Lookup lookup;
        private javax.swing.Action helpAction;
        ToolBarView(final ExplorerManager manager, org.openide.nodes.Node root, javax.swing.Action helpAction) {
            super();
            this.manager=manager;
            this.helpAction=helpAction;
            // same as before...
            
            setLayout(new java.awt.GridBagLayout());
            ActionMap map = getActionMap();
            // ...and initialization of lookup variable
            lookup = ExplorerUtils.createLookup (manager, map);

            ChoiceView cView = new ChoiceView();
            ((NodeListModel)(cView.getModel())).setNode(root);
            setFloatable(false);
            ((NodeListModel)(cView.getModel())).setDepth(5);
            
            java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0, 2, 4, 0);
            add(cView,gridBagConstraints);
            
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx = 1.0;
            JPanel filler = new JPanel();
            add(filler,gridBagConstraints);
            
            javax.swing.JButton helpButton = new javax.swing.JButton(helpAction);
            helpButton.setText("");
            helpButton.setContentAreaFilled(false);
            helpButton.setFocusPainted(false);
            helpButton.setBorderPainted(false);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
            //gridBagConstraints.weightx = 1.0;
            add(helpButton,gridBagConstraints);
        }
        // ...method as before and getLookup
        public ExplorerManager getExplorerManager() {
            return manager;
        }
        
        public Lookup getLookup() {
            return lookup;
        }
        // ...methods as before, but replace componentActivated and
        // componentDeactivated with e.g.:
        
        public void addNotify() {
            //System.out.println("addNotify()");
            super.addNotify();
            ExplorerUtils.activateActions(manager, true);
        }
        public void removeNotify() {
            //System.out.println("removeNotify()");
            ExplorerUtils.activateActions(manager, false);
            //super.removeNotify();
        }
    }
    
    public Object getLastActive() {
        return lastActive;
    }
    
    public void setLastActive(Object lastActive) {
        this.lastActive=lastActive;
    }

    public void fireVetoableChange(String propertyName, Object oldValue, Object newValue)
            throws PropertyVetoException {
        super.fireVetoableChange(propertyName, oldValue, newValue);
    }
    
}
