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

package org.netbeans.modules.apisupport.project.ui.wizard.action;

import java.awt.Component;
import java.awt.Dialog;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.netbeans.modules.apisupport.project.ui.wizard.BasicWizardIterator;
import org.netbeans.modules.apisupport.project.ui.wizard.action.DataModel.Position;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

// XXX s/hardcoded_values/values_from_target_SFS

/**
 * The second panel in the <em>New Action Wizard</em>.
 *
 * @author Martin Krauskopf
 */
final class GUIRegistrationPanel extends BasicWizardIterator.Panel {
    
    private static final ListCellRenderer POSITION_RENDERER = new PositionRenderer();
    
    private DataModel data;
    private boolean firstTime = true;
    
    private final JComponent[] gmiGroup;
    private final JComponent[] toolbarGroup;
    private final JComponent[] shortcutGroup;
    
    public GUIRegistrationPanel(final WizardDescriptor setting, final DataModel data) {
        super(setting);
        this.data = data;
        initComponents();
        readSFS();
        gmiGroup = new JComponent[] {
            menu, menuTxt, menuPosition, menuPositionTxt, menuSeparatorAfter, menuSeparatorBefore
        };
        toolbarGroup = new JComponent[] {
            toolbar, toolbarTxt, toolbarPosition, toolbarPositionTxt
        };
        shortcutGroup = new JComponent[] {
            keyStroke, keyStrokeTxt, keyStrokeChange
        };
    }
    
    protected String getPanelName() {
        return getMessage("LBL_GUIRegistration_Title"); // NOI18N
    }
    
    protected void storeToDataModel() {
        // second panel data (GUI Registration)
        data.setCategory((String) category.getSelectedItem());
        // global menu item
        data.setGlobalMenuItemEnabled(globalMenuItem.isSelected());
        data.setGMIParentMenu(new String[]{(String) menu.getSelectedItem()});
        data.setGMIPosition((Position) menuPosition.getSelectedItem());
        data.setGMISeparatorAfter(menuSeparatorAfter.isSelected());
        data.setGMISeparatorBefore(menuSeparatorBefore.isSelected());
        // global toolbar button
        data.setToolbarEnabled(globalToolbarButton.isSelected());
        data.setToolbar((String) toolbar.getSelectedItem());
        data.setToolbarPosition((Position) toolbarPosition.getSelectedItem());
        // global keyboard shortcut
        data.setKeyboardShortcutEnabled(globalKeyboardShortcut.isSelected());
    }
    
    protected void readFromDataModel() {
        if (data.isAlwaysEnabled()) {
            if (firstTime) {
                initializeGlobalAction();
                firstTime = false;
                setValid(Boolean.TRUE);
            } else {
                checkValidity();
            }
        }
    }
    
    private void initializeGlobalAction() {
        globalMenuItem.setSelected(true);
        
        globalMenuItem.setEnabled(true);
        setGroupEnabled(gmiGroup, globalMenuItem.isSelected());
        
        globalToolbarButton.setEnabled(true);
        setGroupEnabled(toolbarGroup, globalToolbarButton.isSelected());
        
        globalKeyboardShortcut.setEnabled(true);
        setGroupEnabled(shortcutGroup, globalKeyboardShortcut.isSelected());
        // XXX setGroupEnabled(fileTypeCMIGroup, false);
        // XXX setGroupEnabled(editorCMIGroup, false);
    }
    
    private void checkValidity() {
        if (globalKeyboardShortcut.isSelected() && keyStroke.getText().equals("")) { // NOI18N
            setErrorMessage(getMessage("MSG_YouMustSpecifyShortcut")); // NOI18N
        } else {
            setErrorMessage(null);
        }
    }
    
    private void setGroupEnabled(JComponent[] group, boolean enabled) {
        for (int i = 0; i < group.length; i++) {
            if (group[i] != null) {
                group[i].setEnabled(enabled);
            }
        }
    }
    
    private void readSFS() {
        category.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Build", "Edit", "Help", "Tools" }));
        
        menu.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "File" }));
        Position menuPos1 = new Position(
                "org-netbeans-modules-project-ui-NewProject.shadow",
                "org-netbeans-modules-project-ui-NewFile.shadow",
                "New Project",
                "New File");
        Position menuPos2 = new Position(
                "SeparatorNew.instance",
                "org-netbeans-modules-project-ui-NewProject.shadow",
                "New File",
                "New Separator");
        DefaultComboBoxModel menuPosModel = new DefaultComboBoxModel(new Object[] {
            menuPos1, menuPos2
        });
        menuPosition.setModel(menuPosModel);
        
        toolbar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Edit" }));
        Position toolbarPos1 = new Position(
                "org-openide-actions-CutAction.shadow",
                "org-openide-actions-CopyAction.shadow",
                "Cut",
                "Copy");
        Position toolbarPos2 = new Position(
                "org-openide-actions-CopyAction.shadow",
                "org-openide-actions-PasteAction.shadow",
                "Cut",
                "Paste");
        DefaultComboBoxModel toolbarPosModel = new DefaultComboBoxModel(new Object[] {
            toolbarPos1, toolbarPos2
        });
        toolbarPosition.setModel(toolbarPosModel);
        
    }
    
    private static class PositionRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(
                JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Position pos = (Position) value;
            String txtPosition = pos.getBeforeName() + " - " + // NOI18N
                    NbBundle.getMessage(GUIRegistrationPanel.class, "CTL_PositionHere") +
                    " - " + pos.getAfterName(); // NOI18N
            Component c = super.getListCellRendererComponent(
                    list, txtPosition, index, isSelected, cellHasFocus);
            return c;
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        categoryTxt = new javax.swing.JLabel();
        category = new javax.swing.JComboBox();
        globalMenuItem = new javax.swing.JCheckBox();
        menuTxt = new javax.swing.JLabel();
        menu = new javax.swing.JComboBox();
        menuPositionTxt = new javax.swing.JLabel();
        menuPosition = new javax.swing.JComboBox();
        menuSeparatorPanel = new javax.swing.JPanel();
        menuSeparatorBefore = new javax.swing.JCheckBox();
        menuSeparatorAfter = new javax.swing.JCheckBox();
        globalToolbarButton = new javax.swing.JCheckBox();
        toolbarTxt = new javax.swing.JLabel();
        toolbar = new javax.swing.JComboBox();
        toolbarPositionTxt = new javax.swing.JLabel();
        toolbarPosition = new javax.swing.JComboBox();
        globalKeyboardShortcut = new javax.swing.JCheckBox();
        keyStrokeTxt = new javax.swing.JLabel();
        keyStroke = new javax.swing.JLabel();
        keyStrokeChange = new javax.swing.JButton();
        filler = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        categoryTxt.setLabelFor(menuPosition);
        org.openide.awt.Mnemonics.setLocalizedText(categoryTxt, org.openide.util.NbBundle.getMessage(GUIRegistrationPanel.class, "LBL_Category"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(categoryTxt, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(category, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(globalMenuItem, org.openide.util.NbBundle.getMessage(GUIRegistrationPanel.class, "LBL_GlobalMenuItem"));
        globalMenuItem.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 0, 0, 0)));
        globalMenuItem.setMargin(new java.awt.Insets(0, 0, 0, 0));
        globalMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                globalMenuItemActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 3, 0);
        add(globalMenuItem, gridBagConstraints);

        menuTxt.setLabelFor(menuPosition);
        org.openide.awt.Mnemonics.setLocalizedText(menuTxt, org.openide.util.NbBundle.getMessage(GUIRegistrationPanel.class, "LBL_Menu"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 18, 3, 0);
        add(menuTxt, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 12, 3, 0);
        add(menu, gridBagConstraints);

        menuPositionTxt.setLabelFor(menuPosition);
        org.openide.awt.Mnemonics.setLocalizedText(menuPositionTxt, org.openide.util.NbBundle.getMessage(GUIRegistrationPanel.class, "LBL_Position"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 18, 3, 0);
        add(menuPositionTxt, gridBagConstraints);

        menuPosition.setRenderer(POSITION_RENDERER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 12, 3, 0);
        add(menuPosition, gridBagConstraints);

        menuSeparatorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(menuSeparatorBefore, org.openide.util.NbBundle.getMessage(GUIRegistrationPanel.class, "LBL_SeparatorBefore"));
        menuSeparatorBefore.setMargin(new java.awt.Insets(0, 0, 0, 0));
        menuSeparatorPanel.add(menuSeparatorBefore);

        org.openide.awt.Mnemonics.setLocalizedText(menuSeparatorAfter, org.openide.util.NbBundle.getMessage(GUIRegistrationPanel.class, "LBL_SeparatorAfter"));
        menuSeparatorAfter.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 6, 0, 0)));
        menuSeparatorAfter.setMargin(new java.awt.Insets(0, 0, 0, 0));
        menuSeparatorPanel.add(menuSeparatorAfter);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 18, 0, 0);
        add(menuSeparatorPanel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(globalToolbarButton, org.openide.util.NbBundle.getMessage(GUIRegistrationPanel.class, "LBL_GlobalToolbarButton"));
        globalToolbarButton.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 0, 0, 0)));
        globalToolbarButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        globalToolbarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                globalToolbarButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 3, 0);
        add(globalToolbarButton, gridBagConstraints);

        toolbarTxt.setLabelFor(menuPosition);
        org.openide.awt.Mnemonics.setLocalizedText(toolbarTxt, org.openide.util.NbBundle.getMessage(GUIRegistrationPanel.class, "LBL_Toolbar"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 18, 3, 0);
        add(toolbarTxt, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 12, 3, 0);
        add(toolbar, gridBagConstraints);

        toolbarPositionTxt.setLabelFor(menuPosition);
        org.openide.awt.Mnemonics.setLocalizedText(toolbarPositionTxt, org.openide.util.NbBundle.getMessage(GUIRegistrationPanel.class, "LBL_Position"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 18, 3, 0);
        add(toolbarPositionTxt, gridBagConstraints);

        toolbarPosition.setRenderer(POSITION_RENDERER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 12, 3, 0);
        add(toolbarPosition, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(globalKeyboardShortcut, org.openide.util.NbBundle.getMessage(GUIRegistrationPanel.class, "LBL_GlobalKeyboardShortcut"));
        globalKeyboardShortcut.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 0, 0, 0)));
        globalKeyboardShortcut.setMargin(new java.awt.Insets(0, 0, 0, 0));
        globalKeyboardShortcut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                globalKeyboardShortcutActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 3, 0);
        add(globalKeyboardShortcut, gridBagConstraints);

        keyStrokeTxt.setLabelFor(menuPosition);
        org.openide.awt.Mnemonics.setLocalizedText(keyStrokeTxt, org.openide.util.NbBundle.getMessage(GUIRegistrationPanel.class, "LBL_KeyStroke"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 18, 3, 0);
        add(keyStrokeTxt, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 12, 3, 0);
        add(keyStroke, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(keyStrokeChange, org.openide.util.NbBundle.getMessage(GUIRegistrationPanel.class, "CTL_Change"));
        keyStrokeChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyStrokeChangeActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 12, 3, 0);
        add(keyStrokeChange, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(filler, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    private void globalMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_globalMenuItemActionPerformed
        setGroupEnabled(gmiGroup, globalMenuItem.isSelected());
    }//GEN-LAST:event_globalMenuItemActionPerformed
    
    private void globalToolbarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_globalToolbarButtonActionPerformed
        setGroupEnabled(toolbarGroup, globalToolbarButton.isSelected());
    }//GEN-LAST:event_globalToolbarButtonActionPerformed
    
    private void globalKeyboardShortcutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_globalKeyboardShortcutActionPerformed
        setGroupEnabled(shortcutGroup, globalKeyboardShortcut.isSelected());
        checkValidity();
    }//GEN-LAST:event_globalKeyboardShortcutActionPerformed
    
    private void keyStrokeChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keyStrokeChangeActionPerformed
        ShortcutEnterPanel sepPanel = new ShortcutEnterPanel();
        DialogDescriptor dd = new DialogDescriptor(sepPanel, getMessage("LBL_AddShortcutTitle")); // NOI18N
        Dialog addshort = DialogDisplayer.getDefault().createDialog(dd);
        addshort.setVisible(true);
        if (dd.getValue().equals(DialogDescriptor.OK_OPTION)) {
            keyStroke.setText(sepPanel.getKeyText());
            data.setKeyStroke(Utilities.keyToString(sepPanel.getKeyStroke()));
            checkValidity();
        }
    }//GEN-LAST:event_keyStrokeChangeActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox category;
    private javax.swing.JLabel categoryTxt;
    private javax.swing.JLabel filler;
    private javax.swing.JCheckBox globalKeyboardShortcut;
    private javax.swing.JCheckBox globalMenuItem;
    private javax.swing.JCheckBox globalToolbarButton;
    private javax.swing.JLabel keyStroke;
    private javax.swing.JButton keyStrokeChange;
    private javax.swing.JLabel keyStrokeTxt;
    private javax.swing.JComboBox menu;
    private javax.swing.JComboBox menuPosition;
    private javax.swing.JLabel menuPositionTxt;
    private javax.swing.JCheckBox menuSeparatorAfter;
    private javax.swing.JCheckBox menuSeparatorBefore;
    private javax.swing.JPanel menuSeparatorPanel;
    private javax.swing.JLabel menuTxt;
    private javax.swing.JComboBox toolbar;
    private javax.swing.JComboBox toolbarPosition;
    private javax.swing.JLabel toolbarPositionTxt;
    private javax.swing.JLabel toolbarTxt;
    // End of variables declaration//GEN-END:variables
    
}
