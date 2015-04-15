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

package org.netbeans.modules.javadoc.search;
import java.beans.PropertyChangeEvent;

import java.beans.PropertyChangeListener;
import java.io.InputStream;


import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import java.io.Reader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;

import javax.swing.event.ChangeListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.parser.ParserDelegator;
import javax.swing.text.MutableAttributeSet;

import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;


import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.java.classpath.GlobalPathRegistryEvent;

import org.netbeans.api.java.classpath.GlobalPathRegistryListener;

import org.netbeans.api.java.queries.JavadocForBinaryQuery;
import org.openide.util.BaseUtilities;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;

/**
 * Class which is able to serve index files of Javadoc for all
 * currently used Javadoc documentation sets.
 * @author Petr Hrebejk
 */
public class JavadocRegistry implements GlobalPathRegistryListener, ChangeListener, PropertyChangeListener  {
        
    private static JavadocRegistry INSTANCE;
    private static final Logger LOG = Logger.getLogger(JavadocRegistry.class.getName());
    
    private GlobalPathRegistry regs;    
    private final ChangeSupport cs = new ChangeSupport(this);
    private Set<JavadocForBinaryQuery.Result> results;
    private ClassPath docRoots;
    private Set<ClassPath> classpaths;
    
    @SuppressWarnings("LeakingThisInConstructor")
    private JavadocRegistry() {
        this.regs = GlobalPathRegistry.getDefault ();        
        this.regs.addGlobalPathRegistryListener(this);
    }
    
    public static synchronized JavadocRegistry getDefault() {
        if ( INSTANCE == null ) {
            INSTANCE = new JavadocRegistry();
        }
        return INSTANCE;
    }

    /** Returns Array of the Javadoc Index roots
     */
    public URL[] getDocRoots() {
        synchronized (this) {
            if (this.docRoots != null) {
                return docRoots();
            }
        }        
        //XXX must be called out of synchronized block to prevent
        // deadlock. throwCache is called under the ProjectManager.mutex
        // write lock and Project's SFBQI requires the ProjectManager.mutex readLock
        Set<ClassPath> _classpaths = new HashSet<ClassPath>();
        Set<JavadocForBinaryQuery.Result> _results = new HashSet<JavadocForBinaryQuery.Result>();
        Set<URL> s = readRoots(this, _classpaths, _results);
        synchronized (this) {
            if (this.docRoots == null) {
                this.docRoots = ClassPathSupport.createClassPath(s.toArray(new URL[s.size()]));
                this.classpaths = _classpaths;
                this.results = _results;
                registerListeners(this, _classpaths, _results, this.docRoots);
            }
            return docRoots();
        }
    }
    private URL[] docRoots() {
        List<ClassPath.Entry> entries = docRoots.entries();
        URL[] roots = new URL[entries.size()];
        for (int i = 0; i < roots.length; i++) {
            roots[i] = entries.get(i).getURL();
        }
        return roots;
    }
    
    
    public JavadocSearchType findSearchType(URL apidocRoot) {
        String encoding = getDocEncoding (apidocRoot);
        for (JavadocSearchType jdst : Lookup.getDefault().lookupAll(JavadocSearchType.class)) {
            if (jdst.accepts(apidocRoot, encoding)) {
                return jdst;
            }
        }        
        return null;
    }    
        
    // Private methods ---------------------------------------------------------
    
    private static Set<URL> readRoots(
            JavadocRegistry jdr,
            Set<ClassPath> classpaths,
            Set<JavadocForBinaryQuery.Result> results) {
        
        Set<URL> roots = new HashSet<URL>();
        List<ClassPath> paths = new LinkedList<ClassPath>();
        paths.addAll( jdr.regs.getPaths( ClassPath.COMPILE ) );        
        paths.addAll( jdr.regs.getPaths( ClassPath.BOOT ) );
        for (ClassPath ccp : paths) {
            classpaths.add (ccp);
            //System.out.println("CCP " + ccp );
            for (ClassPath.Entry ccpRoot : ccp.entries()) {
                //System.out.println(" CCPR " + ccpRoot.getURL());
                JavadocForBinaryQuery.Result result = JavadocForBinaryQuery.findJavadoc(ccpRoot.getURL());
                results.add (result);
                URL[] jdRoots = result.getRoots();
                for (URL jdRoot : jdRoots) {
                    if(verify(jdRoot)) {
                        roots.add(jdRoot);
                    }
                }
            }
        }
        //System.out.println("roots=" + roots);
        return roots;
    }
    
    /**
     * Copied and modified from {@link org.netbeans.modules.java.classpath.SimplePathResourceImplementation}
     * @param root
     */
    private static boolean verify(final URL root) {
        if (root == null) {
            return false;
        }
        final String rootS = root.toString();
        if (rootS.matches("file:.+[.]jar/?")) { //NOI18N
            File f = null;
            boolean dir = false;
            try {
                f = BaseUtilities.toFile(root.toURI());
                dir = f.isDirectory();
            } catch (URISyntaxException use) {
                //pass - handle as non dir
            }
            if (!dir) {
                //Special case for /tmp/build/.jar/ see issue #235695
                if (f == null || f.exists() || !f.getName().equals(".jar")) {   //NOI18N
                    return false;
                }
            }
        }
        if (!rootS.endsWith("/")) {
            return false;
        }
        if (rootS.contains("/../") || rootS.contains("/./")) {  //NOI18N
            return false;
        }
        return true;
    }
    
    private static void registerListeners(
            JavadocRegistry jdr,
            Set<ClassPath> classpaths,
            Set<JavadocForBinaryQuery.Result> results,
            ClassPath docRoots) {
        
        for (ClassPath cpath : classpaths) {
            cpath.addPropertyChangeListener(jdr);
        }
        for (JavadocForBinaryQuery.Result result : results) {
            result.addChangeListener(jdr);
        }
        
        docRoots.addPropertyChangeListener (jdr);
        
    }

    public @Override void pathsAdded(GlobalPathRegistryEvent event) {
        this.throwCache ();
        cs.fireChange();
    }

    public @Override void pathsRemoved(GlobalPathRegistryEvent event) {
        this.throwCache ();
        cs.fireChange();
    }
    
    public @Override void propertyChange (PropertyChangeEvent event) {
        if (ClassPath.PROP_ENTRIES.equals (event.getPropertyName()) ||
            event.getSource() == this.docRoots) {
            this.throwCache ();
            cs.fireChange();
        }
    }
    
    
    public @Override void stateChanged(ChangeEvent e) {
        this.throwCache ();
        cs.fireChange();
    }

    
    
    public void addChangeListener(ChangeListener l) {
        cs.addChangeListener(l);
    }
    
    public void removeChangeListener(ChangeListener l) {
        cs.removeChangeListener(l);
    }
    
    private synchronized void throwCache () {
        //Unregister itself from classpaths, not interested in events
        if (classpaths != null) {
            for (ClassPath cp : classpaths) {
                cp.removePropertyChangeListener(this);
            }
            classpaths.clear();
        }
        //Unregister itself from results, not interested in events
        if (results != null) {
            for (JavadocForBinaryQuery.Result result : results) {
                result.removeChangeListener(this);
            }
            results.clear();
        }
        //Unregister listener from docRoots
        if (docRoots != null) {
            docRoots.removePropertyChangeListener(this);
            docRoots = null;
        }
    }


    private String getDocEncoding(URL root) {
        assert root != null && root.toString().endsWith("/") : root;
        InputStream is = URLUtils.open(root, "index-all.html", "index-files/index-1.html");
        if (is != null) {
            try {
                try {
                    ParserDelegator pd = new ParserDelegator();
                    BufferedReader in = new BufferedReader(new InputStreamReader(is));
                    EncodingCallback ecb = new EncodingCallback(in);
                    pd.parse(in, ecb, true);
                    return ecb.getEncoding();
                } finally {
                    is.close();
                }
            } catch (IOException x) {
                LOG.log(Level.FINE, "Getting encoding from " + root, x);
            }
        }
        return null;
    }

    private static class EncodingCallback extends HTMLEditorKit.ParserCallback {


        private Reader in;
        private String encoding;

        public EncodingCallback (Reader in) {
            this.in = in;
        }


        public String getEncoding () {
            return this.encoding;
        }


        public @Override void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
            if (t == HTML.Tag.META) {
                String value = (String) a.getAttribute(HTML.Attribute.CONTENT);
                if (value != null) {
                    StringTokenizer tk = new StringTokenizer(value,";"); // NOI18N
                    while (tk.hasMoreTokens()) {
                        String str = tk.nextToken().trim();
                        if (str.startsWith("charset")) {        //NOI18N
                            str = str.substring(7).trim();
                            if (str.charAt(0)=='=') {
                                this.encoding = str.substring(1).trim();
                                try {
                                    this.in.close();
                                } catch (IOException ioe) {/*Ignore it*/}
                                return;                                
                            }
                        }
                    }
                }
            }
        }

        public @Override void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
            if (t == HTML.Tag.BODY) {
                try {
                    this.in.close ();
                } catch (IOException ioe) {/*Ignore it*/}
            }
        }
    }

}
