/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.java.ui;

import static org.netbeans.modules.java.ui.FmtOptions.*;
import static org.netbeans.modules.java.ui.FmtOptions.CategorySupport.OPTION_ID;
import org.netbeans.modules.java.ui.FmtOptions.CategorySupport;
import org.netbeans.modules.options.editor.spi.PreferencesCustomizer;


/**
 *
 * @author  phrebejk
 */
public class FmtCodeGeneration extends javax.swing.JPanel {
    
    /** Creates new form FmtCodeGeneration */
    public FmtCodeGeneration() {
        initComponents();
        
        preferLongerNamesCheckBox.putClientProperty(OPTION_ID, preferLongerNames);
        fieldPrefixField.putClientProperty(OPTION_ID, fieldNamePrefix);
        fieldSuffixField.putClientProperty(OPTION_ID, fieldNameSuffix);
        staticFieldPrefixField.putClientProperty(OPTION_ID, staticFieldNamePrefix);
        staticFieldSuffixField.putClientProperty(OPTION_ID, staticFieldNameSuffix);
        parameterPrefixField.putClientProperty(OPTION_ID, parameterNamePrefix);
        parameterSuffixField.putClientProperty(OPTION_ID, parameterNameSuffix);
        localVarPrefixField.putClientProperty(OPTION_ID, localVarNamePrefix);
        localVarSuffixField.putClientProperty(OPTION_ID, localVarNameSuffix);
        qualifyFieldAccessCheckBox.putClientProperty(OPTION_ID, qualifyFieldAccess);
        isForBooleanGettersCheckBox.putClientProperty(OPTION_ID, useIsForBooleanGetters);
        addOverrideAnnortationCheckBox.putClientProperty(OPTION_ID, addOverrideAnnotation);
        parametersFinalCheckBox.putClientProperty(OPTION_ID, makeParametersFinal);
        localVarsFinalCheckBox.putClientProperty(OPTION_ID, makeLocalVarsFinal);
        // importsOrderList.putClientProperty(OPTION_ID, importsOrder); XXX
        
    }
    
    public static PreferencesCustomizer.Factory getController() {
        return new CategorySupport.Factory("code-generation", FmtCodeGeneration.class, null);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        preferLongerNamesLabel = new javax.swing.JLabel();
        preferLongerNamesCheckBox = new javax.swing.JCheckBox();
        prefixLabel = new javax.swing.JLabel();
        suffixLabel = new javax.swing.JLabel();
        fieldLabel = new javax.swing.JLabel();
        fieldPrefixField = new javax.swing.JTextField();
        fieldSuffixField = new javax.swing.JTextField();
        staticFieldLabel = new javax.swing.JLabel();
        staticFieldPrefixField = new javax.swing.JTextField();
        staticFieldSuffixField = new javax.swing.JTextField();
        parameterLabel = new javax.swing.JLabel();
        parameterPrefixField = new javax.swing.JTextField();
        parameterSuffixField = new javax.swing.JTextField();
        localVarLabel = new javax.swing.JLabel();
        localVarPrefixField = new javax.swing.JTextField();
        localVarSuffixField = new javax.swing.JTextField();
        miscLabel = new javax.swing.JLabel();
        qualifyFieldAccessCheckBox = new javax.swing.JCheckBox();
        isForBooleanGettersCheckBox = new javax.swing.JCheckBox();
        addOverrideAnnortationCheckBox = new javax.swing.JCheckBox();
        finalLabel = new javax.swing.JLabel();
        parametersFinalCheckBox = new javax.swing.JCheckBox();
        localVarsFinalCheckBox = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        importsOrderList = new javax.swing.JList();
        importUpButton = new javax.swing.JButton();
        importDownButton = new javax.swing.JButton();

        setName(org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_CodeGeneration")); // NOI18N
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(preferLongerNamesLabel, org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_Naming")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        add(preferLongerNamesLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(preferLongerNamesCheckBox, org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_PreferLongerNames")); // NOI18N
        preferLongerNamesCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        preferLongerNamesCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        preferLongerNamesCheckBox.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 4, 0);
        add(preferLongerNamesCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(prefixLabel, org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_Prefix")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 4, 0);
        add(prefixLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(suffixLabel, org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_Suffix")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 4, 0);
        add(suffixLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(fieldLabel, org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_Field")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        add(fieldLabel, gridBagConstraints);

        fieldPrefixField.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 4, 0);
        add(fieldPrefixField, gridBagConstraints);

        fieldSuffixField.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 4, 0);
        add(fieldSuffixField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(staticFieldLabel, org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_StaticField")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        add(staticFieldLabel, gridBagConstraints);

        staticFieldPrefixField.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 4, 0);
        add(staticFieldPrefixField, gridBagConstraints);

        staticFieldSuffixField.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 4, 0);
        add(staticFieldSuffixField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(parameterLabel, org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_Parameter")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        add(parameterLabel, gridBagConstraints);

        parameterPrefixField.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 4, 0);
        add(parameterPrefixField, gridBagConstraints);

        parameterSuffixField.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 4, 0);
        add(parameterSuffixField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(localVarLabel, org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_LocalVariable")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 9, 0);
        add(localVarLabel, gridBagConstraints);

        localVarPrefixField.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 9, 0);
        add(localVarPrefixField, gridBagConstraints);

        localVarSuffixField.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 9, 0);
        add(localVarSuffixField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(miscLabel, org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_Misc")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        add(miscLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(qualifyFieldAccessCheckBox, org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_QualifyFieldAccess")); // NOI18N
        qualifyFieldAccessCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        qualifyFieldAccessCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        qualifyFieldAccessCheckBox.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 4, 0);
        add(qualifyFieldAccessCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(isForBooleanGettersCheckBox, org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_UseIsForBooleanGetters")); // NOI18N
        isForBooleanGettersCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        isForBooleanGettersCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        isForBooleanGettersCheckBox.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 4, 0);
        add(isForBooleanGettersCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(addOverrideAnnortationCheckBox, org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_AddOverrideAnnotation")); // NOI18N
        addOverrideAnnortationCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        addOverrideAnnortationCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        addOverrideAnnortationCheckBox.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 8, 0);
        add(addOverrideAnnortationCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(finalLabel, org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_FinalMofier")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        add(finalLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(parametersFinalCheckBox, org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_ParametersFinal")); // NOI18N
        parametersFinalCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        parametersFinalCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        parametersFinalCheckBox.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 4, 0);
        add(parametersFinalCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(localVarsFinalCheckBox, org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_LocalVariablesFinal")); // NOI18N
        localVarsFinalCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        localVarsFinalCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        localVarsFinalCheckBox.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 8, 0);
        add(localVarsFinalCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_ImportOredering")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        add(jLabel10, gridBagConstraints);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        importsOrderList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(importsOrderList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 4, 0);
        jPanel1.add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(importUpButton, org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_ImportUp")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 1);
        jPanel1.add(importUpButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(importDownButton, org.openide.util.NbBundle.getMessage(FmtCodeGeneration.class, "LBL_ImportDown")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 1);
        jPanel1.add(importDownButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox addOverrideAnnortationCheckBox;
    private javax.swing.JLabel fieldLabel;
    private javax.swing.JTextField fieldPrefixField;
    private javax.swing.JTextField fieldSuffixField;
    private javax.swing.JLabel finalLabel;
    private javax.swing.JButton importDownButton;
    private javax.swing.JButton importUpButton;
    private javax.swing.JList importsOrderList;
    private javax.swing.JCheckBox isForBooleanGettersCheckBox;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel localVarLabel;
    private javax.swing.JTextField localVarPrefixField;
    private javax.swing.JTextField localVarSuffixField;
    private javax.swing.JCheckBox localVarsFinalCheckBox;
    private javax.swing.JLabel miscLabel;
    private javax.swing.JLabel parameterLabel;
    private javax.swing.JTextField parameterPrefixField;
    private javax.swing.JTextField parameterSuffixField;
    private javax.swing.JCheckBox parametersFinalCheckBox;
    private javax.swing.JCheckBox preferLongerNamesCheckBox;
    private javax.swing.JLabel preferLongerNamesLabel;
    private javax.swing.JLabel prefixLabel;
    private javax.swing.JCheckBox qualifyFieldAccessCheckBox;
    private javax.swing.JLabel staticFieldLabel;
    private javax.swing.JTextField staticFieldPrefixField;
    private javax.swing.JTextField staticFieldSuffixField;
    private javax.swing.JLabel suffixLabel;
    // End of variables declaration//GEN-END:variables
    
}
