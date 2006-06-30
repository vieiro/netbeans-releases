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

package org.netbeans.modules.j2ee.ddloaders.web.multiview;

import org.netbeans.modules.j2ee.ddloaders.web.*;
import org.netbeans.modules.xml.multiview.ui.*;
import org.netbeans.modules.j2ee.dd.api.common.MessageDestinationRef;
import org.netbeans.modules.j2ee.dd.api.common.VersionNotSupportedException;

/** MessageDestRefsPanel section inner panel for Message Destination References
 *
 * Created on April 14, 2005
 * @author  mkuchtiak
 */
public class MessageDestRefsPanel extends SectionInnerPanel {

    /** Creates new form JspPGPanel */
    public MessageDestRefsPanel(SectionView sectionView, DDDataObject dObj) {
        super(sectionView);
        initComponents();
        // Error Pages
        MessageDestRefTableModel model = new MessageDestRefTableModel();
        MessageDestRefsTablePanel panel = new MessageDestRefsTablePanel(dObj, model);
        MessageDestinationRef[] refs = null;
        try {
            refs = dObj.getWebApp().getMessageDestinationRef();
        } catch (VersionNotSupportedException ex) {
            refs = new MessageDestinationRef[0];
        }
        panel.setModel(dObj.getWebApp(),refs);       
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        //gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        gridBagConstraints.weightx = 1.0;
        //gridBagConstraints.weighty = 5.0;
        add(panel, gridBagConstraints);
    }
    public javax.swing.JComponent getErrorComponent(String errorId) {
        return null;
    }

    public void setValue(javax.swing.JComponent source, Object value) {    
    }
    
    public void linkButtonPressed(Object obj, String id) {
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        filler = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        filler.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(filler, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel filler;
    // End of variables declaration//GEN-END:variables
    
}
