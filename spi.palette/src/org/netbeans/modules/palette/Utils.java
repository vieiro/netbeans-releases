/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */


package org.netbeans.modules.palette;

import java.awt.event.ActionListener;
import java.beans.BeanInfo;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.netbeans.spi.palette.PaletteController;
import org.netbeans.modules.palette.ui.PalettePanel;
import org.netbeans.spi.palette.PaletteActions;

import org.openide.*;
import org.openide.loaders.DataObject;
import org.openide.nodes.*;
import org.openide.filesystems.*;
import org.openide.util.*;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.datatransfer.NewType;
import org.openide.util.datatransfer.ExClipboard;
import org.openide.windows.TopComponent;


/**
 * Class providing various useful methods for palette classes.
 *
 * @author S Aubrecht
 */
public final class Utils {

    private static final Logger ERR = Logger.getLogger( "org.netbeans.modules.palette" ); // NOI18N

    private Utils() {
    }

    // -----------

    public static ResourceBundle getBundle() {
        return NbBundle.getBundle(Utils.class);
    }

    public static String getBundleString(String key) {
        return getBundle().getString(key);
    }
    
    public static Action[] mergeActions( Action[] first, Action[] second ) {
        if( null == first )
            return second;
        if( null == second )
            return first;
        
        Action[] res = new Action[first.length+second.length+1];
        System.arraycopy( first, 0, res, 0, first.length );
        res[first.length] = null;
        System.arraycopy( second, 0, res, first.length+1, second.length );
        return res;
    }
    
    public static boolean isReadonly( Node node ) {
        return getBoolean(node, PaletteController.ATTR_IS_READONLY, !node.canDestroy());
    }

    public static boolean getBoolean( Node node, String attrName, boolean defaultValue ) {
        Object val = node.getValue( attrName );
        if( null == val ) {
            DataObject dobj = (DataObject)node.getCookie( DataObject.class );
            if( null != dobj ) {
                val = dobj.getPrimaryFile().getAttribute( attrName );
            }
        }
        if( null != val ) {
            return Boolean.valueOf( val.toString() ).booleanValue();
        } else {
            return defaultValue;
        }
    }
    
    public static HelpCtx getHelpCtx( Node node, HelpCtx defaultHelp ) {
        HelpCtx retValue = defaultHelp;
        if( null == retValue || HelpCtx.DEFAULT_HELP.equals( retValue ) ) {
            Object val = node.getValue( PaletteController.ATTR_HELP_ID );
            if( null == val ) {
                DataObject dobj = (DataObject)node.getCookie( DataObject.class );
                if( null != dobj ) {
                    val = dobj.getPrimaryFile().getAttribute( PaletteController.ATTR_HELP_ID );
                }
            }
        
            if( null != val )
                retValue = new HelpCtx( val.toString() );
        }
        return retValue;
    }
    
    public static void addCustomizationMenuItems( JPopupMenu popup, PaletteController controller, Settings settings ) {
        popup.addSeparator();
        popup.add( new ShowNamesAction( settings ) );
        popup.add( new ChangeIconSizeAction( settings ) );
        addResetMenuItem( popup, controller, settings );
        popup.addSeparator();
        popup.add( new ShowCustomizerAction( controller ) );
    }
    
    static void addResetMenuItem( JPopupMenu popup, final PaletteController controller, final Settings settings ) {
        JMenuItem item = new JMenuItem( getBundleString( "CTL_ResetPalettePopup" ) );
        item.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetPalette( controller, settings );
            }
        });
        popup.add( item );
    }
    
    /**
     * Find a Node representing the given category.
     *
     * @param root Palette's root node.
     * @param categoryName Name of the category to search for.
     * @return Category with the given name or null.
     */
    public static Node findCategoryNode( Node root, String categoryName ) {
        return root.getChildren().findChild( categoryName );
    }
    
    public static void resetPalette( final PaletteController controller, final Settings settings ) {
        Node rootNode = (Node)controller.getRoot().lookup( Node.class );
        if( null != rootNode ) {
            PaletteActions customActions = rootNode.getLookup().lookup( PaletteActions.class );
            Action resetAction = customActions.getResetAction();
            if( null != resetAction ) {
                settings.reset();
                resetAction.actionPerformed( new ActionEvent( controller, 0, "reset" ) ); //NOI18N
                controller.refresh();
            } else {
                resetPalette( rootNode, controller, settings );
            }
        }
    }
    
    public static void resetPalette( Node rootNode, PaletteController controller, Settings settings ) {
        // first user confirmation...
        NotifyDescriptor desc = new NotifyDescriptor.Confirmation(
            getBundleString("MSG_ConfirmPaletteReset"), // NOI18N
            getBundleString("CTL_ConfirmResetTitle"), // NOI18N
            NotifyDescriptor.YES_NO_OPTION);

        if( NotifyDescriptor.YES_OPTION.equals(
                    DialogDisplayer.getDefault().notify(desc)) ) {
            
            settings.reset();
            DataObject dob = (DataObject)rootNode.getLookup().lookup( DataObject.class );
            if( null != dob ) {
                FileObject primaryFile = dob.getPrimaryFile();
                if( null != primaryFile && primaryFile.isFolder() ) {
                    try {
                        primaryFile.revert();
                        for( FileObject fo : primaryFile.getChildren() ) {
                            fo.setAttribute( "categoryName", null );
                            fo.setAttribute( "position", null );
                        }
                    } catch (IOException ex) {
                        ERR.log(Level.INFO, null, ex);
                    }
                }
            }
            controller.refresh();
        }
    }
    
    public static void setOpenedByUser( TopComponent tc, boolean userOpened ) {
        tc.putClientProperty( "userOpened", Boolean.valueOf(userOpened) ); //NOI18N
    }
    
    public static boolean isOpenedByUser( TopComponent tc ) {
        Object val = tc.getClientProperty( "userOpened" );
        tc.putClientProperty("userOpened", null);
        return null != val && val instanceof Boolean && ((Boolean)val).booleanValue();
    }

    /**
     * An action to create a new palette category.
     */
    public static class NewCategoryAction extends AbstractAction {
        private Node paletteNode;
        
        /**
         * @param paletteRootNode Palette's root node.
         */
        public NewCategoryAction( Node paletteRootNode ) {
            putValue(Action.NAME, getBundleString("CTL_CreateCategory")); // NOI18N
            this.paletteNode = paletteRootNode;
        }

        public void actionPerformed(ActionEvent event) {
            NewType[] newTypes = paletteNode.getNewTypes();
            try {
                if( null != newTypes && newTypes.length > 0 ) {
                    newTypes[0].create();
                }
            } catch( IOException ioE ) {
                ERR.log( Level.INFO, ioE.getLocalizedMessage(), ioE );
            }
        }

        @Override
        public boolean isEnabled() {
            NewType[] newTypes = paletteNode.getNewTypes();
            return null != newTypes && newTypes.length > 0;
        }
    }
    
    /**
     * An action to sort categories alphabetically.
     */
    static class SortCategoriesAction extends AbstractAction {
        private Node paletteNode;
        public SortCategoriesAction( Node paletteNode ) {
            putValue(Action.NAME, getBundleString("CTL_SortCategories")); // NOI18N
            this.paletteNode = paletteNode;
        }
        
        public void actionPerformed(ActionEvent event) {
            Index order = (Index)paletteNode.getCookie(Index.class);
            if (order != null) {
                final Node[] nodes = order.getNodes();
                Arrays.sort( nodes, new Comparator<Node>() {
                    public int compare(Node n1, Node n2) {
                        return n1.getDisplayName().compareTo( n2.getDisplayName() );
                    }
                } );
                int[] perm = new int[nodes.length];
                for( int i=0; i<perm.length; i++ ) {
                    perm[order.indexOf( nodes[i] )] = i;
                }
                order.reorder( perm );
            }
        }
        
        public boolean isEnabled() {
            return (paletteNode.getCookie(Index.class) != null);
        }
    }
    
    /**
     * An action to show/hide palette item names.
     */
    private static class ShowNamesAction extends AbstractAction {
        
        private Settings settings;
        
        public ShowNamesAction( Settings settings ) {
            this.settings = settings;
        }
        
        public void actionPerformed(ActionEvent event) {
            settings.setShowItemNames( !settings.getShowItemNames() );
        }
        
        public Object getValue(String key) {
            if (Action.NAME.equals(key)) {
                boolean showNames = settings.getShowItemNames();
                return getBundleString(showNames ? "CTL_HideNames" : "CTL_ShowNames"); // NOI18N
            } else {
                return super.getValue(key);
            }
        }
    }
    
    /**
     * An action to change the size of palette icons.
     */
    private static class ChangeIconSizeAction extends AbstractAction {
        
        private Settings settings;
        
        public ChangeIconSizeAction( Settings settings ) {
            this.settings = settings;
        }
        
        public void actionPerformed(ActionEvent event) {
            int oldSize = settings.getIconSize();
            int newSize = (oldSize == BeanInfo.ICON_COLOR_16x16) ?
                BeanInfo.ICON_COLOR_32x32 : BeanInfo.ICON_COLOR_16x16;
            settings.setIconSize( newSize );
        }
        
        public Object getValue(String key) {
            if (Action.NAME.equals(key)) {
                String namePattern = getBundleString("CTL_IconSize"); // NOI18N
                return MessageFormat.format(namePattern,
                new Object[] {Integer.valueOf(settings.getIconSize())});
            } else {
                return super.getValue(key);
            }
        }
    }
    
    /**
     * An action to restore palette's default state.
     */
    static class RefreshPaletteAction extends AbstractAction {
        
        public RefreshPaletteAction() {
            putValue(Action.NAME, getBundleString("CTL_RefreshPalette")); // NOI18N
        }
        
        public void actionPerformed(ActionEvent event) {
            PalettePanel.getDefault().doRefresh();
        }
        
    }
    
    /**
     * An action to remove a category and all items in it.
     */
    static class DeleteCategoryAction extends AbstractAction {
        private Node categoryNode;
        
        public DeleteCategoryAction(Node categoryNode) {
            this.categoryNode = categoryNode;
            putValue(Action.NAME, getBundleString("CTL_DeleteCategory")); // NOI18N
        }
        
        public void actionPerformed(ActionEvent event) {
            // first user confirmation...
            String message = MessageFormat.format(
                getBundleString("FMT_ConfirmCategoryDelete"), // NOI18N
                new Object [] { categoryNode.getName() });

            NotifyDescriptor desc = new NotifyDescriptor.Confirmation(message,
                getBundleString("CTL_ConfirmCategoryTitle"), // NOI18N
                NotifyDescriptor.YES_NO_OPTION);

            if (NotifyDescriptor.YES_OPTION.equals(DialogDisplayer.getDefault().notify(desc))) {
                try {
                    categoryNode.destroy();
                } catch (java.io.IOException e) {
                    ERR.log( Level.INFO, e.getLocalizedMessage(), e );
                }
            }
        }
        
        public boolean isEnabled() {
            return categoryNode.canDestroy();
        }
    }
    
    /**
     * An action to rename a category.
     */
    static class RenameCategoryAction extends AbstractAction {
        private Node categoryNode;
        
        public RenameCategoryAction(Node categoryNode) {
            this.categoryNode = categoryNode;
            putValue(Action.NAME, getBundleString("CTL_RenameCategory")); // NOI18N
        }
        
        public void actionPerformed(ActionEvent event) {
            NotifyDescriptor.InputLine desc = new NotifyDescriptor.InputLine(
                getBundleString("CTL_NewName"), // NOI18N
                getBundleString("CTL_Rename")); // NOI18N
            desc.setInputText(categoryNode.getDisplayName());

            if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(desc))) {
                String newName;
                try {
                    newName = desc.getInputText();
                    if (!"".equals(newName)) // NOI18N
                    categoryNode.setDisplayName(newName);
                } catch (IllegalArgumentException e) {
                    ERR.log( Level.INFO, e.getLocalizedMessage(), e );
                }
            }
        }
        
        public boolean isEnabled() {
            return categoryNode.canRename();
        }
    }

    /**
     * An action to sort categories alphabetically.
     */
    static class SortItemsAction extends AbstractAction {
        private Node categoryNode;
        public SortItemsAction( Node categoryNode ) {
            putValue(Action.NAME, getBundleString("CTL_SortItems")); // NOI18N
            this.categoryNode = categoryNode;
        }
        
        public void actionPerformed(ActionEvent event) {
            Index order = (Index)categoryNode.getCookie(Index.class);
            if (order != null) {
                final Node[] nodes = order.getNodes();
                Arrays.sort( nodes, new Comparator<Node>() {
                    public int compare(Node n1, Node n2) {
                        return n1.getDisplayName().compareTo( n2.getDisplayName() );
                    }
                } );
                int[] perm = new int[nodes.length];
                for( int i=0; i<perm.length; i++ ) {
                    perm[order.indexOf( nodes[i] )] = i;
                }
                order.reorder( perm );
            }
        }
        
        public boolean isEnabled() {
            return (categoryNode.getCookie(Index.class) != null);
        }
    }
    
    /**
     * An action to create a new palette item from clipboard contents.
     */
    public static class PasteItemAction extends AbstractAction {
        private Node categoryNode;
        
        public PasteItemAction(Node categoryNode) {
            this.categoryNode = categoryNode;
            putValue(Action.NAME, getBundleString("CTL_Paste")); // NOI18N
        }
        
        public void actionPerformed(ActionEvent event) {
            PasteType type = getPasteType();
            if (type != null) {
                try {
                    Transferable trans = type.paste();
                    if (trans != null) {
                        ClipboardOwner owner = trans instanceof ClipboardOwner ?
                            (ClipboardOwner)trans : new StringSelection(""); // NOI18N
                        Clipboard clipboard = (Clipboard)Lookup.getDefault().lookup(ExClipboard.class);
                        clipboard.setContents(trans, owner);
                    }
                } catch (java.io.IOException e) {
                    ERR.log( Level.INFO, e.getLocalizedMessage(), e );
                }
            }
        }
        
        public boolean isEnabled() {
            return (getPasteType() != null);
        }

        private PasteType getPasteType() {
            Clipboard clipboard = (Clipboard) Lookup.getDefault().lookup(ExClipboard.class);
            Transferable trans = clipboard.getContents(this);
            if (trans != null) {
                PasteType[] pasteTypes = categoryNode.getPasteTypes(trans);
                if (pasteTypes != null && pasteTypes.length != 0)
                    return pasteTypes[0];
            }
            return null;
        }

    }
    
    /**
     * An action to cut a palette item to clipboard.
     */
    public static class CutItemAction extends AbstractAction {
        private Node itemNode;
        
        public CutItemAction(Node itemNode) {
            this.itemNode = itemNode;
            putValue(Action.NAME, getBundleString("CTL_Cut")); // NOI18N
        }
        
        public void actionPerformed(ActionEvent event) {
            try {
                Transferable trans = itemNode.clipboardCut();
                if (trans != null) {
                    Clipboard clipboard = (Clipboard)
                        Lookup.getDefault().lookup(ExClipboard.class);
                    clipboard.setContents(trans, new StringSelection("")); // NOI18N
                }
            } catch (java.io.IOException e) {
                ERR.log( Level.INFO, e.getLocalizedMessage(), e );
            }
        }

        public boolean isEnabled() {
            return itemNode.canCut();
        }
    }
    
    /**
     * An action to copy palette item to clipboard.
     */
    public static class CopyItemAction extends AbstractAction {
        private Node itemNode;
        
        public CopyItemAction(Node itemNode) {
            this.itemNode = itemNode;
            putValue(Action.NAME, getBundleString("CTL_Copy")); // NOI18N
        }
        
        public void actionPerformed(ActionEvent event) {
            try {
                Transferable trans = itemNode.clipboardCopy();
                if (trans != null) {
                    Clipboard clipboard = (Clipboard)
                        Lookup.getDefault().lookup(ExClipboard.class);
                    clipboard.setContents(trans, new StringSelection("")); // NOI18N
                }
            } catch (java.io.IOException e) {
                ERR.log( Level.INFO, e.getLocalizedMessage(), e );
            }
        }

        public boolean isEnabled() {
            return itemNode.canCopy();
        }
    }
    
    /**
     * An action to remove an item from palette.
     */
    static class RemoveItemAction extends AbstractAction {
        private Node itemNode;
        
        public RemoveItemAction(Node itemNode) {
            this.itemNode = itemNode;
            putValue(Action.NAME, getBundleString("CTL_Delete")); // NOI18N
        }
        
        public void actionPerformed(ActionEvent event) {
            // first user confirmation...
            String message = MessageFormat.format(
                getBundleString("FMT_ConfirmBeanDelete"), // NOI18N
                new Object[] { itemNode.getDisplayName() });

            NotifyDescriptor desc = new NotifyDescriptor.Confirmation(message,
                getBundleString("CTL_ConfirmBeanTitle"), // NOI18N
                NotifyDescriptor.YES_NO_OPTION);

            if (NotifyDescriptor.YES_OPTION.equals(DialogDisplayer.getDefault().notify(desc))) {
                try {
                    itemNode.destroy();
                } catch (java.io.IOException e) {
                    ERR.log( Level.INFO, e.getLocalizedMessage(), e );
                }
            }
        }
        
        public boolean isEnabled() {
            return itemNode.canDestroy();
        }
    }
    
    /**
     * An action to remove an item from palette.
     */
    private static class ShowCustomizerAction extends AbstractAction {
        private PaletteController palette;
        
        public ShowCustomizerAction( PaletteController palette ) {
            this.palette = palette;
            putValue(Action.NAME, getBundleString("CTL_ShowCustomizer")); // NOI18N
        }
        
        public void actionPerformed(ActionEvent event) {
            palette.showCustomizer();
        }
    }
}
