/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.latte.lexer;

import java.util.Objects;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;

@org.netbeans.api.annotations.common.SuppressWarnings({"SF_SWITCH_FALLTHROUGH", "URF_UNREAD_FIELD", "DLS_DEAD_LOCAL_STORE", "DM_DEFAULT_ENCODING"})
%%

%public
%class LatteMarkupColoringLexer
%type LatteMarkupTokenId
%function findNextToken
%unicode
%caseless
%char

%eofval{
        if(input.readLength() > 0) {
            // backup eof
            input.backup(1);
            //and return the text as error token
            return LatteMarkupTokenId.T_ERROR;
        } else {
            return null;
        }
%eofval}

%{

    private LatteStateStack stack = new LatteStateStack();
    private LexerInput input;

    public LatteMarkupColoringLexer(LexerRestartInfo info) {
        this.input = info.input();
        if(info.state() != null) {
            //reset state
            setState((LexerState) info.state());
        } else {
            zzState = zzLexicalState = YYINITIAL;
            stack.clear();
        }

    }

    private enum Syntax {
        LATTE,
        DOUBLE,
        ASP,
        PYTHON,
        OFF;
    }

    public static final class LexerState  {
        final LatteStateStack stack;
        /** the current state of the DFA */
        final int zzState;
        /** the current lexical state */
        final int zzLexicalState;

        LexerState(LatteStateStack stack, int zzState, int zzLexicalState) {
            this.stack = stack;
            this.zzState = zzState;
            this.zzLexicalState = zzLexicalState;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 29 * hash + Objects.hashCode(this.stack);
            hash = 29 * hash + this.zzState;
            hash = 29 * hash + this.zzLexicalState;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final LexerState other = (LexerState) obj;
            if (!Objects.equals(this.stack, other.stack)) {
                return false;
            }
            if (this.zzState != other.zzState) {
                return false;
            }
            if (this.zzLexicalState != other.zzLexicalState) {
                return false;
            }
            return true;
        }
    }

    public LexerState getState() {
        return new LexerState(stack.createClone(), zzState, zzLexicalState);
    }

    public void setState(LexerState state) {
        this.stack.copyFrom(state.stack);
        this.zzState = state.zzState;
        this.zzLexicalState = state.zzLexicalState;
    }

    protected int getZZLexicalState() {
        return zzLexicalState;
    }

    protected void popState() {
        yybegin(stack.popStack());
    }

    protected void pushState(final int state) {
        stack.pushStack(getZZLexicalState());
        yybegin(state);
    }


 // End user code

%}

WHITESPACE=[ \t\r\n]+
D_STRING="\""([^"\r""\n""\r\n""\""])*"\""
S_STRING="'"([^"\r""\n""\r\n""'"])*"'"
STRING = {D_STRING} | {S_STRING}
KEYWORD="true"|"false"|"null"|"and"|"or"|"xor"|"clone"|"new"|"instanceof"|"return"|"continue"|"break"
CAST="(" ("expand"|"string"|"array"|"int"|"integer"|"float"|"bool"|"boolean"|"object") ")"
VARIABLE="$"[a-zA-Z0-9_]+
NUMBER=["+""-"]?[0-9]+(\.[0-9]+)?(e[0-9]+)?
STRICT_CHAR="::"|"=>"|"as"
GLOBAL_CHAR=[^\"']
SYMBOL=[a-zA-Z0-9_]+(\-[a-zA-Z0-9_]+)*


%state ST_HIGHLIGHTING_ERROR

%%
<YYINITIAL>{WHITESPACE}+ {
    return LatteMarkupTokenId.T_WHITESPACE;
}

<YYINITIAL> {
    {NUMBER} {
        return LatteMarkupTokenId.T_NUMBER;
    }
    {KEYWORD} {
        return LatteMarkupTokenId.T_KEYWORD;
    }
    {CAST} {
        return LatteMarkupTokenId.T_CAST;
    }
    {VARIABLE} {
        return LatteMarkupTokenId.T_VARIABLE;
    }
    {STRING} {
        return LatteMarkupTokenId.T_STRING;
    }
    {STRICT_CHAR} {
        return LatteMarkupTokenId.T_CHAR;
    }
    {SYMBOL} {
        return LatteMarkupTokenId.T_SYMBOL;
    }
    {GLOBAL_CHAR} {
        return LatteMarkupTokenId.T_CHAR;
    }
}

/* ============================================
   Stay in this state until we find a whitespace.
   After we find a whitespace we go the the prev state and try again from the next token.
   ============================================ */
<ST_HIGHLIGHTING_ERROR> {
    {WHITESPACE} {
        popState();
    }
    . {
        return LatteMarkupTokenId.T_ERROR;
    }
}

/* ============================================
   This rule must be the last in the section!!
   it should contain all the states.
   ============================================ */
<YYINITIAL> {
    . {
        yypushback(yylength());
        pushState(ST_HIGHLIGHTING_ERROR);
    }
}
