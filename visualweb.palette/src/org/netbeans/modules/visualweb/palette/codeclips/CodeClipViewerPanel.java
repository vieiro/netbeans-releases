/*
 * NewJPanel.java
 *
 * Created on August 13, 2007, 6:13 PM
 */

package org.netbeans.modules.visualweb.palette.codeclips;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.border.EtchedBorder;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author  joelle
 */
public class CodeClipViewerPanel extends javax.swing.JPanel {

    private static final int DIALOG_HEIGHT = 250;

    /** Creates new form NewJPanel */
    public CodeClipViewerPanel(String title, String content) {
        initComponents();

        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        setClipName(title);
        setContentText(content);
        
        titleField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(CodeClipViewerPanel.class, "Acc_CodeSnippetViewer_Title"));
        titleField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(CodeClipViewerPanel.class, "Acc_CodeSnippetViewer_TitleDesc"));

        clipContentTextArea.getAccessibleContext().setAccessibleName(NbBundle.getMessage(CodeClipViewerPanel.class, "Acc_EditorPane_Name")); // NOI18N
        clipContentTextArea.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CodeClipViewerPanel.class, "Acc_EditorPane_Desc")); // NOI18N
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        clipContentTextArea = new javax.swing.JTextArea();
        titleLabel = new javax.swing.JLabel();

        titleField.setText(org.openide.util.NbBundle.getMessage(CodeClipViewerPanel.class, "CodeClipViewerPanel.titleField.text")); // NOI18N

        clipContentTextArea.setColumns(20);
        clipContentTextArea.setRows(5);
        jScrollPane1.setViewportView(clipContentTextArea);

        titleLabel.setText(org.openide.util.NbBundle.getMessage(CodeClipViewerPanel.class, "CodeClipViewerPanel.titleLabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(titleLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(titleField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 231, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(titleField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(titleLabel))
                .add(18, 18, 18)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 227, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea clipContentTextArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField titleField;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables

    public String getContentText() {
        return clipContentTextArea.getText();
    }

    public void setContentText(String clipContent) {
        clipContentTextArea.setText(clipContent);
    }

    public String getClipName() {
        return titleField.getText();
    }

    public void setClipName(String clipName) {
        titleField.setText(clipName);
    }

    private DialogDescriptor dd;
    private Dialog dialog;
    public void setupDialog() {
        dd = new DialogDescriptor(this, getClipName());
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        dialog = DialogDisplayer.getDefault().createDialog(dd);
        dialog.setPreferredSize(new Dimension(screenWidth * 2 / 3, DIALOG_HEIGHT));
        dialog.getAccessibleContext().setAccessibleName(NbBundle.getMessage(CodeClipViewerPanel.class, "Acc_Dialog_Name")); // NOI18N
        dialog.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CodeClipViewerPanel.class, "Acc_Dialog_Desc")); // NOI18N
        dialog.setVisible(true);
    }
    
    public boolean isCancelled() {
        if ( dd.getValue().equals(DialogDescriptor.CANCEL_OPTION) ) {
            return true;
        }
        return false;
    }
    

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if(aFlag){
            setupDialog();
        } else {
            dialog.dispose();
        }
    }
    
    
}
