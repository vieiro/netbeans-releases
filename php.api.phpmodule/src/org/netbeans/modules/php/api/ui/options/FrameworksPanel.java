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
package org.netbeans.modules.php.api.ui.options;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 * Visual panel for Frameworks and Tools options.
 *
 * @author S. Aubrecht
 */
@org.netbeans.api.annotations.common.SuppressWarnings("SE_BAD_FIELD_STORE")
@OptionsPanelController.Keywords(
        keywords = "php",
        location = UiUtils.OPTIONS_PATH, tabTitle = "#LBL_FrameworksTabTitle")
final class FrameworksPanel extends JPanel {

    private final Lookup masterLookup;
    private final FrameworksOptionsPanelController masterController;
    private final List<AdvancedOption> options;
    private final Map<AdvancedOption, OptionsPanelController> option2controller;
    // @GuardedBy("EDT")
    private final Map<AdvancedOption, List<String>> option2keywords;
    private final PropertyChangeListener changeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            masterController.fireChange(evt);
        }
    };

    volatile int selectedCategoryIndex = 0;


    FrameworksPanel(FrameworksOptionsPanelController masterController, Lookup masterLookup, List<AdvancedOption> options) {
        assert EventQueue.isDispatchThread();
        this.masterLookup = masterLookup;
        this.masterController = masterController;
        sortOptions(options);
        this.options = new CopyOnWriteArrayList<>(options);
        option2controller = new ConcurrentHashMap<>(options.size() * 2);
        option2keywords = new HashMap<>(options.size() * 2);
        initComponents();
        init();
    }

    private void sortOptions(List<AdvancedOption> options) {
        final Collator collator = Collator.getInstance();
        Collections.sort(options, new Comparator<AdvancedOption>() {
            @Override
            public int compare(AdvancedOption o1, AdvancedOption o2) {
                return collator.compare(o1.getDisplayName(), o2.getDisplayName());
            }
        });
    }

    private void init() {
        assert EventQueue.isDispatchThread();
        DefaultListModel<String> model = new DefaultListModel<>();
        for (AdvancedOption ao : options) {
            model.addElement(ao.getDisplayName());
        }
        listCategories.setModel(model);
        listCategories.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                switchPanel();
            }
        });
        listCategories.setSelectedIndex(selectedCategoryIndex);
        listCategories.setCellRenderer(new NameListCellRenderer(listCategories.getCellRenderer()));
        listCategories.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedCategoryIndex = listCategories.getSelectedIndex();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollCategories = new JScrollPane();
        listCategories = new JList<String>();
        panelContent = new JPanel();

        listCategories.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollCategories.setViewportView(listCategories);

        panelContent.setLayout(new BorderLayout());

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(scrollCategories, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelContent, GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(scrollCategories, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addComponent(panelContent, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JList<String> listCategories;
    private JPanel panelContent;
    private JScrollPane scrollCategories;
    // End of variables declaration//GEN-END:variables

    @CheckForNull
    OptionsPanelController getSelectedController() {
        AdvancedOption option = getSelectedOption();
        if (option == null) {
            return null;
        }
        return option2controller.get(option);
    }

    @CheckForNull
    private AdvancedOption getSelectedOption() {
        if (selectedCategoryIndex < 0) {
            return null;
        }
        return options.get(selectedCategoryIndex);
    }

    void switchPanel() {
        assert EventQueue.isDispatchThread();
        panelContent.removeAll();

        AdvancedOption selOption = getSelectedOption();
        if (selOption != null) {
            OptionsPanelController controller = getController(selOption);
            controller.update();
        }
        OptionsPanelController selection = getSelectedController();
        if (selection != null) {
            panelContent.add(selection.getComponent(Lookup.EMPTY), BorderLayout.CENTER);
        }
        panelContent.invalidate();
        panelContent.revalidate();
        panelContent.repaint();
        // #240467
        masterController.fireChange(new PropertyChangeEvent(this, OptionsPanelController.PROP_HELP_CTX, null, null));
    }

    private OptionsPanelController getController(AdvancedOption option) {
        assert EventQueue.isDispatchThread();
        OptionsPanelController controller = option2controller.get(option);
        if (controller == null) {
            controller = option.create();
            // comply with javadoc
            controller.getComponent(masterLookup);
            option2controller.put(option, controller);
            controller.addPropertyChangeListener(changeListener);
        }
        return controller;
    }

    void handleSearch(List<String> matchedKeywords) {
        assert EventQueue.isDispatchThread();
        for (AdvancedOption option : options) {
            List<String> keywords = option2keywords.get(option);
            if (keywords == null) {
                keywords = loadKeywords(option);
                option2keywords.put(option, keywords);
            }
            for (String kw : matchedKeywords) {
                if (keywords.contains(kw)) {
                    setSelecteOption(option);
                    return;
                }
            }
        }
    }

    private List<String> loadKeywords(AdvancedOption option) {
        OptionsPanelController controller = getController(option);
        JComponent panel = controller.getComponent(masterLookup);
        String id = "OptionsDialog/Keywords/" + panel.getClass().getName(); // NOI18N
        List<String> res = new ArrayList<>(20);
        FileObject keywordsFO = FileUtil.getConfigFile(id);
        if (keywordsFO != null) {
            Enumeration<String> attributes = keywordsFO.getAttributes();
            while (attributes.hasMoreElements()) {
                String attribute = attributes.nextElement();
                if (attribute.startsWith("keywords")) { // NOI18N
                    String word = keywordsFO.getAttribute(attribute).toString();
                    res.add(word.toUpperCase());
                }
            }
        }
        return res;
    }

    void update() {
        for (OptionsPanelController controller : option2controller.values()) {
            controller.update();
        }
    }

    void applyChanges() {
        for (OptionsPanelController controller : option2controller.values()) {
            controller.applyChanges();
        }
    }

    void cancel() {
        for (OptionsPanelController controller : option2controller.values()) {
            controller.cancel();
        }
    }

    boolean isControllerValid() {
        for (OptionsPanelController controller : option2controller.values()) {
            if (!controller.isValid()) {
                return false;
            }
        }
        return true;
    }

    boolean isChanged() {
        for (OptionsPanelController controller : option2controller.values()) {
            if (controller.isChanged()) {
                return true;
            }
        }
        return false;
    }

    void setSelecteOption(AdvancedOption option) {
        int index = options.indexOf(option);
        if (index >= 0) {
            listCategories.setSelectedIndex(index);
        }
    }

    //~ Inner classes

    private static final class NameListCellRenderer implements ListCellRenderer<String> {

        private final ListCellRenderer<? super String> defaultCellRenderer;


        NameListCellRenderer(ListCellRenderer<? super String> defaultCellRenderer) {
            assert defaultCellRenderer != null;
            this.defaultCellRenderer = defaultCellRenderer;
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
            assert EventQueue.isDispatchThread();
            // is there a better way to simply add a padding?
            value += "   "; // NOI18N
            return defaultCellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }

    }

}
