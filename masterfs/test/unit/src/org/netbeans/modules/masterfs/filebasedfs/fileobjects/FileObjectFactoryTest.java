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

package org.netbeans.modules.masterfs.filebasedfs.fileobjects;

import org.netbeans.modules.masterfs.*;
import java.io.File;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author rmatous
 */
public class FileObjectFactoryTest extends NbTestCase {
    private File testFile;
    private FileObject testFo;


    public FileObjectFactoryTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        testFile = new File(getWorkDir(),"testfile");//NOI18N
        if (!testFile.exists()) {
            assert testFile.createNewFile();
        }
        testFo = FileUtil.toFileObject(testFile);
        assert testFo != null;
        
    }
    /*
    public void testIssue64363() throws Exception {
        assertTrue(testFo.isValid());
        assertTrue(testFile.delete());
        testFo.getFileSystem().findResource(testFo.getPath());        
                
        FileObject testFo2 = Cache.getDefault().getValidOrInvalid(((MasterFileObject)testFo).getResource());
        if (!ProvidedExtensionsTest.ProvidedExtensionsImpl.isImplsDeleteRetVal()) {
            assertFalse(testFo2.isValid());
        }
        assertEquals(testFo, testFo2);        
    }
    
    public void testIssue61221() throws Exception {        
        assertTrue(testFo.isValid());
        FileObject par = testFo.getParent();
        testFo.delete();
        MasterFileObject testFo2 = (MasterFileObject)par.createData(testFo.getNameExt());
        assertNotSame(testFo2, testFo);
        Reference ref = new WeakReference(testFo);
        testFo = null;
        assertGC("",ref);
        MasterFileObject testFo3 = (MasterFileObject)par.getFileObject(testFo2.getNameExt());
        assertEquals(testFo3.isValid(), testFo2.isValid());
        assertSame(testFo3, testFo2);
    }

    public void testIssue61221_2() throws Exception {        
        assertTrue(testFo.isValid());
        FileObject par = testFo.getParent();
        testFo.delete();
        MasterFileObject testFo2 = (MasterFileObject)par.createData(testFo.getNameExt());
        assertNotSame(testFo2, testFo);
        Reference ref = new WeakReference(testFo);
        testFo = null;
        assertGC("",ref);
        assertNotNull(Cache.getDefault().get(testFo2.getResource()));
    }
    
*/    
}
