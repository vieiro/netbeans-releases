/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is Forte for Java, Community Edition. The Initial
 * Developer of the Original Code is Sun Microsystems, Inc. Portions
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.developer.editors;

import java.awt.Point;
import java.util.ResourceBundle;

import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
*
* @author   Ian Formanek
* @version  1.00, 01 Sep 1998
*/
public class PointCustomEditor extends javax.swing.JPanel implements EnhancedCustomPropertyEditor {

  // the bundle to use
  static ResourceBundle bundle = NbBundle.getBundle (
    PointCustomEditor.class);

static final long serialVersionUID =-4067033871196801978L;
  /** Initializes the Form */
  public PointCustomEditor(PointEditor editor) {
    initComponents ();
    this.editor = editor;
    Point point = (Point)editor.getValue ();
    if (point == null) point = new Point (0, 0);
    xField.setText (""+point.x);
    yField.setText (""+point.y);

    setBorder (new javax.swing.border.EmptyBorder (new java.awt.Insets(5, 5, 5, 5)));
    insidePanel.setBorder (new javax.swing.border.CompoundBorder (
      new javax.swing.border.TitledBorder (
        new javax.swing.border.EtchedBorder (), 
        " " + bundle.getString ("CTL_Point") + " "
      ), 
      new javax.swing.border.EmptyBorder (new java.awt.Insets(5, 5, 5, 5))));

    xLabel.setText (bundle.getString ("CTL_X"));
    yLabel.setText (bundle.getString ("CTL_Y"));

    HelpCtx.setHelpIDString (this, PointCustomEditor.class.getName ());
  }

  public java.awt.Dimension getPreferredSize () {
    return new java.awt.Dimension (280, 160);
  }

  public Object getPropertyValue () throws IllegalStateException {
    try {
      int x = Integer.parseInt (xField.getText ());
      int y = Integer.parseInt (yField.getText ());
      return new Point (x, y);
    } catch (NumberFormatException e) {
      throw new IllegalStateException ();
    }
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the FormEditor.
   */
  private void initComponents () {//GEN-BEGIN:initComponents
    setLayout (new java.awt.BorderLayout ());

    insidePanel = new javax.swing.JPanel ();
    insidePanel.setLayout (new java.awt.GridBagLayout ());
    java.awt.GridBagConstraints gridBagConstraints1;

      xLabel = new javax.swing.JLabel ();
      xLabel.setText ("X:");

    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
    insidePanel.add (xLabel, gridBagConstraints1);

      xField = new javax.swing.JTextField ();
      xField.addActionListener (new java.awt.event.ActionListener () {
          public void actionPerformed (java.awt.event.ActionEvent evt) {
            updateInsets (evt);
          }
        }
      );

    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.gridwidth = 0;
    gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints1.insets = new java.awt.Insets (4, 8, 4, 0);
    gridBagConstraints1.weightx = 1.0;
    insidePanel.add (xField, gridBagConstraints1);

      yLabel = new javax.swing.JLabel ();
      yLabel.setText ("Y:");

    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
    insidePanel.add (yLabel, gridBagConstraints1);

      yField = new javax.swing.JTextField ();
      yField.addActionListener (new java.awt.event.ActionListener () {
          public void actionPerformed (java.awt.event.ActionEvent evt) {
            updateInsets (evt);
          }
        }
      );

    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.gridwidth = 0;
    gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints1.insets = new java.awt.Insets (4, 8, 4, 0);
    gridBagConstraints1.weightx = 1.0;
    insidePanel.add (yField, gridBagConstraints1);


    add (insidePanel, "Center");

  }//GEN-END:initComponents


  private void updateInsets (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateInsets
    try {
      int x = Integer.parseInt (xField.getText ());
      int y = Integer.parseInt (yField.getText ());
      editor.setValue (new Point (x, y));
    } catch (NumberFormatException e) {
      // [PENDING beep]
    }
  }//GEN-LAST:event_updateInsets


// Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel insidePanel;
  private javax.swing.JLabel xLabel;
  private javax.swing.JTextField xField;
  private javax.swing.JLabel yLabel;
  private javax.swing.JTextField yField;
// End of variables declaration//GEN-END:variables

  private PointEditor editor;

}


/*
 * Log
 *  10   Gandalf   1.9         10/22/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  9    Gandalf   1.8         8/18/99  Ian Formanek    Fixed bug 2322 - Some PE
 *       couldn't be initialized - en exception is issued
 *  8    Gandalf   1.7         8/17/99  Ian Formanek    Generated serial version
 *       UID
 *  7    Gandalf   1.6         7/8/99   Jesse Glick     Context help.
 *  6    Gandalf   1.5         6/30/99  Ian Formanek    Reflecting changes in 
 *       editors packages and enhanced property editor interfaces
 *  5    Gandalf   1.4         6/8/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  4    Gandalf   1.3         6/2/99   Ian Formanek    Fixed event handlers
 *  3    Gandalf   1.2         5/31/99  Ian Formanek    Updated to X2 format
 *  2    Gandalf   1.1         3/4/99   Jan Jancura     bundle moved
 *  1    Gandalf   1.0         1/5/99   Ian Formanek    
 * $
 */
