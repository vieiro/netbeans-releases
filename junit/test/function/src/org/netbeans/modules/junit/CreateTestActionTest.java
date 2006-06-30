/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.junit;

import org.openide.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.loaders.*;
import org.openide.src.*;
import org.openide.filesystems.*;
import org.openide.cookies.*;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;
import junit.framework.*;
import org.netbeans.junit.*;

public class CreateTestActionTest extends NbTestCase {

    public CreateTestActionTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(CreateTestActionTest.class);
        
        return suite;
    }
    
    /** Test of getName method, of class org.netbeans.modules.junit.CreateTestAction. */
    public void testGetName() {
        System.out.println("testGetName");
        String name = TO.getName();
        assertTrue(null != name);
    }
    
    /** Test of getHelpCtx method, of class org.netbeans.modules.junit.CreateTestAction. */
    public void testGetHelpCtx() {
        System.out.println("testGetHelpCtx");
        HelpCtx hc = TO.getHelpCtx();
        assertTrue(null != hc);
    }
    
    /** Test of cookieClasses method, of class org.netbeans.modules.junit.CreateTestAction. */
    public void testCookieClasses() {
        System.out.println("testCookieClasses");
        Class[] c = TO.cookieClasses();
        assertTrue(null != c);
    }
    
    /** Test of iconResource method, of class org.netbeans.modules.junit.CreateTestAction. */
    public void testIconResource() {
        System.out.println("testIconResource");
        String icon = TO.iconResource();
        assertTrue(null != icon);
    }
    
    /** Test of mode method, of class org.netbeans.modules.junit.CreateTestAction. */
    public void testMode() {
        System.out.println("testMode");
        TO.mode();
    }
    
    /** Test of performAction method, of class org.netbeans.modules.junit.CreateTestAction. */
    public void testPerformAction() throws Exception {
        System.out.println("testPerformAction");
        
        // check if called from IDE
        if (null == System.getProperty("netbeans.home")) {
            fail("This tast can run within the IDE only.");
        }
        
        LocalFileSystem fsPass = new LocalFileSystem();
        LocalFileSystem fsTest = new LocalFileSystem();
        LocalFileSystem fsSrc = new LocalFileSystem();
        fsPass.setRootDirectory(new File(appendSlash(m_pathData) + "CreateTestAction/pass"));
        fsTest.setRootDirectory(new File(appendSlash(m_pathData) + "CreateTestAction/test"));
        fsSrc.setRootDirectory(new File(appendSlash(m_pathData) + "CreateTestAction/src"));
        TopManager.getDefault().getRepository().addFileSystem(fsPass);
        TopManager.getDefault().getRepository().addFileSystem(fsTest);
        TopManager.getDefault().getRepository().addFileSystem(fsSrc);

        JUnitSettings js = JUnitSettings.getDefault();
        setGenerateFlags(js, true);
        js.setFileSystem(fsTest.getSystemName());
        
        assertTrue("Can't clean up the test directory.", delete(fsTest.getRoot(), false));
        TO.performAction(new Node[] { DataObject.find(fsTest.getRoot()).getNodeDelegate() });
        
        assertDirectories(fsTest.getRootDirectory(), fsPass.getRootDirectory());
    }
    
    /* protected members */
    protected CreateTestAction TO = null;
    protected String    m_pathData = null;
    
    protected void setUp() {
        if (null == TO)
            TO = (CreateTestAction)CreateTestAction.findObject(CreateTestAction.class, true);

        if (null == m_pathData)
            m_pathData = System.getProperty("xdata");
    }

    protected void tearDown() {
    }

    // private members
    private String appendSlash(String path) {
        if (null == path)
            return new String();
        
        if (!path.endsWith("\\") && !path.endsWith("/"))
            return path + "\\";
        
        return path;
    }
    
    private final void assertDirectories(File test, File pass) {
        File    fKids[];
        
        // assert all childern
        fKids = test.listFiles();
        for(int i = 0; i < fKids.length; i++) {
            assertDirectories(fKids[i], new File(pass, fKids[i].getName()));
        }
        
        if (!test.isDirectory()) {
            assertFile(test, new File(pass, test.getName()), new File(System.getProperty("xresults")));
        }
        else {
            // find missings
            File fPassKids[] = pass.listFiles();
            for(int i = 0; i < fPassKids.length; i++) {
                if (!new File(test, fPassKids[i].getName()).exists())
                    fail("The file or directory '" + fPassKids[i].getAbsolutePath() + "' is missing.");
            }
        }
    }

    private final boolean delete(FileObject fo, boolean deleteCurrent) {
        FileObject  foKids[];
        FileLock    lock = null;
        
        // delete all childern
        foKids = fo.getChildren();
        for(int i = 0; i < foKids.length; i++) {
            if (!delete(foKids[i], true))
                return false;
        }
        
        if (!deleteCurrent)
            return true;
        
        try {
            lock = fo.lock();
            fo.delete(lock);
        }
        catch (IOException e) {
            return false;
        }
        finally {
            if (null != lock)
                lock.releaseLock();
        }
        return true;
    }
    
    private void setGenerateFlags(JUnitSettings js, boolean flag) {
        js.setMembersPublic(flag); assertTrue(flag == js.isMembersPublic());
        js.setMembersProtected(flag); assertTrue(flag == js.isMembersProtected());
        js.setMembersPackage(flag); assertTrue(flag == js.isMembersPackage());
        js.setBodyComments(flag); assertTrue(flag == js.isBodyComments());
        js.setBodyContent(flag); assertTrue(flag == js.isBodyContent());
        js.setJavaDoc(flag); assertTrue(flag == js.isJavaDoc());
    }
}
