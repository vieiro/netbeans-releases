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

package org.netbeans;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.netbeans.junit.AssertionFailedErrorException;
import org.netbeans.junit.MockServices;
import org.netbeans.junit.NbTestCase;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.ClipboardEvent;
import org.openide.util.datatransfer.ClipboardListener;
import org.openide.util.datatransfer.ExClipboard;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.TransferListener;

/** Test NbClipboard, in "native" mode (e.g. Windows).
 * @author Jesse Glick
 * @see "#30923"
 */
public class NbClipboardNativeTest extends NbTestCase implements ClipboardListener {

    public static Test suite() {
        return GraphicsEnvironment.isHeadless() ? new TestSuite() : new TestSuite(NbClipboardNativeTest.class);
    }

    private NbClipboard ec;
    private int listenerCalls;
    private Logger LOG;
    
    public NbClipboardNativeTest(String name) {
        super(name);
    }
    
    protected void setUp() throws Exception {
        MockServices.setServices();
        LOG = Logger.getLogger("TEST-" + getName());
        
        class EmptyTrans  implements Transferable, ClipboardOwner {
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[0];
            }

            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return false;
            }

            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                throw new IOException("Nothing here");
            }

            public void lostOwnership(Clipboard clipboard, Transferable contents) {
            }
        }

        
        super.setUp();
        
        LOG.info("Setting up");
        
        //System.setProperty("org.netbeans.core.NbClipboard", "-5");
        System.setProperty("netbeans.slow.system.clipboard.hack", String.valueOf(slowClipboardHack()));
        this.ec = new NbClipboard();
        
        LOG.info("Clipboard created");

        EmptyTrans et = new EmptyTrans();
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(et, et);

        waitFinished(this.ec);
        
        LOG.info("system clipboard content changed");
        
        this.ec.addClipboardListener(this);
        
        LOG.info("Listener added");
    }
    
    protected void tearDown () throws Exception {
        super.tearDown ();
        if (ec != null) {
            this.ec.removeClipboardListener(this);
        }
        
        waitFinished(ec);
    }
    
    protected boolean slowClipboardHack() {
        return false;
    }
    
    public void testGetClipboardWorks() throws Exception {
        class Get implements ClipboardListener {
            Transferable in;
            
            public void clipboardChanged(ClipboardEvent ev) {
                in = ec.getContents(this);
            }
        }
        
        Get get = new Get();
        ec.addClipboardListener(get);
        
        StringSelection ss = new StringSelection("x");
        ec.setContents(ss, ss);

        assertEquals("Inside is the right one", ss.getTransferData(DataFlavor.stringFlavor), get.in.getTransferData(DataFlavor.stringFlavor));
    }
    
    public void testWhenCallingGetContentsItChecksSystemClipboardFirstTimeAfterActivation () throws Exception {
        assertEquals ("No changes yet", 0, listenerCalls);
        
        Clipboard sc = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection ss = new StringSelection ("oldvalue");
        sc.setContents(ss, ss);
        
        
        // just simulate initial switch to NetBeans main window
        ec.activateWindowHack (true);
        waitFinished (ec);
        
        if (listenerCalls == 0) {
            fail("We need at least one call: " + listenerCalls);
        }
        listenerCalls = 0;
        
        StringSelection s2 = new StringSelection ("data2");
        sc.setContents (s2, s2);
        
        waitFinished (ec);

        if (slowClipboardHack()) {
            assertEquals ("No change notified", 0, listenerCalls);
        }
        
        // we need to wait longer time than the value in NbClipboard
        Thread.sleep (200);
        
        Transferable t = this.ec.getContents(this);
        assertTrue ("String flavor is there", t.isDataFlavorSupported(DataFlavor.stringFlavor));
        
        String s = (String)t.getTransferData(DataFlavor.stringFlavor);
        assertEquals ("The getContents rechecked the system clipboard first time after window activated", "data2", s);
        
        sc.setContents (ss, ss);
        
        t = this.ec.getContents(this);
        s = (String)t.getTransferData(DataFlavor.stringFlavor);
        if (slowClipboardHack ()) {
            assertEquals ("The getContents rechecked the clipboard just for the first time, not now, so the content is the same", "data2", s);

            ec.activateWindowHack (true);
            Thread.sleep (200);

            t = this.ec.getContents(this);
            s = (String)t.getTransferData(DataFlavor.stringFlavor);
            assertEquals ("The WINDOW_ACTIVATED rechecks the clipboard", "oldvalue", s);
        } else {
            assertEquals ("without slow hack it gets the value immediatelly", "oldvalue", s);
        }
    }
    
    public void testClipboard() throws Exception {
        MockServices.setServices(Cnv.class);
        Clipboard c = Lookup.getDefault().lookup(Clipboard.class);
        ExClipboard ec = Lookup.getDefault().lookup(ExClipboard.class);
        assertEquals("Clipboard == ExClipboard", c, ec);
        assertNotNull(Lookup.getDefault().lookup(ExClipboard.Convertor.class));
        assertEquals(Cnv.class, Lookup.getDefault().lookup(ExClipboard.Convertor.class).getClass());
        c.setContents(new ExTransferable.Single(DataFlavor.stringFlavor) {
            protected Object getData() throws IOException, UnsupportedFlavorException {
                return "17";
            }
        }, null);
        Transferable t = c.getContents(null);
        assertTrue("still supports stringFlavor", t.isDataFlavorSupported(DataFlavor.stringFlavor));
        assertEquals("correct string in clipboard", "17", t.getTransferData(DataFlavor.stringFlavor));
        assertTrue("support Integer too", t.isDataFlavorSupported(MYFLAV));
        assertEquals("correct Integer", new Integer(17), t.getTransferData(MYFLAV));
    }
    
    private static final DataFlavor MYFLAV = new DataFlavor("text/x-integer", "Integer"); // data: java.lang.Integer
    public static final class Cnv implements ExClipboard.Convertor {
        public Transferable convert(Transferable t) {
            Logger.getAnonymousLogger().info("converting...");
            if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                final ExTransferable t2 = ExTransferable.create(t);
                if (t2.isDataFlavorSupported(DataFlavor.stringFlavor) && !t2.isDataFlavorSupported(MYFLAV)) {
                    t2.put(new ExTransferable.Single(MYFLAV) {
                        protected Object getData() throws IOException, UnsupportedFlavorException {
                            String s = (String)t2.getTransferData(DataFlavor.stringFlavor);
                            try {
                                return new Integer(s);
                            } catch (NumberFormatException nfe) {
                                throw new IOException(nfe.toString());
                            }
                        }
                    });
                }
                return t2;
            } else {
                return t;
            }
        }
    }


    // #25537
    public void testOwnershipLostEvent() throws Exception {
        final int[] holder = new int[] { 0 };
        ExTransferable transferable = ExTransferable.create (new StringSelection("A"));

        // listen on ownershipLost
        transferable.addTransferListener (new TransferListener () {
            public void accepted (int action) {}
            public void rejected () {}
            public void ownershipLost () { holder[0]++; }
        });

        Clipboard c = Lookup.getDefault().lookup(Clipboard.class);

        c.setContents(transferable, null);

        assertTrue("Still has ownership", holder[0] == 0);

        c.setContents(new StringSelection("B"), null);

        assertTrue("Exactly one ownershipLost event have happened.", holder[0] == 1);
    }

    public void clipboardChanged(ClipboardEvent ev) {
        listenerCalls++;
        LOG.log(Level.INFO, "clipboardChanged: " + listenerCalls, new Exception());
    }
    
    private void waitFinished(NbClipboard ec) {
        try {
            ec.waitFinished();
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                }
            });
            ec.waitFinished();
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                }
            });
        } catch (InterruptedException ex) {
            throw new AssertionFailedErrorException(ex);
        } catch (InvocationTargetException ex) {
            throw new AssertionFailedErrorException(ex);
        }
    }

    protected Level logLevel() {
        return Level.ALL;
    }
    
}
