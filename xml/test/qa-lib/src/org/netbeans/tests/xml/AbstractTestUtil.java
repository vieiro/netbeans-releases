/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.tests.xml;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Random;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.xml.core.cookies.TreeDocumentCookie;
import org.netbeans.tax.TreeDocument;
import org.netbeans.tax.TreeException;
import org.netbeans.tax.TreeNode;
import org.netbeans.tax.io.XMLStringResult;
import org.openide.TopManager;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.execution.NbfsURLConnection;
import org.openide.filesystems.*;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.XMLDataObject;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Template;
import org.openide.util.NbBundle;

/**
 * Provides the basic support for XML API tests.
 * @author mschovanek
 */
public abstract class AbstractTestUtil {
    protected boolean DEBUG = false;
    
    public static final String CATALOG_PACKAGE    = "org.netbeans.modules.xml.catalog";
    public static final String CORE_PACKAGE       = "org.netbeans.modules.xml.core";
    public static final String TAX_PACKAGE        = "org.netbeans.modules.xml.tax";
    public static final String TEXT_PACKAGE       = "org.netbeans.modules.xml.text";
    public static final String TOOLS_PACKAGE      = "org.netbeans.modules.xml.tools";
    public static final String TREE_PACKAGE       = "org.netbeans.modules.xml.tree";
    
    //--------------------------------------------------------------------------
    //                         * * *  X M L * * *
    //--------------------------------------------------------------------------
    
    /**
     *  Converts the TreeNode to a string.
     */
    public String nodeToString(TreeNode node) throws TreeException {
        return XMLStringResult.toString(node);
        
    }
    
    /**
     *  Converts the node to a string.
     */
    public String nodeToString(Object node) throws TreeException {
        if (node instanceof TreeNode) {
            return nodeToString((TreeNode) node);
        } else {
            return node.toString();
        }
    }
    
    //--------------------------------------------------------------------------
    //                      * * *  S T R I N G S * * *
    //--------------------------------------------------------------------------
    
    /** Search-and-replace string matches to expression "begin.*end"
     * @param original the original string
     * @param begin the begin of substring to be find
     * @param end the end of substring to be find
     * @param replaceTo the substring to replace it with
     * @return a new string with 1st occurrence replaced
     */
    public String replaceString(String original, String begin, String end, String replaceTo) {
        int bi = original.indexOf(begin);
        int ei = original.indexOf(end, bi) + end.length();
        
        return original.substring(0, bi) + replaceTo + original.substring(ei, original.length());
    }
    
    /**
     * Removes first character occurence from string.
     */
    public String removeChar(String str, char ch) {
        int index = str.indexOf(ch);
        
        if (index > -1) {
            StringBuffer sb = new StringBuffer(str).deleteCharAt(str.indexOf(ch));
            return new String(sb);
        } else {
            return str;
        }
    }
    
    /**
     * Joins elemets delimited by delim.
     */
    public String joinElements(String[] elements, String delim) {
        if (elements == null) {
            return null;
        }
        
        String path = elements[0];
        for (int i = 1; i < elements.length; i++) {
            path += (delim + elements[i]);
        }
        return path;
    }
    
    /**
     * Returns last element.
     */
    public final String lastElement(String string, String delim) {
        int index = string.lastIndexOf(delim);
        if (index == -1) {
            return string;
        } else {
            return string.substring(index + 1);
        }
    }
    
    //--------------------------------------------------------------------------
    //                      * * *  S T R I N G   L O C A L I Z A T I O N * * *
    //--------------------------------------------------------------------------
    
    /** Get localized string.
     * @param key key of localized value.
     * @return localized value.
     */
    public final String getString(String key) {
        return NbBundle.getMessage(this.getClass(), key);
    }
    
    /** Get localized string by passing parameter.
     * @param key key of localized value.
     * @param param argument to use when formating the message
     * @return localized value.
     */
    public final String getString(String key, Object param) {
        return NbBundle.getMessage(this.getClass(), key, param);
    }
    
    /** Get localized string by passing parameter.
     * @param key key of localized value.
     * @param param1 argument to use when formating the message
     * @param param2 the second argument to use for formatting
     * @return localized value.
     */
    public final String getString(String key, Object param1, Object param2) {
        return NbBundle.getMessage(this.getClass(), key, param1, param2);
    }
    
    /** Get localized character. Usually used on mnemonic.
     * @param key key of localized value.
     * @return localized value.
     */
    public final char getChar(String key) {
        return NbBundle.getMessage(this.getClass(), key).charAt(0);
    }
    
    //--------------------------------------------------------------------------
    //                  * * *  D A T A   O B J E C T S  * * *
    //--------------------------------------------------------------------------
    
    /** Converts DataObject to String.
     */
    public String dataObjectToString(DataObject dataObject) throws IOException, BadLocationException {
        EditorCookie editorCookie = (EditorCookie) dataObject.getCookie(EditorCookie.class);
        
        if (editorCookie != null) {
            StyledDocument document = editorCookie.openDocument();
            if (document != null) {
                return  document.getText(0, document.getLength());
            }
        }
        return null;
    }
    
    /** Saves DataObject
     */
    public void saveDataObject(DataObject dataObject) throws IOException {
        SaveCookie cookie = (SaveCookie) dataObject.getCookie(SaveCookie.class);
        cookie.save();
    }
    
    //--------------------------------------------------------------------------
    //                  * * *  F I L E S Y S T E M S  * * *
    //--------------------------------------------------------------------------
    
    /**
     * Mounts local directory
     */
    public LocalFileSystem.Impl mountDirectory(File dir) throws PropertyVetoException, IOException {
        LocalFileSystem fs = new LocalFileSystem();
        fs.setRootDirectory(dir);
        Repository rep = Repository.getDefault();
        FileSystem ffs = rep.findFileSystem(fs.getSystemName());
        if (ffs != null) {
            rep.removeFileSystem(ffs);
        }
        rep.addFileSystem(fs);
        return new LocalFileSystem.Impl(fs);
    }
    
    /**
     * Opens the XML Document with the given package, name and extension
     */
    public TreeDocument openXMLDocument(String aPackage, String name, String ext) throws IOException {
        DataObject dao = findDataObject(aPackage, name, ext);
        
        if (dao == null) {
            throw new IOException(aPackage + "." + name + "." + ext + " data object not found.");
        }
        
        XMLDataObject xmlDataObject;
        if (XMLDataObject.class.isInstance(dao)) {
            xmlDataObject = (XMLDataObject) dao;
        } else {
            throw new IOException(aPackage + "." + name + "." + ext + " data object is not XMLDataObject.");
        }
        
        TreeDocumentCookie cookie = (TreeDocumentCookie) xmlDataObject.getCookie(TreeDocumentCookie.class);
        if (cookie == null) {
            throw new IOException("Missing TreeDocumentCookie at " + aPackage + "." + name + "." + ext);
        }
        
        TreeDocument document = (TreeDocument) cookie.getDocumentRoot();
        if (document == null) {
            throw new IOException("Ivalid XML data object" + aPackage + "." + name + "." + ext);
        }
        
        return document;
    }
    
    /**
     * Deletes FileObject.
     */
    public void deleteFileObject(FileObject fo) throws IOException {
        DataObject dataObject = DataObject.find(fo);
        dataObject.getNodeDelegate().destroy();
    }
    
    /**
     * Finds DataFolder.
     */
    public DataFolder findFolder(String aPackage) throws Exception {
        return (DataFolder) findDataObject(aPackage, null, null);
    }
    
    /**
     * Finds absolut path for FileObject.
     */
    public String toAbsolutePath(FileObject fo) {
        return FileUtil.toFile(fo).getAbsolutePath();
    }
    
    /**
     * Finds the DataObject with the given package, name and extension
     */
    public DataObject findDataObject(String aPackage, String name, String ext) throws DataObjectNotFoundException {
        FileObject fo = null;
        fo = Repository.getDefault().find(aPackage, name, ext);
        if (fo == null) {
            return null;
        } else {
            return DataObject.find(fo);
        }
    }
    
    /**
     * Finds the DataObject with the given package, name and extension
     */
    public FileObject findFileObject(String aPackage, String name, String ext) {
        return Repository.getDefault().find(aPackage, name, ext);
    }
    
    /**
     * Finds the DataObject with the given name in test's 'data' folder. The name of a resource is
     * a "/"-separated path name that identifies the resource relatively to 'data' folder.<p />
     * <i>e.g. "sub_dir/data.xml"</i>
     */
    public DataObject findData(String name) throws DataObjectNotFoundException {
        URL url = this.getClass().getResource("data/" + name);
        if (url == null) return null;
        FileObject fo = findFileObject(url.toExternalForm());
        if (fo == null) {
            if (DEBUG) {
                System.err.println("I cannot find FileObject: " + url);
            }
            return null;
        } else {
            return DataObject.find(fo);
        }
    }
    
    /**
     * Finds the DataObject with the given name. The name of a resource is
     * a "/"-separated path name that identifies the resource or Nbfs URL.
     */
    public DataObject findDataObject(String name) throws DataObjectNotFoundException {
        FileObject fo = findFileObject(name);
        if (fo == null) {
            if (DEBUG) {
                System.err.println("I cannot find FileObject: " + name);
            }
            return null;
        } else {
            return DataObject.find(fo);
        }
    }
    
    /**
     * Finds the FileObject with the given name. The name of a resource is
     * a "/"-separated path name that identifies the resource or Nbfs URL.
     */
    public FileObject findFileObject(String name) {
        FileObject fo = null;
        if (name.startsWith("nbfs:")) {
            try {
                fo = NbfsURLConnection.decodeURL(new URL(name));
            } catch (MalformedURLException mue) {};
        } else {
            fo = Repository.getDefault().findResource(name);
        }
        return fo;
    }
    
    /**
     * Finds the template with the given name.
     */
    public DataObject getTemplate(String tname) throws DataObjectNotFoundException {
        FileObject fileObject = Repository.getDefault().findResource("Templates/" + tname);
        if (fileObject == null) {
            throw new IllegalArgumentException("Cannot find template: " + tname);
        }
        return DataObject.find(fileObject);
    }
    
    /**
     * Creates new DataObject at the folder with given name from the template
     * with the given tname.
     */
    public DataObject newFromTemplate(String tname, String folder, String name) throws IOException {
        DataObject dataObject = getTemplate(tname);
        DataFolder dataFolder = (DataFolder) findDataObject(folder);
        return dataObject.createFromTemplate(dataFolder, name);
    }
    
    /**
     * Removes the DataObject with the given name. The name of a resource is
     * a "/"-separated path name that identifies the resource or Nbfs URL.
     */
    public boolean removeDocument(String name) throws IOException {
        DataObject  dataObject = findDataObject(name);
        if (dataObject != null) {
            dataObject.delete();
            return true;
        } else {
            return false;
        }
    }
    
    //--------------------------------------------------------------------------
    //                        * * *  O T H E R  * * *
    //--------------------------------------------------------------------------
    
    /**
     * Enbles <code>enable = true</code>  or disables <code>enable = false</code> the module.
     */
    public void switchModule(String codeName, boolean enable) throws Exception {
        String statusFile = "Modules/" + codeName.replace('.', '-') + ".xml";
        ModuleInfo mi = getModuleInfo(codeName);
/*
        FileObject fo = findFileObject(statusFile);
        Document document = XMLUtil.parse(new InputSource(fo.getInputStream()), false, false, null, EntityCatalog.getDefault());
        //Document document = XMLUtil.parse(new InputSource(data.getPrimaryFile().getInputStream()), false, false, null, EntityCatalog.getDefault());
        NodeList list = document.getElementsByTagName("param");
 
        for (int i = 0; i < list.getLength(); i++) {
            Element ele = (Element) list.item(i);
            if (ele.getAttribute("name").equals("enabled")) {
                ele.getFirstChild().setNodeValue(enable ? "true" : "false");
                break;
            }
        }
 
        FileLock lock = fo.lock();
        OutputStream os = fo.getOutputStream(lock);
        XMLUtil.write(document, os, "UTF-8");
        lock.releaseLock();
        os.close();
 */
        
        // module is switched
        if (mi.isEnabled() == enable) {
            return;
        }
        
        DataObject data = findDataObject(statusFile);
        EditorCookie ec = (EditorCookie) data.getCookie(EditorCookie.class);
        StyledDocument doc = ec.openDocument();
        
        // Change parametr enabled
        String stag = "<param name=\"enabled\">";
        String etag = "</param>";
        String enabled = enable ? "true" : "false";
        String result;
        
        String str = doc.getText(0,doc.getLength());
        int sindex = str.indexOf(stag);
        int eindex = str.indexOf(etag, sindex);
        if (sindex > -1 && eindex > sindex) {
            result = str.substring(0, sindex + stag.length()) + enabled + str.substring(eindex);
            //System.err.println(result);
        } else {
            //throw new IllegalStateException("Invalid format of: " + statusFile + ", missing parametr 'enabled'");
            // Probably autoload module
            return;
        }
        
        // prepare synchronization and register listener
        final Waiter waiter = new Waiter();
        final PropertyChangeListener pcl = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("enabled")) {
                    waiter.notifyFinished();
                }
            }
        };
        mi.addPropertyChangeListener(pcl);
        
        // save document
        doc.remove(0,doc.getLength());
        doc.insertString(0,result,null);
        ec.saveDocument();
        
        // wait for enabled propety change and remove listener
        waiter.waitFinished();
        mi.removePropertyChangeListener(pcl);
    }
    
    /**
     * Switch on all XML modules and returns <code>true</code> if change state of any module else <code>false</code>.
     */
    public boolean switchAllXMLModules(boolean enable) throws Exception {
        boolean result = false;
        Iterator it = Lookup.getDefault().lookup(new Template(ModuleInfo.class)).allInstances().iterator();
        
        while (it.hasNext()) {
            ModuleInfo mi = (ModuleInfo) it.next();
            if (mi.getCodeNameBase().startsWith("org.netbeans.modules.xml.") && (mi.isEnabled() != enable)) {
                switchModule(mi.getCodeNameBase(), enable);
                result = true;
            }
        }
        return result;
    }
    
    /**
     * Returns module's info or <code>null</null>.
     */
    public ModuleInfo getModuleInfo(String codeName) {
        Iterator it = Lookup.getDefault().lookup(new Template(ModuleInfo.class)).allInstances().iterator();
        
        while (it.hasNext()) {
            ModuleInfo mi = (ModuleInfo) it.next();
            //            if (mi.getCodeNameBase().equals(codeName) && mi.isEnabled()) {
            if (mi.getCodeNameBase().equals(codeName)) {
                return mi;
            }
        }
        return null;
    }
    
    /**
     * Returns <code>true</code> if module is enabled else <code>false</code>.
     */
    public boolean isModuleEnabled(String codeName) {
        ModuleInfo mi = getModuleInfo(codeName);
        if (mi == null) {
            throw new IllegalArgumentException("Invalid codeName: " + codeName);
        }
        
        return mi.isEnabled();
    }
    
    protected static Random randomGenerator = new Random();
    
    /**
     * Generates random integer.
     */
    public int randomInt(int n) {
        return randomGenerator.nextInt(n);
    }
    
    // ************************
    // * * *  C L A S E S * * *
    // ************************
    
    static class Waiter {
        private boolean finished = false;
        
        /** Restarts Synchronizer.
         */
        public void start() {
            finished = false;
        }
        
        /** Wait until the task is finished.
         */
        public void waitFinished() {
            if (!finished) {
                synchronized (this) {
                    while (!finished) {
                        try {
                            wait();
                        } catch (InterruptedException ex) {
                        }
                    }
                }
            }
        }
        
        /** Notify all waiters that this task has finished.
         */
        public void notifyFinished() {
            synchronized (this) {
                finished = true;
                notifyAll();
            }
        }
    }
}
