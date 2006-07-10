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
package org.netbeans.modules.collab.channel.filesharing.ui;

import org.openide.nodes.*;
import org.openide.util.*;
import org.openide.util.actions.*;

import java.awt.Image;

import java.beans.*;

import java.io.*;

import java.util.*;

import org.netbeans.modules.collab.channel.filesharing.FilesharingConstants;
import org.netbeans.modules.collab.channel.filesharing.FilesharingContext;
import org.netbeans.modules.collab.core.Debug;


/**
 *
 *
 * @author        Ayub Khan, ayub.khan@sun.com
 */
public class PackageNode extends AbstractNode implements FilesharingConstants {
    ////////////////////////////////////////////////////////////////////////////
    // Class variables
    ////////////////////////////////////////////////////////////////////////////
    public static final Image PACKAGE_BADGE = Utilities.loadImage(
            "org/netbeans/modules/collab/channel/filesharing/resources/package.gif", true
        ); // NOI18N	
    public static final Image EMPTY_PACKAGE_BADGE = Utilities.loadImage(
            "org/netbeans/modules/collab/channel/filesharing/resources/packageEmpty.gif", true
        ); // NOI18N	

    ////////////////////////////////////////////////////////////////////////////
    // Class variables
    ////////////////////////////////////////////////////////////////////////////
    public final String ICON_BASE = "org/netbeans/modules/collab/channel/filesharing/resources/package"; // NOI18N
    private final SystemAction[] DEFAULT_ACTIONS = new SystemAction[] {  };

    ////////////////////////////////////////////////////////////////////////////
    // Instance variables
    ////////////////////////////////////////////////////////////////////////////
    private boolean isLocal = false;
    private FilesharingContext context = null;

    /**
     *
     *
     */
    public PackageNode(String name, boolean isLocal, FilesharingContext context) {
        super(new PackageNodeChildren(isLocal, context));
        this.isLocal = isLocal;
        this.context = context;
        setName(name);
        setDisplayName(name.replaceAll(FILE_SEPERATOR, "."));
        setIconBase(ICON_BASE);
        systemActions = DEFAULT_ACTIONS;
    }

    public Image getIcon(int type) {
        Image icon = PACKAGE_BADGE;

        if (getChildren().getNodesCount() == 0) {
            icon = EMPTY_PACKAGE_BADGE;
        }

        return icon;
    }

    /**
     *
     *
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(PackageNode.class);
    }

    /**
     *
     *
     */
    public boolean canCut() {
        return false;
    }

    /**
     *
     *
     */
    public boolean canCopy() {
        return false;
    }

    /**
     *
     *
     */
    public boolean canDestroy() {
        return false;
    }

    /**
     *
     *
     */
    public boolean canRename() {
        return false;
    }

    /**
     *
     *
     */
    public void destroy() throws IOException {
        super.destroy();
    }

    /**
     *
     *
     */
    public PackageNodeChildren getPackageNodeChildren() {
        return (PackageNodeChildren) getChildren();
    }

    public static class PackageNodeChildren extends Children.Keys implements NodeListener, PropertyChangeListener {
        ////////////////////////////////////////////////////////////////////////////
        // Instance variables
        ////////////////////////////////////////////////////////////////////////////
        private Collection keys;
        private String displayName;
        private boolean isLocal;
        private FilesharingContext context = null;

        /**
         *
         *
         */
        public PackageNodeChildren(boolean isLocal, FilesharingContext context) {
            super();
            this.isLocal = isLocal;
            this.context = context;
            Debug.out.println("In PackageNodeChildren ");
        }

        /**
         *
         *
         */
        public boolean add(Node[] nodes) {
            Debug.out.println("PN add: " + nodes.length);

            for (int i = 0; i < nodes.length; i++) {
                if (super.findChild(nodes[i].getName()) == null) {
                    super.add(createNodes(nodes[i]));
                }
            }

            return true;
        }

        /**
         *
         *
         */
        protected void addNotify() {
            refreshChildren();
        }

        /**
         *
         *
         */
        protected void removeNotify() {
            _setKeys(Collections.EMPTY_SET);
        }

        /**
         *
         *
         */
        protected Node[] createNodes(Object key) {
            Debug.out.println("In PackageNodeChildren createNodes");

            Node[] result = null;

            try {
                result = new Node[] { new SharedProjectNode((Node) key, isLocal, context, false) };
            } catch (Exception e) {
                Debug.debugNotify(e);
            }

            return result;
        }

        /**
         *
         *
         */
        public Collection getKeys() {
            return keys;
        }

        /**
         *
         *
         */
        public void _setKeys(Collection value) {
            keys = value;
            super.setKeys(value);
        }

        /**
         *
         *
         */
        public void refreshChildren() {
            java.util.List keys = new ArrayList();

            try {
                _setKeys(keys);
            } catch (Exception e) {
                Debug.errorManager.notify(e);
            }
        }

        /**
         *
         *
         */
        public void propertyChange(PropertyChangeEvent event) {
        }

        /**
         *
         *
         */
        public void childrenAdded(NodeMemberEvent ev) {
            // Ignore
        }

        /**
         *
         *
         */
        public void childrenRemoved(NodeMemberEvent ev) {
            // Ignore
        }

        /**
         *
         *
         */
        public void childrenReordered(NodeReorderEvent ev) {
            // Ignore
        }

        /**
         *
         *
         */
        public void nodeDestroyed(NodeEvent ev) {
            refreshChildren();
        }
    }
}
