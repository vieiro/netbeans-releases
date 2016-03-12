/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

package org.netbeans.modules.remote.impl.fs;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.util.FileInfoProvider.StatInfo.FileType;
import org.openide.filesystems.FileLock;
import org.openide.util.NbBundle;
import org.openide.util.Parameters;

/**
 *
 * @author vk155633
 */
public class RemoteLinkChild extends RemoteLinkBase {

    private final RemoteFileObjectBase delegate;

    private static final boolean NO_CYCLIC_LINKS = RemoteFileSystemUtils.getBoolean("remote.block.cyclic.links", true);

    /*package*/ RemoteLinkChild(RemoteFileObject wrapper, RemoteFileSystem fileSystem, ExecutionEnvironment execEnv, RemoteLinkBase parent, String remotePath, RemoteFileObjectBase delegate) {
        super(wrapper, fileSystem, execEnv, parent, remotePath);
        Parameters.notNull("delegate", delegate);
        this.delegate = delegate;
    }

    @Override
    protected void init() {
        if (NO_CYCLIC_LINKS && hasCycle()) {
            setFlag(MASK_CYCLIC_LINK, true);
        }
    }

    private boolean hasCycle() {
        RemoteFileObjectBase dlg = getCanonicalDelegate();
        RemoteFileObjectBase p = getParent();
        while (p instanceof RemoteLinkChild) {
            if( ((RemoteLinkChild) p).delegate == dlg) {
                return true;
            }
            p = p.getParent();
        }
        return false;
    }

    @Override
    public RemoteFileObject[] getChildren() {
        if (getFlag(MASK_CYCLIC_LINK)) {
            return new RemoteFileObject[0];
        } else {
            return super.getChildren();
        }
    }

    @Override
    public RemoteFileObject getFileObject(String relativePath, Set<String> antiLoop) {
        if (getFlag(MASK_CYCLIC_LINK)) {
            return null;
        } else {
            return super.getFileObject(relativePath, antiLoop);
        }
    }

    @Override
    public RemoteFileObjectBase getCanonicalDelegate() {
        // We previously returned just a delegate.
        // In the case of links to links or cyclic links this leads to too deep delegation
        RemoteFileObjectBase d = delegate;
        while (d instanceof RemoteLinkChild) {
            d = ((RemoteLinkChild) d).delegate;
        }
        return d;
    }

    @Override
    protected RemoteFileObjectBase getDelegateImpl() {
        return delegate;
    }
        
    @Override
    protected String getDelegateNormalizedPath() {
        return delegate.getPath();
    }

    @Override
    public FileType getType() {
        return delegate.getType();
    }    
    
    @Override
    public boolean isValid() {
        if (isValidFastWithParents()) {
            return delegate.isValid();
        }
        return false;
    }
    
    @Override
    protected void postDeleteOrCreateChild(RemoteFileObject child, DirEntryList entryList) {
        RemoteFileObjectBase canonicalDelegate = getCanonicalDelegate();
        if (canonicalDelegate != null) {
            canonicalDelegate.postDeleteOrCreateChild(child, entryList);
        }
    }

    @Override
    protected DirEntryList deleteImpl(FileLock lock) throws IOException {
        RemoteFileObjectBase canonicalDelegate = getCanonicalDelegate();
        if (canonicalDelegate != null) {
            return canonicalDelegate.deleteImpl(lock);
        } else {
            throw new FileNotFoundException(getDisplayName() + " does not exist"); //NOI18Ns
        }
    }

    protected void renameImpl(FileLock lock, String name, String ext, RemoteFileObjectBase orig) throws IOException {
        // all work in delegate
        RemoteFileObjectBase dlg = getCanonicalDelegate();
        if (dlg != null) {
            dlg.renameImpl(lock, name, ext, orig);
        } else {
            throw RemoteExceptions.createIOException(NbBundle.getMessage(RemoteLinkChild.class,
                    "EXC_CanNotRenameFO", getDisplayName())); //NOI18N
        }
    }
}
