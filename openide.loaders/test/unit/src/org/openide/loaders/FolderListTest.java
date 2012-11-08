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


package org.openide.loaders;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.*;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.*;
import org.openide.util.*;

/** Tests for internals of FolderList as there seems to be some
 * inherent problems.
 *
 * @author Jaroslav Tulach
 */
public class FolderListTest extends NbTestCase {
    private FileObject folder;
    private FolderList list;
    private FileObject a;
    private FileObject b;
    private FileObject c;
    
    
    public FolderListTest(String testName) {
        super(testName);
    }
    
    @Override
    protected Level logLevel() {
        return Level.FINE;
    }

    @Override
    protected void setUp() throws Exception {
        clearWorkDir();
        
        LocalFileSystem lfs = new LocalFileSystem();
        lfs.setRootDirectory(getWorkDir());

        folder = FileUtil.createFolder(lfs.getRoot(), "folder");

        a = FileUtil.createData(folder, "A.txt");
        b = FileUtil.createData(folder, "B.txt");
        c = FileUtil.createData(folder, "C.txt");
        
        list = FolderList.find(folder, true);
    }

    public void testComputeChildrenList() throws Exception {
        L listener = new L();       
        RequestProcessor.Task t = list.computeChildrenList(listener);
        t.waitFinished();
        
        assertEquals("Three files", 3, listener.cnt);
        assertTrue("finished", listener.finished);

        assertEquals("a", a, listener.cummulate.get(0).getPrimaryFile());
        assertEquals("c", b, listener.cummulate.get(1).getPrimaryFile());
        assertEquals("b", c, listener.cummulate.get(2).getPrimaryFile());
    }

    public void testComputeChildrenListOrder() throws Exception {
        list.computeChildrenList(new L()).waitFinished();
        
        class PCL implements PropertyChangeListener {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                list.assertNullComparator();
            }
        }
        PCL pcl = new PCL();
        
        list.addPropertyChangeListener(pcl);
        
        a.setAttribute("position", 300);
        b.setAttribute("position", 200);
        c.setAttribute("position", 100);

        L listener = new L();
        RequestProcessor.Task t = list.computeChildrenList(listener);
        t.waitFinished();

        assertEquals("Three files", 3, listener.cnt);
        assertTrue("finished", listener.finished);

        assertEquals("a", a, listener.cummulate.get(2).getPrimaryFile());
        assertEquals("c", b, listener.cummulate.get(1).getPrimaryFile());
        assertEquals("b", c, listener.cummulate.get(0).getPrimaryFile());
    }
    
    static class L implements FolderListListener {
        int cnt;
        boolean finished;
        List<DataObject> cummulate = new ArrayList<DataObject>();

        @Override
        public void finished(List<DataObject> arr) {
            assertTrue(arr.isEmpty());
            finished = true;
        }

        @Override
        public void process(DataObject obj, List<DataObject> arr) {
            cnt++;
            cummulate.add(obj);
        }
    }
}
