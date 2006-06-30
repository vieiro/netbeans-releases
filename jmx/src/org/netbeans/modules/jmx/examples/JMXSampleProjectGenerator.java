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

package org.netbeans.modules.jmx.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.netbeans.spi.project.support.ant.AntProjectHelper;

import org.openide.modules.InstalledFileLocator;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.filesystems.FileLock;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import org.netbeans.api.project.ProjectManager;
import org.openide.util.Mutex;
import org.openide.util.MutexException;

/**
 * Create a sample web project by unzipping a template into some directory
 *
 * @author Martin Grebac, Tomas Zezula. Modified by jfdenise to update namespace and jar name generation
 */
public class JMXSampleProjectGenerator {

    private static final String PROJECT_CONFIGURATION_NAMESPACE = "http://www.netbeans.org/ns/j2se-project/3";   //NOI18N

    private JMXSampleProjectGenerator() {}

    public static void overwriteProperty(File projectLocation, final String key, final String value) throws Exception {
        FileObject prjLoc = FileUtil.toFileObject(projectLocation);
        final File projProperties = FileUtil.toFile(prjLoc.getFileObject(AntProjectHelper.PROJECT_PROPERTIES_PATH));
      
        ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction() {
          public Object run() throws Exception {
              java.util.Properties p = new java.util.Properties();
              
              FileInputStream fis = new FileInputStream(projProperties);
              try {
                  p.load(fis);
                  p.setProperty(key, value);
              }finally{
                  fis.close();
              }
              FileOutputStream fos = new FileOutputStream(projProperties);
              try {
                  p.store(fos,null);
              }finally{
                  fos.close();
              }
              return null;
          }
      });
    }
 
    public static FileObject createProjectFromTemplate(final FileObject template, 
            File projectLocation, 
            final String name) throws IOException {
        FileObject prjLoc = null;
        if (template.getExt().endsWith("zip")) {  //NOI18N
            unzip(template.getInputStream(), projectLocation);
            // update project.xml
            try {
                prjLoc = FileUtil.toFileObject(projectLocation);
                File projXml = FileUtil.toFile(prjLoc.getFileObject(AntProjectHelper.PROJECT_XML_PATH));
                Document doc = XMLUtil.parse(new InputSource(projXml.toURI().toString()), false, true, null, null);
                NodeList nlist = doc.getElementsByTagNameNS(PROJECT_CONFIGURATION_NAMESPACE, "name");       //NOI18N
                if (nlist != null) {
                    for (int i=0; i < nlist.getLength(); i++) {
                        Node n = nlist.item(i);
                        if (n.getNodeType() != Node.ELEMENT_NODE) {
                            continue;
                        }
                        Element e = (Element)n;
                        
                        replaceText(e, name);
                    }
                    saveXml(doc, prjLoc, AntProjectHelper.PROJECT_XML_PATH);
                }
                overwriteProperty(projectLocation, "dist.jar", "dist" + File.separator + name.toLowerCase() + ".jar");// NOI18N
            } catch (Exception e) {
                throw new IOException(e.toString());
            }
        
            prjLoc.refresh(false);
        }
        return prjLoc;
    }
    
    private static void unzip(InputStream source, File targetFolder) throws IOException {
        //installation
        ZipInputStream zip=new ZipInputStream(source);
        try {
            ZipEntry ent;
            while ((ent = zip.getNextEntry()) != null) {
                File f = new File(targetFolder, ent.getName());
                if (ent.isDirectory()) {
                    f.mkdirs();
                } else {
                    f.getParentFile().mkdirs();
                    FileOutputStream out = new FileOutputStream(f);
                    try {
                        FileUtil.copy(zip, out);
                    } finally {
                        out.close();
                    }
                }
            }
        } finally {
            zip.close();
        }
    }

    /**
     * Extract nested text from an element.
     * Currently does not handle coalescing text nodes, CDATA sections, etc.
     * @param parent a parent element
     */
    private static void replaceText(Element parent, String name) {
        NodeList l = parent.getChildNodes();
        for (int i = 0; i < l.getLength(); i++) {
            if (l.item(i).getNodeType() == Node.TEXT_NODE) {
                Text text = (Text)l.item(i);
                text.setNodeValue(name);
                return;
            }
        }
    }
    
    /**
     * Save an XML config file to a named path.
     * If the file does not yet exist, it is created.
     */
    private static void saveXml(Document doc, FileObject dir, String path) throws IOException {
        FileObject xml = FileUtil.createData(dir, path);
        FileLock lock = xml.lock();
        try {
            OutputStream os = xml.getOutputStream(lock);
            try {
                XMLUtil.write(doc, os, "UTF-8"); // NOI18N
            } finally {
                os.close();
            }
        } finally {
            lock.releaseLock();
        }
    }   
}