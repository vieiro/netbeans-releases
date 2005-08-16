/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.deployment.impl.ui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.j2ee.deployment.impl.ServerInstance;
import org.netbeans.modules.j2ee.deployment.impl.ServerRegistry;
import org.netbeans.modules.j2ee.deployment.impl.ui.wizard.AddServerInstanceWizard;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.openide.nodes.Node;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.util.NbBundle;



/**
 * Servers customizer displays a list of registered server and allows to add,
 * remove and configure them.
 *
 * @author  Stepan Herold
 */
public class ServersCustomizer extends javax.swing.JPanel implements PropertyChangeListener, VetoableChangeListener, ExplorerManager.Provider {
    
    private static final Dimension PREFERRED_SIZE = new Dimension(720,400);
    
    private ServerCategoriesChildren children;
    private ExplorerManager manager;
    private ServerInstance initialInstance;
    
    /** Creates new form PlatformsCustomizer */
    public ServersCustomizer(ServerInstance initialInstance) {
        initComponents();
        this.initialInstance = initialInstance;
    }
    
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
        Node[] nodes = (Node[]) evt.getNewValue();
        if (nodes.length!=1) {
            selectServer(null);
        } else {
            selectServer(nodes[0]);
        }
        }
    }
    
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            Node[] nodes = (Node[]) evt.getNewValue();
            if (nodes.length>1) {
                throw new PropertyVetoException("Invalid length",evt);   //NOI18N
            }
        }
    }
    
    
    public Dimension getPreferredSize() {
        return PREFERRED_SIZE;
    }
    
    public synchronized ExplorerManager getExplorerManager() {
        if (this.manager == null) {
            this.manager = new ExplorerManager();
            this.manager.setRootContext(new AbstractNode(getChildren()));
            this.manager.addPropertyChangeListener(this);
            this.manager.addVetoableChangeListener(this);
        }
        return manager;
    }
    
    public void addNotify() {
        super.addNotify();
        expandServers(initialInstance);
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel3 = new javax.swing.JPanel();
        servers = new PlatformsView ();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        cards = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        serverName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        serverType = new javax.swing.JTextField();
        clientArea = new javax.swing.JPanel();
        messageArea = new javax.swing.JPanel();
        serversLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        getAccessibleContext().setAccessibleDescription(null);
        servers.setBorder(new javax.swing.border.EtchedBorder());
        servers.setPreferredSize(new java.awt.Dimension(220, 400));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 12, 6, 6);
        add(servers, gridBagConstraints);
        servers.getAccessibleContext().setAccessibleName(null);
        servers.getAccessibleContext().setAccessibleDescription(null);

        org.openide.awt.Mnemonics.setLocalizedText(addButton, NbBundle.getMessage(ServersCustomizer.class, "CTL_AddServer"));
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addServer(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 12, 0);
        add(addButton, gridBagConstraints);
        addButton.getAccessibleContext().setAccessibleDescription(null);

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, NbBundle.getMessage(ServersCustomizer.class, "CTL_Remove"));
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeServer(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 12, 6);
        add(removeButton, gridBagConstraints);
        removeButton.getAccessibleContext().setAccessibleDescription(null);

        cards.setLayout(new java.awt.CardLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setLabelFor(serverName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, NbBundle.getMessage(ServersCustomizer.class, "CTL_ServerName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(jLabel1, gridBagConstraints);

        serverName.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel1.add(serverName, gridBagConstraints);
        serverName.getAccessibleContext().setAccessibleDescription(null);

        jLabel2.setLabelFor(serverType);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, NbBundle.getMessage(ServersCustomizer.class, "CTL_ServerType"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 12, 0);
        jPanel1.add(jLabel2, gridBagConstraints);

        serverType.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 12, 0);
        jPanel1.add(serverType, gridBagConstraints);
        serverType.getAccessibleContext().setAccessibleDescription(null);

        clientArea.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(clientArea, gridBagConstraints);

        cards.add(jPanel1, "card2");

        messageArea.setLayout(new java.awt.GridBagLayout());

        cards.add(messageArea, "card3");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 12);
        add(cards, gridBagConstraints);

        serversLabel.setLabelFor(servers);
        org.openide.awt.Mnemonics.setLocalizedText(serversLabel, org.openide.util.NbBundle.getMessage(ServersCustomizer.class, "CTL_Servers"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(serversLabel, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    private void removeServer(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeServer
        Node[] nodes = getExplorerManager().getSelectedNodes();
        if (nodes.length!=1) {
            assert false : "Illegal number of selected nodes";      //NOI18N
            return;
        }
        if (nodes[0] instanceof ServerNode) {
            ServerInstance serverInstance = ((ServerNode)nodes[0]).getServerInstance();
            if (!serverInstance.isRemoveForbidden()) {
                ServerRegistry.getInstance().removeServerInstance(serverInstance.getUrl());
                getChildren().refreshServers();
                expandServers(null);
            }
        }
    }//GEN-LAST:event_removeServer
    
    private void addServer(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addServer
        AddServerInstanceWizard wizard = new AddServerInstanceWizard();
        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
        dialog.setVisible(true);
        if (wizard.getValue() == WizardDescriptor.FINISH_OPTION) {
            getChildren().refreshServers();
            ServerInstance servInst = null;
            // PENDING : AddServerInstanceWizard.getInstantiatedObjects is expected to 
            // return set of InstanceProperties instances - this should be ensured
            Set result = wizard.getInstantiatedObjects();
            if (result != null) {
                for (Iterator i = result.iterator(); i.hasNext();) {
                    Object instObj = i.next();
                    if (instObj instanceof InstanceProperties) {
                        InstanceProperties ip = (InstanceProperties)instObj;
                        String url = ip.getProperty(InstanceProperties.URL_ATTR);
                        servInst = ServerRegistry.getInstance().getServerInstance(url);
                    }
                }
            }
            expandServers(servInst);
        }
    }//GEN-LAST:event_addServer
    
    
        private synchronized ServerCategoriesChildren getChildren() {
            if (this.children == null) {
                this.children = new ServerCategoriesChildren();
            }
            return this.children;
        }
    
        private void selectServer(Node aNode) {
            clientArea.removeAll();

            if (aNode instanceof ServerNode) {
                ServerInstance serverInstance = ((ServerNode)aNode).getServerInstance();
                serverName.setText(serverInstance.getDisplayName());
                serverType.setText(serverInstance.getServer().getDisplayName());
                if (serverInstance.isRemoveForbidden()) {
                    removeButton.setEnabled(false);
                } else {
                    removeButton.setEnabled(true);
                }
            } else {
                removeButton.setEnabled(false);
                ((CardLayout)cards.getLayout()).last(cards);
                return;
            }
            
            if (aNode.hasCustomizer()) {
                Component component = aNode.getCustomizer();
                if (component != null) {
                    addComponent(clientArea, component);
                }
            }
            clientArea.revalidate();
            CardLayout cl = (CardLayout)cards.getLayout();
            cl.first(cards);
        }
    
    private static void addComponent(Container container, Component component) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = c.gridy = GridBagConstraints.RELATIVE;
        c.gridheight = c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = c.weighty = 1.0;
        ((GridBagLayout)container.getLayout()).setConstraints(component,c);
        container.add(component);
    }
    
    private void expandServers(ServerInstance servInst) {
        ExplorerManager mgr = this.getExplorerManager();
        Node node = mgr.getRootContext();
        expandAllNodes(servers, node, mgr, servInst);
    }
    
    private static void expandAllNodes(BeanTreeView btv, Node node, ExplorerManager mgr, ServerInstance servInst) {
        btv.expandNode(node);
        Children ch = node.getChildren();
        
        // preselect node for the specified server instance
        if (servInst != null && ch == Children.LEAF && node instanceof ServerNode) {
            try {
                if (((ServerNode)node).getServerInstance() == servInst) {
                    mgr.setSelectedNodes(new Node[] {node});
                }
            } catch (PropertyVetoException e) {
                //Ignore it
            }
        }
        
        // preselect first server
        if (servInst == null && ch == Children.LEAF && mgr.getSelectedNodes().length == 0) {
            try {
                mgr.setSelectedNodes(new Node[] {node});
            } catch (PropertyVetoException e) {
                //Ignore it
            }
        }
        Node nodes[] = ch.getNodes( true );
        for ( int i = 0; i < nodes.length; i++ ) {
            expandAllNodes( btv, nodes[i], mgr, servInst);
        }
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel cards;
    private javax.swing.JPanel clientArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel messageArea;
    private javax.swing.JButton removeButton;
    private javax.swing.JTextField serverName;
    private javax.swing.JTextField serverType;
    private org.openide.explorer.view.BeanTreeView servers;
    private javax.swing.JLabel serversLabel;
    // End of variables declaration//GEN-END:variables
    
    
    private static class PlatformsView extends BeanTreeView {
        
        public PlatformsView() {
            super();
            this.setPopupAllowed(false);
            this.setDefaultActionAllowed(false);
            this.setRootVisible(false);
            this.tree.setEditable(false);
            this.tree.setShowsRootHandles(false);
        }
        
    }
    
    private static class ServerCategoriesDescriptor implements Comparable {
        private final String categoryName;
        private final List/*<Node>*/ servers;

        public ServerCategoriesDescriptor(String categoryName) {
            assert categoryName != null;
            this.categoryName = categoryName;
            this.servers = new ArrayList();
        }

        public String getName() {
            return categoryName;
        }

        public List getServers() {
            return Collections.unmodifiableList(this.servers);
        }

        public void add(Node node) {
            servers.add(node);
        }

        public int hashCode() {
            return categoryName.hashCode();
        }

        public boolean equals(Object other) {
            if (other instanceof ServerCategoriesDescriptor) {
                ServerCategoriesDescriptor desc = (ServerCategoriesDescriptor) other;
                return categoryName.equals(desc.categoryName) &&
                        servers.size() == desc.servers.size();
            }
            return false;
        }

        public int compareTo(Object other) {
            if (!(other instanceof ServerCategoriesDescriptor )) {
                throw new IllegalArgumentException();
            }
            ServerCategoriesDescriptor desc = (ServerCategoriesDescriptor) other;
            return categoryName.compareTo(desc.categoryName);
        }

    }

    private static class ServersChildren extends Children.Keys {

        private List servers;

        public ServersChildren (List/*<Node>*/ servers) {
            this.servers = servers;
        }

        protected void addNotify() {
            super.addNotify();
            this.setKeys (this.servers);
        }

        protected void removeNotify() {
            super.removeNotify();
            this.setKeys(new Object[0]);
        }

        protected Node[] createNodes(Object key) {
            return new Node[] {(Node) key};
        }
    }
    
    private static class ServerNode extends FilterNode {
        
        private final ServerInstance serverInstance;
        
        public ServerNode(ServerInstance serverInstance) {
            super(serverInstance.getServer().getRegistryNodeFactory().getManagerNode(RegistryNodeProvider.createLookup(serverInstance)));
            disableDelegation(DELEGATE_GET_DISPLAY_NAME | DELEGATE_SET_DISPLAY_NAME |
                    DELEGATE_GET_NAME | DELEGATE_SET_NAME);
            this.serverInstance = serverInstance;
            setChildren(Children.LEAF);
            setDisplayName(serverInstance.getDisplayName());
            setName(serverInstance.getUrl());
        }
        
        public ServerInstance getServerInstance() {
            return serverInstance;
        }
        
    }
    
    private static class ServerCategoryNode extends AbstractNode {

        private final ServerCategoriesDescriptor desc;
        private Node iconDelegate;

        public ServerCategoryNode (ServerCategoriesDescriptor desc) {
            super (new ServersChildren (desc.getServers()));
            this.desc = desc;
            this.iconDelegate = DataFolder.findFolder (Repository.getDefault().getDefaultFileSystem().getRoot()).getNodeDelegate();
        }

        public String getDisplayName () {
            return desc.getName();
        }

        public Image getIcon(int type) {
            return iconDelegate.getIcon(type);
        }        

        public Image getOpenedIcon(int type) {
            return iconDelegate.getOpenedIcon(type);
        }
    }

    private static class ServerCategoriesChildren extends Children.Keys {

        protected void addNotify () {
            super.addNotify ();
            this.refreshServers();
        }

        protected void removeNotify () {
            super.removeNotify ();
        }

        protected Node[] createNodes(Object key) {
            if (key instanceof ServerCategoriesDescriptor) {
                ServerCategoriesDescriptor desc = (ServerCategoriesDescriptor) key;
                return new Node[] {
                    new ServerCategoryNode (desc)
                };
            }
            else if (key instanceof Node) {
                return new Node[] {
                    new FilterNode ((Node)key,Children.LEAF)
                };
            }
            else {
                return new Node[0];
            }
        }

        private void refreshServers() {
            Collection servInstances = ServerRegistry.getInstance().getInstances();
            HashMap/*<String,ServerCategoriesDescriptor>*/ categories = new HashMap();
            
            // currently we have only j2eeServers category
            final String J2EE_SERVERS_CATEGORY = NbBundle.getMessage(ServersCustomizer.class, "LBL_J2eeServersNode");  // NOI18N
            ServerCategoriesDescriptor j2eeServers = new ServerCategoriesDescriptor(J2EE_SERVERS_CATEGORY);
            for(Iterator it = servInstances.iterator(); it.hasNext();) {
                ServerInstance serverInstance = (ServerInstance)it.next();
                j2eeServers.add(new ServerNode(serverInstance));
            }
            categories.put(J2EE_SERVERS_CATEGORY, j2eeServers);
            List keys = new ArrayList(categories.values());
            Collections.sort(keys);
            this.setKeys(keys);
        }
    }
}

