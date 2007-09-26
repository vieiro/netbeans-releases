/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.versioning.system.cvss.ui.actions.commit;

import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;
import org.openide.awt.Mnemonics;
import org.netbeans.modules.versioning.system.cvss.CvsModuleConfig;
import org.netbeans.modules.versioning.system.cvss.FileStatusCache;
import org.netbeans.modules.versioning.system.cvss.CvsVersioningSystem;
import org.netbeans.modules.versioning.system.cvss.FileInformation;
import org.netbeans.modules.versioning.spi.VCSContext;

import javax.swing.*;
import java.io.File;
import java.awt.event.ActionEvent;

/**
 * Excludes selected nodes from commit.
 *
 * @author Maros Sandor
 */
public class ExcludeFromCommitAction extends AbstractAction implements Presenter.Menu {

    private final File [] files;
    private final int status;

    public ExcludeFromCommitAction(VCSContext ctx) {
        this(ctx.getRootFiles().toArray(new File[0]));
    }

    public ExcludeFromCommitAction(File[] files) {
        this.files = files;
        putValue(Action.NAME, NbBundle.getBundle(ExcludeFromCommitAction.class).getString("CTL_MenuItem_ExcludeFromCommit"));
        status = getActionStatus();
        if (status == 1) {
            putValue("BooleanState.Selected", Boolean.FALSE);  // NOI18N
        } else if (status == 2) {
            putValue("BooleanState.Selected", Boolean.TRUE);  // NOI18N
        }
        setEnabled(status != -1);
    }

    public boolean isEnabled() {
        FileStatusCache cache = CvsVersioningSystem.getInstance().getStatusCache();
        for (File file : files) {
            if ((cache.getStatus(file).getStatus() & FileInformation.STATUS_IN_REPOSITORY) != 0) return true;
        }
        return false;
    }
    
    public JMenuItem getMenuPresenter() {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(this);
        if (status != -1) item.setSelected(status == 2);
        Mnemonics.setLocalizedText(item, item.getText());
        return item;
    }

    private int getActionStatus() {
        CvsModuleConfig config = CvsModuleConfig.getDefault();
        int status = -1;
        for (File file : files) {
            if (config.isExcludedFromCommit(file)) {
                if (status == 1) return -1;
                status = 2;
            }
            else {
                if (status == 2) return -1;
                status = 1;
            }
        }
        return status;
    }

    public void actionPerformed(ActionEvent e) {
        CvsModuleConfig config = CvsModuleConfig.getDefault();
        for (File file : files) {
            if (status == 1) {
                config.addExclusion(file);
            } else if (status == 2) {
                config.removeExclusion(file);
            }
        }
    }
}
