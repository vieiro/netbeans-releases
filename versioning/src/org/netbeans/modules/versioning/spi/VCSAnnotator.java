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
package org.netbeans.modules.versioning.spi;

import javax.swing.*;
import java.awt.Image;

/**
 * Versioning systems that need to annotate nodes' labels and icons implement this class.
 *  
 * @author Maros Sandor
 */
public abstract class VCSAnnotator {

    /**
     * Allows a versioning system to decorate given name with HTML markup. This can be used to hilight file status. 
     * 
     * @param name text to decorate
     * @param context a context this name represents
     * @return decorated name
     */
    public String annotateName(String name, VCSContext context) {
        return name;
    }

    public Image annotateIcon(Image icon, VCSContext context) {
        return icon;
    }

    /**
     * Indicates that returned actions will be used to construct the main menu.
     */
    public static final int DEST_MAINMENU = 1;

    public static final int DEST_POPUPMENU = 2;
    
    /**
     * 
     * @param context
     * @param destinationFlags
     * @return
     */
    public Action[] getActions(VCSContext context, int destination) {
        return new Action[0];
    }
}
