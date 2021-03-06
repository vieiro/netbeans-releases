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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.bugtracking.ui.issue;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Jan Stola
 */
class FindBar extends javax.swing.JPanel {
    private static final int MAX_SEARCH_MODEL_SIZE = 5;
    private FindSupport support;
    private boolean initialized;
    private DefaultComboBoxModel lastSearchModel;

    public FindBar(FindSupport support) {
        this.support = support;
        initComponents();
        lastSearchModel = new DefaultComboBoxModel();
        findCombo.setModel(lastSearchModel);
        findCombo.setSelectedItem(""); // NOI18N
        initialized = true;
        addComboEditorListener();
        InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        String closeKey = "close"; // NOI18N
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), closeKey);
        getActionMap().put(closeKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FindBar.this.support.cancel();
            }
        });
    }

    private void addComboEditorListener() {
        Component editor = findCombo.getEditor().getEditorComponent();
        if (editor instanceof JTextComponent) {
            JTextComponent tcomp = (JTextComponent)editor;
            tcomp.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    changedUpdate(e);
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    changedUpdate(e);
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    support.updatePattern();
                }
            });
        }
    }

    String getPattern() {
        return findCombo.getEditor().getItem().toString();
    }

    boolean getMatchCase() {
        return machCaseChoice.isSelected();
    }

    boolean getWholeWords() {
        return wholeWordsChoice.isSelected();
    }

    boolean getRegularExpression() {
        return regularExpressionChoice.isSelected();
    }

    boolean getHighlightResults() {
        return highlightResultsChoice.isSelected();
    }

    @Override
    public boolean requestFocusInWindow() {
        return findCombo.requestFocusInWindow();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        findLabel = new javax.swing.JLabel();
        findCombo = new javax.swing.JComboBox();
        machCaseChoice = new javax.swing.JCheckBox();
        closeButton = new javax.swing.JButton();
        wholeWordsChoice = new javax.swing.JCheckBox();
        regularExpressionChoice = new javax.swing.JCheckBox();
        highlightResultsChoice = new javax.swing.JCheckBox();
        findPreviousButton = new javax.swing.JButton();
        findNextButton = new javax.swing.JButton();

        findLabel.setText(org.openide.util.NbBundle.getMessage(FindBar.class, "FindBar.findLabel.text")); // NOI18N

        findCombo.setEditable(true);
        findCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findComboActionPerformed(evt);
            }
        });

        machCaseChoice.setText(org.openide.util.NbBundle.getMessage(FindBar.class, "FindBar.machCaseChoice.text")); // NOI18N
        machCaseChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                machCaseChoiceActionPerformed(evt);
            }
        });

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/bugtracking/ui/resources/find_close.png"))); // NOI18N
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusable(false);
        closeButton.setMargin(new java.awt.Insets(2, 1, 0, 1));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeButtonMouseExited(evt);
            }
        });

        wholeWordsChoice.setText(org.openide.util.NbBundle.getMessage(FindBar.class, "FindBar.wholeWordsChoice.text")); // NOI18N
        wholeWordsChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wholeWordsChoiceActionPerformed(evt);
            }
        });

        regularExpressionChoice.setText(org.openide.util.NbBundle.getMessage(FindBar.class, "FindBar.regularExpressionChoice.text")); // NOI18N
        regularExpressionChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regularExpressionChoiceActionPerformed(evt);
            }
        });

        highlightResultsChoice.setText(org.openide.util.NbBundle.getMessage(FindBar.class, "FindBar.highlightResultsChoice.text")); // NOI18N
        highlightResultsChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                highlightResultsChoiceActionPerformed(evt);
            }
        });

        findPreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/bugtracking/ui/resources/find_previous.png"))); // NOI18N
        findPreviousButton.setText(org.openide.util.NbBundle.getMessage(FindBar.class, "FindBar.findPreviousButton.text")); // NOI18N
        findPreviousButton.setBorderPainted(false);
        findPreviousButton.setContentAreaFilled(false);
        findPreviousButton.setFocusable(false);
        findPreviousButton.setMargin(new java.awt.Insets(2, 1, 0, 1));
        findPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findPreviousButtonActionPerformed(evt);
            }
        });
        findPreviousButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                findPreviousButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                findPreviousButtonMouseExited(evt);
            }
        });

        findNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/bugtracking/ui/resources/find_next.png"))); // NOI18N
        findNextButton.setText(org.openide.util.NbBundle.getMessage(FindBar.class, "FindBar.findNextButton.text")); // NOI18N
        findNextButton.setBorderPainted(false);
        findNextButton.setContentAreaFilled(false);
        findNextButton.setFocusable(false);
        findNextButton.setMargin(new java.awt.Insets(2, 1, 0, 1));
        findNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findNextButtonActionPerformed(evt);
            }
        });
        findNextButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                findNextButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                findNextButtonMouseExited(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(findLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(findCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(findPreviousButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(findNextButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(machCaseChoice)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wholeWordsChoice)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regularExpressionChoice)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(highlightResultsChoice)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closeButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(findLabel)
                .addComponent(findCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(machCaseChoice)
                .addComponent(closeButton)
                .addComponent(wholeWordsChoice)
                .addComponent(regularExpressionChoice)
                .addComponent(highlightResultsChoice)
                .addComponent(findPreviousButton)
                .addComponent(findNextButton))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closeButtonMouseEntered
        processMouseEvent(evt, true);
    }//GEN-LAST:event_closeButtonMouseEntered

    private void closeButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closeButtonMouseExited
        processMouseEvent(evt, false);
    }//GEN-LAST:event_closeButtonMouseExited

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        support.cancel();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void findComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findComboActionPerformed
        if (initialized && isVisible()) {
            updateComboModel();
            support.findNext();
        }
    }//GEN-LAST:event_findComboActionPerformed

    private void machCaseChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_machCaseChoiceActionPerformed
        support.updatePattern();
    }//GEN-LAST:event_machCaseChoiceActionPerformed

    private void wholeWordsChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wholeWordsChoiceActionPerformed
        support.updatePattern();
    }//GEN-LAST:event_wholeWordsChoiceActionPerformed

    private void regularExpressionChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regularExpressionChoiceActionPerformed
        wholeWordsChoice.setEnabled(!regularExpressionChoice.isSelected());
        support.updatePattern();
    }//GEN-LAST:event_regularExpressionChoiceActionPerformed

    private void highlightResultsChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_highlightResultsChoiceActionPerformed
        support.switchHighlight(highlightResultsChoice.isSelected());
    }//GEN-LAST:event_highlightResultsChoiceActionPerformed

    private void findNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findNextButtonActionPerformed
        updateComboModel();
        support.findNext();
    }//GEN-LAST:event_findNextButtonActionPerformed

    private void findPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findPreviousButtonActionPerformed
        updateComboModel();
        support.findPrevious();
    }//GEN-LAST:event_findPreviousButtonActionPerformed

    private void findPreviousButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_findPreviousButtonMouseEntered
        processMouseEvent(evt, true);
    }//GEN-LAST:event_findPreviousButtonMouseEntered

    private void findPreviousButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_findPreviousButtonMouseExited
        processMouseEvent(evt, false);
    }//GEN-LAST:event_findPreviousButtonMouseExited

    private void findNextButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_findNextButtonMouseEntered
        processMouseEvent(evt, true);
    }//GEN-LAST:event_findNextButtonMouseEntered

    private void findNextButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_findNextButtonMouseExited
        processMouseEvent(evt, false);
    }//GEN-LAST:event_findNextButtonMouseExited

    private void processMouseEvent(MouseEvent evt, boolean over) {
        Object src = evt.getSource();
        if (src instanceof JButton) {
            JButton button = (JButton)src;
            button.setContentAreaFilled(over);
            button.setBorderPainted(over);
        }
    }

    private void updateComboModel() {
        String pattern = getPattern();
        int idx = -1;
        for (int i=0; i<lastSearchModel.getSize(); i++) {
            if (pattern.equals(lastSearchModel.getElementAt(i))) {
                idx = i;
            }
        }
        if (idx != 0) {
            if (idx != -1) {
                lastSearchModel.removeElementAt(idx);
            }
            lastSearchModel.insertElementAt(pattern, 0);
            if (lastSearchModel.getSize() > MAX_SEARCH_MODEL_SIZE) {
                lastSearchModel.removeElementAt(MAX_SEARCH_MODEL_SIZE);
            }
            findCombo.setSelectedItem(pattern);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JComboBox findCombo;
    private javax.swing.JLabel findLabel;
    private javax.swing.JButton findNextButton;
    private javax.swing.JButton findPreviousButton;
    private javax.swing.JCheckBox highlightResultsChoice;
    private javax.swing.JCheckBox machCaseChoice;
    private javax.swing.JCheckBox regularExpressionChoice;
    private javax.swing.JCheckBox wholeWordsChoice;
    // End of variables declaration//GEN-END:variables

}
