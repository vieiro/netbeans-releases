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
package org.netbeans.jellytools;

import java.awt.Dialog;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JLabel;
import javax.swing.event.ChangeListener;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 * Test of org.netbeans.jellytools.WizardOperator.
 */
public class WizardOperatorTest extends JellyTestCase implements PropertyChangeListener {
    
    /** Title of test wizard. */
    private static final String TEST_WIZARD_TITLE = "Test Wizard";
    /** Caption of wizard panel. */
    private static final String TEST_WIZARD_PANEL0 = "First Panel";
    /** Caption of wizard panel. */
    private static final String TEST_WIZARD_PANEL1 = "Second Panel";
    /** Caption of wizard panel. */
    private static final String TEST_WIZARD_PANEL2 = "Third Panel";
    /** Text of JLabel in panels. */
    private static final String TEST_WIZARD_LABEL = "This is a test wizard panel ";
    
    /** constructor required by JUnit
     * @param testName method name to be used as testcase
     */
    public WizardOperatorTest(java.lang.String testName) {
        super(testName);
    }
    
    /** Redirect output to log files, wait before each test case and
     * show dialog to test. */
    protected void setUp() {
        System.out.println("### "+getName()+" ###");
        showTestWizard();
    }
    
    /** Dispose test dialog. */
    protected void tearDown() {
        dialog.dispose();
    }
    
    /** Test Next button getter. Go to next panel and check if second panel
     * is shown. */
    public void testBtNext() {
            WizardOperator wo = new WizardOperator(TEST_WIZARD_TITLE);
            wo.btNext().push();
            String text = new JLabelOperator(wo, TEST_WIZARD_LABEL).getText();
            assertEquals("Next not detected correctly.", TEST_WIZARD_LABEL+"1", text);
    }
    
    /** Test of next method. Go to next panel and check if second panel
     * is shown. */
    public void testNext() {
            WizardOperator wo = new WizardOperator(TEST_WIZARD_TITLE);
            wo.next();
            String text = new JLabelOperator(wo, TEST_WIZARD_LABEL).getText();
            assertEquals("Next not detected correctly.", TEST_WIZARD_LABEL+"1", text);
    }
    
    /** Test Back button getter. Go to next panel, then back and check if
     * first panel is shown. */
    public void testBtBack() {
            WizardOperator wo = new WizardOperator(TEST_WIZARD_TITLE);
            wo.btNext().push();
            wo.btBack().push();
            String text = new JLabelOperator(wo, TEST_WIZARD_LABEL).getText();
            assertEquals("Back not detected correctly.", TEST_WIZARD_LABEL+"0", text);
    }
    
    /** Test of back method. Go to next panel, then back and check if
     * first panel is shown. */
    public void testBack() {
            WizardOperator wo = new WizardOperator(TEST_WIZARD_TITLE);
            wo.next();
            wo.back();
            String text = new JLabelOperator(wo, TEST_WIZARD_LABEL).getText();
            assertEquals("Back not detected correctly.", TEST_WIZARD_LABEL+"0", text);
    }
    
    /** Test Finish button getter. Go to the last panel and push Finish button.
     * Check if returned value correspond to FINISH_OPTION. */
    public void testBtFinish() {
            WizardOperator wo = new WizardOperator(TEST_WIZARD_TITLE);
            wo.btNext().push();
            wo.btNext().push();
            wo.btFinish().push();
            assertEquals("Finish not detected correctly.", wd.FINISH_OPTION, wd.getValue());
    }
    
    /** Test of finish method. Go to the last panel and push Finish button.
     * Check if returned value correspond to FINISH_OPTION. */
    public void testFinish() {
            WizardOperator wo = new WizardOperator(TEST_WIZARD_TITLE);
            wo.next();
            wo.next();
            wo.finish();
            assertEquals("Finish not detected correctly.", wd.FINISH_OPTION, wd.getValue());
    }
    
    /** Test of lstSteps method, of class org.netbeans.jellytools.WizardOperator. */
    public void testLstSteps() {
            WizardOperator wo = new WizardOperator(TEST_WIZARD_TITLE);
            wo.btNext().push();
            String value0 = wo.lstSteps().getModel().getElementAt(0).toString();
            assertEquals("List of steps not detected correctly", TEST_WIZARD_PANEL0, value0);
    }
    
    /** Test of stepsGetSelectedIndex method. On first panel it should be 0,
     * then go to next panel where it should be 1. */
    public void testStepsGetSelectedIndex() {
            WizardOperator wo = new WizardOperator(TEST_WIZARD_TITLE);
            int index = wo.stepsGetSelectedIndex();
            assertEquals("Selected index not detected correctly", 0, index);
            wo.next();
            index = wo.stepsGetSelectedIndex();
            assertEquals("Selected index not detected correctly", 1, index);
    }
    
    /** Test of stepsGetSelectedValue method. On first panel it should be "First Panel",
     * then go to next panel where it should be "Second Panel". */
    public void testStepsGetSelectedValue() {
            WizardOperator wo = new WizardOperator(TEST_WIZARD_TITLE);
            String selectedValue = wo.stepsGetSelectedValue();
            assertEquals("Selected index not detected correctly", TEST_WIZARD_PANEL0, selectedValue);
            wo.next();
            selectedValue = wo.stepsGetSelectedValue();
            assertEquals("Selected value not detected correctly", TEST_WIZARD_PANEL1, selectedValue);
    }
    
    /** Test of checkPanel method. Check first panel, then go to next panel
     * and check again. Then check negative case. */
    public void tstCheckPanel() {
            WizardOperator wo = new WizardOperator(TEST_WIZARD_TITLE);
            try {
                wo.checkPanel(TEST_WIZARD_PANEL0);
            } catch (JemmyException e) {
                fail("checkPanel() method doesn't work properly.");
            }
            wo.next();
            try {
                wo.checkPanel(TEST_WIZARD_PANEL1);
            } catch (JemmyException e) {
                fail("checkPanel() method doesn't work properly.");
            }
            // negative case
            try {
                wo.checkPanel(TEST_WIZARD_PANEL0);
                fail("checkPanel() should throw exception if check fails.");
            } catch (JemmyException e) {
                // right, it should fail
            }
    }
    
    /** Constants to create a wizard. */
    private static final String PROP_AUTO_WIZARD_STYLE = WizardDescriptor.PROP_AUTO_WIZARD_STYLE; // NOI18N
    private static final String PROP_CONTENT_DISPLAYED = WizardDescriptor.PROP_CONTENT_DISPLAYED; // NOI18N
    private static final String PROP_CONTENT_NUMBERED = WizardDescriptor.PROP_CONTENT_NUMBERED; // NOI18N
    private static final String PROP_CONTENT_SELECTED_INDEX = WizardDescriptor.PROP_CONTENT_SELECTED_INDEX; // NOI18N
    private static final String PROP_CONTENT_DATA = WizardDescriptor.PROP_CONTENT_DATA; // NOI18N
    
    /** Instance of WizardDescriptor used to test. */
    private WizardDescriptor wd;
    /** Instance of Dialog which hosts test wizard. */
    private Dialog dialog;
    
    /** Opens test wizard with 3 steps. */
    private void showTestWizard() {
        TestPanel panel0 = new TestPanel(0);
        panel0.addPropertyChangeListener(this);
        TestPanel panel1 = new TestPanel(1);
        panel1.addPropertyChangeListener(this);
        TestPanel panel2 = new TestPanel(2);
        panel2.addPropertyChangeListener(this);
        WizardDescriptor.Panel[] panels = {
            panel0,
            panel1,
            panel2
        };
        WizardDescriptor.ArrayIterator iterator = new WizardDescriptor.ArrayIterator(panels);
        wd = new WizardDescriptor(iterator);
        wd.putProperty(PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
        wd.putProperty(PROP_CONTENT_DISPLAYED, Boolean.TRUE);
        wd.putProperty(PROP_CONTENT_NUMBERED, Boolean.TRUE);
        wd.putProperty(PROP_CONTENT_DATA, new String[] {
            TEST_WIZARD_PANEL0,
            TEST_WIZARD_PANEL1,
            TEST_WIZARD_PANEL2
        });
        wd.setTitle(TEST_WIZARD_TITLE);
        wd.setModal(false);
        dialog = DialogDisplayer.getDefault().createDialog(wd);
        dialog.setVisible(true);
    }
    
    /** Used to change selected item in list of steps. */
    public void propertyChange(PropertyChangeEvent evt) {
        if (PROP_CONTENT_SELECTED_INDEX.equals(evt.getPropertyName())) {
            wd.putProperty(PROP_CONTENT_SELECTED_INDEX, evt.getNewValue());
        }
    }
    
    /** Test panel - one wizard step. */
    private class TestPanel implements WizardDescriptor.Panel {
        
        PropertyChangeSupport changeSupport;
        int index;
        
        public TestPanel(int index) {
            super();
            this.index = index;
        }
        
        public java.awt.Component getComponent() {
            if(changeSupport == null) {
                changeSupport = new PropertyChangeSupport(this);
            }
            changeSupport.firePropertyChange(PROP_CONTENT_SELECTED_INDEX, null, new Integer(index));
            JLabel label = new JLabel(TEST_WIZARD_LABEL+index);
            label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            return label;
        }
        
        public HelpCtx getHelp() {
            return new HelpCtx("");
        }
        
        /** Test whether the panel is finished and it is safe to proceed to the next one.
         * If the panel is valid, the "Next" (or "Finish") button will be enabled.
         */
        public boolean isValid() {
            return true;
        }
        
        public void storeSettings(Object settings) {}
        
        public void readSettings(Object settings) {}
        
        public void addChangeListener(ChangeListener listener) {}
        
        public void removeChangeListener(ChangeListener listener) {}
        
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            if(changeSupport == null) {
                changeSupport = new PropertyChangeSupport(this);
            }
            changeSupport.addPropertyChangeListener(listener);
        }
        
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            if (changeSupport != null) {
                changeSupport.removePropertyChangeListener(listener);
            }
        }
    }
    
    
}
