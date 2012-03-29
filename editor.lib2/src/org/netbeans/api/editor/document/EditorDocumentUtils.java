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
package org.netbeans.api.editor.document;

import javax.swing.text.Document;
import org.netbeans.modules.editor.lib2.document.EditorDocumentHandler;

/**
 * Utilities operation on top of an editor document.
 *
 * @author Miloslav Metelka
 * @since 1.58
 */
public final class EditorDocumentUtils {
    
    private EditorDocumentUtils() {
        // No instances
    }
    
    /**
     * Execute a non mutating runnable under an exclusive document lock over the document
     * (no other read locks or write locks are taking place).
     * <br/>
     * Nested calls to {@link #runExclusive(Document, Runnable) } are allowed.
     * The given runnable may also call {@link Document#render(Runnable) }.
     * <br/>
     * However any mutations by {@link Document#insertString(int, String, javax.swing.text.AttributeSet) }
     * or {@link Document#remove(int, int) } are prohibited.
     * <br/>
     * Calling atomic transactions (BaseDocument.runAtomic() or NbDocument.runAtomic())
     * from the given runnable is prohibited as well.
     * <br/>
     * Calls to {@link #runExclusive(Document, Runnable) } within an atomic section
     * are allowed (but no document mutations may be done during runExclusive() call).
     * <br/>
     * Calls to {@link Document#render(java.lang.Runnable) } within
     * {@link #runExclusive(Document, Runnable) } are allowed.
     * <br/>
     * Calls to {@link #runExclusive(Document, Runnable) } within
     * {@link Document#render(java.lang.Runnable) } are prohibited and may lead to starvation.
     * 
     * @param doc document being exclusively locked. For non-editor document implementations
     * (currently <code>org.netbeans.editor.BaseDocument</code>) the implementation
     * synchronizes over the document and does not check for mutations within runExclusive().
     * @param r runnable to be performed. It is not allowed to mutate the document by any insertions or removals.
     * @throws IllegalStateException in case the given runnable wants to mutate the document.
     *
     * @since 1.58
     */
    public static void runExclusive(Document doc, Runnable r) {
        EditorDocumentHandler.runExclusive(doc, r);
    }
    
    
}
