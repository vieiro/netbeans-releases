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

package org.netbeans.modules.bugtracking.vcs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.netbeans.modules.bugtracking.spi.IssueProvider;
import org.netbeans.modules.bugtracking.spi.RepositoryProvider;
import org.netbeans.modules.bugtracking.util.BugtrackingOwnerSupport;
import org.netbeans.modules.bugtracking.util.BugtrackingUtil;
import org.netbeans.modules.bugtracking.util.RepositoryComboSupport;
import org.netbeans.modules.bugtracking.vcs.VCSHooksConfig.Format;
import org.netbeans.modules.bugtracking.vcs.VCSHooksConfig.PushOperation;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Stupka
 */
class HookImpl {
    
    private HookPanel panel;
    static final Logger LOG = Logger.getLogger("org.netbeans.modules.bugtracking.vcshooks");        // NOI18N
    private static final SimpleDateFormat CC_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");// NOI18N
    private final VCSHooksConfig config;
    private String[] supportedIssueInfoVariables;
    private final String[] supportedRevisionVariables;

    HookImpl(VCSHooksConfig config, String[] supportedIssueInfoVariables, String[] supportedRevisionVariables) {
        this.config = config;
        this.supportedIssueInfoVariables = supportedIssueInfoVariables;
        this.supportedRevisionVariables = supportedRevisionVariables;
    }
    
    public String beforeCommit(File[] files, String msg) throws IOException {
        RepositoryProvider selectedRepository = getSelectedRepository();

        if(files.length == 0) {

            if (selectedRepository != null) {
                BugtrackingOwnerSupport.getInstance().setLooseAssociation(
                        BugtrackingOwnerSupport.ContextType.MAIN_OR_SINGLE_PROJECT,
                        selectedRepository);
            }

            LOG.warning("calling beforeCommit for zero files");              // NOI18N
            return null;
        }

        if (selectedRepository != null) {
            BugtrackingOwnerSupport.getInstance().setFirmAssociations(
                    files,
                    selectedRepository);
        }

        File file = files[0];
        LOG.log(Level.FINE, "beforeCommit start for {0}", file);             // NOI18N

        if (isLinkSelected()) {

            Format format = config.getIssueInfoTemplate();
            String formatString = format.getFormat();
            formatString = HookUtils.prepareFormatString(formatString, supportedIssueInfoVariables);
            
            IssueProvider issue = getIssue();
            if (issue == null) {
                LOG.log(Level.FINE, " no issue set for {0}", file);             // NOI18N
                return null;
            }
            String issueInfo = new MessageFormat(formatString).format(
                    new Object[] {issue.getID(), issue.getSummary()},
                    new StringBuffer(),
                    null).toString();

            LOG.log(Level.FINER, " commit hook issue info ''{0}''", issueInfo); // NOI18N
            if(format.isAbove()) {
                msg = issueInfo + "\n" + msg;                                   // NOI18N
            } else {
                msg = msg + "\n" + issueInfo;                                   // NOI18N
            }                        
            return msg;
        }
        return null;
    }
            
    public void afterCommit(File[] files, String author, String revision, Date date, String message, String hookUsageName, boolean applyPush) {
        if(panel == null) {
            LOG.fine("no settings for afterCommit");                            // NOI18N
            return;
        }

        if(files.length == 0) {
            LOG.warning("calling afterCommit for zero files");               // NOI18N
            return;
        }

        File file = files[0];
        LOG.log(Level.FINE, "afterCommit start for {0}", file);              // NOI18N

        IssueProvider issue = getIssue();
        if (issue == null) {
            LOG.log(Level.FINE, " no issue set for {0}", file);                 // NOI18N
            return;
        }

        config.setLink(isLinkSelected());
        config.setResolve(isResolveSelected());
        config.setAfterCommit(isCommitSelected());

        if (!isLinkSelected() &&
            !isResolveSelected())
        {
            LOG.log(Level.FINER, " nothing to do in afterCommit for {0}", file);   // NOI18N
            return;
        }

        String msg = null;
        if(isLinkSelected()) {
            String formatString = config.getRevisionTemplate().getFormat();
            formatString = HookUtils.prepareFormatString(formatString, supportedRevisionVariables); // NOI18N

            msg = new MessageFormat(formatString).format(
                    new Object[] {
                        revision,
                        author,
                        date != null ? CC_DATE_FORMAT.format(date) : "",        // NOI18N
                        message},
                    new StringBuffer(),
                    null).toString();

            LOG.log(Level.FINER, " afterCommit message ''{0}''", msg);       // NOI18N
        }        
        
        LOG.log(Level.FINER, " commit hook message ''{0}'', resolved {1}", new Object[]{msg, isResolveSelected()});     // NOI18N
        if((isLinkSelected() || isResolveSelected() ) && isCommitSelected()) {
            issue.addComment(msg, isResolveSelected());
            issue.open();
        } else if(applyPush) {
            LOG.log(Level.FINER, " commit hook message will be set after push");     // NOI18N            
            config.setPushAction(revision, new PushOperation(issue.getID(), msg, isResolveSelected()));
            LOG.log(Level.FINE, "schedulig issue {0} for file {1}", new Object[]{issue.getID(), file}); // NOI18N
        }
        LOG.log(Level.FINE, "afterCommit end for {0}", file);                // NOI18N
        VCSHooksConfig.logHookUsage(hookUsageName, getSelectedRepository());             // NOI18N
    }
        
    public void afterPush(File[] files, String[] changesets, String hookUsageName) {
        if(files.length == 0) {
            LOG.warning("calling after push for zero files");                   // NOI18N
            return;
        }
        File file = files[0];
        LOG.log(Level.FINE, "push hook start for {0}", file);                   // NOI18N

        RepositoryProvider repo = null;
        for (String changeset : changesets) {

            PushOperation operation = config.popPushAction(changeset);
            if(operation == null) {
                LOG.log(Level.FINE, " no push hook scheduled for {0}", file);   // NOI18N
                continue;
            }

            if(repo == null) { // don't go for the repository until we really need it
                repo = BugtrackingOwnerSupport.getInstance().getRepository(file, true); // true -> ask user if repository unknown
                                                                                        //         might have deleted in the meantime
                if(repo == null) {
                    LOG.log(Level.WARNING, " could not find issue tracker for {0}", file);      // NOI18N
                    break;
                }
            }

            IssueProvider issue = repo.getIssue(operation.getIssueID());
            if(issue == null) {
                LOG.log(Level.FINE, " no issue found with id {0}", operation.getIssueID());  // NOI18N
                continue;
            }

            issue.addComment(operation.getMsg(), operation.isClose());
        }
        LOG.log(Level.FINE, "push hook end for {0}", file);                     // NOI18N
        VCSHooksConfig.logHookUsage(hookUsageName, getSelectedRepository());             // NOI18N
    }

    public HookPanel createComponent(File[] files) {
        return createComponent(files, null);
    }
    public HookPanel createComponent(File[] files, Boolean afterCommit) {
        LOG.finer("HookImpl.createComponent()");                              // NOI18N
        File referenceFile;
        if(files.length == 0) {
            referenceFile = null;
            LOG.warning("creating hook component for zero files");           // NOI18N
        } else {
            referenceFile = files[0];
        }
        
        panel = new HookPanel(
                        config.getLink(),
                        config.getResolve(),
                        afterCommit != null ? afterCommit : config.getAfterCommit());
        
        if (referenceFile != null) {
            RepositoryComboSupport.setup(panel, panel.repositoryComboBox, referenceFile);
        } else {
            RepositoryComboSupport.setup(panel, panel.repositoryComboBox, false);
        }
        panel.changeFormatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onShowFormat();
            }
        });
        return panel;
    }

    private void onShowFormat() {
        FormatPanel p = 
                new FormatPanel(
                    config.getRevisionTemplate(),
                    config.getDefaultRevisionTemplate(),
                    supportedRevisionVariables,
                    config.getIssueInfoTemplate(),
                    config.getDefaultIssueInfoTemplate(),
                    supportedIssueInfoVariables);
        if(BugtrackingUtil.show(p, NbBundle.getMessage(HookPanel.class, "LBL_FormatTitle"), NbBundle.getMessage(HookPanel.class, "LBL_OK"))) {  // NOI18N
            config.setRevisionTemplate(p.getIssueFormat());
            config.setIssueInfoTemplate(p.getCommitFormat());
        }
    }

    private boolean isLinkSelected() {
        return (panel != null) && panel.linkCheckBox.isSelected();
    }

    private boolean isResolveSelected() {
        return (panel != null) && panel.resolveCheckBox.isSelected();
    }

    private boolean isCommitSelected() {
        return (panel != null) && panel.commitRadioButton.isSelected();
    }

    private RepositoryProvider getSelectedRepository() {
        return (panel != null) ? panel.getSelectedRepository() : null;
    }

    private IssueProvider getIssue() {
        return (panel != null) ? panel.getIssue() : null;
    }
}
