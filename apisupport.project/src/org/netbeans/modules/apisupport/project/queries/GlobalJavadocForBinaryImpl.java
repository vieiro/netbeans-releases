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

package org.netbeans.modules.apisupport.project.queries;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.queries.JavadocForBinaryQuery;
import org.netbeans.modules.apisupport.project.Util;
import org.netbeans.modules.apisupport.project.universe.NbPlatform;
import org.netbeans.spi.java.queries.JavadocForBinaryQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;

/**
 * Able to find javadoc in the appropriate NbPlatform for the given URL.
 *
 * @author Jesse Glick, Martin Krauskopf
 */
public final class GlobalJavadocForBinaryImpl implements JavadocForBinaryQueryImplementation {
    
    public JavadocForBinaryQuery.Result findJavadoc(URL binaryRoot) {
        try {
            NbPlatform supposedPlaf = null;
            for (Iterator it = NbPlatform.getPlatforms().iterator(); it.hasNext(); ) {
                NbPlatform plaf = (NbPlatform) it.next();
                // XXX more robust condition?
                if (binaryRoot.toExternalForm().indexOf(plaf.getDestDir().toURI().toURL().toExternalForm()) != -1) {
                    supposedPlaf = plaf;
                    break;
                }
            }
            if (supposedPlaf == null) {
                return null;
            }
            if (!binaryRoot.getProtocol().equals("jar")) { // NOI18N
                // XXX probably shouldn't just return null in this case
                Util.err.log(binaryRoot + " is not an archive file."); // NOI18N
                return null;
            }
            File binaryRootF = new File(URI.create(FileUtil.getArchiveFile(binaryRoot).toExternalForm()));
            FileObject fo = FileUtil.toFileObject(binaryRootF);
            if (fo == null) {
                Util.err.log("Cannot found FileObject for " + binaryRootF + "(" + binaryRoot + ")"); // NOI18N
                return null;
            }
            String cnbdashes = fo.getName();
            final List/*<URL>*/ candidates = new ArrayList();
            URL[] roots = supposedPlaf.getJavadocRoots();
            for (int i = 0; i < roots.length; i++) {
                if (roots[i].getProtocol().equals("jar")) { // NOI18N
                    // suppose javadoc zip like org-openide-util.zip
                    candidates.add(roots[i]);
                } else {
                    // suppose netbeans cvs root
                    candidates.add(new URL(roots[i], cnbdashes + '/'));
                }
            }
            Iterator it = candidates.iterator();
            while (it.hasNext()) {
                URL u = (URL) it.next();
                if (URLMapper.findFileObject(u) == null) {
                    it.remove();
                }
            }
            return new JavadocForBinaryQuery.Result() {
                public URL[] getRoots() {
                    return (URL[]) candidates.toArray(new URL[candidates.size()]);
                }
                public void addChangeListener(ChangeListener l) {}
                public void removeChangeListener(ChangeListener l) {}
            };
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }
    
}
