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
package org.netbeans.modules.web.inspect.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.css.model.api.Model;
import org.netbeans.modules.css.model.api.Rule;
import org.netbeans.modules.css.model.api.StyleSheet;
import org.netbeans.modules.css.visual.spi.CssStylesListener;
import org.netbeans.modules.css.visual.spi.CssStylesPanelProvider;
import org.netbeans.modules.web.browser.api.Page;
import org.netbeans.modules.web.common.api.DependentFileQuery;
import org.netbeans.modules.web.inspect.PageInspectorImpl;
import org.netbeans.modules.web.inspect.PageModel;
import org.netbeans.spi.project.ActionProvider;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * CSS Styles view.
 *
 * @author Jan Stola
 */
public abstract class CssStylesPanelProviderImpl extends JPanel implements CssStylesPanelProvider {

    /**
     * Label shown when no styles information is available.
     */
    private JLabel noStylesLabel;
    /**
     * The latest "related" file, i.e. file provided through the context lookup.
     */
    private FileObject lastRelatedFOB;
    /**
     * Currently inspected page model.
     */
    private PageModel currentPageModel;
    /**
     * Inspected file object.
     */
    private FileObject inspectedFOB;
    /**
     * Panel shown when no page model is available but when we have some
     * "related" file.
     */
    private JPanel runFilePanel;
    /**
     * Run button in {@code runFilePanel}.
     */
    private JButton runButton;
    /**
     * Wrapper for the lookup of the current view.
     */
    private final MatchedRulesLookup lookup;
    /**
     * Determines whether the view is active or not.
     */
    private boolean active = true;
    
    private static final RequestProcessor RP = new RequestProcessor(CssStylesPanelProviderImpl.class);

    /**
     * Creates a new {@code MatchedRulesTC}.
     */
    public CssStylesPanelProviderImpl() {
        lookup = new MatchedRulesLookup();
        setLayout(new BorderLayout());
        initNoStylesLabel();
        initRunFilePanel();
        add(noStylesLabel, BorderLayout.CENTER);
        PageInspectorImpl.getDefault().addPropertyChangeListener(createInspectorListener());
        update(PageInspectorImpl.getDefault().getPage());
    }

    Lookup getMatchedRulesLookup() {
        return lookup;
    }

    /**
     * Initializes the "no Styles" label.
     */
    private void initNoStylesLabel() {
        noStylesLabel = new JLabel();
        noStylesLabel.setText(NbBundle.getMessage(CssStylesPanelProviderImpl.class, "CssStylesPanelProviderImpl.noStylesLabel")); // NOI18N
        noStylesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        noStylesLabel.setVerticalAlignment(SwingConstants.CENTER);
        noStylesLabel.setEnabled(false);
        noStylesLabel.setBackground(new BeanTreeView().getViewport().getView().getBackground());
        noStylesLabel.setOpaque(true);
    }

    /**
     * Initializes the "Run File" panel.
     */
    private void initRunFilePanel() {
        runFilePanel = new JPanel();
        JLabel label = new JLabel(NbBundle.getMessage(CssStylesPanelProviderImpl.class, "CssStylesPanelProviderImpl.runFileLabel")); // NOI18N
        label.setHorizontalAlignment(SwingConstants.CENTER);
        runButton = new JButton();
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileObject lastRelatedFileObject = getLastRelatedFileObject();
                if (lastRelatedFileObject != null) {
                    ActionProvider provider = actionProviderForFileObject(lastRelatedFileObject);
                    if (provider != null) {
                        Lookup context = contextForFileObject(lastRelatedFileObject);
                        if (provider.isActionEnabled(ActionProvider.COMMAND_RUN_SINGLE, context)) {
                            provider.invokeAction(ActionProvider.COMMAND_RUN_SINGLE, context);
                        }
                    }
                }
            }
        });
        GroupLayout layout = new GroupLayout(runFilePanel);
        runFilePanel.setLayout(layout);
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(label)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(runButton)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(label, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(runButton))
                .addContainerGap());
    }

    void update(FileObject fob) {
        setLastRelatedFileObject(fob);
        update();
    }

    private void update() {
        if (EventQueue.isDispatchThread()) {
            PageModel pageModel = PageInspectorImpl.getDefault().getPage();
            FileObject lastRelatedFileObject = getLastRelatedFileObject();
            FileObject inspectedFileObject = getInspectedFileObject();
            if (pageModel != null
                    && (inspectedFileObject == null
                        || (lastRelatedFileObject != null
                            && (DependentFileQuery.isDependent(inspectedFileObject, lastRelatedFileObject)
                                || (FileOwnerQuery.getOwner(inspectedFileObject) == FileOwnerQuery.getOwner(lastRelatedFileObject)
                                    && Utilities.isServerSideMimeType(inspectedFileObject.getMIMEType())
                                    && Utilities.isServerSideMimeType(lastRelatedFileObject.getMIMEType())))))) {
                removeAll();
                PageModel.CSSStylesView stylesView = pageModel.getCSSStylesView();
                add(stylesView.getView(), BorderLayout.CENTER);
                lookup.setView(pageModel.getCSSStylesView());
            } else {
                boolean noStylesLabelShown = noStylesLabel.getParent() != null;
                boolean runFilePanelShown = runFilePanel.getParent() != null;
                if ((lastRelatedFileObject == null) ? !noStylesLabelShown : !runFilePanelShown) {
                    removeAll();
                    if (lastRelatedFileObject == null) {
                        add(noStylesLabel, BorderLayout.CENTER);
                    } else {
                        add(runFilePanel, BorderLayout.CENTER);
                    }
                }
                if (lastRelatedFileObject != null) {
                    String text = NbBundle.getMessage(
                            CssStylesPanelProviderImpl.class,
                            "CssStylesPanelProviderImpl.runFileButton", // NOI18N
                            lastRelatedFileObject.getNameExt());
                    runButton.setText(text);
                    boolean enabled = false;
                    ActionProvider provider = actionProviderForFileObject(lastRelatedFileObject);
                    if (provider != null) {
                        Lookup context = contextForFileObject(lastRelatedFileObject);
                        enabled = provider.isActionEnabled(ActionProvider.COMMAND_RUN_SINGLE, context);
                    }
                    runButton.setEnabled(enabled);
                }
                lookup.setView(null);
            }
            revalidate();
            repaint();
        } else {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    update();
                }
            });
        }
    }

    /**
     * Sets the last related file.
     * 
     * @param fob the last related file.
     */
    private void setLastRelatedFileObject(FileObject fob) {
        synchronized (this) {
            lastRelatedFOB = fob;
        }
    }

    /**
     * Returns the last related file.
     * 
     * @return the last related file.
     */
    FileObject getLastRelatedFileObject() {
        synchronized (this) {
            return lastRelatedFOB;
        }
    }

    /**
     * Sets the inspected file.
     * 
     * @param fob inspected file.
     */
    private void setInspectedFileObject(FileObject fob) {
        synchronized (this) {
            inspectedFOB = fob;
        }
    }

    /**
     * Returns the inspected file.
     * 
     * @return inspected file.
     */
    FileObject getInspectedFileObject() {
        synchronized (this) {
            return inspectedFOB;
        }
    }

    void activateView() {
        active = true;
        if (currentPageModel != null) {
            currentPageModel.getCSSStylesView().activated();
        }
    }

    void deactivateView() {
        active = false;
        if (currentPageModel != null) {
            currentPageModel.getCSSStylesView().deactivated();
        }
    }

    /**
     * Creates a page inspector listener.
     *
     * @return page inspector listener.
     */
    private PropertyChangeListener createInspectorListener() {
        return new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String propName = evt.getPropertyName();
                if (PageInspectorImpl.PROP_MODEL.equals(propName)) {
                    PageModel pageModel = PageInspectorImpl.getDefault().getPage();
                    update(pageModel);
                }
            }
        };
    }

    private void update(final PageModel pageModel) {
        currentPageModel = pageModel;
        if (EventQueue.isDispatchThread()) {
            RP.post(new Runnable() {
                @Override
                public void run() {
                    update(pageModel);
                }
            });
            return;
        }
        if (pageModel != null) {
            pageModel.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    String propName = evt.getPropertyName();
                    if (Page.PROP_DOCUMENT.equals(propName)) {
                        if (pageModel == currentPageModel) {
                            setInspectedFileObject(Utilities.inspectedFileObject(pageModel));
                            update();
                        }
                    }
                }
            });
            setInspectedFileObject(Utilities.inspectedFileObject(pageModel));
            PageModel.CSSStylesView view = pageModel.getCSSStylesView();
            if (active) {
                view.activated();
            } else {
                view.deactivated();
            }
        }
        update();
    }

    /**
     * Returns an action provider for the specified {@code FileObject}.
     *
     * @return {@code ActionProvider} for the specified {@code FileObject}.
     */
    private static ActionProvider actionProviderForFileObject(FileObject fileObject) {
        ActionProvider provider = null;
        Project project = FileOwnerQuery.getOwner(fileObject);
        if (project != null) {
            Lookup lkp = project.getLookup();
            provider = lkp.lookup(ActionProvider.class);
        }
        return provider;
    }

    /**
     * Returns context (to pass to action provider) for the specified file object.
     * 
     * @param fileObject file object for which the context should be returned.
     * @return context for the specified file object.
     */
    private static Lookup contextForFileObject(FileObject fileObject) {
        Lookup context;
        if (fileObject == null) {
            context = Lookup.EMPTY;
        } else {
            try {
                DataObject dob = DataObject.find(fileObject);
                context = Lookups.fixed(fileObject, dob);
            } catch (DataObjectNotFoundException donfex) {
                context = Lookups.singleton(fileObject);
            }
        }
        return context;
    }

    @NbBundle.Messages({
        "CTL_CssStylesProviderImpl.selection.view.title=Selection" // NOI18N
    })
    @ServiceProvider(service = CssStylesPanelProvider.class, position = 1000)
    public static class SelectionView extends CssStylesPanelProviderImpl {

        private static final String SELECTION_PANEL_ID = "selection"; //NOI18N
        private static final Collection<String> MIME_TYPES = new HashSet(Arrays.asList(new String[]{"text/html", "text/xhtml", "text/x-jsp", "text/x-php5"})); // NOI18N

        @Override
        public String getPanelID() {
            return SELECTION_PANEL_ID;
        }

        @Override
        public String getPanelDisplayName() {
            return Bundle.CTL_CssStylesProviderImpl_selection_view_title();
        }

        Lookup lookup;
        Lookup.Result<FileObject> result;
        LookupListener listener;

        @Override
        public JComponent getContent(Lookup lookup) {
            final Lookup.Result<FileObject> result = lookup.lookupResult(FileObject.class);
            this.lookup = lookup;
            this.result = result;
            this.listener = new LookupListener() {
                @Override
                public void resultChanged(LookupEvent ev) {
                    update(result);
                }
            };
            result.addLookupListener(listener);
            update(result);
            return this;
        }

        void update(Lookup.Result<FileObject> result) {
            Collection<? extends FileObject> fobs = result.allInstances();
            FileObject fob = null;
            if (!fobs.isEmpty()) {
                fob = fobs.iterator().next();
            }
            update(fob);
        }

        @Override
        public Lookup getLookup() {
            return getMatchedRulesLookup();
        }

        @Override
        public void activated() {
            activateView();
        }

        @Override
        public void deactivated() {
            deactivateView();
        }

        @Override
        public boolean providesContentFor(FileObject file) {
            if (!MIME_TYPES.contains(file.getMIMEType())) {
                return false;
            }

            ActionProvider provider = actionProviderForFileObject(file);
            if (provider == null) {
                return false;
            }

            Lookup context = contextForFileObject(file);
            return provider.isActionEnabled(ActionProvider.COMMAND_RUN_SINGLE, context);
        }
    }

    @ServiceProvider(service = CssStylesListener.class)
    public static class WebCssStylesPanelListener implements CssStylesListener {

        @Override
        public void ruleSelected(final Rule rule) {
            //rule selected in document view...
            final PageModel pageModel = PageInspectorImpl.getDefault().getPage();
            if (pageModel != null) {
                RP.post(new Runnable() {
                    @Override
                    public void run() {
                        FileObject file = Utilities.inspectedFileObject(pageModel);
                        if (file != null) {
                            final Model model = rule.getModel();
                            model.runReadTask(new Model.ModelTask() {
                                @Override
                                public void run(StyleSheet styleSheet) {
                                    final String elementSource = model.getElementSource(rule.getSelectorsGroup()).toString();
                                    RP.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            pageModel.setSelectedSelector(elementSource);
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        }

    }

    /**
     * Wrapper for the lookup of the current view.
     */
    static class MatchedRulesLookup extends ProxyLookup {

        /**
         * Sets the current view.
         *
         * @param view current view.
         */
        void setView(PageModel.CSSStylesView view) {
            if (view == null) {
                setLookups();
            } else {
                setLookups(view.getLookup());
            }
        }
    }
}
