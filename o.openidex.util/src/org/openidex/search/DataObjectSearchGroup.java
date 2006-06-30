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


package org.openidex.search;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.openide.ErrorManager;

import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.nodes.Node;

//import org.netbeans.api.project.FileOwnerQuery;


/**
 * Search group which perform search on data objects. It is a
 * convenience and the default implementation of <code>SearchGroup</code>
 * abstract class.
 *
 * @author  Peter Zavadsky
 * @author  Marian Petras
 * @see org.openidex.search.SearchGroup
 */
public class DataObjectSearchGroup extends SearchGroup {

    
    /**
     * {@inheritDoc} If the specified search type does not support searching
     * in <code>DataObject</code>s, the group is left unmodified, too.
     *
     * @see  SearchType#getSearchTypeClasses()
     */
    protected void add(SearchType searchType) {
        boolean ok = false;
        Class[] classes = searchType.getSearchTypeClasses();
        for (int i = 0; i < classes.length; i++) {
            if (classes[i] == DataObject.class) {
                ok = true;
                break;
            }
        }
        if (ok) {
            super.add(searchType);
        }
    }

    /**
     * Actual search implementation. Fires PROP_FOUND notifications.
     * Implements superclass abstract method.
     *
     * @throws RuntimeException annotated at USER level by reason (on low memory condition)
     */
    public void doSearch() {
        Node[] nodes = normalizeNodes(
                (Node[]) searchRoots.toArray(new Node[searchRoots.size()]));

        lowMemoryWarning = false;
        lowMemoryWarningCount = 0;
        assureMemory(REQUIRED_PER_ITERATION, true);

        for (int i = 0; i < nodes.length; i++) {
            Node node = nodes[i];
            SearchInfo info = Utils.getSearchInfo(node);
            if (info != null) {
                for (Iterator j = info.objectsToSearch(); j.hasNext(); ) {
                    if (stopped) return;
                    assureMemory(REQUIRED_PER_ITERATION, false);
                    processSearchObject(/*DataObject*/ j.next());
                }
            }
        }
    }


    private static boolean lowMemoryWarning = false;
    private static int lowMemoryWarningCount = 0;
    private static int MB = 1024 * 1024;
    private static int REQUIRED_PER_ITERATION = 2 * MB;
    private static int REQUIRED_PER_FULL_GC = 7 * MB;

    /**
     * throws RuntimeException if low memory condition happens
     * @param estimate etimated memory requirements before next check
     * @param tryGC on true use potentionally very slow test that is more accurate (cooperates with GC)
     */
    private static void assureMemory(int estimate, boolean tryGC) {
        Runtime rt = Runtime.getRuntime();
        long total = rt.totalMemory();
        long max = rt.maxMemory();  // XXX on some 1.4.1 returns heap&native instead of -Xmx
        long required = Math.max(total/13, estimate + REQUIRED_PER_FULL_GC);
        if (total ==  max && rt.freeMemory() < required) {
            // System.err.println("MEM " + max + " " +  total + " " + rt.freeMemory());
            if (tryGC) {
                try {
                    byte[] gcProvocation = new byte[(int)required];
                    gcProvocation[0] = 75;
                    gcProvocation = null;
                    return;
                } catch (OutOfMemoryError e) {
                    throwNoMemory();
                }
            } else {
                lowMemoryWarning = true;
            }
        } else if (lowMemoryWarning) {
            lowMemoryWarning = false;
            lowMemoryWarningCount ++;
        }
        // gc is getting into corner
        if (lowMemoryWarningCount > 7 || (total == max && rt.freeMemory() < REQUIRED_PER_FULL_GC)) {
            throwNoMemory();
        }

    }

    private static void throwNoMemory() {
        RuntimeException ex = new RuntimeException("Low memory condition"); // NOI18N
        String msg = NbBundle.getMessage(DataObjectSearchGroup.class, "EX_memory");
        ErrorManager.getDefault().annotate(ex, ErrorManager.USER, null, msg, null, null);
        throw ex;
    }

//    /**
//     * Gets data folder roots on which to search.
//     *
//     * @return  array of data folder roots
//     */
//    private DataObject.Container[] getContainers() {
//        List children = null;
//        Node[] nodes = normalizeNodes(
//                (Node[]) searchRoots.toArray(new Node[searchRoots.size()]));
//
//        for (int i = 0; i < nodes.length; i++) {
//            Node node = nodes[i];
//            if (node.getParentNode() == null) {
//
//                /* it should be the root of some project */
//            }
//        }
//
//        /* test whether to scan whole repository: */
//        if (nodes.length == 1) {
//            InstanceCookie ic = (InstanceCookie) nodes[0].getCookie(
//                                                          InstanceCookie.class);
//            try {
//                if (ic != null && Repository.class
//                                  .isAssignableFrom(ic.instanceClass())) {
//
//                    /* yes - scanning whole repository: */
//                    children = new ArrayList(10);
//                    Enumeration fss = Repository.getDefault().getFileSystems();
//                    while (fss.hasMoreElements()) {
//                        FileSystem fs = (FileSystem) fss.nextElement();
//                        if (fs.isValid() && !fs.isHidden()) {
//                            children.add(DataObject.find(fs.getRoot()));
//                        }
//                    }
//                }
//            } catch (IOException ioe) {
//                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ioe);
//                children = null;
//            } catch (ClassNotFoundException cnfe) {
//                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, cnfe);
//                children = null;
//            }
//        }
//        if (children == null) {
//            children = new ArrayList(nodes.length);
//            for (int i = 0; i < nodes.length; i++) {
//                DataObject.Container container = (DataObject.Container)
//                        nodes[i].getCookie(DataObject.Container.class);
//                if (container != null) {
//                    children.add(container);
//                }
//            }
//        }
//        return (DataObject.Container[])
//               children.toArray(new DataObject.Container[children.size()]);
//    }
//
//    /**
//     * Scans data folder recursively.
//     *
//     * @return <code>true</code> if scanned entire folder successfully
//     * or <code>false</code> if scanning was stopped. */
//    private boolean scanContainer(DataObject.Container container) {
//        DataObject[] children = container.getChildren();
//
//        for (int i = 0; i < children.length; i++) {
//
//            /* Test if the search was stopped. */
//            if (stopped) {
//                stopped = true;
//                return false;
//            }
//
//            DataObject.Container c = (DataObject.Container)
//                    children[i].getCookie(DataObject.Container.class);
//            if (c != null) {
//                if (!scanContainer(c)) {
//                    return false;
//                }
//            } else {
//                processSearchObject(children[i]);
//            }
//        }
//
//        return true;
//    }


    /** Gets node for found object. Implements superclass method.
     * @return node delegate for found data object or <code>null</code>
     * if the object is not of <code>DataObjectType</code> */
    public Node getNodeForFoundObject(Object object) {
        if (!(object instanceof DataObject)) {
            return null;
        }
        return ((DataObject) object).getNodeDelegate();
    }
    
    /**
     * Computes a subset of nodes (search origins) covering all specified nodes.
     * <p>
     * Search is performed on trees whose roots are the specified nodes.
     * If node A is a member of the tree determined by node B, then the A's tree
     * is a subtree of the B's tree. It means that it is redundant to extra
     * search the A's tree. This method computes a minimum set of nodes whose
     * trees cover all nodes' subtrees but does not contain any node not covered
     * by the original set of nodes.
     *
     * @param  nodes  roots of search trees
     * @return  subset of the original set of nodes
     *          (may be the same object as the parameter)
     */
    private static Node[] normalizeNodes(Node[] nodes) {
        
        /* No need to normalize: */
        if (nodes.length < 2) {
            return nodes;
        }
        
        /*
         * In the algorithm, we use two sets of nodes: "good nodes" and "bad
         * nodes". "Good nodes" are nodes not known to be covered by any 
         * search root. "Bad nodes" are nodes known to be covered by at least
         * one of the search roots.
         *
         * Since all the search roots are covered by themselves, they are all
         * put to "bad nodes" initially. To recognize whether a search root
         * is covered only by itself or whether it is covered by any other
         * search root, the former group of nodes has mapped value FALSE
         * and the later group of nodes has mapped value TRUE.
         *
         * Initially, all search roots have mapped value FALSE (not known to be
         * covered by any other search root) and as the procedure runs, some of
         * them may be remapped to value TRUE (known to be covered by at least
         * one other search root).
         *
         * The algorithm checks all search roots one by one. The ckeck starts
         * at a search root to be tested and continues up to its parents until
         * one of the following:
         *  a) the root of the whole tree of nodes is reached
         *     - i.e. the node being checked is not covered by any other
         *       search root
         *     - mark all the nodes in the path from the node being checked
         *       to the root as "good nodes", except the search root being
         *       checked
         *     - put the search root being checked into the resulting set
         *       of nodes
         *  b) a "good node" is reached
         *     - i.e. neither the good node nor any of the nodes on the path
         *       are covered by any other search root
         *     - mark all the nodes in the path from the node being checked
         *       to the root as "good nodes", except the search root being
         *       checked
         *     - put the search root being checked into the resulting set
         *       of nodes
         *  c) a "bad node" is reached (it may be either another search root
         *     or another "bad node")
         *     - i.e. we know that the reached node is covered by another search
         *       root or the reached node is another search root - in both cases
         *       the search root being checked is covered by another search root
         *     - mark all the nodes in the path from the node being checked
         *       to the root as "bad nodes"; the search root being checked
         *       will be remapped to value TRUE
         */
        
        HashMap badNodes = new HashMap(2 * nodes.length, 0.75f);
        HashMap goodNodes = new HashMap(2 * nodes.length, 0.75f);
        ArrayList path = new ArrayList(10);
        ArrayList result = new ArrayList(nodes.length);
        
        /* Put all search roots into "bad nodes": */
        for (int i = 0; i < nodes.length; i++) {
            badNodes.put(nodes[i], Boolean.FALSE);
        }
        
        main_cycle:
        for (int i = 0; i < nodes.length; i++) {
            path.clear();
            boolean isBad = false;
            for (Node n = nodes[i].getParentNode(); n != null;
                                                    n = n.getParentNode()) {
                if (badNodes.containsKey(n)) {
                    isBad = true;
                    break;
                }
                if (goodNodes.containsKey(n)) {
                    break;
                }
                path.add(n);
            }
            if (isBad) {
                badNodes.put(nodes[i], Boolean.TRUE);
                for (Iterator j = path.iterator(); j.hasNext(); ) {
                    badNodes.put(j.next(), Boolean.TRUE);
                }
            } else {
                for (Iterator j = path.iterator(); j.hasNext(); ) {
                    goodNodes.put(j.next(), Boolean.TRUE);
                }
                result.add(nodes[i]);
            }
        }
        return (Node[]) result.toArray(new Node[result.size()]);
    }

}
