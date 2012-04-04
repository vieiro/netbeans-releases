/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.apisupport;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.xml.namespace.QName;
import org.netbeans.api.project.Project;
import org.netbeans.modules.maven.api.FileUtilities;
import org.netbeans.modules.maven.api.ModelUtils;
import org.netbeans.modules.maven.api.PluginPropertyUtils;
import org.netbeans.modules.maven.api.customizer.ModelHandle2;
import org.netbeans.modules.maven.api.customizer.support.SelectedItemsTable;
import org.netbeans.modules.maven.api.customizer.support.SelectedItemsTable.SelectedItemsTableModel;
import org.netbeans.modules.maven.model.ModelOperation;
import org.netbeans.modules.maven.model.pom.Build;
import org.netbeans.modules.maven.model.pom.Configuration;
import org.netbeans.modules.maven.model.pom.POMExtensibilityElement;
import org.netbeans.modules.maven.model.pom.POMModel;
import org.netbeans.modules.maven.model.pom.Plugin;
import org.netbeans.modules.maven.spi.customizer.SelectedItemsTablePersister;

/**
 * Panel showing list of public packages for a module, also implements
 * persistence to/from pom.xml
 *
 * @author Dafe Simonek
 */
public class PublicPackagesPanel extends javax.swing.JPanel implements SelectedItemsTablePersister {
    private static final String ALL_SUBPACKAGES = ".*";
    private static final String ALL_SUBPACKAGES_2 = ".**";
    private static final int COALESCE_LIMIT = 2;
    private static final String PUBLIC_PACKAGE = "publicPackage";
    private static final String PUBLIC_PACKAGES = "publicPackages";

    private final SelectedItemsTableModel tableModel;
    private final ModelHandle2 handle;
    private final Project project;
    private ModelOperation<POMModel> operation;

    /** Creates new form PublicPackagesPanel */
    public PublicPackagesPanel(ModelHandle2 handle, Project prj) {
        this.handle = handle;
        this.project = prj;
        tableModel = new SelectedItemsTableModel(this);

        initComponents();

        jScrollPane1.getViewport().setBackground(exportTable.getBackground());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        exportTable = new SelectedItemsTable(tableModel);

        jLabel1.setLabelFor(exportTable);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(PublicPackagesPanel.class, "PublicPackagesPanel.jLabel1.text")); // NOI18N

        jScrollPane1.setViewportView(exportTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap(411, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable exportTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public SortedMap<String, Boolean> read() {
        SortedMap<String, Boolean> pkgMap = new TreeMap<String, Boolean>();

        SortedSet<String> packageNames = FileUtilities.getPackageNames(project);
        for (String pkgName : packageNames) {
            pkgMap.put(pkgName, Boolean.FALSE);
        }

        String[] publicPkgs = PluginPropertyUtils.getPluginPropertyList(project,
                MavenNbModuleImpl.GROUPID_MOJO, MavenNbModuleImpl.NBM_PLUGIN, PUBLIC_PACKAGES, PUBLIC_PACKAGE,
                "manifest"); //NOI18N

        if (publicPkgs != null) {
            for (int i = 0; i < publicPkgs.length; i++) {
                String prefix = null;
                String curPkg = publicPkgs[i];
                if (curPkg.endsWith(ALL_SUBPACKAGES)) {
                    prefix = curPkg.substring(0, curPkg.length() - ALL_SUBPACKAGES.length());
                } else if (curPkg.endsWith(ALL_SUBPACKAGES_2)) {
                    prefix = curPkg.substring(0, curPkg.length() - ALL_SUBPACKAGES_2.length());
                }
                if (prefix == null) {
                    pkgMap.put(curPkg, Boolean.TRUE);
                } else {
                    for (String pkgName : packageNames) {
                        if (pkgName.startsWith(prefix)) {
                            pkgMap.put(pkgName, Boolean.TRUE);
                        }
                    }
                }
            }
        }

        return pkgMap;
    }

    @Override
    public void write(SortedMap<String, Boolean> items) {
        if (operation != null) {
            handle.removePOMModification(operation);
        }
        final SortedMap<String, Boolean> selItems = new TreeMap<String, Boolean>(items);
        operation = new ModelOperation<POMModel>() {

            @Override
            public void performOperation(POMModel pomModel) {
        Build build = pomModel.getProject().getBuild();
        boolean selEmpty = true;
        for (Boolean selected : selItems.values()) {
            if (selected) {
                selEmpty = false;
                break;
            }
        }

        Plugin nbmPlugin = null;
        if (build != null) {
            nbmPlugin = build.findPluginById(MavenNbModuleImpl.GROUPID_MOJO, MavenNbModuleImpl.NBM_PLUGIN);
        } else {
            build = pomModel.getFactory().createBuild();
            pomModel.getProject().setBuild(build);
        }
        Configuration config = null;
        if (nbmPlugin != null) {
            config = nbmPlugin.getConfiguration();
        } else {
            nbmPlugin = pomModel.getFactory().createPlugin();
            nbmPlugin.setGroupId(MavenNbModuleImpl.GROUPID_MOJO);
            nbmPlugin.setArtifactId(MavenNbModuleImpl.NBM_PLUGIN);
            nbmPlugin.setExtensions(Boolean.TRUE);
            build.addPlugin(nbmPlugin);
        }

        if (config == null) {
            config = pomModel.getFactory().createConfiguration();
            nbmPlugin.setConfiguration(config);
        }

        List<POMExtensibilityElement> configElems = config.getConfigurationElements();
        POMExtensibilityElement packages = null;
        for (POMExtensibilityElement elem : configElems) {
            if (PUBLIC_PACKAGES.equals(elem.getQName().getLocalPart())) {
                packages = elem;
                break;
            }
        }

        if (selEmpty) {
            if (packages != null) {
                config.removeExtensibilityElement(packages);
            }
            return;
        }

        packages = ModelUtils.getOrCreateChild(config, PUBLIC_PACKAGES, pomModel);
        List<POMExtensibilityElement> elems = packages.getAnyElements();
        for (POMExtensibilityElement elem : elems) {
            packages.removeAnyElement(elem);
        }
        POMExtensibilityElement publicP;

        for (String elemText : getPublicPackagesForPlugin(selItems)) {
            publicP = pomModel.getFactory().createPOMExtensibilityElement(
                    new QName(PUBLIC_PACKAGE));
            publicP.setElementText(elemText);
            packages.addExtensibilityElement(publicP);
        }

    }
        };
        handle.addPOMModification(operation);
    }

    /**
     * Transforms public packages in form of "selected items" map into
     * set of strings describing public packages in maven-nbm-plugin syntax.
     */
    public static SortedSet<String> getPublicPackagesForPlugin (SortedMap<String, Boolean> selItems) {
        SortedSet<String> result = new TreeSet<String>();
        Set<String> processed = new HashSet<String>();
        for (Entry<String, Boolean> entry : selItems.entrySet()) {
            if (entry.getValue() && !processed.contains(entry.getKey())) {
                boolean allSubpackages = true;
                Set<String> processedCandidates = new HashSet<String>();
                String prefix = entry.getKey() + ".";
                for (String key : selItems.keySet()) {
                    if (key.startsWith(prefix)) {
                        if (selItems.get(key)) {
                            processedCandidates.add(key);
                        } else {
                            allSubpackages = false;
                            break;
                        }
                    }
                }
                if (allSubpackages && processedCandidates.size() > COALESCE_LIMIT) {
                    result.add(entry.getKey() + ALL_SUBPACKAGES);
                    processed.addAll(processedCandidates);
                } else {
                    result.add(entry.getKey());
                }
            }
        }

        return result;
    }

}