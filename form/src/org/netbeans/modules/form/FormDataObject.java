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

package com.netbeans.developer.modules.loaders.form;

import java.io.IOException;
import java.text.MessageFormat;

import com.netbeans.ide.*;
import com.netbeans.ide.actions.OpenAction;
import com.netbeans.ide.loaders.*;
import com.netbeans.ide.filesystems.*;
import com.netbeans.ide.util.actions.SystemAction;
import com.netbeans.ide.nodes.Node;
import com.netbeans.ide.nodes.CookieSet;
import com.netbeans.developer.modules.loaders.java.JavaDataObject;
import com.netbeans.developer.modules.loaders.java.JavaEditor;
import com.netbeans.developer.modules.loaders.form.*;

/** The DataObject for forms.
*
* @author Ian Formanek, Petr Hamernik
*/
public class FormDataObject extends JavaDataObject {
  /** generated Serialized Version UID */
//  static final long serialVersionUID = 7952143476761137063L;

//--------------------------------------------------------------------
// Static variables

  private static java.util.ResourceBundle formBundle = com.netbeans.ide.util.NbBundle.getBundle (FormDataObject.class);

  /** lock for closing window */
  private static final Object OPEN_FORM_LOCK = new Object ();

//--------------------------------------------------------------------
// Private variables

  /** If true, a postInit method is called after reparsing - used after createFromTemplate */
  transient private boolean templateInit;
  /** If true, the form is marked as modified after regeneration - used if created from template */
  transient private boolean modifiedInit;
  /** A flag to prevent multiple registration of ComponentRefListener */
  transient private boolean componentRefRegistered;


  /** The entry for the .form file */
  FileEntry formEntry;
  
//--------------------------------------------------------------------
// Constructors

  public FormDataObject (FileObject ffo, FileObject jfo, FormDataLoader loader) throws DataObjectExistsException {
    super(jfo, loader);
    init ();
  }

//--------------------------------------------------------------------
// Other methods

  /** Initalizes the FormDataObject after deserialization */
  private void init() {
    templateInit = false;
    modifiedInit = false;
    componentRefRegistered = false;
  }

  protected JavaEditor createJavaEditor () {
    return new FormEditorSupport (getPrimaryEntry (), this);
  }

  FileEntry getFormEntry () {
    return formEntry;
  }
  
  /** Help context for this object.
  * @return help context
  */
  public com.netbeans.ide.util.HelpCtx getHelpCtx () {
    return null; // [PENDING]
  }

  /** Provides node that should represent this data object. When a node for representation
  * in a parent is requested by a call to getNode (parent) it is the exact copy of this node
  * with only parent changed. This implementation creates instance
  * <CODE>DataNode</CODE>.
  * <P>
  * This method is called only once.
  *
  * @return the node representation for this data object
  * @see DataNode
  */
  protected Node createNodeDelegate () {
    return new FormDataNode (this);
  }

//--------------------------------------------------------------------
// Serialization

  private void readObject(java.io.ObjectInputStream is)
  throws java.io.IOException, ClassNotFoundException {
    is.defaultReadObject();
    init();
  }

}

/*
 * Log
 *  15   Gandalf   1.14        4/27/99  Ian Formanek    Fixed bug #1457 - Form 
 *       DataObject does not have the "Execution" properties
 *  14   Gandalf   1.13        4/26/99  Ian Formanek    
 *  13   Gandalf   1.12        4/4/99   Ian Formanek    Fixed creation from 
 *       template
 *  12   Gandalf   1.11        3/27/99  Ian Formanek    Removed obsoleted import
 *  11   Gandalf   1.10        3/24/99  Ian Formanek    
 *  10   Gandalf   1.9         3/24/99  Ian Formanek    
 *  9    Gandalf   1.8         3/22/99  Ian Formanek    
 *  8    Gandalf   1.7         3/17/99  Ian Formanek    
 *  7    Gandalf   1.6         3/17/99  Ian Formanek    
 *  6    Gandalf   1.5         3/16/99  Ian Formanek    
 *  5    Gandalf   1.4         3/14/99  Jaroslav Tulach Change of 
 *       MultiDataObject.Entry.
 *  4    Gandalf   1.3         3/10/99  Ian Formanek    Gandalf updated
 *  3    Gandalf   1.2         2/11/99  Ian Formanek    getXXXPresenter -> 
 *       createXXXPresenter (XXX={Menu, Toolbar})
 *  2    Gandalf   1.1         1/6/99   Ian Formanek    Reflecting change in 
 *       datasystem package
 *  1    Gandalf   1.0         1/5/99   Ian Formanek    
 * $
 */
