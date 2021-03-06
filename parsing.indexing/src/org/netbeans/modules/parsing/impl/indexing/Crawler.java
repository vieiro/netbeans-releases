/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.parsing.impl.indexing;

import org.netbeans.modules.parsing.spi.indexing.SuspendStatus;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Tomas Zezula
 */
abstract class Crawler {
    
    enum TimeStampAction {
        CHECK,
        UPDATE
    }

    /**
     *
     * @param root
     * @param checkTimeStamps
     * @param detectDeletedFiles 
     * @param supportsAllFiles if true all files are collected
     * otherwise the {@link Crawler#getAllResources()} returns null
     *   Can be <code>null</code> in which case all mime types will be checked.
     *
     * @throws java.io.IOException
     */
    protected Crawler(
            @NonNull final URL root,
            final Set<? extends TimeStampAction> checkTimeStamps,
            final boolean detectDeletedFiles,
            final boolean supportsAllFiles,            
            @NonNull final CancelRequest cancelRequest,
            @NonNull final SuspendStatus suspendStatus) throws IOException {
        this.root = root;
        this.checkTimeStamps = checkTimeStamps;
        this.timeStamps = checkTimeStamps.contains(TimeStampAction.UPDATE) ?
                TimeStamps.forRoot(root, detectDeletedFiles) :
                TimeStamps.changedTransient();
        this.supportsAllFiles = supportsAllFiles;
        this.cancelRequest = cancelRequest;
        this.suspendStatus = suspendStatus;
    }

    public static boolean listenOnVisibility() {
        return listenOnVisibility;
    }

    public final @NonNull List<Indexable> getResources() throws IOException {
        init ();        
        return resources;
    }

    /**
     * Returns all resources within the root or null when all resources are hard to be
     * computed. The resources are hard to be computed when the crawler was created with
     * list of files (folders) which are inside the root. In case when Crawler does not
     * do the time stamp check the resources == allResources.
     * @return all resources within the root null
     * @throws IOException 
     */
    public final @CheckForNull List<Indexable> getAllResources() throws IOException {
        init ();        
        return checkTimeStamps.contains(TimeStampAction.CHECK) || !supportsAllFiles ? allResources : resources;
    }

    public final @NonNull List<Indexable> getDeletedResources () throws IOException {
        init ();        
        return deleted;
    }

    public final void storeTimestamps() throws IOException {
        init();
        timeStamps.store();
    }
    
    public final boolean hasChanged() throws IOException {
        init();
        return this.changed;
    }

    public final boolean isFinished() throws IOException {
        init();
        return finished;
    }

    protected final boolean isUpToDate(@NonNull FileObject f, @NullAllowed String relativePath) {
        // always call this in order to update the file's timestamp
        boolean upToDate = timeStamps.checkAndStoreTimestamp(f, relativePath);
        return checkTimeStamps.contains(TimeStampAction.CHECK) ? upToDate : false;
    }

    protected final boolean isCancelled() {
        return cancelRequest.isRaised();
    }

    protected final void parkWhileSuspended() {
        try {
            suspendStatus.parkWhileSuspended();
        } catch (InterruptedException ex) {
            //pass - cancelled
        }
    }

    @NonNull
    protected final Indexable createIndexable(@NonNull final IndexableImpl impl) {
        return SPIAccessor.getInstance().create(impl);
    }

    /**
     * Used by subclasses and unit tests to simulate restart
     * @param value
     */
    static void setListenOnVisibility(final boolean value) {
        listenOnVisibility = value;
    }

    protected abstract boolean collectResources(@NonNull Collection<Indexable> resources, @NonNull Collection<Indexable> allResources);

    // -----------------------------------------------------------------------
    // private implementation
    // -----------------------------------------------------------------------

    private final URL root;
    private final Set<? extends TimeStampAction> checkTimeStamps;
    private final boolean supportsAllFiles;
    private final TimeStamps timeStamps;
    private final CancelRequest cancelRequest;
    private final SuspendStatus suspendStatus;

    private List<Indexable> resources;
    private List<Indexable> allResources;
    private List<Indexable> deleted;
    private boolean finished;
    private boolean changed;
    private boolean initialized;
    
    private static volatile boolean listenOnVisibility;

    private void init () throws IOException {
        if (!initialized) {
            try {
                List<Indexable> _resources = new ArrayList<Indexable>();
                List<Indexable> _allResources = checkTimeStamps.contains(TimeStampAction.CHECK) && supportsAllFiles ?
                        new ArrayList<Indexable>() : new NullList<Indexable>();
                this.finished = collectResources(_resources, _allResources);
                this.resources = Collections.unmodifiableList(_resources);
                this.allResources = checkTimeStamps.contains(TimeStampAction.CHECK) && supportsAllFiles ?
                    Collections.unmodifiableList(_allResources) :
                    null;
                changed = !_resources.isEmpty();

                final Set<String> unseen = timeStamps.getUnseenFiles();                
                if (unseen != null) {
                    List<Indexable> _deleted = new ArrayList<Indexable>(unseen.size());
                    for (String u : unseen) {
                        _deleted.add(createIndexable(new DeletedIndexable(root, u)));
                    }
                    deleted = Collections.unmodifiableList(_deleted);
                } else {
                    deleted = Collections.<Indexable>emptyList();
                }
                changed |= !deleted.isEmpty();
            } finally {
                initialized = true;
            }
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="Proxy collection">
    /**
     * Only add method is used.
     * @param <T>
     */
    private static class NullList<T> implements List<T> {
        
        private boolean changed;

        @Override
        public int size() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEmpty() {
            return !changed;
        }

        @Override
        public boolean contains(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<T> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(T e) {
            changed = true;
            return true;
        }

        @Override
        public void add(int index, T element) {
            changed = true;
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            changed = true;
            return true;
        }

        @Override
        public boolean addAll(int index, Collection<? extends T> c) {
            changed = true;
            return true;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }               

        @Override
        public T get(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public T set(int index, T element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public T remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int lastIndexOf(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ListIterator<T> listIterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ListIterator<T> listIterator(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<T> subList(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException();
        }
    }
    //</editor-fold>
}
