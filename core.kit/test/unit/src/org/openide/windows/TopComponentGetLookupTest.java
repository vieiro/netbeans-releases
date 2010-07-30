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

package org.openide.windows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.beans.FeatureDescriptor;
import java.util.*;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JTextField;
import junit.framework.Test;


import org.netbeans.junit.*;
import org.openide.cookies.*;
import org.openide.nodes.*;
import org.openide.util.*;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Check the behaviour of TopComponent's lookup.
 * @author Jaroslav Tulach, Jesse Glick
 */
public class TopComponentGetLookupTest extends NbTestCase {
    
    /** top component we work on */
    protected TopComponent top;
    protected TopComponent get;
    /** its lookup */
    protected Lookup lookup;
    
    public TopComponentGetLookupTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new NbTestSuite(TopComponentGetLookupTest.class);
    }
    
    /** Setup component with lookup.
     */
    protected void setUp () {
        top = new TopComponent ();
        get = top;
        lookup = top.getLookup ();
    }
    
    protected boolean runInEQ () {
        return true;
    }
    
    
    
    /** Test to find nodes.
     */
    private void doTestNodes(Node[] arr, Class c, int cnt) {
        if (arr != null) {
            top.setActivatedNodes(arr);
        }
        
        assertNotNull ("At least one node is registered", lookup.lookup (c));
        Lookup.Result res = lookup.lookup (new Lookup.Template (c));
        Collection coll = res.allItems();
        assertEquals ("Two registered: " + coll, cnt, coll.size ());
    }
    
    public void testNodes () {
        doTestNodes(new Node[] {new N("1"), new N("2")}, N.class, 2);
        doTestNodes(new Node[] {new N("1"), new N("2")}, FeatureDescriptor.class, 2);
    }
    
    private void doTestNodesWithChangesInLookup (Class c) {
        InstanceContent ic = new InstanceContent();
        
        Node[] arr = new Node[] {
            new AbstractNode(Children.LEAF, new AbstractLookup(ic)),
            new AbstractNode(Children.LEAF, Lookup.EMPTY),
        };
        arr[0].setName("cookie-container-node");
        arr[1].setName("node-as-cookie");
        //doTestNodes(arr, AbstractNode.class);
        doTestNodes (arr, c, 2);
        
        ic.add (arr[1]);
        
        /* Huh? There should be both [0] and [1], how can you say which one will be returned?
        assertEquals ("Now the [1] is in lookup of [0]", arr[1], lookup.lookup (c));
         */
        Collection all = lookup.lookup(new Lookup.Template(c)).allInstances();
        assertEquals("Two nodes are in TC lookup", 2, all.size());
        assertEquals("They are the ones we expect", new HashSet(Arrays.asList(arr)), new HashSet(all));
        assertTrue("Lookup simple query gives one or the other", new HashSet(Arrays.asList(arr)).contains(lookup.lookup(c)));
        assertEquals("Have two lookup items", 2, lookup.lookup(new Lookup.Template(c)).allItems().size());

        doTestNodes (null, c, 2);
    }
    
    public void testNodesWhenTheyAreNotInTheirLookup () {
        doTestNodesWithChangesInLookup(AbstractNode.class);
    }
    
    public void testNodesSuperclassesWhenTheyAreNotInTheirLookup () {
        doTestNodesWithChangesInLookup(FeatureDescriptor.class);
    }
    
    public void testFilterNodeProblems () {
        class CookieN extends AbstractNode implements Node.Cookie {
            public CookieN () {
                super (Children.LEAF);
                getCookieSet ().add (this);
            }
            
        }
        
        CookieN n = new CookieN ();
        FilterNode fn = new FilterNode (n);
        top.setActivatedNodes (new Node[] { fn });
        assertTrue ("CookieN is in FilterNode lookup", n == fn.getLookup ().lookup (CookieN.class));
        assertTrue ("CookieN is in TopComponent", n == lookup.lookup (CookieN.class));
        assertEquals ("Just one node", 1, lookup.lookup (new Lookup.Template (Node.class)).allItems ().size ());
        assertTrue ("Plain cookie found", n == lookup.lookup (Node.Cookie.class));
    }
    
    
    /** Tests changes in cookies.
     */
    public void testCookies () {
        N[] arr = { new N ("1"), new N ("2"), new N ("3") };
        
        top.setActivatedNodes (arr);
        assertEquals ("Three nodes there", 3, top.getActivatedNodes ().length);
        
        L l = new L ();
        Lookup.Result res = lookup.lookup(new Lookup.Template(OpenCookie.class));
        res.addLookupListener (l);
     
        assertEquals ("Empty now", res.allItems().size (), 0);
        
        arr[0].state (0x01); // enabled open cookie

        assertEquals ("One item", res.allItems ().size (), 1);
        l.check ("One change", 1);
        
        arr[2].state (0x02); // change of different cookie
        
        assertEquals ("Still one item", res.allItems ().size (), 1);
        l.check ("No change", 0);
        
        arr[2].state (0x03); // added also OpenCookie
        
        assertEquals ("Both items", res.allItems ().size (), 2);
        l.check ("One change again", 1);
        
        arr[0].state (0x00);
        
        assertEquals ("One still there", res.allItems ().size (), 1);
        assertEquals("The second object", lookup.lookup(OpenCookie.class), arr[2].getCookie(OpenCookie.class));
        
        top.setActivatedNodes(new Node[0]);
        assertNull("No cookie now", lookup.lookup(OpenCookie.class));
    }
    
    public void testNodesAreInTheLookupAndNothingIsFiredBeforeFirstQuery () {
        AbstractNode n1 = new AbstractNode(Children.LEAF, Lookup.EMPTY);
        top.setActivatedNodes(new Node[] { n1 });
        assertEquals ("One node there", 1, top.getActivatedNodes ().length);
        assertEquals ("Is the right now", n1, top.getActivatedNodes ()[0]);
        
        Lookup.Result res = lookup.lookup(new Lookup.Template(Node.class));
        L l = new L ();
        res.addLookupListener(l);
        
        l.check ("Nothing fired before first query", 0);
        res.allInstances ();
        l.check ("Nothing is fired on first query", 0);
        lookup.lookup(new Lookup.Template(Node.class)).allInstances();
        l.check ("And additional query does not change anything either", 0);
    }
   
    public void testNodesAreThereEvenIfTheyAreNotContainedInTheirOwnLookup () {
        Lookup.Result res = lookup.lookup(new Lookup.Template(Node.class));
        
        AbstractNode n1 = new AbstractNode(Children.LEAF, Lookup.EMPTY);
        
        InstanceContent content = new InstanceContent ();
        AbstractNode n2 = new AbstractNode(Children.LEAF, new AbstractLookup(content));
        
        assertNull ("Not present in its lookup", n1.getLookup ().lookup (n1.getClass ()));
        assertNull ("Not present in its lookup", n2.getLookup ().lookup (n2.getClass ()));
        
        top.setActivatedNodes (new AbstractNode[] { n1 });
        assertEquals ("But node is in the lookup", n1, lookup.lookup (n1.getClass ()));
        
        assertEquals ("One item there", 1, res.allInstances ().size ());
        
        L listener = new L ();
        res.addLookupListener(listener);
        
        top.setActivatedNodes (new AbstractNode[] { n2 });
        assertEquals ("One node there", 1, top.getActivatedNodes ().length);
        assertEquals ("n2", n2, top.getActivatedNodes ()[0]);
        
//MK - here it changes twice.. because the setAtivatedNodes is trigger on inner TC, then lookup of MVTC contains old activated node..
        // at this monent the merged lookup contains both items.. later it gets synchronized by setting the activated nodes on the MVTC as well..
        // then it contains only the one correct node..
        listener.check ("Node changed", 1);
        
        Collection addedByTCLookup = res.allInstances();
        assertEquals ("One item still", 1, addedByTCLookup.size ());
        
        content.add (n2);
        assertEquals ("After the n2.getLookup starts to return itself, there is no change", 
            addedByTCLookup, res.allInstances ());

        // this could be commented out if necessary:
        listener.check ("And nothing is fired", 0);
        
        content.remove (n2);
        assertEquals ("After the n2.getLookup stops to return itself, there is no change", 
            addedByTCLookup, res.allInstances ());
        // this could be commented out if necessary:
        listener.check ("And nothing is fired", 0);
        
        content.add (n1);
        // this could be commented out if necessary:
        listener.check ("And nothing is fired", 0);
        // Change from former behavior (#36336): we don't *want* n1 in res.
        Collection one = res.allInstances();
        assertEquals("Really just the activated node", 1, one.size());
        Iterator it = one.iterator();
        assertEquals("It is the one added by the TC lookup", n2, it.next());
    }
    
    public void testNoChangeWhenSomethingIsChangedOnNotActivatedNode () {
        doTestNoChangeWhenSomethingIsChangedOnNotActivatedNode (0);
    }
    
    public void testNoChangeWhenSomethingIsChangedOnNotActivatedNode2 () {
        doTestNoChangeWhenSomethingIsChangedOnNotActivatedNode (50);
    }
        
    private void doTestNoChangeWhenSomethingIsChangedOnNotActivatedNode (int initialSize) {
        Object obj = new OpenCookie() { public void open() {} };
        
        Lookup.Result res = lookup.lookup(new Lookup.Template(OpenCookie.class));
        Lookup.Result nodeRes = lookup.lookup (new Lookup.Template(Node.class));
        
        InstanceContent ic = new InstanceContent ();
        CountingLookup cnt = new CountingLookup (ic);
        AbstractNode ac = new AbstractNode(Children.LEAF, cnt);
        for (int i = 0; i < initialSize; i++) {
            ic.add (new Integer (i));
        }
        
        top.setActivatedNodes(new org.openide.nodes.Node[] { ac });
        assertEquals ("One node there", 1, top.getActivatedNodes ().length);
        assertEquals ("It is the ac one", ac, top.getActivatedNodes ()[0]);
        ic.add (obj);
        
        L listener = new L ();
        
        res.allItems();
        nodeRes.allItems ();
        res.addLookupListener (listener);
        
        Collection allListeners = cnt.listeners;
        
        assertEquals ("Has the cookie", 1, res.allItems ().size ());
        listener.check ("No changes yet", 0);

        ic.remove (obj);
        
        assertEquals ("Does not have the cookie", 0, res.allItems ().size ());
        listener.check ("One change", 1);
        
        top.setActivatedNodes (new N[0]);
        assertEquals("The nodes are empty", 0, top.getActivatedNodes ().length);
        listener.check ("No change", 0);
        
        cnt.queries = 0;
        ic.add (obj);
        ic.add (ac);
        listener.check ("Removing the object or node from not active node does not send any event", 0);
        
        nodeRes.allItems ();
        listener.check ("Queriing for node does generate an event", 0);
        assertEquals ("No Queries to the not active node made", 0, cnt.queries);
        assertEquals ("No listeneners on cookies", allListeners, cnt.listeners);
    }
    
    public void testBug32470FilterNodeAndANodeImplementingACookie () {
        class NY extends AbstractNode implements SaveCookie {
            public NY () {
                super(Children.LEAF);
                getCookieSet ().add (this);
            }
            
            public void save () {
            }
        }
        
        Node ny = new NY ();
        Node node = new FilterNode (new FilterNode (ny, null, ny.getLookup ()));
        top.setActivatedNodes (new Node[] { node });
        
        Lookup.Template nodeTemplate = new Lookup.Template(Node.class);
        Lookup.Template saveTemplate = new Lookup.Template(SaveCookie.class);
        java.util.Collection res;
        
        res = lookup.lookup (nodeTemplate).allInstances ();
        
        assertEquals("just one returned", res.size(), 1);
        assertEquals("node is node", node, res.iterator().next());
        //MK - the above 2 tests should test the same..
//        assertEquals ("FilterNode is the only node there", 
//            Collections.singletonList(node), res
//        );

        res = lookup.lookup (saveTemplate).allInstances ();
        
        assertEquals("just one returned", res.size(), 1);
        assertEquals("node is node", ny, res.iterator().next());
        //MK - the above 2 tests should test the same..
//        assertEquals ("SaveCookie is there only once", 
//            Collections.singletonList(ny), res
//        );

        res = lookup.lookup (nodeTemplate).allInstances ();
        
        assertEquals("just one returned", res.size(), 1);
        assertEquals("node is node", node, res.iterator().next());
        //MK - the above 2 tests should test the same..
//        assertEquals ("FilterNode is still the only node there", 
//            Collections.singletonList(node), res
//        );
    }

    public void testActionMapIsTakenFromComponentAndAlsoFromFocusedOne () {
        JTextField panel = new JTextField();
        
        class Def extends DefaultKeyboardFocusManager {
            private Component c;
            
            public Def(Component c) {
                this.c = c;
            }
            public Component getFocusOwner() {
                return c;
            }
        }
        KeyboardFocusManager prev = KeyboardFocusManager.getCurrentKeyboardFocusManager();

        try {
            KeyboardFocusManager.setCurrentKeyboardFocusManager(new Def (panel));



            top.add(BorderLayout.CENTER, panel);

            class Act extends AbstractAction {
                public void actionPerformed(ActionEvent ev) {
                }
            }
            Act act1 = new Act ();
            Act act2 = new Act ();
            Act act3 = new Act ();

            top.getActionMap ().put ("globalRegistration", act1);
            top.getActionMap ().put ("doubleRegistration", act2);

            panel.getActionMap ().put ("doubleRegistration", act3);
            panel.getActionMap ().put ("focusedRegistration", act3);


            ActionMap map = (ActionMap)top.getLookup ().lookup (ActionMap.class);

            assertEquals ("actions registered directly on TC are found", act1, map.get ("globalRegistration"));
            assertEquals ("even if they are provided by focused component", act2, map.get ("doubleRegistration"));

            assertEquals ("Should be focused now", 
                panel, 
                KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()
            );
            assertEquals ("actions are delegated to focus owner, if not present", act3, map.get ("focusedRegistration"));

            JTextField f = new JTextField ();
            f.getActionMap ().put ("focusedRegistration", act3);
            KeyboardFocusManager.setCurrentKeyboardFocusManager(new Def (f));
            assertEquals ("f should be focused now", 
                f, 
                KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()
            );
            assertEquals ("but as it is not in the right component, nothing is found", null, map.get ("focusedRegistration"));
        } finally {
            KeyboardFocusManager.setCurrentKeyboardFocusManager (prev);
        }
    }
    
    
    public void testChangingNodesDoesNotChangeActionMap () {
        N node = new N ("testChangingNodesDoesNotChangeActionMap");
        node.state (0x00);
        top.setActivatedNodes(new Node[] { node });
        
        Lookup.Result res = lookup.lookup (new Lookup.Template (ActionMap.class));
        assertEquals ("One item there", 1, res.allInstances ().size ());
        ActionMap map = (ActionMap)res.allInstances().toArray()[0];
        
        L l = new L ();
        res.addLookupListener (l);

        node.state (0x01);
        
        assertEquals ("Map is still the same", map, res.allInstances().toArray()[0]);
        
        l.check ("No change in lookup", 0);
        
        top.setActivatedNodes (new Node[] { Node.EMPTY });
        assertEquals ("Map remains the same", map, res.allInstances().toArray()[0]);
        
        l.check ("There is no change", 0);
        
    }

    public void testMapKeys45323 () {
        assertNotNull (top.getActionMap ().keys ());
    }
     
     
    /**
     * Check that even if a node has a <em>different</em> node in its lookup, a
     * query on Node.class will produce only the actual activated nodes.
     * Other queries may return the embedded node, but not duplicates.
     * @see "#36336"
     */
    public void testForeignNodesInLookupIgnoredForNodeQuery() throws Exception {
        class CloseCookieNode extends AbstractNode implements CloseCookie {
            CloseCookieNode() {
                super(Children.LEAF);
                setName("n1");
            }
            public boolean close() {return true;}
        }
        Node n1 = new CloseCookieNode();
        Node n2 = new AbstractNode(Children.LEAF) {
            {
                setName("n2");
                class ViewCookieNode extends AbstractNode implements ViewCookie {
                    ViewCookieNode() {
                        super(Children.LEAF);
                        setName("n3");
                    }
                    public void view() {}
                }
                getCookieSet().add(new ViewCookieNode());
                getCookieSet().add(new OpenCookie() {
                    public void open() {}
                });
            }
        };
        Node[] sel = new Node[] {n1, n2};
        assertEquals("First node in selection has CloseCookie",
            1,
            n1.getLookup().lookup(new Lookup.Template(CloseCookie.class)).allInstances().size());
        assertEquals("Second node in selection has OpenCookie",
            1,
            n2.getLookup().lookup(new Lookup.Template(OpenCookie.class)).allInstances().size());
        assertEquals("Second node in selection has ViewCookie (actually a Node)",
            1,
            n2.getLookup().lookup(new Lookup.Template(ViewCookie.class)).allInstances().size());
        ViewCookie v = (ViewCookie)n2.getCookie(ViewCookie.class);
        assertNotNull(v);
        assertTrue(v instanceof Node);
        
        HashSet queryJustOnce = new HashSet(n2.getLookup().lookup(new Lookup.Template(Node.class)).allInstances());
        assertEquals("Second node in selection has two nodes in its own lookup",
            new HashSet(Arrays.asList(new Object[] {n2, v})), queryJustOnce
        );
        assertEquals(2, queryJustOnce.size());
        top.setActivatedNodes(sel);
        assertEquals("CloseCookie propagated from one member of node selection to TC lookup",
            1,
            lookup.lookup(new Lookup.Template(CloseCookie.class)).allInstances().size());
        assertEquals("OpenCookie propagated from one member of node selection to TC lookup",
            1,
            lookup.lookup(new Lookup.Template(OpenCookie.class)).allInstances().size());
        assertEquals("ViewCookie propagated from one member of node selection to TC lookup",
            1,
            lookup.lookup(new Lookup.Template(ViewCookie.class)).allInstances().size());
        assertEquals("But TC lookup query on Node gives only selection, not cookie node",
            new HashSet(Arrays.asList(sel)),
            new HashSet(lookup.lookup(new Lookup.Template(Node.class)).allInstances()));
        assertEquals(2, lookup.lookup(new Lookup.Template(Node.class)).allInstances().size());
        assertEquals("TC lookup query on FeatureDescriptor gives all three however",
            3,
            lookup.lookup(new Lookup.Template(FeatureDescriptor.class)).allInstances().size());
        top.setActivatedNodes(new Node[] {n1});
        assertEquals("After setting node selection to one node, TC lookup has only that node",
            Collections.singleton(n1),
            new HashSet(lookup.lookup(new Lookup.Template(Node.class)).allInstances()));
        assertEquals(1, lookup.lookup(new Lookup.Template(Node.class)).allInstances().size());
        assertEquals("And the OpenCookie is gone",
            0,
            lookup.lookup(new Lookup.Template(OpenCookie.class)).allInstances().size());
        assertEquals("And the ViewCookie is gone",
            0,
            lookup.lookup(new Lookup.Template(ViewCookie.class)).allInstances().size());
        assertEquals("But the CloseCookie remains",
            1,
            lookup.lookup(new Lookup.Template(CloseCookie.class)).allInstances().size());
    }

    public void testAssociateLookupCanBecalledJustOnce () throws Exception {
        class TC extends TopComponent {
            public TC () {
            }
            
            public TC (Lookup l) {
                super (l);
            }
            
            public void asso (Lookup l) {
                associateLookup (l);
            }
        }
        
        TC tc = new TC ();
        assertNotNull ("There is default lookup", tc.getLookup ());
        try {
            tc.asso (Lookup.EMPTY);
            fail ("Should throw an exception");
        } catch (IllegalStateException ex) {
            // ok, should be thrown
        }
        
        tc = new TC (Lookup.EMPTY);
        assertEquals ("Should return the provided lookup", Lookup.EMPTY, tc.getLookup ());
        
        try {
            tc.asso (Lookup.EMPTY);
            fail ("Should throw an exception - second association not possible");
        } catch (IllegalStateException ex) {
            // ok, should be thrown
        }
    
        tc = new TC ();
        tc.asso (Lookup.EMPTY);
        assertEquals ("First association was successful", Lookup.EMPTY, tc.getLookup ());
        
        try {
            tc.asso (new TC ().getLookup ());
            fail ("Should throw an exception - second association not possible");
        } catch (IllegalStateException ex) {
            // ok, should be thrown
        }
    }
    
    /** Listener to count number of changes.
     */
    private static final class L extends Object 
    implements LookupListener {
        private int cnt;
        
        /** A change in lookup occured.
         * @param ev event describing the change
         */
        public void resultChanged(LookupEvent ev) {
            cnt++;
        }
        
        /** Checks at least given number of changes.
         */
        public void checkAtLeast (String text, int num) {
            if (cnt < num) {
                fail (text + " expected at least " + num + " but was " + cnt);
            }
            cnt = 0;
        }
        
        /** Checks number of modifications.
         */
        public void check (String text, int num) {
            assertEquals (text, num, cnt);
            cnt = 0;
        }
    }
    

    /** Overides some methods so it is not necessary to use the data object.
     */
    protected static final class N extends AbstractNode {
        private Node.Cookie[] cookies = {
            new OpenCookie() { public void open() {} },
            new EditCookie() { public void edit() {} },
            new SaveCookie() { public void save() {} },
            new CloseCookie() { public boolean close() { return true; } },
        };
    
        private int s;
        
        public N (String name) {
            super(Children.LEAF);
            setName (name);
        }

        public void state (int s) {
            this.s = s;
            fireCookieChange ();
        }
        
        public Node.Cookie getCookie(Class c) {
            int mask = 0x01;
            
            for (int i = 0; i < cookies.length; i++) {
                if ((s & mask) != 0 && c.isInstance(cookies[i])) {
                    return cookies[i];
                }
                mask = mask << 1;

            }
            return null;
        }
    }
    
    private static final class CountingLookup extends Lookup {
        private Lookup delegate;
        public List listeners = new ArrayList();
        public int queries;
        
        public CountingLookup(InstanceContent ic) {
            delegate = new AbstractLookup (ic);
            
        }
        
        public Object lookup(Class clazz) {
            return delegate.lookup (clazz);
        }
        
        public Lookup.Result lookup(Lookup.Template template) {
            if (
                !Node.Cookie.class.isAssignableFrom(template.getType()) &&
                !Node.class.isAssignableFrom(template.getType())
            ) {
                return delegate.lookup (template);
            }
            
            
            final Lookup.Result d = delegate.lookup (template);
            
            class Wrap extends Lookup.Result {
                public void addLookupListener(LookupListener l) {
                    listeners.add (l);
                    d.addLookupListener (l);
                }
                
                public void removeLookupListener(LookupListener l) {
                    listeners.remove (l);
                    d.removeLookupListener (l);
                }
                public Collection allInstances() {
                    queries++;
                    return d.allInstances ();
                }
                public Collection allItems() {
                    queries++;
                    return d.allItems ();
                }
                public Set allClasses() {
                    queries++;
                    return d.allClasses ();
                }
            }
            
            return new Wrap ();
        }
        
    }
}
