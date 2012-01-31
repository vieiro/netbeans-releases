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
package org.netbeans.modules.javascript2.editor.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.javascript2.editor.model.JsElement;
import org.netbeans.modules.javascript2.editor.model.JsObject;
import org.netbeans.modules.javascript2.editor.model.impl.JsElementImpl;
import org.netbeans.modules.javascript2.editor.model.impl.ModelUtils;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.netbeans.modules.parsing.spi.indexing.support.IndexDocument;
import org.netbeans.modules.parsing.spi.indexing.support.IndexResult;
import org.netbeans.modules.parsing.spi.indexing.support.IndexingSupport;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Petr Pisl
 */
public class IndexedElement extends JsElementImpl {
    
    private final JsElement.Kind jsKind;
    
    public IndexedElement(FileObject fileObject, String name, JsElement.Kind kind, OffsetRange offsetRange, Set<Modifier> modifiers) {
        super(fileObject, name, offsetRange, modifiers);
        this.jsKind = kind;
    }

    
    @Override
    public Kind getJSKind() {
        return jsKind;
    }
    
    public static IndexDocument createDocument(JsObject object, IndexingSupport support, Indexable indexable) {
        IndexDocument elementDocument = support.createDocument(indexable);
        elementDocument.addPair(JsIndex.FIELD_BASE_NAME, object.getName(), true, true);
        elementDocument.addPair(JsIndex.FIELD_FQ_NAME,  ModelUtils.createFQN(object), true, true);
        elementDocument.addPair(JsIndex.FIELD_JS_KIND, Integer.toString(object.getJSKind().getId()), true, true);
        elementDocument.addPair(JsIndex.FIELD_IS_GLOBAL, (ModelUtils.isGlobal(object.getParent()) ? "1" : "0"), true, true);
        elementDocument.addPair(JsIndex.FIELD_OFFSET, Integer.toString(object.getOffset()), true, true);            
        for (JsObject property : object.getProperties().values()) {
            elementDocument.addPair(JsIndex.FIELD_PROPERTY, property.getName(), false, true);
        }
        return elementDocument;
    }
    
    public static IndexedElement create(IndexResult indexResult) {
        FileObject fo = indexResult.getFile();
        String name = indexResult.getValue(JsIndex.FIELD_BASE_NAME);
        JsElement.Kind kind = JsElement.Kind.fromId(Integer.parseInt(indexResult.getValue(JsIndex.FIELD_JS_KIND)));
        int offset = Integer.parseInt(indexResult.getValue(JsIndex.FIELD_OFFSET));
        return new IndexedElement(fo, name, kind, new OffsetRange(offset, offset + name.length()), EnumSet.of(Modifier.PUBLIC));
    }
    
    public static Collection<IndexedElement> createProperties(IndexResult indexResult) {
        Collection<IndexedElement> result = new ArrayList<IndexedElement>();
        FileObject fo = indexResult.getFile();
        for(String sProperty : indexResult.getValues(JsIndex.FIELD_PROPERTY)) {
            result.add(new IndexedElement(fo, sProperty, JsElement.Kind.PROPERTY, OffsetRange.NONE, EnumSet.of(Modifier.PUBLIC)));
        }
        return result;
    }
}
