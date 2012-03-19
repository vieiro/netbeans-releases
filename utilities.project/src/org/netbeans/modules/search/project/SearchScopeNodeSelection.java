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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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

package org.netbeans.modules.search.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.api.search.provider.SearchInfo;
import org.netbeans.api.search.provider.SearchInfoUtils;
import org.netbeans.spi.search.SearchScopeDefinition;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.windows.TopComponent;


/**
 * Defines search scope across selected nodes.
 *
 * @author  Marian Petras
 */
final class SearchScopeNodeSelection extends SearchScopeDefinition
                                     implements LookupListener {
    
    private final Lookup.Result<Node> lookupResult;
    private LookupListener lookupListener;

    public SearchScopeNodeSelection() {
        Lookup lookup = Utilities.actionsGlobalContext();
        lookupResult = lookup.lookupResult(Node.class);
        lookupListener = WeakListeners.create(LookupListener.class,
                this,
                lookupResult);
        lookupResult.addLookupListener(lookupListener);
    }

    @Override
    public String getTypeId() {
        return "node selection";                                        //NOI18N
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(getClass(),
                                   "SearchScopeNameSelectedNodes");     //NOI18N
    }

    @Override
    public String getAdditionalInfo() {
        final Node[] nodes = getNodes();

        if ((nodes == null) || (nodes.length == 0)) {
            return null;
        }

        return (nodes.length == 1)
               ? nodes[0].getDisplayName()
               : NbBundle.getMessage(getClass(),
                                     "SearchScopeSelectionAddInfo",     //NOI18N
                                     nodes.length);
    }

    @Override
    public boolean isApplicable() {
        return checkIsApplicable(getNodes());
    }

    private Node[] getNodes() {
        Collection<? extends Node> lookupAll =
                Utilities.actionsGlobalContext().lookupAll(Node.class);
        Node[] nodes = new Node[lookupAll.size()];
        int i = 0;
        for (Node n : lookupAll) {
            nodes[i] = n;
            i++;
        }
        return nodes;
    }

    /**
     * Decides whether searching should be enabled with respect to a set
     * of selected nodes.
     * Searching is enabled if searching instructions
     * (<code>SearchInfo</code> object) are available for all selected nodes.
     *
     * @param  nodes  selected nodes
     * @return  <code>true</code> if searching the selected nodes should be
     *          enabled; <code>false</code> otherwise
     * @see  SearchInfo
     */
    private static boolean checkIsApplicable(Node[] nodes) {
        if (nodes == null || nodes.length == 0) {
            return false;
        }

        for (Node node : nodes) {
            if (!canSearch(node)) {
                return false;
            }
        }
        return true;
    }

    /**
     */
    private static boolean canSearch(Node node) {

        SearchInfo si = SearchInfoHelper.getSearchInfoForNode(node);
        return si != null && si.canSearch();
    }

    @Override
    public SearchInfo getSearchInfo() {
        return getSearchInfo(TopComponent.getRegistry().getActivatedNodes());
    }

    private SearchInfo getSearchInfo(Node[] nodes) {
        if ((nodes == null) || (nodes.length == 0)) {
            return SearchInfoUtils.createEmptySearchInfo();
        }
        
        nodes = normalizeNodes(nodes);
        if (nodes.length == 1) {
            SearchInfo searchInfo = getSearchInfo(nodes[0]);
            return (searchInfo != null)
                    ? searchInfo
                    : SearchInfoUtils.createEmptySearchInfo();
        }
        
        List<SearchInfo> searchInfos = new ArrayList<SearchInfo>(nodes.length);
        for (Node node : nodes) {
            SearchInfo searchInfo = getSearchInfo(node);
            if (searchInfo != null) {
                searchInfos.add(searchInfo);
            }
        }
        
        if (searchInfos.isEmpty()) {
            return SearchInfoUtils.createEmptySearchInfo();
        }
        int searchInfoCount = searchInfos.size();
        if (searchInfoCount == 1) {
            return searchInfos.get(0);
        } else {
            return SearchInfoUtils.createCompoundSearchInfo(
                        searchInfos.toArray(new SearchInfo[searchInfoCount]));
        }
    }

    /**
     */
    private static SearchInfo getSearchInfo(Node node) {
        return SearchInfoHelper.getSearchInfoForNode(node);
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
        
        Map<Node, Boolean> badNodes = new HashMap<Node, Boolean>(2 * nodes.length, 0.75f);
        Map<Node, Boolean> goodNodes = new HashMap<Node, Boolean>(2 * nodes.length, 0.75f);
        List<Node> path = new ArrayList<Node>(10);
        List<Node> result = new ArrayList<Node>(nodes.length);
        
        /* Put all search roots into "bad nodes": */
        for (Node node : nodes) {
            badNodes.put(node, Boolean.FALSE);
        }
        
        main_cycle:
        for (Node node : nodes) {
            path.clear();
            boolean isBad = false;
            for (Node n = node.getParentNode(); n != null;
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
                badNodes.put(node, Boolean.TRUE);
		for (Node n : path) {
                    badNodes.put(n, Boolean.TRUE);
		}
            } else {
                for (Node n : path) {
                    goodNodes.put(n, Boolean.TRUE);
                }
                result.add(node);
            }
        }
        return result.toArray(new Node[result.size()]);
    }

    @Override
    public void clean() {
        if (lookupResult != null && lookupListener != null) {
            lookupResult.removeLookupListener(lookupListener);
        }
    }

    @Override
    public int getPriority() {
        return 400;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        notifyListeners();
    }
}
