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
package org.netbeans.modules.subversion.ui.history;

import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;
import org.openide.nodes.Node;
import org.openide.filesystems.FileUtil;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.settings.FontColorSettings;
import org.netbeans.modules.subversion.util.Context;
import org.netbeans.modules.subversion.Subversion;
import org.netbeans.modules.subversion.RepositoryFile;
import org.netbeans.modules.subversion.VersionsCache;
import org.netbeans.modules.subversion.client.SvnProgressSupport;
import org.netbeans.modules.subversion.ui.update.RevertModifications;
import org.netbeans.modules.subversion.ui.update.RevertModificationsAction;
import org.netbeans.modules.subversion.ui.diff.DiffSetupSource;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import org.netbeans.modules.subversion.FileStatusCache;
import org.netbeans.modules.subversion.kenai.SvnKenaiAccessor;
import org.netbeans.modules.subversion.client.SvnClient;
import org.netbeans.modules.subversion.client.SvnClientExceptionHandler;
import org.netbeans.modules.subversion.ui.diff.Setup;
import org.netbeans.modules.subversion.util.SvnUtils;
import org.netbeans.modules.versioning.util.VCSHyperlinkSupport;
import org.netbeans.modules.versioning.util.VCSHyperlinkSupport.AuthorLinker;
import org.netbeans.modules.versioning.util.VCSHyperlinkSupport.IssueLinker;
import org.netbeans.modules.versioning.util.VCSHyperlinkProvider;
import org.netbeans.modules.versioning.util.VCSHyperlinkSupport.StyledDocumentHyperlink;
import org.netbeans.modules.versioning.util.VCSKenaiAccessor.KenaiUser;
import org.openide.util.Lookup;
import org.tigris.subversion.svnclientadapter.SVNClientException;

/**
 * @author Maros Sandor
 */
/**
 * Shows Search History results in a JList.
 *
 * @author Maros Sandor
 */
class SummaryView implements MouseListener, ComponentListener, MouseMotionListener, DiffSetupSource {

    private static final String SUMMARY_REVERT_PROPERTY = "Summary-Revert-";

    private final SearchHistoryPanel master;

    private JList       resultsList;
    private JScrollPane scrollPane;

    private final List  dispResults;
    private String      message;
    private AttributeSet searchHiliteAttrs;
    private List<RepositoryRevision> results;

    private Map<String, KenaiUser> kenaiUsersMap = null;
    private VCSHyperlinkSupport linkerSupport = new VCSHyperlinkSupport();

    public SummaryView(SearchHistoryPanel master, List<RepositoryRevision> results) {
        this.master = master;
        this.results = results;
        this.dispResults = expandResults(results);
        FontColorSettings fcs = (FontColorSettings) MimeLookup.getMimeLookup("text/x-java").lookup(FontColorSettings.class); // NOI18N
        searchHiliteAttrs = fcs.getFontColors("highlight-search"); // NOI18N
        message = master.getCriteria().getCommitMessage();
        resultsList = new JList(new SummaryListModel());
        resultsList.setFixedCellHeight(-1);
        resultsList.addMouseListener(this);
        resultsList.addMouseMotionListener(this);
        resultsList.setCellRenderer(new SummaryCellRenderer());
        resultsList.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SummaryView.class, "ACSN_SummaryView_List")); // NOI18N
        resultsList.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SummaryView.class, "ACSD_SummaryView_List")); // NOI18N
        scrollPane = new JScrollPane(resultsList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        master.addComponentListener(this);
        resultsList.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_F10, KeyEvent.SHIFT_DOWN_MASK ), "org.openide.actions.PopupAction");
        resultsList.getActionMap().put("org.openide.actions.PopupAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onPopup(org.netbeans.modules.versioning.util.Utils.getPositionForPopup(resultsList));
            }
        });

        if(results.size() > 0) {
            SVNUrl url = results.get(0).getRepositoryRootUrl();
            boolean isKenaiRepository = url != null && SvnKenaiAccessor.getInstance().isKenai(url.toString());
            if(isKenaiRepository) {
                kenaiUsersMap = new HashMap<String, KenaiUser>();
                for (RepositoryRevision repositoryRevision : results) {
                    String author = repositoryRevision.getLog().getAuthor();
                    if(author != null && !author.equals("")) {
                        if(!kenaiUsersMap.keySet().contains(author)) {
                            KenaiUser kenaiUser = SvnKenaiAccessor.getInstance().forName(author, url.toString());
                            if(kenaiUser != null) {
                                kenaiUsersMap.put(author, kenaiUser);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        int [] selection = resultsList.getSelectedIndices();
        resultsList.setModel(new SummaryListModel());
        resultsList.setSelectedIndices(selection);
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // not interested
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        // not interested
    }

    @Override
    public void componentShown(ComponentEvent e) {
        // not interested
    }

    private List expandResults(List<RepositoryRevision> results) {
        ArrayList newResults = new ArrayList(results.size());
        for (RepositoryRevision repositoryRevision : results) {
            newResults.add(repositoryRevision);
            List<RepositoryRevision.Event> events = repositoryRevision.getEvents();
            for (RepositoryRevision.Event event : events) {
                newResults.add(event);
            }
        }
        return newResults;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int idx = resultsList.locationToIndex(e.getPoint());
        if (idx == -1) return;
        Rectangle rect = resultsList.getCellBounds(idx, idx);
        Point p = new Point(e.getX() - rect.x, e.getY() - rect.y);
        Rectangle diffBounds = (Rectangle) resultsList.getClientProperty("Summary-Diff-" + idx); // NOI18N
        if (diffBounds != null && diffBounds.contains(p)) {
            diffPrevious(idx);
        }
        diffBounds = (Rectangle) resultsList.getClientProperty(SUMMARY_REVERT_PROPERTY + idx); // NOI18N
        if (diffBounds != null && diffBounds.contains(p)) {
            revertModifications(new int [] { idx });
        }

        linkerSupport.mouseClicked(p, idx);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // not interested
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // not interested
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            onPopup(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            onPopup(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        resultsList.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        resultsList.setToolTipText("");

        int idx = resultsList.locationToIndex(e.getPoint());
        if (idx == -1) return;
//        resultsList.setToolTipText("");
        Rectangle rect = resultsList.getCellBounds(idx, idx);
        Point p = new Point(e.getX() - rect.x, e.getY() - rect.y);
        Rectangle diffBounds = (Rectangle) resultsList.getClientProperty("Summary-Diff-" + idx); // NOI18N
        if (diffBounds != null && diffBounds.contains(p)) {
            resultsList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }
        diffBounds = (Rectangle) resultsList.getClientProperty(SUMMARY_REVERT_PROPERTY + idx); // NOI18N
        if (diffBounds != null && diffBounds.contains(p)) {
            resultsList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }

        linkerSupport.mouseMoved(p, resultsList, idx);
    }

    @Override
    public Collection<Setup> getSetups() {
        Node [] nodes = TopComponent.getRegistry().getActivatedNodes();
        if (nodes.length == 0) {
            return master.getSetups(results.toArray(new RepositoryRevision[results.size()]), new RepositoryRevision.Event[0]);
        }

        Set<RepositoryRevision.Event> events = new HashSet<RepositoryRevision.Event>();
        Set<RepositoryRevision> revisions = new HashSet<RepositoryRevision>();

        int [] sel = resultsList.getSelectedIndices();
        for (int i : sel) {
            Object revCon = dispResults.get(i);
            if (revCon instanceof RepositoryRevision) {
                revisions.add((RepositoryRevision) revCon);
            } else {
                events.add((RepositoryRevision.Event) revCon);
            }
        }
        return master.getSetups(revisions.toArray(new RepositoryRevision[revisions.size()]), events.toArray(new RepositoryRevision.Event[events.size()]));
    }

    @Override
    public String getSetupDisplayName() {
        return null;
    }

    private boolean isMixedSelection(List dispResults, int[] selection) {
        if(selection.length < 1) return false;
        Class c = dispResults.get(selection[0]).getClass();
        for(int i = 0; i < selection.length; i++) {
            if(!c.equals(dispResults.get(selection[i]).getClass())) return true;
        }
        return false;
    }

    private void onPopup(MouseEvent e) {
        onPopup(e.getPoint());
    }

    private void onPopup(Point p) {
        int [] sel = resultsList.getSelectedIndices();
        if (sel.length == 0) {
            int idx = resultsList.locationToIndex(p);
            if (idx == -1) return;
            resultsList.setSelectedIndex(idx);
            sel = new int [] { idx };
        }

        final int [] selection = sel;
//        if(isMixedSelection(dispResults, selection)) return;

        JPopupMenu menu = new JPopupMenu();

        String previousRevision = null;
        RepositoryRevision container = null;
        List<RepositoryRevision.Event> drevList;
        Object revCon = dispResults.get(selection[0]);


        boolean noExDeletedExistingFiles = true;
        boolean revisionSelected;
        boolean missingFile = false;
        boolean oneRevisionMultiselected = true;
        boolean deleted = false;

        if (revCon instanceof RepositoryRevision) {
            revisionSelected = true;
            container = (RepositoryRevision) dispResults.get(selection[0]);
            drevList = new ArrayList<RepositoryRevision.Event>(0);
            oneRevisionMultiselected = true;
            noExDeletedExistingFiles = true;
        } else {
            revisionSelected = false;
            drevList = new ArrayList<RepositoryRevision.Event>(selection.length);
            for(int i = 0; i < selection.length; i++) {
                if (!(dispResults.get(selection[i]) instanceof RepositoryRevision.Event)) {
                    revisionSelected = true;
                    continue;
                }

                RepositoryRevision.Event event = (RepositoryRevision.Event) dispResults.get(selection[i]);
                drevList.add(event);
                File file = event.getFile();

                if(!deleted && file != null && !file.exists() && event.getChangedPath().getAction() == 'D') {
                    deleted = true;
                }
                if(!missingFile && event.getFile() == null) {
                    missingFile = true;
                }
                if(oneRevisionMultiselected && i > 0 &&
                   drevList.get(0).getLogInfoHeader().getLog().getRevision().getNumber() != drevList.get(0).getLogInfoHeader().getLog().getRevision().getNumber())
                {
                    oneRevisionMultiselected = false;
                }
                if(file != null && file.exists() && event.getChangedPath().getAction() == 'D') {
                    noExDeletedExistingFiles = false;
                }
            }
            container = drevList.get(0).getLogInfoHeader();
        }
        final RepositoryRevision.Event[] drev = drevList.toArray(new RepositoryRevision.Event[drevList.size()]);
        long revision = container.getLog().getRevision().getNumber();

        final boolean rollbackToEnabled = !deleted && !missingFile && !revisionSelected && oneRevisionMultiselected;
        final boolean rollbackChangeEnabled = !missingFile && oneRevisionMultiselected && (drev.length == 0 || noExDeletedExistingFiles); // drev.length == 0 => the whole revision was selected
        final boolean viewEnabled = selection.length == 1 && !revisionSelected && drev[0].getFile() != null && !drev[0].getFile().isDirectory() && drev[0].getChangedPath().getAction() != 'D';
        final boolean diffToPrevEnabled = selection.length == 1;

        if (revision > 1) {
            menu.add(new JMenuItem(new AbstractAction(NbBundle.getMessage(SummaryView.class, "CTL_SummaryView_DiffToPrevious", previousRevision)) { // NOI18N
                {
                    setEnabled(diffToPrevEnabled);
                }
                @Override
                public void actionPerformed(ActionEvent e) {
                    diffPrevious(selection[0]);
                }
            }));
        }

        menu.add(new JMenuItem(new AbstractAction(NbBundle.getMessage(SummaryView.class, "CTL_SummaryView_RollbackChange")) { // NOI18N
            {
                setEnabled(rollbackChangeEnabled);
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                revertModifications(selection);
            }
        }));

        if (!revisionSelected) {
            menu.add(new JMenuItem(new AbstractAction(NbBundle.getMessage(SummaryView.class, "CTL_SummaryView_RollbackTo", revision)) { // NOI18N
                {
                    setEnabled(rollbackToEnabled);
                }
                @Override
                public void actionPerformed(ActionEvent e) {
                    Subversion.getInstance().getParallelRequestProcessor().post(new Runnable() {
                        @Override
                        public void run() {
                            rollback(drev);
                        }
                    });
                }
            }));
            menu.add(new JMenuItem(new AbstractAction(NbBundle.getMessage(SummaryView.class, "CTL_SummaryView_View")) { // NOI18N
                {
                    setEnabled(viewEnabled);
                }
                @Override
                public void actionPerformed(ActionEvent e) {
                    Subversion.getInstance().getParallelRequestProcessor().post(new Runnable() {
                        @Override
                        public void run() {
                            view(selection[0], false);
                        }
                    });
                }
            }));
            menu.add(new JMenuItem(new AbstractAction(NbBundle.getMessage(SummaryView.class, "CTL_SummaryView_ShowAnnotations")) { // NOI18N
                {
                    setEnabled(viewEnabled);
                }
                @Override
                public void actionPerformed(ActionEvent e) {
                    Subversion.getInstance().getParallelRequestProcessor().post(new Runnable() {
                        @Override
                        public void run() {
                            view(selection[0], true);
                        }
                    });
                }
            }));
        }

        menu.show(resultsList, p.x, p.y);
    }

    /**
     * Overwrites local file with this revision.
     *
     * @param event
     */
    static void rollback(RepositoryRevision.Event event) {
        rollback(new RepositoryRevision.Event[ ]{event});
    }

    /**
     * Overwrites local file with this revision.
     *
     * @param event
     */
    static void rollback(final RepositoryRevision.Event[] events) {
        // TODO: confirmation
        SVNUrl repository = events[0].getLogInfoHeader().getRepositoryRootUrl();
        RequestProcessor rp = Subversion.getInstance().getRequestProcessor(repository);
        SvnProgressSupport support = new SvnProgressSupport() {
            @Override
            public void perform() {
                for(RepositoryRevision.Event event : events) {
                    rollback(event);
                }
            }

            private void rollback (RepositoryRevision.Event event) {
                File file = event.getFile();
                if (event.getChangedPath().getAction() == 'D') {
                    // it was deleted, lets delete it again
                    if (file.exists()) {
                        try {
                            SvnClient client = Subversion.getInstance().getClient(false);
                            client.remove(new File[]{file}, true);
                        } catch (SVNClientException ex) {
                            Subversion.LOG.log(Level.SEVERE, null, ex);
                        }
                        Subversion.getInstance().getStatusCache().refresh(file, FileStatusCache.REPOSITORY_STATUS_UNKNOWN);
                    }
                    return;
                }
                File parent = file.getParentFile();
                parent.mkdirs();
                SVNUrl repoUrl = event.getLogInfoHeader().getRepositoryRootUrl();
                SVNUrl fileUrl = repoUrl.appendPath(event.getChangedPath().getPath());
                try {
                    File oldFile = VersionsCache.getInstance().getFileRevision(repoUrl, fileUrl, Long.toString(event.getLogInfoHeader().getLog().getRevision().getNumber()), event.getFile().getName());
                    for (int i = 1; i < 7; i++) {
                        if (file.delete()) {
                            break;
                        }
                        try {
                            Thread.sleep(i * 34);
                        } catch (InterruptedException e) {
                        }
                    }
                    FileUtil.copyFile(FileUtil.toFileObject(oldFile), FileUtil.toFileObject(parent), file.getName(), "");
                } catch (IOException e) {
                    if (refersToDirectory(e)) {
                        Subversion.LOG.log(Level.FINE, null, e);
                        getLogger().logError(NbBundle.getMessage(SummaryView.class, "MSG_SummaryView.refersToDirectory", fileUrl)); //NOI18N
                    } else {
                        Subversion.LOG.log(Level.SEVERE, null, e);
                    }
                }
            }
        };
        support.start(rp, repository, NbBundle.getMessage(SummaryView.class, "MSG_Rollback_Progress")); // NOI18N
    }

    private static boolean refersToDirectory (Exception ex) {
        Throwable t = ex;
        boolean dir = false;
        while (t != null && !(dir = t.getMessage().contains("refers to a directory"))) { //NOI18N
            t = t.getCause();
        }
        return dir;
    }

    private void revertModifications(int[] selection) {
        Set<RepositoryRevision.Event> events = new HashSet<RepositoryRevision.Event>();
        Set<RepositoryRevision> revisions = new HashSet<RepositoryRevision>();
        for (int idx : selection) {
            Object o = dispResults.get(idx);
            if (o instanceof RepositoryRevision) {
                revisions.add((RepositoryRevision) o);
            } else {
                events.add((RepositoryRevision.Event) o);
            }
        }
        revert(master, revisions.toArray(new RepositoryRevision[revisions.size()]), (RepositoryRevision.Event[]) events.toArray(new RepositoryRevision.Event[events.size()]));
    }

    static void revert(final SearchHistoryPanel master, final RepositoryRevision [] revisions, final RepositoryRevision.Event [] events) {
        SVNUrl url;
        try {
            url = master.getSearchRepositoryRootUrl();
        } catch (SVNClientException ex) {
            SvnClientExceptionHandler.notifyException(ex, true, true);
            return;
        }
        RequestProcessor rp = Subversion.getInstance().getRequestProcessor(url);
        SvnProgressSupport support = new SvnProgressSupport() {
            @Override
            public void perform() {
                revertImpl(master, revisions, events, this);
            }
        };
        support.start(rp, url, NbBundle.getMessage(SummaryView.class, "MSG_Revert_Progress")); // NOI18N
    }

    private static void revertImpl(SearchHistoryPanel master, RepositoryRevision[] revisions, RepositoryRevision.Event[] events, SvnProgressSupport progress) {
        SVNUrl url;
        try {
            url = master.getSearchRepositoryRootUrl();
        } catch (SVNClientException ex) {
            SvnClientExceptionHandler.notifyException(ex, true, true);
            return;
        }
        final RepositoryFile repositoryFile = new RepositoryFile(url, url, SVNRevision.HEAD);
        for (RepositoryRevision revision : revisions) {
            RevertModifications.RevisionInterval revisionInterval = new RevertModifications.RevisionInterval(revision.getLog().getRevision());
            final Context ctx = new Context(master.getRoots());
            RevertModificationsAction.performRevert(revisionInterval, false, ctx, progress);
        }
        for (RepositoryRevision.Event event : events) {
            if (event.getFile() == null) continue;
            RevertModifications.RevisionInterval revisionInterval = new RevertModifications.RevisionInterval(event.getLogInfoHeader().getLog().getRevision());
            final Context ctx = new Context(event.getFile());
            RevertModificationsAction.performRevert(revisionInterval, false, ctx, progress);
        }
    }

    private void view(int idx, boolean showAnnotations) {
        Object o = dispResults.get(idx);
        if (o instanceof RepositoryRevision.Event) {
            RepositoryRevision.Event drev = (RepositoryRevision.Event) o;
            File originFile = drev.getFile();
            SVNRevision rev = drev.getLogInfoHeader().getLog().getRevision();
            SVNUrl repoUrl = drev.getLogInfoHeader().getRepositoryRootUrl();
            SVNUrl fileUrl = repoUrl.appendPath(drev.getChangedPath().getPath());
            SvnUtils.openInRevision(originFile, repoUrl, fileUrl, rev, rev, showAnnotations);
        }
    }
    private void diffPrevious(int idx) {
        Object o = dispResults.get(idx);
        if (o instanceof RepositoryRevision.Event) {
            RepositoryRevision.Event drev = (RepositoryRevision.Event) o;
            master.showDiff(drev);
        } else {
            RepositoryRevision container = (RepositoryRevision) o;
            master.showDiff(container);
        }
    }

    public JComponent getComponent() {
        return scrollPane;
    }

    private class SummaryListModel extends AbstractListModel {

        @Override
        public int getSize() {
            return dispResults.size();
        }

        @Override
        public Object getElementAt(int index) {
            return dispResults.get(index);
        }
    }

    private class SummaryCellRenderer extends JPanel implements ListCellRenderer {
        private static final String FIELDS_SEPARATOR = "        "; // NOI18N

        private RevisionRenderer cr = new RevisionRenderer();
        private ChangepathRenderer rr = new ChangepathRenderer();

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof RepositoryRevision) {
                return cr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            } else {
                return rr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        }

        private class RevisionRenderer extends JPanel implements ListCellRenderer {

            private static final double DARKEN_FACTOR = 0.95;

            private Style selectedStyle;
            private Style normalStyle;
            private Style filenameStyle;
            private Style indentStyle;
            private Style noindentStyle;
            private Style hiliteStyle;
            private Style issueHyperlinkStyle;
            private final Style authorStyle;

            private Color selectionBackground;
            private Color selectionForeground;

            private JTextPane textPane = new JTextPane();
            private JPanel    actionsPane = new JPanel();

            private DateFormat defaultFormat;

            private int             index;
            private HyperlinkLabel  diffLink;
            private HyperlinkLabel  revertLink;

            public RevisionRenderer() {
                selectionBackground = new JList().getSelectionBackground();
                selectionForeground = new JList().getSelectionForeground();

                selectedStyle = textPane.addStyle("selected", null); // NOI18N
                StyleConstants.setForeground(selectedStyle, selectionForeground); // NOI18N
                StyleConstants.setBackground(selectedStyle, selectionBackground); // NOI18N
                normalStyle = textPane.addStyle("normal", null); // NOI18N
                StyleConstants.setForeground(normalStyle, UIManager.getColor("List.foreground")); // NOI18N
                filenameStyle = textPane.addStyle("filename", normalStyle); // NOI18N
                StyleConstants.setBold(filenameStyle, true);
                indentStyle = textPane.addStyle("indent", null); // NOI18N
                StyleConstants.setLeftIndent(indentStyle, 50);
                noindentStyle = textPane.addStyle("noindent", null); // NOI18N
                StyleConstants.setLeftIndent(noindentStyle, 0);
                defaultFormat = DateFormat.getDateTimeInstance();

                issueHyperlinkStyle = textPane.addStyle("issuehyperlink", normalStyle); //NOI18N
                StyleConstants.setForeground(issueHyperlinkStyle, Color.BLUE);
                StyleConstants.setUnderline(issueHyperlinkStyle, true);

                authorStyle = textPane.addStyle("author", normalStyle); //NOI18N
                StyleConstants.setForeground(authorStyle, Color.BLUE);

                hiliteStyle = textPane.addStyle("hilite", normalStyle); // NOI18N
                Color c = (Color) searchHiliteAttrs.getAttribute(StyleConstants.Background);
                if (c != null) StyleConstants.setBackground(hiliteStyle, c);
                c = (Color) searchHiliteAttrs.getAttribute(StyleConstants.Foreground);
                if (c != null) StyleConstants.setForeground(hiliteStyle, c);

                setLayout(new BorderLayout());
                add(textPane);
                add(actionsPane, BorderLayout.PAGE_END);
                actionsPane.setLayout(new FlowLayout(FlowLayout.TRAILING, 2, 5));

                diffLink = new HyperlinkLabel();
                diffLink.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
                actionsPane.add(diffLink);

                revertLink = new HyperlinkLabel();
                actionsPane.add(revertLink);

                textPane.setBorder(null);
            }

            public Color darker(Color c) {
                return new Color(Math.max((int)(c.getRed() * DARKEN_FACTOR), 0),
                     Math.max((int)(c.getGreen() * DARKEN_FACTOR), 0),
                     Math.max((int)(c.getBlue() * DARKEN_FACTOR), 0));
            }

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                RepositoryRevision revision = (RepositoryRevision) value;
                StyledDocument sd = textPane.getStyledDocument();

                Style style;
                Color backgroundColor;
                Color foregroundColor;

                if (isSelected) {
                    foregroundColor = selectionForeground;
                    backgroundColor = selectionBackground;
                    style = selectedStyle;
                } else {
                    foregroundColor = UIManager.getColor("List.foreground"); // NOI18N
                    backgroundColor = UIManager.getColor("List.background"); // NOI18N
                    backgroundColor = darker(backgroundColor);
                    style = normalStyle;
                }
                textPane.setBackground(backgroundColor);
                actionsPane.setBackground(backgroundColor);

                this.index = index;

                // XXX cache
                Lookup.Result<VCSHyperlinkProvider> hpResult = Lookup.getDefault().lookupResult(VCSHyperlinkProvider.class);
                Collection<VCSHyperlinkProvider> hpInstances = (Collection<VCSHyperlinkProvider>) hpResult.allInstances();

                try {
                    // clear document
                    sd.remove(0, sd.getLength());
                    sd.setParagraphAttributes(0, sd.getLength(), noindentStyle, false);

                    // add revision
                    sd.insertString(0, Long.toString(revision.getLog().getRevision().getNumber()), style);
                    sd.setCharacterAttributes(0, sd.getLength(), filenameStyle, false);

                    // add author
                    sd.insertString(sd.getLength(), FIELDS_SEPARATOR, style);
                    String author = revision.getLog().getAuthor();
                    StyledDocumentHyperlink l = linkerSupport.getLinker(AuthorLinker.class, index);
                    if(l == null) {
                        if(kenaiUsersMap != null && author != null && !author.equals("")) {
                            KenaiUser kenaiUser = kenaiUsersMap.get(author);
                            if(kenaiUser != null) {
                                l = new AuthorLinker(kenaiUser, authorStyle, sd, author);
                                linkerSupport.add(l, index);
                            }
                        }
                    }
                    if(l != null) {
                        l.insertString(sd, isSelected ? style : null);
                    } else {
                        sd.insertString(sd.getLength(), author, style);
                    }

                    // add date
                    Date date = revision.getLog().getDate();
                    if (date != null) {
                        sd.insertString(sd.getLength(), FIELDS_SEPARATOR + defaultFormat.format(date), style);
                    }

                    // add commit msg
                    String commitMessage = revision.getLog().getMessage();
                    if (commitMessage == null) commitMessage = "";
                    if (commitMessage.endsWith("\n")) commitMessage = commitMessage.substring(0, commitMessage.length() - 1); // NOI18N
                    sd.insertString(sd.getLength(), "\n", style);

                    // compute issue hyperlinks
                    l = linkerSupport.getLinker(IssueLinker.class, index);
                    if(l == null) {
                        for (VCSHyperlinkProvider hp : hpInstances) {
                            l = IssueLinker.create(hp, issueHyperlinkStyle, master.getRoots()[0], sd, commitMessage);
                            if(l != null) {
                                linkerSupport.add(l, index);
                                break; // get the first one
                            }
                        }
                    }
                    if(l != null) {
                        l.insertString(sd, style);
                    } else {
                        sd.insertString(sd.getLength(), commitMessage, style);
                    }

                    int msglen = commitMessage.length();
                    int doclen = sd.getLength();
                    if (message != null && !isSelected) {
                        int idx = commitMessage.indexOf(message);
                        if (idx != -1) {
                            sd.setCharacterAttributes(doclen - msglen + idx, message.length(), hiliteStyle, true);
                        }
                    }

                    resizePane(commitMessage, list.getFontMetrics(list.getFont()));
                    if(isSelected) {
                        sd.setCharacterAttributes(0, Integer.MAX_VALUE, style, false);
                    }
                } catch (BadLocationException e) {
                    Subversion.LOG.log(Level.SEVERE, null, e);
                }

                actionsPane.setVisible(true);
                diffLink.set(NbBundle.getMessage(SummaryView.class, "CTL_Action_Diff"), foregroundColor, backgroundColor);
                revertLink.set(NbBundle.getMessage(SummaryView.class, "CTL_Action_Revert"), foregroundColor, backgroundColor); // NOI18N

                return this;
            }

            private void resizePane(String text, FontMetrics fm) {
                if(text == null) {
                    text = "";
                }
                int width = master.getWidth();
                if (width > 0) {
                    Rectangle2D rect = fm.getStringBounds(text, textPane.getGraphics());
                    int nlc, i;
                    for (nlc = -1, i = 0; i != -1 ; i = text.indexOf('\n', i + 1), nlc++);
                    nlc++;
                    int lines = (int) (rect.getWidth() / (width - 80) + 1);
                    int ph = fm.getHeight() * (lines + nlc) + 0;
                    textPane.setPreferredSize(new Dimension(width - 50, ph));
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (index == -1) return;
                Rectangle apb = actionsPane.getBounds();

                Rectangle bounds = diffLink.getBounds();
                bounds.setBounds(bounds.x, bounds.y + apb.y, bounds.width, bounds.height);
                resultsList.putClientProperty("Summary-Diff-" + index, bounds); // NOI18N

                bounds = revertLink.getBounds();
                bounds.setBounds(bounds.x, bounds.y + apb.y, bounds.width, bounds.height);
                resultsList.putClientProperty(SUMMARY_REVERT_PROPERTY + index, bounds); // NOI18N

                linkerSupport.computeBounds(textPane, index);
            }

        }

        private class ChangepathRenderer extends DefaultListCellRenderer {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                RepositoryRevision.Event revisionEvent = (RepositoryRevision.Event) value;
                StringBuilder sb = new StringBuilder();
                sb.append(FIELDS_SEPARATOR);
                sb.append(String.valueOf(revisionEvent.getChangedPath().getAction()));
                sb.append(FIELDS_SEPARATOR);
                sb.append(revisionEvent.getChangedPath().getPath());
                Component renderer = super.getListCellRendererComponent(list, sb.toString(), index, isSelected, isSelected);
                if(renderer instanceof JLabel) {
                    ((JLabel) renderer).setToolTipText(sb.toString());
                }
                return renderer;
            }
        }
    }

    private static class HyperlinkLabel extends JLabel {

        public HyperlinkLabel() {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        public void set(String text, Color foreground, Color background) {
            StringBuilder sb = new StringBuilder(100);
            if (foreground.equals(UIManager.getColor("List.foreground"))) { // NOI18N
                sb.append("<html><a href=\"\">"); // NOI18N
                sb.append(text);
                sb.append("</a>"); // NOI18N
            } else {
                sb.append("<html><a href=\"\" style=\"color:"); // NOI18N
                sb.append("rgb("); // NOI18N
                sb.append(foreground.getRed());
                sb.append(","); // NOI18N
                sb.append(foreground.getGreen());
                sb.append(","); // NOI18N
                sb.append(foreground.getBlue());
                sb.append(")"); // NOI18N
                sb.append("\">"); // NOI18N
                sb.append(text);
                sb.append("</a>"); // NOI18N
            }
            setText(sb.toString());
            setBackground(background);
        }
    }

}
