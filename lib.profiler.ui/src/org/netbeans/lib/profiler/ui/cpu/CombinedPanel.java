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

package org.netbeans.lib.profiler.ui.cpu;

import java.awt.Color;
import org.netbeans.lib.profiler.results.ExportDataDumper;
import org.netbeans.lib.profiler.ui.UIUtils;
import java.awt.Component;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;


/**
 *
 * @author Jaroslav Bachorik
 */
public class CombinedPanel extends JSplitPane implements ScreenshotProvider {
    //~ Constructors -------------------------------------------------------------------------------------------------------------

    public CombinedPanel(int splitOrientation, Component component1, Component component2) {
        super(splitOrientation, component1, component2);
        tweakUI();
    }

    public void exportData(int exportedFileType, ExportDataDumper eDD, String viewName) {
        ((CCTDisplay)leftComponent).exportData(exportedFileType, eDD, true, viewName);
        ((SnapshotFlatProfilePanel)rightComponent).exportData(exportedFileType, eDD, true, viewName);
    }

    //~ Methods ------------------------------------------------------------------------------------------------------------------

    public BufferedImage getCurrentViewScreenshot(boolean onlyVisible) {
        return UIUtils.createScreenshot(this);
    }

    public String getDefaultViewName() {
        return "cpu-combined"; // NOI18N
    }

    public boolean fitsVisibleArea() {
        return true;
    }
    
    private void tweakUI() {
        setBorder(null);
        setDividerSize(5);

        if (!(getUI() instanceof BasicSplitPaneUI)) return;

        BasicSplitPaneDivider divider = ((BasicSplitPaneUI)getUI()).getDivider();
        if (divider != null) {
            Color c = UIUtils.isNimbus() ? UIUtils.getDisabledLineColor() :
                    new JSeparator().getForeground();
            divider.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, c));
        }
    }
}
