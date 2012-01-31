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
package org.netbeans.modules.openide.loaders;

import java.awt.GraphicsEnvironment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.openide.loaders.data.DoFPDataObject;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.test.AnnotationProcessorTestUtils;

/**
 *
 * @author Eric Barboni <skygo@netbeans.org>
 */
public class DataObjectFactoryProcessorTest extends NbTestCase {
// XXX inner class for DataObject fail
//

    static {
        System.setProperty("java.awt.headless", "true");
    }

    public DataObjectFactoryProcessorTest(String n) {
        super(n);
    }

    @Override
    protected boolean runInEQ() {
        return true;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createMIMEs();
    }

// Several test for javac
    public void testConstructorWrongType() throws IOException {
        clearWorkDir();
        assertTrue("Headless run", GraphicsEnvironment.isHeadless());
        AnnotationProcessorTestUtils.makeSource(getWorkDir(), "test.A",
                "import org.openide.loaders.DataObject;\n"
                + "@DataObject.Registration(mimeType=\"text/testa\")"
                + "public class A {\n"
                + "    A() {}"
                + "}\n");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        boolean r = AnnotationProcessorTestUtils.runJavac(getWorkDir(), null, getWorkDir(), null, os);
        assertFalse("Compilation has to fail:\n" + os, r);
    }

    public void testWrongAmountofParameterInConstructorTypeDataObject() throws IOException {
        {
            clearWorkDir();
            assertTrue("Headless run", GraphicsEnvironment.isHeadless());
            AnnotationProcessorTestUtils.makeSource(getWorkDir(), "test.B",
                    DUMMYCLASSIMPORTPART
                    + "@DataObject.Registration(mimeType = \"text/testb\")"
                    + DUMMYCLASSDEFPART
                    + "public B(int a,int b, int c){}"
                    + DUMMYCLASSOVERRIDEPART);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            boolean r = AnnotationProcessorTestUtils.runJavac(getWorkDir(), null, getWorkDir(), null, os);
            assertFalse("Compilation has to fail:\n" + os, r);
        }
        {
            clearWorkDir();
            assertTrue("Headless run", GraphicsEnvironment.isHeadless());
            AnnotationProcessorTestUtils.makeSource(getWorkDir(), "test.B",
                    DUMMYCLASSIMPORTPART
                    + "@DataObject.Registration(mimeType = \"text/testb\")"
                    + DUMMYCLASSDEFPART
                    + "public B(){}"
                    + DUMMYCLASSOVERRIDEPART);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            boolean r = AnnotationProcessorTestUtils.runJavac(getWorkDir(), null, getWorkDir(), null, os);
            assertFalse("Compilation has to fail:\n" + os, r);
        }
    }

    public void testConstructorTypeDataObject() throws IOException {
        {
            clearWorkDir();
            assertTrue("Headless run", GraphicsEnvironment.isHeadless());
            AnnotationProcessorTestUtils.makeSource(getWorkDir(), "test.B",
                    DUMMYCLASSIMPORTPART
                    + "@DataObject.Registration(mimeType = \"text/testb\")"
                    + DUMMYCLASSDEFPART
                    + "public B(FileObject fo,DataLoader dol)throws DataObjectExistsException {        super(fo,dol);    }"
                    + DUMMYCLASSOVERRIDEPART);



            ByteArrayOutputStream os = new ByteArrayOutputStream();
            boolean r = AnnotationProcessorTestUtils.runJavac(getWorkDir(), null, getWorkDir(), null, os);
            assertFalse("Compilation has to fail:\n" + os, r);
        }
        {
            clearWorkDir();
            assertTrue("Headless run", GraphicsEnvironment.isHeadless());
            AnnotationProcessorTestUtils.makeSource(getWorkDir(), "test.B",
                    DUMMYCLASSIMPORTPART
                    + "@DataObject.Registration(mimeType = \"text/testb\")"
                    + DUMMYCLASSDEFPART
                    + "public B(FileObject fo,MultiFileLoader dol)throws DataObjectExistsException {        super(fo,dol);    }"
                    + DUMMYCLASSOVERRIDEPART);



            ByteArrayOutputStream os = new ByteArrayOutputStream();
            boolean r = AnnotationProcessorTestUtils.runJavac(getWorkDir(), null, getWorkDir(), null, os);
            assertTrue("Compilation has to succed:\n" + os, r);
        }
    }

    public void testConstructorTypeDataObjectFactory() throws IOException {
        {
            clearWorkDir();
            assertTrue("Headless run", GraphicsEnvironment.isHeadless());
            AnnotationProcessorTestUtils.makeSource(getWorkDir(), "test.B",
                    DUMMYFACTORYCLASSIMPORTPART
                    + "@DataObject.Registration(mimeType = \"text/testb\")"
                    + DUMMYFACTORYCLASSDEFPART
                    + "public B() { super( \"test\"); }"
                    + DUMMYFACTORYCLASSOVERRIDEPART);



            ByteArrayOutputStream os = new ByteArrayOutputStream();
            boolean r = AnnotationProcessorTestUtils.runJavac(getWorkDir(), null, getWorkDir(), null, os);
            assertTrue("Compilation has to succed:\n" + os, r);
        }
        {
            clearWorkDir();
            assertTrue("Headless run", GraphicsEnvironment.isHeadless());
            AnnotationProcessorTestUtils.makeSource(getWorkDir(), "test.B",
                    DUMMYFACTORYCLASSIMPORTPART
                    + "@DataObject.Registration(mimeType = \"text/testb\")"
                    + DUMMYFACTORYCLASSDEFPART
                    + "protected B() { super( \"test\"); }"
                    + DUMMYFACTORYCLASSOVERRIDEPART);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            boolean r = AnnotationProcessorTestUtils.runJavac(getWorkDir(), null, getWorkDir(), null, os);
            assertFalse("Compilation has to fail:\n" + os, r);
        }
    }

    // use external DoFP* class and check if registration is good
    public void testSeveralMimeType() throws IOException {
        clearWorkDir();
        {
            FileObject fo = FileUtil.getConfigFile(
                    "Loaders/text/test1/Factories/" + DoFPDataObject.class.getName().replace(".", "-") + ".instance");
            assertNotNull("File found", fo);

            assertEquals("Position Ok", 3565, fo.getAttribute("position"));
            assertEquals("Label Ok", "labeltest", fo.getAttribute("displayName"));
            assertEquals("MimeOk", "text/test1", fo.getAttribute("mimeType"));
            Object icon = fo.getAttribute("iconBase");
            assertEquals("Icon found", "org/openide/loaders/unknow.gif", icon);
            assertEquals("DataObjectClass found", DoFPDataObject.class.getName(), fo.getAttribute("dataObjectClass"));

        }

    }

// use external DoFP* class and their registration to test if dataobject return is good
    public void testDataLoad() throws Exception {
        // be sure mime are correct
        {
            FileObject fo = createXmlFile("sdfsdf", ".tt1");
            assertEquals("text/test1", fo.getMIMEType());
            DataObject find = DataObject.find(fo);
            assertEquals("DataLoader type", DoFPDataObject.class, find.getClass());
        }
        {
            FileObject fo = createXmlFile("sdfsdf", ".tt2");
            assertEquals("text/test2", fo.getMIMEType());
            DataObject find = DataObject.find(fo);
            assertEquals("DataLoader type", DoFPDataObject.class, find.getClass());
        }
        {
            FileObject fo = createXmlFile("sdfsdf", ".tt3");
            assertEquals("text/test3", fo.getMIMEType());
            // XXX DoFPCustomLoader not loaded cannot assert for loader
        }
    }

    // utility method inspired by FsMimeResolverTest
    private void createMIMEs() throws Exception {
// create  resolver to get tt1 extension resolve as test/test1 and do so for 3 different
        FileObject resolver = FileUtil.createData(FileUtil.getConfigRoot(), "Services/MIMEResolver/resolver.xml");
        OutputStream os = resolver.getOutputStream();
        PrintStream ps = new PrintStream(os);
        ps.println("<!DOCTYPE MIME-resolver PUBLIC '-//NetBeans//DTD MIME Resolver 1.0//EN' 'http://www.netbeans.org/dtds/mime-resolver-1_0.dtd'>");
        ps.println("<MIME-resolver>");
        for (int i = 1; i < 4; i++) {
            ps.println(" <file>");
            ps.println("  <ext name=\"tt" + i + "\"/>");
            ps.println("    <resolver mime=\"text/test" + i + "\"/>");
            ps.println(" </file>");
        }
        ps.println("</MIME-resolver>");
        os.close();
    }

    private FileObject createXmlFile(String content, String ext) throws Exception {
        FileObject file = FileUtil.createMemoryFileSystem().getRoot().createData("file" + ext);
        FileLock lock = file.lock();
        try {
            OutputStream out = file.getOutputStream(lock);
            try {
                out.write(content.getBytes());
            } finally {
                out.close();
            }
        } finally {
            lock.releaseLock();
        }
        return file;
    }
    //---------------------- class text part DataObject
    String DUMMYCLASSIMPORTPART = "import java.io.IOException;"
            + "import org.openide.filesystems.FileObject;"
            + "import org.openide.loaders.DataFolder;"
            + "import org.openide.loaders.DataObject;"
            + "import org.openide.loaders.MultiFileLoader;"
            + "import org.openide.loaders.DataObjectExistsException;"
            + "import org.openide.loaders.DataLoader;"
            + "import org.openide.util.HelpCtx;";
    String DUMMYCLASSDEFPART = "public class B extends DataObject {";
    String OVERRIDE = "@Override\n";
    String DUMMYRUNTIMEEXCEPTION = " throw new RuntimeException(\"Not implemented yet.\");";
    String DUMMYCLASSOVERRIDEPART =
            ""
            + OVERRIDE + "public boolean isDeleteAllowed() {" + DUMMYRUNTIMEEXCEPTION + "}"
            + OVERRIDE + "public boolean isCopyAllowed() {" + DUMMYRUNTIMEEXCEPTION + "}"
            + OVERRIDE + "public boolean isMoveAllowed() {" + DUMMYRUNTIMEEXCEPTION + "}"
            + OVERRIDE + "public boolean isRenameAllowed() {" + DUMMYRUNTIMEEXCEPTION + "}"
            + OVERRIDE + "public HelpCtx getHelpCtx() {" + DUMMYRUNTIMEEXCEPTION + "}"
            + OVERRIDE + "protected DataObject handleCopy(DataFolder f) throws IOException {" + DUMMYRUNTIMEEXCEPTION + "}"
            + OVERRIDE + "protected void handleDelete() throws IOException {" + DUMMYRUNTIMEEXCEPTION + "}"
            + OVERRIDE + "protected FileObject handleRename(String name) throws IOException {" + DUMMYRUNTIMEEXCEPTION + "}"
            + OVERRIDE + "protected FileObject handleMove(DataFolder df) throws IOException {" + DUMMYRUNTIMEEXCEPTION + "}"
            + OVERRIDE + "protected DataObject handleCreateFromTemplate(DataFolder df, String name) throws IOException {" + DUMMYRUNTIMEEXCEPTION + "}"
            + "}\n";
    // ---------------------- class text part DataObject.Factory
    String DUMMYFACTORYCLASSIMPORTPART = "   import java.io.IOException;"
            + "import org.openide.filesystems.FileObject;"
            + "import org.openide.loaders.DataObject;"
            + "import org.openide.loaders.MultiDataObject;"
            + "import org.openide.loaders.DataObjectExistsException;"
            + "import org.openide.loaders.UniFileLoader;";
//@DataObject.Registration(mimeType = "text/test3", position = 300)
    String DUMMYFACTORYCLASSDEFPART = "public class B extends UniFileLoader {";

    /*
     * public DoFPCustomLoader() {
     * super("org.netbeans.modules.openide.loaders.DoFPDataObjectCustomLoader");
     * }
     */
    String DUMMYFACTORYCLASSOVERRIDEPART = OVERRIDE
            + "  protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {"
            + DUMMYRUNTIMEEXCEPTION
            + "   }"
            + "}";
}
