/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.junit;

import java.io.File;
import java.io.PrintStream;

/** Checks that we can do proper logging.
 *
 * @author Jaroslav Tulach
 */
public class LoggingTest extends NbTestCase {
    
    public LoggingTest(String testName) {
        super(testName);
    }
    
    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        // you can define order of test cases here
        suite.addTest(new LoggingTest("testLogFileName"));
        suite.addTest(new LoggingTest("testLogFileNameEqualsToNameOfTest"));
        suite.addTest(new LoggingTest("testTrimLogFiles"));
        suite.addTest(new LoggingTest("testCheckTrimLogFiles"));
        return suite;
    }
    
    public void testLogFileName() throws Exception {
        PrintStream ps = getLog("ahoj");
        File f = new File(getWorkDir(), "ahoj");
        assertEquals("Log file exists", true, f.exists());
    }
    
    public void testLogFileNameEqualsToNameOfTest() throws Exception {
        PrintStream ps = getLog();
        File f = new File(getWorkDir(), getName() + ".log");
        assertEquals("Log file exists", true, f.exists());
    }
    
    /** Test of NbTestCase#trimLogFiles method. It should trim size of all files
     * in test case workdir to 1 MB. The method is called at the end of 
     * NbTestCase#run method.
     */
    public void testTrimLogFiles() {
        StringBuffer buff = new StringBuffer(1024);
        for(int i=0;i<1024;i++) {
            buff.append('A');
        }
        String string1kB = buff.toString();
        for(int i=0;i<2024;i++) {
            log(string1kB);
            log("myLog", string1kB);
            ref(string1kB);
        }
    }
    
    /** Check log files created by previous test case were correctly trimmed. */
    public void testCheckTrimLogFiles() throws Exception {
        File trimmedDir = new File(getWorkDir().getParentFile(), "testTrimLogFiles");
        String[] filenames = {"testTrimLogFiles.log", "testTrimLogFiles.ref", "myLog" };
        for(int i=0;i<filenames.length;i++) {
            File file = new File(trimmedDir, "TRIMMED_"+filenames[i]);
            assertTrue(file.getName()+" not exists.", file.exists());
            assertTrue(file.getName()+" not trimmed to 1 MB.", file.length() < 2097152L);
            file = new File(trimmedDir, filenames[i]);
            if(file.exists()) {
                // original file exists only if cannot be deleted. Then it has minimal size.
                assertTrue(file.getName()+" not trimmed.", file.length() < 1024L);
            }
        }
    }
}