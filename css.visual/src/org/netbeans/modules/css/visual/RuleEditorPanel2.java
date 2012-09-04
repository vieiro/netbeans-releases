/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

/*
 * CssRuleCreateActionDialog.java
 *
 * Created on February 3, 2005, 9:16 AM
 */

package org.netbeans.modules.css.visual;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.netbeans.modules.html.editor.lib.api.HtmlVersion;
import org.netbeans.modules.html.editor.lib.api.model.HtmlModel;
import org.netbeans.modules.html.editor.lib.api.model.HtmlModelFactory;
import org.netbeans.modules.html.editor.lib.api.model.HtmlTag;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Dialog for creating the Style Rule
 * @author  Winston Prakash
 * @version 1.0
 */
public class RuleEditorPanel2 extends javax.swing.JPanel {

    private JDialog dialog;
    private DialogDescriptor dlg = null;

    private static final String ELEMENT_TYPE = "elelment"; //NOI18N
    private static final String CLASS_TYPE = "class"; //NOI18N
    private static final String ELEMENT_ID_TYPE = "element_id"; //NOI18N

    private static final String NONE = "<None>";  //NOI18N

    DefaultListModel selectedRules = new DefaultListModel();

    private String styleRuleName = "";

    /** Creates new form CssRuleCreateActionDialog */
    public RuleEditorPanel2() {
        initComponents();
        String[] htmlTags = getHtmlTagNames();

        // Optional prefix
        DefaultComboBoxModel htmlTagsModel1 = new DefaultComboBoxModel();
        htmlTagsModel1.addElement(NONE);
        htmlTagsModel1.addElement("a:link");
        htmlTagsModel1.addElement("a:visited");
        htmlTagsModel1.addElement("a:hover");
        htmlTagsModel1.addElement("a:active");
        for( int i=0; i< htmlTags.length; i++){
            htmlTagsModel1.addElement(htmlTags[i]);
        }

        DefaultComboBoxModel htmlTagsModel = new DefaultComboBoxModel();
        //htmlTagsModel.addElement(NONE);
        for( int i=0; i< htmlTags.length; i++){
            htmlTagsModel.addElement(htmlTags[i]);
        }
        selectElementComboBox.setModel(htmlTagsModel);
        classPrefixComboBox.setModel(htmlTagsModel1);
        ruleHierarchyList.setModel(selectedRules);
        removeRuleButton.setEnabled(false);
    }

    private String[] getHtmlTagNames() {
        HtmlModel model = HtmlModelFactory.getModel(HtmlVersion.HTML5);
        Collection<HtmlTag> tags = model.getAllTags();
        Collection<String> tagNames = new ArrayList<String>();
        for(HtmlTag tag : tags) {
            tagNames.add(tag.getName());
        }
        return tagNames.toArray(new String[]{});
    }
    
    public void showDialog(){
        // Add a listener to the dialog's buttons
        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource() == DialogDescriptor.OK_OPTION) {
                    styleRuleName = previewTextField.getText().trim();
                    // As Jeff pointed out even if user has not added
                    // any value to the right hand side the values selected
                    // in the left hand side should be used
                    if(styleRuleName.equals("")){
                        String selectionType = selectRuleButtonGroup.getSelection().getActionCommand();
                        styleRuleName = getRule(selectionType);
                    }
                    dialog.setVisible(false);
                }
            }
        };
        dlg = new DialogDescriptor(this, NbBundle.getMessage(RuleEditorPanel2.class, "STYLE_RULE_EDITOR_TITLE"), true, listener);
        dlg.setHelpCtx(new HelpCtx("projrave_ui_elements_css_create_style_rule")); // NOI18N

        dialog = (JDialog) DialogDisplayer.getDefault().createDialog(dlg);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
    }
    
    public String getStyleRuleName(){
        return styleRuleName;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        selectRuleButtonGroup = new javax.swing.ButtonGroup();
        selectElementRadioButton = new javax.swing.JRadioButton();
        selectElementComboBox = new javax.swing.JComboBox();
        selectClassRadioButton = new javax.swing.JRadioButton();
        selectClassTextField = new javax.swing.JTextField();
        selectElelmentIdRadioButton = new javax.swing.JRadioButton();
        selectElementIdTextField = new javax.swing.JTextField();
        classPrefixComboBox = new javax.swing.JComboBox();
        classPrefixSeparator = new javax.swing.JLabel();
        addRuleButton = new javax.swing.JButton();
        removeRuleButton = new javax.swing.JButton();
        previewLable = new javax.swing.JLabel();
        previewTextField = new javax.swing.JTextField();
        ruleHierarchyScroll = new javax.swing.JScrollPane();
        ruleHierarchyList = new javax.swing.JList();
        moveRuleUpButton = new javax.swing.JButton();
        moveRuleDownButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();

        selectRuleButtonGroup.add(selectElementRadioButton);
        selectElementRadioButton.setMnemonic(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "ELEMENT_RULE_TYPE_MNEMONIC").charAt(0));
        selectElementRadioButton.setText(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "HTML_ELELEMT")); // NOI18N
        selectElementRadioButton.setActionCommand(ELEMENT_TYPE);
        selectElementRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectRuleActionPerformed(evt);
            }
        });

        selectElementComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "a", "abbr" }));
        selectElementComboBox.setEnabled(false);

        selectRuleButtonGroup.add(selectClassRadioButton);
        selectClassRadioButton.setMnemonic(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "CLASS_RULE_TYPE_MNEMONIC").charAt(0));
        selectClassRadioButton.setSelected(true);
        selectClassRadioButton.setText(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "CLASS_NAME_LBL")); // NOI18N
        selectClassRadioButton.setActionCommand(CLASS_TYPE);
        selectClassRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectRuleActionPerformed(evt);
            }
        });

        selectClassTextField.setColumns(15);
        selectClassTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                selectClassTextFieldKeyTyped(evt);
            }
        });

        selectRuleButtonGroup.add(selectElelmentIdRadioButton);
        selectElelmentIdRadioButton.setMnemonic(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "ELEMENT_ID_RULE_TYPE_MNEMONIC").charAt(0));
        selectElelmentIdRadioButton.setText(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "ELEMENT_ID_LBL")); // NOI18N
        selectElelmentIdRadioButton.setActionCommand(ELEMENT_ID_TYPE);
        selectElelmentIdRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectRuleActionPerformed(evt);
            }
        });

        selectElementIdTextField.setEnabled(false);

        classPrefixComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "abbr" }));
        classPrefixComboBox.setToolTipText(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "OPTIONAL_ELEMENT_TOOLTIP")); // NOI18N
        classPrefixComboBox.setEnabled(false);

        classPrefixSeparator.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        classPrefixSeparator.setText(".");

        addRuleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/css/resources/icons/plus.gif"))); // NOI18N
        addRuleButton.setMnemonic(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "ADD_RULE_BUTTON_MNEMONIC").charAt(0));
        addRuleButton.setText(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "ADD_RULE_LBL")); // NOI18N
        addRuleButton.setToolTipText(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "ADD_RULE_TOOL_TIP")); // NOI18N
        addRuleButton.setActionCommand(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "ADD_RULE_LBL")); // NOI18N
        addRuleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRuleButtonActionPerformed(evt);
            }
        });

        removeRuleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/css/resources/icons/minus.gif"))); // NOI18N
        removeRuleButton.setMnemonic(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "REMOVE_RULE_BUTTON_MNEMONIC").charAt(0));
        removeRuleButton.setText(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "REMOVE_RULE_LBL")); // NOI18N
        removeRuleButton.setToolTipText(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "REMOVE_RULE_TOOL_TIP")); // NOI18N
        removeRuleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeRuleButtonActionPerformed(evt);
            }
        });

        previewLable.setLabelFor(previewTextField);
        org.openide.awt.Mnemonics.setLocalizedText(previewLable, org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "RULE_PREVIEW_LABEL")); // NOI18N

        previewTextField.setEnabled(false);

        ruleHierarchyScroll.setPreferredSize(new java.awt.Dimension(150, 200));
        ruleHierarchyScroll.setViewportView(ruleHierarchyList);
        ruleHierarchyList.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "STYLE_RULE_LIST_ACCESSIBLE_NAME")); // NOI18N
        ruleHierarchyList.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "STYLE_RULE_LIST_ACCESSIBLE_DESC")); // NOI18N

        moveRuleUpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/css/resources/icons/up.gif"))); // NOI18N
        moveRuleUpButton.setMnemonic(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "UP_RULE_BUTTON_MNEMONIC").charAt(0));
        moveRuleUpButton.setText(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "MOVE_RULE_UP_LBL")); // NOI18N
        moveRuleUpButton.setToolTipText(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "UP_RULE_BUTTON_TOOLTIP")); // NOI18N
        moveRuleUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveRuleUpActionPerformed(evt);
            }
        });

        moveRuleDownButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/css/resources/icons/down.gif"))); // NOI18N
        moveRuleDownButton.setMnemonic(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "DOWN_RULE_BUTTON_MNEMONIC").charAt(0));
        moveRuleDownButton.setText(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "MOVE_RULE_DOWN_LBL")); // NOI18N
        moveRuleDownButton.setToolTipText(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "DOWN_RULE_BUTTON_TOOLTIP")); // NOI18N
        moveRuleDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveRuleDownActionPerformed(evt);
            }
        });

        jLabel1.setText("Selector Type");

        jLabel2.setText("Selectors");

        jLabel3.setText("At-Rule");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(selectElementRadioButton)
                                    .addComponent(selectClassRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(selectElelmentIdRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(classPrefixComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(selectElementComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(classPrefixSeparator)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(selectClassTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(selectElementIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(previewLable)
                            .addComponent(previewTextField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ruleHierarchyScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(addRuleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(removeRuleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(moveRuleUpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(moveRuleDownButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(268, 268, 268)
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(selectClassRadioButton)
                            .addComponent(classPrefixComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(classPrefixSeparator)
                            .addComponent(selectClassTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(selectElementRadioButton)
                            .addComponent(selectElementComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(selectElelmentIdRadioButton)
                            .addComponent(selectElementIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(previewLable, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(previewTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ruleHierarchyScroll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(removeRuleButton)
                            .addComponent(moveRuleDownButton)
                            .addComponent(moveRuleUpButton)
                            .addComponent(addRuleButton, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );

        selectElementRadioButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "HTML_ELEMENT_RULE_TYPE_ACCESSIBLE_DESCRIPTION")); // NOI18N
        selectElementComboBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "HTML_ELEMENT_ACCESSIBLE_NAME")); // NOI18N
        selectElementComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "HTML_ELEMENT_ACCESSIBLE_DESC")); // NOI18N
        selectClassRadioButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "CLASS_RULE_TYPE_ACCESSIBLE_DESCRIPTION")); // NOI18N
        selectClassTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "CLASS_TEXT_FIELD_ACCESSIBLE_NAME")); // NOI18N
        selectClassTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "CLASS_TEXT_FIELD_ACCESSIBLE_DESC")); // NOI18N
        selectElelmentIdRadioButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "ELEMENT_ID_LBL")); // NOI18N
        selectElelmentIdRadioButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "ELEMENT_ID_RULE_TYPE_ACCESSIBLE_DESCRIPTION")); // NOI18N
        selectElementIdTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "ELEMENT_ID_TEXTFIELD_ACCESSIBLE_NAME")); // NOI18N
        selectElementIdTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "ELEMENT_ID_TEXTFIELD_ACCESSIBLE_DESC")); // NOI18N
        classPrefixComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "OPTIONAL_ELEMENT_ACCESSIBLE_DESC")); // NOI18N
        previewTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RuleEditorPanel2.class, "PREVIEW_LABEL_ACCESSIBLE_DESC")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    private void selectClassTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_selectClassTextFieldKeyTyped
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                if (!selectClassTextField.getText().equals("")){
                    classPrefixComboBox.setEnabled(true);
                }else{
                    classPrefixComboBox.setEnabled(false);
                }
            }
        });
    }//GEN-LAST:event_selectClassTextFieldKeyTyped
    
    private void moveRuleDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveRuleDownActionPerformed
        int index = ruleHierarchyList.getSelectedIndex();
        if(index >=0 && index < selectedRules.getSize()){
            Object currentObject = selectedRules.get(index);
            int nextIndex = index+1;
            if(nextIndex < selectedRules.getSize()) {
                Object prevObject = selectedRules.get(nextIndex);
                selectedRules.setElementAt(currentObject, index+1);
                selectedRules.setElementAt(prevObject, index);
                ruleHierarchyList.setSelectedIndex(index+1);
                resetRuleHierarchy();
            }
        }
    }//GEN-LAST:event_moveRuleDownActionPerformed
    
    private void moveRuleUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveRuleUpActionPerformed
        int index = ruleHierarchyList.getSelectedIndex();
        if(index > 0){
            Object currentObject = selectedRules.get(index);
            Object prevObject = selectedRules.get(index-1);
            selectedRules.setElementAt(currentObject, index-1);
            selectedRules.setElementAt(prevObject, index);
            ruleHierarchyList.setSelectedIndex(index-1);
            resetRuleHierarchy();
        }
    }//GEN-LAST:event_moveRuleUpActionPerformed
    
    private void removeRuleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeRuleButtonActionPerformed
        Object[] selections = ruleHierarchyList.getSelectedValues();
        for(int i=0; i< selections.length ;i++){
            if (selectedRules.contains(selections[i])){
                selectedRules.removeElement(selections[i]);
            }
        }
        if(!selectedRules.isEmpty()) {
            ruleHierarchyList.setSelectedIndex(0);
        }else{
            removeRuleButton.setEnabled(false);
        }
        resetRuleHierarchy();
    }//GEN-LAST:event_removeRuleButtonActionPerformed
    
    private void selectRuleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectRuleActionPerformed
        String ruleType = evt.getActionCommand();
        if(ruleType.equals(ELEMENT_TYPE)){
            selectElementComboBox.setEnabled(true);
            classPrefixComboBox.setEnabled(false);
            selectClassTextField.setEnabled(false);
            selectElementIdTextField.setEnabled(false);
        }else if(ruleType.equals(CLASS_TYPE)){
            selectElementComboBox.setEnabled(false);
            classPrefixComboBox.setEnabled(true);
            selectClassTextField.setEnabled(true);
            selectElementIdTextField.setEnabled(false);
        }else if(ruleType.equals(ELEMENT_ID_TYPE)){
            selectElementComboBox.setEnabled(false);
            classPrefixComboBox.setEnabled(false);
            selectClassTextField.setEnabled(false);
            selectElementIdTextField.setEnabled(true);
        }
        resetRuleHierarchy();
    }//GEN-LAST:event_selectRuleActionPerformed
    
    private void addRuleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRuleButtonActionPerformed
        String ruleType = selectRuleButtonGroup.getSelection().getActionCommand();
        String rule = null;
        if(ruleType.equals(ELEMENT_TYPE)){
            rule = (String) selectElementComboBox.getSelectedItem();
            if(rule.equals(NONE)) rule = null;
        }else if(ruleType.equals(CLASS_TYPE)){
            if(!selectClassTextField.getText().trim().equals("")){
                String rulePrefix = (String) classPrefixComboBox.getSelectedItem();
                rule = "." + selectClassTextField.getText().trim();
                if(!rulePrefix.equals(NONE)){
                    rule = rulePrefix  + rule;
                }
            }
        }else if(ruleType.equals(ELEMENT_ID_TYPE)){
            if(!selectElementIdTextField.getText().trim().equals("")){
                rule = "#" + selectElementIdTextField.getText().trim();;
            }
        }
        if((rule != null) && (!selectedRules.contains(rule))){
            selectedRules.addElement(rule);
            ruleHierarchyList.setSelectedValue(rule,true);
            removeRuleButton.setEnabled(true);
        }
        resetRuleHierarchy();
    }//GEN-LAST:event_addRuleButtonActionPerformed
    
    private String getRule(String ruleType){
        String rule = null;
        if(ruleType.equals(ELEMENT_TYPE)){
            rule = (String) selectElementComboBox.getSelectedItem();
            if(rule.equals(NONE)) rule = null;
        }else if(ruleType.equals(CLASS_TYPE)){
            if(!selectClassTextField.getText().trim().equals("")){
                String rulePrefix = (String) classPrefixComboBox.getSelectedItem();
                rule = "." + selectClassTextField.getText().trim();
                if(!rulePrefix.equals(NONE)){
                    rule = rulePrefix  + rule;
                }
            }
        }else if(ruleType.equals(ELEMENT_ID_TYPE)){
            if(!selectElementIdTextField.getText().trim().equals("")){
                rule = "#" + selectElementIdTextField.getText().trim();;
            }
        }
        return rule;
    }
    
    private void resetRuleHierarchy(){
        StringBuffer ruleSetBuf = new StringBuffer();
        for(int i = 0; i < selectedRules.size(); i++){
            String ruleName = ((String) selectedRules.get(i)).trim();
            ruleSetBuf.append(ruleName);
            if(i < selectedRules.size()-1 )ruleSetBuf.append(" ");
        }
        previewTextField.setText(ruleSetBuf.toString());
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addRuleButton;
    private javax.swing.JComboBox classPrefixComboBox;
    private javax.swing.JLabel classPrefixSeparator;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton moveRuleDownButton;
    private javax.swing.JButton moveRuleUpButton;
    private javax.swing.JLabel previewLable;
    private javax.swing.JTextField previewTextField;
    private javax.swing.JButton removeRuleButton;
    private javax.swing.JList ruleHierarchyList;
    private javax.swing.JScrollPane ruleHierarchyScroll;
    private javax.swing.JRadioButton selectClassRadioButton;
    private javax.swing.JTextField selectClassTextField;
    private javax.swing.JRadioButton selectElelmentIdRadioButton;
    private javax.swing.JComboBox selectElementComboBox;
    private javax.swing.JTextField selectElementIdTextField;
    private javax.swing.JRadioButton selectElementRadioButton;
    private javax.swing.ButtonGroup selectRuleButtonGroup;
    // End of variables declaration//GEN-END:variables
}
