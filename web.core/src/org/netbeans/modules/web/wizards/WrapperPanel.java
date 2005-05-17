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

package org.netbeans.modules.web.wizards;

import org.openide.util.NbBundle;

/** A single panel for a wizard - the GUI portion.
 *
 * @author  mk115033
 */
public class WrapperPanel extends javax.swing.JPanel {
    
    /** The wizard panel descriptor associated with this GUI panel.
     * If you need to fire state changes or something similar, you can
     * use this handle to do so.
     */
    private WrapperSelection wizardPanel;
    /** Create the wizard panel and set up some basic properties. */
    public WrapperPanel(WrapperSelection wizardPanel) {
        this.wizardPanel=wizardPanel;
        initComponents();
        // Provide a name in the title bar.
        setName(NbBundle.getMessage(WrapperPanel.class, "TITLE_wrapperPanel"));
        /*
        // Optional: provide a special description for this pane.
        // You must have turned on WizardDescriptor.WizardPanel_helpDisplayed
        // (see descriptor in standard iterator template for an example of this).
        try {
            putClientProperty ("WizardPanel_helpURL", // NOI18N
                new URL ("nbresloc:/org/netbeans/modules/web/wizards/WrapperPanelHelp.html")); // NOI18N
        } catch (MalformedURLException mfue) {
            throw new IllegalStateException (mfue.toString ());
        }
         */
        // a11y part
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(WrapperPanel.class, "A11Y_DESC_wrapperPanel"));
        jCheckBox1.getAccessibleContext().setAccessibleName(jCheckBox1.getText());
        jCheckBox1.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(WrapperPanel.class, "A11Y_DESC_wrapperPanel"));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(450, 250));
        setRequestFocusEnabled(false);
        jCheckBox1.setMnemonic(org.openide.util.NbBundle.getMessage(WrapperPanel.class, "LBL_WebModule_Mnemonic").charAt(0));
        jCheckBox1.setText(org.openide.util.NbBundle.getMessage(WrapperPanel.class, "OPT_FilterWrapper"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(40, 10, 10, 0);
        add(jCheckBox1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

    }//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
    
    boolean isWrapper() {
        return jCheckBox1.isSelected();
    }

}
