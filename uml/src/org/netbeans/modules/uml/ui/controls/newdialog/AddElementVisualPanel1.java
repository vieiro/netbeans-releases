/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

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

package org.netbeans.modules.uml.ui.controls.newdialog;

import java.awt.Component;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.modules.uml.common.Util;
import org.netbeans.modules.uml.common.generics.ETPairT;
import org.netbeans.modules.uml.core.metamodel.core.foundation.IConfigManager;
import org.netbeans.modules.uml.core.metamodel.core.foundation.INamespace;
import org.netbeans.modules.uml.core.support.umlsupport.ProductRetriever;
import org.netbeans.modules.uml.core.support.umlsupport.XMLManip;
import org.netbeans.modules.uml.ui.support.NewElementKind;
import org.netbeans.modules.uml.ui.support.commonresources.CommonResourceManager;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

public final class AddElementVisualPanel1 extends JPanel 
      implements DocumentListener, ListSelectionListener, INewUMLFileTemplates {
    
    private AddElementWizardPanel1 panel;
    private INewDialogElementDetails mDetails = null;
    private static HashMap elementTypeNameMap = new HashMap();
    private static org.dom4j.Document m_doc = null;
    private java.util.ResourceBundle bundle =
            NbBundle.getBundle(NewUMLDiagVisualPanel1.class);
    
    /** Creates new form AddElementVisualPanel1 
     * @param panel 
     */
    public AddElementVisualPanel1(AddElementWizardPanel1 panel) {
        this.panel = panel;
        getElementListFromConfigFile();
        initComponents();
        elementTypeList.addListSelectionListener(this);
        // Register listener on the textFields to validate entered text
        elementNameTextField.getDocument().addDocumentListener(this);
    }
    
    public String getName() {
        return org.openide.util.NbBundle.getBundle(AddElementVisualPanel1.class).getString("IDS_NEWELEMENT");
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
   // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
   private void initComponents()
   {

      jLabel1 = new javax.swing.JLabel();
      jScrollPane1 = new javax.swing.JScrollPane();
      elementTypeList = new javax.swing.JList();
      jLabel2 = new javax.swing.JLabel();
      elementNameTextField = new javax.swing.JTextField();
      jLabel3 = new javax.swing.JLabel();
      nameSpaceComboBox = new javax.swing.JComboBox();

      jLabel1.setLabelFor(elementTypeList);
      org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getBundle(AddElementVisualPanel1.class).getString("IDS_ELEMENTTYPE")); // NOI18N

      elementTypeList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
      elementTypeList.setCellRenderer(new ElementListCellRenderer());
      jScrollPane1.setViewportView(elementTypeList);
      elementTypeList.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(AddElementVisualPanel1.class).getString("ACSD_NEW_ELEMENT_WIZARD_ELEMENTTYPE_LIST")); // NOI18N

      jLabel2.setLabelFor(elementNameTextField);
      org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getBundle(AddElementVisualPanel1.class).getString("IDS_ELEMENTNAME")); // NOI18N

      elementNameTextField.setText(NewDialogUtilities.getDefaultElementName());
      elementNameTextField.selectAll();
      elementNameTextField.requestFocus();

      jLabel3.setLabelFor(nameSpaceComboBox);
      org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getBundle(AddElementVisualPanel1.class).getString("IDS_NAMESPACE")); // NOI18N

      org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(layout.createSequentialGroup()
            .addContainerGap()
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
               .add(jLabel1)
               .add(jLabel2)
               .add(jLabel3))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
               .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
               .add(elementNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
               .add(nameSpaceComboBox, 0, 304, Short.MAX_VALUE))
            .addContainerGap())
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(layout.createSequentialGroup()
            .addContainerGap()
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
               .add(jLabel1)
               .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
               .add(jLabel2)
               .add(elementNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
               .add(jLabel3)
               .add(nameSpaceComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
      );

      elementNameTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(AddElementVisualPanel1.class).getString("ACSD_NEW_ELEMENT_WIZARD_ELEMENTNAME_TEXTFIELD")); // NOI18N
      nameSpaceComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(AddElementVisualPanel1.class).getString("ACSD_NEW_ELEMENT_WIZARD_ELEMENTNAMESPACE_COMBOBOX")); // NOI18N
   }// </editor-fold>//GEN-END:initComponents

   public void read(WizardDescriptor wizDesc) {
        mDetails = (INewDialogElementDetails) wizDesc.getProperty(this.ELEMENT_DETAILS);
        
        populateList();
        populateCombobox();
    }
   
   void store(WizardDescriptor wizDesc)
   {
      String elemName = (String)getSelectedListElement();
      String elemType = (String) elementTypeNameMap.get(elemName);
      
      // CR#6263225 cvc
      //  added arrays to NewElementKind to make the
      //  maintenance of adding/changing/removing elements much easier
      //	switch/case logic no longer needed
      List eleNameList = Arrays.asList(NewElementKind.ELEMENT_NAMES);
      int index = eleNameList.indexOf(elemType);
      
      if (index == -1)
         // "None" element type, default to the 1st element in the list
         mDetails.setElementKind(
               NewElementKind.ELEMENT_NUMBERS[0].intValue());
      else
         mDetails.setElementKind(
               NewElementKind.ELEMENT_NUMBERS[index].intValue());
      
      //get the name
      mDetails.setName(getElementName());
      
      // Get the namespace
      INamespace pSelectedNamespace = NewDialogUtilities.getNamespace(
            (String) getSelectedNamespace());
      mDetails.setNamespace(pSelectedNamespace);
      
      // store the element Details
      wizDesc.putProperty(this.ELEMENT_DETAILS, mDetails);
   }
   
   private void getElementListFromConfigFile()
   {
      if (elementTypeNameMap == null || elementTypeNameMap.size() == 0)
      {
         if (elementTypeNameMap == null)
         {
            elementTypeNameMap = new HashMap();
         }
         ETPairT typeNamePair = null;
         IConfigManager conMan = ProductRetriever.retrieveProduct().getConfigManager();
         String fileName = conMan.getDefaultConfigLocation();
         fileName += "NewDialogDefinitions.etc"; // NOI18N
         m_doc = XMLManip.getDOMDocument(fileName);
         org.dom4j.Node node = m_doc.selectSingleNode(
               "//PropertyDefinitions/PropertyDefinition"); // NOI18N
         
         if (node != null)
         {
            String displName ="";
            org.dom4j.Element elem = (org.dom4j.Element)node;
            String name = elem.attributeValue("name"); // NOI18N
            List nodeList = m_doc.selectNodes(
                  "//PropertyDefinition/aDefinition[@name='" // NOI18N
                  + "Element" + "']/aDefinition"); // NOI18N
            
            int count = nodeList.size();
            for (int i=0; i<count; i++)
            {
               org.dom4j.Element subNode = (org.dom4j.Element)nodeList.get(i);
               displName = subNode.attributeValue("displayName"); // NOI18N
               elementTypeNameMap.put(bundle.getString(displName), subNode.attributeValue("name")); // NOI18N
            }
         }
      }
   }
   
    private void populateList()
    {
       getElementListFromConfigFile();
       if (elementTypeList != null)
       {
          // fixed #107312. Use TreeSet to have the set sorted
          Set elemDisplaySet = new TreeSet(elementTypeNameMap.keySet());
          if (elemDisplaySet != null)
          {
             elementTypeList.setListData(elemDisplaySet.toArray());
          }
          // select the 1st element in the list by default
          elementTypeList.setSelectedIndex(0);
       }
    }

    private void populateCombobox() {        
        if ((nameSpaceComboBox != null) && (mDetails != null)) {   
            NewDialogUtilities.loadNamespace(nameSpaceComboBox, mDetails.getNamespace());
        }
    }
    
    protected String getElementName() {
        return elementNameTextField.getText().trim();
    }
    
    protected Object getSelectedNamespace() {
        return nameSpaceComboBox.getSelectedItem();
    }
    
    protected Object getSelectedListElement() {
        return elementTypeList.getSelectedValue();
    }
    
    protected int getSelectedListIndex() {
        return elementTypeList.getSelectedIndex();        
    }
    
    public boolean isValid(WizardDescriptor wizDesc)
    {
       boolean valid = true;
       String errorMsg = "";
       
       // validate if an element type is selected
       String selectedElemType = (String) this.getSelectedListElement();
       if (selectedElemType == null || selectedElemType.length() == 0)
       {
          errorMsg = bundle.getString("IDS_PLEASESELECTAELEMENT"); // NOI18N
          valid = false;
       }
       
       // validate element name
       if (valid)
       {
          String elemName = getElementName();
          String trimmedName = elemName.trim();
          int trimmedLen = trimmedName.length();
          // boolean bNameHasSpaces = (elemName.length() > trimmedLen);
          if (trimmedLen == 0)
          {  //empty element name
             errorMsg = bundle.getString("IDS_PLEASEENTERELEMENTNAME"); // NOI18N
             valid = false;
          }
       }
       
       // check for element name collision in the selected namespace
       if (valid)
       {
          // Get the namespace
          INamespace selectedNamespace = NewDialogUtilities.getNamespace(
                (String) getSelectedNamespace());
          
          if ( selectedNamespace != null )
          {
             String selectedElem = (String)getSelectedListElement();
             String elemType = (String) elementTypeNameMap.get(selectedElem);
             
             if (Util.hasNameCollision(selectedNamespace, getElementName(), elemType, null))
             {
                errorMsg = bundle.getString("IDS_NAMESPACECOLLISION"); // NOI18N
                valid = false;
             }
          }
       }
       wizDesc.putProperty(PROP_WIZARD_ERROR_MESSAGE, errorMsg);
       return valid;
    }
    
    class ElementListCellRenderer extends JLabel implements ListCellRenderer {
        public Icon getImageIcon(String elemName) {
            Icon retIcon = null;
            String displayName = NewDialogResources.getStringKey(elemName);
            String str = "//PropertyDefinition/aDefinition[@name='" + // NOI18N
                    "Element" + "']/aDefinition[@displayName='" +  // NOI18N
                    displayName + "']"; // NOI18N
            
            org.dom4j.Node node = m_doc.selectSingleNode(str);
            if (node.getNodeType() == org.dom4j.Element.ELEMENT_NODE) {
                org.dom4j.Element elem = (org.dom4j.Element)node;
                String fileName = elem.attributeValue("image"); // NOI18N
                File file = new File(fileName);
                retIcon = CommonResourceManager.instance().getIconForFile(fileName);
            }
            return retIcon;
        }
        
        public Component getListCellRendererComponent(
                JList list,
                Object value,            // value to display
                int index,               // cell index
                boolean isSelected,      // is the cell selected
                boolean cellHasFocus)    // the list and the cell have the focus
        {
            String s = value.toString();
            setText(s);
            setIcon(getImageIcon(s));
            
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }
    
    // implementing abstact methods in DocumentListener
    public void changedUpdate(DocumentEvent event)
    {
        if (panel != null)
        {
            panel.fireChangeEvent();
        }
    }
    
    public void insertUpdate( DocumentEvent event )
    {
        changedUpdate(event);
    }
    
    public void removeUpdate(DocumentEvent event)
    {
        changedUpdate(event);
    }
    
    // methods in ListSelectionListener
    public void valueChanged(ListSelectionEvent e) {
        //fire change event to validate the selection
        if (panel != null) {
            panel.fireChangeEvent();
        }
    }
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JTextField elementNameTextField;
   private javax.swing.JList elementTypeList;
   private javax.swing.JLabel jLabel1;
   private javax.swing.JLabel jLabel2;
   private javax.swing.JLabel jLabel3;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JComboBox nameSpaceComboBox;
   // End of variables declaration//GEN-END:variables
}

