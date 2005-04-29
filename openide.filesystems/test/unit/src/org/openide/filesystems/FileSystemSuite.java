/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.openide.filesystems;
import junit.framework.*;
import org.netbeans.junit.*;

public class FileSystemSuite extends NbTestCase {
    
    public FileSystemSuite(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        NbTestSuite suite = new NbTestSuite("FileSystemTest");
        

/*
        suite.addTest(XMLFileSystemTest.suite());                                
        suite.addTest(LocalFileSystemTest.suite());
        suite.addTest(JarFileSystemTest.suite());                                
        //suite.addTest(MultiFileSystemTest.suite());                        
        suite.addTest(MultiFileSystem1Test.suite());                                
        //suite.addTest(MultiFileSystem2Test.suite());                                
        //suite.addTest(MultiFileSystem3Test.suite());                                
*/

        return suite;
    }
    
}
