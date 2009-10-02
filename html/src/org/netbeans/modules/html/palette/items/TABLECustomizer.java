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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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


package org.netbeans.modules.html.palette.items;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;



/**
 *
 * @author  Libor Kotouc
 */
public class TABLECustomizer extends javax.swing.JPanel {

    private Dialog dialog = null;
    private DialogDescriptor descriptor = null;
    private boolean dialogOK = false;

    private TABLE table;
            
    /** Creates new form TABLE_1Panel */
    public TABLECustomizer(TABLE table) {
        this.table = table;

        initComponents();
        try {
            ((JSpinner.NumberEditor)jSpinner1.getEditor()).getTextField().getAccessibleContext().setAccessibleName(jSpinner1.getAccessibleContext().getAccessibleName());
            ((JSpinner.NumberEditor)jSpinner1.getEditor()).getTextField().getAccessibleContext().setAccessibleDescription(jSpinner1.getAccessibleContext().getAccessibleDescription());
            ((JSpinner.NumberEditor)jSpinner2.getEditor()).getTextField().getAccessibleContext().setAccessibleName(jSpinner2.getAccessibleContext().getAccessibleName());
            ((JSpinner.NumberEditor)jSpinner2.getEditor()).getTextField().getAccessibleContext().setAccessibleDescription(jSpinner2.getAccessibleContext().getAccessibleDescription());
            ((JSpinner.NumberEditor)jSpinner3.getEditor()).getTextField().getAccessibleContext().setAccessibleName(jSpinner3.getAccessibleContext().getAccessibleName());
            ((JSpinner.NumberEditor)jSpinner3.getEditor()).getTextField().getAccessibleContext().setAccessibleDescription(jSpinner3.getAccessibleContext().getAccessibleDescription());
            ((JSpinner.NumberEditor)jSpinner4.getEditor()).getTextField().getAccessibleContext().setAccessibleName(jSpinner4.getAccessibleContext().getAccessibleName());
            ((JSpinner.NumberEditor)jSpinner4.getEditor()).getTextField().getAccessibleContext().setAccessibleDescription(jSpinner4.getAccessibleContext().getAccessibleDescription());
            ((JSpinner.NumberEditor)jSpinner5.getEditor()).getTextField().getAccessibleContext().setAccessibleName(jSpinner5.getAccessibleContext().getAccessibleName());
            ((JSpinner.NumberEditor)jSpinner5.getEditor()).getTextField().getAccessibleContext().setAccessibleDescription(jSpinner5.getAccessibleContext().getAccessibleDescription());
            ((JSpinner.NumberEditor)widthSpinner.getEditor()).getTextField().getAccessibleContext().setAccessibleName(widthSpinner.getAccessibleContext().getAccessibleName());
            ((JSpinner.NumberEditor)widthSpinner.getEditor()).getTextField().getAccessibleContext().setAccessibleDescription(widthSpinner.getAccessibleContext().getAccessibleDescription());
        } catch (Exception e) {
        }
    }
    
    public boolean showDialog() {
        
        dialogOK = false;
        
        String displayName = "";
        try {
            displayName = NbBundle.getBundle("org.netbeans.modules.html.palette.items.resources.Bundle").getString("NAME_html-TABLE"); // NOI18N
        }
        catch (Exception e) {}
        
        descriptor = new DialogDescriptor
                (this, NbBundle.getMessage(TABLECustomizer.class, "LBL_Customizer_InsertPrefix") + " " + displayName, true,
                 DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION,
                 new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        if (descriptor.getValue().equals(DialogDescriptor.OK_OPTION)) {
                            evaluateInput();
                            dialogOK = true;
                        }
                        dialog.dispose();
		     }
		 } 
                );
        
        dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        dialog.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSN_TABLE_Dialog"));
        dialog.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSD_TABLE_Dialog"));
        dialog.setVisible(true);
        repaint();
        
        return dialogOK;
    }
    
    private void evaluateInput() {
        
        int rows = ((Integer)jSpinner1.getValue()).intValue();
        table.setRows(rows);

        int cols = ((Integer)jSpinner2.getValue()).intValue();
        table.setCols(cols);

        int border = ((Integer)jSpinner3.getValue()).intValue();
        table.setBorder(border);

        int width = ((Integer)widthSpinner.getValue()).intValue();
        table.setWidth(width);

        int cspac = ((Integer)jSpinner4.getValue()).intValue();
        table.setCspac(cspac);

        int cpadd = ((Integer)jSpinner5.getValue()).intValue();
        table.setCpadd(cpadd);
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jSpinner2 = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jSpinner3 = new javax.swing.JSpinner();
        jSpinner4 = new javax.swing.JSpinner();
        jSpinner5 = new javax.swing.JSpinner();
        widthSpinner = new javax.swing.JSpinner();

        jLabel1.setLabelFor(jSpinner2);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "LBL_TABLE_Columns")); // NOI18N

        jLabel2.setLabelFor(jSpinner1);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "LBL_TABLE_Rows")); // NOI18N

        jSpinner1.setModel(new SpinnerNumberModel(table.getRows(), 0, Integer.MAX_VALUE, 1));
        jSpinner1.setEditor(new JSpinner.NumberEditor(jSpinner1, "#"));
        jSpinner1.setValue(new Integer(table.getRows()));

        jSpinner2.setModel(new SpinnerNumberModel(table.getCols(), 0, Integer.MAX_VALUE, 1));
        jSpinner2.setEditor(new JSpinner.NumberEditor(jSpinner2, "#"));
        jSpinner2.setValue(new Integer(table.getCols()));

        jLabel3.setLabelFor(jSpinner3);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "LBL_TABLE_Border")); // NOI18N

        jLabel4.setLabelFor(widthSpinner);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "LBL_TABLE_Width")); // NOI18N

        jLabel5.setLabelFor(jSpinner4);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "LBL_TABLE_Spacing")); // NOI18N

        jLabel6.setLabelFor(jSpinner5);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "LBL_TABLE_Padding")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "LBL_TABLE_SpacingHelp")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "LBL_TABLE_PaddingHelp")); // NOI18N

        jSpinner3.setModel(new SpinnerNumberModel(table.getBorder(), 0, Integer.MAX_VALUE, 1));
        jSpinner3.setEditor(new JSpinner.NumberEditor(jSpinner3, "#"));
        jSpinner3.setValue(new Integer(table.getBorder()));

        jSpinner4.setModel(new SpinnerNumberModel(table.getCspac(), 0, Integer.MAX_VALUE, 1));
        jSpinner4.setEditor(new JSpinner.NumberEditor(jSpinner4, "#"));
        jSpinner4.setValue(new Integer(table.getCspac()));

        jSpinner5.setModel(new SpinnerNumberModel(table.getCpadd(), 0, Integer.MAX_VALUE, 1));
        jSpinner5.setEditor(new JSpinner.NumberEditor(jSpinner5, "#"));
        jSpinner5.setValue(new Integer(table.getCpadd()));

        widthSpinner.setModel(new SpinnerNumberModel(table.getWidth(), 0, Integer.MAX_VALUE, 1));
        widthSpinner.setEditor(new JSpinner.NumberEditor(widthSpinner, "#"));
        widthSpinner.setValue(new Integer(table.getWidth()));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(jLabel1)
                    .add(jLabel3)
                    .add(jLabel4)
                    .add(jLabel5)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSpinner1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .add(jSpinner2)
                    .add(jSpinner3)
                    .add(widthSpinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .add(jSpinner4)
                    .add(jSpinner5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE))
                .add(245, 245, 245))
            .add(layout.createSequentialGroup()
                .add(44, 44, 44)
                .add(jLabel7)
                .addContainerGap(216, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .add(44, 44, 44)
                .add(jLabel8)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {jSpinner1, jSpinner2, jSpinner3, jSpinner4, jSpinner5, widthSpinner}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jSpinner1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jSpinner2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jSpinner3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(widthSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jSpinner4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(jSpinner5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel8)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSN_TABLE_Columns")); // NOI18N
        jLabel1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSD_TABLE_Columns")); // NOI18N
        jLabel2.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSN_TABLE_Rows")); // NOI18N
        jLabel2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSD_TABLE_Rows")); // NOI18N
        jSpinner1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSN_TABLE_Rows_Spinner")); // NOI18N
        jSpinner1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSD_TABLE_Rows_Spinner")); // NOI18N
        jSpinner2.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSN_TABLE_Columns_Spinner")); // NOI18N
        jSpinner2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSD_TABLE_Columns_Spinner")); // NOI18N
        jLabel3.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSN_TABLE_Border")); // NOI18N
        jLabel3.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSD_TABLE_Border")); // NOI18N
        jLabel4.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSN_TABLE_Width")); // NOI18N
        jLabel4.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSD_TABLE_Width")); // NOI18N
        jLabel5.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSN_TABLE_Spacing")); // NOI18N
        jLabel5.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSD_TABLE_Spacing")); // NOI18N
        jLabel6.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSN_TABLE_Padding")); // NOI18N
        jLabel6.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSD_TABLE_Padding")); // NOI18N
        jLabel7.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSN_TABLE_SpacingHelp")); // NOI18N
        jLabel7.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSD_TABLE_SpacingHelp")); // NOI18N
        jLabel8.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSN_TABLE_PaddingHelp")); // NOI18N
        jLabel8.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSD_TABLE_PaddingHelp")); // NOI18N
        jSpinner3.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSN_TABLE_Border_Spinner")); // NOI18N
        jSpinner3.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSD_TABLE_Border_Spinner")); // NOI18N
        jSpinner4.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSN_TABLE_Spacing_Spinner")); // NOI18N
        jSpinner4.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSD_TABLE_Spacing_Spinner")); // NOI18N
        jSpinner5.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSN_TABLE_Padding_Spinner")); // NOI18N
        jSpinner5.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSD_TABLE_Padding_Spinner")); // NOI18N
        widthSpinner.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSN_TABLE_Width_Spinner")); // NOI18N
        widthSpinner.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TABLECustomizer.class, "ACSD_TABLE_Width_Spinner")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JSpinner jSpinner3;
    private javax.swing.JSpinner jSpinner4;
    private javax.swing.JSpinner jSpinner5;
    private javax.swing.JSpinner widthSpinner;
    // End of variables declaration//GEN-END:variables
    
}
