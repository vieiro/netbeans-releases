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
 * Software is Sun Microsystems, Inc. Portions Copyright 2006 Sun
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

package org.netbeans.modules.websvc.wsitconf.ui.service.profiles;

import org.netbeans.modules.websvc.wsitconf.spi.SecurityProfile;
import org.netbeans.modules.websvc.wsitconf.spi.features.SecureConversationFeature;
import org.netbeans.modules.websvc.wsitconf.ui.ComboConstants;
import org.netbeans.modules.websvc.wsitconf.wsdlmodelext.AlgoSuiteModelHelper;
import org.netbeans.modules.websvc.wsitconf.wsdlmodelext.PolicyModelHelper;
import org.netbeans.modules.websvc.wsitconf.wsdlmodelext.SecurityPolicyModelHelper;
import org.netbeans.modules.websvc.wsitconf.wsdlmodelext.SecurityTokensModelHelper;
import org.netbeans.modules.websvc.wsitmodelext.policy.Policy;
import org.netbeans.modules.websvc.wsitmodelext.security.BootstrapPolicy;
import org.netbeans.modules.websvc.wsitmodelext.security.tokens.ProtectionToken;
import org.netbeans.modules.websvc.wsitmodelext.security.tokens.SecureConversationToken;
import org.netbeans.modules.websvc.wsitmodelext.versioning.ConfigVersion;
import org.netbeans.modules.xml.wsdl.model.WSDLComponent;

/**
 *
 * @author  Martin Grebac
 */
public class UsernameAuthentication extends ProfileBaseForm {

    /**
     * Creates new form UsernameAuthentication
     */
    public UsernameAuthentication(WSDLComponent comp, SecurityProfile secProfile) {
        super(comp, secProfile);
        initComponents();
        this.comp = comp;
        this.secProfile = secProfile;

        inSync = true;
        supportTokenCombo.removeAllItems();
        supportTokenCombo.addItem(ComboConstants.X509);
        supportTokenCombo.addItem(ComboConstants.USERNAME);

        fillLayoutCombo(layoutCombo);
        fillAlgoSuiteCombo(algoSuiteCombo);
        inSync = false;
        
        sync();
    }
    
    protected void sync() {
        inSync = true;

        WSDLComponent secBinding = null;
        WSDLComponent topSecBinding = SecurityPolicyModelHelper.getSecurityBindingTypeElement(comp);
        WSDLComponent protTokenKind = SecurityTokensModelHelper.getTokenElement(topSecBinding, ProtectionToken.class);
        WSDLComponent protToken = SecurityTokensModelHelper.getTokenTypeElement(protTokenKind);
        
        boolean secConv = (protToken instanceof SecureConversationToken);
        
        if (secConv) {
            WSDLComponent bootPolicy = SecurityTokensModelHelper.getTokenElement(protToken, BootstrapPolicy.class);
            secBinding = SecurityPolicyModelHelper.getSecurityBindingTypeElement(bootPolicy);
            Policy p = (Policy) secBinding.getParent();
            setChBox(secConvChBox, true);
            setChBox(derivedKeysChBox, SecurityPolicyModelHelper.isRequireDerivedKeys(protToken));
            setChBox(reqSigConfChBox, SecurityPolicyModelHelper.isRequireSignatureConfirmation(p));
            setChBox(encryptSignatureChBox, SecurityPolicyModelHelper.isEncryptSignature(bootPolicy));
            setChBox(encryptOrderChBox, SecurityPolicyModelHelper.isEncryptBeforeSigning(bootPolicy));
            p = PolicyModelHelper.getTopLevelElement(bootPolicy, Policy.class,false);
            WSDLComponent tokenKind = SecurityTokensModelHelper.getSupportingToken(p, SecurityTokensModelHelper.SIGNED_SUPPORTING);
            if (tokenKind == null) {
                tokenKind = SecurityTokensModelHelper.getSupportingToken(p, SecurityTokensModelHelper.SIGNED_ENCRYPTED);
            }
            String tokenType = SecurityTokensModelHelper.getTokenType(tokenKind);
            setCombo(supportTokenCombo, tokenType);
            if (ComboConstants.USERNAME.equals(tokenType)) {
                WSDLComponent token = SecurityTokensModelHelper.getTokenTypeElement(tokenKind);
                setChBox(hashPasswdChBox, SecurityTokensModelHelper.isHashPassword(token));
            }
        } else {
            secBinding = SecurityPolicyModelHelper.getSecurityBindingTypeElement(comp);
            setChBox(secConvChBox, false);
            setChBox(derivedKeysChBox, false);
            setChBox(reqSigConfChBox, SecurityPolicyModelHelper.isRequireSignatureConfirmation(comp));
            setChBox(encryptSignatureChBox, SecurityPolicyModelHelper.isEncryptSignature(comp));
            setChBox(encryptOrderChBox, SecurityPolicyModelHelper.isEncryptBeforeSigning(comp));
            WSDLComponent tokenKind = SecurityTokensModelHelper.getSupportingToken(comp, SecurityTokensModelHelper.SIGNED_SUPPORTING);
            if (tokenKind == null) {
                tokenKind = SecurityTokensModelHelper.getSupportingToken(comp, SecurityTokensModelHelper.SIGNED_ENCRYPTED);
            }
            String tokenType = SecurityTokensModelHelper.getTokenType(tokenKind);
            setCombo(supportTokenCombo, tokenType);
            if (ComboConstants.USERNAME.equals(tokenType)) {
                WSDLComponent token = SecurityTokensModelHelper.getTokenTypeElement(tokenKind);
                setChBox(hashPasswdChBox, SecurityTokensModelHelper.isHashPassword(token));
            }
        }

        WSDLComponent tokenKind = SecurityTokensModelHelper.getTokenElement(secBinding, ProtectionToken.class);
        WSDLComponent token = SecurityTokensModelHelper.getTokenTypeElement(tokenKind);
        setChBox(reqDerivedKeys, SecurityPolicyModelHelper.isRequireDerivedKeys(token));

        setCombo(algoSuiteCombo, AlgoSuiteModelHelper.getAlgorithmSuite(secBinding));
        setCombo(layoutCombo, SecurityPolicyModelHelper.getMessageLayout(secBinding));
        
        enableDisable();
        
        inSync = false;
    }

    public void setValue(javax.swing.JComponent source) {

        if (inSync) return;
            
        WSDLComponent secBinding = null;
        WSDLComponent topSecBinding = SecurityPolicyModelHelper.getSecurityBindingTypeElement(comp);
        WSDLComponent protTokenKind = SecurityTokensModelHelper.getTokenElement(topSecBinding, ProtectionToken.class);
        WSDLComponent protToken = SecurityTokensModelHelper.getTokenTypeElement(protTokenKind);
        
        boolean secConv = (protToken instanceof SecureConversationToken);

        if (source.equals(secConvChBox)) {
            ((SecureConversationFeature)secProfile).enableSecureConversation(comp, secConvChBox.isSelected());
            sync();
        }
        
        int suppTokenType = ((ConfigVersion.CONFIG_1_0.equals(cfgVersion)) || (hashPasswdChBox.isSelected())) ? 
            SecurityTokensModelHelper.SIGNED_SUPPORTING : SecurityTokensModelHelper.SIGNED_ENCRYPTED;
        
        SecurityPolicyModelHelper spmh = SecurityPolicyModelHelper.getInstance(cfgVersion);
        SecurityTokensModelHelper stmh = SecurityTokensModelHelper.getInstance(cfgVersion);
        AlgoSuiteModelHelper asmh = AlgoSuiteModelHelper.getInstance(cfgVersion);
        if (secConv) {
            WSDLComponent bootPolicy = SecurityTokensModelHelper.getTokenElement(protToken, BootstrapPolicy.class);
            secBinding = SecurityPolicyModelHelper.getSecurityBindingTypeElement(bootPolicy);
            Policy p = (Policy) secBinding.getParent();
            if (source.equals(reqSigConfChBox)) {
                spmh.enableRequireSignatureConfirmation(
                        SecurityPolicyModelHelper.getWss11(p), reqSigConfChBox.isSelected());
            }
            if (source.equals(derivedKeysChBox)) {
                spmh.enableRequireDerivedKeys(protToken, derivedKeysChBox.isSelected());
            }
            p = PolicyModelHelper.getTopLevelElement(bootPolicy, Policy.class,false);
            if (source.equals(supportTokenCombo)) {
                String selTokTyp = (String)supportTokenCombo.getSelectedItem();
                stmh.setSupportingTokens(p, selTokTyp, suppTokenType);
            }
        } else {
            secBinding = SecurityPolicyModelHelper.getSecurityBindingTypeElement(comp);
            if (source.equals(reqSigConfChBox)) {
                spmh.enableRequireSignatureConfirmation(SecurityPolicyModelHelper.getWss11(comp), reqSigConfChBox.isSelected());
            }
            if (source.equals(supportTokenCombo)) {
                String selTokTyp = (String)supportTokenCombo.getSelectedItem();
                stmh.setSupportingTokens(comp, selTokTyp, suppTokenType);
            }
        }

        if (source.equals(hashPasswdChBox)) {
            String selTokTyp = (String)supportTokenCombo.getSelectedItem();
            SecurityTokensModelHelper.removeSupportingTokens(comp);
            stmh.setSupportingTokens(comp, selTokTyp, suppTokenType);
            WSDLComponent suppToken = SecurityTokensModelHelper.getSupportingToken(comp, suppTokenType);
            WSDLComponent uToken = SecurityTokensModelHelper.getTokenTypeElement(suppToken);
            stmh.setHashPassword(uToken, hashPasswdChBox.isSelected());
        }
        
        if (source.equals(encryptSignatureChBox)) {
            spmh.enableEncryptSignature(secBinding, encryptSignatureChBox.isSelected());
            if (secConv) {
                spmh.enableEncryptSignature(topSecBinding, encryptSignatureChBox.isSelected());
            }
        }
        if (source.equals(encryptOrderChBox)) {
            spmh.enableEncryptBeforeSigning(secBinding, encryptOrderChBox.isSelected());
            if (secConv) {
                spmh.enableEncryptBeforeSigning(topSecBinding, encryptOrderChBox.isSelected());
            }
        }
        if (source.equals(layoutCombo)) {
            spmh.setLayout(secBinding, (String) layoutCombo.getSelectedItem());
            if (secConv) {
                spmh.setLayout(topSecBinding, (String) layoutCombo.getSelectedItem());
            }
        }
        if (source.equals(algoSuiteCombo)) {
            asmh.setAlgorithmSuite(secBinding, (String) algoSuiteCombo.getSelectedItem());
            if (secConv) {
                asmh.setAlgorithmSuite(topSecBinding, (String) algoSuiteCombo.getSelectedItem());
            }
        }
        if (source.equals(reqDerivedKeys)) {
            WSDLComponent tokenKind = SecurityTokensModelHelper.getTokenElement(secBinding, ProtectionToken.class);
            WSDLComponent token = SecurityTokensModelHelper.getTokenTypeElement(tokenKind);
            spmh.enableRequireDerivedKeys(token, reqDerivedKeys.isSelected());
            return;
        }
        
        enableDisable();
    }

    protected void enableDisable() {
        boolean secConvEnabled = secConvChBox.isSelected();
        derivedKeysChBox.setEnabled(secConvEnabled);
        hashPasswdChBox.setEnabled(ConfigVersion.CONFIG_1_2.equals(cfgVersion) && 
                                   supportTokenCombo.getSelectedItem().equals(ComboConstants.USERNAME));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        secConvChBox = new javax.swing.JCheckBox();
        reqSigConfChBox = new javax.swing.JCheckBox();
        derivedKeysChBox = new javax.swing.JCheckBox();
        algoSuiteLabel = new javax.swing.JLabel();
        algoSuiteCombo = new javax.swing.JComboBox();
        layoutLabel = new javax.swing.JLabel();
        layoutCombo = new javax.swing.JComboBox();
        encryptSignatureChBox = new javax.swing.JCheckBox();
        reqDerivedKeys = new javax.swing.JCheckBox();
        encryptOrderChBox = new javax.swing.JCheckBox();
        supportTokenLabel = new javax.swing.JLabel();
        supportTokenCombo = new javax.swing.JComboBox();
        hashPasswdChBox = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(secConvChBox, org.openide.util.NbBundle.getMessage(UsernameAuthentication.class, "LBL_SecConvLabel")); // NOI18N
        secConvChBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        secConvChBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secConvChBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(reqSigConfChBox, org.openide.util.NbBundle.getMessage(UsernameAuthentication.class, "LBL_RequireSigConfirmation")); // NOI18N
        reqSigConfChBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        reqSigConfChBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reqSigConfChBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(derivedKeysChBox, org.openide.util.NbBundle.getMessage(UsernameAuthentication.class, "LBL_RequireDerivedKeysForSecConv")); // NOI18N
        derivedKeysChBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        derivedKeysChBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                derivedKeysChBoxActionPerformed(evt);
            }
        });

        algoSuiteLabel.setLabelFor(algoSuiteCombo);
        org.openide.awt.Mnemonics.setLocalizedText(algoSuiteLabel, org.openide.util.NbBundle.getMessage(UsernameAuthentication.class, "LBL_AlgoSuiteLabel")); // NOI18N

        algoSuiteCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                algoSuiteComboActionPerformed(evt);
            }
        });

        layoutLabel.setLabelFor(layoutCombo);
        org.openide.awt.Mnemonics.setLocalizedText(layoutLabel, org.openide.util.NbBundle.getMessage(UsernameAuthentication.class, "LBL_LayoutLabel")); // NOI18N

        layoutCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                layoutComboActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(encryptSignatureChBox, org.openide.util.NbBundle.getMessage(UsernameAuthentication.class, "LBL_EncryptSignatureLabel")); // NOI18N
        encryptSignatureChBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        encryptSignatureChBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                encryptSignatureChBox(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(reqDerivedKeys, org.openide.util.NbBundle.getMessage(UsernameAuthentication.class, "LBL_RequireDerivedKeys")); // NOI18N
        reqDerivedKeys.setMargin(new java.awt.Insets(0, 0, 0, 0));
        reqDerivedKeys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reqDerivedKeysActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(encryptOrderChBox, org.openide.util.NbBundle.getMessage(UsernameAuthentication.class, "LBL_EncryptOrderLabel")); // NOI18N
        encryptOrderChBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        encryptOrderChBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                encryptOrderChBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(supportTokenLabel, org.openide.util.NbBundle.getMessage(UsernameAuthentication.class, "LBL_AuthToken")); // NOI18N

        supportTokenCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supportTokenComboActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(hashPasswdChBox, org.openide.util.NbBundle.getMessage(UsernameAuthentication.class, "LBL_HashPasswdLabel")); // NOI18N
        hashPasswdChBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        hashPasswdChBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hashPasswdChBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(hashPasswdChBox)
                    .add(reqDerivedKeys)
                    .add(reqSigConfChBox)
                    .add(secConvChBox)
                    .add(derivedKeysChBox)
                    .add(encryptSignatureChBox)
                    .add(encryptOrderChBox)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layoutLabel)
                            .add(algoSuiteLabel)
                            .add(supportTokenLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(supportTokenCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(algoSuiteCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(layoutCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {algoSuiteCombo, layoutCombo, supportTokenCombo}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(supportTokenLabel)
                    .add(supportTokenCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(algoSuiteLabel)
                    .add(algoSuiteCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(layoutLabel)
                    .add(layoutCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(reqDerivedKeys)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(secConvChBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(derivedKeysChBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(reqSigConfChBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(encryptSignatureChBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(encryptOrderChBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(hashPasswdChBox)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {algoSuiteCombo, layoutCombo, supportTokenCombo}, org.jdesktop.layout.GroupLayout.VERTICAL);

    }// </editor-fold>//GEN-END:initComponents

private void supportTokenComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supportTokenComboActionPerformed
    setValue(supportTokenCombo);
}//GEN-LAST:event_supportTokenComboActionPerformed

    private void encryptOrderChBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_encryptOrderChBoxActionPerformed
         setValue(encryptOrderChBox);
    }//GEN-LAST:event_encryptOrderChBoxActionPerformed

    private void reqDerivedKeysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reqDerivedKeysActionPerformed
         setValue(reqDerivedKeys);
    }//GEN-LAST:event_reqDerivedKeysActionPerformed

    private void reqSigConfChBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reqSigConfChBoxActionPerformed
         setValue(reqSigConfChBox);
    }//GEN-LAST:event_reqSigConfChBoxActionPerformed

    private void derivedKeysChBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_derivedKeysChBoxActionPerformed
         setValue(derivedKeysChBox);
    }//GEN-LAST:event_derivedKeysChBoxActionPerformed

    private void secConvChBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secConvChBoxActionPerformed
        setValue(secConvChBox);
    }//GEN-LAST:event_secConvChBoxActionPerformed

    private void encryptSignatureChBox(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_encryptSignatureChBox
        setValue(encryptSignatureChBox);
    }//GEN-LAST:event_encryptSignatureChBox

    private void layoutComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_layoutComboActionPerformed
        setValue(layoutCombo);
    }//GEN-LAST:event_layoutComboActionPerformed

    private void algoSuiteComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_algoSuiteComboActionPerformed
        setValue(algoSuiteCombo);
    }//GEN-LAST:event_algoSuiteComboActionPerformed

private void hashPasswdChBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hashPasswdChBoxActionPerformed
        setValue(hashPasswdChBox);
}//GEN-LAST:event_hashPasswdChBoxActionPerformed
            
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox algoSuiteCombo;
    private javax.swing.JLabel algoSuiteLabel;
    private javax.swing.JCheckBox derivedKeysChBox;
    private javax.swing.JCheckBox encryptOrderChBox;
    private javax.swing.JCheckBox encryptSignatureChBox;
    private javax.swing.JCheckBox hashPasswdChBox;
    private javax.swing.JComboBox layoutCombo;
    private javax.swing.JLabel layoutLabel;
    private javax.swing.JCheckBox reqDerivedKeys;
    private javax.swing.JCheckBox reqSigConfChBox;
    private javax.swing.JCheckBox secConvChBox;
    private javax.swing.JComboBox supportTokenCombo;
    private javax.swing.JLabel supportTokenLabel;
    // End of variables declaration//GEN-END:variables
    
}
