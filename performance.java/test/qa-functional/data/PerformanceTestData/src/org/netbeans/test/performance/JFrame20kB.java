/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http:www.netbeans.org/cddl-gplv2.html
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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


package org.netbeans.test.performance;

public class JFrame20kB extends javax.swing.JFrame {

    /** Creates new form JFrame20kB */
    public JFrame20kB() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        javax.swing.JToggleButton jtb1;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jFileChooser1 = new javax.swing.JFileChooser();
        jFrame1 = new javax.swing.JFrame();
        jLabel3 = new javax.swing.JLabel();
        jToggleButton3 = new javax.swing.JToggleButton();
        jCheckBox3 = new javax.swing.JCheckBox();
        jButton4 = new javax.swing.JButton();
        jDialog1 = new javax.swing.JDialog();
        jButton3 = new javax.swing.JButton();
        jToggleButton2 = new javax.swing.JToggleButton();
        jCheckBox2 = new javax.swing.JCheckBox();
        jRadioButton2 = new javax.swing.JRadioButton();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jComboBox1 = new javax.swing.JComboBox();
        jList1 = new javax.swing.JList();
        jTextField1 = new javax.swing.JTextField();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jl1111 = new javax.swing.JLabel();
        jb1111 = new javax.swing.JButton();
        jtb1 = new javax.swing.JToggleButton();
        jcb1111 = new javax.swing.JCheckBox();
        jrb1111 = new javax.swing.JRadioButton();
        jsb111 = new javax.swing.JScrollBar();
        jsp1 = new javax.swing.JScrollPane();
        js111 = new javax.swing.JSlider();
        jpb1 = new javax.swing.JProgressBar();
        jSplitPane1 = new javax.swing.JSplitPane();
        jftf1 = new javax.swing.JFormattedTextField();
        jpf1 = new javax.swing.JPasswordField();
        jSpinner1 = new javax.swing.JSpinner();
        jSeparator1 = new javax.swing.JSeparator();
        jTextPane1 = new javax.swing.JTextPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jTable1 = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        jOptionPane1 = new javax.swing.JOptionPane();
        jColorChooser1 = new javax.swing.JColorChooser();
        jLabel2 = new javax.swing.JLabel();

        jLabel3.setText("jLabel3");
        jFrame1.getContentPane().add(jLabel3, java.awt.BorderLayout.CENTER);

        jToggleButton3.setText("jToggleButton3");
        jFrame1.getContentPane().add(jToggleButton3, java.awt.BorderLayout.NORTH);

        jCheckBox3.setText("jCheckBox3");
        jFrame1.getContentPane().add(jCheckBox3, java.awt.BorderLayout.SOUTH);

        jButton4.setText("jButton4");
        jFrame1.getContentPane().add(jButton4, java.awt.BorderLayout.EAST);

        jButton3.setText("jButton3");
        jToggleButton2.setText("jToggleButton2");
        jCheckBox2.setText("jCheckBox2");
        jRadioButton2.setText("jRadioButton2");
        jTextField1.setText("jTextField1");
        jMenu1.setText("Menu");
        jMenuItem2.setText("Item");
        jMenu1.add(jMenuItem2);

        jMenuItem1.setText("Item");
        jMenu1.add(jMenuItem1);

        jMenuItem3.setText("Item");
        jMenu1.add(jMenuItem3);

        jMenuItem4.setText("Item");
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        getContentPane().setLayout(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jl1111.setText("jLabel1");
        getContentPane().add(jl1111);
        jl1111.setBounds(0, 0, 400, 15);

        jb1111.setText("jB");
        jb1111.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        getContentPane().add(jb1111);
        jb1111.setBounds(60, 170, 16, 19);

        jtb1.setText("jToggleButton1");
        getContentPane().add(jtb1);
        jtb1.setBounds(260, 120, 131, 25);

        jcb1111.setText("jCb");
        getContentPane().add(jcb1111);
        jcb1111.setBounds(170, 70, 46, 23);

        jrb1111.setText("jRadioButton1");
        getContentPane().add(jrb1111);
        jrb1111.setBounds(60, 50, 114, 23);

        getContentPane().add(jsb111);
        jsb111.setBounds(240, 200, 17, 61);

        getContentPane().add(jsp1);
        jsp1.setBounds(0, 130, 3, 3);

        getContentPane().add(js111);
        js111.setBounds(50, 70, 200, 16);

        getContentPane().add(jpb1);
        jpb1.setBounds(340, 40, 148, 14);

        jSplitPane1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        getContentPane().add(jSplitPane1);
        jSplitPane1.setBounds(50, 120, 227, 31);

        jftf1.setText("jtf");
        getContentPane().add(jftf1);
        jftf1.setBounds(20, 270, 16, 19);

        getContentPane().add(jpf1);
        jpf1.setBounds(180, 220, 4, 19);

        jSpinner1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        getContentPane().add(jSpinner1);
        jSpinner1.setBounds(350, 270, 28, 20);

        getContentPane().add(jSeparator1);
        jSeparator1.setBounds(320, 180, 0, 2);

        getContentPane().add(jTextPane1);
        jTextPane1.setBounds(190, 40, 6, 21);

        getContentPane().add(jEditorPane1);
        jEditorPane1.setBounds(280, 40, 106, 21);

        jTable1.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        getContentPane().add(jTable1);
        jTable1.setBounds(100, 20, 300, 64);

        getContentPane().add(jToolBar1);
        jToolBar1.setBounds(40, 40, 18, 4);

        jInternalFrame1.setVisible(true);
        getContentPane().add(jInternalFrame1);
        jInternalFrame1.setBounds(40, 90, 30, 32);

        getContentPane().add(jLayeredPane1);
        jLayeredPane1.setBounds(320, 200, 0, 0);

        getContentPane().add(jDesktopPane1);
        jDesktopPane1.setBounds(210, 280, 0, 0);

        getContentPane().add(jOptionPane1);
        jOptionPane1.setBounds(350, 190, 262, 90);

        jColorChooser1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        getContentPane().add(jColorChooser1);
        jColorChooser1.setBounds(40, 220, 433, 340);

        jLabel2.setText("jLabel2");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(30, 30, 45, 15);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new JFrame20kB().show();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JColorChooser jColorChooser1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JList jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JToggleButton jToggleButton3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton jb1111;
    private javax.swing.JCheckBox jcb1111;
    private javax.swing.JFormattedTextField jftf1;
    private javax.swing.JLabel jl1111;
    private javax.swing.JProgressBar jpb1;
    private javax.swing.JPasswordField jpf1;
    private javax.swing.JRadioButton jrb1111;
    private javax.swing.JSlider js111;
    private javax.swing.JScrollBar jsb111;
    private javax.swing.JScrollPane jsp1;
    // End of variables declaration//GEN-END:variables
    
}
