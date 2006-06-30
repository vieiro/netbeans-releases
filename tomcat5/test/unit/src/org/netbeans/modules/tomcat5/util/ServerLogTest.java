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

package org.netbeans.modules.tomcat5.util;

import java.io.File;
import junit.textui.TestRunner;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.modules.tomcat5.TomcatFactory55;
import org.netbeans.modules.tomcat5.TomcatFactory55Test;
import org.netbeans.modules.tomcat5.TomcatManager;
import org.openide.util.Utilities;
import org.netbeans.modules.tomcat5.util.LogSupport.LineInfo;
import org.netbeans.modules.tomcat5.util.ServerLog.ServerLogSupport;

/**
 *
 * @author sherold
 */
public class ServerLogTest extends NbTestCase {
    
    private File datadir;
    
    public ServerLogTest(String testName) {
        super(testName);
    }
    
    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        suite.addTest(new ServerLogTest("testAnalyzeLine"));
        return suite;
    }
    
    protected void setUp() throws Exception {
        super.setUp ();
        datadir = getDataDir();
    }
    
    public void testAnalyzeLine() {
        
        String log[] = new String[] {
            "Jan 5, 2006 6:46:45 PM org.apache.catalina.core.StandardWrapperValve invoke",
            "SEVERE: Servlet.service() for servlet HyperlinkTest threw exception",
            "java.lang.IllegalStateException",
            "       at t.HyperlinkTest$1.run(HyperlinkTest.java:24)",
            "       at t.HyperlinkTest.processRequest(HyperlinkTest.java:27)",
            "       at foo.bar",
        };
        
        String files[] = new String[] {
            null,
            null,
            null,
            "t/HyperlinkTest.java",
            "t/HyperlinkTest.java",
            null,
        };
        
        int lines[] = new int[] {
            -1,
            -1,
            -1,
            24,
            27,
            -1,
        };
        
        String message[] = new String[] {
            null,
            null,
            null,
            "java.lang.IllegalStateException",
            "java.lang.IllegalStateException",
            null,
        };
        
        ServerLogSupport sup = new ServerLogSupport();
        for (int i = 0; i < log.length; i++) {
            LineInfo nfo = sup.analyzeLine(log[i]);
            System.out.println(nfo);
            assertEquals("Path \"" + nfo.path() + "\" incorrectly recognized from: " + log[i],
                         files[i], nfo.path());
            assertEquals("Line \"" + nfo.line() + "\" incorrectly recognized from: " + log[i],
                         lines[i], nfo.line());
            assertEquals("Message \"" + nfo.message() + "\" incorrectly recognized from: " + log[i],
                         message[i], nfo.message());
        }
    }
    
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
}
