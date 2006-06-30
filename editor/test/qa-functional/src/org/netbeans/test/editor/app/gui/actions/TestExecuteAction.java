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
package org.netbeans.test.editor.app.gui.actions;

import org.netbeans.test.editor.app.core.cookies.PerformCookie;
import org.netbeans.test.editor.app.gui.tree.TestNodeDelegate;

/**
 *
 * @author  ehucka
 * @version
 */
public class TestExecuteAction extends TreeNodeAction {

    /** Creates new TestExecuteAction */
    public TestExecuteAction() {
    }

    public String getHelpCtx() {
        return "Perform action or all subactions";
    }

    public String getName() {
        return "Execute";
    }

    public boolean enable(TestNodeDelegate[] activatedNodes) {
        if (activatedNodes.length == 0) {
            return false;
        } else {
            boolean ret=true;
            for (int i=0;i < activatedNodes.length;i++) {
                PerformCookie pc = (PerformCookie) (activatedNodes[i].getTestNode().getCookie(PerformCookie.class));
                
                if (pc != null && !pc.isPerforming()) {
                    ret=true;
                } else {
                    return false;
                }
            }
            return ret;
        }
    }
    
    public void performAction(TestNodeDelegate[] activatedNodes) {
	for(int i=0;i < activatedNodes.length;i++) {
	    PerformCookie pc = (PerformCookie) activatedNodes[i].getTestNode().getCookie(PerformCookie.class);
	    
	    if (pc != null) {
		pc.perform();
	    }
	}
    }
    
}
