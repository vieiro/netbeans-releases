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
package org.openide.nodes;

import java.util.List;


/** Event describing change in the list of a node's children.
*
* @author Jaroslav Tulach
*/
public final class NodeReorderEvent extends NodeEvent {
    static final long serialVersionUID = 4479234495493767448L;

    /** list of new nodes indexes on the original positions */
    private int[] newIndices;

    /** current snapshot */
    private final List<Node> currSnapshot;    

    /** Package private constructor to allow construction only
    * @param node the node that has changed
    * @param newIndices new indexes of the nodes
    */
    NodeReorderEvent(Node n, int[] newIndices) {
        super(n);
        this.newIndices = newIndices;
        this.currSnapshot = n.getChildren().entrySupport().createSnapshot(false);
    }
    
    /** Provides static and immmutable info about the number, and instances of
     * nodes available during the time the event was emited.
     * @return immutable and unmodifiable list of nodes
     * @since 7.7
     */
    public final List<Node> getSnapshot() {
        return currSnapshot;
    }
    
    /** Get the new position of the child that had been at a given position.
    * @param i the original position of the child
    * @return the new position of the child
    */
    public int newIndexOf(int i) {
        return newIndices[i];
    }

    /** Get the permutation used for reordering.
    * @return array of integers used for reordering
    */
    public int[] getPermutation() {
        return newIndices;
    }

    /** Get the number of children reordered.
     * @return size of the permutation array */
    public int getPermutationSize() {
        return newIndices.length;
    }

    /** Human presentable information about the event */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append("[node="); // NOI18N
        sb.append(getSource());
        sb.append(", permutation = ("); // NOI18N

        int[] perm = getPermutation();

        for (int i = 0; i < perm.length;) {
            sb.append(perm[i]);

            if (++i < perm.length) {
                sb.append(", "); // NOI18N
            }
        }

        sb.append(")]"); // NOI18N

        return sb.toString();
    }
}
