/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.hints.pom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.prefs.Preferences;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

/**
 *
 * @author mkleint
 */
public class JavaNetRepositoryErrorCustomizer extends javax.swing.JPanel {
    private Preferences preferences;

    /** Creates new form ReleaseVersionErrorCustomizer */
    public JavaNetRepositoryErrorCustomizer(Preferences prefs) {
        initComponents();
        this.preferences = prefs;
        boolean selected = preferences.getBoolean(JavaNetRepositoryError.PROP_SELECTED, true);
        rbSelected.setSelected(selected);
        rbAny.setSelected(!selected);
        String values = preferences.get(JavaNetRepositoryError.PROP_URLS, JavaNetRepositoryError.DEFAULT_URLS);
        String[] vals = values.split("([\\s])+");
        StringBuilder sb = new StringBuilder();
        for (String v : vals) {
            sb.append(v.trim()).append("\n");
        }
        taSelected.setText(sb.toString());
        if (rbAny.isSelected()) {
            taSelected.setEnabled(false);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new ButtonGroup();
        rbAny = new JRadioButton();
        rbSelected = new JRadioButton();
        jScrollPane1 = new JScrollPane();
        taSelected = new JTextArea();
        jLabel1 = new JLabel();

        buttonGroup1.add(rbAny);
        Mnemonics.setLocalizedText(rbAny, NbBundle.getMessage(JavaNetRepositoryErrorCustomizer.class, "JavaNetRepositoryErrorCustomizer.rbAny.text")); // NOI18N
        rbAny.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                rbAnyActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbSelected);
        Mnemonics.setLocalizedText(rbSelected, NbBundle.getMessage(JavaNetRepositoryErrorCustomizer.class, "JavaNetRepositoryErrorCustomizer.rbSelected.text")); // NOI18N
        rbSelected.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                rbSelectedActionPerformed(evt);
            }
        });

        taSelected.setColumns(20);
        taSelected.setRows(5);
        taSelected.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent evt) {
                taSelectedKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(taSelected);

        Mnemonics.setLocalizedText(jLabel1, NbBundle.getMessage(JavaNetRepositoryErrorCustomizer.class, "JavaNetRepositoryErrorCustomizer.jLabel1.text")); // NOI18N
        jLabel1.setToolTipText(NbBundle.getMessage(JavaNetRepositoryErrorCustomizer.class, "JavaNetRepositoryErrorCustomizer.jLabel1.toolTipText")); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(rbAny)
                            .addComponent(rbSelected)
                            .addComponent(jLabel1))
                        .addGap(0, 124, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbAny)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(rbSelected)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void rbAnyActionPerformed(ActionEvent evt) {//GEN-FIRST:event_rbAnyActionPerformed
        preferences.putBoolean(JavaNetRepositoryError.PROP_SELECTED, false);
        taSelected.setEnabled(false);
    }//GEN-LAST:event_rbAnyActionPerformed

    private void rbSelectedActionPerformed(ActionEvent evt) {//GEN-FIRST:event_rbSelectedActionPerformed
        preferences.putBoolean(JavaNetRepositoryError.PROP_SELECTED, true);
        taSelected.setEnabled(true);
    }//GEN-LAST:event_rbSelectedActionPerformed

    private void taSelectedKeyTyped(KeyEvent evt) {//GEN-FIRST:event_taSelectedKeyTyped
        String[] vals = taSelected.getText().split("([\\s])+");
        StringBuilder sb = new StringBuilder();
        for (String v : vals) {
            sb.append(v.trim()).append(" ");
        }
        preferences.put(JavaNetRepositoryError.PROP_URLS, sb.toString());
    }//GEN-LAST:event_taSelectedKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ButtonGroup buttonGroup1;
    private JLabel jLabel1;
    private JScrollPane jScrollPane1;
    private JRadioButton rbAny;
    private JRadioButton rbSelected;
    private JTextArea taSelected;
    // End of variables declaration//GEN-END:variables

}
