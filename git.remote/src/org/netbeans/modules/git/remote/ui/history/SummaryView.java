/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2009 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.git.remote.ui.history;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.modules.git.remote.cli.GitBranch;
import org.netbeans.modules.git.remote.cli.GitRevisionInfo;
import org.netbeans.modules.git.remote.cli.GitTag;
import org.netbeans.modules.git.remote.cli.GitUser;
import org.netbeans.modules.git.remote.Git;
import org.netbeans.modules.git.remote.client.GitProgressSupport;
import org.netbeans.modules.git.remote.options.AnnotationColorProvider;
import org.netbeans.modules.git.remote.ui.diff.DiffAction;
import org.netbeans.modules.git.remote.ui.repository.Revision;
import org.netbeans.modules.git.remote.utils.GitUtils;
import org.netbeans.modules.remotefs.versioning.api.VCSFileProxySupport;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;
import org.netbeans.modules.versioning.core.spi.VCSContext;
import org.netbeans.modules.versioning.history.AbstractSummaryView;
import org.netbeans.modules.versioning.history.AbstractSummaryView.SummaryViewMaster.SearchHighlight;
import org.netbeans.modules.versioning.util.VCSKenaiAccessor.KenaiUser;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

class SummaryView extends AbstractSummaryView {

    private final SearchHistoryPanel master;
    
    private static final DateFormat defaultFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    private static final Color HIGHLIGHT_BRANCH_FG = Color.BLACK;
    private static final Color HIGHLIGHT_TAG_FG = Color.BLACK;
    private static final Color HIGHLIGHT_BRANCH_BG = Color.decode("0xaaffaa"); //NOI18N
    private static final Color HIGHLIGHT_TAG_BG = Color.decode("0xffffaa"); //NOI18N
    
    static final class GitLogEntry extends AbstractSummaryView.LogEntry implements PropertyChangeListener {

        private final RepositoryRevision revision;
        private List<Event> events = new ArrayList<>(10);
        private List<Event> dummyEvents;
        private final SearchHistoryPanel master;
        private String complexRevision;
        private final PropertyChangeListener list;
        private Collection<AbstractSummaryView.LogEntry.RevisionHighlight> complexRevisionHighlights;
    
        public GitLogEntry (RepositoryRevision revision, SearchHistoryPanel master) {
            this.revision = revision;
            this.master = master;
            this.dummyEvents = Collections.<Event>emptyList();
            if (revision.isEventsInitialized()) {
                refreshEvents();
                list = null;
            } else {
                prepareDummyEvents();
                revision.addPropertyChangeListener(RepositoryRevision.PROP_EVENTS_CHANGED, list = WeakListeners.propertyChange(this, revision));
            }
        }

        @Override
        public Collection<AbstractSummaryView.LogEntry.Event> getEvents () {
            return events;
        }

        @Override
        public Collection<Event> getDummyEvents () {
            return dummyEvents;
        }

        @Override
        public String getAuthor () {
            GitUser author = revision.getLog().getAuthor();
            return author == null ? "" : author.toString(); //NOI18N
        }

        @Override
        public String getDate () {
            Date date = new Date(revision.getLog().getCommitTime());
            return defaultFormat.format(date);
        }

        @Override
        public String getRevision () {
            if (complexRevision == null) {
                complexRevisionHighlights = new ArrayList<>(revision.getBranches().length + revision.getTags().length + 1);
                StringBuilder sb = new StringBuilder();
                // add branch labels
                for (GitBranch branch : revision.getBranches()) {
                    if (branch.getName() != GitBranch.NO_BRANCH) {
                        complexRevisionHighlights.add(new AbstractSummaryView.LogEntry.RevisionHighlight(sb.length(), branch.getName().length(), HIGHLIGHT_BRANCH_FG, HIGHLIGHT_BRANCH_BG));
                        sb.append(branch.getName()).append(' ');
                    }
                    if (branch.isActive()) {
                        complexRevisionHighlights.add(new AbstractSummaryView.LogEntry.RevisionHighlight(sb.length(), GitUtils.HEAD.length(), HIGHLIGHT_BRANCH_FG, HIGHLIGHT_BRANCH_BG));
                        sb.append(GitUtils.HEAD).append(' ');
                    }
                }
                // add tag labels
                for (GitTag tag : revision.getTags()) {
                    complexRevisionHighlights.add(new AbstractSummaryView.LogEntry.RevisionHighlight(sb.length(), tag.getTagName().length(), HIGHLIGHT_TAG_FG, HIGHLIGHT_TAG_BG));
                    sb.append(tag.getTagName()).append(' ');
                }
                String rev = revision.getLog().getRevision();
                sb.append(rev.length() > 7 ? rev.substring(0, 7) : rev);
                complexRevision = sb.toString();
            }
            return complexRevision;
        }

        @Override
        protected Collection<AbstractSummaryView.LogEntry.RevisionHighlight> getRevisionHighlights () {
            getRevision();
            return complexRevisionHighlights;
        }

        @Override
        public String getMessage () {
            return revision.getLog().getFullMessage();
        }

        @Override
        public Action[] getActions () {
            List<Action> actions = new ArrayList<>();
            boolean hasParents = revision.getLog().getParents().length > 0;
            
            if (hasParents) {
                actions.add(new AbstractAction(NbBundle.getMessage(SummaryView.class, "CTL_SummaryView_DiffToPrevious_Short")) { //NOI18N
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        diffPrevious(revision, master);
                    }
                });
            }
            actions.addAll(Arrays.asList(revision.getActions()));
            return actions.toArray(new Action[actions.size()]);
        }

        @Override
        public String toString () {
            return revision.toString();
        }

        @Override
        protected void expand () {
            revision.expandEvents();
        }

        @Override
        protected void cancelExpand () {
            revision.cancelExpand();
        }

        @Override
        protected boolean isEventsInitialized () {
            return revision.isEventsInitialized();
        }

        @Override
        public boolean isVisible () {
            return master.applyFilter(revision);
        }

        @Override
        protected boolean isLessInteresting () {
            return getRepositoryRevision().getLog().getParents().length > 1;
        }

        RepositoryRevision getRepositoryRevision () {
            return revision;
        }

        void prepareDummyEvents () {
            ArrayList<Event> evts = new ArrayList<>(revision.getDummyEvents().length);
            for (RepositoryRevision.Event event : revision.getDummyEvents()) {
                evts.add(new GitLogEvent(master, event));
            }
            dummyEvents = evts;
        }

        void refreshEvents () {
            ArrayList<Event> evts = new ArrayList<>(revision.getEvents().length);
            for (RepositoryRevision.Event event : revision.getEvents()) {
                evts.add(new GitLogEvent(master, event));
            }
            List<Event> newEvents = new ArrayList<>(evts);
            events = evts;
            dummyEvents.clear();
            eventsChanged(null, newEvents);
        }

        @Override
        public void propertyChange (PropertyChangeEvent evt) {
            if (RepositoryRevision.PROP_EVENTS_CHANGED.equals(evt.getPropertyName()) && revision == evt.getSource()) {
                refreshEvents();
            }
        }
    }
    
    static class GitLogEvent extends AbstractSummaryView.LogEntry.Event {

        private final RepositoryRevision.Event event;
        private final SearchHistoryPanel master;

        GitLogEvent (SearchHistoryPanel master, RepositoryRevision.Event event) {
            this.master = master;
            this.event = event;
        }

        @Override
        public String getPath () {
            return event.getPath();
        }

        @Override
        public String getOriginalPath () {
            return event.getOriginalPath();
        }

        @Override
        public String getAction () {
            return Character.toString(event.getAction());
        }
        
        public RepositoryRevision.Event getEvent() {
            return event;
        }

        @Override
        public Action[] getUserActions () {
            List<Action> actions = new ArrayList<>();
            boolean hasParents = event.getLogInfoHeader().getLog().getParents().length > 0;
            
            if (hasParents) {
                actions.add(new AbstractAction(NbBundle.getMessage(SummaryView.class, "CTL_SummaryView_DiffToPrevious")) { // NOI18N
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        diffPrevious(event, master);
                    }
                });
            }
            actions.addAll(Arrays.asList(event.getActions(false)));
            return actions.toArray(new Action[actions.size()]);
        }

        @Override
        public boolean isVisibleByDefault () {
            return master.isShowInfo() || event.isUnderRoots();
        }

        @Override
        public String toString () {
            return event.toString();
        }
    }
    
    public SummaryView (SearchHistoryPanel master, List<? extends LogEntry> results, Map<String, KenaiUser> kenaiUserMap) {
        super(createViewSummaryMaster(master), results, kenaiUserMap);
        this.master = master;
    }
    
    private static SummaryViewMaster createViewSummaryMaster (final SearchHistoryPanel master) {
        final Map<String, String> colors = new HashMap<>();
        colors.put("A", GitUtils.getColorString(AnnotationColorProvider.getInstance().ADDED_FILE.getActualColor()));
        colors.put("C", GitUtils.getColorString(AnnotationColorProvider.getInstance().ADDED_FILE.getActualColor()));
        colors.put("R", GitUtils.getColorString(AnnotationColorProvider.getInstance().ADDED_FILE.getActualColor()));
        colors.put("M", GitUtils.getColorString(AnnotationColorProvider.getInstance().MODIFIED_FILE.getActualColor()));
        colors.put("D", GitUtils.getColorString(AnnotationColorProvider.getInstance().REMOVED_FILE.getActualColor()));
        colors.put("?", GitUtils.getColorString(AnnotationColorProvider.getInstance().EXCLUDED_FILE.getActualColor()));

        return new SummaryViewMaster() {

            @Override
            public JComponent getComponent () {
                return master;
            }

            @Override
            public File[] getRoots(){
                List<File> files = new ArrayList<>();
                for(VCSFileProxy proxy : master.getRoots()) {
                    File file = proxy.toFile();
                    if (file != null) {
                        files.add(file);
                    }
                }
                return files.toArray(new File[files.size()]);
            }

            @Override
            public Collection<SearchHighlight> getSearchHighlights () {
                return master.getSearchHighlights();
            }

            @Override
            public Map<String, String> getActionColors () {
                return colors;
            }

            @Override
            public void getMoreResults (PropertyChangeListener callback, int count) {
                master.getMoreRevisions(callback, count);
            }

            @Override
            public boolean hasMoreResults () {
                return master.hasMoreResults();
            }
        };
    }
    
    @Override
    @NbBundle.Messages({
        "LBL_SummaryView.action.diffRevisions=Diff Selected Revisions",
        "LBL_SummaryView.action.diffFiles=Open Selected Files in Diff Tab"
    })
    protected void onPopup (JComponent invoker, Point p, final Object[] selection) {
        JPopupMenu menu = new JPopupMenu();
        
        final RepositoryRevision container;
        final RepositoryRevision.Event[] drev;

        boolean revisionsSelected = false;
        boolean missingFile = false;   
        boolean viewEnabled = false;
        boolean revertEnabled = false;
        final boolean singleSelection = selection.length == 1;
        
        for (Object o : selection) {
            revisionsSelected = true;
            if (!(o instanceof GitLogEntry)) {
                revisionsSelected = false;
            }
        }
        if (revisionsSelected) {
            container = ((GitLogEntry) selection[0]).revision;
            drev = new RepositoryRevision.Event[0];
        } else {
            drev = new RepositoryRevision.Event[selection.length];

            revertEnabled = true;
            for(int i = 0; i < selection.length; i++) {
                if (!(selection[i] instanceof GitLogEvent)) {
                    return;
                }
                drev[i] = ((GitLogEvent) selection[i]).getEvent();
                
                if(!missingFile && drev[i].getFile() == null) {
                    missingFile = true;
                }
                if (drev[i].getFile() != null && drev[i].getAction() != 'D') {
                    // we have something to view
                    viewEnabled = true;
                } else {
                    revertEnabled = false;
                }
                // only one revision can be selected
                if (i > 0) {
                    revertEnabled &= drev[i].getLogInfoHeader() == drev[i - 1].getLogInfoHeader();
                }
            }                
            container = drev[0].getLogInfoHeader();
        }
        boolean hasParents = singleSelection && container.getLog().getParents().length > 0;

        final boolean canView = viewEnabled;
        final boolean canAnnotate = viewEnabled;
        
        if (hasParents) {
            menu.add(new JMenuItem(new AbstractAction(NbBundle.getMessage(SummaryView.class, "CTL_SummaryView_DiffToPrevious")) { // NOI18N
                {
                    setEnabled(singleSelection);
                }
                @Override
                public void actionPerformed(ActionEvent e) {
                    diffPrevious(selection[0], master);
                }
            }));
        }

        if (revisionsSelected) {
            if (singleSelection) {
                for (Action a : container.getActions()) {
                    menu.add(new JMenuItem(a));
                }
            } else if (selection.length == 2) {
                menu.add(new JMenuItem(new AbstractAction(Bundle.LBL_SummaryView_action_diffRevisions()) {
                    @Override
                    public void actionPerformed (ActionEvent e) {
                        VCSFileProxy[] roots = master.getRoots();
                        List<Node> nodes = new ArrayList<>(roots.length);
                        for (final VCSFileProxy root : roots) {
                            nodes.add(new AbstractNode(Children.LEAF, Lookups.fixed(root)) {
                                @Override
                                public String getDisplayName () {
                                    return root.getName();
                                }
                            });
                        }
                        GitRevisionInfo info1 = ((GitLogEntry) selection[0]).getRepositoryRevision().getLog();
                        GitRevisionInfo info2 = ((GitLogEntry) selection[1]).getRepositoryRevision().getLog();
                        SystemAction.get(DiffAction.class).diff(VCSContext.forNodes(nodes.toArray(new Node[nodes.size()])),
                                new Revision(info2.getRevision(), info2.getRevision(), info2.getShortMessage(), info2.getFullMessage()),
                                new Revision(info1.getRevision(), info1.getRevision(), info1.getShortMessage(), info1.getFullMessage()));
                    }
                }));
            }
        } else {
            menu.add(new JMenuItem(new AbstractAction(NbBundle.getMessage(SummaryView.class, "CTL_SummaryView_View")) { // NOI18N
                {
                    setEnabled(canView);
                }
                @Override
                public void actionPerformed(ActionEvent e) {
                    new GitProgressSupport() {
                        @Override
                        protected void perform () {
                            for (RepositoryRevision.Event evt : drev) {
                                if (evt.getFile() != null && evt.getAction() != 'D') {
                                    evt.openFile(false, getProgressMonitor());
                                    if (getProgressMonitor().isCanceled()) {
                                        return;
                                    }
                                }
                            }
                        }
                    }.start(Git.getInstance().getRequestProcessor(), master.getRepository(), NbBundle.getMessage(SummaryView.class, "MSG_SummaryView.openingFilesFromHistory")); //NOI18N
                }
            }));
            menu.add(new JMenuItem(new AbstractAction(NbBundle.getMessage(SummaryView.class, "CTL_SummaryView_ShowAnnotations")) { // NOI18N
                {
                    setEnabled(canAnnotate);
                }
                @Override
                public void actionPerformed(ActionEvent e) {
                    new GitProgressSupport() {
                        @Override
                        protected void perform () {
                            for (RepositoryRevision.Event evt : drev) {
                                if (evt.getFile() != null && evt.getAction() != 'D') {
                                    evt.openFile(true, getProgressMonitor());
                                    if (getProgressMonitor().isCanceled()) {
                                        return;
                                    }
                                }
                            }
                        }
                    }.start(Git.getInstance().getRequestProcessor(), master.getRepository(), NbBundle.getMessage(SummaryView.class, "MSG_SummaryView.openingFilesFromHistory")); //NOI18N
                }
            }));
            if (revertEnabled) {
                menu.add(new JMenuItem(drev[0].getRevertAction(null).createAction(master.getRepository(), drev)));
            }
            menu.add(new JMenuItem(new AbstractAction(Bundle.CTL_Action_ViewCurrent_name()) {
                {
                    setEnabled(canAnnotate);
                }
                @Override
                public void actionPerformed(ActionEvent e) {
                    new GitProgressSupport() {
                        @Override
                        protected void perform () {
                            for (RepositoryRevision.Event evt : drev) {
                                if (evt.getFile() != null && evt.getAction() != 'D') {
                                    VCSFileProxySupport.openFile(evt.getFile().normalizeFile());
                                }
                            }
                        }
                    }.start(Git.getInstance().getRequestProcessor(), master.getRepository(), NbBundle.getMessage(SummaryView.class, "MSG_SummaryView.openingFilesFromHistory")); //NOI18N
                }
            }));
            if (drev.length == 2 && drev[0].getLogInfoHeader() != drev[1].getLogInfoHeader()) {
                menu.add(new JMenuItem(new AbstractAction(Bundle.LBL_SummaryView_action_diffFiles()) {
                    @Override
                    public void actionPerformed (ActionEvent e) {
                        master.showDiff(drev);
                    }
                }));
            }
        }
        menu.show(invoker, p.x, p.y);
    }

    private static void diffPrevious (Object o, SearchHistoryPanel master) {
        if (o instanceof RepositoryRevision.Event) {
            RepositoryRevision.Event drev = (RepositoryRevision.Event) o;
            master.showDiff(drev);
        } else if (o instanceof RepositoryRevision) {
            RepositoryRevision container = (RepositoryRevision) o;
            master.showDiff(container);
        } else if (o instanceof GitLogEvent) {
            master.showDiff(((GitLogEvent) o).event);
        } else if (o instanceof GitLogEntry) {
            master.showDiff(((GitLogEntry) o).revision);
        }
    }

}
