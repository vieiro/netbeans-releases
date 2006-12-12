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
package org.netbeans.core;

import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.netbeans.junit.NbTestCase;
import org.openide.windows.TopComponent;

/** Basic tests on NbClipboard
 *
 * @author Jaroslav Tulach
 */
public class NbClipboardTest extends NbTestCase {

    public NbClipboardTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        System.getProperties().remove("netbeans.slow.system.clipboard.hack");
    }

    protected void tearDown() throws Exception {
    }

    public void testDefaultOnJDK15AndLater() {
        if (System.getProperty("java.version").startsWith("1.4")) {
            return;
        }
        
        NbClipboard ec = new NbClipboard();
        assertTrue("By default we still do use slow hacks", ec.slowSystemClipboard);
    }
    public void testPropOnJDK15AndLater() {
        if (System.getProperty("java.version").startsWith("1.4")) {
            return;
        }
        
        System.setProperty("netbeans.slow.system.clipboard.hack", "false");
        
        NbClipboard ec = new NbClipboard();
        assertFalse("Property overrides default", ec.slowSystemClipboard);
        assertEquals("sun.awt.datatransfer.timeout is now 1000", "1000", System.getProperty("sun.awt.datatransfer.timeout"));
    }
    
    public void testMemoryLeak89844() throws Exception {
        NbClipboard ec = new NbClipboard();
        
        TopComponent tc = new TopComponent();
        tc.open();
        
        Window w;
        for(;;) {
            w = SwingUtilities.getWindowAncestor(tc);
            if (w != null && w.isVisible()) {
                break;
            }
            Thread.sleep(100);
        }
       
        tc.close();
        w.dispose();
        
        // opening new frame shall clear all the AWT references to previous frame
        JFrame f = new JFrame("Focus stealer");
        f.setVisible(true);
        f.pack();
        f.toFront();
        f.requestFocus();
        f.requestFocusInWindow();
        
        waitEQ(f);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        
        WeakReference<Object> ref = new WeakReference<Object>(w);
        w = null;
        tc = null;

        try {
            assertGC("Top component can disappear", ref);
        } catch (junit.framework.AssertionFailedError ex) {
            if (ex.getMessage().indexOf("NbClipboard") >= 0) {
                throw ex;
            }
            Logger.getAnonymousLogger().log(Level.WARNING, "Cannot do GC, but not due to NbClipboard itself", ex);
        }
    }
    
    private static void waitEQ(final Window w) throws Exception {
        class R implements Runnable {
            boolean visible;
            
            public void run() {
                visible = w.isShowing();
            }
        }
        R r = new R();
        while (!r.visible) {
            SwingUtilities.invokeAndWait(r);
        }
    }
}
