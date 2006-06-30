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

package org.netbeans.modules.j2ee.ddloaders.app;

import java.io.IOException;
//import java.util.Vector;

import org.openide.actions.*;
import org.openide.filesystems.FileObject;
import org.openide.loaders.UniFileLoader;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.DataObjectExistsException;

import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;
import org.netbeans.modules.j2ee.ddloaders.common.*;

/** Recognizes deployment descriptors of ejb modules.
 *
 * @author Ludovic Champenois
 */
public class EarDataLoader extends UniFileLoader {
    
    private static final long serialVersionUID = 3616780278674213886L;
    private static final String REQUIRED_MIME_PREFIX = "text/x-dd-ear"; // NOI18N


    public EarDataLoader () {
        super ("org.netbeans.modules.j2ee.ddloaders.app.EarDataObject");  // NOI18N
    }


    protected String defaultDisplayName () {
        return NbBundle.getMessage (EarDataLoader.class, "LBL_loaderName");
    }
    
    protected String actionsContext() {
        return "Loaders/text/x-dd/Actions/"; // NOI18N
    }

    protected void initialize () {
         super.initialize ();
            getExtensions().addMimeType(REQUIRED_MIME_PREFIX);
     }

    protected MultiDataObject createMultiObject (FileObject primaryFile)
    throws DataObjectExistsException, IOException {
	return new EarDataObject (primaryFile, this);
    }


}
