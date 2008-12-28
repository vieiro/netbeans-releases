/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.repository.ui;

import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 *
 * @author mkleint
 */
public class ProjectInfoPanel extends TopComponent implements MultiViewElement, LookupListener {
    private MultiViewElementCallback callback;
    private Result<MavenProject> result;

    /** Creates new form ProjectInfoPanel */
    public ProjectInfoPanel(Lookup lookup) {
        super(lookup);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        lblProjectName = new javax.swing.JLabel();
        txtProjectName = new javax.swing.JTextField();
        lblDescription = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taDescription = new javax.swing.JTextArea();
        lblProjectHome = new javax.swing.JLabel();
        btnProjectHome = new javax.swing.JButton();
        pnlIssues = new javax.swing.JPanel();
        lblSystem = new javax.swing.JLabel();
        txtSystem = new javax.swing.JTextField();
        lblIssues = new javax.swing.JLabel();
        btnIssues = new javax.swing.JButton();
        pnlScm = new javax.swing.JPanel();
        lblScmUrl = new javax.swing.JLabel();
        btnScmUrl = new javax.swing.JButton();
        lblConnection = new javax.swing.JLabel();
        txtConnection = new javax.swing.JTextField();
        lblDevConnection = new javax.swing.JLabel();
        txtDevConnection = new javax.swing.JTextField();
        pnlCim = new javax.swing.JPanel();
        lblCimSystem = new javax.swing.JLabel();
        txtCimSystem = new javax.swing.JTextField();
        lblCimUrl = new javax.swing.JLabel();
        btnCimUrl = new javax.swing.JButton();
        pnlLicense = new javax.swing.JPanel();
        pnlMailingLists = new javax.swing.JPanel();

        lblProjectName.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblProjectName.text")); // NOI18N

        txtProjectName.setEditable(false);

        lblDescription.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblDescription.text")); // NOI18N

        taDescription.setColumns(20);
        taDescription.setEditable(false);
        taDescription.setRows(5);
        jScrollPane1.setViewportView(taDescription);

        lblProjectHome.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblProjectHome.text")); // NOI18N

        btnProjectHome.setText("prj url"); // NOI18N
        btnProjectHome.setBorder(null);
        btnProjectHome.setBorderPainted(false);
        btnProjectHome.setContentAreaFilled(false);
        btnProjectHome.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);

        pnlIssues.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "TIT_Issues"))); // NOI18N

        lblSystem.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblSystem.text")); // NOI18N

        txtSystem.setEditable(false);

        lblIssues.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblIssues.text")); // NOI18N

        btnIssues.setText("isssue tracking url"); // NOI18N
        btnIssues.setBorder(null);
        btnIssues.setBorderPainted(false);
        btnIssues.setContentAreaFilled(false);
        btnIssues.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);

        org.jdesktop.layout.GroupLayout pnlIssuesLayout = new org.jdesktop.layout.GroupLayout(pnlIssues);
        pnlIssues.setLayout(pnlIssuesLayout);
        pnlIssuesLayout.setHorizontalGroup(
            pnlIssuesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlIssuesLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlIssuesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblSystem)
                    .add(lblIssues))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlIssuesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btnIssues, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
                    .add(txtSystem, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlIssuesLayout.setVerticalGroup(
            pnlIssuesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlIssuesLayout.createSequentialGroup()
                .add(pnlIssuesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblSystem)
                    .add(txtSystem, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlIssuesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnIssues)
                    .add(lblIssues))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pnlScm.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "TIT_SCM"))); // NOI18N

        lblScmUrl.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblScmUrl.text")); // NOI18N

        btnScmUrl.setText("scm url"); // NOI18N
        btnScmUrl.setBorder(null);
        btnScmUrl.setBorderPainted(false);
        btnScmUrl.setContentAreaFilled(false);
        btnScmUrl.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);

        lblConnection.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblConnection.text")); // NOI18N

        txtConnection.setEditable(false);

        lblDevConnection.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblDevConnection.text")); // NOI18N

        txtDevConnection.setEditable(false);

        org.jdesktop.layout.GroupLayout pnlScmLayout = new org.jdesktop.layout.GroupLayout(pnlScm);
        pnlScm.setLayout(pnlScmLayout);
        pnlScmLayout.setHorizontalGroup(
            pnlScmLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlScmLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlScmLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblScmUrl)
                    .add(lblConnection)
                    .add(lblDevConnection))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlScmLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtConnection, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                    .add(btnScmUrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                    .add(txtDevConnection, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlScmLayout.setVerticalGroup(
            pnlScmLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlScmLayout.createSequentialGroup()
                .add(pnlScmLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblScmUrl)
                    .add(btnScmUrl))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlScmLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblConnection)
                    .add(txtConnection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlScmLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblDevConnection)
                    .add(txtDevConnection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlCim.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "TIT_CIManagement"))); // NOI18N

        lblCimSystem.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblCimSystem.text")); // NOI18N

        txtCimSystem.setEditable(false);

        lblCimUrl.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblCimUrl.text")); // NOI18N

        btnCimUrl.setText("cim url"); // NOI18N
        btnCimUrl.setBorder(null);
        btnCimUrl.setBorderPainted(false);
        btnCimUrl.setContentAreaFilled(false);
        btnCimUrl.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);

        org.jdesktop.layout.GroupLayout pnlCimLayout = new org.jdesktop.layout.GroupLayout(pnlCim);
        pnlCim.setLayout(pnlCimLayout);
        pnlCimLayout.setHorizontalGroup(
            pnlCimLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 617, Short.MAX_VALUE)
            .add(pnlCimLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlCimLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblCimSystem)
                    .add(lblCimUrl))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCimLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btnCimUrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
                    .add(txtCimSystem, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlCimLayout.setVerticalGroup(
            pnlCimLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 52, Short.MAX_VALUE)
            .add(pnlCimLayout.createSequentialGroup()
                .add(pnlCimLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblCimSystem)
                    .add(txtCimSystem, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCimLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCimUrl)
                    .add(lblCimUrl))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlLicense.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "TIT_Licenses"))); // NOI18N
        pnlLicense.setLayout(new java.awt.GridLayout(1, 1));

        pnlMailingLists.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "TIT_MailingLists"))); // NOI18N
        pnlMailingLists.setLayout(new java.awt.GridLayout(1, 1));

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlMailingLists, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlLicense, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4Layout.createSequentialGroup()
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblProjectName)
                            .add(lblDescription))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                            .add(txtProjectName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)))
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(lblProjectHome)
                        .add(18, 18, 18)
                        .add(btnProjectHome, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlIssues, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlScm, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlCim, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblProjectName)
                    .add(txtProjectName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblDescription)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnProjectHome, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblProjectHome))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(pnlIssues, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(pnlScm, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(pnlCim, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(pnlLicense, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(pnlMailingLists, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(85, 85, 85))
        );

        jScrollPane2.setViewportView(jPanel4);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCimUrl;
    private javax.swing.JButton btnIssues;
    private javax.swing.JButton btnProjectHome;
    private javax.swing.JButton btnScmUrl;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCimSystem;
    private javax.swing.JLabel lblCimUrl;
    private javax.swing.JLabel lblConnection;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblDevConnection;
    private javax.swing.JLabel lblIssues;
    private javax.swing.JLabel lblProjectHome;
    private javax.swing.JLabel lblProjectName;
    private javax.swing.JLabel lblScmUrl;
    private javax.swing.JLabel lblSystem;
    private javax.swing.JPanel pnlCim;
    private javax.swing.JPanel pnlIssues;
    private javax.swing.JPanel pnlLicense;
    private javax.swing.JPanel pnlMailingLists;
    private javax.swing.JPanel pnlScm;
    private javax.swing.JTextArea taDescription;
    private javax.swing.JTextField txtCimSystem;
    private javax.swing.JTextField txtConnection;
    private javax.swing.JTextField txtDevConnection;
    private javax.swing.JTextField txtProjectName;
    private javax.swing.JTextField txtSystem;
    // End of variables declaration//GEN-END:variables

    public JComponent getVisualRepresentation() {
        return this;
    }

    public JComponent getToolbarRepresentation() {
        return new JPanel();
    }


    @Override
    public void componentOpened() {
        super.componentOpened();
        result = getLookup().lookup(new Lookup.Template<MavenProject>(MavenProject.class));
        populateFields();
        result.addLookupListener(this);
    }

    @Override
    public void componentClosed() {
        super.componentClosed();
        result.removeLookupListener(this);
    }

    @Override
    public void componentShowing() {
        super.componentShowing();
    }

    @Override
    public void componentHidden() {
        super.componentHidden();
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
    }

    @Override
    public void componentDeactivated() {
        super.componentDeactivated();
    }


    public void setMultiViewCallback(MultiViewElementCallback callback) {
        this.callback = callback;
    }

    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    private void populateFields() {
        boolean loading = true;
        Iterator<? extends MavenProject> iter = result.allInstances().iterator();
        String name = null, desc = null, homeUrl = null;
        String imUrl = null, imSystem = null;
        String scmUrl = null, scmConn = null, scmDevConn = null;
        String cimSystem = null, cimUrl = null;
        if (iter.hasNext()) {
            loading = false;
            MavenProject prj = iter.next();
            name = prj.getName();
            desc = prj.getDescription();
            homeUrl = prj.getUrl();
            IssueManagement im = prj.getIssueManagement();
            if (im != null) {
                imUrl = im.getUrl();
                imSystem = im.getSystem();
            }
            Scm scm = prj.getScm();
            if (scm != null) {
                scmUrl = scm.getUrl();
                scmConn = scm.getConnection();
                scmDevConn = scm.getDeveloperConnection();
            }
            CiManagement cim = prj.getCiManagement();
            if (cim != null) {
                cimSystem = cim.getSystem();
                cimUrl = cim.getUrl();
            }
            @SuppressWarnings("unchecked")
            List<License> licenses = prj.getLicenses();
            if (licenses != null) {
                GridLayout layout = (GridLayout)pnlLicense.getLayout();
                layout.setColumns(1);
                layout.setRows(licenses.size());
                for (License lic : licenses) {
                    LicensePanel pnl = new LicensePanel();
                    setPlainText(pnl.txtName, lic.getName(), loading);
                    setLinkedText(pnl.btnURL, lic.getUrl(), loading);
                    pnlLicense.add(pnl);
                }
            }
            @SuppressWarnings("unchecked")
            List<MailingList> mailings = prj.getMailingLists();
            if (mailings != null) {
                GridLayout layout = (GridLayout)pnlMailingLists.getLayout();
                layout.setColumns(1);
                layout.setRows(mailings.size());
                for (MailingList list : mailings) {
                    MailingListPanel pnl = new MailingListPanel();
                    setPlainText(pnl.txtName, list.getName(), loading);
                    setLinkedText(pnl.btnArchive, list.getArchive(), loading);
                    setPlainText(pnl.txtSubscribe, list.getSubscribe(), loading);
                    setPlainText(pnl.txtUnsubscribe, list.getUnsubscribe(), loading);
                    pnlMailingLists.add(pnl);
                }
            }
        }
        setPlainText(txtProjectName, name, loading); 
        setPlainText(taDescription, desc, loading); 
        setLinkedText(btnProjectHome, homeUrl, loading);
        
        setLinkedText(btnIssues, imUrl, loading);
        setPlainText(txtSystem, imSystem, loading); 

        setLinkedText(btnScmUrl, scmUrl, loading);
        setPlainText(txtConnection, scmConn, loading);
        setPlainText(txtDevConnection, scmDevConn, loading);

        setLinkedText(btnCimUrl, cimUrl, loading);
        setPlainText(txtCimSystem, cimSystem, loading);

    }

    public void resultChanged(LookupEvent ev) {
        populateFields();
    }

    private void setLinkedText(JButton btn, String url, boolean loading) {
        if (url == null) {
            btn.setAction(null);
            if (loading) {
                btn.setText(NbBundle.getMessage(ProjectInfoPanel.class, "LBL_Loading"));
            } else {
                btn.setText(NbBundle.getMessage(ProjectInfoPanel.class, "LBL_Undefined"));
            }
            btn.setCursor(null);
        } else {
            btn.setAction(new LinkAction(url));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setText("<html><a href=\"\">" + url + "</a></html>");
        }
    }

    private void setPlainText(JTextComponent field, String value, boolean loading) {
        if (value == null) {
            if (loading) {
                field.setText(NbBundle.getMessage(ProjectInfoPanel.class, "LBL_Loading"));
            } else {
                field.setText(NbBundle.getMessage(ProjectInfoPanel.class, "LBL_Undefined"));
            }
        } else {
            field.setText(value);
        }
    }

    private class LinkAction extends AbstractAction {
        private String url;

        public LinkAction(String url) {
            this.url = url;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                URL u = new URL(url);
                HtmlBrowser.URLDisplayer.getDefault().showURL(u);
            } catch (MalformedURLException ex) {
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(ProjectInfoPanel.class, "ERR_WrongURL", url));
            }
        }

    }
}
