/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.maven.model.settings.visitor;

import org.netbeans.modules.maven.model.settings.SettingsComponent;
import org.netbeans.modules.xml.xam.AbstractComponent;
import org.netbeans.modules.xml.xam.Component;
import org.netbeans.modules.xml.xam.ComponentUpdater;
import org.netbeans.modules.xml.xam.dom.DocumentComponent;

/**
 * Visitor to add or remove a child of a domain component.
 * 
 * @author mkleint
 */
public class ChildComponentUpdateVisitor<T extends SettingsComponent>
        implements ComponentUpdater<T> {
    
    private SettingsComponent parent;
    private int index;
    private boolean canAdd = false;
    
    /**
     * Creates a new instance of ChildComponentUpdateVisitor
     */
    public ChildComponentUpdateVisitor() {
    }
    
    public boolean canAdd(SettingsComponent target, Component child) {
        if (!(child instanceof SettingsComponent)) return false;
        update(target, (SettingsComponent) child, null);
        return canAdd;
    }
    
    public void update(SettingsComponent target, SettingsComponent child, Operation operation) {
        update(target, child, -1, operation);
    }
    
    public void update(SettingsComponent target, SettingsComponent child, int index, Operation operation) {
        assert target != null;
        assert child != null;

        this.parent = target;
        this.index = index;
        //#165465
        if (operation != null) {
            if (operation == Operation.REMOVE) {
                //TODO what property shall be fired? is it important?
                removeChild("XXX", child);
                return;
            } else {
                //TODO what property shall be fired? is it important?
                addChild("XXX", child);
            }
        }
    }

    private void addChild(String eventName, DocumentComponent child) {
        ((AbstractComponent) parent).insertAtIndex(eventName, child, index);
    }

    private void removeChild(String eventName, DocumentComponent child) {
        ((AbstractComponent) parent).removeChild(eventName, child);
    }
}
