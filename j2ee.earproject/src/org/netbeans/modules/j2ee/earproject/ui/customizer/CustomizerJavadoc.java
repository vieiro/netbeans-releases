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

package org.netbeans.modules.j2ee.earproject.ui.customizer;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openide.util.HelpCtx;

/** Customizer for general project attributes.
 *
 * @author  phrebejk
 */
public class CustomizerJavadoc extends JPanel implements ArchiveCustomizerPanel, HelpCtx.Provider  {

    private VisualPropertySupport vps;

    /** Creates new form CustomizerCompile */
    public CustomizerJavadoc(EarProjectProperties webProperties) {
        initComponents();

        vps = new VisualPropertySupport(webProperties);
    }
    
    
    public void initValues() {
        vps.register( jCheckBoxPrivate, EarProjectProperties.JAVADOC_PRIVATE );
        vps.register( jCheckBoxTree, EarProjectProperties.JAVADOC_NO_TREE );
        vps.register( jCheckBoxUsages, EarProjectProperties.JAVADOC_USE );
        vps.register( jCheckBoxNavigation, EarProjectProperties.JAVADOC_NO_NAVBAR ); 
        vps.register( jCheckBoxIndex, EarProjectProperties.JAVADOC_NO_INDEX ); 
        vps.register( jCheckBoxSplitIndex, EarProjectProperties.JAVADOC_SPLIT_INDEX ); 
        vps.register( jCheckBoxAuthor, EarProjectProperties.JAVADOC_AUTHOR ); 
        vps.register( jCheckBoxVersion, EarProjectProperties.JAVADOC_VERSION );
        vps.register( jTextFieldWinTitle, EarProjectProperties.JAVADOC_WINDOW_TITLE );
        // vps.register( jTextFieldEncoding, WebProjectProperties.JAVADOC_ENCODING ); 
        vps.register( jCheckBoxPreview, EarProjectProperties.JAVADOC_PREVIEW ); 
                
        reenableSplitIndex( null );
        
        // XXX Temporarily removing some controls
        remove( jLabelPackage );
        remove( jTextFieldPackage );
        remove( jButtonPackage );       
        remove( jCheckBoxSubpackages );
        jPanel1.remove( jLabelEncoding );
        jPanel1.remove( jTextFieldEncoding );
        
        
    } 
        
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelPackage = new javax.swing.JLabel();
        jTextFieldPackage = new javax.swing.JTextField();
        jButtonPackage = new javax.swing.JButton();
        jCheckBoxSubpackages = new javax.swing.JCheckBox();
        jCheckBoxPrivate = new javax.swing.JCheckBox();
        jLabelGenerate = new javax.swing.JLabel();
        jCheckBoxTree = new javax.swing.JCheckBox();
        jCheckBoxUsages = new javax.swing.JCheckBox();
        jCheckBoxNavigation = new javax.swing.JCheckBox();
        jCheckBoxIndex = new javax.swing.JCheckBox();
        jCheckBoxSplitIndex = new javax.swing.JCheckBox();
        jLabelTags = new javax.swing.JLabel();
        jCheckBoxAuthor = new javax.swing.JCheckBox();
        jCheckBoxVersion = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jLabelWinTitle = new javax.swing.JLabel();
        jTextFieldWinTitle = new javax.swing.JTextField();
        jLabelEncoding = new javax.swing.JLabel();
        jTextFieldEncoding = new javax.swing.JTextField();
        jCheckBoxPreview = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.EtchedBorder(), new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 12, 12))));
        jLabelPackage.setLabelFor(jTextFieldPackage);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelPackage, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Package_JLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 12);
        add(jLabelPackage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 6);
        add(jTextFieldPackage, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jButtonPackage, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Package_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(jButtonPackage, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxSubpackages, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Subpackages_JCheckBox"));
        jCheckBoxSubpackages.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(jCheckBoxSubpackages, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxPrivate, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Private_JCheckBox"));
        jCheckBoxPrivate.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(jCheckBoxPrivate, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabelGenerate, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Generate_JLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        add(jLabelGenerate, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxTree, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Tree_JCheckBox"));
        jCheckBoxTree.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jCheckBoxTree, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxUsages, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Usages_JCheckBox"));
        jCheckBoxUsages.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jCheckBoxUsages, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxNavigation, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Navigation_JCheckBox"));
        jCheckBoxNavigation.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jCheckBoxNavigation, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxIndex, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Index_JCheckBox"));
        jCheckBoxIndex.setMargin(new java.awt.Insets(0, 0, 0, 2));
        jCheckBoxIndex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reenableSplitIndex(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jCheckBoxIndex, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxSplitIndex, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_SplitIndex_JCheckBox"));
        jCheckBoxSplitIndex.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 12, 0);
        add(jCheckBoxSplitIndex, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabelTags, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Tags_JLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        add(jLabelTags, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxAuthor, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Author_JCheckBox"));
        jCheckBoxAuthor.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jCheckBoxAuthor, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxVersion, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Version_JCheckBox"));
        jCheckBoxVersion.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 12, 0);
        add(jCheckBoxVersion, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabelWinTitle, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_WinTitle_JLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 6);
        jPanel1.add(jLabelWinTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        jPanel1.add(jTextFieldWinTitle, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabelEncoding, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Encoding_JLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        jPanel1.add(jLabelEncoding, gridBagConstraints);

        jTextFieldEncoding.setMinimumSize(new java.awt.Dimension(150, 22));
        jTextFieldEncoding.setPreferredSize(new java.awt.Dimension(150, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jTextFieldEncoding, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(jPanel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxPreview, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Preview_JCheckBox"));
        jCheckBoxPreview.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        add(jCheckBoxPreview, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    private void reenableSplitIndex(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reenableSplitIndex
        jCheckBoxSplitIndex.setEnabled( jCheckBoxIndex.isSelected() );
    }//GEN-LAST:event_reenableSplitIndex
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonPackage;
    private javax.swing.JCheckBox jCheckBoxAuthor;
    private javax.swing.JCheckBox jCheckBoxIndex;
    private javax.swing.JCheckBox jCheckBoxNavigation;
    private javax.swing.JCheckBox jCheckBoxPreview;
    private javax.swing.JCheckBox jCheckBoxPrivate;
    private javax.swing.JCheckBox jCheckBoxSplitIndex;
    private javax.swing.JCheckBox jCheckBoxSubpackages;
    private javax.swing.JCheckBox jCheckBoxTree;
    private javax.swing.JCheckBox jCheckBoxUsages;
    private javax.swing.JCheckBox jCheckBoxVersion;
    private javax.swing.JLabel jLabelEncoding;
    private javax.swing.JLabel jLabelGenerate;
    private javax.swing.JLabel jLabelPackage;
    private javax.swing.JLabel jLabelTags;
    private javax.swing.JLabel jLabelWinTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextFieldEncoding;
    private javax.swing.JTextField jTextFieldPackage;
    private javax.swing.JTextField jTextFieldWinTitle;
    // End of variables declaration//GEN-END:variables
        
    /** Help context where to find more about the paste type action.
     * @return the help context for this action
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(CustomizerJavadoc.class);
    }

}
