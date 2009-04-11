/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

/*
 * AvailableProvidersPanel.java
 *
 * Created on 10.4.2009, 12:12:45
 */

package org.netbeans.modules.server.ui.wizard;

import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.JRadioButton;

/**
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
final class AvailableProvidersPanel extends javax.swing.JPanel {

    /** Creates new form AvailableProvidersPanel */
    public AvailableProvidersPanel(JRadioButton[] arr) {
        initComponents();

        radioPanel.removeAll();
        for (JRadioButton b : arr) {
            radioGroup.add(b);
            radioPanel.add(b);
        }
        arr[0].setSelected(true);
    }

    public JRadioButton getSelected() {
        Enumeration<AbstractButton> en = radioGroup.getElements();
        while (en.hasMoreElements()) {
            AbstractButton b = en.nextElement();
            if (b.isSelected()) {
                return (JRadioButton)b;
            }
        }
        throw new IllegalStateException();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        radioGroup = new javax.swing.ButtonGroup();
        radioPanel = new javax.swing.JPanel();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        msgPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        radioPanel.setLayout(new java.awt.GridLayout(0, 1));

        radioGroup.add(jRadioButton2);
        jRadioButton2.setText(org.openide.util.NbBundle.getBundle(AvailableProvidersPanel.class).getString("AvailableProvidersPanel.jRadioButton2.text")); // NOI18N
        radioPanel.add(jRadioButton2);

        radioGroup.add(jRadioButton3);
        jRadioButton3.setText(org.openide.util.NbBundle.getBundle(AvailableProvidersPanel.class).getString("AvailableProvidersPanel.jRadioButton3.text")); // NOI18N
        radioPanel.add(jRadioButton3);

        jLabel1.setText(org.openide.util.NbBundle.getBundle(AvailableProvidersPanel.class).getString("MSG_AvailableProviders")); // NOI18N

        org.jdesktop.layout.GroupLayout msgPanelLayout = new org.jdesktop.layout.GroupLayout(msgPanel);
        msgPanel.setLayout(msgPanelLayout);
        msgPanelLayout.setHorizontalGroup(
            msgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(msgPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                .addContainerGap())
        );
        msgPanelLayout.setVerticalGroup(
            msgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(msgPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(12, 12, 12)
                .add(radioPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                .add(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(msgPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(radioPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JPanel msgPanel;
    private javax.swing.ButtonGroup radioGroup;
    private javax.swing.JPanel radioPanel;
    // End of variables declaration//GEN-END:variables

}
