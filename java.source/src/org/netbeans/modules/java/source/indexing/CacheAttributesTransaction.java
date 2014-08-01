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
package org.netbeans.modules.java.source.indexing;

import java.io.IOException;
import java.net.URL;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.java.source.usages.ClassIndexImpl;
import org.netbeans.modules.java.source.usages.ClassIndexManager;
import org.openide.util.Parameters;

/**
 *
 * @author Tomas Zezula
 */
class CacheAttributesTransaction extends TransactionContext.Service {

    private final URL root;
    private final boolean srcRoot;
    private final boolean allFiles;
    private boolean closed;
    private boolean invalid;

    private CacheAttributesTransaction(
        @NonNull final URL root,
        final boolean srcRoot,
        final boolean allFiles) {
        this.root = root;
        this.srcRoot = srcRoot;
        this.allFiles = allFiles;
    }    

    static CacheAttributesTransaction create(
            @NonNull final URL root,
            final boolean srcRoot,
            final boolean allFiles) {
        Parameters.notNull("root", root);   //NOI18N
        return new CacheAttributesTransaction(root, srcRoot, allFiles);
    }

    @Override
    protected void commit() throws IOException {
        closeTx();
        final ClassIndexImpl uq = ClassIndexManager.getDefault().getUsagesQuery(root, false);
        if (uq == null) {
            //Closing
            return;
        }
        if (srcRoot) {
            if (uq.getState() == ClassIndexImpl.State.NEW && uq.getType() != ClassIndexImpl.Type.SOURCE) {
                JavaIndex.setAttribute(root, ClassIndexManager.PROP_SOURCE_ROOT, Boolean.TRUE.toString());
            }
        } else {            
            if (allFiles) {
                JavaIndex.setAttribute(root, ClassIndexManager.PROP_SOURCE_ROOT, Boolean.FALSE.toString());
            }
        }
        if (!invalid) {
            uq.setState(ClassIndexImpl.State.INITIALIZED);
        }
    }

    @Override
    protected void rollBack() throws IOException {
        closeTx();
        //NOP - keep index state as NEW
    }

    void setInvalid(final boolean invalid) {
        this.invalid = invalid;
    }

    private void closeTx() {
        if (closed) {
            throw new IllegalStateException("Already commited or rolled back transaction.");    //NOI18N
        }
        closed = true;
    }
}
