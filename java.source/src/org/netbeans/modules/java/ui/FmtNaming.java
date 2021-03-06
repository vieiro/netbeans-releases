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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.prefs.Preferences;

import javax.lang.model.element.Modifier;
import javax.swing.JPanel;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import javax.lang.model.type.TypeKind;

import org.netbeans.api.java.source.CodeStyle;
import org.netbeans.api.java.source.CodeStyleUtils;
import org.netbeans.api.java.source.GeneratorUtilities;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import static org.netbeans.modules.java.ui.FmtOptions.*;
import static org.netbeans.modules.java.ui.CategorySupport.OPTION_ID;
import org.netbeans.modules.options.editor.spi.PreferencesCustomizer;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.openide.util.NbBundle;

/**
 *
 * @author Ralph Benjamin Ruijs <ralphbenjamin@netbeans.org>
 */
public class FmtNaming extends javax.swing.JPanel implements Runnable {

    /**
     * Creates new form FmtCodeGeneration
     */
    public FmtNaming() {
        initComponents();
        preferLongerNamesCheckBox.putClientProperty(OPTION_ID, preferLongerNames);
        preferLongerNamesCheckBox.setVisible(false);
        fieldPrefixField.putClientProperty(OPTION_ID, fieldNamePrefix);
        fieldSuffixField.putClientProperty(OPTION_ID, fieldNameSuffix);
        staticFieldPrefixField.putClientProperty(OPTION_ID, staticFieldNamePrefix);
        staticFieldSuffixField.putClientProperty(OPTION_ID, staticFieldNameSuffix);
        parameterPrefixField.putClientProperty(OPTION_ID, parameterNamePrefix);
        parameterSuffixField.putClientProperty(OPTION_ID, parameterNameSuffix);
        localVarPrefixField.putClientProperty(OPTION_ID, localVarNamePrefix);
        localVarSuffixField.putClientProperty(OPTION_ID, localVarNameSuffix);
    }

    public static PreferencesCustomizer.Factory getController() {
        return new PreferencesCustomizer.Factory() {
            public PreferencesCustomizer create(Preferences preferences) {
                NamingCategorySupport support = new NamingCategorySupport(preferences, new FmtNaming());
                ((Runnable) support.panel).run();
                return support;
            }
        };
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        namingConventionsLabel = new javax.swing.JLabel();
        preferLongerNamesCheckBox = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
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
        localVarSuffixField = new javax.swing.JTextField();
        localVarPrefixField = new javax.swing.JTextField();

        setName(org.openide.util.NbBundle.getMessage(FmtNaming.class, "LBL_Naming")); // NOI18N
        setOpaque(false);

        org.openide.awt.Mnemonics.setLocalizedText(namingConventionsLabel, org.openide.util.NbBundle.getMessage(FmtNaming.class, "LBL_gen_Naming")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(preferLongerNamesCheckBox, org.openide.util.NbBundle.getMessage(FmtNaming.class, "LBL_gen_PreferLongerNames")); // NOI18N
        preferLongerNamesCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        preferLongerNamesCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        preferLongerNamesCheckBox.setOpaque(false);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(prefixLabel, org.openide.util.NbBundle.getMessage(FmtNaming.class, "LBL_gen_Prefix")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 4, 0);
        jPanel1.add(prefixLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(suffixLabel, org.openide.util.NbBundle.getMessage(FmtNaming.class, "LBL_gen_Suffix")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 4, 0);
        jPanel1.add(suffixLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(fieldLabel, org.openide.util.NbBundle.getMessage(FmtNaming.class, "LBL_gen_Field")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(fieldLabel, gridBagConstraints);

        fieldPrefixField.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 0);
        jPanel1.add(fieldPrefixField, gridBagConstraints);

        fieldSuffixField.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 0);
        jPanel1.add(fieldSuffixField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(staticFieldLabel, org.openide.util.NbBundle.getMessage(FmtNaming.class, "LBL_gen_StaticField")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(staticFieldLabel, gridBagConstraints);

        staticFieldPrefixField.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 0);
        jPanel1.add(staticFieldPrefixField, gridBagConstraints);

        staticFieldSuffixField.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 0);
        jPanel1.add(staticFieldSuffixField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(parameterLabel, org.openide.util.NbBundle.getMessage(FmtNaming.class, "LBL_gen_Parameter")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(parameterLabel, gridBagConstraints);

        parameterPrefixField.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 0);
        jPanel1.add(parameterPrefixField, gridBagConstraints);

        parameterSuffixField.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 0);
        jPanel1.add(parameterSuffixField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(localVarLabel, org.openide.util.NbBundle.getMessage(FmtNaming.class, "LBL_gen_LocalVariable")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel1.add(localVarLabel, gridBagConstraints);

        localVarSuffixField.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 0, 0);
        jPanel1.add(localVarSuffixField, gridBagConstraints);

        localVarPrefixField.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 0, 0);
        jPanel1.add(localVarPrefixField, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(namingConventionsLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(preferLongerNamesCheckBox)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(namingConventionsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(preferLongerNamesCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fieldLabel;
    private javax.swing.JTextField fieldPrefixField;
    private javax.swing.JTextField fieldSuffixField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel localVarLabel;
    private javax.swing.JTextField localVarPrefixField;
    private javax.swing.JTextField localVarSuffixField;
    private javax.swing.JLabel namingConventionsLabel;
    private javax.swing.JLabel parameterLabel;
    private javax.swing.JTextField parameterPrefixField;
    private javax.swing.JTextField parameterSuffixField;
    private javax.swing.JCheckBox preferLongerNamesCheckBox;
    private javax.swing.JLabel prefixLabel;
    private javax.swing.JLabel staticFieldLabel;
    private javax.swing.JTextField staticFieldPrefixField;
    private javax.swing.JTextField staticFieldSuffixField;
    private javax.swing.JLabel suffixLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void run() {
        
    }

    private static final class NamingCategorySupport extends CategorySupport.DocumentCategorySupport {

        private NamingCategorySupport(Preferences preferences, JPanel panel) {
            super(preferences, "naming", panel, NbBundle.getMessage(FmtNaming.class, "SAMPLE_Naming"), //NOI18N
                  new String[]{FmtOptions.blankLinesBeforeFields, "1"}); //NOI18N
        }

        protected void doModification(ResultIterator resultIterator) throws Exception {
            final CodeStyle codeStyle = codeStyleProducer.create(previewPrefs);
            WorkingCopy copy = WorkingCopy.get(resultIterator.getParserResult());
            copy.toPhase(Phase.RESOLVED);
            TreeMaker tm = copy.getTreeMaker();
            GeneratorUtilities gu = GeneratorUtilities.get(copy);
            CompilationUnitTree cut = copy.getCompilationUnit();
            ClassTree ct = (ClassTree) cut.getTypeDecls().get(0);
            List<Tree> members = new ArrayList<Tree>();
            String name = CodeStyleUtils.addPrefixSuffix("name",
                    codeStyle.getFieldNamePrefix(),
                    codeStyle.getFieldNameSuffix());
            VariableTree field = tm.Variable(tm.Modifiers(Collections.singleton(Modifier.PRIVATE)), name, tm.Type("String"), null);
            members.add(field);
            String cond = CodeStyleUtils.addPrefixSuffix("cond",
                    codeStyle.getFieldNamePrefix(),
                    codeStyle.getFieldNameSuffix());
            VariableTree booleanField = tm.Variable(tm.Modifiers(Collections.singleton(Modifier.PRIVATE)), cond, tm.PrimitiveType(TypeKind.BOOLEAN), null);
            members.add(booleanField);
            members.add(gu.createConstructor(ct, Collections.singletonList(field)));
            members.add(gu.createGetter(field));
            members.add(gu.createSetter(ct, field));
            members.add(gu.createGetter(booleanField));
            members.add(gu.createSetter(ct, booleanField));
            ModifiersTree mods = tm.Modifiers(EnumSet.of(Modifier.PRIVATE, Modifier.STATIC));
            ClassTree nested = tm.Class(mods, "Nested", Collections.<TypeParameterTree>emptyList(), null, Collections.<Tree>emptyList(), Collections.<Tree>emptyList()); //NOI18N
            members.add(nested);
            IdentifierTree nestedId = tm.Identifier("Nested"); //NOI18N
            String instance = CodeStyleUtils.addPrefixSuffix("instance",
                    codeStyle.getStaticFieldNamePrefix(),
                    codeStyle.getStaticFieldNameSuffix());
            VariableTree staticField = tm.Variable(mods, instance, nestedId, null); //NOI18N
            members.add(staticField);
            members.add(gu.createGetter(staticField));
            members.add(gu.createSetter(ct, staticField));
            ClassTree newCT = gu.insertClassMembers(ct, members);
            copy.rewrite(ct, newCT);
        }
    }
}
