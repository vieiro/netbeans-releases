/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

package org.netbeans.modules.editor.settings;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Weak set with open addressing that allows efficient getOrAdd().
 * Non-null objects only; load factor fixed to 0.5f; not thread-safe.
 * Other set-like ops can be added if desired.
 *
 * @author Miloslav Metelka
 */
final class SimpleWeakSet<E> {

    /**
     * Length of the table table MUST always be a power of two.
     */
    private Item<E>[] table;

    private int size;

    private final ReferenceQueue<E> queue = new ReferenceQueue<E>();

    public SimpleWeakSet() {
        table = newArray(16); // Capacity MUST be power of two
    }

    public int size() {
        if (size == 0) {
            return 0;
        }
        expungeStaleEntries();
        return size;
    }

    public boolean contains(E e) {
        int hashCode = e.hashCode();
        int i = hashCode & (table.length - 1);
        expungeStaleEntries();
        while (true) {
            Item<E> item = table[i];
            if (item == null) {
                return false;
            }
            E e2 = item.get();
            if (e2 != null && e2.hashCode() == hashCode && e.equals(e2)) {
                return true;
            }
            i = nextIndex(i, table.length);
        }
    }

    /**
     * Get existing equal element from the set if it already exists.
     * <br/>
     * If it does not exist then either put the given element or build a new element
     * if element provider is non-null.
     *
     * @param element non-null element to search for (an equal item).
     * @param eProvider if non-null call it to provide a new element. If null
     *  then the given element parameter will be used for addition.
     * @return either existing element already in the set or the element that was added
     *  (either element parameter or created element from element provider).
     */
    public E getOrAdd(E e, ElementProvider<E> eProvider) {
        int hashCode = e.hashCode();
        int i = hashCode & (table.length - 1);
        expungeStaleEntries();
        while (true) {
            Item<E> item = table[i];
            if (item == null) { // Empty slot - attempt to create value
                if (eProvider != null) {
                    e = eProvider.createElement();
                    if (e == null) {
                        return null;
                    }
                }
                table[i] = new Item<E>(e, queue, hashCode);
                size++;
                if (size >= (table.length >> 1)) {
                    resize();
                }
                return e;
            }
            E e2 = item.get();
            if (e2 != null && e2.hashCode() == hashCode && e.equals(e2)) {
                return e2;
            }
            i = nextIndex(i, table.length);
        }
    }

    private void resize() {
        int newLength = table.length << 1;
        Item<E>[] newTable = newArray(newLength);
        for (int i = table.length - 1; i >= 0; i--) {
            Item<E> item = table[i];
            if (item != null) {
                table[i] = null;
                int ni = item.hashCode & (newLength - 1);
                while (newTable[ni] != null) {
                    ni = nextIndex(ni, newLength);
                }
                newTable[ni] = item;
            }
        }
        table = newTable;
    }

    public E remove(E e) {
        int hashCode = e.hashCode();
        int i = hashCode & (table.length - 1);
        expungeStaleEntries();
        while (true) {
            Item<E> item = table[i];
            if (item == null) { // End of search
                return null;
            }
            E e2 = item.get();
            if (e2 != null && e2.hashCode() == hashCode && e.equals(e2)) {
                item.clear(); // Explicit remove so do not enqueue
                clearAtIndex(i);
                return e2;
            }
            i = nextIndex(i, table.length);
        }
    }

    public void clear() {
        while (queue.poll() != null) { } // Will clear so ignore stale entries
        for (int i = table.length; i >= 0; i--) {
            Item<E> item = table[i];
            if (item != null) {
                item.clear(); // Explicit remove so do not enqueue
            }
            table[i] = null;
        }
        size = 0;
    }

    public List<E> asList() { // Mainly for testing purposes
        expungeStaleEntries();
        List<E> l = new ArrayList<E>(size()); // size() does expunge()
        // Order the items like in the table i.e. object with zero hashcode will be first
        for (Item<E> item : table) {
            E e;
            if (item != null && (e = item.get()) != null) {
                l.add(e);
            }
        }
        return l;
    }

    private void clearAtIndex(int r) {
        table[r] = null;
        size--;

        // Close the removal.
        // The index where item was removed 'r' (it now contains null) will cause stopping
        // of a possible search for a value. Entries that were not placed
        // right at their natural hash-related index (because it was alread occupied
        // by another item) at time of their insertion may be affected by emptied 'r'
        // since search for them would stop at 'r' (finding a null item means end of search).
        // So correction needs to be made and such entries need to be moved
        // to the emptied index 'r'. And their emptied index needs then to follow
        // the same algorithm until originally-empty index is found.
        int i = nextIndex(r, table.length);
        Item<E> candidate;
        while ((candidate = table[i]) != null) {
            int c = candidate.hashCode & (table.length - 1); // Candidate's hash
            // The situation that does not need to be corrected is when
            // c lies cyclically in [r,i] interval:
            // |   r.c.i | (here r <= i)
            // |..i  r.c.| or  |.c..i  r..| (here r > i)
            // So exactly a reverse situation requires correction:
            if (!((r <= i) ? ((r < c) && c <= i) : ((r < c) || (c <= i)))) {
                // Perform a move of the candidate into the empty slot but continue the algorithm
                // with just emptied slot until an originally empty slot is found.
                table[r] = candidate;
                table[i] = null;
                r = i;
            }
            i = nextIndex(i, table.length); // Next index after candidate
        }
    }

    private void expungeStaleEntries() {
        Item<?> item; // ? instead of E to easily avoid unchecked cast
        while ((item = (Item<?>) queue.poll()) != null) {
            int h = item.hashCode;
            int i = h & (table.length - 1);
            while (true) {
                Item<E> item2 = table[i];
                if (item == item2) {
                    clearAtIndex(i);
                    break;
                }
                if (item2 == null) {
                    // Not in table (e.g. clear() was done; but since we do ref.clear()
                    // then this probably should never happen.
                    break; // Skip handling of item
                }
                i = nextIndex(i, table.length);
            }
        }
    }

    Item<E>[] newArray(int size) {
        @SuppressWarnings("unchecked")
        Item<E>[] array = (Item<E>[]) new Item<?>[size];
        return array;
    }

    private static int nextIndex(int i, int length) {
        return (++i < length ? i : 0);
    }

    @Override
    public String toString() {
        return asList().toString();
    }

    interface ElementProvider<V> {

        /**
         * Build object's instance.
         * @return object's instance or null.
         */
        V createElement();
    }

    private static final class Item<E> extends WeakReference<E> {

        final int hashCode;

        Item(E e, ReferenceQueue<E> queue, int hashCode) {
            super(e, queue);
            this.hashCode = hashCode;
        }

        @Override
        public String toString() {
            return "Item@" + System.identityHashCode(this) + ";e:" + get(); // NOI18N
        }

    }
}
