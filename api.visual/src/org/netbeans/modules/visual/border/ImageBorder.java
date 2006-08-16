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
public final class ImageBorder implements Border {

    private Insets borderInsets;
    private Insets imageInsets;
    private Image image;
    private int width, height;
    private int verStep, horStep;
    private int verEdge, horEdge;

    public ImageBorder (Insets borderInsets, Insets imageInsets, Image image) {
        this.borderInsets = borderInsets;
        this.imageInsets = imageInsets;
        this.image = image;
        width = image.getWidth (null);
        height = image.getHeight (null);
        horEdge = width - this.imageInsets.right;
        verEdge = height - this.imageInsets.bottom;
        horStep = horEdge - this.imageInsets.left;
        verStep = verEdge - this.imageInsets.top;
    }

    public Insets getInsets () {
        return borderInsets;
    }

    public void paint (Graphics2D gr, Rectangle bounds) {
        int destVerMax = bounds.y + bounds.height;
        int destHorMax = bounds.x + bounds.width;
        int destVerEdge = destVerMax - imageInsets.bottom;
        int destHorEdge = destHorMax - imageInsets.right;

        int horInner = bounds.width - imageInsets.left - imageInsets.right;
        int xdiv = horInner / horStep;
        int xmod = horInner % horStep;

        gr.drawImage (image, bounds.x, bounds.y, bounds.x + xmod + imageInsets.left, bounds.y + imageInsets.top, 0, 0, xmod + imageInsets.left, imageInsets.top, null);
        gr.drawImage (image, destHorEdge - xmod, destVerEdge, destHorMax, destVerMax, horEdge - xmod, verEdge, width, height, null);

        for (int i = 0, x = bounds.x + xmod + imageInsets.left; i < xdiv; i ++, x += horStep) {
            gr.drawImage (image, x, bounds.y, x + horStep, bounds.y + imageInsets.top, imageInsets.left, 0, horEdge, imageInsets.top, null);
            gr.drawImage (image, x - xmod, destVerEdge, x - xmod + horStep, destVerMax, imageInsets.left, verEdge, horEdge, height, null);
        }

        int verInner = bounds.height - imageInsets.top - imageInsets.bottom;
        int ydiv = verInner / verStep;
        int ymod = verInner % verStep;

        gr.drawImage (image, destHorEdge, bounds.y, destHorMax, bounds.y + ymod + imageInsets.top, horEdge, 0, width, ymod + imageInsets.top, null);
        gr.drawImage (image, bounds.x, destVerEdge - ymod, bounds.x + imageInsets.left, destVerMax, 0, verEdge - ymod, imageInsets.left, height, null);

        for (int i = 0, y = bounds.y + ymod + imageInsets.top; i < ydiv; i ++, y += verStep) {
            gr.drawImage (image, destHorEdge, y, destHorMax, y + verStep, horEdge, imageInsets.top, width, verEdge, null);
            gr.drawImage (image, bounds.x, y - ymod, bounds.x + imageInsets.left, y - ymod + verStep, 0, imageInsets.top, imageInsets.left, verEdge, null);
        }
    }

    public boolean isOpaque () {
        return false;
    }

}
