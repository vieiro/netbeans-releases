/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.core.filesystems;

import org.openide.filesystems.*;

/**
 * Select which FileObject is suitable.
 *
 * @author  Petr Kuzel
 * @version 1.0
 */
public interface FileObjectFilter {

    /**
     * @param fo is FileObject which should be accepted
     * @return  true if accepted
     */
    public boolean acceptFileObject (FileObject fo);        
    
}

