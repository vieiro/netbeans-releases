/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.css.formatting.api.embedding;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @since org.netbeans.modules.css.editor/1 1.3
 */
public final class JoinedTokenSequence<T1 extends TokenId> {

    List<TokenSequenceWrapper<T1>> tss;
    private int currentTokenSequence = -1;

    private JoinedTokenSequence(List<TokenSequenceWrapper<T1>> tss) {
        this.tss = tss;
    }

    public static <T1 extends TokenId> JoinedTokenSequence<T1> createFromTokenSequenceWrappers(List<TokenSequenceWrapper<T1>> tss) {
        return new JoinedTokenSequence<T1>(tss);
    }

    public static <T1 extends TokenId> JoinedTokenSequence<T1> createFromCodeBlocks(List<JoinedTokenSequence.CodeBlock<T1>> codeBlocks) {
        List<TokenSequenceWrapper<T1>> tss = new ArrayList<TokenSequenceWrapper<T1>>();
        for (JoinedTokenSequence.CodeBlock<T1> block : codeBlocks) {
            tss.addAll(block.tss);
        }
        return new JoinedTokenSequence<T1>(tss);
    }

    private void checkCurrentTokenSequence() {
        if (currentTokenSequence == -1) {
            throw new IllegalStateException("token position was no initialized. call moveStart or something");
        }
    }

    public Token<T1> token() {
        checkCurrentTokenSequence();
        return currentTokenSequence().token();
    }

    public TokenSequence<?> embedded() {
        checkCurrentTokenSequence();
        return currentTokenSequence().embedded();
    }

    public int index() {
        checkCurrentTokenSequence();
        int index = (currentTokenSequence+1) *100000;
        return index + currentTokenSequence().index();
    }

    private void setCurrentTokenSequenceIndex(int index) {
        currentTokenSequence = index;
        currentTSW = null;
    }

    public void moveIndex(int ind) {
        checkCurrentTokenSequence();
        String s = ""+ind;
        assert s.length() > 5 : s;
        s = s.substring(s.length()-5);
        int tokenIndex = Integer.parseInt(s);
        int tokenSequence = ((ind - tokenIndex) / 100000)-1;
        if (tokenSequence < 0 || tokenSequence >= tss.size()) {
            throw new IllegalStateException("index "+ind+" is out of boundaries "+tss.size() );
        }
        setCurrentTokenSequenceIndex(tokenSequence);
        currentTokenSequence().moveIndex(tokenIndex);
    }

    public TokenSequence<T1> currentTokenSequence() {
        checkCurrentTokenSequence();
        return getTokenSequenceWrapper().getTokenSequence();
    }

    public boolean isCurrentTokenSequenceVirtual() {
        checkCurrentTokenSequence();
        return getTokenSequenceWrapper().isVirtual();
    }

    private TokenSequenceWrapper<T1> currentTSW;

    private TokenSequenceWrapper<T1> getTokenSequenceWrapper() {
        if (currentTSW == null) {
            currentTSW = tss.get(currentTokenSequence);
        }
        return currentTSW;
    }

    public List<TokenSequenceWrapper<T1>> getContextDataTokenSequences() {
        return tss;
    }

    public void moveStart() {
        setCurrentTokenSequenceIndex(0);
        currentTokenSequence().moveStart();
    }

    public void moveEnd() {
        setCurrentTokenSequenceIndex(tss.size()-1);
        currentTokenSequence().moveEnd();
    }

    public boolean moveNext() {
        checkCurrentTokenSequence();
        boolean moreTokens = currentTokenSequence().moveNext();

        if (!moreTokens) {
            if (currentTokenSequence < tss.size()-1) {
                setCurrentTokenSequenceIndex(currentTokenSequence+1);
                currentTokenSequence().moveStart();
                moveNext();
            } else {
                return false;
            }
        }

        return true;
    }

    public boolean movePrevious() {
        checkCurrentTokenSequence();
        boolean moreTokens = currentTokenSequence().movePrevious();

        if (!moreTokens) {
            if (currentTokenSequence > 0) {
                setCurrentTokenSequenceIndex(currentTokenSequence-1);
                currentTokenSequence().moveEnd();
                movePrevious();
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * Method move() iterates over all items in tss array to find one
     * corresponding to given offset. In order to make move() faster this
     * method was added to find nearby start item instead of starting always
     * from 0.
     */
    private int findNearbyTokenSequenceIndexForOffset(int offset) {
        int end = tss.size()-1;
        int start = 0;
        while (true) {
            if (end - start < 10) {
                // do not bother further optimize
                return start;
            }
            int middle = start+((end-start)/2);
            TokenSequenceWrapper<T1> middleItem = tss.get(middle);
            while (middleItem.isVirtual() && middle > 0) {
                middle--;
                middleItem = tss.get(middle);
            }
            if (middle == 0) {
                // something wrong; abort optimization.
                return 0;
            }
            if (offset >= middleItem.getStart() && offset <= middleItem.getEnd()) {
                return middle;
            }
            if (offset > middleItem.getEnd()) {
                start = middle;
            } else {
                end = middle;
            }
        }
    }

    public int move(int offset) {
        int start = findNearbyTokenSequenceIndexForOffset(offset);
        for (int i = start; i < tss.size(); i++) {
            TokenSequenceWrapper<T1> cdts = tss.get(i);
            if (cdts.isVirtual()) {
                continue;
            }
            if (offset >= cdts.getStart() && offset <= cdts.getEnd()) {
                setCurrentTokenSequenceIndex(i);
                return currentTokenSequence().move(offset);
            }
        }

        return Integer.MIN_VALUE;
    }

    public boolean move(int offset, boolean forward) {
        int previous = -1;
        int start = findNearbyTokenSequenceIndexForOffset(offset);
        for (int i = start; i < tss.size(); i++) {
            TokenSequenceWrapper<T1> cdts = tss.get(i);
            if (cdts.isVirtual()) {
                continue;
            }
            if (offset >= cdts.getStart() && offset <= cdts.getEnd()) {
                setCurrentTokenSequenceIndex(i);
                currentTokenSequence().move(offset);
                return true;
            }
            if (forward && cdts.getStart() > offset) {
                setCurrentTokenSequenceIndex(i);
                currentTokenSequence().moveStart();
                return true;
            }
            if (!forward) {
                if (cdts.getStart() > offset) {
                    if (previous != -1) {
                        setCurrentTokenSequenceIndex(previous);
                        currentTokenSequence().moveEnd();
                        return true;
                    } else {
                        return false;
                    }
                } else if (i == tss.size()-1 && cdts.getEnd() < offset) {
                    setCurrentTokenSequenceIndex(i);
                    currentTokenSequence().moveEnd();
                    return true;
                }
            }
            previous = i;
        }

        return false;
    }

    public int offset() {
        checkCurrentTokenSequence();
        if (isCurrentTokenSequenceVirtual()) {
            assert currentTokenSequence > 0;
            TokenSequenceWrapper<T1> cdts = tss.get(currentTokenSequence-1);
            return cdts.getEnd();
        } else {
            return currentTokenSequence().offset();
        }
    }

    public Language<T1> language() {
        checkCurrentTokenSequence();
        return currentTokenSequence().language();
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (TokenSequenceWrapper<T1> cdts : tss) {
            String s = "";
            if (currentTokenSequence != -1 && cdts == getTokenSequenceWrapper()) {
                s = "CURRENT,";
            }
            sb.append("ContextDataTokenSequence["+s+"ts="+cdts.getTokenSequence().toString()+",virtual="+cdts.isVirtual()+"],");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length()-1);
        }
        return "JoinedTokenSequence["+sb.toString()+"]";
    }

    public static final class TokenSequenceWrapper<T1 extends TokenId> {
        private TokenSequence<T1> ts;
        private boolean virtual;
        private int start;
        private int end;

        public TokenSequenceWrapper(TokenSequence<T1> ts, boolean virtual) {
            this.ts = ts;
            this.virtual = virtual;
            ts.moveStart();
            ts.moveNext();
            start = ts.offset();
            ts.moveEnd();
            ts.movePrevious();
            end = ts.offset() + ts.token().length();
        }

        public TokenSequence<T1> getTokenSequence() {
            return ts;
        }

        public int getEnd() {
            return end;
        }

        public int getStart() {
            return start;
        }

        public boolean isVirtual() {
            return virtual;
        }

        @Override
        public String toString() {
            return "ContextDataTokenSequence[ts="+ts+",virtual="+virtual+"]";
        }

    }


    public static final class CodeBlock<T1 extends TokenId> {
        public List<TokenSequenceWrapper<T1>> tss;

        public CodeBlock(List<TokenSequenceWrapper<T1>> tss) {
            this.tss = tss;
        }

        @Override
        public String toString() {
            return "CodeBlock[tss="+tss+"]";
        }
    }


}
