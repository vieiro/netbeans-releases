/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.editor.ext.java;

import java.util.List;
import java.util.HashMap;
import javax.swing.text.BadLocationException;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import org.netbeans.editor.*;

/**
* Various java-layers
*
* @author Miloslav Metelka
* @version 1.00
*/

public class JavaDrawLayerFactory {

    public static final String JAVA_LAYER_NAME = "java-layer"; // NOI18N

    public static final int JAVA_LAYER_VISIBILITY = 1010;

    /** Layer that colors extra java information like the methods or special
     * characters in the character and string literals.
     */
    public static class JavaLayer extends DrawLayer.AbstractLayer {

        /** End of the area that is resolved right now. It saves
         * repetitive searches for '(' for multiple fragments
         * inside one identifier token.
         */
        private int resolvedEndOffset;

        private boolean resolvedValue;

        private NonWhitespaceFwdFinder nwFinder = new NonWhitespaceFwdFinder();

        public JavaLayer() {
            super(JAVA_LAYER_NAME);
        }

        public void init(DrawContext ctx) {
            resolvedEndOffset = 0; // nothing resolved
        }

        public boolean isActive(DrawContext ctx, MarkFactory.DrawMark mark) {
            int nextOffset = ctx.getTokenOffset() + ctx.getTokenLength();

            setNextActivityChangeOffset(nextOffset);
            return true;
        }

        protected Coloring getMethodColoring(DrawContext ctx) {
            TokenContextPath path = ctx.getTokenContextPath().replaceStart(
                JavaLayerTokenContext.contextPath);
            return ctx.getEditorUI().getColoring(
                path.getFullTokenName(JavaLayerTokenContext.METHOD));
        }

        private boolean isGenericType(JavaSyntaxSupport sup, int offset) {
            try {
                int lastSepOffset = sup.getLastCommandSeparator(offset);
                JCTokenProcessor tp = new JCTokenProcessor(offset);
                tp.setJava15(true);
                sup.tokenizeText(tp, lastSepOffset + 1, offset, true);
                return tp.getResultExp().getExpID() == JCExpression.GENERIC_TYPE;
            } catch (BadLocationException e) {
                return false;
            }
        }

        private boolean isMethod(DrawContext ctx) {
            int idEndOffset = ctx.getTokenOffset() + ctx.getTokenLength();
            if (idEndOffset > resolvedEndOffset) { // beyond the resolved area
                resolvedEndOffset = idEndOffset; // will resolve now
                int endOffset = ctx.getEndOffset();
                int bufferStartOffset = ctx.getBufferStartOffset();
                char[] buffer = ctx.getBuffer();
                int nwOffset = Analyzer.findFirstNonWhite(buffer,
                        idEndOffset - bufferStartOffset,
                        endOffset - idEndOffset);
                if (nwOffset >= 0) { // found non-white
                    resolvedValue = (buffer[nwOffset] == '(');
                    if (!resolvedValue && buffer[nwOffset] == '<') {
                        JavaSyntaxSupport sup = (JavaSyntaxSupport) ctx.getEditorUI().getDocument().getSyntaxSupport().get(JavaSyntaxSupport.class);
                        try {
                            int[] block = sup.findMatchingBlock(ctx.getBufferStartOffset() + nwOffset, true);
                            if (block != null) {
                                int off = Utilities.getFirstNonWhiteFwd(ctx.getEditorUI().getDocument(), block[1]);
                                if (off > -1) {
                                    if (bufferStartOffset + buffer.length > off) {
                                        resolvedValue = (buffer[off - bufferStartOffset] == '(') && isGenericType(sup, off);
                                    } else {
                                        resolvedValue = (ctx.getEditorUI().getDocument().getChars(off, 1)[0] == '(') && isGenericType(sup, off);
                                    }
                                }
                            }
                        } catch (BadLocationException e) {
                            resolvedValue = false;
                        }
                    }
                } else { // must resolve after buffer end
                    try {
                        int off = ctx.getEditorUI().getDocument().find(nwFinder, endOffset, -1);
                        resolvedValue = off >= 0 && (nwFinder.getFoundChar() == '(');
                        if (!resolvedValue && nwFinder.getFoundChar() == '<') {
                            JavaSyntaxSupport sup = (JavaSyntaxSupport) ctx.getEditorUI().getDocument().getSyntaxSupport().get(JavaSyntaxSupport.class);
                            int[] block = sup.findMatchingBlock(off, true);
                            if (block != null) {
                                off = Utilities.getFirstNonWhiteFwd(ctx.getEditorUI().getDocument(), block[1]);
                                if (off > -1)
                                    resolvedValue = (ctx.getEditorUI().getDocument().getChars(off, 1)[0] == '(') && isGenericType(sup, off);
                            }
                        }
                    } catch (BadLocationException e) {
                        resolvedValue = false;
                    }
                }
                if (resolvedValue) {
                    resolvedValue = !isAnnotation(ctx);
                }
            }

            return resolvedValue;
        }

        private boolean isAnnotation(DrawContext ctx) {
            try {
                BaseDocument document = ctx.getEditorUI().getDocument();
                int off = Utilities.getFirstNonWhiteBwd(document, ctx.getTokenOffset());
                char ch = '*';
                while (off > -1 && (ch = document.getChars(off, 1)[0]) == '.') {
                    off = Utilities.getFirstNonWhiteBwd(document, off);
                    if (off > -1)
                    off = Utilities.getPreviousWord(document, off);
                    if (off > -1)
                        off = Utilities.getFirstNonWhiteBwd(document, off);
                }
                if (off > -1 && ch == '@')
                    return true;
            } catch (BadLocationException e) {}
            return false;
        }

        public void updateContext(DrawContext ctx) {
            if (ctx.getTokenID() == JavaTokenContext.IDENTIFIER && isMethod(ctx)) {
                Coloring mc = getMethodColoring(ctx);
                if (mc != null) {
                    mc.apply(ctx);
                }
            }
        }

    }

    /** Find first non-white character forward */
    static class NonWhitespaceFwdFinder extends FinderFactory.GenericFwdFinder {

        private char foundChar;

        public char getFoundChar() {
            return foundChar;
        }

        protected int scan(char ch, boolean lastChar) {
            if (!Character.isWhitespace(ch)) {
                found = true;
                foundChar = ch;
                return 0;
            }
            return 1;
        }
    }

    /** Find first non-white character backward */
    public static class NonWhitespaceBwdFinder extends FinderFactory.GenericBwdFinder {

        private char foundChar;

        public char getFoundChar() {
            return foundChar;
        }

        protected int scan(char ch, boolean lastChar) {
            if (!Character.isWhitespace(ch)) {
                found = true;
                foundChar = ch;
                return 0;
            }
            return -1;
        }
    }

    /** This class watches whether the '(' character was inserted/removed.
     * It ensures the appropriate part of the document till
     * the previous non-whitespace will be repainted.
     */
    public static class LParenWatcher implements DocumentListener {

        NonWhitespaceBwdFinder nwFinder = new NonWhitespaceBwdFinder();

        private void check(DocumentEvent evt) {
            if (evt.getDocument() instanceof BaseDocument) {
                BaseDocument doc = (BaseDocument)evt.getDocument();
                BaseDocumentEvent bevt = (BaseDocumentEvent)evt;
                String text = bevt.getText();
                if (text != null) {
                    boolean found = false;
                    for (int i = text.length() - 1; i >= 0; i--) {
                        if (text.charAt(i) == '(') {
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        int offset = evt.getOffset();
                        // Need to repaint
                        int redrawOffset = 0;
                        if (offset > 0) {
                            try {
                                redrawOffset = doc.find(nwFinder, offset - 1, 0);
                            } catch (BadLocationException e) {
                            }

                            if (redrawOffset < 0) { // not found non-whitespace
                                redrawOffset = 0;
                            }
                        }
                        doc.repaintBlock(redrawOffset, offset);
                    }
                }
            }
        }

        public void insertUpdate(DocumentEvent evt) {
            check(evt);
        }

        public void removeUpdate(DocumentEvent evt) {
            check(evt);
        }

        public void changedUpdate(DocumentEvent evt) {
        }

    }

}

