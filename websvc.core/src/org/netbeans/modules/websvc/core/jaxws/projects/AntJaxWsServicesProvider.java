/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.websvc.core.jaxws.projects;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.modules.websvc.api.jaxws.project.config.Client;
import org.netbeans.modules.websvc.api.jaxws.project.config.JaxWsModel;
import org.netbeans.modules.websvc.api.jaxws.project.config.Service;
import org.netbeans.modules.websvc.project.api.WebService;
import org.netbeans.modules.websvc.project.spi.WebServiceDataProvider;
import org.netbeans.modules.websvc.project.spi.WebServiceFactory;
import org.netbeans.spi.project.ProjectServiceProvider;

/**
 *
 * @author mkuchtiak
 */
@ProjectServiceProvider(service=WebServiceDataProvider.class, projectType={
    "org-netbeans-modules-web-project",
    "org-netbeans-modules-j2ee-ejbjarproject",
    "org-netbeans-modules-j2ee-clientproject",
    "org-netbeans-modules-java-j2seproject"
})
public class AntJaxWsServicesProvider implements WebServiceDataProvider, PropertyChangeListener {
    private Project prj;

    /** Constructor.
     *
     * @param prj project
     * @param jaxWsSupport JAXWSLightSupport
     */
    public AntJaxWsServicesProvider(Project prj) {
        this.prj = prj;
    }

    public List<WebService> getServiceProviders() {
        List<WebService> webServices = new ArrayList<WebService>();
        JaxWsModel jaxWsModel = prj.getLookup().lookup(JaxWsModel.class);
        if (jaxWsModel != null) {
            Service[] services = jaxWsModel.getServices();
            for (Service s : services) {
                webServices.add(WebServiceFactory.createWebService(new AntJAXWSService(jaxWsModel, s, prj)));
            }
        }
        return webServices;
    }

    public List<WebService> getServiceConsumers() {
        List<WebService> webServices = new ArrayList<WebService>();
        JaxWsModel jaxWsModel = prj.getLookup().lookup(JaxWsModel.class);
        if (jaxWsModel != null) {
            Client[] clients = jaxWsModel.getClients();
            for (Client c : clients) {
                webServices.add(WebServiceFactory.createWebService(new AntJAXWSClient(jaxWsModel, c, prj)));
            }
        }
        return webServices;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        JaxWsModel jaxWsModel = prj.getLookup().lookup(JaxWsModel.class);
        if (jaxWsModel != null) {
            jaxWsModel.addPropertyChangeListener(pcl);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        JaxWsModel jaxWsModel = prj.getLookup().lookup(JaxWsModel.class);
        if (jaxWsModel != null) {
            jaxWsModel.removePropertyChangeListener(pcl);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
//        if (JAXWSLightSupport.PROPERTY_SERVICE_ADDED.equals(evt.getPropertyName())) {
//            JaxWsService newService = (JaxWsService) evt.getNewValue();
//            if (newService.isServiceProvider()) {
//                Service service = jaxWsModel.findServiceByImplementationClass(newService.getImplementationClass());
//                if (service != null) {
//                    AntJAXWSService mavenService = new AntJAXWSService(newService, jaxWsModel, service, prj);
//                    WebService webService = WebServiceFactory.createWebService(mavenService);
//                    providers.add(webService);
//                }
//            } else {
//                Client client = jaxWsModel.findClientByWsdlUrl(newService.getWsdlUrl());
//                if (client != null) {
//                    AntJAXWSClient mavenClient = new AntJAXWSClient(newService, jaxWsModel, client, prj);
//                    WebService webService = WebServiceFactory.createWebService(mavenClient);
//                    consumers.add(webService);
//                }
//            }
//        } else if (JAXWSLightSupport.PROPERTY_SERVICE_REMOVED.equals(evt.getPropertyName())) {
//            JaxWsService jaxWsService = (JaxWsService) evt.getOldValue();
//            if (jaxWsService.isServiceProvider()) {
//                String implClass = jaxWsService.getImplementationClass();
//                for (WebService service : providers) {
//                    if (implClass.equals(service.getIdentifier())) {
//                        providers.remove(service);
//                        break;
//                    }
//                }
//            } else {
//                String wsdlUrl = jaxWsService.getWsdlUrl();
//                for (WebService service : consumers) {
//                    if (wsdlUrl.equals(service.getIdentifier())) {
//                        consumers.remove(service);
//                        break;
//                    }
//                }
//            }
//        }
    }

}
