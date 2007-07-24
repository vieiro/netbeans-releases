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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.properties;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.Children;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.NotifyDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Item in a set of properties files represented by a single
 * <code>PropertiesDataObject</code>.
 *
 * @see  PropertiesDataLoader#createPrimaryEntry
 * @see  PropertiesDataLoader#createSecondaryEntry
 */
public class PropertiesFileEntry extends PresentableFileEntry
                                 implements CookieSet.Factory {

    /** Basic name of bundle .properties file. */
    private String basicName;
    
    /** Structure handler for .properties file represented by this instance. */
    private transient StructHandler propStruct;
    
    /** Editor support for this entry. */
    private transient PropertiesEditorSupport editorSupport;

    /** Generated serial version UID. */    
    static final long serialVersionUID = -3882240297814143015L;
    
    
    /**
     * Creates a new <code>PropertiesFileEntry</code>.
     *
     * @param  obj  data object this entry belongs to
     * @param  file  file object for this entry
     */
    PropertiesFileEntry(MultiDataObject obj, FileObject file) {
        super(obj, file);
        FileObject fo = getDataObject().getPrimaryFile();
        if (fo == null)
            // primary file not init'ed yet => I'm the primary entry
            basicName = getFile().getName();
        else
            basicName = fo.getName();
        
        getCookieSet().add(PropertiesEditorSupport.class, this);
    }

    
    /** Copies entry to folder. Overrides superclass method. 
     * @param folder folder where copy
     * @param suffix suffix to use
     * @exception IOException when error happens */
    @Override
    public FileObject copy(FileObject folder, String suffix) throws IOException {
        String pasteSuffix = ((PropertiesDataObject)getDataObject()).getPasteSuffix();
        
        if(pasteSuffix == null)
            return super.copy(folder, suffix);
        
        FileObject fileObject = getFile();
        
        String basicName = getDataObject().getPrimaryFile().getName();
        String newName = basicName + pasteSuffix + Util.getLocaleSuffix(this);
        
        return fileObject.copy(folder, newName, fileObject.getExt());
    }
    
    /** Deletes file. Overrides superclass method. */
    @Override
    public void delete() throws IOException {
        getHandler().stopParsing();

        try {
            super.delete();
        } finally {
            // Sets back parsing flag.
            getHandler().allowParsing();
        }
    }
   
    /** Moves entry to folder. Overrides superclass method. 
     * @param folder folder where copy
     * @param suffix suffix to use 
     * @exception IOException when error happens */
    @Override
    public FileObject move(FileObject folder, String suffix) throws IOException {
        String pasteSuffix = ((PropertiesDataObject)getDataObject()).getPasteSuffix();

        if(pasteSuffix == null)
            return super.move(folder, suffix);

        FileObject fileObject = getFile();
        FileLock lock = takeLock ();

        try {
            String basicName = getDataObject().getPrimaryFile().getName();
            String newName = basicName + pasteSuffix + Util.getLocaleSuffix(this);

            return fileObject.move (lock, folder, newName, fileObject.getExt());
        } finally {
            lock.releaseLock ();
        }
    }
    
    /** Implements <code>CookieSet.Factory</code> interface method. */
    @SuppressWarnings("unchecked")
    public <T extends Node.Cookie> T createCookie(Class<T> clazz) {
        if (clazz.isAssignableFrom(PropertiesEditorSupport.class)) {
            return (T) getPropertiesEditor();
        } else {
            return null;
        }
    }
    
    /** Creates a node delegate for this entry. Implements superclass abstract method. */
    protected Node createNodeDelegate() {
        return new PropertiesLocaleNode(this);
    }

    /** Gets children for this file entry. */
    public Children getChildren() {
        return new PropKeysChildren();
    }

    /** Gets struct handler for this entry. 
     * @return <StructHanlder</code> for this entry */
    public StructHandler getHandler() {
        if (propStruct == null) {
            propStruct = new StructHandler(this);
        }
        return propStruct;
    }

    /** Deserialization. */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    /** Gets editor support for this entry.
     * @return <code>PropertiesEditorSupport</code> instance for this entry */
    protected PropertiesEditorSupport getPropertiesEditor() {
        // Hack to ensure open support is created.
        // PENDING has to be made finer.
        getDataObject().getCookie(PropertiesOpen.class);
        
        if(editorSupport == null) {
            synchronized(this) {
                if(editorSupport == null)
                    editorSupport = new PropertiesEditorSupport(this);
            }
        }
            
        return editorSupport;
    }

    /** Renames underlying fileobject. This implementation returns the same file.
     * Overrides superclass method.
     *
     * @param name new base name of the bundle
     * @return file object with renamed file
     */
    @Override
    public FileObject rename (String name) throws IOException {
    
        if (!getFile().getName().startsWith(basicName))
            throw new IllegalStateException("Resource Bundles: error in Properties loader/rename."); // NOI18N

        FileObject fo = super.rename(name + getFile().getName().substring(basicName.length()));
        basicName = name;
        return fo;
    }

    /** Renames underlying fileobject. This implementation returns the same file.
     * Overrides superclass method.
     * 
     * @param name full name of the file represented by this entry
     * @return file object with renamed file
     */
    @Override
    public FileObject renameEntry (String name) throws IOException {

        if (!getFile().getName().startsWith(basicName))
            throw new IllegalStateException("Resource Bundles: error in Properties loader / rename"); // NOI18N

        if (basicName.equals(getFile().getName())) {
            // primary entry - can not rename
            NotifyDescriptor.Message msg = new NotifyDescriptor.Message(
                NbBundle.getBundle(PropertiesDataLoader.class).getString("MSG_AttemptToRenamePrimaryFile"),
                NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(msg);
            return getFile();
        }

        FileObject fo = super.rename(name);

        // to notify the bundle structure that name of one file was changed
        ((PropertiesDataObject)getDataObject()).getBundleStructure().notifyOneFileChanged(getHandler());
        
        return fo;
    }

    @Override
    public FileObject createFromTemplate (FileObject folder, String name) throws IOException {
        if (!getFile().getName().startsWith(basicName))
            throw new IllegalStateException("Resource Bundles: error in Properties createFromTemplate"); // NOI18N
        
        String suffix = getFile ().getName ().substring (basicName.length ());
        String nuename = name + suffix;
        String ext = getFile ().getExt ();
        FileObject existing = folder.getFileObject (nuename, ext);
        if (existing == null) {
            return super.createFromTemplate (folder, nuename);
        } else {
            // Append new content. Used to ask you whether the leave the old
            // file alone, or overwrite it with the new file, or append the new
            // content; but it can just cause deadlocks (#38599) to try to prompt
            // the user for anything from inside a Datasystems method, so don't
            // bother. Appending is the safest option; redundant stuff can always
            // be cleaned up manually.
            { // avoiding reindenting code
                byte[] originalData;
                byte[] buf = new byte[4096];
                int count;
                FileLock lock = existing.lock ();
                try {
                    InputStream is = existing.getInputStream ();
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream ((int) existing.getSize ());
                        try {
                            while ((count = is.read (buf)) != -1) {
                                baos.write (buf, 0, count);
                            }
                        } finally {
                            originalData = baos.toByteArray ();
                            baos.close ();
                        }
                    } finally {
                        is.close ();
                    }
                    existing.delete (lock);
                } finally {
                    lock.releaseLock ();
                }
                FileObject nue = folder.createData (nuename, ext);
                lock = nue.lock ();
                try {
                    OutputStream os = nue.getOutputStream (lock);
                    try {
                        os.write (originalData);
                        InputStream is = getFile ().getInputStream ();
                        try {
                            while ((count = is.read (buf)) != -1) {
                                os.write (buf, 0, count);
                            }
                        } finally {
                            is.close ();
                        }
                    } finally {
                        os.close ();
                    }
                } finally {
                    lock.releaseLock ();
                }
                // Does not appear to have any effect:
                // ((PropertiesDataObject) getDataObject ()).getBundleStructure ().
                //   notifyOneFileChanged (getHandler ());
                return nue;
            }
        }
    }

    /** Whether the object may be deleted. Implemenst superclass abstract method.
     * @return <code>true</code> if it may (primary file can't be deleted)
     */
    public boolean isDeleteAllowed() {
        // PENDING - better implementation : don't allow deleting Bunlde_en when Bundle_en_US exists
        return (getFile ().canWrite ()) && (!basicName.equals(getFile().getName()));
    }

    /** Whether the object may be copied. Implements superclass abstract method.
     * @return <code>true</code> if it may
     */
    public boolean isCopyAllowed() {
        return true;
    }
    // [PENDING] copy should be overridden because e.g. copy and then paste
    // to the same folder creates a new locale named "1"! (I.e. "foo_1.properties")

    /** Indicates whether the object can be moved. Implements superclass abstract method.
     * @return <code>true</code> if the object can be moved */
    public boolean isMoveAllowed() {
        return (getFile().canWrite()) && (getDataObject().getPrimaryEntry() != this);
    }

    /** Getter for rename action. Implements superclass abstract method.
     * @return true if the object can be renamed
     */
    public boolean isRenameAllowed () {
        return getFile ().canWrite ();
    }

    /** Help context for this object. Implements superclass abstract method.
     * @return help context
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(Util.HELP_ID_CREATING);
    }

    
    /** Children of a node representing single properties file.
     * Contains nodes representing individual properties (key-value pairs with comments). */
    private class PropKeysChildren extends Children.Keys<String> {

        /** Listens to changes on the property bundle structure. */
        private PropertyBundleListener bundleListener = null;

        
        /** Constructor. */
        PropKeysChildren() {
            super();
        }

        
        /** Sets all keys in the correct order. Calls <code>setKeys</code>. Helper method. 
         * @see org.openide.nodes.Children.Keys#setKeys(java.util.Collection) */
        private void mySetKeys() {
            // Use TreeSet because its iterator iterates in ascending order.
            Set<String> keys = new TreeSet<String>(new KeyComparator());
            PropertiesStructure propStructure = getHandler().getStructure();
            if (propStructure != null) {
                for (Iterator<Element.ItemElem> iterator = propStructure.allItems(); iterator.hasNext(); ) {
                    Element.ItemElem item = iterator.next();
                    if (item != null && item.getKey() != null) {
                        keys.add(item.getKey());
                    }
                }
            }
            
            setKeys(keys);
        }

        /** Called to notify that the children has been asked for children
         * after and that they should set its keys. Overrides superclass method.
         */
        @Override
        protected void addNotify () {
            mySetKeys();

            bundleListener = new PropertyBundleListener () {
                public void bundleChanged(PropertyBundleEvent evt) {
                    int changeType = evt.getChangeType();
                    
                    if(changeType == PropertyBundleEvent.CHANGE_STRUCT 
                        || changeType == PropertyBundleEvent.CHANGE_ALL) {
                        mySetKeys();
                    } else if(changeType == PropertyBundleEvent.CHANGE_FILE 
                        && evt.getEntryName().equals(getFile().getName())) {
                            
                        // File underlying this entry changed.
                        mySetKeys();
                    }
                }
            }; // End of annonymous class.

            bundleStructure().addPropertyBundleListener(bundleListener);
        }

        /** Called to notify that the children has lost all of its references to
         * its nodes associated to keys and that the keys could be cleared without
         * affecting any nodes (because nobody listens to that nodes). Overrides superclass method.
         */
        @Override
        protected void removeNotify () {
            bundleStructure().removePropertyBundleListener(bundleListener);
            setKeys(new ArrayList<String>());
        }

        /** Create nodes. Implements superclass abstract method. */
        protected Node[] createNodes (String itemKey) {
            return new Node[] { new KeyNode(getHandler().getStructure(), itemKey) };
        }

        /** Model accessor method. */
        private BundleStructure bundleStructure() {
            return ((PropertiesDataObject)PropertiesFileEntry.this.getDataObject()).getBundleStructure();
        }
    } // End of inner class PropKeysChildren.

}
