/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.editor.ext;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.EditorUI;
import org.netbeans.modules.editor.lib2.view.ViewHierarchy;
import org.netbeans.modules.editor.lib2.view.ViewHierarchyEvent;
import org.netbeans.modules.editor.lib2.view.ViewHierarchyListener;

/**
 *
 * @author ralph
 */
public class StickyWindowSupport {

    private final EditorUI eui;

    public StickyWindowSupport(final EditorUI eui) {
        this.eui = eui;
        ViewHierarchy.get(eui.getComponent()).addViewHierarchyListener(new ViewHierarchyListener() {
            @Override
            public void viewHierarchyChanged(ViewHierarchyEvent evt) {
                JTextComponent editor = eui.getComponent();
                Container container = editor.getParent();
                if(container instanceof JLayeredPane && evt.isChangeY()) {
                    JLayeredPane pane = (JLayeredPane) container;
                    double deltaY = evt.deltaY();
                    Component[] components = pane.getComponentsInLayer(JLayeredPane.PALETTE_LAYER);
                    Rectangle rv = null;
                    for (Component component : components) {
                        rv = component.getBounds(rv);
                        if(rv.getY() > evt.startY() ||
                                rv.contains(new Point(rv.x, (int) evt.startY())) ||
                                rv.contains(new Point(rv.x, (int) evt.endY()))) {
                            Point p = rv.getLocation();
                            p.translate(0, (int) deltaY);
                            component.setLocation(p);
                        }
                    }
                }
            }
        });
    }

    public void addWindow(JComponent frame, Point location) {
        JTextComponent component = eui.getComponent();
        Container container = component.getParent();
        if(container instanceof JLayeredPane) {
            JLayeredPane pane = (JLayeredPane) container;
            Point point = SwingUtilities.convertPoint(null, location, pane);
            point.translate(0, 34);
            ((JInternalFrame)frame).pack();
//            Dimension size = frame.getPreferredSize();
//            Dimension size = new Dimension(200, 100);
//            frame.setBounds(point.x, point.y, size.width, size.height);
            frame.setLocation(point);
            pane.add(frame, JLayeredPane.PALETTE_LAYER);
            frame.setVisible(true);
        }
    }

    public void removeWindow(JComponent frame) {
        JTextComponent component = eui.getComponent();
        Container container = component.getParent();
        if(container instanceof JLayeredPane) {
            JLayeredPane pane = (JLayeredPane) container;
            pane.remove(frame);
        }
    }
    
    
}
