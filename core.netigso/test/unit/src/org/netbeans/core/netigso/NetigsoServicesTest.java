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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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

package org.netbeans.core.netigso;

import org.netbeans.core.startup.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import org.netbeans.Module;
import org.netbeans.ModuleManager;
import org.netbeans.NetigsoFramework;
import org.netbeans.SetupHid;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

/**
 * How does OSGi integration deals with layer registration?
 *
 * @author Jaroslav Tulach
 */
public class NetigsoServicesTest extends SetupHid implements LookupListener {
    private static Module m1;
    private static ModuleManager mgr;
    private int cnt;

    public NetigsoServicesTest(String name) {
        super(name);
    }

    protected @Override void setUp() throws Exception {
        Locale.setDefault(Locale.US);
        clearWorkDir();

        
        data = new File(getDataDir(), "jars");
        jars = new File(getWorkDir(), "jars");
        jars.mkdirs();
        File simpleModule = createTestJAR("simple-module", null);
        File dependsOnSimpleModule = createTestJAR("depends-on-simple-module", null, simpleModule);

        if (System.getProperty("netbeans.user") == null) {
            File ud = new File(getWorkDir(), "ud");
            ud.mkdirs();

            System.setProperty("netbeans.user", ud.getPath());


            ModuleSystem ms = Main.getModuleSystem();
            mgr = ms.getManager();
            mgr.mutexPrivileged().enterWriteAccess();
            try {
                File j1 = new File(jars, "simple-module.jar");
                m1 = mgr.create(j1, null, false, false, false);
                mgr.enable(Collections.<Module>singleton(m1));
            } finally {
                mgr.mutexPrivileged().exitWriteAccess();
            }
        }

    }
    private File createTestJAR(String name, String srcdir, File... classpath) throws IOException {
        return createTestJAR(data, jars, name, srcdir, classpath);
    }
    public void testOSGiServicesVisibleInLookup() throws Exception {
        mgr.mutexPrivileged().enterWriteAccess();
        FileObject fo;
        try {
            String mfBar = "Bundle-SymbolicName: org.bar\n" +
                "Bundle-Version: 1.1.0\n" +
                "Bundle-ManifestVersion: 2\n" +
                "Import-Package: org.foo\n" +
                "\n\n";

            File j2 = changeManifest(new File(jars, "depends-on-simple-module.jar"), mfBar);
            Module m2 = mgr.create(j2, null, false, false, false);
            mgr.enable(m2);
        } finally {
            mgr.mutexPrivileged().exitWriteAccess();
        }

        Bundle b = findBundle("org.bar");
        assertNotNull("Bundle really found", b);
        IOException s = new IOException();
        ServiceRegistration sr = b.getBundleContext().registerService(IOException.class.getName(), s, null);
        assertBundles("Nobody is using the service yet", 0, sr.getReference().getUsingBundles());
        IOException found = Lookup.getDefault().lookup(IOException.class);
        assertNotNull("Result really found", found);
        assertBundles("Someone is using the service now", 1, sr.getReference().getUsingBundles());
        Result<IOException> res = Lookup.getDefault().lookupResult(IOException.class);
        res.addLookupListener(this);
        assertEquals("One instance found", 1, res.allInstances().size());
        sr.unregister();
        IOException notFound = Lookup.getDefault().lookup(IOException.class);
        assertNull("Result not found", notFound);
        assertEquals("No instance found", 0, res.allInstances().size());
        assertEquals("One change", 1, cnt);
    }

    static void assertBundles(String msg, int len, Bundle[] bundles) {
        if (bundles == null && len == 0) {
            return;
        }
        if (len == bundles.length) {
            return;
        }
        fail(msg + " expected: " + len + " was: " + bundles.length + "\n" + Arrays.toString(bundles));
    }


    static Bundle findBundle(String cnb) throws Exception {
        Object o = Lookup.getDefault().lookup(NetigsoFramework.class);
        assertEquals("The right class", Netigso.class, o.getClass());
        Netigso f = (Netigso)o;
        Bundle[] arr = f.getFramework().getBundleContext().getBundles();
        for (Bundle b : arr) {
            if (cnb.equals(b.getSymbolicName())) {
                return b;
            }
        }
        return null;
    }

    private File changeManifest(File orig, String manifest) throws IOException {
        File f = new File(getWorkDir(), orig.getName());
        Manifest mf = new Manifest(new ByteArrayInputStream(manifest.getBytes("utf-8")));
        mf.getMainAttributes().putValue("Manifest-Version", "1.0");
        JarOutputStream os = new JarOutputStream(new FileOutputStream(f), mf);
        JarFile jf = new JarFile(orig);
        Enumeration<JarEntry> en = jf.entries();
        InputStream is;
        while (en.hasMoreElements()) {
            JarEntry e = en.nextElement();
            if (e.getName().equals("META-INF/MANIFEST.MF")) {
                continue;
            }
            os.putNextEntry(e);
            is = jf.getInputStream(e);
            FileUtil.copy(is, os);
            is.close();
            os.closeEntry();
        }
        os.close();

        return f;
    }

    public void resultChanged(LookupEvent ev) {
        cnt++;
    }
}
