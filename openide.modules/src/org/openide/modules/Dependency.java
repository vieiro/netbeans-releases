/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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

package org.openide.modules;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.openide.util.Utilities;

/** A dependency a module can have. Since version 7.10 this class is
 * {@link Serializable}.
 * 
 * @author Jesse Glick
 * @since 1.24
 */
public final class Dependency implements Serializable {
    static final long serialVersionUID = 9548259318L;

    /** Dependency on another module. */
    public final static int TYPE_MODULE = 1;

    /** Dependency on a package. */
    public final static int TYPE_PACKAGE = 2;

    /** Dependency on Java. */
    public final static int TYPE_JAVA = 3;

    /**
     * Dependency on the IDE.
     * @deprecated This type of dependency should no longer be used.
     */
    @Deprecated
    public final static int TYPE_IDE = 4;

    /** Dependency on a token.
     * @see ModuleInfo#getProvides
     * @since 2.3
     */
    public final static int TYPE_REQUIRES = 5;

    /** Dependency on a token, but without need to have token provider be initialised sooner.
     * @see ModuleInfo#getProvides
     * @since 7.1
     */
    public final static int TYPE_NEEDS = 6;

    /** An advisory dependency on a token. If at least one provider of such token is 
     * available, it is enabled. If there is no such provider, then nothing is done
     * or reported.
     *
     * @see ModuleInfo#getProvides
     * @since 7.1
     */
    public final static int TYPE_RECOMMENDS = 7;

    /** Comparison by specification version. */
    public final static int COMPARE_SPEC = 1;

    /** Comparison by implementation version. */
    public final static int COMPARE_IMPL = 2;

    /** No comparison, just require the dependency to be present. */
    public final static int COMPARE_ANY = 3;

    /** @deprecated request dependencies on direct modules */
    @Deprecated
    public static final String IDE_NAME = System.getProperty("org.openide.major.version", "IDE"); // NOI18N

    /** @deprecated request dependencies on direct modules */
    @Deprecated
    public static final SpecificationVersion IDE_SPEC = makeSpec(
            System.getProperty("org.openide.specification.version")
        ); // NOI18N

    /** @deprecated request dependencies on direct modules */
    @Deprecated
    public static final String IDE_IMPL = System.getProperty("org.openide.version"); // NOI18N

    /** Name, for purposes of dependencies, of the Java platform. */
    public static final String JAVA_NAME = "Java"; // NOI18N

    /** Specification version of the Java platform. */
    public static final SpecificationVersion JAVA_SPEC = makeSpec(System.getProperty("java.specification.version")); // NOI18N

    /** Implementation version of the Java platform. */
    public static final String JAVA_IMPL = System.getProperty("java.version"); // NOI18N

    /** Name, for purposes of dependencies, of the Java VM. */
    public static final String VM_NAME = "VM"; // NOI18N

    /** Specification version of the Java VM. */
    public static final SpecificationVersion VM_SPEC = makeSpec(System.getProperty("java.vm.specification.version")); // NOI18N

    /** Implementation version of the Java VM. */
    public static final String VM_IMPL = System.getProperty("java.vm.version"); // NOI18N
    private final int type;
    private final int comparison;
    private final String name;
    private final String version;

    private Dependency(int type, String name, int comparison, String version) {
        this.type = type;
        this.name = name.intern();
        this.comparison = comparison;
        this.version = (version != null) ? version.intern() : null;
    }

    /** Verify the format of a code name.
     * Caller specifies whether a slash plus release version is permitted in this context.
     */
    private static void checkCodeName(String codeName, boolean slashOK)
    throws IllegalArgumentException {
        String base;
        int slash = codeName.indexOf('/'); // NOI18N

        if (slash == -1) {
            base = codeName;
        } else {
            if (!slashOK) {
                throw new IllegalArgumentException("No slash permitted in: " + codeName); // NOI18N
            }

            base = codeName.substring(0, slash);

            String rest = codeName.substring(slash + 1);
            int dash = rest.indexOf('-'); // NOI18N

            try {
                if (dash == -1) {
                    int release = Integer.parseInt(rest);

                    if (release < 0) {
                        throw new IllegalArgumentException("Negative release number: " + codeName); // NOI18N
                    }
                } else {
                    int release = Integer.parseInt(rest.substring(0, dash));
                    int releaseMax = Integer.parseInt(rest.substring(dash + 1));

                    if (release < 0) {
                        throw new IllegalArgumentException("Negative release number: " + codeName); // NOI18N
                    }

                    if (releaseMax <= release) {
                        throw new IllegalArgumentException("Release number range must be increasing: " + codeName); // NOI18N
                    }
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(e.toString());
            }
        }

        // Now check that the rest is a valid package.
        StringTokenizer tok = new StringTokenizer(base, ".", true); // NOI18N

        if ((tok.countTokens() % 2) == 0) {
            throw new NumberFormatException("Even number of pieces: " + base); // NOI18N
        }

        boolean expectingPath = true;

        while (tok.hasMoreTokens()) {
            if (expectingPath) {
                expectingPath = false;

                String nt = tok.nextToken();
                if (!Utilities.isJavaIdentifier(nt) &&  !"enum".equals (nt)) { // NOI18N
                    throw new IllegalArgumentException("Bad package component in " + base); // NOI18N
                }
            } else {
                if (!".".equals(tok.nextToken())) { // NOI18N
                    throw new NumberFormatException("Expected dot in code name: " + base); // NOI18N
                }

                expectingPath = true;
            }
        }
    }

    /** Parse dependencies from tags.
    * @param type like Dependency.type
    * @param body actual text of tag body; if <code>null</code>, returns nothing
    * @return a set of dependencies
    * @throws IllegalArgumentException if they are malformed or inconsistent
    */
    public static Set<Dependency> create(int type, String body) throws IllegalArgumentException {
        if (body == null) {
            return Collections.emptySet();
        }

        Set<Dependency> deps = new HashSet<Dependency>(5);

        // First split on commas.
        StringTokenizer tok = new StringTokenizer(body, ","); // NOI18N

        if (!tok.hasMoreTokens()) {
            throw new IllegalArgumentException("No deps given: \"" + body + "\""); // NOI18N
        }

        Map<DependencyKey, Dependency> depsByKey = new HashMap<DependencyKey, Dependency>(11);

        while (tok.hasMoreTokens()) {
            String onedep = tok.nextToken();
            StringTokenizer tok2 = new StringTokenizer(onedep, " \t\n\r"); // NOI18N

            if (!tok2.hasMoreTokens()) {
                throw new IllegalArgumentException("No name in dependency: " + onedep); // NOI18N
            }

            String name = tok2.nextToken();
            int comparison;
            String version;

            if (tok2.hasMoreTokens()) {
                String compthing = tok2.nextToken();

                if (compthing.equals(">")) { // NOI18N
                    comparison = Dependency.COMPARE_SPEC;
                } else if (compthing.equals("=")) { // NOI18N
                    comparison = Dependency.COMPARE_IMPL;
                } else {
                    throw new IllegalArgumentException("Strange comparison string: " + compthing); // NOI18N
                }

                if (!tok2.hasMoreTokens()) {
                    throw new IllegalArgumentException("Comparison string without version: " + onedep); // NOI18N
                }

                version = tok2.nextToken();

                if (tok2.hasMoreTokens()) {
                    throw new IllegalArgumentException("Trailing garbage in dependency: " + onedep); // NOI18N
                }

                if (comparison == Dependency.COMPARE_SPEC) {
                    try {
                        new SpecificationVersion(version);
                    } catch (NumberFormatException nfe) {
                        throw new IllegalArgumentException(nfe.toString());
                    }
                }
            } else {
                comparison = Dependency.COMPARE_ANY;
                version = null;
            }

            if (type == Dependency.TYPE_MODULE) {
                checkCodeName(name, true);

                if ((name.indexOf('-') != -1) && (comparison == Dependency.COMPARE_IMPL)) {
                    throw new IllegalArgumentException(
                        "Cannot have an implementation dependency on a ranged release version: " + onedep
                    ); // NOI18N
                }
            } else if (type == Dependency.TYPE_PACKAGE) {
                int idx = name.indexOf('[');

                if (idx != -1) {
                    if (idx > 0) {
                        checkCodeName(name.substring(0, idx), false);
                    }

                    if (name.charAt(name.length() - 1) != ']') {
                        throw new IllegalArgumentException("No close bracket on package dep: " + name); // NOI18N
                    }

                    checkCodeName(name.substring(idx + 1, name.length() - 1), false);
                } else {
                    checkCodeName(name, false);
                }

                if ((idx == 0) && (comparison != Dependency.COMPARE_ANY)) {
                    throw new IllegalArgumentException(
                        "Cannot use a version comparison on a package dependency when only a sample class is given"
                    ); // NOI18N
                }

                if ((idx > 0) && (name.substring(idx + 1, name.length() - 1).indexOf('.') != -1)) {
                    throw new IllegalArgumentException(
                        "Cannot have a sample class with dots when package is specified"
                    ); // NOI18N
                }
            } else if (type == Dependency.TYPE_JAVA) {
                if (!(name.equals(JAVA_NAME) || name.equals(VM_NAME))) { // NOI18N
                    throw new IllegalArgumentException("Java dependency must be on \"Java\" or \"VM\": " + name); // NOI18N
                }

                if (comparison == Dependency.COMPARE_ANY) {
                    throw new IllegalArgumentException("Must give a comparison for a Java dep: " + body); // NOI18N
                }
            } else if (type == Dependency.TYPE_IDE) {
                if (!(name.equals("IDE"))) { // NOI18N

                    int slash = name.indexOf("/"); // NOI18N
                    boolean ok;

                    if (slash == -1) {
                        ok = false;
                    } else {
                        if (!name.substring(0, slash).equals("IDE")) { // NOI18N
                            ok = false;
                        }

                        try {
                            int v = Integer.parseInt(name.substring(slash + 1));
                            ok = (v >= 0);
                        } catch (NumberFormatException e) {
                            ok = false;
                        }
                    }

                    if (!ok) {
                        throw new IllegalArgumentException("Invalid IDE dependency: " + name); // NOI18N
                    }
                }

                if (comparison == Dependency.COMPARE_ANY) {
                    throw new IllegalArgumentException("Must give a comparison for an IDE dep: " + body); // NOI18N
                }
            } else if (type == Dependency.TYPE_REQUIRES) {
                if (comparison != Dependency.COMPARE_ANY) {
                    throw new IllegalArgumentException("Cannot give a comparison for a token requires dep: " + body); // NOI18N
                }

                checkCodeName(name, false);
            } else if (type == Dependency.TYPE_NEEDS) {
                if (comparison != Dependency.COMPARE_ANY) {
                    throw new IllegalArgumentException("Cannot give a comparison for a token needs dep: " + body); // NOI18N
                }

                checkCodeName(name, false);
            } else if (type == Dependency.TYPE_RECOMMENDS) {
                if (comparison != Dependency.COMPARE_ANY) {
                    throw new IllegalArgumentException("Cannot give a comparison for a token needs dep: " + body); // NOI18N
                }

                checkCodeName(name, false);
            } else {
                throw new IllegalArgumentException("unknown type"); // NOI18N
            }

            Dependency nue = new Dependency(type, name, comparison, version);
            DependencyKey key = new DependencyKey(nue);

            if (depsByKey.containsKey(key)) {
                throw new IllegalArgumentException(
                    "Dependency " + nue + " duplicates the similar dependency " + depsByKey.get(key)
                ); // NOI18N
            } else {
                deps.add(nue);
                depsByKey.put(key, nue);
            }
        }

        return deps;
    }

    /** Get the type. */
    public final int getType() {
        return type;
    }

    /** Get the name of the depended-on object. */
    public final String getName() {
        return name;
    }

    /** Get the comparison type. */
    public final int getComparison() {
        return comparison;
    }

    /** Get the version to compare against (or null). */
    public final String getVersion() {
        return version;
    }

    /** Overridden to compare contents. */
    @Override
    public boolean equals(Object o) {
        if (o.getClass() != Dependency.class) {
            return false;
        }

        Dependency d = (Dependency) o;

        return (type == d.type) && (comparison == d.comparison) && name.equals(d.name) &&
        Utilities.compareObjects(version, d.version);
    }

    /** Overridden to hash by contents. */
    @Override
    public int hashCode() {
        return 772067 ^ type ^ name.hashCode();
    }

    /** Unspecified string representation for debugging. */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer(100);

        if (type == TYPE_MODULE) {
            buf.append("module "); // NOI18N
        } else if (type == TYPE_PACKAGE) {
            buf.append("package "); // NOI18N
        } else if (type == TYPE_REQUIRES) {
            buf.append("requires "); // NOI18N
        } else if (type == TYPE_NEEDS) {
            buf.append("needs "); // NOI18N
        } else if (type == TYPE_RECOMMENDS) {
            buf.append("recommends "); // NOI18N
        }

        buf.append(name);

        if (comparison == COMPARE_IMPL) {
            buf.append(" = "); // NOI18N
            buf.append(version);
        } else if (comparison == COMPARE_SPEC) {
            buf.append(" > "); // NOI18N
            buf.append(version);
        }

        return buf.toString();
    }

    /** Try to make a specification version from a string.
     * Deal with errors gracefully and try to recover something from it.
     * E.g. "1.4.0beta" is technically erroneous; correct to "1.4.0".
     */
    private static SpecificationVersion makeSpec(String vers) {
        if (vers != null) {
            try {
                return new SpecificationVersion(vers);
            } catch (NumberFormatException nfe) {
                System.err.println("WARNING: invalid specification version: " + vers); // NOI18N
            }

            do {
                vers = vers.substring(0, vers.length() - 1);

                try {
                    return new SpecificationVersion(vers);
                } catch (NumberFormatException nfe) {
                    // ignore
                }
            } while (vers.length() > 0);
        }

        // Nothing decent in it at all; use zero.
        return new SpecificationVersion("0"); // NOI18N
    }

    /** Key for checking for duplicates among dependencies.
     * The unique characteristics of a dependency are:
     * 1. The basic name. No release versions, no sample classes for packages
     * (though if you specify only the class and not the package, this is different).
     * 2. The type of dependency (module, package, etc.).
     * Sample things which ought not be duplicated:
     * 1. Sample classes within a package.
     * 2. The same module with different release versions (use ranged releases as needed).
     * 3. Impl & spec comparisons (the impl comparison is stricter anyway).
     * 4. Different versions of the same thing (makes no sense).
     */
    private static final class DependencyKey {
        private final int type;
        private final String name;

        public DependencyKey(Dependency d) {
            type = d.getType();

            switch (type) {
            case TYPE_MODULE:
            case TYPE_IDE:

                String codeName = d.getName();
                int idx = codeName.lastIndexOf('/');

                if (idx == -1) {
                    name = codeName;
                } else {
                    name = codeName.substring(0, idx);
                }

                break;

            case TYPE_PACKAGE:

                String pkgName = d.getName();
                idx = pkgName.indexOf('[');

                if (idx != -1) {
                    if (idx == 0) {
                        // [org.apache.jasper.Constants]
                        // Keep the [] only to differentiate it from a package name:
                        name = pkgName;
                    } else {
                        // org.apache.jasper[Constants]
                        name = pkgName.substring(0, idx);
                    }
                } else {
                    // org.apache.jasper
                    name = pkgName;
                }

                break;

            default:

                // TYPE_REQUIRES, TYPE_JAVA
                name = d.getName();

                break;
            }

            //System.err.println("Key for " + d + " is " + this);
        }

        public int hashCode() {
            return name.hashCode();
        }

        public boolean equals(Object o) {
            return (o instanceof DependencyKey) && ((DependencyKey) o).name.equals(name) &&
            (((DependencyKey) o).type == type);
        }

        public String toString() {
            return "DependencyKey[" + name + "," + type + "]"; // NOI18N
        }
    }
}
