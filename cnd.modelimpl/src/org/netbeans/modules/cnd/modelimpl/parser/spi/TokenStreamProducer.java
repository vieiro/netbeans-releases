/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.modelimpl.parser.spi;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.cnd.api.lexer.CndLexerUtilities;
import org.netbeans.cnd.api.lexer.CppTokenId;
import org.netbeans.cnd.api.lexer.FortranTokenId;
import org.netbeans.modules.cnd.antlr.TokenStream;
import org.netbeans.modules.cnd.api.model.CsmInclude;
import org.netbeans.modules.cnd.api.project.NativeProject;
import org.netbeans.modules.cnd.apt.debug.APTTraceFlags;
import org.netbeans.modules.cnd.apt.support.APTToken;
import org.netbeans.modules.cnd.apt.support.APTTokenTypes;
import org.netbeans.modules.cnd.apt.support.api.PreprocHandler;
import org.netbeans.modules.cnd.apt.support.lang.APTLanguageSupport;
import org.netbeans.modules.cnd.apt.support.spi.APTIndexFilter;
import org.netbeans.modules.cnd.apt.utils.APTUtils;
import org.netbeans.modules.cnd.indexing.api.CndTextIndex;
import org.netbeans.modules.cnd.modelimpl.content.file.FileContent;
import org.netbeans.modules.cnd.modelimpl.csm.core.FileBuffer;
import org.netbeans.modules.cnd.modelimpl.csm.core.FileImpl;
import org.netbeans.modules.cnd.modelimpl.csm.core.FilePreprocessorConditionState;
import org.netbeans.modules.cnd.modelimpl.parser.apt.APTTokenStreamProducer;
import org.netbeans.modules.cnd.modelimpl.parser.clank.ClankTokenStreamProducer;
import org.netbeans.modules.cnd.support.Interrupter;
import org.openide.util.Lookup;

/**
 *
 * @author Vladimir Voskresensky
 */
public abstract class TokenStreamProducer {
    private PreprocHandler curPreprocHandler;
    private String language = APTLanguageSupport.GNU_CPP;
    private String languageFlavor = APTLanguageSupport.FLAVOR_UNKNOWN;
    private final FileImpl fileImpl;
    private final FileContent fileContent;
    private boolean allowToCacheOnRelease;
    private CodePatch codePatch;

    protected TokenStreamProducer(FileImpl fileImpl, FileContent newFileContent) {
        assert fileImpl != null : "null file is not allowed";        
        assert newFileContent != null : "null file content is not allowed";        
        this.fileImpl = fileImpl;
        this.fileContent = newFileContent;
    }        
    
    public static TokenStreamProducer create(FileImpl file, boolean emptyFileContent, boolean index) {
        FileContent newFileContent = FileContent.getHardReferenceBasedCopy(file.getCurrentFileContent(), emptyFileContent);
        if (index) {
            indexFileContent(file);
        }
        if (APTTraceFlags.USE_CLANK) {
            return ClankTokenStreamProducer.createImpl(file, newFileContent);
        } else {
            return APTTokenStreamProducer.createImpl(file, newFileContent);
        }
    }

    public abstract TokenStream getTokenStreamOfIncludedFile(PreprocHandler.State includeOwnerState, CsmInclude include, Interrupter interrupter);

    public abstract TokenStream getTokenStream(boolean triggerParsingActivity, boolean filterOutComments, boolean applyLanguageFilter, Interrupter interrupter);
    
    /** must be called when TS was completely consumed */
    public abstract FilePreprocessorConditionState release();

    public void prepare(PreprocHandler handler, String language, String languageFlavor, boolean allowToCacheOnRelease) {
        assert handler != null : "null preprocHandler is not allowed";
        curPreprocHandler = handler;
        assert language != null : "null language is not allowed";
        this.language = language;
        assert languageFlavor != null : "null language flavor is not allowed";
        this.languageFlavor = languageFlavor;
        this.allowToCacheOnRelease = allowToCacheOnRelease;
    }
    
    public PreprocHandler getCurrentPreprocHandler() {
        return curPreprocHandler;
    }
    
    public String getLanguage() {
        return language;
    }        

    public String getLanguageFlavor() {
        return languageFlavor;
    }

    public FileImpl getMainFile() {
        return fileImpl;
    }    

    public FileContent getFileContent() {
        assert fileContent != null;
        return fileContent;
    }

    protected final boolean isAllowedToCacheOnRelease() {
        return allowToCacheOnRelease;
    }

    public CodePatch getFixCode() {
        return codePatch;
    }

    public void setCodePatch(CodePatch codePatch) {
        this.codePatch = codePatch;
    }

    public static final class CodePatch {
        private final int startOffset;
        private final int endOffset;
        private final String patch;

        public CodePatch(int startOffset, int endOffset, String patch) {
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.patch = patch;
        }

        public int getStartOffset() {
            return startOffset;
        }

        public int getEndOffset() {
            return endOffset;
        }

        public String getPatch() {
            return patch;
        }

    }

    private static void indexFileContent(FileImpl file) {
        TokenSequence<?> tsToIndex = createFileTokenSequence(file);
        if (tsToIndex == null) {
            return;
        }
        APTIndexFilter[] indexFilters = getIndexFilters(file);
        assert indexFilters != null;
        assert indexFilters.length > 0 : "must be at least one filter";
        Set<CharSequence> ids = new HashSet<>(1024);
        indexFileTokens(tsToIndex, indexFilters, ids);
        CndTextIndex.put(file.getTextIndexKey(), ids);
    }

    private static TokenSequence<?> createFileTokenSequence(FileImpl file) {
        FileBuffer buffer = file.getBuffer();
        if (buffer == null) {
            return null;
        }
        char[] charBuffer;
        try {
            charBuffer = buffer.getCharBuffer();
        } catch (IOException ex) {
            // OK for removed files
            return null;
        }
        Language<TokenId> language;
        Set<TokenId> mergedSkippedTokens;
        if (APTLanguageSupport.FORTRAN.equals(file.getFileLanguage())) {
            language = FortranSkippedTokensPool.LANGUAGE;
            mergedSkippedTokens = FortranSkippedTokensPool.SKIP_TOKENS;
        } else {
            language = CppSkippedTokensPool.LANGUAGE;
            mergedSkippedTokens = CppSkippedTokensPool.SKIP_TOKENS;
        }
        TokenHierarchy<?> hi = TokenHierarchy.create(new CharBufferChars(charBuffer), false, language, mergedSkippedTokens, null);
//        TokenHierarchy<?> hi = TokenHierarchy.create(new String(charBuffer), false, language, mergedSkippedTokens, null);
        List<TokenSequence<?>> tsList = hi.embeddedTokenSequences(0, true);
        // Go from inner to outer TSes
        TokenSequence<?> tsToIndex = null;
        for (int i = tsList.size() - 1; i >= 0; i--) {
            TokenSequence<?> ts = tsList.get(i);
            final Language<?> lang = ts.languagePath().innerLanguage();
            if (CndLexerUtilities.isCppLanguage(lang, false) || (lang == FortranTokenId.languageFortran())) {
                tsToIndex = ts;
            }
        }
        return tsToIndex;
    }


    private static final class CppSkippedTokensPool {
        static final Set<TokenId> SKIP_TOKENS;
        static final Language<TokenId> LANGUAGE;
        static {
            LANGUAGE = (Language<TokenId>)(Language<?>)CppTokenId.languageCpp();
            Set<TokenId> skipNumTokens = LANGUAGE.tokenCategoryMembers(CppTokenId.NUMBER_CATEGORY);
            Set<TokenId> skipWSTokens = LANGUAGE.tokenCategoryMembers(CppTokenId.WHITESPACE_CATEGORY);
            Set<TokenId> skipCommentTokens = LANGUAGE.tokenCategoryMembers(CppTokenId.COMMENT_CATEGORY);
            Set<TokenId> skipSeparatorTokens = LANGUAGE.tokenCategoryMembers(CppTokenId.SEPARATOR_CATEGORY);
            Set<TokenId> skipOperatorTokens = LANGUAGE.tokenCategoryMembers(CppTokenId.OPERATOR_CATEGORY);
            SKIP_TOKENS = LANGUAGE.merge(skipNumTokens, 
                        LANGUAGE.merge(skipWSTokens, 
                            LANGUAGE.merge(skipCommentTokens, 
                                    LANGUAGE.merge(skipSeparatorTokens, 
                                            skipOperatorTokens))));
        }
    }
    
    private static final class FortranSkippedTokensPool {
        static final Set<TokenId> SKIP_TOKENS;
        static final Language<TokenId> LANGUAGE;
        static {
            LANGUAGE = (Language<TokenId>)(Language<?>)FortranTokenId.languageFortran();
            Set<TokenId> skipNumTokens = LANGUAGE.tokenCategoryMembers(FortranTokenId.NUMBER_CATEGORY);
            Set<TokenId> skipWSTokens = LANGUAGE.tokenCategoryMembers(FortranTokenId.WHITESPACE_CATEGORY);
            Set<TokenId> skipCommentTokens = LANGUAGE.tokenCategoryMembers(FortranTokenId.COMMENT_CATEGORY);
            Set<TokenId> skipSeparatorTokens = LANGUAGE.tokenCategoryMembers(FortranTokenId.SPECIAL_CATEGORY);
            Set<TokenId> skipOperatorTokens = LANGUAGE.tokenCategoryMembers(FortranTokenId.OPERATOR_CATEGORY);
            Set<TokenId> skipKwdOperatorTokens = LANGUAGE.tokenCategoryMembers(FortranTokenId.KEYWORD_OPERATOR_CATEGORY);
            SKIP_TOKENS = LANGUAGE.merge(skipNumTokens, 
                        LANGUAGE.merge(skipWSTokens, 
                            LANGUAGE.merge(skipCommentTokens, 
                                    LANGUAGE.merge(skipSeparatorTokens, 
                                            LANGUAGE.merge(skipOperatorTokens,
                                                     skipKwdOperatorTokens)))));
        }
    }
    
    private static void indexFileTokens(TokenSequence<?> expTS, APTIndexFilter[] indexFilters, Set<CharSequence> ids) {
        if (expTS != null) {
            expTS.moveStart();
            while (expTS.moveNext()) {
                Token<?> expToken = expTS.token();
                // index preprocessor directive tokens as well
                if (expToken.id() == CppTokenId.PREPROCESSOR_DIRECTIVE) {
                    indexFileTokens(expTS.embedded(), indexFilters, ids);
                } else {
                    String primaryCategory = expToken.id().primaryCategory();
                    if (CppTokenId.IDENTIFIER_CATEGORY.equals(primaryCategory) ||
                        CppTokenId.PREPROCESSOR_IDENTIFIER_CATEGORY.equals(primaryCategory) ||
                        CppTokenId.KEYWORD_CATEGORY.equals(primaryCategory)) {
                        ids.add(expToken.text().toString());
                    } if (CppTokenId.STRING_CATEGORY.equals(primaryCategory)) {
//                        for (APTIndexFilter filter : indexFilters) {
//                            CharSequence indexText = filter.getIndexText(token);
//                            if (indexText != null) {
//                                ids.add(indexText);
//                            }
//                        }
                    }
                }
            }
        }
    }

    private static APTIndexFilter[] getIndexFilters(FileImpl file) {
        Collection<? extends APTIndexFilter> extraIndexFilters = Collections.emptyList();
        Object pp = file.getProject().getPlatformProject();
        if (pp instanceof NativeProject) {
            final Lookup.Provider project = ((NativeProject) pp).getProject();
            if (project != null) {
                extraIndexFilters = project.getLookup().lookupAll(APTIndexFilter.class);
            }
        }
        // index using CndLexer and index filters
        final APTIndexFilter[] indexFilters = new APTIndexFilter[extraIndexFilters.size() + 1];
        int i = 0;
        for (APTIndexFilter f : extraIndexFilters) {
            indexFilters[i] = f;
            i++;
        }
        indexFilters[i] = new DefaultIndexFilter();
        return indexFilters;
    }
    
    private static final class DefaultIndexFilter implements APTIndexFilter {
        @Override
        public CharSequence getIndexText(APTToken token) {
            if (APTUtils.isID(token) || token.getType() == APTTokenTypes.ID_DEFINED) {
                return token.getTextID();
            }
            return null;
        }
    }    

    private static final class CharBufferChars implements CharSequence {
        private final char[] buffer;
        private final int firstIndex;
        private final int length;

        public CharBufferChars(char[] charBuffer) {
            this(charBuffer, 0, charBuffer.length);
        }

        private CharBufferChars(char[] charBuffer, int firstInclusiveIndex, int lastExclusiveIndex) {
            assert charBuffer != null;
            this.buffer = charBuffer;
            this.firstIndex = firstInclusiveIndex;
            this.length = lastExclusiveIndex - firstInclusiveIndex;
        }

        @Override
        public int length() {
            return length;
        }

        @Override
        public char charAt(int index) {
            return buffer[index];
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return new CharBufferChars(buffer, start, end);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + Arrays.hashCode(this.buffer);
            hash = 97 * hash + this.firstIndex;
            hash = 97 * hash + this.length;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CharBufferChars other = (CharBufferChars) obj;
            if (this.firstIndex != other.firstIndex) {
                return false;
            }
            if (this.length != other.length) {
                return false;
            }
            if (!Arrays.equals(this.buffer, other.buffer)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return new String(this.buffer, this.firstIndex, this.length);
        }
    }

}
