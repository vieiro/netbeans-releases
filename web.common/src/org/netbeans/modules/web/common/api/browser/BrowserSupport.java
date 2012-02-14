/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.common.api.browser;

import java.net.URL;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.web.common.spi.browser.DOMInspectionFeature;
import org.openide.filesystems.FileObject;
import org.openide.windows.TopComponent;

/**
 * Helper class to be added to project's lookup and to be used to open URLs from
 * project. It keeps association between URL opened in browser and project's file.
 * According to results of DependentFileQuery it can answer whether project's file
 * change should result into refresh of URL in the browser. It also keeps 
 * WebBrowserPane instance to open URLs in. Such browser pane can be single global one
 * shared with HtmlBrowser.URLDisaplyer (ie. getGlobalSharedOne method) or single
 * global one owned by BrowserSupport (ie. getDefault method) or per project
 * pane (ie. getProjectScoped method).
 */
public final class BrowserSupport {
    
    private WebBrowserPane pane;
    private Project project;
    
    private Pair currentContext;
    private URL currentURL;
    private WebBrowserPane.WebBrowserPaneListener l;

    private static BrowserSupport INST;
    
    /**
     * Returns instance of BrowserSupport which shares WebBrowserPane
     * with HtmlSupport.URLDisplayer. That means that opening a URL via
     * BrowserSupport.load() or via HtmlSupport.URLDisaplayer.show() will
     * results into URL being opened in the same browser pane.
     */
    public static synchronized BrowserSupport getGlobalSharedOne() {
        // XXX: to implement this I need to hack in NbDisplayerURL
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    /**
     * Returns singleton instance of BrowserSupport with its own WebBrowserPane.
     * Using this instance means that all URLs opened with BrowserSupport will 
     * have its own browser pane and all URLs opened via HtmlSupport.URLDisplayer
     * will have its own browser pane as well.
     */
    public static synchronized BrowserSupport getDefault() {
        if (INST == null) {
            // XXX: update when preferred browser changes:
            INST = new BrowserSupport(WebBrowsers.getInstance().getPreffered().createNewBrowserPane(), null);
        }
        return INST;
    }

    /**
     * Returns per-project instance of BrowserSupport. All project URLs opened
     * via this instance will have its own dedicated browser pane.
     */
    public static BrowserSupport getProjectScoped(Project p) {
        // XXX: easy to implement; to be done later
        throw new UnsupportedOperationException("not implemented yet");
    }

    private BrowserSupport(WebBrowserPane pane, Project project) {
        this.pane = pane;
        this.project = project;
        this.l = new ListenerImpl();
        pane.addListener(l);
    }
    
    /**
     * Opens URL in a browser pane associated with this BrowserSupport.
     * FileObject param is "context" associated with URL being opened. It can be 
     * file which is being opened in the browser or project folder in case of URL being result
     * of project execution. If browser pane does not support concept of reloading it will simply 
     * open a tab with this URL on each execution.
     * 
     */
    public void load(URL url, FileObject context) {
        FileObject file = context;
        Project p = FileOwnerQuery.getOwner(context);
        if (p != null && p.getProjectDirectory().equals(file)) {
            // if context fileobject points to a project folder then keep reference just to project:
            file = null;
        }

        currentContext = new Pair(p, file);
        currentURL = url;
        pane.showURL(url);
    }

    /**
     * The same behaviour as load() method but file object context is not necessary
     * to be passed again.
     */
    public boolean reload(URL url) {
        if (!canReload(url)) {
            return false;
        }
        pane.reload();
        return true;
    }
    
    /**
     * Has this URL being previous opened via load() method or not? BrowserSupport
     * remember last URL opened.
     */
    public boolean canReload(URL url) {
        return currentURL != null && currentURL.equals(url);
    }
    
    /**
     * Returns URL which was opened in the browser and which was associated with
     * given FileObject. That is calling load(URL, FileObject) creates mapping 
     * between FileObject in IDE side and URL in browser side and this method 
     * allows to use the mapping to retrieve URL.
     * 
     * If checkDependentFiles parameter is set to true then 
     * DependentFileQuery.isDependent will be consulted to check whether URL opened
     * in the browser does not depend on given FileObject. If answer is yes than
     * any change in this FileObject should be reflected in browser and URL
     * should be refreshed in browser.
     */
    public URL getBrowserURL(FileObject fo, boolean checkDependentFiles) {
        Project p = FileOwnerQuery.getOwner(fo);
        if (currentContext == null || currentURL == null) {
            return null;
        }
        URL projectURL = null;
        if (checkDependentFiles) {
            if (currentContext.file == null) {
                if (currentContext.project != null) {
                    // a project was "Run" and we have no idea which exact project's 
                    // file was opened in browser;
                    // because "fo" belongs to the project we could say
                    // that URL corresponding for this fo is project's URL;
                    // let's first check other opened browsers for better match and
                    // if nothing better is found we can return project's URL
                    projectURL = currentURL;
                }
            } else {
                if (DependentFileQuery.isDependent(currentContext.file, fo)) {
                    return currentURL;
                }
            }
        } else {
            if (fo.equals(currentContext.file)) {
                return currentURL;
            }
        }
        if (projectURL != null) {
            return projectURL;
        }
        return null;
    }
    
    /**
     * Returns DOM for given URL for inspection. Just an illustration how 
     * DOMInspectionFeature would fit here.
     */
    public Object getDOM() {
        DOMInspectionFeature dom = pane.getLookup().lookup(DOMInspectionFeature. class);
        if (dom == null) {
            return null;
        }
        return dom.getDOM();
    }

    public static Object getDOM(TopComponent component) {
        DOMInspectionFeature impl = component.getLookup().lookup(DOMInspectionFeature.class);
        if (impl == null) {
            return null;
        } else {
            return impl.getDOM();
        }
    }
    
    
    private static class Pair {
        Project project;
        FileObject file;
        
        public Pair(Project project, FileObject file) {
            this.project = project;
            this.file = file;
        }
    }
    
    private class ListenerImpl implements WebBrowserPane.WebBrowserPaneListener {

        @Override
        public void browserEvent(WebBrowserPane.WebBrowserPaneEvent event) {
            if (event instanceof WebBrowserPane.WebBrowserPaneWasClosedEvent) {
                currentURL = null;
                currentContext = null;
            }
        }
        
    }
    
}
