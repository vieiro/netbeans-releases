/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.insane.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CancellationException;
import javax.swing.DefaultBoundedRangeModel;
import junit.framework.TestCase;
import org.netbeans.insane.live.CancelException;

/**
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
public class LiveEngineTest extends TestCase {
    
    public LiveEngineTest(String testName) {
        super(testName);
    }

    static LinkedList<Object> list = new LinkedList<Object>();
    static Object last;

    @Override
    protected void setUp() {
        for (int i = 0; i < 100000; i++) {
            Object o = new Object();
            if (i % 2 == 50000) {
                last = o;
            }
            list.add(o);
        }
    }

    public void testIsKnown() {
        class M extends DefaultBoundedRangeModel {

            @Override
            public void setValue(int n) {
                super.setValue(n);
                if (n > 10) {
                    super.setValue(10);
                    throw new CancelException();
                }
            }

        }
        M model = new M();
        LiveEngine instance = new LiveEngine(model);

        Map<?,?> path = instance.trace(Collections.singleton(last), Collections.<Object>singleton(list));

        assertEquals("Model stops at 10", 10, model.getValue());
        assertEquals("No path found", null, path);
    }
}
