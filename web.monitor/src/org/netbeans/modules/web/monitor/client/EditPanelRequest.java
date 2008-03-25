/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

/**
 * EditPanelRequest.java
 *
 *
 * Created: Fri Feb 9 2001
 *
 * @author Ana von Klopp
 * @author Simran Gleason
 * @version
 */

/**
 * Contains the Request sub-panel for the EditPanel
 */

package org.netbeans.modules.web.monitor.client;

import java.awt.event.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.util.ResourceBundle;
import java.util.Hashtable;
import org.openide.util.NbBundle;
import org.netbeans.modules.web.monitor.data.*;

class EditPanelRequest extends DataDisplay {

    private final static boolean debug = false;

    private static final String [] methodChoices = {
	EditPanel.GET, 
	EditPanel.POST, 
	EditPanel.PUT
    };

    private DisplayTable requestTable = null; 
    private MonitorData monitorData = null;
    
    EditPanelRequest() { 
	super();
    }
    
    // Redesign this. It is inefficient and prevents us from
    // maintaining the sorting state
    void redisplayData() {
	setData(monitorData);
    }

    void setData(MonitorData md) {

	this.monitorData = md;
	if(debug) log("setData()");  // NOI18N
	setRequestTable(); 

	this.removeAll();
	
	int gridy = -1;
	int fullGridWidth = java.awt.GridBagConstraints.REMAINDER;

	addGridBagComponent(this, createTopSpacer(), 0, ++gridy,
			    fullGridWidth, 1, 0, 0, 
			    java.awt.GridBagConstraints.WEST,
			    java.awt.GridBagConstraints.NONE,
			    topSpacerInsets,
			    0, 0);

	addGridBagComponent(this, 
			    createHeaderLabel
			    (NbBundle.getBundle(EditPanelRequest.class).getString("MON_Request_19"),
			     NbBundle.getBundle(EditPanelRequest.class).getString("ACS_MON_Request_19A11yDesc"),
			     requestTable),
			    0, ++gridy,
			    fullGridWidth, 1, 0, 0, 
			    java.awt.GridBagConstraints.WEST,
			    java.awt.GridBagConstraints.NONE,
			    labelInsets,
			    0, 0);

	addGridBagComponent(this, requestTable, 0, ++gridy,
			    fullGridWidth, 1, 1.0, 0, 
			    java.awt.GridBagConstraints.NORTHWEST,
			    java.awt.GridBagConstraints.HORIZONTAL,
			    tableInsets,
			    0, 0);

	addGridBagComponent(this, createGlue(), 0, ++gridy,
			    1, 1, 1.0, 1.0, 
			    java.awt.GridBagConstraints.WEST,
			    java.awt.GridBagConstraints.BOTH,
			    zeroInsets,
			    0, 0);


	int gridx = -1;
	addGridBagComponent(this, createGlue(), ++gridx, ++gridy,
			    1, 1, 1.0, 0, 
			    java.awt.GridBagConstraints.WEST,
			    java.awt.GridBagConstraints.NONE,
			    buttonInsets,
			    0, 0);

	// Housekeeping
	this.setMaximumSize(this.getPreferredSize()); 
	this.repaint();
    }

    void setRequestTable() {
	
	String[] requestCategories = { 
	    NbBundle.getBundle(EditPanelRequest.class).getString("MON_Request_URI"),
	    NbBundle.getBundle(EditPanelRequest.class).getString("MON_Method"),
	    NbBundle.getBundle(EditPanelRequest.class).getString("MON_Protocol")
	};

	requestTable = 
	    new DisplayTable(requestCategories, DisplayTable.REQUEST);

	RequestData rd = monitorData.getRequestData();
	requestTable.setValueAt(rd.getAttributeValue("uri"), 0,1); //NOI18N
	requestTable.setValueAt(rd.getAttributeValue(EditPanel.METHOD),1,1);
	requestTable.setValueAt(rd.getAttributeValue("protocol"), 2,1);  // NOI18N

	javax.swing.JComboBox box = requestTable.setChoices(1, 1, methodChoices, false);
        box.getAccessibleContext().setAccessibleName(requestCategories[1]);
        box.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(EditPanelRequest.class,"ACS_MON_RequestMethod"));
        requestTable.getAccessibleContext().setAccessibleName(NbBundle.getBundle(EditPanelRequest.class).getString("ACS_MON_RequestTable_19A11yName"));
        requestTable.setToolTipText(NbBundle.getBundle(EditPanelRequest.class).getString("ACS_MON_RequestTable_19A11yDesc"));

	requestTable.addTableModelListener(new TableModelListener() {
		public void tableChanged(TableModelEvent evt) {

		    if(debug) log("tableChanged"); //NOI18N
		    
		    RequestData rd = monitorData.getRequestData();

		    // The query panel depends on the value of the
		    // method attribute. 
		    String method = rd.getAttributeValue(EditPanel.METHOD);
		    String newMethod = (String)requestTable.getValueAt(1, 1);
		    if (method != null && !method.equals(newMethod)) {
			rd.setAttributeValue(EditPanel.METHOD,    newMethod);

			if(method.equals(EditPanel.GET) && newMethod.equals(EditPanel.POST)) {

			    // Set the query string to null if we got
			    // parameters from it, o/w leave it as is
			    try {
				String queryString =
				    rd.getAttributeValue("queryString"); //NOI18N 
				Hashtable ht =
				    javax.servlet.http.HttpUtils.parseQueryString(queryString); 
				rd.setAttributeValue("queryString", ""); //NOI18N 
			    }
			    catch(Exception ex) { }


			}
			else if(method.equals(EditPanel.POST) && 
				newMethod.equals(EditPanel.GET)) {
			    Util.addParametersToQuery(rd);
			}
		    }

		    //
		    // Set the rest...
		    //
		    String uri =  (String)requestTable.getValueAt(0,1);
		    uri = uri.trim();

		    String protocol =  (String)requestTable.getValueAt(2,1);
		    protocol = protocol.trim();
		    rd.setAttributeValue("uri", uri); //NOI18N 
		    rd.setAttributeValue("protocol", protocol); //NOI18N 
		}});
    }

    public void repaint() {
	super.repaint();
	//if (editPanel != null) 
	//  editPanel.repaint();
    }

    void log(String s) {
	System.out.println("EditPanelRequest::" + s); //NOI18N
    }
    
} // EditPanelRequest
