/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.debugger.dbx.arraybrowser;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;

import com.sun.tools.swdev.glue.dbx.GPDbxVItemDynamic;
import com.sun.tools.swdev.glue.dbx.GPDbxVItemStatic;

class ArrayView extends JComponent implements TableModelListener {

    private final String avName =
	Catalog.get("TITLE_ArrayBrowserView");  // NOI18N

    private class DimensionInfo {
	long low_range;
	long high_range;
	long low_bound;
	long high_bound;
	long stride;
	int extent; 
    }

    // VItem static info
    private int avId;
    private int avDupId;
    private String avArrayExpr;
    private String avQArrayExpr;
    // private ScalarType avScalarType;  // not set at this point
    private int avUpdateMode;
    private int avNVarying;  // == avNDim, or avNDim-1 if assumed-size
    private boolean avCLike; // is this C-like language
    private int avNDim;  // # of dimensions in aray data
    private DimensionInfo[] avDimInfo; // dimension info for avNDim

    private int avNElem; // # of total elements in array data

    private ListModel avListModel;
    private DataModel avDataModel;

    private JTable avTable;
    private JList avRowHeader;
    private JScrollPane avScroll = null;

    /*
     *  Constructor
     */

    public ArrayView() {
	setLayout(new BorderLayout());
    }

    /*
     *  Copy the data in GPDbxVItemStatic info ArrayView, and calculate
     *  the number of elements.
     */

    public void setArrayViewStatic(GPDbxVItemStatic vis) {

	avId = vis.vis_id;
	avDupId = vis.vis_dup_id;
	avArrayExpr = new String(vis.vis_array_expr);
	avQArrayExpr = new String(vis.vis_qarray_expr);
	avUpdateMode = vis.vis_update_mode;
	avNVarying = vis.vis_nvarying;
	avCLike = vis.vis_C_like;
	avNDim = vis.vis_ndim;
        avDimInfo = new DimensionInfo[avNDim];

	avNElem = 1; // so that we won't multiply by 0
	for (int i = 0; i < avNDim; i++) {
	    avDimInfo[i] = new DimensionInfo();
	    avDimInfo[i].low_range = vis.vis_dim[i].range_lo;
	    avDimInfo[i].high_range = vis.vis_dim[i].range_hi;
	    avDimInfo[i].low_bound = vis.vis_dim[i].bound_lo;
	    avDimInfo[i].high_bound = vis.vis_dim[i].bound_hi;
	    avDimInfo[i].stride = vis.vis_dim[i].stride;

	    /* 
	     * Cast the rhs to int to assign to extent.  this
	     * assumes array browser will not be asked to display
	     * very big array. 
	     */
	    if (avDimInfo[i].stride > 0)   // positive stride
		avDimInfo[i].extent = (int) ((avDimInfo[i].high_range - avDimInfo[i].low_range) / avDimInfo[i].stride) + 1;
	    else // negative stride
		avDimInfo[i].extent = (int) ((avDimInfo[i].low_range - avDimInfo[i].high_range) / avDimInfo[i].stride) + 1;
	    if (avDimInfo[i].extent != 0)  // in case of 0-content array
	        avNElem *= avDimInfo[i].extent;
	}
    }

    /*
     * Create "the view" with the data in "this".  The data is set
     * in setArrayViewStatic().
     */

    public void createArrayViewScroll() {
	int rowExtent = 0;
	int colExtent = 0;

	if (avNDim == 1) {
	    rowExtent = 1;
	    colExtent = avDimInfo[0].extent;
	} else if (avNDim == 2) {
	    rowExtent = avDimInfo[0].extent;
	    colExtent = avDimInfo[1].extent;
	} else {
	    // return;
	    // ijc FIXUP: only do 2-d now, TBC
	}

	avListModel = new ListModel(rowExtent);
        avDataModel = new DataModel(rowExtent, colExtent);

        avTable = new JTable(avDataModel);
	avTable.getModel().addTableModelListener(this);

        avTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	// avTable.setFillsViewportHeight(true);

        // Create single component to add to scrollpane
        avRowHeader = new JList(avListModel);
        avRowHeader.setFixedCellWidth(50);
        avRowHeader.setFixedCellHeight(avTable.getRowHeight());
        avRowHeader.setCellRenderer(new RowHeaderRenderer(avTable));

        avScroll = new JScrollPane(avTable);

	// ijc FIXUP: make sure this is the right name to use
	avScroll.setName(avArrayExpr);

	// Adds row-list at the left of the table
        avScroll.setRowHeaderView(avRowHeader); 

	add(avScroll, BorderLayout.CENTER);
    }

    public void setArrayViewDynamic(int n, GPDbxVItemDynamic[] vid) {

	/*
	 * only use the array content info out of GPDbxVItemDynamic
	 * the rest info is either not used (i.e, pid, base_addr) or
	 * already in GPDbxVItemStatic (i.e. ndim, low bound, hi bound)
	 *
	 *  ijc FIXUP: we might need ndim etc. in GPDbxVItemDynamic
	 *             later when supporting array slicing?
	 *
	 * For the time being assume array is either 1 or 2-d.
	 */

        String str = vid[0].vid_arrayContent;
        boolean done = false,
                baseType = false,
		complexType = false,
		intervalType = false;

	int x = 0, y = 0;
	int i = 0;

	if (str.charAt(0) == '(' )
	    complexType = true;
	else if (str.charAt(0) == '[' )
	    intervalType = true;
	else // if '+', '-', '.', or isDigit
	    baseType = true;

	while (!done) {
	    if (baseType)
		i = str.indexOf(',');
	    else if (complexType)
		i = str.indexOf(',', str.indexOf(')'));
	    else if (intervalType)
		i = str.indexOf(',', str.indexOf(']'));

	    if (i == -1) {
		done = true;
	        avDataModel.setValueAt(str, x, y);
	    } else
	        avDataModel.setValueAt(str.substring(0, i), x, y);

	    if (avCLike) {
		y += 1;
		if (avNDim == 2) {
		    if (y == avDimInfo[1].extent) {
			x += 1;
			if (x == avDimInfo[0].extent)
			    // don't go beyond array bond
			    done = true;
			else
			    y = 0;
		    }
		} else if (avNDim == 1) {
		    if (y == avDimInfo[0].extent)
			done = true;
		}
	    } else {  // assume it's either C-like, or fortran-like
		if (avNDim == 2) {
		    x += 1;
		    if (x == avDimInfo[0].extent) {
			y += 1;
			if ( y == avDimInfo[1].extent)
			    // don't go beyond array bond
			    done = true;
			else
			    x = 0;
		    }
		} else if (avNDim == 1) {
		    /* fortran 1-d array will also be displayed
		     * "horizontally", so unlike the uaual fortran
		     * way of "column-major", we treat it like C
		     * array as "row-major".
		     * 
		     * Note that this is 1-d array, but we pretend
		     * it is a 2-d array with only one row
		     */
		    y += 1;
		    if (y == avDimInfo[0].extent)
			done = true;
		}
	    }

	    if (! done)
		str = str.substring(i + 2).trim();
	}

    }

    public void tableChanged(TableModelEvent e) {
	// TBI
    }

    class RowHeaderRenderer extends JLabel implements ListCellRenderer {

	/* 
	 * Constructor creates all cells the same.  To change look
	 * for individual cells put code in getListCellRendererComponent
	 * method
	 */

	RowHeaderRenderer(JTable table) {
	    JTableHeader header = table.getTableHeader();
	    setOpaque(true);
	    setBorder(UIManager.getBorder("TableHeader.cellBorder")); // NOI18N
	    setHorizontalAlignment(CENTER);
            setForeground(header.getForeground());
            setBackground(header.getBackground());
            setFont(header.getFont());
	}

        /*
         * Returns the JLabel after setting the text of the cell
         */

        public Component getListCellRendererComponent( JList list,
                    Object value, int index, boolean isSelected, 
		    boolean cellHasFocus) {
        
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    public JScrollPane getScroll() {
	return avScroll;
    }

    public JTable getTable() {
	return avTable;
    }

    public int getId() {
	return avId;
    }

    @Override
    public String getName() {
	return avArrayExpr;
    }
}
