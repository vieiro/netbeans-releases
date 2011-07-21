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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.editor.lib2.view;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.View;
import org.netbeans.lib.editor.util.ArrayUtilities;
import org.netbeans.lib.editor.util.GapList;

/**
 * Class that manages children of either DocumentView or ParagraphView.
 * <br/>
 * For document view the class manages visual spans (end visual offsets).
 * For paragraphs the class manages end offsets of children as well as their end visual offsets.
 * <br/>
 * Generally children of {@link #ParagraphView} manage their raw end offsets
 * while children of {@link #DocumentView} do not manage them (they use Position objects
 * to manage its start).
 * 
 * @author Miloslav Metelka
 */

class ViewChildren<V extends EditorView> extends GapList<V> {

    // -J-Dorg.netbeans.modules.editor.lib2.view.ViewChildren.level=FINE
    private static final Logger LOG = Logger.getLogger(ViewChildren.class.getName());

    private static final long serialVersionUID  = 0L;

    ViewGapStorage gapStorage; // 24=super + 4 = 28 bytes
    
    ViewChildren(int capacity) {
        super(capacity);
    }

    int raw2Offset(int rawEndOffset) {
        // Use <= so that only views that follow the one with particular raw end offset satisfy the condition.
        return (gapStorage == null || rawEndOffset <= gapStorage.offsetGapStart)
                ? rawEndOffset
                : rawEndOffset - gapStorage.offsetGapLength;
    }

    int offset2Raw(int offset) {
        // Use <= so that only views that follow the one with particular raw end offset satisfy the condition.
        return (gapStorage == null || offset <= gapStorage.offsetGapStart)
                ? offset
                : offset + gapStorage.offsetGapLength;
    }
    
    int startOffset(int index) {
        return (index > 0) ? raw2Offset(get(index - 1).getRawEndOffset()) : 0;
    }
    
    int endOffset(int index) {
        return raw2Offset(get(index).getRawEndOffset());
    }
    
    /**
     * Get view index of first view that "contains" the given offset (starts with it or it's inside)
     * by examining child views' raw end offsets.
     * <br/>
     * This is suitable for paragraph view which manages its views' raw end offsets.
     * 
     * @param offset offset to search for.
     * @return view index or -1.
     */
    int viewIndexFirst(int offset) {
        // Translate relOffset into its raw form and search in raw offsets only.
        // Since the raw offsets are sorted (the gap is >= 0) this should work fine.
        offset = offset2Raw(offset);
        int last = size() - 1;
        int low = 0;
        int high = last;
        while (low <= high) {
            int mid = (low + high) >>> 1; // mid in the binary search
            V view = get(mid);
            int rawEndOffset = view.getRawEndOffset();
            if (rawEndOffset < offset) {
                low = mid + 1;
            } else if (rawEndOffset > offset) {
                high = mid - 1;
            } else { // rawEndOffset == relOffset
                while (view.getLength() == 0 && mid > 0) {
                    view = get(--mid);
                }
                low = mid + 1;
                break;
            }
        }
        return Math.min(low, last); // Make sure last item is returned for relOffset above end
    }

    void moveOffsetGap(int index, int newOffsetGapStart) {
        if (gapStorage == null) {
            return;
        }
        int origStart = gapStorage.offsetGapStart;
        int shift = gapStorage.offsetGapLength;
        gapStorage.offsetGapStart = newOffsetGapStart;
        int viewCount = size();
        if (index == viewCount || get(index).getRawEndOffset() > origStart) {
            // Go down to check and fix views so that they are <= the offset
            while (--index >= 0) {
                EditorView view = get(index);
                int offset = view.getRawEndOffset();
                if (offset > origStart) { // Corresponds to <= in rawOffset => offset computation
                    view.setRawEndOffset(offset - shift);
                } else {
                    break;
                }
            }
        } else { // go up to check and fix the marks are above the offset
            while (index < viewCount) {
                EditorView view = get(index);
                int offset = view.getRawEndOffset();
                if (offset <= origStart) { // Corresponds to <= in rawOffset => offset computation
                    view.setRawEndOffset(offset + shift);
                } else {
                    break;
                }
                index++;
            }
        }
    }
    
    int getLength() { // Total offset length of contained child views
        int size = size();
        if (size > 0) {
            V lastChildView = get(size - 1);
            return raw2Offset(lastChildView.getRawEndOffset());
        } else {
            return 0;
        }
    }

    double raw2VisualOffset(double rawVisualOffset) {
        // Use <= so that only views that follow the one with particular raw end visual offset satisfy the condition.
        return (gapStorage == null || rawVisualOffset <= gapStorage.visualGapStart)
                ? rawVisualOffset
                : rawVisualOffset - gapStorage.visualGapLength;
    }

    double visualOffset2Raw(double visualOffset) {
        // Use <= so that only views that follow the one with particular raw end visual offset satisfy the condition.
        return (gapStorage == null || visualOffset <= gapStorage.visualGapStart)
                ? visualOffset
                : visualOffset + gapStorage.visualGapLength;
    }

    /**
     * Start visual offset of the particular child view.
     * @param index &gt;= 0 and &lt;= size().
     * @return start visual offset of the child view at given index.
     */
    final double startVisualOffset(int index) {
        return (index > 0)
                ? raw2VisualOffset(get(index - 1).getRawEndVisualOffset())
                : 0d;
    }
    
    /**
     * End visual offset of the particular child view.
     * @param index &gt;= 0 and &lt; size().
     * @return ending visual offset of the child view at given index.
     */
    final double endVisualOffset(int index) {
        return raw2VisualOffset(get(index).getRawEndVisualOffset());
    }

    /**
     * Determine view index from given visual offset.
     *
     * @param visualOffset
     * @param measuredViewCount number of views that have their span (and end-visual-offset) measured.
     * @return index or -1 for no measured views.
     */
    final int viewIndexFirstVisual(double visualOffset, int measuredViewCount) {
        int last = measuredViewCount - 1;
        if (last == -1) {
            return -1; // No items
        }
        // Translate visualOffset into its raw form and search in raw offsets only.
        // Since the raw offsets are sorted (the gap is >= 0) this should work fine.
        visualOffset = visualOffset2Raw(visualOffset);
        int low = 0;
        int high = last;
        while (low <= high) {
            int mid = (low + high) >>> 1; // mid in the binary search
            double rawEndVisualOffset = get(mid).getRawEndVisualOffset();
            if (rawEndVisualOffset < visualOffset) {
                low = mid + 1;
            } else if (rawEndVisualOffset > visualOffset) {
                high = mid - 1;
            } else { // exact raw end visual offset found at index
                while (mid > 0 && get(mid - 1).getRawEndVisualOffset() == visualOffset) {
                    mid--;
                }
                low = mid + 1;
                break;            }
        }
        return Math.min(low, last);
    }

    void moveVisualGap(int index, double newVisualGapStart) {
        if (gapStorage == null) {
            return;
        }
        gapStorage.visualGapStart = newVisualGapStart;
        if (index != gapStorage.visualGapIndex) {
            if (index < gapStorage.visualGapIndex) {
                for (int i = gapStorage.visualGapIndex - 1; i >= index; i--) {
                    V view = get(i);
                    view.setRawEndVisualOffset(view.getRawEndVisualOffset() + gapStorage.visualGapLength);
                }

            } else { // index > gapStorage.visualGapIndex
                for (int i = gapStorage.visualGapIndex; i < index; i++) {
                    V view = get(i);
                    view.setRawEndVisualOffset(view.getRawEndVisualOffset() - gapStorage.visualGapLength);
                }
            }
            gapStorage.visualGapIndex = index;
        }
    }

    void checkVisualGapIfLoggable() {
        if (gapStorage != null && LOG.isLoggable(Level.FINE)) {
            String error = null;
            int visualGapIndex = gapStorage.visualGapIndex;
            for (int i = 0; i < size(); i++) {
                V view = get(i);
                double rawVisualOffset = view.getRawEndVisualOffset();
                double visualOffset = raw2VisualOffset(rawVisualOffset);
                // Check visual offset
                if (i < visualGapIndex) {
                    if (rawVisualOffset >= gapStorage.visualGapStart) {
                        error = "Not below visual-gap: rawVisualOffset=" + rawVisualOffset + // NOI18N
                                " >= visualGapStart=" + gapStorage.visualGapStart; // NOI18N
                    }
                } else { // Above gap
                    if (rawVisualOffset < gapStorage.visualGapStart) {
                        error = "Not above visual-gap: rawVisualOffset=" + rawVisualOffset + // NOI18N
                                " < visualGapStart=" + gapStorage.visualGapStart; // NOI18N
                    }
                    if (i == visualGapIndex) {
                        if (visualOffset != gapStorage.visualGapStart) {
                            error = "visualOffset=" + visualOffset + " != gapStorage.visualGapStart=" + // NOI18N
                                    gapStorage.visualGapStart;
                        }
                    }

                }
                if (error != null) {
                    break;
                }
            }
            if (error != null) {
                throw new IllegalStateException("gapStorage INTEGRITY ERROR!!!\n" + error);
            }
        }
    }

    void checkIntegrityIfLoggable(EditorView parent) {
        if (gapStorage != null && LOG.isLoggable(Level.FINE)) {
            String err = findIntegrityError(parent);
            if (err != null) {
                throw new IllegalStateException("ViewChildren ERROR!!!\n" + err);
            }
        }
    }

    protected String findIntegrityError(EditorView parent) {
        String err = null;
        int lastRawEndOffset = 0;
        int lastOffset = 0;
        double lastRawEndVisualOffset = 0d;
        double lastVisualOffset = 0d;
        for (int i = 0; i < size(); i++) {
            V view = get(i);
            View p = view.getParent();
            if (err == null && p != parent) {
                err = "p=" + p + " != parent=" + parent;
            }
            int rawEndOffset = view.getRawEndOffset();
            int childStartOffset = view.getStartOffset();
            int childEndOffset = view.getEndOffset();
            double rawEndVisualOffset = view.getRawEndVisualOffset();
            double visualOffset = raw2VisualOffset(rawEndVisualOffset);
            // Check textual offset
            if (err == null && rawEndOffset < lastRawEndOffset) {
                err = "rawEndOffset=" + rawEndOffset + " < lastRawEndOffset=" + lastRawEndOffset; // NOI18N
            }
            if (err == null && childStartOffset < lastOffset) {
                err = "childStartOffset=" + childStartOffset + " < lastEndOffset=" + lastOffset; // NOI18N
            }
            if (err == null && childEndOffset < childStartOffset) {
                err = "childEndOffset=" + childEndOffset + " < childStartOffset=" + childStartOffset; // NOI18N
            }
            lastOffset = childEndOffset;

            // Check visual offset
            if (err == null && rawEndVisualOffset < lastRawEndVisualOffset) {
                err = "rawEndVisualOffset=" + rawEndVisualOffset + " < lastRawEndVisualOffset=" + // NOI18N
                        lastRawEndVisualOffset;
            }
            if (err == null && visualOffset < lastVisualOffset) {
                err = "visualOffset=" + visualOffset + " < lastVisualOffset=" + lastVisualOffset; // NOI18N
            }
            if (err != null) {
                err = "EBVC[" + i + "]: "; // NOI18N
                break;
            }
        }
        return err;
    }

    /**
     * Append debugging info.
     *
     * @param sb non-null string builder
     * @param indent &gt;=0 indentation in spaces.
     * @param importantIndex either an index of child that is important to describe in the output
     *  (Initial and ending two displayed plus two before and after the important index).
     *  Or -1 to display just starting and ending two. Or -2 to display all children.
     * @return
     */
    public StringBuilder appendChildrenInfo(StringBuilder sb, int indent, int importantIndex) {
        if (gapStorage != null) {
            sb.append("Gap: ");
            gapStorage.appendInfo(sb);
        }
        int viewCount = size();
        int digitCount = ArrayUtilities.digitCount(viewCount);
        int importantLastIndex = -1; // just be < 0
        int childImportantIndex = (importantIndex == -2) ? -2 : -1;
        for (int i = 0; i < viewCount; i++) {
            sb.append('\n');
            ArrayUtilities.appendSpaces(sb, indent);
            ArrayUtilities.appendBracketedIndex(sb, i, digitCount);
            V view = get(i);
            appendChildInfo(sb, i);
            view.appendViewInfo(sb, indent, childImportantIndex);
            boolean appendDots = false;
            if (i == 4) { // After showing first 5 items => possibly skip to important index
                if (importantIndex == -1) { // Display initial five
                    if (i < viewCount - 6) { // -6 since i++ will follow
                        appendDots = true;
                        i = viewCount - 6;
                    }
                } else if (importantIndex >= 0) {
                    importantLastIndex = importantIndex + 3;
                    importantIndex = importantIndex - 3;
                    if (i < importantIndex - 1) {
                        appendDots = true;
                        i = importantIndex - 1;
                    }
                } // otherwise importantIndex == -2 to display every child
            } else if (i == importantLastIndex) {
                if (i < viewCount - 6) { // -6 since i++ will follow
                    appendDots = true;
                    i = viewCount - 6;
                }
            }
            if (appendDots) {
                sb.append('\n');
                ArrayUtilities.appendSpaces(sb, indent);
                sb.append("...");
            }
        }
        return sb;
    }
    
    protected StringBuilder appendChildInfo(StringBuilder sb, int index) {
        return sb;
    }

}
