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

package org.netbeans.modules.ruby.platform.gems;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.api.ruby.platform.RubyInstallation;
import org.netbeans.api.ruby.platform.RubyPlatform;
import org.netbeans.api.ruby.platform.RubyPlatformManager;
import org.netbeans.modules.ruby.platform.PlatformComponentFactory;
import org.netbeans.modules.ruby.platform.RubyPlatformCustomizer;
import org.openide.DialogDescriptor;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * 
 * @todo Use a table instead of a list for the gem lists, use checkboxes to choose
 *   items to be uninstalled, and show the installation date (based
 *   on file timestamps)
 * @todo Find a way to execute both gem commands (local and remote listing) in the 
 *   same Ruby VM so it's faster to perform updates. Does
 *      gem list --both 
 *   work?
 * @todo Split error output
 *
 * @author  Tor Norbye
 */
public class GemPanel extends JPanel implements Runnable {
    
    private static final int UPDATED_TAB_INDEX  = 0;
    private static final int INSTALLED_TAB_INDEX  = 1;
    private static final int NEW_TAB_INDEX  = 2;
    
    private GemManager gemManager;
    
    private List<Gem> installedGems;
    private List<Gem> availableGems;
    private List<Gem> newGems;
    private List<Gem> updatedGems;
    private boolean installedModified;
    private boolean gemsModified;
    private boolean fetchingLocal;
    private boolean fetchingRemote;
    private List<String> remoteFailure;
    
    private static boolean useCached;
    
    public GemPanel(String availableFilter) {
        initComponents();
       
        this.gemManager = RubyPlatformManager.getDefaultPlatform().getGemManager();
        
        installedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        installedList.getSelectionModel().addListSelectionListener(new MyListSelectionListener(installedList, installedDesc, uninstallButton));

        newList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        newList.getSelectionModel().addListSelectionListener(new MyListSelectionListener(newList, newDesc, installButton));

        updatedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        updatedList.getSelectionModel().addListSelectionListener(new MyListSelectionListener(updatedList, updatedDesc, updateButton));

        installedModified = true;

        if (availableFilter != null) {
            searchNewText.setText(availableFilter);
            gemsTab.setSelectedIndex(NEW_TAB_INDEX);
        }

        platforms.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    GemPanel.this.gemManager = ((RubyPlatform) platforms.getSelectedItem()).getGemManager();
                    useCached = false;
                    RequestProcessor.getDefault().post(GemPanel.this, 300);
                }
            }
        });
        RequestProcessor.getDefault().post(this, 300);
    }
    
    public void run() {
        // This will also update the New and Installed lists because Update depends on these
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                refreshUpdated();
            }
        });
    }
    
    private void updateGemDescription(JTextPane pane, Gem gem) {
        assert SwingUtilities.isEventDispatchThread();

        if (gem == null) {
            pane.setText("");
            return;
        }

        String htmlMimeType = "text/html"; // NOI18N
        pane.setContentType(htmlMimeType);

        StringBuilder sb = new StringBuilder();
        sb.append("<html>"); // NOI18N
        sb.append("<h2>"); // NOI18N
        sb.append(gem.getName());
        sb.append("</h2>\n"); // NOI18N

        if (gem.getInstalledVersions() != null && gem.getAvailableVersions() != null) {
            // It's an update gem
            sb.append("<h3>"); // NOI18N
            sb.append(NbBundle.getMessage(GemPanel.class, "InstalledVersion"));
            sb.append("</h3>"); // NOI18N
            sb.append(gem.getInstalledVersions());

            sb.append("<h3>"); // NOI18N
            sb.append(NbBundle.getMessage(GemPanel.class, "AvailableVersion"));
            sb.append("</h3>"); // NOI18N
            sb.append(gem.getAvailableVersions());
            sb.append("<br>"); // NOI18N
        } else {
            sb.append("<h3>"); // NOI18N
            String version = gem.getInstalledVersions();
            if (version == null) {
                version = gem.getAvailableVersions();
            }
            if (version.indexOf(',') == -1) {
            // TODO I18N
                sb.append(NbBundle.getMessage(GemPanel.class, "Version"));
            } else {
                sb.append(NbBundle.getMessage(GemPanel.class, "Versions"));
            }
            sb.append("</h3>"); // NOI18N
            sb.append(version);
        }

        if (gem.getDescription() != null) {
            sb.append("<h3>"); // NOI18N
            sb.append(NbBundle.getMessage(GemPanel.class, "Description"));
            sb.append("</h3>"); // NOI18N
            sb.append(gem.getDescription());
        }
        
        sb.append("</html>"); // NOI18N
        
        pane.setText(sb.toString());
    }

    /** Called when installedList or newList is refreshed; recompute the updated list
     * @return True iff we're done with the updates
     */
    private synchronized boolean updateGems() {
        assert SwingUtilities.isEventDispatchThread();

        if (!(fetchingRemote || fetchingLocal)) {
            updatedProgress.setVisible(false);
            updatedProgressLabel.setVisible(false);
        }
        if (!fetchingRemote) {
            newProgress.setVisible(false);
            newProgressLabel.setVisible(false);
        }
        if (!fetchingLocal) {
            installedProgress.setVisible(false);
            installedProgressLabel.setVisible(false);
        }
    
        if (installedGems != null && availableGems != null) {
            Map<String,Gem> nameMap = new HashMap<String,Gem>();
            for (Gem gem : installedGems) {
                nameMap.put(gem.getName(), gem);
            }
            Set<String> installedNames = nameMap.keySet();
            
            updatedGems = new ArrayList<Gem>();
            newGems = new ArrayList<Gem>();
            for (Gem gem : availableGems) {
                if (installedNames.contains(gem.getName())) {
                    // We have this gem; let's see if we have the latest version
                    String available = gem.getAvailableVersions();
                    Gem installedGem = nameMap.get(gem.getName());
                    String installed = installedGem.getInstalledVersions(); 
                    // Gem always lists the most recent version first...
                    int firstVer = available.indexOf(',');
                    if (firstVer == -1) {
                        firstVer = available.indexOf(')');
                        if (firstVer == -1) {
                            firstVer = available.length();
                        }
                    }
                    if (!installed.regionMatches(0, available, 0, firstVer)) {
                        Gem update = new Gem(gem.getName(), installed, available.substring(0, firstVer));
                        update.setDescription(installedGem.getDescription());
                        updatedGems.add(update);
                    }
                } else {
                    newGems.add(gem);
                }
            }
            
            updateList(NEW_TAB_INDEX, true);
            updateList(UPDATED_TAB_INDEX, true);
        }
        
        return !(fetchingRemote || fetchingLocal);
    }
    
    private void updateList(int tab, boolean showCount) {
        assert SwingUtilities.isEventDispatchThread();

        Pattern pattern = null;
        String filter = getGemFilter(tab);
        String lcFilter = null;
        if ((filter != null) && (filter.indexOf('*') != -1 || filter.indexOf('^') != -1 || filter.indexOf('$') != -1)) {
            try {
                pattern = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);
            } catch (PatternSyntaxException pse) {
                // Don't treat the filter as a regexp
            }
        } else if (filter != null) {
            lcFilter = filter.toLowerCase();
        }
        List<Gem> gems;
        JList list;
        
        switch (tab) {
        case NEW_TAB_INDEX:
            gems = newGems;
            list = newList;
            break;
        case UPDATED_TAB_INDEX:
            gems = updatedGems;
            list = updatedList;
            break;
        case INSTALLED_TAB_INDEX:
            gems = installedGems;
            list = installedList;
            break;
        default:
            throw new IllegalArgumentException();
        }
        
        if (gems == null) {
            // attempting to filter before the list has been fetched - ignore
            return;
        }
        
        DefaultListModel model = new DefaultListModel();
        for (Gem gem : gems) {
            if (filter == null || filter.length() == 0) {
                model.addElement(gem);
            } else if (pattern == null) {
                if (lcFilter != null) {
                    String lcName = gem.getName().toLowerCase();
                    if (lcName.indexOf(lcFilter) != -1) {
                        model.addElement(gem);
                    } else if (gem.getDescription() != null) {
                        String lcDesc =gem.getDescription().toLowerCase();
                        if (lcDesc.indexOf(lcFilter) != -1) {
                            model.addElement(gem);
                        }
                    }
                } else {
                    model.addElement(gem);
                }
            } else if (pattern.matcher(gem.getName()).find() || 
                    (gem.getDescription() != null && pattern.matcher(gem.getDescription()).find())) {
                model.addElement(gem);
            }
        }
        if (remoteFailure != null && (tab == UPDATED_TAB_INDEX || tab == NEW_TAB_INDEX)) {
            model.addElement(NbBundle.getMessage(GemPanel.class, "NoNetwork"));
            for (String line : remoteFailure) {
                model.addElement("<html><span color=\"red\">" + line + "</span></html>"); // NOI18N
            }
        }
        list.clearSelection();
        list.setModel(model);
        list.invalidate();
        list.repaint();
        // This sometimes gives NPEs within setSelectedIndex...
        //        if (gems.size() > 0) {
        //            list.setSelectedIndex(0);
        //        }

        if (showCount) {
            String tabTitle = gemsTab.getTitleAt(tab);
            String originalTabTitle = tabTitle;
            int index = tabTitle.lastIndexOf('(');
            if (index != -1) {
                tabTitle = tabTitle.substring(0, index);
            }
            String count;
            if (model.size() < gems.size()) {
                count = model.size() + "/" + gems.size();
            } else {
                count = Integer.toString(gems.size());
            }
            tabTitle = tabTitle + "(" + count + ")";
            if (!tabTitle.equals(originalTabTitle)) {
                gemsTab.setTitleAt(tab, tabTitle);
            }
        }
    }
    
    /** Return whether any gems were modified - roots should be recomputed after panel is taken down */
    public boolean isModified() {
        return gemsModified;
    }

    private synchronized void refreshInstalled(boolean fetch) {
        assert SwingUtilities.isEventDispatchThread();

        if (installedList.getSelectedIndex() != -1) {
            updateGemDescription(installedDesc, null);
        }
        installedProgress.setVisible(true);
        installedProgressLabel.setVisible(true);
        fetchingLocal = true;
        if (fetch) {
            refreshGemList(INSTALLED_TAB_INDEX);
        }
        installedModified = false;
    }
    
    private synchronized void refreshNew(boolean fetch) {
        assert SwingUtilities.isEventDispatchThread();

        if (newList.getSelectedIndex() != -1) {
            updateGemDescription(newDesc, null);
        }
        newProgress.setVisible(true);
        newProgressLabel.setVisible(true);
        fetchingRemote = true;
        if (fetch) {
            refreshGemList(NEW_TAB_INDEX);
        }
    }

    private void refreshUpdated() {
        assert SwingUtilities.isEventDispatchThread();
        gemHomeValue.setText(gemManager.getGemDir());

        if (updatedList.getSelectedIndex() != -1) {
            updateGemDescription(updatedDesc, null);
        }
        updatedProgress.setVisible(true);
        updatedProgressLabel.setVisible(true);
        refreshInstalled(false);
        refreshNew(false);
        refreshGemLists();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gemsTab = new javax.swing.JTabbedPane();
        updatedPanel = new javax.swing.JPanel();
        searchUpdatedText = new javax.swing.JTextField();
        searchUpdatedLbl = new javax.swing.JLabel();
        reloadReposButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        updatedList = new javax.swing.JList();
        updateButton = new javax.swing.JButton();
        updateAllButton = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        updatedDesc = new javax.swing.JTextPane();
        updatedProgress = new javax.swing.JProgressBar();
        updatedProgressLabel = new javax.swing.JLabel();
        installedPanel = new javax.swing.JPanel();
        instSearchText = new javax.swing.JTextField();
        instSearchLbl = new javax.swing.JLabel();
        reloadInstalledButton = new javax.swing.JButton();
        uninstallButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        installedList = new javax.swing.JList();
        jScrollPane5 = new javax.swing.JScrollPane();
        installedDesc = new javax.swing.JTextPane();
        installedProgress = new javax.swing.JProgressBar();
        installedProgressLabel = new javax.swing.JLabel();
        newPanel = new javax.swing.JPanel();
        searchNewText = new javax.swing.JTextField();
        searchNewLbl = new javax.swing.JLabel();
        reloadNewButton = new javax.swing.JButton();
        installButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        newList = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        newDesc = new javax.swing.JTextPane();
        newProgress = new javax.swing.JProgressBar();
        newProgressLabel = new javax.swing.JLabel();
        settingsPanel = new javax.swing.JPanel();
        proxyButton = new javax.swing.JButton();
        rubyPlatformLabel = new javax.swing.JLabel();
        platforms = org.netbeans.modules.ruby.platform.PlatformComponentFactory.getRubyPlatformsComboxBox();
        manageButton = new javax.swing.JButton();
        gemHome = new javax.swing.JLabel();
        gemHomeValue = new javax.swing.JTextField();

        FormListener formListener = new FormListener();

        searchUpdatedText.setColumns(14);
        searchUpdatedText.addActionListener(formListener);

        searchUpdatedLbl.setLabelFor(searchUpdatedText);
        org.openide.awt.Mnemonics.setLocalizedText(searchUpdatedLbl, org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.searchUpdatedLbl.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(reloadReposButton, org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.reloadReposButton.text")); // NOI18N
        reloadReposButton.addActionListener(formListener);

        jScrollPane3.setViewportView(updatedList);
        updatedList.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.updatedList.AccessibleContext.accessibleName")); // NOI18N
        updatedList.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.updatedList.AccessibleContext.accessibleDescription")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(updateButton, org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.updateButton.text")); // NOI18N
        updateButton.setEnabled(false);
        updateButton.addActionListener(formListener);

        org.openide.awt.Mnemonics.setLocalizedText(updateAllButton, org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.updateAllButton.text")); // NOI18N
        updateAllButton.addActionListener(formListener);

        jScrollPane6.setViewportView(updatedDesc);
        updatedDesc.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.updatedDesc.AccessibleContext.accessibleName")); // NOI18N
        updatedDesc.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.updatedDesc.AccessibleContext.accessibleDescription")); // NOI18N

        updatedProgress.setIndeterminate(true);

        org.openide.awt.Mnemonics.setLocalizedText(updatedProgressLabel, org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.updatedProgressLabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout updatedPanelLayout = new org.jdesktop.layout.GroupLayout(updatedPanel);
        updatedPanel.setLayout(updatedPanelLayout);
        updatedPanelLayout.setHorizontalGroup(
            updatedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(updatedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(updatedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, updatedPanelLayout.createSequentialGroup()
                        .add(reloadReposButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 423, Short.MAX_VALUE)
                        .add(searchUpdatedLbl)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(searchUpdatedText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 156, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(updatedPanelLayout.createSequentialGroup()
                        .add(updateButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(updateAllButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 349, Short.MAX_VALUE)
                        .add(updatedProgressLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(updatedProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, updatedPanelLayout.createSequentialGroup()
                        .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(jScrollPane6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 283, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        updatedPanelLayout.setVerticalGroup(
            updatedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(updatedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(updatedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(searchUpdatedLbl)
                    .add(searchUpdatedText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(reloadReposButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(updatedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                    .add(jScrollPane6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(updatedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(updatedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(updateButton)
                        .add(updateAllButton))
                    .add(updatedProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(updatedProgressLabel))
                .addContainerGap())
        );

        searchUpdatedText.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.searchUpdatedText.AccessibleContext.accessibleDescription")); // NOI18N
        searchUpdatedLbl.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.searchUpdatedLbl.AccessibleContext.accessibleDescription")); // NOI18N
        reloadReposButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.reloadReposButton.AccessibleContext.accessibleDescription")); // NOI18N
        jScrollPane3.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.jScrollPane3.AccessibleContext.accessibleDescription")); // NOI18N
        updateButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.updateButton.AccessibleContext.accessibleDescription")); // NOI18N
        updateAllButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.updateAllButton.AccessibleContext.accessibleDescription")); // NOI18N
        jScrollPane6.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.jScrollPane6.AccessibleContext.accessibleDescription")); // NOI18N
        updatedProgress.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.updatedProgress.AccessibleContext.accessibleDescription")); // NOI18N
        updatedProgressLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.updatedProgressLabel.AccessibleContext.accessibleDescription")); // NOI18N

        gemsTab.addTab(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.updatedPanel.TabConstraints.tabTitle"), updatedPanel); // NOI18N

        instSearchText.setColumns(14);
        instSearchText.addActionListener(formListener);

        instSearchLbl.setLabelFor(instSearchText);
        org.openide.awt.Mnemonics.setLocalizedText(instSearchLbl, org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.instSearchLbl.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(reloadInstalledButton, org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.reloadInstalledButton.text")); // NOI18N
        reloadInstalledButton.addActionListener(formListener);

        org.openide.awt.Mnemonics.setLocalizedText(uninstallButton, org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.uninstallButton.text")); // NOI18N
        uninstallButton.setEnabled(false);
        uninstallButton.addActionListener(formListener);

        jScrollPane1.setViewportView(installedList);
        installedList.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.installedList.AccessibleContext.accessibleName")); // NOI18N
        installedList.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.installedList.AccessibleContext.accessibleDescription")); // NOI18N

        jScrollPane5.setViewportView(installedDesc);
        installedDesc.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.installedDesc.AccessibleContext.accessibleName")); // NOI18N
        installedDesc.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.installedDesc.AccessibleContext.accessibleDescription")); // NOI18N

        installedProgress.setIndeterminate(true);

        org.openide.awt.Mnemonics.setLocalizedText(installedProgressLabel, org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.installedProgressLabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout installedPanelLayout = new org.jdesktop.layout.GroupLayout(installedPanel);
        installedPanel.setLayout(installedPanelLayout);
        installedPanelLayout.setHorizontalGroup(
            installedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(installedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(installedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, installedPanelLayout.createSequentialGroup()
                        .add(reloadInstalledButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 423, Short.MAX_VALUE)
                        .add(instSearchLbl)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(instSearchText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 156, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(installedPanelLayout.createSequentialGroup()
                        .add(uninstallButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 442, Short.MAX_VALUE)
                        .add(installedProgressLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(installedProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, installedPanelLayout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(jScrollPane5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 283, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        installedPanelLayout.setVerticalGroup(
            installedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(installedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(installedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(instSearchLbl)
                    .add(reloadInstalledButton)
                    .add(instSearchText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(installedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                    .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(installedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(uninstallButton)
                    .add(installedProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(installedProgressLabel))
                .addContainerGap())
        );

        instSearchText.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.instSearchText.AccessibleContext.accessibleDescription")); // NOI18N
        instSearchLbl.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.instSearchLbl.AccessibleContext.accessibleDescription")); // NOI18N
        reloadInstalledButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.reloadInstalledButton.AccessibleContext.accessibleDescription")); // NOI18N
        uninstallButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.uninstallButton.AccessibleContext.accessibleDescription")); // NOI18N
        jScrollPane1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.jScrollPane1.AccessibleContext.accessibleDescription")); // NOI18N
        jScrollPane5.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.jScrollPane5.AccessibleContext.accessibleDescription")); // NOI18N
        installedProgress.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.installedProgress.AccessibleContext.accessibleDescription")); // NOI18N
        installedProgressLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.installedProgressLabel.AccessibleContext.accessibleDescription")); // NOI18N

        gemsTab.addTab(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.installedPanel.TabConstraints.tabTitle"), installedPanel); // NOI18N

        searchNewText.setColumns(14);
        searchNewText.addActionListener(formListener);

        searchNewLbl.setLabelFor(searchNewText);
        org.openide.awt.Mnemonics.setLocalizedText(searchNewLbl, org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.searchNewLbl.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(reloadNewButton, org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.reloadNewButton.text")); // NOI18N
        reloadNewButton.addActionListener(formListener);

        org.openide.awt.Mnemonics.setLocalizedText(installButton, org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.installButton.text")); // NOI18N
        installButton.setEnabled(false);
        installButton.addActionListener(formListener);

        jScrollPane2.setViewportView(newList);
        newList.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.newList.AccessibleContext.accessibleName")); // NOI18N
        newList.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.newList.AccessibleContext.accessibleDescription")); // NOI18N

        jScrollPane4.setViewportView(newDesc);
        newDesc.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.newDesc.AccessibleContext.accessibleName")); // NOI18N
        newDesc.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.newDesc.AccessibleContext.accessibleDescription")); // NOI18N

        newProgress.setIndeterminate(true);

        org.openide.awt.Mnemonics.setLocalizedText(newProgressLabel, org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.newProgressLabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout newPanelLayout = new org.jdesktop.layout.GroupLayout(newPanel);
        newPanel.setLayout(newPanelLayout);
        newPanelLayout.setHorizontalGroup(
            newPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(newPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(newPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, newPanelLayout.createSequentialGroup()
                        .add(reloadNewButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 423, Short.MAX_VALUE)
                        .add(searchNewLbl)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(searchNewText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 156, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(newPanelLayout.createSequentialGroup()
                        .add(installButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 459, Short.MAX_VALUE)
                        .add(newProgressLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(newProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, newPanelLayout.createSequentialGroup()
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 283, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        newPanelLayout.setVerticalGroup(
            newPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(newPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(newPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(searchNewLbl)
                    .add(reloadNewButton)
                    .add(searchNewText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(newPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                    .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(newPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(installButton)
                    .add(newProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(newProgressLabel))
                .addContainerGap())
        );

        searchNewText.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.searchNewText.AccessibleContext.accessibleDescription")); // NOI18N
        searchNewLbl.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.searchNewLbl.AccessibleContext.accessibleDescription")); // NOI18N
        reloadNewButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.reloadNewButton.AccessibleContext.accessibleDescription")); // NOI18N
        installButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.installButton.AccessibleContext.accessibleDescription")); // NOI18N
        jScrollPane2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.jScrollPane2.AccessibleContext.accessibleDescription")); // NOI18N
        jScrollPane4.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.jScrollPane4.AccessibleContext.accessibleDescription")); // NOI18N
        newProgress.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.newProgress.AccessibleContext.accessibleDescription")); // NOI18N
        newProgressLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.newProgressLabel.AccessibleContext.accessibleDescription")); // NOI18N

        gemsTab.addTab(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.newPanel.TabConstraints.tabTitle"), newPanel); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(proxyButton, org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.proxyButton.text")); // NOI18N
        proxyButton.addActionListener(formListener);

        org.jdesktop.layout.GroupLayout settingsPanelLayout = new org.jdesktop.layout.GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
            settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(proxyButton)
                .addContainerGap(607, Short.MAX_VALUE))
        );
        settingsPanelLayout.setVerticalGroup(
            settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(proxyButton)
                .addContainerGap(334, Short.MAX_VALUE))
        );

        proxyButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.proxyButton.AccessibleContext.accessibleDescription")); // NOI18N

        gemsTab.addTab(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.settingsPanel.TabConstraints.tabTitle"), settingsPanel); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(rubyPlatformLabel, org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.rubyPlatformLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(manageButton, org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.manageButton.text")); // NOI18N
        manageButton.addActionListener(formListener);

        org.openide.awt.Mnemonics.setLocalizedText(gemHome, org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.gemHome.text")); // NOI18N

        gemHomeValue.setEditable(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(gemsTab, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 778, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(rubyPlatformLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(gemHomeValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 675, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(platforms, 0, 587, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(manageButton)))
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(gemHome, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                        .add(707, 707, 707))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rubyPlatformLabel)
                    .add(manageButton)
                    .add(platforms, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(gemHome)
                    .add(gemHomeValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(gemsTab, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                .addContainerGap())
        );

        gemsTab.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.gemsTab.AccessibleContext.accessibleName")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.AccessibleContext.accessibleDescription")); // NOI18N
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == searchUpdatedText) {
                GemPanel.this.searchUpdatedTextActionPerformed(evt);
            }
            else if (evt.getSource() == reloadReposButton) {
                GemPanel.this.reloadReposButtonActionPerformed(evt);
            }
            else if (evt.getSource() == updateButton) {
                GemPanel.this.updateButtonActionPerformed(evt);
            }
            else if (evt.getSource() == updateAllButton) {
                GemPanel.this.updateAllButtonActionPerformed(evt);
            }
            else if (evt.getSource() == instSearchText) {
                GemPanel.this.instSearchTextActionPerformed(evt);
            }
            else if (evt.getSource() == reloadInstalledButton) {
                GemPanel.this.reloadInstalledButtonActionPerformed(evt);
            }
            else if (evt.getSource() == uninstallButton) {
                GemPanel.this.uninstallButtonActionPerformed(evt);
            }
            else if (evt.getSource() == searchNewText) {
                GemPanel.this.searchNewTextActionPerformed(evt);
            }
            else if (evt.getSource() == reloadNewButton) {
                GemPanel.this.reloadNewButtonActionPerformed(evt);
            }
            else if (evt.getSource() == installButton) {
                GemPanel.this.installButtonActionPerformed(evt);
            }
            else if (evt.getSource() == proxyButton) {
                GemPanel.this.proxyButtonActionPerformed(evt);
            }
            else if (evt.getSource() == manageButton) {
                GemPanel.this.manageButtonActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void reloadNewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadNewButtonActionPerformed
        refreshNew(true);
    }//GEN-LAST:event_reloadNewButtonActionPerformed

    private void proxyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proxyButtonActionPerformed
        OptionsDisplayer.getDefault().open("General"); // NOI18Nd
    }//GEN-LAST:event_proxyButtonActionPerformed

    private void searchNewTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchNewTextActionPerformed
        updateList(NEW_TAB_INDEX, true);
    }//GEN-LAST:event_searchNewTextActionPerformed

    private void searchUpdatedTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchUpdatedTextActionPerformed
        updateList(UPDATED_TAB_INDEX, true);
    }//GEN-LAST:event_searchUpdatedTextActionPerformed

    private void reloadReposButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadReposButtonActionPerformed
        useCached = false;
        refreshUpdated();
    }//GEN-LAST:event_reloadReposButtonActionPerformed

    private void installButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_installButtonActionPerformed
        assert SwingUtilities.isEventDispatchThread();

        int[] indices = newList.getSelectedIndices();
        List<Gem> gems = new ArrayList<Gem>();
        for (int index : indices) {
            Object o = newList.getModel().getElementAt(index);
            if (o instanceof Gem) { // Could be error or please wait string
                Gem gem = (Gem)o;
                gems.add(gem);
            }
        }

        if (gems.size() > 0) {
            for (Gem chosen : gems) {
                // Get some information about the chosen gem
                InstallationSettingsPanel panel = new InstallationSettingsPanel(chosen);
                panel.getAccessibleContext().setAccessibleDescription(
                        NbBundle.getMessage(GemPanel.class, "InstallationSettingsPanel.AccessibleContext.accessibleDescription"));

                DialogDescriptor dd = new DialogDescriptor(panel, NbBundle.getMessage(GemPanel.class, "ChooseGemSettings"));
                dd.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
                dd.setModal(true);
                dd.setHelpCtx(new HelpCtx(GemPanel.class));
                Object result = DialogDisplayer.getDefault().notify(dd);
                if (result.equals(NotifyDescriptor.OK_OPTION)) {
                    Gem gem = new Gem(panel.getGemName(), null, null);
                    // XXX Do I really need to refresh it right way?
                    GemListRefresher completionTask = new GemListRefresher(newList, INSTALLED_TAB_INDEX);
                    boolean changed = gemManager.install(new Gem[] { gem }, this, false, false, panel.getVersion(), 
                            panel.getIncludeDepencies(), true, completionTask);
                    installedModified = installedModified || changed;
                }
            }
        }

    }//GEN-LAST:event_installButtonActionPerformed

    private void instSearchTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_instSearchTextActionPerformed
        updateList(INSTALLED_TAB_INDEX, true);
    }//GEN-LAST:event_instSearchTextActionPerformed

    private void updateAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateAllButtonActionPerformed
        Runnable completionTask = new GemListRefresher(installedList, INSTALLED_TAB_INDEX);
        gemManager.update(null, this, false, false, true, completionTask);
        installedModified = true; 
    }//GEN-LAST:event_updateAllButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        assert SwingUtilities.isEventDispatchThread();

        int[] indices = updatedList.getSelectedIndices();
        List<Gem> gems = new ArrayList<Gem>();
        if (indices != null) {
            for (int index : indices) {
                assert index >= 0;
                Object o = updatedList.getModel().getElementAt(index);
                if (o instanceof Gem) { // Could be error or please wait string
                    Gem gem = (Gem)o;
                    gems.add(gem);
                }            
            }
        }
        if (gems.size() > 0) {
            Runnable completionTask = new GemListRefresher(updatedList, INSTALLED_TAB_INDEX);
            gemManager.update(gems.toArray(new Gem[gems.size()]), this, false, false, true, completionTask);
            installedModified = true;
        }
    }//GEN-LAST:event_updateButtonActionPerformed

    private void uninstallButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uninstallButtonActionPerformed
        assert SwingUtilities.isEventDispatchThread();

        int[] indices = installedList.getSelectedIndices();
        List<Gem> gems = new ArrayList<Gem>();
        if (indices != null) {
            for (int index : indices) {
                assert index >= 0;
                Object o = installedList.getModel().getElementAt(index);
                if (o instanceof Gem) { // Could be error or please wait string
                    Gem gem = (Gem)o;
                    gems.add(gem);
                }            
            }
        }
        if (gems.size() > 0) {
            Runnable completionTask = new GemListRefresher(installedList, INSTALLED_TAB_INDEX);
            gemManager.uninstall(gems.toArray(new Gem[gems.size()]), this, true, completionTask);
            installedModified = true;
        }
    }//GEN-LAST:event_uninstallButtonActionPerformed

    private void reloadInstalledButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadInstalledButtonActionPerformed
        refreshInstalled(true);
    }//GEN-LAST:event_reloadInstalledButtonActionPerformed

    private void manageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageButtonActionPerformed
        RubyPlatformCustomizer.showCustomizer();
        platforms.setModel(new PlatformComponentFactory.RubyPlatformListModel()); // refresh
        platforms.requestFocus();
    }//GEN-LAST:event_manageButtonActionPerformed

    /**
     * Refresh the list of displayed gems. If refresh is true, refresh the list
     * from the gem manager, otherwise just refilter list.
    */
    private void refreshGemList(final int tab) {        
        Runnable runner = new Runnable() {
            public void run() {
                synchronized(this) {
                    assert !SwingUtilities.isEventDispatchThread();

                    List<String> errors = new ArrayList<String>(500);
                    if (tab == INSTALLED_TAB_INDEX) {
                        installedGems = gemManager.reloadInstalledGems(errors);
                        fetchingLocal = false;
                    } else if (tab == NEW_TAB_INDEX) {
                        remoteFailure = null;
                        availableGems = newGems = gemManager.reloadAvailableGems(errors);
                        if (availableGems.size() == 0 && errors.size() > 0) {
                            remoteFailure = errors;
                        }
                        fetchingRemote = false;
                    }
                    
                    // Update UI
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            boolean done = updateGems();

                            if (!done) {
                                // Just filter
                                updateList(tab, false);
                            } else if (tab == INSTALLED_TAB_INDEX) {
                                updateList(tab, true);
                            }
                        }
                    });
                }
            }
        };
        
        RequestProcessor.getDefault().post(runner, 50);
    }

    private void refreshGemLists() {        
        Runnable runner = new Runnable() {
            public void run() {
                synchronized(this) {
                    assert !SwingUtilities.isEventDispatchThread();

                    List<String> lines = Collections.emptyList();
                    remoteFailure = null;
                    if (!useCached) {
                        lines = gemManager.reload();
                        useCached = true;
                    }
                    installedGems = gemManager.getInstalledGems();
                    availableGems = gemManager.getAvailableGems();
                    newGems = availableGems;
                    fetchingLocal = false;
                    fetchingRemote = false;
                    if (availableGems.size() == 0 && lines.size() > 0) {
                        remoteFailure = lines;
                    }
                    
                    // Update UI
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            updateGems();
                            updateList(INSTALLED_TAB_INDEX, true);

                            if (remoteFailure != null && !fetchingLocal) {
                                // Update the local list which shouldn't have any errors
                                refreshInstalled(true);
                            }
                        }
                    });
                }
            }
        };
        
        RequestProcessor.getDefault().post(runner, 50);
    }
    
    private String getGemFilter(int tab) {
        assert SwingUtilities.isEventDispatchThread();

        String filter = null;
        JTextField tf;
        if (tab == INSTALLED_TAB_INDEX) {
            tf = instSearchText;
        } else if (tab == UPDATED_TAB_INDEX) {
            tf = searchUpdatedText;
        } else {
            assert tab == NEW_TAB_INDEX;
            tf = searchNewText;
        }
        filter = tf.getText().trim();
        if (filter.length() == 0) {
            filter = null;
        }
        
        return filter;
    }

    private class MyListSelectionListener implements ListSelectionListener {
        private JButton button;
        private JTextPane pane;
        private JList list;
        
        private MyListSelectionListener(JList list, JTextPane pane, JButton button) {
            this.list = list;
            this.pane = pane;
            this.button = button;
        }
        public void valueChanged(ListSelectionEvent ev) {
            if (ev.getValueIsAdjusting()) {
                return;
            }
            int index = list.getSelectedIndex();
            if (index != -1) {
                Object o = list.getModel().getElementAt(index);
                if (o instanceof Gem) { // Could be "Please Wait..." String
                    button.setEnabled(true);
                    if (pane != null) {
                        updateGemDescription(pane, (Gem)o);
                    }
                    return;
                }
            } else if (pane != null) {
                pane.setText("");
            }
            button.setEnabled(index != -1);
        }
    }
            
    private class GemListRefresher implements Runnable {
        private JList list;
        private int tab;
        
        public GemListRefresher(JList list, int tab) {
            this.list = list;
            this.tab = tab;
        }

        public void run() {
            if (!gemsModified) {
                gemsModified = true;
            }
            refreshGemList(tab);
            if (list == installedList) {
                installedModified = false;
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel gemHome;
    private javax.swing.JTextField gemHomeValue;
    private javax.swing.JTabbedPane gemsTab;
    private javax.swing.JLabel instSearchLbl;
    private javax.swing.JTextField instSearchText;
    private javax.swing.JButton installButton;
    private javax.swing.JTextPane installedDesc;
    private javax.swing.JList installedList;
    private javax.swing.JPanel installedPanel;
    private javax.swing.JProgressBar installedProgress;
    private javax.swing.JLabel installedProgressLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JButton manageButton;
    private javax.swing.JTextPane newDesc;
    private javax.swing.JList newList;
    private javax.swing.JPanel newPanel;
    private javax.swing.JProgressBar newProgress;
    private javax.swing.JLabel newProgressLabel;
    private javax.swing.JComboBox platforms;
    private javax.swing.JButton proxyButton;
    private javax.swing.JButton reloadInstalledButton;
    private javax.swing.JButton reloadNewButton;
    private javax.swing.JButton reloadReposButton;
    private javax.swing.JLabel rubyPlatformLabel;
    private javax.swing.JLabel searchNewLbl;
    private javax.swing.JTextField searchNewText;
    private javax.swing.JLabel searchUpdatedLbl;
    private javax.swing.JTextField searchUpdatedText;
    private javax.swing.JPanel settingsPanel;
    private javax.swing.JButton uninstallButton;
    private javax.swing.JButton updateAllButton;
    private javax.swing.JButton updateButton;
    private javax.swing.JTextPane updatedDesc;
    private javax.swing.JList updatedList;
    private javax.swing.JPanel updatedPanel;
    private javax.swing.JProgressBar updatedProgress;
    private javax.swing.JLabel updatedProgressLabel;
    // End of variables declaration//GEN-END:variables
    
}
