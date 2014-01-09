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

package org.netbeans.core.windows.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.core.WindowSystem;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

@OptionsPanelController.SubRegistration(
    displayName="#Tabs_DisplayName",
    id="DocumentTabs",
    keywords="#KW_TabsOptions",
    keywordsCategory="Appearance/Tabs",
    location = "Appearance"
)
@org.openide.util.NbBundle.Messages({"KW_TabsOptions=editor tabs, multi-row", "Tabs_DisplayName=Document Tabs"})
public class TabsOptionsPanelController extends OptionsPanelController {

    private TabsPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;
    private boolean changedInnerTabsPanel;

    @Override
    public void update() {
        getPanel().load();
        changed = false;
        changedInnerTabsPanel = false;
    }

    @Override
    public void applyChanges() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                boolean refreshWinsys = getPanel().store();
                changed = false;
                changedInnerTabsPanel = false;
                if (refreshWinsys) {
                    WindowSystem ws = Lookup.getDefault().lookup(WindowSystem.class);
                    ws.hide();
                    ws.show();
                }
            }
        });
    }

    @Override
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    @Override
    public boolean isValid() {
        return getPanel().valid();
    }

    @Override
    public boolean isChanged() {
        return changed || changedInnerTabsPanel;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx( "org.netbeans.core.windows.options.TabsOptionsPanelController" ); //NOI18N
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    protected TabsPanel getPanel() {
        if (panel == null) {
            panel = new TabsPanel(this);
        }
        return panel;
    }

    /**
     *
     * @param isChanged true if global options under "Document Tabs" were modified, null otherwise
     * @param isChangedInnerTabsPanel true if tab related options under "Document Tabs" were modified, null otherwise
     */
    protected void changed(Object isChanged, Object isChangedInnerTabsPanel) {
        if (!changed) {
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        if(isChanged != null) {
            changed = (boolean) isChanged;
        }
        if(isChangedInnerTabsPanel != null) {
            changedInnerTabsPanel = (boolean) isChangedInnerTabsPanel;
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
}
