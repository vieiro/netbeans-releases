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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.repository.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JButton;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.api.project.Project;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.netbeans.modules.maven.api.CommonArtifactActions;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.embedder.DependencyTreeFactory;
import org.netbeans.modules.maven.embedder.EmbedderFactory;
import org.netbeans.modules.maven.embedder.MavenEmbedder;
import org.netbeans.modules.maven.embedder.exec.ProgressTransferListener;
import org.netbeans.modules.maven.indexer.api.NBVersionInfo;
import org.netbeans.modules.maven.indexer.api.RepositoryInfo;
import org.netbeans.modules.maven.indexer.api.RepositoryPreferences;
import org.netbeans.modules.maven.indexer.api.RepositoryUtil;
import org.netbeans.modules.maven.indexer.spi.ui.ArtifactViewerFactory;
import org.netbeans.modules.maven.indexer.spi.ui.ArtifactViewerPanelProvider;
import org.netbeans.modules.maven.model.Utilities;
import org.netbeans.modules.maven.model.pom.POMModel;
import org.netbeans.modules.maven.model.pom.POMModelFactory;
import org.netbeans.modules.maven.repository.dependency.AddAsDependencyAction;
import static org.netbeans.modules.maven.repository.ui.Bundle.*;
import org.netbeans.modules.xml.xam.ModelSource;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author mkleint
 */
@ServiceProvider( service=ArtifactViewerFactory.class )
public final class ArtifactMultiViewFactory implements ArtifactViewerFactory {

    private static final RequestProcessor RP = new RequestProcessor(ArtifactMultiViewFactory.class);

    @Override @NonNull public Lookup createLookup(@NonNull Artifact artifact, @NullAllowed List<ArtifactRepository> repos) {
        return createLookup(null, null, artifact, repos);
    }
    @Override @NonNull public Lookup createLookup(@NonNull NBVersionInfo info) {
        return createLookup(null, info, RepositoryUtil.createArtifact(info), null);
    }

    @Override @CheckForNull public Lookup createLookup(@NonNull Project prj) {
        NbMavenProject mvPrj = prj.getLookup().lookup(NbMavenProject.class);
        MavenProject mvn = mvPrj.getMavenProject();
        Artifact artifact = mvn.getArtifact();
        return artifact != null ? createLookup(prj, null, artifact, null) : null;
    }

    @Override @NonNull public TopComponent createTopComponent(@NonNull Lookup lookup) {
        Artifact artifact = lookup.lookup(Artifact.class);
        assert artifact != null;
        TopComponent existing = findExistingTc(artifact);
        if (existing != null) {
            return existing;
        }
        Collection<? extends ArtifactViewerPanelProvider> provs = Lookup.getDefault().lookupAll(ArtifactViewerPanelProvider.class);
        MultiViewDescription[] panels = new MultiViewDescription[provs.size()];
        int i = 0;
        for (ArtifactViewerPanelProvider prov : provs) {
            panels[i] = prov.createPanel(lookup);
            i = i + 1;
        }
        TopComponent tc = MultiViewFactory.createMultiView(panels, panels[0]);
        tc.setDisplayName(artifact.getArtifactId() + ":" + artifact.getVersion()); //NOI18N
        tc.setToolTipText(artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion()); //NOI18N
        tc.putClientProperty(MAVEN_TC_PROPERTY, getTcId(artifact));
        return tc;
    }

    @Messages({
        "Progress_Download=Downloading Maven dependencies",
        "TIT_Error=Panel loading error.",
        "BTN_CLOSE=&Close"
    })
    @NonNull private Lookup createLookup(final @NullAllowed Project prj, final @NullAllowed NBVersionInfo info, final @NonNull Artifact artifact, final @NullAllowed List<ArtifactRepository> fRepos) {
        final InstanceContent ic = new InstanceContent();
        AbstractLookup lookup = new AbstractLookup(ic);
        if (prj != null) {
            ic.add(prj);
        }
        ic.add(artifact);
        if (info != null) {
            ic.add(info);
        }
        final Artifact fArt = artifact;

        if (prj == null) {
            RP.post(new Runnable() {
                    @Override
                public void run() {
                    MavenEmbedder embedder = EmbedderFactory.getOnlineEmbedder();
                    AggregateProgressHandle hndl = AggregateProgressFactory.createHandle(Progress_Download(),
                                new ProgressContributor[] {
                                    AggregateProgressFactory.createProgressContributor("zaloha") },  //NOI18N
                                ProgressTransferListener.cancellable(), null);
                    ProgressTransferListener.setAggregateHandle(hndl);
                    hndl.start();
                    try {
                            List<ArtifactRepository> repos = new ArrayList<ArtifactRepository>();
                            if (fRepos != null) {
                                repos.addAll(fRepos);
                            }
                            if (repos.isEmpty()) {
                                //add central repo
                                repos.add(embedder.createRemoteRepository(RepositorySystem.DEFAULT_REMOTE_REPO_URL, RepositorySystem.DEFAULT_REMOTE_REPO_ID));
                                //add repository form info
                                if (info != null && !RepositorySystem.DEFAULT_REMOTE_REPO_ID.equals(info.getRepoId())) {
                                    RepositoryInfo rinfo = RepositoryPreferences.getInstance().getRepositoryInfoById(info.getRepoId());
                                    if (rinfo != null) {
                                        String url = rinfo.getRepositoryUrl();
                                        if (url != null) {
                                            repos.add(embedder.createRemoteRepository(url, rinfo.getId()));
                                        }
                                    }
                                }
                            }
                            MavenProject mvnprj = readMavenProject(embedder, fArt, repos);

                        if(mvnprj != null){
                            DependencyNode root = DependencyTreeFactory.createDependencyTree(mvnprj, embedder, Artifact.SCOPE_TEST);
                            ic.add(root);
                            ic.add(mvnprj);
                        }

                    } catch (ProjectBuildingException ex) {
                        ErrorPanel pnl = new ErrorPanel(ex);
                        DialogDescriptor dd = new DialogDescriptor(pnl, TIT_Error());
                        JButton close = new JButton();
                        org.openide.awt.Mnemonics.setLocalizedText(close, BTN_CLOSE());
                        dd.setOptions(new Object[] { close });
                        dd.setClosingOptions(new Object[] { close });
                        DialogDisplayer.getDefault().notify(dd);
                        ic.add(new MavenProject()); // XXX is this useful for anything?
                    } catch (ThreadDeath d) { // download interrupted
                    } finally {
                        hndl.finish();
                        ProgressTransferListener.clearAggregateHandle();
                    }
                }
            });
        } else {
            RP.post(new Runnable() {
                @Override
                public void run() {
                    NbMavenProject im = prj.getLookup().lookup(NbMavenProject.class);
                    List<String> profileIds = new ArrayList<String>();
                    for (Profile p : im.getMavenProject().getActiveProfiles()) {
                        profileIds.add(p.getId());
                    }
                    MavenProject mvnprj = im.loadAlternateMavenProject(EmbedderFactory.getProjectEmbedder(), profileIds, new Properties());
                    DependencyNode tree = DependencyTreeFactory.createDependencyTree(mvnprj, EmbedderFactory.getProjectEmbedder(), Artifact.SCOPE_TEST);
                    FileObject fo = prj.getLookup().lookup(FileObject.class);
                    POMModel pommodel = null;
                    if (fo != null) {
                        ModelSource ms = Utilities.createModelSource(fo);
                        if (ms.isEditable()) {
                            POMModel model = POMModelFactory.getDefault().getModel(ms);
                            if (model != null) {
                                pommodel = model;
                            }
                        }
                    }
                    //add all in one place to prevent large time delays between additions
                    if (pommodel != null) {
                        ic.add(pommodel);
                    }
                    ic.add(tree);
                    ic.add(mvnprj);
                }
            });
        }

        Action[] toolbarActions = new Action[] {
            new AddAsDependencyAction(fArt),
            CommonArtifactActions.createScmCheckoutAction(lookup),
            CommonArtifactActions.createLibraryAction(lookup)
        };
        ic.add(toolbarActions);

        return lookup;
    }

    private static MavenProject readMavenProject(MavenEmbedder embedder, Artifact artifact, List<ArtifactRepository> remoteRepos) throws  ProjectBuildingException {
        //TODO rewrite
        MavenProjectBuilder bldr = embedder.lookupComponent(MavenProjectBuilder.class);
        assert bldr !=null : "MavenProjectBuilder component not found in maven";
        embedder.setUpLegacySupport();
        return bldr.buildFromRepository(artifact, remoteRepos, embedder.getLocalRepository()) ;
    }
    
    private static final String MAVEN_TC_PROPERTY = "mvn_tc_id";

    private static TopComponent findExistingTc(Artifact artifact) {
        String id = getTcId(artifact);
        Set<TopComponent> tcs = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent tc : tcs) {
            if (id.equals(tc.getClientProperty(MAVEN_TC_PROPERTY))) {
                return tc;
            }
        }
        return null;
    }

    private static String getTcId(Artifact artifact) {
        return artifact.getGroupId() + ":" + artifact.getArtifactId() +
                ":" + artifact.getVersion();
    }

}
