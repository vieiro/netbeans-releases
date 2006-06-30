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

package org.netbeans.modules.options.generaleditor;

import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;


/**
 * Implementation of one panel in Options Dialog.
 *
 * @author Jan Jancura
 */
public final class GeneralEditorPanelController extends OptionsPanelController {


    public void update () {
        getGeneralEditorPanel ().update ();
    }

    public void applyChanges () {
        getGeneralEditorPanel ().applyChanges ();
    }
    
    public void cancel () {
        getGeneralEditorPanel ().cancel ();
    }
    
    public boolean isValid () {
        return getGeneralEditorPanel ().dataValid ();
    }
    
    public boolean isChanged () {
        return getGeneralEditorPanel ().isChanged ();
    }
    
    public HelpCtx getHelpCtx () {
        return new HelpCtx ("netbeans.optionsDialog.editor.general");
    }
    
    public JComponent getComponent (Lookup masterLookup) {
        return getGeneralEditorPanel ();
    }

    public void addPropertyChangeListener (PropertyChangeListener l) {
        getGeneralEditorPanel ().addPropertyChangeListener (l);
    }

    public void removePropertyChangeListener (PropertyChangeListener l) {
        getGeneralEditorPanel ().removePropertyChangeListener (l);
    }
    
    
    private GeneralEditorPanel generalEditorPanel;
    
    private GeneralEditorPanel getGeneralEditorPanel () {
        if (generalEditorPanel == null)
            generalEditorPanel = new GeneralEditorPanel ();
        return generalEditorPanel;
    }
}

