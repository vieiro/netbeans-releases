/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.websvc.rest;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.j2ee.common.dd.DDHelper;
import org.netbeans.modules.j2ee.dd.api.common.InitParam;
import org.netbeans.modules.j2ee.dd.api.web.DDProvider;
import org.netbeans.modules.j2ee.dd.api.web.Servlet;
import org.netbeans.modules.j2ee.dd.api.web.ServletMapping;
import org.netbeans.modules.j2ee.dd.api.web.ServletMapping25;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleProvider;
import org.netbeans.modules.websvc.rest.spi.RestSupport;
import static org.netbeans.modules.websvc.rest.spi.RestSupport.REST_SERVLET_ADAPTOR;
import static org.netbeans.modules.websvc.rest.spi.RestSupport.REST_SERVLET_ADAPTOR_CLASS;
import static org.netbeans.modules.websvc.rest.spi.RestSupport.REST_SERVLET_ADAPTOR_CLASS_2_0;
import static org.netbeans.modules.websvc.rest.spi.RestSupport.REST_SERVLET_ADAPTOR_CLASS_OLD;
import static org.netbeans.modules.websvc.rest.spi.RestSupport.REST_SPRING_SERVLET_ADAPTOR_CLASS;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 */
public class WebXmlUpdater {

    private static final String JERSEY_PROP_PACKAGES = "com.sun.jersey.config.property.packages"; //NOI18N
    private static final String JERSEY_PROP_PACKAGES_DESC = "Multiple packages, separated by semicolon(;), can be specified in param-value"; //NOI18N
    private static final String POJO_MAPPING_FEATURE = "com.sun.jersey.api.json.POJOMappingFeature"; // NOI18N
    
    private RestSupport restSupport;

    public WebXmlUpdater(RestSupport restSupport) {
        this.restSupport = restSupport;
    }

    public static Servlet getRestServletAdaptorByName(WebApp webApp, String servletName) {
        if (webApp != null) {
            for (Servlet s : webApp.getServlet()) {
                if (servletName.equals(s.getServletName())) {
                    return s;
                }
            }
        }
        return null;
    }

    public WebApp findWebApp() {
        WebModule wm = WebModule.getWebModule(restSupport.getProject().getProjectDirectory());
        if (wm != null) {
            FileObject ddFo = wm.getDeploymentDescriptor();
            if (ddFo != null) {
                try {
                    return DDProvider.getDefault().getDDRoot(ddFo);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return null;
    }

    // XXX: TODO: again this code cleans up "ServletAdaptor" servlet from web.xml
    // which looks like EE5 solution; it cannot work in EE6 where servlet names is different
    protected void removeResourceConfigFromWebApp() throws IOException {
        FileObject ddFO = getOrCreateWebXml();
        WebApp webApp = getWebApp();
        if (webApp == null) {
            return;
        }
        boolean needsSave = false;
        Servlet restServlet = getRestServletAdaptorByName(webApp, REST_SERVLET_ADAPTOR);
        if (restServlet != null) {
            webApp.removeServlet(restServlet);
            needsSave = true;
        }

        for (ServletMapping sm : webApp.getServletMapping()) {
            if (REST_SERVLET_ADAPTOR.equals(sm.getServletName())) {
                webApp.removeServletMapping(sm);
                needsSave = true;
                break;
            }
        }

        if (needsSave) {
            webApp.write(ddFO);
        }
    }

    public WebApp getWebApp() throws IOException {
        FileObject fo = getOrCreateWebXml();
        if (fo != null) {
            return DDProvider.getDefault().getDDRoot(fo);
        }
        return null;
    }

    public void configRestPackages( String... packs ) throws IOException {
        try {
            addResourceConfigToWebApp("/webresources/*");           // NOI18N
            FileObject ddFO = getOrCreateWebXml();
            WebApp webApp = getWebApp();
            if (webApp == null) {
                return;
            }
            if (webApp.getStatus() == WebApp.STATE_INVALID_UNPARSABLE ||
                     webApp.getStatus() == WebApp.STATE_INVALID_OLD_VERSION)
            {
                return;
            }
            boolean needsSave = false;
            Servlet adaptorServlet = getRestServletAdaptor(webApp);
            if ( adaptorServlet == null ){
                return;
            }
            InitParam[] initParams = adaptorServlet.getInitParam();
            boolean jerseyParamFound = false;
            boolean jacksonParamFound = false;
            for (InitParam initParam : initParams) {
                if (initParam.getParamName().equals(JERSEY_PROP_PACKAGES)) {
                    jerseyParamFound = true;
                    String paramValue = initParam.getParamValue();
                    if (paramValue != null) {
                        paramValue = paramValue.trim();
                    }
                    else {
                        paramValue = "";
                    }
                    if (paramValue.length() == 0 || paramValue.equals(".")){ // NOI18N
                        initParam.setParamValue(getPackagesList(packs));
                        needsSave = true;
                    }
                    else {
                        String[] existed = paramValue.split(";");
                        LinkedHashSet<String> set = new LinkedHashSet<String>();
                        set.addAll(Arrays.asList(existed));
                        set.addAll(Arrays.asList(packs));
                        initParam.setParamValue(getPackagesList(set));
                        needsSave = existed.length != set.size();
                    }
                }
                else if ( initParam.getParamName().equals( POJO_MAPPING_FEATURE)){
                    jacksonParamFound = true;
                    String paramValue = initParam.getParamValue();
                    if (paramValue != null) {
                        paramValue = paramValue.trim();
                    }
                    if ( !Boolean.TRUE.toString().equals(paramValue)){
                        initParam.setParamValue(Boolean.TRUE.toString());
                        needsSave = true;
                    }
                }
            }
            if (!jerseyParamFound) {
                InitParam initParam = createJerseyPackagesInitParam(adaptorServlet,
                        packs);
                adaptorServlet.addInitParam(initParam);
                needsSave = true;
            }
            if ( !jacksonParamFound ){
                InitParam initParam = createInitParam(adaptorServlet,
                        POJO_MAPPING_FEATURE, Boolean.TRUE.toString(), null);
                adaptorServlet.addInitParam(initParam);
                needsSave = true;
            }
            if (needsSave) {
                webApp.write(ddFO);
                restSupport.logResourceCreation();
            }
	}
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private String getPackagesList( Iterable<String> packs ) {
        StringBuilder builder = new StringBuilder();
        for (String pack : packs) {
            builder.append( pack);
            builder.append(';');
        }
        String packages ;
        if ( builder.length() >0 ){
            packages  = builder.substring( 0 ,  builder.length() -1 );
        }
        else{
            packages = builder.toString();
        }
        return packages;
    }

    private String getPackagesList( String[] packs ) {
        return getPackagesList( Arrays.asList( packs));
    }

    private InitParam createJerseyPackagesInitParam( Servlet adaptorServlet,
            String... packs ) throws ClassNotFoundException
    {
        return createInitParam(adaptorServlet, JERSEY_PROP_PACKAGES,
                getPackagesList(packs), JERSEY_PROP_PACKAGES_DESC);
    }

    private InitParam createInitParam( Servlet adaptorServlet, String name,
            String value , String description ) throws ClassNotFoundException
    {
        InitParam initParam = (InitParam) adaptorServlet
                .createBean("InitParam"); // NOI18N
        initParam.setParamName(name);
        initParam.setParamValue(value);
        if ( description != null ){
            initParam.setDescription(description);
        }
        return initParam;
    }

    public void addResourceConfigToWebApp(String resourcePath) throws IOException {
        FileObject ddFO = getOrCreateWebXml();
        WebApp webApp = getWebApp();
        if (webApp == null) {
            return;
        }
        if (webApp.getStatus() == WebApp.STATE_INVALID_UNPARSABLE ||
                webApp.getStatus() == WebApp.STATE_INVALID_OLD_VERSION)
        {
            DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Message(
                        NbBundle.getMessage(RestSupport.class, "MSG_InvalidDD", webApp.getError()),
                        NotifyDescriptor.ERROR_MESSAGE));
            return;
        }
        boolean needsSave = false;
        try {
            Servlet adaptorServlet = getRestServletAdaptor(webApp);
            if (adaptorServlet == null) {
                adaptorServlet = (Servlet) webApp.createBean("Servlet"); //NOI18N
                adaptorServlet.setServletName(REST_SERVLET_ADAPTOR);
                boolean isSpring = restSupport.hasSpringSupport();
                if (isSpring) {
                    adaptorServlet.setServletClass(REST_SPRING_SERVLET_ADAPTOR_CLASS);
                    InitParam initParam = (InitParam) adaptorServlet.createBean("InitParam"); //NOI18N
                    initParam.setParamName(JERSEY_PROP_PACKAGES);
                    initParam.setParamValue("."); //NOI18N
                    initParam.setDescription(JERSEY_PROP_PACKAGES_DESC);
                    adaptorServlet.addInitParam(initParam);
                } else {
                    if (restSupport.hasJersey2()) {
                        throw new IllegalStateException("this should not be needed!"); //
                        //adaptorServlet.setServletClass(REST_SERVLET_ADAPTOR_CLASS_2_0);
                    } else {
                        adaptorServlet.setServletClass(REST_SERVLET_ADAPTOR_CLASS);
                    }
                }
                adaptorServlet.setLoadOnStartup(BigInteger.valueOf(1));
                webApp.addServlet(adaptorServlet);
                needsSave = true;
            }

            String resourcesUrl = resourcePath;
            if (!resourcePath.startsWith("/")) { //NOI18N
                resourcesUrl = "/"+resourcePath; //NOI18N
            }
            if (resourcesUrl.endsWith("/")) { //NOI18N
                resourcesUrl = resourcesUrl+"*"; //NOI18N
            } else if (!resourcesUrl.endsWith("*")) { //NOI18N
                resourcesUrl = resourcesUrl+"/*"; //NOI18N
            }

            ServletMapping sm = getRestServletMapping(webApp);
            if (sm == null) {
                sm = (ServletMapping) webApp.createBean("ServletMapping"); //NOI18N
                sm.setServletName(adaptorServlet.getServletName());
                if (sm instanceof ServletMapping25) {
                    ((ServletMapping25)sm).addUrlPattern(resourcesUrl);
                } else {
                    sm.setUrlPattern(resourcesUrl);
                }
                webApp.addServletMapping(sm);
                needsSave = true;
            } else {
                // check old url pattern
                boolean urlPatternChanged = false;
                if (sm instanceof ServletMapping25) {
                    String[] urlPatterns = ((ServletMapping25)sm).getUrlPatterns();
                    if (urlPatterns.length == 0 || !resourcesUrl.equals(urlPatterns[0])) {
                        urlPatternChanged = true;
                    }
                } else {
                    if (!resourcesUrl.equals(sm.getUrlPattern())) {
                        urlPatternChanged = true;
                    }
                }

                if (urlPatternChanged) {
                    if (sm instanceof ServletMapping25) {
                        String[] urlPatterns = ((ServletMapping25)sm).getUrlPatterns();
                        if (urlPatterns.length>0) {
                            ((ServletMapping25)sm).setUrlPattern(0, resourcesUrl);
                        } else {
                            ((ServletMapping25)sm).addUrlPattern(resourcesUrl);
                        }
                    } else {
                        sm.setUrlPattern(resourcesUrl);
                    }
                    needsSave = true;
                }
            }
            if (needsSave) {
                webApp.write(ddFO);
                restSupport.logResourceCreation();
            }
        } catch (IOException ioe) {
            throw ioe;
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static ServletMapping getRestServletMapping(WebApp webApp) {
        String servletName = null;
        for (Servlet s : webApp.getServlet()) {
            String servletClass = s.getServletClass();

            /// XXX: TODO: check also for servlet with name "javax.ws.rs.core.Application"
            //  that is a case when Application subclass does not specify value for
            //  ApplicationPath annotation - in that case web.xml must have servlet named
            //  "javax.ws.rs.core.Application" and its mapping will be used

            if (REST_SERVLET_ADAPTOR_CLASS.equals(servletClass) || REST_SPRING_SERVLET_ADAPTOR_CLASS.equals(servletClass) ||
                    REST_SERVLET_ADAPTOR_CLASS_2_0.equals(servletClass)) {
                servletName = s.getServletName();
                break;
            }
        }
        if (servletName != null) {
            for (ServletMapping sm : webApp.getServletMapping()) {
                if (servletName.equals(sm.getServletName())) {
                    return sm;
                }
            }
        }
        return null;
    }

// moving to RestUtils
//    public FileObject getDeploymentDescriptor() {
//        WebModuleProvider wmp = restSupport.getProject().getLookup().lookup(WebModuleProvider.class);
//        if (wmp != null) {
//            return wmp.findWebModule(restSupport.getProject().getProjectDirectory()).getDeploymentDescriptor();
//        }
//        return null;
//    }
//
    public FileObject getOrCreateWebXml() throws IOException {
        WebModule wm = WebModule.getWebModule(restSupport.getProject().getProjectDirectory());
        if (wm != null) {
            FileObject ddFo = wm.getDeploymentDescriptor();
            if (ddFo == null) {
                FileObject webInf = wm.getWebInf();
                if (webInf == null) {
                    FileObject docBase = wm.getDocumentBase();
                    if (docBase != null) {
                        webInf = docBase.createFolder("WEB-INF"); //NOI18N
                    }
                }
                if (webInf != null) {
                    ddFo = DDHelper.createWebXml(wm.getJ2eeProfile(), webInf);
                }
            }
            return ddFo;
        }
        return null;
    }

    private static Servlet getRestServletAdaptor(WebApp webApp) {
        if (webApp != null) {
            for (Servlet s : webApp.getServlet()) {
                String servletClass = s.getServletClass();
                if ( REST_SERVLET_ADAPTOR_CLASS.equals(servletClass) ||
                    REST_SPRING_SERVLET_ADAPTOR_CLASS.equals(servletClass) ||
                    REST_SERVLET_ADAPTOR_CLASS_2_0.equals(servletClass) ||
                    REST_SERVLET_ADAPTOR_CLASS_OLD.equals(servletClass)) {
                    return s;
                }
            }
        }
        return null;
    }

    public static boolean hasRestServletAdaptor(WebApp wa) {
        return getRestServletAdaptor(wa) != null;
    }

}
