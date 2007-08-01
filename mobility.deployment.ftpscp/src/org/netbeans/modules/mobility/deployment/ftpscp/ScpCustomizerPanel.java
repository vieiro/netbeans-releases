/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * ScpCustomizerPanel.java
 *
 * Created on 10. prosinec 2004, 16:46
 */
package org.netbeans.modules.mobility.deployment.ftpscp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.Font;
import javax.swing.JFileChooser;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.util.NbBundle;

/**
 *
 * @author  Adam Sotona
 */
public class ScpCustomizerPanel extends javax.swing.JPanel implements ActionListener, ChangeListener {
    
    /** Creates new form ScpCustomizerPanel */
    public ScpCustomizerPanel() {
        initComponents();
        jSpinnerPort.setEditor(new JSpinner.NumberEditor(jSpinnerPort, "#0")); //NOI18N
        jTextArea1.setFont(jTextArea1.getFont().deriveFont(Font.ITALIC));
        jRadioButtonPassword.addActionListener(this);
        jRadioButtonPassword.addChangeListener(this);
        jRadioButtonKey.addActionListener(this);
        jRadioButtonKey.addChangeListener(this);
    }
    
    public void stateChanged(@SuppressWarnings("unused")
	final ChangeEvent e) {
        actionPerformed(null);
    }
    
    public void actionPerformed(@SuppressWarnings("unused")
	final ActionEvent actionEvent) {
        boolean b = jRadioButtonPassword.isEnabled() && jRadioButtonPassword.isSelected();
        jLabelPassword.setEnabled(b);
        jPasswordFieldPassword.setEnabled(b);
        jPasswordFieldPassword.setEditable(b);
        b = jRadioButtonKey.isEnabled() && jRadioButtonKey.isSelected();
        jLabelKeyfile.setEnabled(b);
        jTextFieldKeyfile.setEnabled(b);
        jTextFieldKeyfile.setEditable(b);
        jButtonBrowse.setEnabled(b);
        jLabelPassphrase.setEnabled(b);
        jPasswordFieldPassphrase.setEnabled(b);
        jPasswordFieldPassphrase.setEditable(b);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabelServer = new javax.swing.JLabel();
        jTextFieldServer = new javax.swing.JTextField();
        jLabelPort = new javax.swing.JLabel();
        jSpinnerPort = new javax.swing.JSpinner();
        jLabelUser = new javax.swing.JLabel();
        jTextFieldUser = new javax.swing.JTextField();
        jRadioButtonPassword = new javax.swing.JRadioButton();
        jLabelPassword = new javax.swing.JLabel();
        jPasswordFieldPassword = new javax.swing.JPasswordField();
        jRadioButtonKey = new javax.swing.JRadioButton();
        jLabelKeyfile = new javax.swing.JLabel();
        jTextFieldKeyfile = new javax.swing.JTextField();
        jButtonBrowse = new javax.swing.JButton();
        jLabelPassphrase = new javax.swing.JLabel();
        jPasswordFieldPassphrase = new javax.swing.JPasswordField();
        jTextArea1 = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        jLabelServer.setLabelFor(jTextFieldServer);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelServer, NbBundle.getMessage(ScpCustomizerPanel.class, "LBL_Scp_Server")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabelServer, gridBagConstraints);

        jTextFieldServer.setName(ScpDeploymentPlugin.PROP_SERVER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(jTextFieldServer, gridBagConstraints);
        jTextFieldServer.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ScpCustomizerPanel.class, "ACSD_Scp_Server")); // NOI18N

        jLabelPort.setLabelFor(jSpinnerPort);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelPort, NbBundle.getMessage(ScpCustomizerPanel.class, "LBL_Scp_Port")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 0, 0);
        add(jLabelPort, gridBagConstraints);
        jLabelPort.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ScpCustomizerPanel.class, "ACSD_Scp_Port")); // NOI18N

        jSpinnerPort.setName(ScpDeploymentPlugin.PROP_PORT);
        jSpinnerPort.setPreferredSize(new java.awt.Dimension(54, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(jSpinnerPort, gridBagConstraints);

        jLabelUser.setLabelFor(jTextFieldUser);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelUser, NbBundle.getMessage(ScpCustomizerPanel.class, "LBL_Scp_User")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jLabelUser, gridBagConstraints);

        jTextFieldUser.setName(ScpDeploymentPlugin.PROP_USERID);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jTextFieldUser, gridBagConstraints);
        jTextFieldUser.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ScpCustomizerPanel.class, "ACSD_Scp_user")); // NOI18N

        buttonGroup1.add(jRadioButtonPassword);
        org.openide.awt.Mnemonics.setLocalizedText(jRadioButtonPassword, NbBundle.getMessage(ScpCustomizerPanel.class, "LBL_Scp_UsePassword")); // NOI18N
        jRadioButtonPassword.setActionCommand("no");
        jRadioButtonPassword.setName(ScpDeploymentPlugin.PROP_USE_KEYFILE);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
        add(jRadioButtonPassword, gridBagConstraints);

        jLabelPassword.setLabelFor(jPasswordFieldPassword);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelPassword, NbBundle.getMessage(ScpCustomizerPanel.class, "LBL_Scp_Password")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jLabelPassword, gridBagConstraints);

        jPasswordFieldPassword.setName(ScpDeploymentPlugin.PROP_PASSWORD);
        jPasswordFieldPassword.setPreferredSize(new java.awt.Dimension(6, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jPasswordFieldPassword, gridBagConstraints);
        jPasswordFieldPassword.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ScpCustomizerPanel.class, "ACSD_Scp_Password")); // NOI18N

        buttonGroup1.add(jRadioButtonKey);
        org.openide.awt.Mnemonics.setLocalizedText(jRadioButtonKey, NbBundle.getMessage(ScpCustomizerPanel.class, "LBL_Scp_UseKeyfile")); // NOI18N
        jRadioButtonKey.setActionCommand("yes");
        jRadioButtonKey.setName(ScpDeploymentPlugin.PROP_USE_KEYFILE);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
        add(jRadioButtonKey, gridBagConstraints);

        jLabelKeyfile.setLabelFor(jTextFieldKeyfile);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelKeyfile, NbBundle.getMessage(ScpCustomizerPanel.class, "LBL_Scp_Keyfile")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jLabelKeyfile, gridBagConstraints);

        jTextFieldKeyfile.setName(ScpDeploymentPlugin.PROP_KEYFILE);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jTextFieldKeyfile, gridBagConstraints);
        jTextFieldKeyfile.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ScpCustomizerPanel.class, "ACSD_Scp_Keyfile")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonBrowse, NbBundle.getMessage(ScpCustomizerPanel.class, "LBL_Scp_Browse")); // NOI18N
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jButtonBrowse, gridBagConstraints);
        jButtonBrowse.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ScpCustomizerPanel.class, "ACSD_Scp_Browse")); // NOI18N

        jLabelPassphrase.setLabelFor(jPasswordFieldPassphrase);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelPassphrase, NbBundle.getMessage(ScpCustomizerPanel.class, "LBL_Scp_Passphrase")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jLabelPassphrase, gridBagConstraints);

        jPasswordFieldPassphrase.setName(ScpDeploymentPlugin.PROP_PASSPHRASE);
        jPasswordFieldPassphrase.setPreferredSize(new java.awt.Dimension(6, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jPasswordFieldPassphrase, gridBagConstraints);
        jPasswordFieldPassphrase.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ScpCustomizerPanel.class, "ACSD_Scp_Passphrase")); // NOI18N

        jTextArea1.setBackground(getBackground());
        jTextArea1.setEditable(false);
        jTextArea1.setLineWrap(true);
        jTextArea1.setText(NbBundle.getMessage(ScpCustomizerPanel.class, "LBL_Scp_Notice")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
        add(jTextArea1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
        final String oldValue = jTextFieldKeyfile.getText();
        final JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (oldValue != null) chooser.setSelectedFile(new File(oldValue));
        chooser.setDialogTitle(NbBundle.getMessage(ScpCustomizerPanel.class, "Title_Scp_SelectKeyfile"));//NOI18N
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            jTextFieldKeyfile.setText(chooser.getSelectedFile().getAbsolutePath());
        }
        
    }//GEN-LAST:event_jButtonBrowseActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    javax.swing.JButton jButtonBrowse;
    javax.swing.JLabel jLabelKeyfile;
    javax.swing.JLabel jLabelPassphrase;
    javax.swing.JLabel jLabelPassword;
    javax.swing.JLabel jLabelPort;
    javax.swing.JLabel jLabelServer;
    javax.swing.JLabel jLabelUser;
    javax.swing.JPasswordField jPasswordFieldPassphrase;
    javax.swing.JPasswordField jPasswordFieldPassword;
    javax.swing.JRadioButton jRadioButtonKey;
    javax.swing.JRadioButton jRadioButtonPassword;
    javax.swing.JSpinner jSpinnerPort;
    javax.swing.JTextArea jTextArea1;
    javax.swing.JTextField jTextFieldKeyfile;
    javax.swing.JTextField jTextFieldServer;
    javax.swing.JTextField jTextFieldUser;
    // End of variables declaration//GEN-END:variables
    
}
