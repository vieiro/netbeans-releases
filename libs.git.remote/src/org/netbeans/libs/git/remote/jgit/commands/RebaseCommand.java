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
package org.netbeans.libs.git.remote.jgit.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RebaseCommand.Operation;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.ResolveMerger;
import org.eclipse.jgit.revwalk.RevCommit;
import org.netbeans.libs.git.remote.GitClient;
import org.netbeans.libs.git.remote.GitException;
import org.netbeans.libs.git.remote.GitRebaseResult;
import org.netbeans.libs.git.remote.GitRevisionInfo;
import org.netbeans.libs.git.remote.GitStatus;
import org.netbeans.libs.git.remote.jgit.DelegatingGitProgressMonitor;
import org.netbeans.libs.git.remote.jgit.DelegatingProgressMonitor;
import org.netbeans.libs.git.remote.jgit.GitClassFactory;
import org.netbeans.libs.git.remote.jgit.JGitRepository;
import org.netbeans.libs.git.remote.jgit.Utils;
import org.netbeans.libs.git.remote.progress.ProgressMonitor;
import org.netbeans.libs.git.remote.progress.StatusListener;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;

/**
 *
 * @author ondra
 */
public class RebaseCommand extends GitCommand {

    private final String revision;
    private GitRebaseResult result;
    private final ProgressMonitor monitor;
    private final GitClient.RebaseOperationType operation;

    public RebaseCommand (JGitRepository repository, GitClassFactory gitFactory, String revision,
            GitClient.RebaseOperationType operation, ProgressMonitor monitor) {
        super(repository, gitFactory, monitor);
        this.revision = revision;
        this.operation = operation;
        this.monitor = monitor;
    }

    @Override
    protected void run () throws GitException {
        Repository repository = getRepository().getRepository();
        org.eclipse.jgit.api.RebaseCommand command = new Git(repository).rebase();
        if (operation == GitClient.RebaseOperationType.BEGIN) {
            Ref ref = null;
            try {
                ref = repository.getRef(revision);
            } catch (IOException ex) {
                throw new GitException(ex);
            }

            if (ref == null) {
                command.setUpstream(Utils.findCommit(repository, revision));
            } else {
                command.setUpstream(ref.getTarget().getObjectId());
                command.setUpstreamName(ref.getName());
            }
        }
        command.setOperation(getOperation(operation));
        command.setProgressMonitor(new DelegatingProgressMonitor(monitor));
        try {
            RebaseResult res = command.call();
            result = createResult(res);
        } catch (GitAPIException ex) {
            throw new GitException(ex);
        }
    }
    
    @Override
    protected void prepare() throws GitException {
        super.prepare();
        addArgument("rebase"); //NOI18N
        if (operation == GitClient.RebaseOperationType.BEGIN) {
            addArgument(revision);
        } else {
            addArgument(operation.toString());
        }
    }

    public GitRebaseResult getResult () {
        return result;
    }

    static Operation getOperation (GitClient.RebaseOperationType operation) {
        return Operation.valueOf(operation.name());
    }

    private GitRebaseResult createResult (RebaseResult res) {
        String currHead;
        Repository repository = getRepository().getRepository();
        VCSFileProxy workTree = getRepository().getLocation();
        try {
            currHead = repository.resolve(Constants.HEAD).name();
        } catch (IOException ex) {
            currHead = Constants.HEAD;
        }
        List<VCSFileProxy> conflicts;
        if (res.getStatus() == RebaseResult.Status.STOPPED) {
            conflicts = getConflicts(res.getCurrentCommit());
        } else {
            conflicts = Collections.<VCSFileProxy>emptyList();
        }
        return getClassFactory().createRebaseResult(res, conflicts, getFailures(res), currHead);
    }

    private List<VCSFileProxy> getConflicts (RevCommit currentCommit) {
        List<VCSFileProxy> conflicts;
        try {
            Repository repository = getRepository().getRepository();
            GitRevisionInfo info = getClassFactory().createRevisionInfo(currentCommit, getRepository());
            Map<VCSFileProxy, GitRevisionInfo.GitFileInfo> modifiedFiles = info.getModifiedFiles();
            ConflictCommand cmd = new ConflictCommand(getRepository(), getClassFactory(), modifiedFiles.keySet().toArray(new VCSFileProxy[modifiedFiles.keySet().size()]),
                    new DelegatingGitProgressMonitor(monitor),
                    new StatusListener() {
                        @Override
                        public void notifyStatus (GitStatus status) { }
                    });
            cmd.execute();
            Map<VCSFileProxy, GitStatus> statuses = cmd.getStatuses();
            conflicts = new ArrayList<VCSFileProxy>(statuses.size());
            for (Map.Entry<VCSFileProxy, GitStatus> e : statuses.entrySet()) {
                if (e.getValue().isConflict()) {
                    conflicts.add(e.getKey());
                }
            }
        } catch (GitException ex) {
            Logger.getLogger(RebaseCommand.class.getName()).log(Level.INFO, null, ex);
            conflicts = Collections.<VCSFileProxy>emptyList();
        }
        return conflicts;
    }

    private List<VCSFileProxy> getFailures (RebaseResult result) {
        List<VCSFileProxy> files = new ArrayList<VCSFileProxy>();
        VCSFileProxy workDir = getRepository().getLocation();
        if (result.getStatus() == RebaseResult.Status.CONFLICTS) {
            List<String> conflicts = result.getConflicts();
            if (conflicts != null) {
                for (String conflict : conflicts) {
                    files.add(VCSFileProxy.createFileProxy(workDir, conflict));
                }
            }
        } else if (result.getStatus() == RebaseResult.Status.FAILED) {
            Map<String, ResolveMerger.MergeFailureReason> obstructions = result.getFailingPaths();
            if (obstructions != null) {
                for (Map.Entry<String, ResolveMerger.MergeFailureReason> failure : obstructions.entrySet()) {
                    files.add(VCSFileProxy.createFileProxy(workDir, failure.getKey()));
                }
            }
        } else if (result.getStatus() == RebaseResult.Status.UNCOMMITTED_CHANGES) {
            List<String> failures = result.getUncommittedChanges();
            if (failures != null) {
                for (String conflict : failures) {
                    files.add(VCSFileProxy.createFileProxy(workDir, conflict));
                }
            }
        }
        return Collections.unmodifiableList(files);
    }
}
