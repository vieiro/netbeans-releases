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

package org.netbeans.modules.bugtracking.ui.search;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallbackSystemAction;
import org.openide.util.actions.SystemAction;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jan Stola
 */
public class FindSupport {
    private TopComponent tc;
    private FindBar bar;
    // Highlighters
    private  Highlighter.HighlightPainter highlighterAll;
    private  Highlighter.HighlightPainter highlighterCurrent;
    // Current search details
    private Pattern pattern;
    private JTextComponent currentComp;
    private int currentStart;
    private int currentEnd;

    private FindSupport(TopComponent tc) {
        this.tc = tc;
        bar = new FindBar(this);
        ActionMap actionMap = tc.getActionMap();
        CallbackSystemAction a = SystemAction.get(org.openide.actions.FindAction.class);
        actionMap.put(a.getActionMapKey(), new FindAction());
        // PENDING the colors below should not be hardcoded
        highlighterAll = new DefaultHighlighter.DefaultHighlightPainter(new Color(255,180,66));
        highlighterCurrent = new DefaultHighlighter.DefaultHighlightPainter(new Color(176,197,227));
        pattern = Pattern.compile("$^"); // NOI18N
    }

    public static FindSupport create(TopComponent tc) {
        return new FindSupport(tc);
    }

    public JComponent getFindBar() {
        return bar;
    }

    void reset() {
        highlight(tc, true);
        currentComp = null;
    }

    void updatePattern() {
        reset();
        String p = bar.getPattern();
        if (!bar.getRegularExpression()) {
            p = Pattern.quote(p);
            if (bar.getWholeWords()) {
                p="\\b"+p+"\\b"; // NOI18N
            }
        }
        int flags = Pattern.MULTILINE;
        if (!bar.getMatchCase()) {
            flags |= Pattern.CASE_INSENSITIVE;
        }
        try {
            pattern = Pattern.compile(p, flags);
        } catch (PatternSyntaxException psex) {
            String message = NbBundle.getMessage(FindSupport.class, "FindBar.invalidExpression"); // NOI18N
            StatusDisplayer.getDefault().setStatusText(message, StatusDisplayer.IMPORTANCE_FIND_OR_REPLACE);
        }
        findNext();
        if (bar.getHighlightResults()) {
            highlight(tc, false);
        }
    }

    void findNext() {
        boolean found = false;
        if (currentComp != null) {
            highlight(tc, true);
            found = findNext(tc);
        }
        if (!found) {
            currentComp = null;
            findNext(tc);
        }
        if (currentComp != null && bar.getHighlightResults()) {
            highlight(tc, false);
        }
    }

    private boolean findNext(Component comp) {
        if (comp == bar) {
            return false;
        }
        if (comp instanceof JTextPane) {
            if (currentComp == null || currentComp == comp) {
                JTextPane tcomp = (JTextPane)comp;
                String txt = tcomp.getText();
                Matcher matcher = pattern.matcher(txt);
                int idx = (currentComp==null) ? 0 : currentEnd;
                if (matcher.find(idx)) {
                    currentComp = tcomp;
                    currentStart = matcher.start();
                    currentEnd = matcher.end();
                    if (currentStart == currentEnd) {
                        currentComp = null;
                    } else {
                        try {
                            Highlighter highlighter = tcomp.getHighlighter();
                            highlighter.addHighlight(currentStart, currentEnd, highlighterCurrent);
                            scrollToCurrent();
                        } catch (BadLocationException blex) {
                            blex.printStackTrace();
                        }
                        return true;
                    }
                } else {
                    currentComp = null;
                }
            }
        } else if (comp instanceof Container) {
            Container cont = (Container)comp;
            for (Component subComp : cont.getComponents()) {
                if (findNext(subComp)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void scrollToCurrent() {
        try {
            Rectangle r1 = currentComp.modelToView(currentStart);
            Rectangle r2 = currentComp.modelToView(currentStart);
            Rectangle r = r1.union(r2);
            currentComp.scrollRectToVisible(r);
        } catch (BadLocationException blex) {
            blex.printStackTrace();
        }
    }

    void findPrevious() {
        boolean found = false;
        if (currentComp != null) {
            highlight(tc, true);
            found = findPrevious(tc);
        }
        if (!found) {
            currentComp = null;
            findPrevious(tc);
        }
        if (currentComp != null && bar.getHighlightResults()) {
            highlight(tc, false);
        }
    }

    private boolean findPrevious(Component comp) {
        if (comp == bar) {
            return false;
        }
        if (comp instanceof JTextPane) {
            if (currentComp == null || currentComp == comp) {
                JTextPane tcomp = (JTextPane)comp;
                String txt = tcomp.getText();
                Matcher matcher = pattern.matcher(txt);
                Highlighter highlighter = tcomp.getHighlighter();
                int lastStart = -1;
                int lastEnd = -1;
                while (true) {
                    boolean found = matcher.find((lastEnd==-1) ? 0 : lastEnd);
                    if (found && ((currentComp == null) || (matcher.end()<=currentStart))) {
                        lastStart = matcher.start();
                        lastEnd = matcher.end();
                        if (lastStart == lastEnd) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (lastEnd == -1 || lastStart == lastEnd) {
                    currentComp = null;
                } else {
                    currentComp = tcomp;
                    currentStart = lastStart;
                    currentEnd = lastEnd;
                    try {
                        highlighter.addHighlight(currentStart, currentEnd, highlighterCurrent);
                        scrollToCurrent();
                    } catch (BadLocationException blex) {
                        blex.printStackTrace();
                    }
                    return true;
                }
            }
        } else if (comp instanceof Container) {
            Container cont = (Container)comp;
            Component[] comps = cont.getComponents();
            for (int i=comps.length-1; i>=0; i--) {
                if (findPrevious(comps[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    void cancel() {
        if (currentComp != null) {
            currentComp.requestFocus();
        }
        reset();
        bar.setVisible(false);
    }

    void switchHighlight(boolean on) {
        if (!on) {
            highlight(tc, true);
        }
        if (currentComp != null) {
            try {
                currentComp.getHighlighter().addHighlight(currentStart, currentEnd, highlighterCurrent);
            } catch (BadLocationException blex) {
                blex.printStackTrace();
            }
        }
        if (on) {
            highlight(tc, false);
        }
    }

    private void highlight(Component comp, boolean cancel) {
        if (comp == bar) {
            return;
        }
        if (comp instanceof JTextPane) {
            JTextPane tcomp = (JTextPane)comp;
            String txt = tcomp.getText();
            Matcher matcher = pattern.matcher(txt);
            Highlighter highlighter = tcomp.getHighlighter();
            if (cancel) {
                highlighter.removeAllHighlights();
            } else {
                int idx = 0;
                while (matcher.find(idx)) {
                    int start = matcher.start();
                    int end = matcher.end();
                    if (start == end) {
                        break;
                    }
                    try {
                        highlighter.addHighlight(start, end, highlighterAll);
                    } catch (BadLocationException blex) {
                        blex.printStackTrace();
                    }
                    idx = matcher.end();
                }
            }
        } else if (comp instanceof Container) {
            Container cont = (Container)comp;
            for (Component subComp : cont.getComponents()) {
                highlight(subComp, cancel);
            }
        }
    }

    private class FindAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (bar.isVisible()) {
                findNext();
            } else {
                bar.setVisible(true);
                bar.requestFocusInWindow();
                updatePattern();
            }
        }

    }

}
