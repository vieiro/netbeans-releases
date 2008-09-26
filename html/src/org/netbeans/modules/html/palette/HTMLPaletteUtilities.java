/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.html.palette;
import java.awt.Component;
import java.awt.Container;
import java.util.StringTokenizer;
import javax.swing.JTree;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.editor.indent.api.Indent;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Formatter;
import org.netbeans.editor.TokenItem;
import org.netbeans.editor.ext.html.HTMLSyntaxSupport;
import org.netbeans.editor.ext.html.HTMLTokenContext;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;


/**
 *
 * @author Libor Kotouc
 */
public final class HTMLPaletteUtilities {
    
    public static int wrapTags(HTMLSyntaxSupport sup, int start, int end, BaseDocument doc) {
        
        try {
            TokenItem token = sup.getTokenChain(start, start + 1);
            
            if (token == null)
                return end;
            
            while (token.getOffset() < end) { // interested only in the tokens inside the body
                token = token.getNext();
                if (token.getTokenID() == HTMLTokenContext.TAG_OPEN_SYMBOL) { // it's '<' token
                    int offset = token.getOffset();
                    doc.insertString(offset, "\n", null);   // insert a new-line before '<'
                    end++;  // remember new body end
                    token = sup.getTokenChain(offset + 1, offset + 2); // create new token chain reflecting changed document
                }
            }
            
        } catch (IllegalStateException ise) {
        } catch (BadLocationException ble) {
        }
        
        return end;
    }

    public static SourceGroup[] getSourceGroups(FileObject fObj) {
    
        Project proj = FileOwnerQuery.getOwner(fObj);
        SourceGroup[] sg = new SourceGroup[] {};
        if (proj != null) {
            Sources sources = ProjectUtils.getSources(proj);
            sg = sources.getSourceGroups("doc_root");
//            if (sg.length == 0)
//                sg = sources.getSourceGroups(Sources.TYPE_GENERIC);
        }
        
        return sg;
    }

    public static JTree findTreeComponent(Component component) {
        if (component instanceof JTree) {
            return (JTree) component;
        }
        if (component instanceof Container) {
            Component[] components = ((Container) component).getComponents();
            for (int i = 0; i < components.length; i++) {
                JTree tree = findTreeComponent(components[i]);
                if (tree != null) {
                    return tree;
                }
            }
        }
        return null;
    }

    public static String getRelativePath(FileObject base, FileObject target) {
        
        final String DELIM = "/";
        final String PARENT = ".." + DELIM;
        
        String targetPath = target.getPath();
        String basePath = base.getPath();

        //paths begin either with '/' or with '<letter>:/' - ensure that in the latter case the <letter>s equal
        String baseDisc = basePath.substring(0, basePath.indexOf(DELIM));
        String targetDisc = targetPath.substring(0, targetPath.indexOf(DELIM));
        if (!baseDisc.equals(targetDisc))
            return ""; //different disc letters, thus returning an empty string to signalize this fact

        //cut a filename at the end taking last index for case of the same dir name as file name, really obscure but possible ;)
        basePath = basePath.substring(0, basePath.lastIndexOf(base.getNameExt()));
        targetPath = targetPath.substring(0, targetPath.lastIndexOf(target.getNameExt()));

        //iterate through prefix dirs until difference occurres
        StringTokenizer baseST = new StringTokenizer(basePath, DELIM);
        StringTokenizer targetST = new StringTokenizer(targetPath, DELIM);
        String baseDir = "";
        String targetDir = "";
        while (baseST.hasMoreTokens() && targetST.hasMoreTokens() && baseDir.equals(targetDir)) {
            baseDir = baseST.nextToken();
            targetDir = targetST.nextToken();
        }
        //create prefix consisting of parent dirs ("..")
        StringBuffer parentPrefix = new StringBuffer(!baseDir.equals(targetDir) ? PARENT : "");
        while (baseST.hasMoreTokens()) {
            parentPrefix.append(PARENT);
            baseST.nextToken();
        }
        //append remaining dirs with delimiter ("/")
        StringBuffer targetSB = new StringBuffer(!baseDir.equals(targetDir) ? targetDir + DELIM : "");
        while (targetST.hasMoreTokens())
            targetSB.append(targetST.nextToken() + DELIM);

        //resulting path
        targetPath = parentPrefix.toString() + targetSB.toString() + target.getNameExt();
        
        return targetPath;
    }

    public static void insert(String s, JTextComponent target) 
    throws BadLocationException 
    {
        insert(s, target, true);
    }
   
    public static void insert(final String s, final JTextComponent target, final boolean reformat)
            throws BadLocationException {
        final Document _doc = target.getDocument();
        if (_doc == null || !(_doc instanceof BaseDocument)) {
            return;
        }

        BaseDocument doc = (BaseDocument) _doc;
        final Reformat reformatter = Reformat.get(doc);
        reformatter.lock();
        try {
            doc.runAtomic(new Runnable() {

                public void run() {
                    try {
                        String s2 = s == null ? "" : s;
                        int start = insert(s2, target, _doc);
                        if (reformat && start >= 0 && _doc instanceof BaseDocument) {
                            // format the inserted text
                            int end = start + s2.length();
                            reformatter.reformat(start, end);
                        }
                    } catch (BadLocationException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            });

        } finally {
            reformatter.unlock();
        }

    }
    
    private static int insert(String s, JTextComponent target, Document doc) 
    throws BadLocationException 
    {

        int start = -1;
        try {
            //at first, find selected text range
            Caret caret = target.getCaret();
            int p0 = Math.min(caret.getDot(), caret.getMark());
            int p1 = Math.max(caret.getDot(), caret.getMark());
            doc.remove(p0, p1 - p0);
            
            //replace selected text by the inserted one
            start = caret.getDot();
            doc.insertString(start, s, null);
        }
        catch (BadLocationException ble) {}
        
        return start;
    }
    
}
