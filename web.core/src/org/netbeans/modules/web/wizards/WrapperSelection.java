/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.web.wizards;

import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.loaders.TemplateWizard;

/** A single panel descriptor for a wizard.
 * You probably want to make a wizard iterator to hold it.
 *
 * @author  Milan Kuchtiak
 */
public class WrapperSelection implements WizardDescriptor.Panel {
    
    /** The visual component that displays this panel.
     * If you need to access the component from this class,
     * just use getComponent().
     */
    private WrapperPanel component;
    private transient TemplateWizard wizard;
    
    /** Create the wizard panel descriptor. */
    public WrapperSelection(TemplateWizard wizard) {
        this.wizard=wizard;
    }
    
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            component = new WrapperPanel(this);
        }
        return component;
    }
    
    public HelpCtx getHelp() {
        return null;
    }
    
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...), then:
        // return someCondition ();
        // and when this condition changes (last form field filled in...) then:
        // fireChangeEvent ();
        // and uncomment the complicated stuff below.
    }
    
/*
    public boolean isValid() {
	if(isListenerSelected()) { 
	    wizard.putProperty("WizardPanel_errorMessage", ""); //NOI18N
	    return true;
	}
	wizard.putProperty("WizardPanel_errorMessage", //NOI18N
            org.openide.util.NbBundle.getMessage(ListenerPanel.class,"MSG_noListenerSelected")); 
	return false; 
    }
*/
 
    public final void addChangeListener(ChangeListener l) {}
    public final void removeChangeListener(ChangeListener l) {}
/*
    private final Set listeners = new HashSet (1); // Set<ChangeListener>
    public final void addChangeListener (ChangeListener l) {
        synchronized (listeners) {
            listeners.add (l);
        }
    }
    public final void removeChangeListener (ChangeListener l) {
        synchronized (listeners) {
            listeners.remove (l);
        }
    }
    protected final void fireChangeEvent () {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet (listeners).iterator ();
        }
        ChangeEvent ev = new ChangeEvent (this);
        while (it.hasNext ()) {
            ((ChangeListener) it.next ()).stateChanged (ev);
        }
    }
*/
    
    // You can use a settings object to keep track of state.
    // Normally the settings object will be the WizardDescriptor,
    // so you can use WizardDescriptor.getProperty & putProperty
    // to store information entered by the user.
    public void readSettings(Object settings) {
    }
    public void storeSettings(Object settings) {
    }
    
    boolean isWrapper() {return component.isWrapper();}

}
