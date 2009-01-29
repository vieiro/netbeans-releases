/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.php.editor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.netbeans.modules.gsf.api.NameKind;
import org.netbeans.modules.gsf.api.annotations.CheckForNull;
import org.netbeans.modules.gsf.api.annotations.NonNull;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Radek Matous
 */
public class ModelUtils {

    private ModelUtils() {
    }

    @CheckForNull
    public static <T extends ModelElement> T getFirst(List<? extends T> all) {
        return all.size() > 0 ? all.get(0) : null;
    }

    @CheckForNull
    public static <T extends Occurence> T getFirst(List<? extends T> all) {
        return all.size() > 0 ? all.get(0) : null;
    }

    @CheckForNull
    public static <T extends ModelElement> T getLast(List<? extends T> all) {
        return all.size() > 0 ? all.get(all.size()-1) : null;
    }


    @NonNull
    public static <T extends ModelElement> List<? extends T> filter(List<T> instances,
            final String... queryName) {
        return filter(instances, NameKind.EXACT_NAME, queryName);
    }

    @NonNull
    public static <T extends ModelElement> List<? extends T> filter(List<T> instances,
            final NameKind nameKind, final String... queryName) {
        return filter(instances, new ElementFilter<T>() {
            public boolean isAccepted(T element) {
                return (queryName.length == 0 || nameKindMatch(element.getName(), nameKind, queryName));
            }
        });
    }

    @NonNull
    public static <T extends ModelElement> List<? extends T> filter(List<? extends T> instances, FileObject fileObject) {
        List<T> retval = new ArrayList<T>();
        for (T element : instances) {
            if (element.getFileObject() == fileObject) {
                retval.add(element);
            }
        }
        return retval;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public static <T extends ModelElement> List<? extends T> merge(List<? extends T>... all) {
        List<T> retval = new ArrayList<T>();
        for (List<? extends T> list : all) {
            retval.addAll(list);
        }
        return retval;
    }


    //TODO: put it directly to ModelElement
    @CheckForNull
    public static PhpFileScope getFileScope(ModelElement element) {
        PhpFileScope retval = (element instanceof PhpFileScope) ? (PhpFileScope)element : null;
        while (retval == null && element != null) {
            element = element.getInScope();
            retval = (PhpFileScope) ((element instanceof PhpFileScope) ? element : null);
        }
        return retval;
    }

    @NonNull
    public static IndexScope getIndexScope(ModelElement element) {
        IndexScope retval = (element instanceof IndexScope) ? (IndexScope)element : null;
        ModelElement tmpElement = element;
        while (retval == null && tmpElement != null) {
            tmpElement = tmpElement.getInScope();
            retval = (IndexScope) ((tmpElement instanceof IndexScope) ? tmpElement : null);
        }
        if (retval == null) {
            PhpFileScope fileScope = getFileScope(element);
            assert fileScope != null;
            retval = fileScope.getIndexScope();
        }
        return retval;
    }

    public static <T extends ModelElement> List<? extends T> filter(final List<T> instances, final ElementFilter<T> filter) {
        List<T> retval = new ArrayList<T>();
        for (T baseElement : instances) {
            boolean accepted = filter.isAccepted(baseElement);
            if (accepted) {
                retval.add(baseElement);
            }
        }
        return retval;
    }

    public static interface ElementFilter<T extends ModelElement> {
        boolean isAccepted(T element);
    }

    public static boolean nameKindMatch(String text, NameKind nameKind, String... queries) {
        return nameKindMatch(true, text, nameKind, queries);
    }

    private static boolean nameKindMatch(boolean forceCaseInsensitivity, String text, NameKind nameKind, String... queries) {
        for (String query : queries) {
            switch (nameKind) {
                case CAMEL_CASE:
                    if (toCamelCase(text).startsWith(query)) {
                        return true;
                    }
                    break;
                case CASE_INSENSITIVE_PREFIX:
                    if (text.toLowerCase().startsWith(query.toLowerCase())) {
                        return true;
                    }
                    break;
                case CASE_INSENSITIVE_REGEXP:
                    text = text.toLowerCase();
                case REGEXP:
                    //TODO: might be perf. problem if called for large collections
                    // and ever and ever again would be compiled still the same query
                    Pattern p = Pattern.compile(query);
                    if (nameKindMatch(p, text)) {
                        return true;
                    }
                    break;
                case EXACT_NAME:
                    boolean retval = (forceCaseInsensitivity) ? text.equalsIgnoreCase(query) : text.equals(query);
                    if (retval) {
                        return true;
                    }
                    break;
                case PREFIX:
                    if (text.startsWith(query)) {
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    private static String toCamelCase(String plainName) {
        char[] retval = new char[plainName.length()];
        int retvalSize = 0;
        for (int i = 0; i < retval.length; i++) {
            char c = plainName.charAt(i);
            if (Character.isUpperCase(c)) {
                retval[retvalSize] = c;
                retvalSize++;
            }
        }
        return String.valueOf(String.valueOf(retval, 0, retvalSize));
    }

    private static boolean nameKindMatch(Pattern p, String text) {
        return p.matcher(text).matches();
    }
}
