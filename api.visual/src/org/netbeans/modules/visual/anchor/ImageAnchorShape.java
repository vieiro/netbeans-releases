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
package org.netbeans.modules.visual.anchor;

import org.netbeans.api.visual.anchor.AnchorShape;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class ImageAnchorShape implements AnchorShape {

    private Image image;
    private int radius;
    private int x, y;

    public ImageAnchorShape (Image image) {
        assert image != null;
        this.image = image;
        x = image.getWidth (null);
        y = image.getHeight (null);
        radius = Math.max (x, y);
        x = - (x / 2);
        y = - (y / 2);
    }

    public boolean isLineOriented () {
        return false;
    }

    public int getRadius () {
        return radius;
    }

    public void paint (Graphics2D graphics, boolean source) {
        graphics.drawImage (image, x, y, null);
    }

}
