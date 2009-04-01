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

package org.netbeans.modules.versioning.util;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Cancellable;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor.Task;

/**
 * 
 * @author Tomas Stupka
 */
public abstract class ExportDiffSupport {
    private AbstractExportDiffPanel panel;
    private DialogDescriptor dd;
    private Preferences preferences;
    private Dialog dialog;
    private ExportDiffProvider exportDiffProvider;
    private File[] files;

    public ExportDiffSupport(File[] files, final Preferences preferences) {
        this.preferences = preferences;
        this.files = files;
    }

    /**
     * Override this to provide your own top diff dialog.
     * This dialog will be displayed if any registered ExportDiffProvider is found.
     * @param insidePanel an inside panel with diff provider UI components, include this panel inside yor provided top panel
     */
    protected void createComplexDialog (AbstractExportDiffPanel insidePanel) {
        dd = new DialogDescriptor(insidePanel, NbBundle.getMessage(ExportDiffSupport.class, "CTL_Export_Title"));
    }

    /**
     * Override this to provide your own implementation of the simple dialog.
     * This dialog will be displayed if no registered ExportDiffProvider has been found.
     * <strong></strong>
     * @param currentFilePath folder location to save diff into as it is present in user configuration
     * @return simple export file diff panel
     */
    protected AbstractExportDiffPanel createSimpleDialog(final String currentFilePath) {
        dd = new DialogDescriptor(createFileChooser(new File(currentFilePath)), NbBundle.getMessage(ExportDiffSupport.class, "CTL_Export_Title"));
        dd.setOptions(new Object[0]);
        // XXX try better
        panel = new ExportDiffPanel(new JPanel());
        panel.setOutputFileText("");
        return panel;
    }

    /**
     * Override this to provide the descriptor of the top panel.
     * @return the descriptor of the top dialog
     */
    protected DialogDescriptor getDialogDescriptor () {
        return dd;
    }

    private void initializePanels() {
        exportDiffProvider = Lookup.getDefault().lookup(ExportDiffProvider.class);
        String currentFilePath = preferences.get("ExportDiff.saveFolder", System.getProperty("user.home"));
        if(exportDiffProvider == null) {
            panel = createSimpleDialog(currentFilePath);
            dd = getDialogDescriptor();
        } else {
            exportDiffProvider.setContext(files);
            ExportDiffPanel edPanel = new ExportDiffPanel(exportDiffProvider.getComponent());
            edPanel.setOutputFileText(currentFilePath);

            exportDiffProvider.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if(evt.getPropertyName().equals(ExportDiffProvider.EVENT_DATA_CHANGED)) {
                        validate();
                    }
                }
            });
            edPanel.asFileRadioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { validate(); }
            });
            edPanel.attachRadioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { validate(); }
            });
            panel = edPanel;
            createComplexDialog(panel);
            dd = getDialogDescriptor();
        }
        panel.addOutputFileTextDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                validate();
            }
            public void removeUpdate(DocumentEvent e) {
                validate();
            }
            public void changedUpdate(DocumentEvent e) {
                validate();
            }
        });
        panel.addBrowseActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onChooseFile(new File(panel.getOutputFileText()));
            }
        });
    }

    private void validate() {
        assert panel != null;
        if (exportDiffProvider == null || panel.isFileOutputSelected()) {
            dd.setValid(!panel.getOutputFileText().trim().equals(""));
        } else {
            dd.setValid(exportDiffProvider.isValid());
        }
    }

    public void export() {
        initializePanels();
        validate();

        dialog = DialogDisplayer.getDefault().createDialog(dd);
        dialog.setVisible(true);
        if(dd.getValue() == DialogDescriptor.OK_OPTION) {
            if(exportDiffProvider == null || ((ExportDiffPanel)panel).asFileRadioButton.isSelected()) {
                final File toFile = new File(panel.getOutputFileText());
                Utils.createTask(new Runnable() {
                    public void run() {
                        writeDiffFile(toFile);
                    }
                }).schedule(0);
            } else {
                final Task[] t = new Task[1];
                Cancellable c = new Cancellable() {
                    public boolean cancel() {
                        if(t[0] != null) {
                            return t[0].cancel();
                        }
                        return true; 
                    }
                };
                final ProgressHandle handle = ProgressHandleFactory.createHandle(NbBundle.getMessage(ExportDiffSupport.class, "CTL_Attaching"), c);
                handle.start();
                t[0] = Utils.createTask(new Runnable() {
                    public void run() {
                        try {
                            File toFile;
                            try {
                                toFile = File.createTempFile("vcs-diff", ".patch");
                            } catch (IOException ex) {
                                // XXX
                                return;
                            }
                            toFile.deleteOnExit();
                            writeDiffFile(toFile);
                            exportDiffProvider.handleDiffFile(toFile);
                        } finally {
                            handle.finish();
                        }
                    }
                });
                t[0].schedule(0);
            }
        }
    }

    /**
     * Synchronously writtes the changes to the given file
     * @param file
     */
    public abstract void writeDiffFile(File file);

    private void onChooseFile(File currentDir) {
        final JFileChooser chooser = createFileChooser(currentDir);

        DialogDescriptor chooseFileDescriptor = new DialogDescriptor(chooser, NbBundle.getMessage(ExportDiffSupport.class, "CTL_Export_Title"));
        chooseFileDescriptor.setOptions(new Object[0]);
        dialog = DialogDisplayer.getDefault().createDialog(chooseFileDescriptor);
        dialog.setVisible(true);
    }

    private JFileChooser createFileChooser(File curentDir) {
        final JFileChooser chooser = new AccessibleJFileChooser(NbBundle.getMessage(ExportDiffSupport.class, "ACSD_Export"));
        chooser.setDialogTitle(NbBundle.getMessage(ExportDiffSupport.class, "CTL_Export_Title"));
        chooser.setMultiSelectionEnabled(false);
        javax.swing.filechooser.FileFilter[] old = chooser.getChoosableFileFilters();
        for (int i = 0; i < old.length; i++) {
            javax.swing.filechooser.FileFilter fileFilter = old[i];
            chooser.removeChoosableFileFilter(fileFilter);

        }
        chooser.setCurrentDirectory(curentDir); // NOI18N
        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.getName().endsWith("diff") || f.getName().endsWith("patch") || f.isDirectory();  // NOI18N
            }
            public String getDescription() {
                return NbBundle.getMessage(ExportDiffSupport.class, "BK3002");
            }
        });

        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setApproveButtonMnemonic(NbBundle.getMessage(ExportDiffSupport.class, "MNE_Export_ExportAction").charAt(0));
        chooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String state = e.getActionCommand();
                if (state.equals(JFileChooser.APPROVE_SELECTION)) {
                    File destination = chooser.getSelectedFile();
                    String name = destination.getName();
                    boolean requiredExt = false;
                    requiredExt |= name.endsWith(".diff");  // NOI18N
                    requiredExt |= name.endsWith(".dif");   // NOI18N
                    requiredExt |= name.endsWith(".patch"); // NOI18N
                    if (requiredExt == false) {
                        File parent = destination.getParentFile();
                        destination = new File(parent, name + ".patch"); // NOI18N
                    }

                    if (destination.exists()) {
                        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(NbBundle.getMessage(ExportDiffSupport.class, "BK3005", destination.getAbsolutePath()));
                        nd.setOptionType(NotifyDescriptor.YES_NO_OPTION);
                        DialogDisplayer.getDefault().notify(nd);
                        if (nd.getValue().equals(NotifyDescriptor.OK_OPTION) == false) {
                            return;
                        }
                    }
                    preferences.put("ExportDiff.saveFolder", destination.getParent()); // NOI18N
                    panel.setOutputFileText(destination.getAbsolutePath());
                } else {
                    dd.setValue(null);
                }
                if(dialog != null) {
                    dialog.dispose();
                }
            }
        });
        return chooser;
    }

    public static abstract class ExportDiffProvider {
        private PropertyChangeSupport support = new PropertyChangeSupport(this);
        private final static String EVENT_DATA_CHANGED = "ExportDiff.data.changed";

        /**
         * Sets the files for which this provider should provide
         * @return
         */
        protected abstract void setContext(File[] files);

        /**
         * Handles the given diff file
         * @param file
         */
        public abstract void handleDiffFile(File file);

        /**
         * Return a visual component representing this ExportDiffProvider
         * @return
         */
        public abstract JComponent getComponent();

        /**
         * Returns true if the user intput in this ExportDiffProvider-s
         * component isValid, oherwise false
         * @return
         */
        public abstract boolean isValid();

        /**
         * To be called if there was a change made in this ExportDiffProvider-s
         * visual components data
         */
        protected void fireDataChanged() {
            support.firePropertyChange(EVENT_DATA_CHANGED, null, null);
        }
        public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
            support.removePropertyChangeListener(listener);
        }
        public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
            support.addPropertyChangeListener(listener);
        }
    }

    /**
     * Abstract ancestor of an output diff panel.
     * @author Ondra Vrabec
     */
    public static abstract class AbstractExportDiffPanel extends JPanel {

        /**
         * Implement this method to access diff output file path field's text.
         * @return value of the diff output file path field.
         */
        public abstract String getOutputFileText();

        /**
         * Implement this to set the diff output file path field' text.
         * @param text
         */
        public abstract void setOutputFileText(final String text);

        /**
         * Implement this to add a listener to the diff output file path field.
         * @param list
         */
        public abstract void addOutputFileTextDocumentListener(final DocumentListener list);

        /**
         * Implement to add a listener on the browse button
         * @param actionListener
         */
        public abstract void addBrowseActionListener(ActionListener actionListener);

        /**
         * Override to specify if the diff output is set to file. Implicitly returns true
         * @return true if the file output is selected, false otherwise
         */
        public boolean isFileOutputSelected() {
            return true;
        }
    }
}
