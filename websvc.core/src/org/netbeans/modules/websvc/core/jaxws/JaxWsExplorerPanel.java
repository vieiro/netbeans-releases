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

package org.netbeans.modules.websvc.core.jaxws;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.border.EtchedBorder;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.websvc.api.webservices.WebServicesSupport;
import org.netbeans.modules.websvc.api.webservices.WebServicesView;
import org.netbeans.modules.websvc.core.jaxws.nodes.JaxWsNode;
import org.netbeans.modules.websvc.core.webservices.action.JaxRpcWsdlCookie;
import org.netbeans.modules.websvc.jaxws.api.JAXWSSupport;
import org.netbeans.modules.websvc.jaxws.api.JAXWSView;
import org.netbeans.modules.websvc.jaxws.api.JaxWsWsdlCookie;
import org.netbeans.spi.project.ui.LogicalViewProvider;

import org.openide.DialogDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.FilterNode;
import org.openide.util.NbBundle;

/**
 *
 * @author Milan Kuchtiak
 */
public class JaxWsExplorerPanel extends JPanel implements ExplorerManager.Provider, PropertyChangeListener {

	private DialogDescriptor descriptor;
	private ExplorerManager manager;
	private BeanTreeView treeView;
	private Node selectedServiceNode;

	public JaxWsExplorerPanel() {
		manager = new ExplorerManager();
		selectedServiceNode = null;
		
		initComponents();
		initUserComponents();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLblTreeView = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jLblTreeView.setText(NbBundle.getMessage(JaxWsExplorerPanel.class, "LBL_AvailableWebServices"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(jLblTreeView, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLblTreeView;
    // End of variables declaration//GEN-END:variables

	private void initUserComponents() {
		treeView = new BeanTreeView();
		treeView.setRootVisible(false);
		treeView.setPopupAllowed(false);
                treeView.setBorder(new EtchedBorder());

		java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(treeView, gridBagConstraints);
		jLblTreeView.setLabelFor(treeView);
	}

	public ExplorerManager getExplorerManager() {
		return manager;
	}	

	public void addNotify() {
		super.addNotify();
		manager.addPropertyChangeListener(this);
                Project[] projects = OpenProjects.getDefault().getOpenProjects();
                Children rootChildren = new Children.Array();
                AbstractNode explorerClientRoot = new AbstractNode(rootChildren);
                List projectNodeList = new ArrayList();        
                for (int i=0;i<projects.length;i++) {
                    LogicalViewProvider logicalProvider = (LogicalViewProvider)projects[i].getLookup().lookup(LogicalViewProvider.class);
                    if (logicalProvider!=null) {
                        Node rootNode = logicalProvider.createLogicalView();
                        JAXWSSupport jaxWsSupport = JAXWSSupport.getJAXWSSupport(projects[i].getProjectDirectory());
                        if (jaxWsSupport!=null && jaxWsSupport.getServices().size()>0) {
                            Node servicesNode = JAXWSView.getJAXWSView().createJAXWSView(projects[i]);
                            
                            if (servicesNode!=null) {
                                Children children = new Children.Array();
                                Node[] nodes= servicesNode.getChildren().getNodes();
                                if (nodes!=null && nodes.length>0) {
                                    Node[] serviceNodes = new Node[nodes.length];
                                    for (int j=0;j<nodes.length;j++) {
                                        serviceNodes[j] = new ServiceNode((JaxWsNode)nodes[j]);
                                    }
                                    children.add(serviceNodes);
                                    projectNodeList.add(new ProjectNode(children, rootNode));
                                }
                            }
                        } else {
                            WebServicesSupport wsSupport = WebServicesSupport.getWebServicesSupport(projects[i].getProjectDirectory());
                            if (wsSupport!=null && wsSupport.getServices().size()>0) {
                                FileObject ddFolder = wsSupport.getWsDDFolder();
                                Node servicesNode = WebServicesView.getWebServicesView(ddFolder).createWebServicesView(ddFolder);
                                if (servicesNode!=null) {
                                    Children children = new Children.Array();
                                    Node[] nodes= servicesNode.getChildren().getNodes();
                                    if (nodes!=null && nodes.length>0) {
                                        Node[] filterNodes = new Node[nodes.length];
                                        for (int j=0;j<nodes.length;j++) filterNodes[j] = new FilterNode(nodes[j]);
                                        children.add(filterNodes);
                                        projectNodeList.add(new ProjectNode(children, rootNode));
                                    }
                                }
                            }
                        }
                    }
                }
                Node[] projectNodes = new Node[projectNodeList.size()];
                projectNodeList.toArray(projectNodes);
                rootChildren.add(projectNodes);
		manager.setRootContext(explorerClientRoot);
		
		// !PW If we preselect a node, this can go away.
		descriptor.setValid(false);
	}

	public void removeNotify() {
		manager.removePropertyChangeListener(this);
		super.removeNotify();
	}

	public void setDescriptor(DialogDescriptor descriptor) {
		this.descriptor = descriptor;
	}
	
	public Node getSelectedService() {
		return selectedServiceNode;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getSource() == manager) {
			if(ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
				Node nodes[] = manager.getSelectedNodes();
				if(nodes != null && nodes.length > 0 ) {
					Node node = nodes[0];
					if(node.getCookie(JaxWsWsdlCookie.class)!=null || node.getCookie(JaxRpcWsdlCookie.class)!=null) {
						// This is a method node.
						selectedServiceNode = node;
						descriptor.setValid(true);
					} else {
						// This is not a method node.
						selectedServiceNode = null;
						descriptor.setValid(false);
					}
				}
			}
		}
	}
        
    private class ProjectNode extends AbstractNode {
        private Node rootNode;
        
        ProjectNode(Children children, Node rootNode) {
            super(children);
            this.rootNode=rootNode;
            setName(rootNode.getDisplayName());
        }
        
        public Image getIcon(int type) {
            return rootNode.getIcon(type);
        }

        public Image getOpenedIcon(int type) {
            return rootNode.getOpenedIcon(type);
        }
    }
    
    private class ServiceNode extends AbstractNode implements JaxWsWsdlCookie{
        
        private JaxWsNode serviceNode;
        
        ServiceNode(JaxWsNode serviceNode) {
            super(Children.LEAF);
            this.serviceNode=serviceNode;
            setName(serviceNode.getDisplayName());
            getCookieSet().add(this);
        }
        
        public Image getIcon(int type) {
            return serviceNode.getIcon(type);
        }
        
        public Image getOpenedIcon(int type) {
            return serviceNode.getOpenedIcon(type);
        }

        public String getWsdlURL() {
            return serviceNode.getWsdlURL();
        }
        
    }
}
