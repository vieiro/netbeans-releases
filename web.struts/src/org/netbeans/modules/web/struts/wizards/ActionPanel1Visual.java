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

package org.netbeans.modules.web.struts.wizards;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.j2ee.dd.api.common.InitParam;
import org.netbeans.modules.j2ee.dd.api.web.Servlet;
import org.netbeans.modules.web.struts.config.model.Action;
import org.netbeans.modules.web.struts.config.model.FormBean;
import org.netbeans.modules.web.struts.dialogs.BrowseFolders;
import org.openide.WizardDescriptor;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.HelpCtx;
import org.netbeans.api.project.Project;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.struts.StrutsConfigUtilities;
import org.netbeans.modules.web.struts.StrutsConfigDataObject;
import org.openide.util.NbBundle;

public class ActionPanel1Visual extends javax.swing.JPanel implements HelpCtx.Provider {
    String configFile;
    private ActionPanel1 panel;
    
    /** Creates new form ActionPanel1Visual */
    public ActionPanel1Visual(ActionPanel1 panel) {  
        this.panel=panel;
        initComponents();
        setName(NbBundle.getMessage(ActionPanel1Visual.class,"TITLE_FormBean&Parameter"));
        putClientProperty ("NewFileWizard_Title",  //NOI18N
                NbBundle.getMessage(ActionPanel1Visual.class, "TITLE_StrutsAction"));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jLabelFormName = new javax.swing.JLabel();
        CBFormName = new javax.swing.JComboBox();
        CBInputAction = new javax.swing.JComboBox();
        TFInputResource = new javax.swing.JTextField();
        CHBUseFormBean = new javax.swing.JCheckBox();
        jButtonBrowse = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        RBSession = new javax.swing.JRadioButton();
        RBRequest = new javax.swing.JRadioButton();
        jLabelScope = new javax.swing.JLabel();
        jLabelAttribute = new javax.swing.JLabel();
        TFAttribute = new javax.swing.JTextField();
        CHBValidate = new javax.swing.JCheckBox();
        jLabelParameter = new javax.swing.JLabel();
        TFParameter = new javax.swing.JTextField();
        RBInputResource = new javax.swing.JRadioButton();
        RBInputAction = new javax.swing.JRadioButton();
        jParameterSpecificLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/wizards/Bundle").getString("ACSD_ActionPanel1"));
        jLabelFormName.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "LBL_FormName_mnem").charAt(0));
        jLabelFormName.setLabelFor(CBFormName);
        jLabelFormName.setText(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "LBL_FormName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        add(jLabelFormName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(CBFormName, gridBagConstraints);
        CBFormName.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/wizards/Bundle").getString("ACSD_CBFormName"));

        CBInputAction.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(CBInputAction, gridBagConstraints);
        CBInputAction.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/wizards/Bundle").getString("RB_InputAction"));
        CBInputAction.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/wizards/Bundle").getString("ACSD_CBInputAction"));

        TFInputResource.setText("/");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(TFInputResource, gridBagConstraints);
        TFInputResource.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/wizards/Bundle").getString("RB_InputResource"));
        TFInputResource.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/wizards/Bundle").getString("ACSD_TFInputResource"));

        CHBUseFormBean.setMnemonic(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "LBL_UseFormBean_mnem").charAt(0));
        CHBUseFormBean.setSelected(true);
        CHBUseFormBean.setText(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "CB_UseFormBean"));
        CHBUseFormBean.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        CHBUseFormBean.setMargin(new java.awt.Insets(0, 0, 0, 0));
        CHBUseFormBean.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CHBUseFormBeanItemStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(CHBUseFormBean, gridBagConstraints);
        CHBUseFormBean.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/wizards/Bundle").getString("ACSD_CHBUseFormBean"));

        jButtonBrowse.setMnemonic(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "LBL_BrowseButton_mnem").charAt(0));
        jButtonBrowse.setText(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "LBL_BrowseButton"));
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jButtonBrowse, gridBagConstraints);
        jButtonBrowse.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/wizards/Bundle").getString("ACSD_jButtonBrowse"));

        buttonGroup1.add(RBSession);
        RBSession.setMnemonic(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "RB_Session_mnem").charAt(0));
        RBSession.setSelected(true);
        RBSession.setText(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "RB_Sesson"));
        RBSession.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        RBSession.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanel1.add(RBSession);
        RBSession.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/wizards/Bundle").getString("ACSD_RBSession"));

        buttonGroup1.add(RBRequest);
        RBRequest.setMnemonic(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "RB_Request_mnem").charAt(0));
        RBRequest.setText(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "RB_Request"));
        RBRequest.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        RBRequest.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanel1.add(RBRequest);
        RBRequest.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/wizards/Bundle").getString("ACSD_Request"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(jPanel1, gridBagConstraints);

        jLabelScope.setText(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "LBL_Scope"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        add(jLabelScope, gridBagConstraints);

        jLabelAttribute.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "LBL_Attribute_mnem").charAt(0));
        jLabelAttribute.setLabelFor(TFAttribute);
        jLabelAttribute.setText(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "LBL_Attribute"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        add(jLabelAttribute, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(TFAttribute, gridBagConstraints);
        TFAttribute.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/wizards/Bundle").getString("ACSD_TFAttribute"));

        CHBValidate.setMnemonic(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "CB_Validate_mnem").charAt(0));
        CHBValidate.setSelected(true);
        CHBValidate.setText(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "CB_Validate"));
        CHBValidate.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        CHBValidate.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        add(CHBValidate, gridBagConstraints);
        CHBValidate.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/wizards/Bundle").getString("ACSD_CHBValidate"));

        jLabelParameter.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "LBL_Parameter_mnem").charAt(0));
        jLabelParameter.setLabelFor(TFParameter);
        jLabelParameter.setText(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "LBL_Parameter"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        add(jLabelParameter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(20, 12, 0, 0);
        add(TFParameter, gridBagConstraints);
        TFParameter.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/wizards/Bundle").getString("ACSD_TFParameter"));

        buttonGroup2.add(RBInputResource);
        RBInputResource.setMnemonic(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "RB_InputResource_mnem").charAt(0));
        RBInputResource.setSelected(true);
        RBInputResource.setText(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "RB_InputResource"));
        RBInputResource.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        RBInputResource.setMargin(new java.awt.Insets(0, 0, 0, 0));
        RBInputResource.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                RBInputResourceItemStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        add(RBInputResource, gridBagConstraints);
        RBInputResource.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/wizards/Bundle").getString("ACSD_RBInputResource"));

        buttonGroup2.add(RBInputAction);
        RBInputAction.setMnemonic(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "RB_InputAction_mnem").charAt(0));
        RBInputAction.setText(org.openide.util.NbBundle.getMessage(ActionPanel1Visual.class, "RB_InputAction"));
        RBInputAction.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        RBInputAction.setMargin(new java.awt.Insets(0, 0, 0, 0));
        RBInputAction.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                RBInputActionItemStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        add(RBInputAction, gridBagConstraints);
        RBInputAction.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/wizards/Bundle").getString("RBInputAction"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(jParameterSpecificLabel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
// TODO add your handling code here:
        Project proj = panel.getProject();
        WebModule wm = WebModule.getWebModule(proj.getProjectDirectory());
        org.openide.filesystems.FileObject configFO = wm.getDocumentBase().getFileObject(configFile);
        if (configFO!=null) {
            try {
                DataObject dObj = DataObject.find(configFO);
                if (dObj instanceof StrutsConfigDataObject) {
                    StrutsConfigDataObject config = (StrutsConfigDataObject)dObj;
                    org.netbeans.api.project.SourceGroup[] groups = StrutsConfigUtilities.getDocBaseGroups(configFO);
                    org.openide.filesystems.FileObject fo = BrowseFolders.showDialog(groups);
                    if (fo!=null) {
                        String res = "/"+StrutsConfigUtilities.getResourcePath(groups,fo,'/',true);
                        TFInputResource.setText(res);
                    }
                }
            } catch (DataObjectNotFoundException ex) {}
            catch (java.io.IOException ex) {}
        }
    }//GEN-LAST:event_jButtonBrowseActionPerformed

    private void RBInputActionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_RBInputActionItemStateChanged
// TODO add your handling code here:
        boolean selected = RBInputAction.isSelected();
        TFInputResource.setEditable(!selected);
        jButtonBrowse.setEnabled(!selected);
        CBInputAction.setEnabled(selected);
    }//GEN-LAST:event_RBInputActionItemStateChanged

    private void RBInputResourceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_RBInputResourceItemStateChanged
// TODO add your handling code here:
        boolean selected = RBInputResource.isSelected();
        TFInputResource.setEditable(selected);
        jButtonBrowse.setEnabled(selected);
        CBInputAction.setEnabled(!selected);
    }//GEN-LAST:event_RBInputResourceItemStateChanged

    private void CHBUseFormBeanItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CHBUseFormBeanItemStateChanged
// TODO add your handling code here:
        boolean selected = CHBUseFormBean.isSelected();
        CBFormName.setEnabled(selected);
        RBInputResource.setEnabled(selected);
        RBInputAction.setEnabled(selected);
        if (selected) {
            if (RBInputResource.isSelected()) {
                TFInputResource.setEditable(true);
                jButtonBrowse.setEnabled(true);
            } else {
                CBInputAction.setEnabled(true);
            }
        } else {
            TFInputResource.setEditable(false);
            jButtonBrowse.setEnabled(false);
            CBInputAction.setEnabled(false);
        }
        
        RBSession.setEnabled(selected);
        RBRequest.setEnabled(selected);
        TFAttribute.setEditable(selected);
        CHBValidate.setEnabled(selected);
    }//GEN-LAST:event_CHBUseFormBeanItemStateChanged
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox CBFormName;
    private javax.swing.JComboBox CBInputAction;
    private javax.swing.JCheckBox CHBUseFormBean;
    private javax.swing.JCheckBox CHBValidate;
    private javax.swing.JRadioButton RBInputAction;
    private javax.swing.JRadioButton RBInputResource;
    private javax.swing.JRadioButton RBRequest;
    private javax.swing.JRadioButton RBSession;
    private javax.swing.JTextField TFAttribute;
    private javax.swing.JTextField TFInputResource;
    private javax.swing.JTextField TFParameter;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JLabel jLabelAttribute;
    private javax.swing.JLabel jLabelFormName;
    private javax.swing.JLabel jLabelParameter;
    private javax.swing.JLabel jLabelScope;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jParameterSpecificLabel;
    // End of variables declaration//GEN-END:variables
 
    boolean valid(WizardDescriptor wizardDescriptor) {
        return true;
    }
    
    void read (WizardDescriptor settings) {
        
        // initialize the parameter value
        if (settings.getProperty(WizardProperties.ACTION_PARAMETER)==null) {
            String actionClass = (String)settings.getProperty(WizardProperties.ACTION_SUPERCLASS);
            if (ActionPanelVisual.DISPATCH_ACTION.equals(actionClass)){
                TFParameter.setText("method"); //NOI18N
                jParameterSpecificLabel.setText(NbBundle.getMessage(ActionPanel1Visual.class, "LBL_Dispatch_Action"));//NOI18
            }
            else if (ActionPanelVisual.MAPPING_DISPATCH_ACTION.equals(actionClass)){
                TFParameter.setText("customMethod"); //NOI18
                jParameterSpecificLabel.setText(NbBundle.getMessage(ActionPanel1Visual.class, "LBL_Mapping_Dispatch_Action"));//NOI18
            }
            else if (ActionPanelVisual.LOOKUP_DISPATCH_ACTION.equals(actionClass)){
                TFParameter.setText(""); //NOI18
                jParameterSpecificLabel.setText(NbBundle.getMessage(ActionPanel1Visual.class, "LBL_Lookup_Dispatch_Action"));//NOI18
            }
            else{
                TFParameter.setText(""); //NOI18
                jParameterSpecificLabel.setText("");   //NOI18
            }
        }
        configFile = (String)settings.getProperty(WizardProperties.ACTION_CONFIG_FILE);
        Project proj = panel.getProject();
        WebModule wm = WebModule.getWebModule(proj.getProjectDirectory());
        org.openide.filesystems.FileObject fo = wm.getDocumentBase().getFileObject(configFile);
        if (fo!=null) {
            try {
                DataObject dObj = DataObject.find(fo);
                if (dObj instanceof StrutsConfigDataObject) {
                    StrutsConfigDataObject strutsDO = (StrutsConfigDataObject)dObj;
                    
                    // initialize Input Actions Combo Box
                    List actions = StrutsConfigUtilities.getAllActionsInModule(strutsDO);
                    String[] actionPaths = new String[actions.size()];
                    for (int i=0;i<actionPaths.length;i++) {
                        actionPaths[i]=((Action)actions.get(i)).getAttributeValue("path"); //NOI18N
                        if (actionPaths[i]==null) actionPaths[i]="???"; //NOI18N
                    }
                    CBInputAction.setModel(new javax.swing.DefaultComboBoxModel(actionPaths));
                    
                    // initialize Form Name Combo Box
                    List formBeans = StrutsConfigUtilities.getAllFormBeansInModule(strutsDO);
                    String[] beans = new String[formBeans.size()];
                    for (int i=0;i<beans.length;i++) {
                        beans[i]=((FormBean)formBeans.get(i)).getAttributeValue("name"); //NOI18N
                        if (beans[i]==null) beans[i]="???"; //NOI18N
                    }
                    CBFormName.setModel(new javax.swing.DefaultComboBoxModel(beans));
                    
                    return;
                }
            } catch (DataObjectNotFoundException ex) {}
        }
        CBInputAction.setModel(new javax.swing.DefaultComboBoxModel(new String[]{}));
        CBFormName.setModel(new javax.swing.DefaultComboBoxModel(new String[]{}));
    }

    void store(WizardDescriptor settings) {
        settings.putProperty(WizardProperties.ACTION_FORM_NAME, 
                CHBUseFormBean.isSelected()?CBFormName.getSelectedItem():null);
        settings.putProperty(WizardProperties.ACTION_INPUT, 
                CHBUseFormBean.isSelected()?getInput():null);
        settings.putProperty(WizardProperties.ACTION_SCOPE, 
                CHBUseFormBean.isSelected()?getScope():null);
        settings.putProperty(WizardProperties.ACTION_ATTRIBUTE, 
                CHBUseFormBean.isSelected()?getAttribute():null);
        settings.putProperty(WizardProperties.ACTION_VALIDATE, 
                CHBUseFormBean.isSelected()?isValidate():null);
        // set the parameter property only when other than default
        String param = getParameter();
        if (param!=null) {
            String actionClass = (String)settings.getProperty(WizardProperties.ACTION_SUPERCLASS);
            if (ActionPanelVisual.DISPATCH_ACTION.equals(actionClass) && param.equals("method")) return;    //NOI18N
            if (ActionPanelVisual.MAPPING_DISPATCH_ACTION.equals(actionClass) && param.equals("customMethod")) return;  //NOI18N
            settings.putProperty(WizardProperties.ACTION_PARAMETER, param);
        }
    }
    
    private String getInput() {
        if (RBInputResource.isSelected()) {
            String input=TFInputResource.getText().trim();
            return input.length()==0?null:input;
        } else {
            return (String)CBInputAction.getSelectedItem();
        }
    }
    
    private String getScope() {
        if (RBSession.isSelected()) {
            return "session"; //NOI18N
        } else {
            return "request"; //NOI18N
        }
    }
    
    private Boolean isValidate() {
        return Boolean.valueOf(CHBValidate.isSelected());
    }
    
    private String getAttribute() {
        String attr=TFAttribute.getText().trim();
        return attr.length()==0?null:attr;
    }
    
    private String getParameter() {
        String param=TFParameter.getText().trim();
        return param.length()==0?null:param;
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(ActionPanel1Visual.class);
    }

}
