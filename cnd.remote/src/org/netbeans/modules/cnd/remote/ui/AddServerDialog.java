/*
 * AddServerDialog.java
 *
 * Created on June 11, 2008, 12:59 PM
 */

package org.netbeans.modules.cnd.remote.ui;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.cnd.remote.server.RemoteServerList;
import org.netbeans.modules.cnd.remote.server.RemoteServerRecord;
import org.openide.util.NbBundle;

/**
 *
 * @author  gordonp
 */
public class AddServerDialog extends JPanel implements DocumentListener {
    
    public static final String PROP_VALID = "valid"; // NOI18N
    
    private boolean valid;

    /** Creates new form AddServerDialog2 */
    public AddServerDialog() {
        initComponents();
        valid = false;
    }
    
    public String getServerName() {
        return tfServer.getText();
    }
    
    public String getLoginName() {
        return tfLogin.getText();
    }
    
    public boolean isDefault() {
        return cbxSetAsDefault.isSelected();
    }
    
    public boolean isOkValid() {
        return valid;
    }
    
    public void insertUpdate(DocumentEvent e) {
        boolean ovalid = valid;
        valid = tfServer.getText().length() > 0 && tfLogin.getText().length() > 0;
        if (valid != ovalid) {
            firePropertyChange(PROP_VALID, ovalid, valid);
        }
    }

    public void removeUpdate(DocumentEvent e) {
        insertUpdate(e);
    }

    public void changedUpdate(DocumentEvent e) {
    }
    
    public class PasswordSourceModel extends DefaultComboBoxModel {
        
        public PasswordSourceModel() {
            addElement(NbBundle.getMessage(AddServerDialog.class, "LBL_PSM_TypeitOnce"));
            addElement(NbBundle.getMessage(AddServerDialog.class, "LBL_PSM_TypeitAlways"));
            
            for (RemoteServerRecord record : RemoteServerList.getInstance()) {
                String user = record.getUserName();
                if (user != null) {
                    addElement(NbBundle.getMessage(AddServerDialog.class, "FMT_SharedPasswordSource", record.getServerName(), user));
                }
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbServer = new javax.swing.JLabel();
        tfServer = new javax.swing.JTextField();
        tfServer.getDocument().addDocumentListener(this);
        lbLogin = new javax.swing.JLabel();
        tfLogin = new javax.swing.JTextField();
        tfLogin.getDocument().addDocumentListener(this);
        lbPasswordSource = new javax.swing.JLabel();
        cbPasswordSource = new javax.swing.JComboBox();
        cbxSetAsDefault = new javax.swing.JCheckBox();

        lbServer.setLabelFor(tfServer);
        lbServer.setText(org.openide.util.NbBundle.getMessage(AddServerDialog.class, "LBL_ServerTF")); // NOI18N
        lbServer.setToolTipText(org.openide.util.NbBundle.getMessage(AddServerDialog.class, "DESC_ServerTF")); // NOI18N

        lbLogin.setLabelFor(tfLogin);
        lbLogin.setText(org.openide.util.NbBundle.getMessage(AddServerDialog.class, "LBL_LoginTF")); // NOI18N
        lbLogin.setToolTipText(org.openide.util.NbBundle.getMessage(AddServerDialog.class, "DESC_LoginTF")); // NOI18N

        tfLogin.setText(System.getProperty("user.name"));

        lbPasswordSource.setLabelFor(cbPasswordSource);
        lbPasswordSource.setText(org.openide.util.NbBundle.getMessage(AddServerDialog.class, "LBL_PasswordSource")); // NOI18N

        cbPasswordSource.setModel(new PasswordSourceModel());

        cbxSetAsDefault.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/remote/ui/Bundle").getString("MNEM_SetAsDefault").charAt(0));
        cbxSetAsDefault.setText(org.openide.util.NbBundle.getMessage(AddServerDialog.class, "cbxSetAsDefault")); // NOI18N
        cbxSetAsDefault.setMargin(new java.awt.Insets(2, 0, 2, 2));
        cbxSetAsDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxSetAsDefaultActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lbServer)
                            .add(lbLogin)
                            .add(lbPasswordSource))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(tfLogin, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                            .add(tfServer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                            .add(cbPasswordSource, 0, 246, Short.MAX_VALUE)))
                    .add(cbxSetAsDefault))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbServer)
                    .add(tfServer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbLogin)
                    .add(tfLogin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbPasswordSource)
                    .add(cbPasswordSource, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(cbxSetAsDefault)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void cbxSetAsDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxSetAsDefaultActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_cbxSetAsDefaultActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbPasswordSource;
    private javax.swing.JCheckBox cbxSetAsDefault;
    private javax.swing.JLabel lbLogin;
    private javax.swing.JLabel lbPasswordSource;
    private javax.swing.JLabel lbServer;
    private javax.swing.JTextField tfLogin;
    private javax.swing.JTextField tfServer;
    // End of variables declaration//GEN-END:variables

}
