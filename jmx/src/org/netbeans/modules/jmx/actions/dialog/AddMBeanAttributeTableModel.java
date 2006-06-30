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
 * Software is Sun Microsystems, Inc. Portions Copyright 2004-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jmx.actions.dialog;

import org.netbeans.modules.jmx.MBeanAttribute;
import org.netbeans.modules.jmx.mbeanwizard.tablemodel.MBeanAttributeTableModel;

/**
 * Allows the table to have non-editable cells.
 * @author tl156378
 */
public class AddMBeanAttributeTableModel extends MBeanAttributeTableModel {

    private int firstEditable = 0;

    /**
     * Method returning wheter the cell (r,c) is editable or not
     * @param r the row of the cell
     * @param c the column of the cell
     * @return boolean true if the cell is editable
     */
    public boolean isCellEditable(int r, int c) {
        if (r < firstEditable)
            return false;
        else 
            return super.isCellEditable(r,c);
    }

    /**
     * Sets the index of the first editable row.
     * @param firstEditable <CODE>int</CODE> index of the first editable row.
     */
    public void setFirstEditable(int firstEditable) {
        this.firstEditable = firstEditable;
    }
    
    /**
     * Gets the index of the first editable row.
     * @return <CODE>int</CODE> index of the first editable row.
     */
    public int getFirstEditable() {
        return firstEditable;
    }
    
    /**
     * Used to add an attribute to the model of this table.
     * @param attribute <CODE>MBeanAttribute</CODE> attribute to add.
     */
    public void addAttribute(MBeanAttribute attribute) {
        data.add(attribute);
        
        //table is informed about the change to update the view
        this.fireTableDataChanged();
    }
}
