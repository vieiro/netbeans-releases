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

package org.netbeans.modules.maven.navigator;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingException;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.maven.api.Constants;
import org.netbeans.modules.maven.embedder.EmbedderFactory;
import org.netbeans.modules.maven.spi.nodes.NodeUtils;
import org.openide.cookies.EditCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author  mkleint
 */
public class POMInheritancePanel extends javax.swing.JPanel implements ExplorerManager.Provider, Runnable {
    private transient ExplorerManager explorerManager = new ExplorerManager();
    
    private BeanTreeView treeView;
    private DataObject current;
    private FileChangeAdapter adapter = new FileChangeAdapter(){
            @Override
            public void fileChanged(FileEvent fe) {
                showWaitNode();
                RequestProcessor.getDefault().post(POMInheritancePanel.this);
            }
        };

    /** Creates new form POMInheritancePanel */
    public POMInheritancePanel() {
        initComponents();
        treeView = (BeanTreeView)jScrollPane1;
    }
    
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    void navigate(DataObject d) {
        if (current != null) {
            current.getPrimaryFile().removeFileChangeListener(adapter);
        }
        current = d;
        current.getPrimaryFile().addFileChangeListener(adapter);
        showWaitNode();
        RequestProcessor.getDefault().post(this);
    }
    
    public void run() {
        //#164852 somehow a folder dataobject slipped in, test mimetype to avoid that.
        // the root cause of the problem is unknown though
        if (current != null && Constants.POM_MIME_TYPE.equals(current.getPrimaryFile().getMIMEType())) { //NOI18N
            File file = FileUtil.toFile(current.getPrimaryFile());
            // can be null for stuff in jars?
            if (file != null) {
                try {
                    List<Model> lin = EmbedderFactory.createModelLineage(file, EmbedderFactory.getProjectEmbedder());
                    final Children ch = Children.create(new PomChildren(lin), false);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                           treeView.setRootVisible(false);
                           explorerManager.setRootContext(new AbstractNode(ch));
                        } 
                    });
                } catch (final ModelBuildingException ex) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                           treeView.setRootVisible(true);
                           explorerManager.setRootContext(POMModelPanel.createErrorNode(ex));
                        }
                    });
                }
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                       treeView.setRootVisible(false);
                       explorerManager.setRootContext(createEmptyNode());
                    } 
                });
            }
        }
    }

    /**
     * 
     */
    void release() {
        if (current != null) {
            current.getPrimaryFile().removeFileChangeListener(adapter);
        }
        current = null;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               treeView.setRootVisible(false);
               explorerManager.setRootContext(createEmptyNode());
            } 
        });
    }

    /**
     * 
     */
    public void showWaitNode() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               treeView.setRootVisible(true);
               explorerManager.setRootContext(createWaitNode());
            } 
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new BeanTreeView();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    
    private static Node createWaitNode() {
        AbstractNode an = new AbstractNode(Children.LEAF);
        an.setIconBaseWithExtension("org/netbeans/modules/maven/navigator/wait.gif");
        an.setDisplayName(NbBundle.getMessage(POMInheritancePanel.class, "LBL_Wait"));
        return an;
    }

    private static Node createEmptyNode() {
        AbstractNode an = new AbstractNode(Children.LEAF);
        return an;
    }
    
    private static class PomChildren extends ChildFactory<Model> {

        private final List<Model> lineage;

        PomChildren(List<Model> lineage) {
            this.lineage = lineage;
        }

        protected @Override boolean createKeys(List<Model> toPopulate) {
            toPopulate.addAll(lineage);
            return true;
        }
        
        protected @Override Node createNodeForKey(Model mdl) {
            File fl = mdl.getPomFile();
            String version = mdl.getVersion();
            if (version == null && mdl.getParent() != null) {
                version = mdl.getParent().getVersion();
            }
            if (version == null) {
                return null;
            }
            if (fl == null) {
                ArtifactRepository repo = EmbedderFactory.getProjectEmbedder().getLocalRepository();
                DefaultArtifactHandler handler = new DefaultArtifactHandler();
                handler.setExtension("pom");
                String groupId = mdl.getGroupId();
                if (groupId == null && mdl.getParent() != null) {
                    groupId = mdl.getParent().getGroupId();
                }
                assert groupId != null;
                fl = new File(repo.getBasedir(), repo.pathOf(new DefaultArtifact(groupId, mdl.getArtifactId(), version, null, "pom", null, handler)));
            }
            FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(fl));
            if (fo != null) {
                try {
                    return new POMNode(fl, mdl, DataObject.find(NodeUtils.readOnlyLocalRepositoryFile(fo)), version);
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return null;
        }
    }

    
    private static class POMNode extends AbstractNode {
        
        private Image icon = ImageUtilities.loadImage("org/netbeans/modules/maven/navigator/Maven2Icon.gif"); // NOI18N
        private boolean readonly = false;
        private final DataObject dobj;
        private POMNode(@NonNull File key, @NonNull Model mdl, @NonNull DataObject dobj, @NonNull String version) {
            super(Children.LEAF);
            setDisplayName(NbBundle.getMessage(POMInheritancePanel.class, "TITLE_PomNode", mdl.getArtifactId(), version));
            if (!dobj.getPrimaryFile().canWrite()) {
                //coming from repository
                readonly = true;
            }
            setShortDescription(key.getAbsolutePath());
            this.dobj = dobj;
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[] {
                new MyEditAction()
            };
        }

        @Override
        public Action getPreferredAction() {
            return new MyEditAction();
        }

        @Override
        public String getHtmlDisplayName() {
            if (readonly) {
                return NbBundle.getMessage(POMInheritancePanel.class, "HTML_TITLE_PomNode", getDisplayName());
            }
            return null;
        }
        
        @Override
        public Image getIcon(int type) {
             return icon;
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }
        
        private class MyEditAction extends AbstractAction {
            
            public MyEditAction() {
                putValue(NAME, NbBundle.getMessage(POMInheritancePanel.class, "ACTION_Edit"));
                setEnabled(dobj != null);
            }
            
            public @Override void actionPerformed(ActionEvent e) {
                if (dobj != null) {
                    EditCookie ec = dobj.getLookup().lookup(EditCookie.class);
                    if (ec != null) {
                        ec.edit();
                    }
                }
            }
        }
    }
    }
