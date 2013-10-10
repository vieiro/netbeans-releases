/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.latte.hints;

import java.util.Collections;
import java.util.List;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.EditList;
import org.netbeans.modules.csl.api.Hint;
import org.netbeans.modules.csl.api.HintFix;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.RuleContext;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.php.latte.lexer.LatteMarkupTokenId;
import org.netbeans.modules.php.latte.lexer.LatteTopTokenId;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author Ondrej Brejla <obrejla@netbeans.org>
 */
public abstract class DeprecatedMacrosHint extends HintRule {
    private FileObject fileObject;
    private BaseDocument baseDocument;
    private List<Hint> hints;

    @Override
    public void invoke(RuleContext context, List<Hint> result) {
        Snapshot snapshot = context.parserResult.getSnapshot();
        hints = result;
        fileObject = snapshot.getSource().getFileObject();
        baseDocument = context.doc;
        if (fileObject != null) {
            TokenSequence<LatteTopTokenId> topTs = snapshot.getTokenHierarchy().tokenSequence(LatteTopTokenId.language());
            if (topTs != null) {
                checkTopTokenSequence(topTs);
            }
        }
    }

    private void checkTopTokenSequence(TokenSequence<LatteTopTokenId> topTs) {
        topTs.moveStart();
        TokenSequence<LatteMarkupTokenId> ts;
        while (topTs.moveNext()) {
            ts = topTs.embeddedJoined(LatteMarkupTokenId.language());
            if (ts != null) {
                checkMarkupTokenSequence(ts);
            }
        }
    }

    private void checkMarkupTokenSequence(TokenSequence<LatteMarkupTokenId> ts) {
        ts.moveStart();
        Token<LatteMarkupTokenId> token;
        while (ts.moveNext()) {
            token = ts.token();
            if (isDeprecatedToken(token)) {
                createHint(ts.offset(), token);
            }
        }
    }

    @NbBundle.Messages("DeprecatedMacroHintText=Deprecated Macro")
    private void createHint(int startOffset, Token<LatteMarkupTokenId> token) {
        OffsetRange offsetRange = new OffsetRange(startOffset, startOffset + token.length());
        if (showHint(offsetRange, baseDocument)) {
            String replaceText = getReplaceText();
            List<HintFix> fixes = replaceText == null
                    ? Collections.<HintFix>emptyList()
                    : Collections.<HintFix>singletonList(new Fix(startOffset, token, baseDocument, replaceText));
            hints.add(new Hint(this, Bundle.DeprecatedMacroHintText(), fileObject, offsetRange, fixes, 500));
        }
    }

    protected abstract boolean isDeprecatedToken(Token<LatteMarkupTokenId> token);

    protected abstract String getReplaceText();

    public static final class WidgetMacroHint extends DeprecatedMacrosHint {
        private static final String HINT_ID = "latte.widget.macro.hint"; //NOI18N
        private static final String WIDGET_MACRO = "widget"; //NOI18N
        private static final String CONTROL_MACRO = "control"; //NOI18N

        @Override
        public String getId() {
            return HINT_ID;
        }

        @Override
        @NbBundle.Messages("WidgetMacroHintDesc=Widget macro is deprecated, use 'control' macro instead.")
        public String getDescription() {
            return Bundle.WidgetMacroHintDesc();
        }

        @Override
        @NbBundle.Messages("WidgetMacroHintDisp=Widget Macro")
        public String getDisplayName() {
            return Bundle.WidgetMacroHintDisp();
        }

        @Override
        protected boolean isDeprecatedToken(Token<LatteMarkupTokenId> token) {
            return token != null && LatteMarkupTokenId.T_MACRO_START.equals(token.id()) && WIDGET_MACRO.equals(token.text().toString().trim());
        }

        @Override
        protected String getReplaceText() {
            return CONTROL_MACRO;
        }

    }

    public static final class IfCurrentMacroHint extends DeprecatedMacrosHint {
        private static final String HINT_ID = "latte.ifcurrent.macro.hint"; //NOI18N
        private static final String DEPRECATED_MACRO = "ifCurrent"; //NOI18N

        @Override
        public String getId() {
            return HINT_ID;
        }

        @Override
        @NbBundle.Messages("IfCurrentMacroHintDesc=IfCurrent macro is deprecated, use 'n:class=\"$presenter->linkCurrent ? ...\"' instead.")
        public String getDescription() {
            return Bundle.IfCurrentMacroHintDesc();
        }

        @Override
        @NbBundle.Messages("IfCurrentMacroHintDisp=IfCurrent Macro")
        public String getDisplayName() {
            return Bundle.IfCurrentMacroHintDisp();
        }

        @Override
        protected boolean isDeprecatedToken(Token<LatteMarkupTokenId> token) {
            return token != null && LatteMarkupTokenId.T_MACRO_START.equals(token.id()) && DEPRECATED_MACRO.equals(token.text().toString().trim());
        }

        @Override
        protected String getReplaceText() {
            return null;
        }

    }

    private static final class Fix implements HintFix {
        private final int startOffset;
        private final Token<LatteMarkupTokenId> token;
        private final BaseDocument baseDocument;
        private final String replaceText;

        private Fix(int startOffset, Token<LatteMarkupTokenId> token, BaseDocument baseDocument, String replaceText) {
            this.startOffset = startOffset;
            this.token = token;
            this.baseDocument = baseDocument;
            this.replaceText = replaceText;
        }

        @Override
        @NbBundle.Messages({
            "# {0} - text of replacement",
            "DeprecatedMacroHintFix=Replace with: {0}"
        })
        public String getDescription() {
            return Bundle.DeprecatedMacroHintFix(replaceText);
        }

        @Override
        public void implement() throws Exception {
            EditList editList = new EditList(baseDocument);
            editList.replace(startOffset, token.length(), replaceText, true, 0);
            editList.apply();
        }

        @Override
        public boolean isSafe() {
            return true;
        }

        @Override
        public boolean isInteractive() {
            return false;
        }

    }

}
