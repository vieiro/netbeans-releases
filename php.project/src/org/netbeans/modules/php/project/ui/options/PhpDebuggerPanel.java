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

package org.netbeans.modules.php.project.ui.options;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 * UI for PHP debugging options.
 */
public class PhpDebuggerPanel extends JPanel {

    private static final long serialVersionUID = 9465641111345L;

    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private final WatchesAndEvalListener watchesAndEvalListener;


    public PhpDebuggerPanel() {
        initComponents();
        errorLabel.setText(" "); // NOI18N

        watchesAndEvalListener = new WatchesAndEvalListener(maxStructuresDepthTextField, maxChildrenTextField);

        init();
        initListeners();
    }

    private void init() {
        watchesAndEvalListener.enableDependentFields(watchesAndEvalCheckBox.isSelected());
    }

    private void initListeners() {
        DocumentListener documentListener = new DefaultDocumentListener();
        portTextField.getDocument().addDocumentListener(documentListener);
        sessionIdTextField.getDocument().addDocumentListener(documentListener);
        maxStructuresDepthTextField.getDocument().addDocumentListener(documentListener);
        maxChildrenTextField.getDocument().addDocumentListener(documentListener);
        watchesAndEvalCheckBox.addItemListener(watchesAndEvalListener);
    }

    public String getPort() {
        return portTextField.getText();
    }

    public void setPort(int debuggerPort) {
        portTextField.setText(String.valueOf(debuggerPort));
    }

    public String getSessionId() {
        return sessionIdTextField.getText();
    }

    public void setSessionId(String sessionId) {
        sessionIdTextField.setText(sessionId);
    }

    public String getMaxStructuresDepth() {
        return maxStructuresDepthTextField.getText();
    }

    public void setMaxStructuresDepth(int maxStructuresDepth) {
        maxStructuresDepthTextField.setText(String.valueOf(maxStructuresDepth));
    }

    public String getMaxChildren() {
        return maxChildrenTextField.getText();
    }

    public void setMaxChildren(int maxChildren) {
        maxChildrenTextField.setText(String.valueOf(maxChildren));
    }

    public boolean isStoppedAtTheFirstLine() {
        return stopAtTheFirstLineCheckBox.isSelected();
    }

    public void setStoppedAtTheFirstLine(boolean stoppedAtTheFirstLine) {
        stopAtTheFirstLineCheckBox.setSelected(stoppedAtTheFirstLine);
    }

    public boolean isWatchesAndEval() {
        return watchesAndEvalCheckBox.isSelected();
    }

    public void setWatchesAndEval(boolean watchesAndEval) {
        watchesAndEvalCheckBox.removeItemListener(watchesAndEvalListener);
        watchesAndEvalCheckBox.setSelected(watchesAndEval);
        watchesAndEvalCheckBox.addItemListener(watchesAndEvalListener);
    }

    public boolean isShowUrls() {
        return requestedUrlsCheckBox.isSelected();
    }

    public void setShowUrls(boolean showUrls) {
        requestedUrlsCheckBox.setSelected(showUrls);
    }

    public boolean isShowConsole() {
        return debuggerConsoleCheckBox.isSelected();
    }

    public void setShowConsole(boolean showConsole) {
        debuggerConsoleCheckBox.setSelected(showConsole);
    }

    public void setError(String message) {
        errorLabel.setText(" "); // NOI18N
        errorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
        errorLabel.setText(message);
    }

    public void setWarning(String message) {
        errorLabel.setText(" "); // NOI18N
        errorLabel.setForeground(UIManager.getColor("nb.warningForeground")); // NOI18N
        errorLabel.setText(message);
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    void fireChange() {
        changeSupport.fireChange();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        portLabel = new JLabel();
        portTextField = new JTextField();
        sessionIdLabel = new JLabel();
        sessionIdTextField = new JTextField();
        stopAtTheFirstLineCheckBox = new JCheckBox();
        watchesAndEvalCheckBox = new JCheckBox();
        maxStructuresDepthLabel = new JLabel();
        maxStructuresDepthTextField = new JTextField();
        maxChildrenLabel = new JLabel();
        maxChildrenTextField = new JTextField();
        requestedUrlsCheckBox = new JCheckBox();
        debuggerConsoleCheckBox = new JCheckBox();
        debuggerConsoleInfoLabel = new JLabel();
        errorLabel = new JLabel();

        portLabel.setLabelFor(portTextField);
        Mnemonics.setLocalizedText(portLabel, NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerPanel.portLabel.text")); // NOI18N

        sessionIdLabel.setLabelFor(sessionIdTextField);

        Mnemonics.setLocalizedText(sessionIdLabel, NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerPanel.sessionIdLabel.text")); // NOI18N
        Mnemonics.setLocalizedText(stopAtTheFirstLineCheckBox, NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerPanel.stopAtTheFirstLineCheckBox.text")); // NOI18N
        Mnemonics.setLocalizedText(watchesAndEvalCheckBox, NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerPanel.watchesAndEvalCheckBox.text")); // NOI18N

        maxStructuresDepthLabel.setLabelFor(maxStructuresDepthTextField);
        Mnemonics.setLocalizedText(maxStructuresDepthLabel, NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerPanel.maxStructuresDepthLabel.text")); // NOI18N

        maxChildrenLabel.setLabelFor(maxChildrenTextField);

        Mnemonics.setLocalizedText(maxChildrenLabel, NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerPanel.maxChildrenLabel.text")); // NOI18N
        Mnemonics.setLocalizedText(requestedUrlsCheckBox, NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerPanel.requestedUrlsCheckBox.text")); // NOI18N
        Mnemonics.setLocalizedText(debuggerConsoleCheckBox, NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerPanel.debuggerConsoleCheckBox.text")); // NOI18N

        debuggerConsoleInfoLabel.setLabelFor(this);
        Mnemonics.setLocalizedText(debuggerConsoleInfoLabel, NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerPanel.debuggerConsoleInfoLabel.text")); // NOI18N

        errorLabel.setLabelFor(debuggerConsoleCheckBox);
        Mnemonics.setLocalizedText(errorLabel, "ERROR"); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(portLabel)
                            .addComponent(sessionIdLabel))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(portTextField, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                            .addComponent(sessionIdTextField, GroupLayout.PREFERRED_SIZE, 176, GroupLayout.PREFERRED_SIZE)))
                    .addComponent(stopAtTheFirstLineCheckBox)
                    .addComponent(watchesAndEvalCheckBox)
                    .addComponent(requestedUrlsCheckBox)
                    .addComponent(debuggerConsoleCheckBox)
                    .addComponent(errorLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(debuggerConsoleInfoLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                    .addComponent(maxStructuresDepthLabel)
                                    .addComponent(maxChildrenLabel))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                    .addComponent(maxChildrenTextField)
                                    .addComponent(maxStructuresDepthTextField, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {maxChildrenTextField, maxStructuresDepthTextField, portTextField});

        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(portLabel)
                    .addComponent(portTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(sessionIdLabel)
                    .addComponent(sessionIdTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(stopAtTheFirstLineCheckBox)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(watchesAndEvalCheckBox)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(maxStructuresDepthLabel)
                    .addComponent(maxStructuresDepthTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(maxChildrenLabel)
                    .addComponent(maxChildrenTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(requestedUrlsCheckBox)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(debuggerConsoleCheckBox)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(debuggerConsoleInfoLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(errorLabel))
        );

        portLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.portLabel.AccessibleContext.accessibleName"));         portLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.portLabel.AccessibleContext.accessibleDescription"));         portTextField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.portTextField.AccessibleContext.accessibleName"));         portTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.portTextField.AccessibleContext.accessibleDescription"));         sessionIdLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.sessionIdLabel.AccessibleContext.accessibleName"));         sessionIdLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.debuggerSessionIdLabel.AccessibleContext.accessibleDescription"));         sessionIdTextField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerPanel.sessionIdTextField.AccessibleContext.accessibleName"));         sessionIdTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.sessionIdTextField.AccessibleContext.accessibleDescription"));         stopAtTheFirstLineCheckBox.getAccessibleContext().setAccessibleName(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.stopAtTheFirstLineCheckBox.AccessibleContext.accessibleName"));         stopAtTheFirstLineCheckBox.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.stopAtTheFirstLineCheckBox.AccessibleContext.accessibleDescription"));         watchesAndEvalCheckBox.getAccessibleContext().setAccessibleName(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.watchesAndEvalCheckBox.AccessibleContext.accessibleName"));         watchesAndEvalCheckBox.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.watchesAndEvalCheckBox.AccessibleContext.accessibleDescription"));         maxStructuresDepthLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.maxStructuresDepthLabel.AccessibleContext.accessibleName"));         maxStructuresDepthLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.maxStructuresDepthLabel.AccessibleContext.accessibleDescription"));         maxStructuresDepthTextField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerPanel.maxStructuresDepthTextField.AccessibleContext.accessibleName"));         maxStructuresDepthTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.maxStructuresDepthTextField.AccessibleContext.accessibleDescription"));         maxChildrenLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.maxChildrenLabel.AccessibleContext.accessibleName"));         maxChildrenLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.maxChildrenLabel.AccessibleContext.accessibleDescription"));         maxChildrenTextField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerPanel.maxChildrenTextField.AccessibleContext.accessibleName"));         maxChildrenTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.maxChildrenTextField.AccessibleContext.accessibleDescription"));         requestedUrlsCheckBox.getAccessibleContext().setAccessibleName(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.requestedUrlsCheckBox.AccessibleContext.accessibleName"));         requestedUrlsCheckBox.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.requestedUrlsCheckBox.AccessibleContext.accessibleDescription"));         debuggerConsoleCheckBox.getAccessibleContext().setAccessibleName(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.debuggerConsoleCheckBox.AccessibleContext.accessibleName"));         debuggerConsoleCheckBox.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.debuggerConsoleCheckBox.AccessibleContext.accessibleDescription"));         debuggerConsoleInfoLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.debuggerConsoleInfoLabel.AccessibleContext.accessibleName"));         debuggerConsoleInfoLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.debuggerConsoleInfoLabel.AccessibleContext.accessibleDescription"));         errorLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.errorLabel.AccessibleContext.accessibleName"));         errorLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.errorLabel.AccessibleContext.accessibleDescription"));
        getAccessibleContext().setAccessibleName(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.AccessibleContext.accessibleName"));         getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(PhpDebuggerPanel.class, "PhpDebuggerOptions.AccessibleContext.accessibleDescription"));     }// </editor-fold>    // Variables declaration - do not modify//GEN-END:initComponents
    private JCheckBox debuggerConsoleCheckBox;
    private JLabel debuggerConsoleInfoLabel;
    private JLabel errorLabel;
    private JLabel maxChildrenLabel;
    private JTextField maxChildrenTextField;
    private JLabel maxStructuresDepthLabel;
    private JTextField maxStructuresDepthTextField;
    private JLabel portLabel;
    private JTextField portTextField;
    private JCheckBox requestedUrlsCheckBox;
    private JLabel sessionIdLabel;
    private JTextField sessionIdTextField;
    private JCheckBox stopAtTheFirstLineCheckBox;
    private JCheckBox watchesAndEvalCheckBox;
    // End of variables declaration//GEN-END:variables

    //~ Inner classes

    private final class DefaultDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            processUpdate();
        }

        private void processUpdate() {
            fireChange();
        }
    }

    private static final class WatchesAndEvalListener implements ItemListener {

        private static boolean warningShown = false;

        private final JComponent[] dependentFields;


        WatchesAndEvalListener(JComponent... dependentFields) {
            this.dependentFields = dependentFields;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            boolean selected = e.getStateChange() == ItemEvent.SELECTED;
            enableDependentFields(selected);
            showWarning(selected);
        }

        private void showWarning(boolean selected) {
            if (warningShown) {
                return;
            }
            if (selected) {
                NotifyDescriptor descriptor = new NotifyDescriptor.Message(
                        NbBundle.getMessage(PhpDebuggerPanel.class, "MSG_WatchesAndEval"),
                        NotifyDescriptor.WARNING_MESSAGE);
                DialogDisplayer.getDefault().notifyLater(descriptor);
                warningShown = true;
            }
        }

        void enableDependentFields(boolean selected) {
            for (JComponent component : dependentFields) {
                component.setEnabled(selected);
            }
        }

    }

}
