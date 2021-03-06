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

package org.openide.text;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.text.StyledDocument;
import org.netbeans.junit.NbTestCase;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataLoader;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.FileEntry;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.loaders.UniFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.test.MockLookup;

/**
 * Test that fileChange event from FileObject.refresh in DES.Env.getTime is ignored.
 */
public class PropertyChangeTest extends NbTestCase {
    
    FileSystem fs;
    static {
        System.setProperty("org.openide.windows.DummyWindowManager.VISIBLE", "false");
    }

    private int counter = 0;

    public PropertyChangeTest(String s) {
        super(s);
    }
    
    @Override
    protected void setUp() throws Exception {
        clearWorkDir();
        MockLookup.setInstances(new Pool());
        fs = org.openide.filesystems.FileUtil.createMemoryFileSystem ();
    }

    /**
     * Test that fileChange event from FileObject.refresh in DES.Env.getTime is ignored
     * ie. no property change event from DES.Env is fired.
     */
    public void testRename () throws IOException {
        counter = 0;
        FileObject folder = FileUtil.toFileObject(getWorkDir());
        FileObject fo = FileUtil.createData(folder,"someFile.obj");
        
        DataObject obj = DataObject.find(fo);
        assertEquals( MyDataObject.class, obj.getClass());
        assertTrue( "we need UniFileLoader", obj.getLoader() instanceof UniFileLoader );

        EditorCookie ec = obj.getCookie(EditorCookie.class);

        DataEditorSupport des = (DataEditorSupport) ec;
        DataEditorSupport.Env env = (DataEditorSupport.Env) des.desEnv();

        File file = FileUtil.toFile(fo);
        //Set lastModified time to future so openDocument -> getTime -> FileObject.refresh
        //will generate fileChange event. fileChange event must be ignored in DES.Env to avoid
        //firing propertyChange event.
        file.setLastModified(file.lastModified() + 1000000L);
        
        env.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                counter++;
            }
        });
        
        StyledDocument doc = ec.openDocument();

        assertEquals("Counter must be zero", 0, counter);
    }
    
    private static class MyDataEditorSupport extends DataEditorSupport implements OpenCookie, CloseCookie, EditorCookie {
        public MyDataEditorSupport( DataObject obj, CloneableEditorSupport.Env env ) {
            super( obj, env );
        }
    }
    
    
    /** MyEnv that uses DataEditorSupport.Env */
    private static final class MyEnv extends DataEditorSupport.Env {
        static final long serialVersionUID = 1L;
        
        public MyEnv (DataObject obj) {
            super (obj);
        }
        
        protected FileObject getFile () {
            return super.getDataObject ().getPrimaryFile ();
        }

        protected FileLock takeLock () throws IOException {
            return super.getDataObject ().getPrimaryFile ().lock ();
        }
        
    }
    
    private static final class Pool extends org.openide.loaders.DataLoaderPool {
        protected java.util.Enumeration<? extends DataLoader> loaders() {
            return org.openide.util.Enumerations.array(DataLoader.getLoader(MyLoader.class), 
                    DataLoader.getLoader(MyMultiFileLoader.class));
        }
    }
    
    public static final class MyLoader extends UniFileLoader {
        
        public MyLoader() {
            super(MyDataObject.class.getName ());
            getExtensions ().addExtension ("obj");
            getExtensions ().addExtension ("newExt");
        }
        protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
            return new MyDataObject(this, primaryFile);
        }
    }
    
    public static final class MyDataObject extends MultiDataObject
    implements CookieSet.Factory {
        public MyDataObject(MyLoader l, FileObject folder) throws DataObjectExistsException {
            super(folder, l);
            getCookieSet ().add (new Class[] { OpenCookie.class, CloseCookie.class, EditorCookie.class }, this);
        }

        public <T extends Node.Cookie> T createCookie(Class<T> klass) {
            if (klass.isAssignableFrom(MyDataEditorSupport.class)) {
                return klass.cast(new MyDataEditorSupport(this, new MyEnv(this)));
            } else {
                return null;
            }
        }

    }

    private static class MyMultiFileLoader extends MultiFileLoader {
        public MyMultiFileLoader () {
            super(MyMultiFileDataObject.class.getName());
        }
        
        protected MultiDataObject createMultiObject (FileObject primaryFile) throws DataObjectExistsException, IOException {
            return new MyMultiFileDataObject( primaryFile, this );
        }
    
        protected FileObject findPrimaryFile(FileObject fo) {
            if (!fo.isFolder()) {
                // here is the common code for the worse behaviour
                if (fo.hasExt("prima")) {
                    return FileUtil.findBrother(fo, "seconda") != null ? fo : null;
                }
                
                if (fo.hasExt("seconda")) {
                    return FileUtil.findBrother(fo, "prima");
                }
            }
            return null;
        }

        protected MultiDataObject.Entry createPrimaryEntry(MultiDataObject obj, FileObject primaryFile) {
            return new FileEntry (obj, primaryFile);
        }

        protected MultiDataObject.Entry createSecondaryEntry(MultiDataObject obj, FileObject secondaryFile) {
            return new FileEntry (obj, secondaryFile);
        }
    } // end of MyDL3

    private static class MyMultiFileDataObject extends MultiDataObject {
        public MyMultiFileDataObject( FileObject primaryFile, MultiFileLoader loader ) throws DataObjectExistsException {
            super( primaryFile, loader );
        }
    }
}
