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
 * Software is Sun Microsystems, Inc. Portions Copyright 2004-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jmx.actions;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.modules.javacore.api.JavaModel;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CookieAction;
import org.openide.cookies.SourceCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.netbeans.modules.jmx.*;
import org.netbeans.modules.jmx.actions.dialog.AddNotifPanel;
import org.netbeans.modules.jmx.mbeanwizard.generator.AddNotifGenerator;
import org.openide.cookies.EditorCookie;
import org.openide.util.NbBundle;

/**
 * Action used to add notifications to an existing MBean.
 * @author tl156378
 */
public class AddNotifAction extends CookieAction {
    
    private DataObject dob;
    private Resource rc;
    
    /**
     * Creates a new instance of UpdateAttrAction 
     */
    public AddNotifAction() {
        putValue("noIconInMenu", Boolean.TRUE); // NOI18N
    }
    
    protected Class[] cookieClasses() {
        return new Class[] { SourceCookie.class };
    }
     
    protected int mode() {
        // allow multiple selected nodes (classes, packages)
        return MODE_EXACTLY_ONE;    
    }
    
    public boolean asynchronous() {
        return true; // yes, this action should run asynchronously
        // would be better to rewrite it to synchronous (running in AWT thread),
        // just replanning test generation to RequestProcessor
    }  
    
    protected boolean enable (Node[] nodes) {
        if (!super.enable(nodes)) return false;
        if (nodes.length == 0) return false;
        
        dob = (DataObject)nodes[0].getCookie(DataObject.class);
        FileObject fo = null;
        if (dob == null) return false;
        
        fo = dob.getPrimaryFile();
        
        JavaClass foClass = WizardHelpers.getJavaClassInProject(fo);
        if (foClass == null) return false;
        
        //We need to do all MDR access in a transaction
        JavaModel.getJavaRepository().beginTrans(false);
        try {
            JavaModel.setClassPath(fo);
            rc = JavaModel.getResource(fo);
            boolean isMBean = Introspector.testCompliance(foClass);
            boolean isNotifBroadCaster = Introspector.isNotifBroadCaster(foClass);
            
            return isMBean && !isNotifBroadCaster;
        } finally {
            JavaModel.getJavaRepository().endTrans();
        }
    }
    
    protected void performAction (Node[] nodes) {
        // show configuration dialog
        // when dialog is canceled, escape the action
        AddNotifPanel cfg = new AddNotifPanel(nodes[0]);
        if (!cfg.configure()) {
            return;
        }
        AddNotifGenerator generator = new AddNotifGenerator();
        
        //We need the file object to determine the classpath
        FileObject fo = null;
        if (dob != null) fo = dob.getPrimaryFile();
        try {
            generator.update(fo, cfg.getMBeanClass(),rc,cfg.getNotifications(),
                    cfg.getGenBroadcastDeleg(),cfg.getGenSeqNumber());
            EditorCookie ec = (EditorCookie)dob.getCookie(EditorCookie.class);
            ec.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public HelpCtx getHelpCtx () {
        return new HelpCtx(""); // NOI18N
    }
    
    public String getName () {
        return NbBundle.getMessage (AddNotifAction.class,"LBL_Action_AddMBeanNotification"); // NOI18N
    }
}
