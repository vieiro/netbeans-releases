/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 2004-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.jmx.actions.dialog;

import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.JButton;
import org.netbeans.modules.jmx.Introspector.AttributeMethods;
import org.netbeans.modules.jmx.MBeanOperation;
import org.netbeans.modules.jmx.actions.AddAttrAction;
import org.netbeans.modules.jmx.actions.AddOpAction;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Class responsible for the warning message shown when you use Add Operations...
 * popup action in the contextual management menu and there is already an 
 * existing implementation of a specified operation.
 * @author  tl156378
 */
public class AddOperationsInfoPanel extends javax.swing.JPanel {
    
    private ResourceBundle bundle;
    
    private JButton btnOK;
     
    /**
     * 
     * Creates new form Panel.
     * @param mbeanClassName <CODE>String</CODE> name of the MBean to update
     * @param operations <CODE>MBeanOperation[]</CODE> operations of this MBean
     * @param opExist <CODE>boolean[]</CODE> represents if each operation already exists.
     */
    public AddOperationsInfoPanel(String mbeanClassName, MBeanOperation[] operations,
            boolean[] opExist) {
        bundle = NbBundle.getBundle(AddOpAction.class);
        
        // init tags
        
        initComponents();
        
        //init labels
        StringBuffer methodsList = new StringBuffer();
        for (int i = 0; i < operations.length; i ++) {
            if (opExist[i])
                methodsList.append(" - " +operations[i].getName() + "(" + // NOI18N
                        operations[i].getSimpleSignature() + ")\n"); // NOI18N
        }
        
        infoTextArea.setText(
                bundle.getString("LBL_OpMethodsAlreadyExist_begin") +  // NOI18N
                mbeanClassName + ".\n" + // NOI18N
                mbeanClassName + 
                bundle.getString("LBL_OpMethodsAlreadyExist_middle") + // NOI18N
                mbeanClassName + " " +  // NOI18N
                bundle.getString("LBL_OpMethodsAlreadyExist_end") + // NOI18N
                methodsList.toString());
    }
    
    private boolean isAcceptable() {
        return true;
    }
    
    /**
     * Displays a configuration dialog and updates Register MBean options 
     * according to the user's settings.
     * @return <CODE>boolean</CODE> true only if user clicks on Ok button.
     */
    public boolean configure() {
        
        // create and display the dialog:
        String title = bundle.getString("LBL_AddOperationsAction.Title"); // NOI18N

        btnOK = new JButton(bundle.getString("LBL_OK")); // NOI18N
        btnOK.setEnabled(isAcceptable());
        
        Object returned = DialogDisplayer.getDefault().notify(
                new DialogDescriptor (
                        this,
                        title,
                        true,                       //modal
                        new Object[] {btnOK, DialogDescriptor.CANCEL_OPTION},
                        btnOK,                      //initial value
                        DialogDescriptor.DEFAULT_ALIGN,
                        new HelpCtx(AddOpAction.class),
                        (ActionListener) null
                ));
        
        if (returned == btnOK) {
            return true;
        }
        return false;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        infoTextArea = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        infoTextArea.setBackground(javax.swing.UIManager.getDefaults().getColor("inactiveCaption"));
        infoTextArea.setEditable(false);
        infoTextArea.setFont(new java.awt.Font("Arial", 0, 12));
        infoTextArea.setBorder(null);
        infoTextArea.setFocusable(false);
        infoTextArea.setSelectionColor(javax.swing.UIManager.getDefaults().getColor("textText"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 11, 12);
        add(infoTextArea, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea infoTextArea;
    // End of variables declaration//GEN-END:variables
    
}
