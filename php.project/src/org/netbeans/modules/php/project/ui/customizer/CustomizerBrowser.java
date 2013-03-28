/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.project.ui.customizer;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import org.netbeans.modules.web.browser.api.WebBrowser;
import org.netbeans.modules.web.browser.api.WebBrowserSupport;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

public final class CustomizerBrowser extends JPanel {

    private static final long serialVersionUID = -8744546565465456L;

    private final PhpProjectProperties uiProps;
    private final WebBrowserSupport.BrowserComboBoxModel browserModel;


    CustomizerBrowser(ProjectCustomizer.Category category, PhpProjectProperties uiProps) {
        assert category != null;
        assert uiProps != null;

        this.uiProps = uiProps;
        browserModel = WebBrowserSupport.createBrowserModel(uiProps.getBrowserId(), true);

        initComponents();
        init();
    }

    private void init() {
        // browser
        browserComboBox.setModel(browserModel);
        browserComboBox.setRenderer(WebBrowserSupport.createBrowserRenderer());
        browserComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                storeData();
                setReloadVisible();
            }
        });
        // reload
        reloadOnSaveCheckBox.setSelected(Boolean.valueOf(uiProps.getBrowserReloadOnSave()));
        reloadOnSaveCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                storeData();
            }
        });
        setReloadVisible();
    }

    void storeData() {
        uiProps.setBrowserId(browserModel.getSelectedBrowserId());
        uiProps.setBrowserReloadOnSave(String.valueOf(reloadOnSaveCheckBox.isSelected()));
    }

    void setReloadVisible() {
        reloadOnSaveCheckBox.setVisible(WebBrowserSupport.isIntegratedBrowser(browserModel.getSelectedBrowserId()));
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        browserLabel = new JLabel();
        browserComboBox = new JComboBox<WebBrowser>();
        reloadOnSaveCheckBox = new JCheckBox();

        Mnemonics.setLocalizedText(browserLabel, NbBundle.getMessage(CustomizerBrowser.class, "CustomizerBrowser.browserLabel.text")); // NOI18N

        Mnemonics.setLocalizedText(reloadOnSaveCheckBox, NbBundle.getMessage(CustomizerBrowser.class, "CustomizerBrowser.reloadOnSaveCheckBox.text")); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(browserLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(browserComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(reloadOnSaveCheckBox))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(browserLabel)
                    .addComponent(browserComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reloadOnSaveCheckBox))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JComboBox<WebBrowser> browserComboBox;
    private JLabel browserLabel;
    private JCheckBox reloadOnSaveCheckBox;
    // End of variables declaration//GEN-END:variables

}
