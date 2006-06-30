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

package org.netbeans.lib.editor.bookmarks.spi;

import javax.swing.text.Document;

/**
 * Implementation of the bookmark to which the bookmark
 * delegates.
 *
 * @author Miloslav Metelka
 */

public interface BookmarkImplementation {

    /**
     * Get the offset at which the bookmark resides.
     * <br>
     * Offsets are required to behave like {@link javax.swing.text.Position}s
     * (they track inserts/removals).
     */
    int getOffset();

    /**
     * Called when a bookmark has been released from its bookmark list
     * and it's no longer active.
     */
    void release();

}

