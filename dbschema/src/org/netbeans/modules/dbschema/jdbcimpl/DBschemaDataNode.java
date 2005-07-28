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

package org.netbeans.modules.dbschema.jdbcimpl;

import org.openide.loaders.DataNode;
import org.openide.nodes.Children;

import org.netbeans.modules.dbschema.nodes.*;

public class DBschemaDataNode extends DataNode {

    public DBschemaDataNode (DBschemaDataObject obj) {
        this (obj, obj.isTemplate() ? Children.LEAF : new SchemaRootChildren(new DefaultDBFactory(false), obj));
    }

    public DBschemaDataNode (DBschemaDataObject obj, Children ch) {
        super (obj, ch);
        setIconBase ("org/netbeans/modules/dbschema/jdbcimpl/DBschemaDataIcon"); //NOI18N
    }

}
