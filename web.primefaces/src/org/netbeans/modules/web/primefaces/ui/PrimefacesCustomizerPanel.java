/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.primefaces.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.libraries.LibrariesCustomizer;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.web.primefaces.PrimefacesImplementation;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * Panel for choosing Primefaces libraries for project.
 *
 * @author Martin Fousek <marfous@netbeans.org>
 */
@SuppressWarnings("serial")
public class PrimefacesCustomizerPanel extends javax.swing.JPanel implements HelpCtx.Provider {

    private static final Logger LOGGER = Logger.getLogger(PrimefacesCustomizerPanel.class.getName());
    private final ChangeSupport changeSupport = new ChangeSupport(this);

    /**
     * Creates new form PrimefacesCustomizerPanel.
     * @param changeListener {@code ChangeListener} which should be notified about changes.
     */
    public PrimefacesCustomizerPanel(ChangeListener changeListener) {
        initComponents();
        changeSupport.addChangeListener(changeListener);
        initLibraries(true);

        primefacesLibrariesComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changeSupport.fireChange();
            }
        });
    }

    /**
     * Initialize {@link #primefacesLibrariesComboBox} with all PrimeFaces libraries.
     * @param setStoredValue {@code true} if should be selected stored value from preferences, {@code false} otherwise
     */
    public final void initLibraries(final boolean setStoredValue) {
        long time = System.currentTimeMillis();
        final List<Library> primefacesLibraries = new ArrayList<Library>();

        RequestProcessor.getDefault().post(new Runnable() {
            @Override
            public void run() {
                for (Library library : PrimefacesImplementation.getAllRegisteredPrimefaces()) {
                    primefacesLibraries.add(library);
                }

                // update the combo box with libraries
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        setPrimefacesLibrariesComboBox(primefacesLibraries);
                        if (setStoredValue && !primefacesLibraries.isEmpty()) {
                            setDefaultPrimefacesComboBoxValue(primefacesLibraries);
                        }
                        changeSupport.fireChange();
                    }
                });
            }
        });

        LOGGER.log(Level.FINEST, "Time spent in {0} initLibraries = {1} ms",
                new Object[]{this.getClass().getName(), System.currentTimeMillis() - time});
    }

    /**
     * Gets in combo box chosen PrimeFaces library.
     * @return name of selected library
     */
    public Library getPrimefacesLibrary() {
        Object selectedItem = primefacesLibrariesComboBox.getSelectedItem();
        if (selectedItem != null && selectedItem instanceof Library) {
            return (Library) selectedItem;
        }
        return null;
    }

    private void setPrimefacesLibrariesComboBox(List<Library> items) {
        primefacesLibrariesComboBox.setModel(new DefaultComboBoxModel(items.toArray()));
        primefacesLibrariesComboBox.setRenderer(new LibraryComboBoxRenderer());
        primefacesLibrariesComboBox.setEnabled(!items.isEmpty());
    }

    private void setDefaultPrimefacesComboBoxValue(List<Library> foundLibraries) {
        Preferences preferences = PrimefacesImplementation.getPrimefacesPreferences();
        String preferred = preferences.get(PrimefacesImplementation.PROP_PREFERRED_LIBRARY, ""); //NOI18N
        for (Library library : foundLibraries) {
            if (library.getName().equals(preferred)) {
                primefacesLibrariesComboBox.setSelectedItem(library);
            }
        }
    }

    /**
     * Gets error messages from the panel.
     * @return error message in cases of any error, {@code null} otherwise
     */
    @NbBundle.Messages("PrimefacesCustomizerPanel.MissingLibraries.label=No valid PrimeFaces libraries found.")
    public String getErrorMessage() {
        if (getPrimefacesLibrary() == null) {
            return Bundle.PrimefacesCustomizerPanel_MissingLibraries_label();
        }
        return null;
    }

   /**
     * Gets warning messages from the panel.
     * @return warning message in cases of any warning, {@code null} otherwise
     */
    public String getWarningMessage() {
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        primefacesLibraryLabel = new javax.swing.JLabel();
        primefacesLibrariesComboBox = new javax.swing.JComboBox();
        createLibraryButton = new javax.swing.JButton();
        noteLabel = new javax.swing.JLabel();

        primefacesLibraryLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/primefaces/ui/Bundle").getString("PrimefacesCustomizerPanel.primefacesLibraryLabel.mnemonics").charAt(0));
        primefacesLibraryLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        primefacesLibraryLabel.setLabelFor(primefacesLibrariesComboBox);
        primefacesLibraryLabel.setText(org.openide.util.NbBundle.getMessage(PrimefacesCustomizerPanel.class, "PrimefacesCustomizerPanel.primefacesLibraryLabel.text")); // NOI18N

        primefacesLibrariesComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        primefacesLibrariesComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Searching Primefaces Libraries..." }));

        createLibraryButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/primefaces/ui/Bundle").getString("PrimefacesCustomizerPanel.createLibraryButton.mnemonic").charAt(0));
        createLibraryButton.setText(org.openide.util.NbBundle.getMessage(PrimefacesCustomizerPanel.class, "PrimefacesCustomizerPanel.createLibraryButton.text")); // NOI18N
        createLibraryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createLibraryButtonActionPerformed(evt);
            }
        });

        noteLabel.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        noteLabel.setText(org.openide.util.NbBundle.getMessage(PrimefacesCustomizerPanel.class, "PrimefacesCustomizerPanel.noteLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(primefacesLibraryLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(primefacesLibrariesComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(createLibraryButton, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(noteLabel, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(primefacesLibraryLabel)
                    .addComponent(primefacesLibrariesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(createLibraryButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(noteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void createLibraryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createLibraryButtonActionPerformed
        LibrariesCustomizer.showCreateNewLibraryCustomizer(LibraryManager.getDefault());
        initLibraries(false);
    }//GEN-LAST:event_createLibraryButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createLibraryButton;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JComboBox primefacesLibrariesComboBox;
    private javax.swing.JLabel primefacesLibraryLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    private class LibraryComboBoxRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Library) {
                ((JLabel) component).setText(((Library) value).getDisplayName());
            } else {
                ((JLabel) component).setText((String) value);
            }
            return component;
        }

    }
}
