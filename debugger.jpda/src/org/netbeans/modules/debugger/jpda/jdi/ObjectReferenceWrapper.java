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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2009 Sun
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

package org.netbeans.modules.debugger.jpda.jdi;

// DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
// Generated by org.netbeans.modules.debugger.jpda.jdi.Generate class located in 'gensrc' folder,
// perform the desired modifications there and re-generate by "ant generate".

/**
 * Wrapper for ObjectReference JDI class.
 * Use methods of this class instead of direct calls on JDI objects.
 * These methods assure that exceptions thrown from JDI calls are handled appropriately.
 *
 * @author Martin Entlicher
 */
public final class ObjectReferenceWrapper {

    private ObjectReferenceWrapper() {}

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static void disableCollection(com.sun.jdi.ObjectReference a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.UnsupportedOperationExceptionWrapper {
        try {
            a.disableCollection();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.UnsupportedOperationException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.UnsupportedOperationExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static void enableCollection(com.sun.jdi.ObjectReference a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.UnsupportedOperationExceptionWrapper {
        try {
            a.enableCollection();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.UnsupportedOperationException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.UnsupportedOperationExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static int entryCount0(com.sun.jdi.ObjectReference a) throws com.sun.jdi.IncompatibleThreadStateException {
        try {
            return a.entryCount();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return 0;
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return 0;
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            return 0;
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static int entryCount(com.sun.jdi.ObjectReference a) throws com.sun.jdi.IncompatibleThreadStateException, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper {
        try {
            return a.entryCount();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static boolean equals0(com.sun.jdi.ObjectReference a, java.lang.Object b) {
        try {
            return a.equals(b);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return false;
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return false;
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            return false;
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static boolean equals(com.sun.jdi.ObjectReference a, java.lang.Object b) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper {
        try {
            return a.equals(b);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static com.sun.jdi.Value getValue(com.sun.jdi.ObjectReference a, com.sun.jdi.Field b) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper {
        try {
            return a.getValue(b);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static java.util.Map<com.sun.jdi.Field, com.sun.jdi.Value> getValues0(com.sun.jdi.ObjectReference a, java.util.List<? extends com.sun.jdi.Field> b) {
        try {
            return a.getValues(b);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyMap();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyMap();
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            return java.util.Collections.emptyMap();
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static java.util.Map<com.sun.jdi.Field, com.sun.jdi.Value> getValues(com.sun.jdi.ObjectReference a, java.util.List<? extends com.sun.jdi.Field> b) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper {
        try {
            return a.getValues(b);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static int hashCode0(com.sun.jdi.ObjectReference a) {
        try {
            return a.hashCode();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return 0;
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return 0;
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            return 0;
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static int hashCode(com.sun.jdi.ObjectReference a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper {
        try {
            return a.hashCode();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static com.sun.jdi.Value invokeMethod(com.sun.jdi.ObjectReference a, com.sun.jdi.ThreadReference b, com.sun.jdi.Method c, java.util.List<? extends com.sun.jdi.Value> d, int e) throws com.sun.jdi.InvalidTypeException, com.sun.jdi.ClassNotLoadedException, com.sun.jdi.IncompatibleThreadStateException, com.sun.jdi.InvocationException, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper {
        try {
            try {
                return a.invokeMethod(b, c, d, e);
            } catch (com.sun.jdi.InternalException iex) {
                if (iex.errorCode() == 502) { // ALREADY_INVOKING
                    iex = (com.sun.jdi.InternalException) org.openide.util.Exceptions.attachLocalizedMessage(iex, org.openide.util.NbBundle.getMessage(org.netbeans.modules.debugger.jpda.JPDADebuggerImpl.class, "JDWPError502"));
                    org.openide.util.Exceptions.printStackTrace(iex);
                    return null;
                } else {
                    throw iex; // re-throw the original
                }
            }
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static boolean isCollected0(com.sun.jdi.ObjectReference a) {
        try {
            return a.isCollected();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return false;
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return false;
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            return false;
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static boolean isCollected(com.sun.jdi.ObjectReference a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper {
        try {
            return a.isCollected();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static com.sun.jdi.ThreadReference owningThread(com.sun.jdi.ObjectReference a) throws com.sun.jdi.IncompatibleThreadStateException, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper {
        try {
            return a.owningThread();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static com.sun.jdi.ReferenceType referenceType(com.sun.jdi.ObjectReference a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper {
        try {
            return a.referenceType();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    /** Wrapper for method referringObjects from JDK 1.6. */
    public static java.util.List<com.sun.jdi.ObjectReference> referringObjects(com.sun.jdi.ObjectReference a, long b) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper {
        try {
            return (java.util.List<com.sun.jdi.ObjectReference>) com.sun.jdi.ObjectReference.class.getMethod("referringObjects", long.class).invoke(a, b);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        } catch (SecurityException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            if (t instanceof com.sun.jdi.InternalException) {
                org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report((com.sun.jdi.InternalException) t);
                throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper((com.sun.jdi.InternalException) t);
            }
            if (t instanceof com.sun.jdi.VMDisconnectedException) {
                throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper((com.sun.jdi.VMDisconnectedException) t);
            }
            throw new IllegalStateException(t);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    /** Wrapper for method referringObjects from JDK 1.6. */
    public static java.util.List<com.sun.jdi.ObjectReference> referringObjects0(com.sun.jdi.ObjectReference a, long b) {
        try {
            return (java.util.List<com.sun.jdi.ObjectReference>) com.sun.jdi.ObjectReference.class.getMethod("referringObjects", long.class).invoke(a, b);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        } catch (SecurityException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            if (t instanceof com.sun.jdi.InternalException) {
                org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report((com.sun.jdi.InternalException) t);
                return java.util.Collections.emptyList();
            }
            if (t instanceof com.sun.jdi.VMDisconnectedException) {
                return java.util.Collections.emptyList();
            }
            throw new IllegalStateException(t);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static void setValue(com.sun.jdi.ObjectReference a, com.sun.jdi.Field b, com.sun.jdi.Value c) throws com.sun.jdi.InvalidTypeException, com.sun.jdi.ClassNotLoadedException, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.IllegalArgumentExceptionWrapper {
        try {
            a.setValue(b, c);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        } catch (java.lang.IllegalArgumentException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.IllegalArgumentExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static long uniqueID(com.sun.jdi.ObjectReference a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper {
        try {
            return a.uniqueID();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static java.util.List<com.sun.jdi.ThreadReference> waitingThreads0(com.sun.jdi.ObjectReference a) throws com.sun.jdi.IncompatibleThreadStateException {
        try {
            return a.waitingThreads();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            return java.util.Collections.emptyList();
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static java.util.List<com.sun.jdi.ThreadReference> waitingThreads(com.sun.jdi.ObjectReference a) throws com.sun.jdi.IncompatibleThreadStateException, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper {
        try {
            return a.waitingThreads();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.ObjectCollectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ObjectCollectedExceptionWrapper(ex);
        }
    }

}
