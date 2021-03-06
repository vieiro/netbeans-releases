/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.debugger.common2.debugger.breakpoints.types;

import org.netbeans.modules.cnd.debugger.common2.debugger.EditorBridge;
import org.netbeans.modules.cnd.debugger.common2.debugger.breakpoints.BreakpointPanel;
import org.netbeans.modules.cnd.debugger.common2.debugger.breakpoints.NativeBreakpoint;
import org.netbeans.modules.cnd.debugger.common2.utils.IpeUtils;

class VariableBreakpointPanel extends BreakpointPanel {

    private VariableBreakpoint fb;
    
    @Override
    protected final void seed(NativeBreakpoint breakpoint) {
	seedCommonComponents(breakpoint);
	fb = (VariableBreakpoint) breakpoint;

	variableText.setText(fb.getVariable());
    }

    /*
     * Constructors
     */
    public VariableBreakpointPanel() {
	this (new VariableBreakpoint(NativeBreakpoint.TOPLEVEL), false);
    }

    public VariableBreakpointPanel(NativeBreakpoint b) {
	this ((VariableBreakpoint)b, true);
    }
    
    /** Creates new form VariableBreakpointPanel */
    public VariableBreakpointPanel(VariableBreakpoint breakpoint,
				    boolean customizing) {
	super(breakpoint, customizing);
	fb = breakpoint;

	initComponents();
	addCommonComponents(1);

	if (!customizing) {
	    String selection = EditorBridge.getCurrentSelection();
	    if (selection != null)
		breakpoint.setVariable(selection);
	}

	seed(breakpoint);

	// Arrange to revalidate on changes
	variableText.getDocument().addDocumentListener(this);
    }
    
    @Override
    public void setDescriptionEnabled(boolean enabled) {
	// variableLabel.setEnabled(false);
	variableText.setEnabled(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
	variableLabel = new javax.swing.JLabel();
	variableText = new javax.swing.JTextField();

	panel_settings.setLayout(new java.awt.GridBagLayout());
	java.awt.GridBagConstraints gridBagConstraints1;

	variableLabel.setText(Catalog.get("Variable"));	// NOI18N
	variableLabel.setDisplayedMnemonic(
	    Catalog.getMnemonic("MNEM_Variable"));	// NOI18N
	variableLabel.setLabelFor(variableText);
	gridBagConstraints1 = new java.awt.GridBagConstraints();
	gridBagConstraints1.ipadx = 5;
	gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
	panel_settings.add(variableLabel, gridBagConstraints1);

	gridBagConstraints1 = new java.awt.GridBagConstraints();
	gridBagConstraints1.gridwidth = 3;
	gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
	gridBagConstraints1.weightx = 1.0;
	panel_settings.add(variableText, gridBagConstraints1);

	// a11y
	variableText.getAccessibleContext().setAccessibleDescription(
	    Catalog.get("ACSD_Variable") // NOI18N
	);
    }

    private javax.swing.JLabel variableLabel;
    private javax.swing.JTextField variableText;

    @Override
    protected void assignProperties() {
	fb.setVariable(variableText.getText());
    }
    
    @Override
    protected boolean propertiesAreValid() {
	return !IpeUtils.isEmpty(variableText.getText());
    }
}
