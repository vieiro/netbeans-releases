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

package org.openide.util.io;

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import org.openide.util.WeakSet;

// note: keep method NbObjectInputStream.resolveObject
// consistent with replaceObject method

/** Object output stream that could in the future be smart about saving certain objects.
* Also static methods to safely write an object that could cause problems during later deserialization.
*/
public class NbObjectOutputStream extends ObjectOutputStream {
    private static final String SVUID = "serialVersionUID"; // NOI18N
    private static final Set<String> alreadyReported = new WeakSet<String>();

    static {
        // See below.
        alreadyReported.add("java.lang.Exception"); // NOI18N
        alreadyReported.add("java.io.IOException"); // NOI18N
        alreadyReported.add("java.util.TreeSet"); // NOI18N
        alreadyReported.add("java.awt.geom.AffineTransform"); // NOI18N
    }

    private static Map<String,Boolean> examinedClasses = new WeakHashMap<String,Boolean>(250);
    private final List<Class> serializing = new ArrayList<Class>(50);

    /** Create a new object output.
    * @param os the underlying output stream
    * @throws IOException for the usual reasons
    */
    public NbObjectOutputStream(OutputStream os) throws IOException {
        super(os);

        try {
            enableReplaceObject(true);
        } catch (SecurityException ex) {
            throw (IOException) new IOException(ex.toString()).initCause(ex);
        }
    }

    /*
    * @param obj is an Object to be checked for replace
    */
    public Object replaceObject(Object obj) throws IOException {
        if (obj instanceof Image) {
            return null;

            // [LIGHT]
            // additional code needed for full version
        }

        return super.replaceObject(obj);
    }

    /** Writes an object safely to the object output.
     * Can be read by {@link NbObjectInputStream#readSafely}.
    * @param oo object output to write to
    * @param obj the object to write
    * @exception SafeException if the object simply fails to be serialized
    * @exception IOException if something more serious fails
    */
    public static void writeSafely(ObjectOutput oo, Object obj)
    throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(200);

        try {
            NbObjectOutputStream oos = new NbObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bos.close();
        } catch (Exception exc) {
            // exception during safe of the object
            // encapsulate all exceptions into safe exception
            oo.writeInt(0);
            throw new SafeException(exc);
        }

        oo.writeInt(bos.size());
        oo.write(bos.toByteArray());
    }

    protected void annotateClass(Class cl) throws IOException {
        super.annotateClass(cl);

        if (cl.isArray()) {
            return;
        }

        if (cl.isInterface()) {
            // TheInterface.class is being serialized, not an instance;
            // no need for svuid.
            return;
        }

        serializing.add(cl);

        if (isSerialVersionUIDDeclared(cl)) {
            return;
        }

        if (IOException.class.isAssignableFrom(cl)) {
            // ObjectOutputStream for some reason stores IOException's that are
            // thrown during serialization (they are rethrown later maybe?).
            // It's no problem, just ignore them here.
            return;
        }

        String classname = cl.getName();

        if (alreadyReported.add(classname)) {
            Set<Class> serializingUniq = new HashSet<Class>();
            StringBuffer b = new StringBuffer("Serializable class "); // NOI18N
            b.append(classname);
            b.append(" does not declare serialVersionUID field. Encountered while storing: ["); // NOI18N

            Iterator it = serializing.iterator();
            boolean first = true;

            while (it.hasNext()) {
                Class c = (Class) it.next();

                if ((c != cl) && serializingUniq.add(c)) {
                    if (first) {
                        first = false;
                    } else {
                        b.append(", "); // NOI18N
                    }

                    b.append(c.getName());
                }
            }

            b.append("] See also http://www.netbeans.org/issues/show_bug.cgi?id=19915"); // NOI18N

            String file = System.getProperty("InstanceDataObject.current.file"); // NOI18N

            if ((file != null) && (file.length() > 0)) {
                b.append(" [may have been writing "); // NOI18N
                b.append(file);
                b.append("]"); // NOI18N
            }

            Logger.getLogger(NbObjectOutputStream.class.getName()).warning(b.toString());
        }
    }
    
    /* Package private for testing */
    static boolean isSerialVersionUIDDeclared(Class clazz) {
        String classname = clazz.getName();
        Boolean okay = examinedClasses.get(classname);
        
        if (okay == null) {
            if (classname.equals("java.util.HashSet") || classname.equals("java.util.ArrayList") || clazz.isEnum()) { // NOI18N
                okay = Boolean.TRUE;
            } else {
                okay = Boolean.FALSE;

                java.lang.reflect.Field[] flds = clazz.getDeclaredFields();

                for (int i = 0; i < flds.length; i++) {
                    if (flds[i].getName().equals(SVUID)) {
                        okay = Boolean.TRUE;

                        break;
                    }
                }
            }

            examinedClasses.put(clazz.getName(), okay);
        }

        return okay.booleanValue();
    }
}
