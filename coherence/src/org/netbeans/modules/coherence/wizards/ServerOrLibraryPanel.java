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
package org.netbeans.modules.coherence.wizards;

/**
 *
 * @author Martin Fousek <marfous@netbeans.org>
 */
public class ServerOrLibraryPanel extends javax.swing.JPanel {

    /**
     * Creates new form ServerOrLibraryPanel
     */
    public ServerOrLibraryPanel() {
        initComponents();
    }

    public boolean getLibraryChecked() {
        return libraryRadioButton.isSelected();
    }

    public boolean getPlatformChecked() {
        return platformRadioButton.isSelected();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        libraryPlatformGroup = new javax.swing.ButtonGroup();
        libraryRadioButton = new javax.swing.JRadioButton();
        platformRadioButton = new javax.swing.JRadioButton();
        libraryLabel = new javax.swing.JLabel();
        platformLabel = new javax.swing.JLabel();

        libraryPlatformGroup.add(libraryRadioButton);
        libraryRadioButton.setSelected(true);
        libraryRadioButton.setText(org.openide.util.NbBundle.getMessage(ServerOrLibraryPanel.class, "ServerOrLibraryPanel.libraryRadioButton.text")); // NOI18N

        libraryPlatformGroup.add(platformRadioButton);
        platformRadioButton.setText(org.openide.util.NbBundle.getMessage(ServerOrLibraryPanel.class, "ServerOrLibraryPanel.platformRadioButton.text")); // NOI18N

        libraryLabel.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        libraryLabel.setText(org.openide.util.NbBundle.getMessage(ServerOrLibraryPanel.class, "ServerOrLibraryPanel.libraryLabel.text")); // NOI18N

        platformLabel.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        platformLabel.setText(org.openide.util.NbBundle.getMessage(ServerOrLibraryPanel.class, "ServerOrLibraryPanel.platformLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(libraryRadioButton)
                    .addComponent(libraryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(platformRadioButton)
                    .addComponent(platformLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(libraryRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(libraryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(platformRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(platformLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel libraryLabel;
    private javax.swing.ButtonGroup libraryPlatformGroup;
    private javax.swing.JRadioButton libraryRadioButton;
    private javax.swing.JLabel platformLabel;
    private javax.swing.JRadioButton platformRadioButton;
    // End of variables declaration//GEN-END:variables
}
