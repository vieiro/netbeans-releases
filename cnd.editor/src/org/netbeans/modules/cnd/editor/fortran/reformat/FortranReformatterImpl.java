/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.editor.fortran.reformat;

import java.util.LinkedList;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.cnd.api.lexer.FortranTokenId;
import org.netbeans.modules.cnd.editor.fortran.reformat.FortranReformatter.Diff;
import static org.netbeans.cnd.api.lexer.FortranTokenId.*;
import org.netbeans.modules.cnd.editor.fortran.options.FortranCodeStyle;
import org.netbeans.modules.cnd.editor.fortran.reformat.FortranContextDetector.OperatorKind;
import org.netbeans.modules.cnd.editor.fortran.reformat.FortranDiffLinkedList.DiffResult;

/**
 *
 * @author Alexander Simon
 */
public class FortranReformatterImpl {
    /*package local*/ final FortranContextDetector ts;
    /*package local*/ final FortranCodeStyle codeStyle;
    /*package local*/ final FortranDiffLinkedList diffs = new FortranDiffLinkedList();
    /*package local*/ final FortranBracesStack braces;
    private FortranPreprocessorFormatter preprocessorFormatter;
    private final int startOffset;
    private final int endOffset;
    private int tabSize;

    FortranReformatterImpl(TokenSequence<FortranTokenId> ts, int startOffset, int endOffset, FortranCodeStyle codeStyle){
        braces = new FortranBracesStack(codeStyle);
        tabSize = codeStyle.getTabSize();
        if (tabSize <= 1) {
            tabSize = 8;
        }
        this.ts = new FortranContextDetector(ts, diffs, braces, tabSize);
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.codeStyle = codeStyle;
        preprocessorFormatter = new FortranPreprocessorFormatter(this);
    }

    LinkedList<Diff> reformat(){
        ts.moveStart();
        Token<FortranTokenId> previous = ts.lookPrevious();
        boolean isFirst = true;
        while(ts.moveNext()){
            if (ts.offset() > endOffset) {
                break;
            }
            //System.out.println("========"+previous+"==========");
            //System.out.println(ts);
            Token<FortranTokenId> current = ts.token();
            FortranTokenId id = current.id();
            if (previous != null && previous.id() == PREPROCESSOR_DIRECTIVE && id != PREPROCESSOR_DIRECTIVE){
                // indent afre preprocessor directive
                if (doFormat()){
                    indentNewLine(current);
                }
            }
            if (isFirst) {
                analyzeLine(previous, current);
            }
            isFirst = false;
            switch(id){
                case PREPROCESSOR_DIRECTIVE: //(null, "preprocessor"),
                {
                    preprocessorFormatter.indentPreprocessor(previous);
                    break;
                }
                case NEW_LINE:
                {
                    analyzeLine(previous, current);
                    break;
                }
                case WHITESPACE:
                {
                    if (doFormat()) {
                        whiteSpaceFormat(previous, current);
                    }
                    break;
                }
                case LINE_COMMENT_FIXED:
                case LINE_COMMENT_FREE:
                {
                    if (doFormat()) {
                        reformatBlockComment(previous, current);
                    }
                    break;
                }
                case LPAREN: //("(", "separator"),
                {
                    braces.parenDepth++;
                    if (doFormat()) {
                        formatLeftParen(previous, current);
                    }
                    break;
                }
                case RPAREN: //(")", "separator"),
                {
                    braces.parenDepth--;
                    if (braces.parenDepth < 0){
                        // unbalanced paren
                        braces.parenDepth = 0;
                    }
                    if (doFormat()) {
                        formatRightParen(previous, current);
                    }
                    break;
                }
                case IDENTIFIER:
                {
                    Token<FortranTokenId> next = ts.lookNextImportant();
                    if (next != null && next.id() == COLON) {
                        indentLabel(previous);
                    }
                    break;
                }
                case COMMA: //(",", "separator"),
                {
                    if (doFormat()) {
                        spaceBefore(previous, codeStyle.spaceBeforeComma());
                        spaceAfter(current, codeStyle.spaceAfterComma());
                    }
                    break;
                }
                case COLON:
                    processColumn(previous, current);
                    break;
                case KWOP_NOT:
                {
                    if (doFormat()) {
                        spaceAfter(current, codeStyle.spaceAroundUnaryOps());
                    }
                    break;
                }
                case OP_PLUS:
                case OP_MINUS:
                {
                    if (doFormat()) {
                        OperatorKind kind = ts.getOperatorKind(current);
                        if (kind == OperatorKind.BINARY){
                            spaceBefore(previous, codeStyle.spaceAroundBinaryOps());
                            spaceAfter(current, codeStyle.spaceAroundBinaryOps());
                        } else if (kind == OperatorKind.UNARY){
                            spaceAfter(current, codeStyle.spaceAroundUnaryOps());
                        }
                    }
                    break;
                }
                case OP_MUL:
                case OP_CONCAT:
                {
                    if (doFormat()) {
                        spaceBefore(previous, codeStyle.spaceAroundBinaryOps());
                        spaceAfter(current, codeStyle.spaceAroundBinaryOps());
                    }
                    break;
                }
                case KWOP_GT:
                case OP_GT:
                case KWOP_GE:
                case OP_GT_EQ:
                case KWOP_LT:
                case OP_LT:
                case KWOP_LE:
                case OP_LT_EQ:
                case KWOP_EQ:
                case OP_LOG_EQ:
                case KWOP_NE:
                case OP_NOT_EQ:
                case PERCENT:
                {
                    if (doFormat()) {
                        spaceBefore(previous, codeStyle.spaceAroundBinaryOps());
                        spaceAfter(current, codeStyle.spaceAroundBinaryOps());
                    }
                    break;
                }
                case EQ: //("=", "operator"),
                {
                    if (doFormat()) {
                        spaceBefore(previous, codeStyle.spaceAroundAssignOps());
                        spaceAfter(current, codeStyle.spaceAroundAssignOps());
                    }
                    break;
                }
                case KW_IF:
                {
                    if (doFormat()) {
                        spaceAfterBefore(current, codeStyle.spaceBeforeIfParen(), LPAREN);
                    }
                    break;
                }
                case KW_ELSE:
                case KW_ELSEIF:
                {
                    if (doFormat()) {
                       formatElse(previous);
                    }
                    break;
                }
                case KW_WHILE:
                {
                    if (doFormat()) {
                        spaceBefore(previous, codeStyle.spaceBeforeWhile());
                        spaceAfterBefore(current, codeStyle.spaceBeforeWhileParen(), LPAREN);
                    }
                    break;
                }
                case KW_FORALL:
                    if (doFormat()) {
                        spaceAfterBefore(current, codeStyle.spaceBeforeForParen(), LPAREN);
                    }
                    break;
                case KW_SELECT: //("switch", "keyword-directive"),
                {
                    if (doFormat()) {
                        spaceAfterBefore(current, codeStyle.spaceBeforeSwitchParen(), LPAREN);
                    }
                    break;
                }
                case KW_DEFAULT: //("default", "keyword-directive"),
                case KW_CASE: //("case", "keyword-directive"),
                {
                    break;
                }
                case KW_CONTINUE: //("continue", "keyword-directive"),
                {
                    break;
                }
            }
            previous = current;
        }
        //System.out.println("Reformatter have prepared "+diffs.getStorage().size()+" diffs");
        return diffs.getStorage();
    }

    /*package local*/ int getParentIndent() {
        return continuationIndent(braces.getSelfIndent());
    }

    /*package local*/ int getCaseIndent() {
        if (codeStyle.indentCasesFromSwitch()) {
            return getParentIndent() + codeStyle.indentSize();
        } else {
            return getParentIndent();
        }
    }

    /*package local*/ int getIndent() {
        return continuationIndent(braces.getIndent());
    }

    /*package local*/ int continuationIndent(int shift){
        return shift;
    }

    private void analyzeLine(Token<FortranTokenId> previous, Token<FortranTokenId> current) {
        Token<FortranTokenId> next = null;
        if (previous == null) {
            switch (current.id()) {
                case LINE_COMMENT_FIXED:
                case LINE_COMMENT_FREE:
                case PREPROCESSOR_DIRECTIVE:
                case NEW_LINE:
                    break;
                case WHITESPACE:
                    next = ts.lookNextLineImportant();
                    break;
                default:
                    next = current;
            }
        } else {
            next = ts.lookNextLineImportant();
        }
        if (next != null) {
            switch (next.id()) {
                case KW_ENDASSOCIATE:
                case KW_ENDBLOCK:
                case KW_ENDBLOCKDATA:
                case KW_ENDDO:
                case KW_ENDENUM:
                case KW_ENDFILE:
                case KW_ENDFORALL:
                case KW_ENDFUNCTION:
                case KW_ENDIF:
                case KW_ENDINTERFACE:
                case KW_ENDMAP:
                case KW_ENDMODULE:
                case KW_ENDPROGRAM:
                case KW_ENDSELECT:
                case KW_ENDSTRUCTURE:
                case KW_ENDSUBROUTINE:
                case KW_ENDTYPE:
                case KW_ENDUNION:
                case KW_ENDWHERE:
                case KW_END:
                    braces.pop(ts);
            }
        }
        if (doFormat()) {
            newLineFormat(previous, current);
        }
        if (next != null) {
            switch (next.id()) {
                case KW_MODULE:
                case KW_PROGRAM:
                case KW_PROCEDURE:
                case KW_SUBROUTINE:
                case KW_FUNCTION:
                case KW_BLOCK:
                case KW_INTERFACE:
                case KW_STRUCTURE:
                case KW_UNION:
                case KW_TYPE:
                case KW_BLOCKDATA:
                case KW_MAP:
//                case KW_FILE:
                case KW_IF:
                case KW_DO:
                case KW_ELSEIF:
                case KW_ELSE:
                case KW_WHILE:
                case KW_FORALL:
                case KW_SELECT:
                    braces.push(next);
            }
        }
    }

    private void formatElse(Token<FortranTokenId> previous) {
        //spaceBefore(previous, codeStyle.spaceBeforeElse());
        if (ts.isFirstLineToken()) {
            DiffResult diff = diffs.getDiffs(ts, -1);
            if (diff != null) {
                boolean done = false;
                if (diff.after != null) {
                    diff.after.replaceSpaces(getParentIndent(), true);
                    done = true;
                }
                if (diff.replace != null && previous.id() == WHITESPACE) {
                    if (!done) {
                        diff.replace.replaceSpaces(getParentIndent(), true);
                        done = true;
                    } else {
                        diff.replace.replaceSpaces(0, false);
                    }
                }
                if (diff.before != null && previous.id() == WHITESPACE){
                    if (!done) {
                        diff.before.replaceSpaces(getParentIndent(), true);
                        done = true;
                    } else {
                        diff.before.replaceSpaces(0, false);
                    }
                }
                if (done) {
                    return;
                }
            }
            if (previous.id() == WHITESPACE) {
                Token<FortranTokenId> p2 = ts.lookPrevious(2);
                if (p2 != null && p2.id()== NEW_LINE) {
                    ts.replacePrevious(previous, 0, getParentIndent(), true);
                } else {
                    ts.replacePrevious(previous, 0, 0, false);
                }
            } else if (previous.id() == NEW_LINE || previous.id() == PREPROCESSOR_DIRECTIVE) {
                ts.addBeforeCurrent(0, getParentIndent(), true);
            }
        }
    }

    private void indentLabel(Token<FortranTokenId> previous) {
        int indent = 0;
        if (!codeStyle.absoluteLabelIndent()) {
            indent = braces.getSelfIndent();
        }
        if (doFormat()) {
            if (!ts.isFirstLineToken()) {
                ts.addBeforeCurrent(1, 0, true);
            } else {
                DiffResult diff = diffs.getDiffs(ts, -1);
                if (diff == null) {
                    if (previous != null && previous.id() == WHITESPACE) {
                        ts.replacePrevious(previous, 0, indent, true);
                    }
                } else {
                    if (diff.after != null) {
                        diff.after.replaceSpaces(indent, true);
                    }
                    if (diff.replace != null) {
                        diff.replace.replaceSpaces(indent, true);
                    }
                }
            }
        }
    }

    private void newLineFormat(Token<FortranTokenId> previous, Token<FortranTokenId> current) {
        if (previous != null) {
            boolean done = false;
            DiffResult diff = diffs.getDiffs(ts, -1);
            if (diff != null) {
                if (diff.after != null) {
                    diff.after.replaceSpaces(0, false); // NOI18N
                    if (diff.replace != null){
                        diff.replace.replaceSpaces(0, false); // NOI18N
                    }
                    done = true;
                } else if (diff.replace != null) {
                    diff.replace.replaceSpaces(0, false); // NOI18N
                    done = true;
                }
            }
            if (!done && previous.id() == WHITESPACE) {
                ts.replacePrevious(previous, 0, 0, false);
            }
        } else {
            int space = -1;
            if (space == -1) {
                space = getIndent();
            }
            if (current.id() == WHITESPACE) {
                ts.replaceCurrent(current, 0, space, true);
            } else {
                if (space > 0) {
                    ts.addBeforeCurrent(0, space, true);
                }
            }
            return;
        }
        Token<FortranTokenId> next = ts.lookNext();
        if (next != null) {
            if (next.id() == NEW_LINE) {
                return;
            }
            int space = -1;
            Token<FortranTokenId> first = ts.lookNextLineImportant();
            if (first != null) {
                switch (first.id()) {
                    case KW_CASE:
                    case KW_DEFAULT:
                        space = getCaseIndent();
                        break;
                }
            }
            if (space == -1) {
                space = getIndent();
            }
            if (next.id() == WHITESPACE) {
                ts.replaceNext(current, next, 0, space, true);
            } else {
                if (space > 0) {
                    ts.addAfterCurrent(current, 0, space, true);
                }
            }
        }
    }


    // indent new line after preprocessor directive
    private void indentNewLine(Token<FortranTokenId> current){
        if (current.id() == NEW_LINE) {
            return;
        }
        int space;
        Token<FortranTokenId> first = ts.lookNextLineImportant();
        if (first != null && (first.id() == KW_CASE ||first.id() == KW_DEFAULT)){
            space = getCaseIndent();
        } else {
            space = getIndent();
        }
        if (current.id() == WHITESPACE) {
            ts.replaceCurrent(current, 0, space, true);
        } else {
            ts.addBeforeCurrent(0, space, true);
        }
    }

    private void processColumn(Token<FortranTokenId> previous, Token<FortranTokenId> current) {
        if (doFormat()) {
            Token<FortranTokenId> p = ts.lookPreviousImportant();
            if (p != null && p.id() == KW_DEFAULT) {
                // TODO use flase?
                spaceBefore(previous, false);
                return;
            }
            Token<FortranTokenId> p2 = ts.lookPreviousImportant(2);
            if (p2 != null && p2.id() == KW_CASE) {
                // TODO use flase?
                spaceBefore(previous, false);
                return;
            }
            spaceBefore(previous, false);
            return;
        }
    }

    private void reformatBlockComment(Token<FortranTokenId> previous, Token<FortranTokenId> current) {
        if (!ts.isFirstLineToken()){
            // do not format block comments inside cole line
            return;
        }
        int originalIndent = 0;
        if (previous == null || previous.id() == NEW_LINE || previous.id() == PREPROCESSOR_DIRECTIVE){
            originalIndent = 0;
        } else if (previous.id()==WHITESPACE) {
            CharSequence s = previous.text();
            for (int i = 0; i < previous.length(); i++) {
                if (s.charAt(i) == ' '){ // NOI18N
                    originalIndent++;
                } else if (s.charAt(i) == '\t'){ // NOI18N
                    originalIndent = (originalIndent/tabSize+1)*tabSize;
                }
            }
        }
        int requiredIndent = getIndent();
        int start = -1;
        int end = -1;
        int currentIndent = 0;
        CharSequence s = current.text();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\n') { // NOI18N
                start = i;
                end = i;
                currentIndent = 0;
            } else if (s.charAt(i) == ' ' || s.charAt(i) == '\t') { // NOI18N
                end = i;
                if (s.charAt(i) == ' '){ // NOI18N
                    currentIndent++;
                } else if (s.charAt(i) == '\t'){ // NOI18N
                    currentIndent = (currentIndent/tabSize+1)*tabSize;
                }
            } else {
                if (start >= 0) {
                    addCommentIndent(start, end, s.charAt(i), requiredIndent, originalIndent, currentIndent);
                }
                start = -1;
            }
        }
        addCommentIndent(start, end, '*', requiredIndent, originalIndent, currentIndent); // NOI18N
    }

    private void addCommentIndent(int start, int end, char c, int requiredIndent, int originalIndent, int currentIndent) {
        if (start >= 0 && end >= start) {
            if (c == '*') { // NOI18N
                diffs.addFirst(ts.offset() + start + 1, ts.offset() + end + 1, 0, 1 + requiredIndent, true);
            } else {
                int indent = requiredIndent + currentIndent - originalIndent;
                if (indent < 0) {
                    indent = requiredIndent;
                }
                diffs.addFirst(ts.offset() + start + 1, ts.offset() + end + 1, 0, indent, true);
            }
        }
    }

    private void whiteSpaceFormat(Token<FortranTokenId> previous, Token<FortranTokenId> current) {
        if (previous != null) {
            DiffResult diff = diffs.getDiffs(ts, 0);
            if (diff != null) {
                if (diff.replace != null) {
                    return;
                }
                if (diff.before != null){
                    ts.replaceCurrent(current, 0, 0, false);
                    return;
                }
            }
            if (previous.id() == NEW_LINE ||
                previous.id() == PREPROCESSOR_DIRECTIVE) {
                // already formatted
                return;
            }
        }
        Token<FortranTokenId> next = ts.lookNext();
        if (next != null && next.id() == NEW_LINE) {
            // will be formatted on new line
            return;
        }
        if (previous != null) {
            ts.replaceCurrent(current, 0, 1, false);
        }
    }

    private void spaceBefore(Token<FortranTokenId> previous, boolean add){
        if (previous != null && !ts.isFirstLineToken()) {
            if (add) {
                DiffResult diff = diffs.getDiffs(ts, -1);
                if (diff != null) {
                    if (diff.after != null && !diff.after.hasNewLine()) {
                        diff.after.replaceSpaces(1, false);
                        if (diff.replace != null && !diff.replace.hasNewLine()){
                            diff.replace.replaceSpaces(0, false);
                        }
                        return;
                    } else if (diff.replace != null && !diff.replace.hasNewLine()) {
                        diff.replace.replaceSpaces(1, false);
                        return;
                    }
                }
                if (!(previous.id() == WHITESPACE ||
                      previous.id() == NEW_LINE ||
                      previous.id() == PREPROCESSOR_DIRECTIVE)) {
                    ts.addBeforeCurrent(0, 1, false);
                }
            } else if (canRemoveSpaceBefore(previous)){
                DiffResult diff = diffs.getDiffs(ts, -1);
                if (diff != null) {
                    if (diff.after != null && !diff.after.hasNewLine()) {
                        diff.after.replaceSpaces(0, false);
                        if (diff.replace != null && !diff.replace.hasNewLine()){
                            diff.replace.replaceSpaces(0, false);
                        }
                        return;
                    } else if (diff.replace != null && !diff.replace.hasNewLine()) {
                        diff.replace.replaceSpaces(0, false);
                        return;
                    }
                }
                if (previous.id() == WHITESPACE) {
                    ts.replacePrevious(previous, 0, 0, false);
                }
            }
        }
    }

    private boolean canRemoveSpaceBefore(Token<FortranTokenId> previous){
        if (previous == null) {
            return false;
        }
        if (previous.id() == WHITESPACE) {
            Token<FortranTokenId> p2 = ts.lookPrevious(2);
            if (p2 == null) {
                return true;
            }
            previous = p2;
        }
        FortranTokenId prev = previous.id();
        FortranTokenId curr = ts.token().id();
        return canRemoveSpace(prev,curr);
    }

    private boolean canRemoveSpace(FortranTokenId prev, FortranTokenId curr){
        if (prev == IDENTIFIER && curr == IDENTIFIER) {
            return false;
        }
        String currCategory = curr.primaryCategory();
        String prevCategory = prev.primaryCategory();
        if (KEYWORD_CATEGORY.equals(prevCategory)) {
            if (SPECIAL_CATEGORY.equals(currCategory)) {
                return true;
            } else if (curr == COLON) {
                return true;
            }
            return false;
        } else if (OPERATOR_CATEGORY.equals(prevCategory)) {
            if (OPERATOR_CATEGORY.equals(currCategory)) {
                if (prev == COLON || curr == COLON){
                    return true;
                }
                return false;
            }
            return true;
        } else if (prev == IDENTIFIER) {
            if (NUMBER_CATEGORY.equals(currCategory) ||
                LITERAL_CATEGORY.equals(currCategory) ||
                STRING_CATEGORY.equals(currCategory)) {
                return false;
            }
        }
        return true;
    }

    private boolean canRemoveSpaceAfter(Token<FortranTokenId> current){
        Token<FortranTokenId> next = ts.lookNext();
        if (next == null) {
            return false;
        }
        if (next.id() == WHITESPACE) {
            Token<FortranTokenId> n2 = ts.lookNext(2);
            if (n2 == null) {
                return true;
            }
            next = n2;
        }
        FortranTokenId curr = next.id();
        FortranTokenId prev = current.id();
        return canRemoveSpace(prev,curr);
    }

    private void spaceAfter(Token<FortranTokenId> current, boolean add){
        Token<FortranTokenId> next = ts.lookNext();
        if (next != null) {
            if (add) {
                if (!(next.id() == WHITESPACE ||
                      next.id() == NEW_LINE)) {
                    ts.addAfterCurrent(current, 0, 1, false);
                }
            } else if (canRemoveSpaceAfter(current)){
                if (next.id() == WHITESPACE) {
                    ts.replaceNext(current, next, 0, 0, false);
                }
            }
        }
    }

    private void spaceAfterBefore(Token<FortranTokenId> current, boolean add, FortranTokenId before){
        Token<FortranTokenId> next = ts.lookNext();
        if (next != null) {
            if (next.id() == WHITESPACE) {
                Token<FortranTokenId> p = ts.lookNext(2);
                if (p!=null && p.id()==before) {
                    if (!add) {
                        ts.replaceNext(current, next, 0, 0, false); // NOI18N
                    }
                }
            } else if (next.id() == before) {
                if (add) {
                    ts.addAfterCurrent(current, 0, 1, false);
                }
            }
        }
    }

    private void formatLeftParen(Token<FortranTokenId> previous, Token<FortranTokenId> current) {
        if (previous != null){
            Token<FortranTokenId> p = ts.lookPreviousStatement();
            if (p != null) {
                switch(p.id()) {
                    case KW_IF:
                        spaceAfter(current, codeStyle.spaceWithinIfParens());
                        return;
                    case KW_FORALL:
                        spaceAfter(current, codeStyle.spaceWithinForParens());
                        return;
                    case KW_WHILE:
                        spaceAfter(current, codeStyle.spaceWithinWhileParens());
                        return;
                    case KW_SELECT:
                        spaceAfter(current, codeStyle.spaceWithinSwitchParens());
                        return;
                }
            }
            p = ts.lookPreviousImportant();
            if (p != null && p.id() == IDENTIFIER) {
                FortranStackEntry entry = braces.peek();
                if (entry == null){
                    spaceBefore(previous, codeStyle.spaceBeforeMethodDeclParen());
                    spaceAfter(current, codeStyle.spaceWithinMethodDeclParens());
                    return;
                }
                switch (entry.getKind()) {
                    case KW_TYPE:
                    case KW_UNION:
                        spaceBefore(previous, codeStyle.spaceBeforeMethodDeclParen());
                        spaceAfter(current, codeStyle.spaceWithinMethodDeclParens());
                        return;
                }
                spaceBefore(previous, codeStyle.spaceBeforeMethodCallParen());
                spaceAfter(current, codeStyle.spaceWithinMethodCallParens());
                return;
            } else if (p != null && KEYWORD_CATEGORY.equals(p.id().primaryCategory())){
                switch (p.id()) {
                    case KW_RETURN:
                        spaceBefore(previous, codeStyle.spaceBeforeKeywordParen());
                        return;
                }
                return;
            } else {
                spaceAfter(current, codeStyle.spaceWithinParens());
            }
        }
    }

    private void formatRightParen(Token<FortranTokenId> previous, Token<FortranTokenId> current) {
        if (previous != null){
            Token<FortranTokenId> p = ts.lookPreviousStatement();
            if (p != null) {
                switch(p.id()) {
                    case KW_IF:
                        spaceBefore(previous, codeStyle.spaceWithinIfParens());
                        return;
                    case KW_FORALL:
                        spaceBefore(previous, codeStyle.spaceWithinForParens());
                        return;
                    case KW_WHILE:
                        spaceBefore(previous, codeStyle.spaceWithinWhileParens());
                        return;
                    case KW_SELECT:
                        spaceBefore(previous, codeStyle.spaceWithinSwitchParens());
                        return;
                }
            }
            p = getImportantBeforeBrace();
            if (p != null && p.id() == IDENTIFIER) {
                FortranStackEntry entry = braces.peek();
                if (entry == null){
                    spaceBefore(previous, codeStyle.spaceWithinMethodDeclParens());
                    return;
                }
                switch (entry.getKind()) {
                    case KW_TYPE:
                        spaceBefore(previous, codeStyle.spaceWithinMethodDeclParens());
                        return;
                }
                spaceBefore(previous, codeStyle.spaceWithinMethodCallParens());
                return;
            } else {
                spaceBefore(previous, codeStyle.spaceWithinParens());
            }
        }
    }

    private Token<FortranTokenId> getImportantBeforeBrace(){
        int index = ts.index();
        try {
            if (ts.token().id() == RPAREN) {
                int depth = 1;
                while (ts.movePrevious()) {
                    switch (ts.token().id()) {
                        case RPAREN:
                            depth++;
                            break;
                        case LPAREN:
                        {
                            depth--;
                            if (depth <=0) {
                                return ts.lookPreviousImportant();
                            }
                            break;
                        }
                    }
                }
            }
            return null;
        } finally {
            ts.moveIndex(index);
            ts.moveNext();
        }
    }

    /*package local*/ boolean doFormat(){
        return ts.offset() >= this.startOffset;
    }
}
