/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-200? Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.j2ee.ejbjarproject.ui;

import java.io.File;
import org.openide.options.SystemOption;
import org.openide.util.NbBundle;


public class FoldersListSettings extends SystemOption {

    static final long serialVersionUID = -4905094097265543014L;
    
    private static final String LAST_EXTERNAL_SOURCE_ROOT = "srcRoot";  //NOI18N

    private static final String NEW_PROJECT_COUNT = "newProjectCount"; //NOI18N
    
    private static final String LAST_USED_CP_FOLDER = "lastUsedClassPathFolder";    //NOI18N
    
    private static final String SHOW_AGAIN_BROKEN_REF_ALERT = "showAgainBrokenRefAlert"; //NOI18N

    private static final String LAST_USED_SOURCE_ROOT_FOLDER = "lastUsedSourceRootFolder";   //NOI18N

    public String displayName() {
        return NbBundle.getMessage (FoldersListSettings.class, "TXT_WebProjectFolderList"); //NOI18N
    }

    public String getLastExternalSourceRoot () {
        return (String) getProperty(LAST_EXTERNAL_SOURCE_ROOT);
    }

    public void setLastExternalSourceRoot (String path) {
        putProperty (LAST_EXTERNAL_SOURCE_ROOT, path, true);
    }

    public int getNewProjectCount () {
        Integer value = (Integer) getProperty (NEW_PROJECT_COUNT);
        return value == null ? 0 : value.intValue();
    }

    public void setNewProjectCount (int count) {
        this.putProperty(NEW_PROJECT_COUNT, new Integer(count),true);
    }
    
    public boolean isShowAgainBrokenRefAlert() {
        Boolean b = (Boolean)getProperty(SHOW_AGAIN_BROKEN_REF_ALERT);
        return b == null ? true : b.booleanValue();
    }
    
    public void setShowAgainBrokenRefAlert(boolean again) {
        this.putProperty(SHOW_AGAIN_BROKEN_REF_ALERT, Boolean.valueOf(again), true);
    }

    public static FoldersListSettings getDefault () {
        return (FoldersListSettings) SystemOption.findObject (FoldersListSettings.class, true);
    }

    public File getLastUsedSourceRootFolder () {
        String folder = (String) this.getProperty (LAST_USED_SOURCE_ROOT_FOLDER);
        if (folder == null) {
            folder = System.getProperty("user.home");    //NOI18N
        }
        return new File (folder);
    }

    public void setLastUsedSourceRootFolder (File folder) {
        assert folder != null : "Folder can not be null";
        String path = folder.getAbsolutePath();
        this.putProperty (LAST_USED_SOURCE_ROOT_FOLDER, path, true);
    }
    
    public File getLastUsedClassPathFolder () {
        String lucpr = (String) this.getProperty (LAST_USED_CP_FOLDER);
        if (lucpr == null) {
            lucpr = System.getProperty("user.home");    //NOI18N
        }
        return new File (lucpr);
    }

    public void setLastUsedClassPathFolder (File folder) {
        assert folder != null : "ClassPath root can not be null";
        String path = folder.getAbsolutePath();
        this.putProperty(LAST_USED_CP_FOLDER, path, true);
    }
}
