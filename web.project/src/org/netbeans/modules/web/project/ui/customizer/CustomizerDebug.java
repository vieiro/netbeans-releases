/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.web.project.ui.customizer;

import javax.swing.JPanel;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.NbBundle;

/**
 * Customizer panel for the Debug category
 * 
 * @author  quynguyen
 */
public class CustomizerDebug extends JPanel {
    private final ProjectCustomizer.Category category;
    
    /** Creates new form CustomizerDebug */
    public CustomizerDebug(ProjectCustomizer.Category category, WebProjectProperties uiProps) {
        this.category = category;
        
        initComponents();
        
        debugServerJCheckBox.setModel(uiProps.DEBUG_SERVER_MODEL);
        debugClientJCheckBox.setModel(uiProps.DEBUG_CLIENT_MODEL);
        
        validateCheckBoxes();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        debugServerJCheckBox = new javax.swing.JCheckBox();
        debugClientJCheckBox = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(debugServerJCheckBox, org.openide.util.NbBundle.getMessage(CustomizerDebug.class, "LBL_CustomizeDebug_ServerDebug_JCheckBox")); // NOI18N
        debugServerJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debugServerActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(debugClientJCheckBox, org.openide.util.NbBundle.getMessage(CustomizerDebug.class, "LBL_CustomizeDebug_ClientDebug_JCheckBox")); // NOI18N
        debugClientJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debugClientActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(debugClientJCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 453, Short.MAX_VALUE)
            .add(debugServerJCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(debugServerJCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(debugClientJCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(430, 430, 430))
        );

        debugServerJCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerDebug.class, "ACS_CustomizeDebug_ServerDebug_A11YDesc")); // NOI18N
        debugClientJCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerDebug.class, "ACS_CustomizeDebug_ClientDebug_A11YDesc")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

private void debugServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debugServerActionPerformed

    validateCheckBoxes();
}//GEN-LAST:event_debugServerActionPerformed

private void debugClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debugClientActionPerformed

    validateCheckBoxes();
}//GEN-LAST:event_debugClientActionPerformed
 
    private void validateCheckBoxes() {
        if (!debugClientJCheckBox.isSelected() && !debugServerJCheckBox.isSelected()) {
            category.setErrorMessage(NbBundle.getMessage(CustomizerDebug.class, "LBL_CustomzieDebug_NoDebug_Error"));
            category.setValid(false);
        }else {
            category.setErrorMessage(null);
            category.setValid(true);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox debugClientJCheckBox;
    private javax.swing.JCheckBox debugServerJCheckBox;
    // End of variables declaration//GEN-END:variables

}
