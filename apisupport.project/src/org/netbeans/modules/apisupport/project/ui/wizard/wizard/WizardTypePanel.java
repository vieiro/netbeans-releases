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

package org.netbeans.modules.apisupport.project.ui.wizard.wizard;

import javax.swing.event.DocumentEvent;
import org.netbeans.modules.apisupport.project.ui.UIUtil;
import org.netbeans.modules.apisupport.project.ui.wizard.BasicWizardIterator;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 * The first panel in the <em>New Wizard Wizard</em>.
 *
 * @author Martin Krauskopf
 */
final class WizardTypePanel extends BasicWizardIterator.Panel {
    
    private DataModel data;
    private boolean firstTime = true;
    private boolean lastStaticValue = true;
    
    public WizardTypePanel(final WizardDescriptor setting, final DataModel data) {
        super(setting);
        this.data = data;
        initComponents();
        initAccessibility();
        putClientProperty("NewFileWizard_Title", getMessage("LBL_WizardWizardTitle"));
        numberOfSteps.getDocument().addDocumentListener(new UIUtil.DocumentAdapter() {
            public void insertUpdate(DocumentEvent e) {
                checkValidity();
            }
        });
    }
    
    protected String getPanelName() {
        return getMessage("LBL_WizardType_Title"); // NOI18N
    }
    
    protected void storeToDataModel() {
        data.setBranching(dynamic.isSelected());
        data.setFileTemplateType(newFile.isSelected());
        data.setNumberOfSteps(getNumberOfSteps());
    }
    
    private int getNumberOfSteps() {
        return Integer.parseInt(numberOfSteps.getText().trim());
    }
    
    protected void readFromDataModel() {
        if (firstTime) {
            firstTime = false;
            setValid(Boolean.FALSE);
        } else {
            checkValidity();
        }
    }
    
    private void checkValidity() {
        if (!isNumberOfStepsValid()) {
            setErrorMessage(getMessage("MSG_IncorrectNumberOfSteps")); // NOI18N
        } else {
            setErrorMessage(null);
        }
    }
    
    private boolean isNumberOfStepsValid() {
        try {
            return getNumberOfSteps() > 0;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    protected HelpCtx getHelp() {
        return new HelpCtx(WizardTypePanel.class);
    }
    
    private void initAccessibility() {
        this.getAccessibleContext().setAccessibleDescription(getMessage("ACS_WizardTypePanel"));
        custom.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_Custom"));
        newFile.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_NewFile"));
        statik.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_Static"));
        dynamic.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_Dynamic"));
        numberOfStepsTxt.getAccessibleContext().setAccessibleDescription(getMessage("ACS_LBL_NumberOfSteps"));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        registrationType = new javax.swing.ButtonGroup();
        wizardSteps = new javax.swing.ButtonGroup();
        registrationTypeTxt = new javax.swing.JLabel();
        custom = new javax.swing.JRadioButton();
        newFile = new javax.swing.JRadioButton();
        wizardStepsTxt = new javax.swing.JLabel();
        statik = new javax.swing.JRadioButton();
        dynamic = new javax.swing.JRadioButton();
        numberOfStepsTxt = new javax.swing.JLabel();
        numberOfSteps = new javax.swing.JTextField();
        filler = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(registrationTypeTxt, org.openide.util.NbBundle.getMessage(WizardTypePanel.class, "LBL_RegistrationType"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(registrationTypeTxt, gridBagConstraints);

        registrationType.add(custom);
        custom.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(custom, org.openide.util.NbBundle.getMessage(WizardTypePanel.class, "CTL_Custom"));
        custom.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        custom.setMargin(new java.awt.Insets(0, 0, 0, 0));
        custom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 18, 0, 0);
        add(custom, gridBagConstraints);

        registrationType.add(newFile);
        org.openide.awt.Mnemonics.setLocalizedText(newFile, org.openide.util.NbBundle.getMessage(WizardTypePanel.class, "CTL_NewFile"));
        newFile.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        newFile.setMargin(new java.awt.Insets(0, 0, 0, 0));
        newFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 18, 0, 0);
        add(newFile, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(wizardStepsTxt, org.openide.util.NbBundle.getMessage(WizardTypePanel.class, "LBL_WizardSteps"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(24, 0, 0, 0);
        add(wizardStepsTxt, gridBagConstraints);

        wizardSteps.add(statik);
        statik.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(statik, org.openide.util.NbBundle.getMessage(WizardTypePanel.class, "CTL_Static"));
        statik.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        statik.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 18, 0, 0);
        add(statik, gridBagConstraints);

        wizardSteps.add(dynamic);
        org.openide.awt.Mnemonics.setLocalizedText(dynamic, org.openide.util.NbBundle.getMessage(WizardTypePanel.class, "CTL_Dynamic"));
        dynamic.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        dynamic.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 18, 24, 0);
        add(dynamic, gridBagConstraints);

        numberOfStepsTxt.setLabelFor(numberOfSteps);
        org.openide.awt.Mnemonics.setLocalizedText(numberOfStepsTxt, org.openide.util.NbBundle.getMessage(WizardTypePanel.class, "LBL_NumberOfSteps"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        add(numberOfStepsTxt, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 3, 0);
        add(numberOfSteps, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(filler, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
	private void typeChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeChanged
        boolean isCustom = custom.isSelected();
        statik.setEnabled(isCustom);
        dynamic.setEnabled(isCustom);
        if (isCustom) {
            statik.setSelected(lastStaticValue);
        } else {
            lastStaticValue = statik.isSelected();
            dynamic.setSelected(true);
        }
	}//GEN-LAST:event_typeChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton custom;
    private javax.swing.JRadioButton dynamic;
    private javax.swing.JLabel filler;
    private javax.swing.JRadioButton newFile;
    private javax.swing.JTextField numberOfSteps;
    private javax.swing.JLabel numberOfStepsTxt;
    private javax.swing.ButtonGroup registrationType;
    private javax.swing.JLabel registrationTypeTxt;
    private javax.swing.JRadioButton statik;
    private javax.swing.ButtonGroup wizardSteps;
    private javax.swing.JLabel wizardStepsTxt;
    // End of variables declaration//GEN-END:variables
    
}
