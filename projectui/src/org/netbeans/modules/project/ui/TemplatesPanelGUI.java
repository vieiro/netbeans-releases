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

package org.netbeans.modules.project.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.explorer.view.ListView;
import org.openide.filesystems.FileObject;
import org.openide.loaders.*;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;
import org.openide.util.NbBundle;

/**
 *
 * @author  tom
 */
public class TemplatesPanelGUI extends javax.swing.JPanel implements PropertyChangeListener {
    
    public static interface Builder {
        
        public Children createCategoriesChildren (DataFolder folder);
        
        public Children createTemplatesChildren (DataFolder folder);
        
        public String getCategoriesName ();
        
        public String getTemplatesName ();
        
        public char getCategoriesMnemonic ();
        
        public char getTemplatesMnemonic ();
        
        public void fireChange ();
    }
    
    public static final String TEMPLATES_FOLDER = "templatesFolder";        //NOI18N
    public static final String TARGET_TEMPLATE = "targetTemplate";          //NOI18N
    private static final String ATTR_INSTANTIATING_DESC = "instantiatingWizardURL"; //NOI18N
    
    private Builder firer;
    private String selectedTemplate = null;
    
    /** Creates new form TemplatesPanelGUI */
    public TemplatesPanelGUI (Builder firer) {
        assert firer != null : "Builder can not be null";  //NOI18N
        this.firer = firer;
        initComponents();
        postInitComponents ();
        setName (NbBundle.getMessage(TemplatesPanelGUI.class,"TXT_SelectTemplate"));
    }


    public void setTemplatesFolder (FileObject folder) {
        DataFolder dobj = DataFolder.findFolder (folder);
        ((ExplorerProviderPanel)this.categoriesPanel).setRootNode(new FilterNode (
            dobj.getNodeDelegate(), this.firer.createCategoriesChildren(dobj)));
    }


    public void setSelectedCategoryByName (final String categoryName) {
        if (categoryName != null) {
            ((ExplorerProviderPanel)this.categoriesPanel).setSelectedNode (categoryName);
        } else {
            // if categoryName is null then select first category leastwise
            ((CategoriesPanel)this.categoriesPanel).selectFirstCategory ();
        }
    }
    
    public String getSelectedCategoryName () {
        return ((ExplorerProviderPanel)this.categoriesPanel).getSelectionPath ();
    }
    
    public void setSelectedTemplateByName (final String templateName) {
        selectedTemplate = templateName;
        final TemplatesPanel tempExplorer = ((TemplatesPanel)this.projectsPanel);
        SwingUtilities.invokeLater (new Runnable () {
            public void run () {
                if (templateName != null) {
                    tempExplorer.setSelectedNode (templateName);
                    if (tempExplorer.getSelectionPath () == null) {
                        tempExplorer.selectFirstTemplate ();
                        selectedTemplate = tempExplorer.getSelectionPath ();
                    }
                } else {
                    tempExplorer.selectFirstTemplate ();
                    selectedTemplate = tempExplorer.getSelectionPath ();
                }
            }
        });
    }
    
    public String getSelectedTemplateName () {
        return selectedTemplate;
    }
    
    public FileObject getSelectedTemplate () {
        Node[] nodes = (Node[]) ((ExplorerProviderPanel)this.projectsPanel).getSelectedNodes();
        if (nodes != null && nodes.length == 1) {
            DataObject dobj = (DataObject) nodes[0].getCookie (DataObject.class);
            if (dobj != null) {
                while (dobj instanceof DataShadow) {
                    dobj = ((DataShadow)dobj).getOriginal();
                }
                return dobj.getPrimaryFile();
            }
        }
        return null;
    }

    public void propertyChange (PropertyChangeEvent event) {
        if (event.getSource() == this.categoriesPanel) {
            if (ExplorerManager.PROP_SELECTED_NODES.equals (event.getPropertyName ())) {
                Node[] selectedNodes = (Node[]) event.getNewValue();
                if (selectedNodes != null && selectedNodes.length == 1) {
                    String lastSelectedTemplate = getSelectedTemplateName ();
                    try {
                        ((ExplorerProviderPanel)this.projectsPanel).setSelectedNodes(new Node[0]);
                    } catch (PropertyVetoException e) {
                        /*Ignore it*/
                    }
                    DataObject template = (DataObject) selectedNodes[0].getCookie(DataFolder.class);
                    if (template != null) {
                        FileObject fo = template.getPrimaryFile();
                        ((ExplorerProviderPanel)this.projectsPanel).setRootNode(
                            new FilterNode (selectedNodes[0], this.firer.createTemplatesChildren((DataFolder)template)));
                        // after change of root select the first template to make easy move in wizard
                        this.setSelectedTemplateByName (lastSelectedTemplate);
                    }
                }
            }
        }
        else if (event.getSource() == this.projectsPanel) {
            if (ExplorerManager.PROP_SELECTED_NODES.equals (event.getPropertyName())) {
                Node[] selectedNodes = (Node[]) event.getNewValue ();
                if (selectedNodes != null && selectedNodes.length == 1) {
                    DataObject template = (DataObject) selectedNodes[0].getCookie(DataObject.class);
                    if (template != null) {
                        FileObject fo = template.getPrimaryFile();
                        URL descURL = getDescription (template);
                        if (descURL != null) {
                            try {
                                this.description.setPage (descURL);                                
                            } catch (IOException e) {
                                this.description.setText (ResourceBundle.getBundle("org/netbeans/modules/project/ui/Bundle").getString("TXT_NoDescription"));
                            }
                        }
                        else {
                            this.description.setText (ResourceBundle.getBundle("org/netbeans/modules/project/ui/Bundle").getString("TXT_NoDescription"));
                        }
                    }                    
                }
                this.firer.fireChange ();
            }
        }
    }
    
    private void postInitComponents () {        
        this.jLabel1.setText (this.firer.getCategoriesName());
        this.jLabel1.setDisplayedMnemonic(this.firer.getCategoriesMnemonic());
        this.jLabel2.setText (this.firer.getTemplatesName());
        this.jLabel2.setDisplayedMnemonic (this.firer.getTemplatesMnemonic());                                                
        this.categoriesPanel.addPropertyChangeListener(this);                        
        this.projectsPanel.addPropertyChangeListener(this);
        this.description.setEditorKit(new HTMLEditorKit());
    }

    public void addNotify() {
        super.addNotify();
        
        if (description.getEditorKit() instanceof HTMLEditorKit) {
            // override the Swing default CSS to make the HTMLEditorKit use the
            // same font as the rest of the UI.  This must be done in addNotify()
            // because before the components are realized the font sizes are wrong
            // on GTKLookAndFeel

            HTMLEditorKit htmlkit = (HTMLEditorKit) description.getEditorKit();
            StyleSheet css = htmlkit.getStyleSheet();
            Font f = jLabel1.getFont();
            css.addRule(new StringBuffer("body { font-size: ").append(f.getSize()) // NOI18N
                        .append("pt; font-family: ").append(f.getName()).append("; }").toString()); // NOI18N
        }
        
        categoriesPanel.requestFocus ();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        categoriesPanel = new CategoriesPanel ();
        projectsPanel = new TemplatesPanel ();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        description = new javax.swing.JEditorPane();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(500, 230));
        jLabel1.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/project/ui/Bundle").getString("MNE_Categories").charAt(0));
        jLabel1.setLabelFor(categoriesPanel);
        jLabel1.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/project/ui/Bundle").getString("CTL_Categories"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        add(jLabel1, gridBagConstraints);

        jLabel2.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/project/ui/Bundle").getString("MNE_Templates").charAt(0));
        jLabel2.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/project/ui/Bundle").getString("CTL_Templates"));
        jLabel2.setLabelFor(projectsPanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(jLabel2, gridBagConstraints);

        categoriesPanel.setBorder(new javax.swing.border.EtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 6, 6);
        add(categoriesPanel, gridBagConstraints);

        projectsPanel.setBorder(new javax.swing.border.EtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 0);
        add(projectsPanel, gridBagConstraints);

        jLabel3.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/project/ui/Bundle").getString("CTL_DescriptionMnemonic").charAt(0));
        jLabel3.setLabelFor(description);
        jLabel3.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/project/ui/Bundle").getString("CTL_Description"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        add(jLabel3, gridBagConstraints);

        description.setEditable(false);
        description.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/project/ui/Bundle").getString("TXT_NoDescription"));
        description.setPreferredSize(new java.awt.Dimension(100, 66));
        jScrollPane1.setViewportView(description);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        add(jScrollPane1, gridBagConstraints);

    }//GEN-END:initComponents
    
    private URL getDescription (DataObject dobj) {
        //XXX: Some templates are using templateWizardURL others instantiatingWizardURL. What is correct?        
        FileObject fo = dobj.getPrimaryFile();
        URL desc = (URL) fo.getAttribute(ATTR_INSTANTIATING_DESC);
        if (desc != null) {
            return desc;
        }
        desc = TemplateWizard.getDescription (dobj);
        return desc;
    }
    
    private static abstract class ExplorerProviderPanel extends JPanel implements ExplorerManager.Provider, PropertyChangeListener, VetoableChangeListener {
        
        private ExplorerManager manager;
        
        protected ExplorerProviderPanel () {           
            this.manager = new ExplorerManager ();
            this.manager.addPropertyChangeListener(this);
            this.manager.addVetoableChangeListener(this);
            this.initGUI ();
        }
                
        public void setRootNode (Node node) {
            this.manager.setRootContext(node);
        }
        
        public Node getRootNode () {
            return this.manager.getRootContext();
        }
        
        public Node[] getSelectedNodes () {
            return this.manager.getSelectedNodes();
        }
        
        public void setSelectedNodes (Node[] nodes) throws PropertyVetoException {
            this.manager.setSelectedNodes(nodes);
        }
        
        public void setSelectedNode (String path) {
            if (path == null) {
                return;
            }
            StringTokenizer tk = new StringTokenizer (path,"/");    //NOI18N
            String[] names = new String[tk.countTokens()];
            for (int i=0;tk.hasMoreTokens();i++) {
                names[i] = tk.nextToken();
            }
            try {
                Node node = NodeOp.findPath(this.manager.getRootContext(),names);
                if (node != null) {
                    this.manager.setSelectedNodes(new Node[] {node});
                }
            } catch (PropertyVetoException e) {
                //Skeep it, not important
            }
            catch (NodeNotFoundException e) {
                //Skeep it, not important
            }
        }
        
        public String getSelectionPath () {
            Node[] selectedNodes = this.manager.getSelectedNodes();
            if (selectedNodes == null || selectedNodes.length != 1) {
                return null;
            }
            Node rootNode = this.manager.getRootContext();
            String[] path = NodeOp.createPath(selectedNodes[0],rootNode);
            StringBuffer builder = new StringBuffer ();
            for (int i=0; i< path.length; i++) {
                builder.append('/');        //NOI18N
                builder.append(path[i]);
            }
            return builder.substring(1);
        }
        
        public ExplorerManager getExplorerManager() {
            return this.manager;
        }
        
        public void propertyChange (final PropertyChangeEvent event) {
            // workaround of issue 43502, update of Help button set back the focus
            // to component which is active when this change starts
            SwingUtilities.invokeLater (new Runnable () {
                public void run () {
                    firePropertyChange(event.getPropertyName(),
                        event.getOldValue(), event.getNewValue());                }
            });

        }
        
        public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
            if (ExplorerManager.PROP_SELECTED_NODES.equals (evt.getPropertyName())) {
                Node[] newValue = (Node[]) evt.getNewValue();
                if (newValue == null || (newValue.length != 1 && newValue.length != 0)) {
                    throw new PropertyVetoException ("Invalid length",evt);      //NOI18N
                }
            }
        }
        
        public void requestFocus () {
            this.createComponent().requestFocus();
        }
        
        protected abstract JComponent createComponent ();
        
        private void initGUI () {
            this.setLayout (new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints ();
            c.gridx = GridBagConstraints.RELATIVE;
            c.gridy = GridBagConstraints.RELATIVE;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.gridheight = GridBagConstraints.REMAINDER;
            c.fill = GridBagConstraints.BOTH;
            c.anchor = GridBagConstraints.NORTHWEST;
            c.weightx = 1.0;
            c.weighty = 1.0;
            JComponent component = this.createComponent ();
            ((GridBagLayout)this.getLayout()).setConstraints(component, c);
            this.add (component);
        }
        
    }


    private static class CategoriesBeanTreeView extends BeanTreeView {
        public CategoriesBeanTreeView () {
            super ();
            this.tree.setEditable(false);
        }
        public void selectFirstCategory () {
            SwingUtilities.invokeLater (new Runnable () {
                public void run () {
                    tree.setSelectionRow (0);
                }
            });
        }
    }

    private static final class CategoriesPanel extends ExplorerProviderPanel {

        private CategoriesBeanTreeView btv;

        protected synchronized JComponent createComponent () {
            if (this.btv == null) {
                this.btv = new CategoriesBeanTreeView ();
                this.btv.setRootVisible(false);
                this.btv.setPopupAllowed(false);
                this.btv.setDefaultActionAllowed(false);
            }
            return this.btv;
        }
        
        public void selectFirstCategory () {
            btv.selectFirstCategory ();
        }
        
    }
    
    private static class TemplatesListView extends ListView {
        public TemplatesListView () {
            super ();
            // bugfix #44717, Enter key must work regardless if TemplatesPanels is focused
            list.unregisterKeyboardAction (KeyStroke.getKeyStroke (KeyEvent.VK_ENTER, 0, false));
        }
    }
    
    private static final class TemplatesPanel extends ExplorerProviderPanel {
        
        private ListView list;

        protected synchronized JComponent createComponent () {
            
            if (this.list == null) {
                this.list = new TemplatesListView ();
                this.list.setPopupAllowed(false);
            }
            
            return this.list;
        }
        
        public void selectFirstTemplate () {
            try {
                Children ch = getExplorerManager ().getRootContext ().getChildren ();
                if (ch.getNodesCount () > 0) {
                    getExplorerManager ().setSelectedNodes (new Node[] { ch.getNodes ()[0] });
                }
            } catch (PropertyVetoException pve) {
                // doesn't matter, can ignore it
            }
        }
        
    }
           
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel categoriesPanel;
    private javax.swing.JEditorPane description;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel projectsPanel;
    // End of variables declaration//GEN-END:variables
    
}
