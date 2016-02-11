/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.modules.spellchecker.bindings.php;

import java.util.List;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.editor.lexer.LexUtilities;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.modules.php.editor.parser.PHPDocCommentParser;
import org.netbeans.modules.php.editor.parser.astnodes.PHPDocBlock;
import org.netbeans.modules.php.editor.parser.astnodes.PHPDocMethodTag;
import org.netbeans.modules.php.editor.parser.astnodes.PHPDocNode;
import org.netbeans.modules.php.editor.parser.astnodes.PHPDocTag;
import org.netbeans.modules.php.editor.parser.astnodes.PHPDocTypeNode;
import org.netbeans.modules.php.editor.parser.astnodes.PHPDocTypeTag;
import org.netbeans.modules.php.editor.parser.astnodes.PHPDocVarTypeTag;
import org.netbeans.modules.spellchecker.spi.language.TokenList;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Pair;

/**
 * Based on JavaTokenList.
 */
public class PHPTokenList implements TokenList {

    private enum State {
        AngleBracket,
        Brace,
        Entity,
        Letter,
        Start,
        Tag,
        Variable;
    }

    private enum LetterType {
        Normal {
            @Override
            public boolean accept(char c) {
                return false;
            }
        },
        See {
            @Override
            public boolean accept(char c) {
                return c == ':' || c == '/' || c == '.' || c == '_' || c == '-' || c == '?' || c == '&';
            }
        },
        MethodName {
            @Override
            public boolean accept(char c) {
                return c == '_' || Character.isDigit(c);
            }
        };

        public abstract boolean accept(char c);
    }

    private final Document doc;
    private boolean hidden = false;
    private int currentBlockStart;
    private int currentOffsetInComment;
    private int currentWordOffset;
    private int nextBlockStart;
    private int startOffset;
    private CharSequence currentWord;
    private String currentBlockText;
    private PHPDocBlock currentDocBlock;
    private final PHPDocCommentParser docParser = new PHPDocCommentParser();

    public PHPTokenList(Document doc) {
        this.doc = doc;
    }

    @Override
    public void setStartOffset(int offset) {
        currentBlockText = null;
        currentOffsetInComment = (-1);
        this.startOffset = offset;
        this.nextBlockStart = offset;
        FileObject fileObject = FileUtil.getConfigFile("Spellcheckers/Phpdoc"); // NOI18N
        Boolean b = (Boolean) fileObject.getAttribute("Hidden"); // NOI18N
        hidden = Boolean.TRUE.equals(b);
    }

    @Override
    public boolean nextWord() {
        if (hidden) {
            return false;
        }
        boolean hasNext = nextWordImpl();
        while (hasNext && (currentWordOffset + currentWord.length()) < startOffset) {
            hasNext = nextWordImpl();
        }
        return hasNext;
    }

    private boolean nextWordImpl() {
        try {
            while (true) {
                if (currentBlockText == null) {
                    int[] span = findNextPHPDocComment();
                    if (span[0] == (-1)) {
                        return false;
                    }
                    currentBlockStart = span[0];
                    currentBlockText = doc.getText(span[0], span[1] - span[0]);
                    currentOffsetInComment = 0;
                    nextBlockStart = span[1];
                    currentDocBlock = docParser.parse(-3, currentBlockText.length(), currentBlockText); // - /**
                }

                // ignore parts of pair tags
                String pairTag = null;
                Pair<CharSequence, Integer> data = wordBroker(currentBlockText, currentOffsetInComment);

                while (data != null) {
                    currentOffsetInComment = getCurrentOffsetInComment(data);
                    if (pairTag == null) {
                        if (Character.isLetter(data.first().charAt(0)) && !isIdentifierLike(data.first())) {
                            currentWordOffset = currentBlockStart + data.second();
                            currentWord = data.first();
                            return true;
                        }
                        switch (data.first().charAt(0)) {
                            case '@':
                                handlePhpdocTag(data.first());
                                break;
                            case '<':
                                if (startsWith(data.first(), "<a ")) { // NOI18N
                                    pairTag = "</a>"; // NOI18N
                                }
                                if (startsWith(data.first(), "<code>")) { // NOI18N
                                    pairTag = "</code>"; // NOI18N
                                }
                                if (startsWith(data.first(), "<pre>")) { // NOI18N
                                    pairTag = "</pre>"; // NOI18N
                                }
                                break;
                            case '{':
                                pairTag = "}"; // NOI18N
                                break;
                            case '$':
                                // no-op
                                break;
                            default:
                                // no-op
                                break;
                        }
                    } else if (pairTag.contentEquals(data.first())) {
                        pairTag = null;
                    }
                    data = wordBroker(currentBlockText, currentOffsetInComment);
                }
                currentBlockText = null;
            }
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(e);
            return false;
        }
    }

    private int[] findNextPHPDocComment() throws BadLocationException {
        TokenSequence<PHPTokenId> ts = null;
        if (doc instanceof AbstractDocument) {
            AbstractDocument ad = (AbstractDocument) doc;
            ad.readLock();
            try {
                ts = LexUtilities.getPHPTokenSequence(ad, nextBlockStart);
            } finally {
                ad.readUnlock();
            }
        }
        if (ts == null) {
            return new int[]{-1, -1};
        }

        ts.move(nextBlockStart);
        while (ts.moveNext()) {
            if (ts.token().id() == PHPTokenId.PHPDOC_COMMENT) {
                return new int[]{ts.offset(), ts.offset() + ts.token().length()};
            }
        }
        return new int[]{-1, -1};
    }

    private void handlePhpdocTag(CharSequence tag) {
        if ("@see".contentEquals(tag)) { // NOI18N
            // e.g.
            // @see MyClass::$items
            // @see http://example.com/my/bar Documentation of Foo.
            // ignore next "word"
            Pair<CharSequence, Integer> data = wordBroker(currentBlockText, currentOffsetInComment, LetterType.See);
            currentOffsetInComment = getCurrentOffsetInComment(data);
            return;
        }

        if ("@author".contentEquals(tag)) { // NOI18N
            // ignore everything till the end of the line:
            Pair<CharSequence, Integer> data = wordBroker(currentBlockText, currentOffsetInComment);
            while (data != null) {
                currentOffsetInComment = getCurrentOffsetInComment(data);
                if ('\n' == data.first().charAt(0)) {
                    // continue
                    return;
                }
                data = wordBroker(currentBlockText, currentOffsetInComment);
            }
            return;
        }

        if (currentDocBlock != null) {
            List<PHPDocTag> phpDocTags = currentDocBlock.getTags();
            for (PHPDocTag phpDocTag : phpDocTags) {
                if (phpDocTag.getStartOffset() == currentOffsetInComment - tag.length()) {
                    if (phpDocTag instanceof PHPDocTypeTag) {
                        handleTypeTag((PHPDocTypeTag) phpDocTag);
                    }
                    break;
                }
            }
        }
    }

    private void handleTypeTag(PHPDocTypeTag docTypeTag) {
        // ignore types
        List<PHPDocTypeNode> types = docTypeTag.getTypes();
        PHPDocTypeNode lastType = null;
        for (PHPDocTypeNode type : types) {
            if (lastType == null || lastType.getEndOffset() < type.getEndOffset()) {
                lastType = type;
            }
        }
        if (lastType != null) {
            currentOffsetInComment = lastType.getEndOffset();
        }

        if (docTypeTag instanceof PHPDocMethodTag) {
            // ignore params
            PHPDocMethodTag methodTag = (PHPDocMethodTag) docTypeTag;
            List<PHPDocVarTypeTag> parameters = methodTag.getParameters();
            PHPDocVarTypeTag lastParam = null;
            for (PHPDocVarTypeTag parameter : parameters) {
                if (lastParam == null || lastParam.getEndOffset() < parameter.getEndOffset()) {
                    lastParam = parameter;
                }
            }
            if (lastParam != null) {
                currentOffsetInComment = lastParam.getEndOffset();
            } else {
                // ignore method name
                PHPDocNode methodName = methodTag.getMethodName();
                if (methodName != null) {
                    Pair<CharSequence, Integer> data = wordBroker(
                            currentBlockText,
                            currentOffsetInComment,
                            LetterType.MethodName
                    );
                    if (data.first().equals(methodName.getValue())) {
                        currentOffsetInComment = getCurrentOffsetInComment(data);
                    }
                }
            }
        }
    }

    private Pair<CharSequence, Integer> wordBroker(CharSequence start, int offset) {
        return wordBroker(start, offset, LetterType.Normal);
    }

    private Pair<CharSequence, Integer> wordBroker(CharSequence start, int offset, LetterType letterType) {
        State state = State.Start;
        int offsetStart = offset;
        int currentOffset = offset;

        while (start.length() > currentOffset) {
            char currentChar = start.charAt(currentOffset);
            switch (state) {
                case Start:
                    if (isLetter(currentChar)) {
                        state = State.Letter;
                        offsetStart = currentOffset;
                        break;
                    }
                    if (currentChar == '@' || currentChar == '#') {
                        state = State.Tag;
                        offsetStart = currentOffset;
                        break;
                    }
                    if (currentChar == '<') {
                        state = State.AngleBracket;
                        offsetStart = currentOffset;
                        break;
                    }
                    if (currentChar == '\n' || currentChar == '}') {
                        return Pair.of(start.subSequence(currentOffset, currentOffset + 1), currentOffset);
                    }
                    if (currentChar == '{') {
                        state = State.Brace;
                        offsetStart = currentOffset;
                        break;
                    }
                    if (currentChar == '&') {
                        state = State.Entity;
                        offsetStart = currentOffset;
                        break;
                    }
                    if (currentChar == '$') {
                        state = State.Variable;
                        offsetStart = currentOffset;
                        break;
                    }
                    break;
                case Letter:
                    if (!isLetter(currentChar)) {
                        if (!letterType.accept(currentChar)) {
                            return Pair.of(start.subSequence(offsetStart, currentOffset), offsetStart);
                        }
                    }
                    break;
                case Tag:
                    // phpdoc tag e.g. @param, @property-read
                    if (!isLetter(currentChar) && currentChar != '-') {
                        return Pair.of(start.subSequence(offsetStart, currentOffset), offsetStart);
                    }
                    break;
                case AngleBracket:
                    // tag e.g. <code>
                    if (currentChar == '>') {
                        return Pair.of(start.subSequence(offsetStart, currentOffset + 1), offsetStart);
                    }
                    break;
                case Brace:
                    if (currentChar == '@') {
                        // inline phpdoc tag e.g. {@inheritdoc}
                        state = State.Tag;
                        break;
                    }
                    currentOffset--;
                    state = State.Start;
                    break;
                case Entity:
                    // entities e.g. &gt; &#62;
                    if (currentChar == ';') {
                        return Pair.of(start.subSequence(offsetStart, currentOffset + 1), offsetStart);
                    }
                    if (!isLetter(currentChar)
                            && currentChar != '#'
                            && !Character.isDigit(currentChar)) {
                        return Pair.of(start.subSequence(offsetStart, currentOffset), offsetStart);
                    }
                    break;
                case Variable:
                    // variable e.g. $var_name
                    if (!isLetter(currentChar)
                            && currentChar != '_'
                            && !Character.isDigit(currentChar)) {
                        return Pair.of(start.subSequence(offsetStart, currentOffset), offsetStart);
                    }
                    break;
                default:
                    assert false;
                    break;
            }
            currentOffset++;
        }

        if (currentOffset > offsetStart) {
            return Pair.of(start.subSequence(offsetStart, currentOffset), offsetStart);
        } else {
            return null;
        }
    }

    private static boolean startsWith(CharSequence where, String withWhat) {
        if (where.length() >= withWhat.length()) {
            return withWhat.contentEquals(where.subSequence(0, withWhat.length()));
        }
        return false;
    }

    static boolean isIdentifierLike(CharSequence s) {
        boolean hasCapitalsInside = false;
        int offset = 1;
        while (offset < s.length() && !hasCapitalsInside) {
            hasCapitalsInside |= Character.isUpperCase(s.charAt(offset));
            offset++;
        }
        return hasCapitalsInside;
    }

    private static int getCurrentOffsetInComment(Pair<CharSequence, Integer> data) {
        return data.second() + data.first().length();
    }

    private static boolean isLetter(char c) {
        return Character.isLetter(c) || c == '\'';
    }

    @Override
    public int getCurrentWordStartOffset() {
        return currentWordOffset;
    }

    @Override
    public CharSequence getCurrentWordText() {
        return currentWord;
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        //ignored...
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        //ignored...
    }

}
