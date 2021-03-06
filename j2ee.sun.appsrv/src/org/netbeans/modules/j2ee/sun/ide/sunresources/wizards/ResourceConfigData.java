/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2015 Oracle and/or its affiliates. All rights reserved.
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
/*
 * ResourceConfigData.java
 *
 * Created on October 5, 2002, 6:20 PM
 */
package org.netbeans.modules.j2ee.sun.ide.sunresources.wizards;

import java.io.File;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;
import org.netbeans.modules.glassfish.tooling.data.GlassFishServer;
import org.netbeans.modules.glassfish.tooling.data.GlassFishVersion;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.sun.api.restricted.ResourceUtils;
import org.netbeans.modules.j2ee.sun.ide.editors.NameValuePair;
import org.netbeans.modules.j2ee.sun.sunresources.beans.Wizard;
import org.netbeans.modules.j2ee.sun.sunresources.beans.WizardConstants;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;

/**
 *
 * @author  shirleyc
 */
public class ResourceConfigData implements WizardConstants {
 
    /** Local logger instance. */
    private static final Logger LOGGER = Logger.getLogger(ResourceConfigData.class.getName());

    private DataFolder targetFolder;
    private String targetFolderPath;
    private String targetFile;
    private FileObject targetFileObject;
    private ResourceConfigHelperHolder holder;
    @SuppressWarnings("UseOfObsoleteCollectionType")
    private Hashtable prop_value_map = new Hashtable();
    private String resName;
    
    @SuppressWarnings("PublicField")
    public static Wizard cpWizard;
    @SuppressWarnings("PublicField")
    public static Wizard dsWizard;
    @SuppressWarnings("PublicField")
    public static Wizard jmsWizard;
    @SuppressWarnings("PublicField")
    public static Wizard mailWizard;
    @SuppressWarnings("PublicField")
    public static Wizard pmWizard;
    
    private FileObject projectDirectory;
    
    public ResourceConfigData() {
        cpWizard = null;
        dsWizard = null;
        jmsWizard = null;
        mailWizard = null;
        pmWizard = null;
    }

    /**
     * Update target folder for GlassFish server 3.1 and later and return new resource
     * file including full path.
     * Directory structure is created when missing.
     * This duplicates more complex and better code from
     * {@code org.netbeans.modules.glassfish.eecommon.api.config.GlassfishConfiguration}
     * but I had to implement it twice because of reverse module dependency.
     * <i>Internal helper method, do not call outside this module.</i>
     * <p/>
     * @param baseName Resource file base name to use when server is not GlassFish server 3.1 and later.
     * @return Server resources file including full path to be written.
     */
    public File targetFileForNewResourceFile(final String baseName) {
        final J2eeModuleProvider provider = ResourceUtils.getJavaEEModuleProvider(targetFileObject);
        final GlassFishServer server = ResourceUtils.getGlassFishServer(provider);
        // Handle GlassFish server 3.1 and later.
        if (server != null) {
            final GlassFishVersion version = server.getVersion();
            if (GlassFishVersion.ge(version, GlassFishVersion.GF_3_1)) {
                @SuppressWarnings("null") // Will never get here with provider == null.
                final J2eeModule module = provider.getJ2eeModule();
                final String resourceFileName = ResourceUtils.getResourcesFileModulePath(module, version);
                final File resourceFile = module.getDeploymentConfigurationFile(resourceFileName);
                final File resourceFileDir = ResourceUtils.createPathForFile(resourceFile);
                if (resourceFileDir != null) {
                    targetFileObject = FileUtil.toFileObject(resourceFileDir);
                    return resourceFile;
                }
            }
        }
        final File resourceFileDir = FileUtil.toFile(targetFileObject);
        // Create directory structure if missing.
        if (resourceFileDir != null && !resourceFileDir.exists()) {
            resourceFileDir.mkdirs();
        }
        return new File(FileUtil.toFile(targetFileObject), baseName);
    }

    @SuppressWarnings("UseOfObsoleteCollectionType")
    public void removeAll() {
        prop_value_map = new Hashtable();
    }
    
    public String getResourceName() {
        return resName;
    }
    
    public void setResourceName(final String name) {
        resName = name;
    }
    
    public String getString(final String name) {
        Object value = prop_value_map.get(name);
        if (value == null) {
            return new String();
        } else {
            return (String)value;
        }
    }
    
    public void setString(final String name, final String value) {
        set(name, value);
    }
    
    public Object get(final String name) {
        return prop_value_map.get(name);
    }
    
    public void set(final String name, final Object value) {
        prop_value_map.put(name, value);
    }
    
    public String[] getFieldNames() {
        final Set keySet = prop_value_map.keySet();
        final String[] fieldNames = new String[keySet.size()];
        return (String[])keySet.toArray(fieldNames);
    }
    
    @SuppressWarnings("UseOfObsoleteCollectionType")
    public Vector getProperties() {
        Vector props = (Vector)prop_value_map.get(__Properties);  //NOI18N
        if (props == null) {
            props = new Vector();
            prop_value_map.put(__Properties, props);  //NOI18N
        }
        return props;
    }
    
    @SuppressWarnings("UseOfObsoleteCollectionType")
    public Vector getPropertyNames() {
        final Vector props = getProperties();
        final Vector vec = new Vector();
        for (int i = 0; i < props.size(); i++) {
            vec.add(((NameValuePair)props.elementAt(i)).getParamName());
        }
        return vec;
    }
    
    public String getPropertyValue(final String propName) {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        final Vector vec = getProperties();
        for (int i = 0; i < vec.size(); i++) {
            NameValuePair pair = (NameValuePair)vec.elementAt(i);
            if (pair.getParamName().equals(propName)) {
                return pair.getParamValue();
            }
        }
        return null;
    }
    
    @SuppressWarnings("UseOfObsoleteCollectionType")
    public Vector addProperty(final NameValuePair pair) {
        final Vector names = getPropertyNames();
        if (names.contains(pair.getParamName())) {
            return null;
        }
        final Vector props = getProperties();
        props.add(pair);   
        return props;
    }
    
    @SuppressWarnings("UseOfObsoleteCollectionType")
    public Vector addProperty(final String name, final String value) {
        final NameValuePair pair = new NameValuePair();
        pair.setParamName(name);
        pair.setParamValue(value); 
        return addProperty(pair);
    }
    
    public void removeProperty(final int index) {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        final Vector props = getProperties();
        props.removeElementAt(index);
    }
    
    public void setProperties(
            @SuppressWarnings("UseOfObsoleteCollectionType")
            final Vector props) {
           set(__Properties, props); 
    }
    
    @Override
    public String toString() {
        final StringBuffer retValue = new StringBuffer();
        retValue.append(getResourceName()).append("::\n");  //NOI18N
        final String[] fieldNames = getFieldNames();
        for (int i = 0; i < fieldNames.length; i++) {
            if (fieldNames[i].equals(__Properties)) {
                retValue.append("properties: \n");  //NOI18N
                @SuppressWarnings("UseOfObsoleteCollectionType")
                final Vector props = (Vector)getProperties();
                for (int j = 0; j < props.size(); j++) {
                    NameValuePair pair = (NameValuePair)props.elementAt(j);
                    retValue.append("    ").append(pair.getParamName());
                    retValue.append(": ").append(pair.getParamValue());
                }
            }
            else { 
                retValue.append(fieldNames[i]).append(": ");
                retValue.append(getString(fieldNames[i])).append("\n");
            }
        }
        return retValue.toString();
    }
    
    public void setTargetFolder(final DataFolder targetFolder) {
        this.targetFolder = targetFolder;
    }
    
    public DataFolder getTargetFolder() {
        return this.targetFolder;
    }  
    
    public void setTargetFolderPath(final String path) {
        this.targetFolderPath = path;
    }
    
    public String getTargetFolderPath() {
        return this.targetFolderPath;
    }

    public void setTargetFile(final String targetFile) {
        this.targetFile = targetFile;
    }
    
    public String getTargetFile() {
        return this.targetFile;
    }
    
    public FileObject getTargetFileObject() {
        return this.targetFileObject;
    }
    
    public void setTargetFileObject(final FileObject targetObject) {
        this.targetFileObject = targetObject;
    }
    
    public FileObject getProjectDirectory() {
        return this.projectDirectory;
    }
    
    public void setProjectDirectory(final FileObject projectDirectory) {
        this.projectDirectory = projectDirectory;
    }
    
 
    public ResourceConfigHelperHolder getHolder() {
        return this.holder;
    }
    
    public void setHolder(final ResourceConfigHelperHolder holder) {
        this.holder = holder;
    }
}
