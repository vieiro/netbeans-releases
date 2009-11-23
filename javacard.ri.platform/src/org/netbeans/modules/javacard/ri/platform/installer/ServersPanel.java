/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

/*
 * ServersPanel2.java
 *
 * Created on Nov 23, 2009, 8:02:16 AM
 */

package org.netbeans.modules.javacard.ri.platform.installer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ListSelectionModel;
import org.netbeans.api.validation.adapters.DialogBuilder;
import org.netbeans.modules.javacard.common.CommonSystemFilesystemPaths;
import org.netbeans.modules.javacard.common.GuiUtils;
import org.netbeans.modules.javacard.common.Utils;
import org.netbeans.modules.javacard.spi.AddCardHandler;
import org.netbeans.modules.javacard.spi.Card;
import org.netbeans.modules.javacard.spi.CardCustomizer;
import org.netbeans.modules.javacard.spi.JavacardPlatform;
import org.netbeans.modules.javacard.spi.actions.CardActions;
import org.netbeans.modules.javacard.spi.capabilities.CardCustomizerProvider;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.openide.DialogDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.ListView;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tim Boudreau
 */
public class ServersPanel extends javax.swing.JPanel implements ExplorerManager.Provider, PropertyChangeListener, AddCardHandler.CardCreatedCallback, Lookup.Provider {
    private final ExplorerManager mgr = new ExplorerManager();
    private final JavacardPlatform pform;
    private final ValidationGroup grp = ValidationGroup.create();
    private Lookup lkp;
    public ServersPanel(JavacardPlatform pform) {
        this.pform = pform;
        mgr.setRootContext(new AbstractNode(pform.getCards().createChildren(), Lookups.fixed(pform)));
        initComponents();
        ((ListView) view).setPopupAllowed(false);
        ((ListView) view).setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        GuiUtils.prepareContainer(this);
        addButton.setEnabled(AddCardHandler.createAddDeviceAction(Utils.findPlatformDataObjectNamed(pform.getSystemName()), pform, this) != null);
        removeButton.setAction(CardActions.createDeleteAction().createContextAwareInstance(lkp = ExplorerUtils.createLookup(mgr, getActionMap())));
        mgr.addPropertyChangeListener(this);
    }

    public void showDialog() {
        DialogBuilder b = new DialogBuilder(ServersPanel.class).setModal(true).
                setContent(this).setValidationGroup(grp);
        if (b.showDialog(DialogDescriptor.OK_OPTION)) {
            save();
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        final Node[] n = mgr.getRootContext().getChildren().getNodes();
        if (n != null && n.length > 0) {
            try {
                mgr.setSelectedNodes(new Node[]{n[0]});
            } catch (PropertyVetoException ex) {
                //do nothing
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        view = new ListView();
        customizerPanel = new javax.swing.JPanel();
        noCustomizerLabel = new javax.swing.JLabel();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        jLabel1 = grp.createProblemLabel();

        view.setBorder(new javax.swing.border.LineBorder(javax.swing.UIManager.getDefaults().getColor("controlShadow"), 1, true));

        customizerPanel.setLayout(new java.awt.BorderLayout());

        noCustomizerLabel.setText(org.openide.util.NbBundle.getMessage(ServersPanel.class, "ServersPanel.noCustomizerLabel.text")); // NOI18N
        customizerPanel.add(noCustomizerLabel, java.awt.BorderLayout.CENTER);

        addButton.setText(org.openide.util.NbBundle.getMessage(ServersPanel.class, "ServersPanel.addButton.text")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onAdd(evt);
            }
        });

        removeButton.setText(org.openide.util.NbBundle.getMessage(ServersPanel.class, "ServersPanel.removeButton.text")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onRemove(evt);
            }
        });

        jLabel1.setText(org.openide.util.NbBundle.getMessage(ServersPanel.class, "ServersPanel.jLabel1.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(view, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 256, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(customizerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(addButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(removeButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(20, 20, 20)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, customizerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, view, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(addButton)
                    .add(removeButton)
                    .add(jLabel1))
                .add(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void onAdd(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onAdd
        Action a = AddCardHandler.createAddDeviceAction(Utils.findPlatformDataObjectNamed(pform.getSystemName()), pform, this);
        if (a != null) {
            a.actionPerformed(evt);
        }
    }//GEN-LAST:event_onAdd

    private void onRemove(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onRemove
        
    }//GEN-LAST:event_onRemove


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel customizerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel noCustomizerLabel;
    private javax.swing.JButton removeButton;
    private javax.swing.JScrollPane view;
    // End of variables declaration//GEN-END:variables

    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt == null || ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            Node[] n = mgr.getSelectedNodes();
            if (n == null || n.length != 1) {
                clear();
                return;
            }
            Node nd = n[0];
            Card card = nd.getLookup().lookup(Card.class);
            if (card == null) {
                clear();
                return;
            }
            CardCustomizer cust = findCustomizer(nd, card);
            if (cust != null) {
                ValidationGroup group = cust.getValidationGroup();
                if (group != null) {
                    try {
                        //XXX use url boolean below does not actually work
                        this.grp.addValidationGroup(group, false);
                    } catch (AssertionError e) {
                        //XXX fixed in next rev of validation api - cannot add twice
                        Logger.getLogger(ServersPanel.class.getName()).log(Level.INFO, null, e);
                    } catch (Throwable e) {
                        //XXX fixed in next rev of validation api - cannot add twice
                        Logger.getLogger(ServersPanel.class.getName()).log(Level.INFO, null, e);
                    }
                }
                Component comp = cust.getComponent();
                setCustomizerComponent(comp);
            } else {
                if (nd.hasCustomizer()) {
                    setCustomizerComponent(nd.getCustomizer());
                }
            }
        }
    }

    private void save() {
        for (CardCustomizer c : cache.values()) {
            if (c.isContentValid()) {
                c.save();
            }
        }
        cache.clear();
    }

    private Map<Node, CardCustomizer> cache = new HashMap<Node, CardCustomizer>();
    private CardCustomizer findCustomizer(Node n, Card card) {
        //Okay, we've got way too many ways to do this...
        CardCustomizer result = cache.get(n);
        if (result == null) {
            CardCustomizerProvider prov = card.getCapability(CardCustomizerProvider.class);
            if (prov == null) {
                prov = n.getLookup().lookup(CardCustomizerProvider.class);
            }
            if (prov == null) {
                prov = Lookups.forPath(CommonSystemFilesystemPaths.SFS_ADD_HANDLER_REGISTRATION_ROOT +
                        pform.getPlatformKind()).lookup(CardCustomizerProvider.class);
            }
            if (prov != null) {
                result = prov.getCardCustomizer(card);
                if (result != null) {
                    cache.put(n, result);
                }
            }
        }
        return result;
    }

    private void clear() {
        setCustomizerComponent(null);
    }

    private void setCustomizerComponent(Component c) {
        c = c == null ? noCustomizerLabel : c;
        customizerPanel.removeAll();
        customizerPanel.add(c, BorderLayout.CENTER);
        customizerPanel.invalidate();
        customizerPanel.revalidate();
        customizerPanel.repaint();
        if (c != noCustomizerLabel) {
            if (getTopLevelAncestor() instanceof Dialog) {
                ((Dialog) getTopLevelAncestor()).pack();
                ((Dialog) getTopLevelAncestor()).setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
            }
        }
    }

    public void onCardCreated(final Card card, final FileObject file) {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                for (Node nd : mgr.getRootContext().getChildren().getNodes(true)) {
                    if (card.equals(nd.getLookup().lookup(Card.class))) {
                        try {
                            mgr.setSelectedNodes(new Node[]{nd});
                        } catch (PropertyVetoException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        return;
                    }
                }
            }
        });
    }

    public Lookup getLookup() {
        return lkp;
    }

}
