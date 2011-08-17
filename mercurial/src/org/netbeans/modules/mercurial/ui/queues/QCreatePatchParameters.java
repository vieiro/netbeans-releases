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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.mercurial.ui.queues;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import org.netbeans.modules.versioning.util.common.VCSCommitParameters.DefaultCommitParameters;
import java.util.prefs.Preferences;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.mercurial.HgModuleConfig;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Stupka
 */
public class QCreatePatchParameters extends DefaultCommitParameters implements ItemListener, DocumentListener {
    private CommitPanel panel;
    private String commitMessage;
    private String patchName;
    private String errorMessage;

    public QCreatePatchParameters (Preferences preferences, String commitMessage, String patchName) {
        super(preferences);
        this.commitMessage = commitMessage;
        this.patchName = patchName;
    }

    @Override
    public CommitPanel getPanel () {
        if(panel == null) {
            panel = createPanel();   
            panel.txtPatchName.getDocument().addDocumentListener(this);
        }
        return panel;
    }

    @Override
    public JLabel getMessagesTemplateLink (JTextArea text) {
        return super.getMessagesTemplateLink(text);
    }

    @Override
    public JLabel getRecentMessagesLink (JTextArea text) {
        return super.getRecentMessagesLink(text);
    }
    
    @Override
    public Preferences getPreferences () {
        return super.getPreferences();
    }

    @Override
    public String getLastCanceledCommitMessage () {
        return HgModuleConfig.getDefault().getLastCanceledCommitMessage();
    }

    List<String> getCommitMessages () {
        return getRecentCommitMessages(getPreferences());
    }
    
    @Override
    protected CommitPanel createPanel () {
        return new CommitPanel(this, commitMessage, patchName);
    }

    @Override
    public String getCommitMessage () {
        return ((CommitPanel) getPanel()).messageTextArea.getText();
    }
    
    public String getPatchName () {
        return getPanel().txtPatchName.getText().trim();
    }

    @Override
    public boolean isCommitable () {            
        if (getPatchName().isEmpty()) {
            errorMessage = NbBundle.getMessage(QCreatePatchParameters.class, "MSG_WARNING_EMPTY_PATCH_NAME"); //NOI18N
            return false;
        }
        errorMessage = null;
        return true;
    }

    @Override
    public String getErrorMessage () {
        return errorMessage;
    }    

    @Override
    public void itemStateChanged(ItemEvent e) {
        fireChange();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        fireChange();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        fireChange();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        fireChange();
    }

    @Override
    protected String getCommitButtonText () {
        return NbBundle.getMessage(QCreatePatchParameters.class, "CTL_Commit_Action_CreatePatch"); //NOI18N
    }

    @Override
    protected String getCommitButtonAccessibleName () {
        return NbBundle.getMessage(QCreatePatchParameters.class, "ACSN_Commit_Action_CreatePatch"); //NOI18N
    }

    @Override
    protected String getCommitButtonAccessibleDescription () {
        return NbBundle.getMessage(QCreatePatchParameters.class, "ACSD_Commit_Action_CreatePatch"); //NOI18N
    }

    @Override
    protected String getCommitPanelTitle (String contentTitle) {
        return NbBundle.getMessage(QCreatePatchParameters.class, "CTL_CommitDialog_CreatePatch_Title", contentTitle); //NOI18N
    }

}
