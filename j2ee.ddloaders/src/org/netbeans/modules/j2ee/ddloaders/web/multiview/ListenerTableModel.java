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

// Netbeans
import org.netbeans.modules.j2ee.dd.api.web.Listener;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.j2ee.dd.api.common.CommonDDBean;
import org.openide.util.NbBundle;

public class ListenerTableModel extends DDBeanTableModel
{
	private static final String[] columnNames = {
            NbBundle.getMessage(ListenerTableModel.class,"TTL_ListenerClass"),
            NbBundle.getMessage(ListenerTableModel.class,"TTL_Description")
        };

        protected String[] getColumnNames() {
            return columnNames;
        }

	public void setValueAt(Object value, int row, int column)
	{
		Listener listener = (Listener)getChildren().get(row);

		if (column == 0) listener.setListenerClass((String)value);
		else listener.setDescription((String)value);
	}


	public Object getValueAt(int row, int column)
	{
		Listener listener = (Listener)getChildren().get(row);

		if (column == 0) return listener.getListenerClass();
		else {
                    String desc = listener.getDefaultDescription();
                    return (desc==null?null:desc.trim());
                }
	}
        
	public CommonDDBean addRow(Object[] values)
	{
            try {
                Listener listener = (Listener)((WebApp)getParent()).createBean("Listener"); //NOI18N
                listener.setListenerClass((String)values[0]);
                String desc = (String)values[1];
                if (desc.length()>0) listener.setDescription(desc);
                ((WebApp)getParent()).addListener(listener);
                getChildren().add(listener);
                fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
                return listener;
            } catch (ClassNotFoundException ex) {}
            return null;
	}


	public void editRow(int row, Object[] values)
	{
                Listener listener = (Listener)getChildren().get(row);
		listener.setListenerClass((String)values[0]);
                String desc=(String)values[1];
                if (desc.length()>0) listener.setDescription(desc);
                fireTableRowsUpdated(row,row);
	}
        
	public void removeRow(int row)
	{
            ((WebApp)getParent()).removeListener((Listener)getChildren().get(row));
            getChildren().remove(row);
            fireTableRowsDeleted(row, row);
            
	}
}