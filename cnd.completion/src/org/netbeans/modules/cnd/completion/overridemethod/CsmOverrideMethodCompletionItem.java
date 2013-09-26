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

package org.netbeans.modules.cnd.completion.overridemethod;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.cnd.api.model.CsmClass;
import org.netbeans.modules.cnd.api.model.CsmClassifier;
import org.netbeans.modules.cnd.api.model.CsmFunction;
import org.netbeans.modules.cnd.api.model.CsmMember;
import org.netbeans.modules.cnd.api.model.CsmMethod;
import org.netbeans.modules.cnd.api.model.CsmParameter;
import org.netbeans.modules.cnd.api.model.CsmScope;
import org.netbeans.modules.cnd.api.model.CsmTemplate;
import org.netbeans.modules.cnd.api.model.CsmTemplateParameter;
import org.netbeans.modules.cnd.api.model.CsmType;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import org.netbeans.modules.cnd.completion.spi.dynhelp.CompletionDocumentationProvider;
import org.netbeans.modules.cnd.modelutil.CsmImageLoader;
import org.netbeans.modules.editor.indent.api.Indent;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author Alexander Simon
 */
public class CsmOverrideMethodCompletionItem implements CompletionItem {

    private final int substitutionOffset;
    private final int priority;
    private final String sortItemText;
    private final boolean supportInstantSubst;
    private static final int PRIORITY = 15;
    private final String appendItemText;
    private final String htmlItemText;
    private final CsmMember item;
    private final ImageIcon icon;
    private final String right;

    private CsmOverrideMethodCompletionItem(CsmMember item, int substitutionOffset, int priority,
            String sortItemText, String appendItemText, String htmlItemText, boolean supportInstantSubst, String right) {
        this.substitutionOffset = substitutionOffset;
        this.priority = priority;
        this.supportInstantSubst = supportInstantSubst;
        this.sortItemText = sortItemText;
        this.appendItemText = appendItemText;
        this.htmlItemText = htmlItemText;
        this.item = item;
        icon = CsmImageLoader.getIcon(item);
        this.right = right;
    }

    public static CsmOverrideMethodCompletionItem createImplementItem(int substitutionOffset, int priority, CsmClass cls, CsmMember item) {
        String sortItemText;
        if (CsmKindUtilities.isDestructor(item)) {
            sortItemText = "~"+cls.getName(); //NOI18N
        } else {
            sortItemText = item.getName().toString();
        }
        String appendItemText = createAppendText(item, cls);
        String rightText = createRightName(item);
        String coloredItemText = createDisplayName(item, cls, "override"); //NOI18N
        return new CsmOverrideMethodCompletionItem(item, substitutionOffset, PRIORITY, sortItemText, appendItemText, coloredItemText, true, rightText);
    }

    private static String createDisplayName(CsmMember item, CsmClass parent, String operation) {
        StringBuilder displayName = new StringBuilder();
        displayName.append("<b>"); //NOI18N
        displayName.append(((CsmFunction)item).getSignature());
        displayName.append("</b>"); //NOI18N
        if (operation != null) {
            displayName.append(" - "); //NOI18N
            displayName.append(operation);
        }
        if (CsmKindUtilities.isDestructor(item)) {
            String s = displayName.toString();
            int i = s.indexOf('('); //NOI18N
            if (i > 0) {
                return "~"+parent.getName()+s.substring(i); //NOI18N
            }
        }
        return displayName.toString();
    }
    
    private static String createRightName(CsmMember item) {
        if (CsmKindUtilities.isConstructor(item)) {
            return "";
        } else if (CsmKindUtilities.isDestructor(item)) {
            return "";
        } else {
            return ((CsmFunction)item).getReturnType().getCanonicalText().toString();
        }
    }
    
    private static String createAppendText(CsmMember item, CsmClass parent) {
        StringBuilder appendItemText = new StringBuilder("virtual "); //NOI18N
        String type = "";
        if (!CsmKindUtilities.isConstructor(item) && !CsmKindUtilities.isDestructor(item)) {
            final CsmType returnType = ((CsmFunction)item).getReturnType();
            type = returnType.getCanonicalText().toString()+" "; //NOI18N
            if (type.indexOf("::") < 0) { //NOI18N
                CsmClassifier classifier = returnType.getClassifier();
                if (classifier != null) {
                    String toReplace = classifier.getName().toString();
                    if (type.indexOf(toReplace) == 0) {
                        CsmScope scope = classifier.getScope();
                        if (CsmKindUtilities.isClass(scope)) {
                            type = ((CsmClass)scope).getName()+"::"+type; //NOI18N
                        }
                    } else if (type.startsWith("const "+toReplace)) { //NOI18N
                        CsmScope scope = classifier.getScope();
                        if (CsmKindUtilities.isClass(scope)) {
                            type = "const "+((CsmClass)scope).getName()+"::"+type.substring(6); //NOI18N
                        }
                    }
                }
            }
        }
        appendItemText.append(type);
        addSignature(item, parent, appendItemText);
        appendItemText.append(";"); //NOI18N
        return appendItemText.toString();
    }
    
    private static void addSignature(CsmMember item, CsmClass parent, StringBuilder sb) {
        //sb.append(item.getSignature());
        if (CsmKindUtilities.isDestructor(item)) {
            sb.append('~');
            sb.append(parent.getName());
        } else {
            sb.append(item.getName());
        }
        if (CsmKindUtilities.isTemplate(item)) {
            List<CsmTemplateParameter> templateParameters = ((CsmTemplate)item).getTemplateParameters();
            // What to do with template?
        }
        //sb.append(parameterList.getText());
        sb.append('('); //NOI18N
        boolean first = true;
        for(CsmParameter param : ((CsmFunction)item).getParameterList().getParameters()) {
            if (!first) {
               sb.append(','); //NOI18N
               sb.append(' '); //NOI18N
            }
            first = false;
            if (param.isVarArgs()) {
                sb.append(param.getName());
                sb.append(' '); //NOI18N
                sb.append("..."); // NOI18N
            } else {
                CsmType type = param.getType();
                if (type != null) {
                    sb.append(type.getCanonicalText());
                    sb.append(' ');
                    sb.append(param.getName());
                }
            }
        }
        sb.append(')'); //NOI18N
        if(CsmKindUtilities.isMethod(item) && ((CsmMethod)item).isConst()) {
            sb.append(" const"); // NOI18N
        }
    }
    
    public String getItemText() {
        return appendItemText;
    }

    @Override
    public void defaultAction(JTextComponent component) {
        if (component != null) {
            Completion.get().hideDocumentation();
            Completion.get().hideCompletion();
            int caretOffset = component.getSelectionEnd();
            substituteText(component, substitutionOffset, caretOffset - substitutionOffset);
        }
    }

    @Override
    public void processKeyEvent(KeyEvent evt) {
        if (evt.getID() == KeyEvent.KEY_TYPED) {
            JTextComponent component = (JTextComponent) evt.getSource();
            int caretOffset = component.getSelectionEnd();
            final int len = caretOffset - substitutionOffset;
            if (len < 0) {
                Completion.get().hideDocumentation();
                Completion.get().hideCompletion();
            }
        }
    }

    @Override
    public boolean instantSubstitution(JTextComponent component) {
        if (supportInstantSubst) {
            defaultAction(component);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public CompletionTask createDocumentationTask() {
        CompletionDocumentationProvider p = Lookup.getDefault().lookup(CompletionDocumentationProvider.class);

        return p != null ? p.createDocumentationTask(this) : null;
    }

    @Override
    public CompletionTask createToolTipTask() {
        return null;
    }

    @Override
    public int getPreferredWidth(Graphics g, Font defaultFont) {
        return CompletionUtilities.getPreferredWidth(getLeftHtmlText(true), getRightHtmlText(true), g, defaultFont);
    }

    @Override
    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(getIcon(), getLeftHtmlText(true), getRightHtmlText(true), g, defaultFont, defaultColor, width, height, selected);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(this.getLeftHtmlText(false));
        out.append(this.getRightHtmlText(false)); 
        return out.toString();
    }

    @Override
    public int getSortPriority() {
        return this.priority;
    }

    @Override
    public CharSequence getSortText() {
        return sortItemText;
    }

    @Override
    public CharSequence getInsertPrefix() {
        return sortItemText;
    }

    protected ImageIcon getIcon() {
        return icon;
    }

    protected String getLeftHtmlText(boolean html) {
        return html ? htmlItemText : getItemText();
    }

    protected String getRightHtmlText(boolean html) {
        return right;
    }

    protected void substituteText(final JTextComponent c, final int offset, final int origLen) {
        final BaseDocument doc = (BaseDocument) c.getDocument();
        doc.runAtomicAsUser(new Runnable() {
            @Override
            public void run() {
                try {
                    if (origLen > 0) {
                        doc.remove(offset, origLen);
                    }
                    String itemText = getItemText();
                    doc.insertString(offset, itemText, null);
                    if (c != null) {
                        int setDot = offset + getInsertPrefix().length();
                        c.setCaretPosition(setDot);
                        if (appendItemText.length() > 0) {
                            Indent indent = Indent.get(doc);
                            indent.lock();
                            try {
                                indent.reindent(offset, offset + itemText.length());
                            } finally {
                                indent.unlock();
                            }
                        }
                    }
                } catch (BadLocationException e) {
                    // Can't update
                }
            }
        });
    }
}
