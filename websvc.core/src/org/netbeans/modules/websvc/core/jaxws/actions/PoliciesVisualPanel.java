/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

/*
 * OWSMPolicies.java
 *
 * Created on 29.06.2011, 18:25:16
 */
package org.netbeans.modules.websvc.core.jaxws.actions;

import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ListModel;

/**
 *
 * @author ads
 */
public class PoliciesVisualPanel extends javax.swing.JPanel {

    
    public PoliciesVisualPanel(List<String> ids ) {
        initComponents();
        
        String policies[] = ids.toArray( new String[ ids.size()]);
        ListModel model = new DefaultComboBoxModel( policies);
        list.setModel( model );
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        label = new javax.swing.JLabel();

        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(list);
        list.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PoliciesVisualPanel.class, "ACSN_SelectPolicy")); // NOI18N
        list.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PoliciesVisualPanel.class, "ACSD_SelectPolicy")); // NOI18N

        label.setLabelFor(list);
        org.openide.awt.Mnemonics.setLocalizedText(label, org.openide.util.NbBundle.getMessage(PoliciesVisualPanel.class, "LBL_ChoosePolicyLbl")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(label))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                .addContainerGap())
        );

        label.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PoliciesVisualPanel.class, "ACSN_SelectPolicy")); // NOI18N
        label.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PoliciesVisualPanel.class, "ACSD_SelectPolicy")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    
    public String getId() {
        Object selected = list.getSelectedValue();
        if ( selected != null ){
            return selected.toString();
        }
        else {
            return null;
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel label;
    private javax.swing.JList list;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
    
}
