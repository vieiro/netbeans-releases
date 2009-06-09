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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.apt.impl.support;

import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.cnd.apt.support.APTMacro;
import org.netbeans.modules.cnd.utils.cache.WeakSharedSet;


/**
 * APT macro table manager
 * Responsibility:
 *  - only one instance per macro object
 *  - based on weak references to allow GC of unused macros
 *
 * @author Vladimir Voskresensky
 */
public abstract class APTMacroCache  {
    public enum CacheKind {
        Single,
        Sliced
    }

    private APTMacroCache() {
    }

    public abstract APTMacro getMacro(APTMacro macro);
    public abstract void dispose();

    private static final int MACRO_MANAGER_DEFAULT_CAPACITY=1024;
    private static final int MACRO_MANAGER_DEFAULT_SLICED_NUMBER = 29;
    private static final APTMacroCache instance = create(false);

    private static APTMacroCache create(boolean single) {
        if (single) {
            return new APTSingleMacroManager(MACRO_MANAGER_DEFAULT_CAPACITY);
        } else {
            return new APTCompoundMacroManager(MACRO_MANAGER_DEFAULT_SLICED_NUMBER, MACRO_MANAGER_DEFAULT_CAPACITY);
        }
    }

    public static APTMacroCache getManager() {
        return instance;
    }
    
    private static final class APTSingleMacroManager extends APTMacroCache {
        private final WeakSharedSet<APTMacro> storage;
        private final int initialCapacity;

        /** Creates a new instance of APTMacroCache */
        private APTSingleMacroManager(int initialCapacity) {
            storage = new WeakSharedSet<APTMacro>(initialCapacity);
            this.initialCapacity = initialCapacity;
        }

        // we need exclusive copy of string => use "new String(String)" constructor
        private final String lock = new String("lock in APTMacroCache"); // NOI18N

        /**
         * returns shared string instance equal to input text.
         *
         * @param test - interested shared string
         * @return the shared instance of text
         * @exception NullPointerException If the <code>text</code> parameter
         *                                 is <code>null</code>.
         */
        public APTMacro getMacro(APTMacro macro) {
            if (macro == null) {
                throw new NullPointerException("null string is illegal to share"); // NOI18N
            }
            APTMacro outMacro = null;

            synchronized (lock) {
                outMacro = storage.addOrGet(macro);
            }
            assert (outMacro != null);
            assert (outMacro.equals(macro));
            return outMacro;
        }

        public final void dispose() {
            if (false){
                System.out.println("Dispose cache "+getClass().getName()); // NOI18N
                Object[] arr = storage.toArray();
                Map<Class, Integer> classes = new HashMap<Class,Integer>();
                for(Object o : arr){
                    if (o != null) {
                        Integer i = classes.get(o.getClass());
                        if (i != null) {
                            i = new Integer(i.intValue() + 1);
                        } else {
                            i = new Integer(1);
                        }
                        classes.put(o.getClass(), i);
                    }
                }
                for(Map.Entry<Class,Integer> e:classes.entrySet()){
                    System.out.println("   "+e.getValue()+" of "+e.getKey().getName()); // NOI18N
                }
            }
            if (storage.size() > 0) {
                storage.clear();
                storage.resize(initialCapacity);
            }
        }
    }

    private static final class APTCompoundMacroManager extends APTMacroCache {
        private final APTMacroCache[] instances;
        private final int sliceNumber; // primary number for better distribution
        private APTCompoundMacroManager(int sliceNumber) {
            this(sliceNumber, APTMacroCache.MACRO_MANAGER_DEFAULT_CAPACITY);
        }
        private APTCompoundMacroManager(int sliceNumber, int initialCapacity) {
            this.sliceNumber = sliceNumber;
            instances = new APTMacroCache[sliceNumber];
            for (int i = 0; i < instances.length; i++) {
                instances[i] = new APTSingleMacroManager(initialCapacity);
            }
        }

        private APTMacroCache getDelegate(APTMacro macro) {
            if (macro == null) {
                throw new NullPointerException("null macro is illegal to share"); // NOI18N
            }
            int index = macro.hashCode() % sliceNumber;
            if (index < 0) {
                index += sliceNumber;
            }
            return instances[index];
        }

        public APTMacro getMacro(APTMacro macro) {
            return getDelegate(macro).getMacro(macro);
        }

        public final void dispose() {
            for (int i = 0; i < instances.length; i++) {
                instances[i].dispose();
            }
        }
    }
}
