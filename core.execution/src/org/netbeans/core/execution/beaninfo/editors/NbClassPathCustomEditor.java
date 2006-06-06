/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.core.execution.beaninfo.editors;

import java.beans.PropertyEditor;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.netbeans.beaninfo.editors.FileEditor;
import org.openide.execution.NbClassPath;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbCollections;
import org.openide.windows.WindowManager;

/**
 * Panel for editing entries in the classpath.
 * @author  David Strupl
 */
class NbClassPathCustomEditor extends javax.swing.JPanel {
    
    /** Remember last folder, which was explored in FileChooser.*/
    private static File lastDirFolder = null;
    /** Remember last folder, which was explored in FileChooser.*/
    private static File lastJarFolder = null;
    /** Property editor associated with. */
    private PropertyEditor editor;
    /** Model of list of class path items. */
    private DefaultListModel listModel = new DefaultListModel();
    private boolean editable = true;

    
    /** Creates new form NbClassPathCustomEditor */
    public NbClassPathCustomEditor() {
        initComponents ();
        pathList.setModel(listModel);
        pathScrollPane.setViewportView(pathList);
        
        setMinimumSize (new java.awt.Dimension(400, 200));

        pathLabel.setDisplayedMnemonic(getString("CTL_Classpath.Border_Mnemonic").charAt(0));
        addDirButton.setMnemonic(getString("CTL_AddDirectory_Mnemonic").charAt(0));
        addJarButton.setMnemonic(getString("CTL_AddJAR_Mnemonic").charAt(0));
        upButton.setMnemonic(getString("CTL_MoveUp_Mnemonic").charAt(0));
        downButton.setMnemonic(getString("CTL_MoveDown_Mnemonic").charAt(0));
        removeButton.setMnemonic(getString("CTL_Remove_Mnemonic").charAt(0));
        
        pathList.getAccessibleContext().setAccessibleDescription(getString("ACSD_PathList"));
        addDirButton.getAccessibleContext().setAccessibleDescription(getString("ACSD_AddDirectory"));
        addJarButton.getAccessibleContext().setAccessibleDescription(getString("ACSD_AddJAR"));
        upButton.getAccessibleContext().setAccessibleDescription(getString("ACSD_MoveUp"));
        downButton.getAccessibleContext().setAccessibleDescription(getString("ACSD_MoveDown"));
        removeButton.getAccessibleContext().setAccessibleDescription(getString("ACSD_Remove"));
        
        getAccessibleContext().setAccessibleDescription(getString("ACSD_CustomNbClassPathEditor"));
    }

    NbClassPathCustomEditor(PropertyEditor propEd) {
        this();
        editor = propEd;
        Object value = propEd.getValue();
        if (value instanceof NbClassPath) {
            setClassPath(((NbClassPath)value).getClassPath());
        }
        if ( editor instanceof NbClassPathEditor )
            if ( ! ((NbClassPathEditor)editor).isEditable() ) {
                editable = false;
                addDirButton.setEnabled( false );
                addJarButton.setEnabled( false );
            }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        java.awt.GridBagConstraints gridBagConstraints;

        innerPanel = new javax.swing.JPanel();
        addDirButton = new javax.swing.JButton();
        addJarButton = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        pathScrollPane = new javax.swing.JScrollPane();
        pathList = new javax.swing.JList();
        pathLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        innerPanel.setLayout(new java.awt.GridBagLayout());

        innerPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 0, 11)));
        addDirButton.setText(getString("CTL_AddDirectory"));
        addDirButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                addDirButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        innerPanel.add(addDirButton, gridBagConstraints);

        addJarButton.setText(getString("CTL_AddJAR"));
        addJarButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                addJarButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        innerPanel.add(addJarButton, gridBagConstraints);

        upButton.setText(getString("CTL_MoveUp"));
        upButton.setEnabled(false);
        upButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                upButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        innerPanel.add(upButton, gridBagConstraints);

        downButton.setText(getString("CTL_MoveDown"));
        downButton.setEnabled(false);
        downButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                downButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        innerPanel.add(downButton, gridBagConstraints);

        removeButton.setText(getString("CTL_Remove"));
        removeButton.setEnabled(false);
        removeButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                removeButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        innerPanel.add(removeButton, gridBagConstraints);

        pathList.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                pathListValueChanged(evt);
            }
        });
        pathList.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                pathListMouseClicked(evt);
            }
        });

        pathScrollPane.setViewportView(pathList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 11);
        innerPanel.add(pathScrollPane, gridBagConstraints);

        pathLabel.setText(getString("CTL_Classpath.Border_Title"));
        pathLabel.setLabelFor(pathList);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        innerPanel.add(pathLabel, gridBagConstraints);

        add(innerPanel, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

  private void pathListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pathListMouseClicked
        if (evt.getClickCount() != 2) {
            // trigger the action on double-click
            return;
        }
        triggerEdit(pathList.getSelectedIndex());
  }//GEN-LAST:event_pathListMouseClicked

  private void pathListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_pathListValueChanged
      enableButtons();
  }//GEN-LAST:event_pathListValueChanged

  private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        int index = pathList.getSelectedIndex();

        Object [] selectedValues = pathList.getSelectedValues();
        for (int i = 0; i < selectedValues.length; i++) {
            listModel.removeElement(selectedValues[i]);
            fireValueChanged();
        }

        // Select some of remaining item.
        int size = listModel.getSize();
        
        if(index >= 0 && size > 0) {
            if(size == index) {
                pathList.setSelectedIndex(index - 1);
            } else if(size > index) {
                pathList.setSelectedIndex(index);
            } else {
                pathList.setSelectedIndex(0);
            }
        }
        
        // If empty disable up, down, remve buttons.
        enableButtons();
  }//GEN-LAST:event_removeButtonActionPerformed

  private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
      int i = pathList.getSelectedIndex();
      swap(i);
      pathList.setSelectedIndex(i+1);
  }//GEN-LAST:event_downButtonActionPerformed

  private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
      int i = pathList.getSelectedIndex();
      swap(i-1);
      pathList.setSelectedIndex(i - 1);
  }//GEN-LAST:event_upButtonActionPerformed

  private void addJarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addJarButtonActionPerformed

        JFileChooser chooser = FileEditor.createHackedFileChooser();
        setHelpToChooser( chooser );
        
        chooser.setFileFilter(new FileFilter() {
                                  public boolean accept(File f) {
                                      return (f.isDirectory() || f.getName().endsWith(".jar") || f.getName().endsWith(".zip")); // NOI18N
                                  }
                                  public String getDescription() {
                                      return getString("CTL_JarArchivesMask");
                                  }
                              });

        if (lastJarFolder != null) {
            chooser.setCurrentDirectory(lastJarFolder);
        }

        chooser.setDialogTitle(getString("CTL_FileSystemPanel.Jar_Dialog_Title"));
        chooser.setMultiSelectionEnabled( true );
        if (chooser.showDialog(WindowManager.getDefault ().getMainWindow (),
                               getString("CTL_Approve_Button_Title"))
                == JFileChooser.APPROVE_OPTION) {
            File[] files = chooser.getSelectedFiles();
            boolean found = false;
            for (int i=0; i<files.length; i++) {
                if ((files[i] != null) && (files[i].isFile())) {
                    found = true;
                    listModel.addElement(files[i].getAbsolutePath());
                }
            }
            if ( found ) {
                lastJarFolder = chooser.getCurrentDirectory();
                fireValueChanged();
            }
            pathList.setSelectedIndex(listModel.size() - 1);
        }
  }//GEN-LAST:event_addJarButtonActionPerformed

  private void addDirButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDirButtonActionPerformed
        JFileChooser chooser = FileEditor.createHackedFileChooser();
        setHelpToChooser( chooser );
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle(getString("CTL_FileSystemPanel.Local_Dialog_Title"));

        if (lastDirFolder != null) {
            chooser.setCurrentDirectory(lastDirFolder);
        }

        if (chooser.showDialog(WindowManager.getDefault ().getMainWindow (),
                               getString("CTL_Approve_Button_Title"))
                == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            if ((f != null) && (f.isDirectory())) {
                try {
                    f = f.getCanonicalFile ();
                } catch(IOException ioe) {
                    // ignore
                }
                lastDirFolder = f.getParentFile();

                listModel.addElement(f.getAbsolutePath());
                fireValueChanged();
                
                pathList.setSelectedIndex(listModel.size() - 1);
            }
        }
  }//GEN-LAST:event_addDirButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addDirButton;
    private javax.swing.JButton addJarButton;
    private javax.swing.JButton downButton;
    private javax.swing.JPanel innerPanel;
    private javax.swing.JLabel pathLabel;
    private javax.swing.JList pathList;
    private javax.swing.JScrollPane pathScrollPane;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables


    /** Sets value to property editor if exists.
     * @see #editor */
    private void fireValueChanged() {
        if (editor != null) {
            editor.setValue(getPropertyValue());
        }
    }
  
    /** Allows the user to edit the selected item. */
    private void triggerEdit(int index) {
        if (index < 0) {
            return;
        }
        String selectedItem = (String)listModel.elementAt(index);
        File selectedF = new File(selectedItem);
        if (selectedF.isDirectory()) {

            JFileChooser chooser = FileEditor.createHackedFileChooser();
            setHelpToChooser( chooser );
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle(getString("CTL_Edit_Local_Dialog_Title"));

            if (selectedF.getParentFile() != null) {
                chooser.setCurrentDirectory(selectedF.getParentFile());
                chooser.setSelectedFile(selectedF);
            }

            if (chooser.showDialog(WindowManager.getDefault ().getMainWindow (),
                                   getString("CTL_Approve_Button_Title"))
                    == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                if ((f != null) && (f.isDirectory())) {
                    lastDirFolder = chooser.getCurrentDirectory();
                    try {
                        f = f.getCanonicalFile ();
                    } catch(IOException ioe) {
                        // ignore
                    }

                    listModel.set(index, f.getAbsolutePath());
                    fireValueChanged();
                }
            }
        } else if (selectedF.isFile()) {
            JFileChooser chooser = FileEditor.createHackedFileChooser();
            setHelpToChooser( chooser );

            chooser.setFileFilter(new FileFilter() {
                  public boolean accept(File f) {
                      return (f.isDirectory() || f.getName().endsWith(".jar") || f.getName().endsWith(".zip")); // NOI18N
                  }
                  public String getDescription() {
                      return getString("CTL_JarArchivesMask");
                  }
              });

            chooser.setCurrentDirectory(selectedF.getParentFile());
            chooser.setSelectedFile(selectedF);

            chooser.setDialogTitle(getString("CTL_Edit_Jar_Dialog_Title"));
            if (chooser.showDialog(WindowManager.getDefault ().getMainWindow (),
                                   getString("CTL_Approve_Button_Title"))
                    == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                if ((f != null) && (f.isFile())) {
                    lastJarFolder = chooser.getCurrentDirectory();
                    listModel.set(index, f.getAbsolutePath());
                    fireValueChanged();
                }
            }
        }
    }
  
    /** Swaps item on the the position index with item on the position index+1. */
    private void swap(int index) {
        if ((index < 0)||(index >= listModel.size() -1 )) {
            return;
        }
        Object value = listModel.elementAt(index);
        listModel.removeElement(value);
        listModel.add(index + 1, value);
        fireValueChanged();
    }
    
    /** Enables buttons according to the state of the list.*/
    private void enableButtons() {
        if ( ! editable )
            return;
        removeButton.setEnabled(pathList.getSelectedIndices().length > 0);
        if (pathList.getSelectedIndices().length == 1) {
            downButton.setEnabled(pathList.getSelectedIndices()[0] < pathList.getModel().getSize() - 1);
            upButton.setEnabled(pathList.getSelectedIndices()[0] > 0);
        } else {
            downButton.setEnabled(false);
            upButton.setEnabled(false);
        }
    }
    
    /** This method parses given classPath and adds the elements to
     * the listModel.
     */
    private void setClassPath(String classPath) {
        StringTokenizer tok = new StringTokenizer(classPath, File.pathSeparator);
        while (tok.hasMoreTokens()) {
            String s = tok.nextToken();
            if (s.startsWith("\"")) { // NOI18N
                s = s.substring(1);
            }
            if (s.endsWith("\"")) { // NOI18N
                s = s.substring(0, s.length() -1 );
            }
            listModel.addElement(s);
        }
        
    }
    
    /** Get the customized property value. Implements <code>EnhancedCustomPropertyEditor</code>.
     * @return the property value
     * @exception InvalidStateException when the custom property editor does not contain a valid property value
     *           (and thus it should not be set)
     */
    public Object getPropertyValue() throws IllegalStateException {
        List<String> list = Collections.list(NbCollections.checkedEnumerationByFilter(listModel.elements(), String.class, true));
        String []arr = list.toArray(new String[list.size()]);
        return new NbClassPath(arr);
    }

    /** Gets localized string. Helper method. */
    private static final String getString(String s) {
        return NbBundle.getMessage(NbClassPathCustomEditor.class, s);
    }
    
    private void setHelpToChooser( JFileChooser chooser ) {
        HelpCtx help = HelpCtx.findHelp( this );
        if ( help != null )
            HelpCtx.setHelpIDString(chooser, help.getHelpID());
    }
}
