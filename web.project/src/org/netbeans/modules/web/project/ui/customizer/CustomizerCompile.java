/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.web.project.ui.customizer;

import org.openide.util.NbBundle;

public class CustomizerCompile extends javax.swing.JPanel implements WebCustomizer.Panel {

    private VisualPropertySupport vps;
    private VisualClasspathSupport vcs;
    private String[] jvm;
    private String[] jvmValues;
    
    /** Creates new form CustomizerCompile */
    public CustomizerCompile(WebProjectProperties webProperties) {
        initComponents();
        
        vps = new VisualPropertySupport(webProperties);
        vcs = new VisualClasspathSupport(
            jTableClasspath,
            jButtonAddJar,
            jButtonAddLibrary,
            jButtonAddProject,
            jButtonEdit,
            jButtonRemove,
            jButtonMoveUp,
            jButtonMoveDown);
        
        jvm = new String[] {
            NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_JVM_JComboBox_11"),
            NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_JVM_JComboBox_12"),
            NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_JVM_JComboBox_13"),
            NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_JVM_JComboBox_14"),
            NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_JVM_JComboBox_15")
        };
        jvmValues = new String[] {
            NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_JVM_JComboBox_11_Value"),
            NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_JVM_JComboBox_12_Value"),
            NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_JVM_JComboBox_13_Value"),
            NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_JVM_JComboBox_14_Value"),
            NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_JVM_JComboBox_15_Value")
        };

    }

    public void initValues() {
        vps.register(jvmComboBox, jvm, jvmValues, WebProjectProperties.JAVAC_TARGET);
        vps.register(jCheckBoxDebugInfo, WebProjectProperties.JAVAC_DEBUG);
        vps.register(jCheckBoxDeprecation, WebProjectProperties.JAVAC_DEPRECATION);
        vps.register(vcs, WebProjectProperties.JAVAC_CLASSPATH);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jvmLabel = new javax.swing.JLabel();
        jvmComboBox = new javax.swing.JComboBox();
        jCheckBoxDebugInfo = new javax.swing.JCheckBox();
        jCheckBoxDeprecation = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabelClasspath = new javax.swing.JLabel();
        jScrollClasspath = new javax.swing.JScrollPane();
        jTableClasspath = new javax.swing.JTable();
        jButtonAddJar = new javax.swing.JButton();
        jButtonAddLibrary = new javax.swing.JButton();
        jButtonAddProject = new javax.swing.JButton();
        jButtonRemove = new javax.swing.JButton();
        jButtonMoveUp = new javax.swing.JButton();
        jButtonMoveDown = new javax.swing.JButton();
        jButtonEdit = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.EtchedBorder(), new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 12, 12))));
        jvmLabel.setLabelFor(jvmComboBox);
        jvmLabel.setText(NbBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("LBL_CustomizeCompile_Compiler_JVM_JComboBox"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 12);
        add(jvmLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(jvmComboBox, gridBagConstraints);

        jCheckBoxDebugInfo.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_DebugInfo_JCheckBox"));
        jCheckBoxDebugInfo.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(jCheckBoxDebugInfo, gridBagConstraints);

        jCheckBoxDeprecation.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_Deprecation_JCheckBox"));
        jCheckBoxDeprecation.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(jCheckBoxDeprecation, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabelClasspath.setLabelFor(jTableClasspath);
        jLabelClasspath.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_JLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(jLabelClasspath, gridBagConstraints);

        jTableClasspath.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollClasspath.setViewportView(jTableClasspath);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        jPanel2.add(jScrollClasspath, gridBagConstraints);

        jButtonAddJar.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_AddJar_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(jButtonAddJar, gridBagConstraints);

        jButtonAddLibrary.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_AddLibrary_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(jButtonAddLibrary, gridBagConstraints);

        jButtonAddProject.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_AddProject_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        jPanel2.add(jButtonAddProject, gridBagConstraints);

        jButtonRemove.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_Remove_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        jPanel2.add(jButtonRemove, gridBagConstraints);

        jButtonMoveUp.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_MoveUp_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(jButtonMoveUp, gridBagConstraints);

        jButtonMoveDown.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_MoveDown_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        jPanel2.add(jButtonMoveDown, gridBagConstraints);

        jButtonEdit.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_Edit_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        jPanel2.add(jButtonEdit, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel2, gridBagConstraints);

    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddJar;
    private javax.swing.JButton jButtonAddLibrary;
    private javax.swing.JButton jButtonAddProject;
    private javax.swing.JButton jButtonEdit;
    private javax.swing.JButton jButtonMoveDown;
    private javax.swing.JButton jButtonMoveUp;
    private javax.swing.JButton jButtonRemove;
    private javax.swing.JCheckBox jCheckBoxDebugInfo;
    private javax.swing.JCheckBox jCheckBoxDeprecation;
    private javax.swing.JLabel jLabelClasspath;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollClasspath;
    private javax.swing.JTable jTableClasspath;
    private javax.swing.JComboBox jvmComboBox;
    private javax.swing.JLabel jvmLabel;
    // End of variables declaration//GEN-END:variables

}
