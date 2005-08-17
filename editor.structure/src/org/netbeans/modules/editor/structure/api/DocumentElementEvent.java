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

package org.netbeans.modules.editor.structure.api;

import java.util.EventObject;


/**
 * This is an implementation of EventObject class holding an information 
 * about a change in an DocumentElement. It is fired by DocumentElement-s and 
 * received via DocumentElementListener-s. 
 * <br/>
 * The event object holds an information about the type of the change which can be one
 * of the following:
 * <ul>
 * <li>A child has been added into the element
 * <li>A child has been removed into the element
 * <li>Children of the element have been reordered
 * <li>Text content of the element has changed
 * <li>Attributes of the element has changed
 * </ul>
 *
 * @author  Marek Fukala
 * @version 1.0
 */
public final class DocumentElementEvent extends EventObject {

    //it can be either an added or a deleted component or null
    private DocumentElement changedChild;
    private int type;
    
    /** Event type indicating that the element's text content has been changed. */
    public static final int CONTENT_CHANGED = 1;
    
    /** Event type indicating that a child has been added into the element. */
    public static final int CHILD_ADDED = 2;
    
    /** Event type indicating that a child has been removed from the element. */
    public static final int CHILD_REMOVED = 3;
    
    /** Event type indicating that children of the element have been reordered. */
    public static final int CHILDREN_REORDERED = 4;
    
    /** Event type indicating that attributes of the element have changed.*/
    public static final int ATTRIBUTES_CHANGED = 5;
    
    DocumentElementEvent(int type, DocumentElement source, DocumentElement changedChild) {
        super(source);
        this.type = type;
        this.changedChild = changedChild;
    }
    
    /** Returns the source element which fired this event. */
    public DocumentElement getSourceDocumentElement() {
        return (DocumentElement)getSource();
    }
    
    /** Returns the added or removed child when the event is one of the CHILD_ADDED or CHILD_REMOVED type.*/
    public DocumentElement getChangedChild() {
        return this.changedChild;
    }

    /** Returns the type of the event. */
    public int getType() {
        return type;
    }
}

