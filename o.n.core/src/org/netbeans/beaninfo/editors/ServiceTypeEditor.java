/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.beaninfo.editors;

import java.beans.*;
import java.util.Enumeration;

import org.openide.ServiceType;
import org.openide.explorer.propertysheet.*;
import org.openide.util.Lookup;


/** Support for property editor for Executor.
*
* @author   Jaroslav Tulach
*/
@SuppressWarnings("deprecation")
public class ServiceTypeEditor extends java.beans.PropertyEditorSupport implements ExPropertyEditor {

    /** Name of the custom property that can be passed in PropertyEnv. */
    private static final String PROPERTY_NEW_TYPE = "createNew"; // NOI18N
    /** Name of the custom property that can be passed in PropertyEnv. */
    private static final String PROPERTY_SUPERCLASS = "superClass"; // NOI18N
    
    /** tagx */
    private String[] tags;

    /** class to work on */
    private Class<? extends ServiceType> clazz;

    /** message key to be used in custom editor */
    private String message;
    
    /** Environment passed to the ExPropertyEditor*/
    private PropertyEnv env;
    
    /**
     * This variable can be read in attachEnv. Defaults to false,
     * false - we are selecting from the registered service types, true - creating
     * new instances of the services
     */
    private boolean createNewInstance = false;

    /** constructs new property editor.
    */
    public ServiceTypeEditor() {
        this (ServiceType.class, "LAB_ChooseServiceType"); // NOI18N
    }

    /** constructs new property editor.
     * @param clazz the class to use 
     * @param message the message for custom editor
     */
    public ServiceTypeEditor(Class<?> clazz, String message) {
        this.clazz = clazz.asSubclass(ServiceType.class);
        this.message = message;
    }

    /**
     * This method is called by the IDE to pass
     * the environment to the property editor.
     * @param env Environment passed by the ide.
     */
    public void attachEnv(PropertyEnv env) {
        this.env = env;
        Object newObj = env.getFeatureDescriptor().getValue(PROPERTY_NEW_TYPE);
        if (newObj instanceof Boolean) {
            createNewInstance = ((Boolean)newObj).booleanValue();
        }
        Object sup = env.getFeatureDescriptor().getValue(PROPERTY_SUPERCLASS);
        if (sup instanceof Class) {
            @SuppressWarnings("unchecked") Class<? extends ServiceType> c = (Class<? extends ServiceType>)sup;
	    clazz = c;
        }
    }
    
    /** Updates the list of executors.
     */
    private void updateTags () {
        java.util.LinkedList<String> names = new java.util.LinkedList<String> ();
        ServiceType.Registry registry = Lookup.getDefault ()
                .lookup (ServiceType.Registry.class);
        Enumeration ee = registry.services (clazz);
        while (ee.hasMoreElements()) {
            ServiceType e = (ServiceType) ee.nextElement();
            names.add(e.getName());
        }
        names.toArray(tags = new String[names.size()]);
    }

    //----------------------------------------------------------------------

    /**
    * @return The property value as a human editable string.
    * <p>   Returns null if the value can't be expressed as an editable string.
    * <p>   If a non-null value is returned, then the PropertyEditor should
    *       be prepared to parse that string back in setAsText().
    */
    public String getAsText() {
        if (createNewInstance) {
            return null;
        }
        ServiceType s = (ServiceType)getValue ();
        if (s == null) {
            return getString ("LAB_DefaultServiceType");
        } else {
            return s.getName();
        }
    }

    /** Set the property value by parsing a given String.  May raise
    * java.lang.IllegalArgumentException if either the String is
    * badly formatted or if this kind of property can't be expressed
    * as text.
    * @param text  The string to be parsed.
    */
    public void setAsText(String text) {
        if (createNewInstance) {
            // new instance cannot be entered as a text
            throw new IllegalArgumentException();
        }

        ServiceType.Registry registry = (ServiceType.Registry)Lookup.getDefault ()
                .lookup (ServiceType.Registry.class);
        Enumeration en = registry.services (clazz);
        while (en.hasMoreElements ()) {
            ServiceType t = (ServiceType)en.nextElement ();
            if (text.equals (t.getName ())) {
                setValue (t);
                return;
            }
        }
        setValue (null);
    }

    /** @return tags */
    public String[] getTags() {
        if (!createNewInstance) {
            updateTags ();
            return tags;
        }
        return null;
    }

    public boolean supportsCustomEditor () {
        return true;
    }

    public java.awt.Component getCustomEditor () {
        final ServiceTypePanel s = new ServiceTypePanel (clazz, getString(message), /*none*/ null, createNewInstance);

        s.setServiceType ((ServiceType)getValue ());
        // [PENDING] why is this here? Cancel does not work correctly because of this, I think:
        s.addPropertyChangeListener (new PropertyChangeListener () {
                                         public void propertyChange (PropertyChangeEvent ev) {
                                             if ("serviceType".equals (ev.getPropertyName ())) {
                                                 setValue (s.getServiceType ());
                                             }
                                         }
                                     });
        return s;
    }

    private static String getString (String s) {
        return org.openide.util.NbBundle.getBundle (ServiceTypeEditor.class).getString (s);
    }
}
