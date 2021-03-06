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
package org.netbeans.modules.j2ee.sun.ide.editors;

import java.awt.Component;
import java.beans.PropertyEditorSupport;

import org.openide.explorer.propertysheet.editors.EnhancedPropertyEditor;

public class ValidationMethodEditor extends PropertyEditorSupport implements EnhancedPropertyEditor {
    static String VALIDATION_TYPE = "validation"; //NOI18N
    static String TRANX_SUPPORT_TYPE = "TransactionSupport"; //NOI18N
    
    public static String editorType = VALIDATION_TYPE;
    public static String curr_Sel;
    public String[] choices = {
                 "auto-commit", // NOI18N
                 "meta-data", // NOI18N
                 "table" // NOI18N
                 };
    
    public String[] choicesTranx = {
                 "", // NOI18N
                 "XATransaction", // NOI18N
                 "LocalTransaction", // NOI18N
                 "NoTransaction"
                 };

   public ValidationMethodEditor() {
	curr_Sel = null;
   }
   
   public ValidationMethodEditor(String type) {
	curr_Sel = null;
        editorType = type;
   }

    public String getAsText () {
	return curr_Sel;
    }

    public void setAsText (String string) throws IllegalArgumentException {
        if (! editorType.equals(TRANX_SUPPORT_TYPE)) {
            if ((string == null) || (string.equals(""))) { // NOI18N
                throw new IllegalArgumentException();
            } else {
                curr_Sel = string;
            }
        } else {
            curr_Sel = string;
        }
        this.firePropertyChange();
    }

   public void setValue (Object val) {
       if (! (val instanceof String)) {
      	    throw new IllegalArgumentException ();
	}
        curr_Sel = (String) val;
    }

    public Object getValue () {
        return curr_Sel;
    }

    public String getJavaInitializationString () {
	return getAsText ();
    }

    public String[] getTags () {
        if(editorType.equals(TRANX_SUPPORT_TYPE)){
           return choicesTranx; 
        } else {
           return choices;
        }    
    }

   public Component getInPlaceCustomEditor () {
        return null;
    }

   
    public boolean hasInPlaceCustomEditor () {
        return false;
    }

    public boolean supportsEditingTaggedValues () {
        return false;
    }

 }


