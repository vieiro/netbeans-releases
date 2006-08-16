/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.visual.border;

import org.netbeans.api.visual.border.Border;

import java.awt.*;

/**
 * @author David Kaspar
 */
public final class EmptyBorder implements Border {

    private Insets insets;

    public EmptyBorder (int top, int left, int bottom, int right) {
        insets = new Insets (top, left, bottom, right);
    }

    public Insets getInsets () {
        return insets;
    }

    public void paint (Graphics2D gr, Rectangle bounds) {
    }

    public boolean isOpaque () {
        return false;
    }

}
