/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.jellytools.modules.javacvs.actions;

import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.actions.Action;

/** Used to call "CVS|Show Changes" popup or "CVS|Show Changes..." main menu item.
 * @see Action
 * @author Jiri.Skrivanek@sun.com
 */
public class ShowChangesAction extends Action {

    // "CVS"
    private static final String CVS_ITEM = Bundle.getStringTrimmed(
            "org.netbeans.modules.versioning.system.cvss.Bundle", "Menu/CVS");
    // "Show Changes"
    private static final String SHOW_POPUP_ITEM = Bundle.getStringTrimmed(
            "org.netbeans.modules.versioning.system.cvss.Bundle",
            "CTL_PopupMenuItem_Status");
    // "Commit "filename"..."
    private static final String SHOW_ITEM = Bundle.getStringTrimmed(
            "org.netbeans.modules.versioning.system.cvss.ui.actions.status.Bundle",
            "CTL_MenuItem_Status_Context");
    
    /** Creates new ShowChangesAction instance. */
    public ShowChangesAction() {
        super(CVS_ITEM+"|"+SHOW_ITEM, CVS_ITEM+"|"+SHOW_POPUP_ITEM);
    }
    
    /** Performs main menu with exact file name.
     * @param filename file name
     */
    public void performMenu(String filename) {
        String oldMenuPath = this.menuPath;
        // CVS|Commit "filename"...
        this.menuPath = CVS_ITEM+"|"+
            Bundle.getStringTrimmed(
                "org.netbeans.modules.versioning.system.cvss.ui.actions.status.Bundle",
                "CTL_MenuItem_Status_Context",
                new String[] {filename});
        try {
            super.performMenu();
        } finally {
            this.menuPath = oldMenuPath;
        }
    }
}
