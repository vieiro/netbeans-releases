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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * OperationPanel.java
 *
 * Created on September 8, 2006, 5:21 PM
 */

package org.netbeans.modules.xml.wsdl.ui.view.treeeditor.newtype;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.xml.wsdl.ui.actions.NameGenerator;
import org.netbeans.modules.xml.wsdl.ui.api.property.PropertyUtil;
import org.netbeans.modules.xml.wsdl.ui.view.OperationConfigurationPanel;
import org.netbeans.modules.xml.wsdl.ui.view.PartAndElementOrTypeTableModel.PartAndElementOrType;
import org.netbeans.modules.xml.xam.ModelSource;
import org.openide.DialogDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author  radval
 */
public class OperationPanel extends javax.swing.JPanel {
    
    private Project mProject;
    
    private WSDLModel mModel;
    
    private DialogDescriptor mDD;
    
    private List<String> existingMessages = new ArrayList<String>();
    
    String mErrorMessage = null;

    private String mWarningMessage;
    
    /** Creates new form OperationPanel */
    public OperationPanel() {
        initComponents();
    }
    
    public OperationPanel(Project project, WSDLModel model) {
        this.mProject = project;
        this.mModel = model;
        initComponents();
        initGUI();
    }
    
    public void setDialogDescriptor(DialogDescriptor dd) {
        this.mDD = dd;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        operationConfigurationPanel1 = new OperationConfigurationPanel(this.mProject, true, mModel, false);
        commonMessagePanel1 = new org.netbeans.modules.xml.wsdl.ui.view.common.CommonMessagePanel();

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, operationConfigurationPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(96, 96, 96)
                        .add(commonMessagePanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(operationConfigurationPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(17, 17, 17)
                .add(commonMessagePanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(60, 60, 60))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    public OperationConfigurationPanel getOperationConfigurationPanel() {
        return this.operationConfigurationPanel1;
    }
    
    private void initGUI() {
        
        ModelSource modelSource = this.mModel.getModelSource();
        FileObject wsdlFile = modelSource.getLookup().lookup(FileObject.class);
        if(wsdlFile != null) {
            String fileName = wsdlFile.getName();
            
            String operationName = fileName + NbBundle.getMessage(OperationPanel.class, "LBL_Operation_suffix");
            this.operationConfigurationPanel1.setOperationName(operationName);
            
            String[] messages = PropertyUtil.getAllMessages(this.mModel);
            if(messages != null) {
                for(int i = 0; i < messages.length; i++) {
                 existingMessages.add(messages[i]);
                }
            }
            
            String inputMessageName = NameGenerator.getInstance().generateUniqueInputMessageName(operationName, mModel);
            String outputMessageName = NameGenerator.getInstance().generateUniqueOutputMessageName(operationName, mModel);
            //String faultMessageName = NameGenerator.getInstance().generateUniqueFaultMessageName(operationName, mModel);
                    
            MessageNameTextChangeListener messageListener = new MessageNameTextChangeListener();
            
            this.operationConfigurationPanel1.setInputMessages(messages, inputMessageName, messageListener);
            this.operationConfigurationPanel1.setOutputMessages(messages, outputMessageName, messageListener);
            this.operationConfigurationPanel1.setFaultMessages(messages, null, messageListener);
            
            
        }
        
                
        OperationNameTextChangeListener operationListener  = new OperationNameTextChangeListener();
        this.operationConfigurationPanel1.getOperationNameTextField().getDocument().addDocumentListener(operationListener);
        
        operationConfigurationPanel1.addPropertyChangeListener(OperationConfigurationPanel.FAULT_PARTS_LISTENER, new PropertyChangeListener() {
            
            public void propertyChange(PropertyChangeEvent evt) {
                validateAll();
            }
        });
    }
    
    
    private boolean isValidName(String text) {
        try {
            boolean isValid  = org.netbeans.modules.xml.xam.dom.Utils.isValidNCName(text);
            if(!isValid) {
                mErrorMessage = NbBundle.getMessage(OperationPanel.class, "ERR_MSG_INVALID_NAME" , text);
            } else {
                mErrorMessage = null;
            }
            
        }  catch(Exception ex) {
            ex.printStackTrace();
        }
        
        return mErrorMessage == null;
    }
    
    private void validateAll() {
        
        boolean validOperation = isValidName(this.operationConfigurationPanel1.getOperationNameTextField().getText());
        if(!validOperation) {
            updateMessagePanel();
            return;
        }
        
                
        boolean isValidInputMessage = isValidInputMessage();
        if(!isValidInputMessage) {
            updateMessagePanel();
            return;
        }
        
        boolean isValidOutputMessage = isValidOutputMessage();
        if(!isValidOutputMessage) {
            updateMessagePanel();
            return;
        }
        
        boolean isValidFaultMessage = isValidFaultMessage();
        if(!isValidFaultMessage) {
            updateMessagePanel();
            return;
        }
        
        this.mErrorMessage = null;
        this.mWarningMessage = null;
        updateMessagePanel();
        
    }
    

    private boolean isValidInputMessage() {
        boolean valid = true;
        
        String messageName = this.operationConfigurationPanel1.getInputMessageName();
        if(messageName != null) {
            //if message is not existing message
            if(!isExistingMessage(messageName)) {
                valid = isValidName(messageName);
            }
        }
        return valid;
    }
    
    private boolean isValidOutputMessage() {
        boolean valid = true;
        
        String messageName = this.operationConfigurationPanel1.getOutputMessageName();
        if(messageName != null) {
            //if message is not existing message
            if(!isExistingMessage(messageName)) {
                valid = isValidName(messageName);
            }
        }
        return valid;
    }

        
    private boolean isValidFaultMessage() {
        boolean valid = true;
        
        String messageName = this.operationConfigurationPanel1.getFaultMessageName();
        if(messageName != null) {
            //if message is not existing message
            if(!isExistingMessage(messageName)) {
                valid = isValidName(messageName);
            }
        }
        
        List<PartAndElementOrType> faultParts = operationConfigurationPanel1.getFaultMessageParts();
        
        if (faultParts != null && faultParts.isEmpty()) {
            if (!valid) {
                mErrorMessage = null;
                valid = true;
            }
            if (messageName != null && messageName.length() > 0) {
                mWarningMessage = NbBundle.getMessage(OperationPanel.class, "WARNING_NO_PARTS_IN_FAULT", messageName);
                valid = false;
            }
        }
        
        return valid;
    }
    
    
    private boolean isExistingMessage(String messageName) {
         return existingMessages.contains(messageName);
     }
    
    private void updateMessagePanel() {
        if(this.mErrorMessage != null) {
            commonMessagePanel1.setErrorMessage(mErrorMessage);
            if(this.mDD != null) {
                this.mDD.setValid(false);
            }
        } else {
            if (mWarningMessage != null) {
                commonMessagePanel1.setWarningMessage(mWarningMessage);
            } else {
                commonMessagePanel1.setMessage(""); 
            }
            if(this.mDD != null) {
                this.mDD.setValid(true);
            }
            
        }
        
        
        
    }
    
    
    class OperationNameTextChangeListener implements DocumentListener {
     
         public void changedUpdate(DocumentEvent e) {
            validateAll();
         }
         
         public void insertUpdate(DocumentEvent e) {
             validateAll();
         }

         public void removeUpdate(DocumentEvent e) {
             validateAll();
         }
 
    }
    
    class MessageNameTextChangeListener implements DocumentListener {
     
         public void changedUpdate(DocumentEvent e) {
             validateAll();
         }
         
         public void insertUpdate(DocumentEvent e) {
             validateAll();
         }

         public void removeUpdate(DocumentEvent e) {
             validateAll();
         }
         
        
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.netbeans.modules.xml.wsdl.ui.view.common.CommonMessagePanel commonMessagePanel1;
    private org.netbeans.modules.xml.wsdl.ui.view.OperationConfigurationPanel operationConfigurationPanel1;
    // End of variables declaration//GEN-END:variables
    
}
