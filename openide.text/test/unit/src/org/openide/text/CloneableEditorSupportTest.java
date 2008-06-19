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

package org.openide.text;


import java.beans.PropertyChangeListener;
import java.io.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.EditorKit;
import junit.framework.*;
import org.netbeans.junit.*;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.*;


/** Testing different features of CloneableEditorSupport
 *
 * @author Jaroslav Tulach
 */
public class CloneableEditorSupportTest extends NbTestCase
implements CloneableEditorSupport.Env {
    static {
        System.setProperty("org.openide.windows.DummyWindowManager.VISIBLE", "false");
    }
    /** the support to work with */
    private CloneableEditorSupport support;
    /** the content of lookup of support */
    private InstanceContent ic;

    @RandomlyFails
    public void testDocCanBeGCdWhenNotModifiedButOpened() throws Exception {
        content = "Ahoj\nMyDoc";
        javax.swing.text.Document doc = support.openDocument ();
        assertNotNull (doc);
        
        WeakReference<Object> ref = new WeakReference<Object>(doc);
        doc = null;
        
        assertGC ("Document can dissapear", ref, Collections.singleton(support));

        assertFalse ("Document is not loaded", support.isDocumentLoaded ());
        assertTrue ("Can be closed without problems", support.close ());
    }

    public void testDocumentIsNotGCedIfModified () throws Exception {
        content = "Ahoj\nMyDoc";
        javax.swing.text.Document doc = support.openDocument ();
        assertNotNull (doc);
        doc.insertString (0, "Zmena", null);
        
        assertTrue ("Is modified", support.isModified ());
        
        WeakReference<Object> ref = new WeakReference<Object>(doc);
        doc = null;

        boolean ok;
        try {
            assertGC ("Should fail", ref);
            ok = false;
        } catch (AssertionFailedError expected) {
            ok = true;
        }
        if (!ok) {
            fail ("Document should not disappear, as it is modified");
        }
        
        assertTrue ("Document remains loaded", support.isDocumentLoaded ());
        
    }
    
    public void testDocumentIsNotGCedIfOpenedInEditor () throws Exception {
        content = "Ahoj\nMyDoc";
        javax.swing.text.Document doc = support.openDocument ();
        assertNotNull (doc);
        
        support.open();
        class R implements Runnable {
            JEditorPane[] arr;
            public void run() {
                arr = support.getOpenedPanes();
            }
            
            public JEditorPane[] getArr() throws Exception {
                SwingUtilities.invokeAndWait(this);
                return arr;
            }
        }
        R panes = new R();
        assertNotNull("There is one pane", panes.getArr());
        
        assertFalse("Not modified", support.isModified ());
        
        WeakReference<Object> ref = new WeakReference<Object>(doc);
        doc = null;

        boolean ok;
        try {
            assertGC ("Should fail", ref);
            ok = false;
        } catch (AssertionFailedError expected) {
            ok = true;
        }
        if (!ok) {
            fail ("Document should not disappear, as it is modified");
        }
        
        assertTrue ("Document remains loaded", support.isDocumentLoaded ());
        
        support.close();
        
        assertNull("There is no pane", panes.getArr());
        assertGC ("Should succeed with GC now", ref);
    }
    
    // Env variables
    private String content = "";
    private boolean valid = true;
    private boolean modified = false;
    /** if not null contains message why this document cannot be modified */
    private String cannotBeModified;
    private java.util.Date date = new java.util.Date ();
    private java.util.List<java.beans.PropertyChangeListener> propL = new ArrayList<PropertyChangeListener>();
    private java.beans.VetoableChangeListener vetoL;

    
    public CloneableEditorSupportTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new NbTestSuite(CloneableEditorSupportTest.class);
        
        return suite;
    }
    

    @Override
    protected void setUp () {
        ic = new InstanceContent ();
        support = new CES (this, new AbstractLookup (ic));
    }
    
    public void testDocumentCanBeRead () throws Exception {
        content = "Ahoj\nMyDoc";
        javax.swing.text.Document doc = support.openDocument ();
        assertNotNull (doc);
        
        String s = doc.getText (0, doc.getLength ());
        assertEquals ("Same text as in the stream", content, s);
        
        assertFalse ("No redo", support.getUndoRedo ().canRedo ());
        assertFalse ("No undo", support.getUndoRedo ().canUndo ());
    }
    
    public void testLineLookupIsPropagated () throws Exception {
        content = "Line1\nLine2\n";
        Integer template = new Integer (1);
        ic.add (template); // put anything into the lookup
        
        // in order to set.getLines() work correctly, the document has to be loaded
        support.openDocument();
        
        Line.Set set = support.getLineSet();
        java.util.List list = set.getLines();
        assertEquals ("Three lines", 3, list.size ());
        
        Line l = (Line)list.get (0);
        Integer i = l.getLookup().lookup(Integer.class);
        assertEquals ("The original integer", template, i);
        ic.remove (template);
        i = l.getLookup().lookup(Integer.class);
        assertNull ("Lookup is dynamic, so now there is nothing", i);
    }
    
    
    public void testGetInputStream () throws Exception {
        content = "goes\nto\nInputStream";
        String added = "added before\n";
        javax.swing.text.Document doc = support.openDocument ();
        assertNotNull (doc);
        
        // modify the document
        doc.insertString(0, added, null);
        compareStreamWithString(support.getInputStream(), added + content);
    }
    
    public void testGetInputStreamWhenClosed () throws Exception {
        content = "basic\ncontent";
        compareStreamWithString(support.getInputStream(), content);
        // we should be doing this with the document still closed 
        assertNull("The document is supposed to be still closed", support.getDocument ());
    }
    
    public void testDocumentCannotBeModified () throws Exception {
        content = "Ahoj\nMyDoc";
        cannotBeModified = "No, you cannot modify this document in this test";
        
        javax.swing.text.Document doc = support.openDocument ();
        assertNotNull (doc);
        
        assertFalse ("Nothing to undo", support.getUndoRedo ().canUndo ());
        
        // this should not be allowed
        doc.insertString (0, "Kuk", null);
        
        String modifiedForAWhile = doc.getText (0, 3);
        //assertEquals ("For a while the test really starts with Kuk", "Kuk", doc.getText (0, 3));
        
        assertFalse ("The document cannot be modified", support.getUndoRedo ().canUndo ());
        
        String s = doc.getText (0, doc.getLength ());
        assertEquals ("The document is now the same as at the begining", content, s);
        
        assertEquals ("Message has been shown to user in status bar", cannotBeModified, org.openide.awt.StatusDisplayer.getDefault ().getStatusText ());
    }
    
    public void testDocumentCanBeGarbageCollectedWhenClosed () throws Exception {
        content = "Ahoj\nMyDoc";
        javax.swing.text.Document doc = support.openDocument ();
        assertNotNull (doc);
        
        assertTrue ("Document is loaded", support.isDocumentLoaded ());
        assertTrue ("Can be closed without problems", support.close ());
        assertFalse ("Document is not loaded", support.isDocumentLoaded ());
        
        WeakReference<Object> ref = new WeakReference<Object>(doc);
        doc = null;
        
        assertGC ("Document can dissapear", ref);
    }

    /**
     * Tests that the wrapEditorComponent() method returns the passed
     * parameter (doesn't wrap the passed component in some additional UI).
     */
    public void testWrapEditorComponent() {
        javax.swing.JPanel panel = new javax.swing.JPanel();
        assertSame(support.wrapEditorComponent(panel), panel);
    }

    public void testSaveWhenNoDocumentOpen() throws IOException {
        modified = true;
        support.saveDocument();
    }

    public void testGetEditorKit() {
        EditorKit kit = CloneableEditorSupport.getEditorKit("text/plain");
        assertNotNull("EditorKit should never be null", kit);
        // There shouldn't be any EK registered and we should get the default one
        assertEquals("Wrong default EditorKit", "org.openide.text.CloneableEditorSupport$PlainEditorKit", kit.getClass().getName());
    }
    
    private void compareStreamWithString(InputStream is, String s) throws Exception{
        int i;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        byte b1[] = baos.toByteArray();
        byte b2[] = s.getBytes();
        assertTrue("Same bytes as would result from the string: " + s, Arrays.equals(b1, b2));
    }
    
    public void testDocumentBeforeSaveRunnableProcessed() throws Exception {
        content = "Ahoj\nMyDoc";
        javax.swing.text.Document doc = support.openDocument ();
        assertNotNull (doc);
        final boolean[] processed = { false };
        doc.putProperty("beforeSaveRunnable", new Runnable() {
            public void run() {
                processed[0] = true;
            }
        });
        doc.insertString(0, "Nazdar", null); // Modify doc to allow save
        support.saveDocument();
        assertTrue("CES.saveDocument() did not execute a runnable in \"beforeSaveRunnable\" document property",
                processed[0]);
    }

    //
    // Implementation of the CloneableEditorSupport.Env
    //
    
    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        propL.add (l);
    }    
    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        propL.remove (l);
    }
    
    public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener l) {
        assertNull ("This is the first veto listener", vetoL);
        vetoL = l;
    }
    public void removeVetoableChangeListener(java.beans.VetoableChangeListener l) {
        assertEquals ("Removing the right veto one", vetoL, l);
        vetoL = null;
    }
    
    public org.openide.windows.CloneableOpenSupport findCloneableOpenSupport() {
        return support;
    }
    
    public String getMimeType() {
        return "text/plain";
    }
    
    public java.util.Date getTime() {
        return date;
    }
    
    public java.io.InputStream inputStream() throws java.io.IOException {
        return new java.io.ByteArrayInputStream (content.getBytes ());
    }
    public java.io.OutputStream outputStream() throws java.io.IOException {
        class ContentStream extends java.io.ByteArrayOutputStream {
            @Override
            public void close() throws java.io.IOException {
                super.close ();
                content = new String (toByteArray ());
            }
        }
        
        return new ContentStream ();
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public boolean isModified() {
        return modified;
    }

    public void markModified() throws java.io.IOException {
        if (cannotBeModified != null) {
            final String notify = cannotBeModified;
            IOException e = new IOException () {
                public String getLocalizedMessage () {
                    return notify;
                }
            };
            Exceptions.attachLocalizedMessage(e, cannotBeModified);
            throw e;
        }
        
        modified = true;
    }
    
    public void unmarkModified() {
        modified = false;
    }

    /** Implementation of the CES */
    private static final class CES extends CloneableEditorSupport {
        public CES (Env env, Lookup l) {
            super (env, l);
        }
        
        protected String messageName() {
            return "Name";
        }
        
        protected String messageOpened() {
            return "Opened";
        }
        
        protected String messageOpening() {
            return "Opening";
        }
        
        protected String messageSave() {
            return "Save";
        }
        
        protected String messageToolTip() {
            return "ToolTip";
        }
        
    }
}
