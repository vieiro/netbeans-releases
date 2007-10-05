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
 * Contributor(s): Alexandre Iline.
 *
 * The Original Software is the Jemmy library.
 * The Initial Developer of the Original Software is Alexandre Iline.
 * All Rights Reserved.
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
 *
 *
 *
 * $Id$ $Revision$ $Date$
 *
 */

package org.netbeans.jemmy.drivers.focus;

import java.awt.event.FocusEvent;

import org.netbeans.jemmy.QueueTool;

import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.FocusDriver;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.MouseDriver;

import org.netbeans.jemmy.drivers.input.EventDriver;

import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JScrollBarOperator;
import org.netbeans.jemmy.operators.JSliderOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTextComponentOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.operators.ListOperator;
import org.netbeans.jemmy.operators.ScrollbarOperator;
import org.netbeans.jemmy.operators.TextAreaOperator;
import org.netbeans.jemmy.operators.TextComponentOperator;
import org.netbeans.jemmy.operators.TextFieldOperator;

public class MouseFocusDriver extends LightSupportiveDriver implements FocusDriver {
    private QueueTool queueTool;
    public MouseFocusDriver() {
	super(new String[] {
		"org.netbeans.jemmy.operators.JListOperator", 
		"org.netbeans.jemmy.operators.JScrollBarOperator", 
		"org.netbeans.jemmy.operators.JSliderOperator", 
		"org.netbeans.jemmy.operators.JTableOperator", 
		"org.netbeans.jemmy.operators.JTextComponentOperator", 
		"org.netbeans.jemmy.operators.JTreeOperator", 
		"org.netbeans.jemmy.operators.ListOperator", 
		"org.netbeans.jemmy.operators.ScrollbarOperator", 
		"org.netbeans.jemmy.operators.TextAreaOperator", 
		"org.netbeans.jemmy.operators.TextComponentOperator", 
		"org.netbeans.jemmy.operators.TextFieldOperator"});
        queueTool = new QueueTool();
    }
    public void giveFocus(final ComponentOperator oper) {
	if(!oper.hasFocus()) {
            queueTool.invokeSmoothly(new QueueTool.QueueAction("Mouse click to get focus") {
                    public Object launch() {
                        DriverManager.getMouseDriver(oper).
                            clickMouse(oper, oper.getCenterXForClick(), oper.getCenterYForClick(),
                                       1, oper.getDefaultMouseButton(), 0, 
                                       oper.getTimeouts().create("ComponentOperator.MouseClickTimeout"));
                        return(null);
                    }
                });
            oper.waitHasFocus();
	}
    }
}
